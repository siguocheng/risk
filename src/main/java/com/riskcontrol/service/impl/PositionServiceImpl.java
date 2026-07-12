package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionMapper;
import com.riskcontrol.domain.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.riskcontrol.domain.vo.position.PositionAllocateItem;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.domain.vo.position.PositionPage;
import com.riskcontrol.domain.vo.position.PositionQuery;
import com.riskcontrol.enums.TradeSideEnum;
import com.riskcontrol.exception.BusinessException;
import com.riskcontrol.service.*;
import com.riskcontrol.util.DateUtil;
import org.springframework.beans.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 持仓列表Service业务层处理
 *
 * @author zpc
 * @date 2026-06-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {

    private final IPositionRelationService positionRelationService;
    private final IPositionRelationHistoryService positionRelationHistoryService;
    private final IPositionAllocateHistoryService positionAllocateHistoryService;

    private final IPositionExecutionService positionExecutionService;
    private final IAccountContractService contractService;
    private final IPositionHistoryService positionHistoryService;

    @Override
    public boolean saveOrUpdatePosition(Position position) {
        LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Position::getAccountCode, position.getAccountCode());
        queryWrapper.eq(Position::getConid, position.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(position, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(position);
        }
    }

    private void checkBeforeExecutionDate(PositionExecution positionExecution){
        String accountCode = positionExecution.getAccountCode();
        Integer conid = positionExecution.getConid();
        String executionDate = DateUtil.localDateToString(DateUtil.stringToLocalDate(positionExecution.getExecutionDate()).minusDays(1));
//        String executionDate = positionExecution.getExecutionDate();

        List<PositionExecution> positionExecutions = positionExecutionService.listPositionExecutionByKey(accountCode, conid, executionDate);
        if (positionExecutions.size() > 0) {
            for (PositionExecution execution : positionExecutions) {
                if (execution.getAllocateRemainQty().compareTo(BigDecimal.ZERO) == 1) {
                    throw new BusinessException("当前交易日之前存在未分配完的数量，请先完成历史交易日的分配");
                }
            }
            return;
        } else {
            positionExecution.setExecutionDate(executionDate);
            this.checkBeforeExecutionDate(positionExecution);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean allocatePosition(PositionAllocateRequest request) {

        Integer operateType = request.getOperateType(); // 操作类型 1持仓分配 2交易分配

        List<PositionAllocateHistory> historyList = new ArrayList<>();

        PositionExecution positionExecution = positionExecutionService.getById(request.getId());

        LambdaQueryWrapper<PositionExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PositionExecution::getAccountCode, positionExecution.getAccountCode())
                .eq(PositionExecution::getConid, positionExecution.getConid())
                .lt(PositionExecution::getExecutionDate, positionExecution.getExecutionDate())
                .last("limit 1");

        PositionExecution execution = positionExecutionService.getOne(wrapper);

        if (execution != null) {
            this.checkBeforeExecutionDate(positionExecution);
        }



        BigDecimal avgRealizedPln = BigDecimal.ZERO; // 每一股的已实现盈亏，依赖出库交易
        BigDecimal avgUnRealizedPln = BigDecimal.ZERO; // 每一股的未实现盈亏，依赖入库
        BigDecimal avgCommissionAnFees = BigDecimal.ZERO;
        if (operateType == 2) {
            String executionDate = positionExecution.getExecutionDate(); // 交易日期

            List<PositionAllocateItem> positionAllocateList = request.getDetails();

            BigDecimal qty = BigDecimal.ZERO;

            BigDecimal accAllocateQty = positionAllocateList.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (qty.compareTo(accAllocateQty) == 1) {
                throw new BusinessException("交易的总数量大于分配的合计数量");
            }

            if (positionExecution.getCalExecutionRealizedPnl() != null) {
                avgRealizedPln = positionExecution.getCalExecutionRealizedPnl().divide(positionExecution.getShares(), 2, RoundingMode.DOWN);
            }

            if (positionExecution.getCalExecutionUnrealizedPnl() != null) {
                avgUnRealizedPln = positionExecution.getCalExecutionUnrealizedPnl().divide(positionExecution.getShares(), 2, RoundingMode.DOWN);
            }

            if (positionExecution.getCommissionAndFees() != null) {
                avgCommissionAnFees = positionExecution.getCommissionAndFees().divide(positionExecution.getShares(), 2, RoundingMode.DOWN);
            }

            // 重置本次交易非陪
            this.resetPositionRelation(positionExecution);

            PositionHistory positionHistory = positionHistoryService.getPositionHistoryByKey(executionDate, positionExecution.getConid(), positionExecution.getAccountCode());

            BigDecimal avgCalUnrealizedPnlPosition = positionHistory.getCalUnrealizedPnl().divide(positionHistory.getCalPositionQty());

            List<PositionRelation> positionRelationList = new ArrayList<>();

            for (PositionAllocateItem detail : positionAllocateList) {

                // 维护分配记录
                PositionAllocateHistory allocateHistory = new PositionAllocateHistory();
                BeanUtils.copyProperties(detail, allocateHistory);
                String strategyName = detail.getStrategyName();
                String traderName = detail.getTraderName();
                String accountCode = detail.getAccountCode();
                int conid = detail.getConid();
                BigDecimal allocateQty = detail.getAllocateQty(); // 分配数量

                BigDecimal accRealizedPln = avgRealizedPln.multiply(allocateQty);
                BigDecimal accUnRealizedPln = avgUnRealizedPln.multiply(allocateQty);
                BigDecimal accCommissionAnFees = avgCommissionAnFees.multiply(allocateQty);

                allocateHistory.setPositionExecutionId(request.getId());
                allocateHistory.setRealizedPnl(accRealizedPln);
                allocateHistory.setUnrealizedPnl(accUnRealizedPln);
                allocateHistory.setCommissionAndFees(accCommissionAnFees);
                positionAllocateHistoryService.save(allocateHistory);


                PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);
                if (positionRelation == null) {
                    positionRelation = new PositionRelation();
                    positionRelation.setDailyDate(executionDate);
                    positionRelation.setAccountCode(detail.getAccountCode());
                    positionRelation.setConid(detail.getConid());
                    positionRelation.setStrategyName(strategyName);
                    positionRelation.setTraderName(traderName);

                    positionRelation.setUnrealizedPnl(avgCalUnrealizedPnlPosition.multiply(allocateQty));
                    positionRelation.setRealizedPnl(accRealizedPln);
                    // 交易分配
                    positionRelation.setPositionQty(allocateQty);
                    positionRelation.setDailyUnrealizedPnl(accUnRealizedPln);
                    positionRelation.setDailyRealizedPnl(accRealizedPln);
                    positionRelation.setCommissionAndFees(accCommissionAnFees);
                    positionRelation.setMarketPrice(positionHistory.getCalMarketPrice());
                    positionRelation.setAvgCost(positionHistory.getCalAvgCost());

                    positionRelationService.save(positionRelation);
                } else {
                    positionRelation.setUnrealizedPnl(avgCalUnrealizedPnlPosition.multiply(allocateQty));
                    positionRelation.setRealizedPnl(positionRelation.getRealizedPnl().add(accRealizedPln));
                    // 交易分配
                    positionRelation.setPositionQty(positionRelation.getPositionQty().add(allocateQty));
                    positionRelation.setDailyUnrealizedPnl(positionRelation.getUnrealizedPnl().add(accUnRealizedPln));
                    positionRelation.setDailyRealizedPnl(positionRelation.getRealizedPnl().add(accRealizedPln));
                    positionRelation.setCommissionAndFees(positionRelation.getCommissionAndFees().add(accCommissionAnFees));
                    positionRelation.setMarketPrice(positionHistory.getCalMarketPrice());
                    positionRelation.setAvgCost(positionHistory.getCalAvgCost());

                    positionRelationService.updateById(positionRelation);
                }

                positionRelationList.add(positionRelation);
                positionExecution.setAllocateRemainQty(positionExecution.getShares().subtract(detail.getAllocateQty()));

                PositionRelationHistory positionRelationHistory = positionRelationHistoryService.getPositionRelationHistoryByKey(executionDate, accountCode, conid, strategyName, traderName);

                if (positionExecution.getSide().equals(TradeSideEnum.BOT.name())) {

                } else if (positionExecution.getSide().equals(TradeSideEnum.SLD.name())) {
                    allocateQty = allocateQty.negate();
                }

                if (positionRelationHistory == null) {
                    // 新增：直接保存到position_relation表
                    positionRelationHistory = new PositionRelationHistory();
                    positionRelationHistory.setDailyDate(executionDate);
                    positionRelationHistory.setAccountCode(detail.getAccountCode());
                    positionRelationHistory.setConid(detail.getConid());
                    positionRelationHistory.setStrategyName(strategyName);
                    positionRelationHistory.setTraderName(traderName);
                    // 交易分配
                    positionRelationHistory.setPositionQty(allocateQty);
                    positionRelationHistory.setDailyUnrealizedPnl(accUnRealizedPln);
                    positionRelationHistory.setDailyRealizedPnl(accRealizedPln);
                    positionRelationHistory.setCommissionAndFees(accCommissionAnFees);
                    positionRelationHistory.setMarketPrice(positionHistory.getCalMarketPrice());
                    positionRelationHistory.setAvgCost(positionHistory.getCalAvgCost());

                    positionRelationHistoryService.save(positionRelationHistory);
                } else {

                    // 交易分配
                    positionRelationHistory.setPositionQty(positionRelationHistory.getPositionQty().add(allocateQty));
                    positionRelationHistory.setDailyUnrealizedPnl(accUnRealizedPln);
                    positionRelationHistory.setDailyRealizedPnl(positionRelationHistory.getRealizedPnl().add(accRealizedPln));
                    positionRelationHistory.setCommissionAndFees(positionRelationHistory.getCommissionAndFees().add(accCommissionAnFees));
                    positionRelationHistory.setMarketPrice(positionHistory.getCalMarketPrice());
                    positionRelationHistory.setAvgCost(positionHistory.getCalAvgCost());

                    positionRelationHistoryService.updateById(positionRelationHistory);
                }
            }

            positionExecutionService.updateById(positionExecution);

            for (PositionRelation positionRelation : positionRelationList) {
                this.handlePositionRelationHistory(positionRelation);
            }
        }

        return true;
    }

    private void handlePositionRelationHistory(PositionRelation positionRelation){
        List<PositionExecution> positionExecutions = positionExecutionService.listPositionExecutionByKey(positionRelation.getAccountCode(), positionRelation.getConid(), positionRelation.getDailyDate());

        for (PositionExecution execution : positionExecutions) {
            if (execution.getAllocateRemainQty().compareTo(BigDecimal.ZERO) == 1) {
                return;
            }
        }

        PositionRelationHistory PositionRelationHistory = new PositionRelationHistory(positionRelation);
        positionRelationHistoryService.save(PositionRelationHistory);
    }

    private void resetPositionRelation(PositionExecution positionExecution){
        List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(null, positionExecution.getId());
        if (positionAllocateHistories.size() > 0) {

            for (PositionAllocateHistory positionAllocateHistory : positionAllocateHistories) {
                String accountCode = positionAllocateHistory.getAccountCode();
                int conid = positionAllocateHistory.getConid();
                String strategyName = positionAllocateHistory.getStrategyName();
                String traderName = positionAllocateHistory.getTraderName();
                PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);

                BigDecimal allocateQty = positionAllocateHistory.getAllocateQty(); // 分配数量

                if (positionExecution.getSide().equals(TradeSideEnum.BOT.name())) {

                } else if (positionExecution.getSide().equals(TradeSideEnum.SLD.name())) {
                    allocateQty = allocateQty.negate();
                }

                positionRelation.setPositionQty(positionRelation.getPositionQty().subtract(allocateQty));
                positionRelation.setDailyUnrealizedPnl(positionRelation.getDailyUnrealizedPnl().subtract(positionAllocateHistory.getUnrealizedPnl()));
                positionRelation.setDailyRealizedPnl(positionRelation.getDailyRealizedPnl().subtract(positionAllocateHistory.getRealizedPnl()));
                positionRelation.setCommissionAndFees(positionRelation.getCommissionAndFees().subtract(positionAllocateHistory.getCommissionAndFees()));

                positionRelationService.updateById(positionRelation);
            }
        }

        // 删除分配记录重新添加
        positionAllocateHistoryService.delPositionAllocateHistoryByKey(null, positionExecution.getId());
    }

    @Override
    public IPage<PositionPage> queryPage(PositionQuery query) {
        Page<Position> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(!CollectionUtils.isEmpty(query.getAccountCodes()), Position::getAccountCode, query.getAccountCodes())
                .eq(!CollectionUtils.isEmpty(query.getConids()), Position::getConid, query.getConids())
                .orderByDesc(Position::getId);

        IPage<Position> entityPage = this.page(page, queryWrapper);

        IPage<PositionPage> pageList = entityPage.convert(entity -> {
            PositionPage vo = new PositionPage();
            BeanUtils.copyProperties(entity, vo);

            List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(vo.getId(), null);
            // 求和，空字段当作0处理
            BigDecimal sum = positionAllocateHistories.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setRemainQty(vo.getPositionQty().subtract(sum));

            AccountContract contract = contractService.getContractByConid(vo.getAccountCode(), vo.getConid());

            if (contract != null) {
                vo.setSymbol(contract.getSymbol());
            }

            return vo;
        });

        return pageList;
    }

    @Override
    public Position getPositionByConid(String accountCode, int conid) {
        LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Position::getConid, conid);
        queryWrapper.eq(Position::getAccountCode, accountCode);
        return this.getOne(queryWrapper);
    }
}
