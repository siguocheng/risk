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
import org.springframework.beans.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean allocatePosition(PositionAllocateRequest request) {

        Integer operateType = request.getOperateType(); // 操作类型 1持仓分配 2交易分配

        List<PositionAllocateHistory> historyList = new ArrayList<>();

        BigDecimal avgRealizedPln = BigDecimal.ZERO; // 每一股的已实现盈亏，依赖出库交易
        BigDecimal avgUnRealizedPln = BigDecimal.ZERO; // 每一股的未实现盈亏，依赖历史持仓
        BigDecimal avgCommissionAnFees = BigDecimal.ZERO;
        if (operateType == 2) {
            PositionExecution positionExecution = positionExecutionService.getById(request.getId()); // 交易信息

            String executionDate = positionExecution.getExecutionDate(); // 交易日期

            PositionHistory positionHistory = positionHistoryService.getPositionHistoryByKey(executionDate, positionExecution.getConid(), positionExecution.getAccountCode());

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
                avgUnRealizedPln = positionHistory.getCalUnrealizedPnl().divide(positionHistory.getCalPositionQty(), 2, RoundingMode.DOWN);
            }

            if (positionExecution.getCommissionAndFees() != null) {
                avgCommissionAnFees = positionExecution.getCommissionAndFees().divide(positionExecution.getShares(), 2, RoundingMode.DOWN);
            }

            // 删除分配记录重新添加
            positionAllocateHistoryService.delPositionAllocateHistoryByKey(null, request.getId());

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
                positionAllocateHistoryService.saveOrUpdate(allocateHistory);

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
                    positionRelationHistory.setMarketPrice(positionExecution.getMarketPrice());

                    positionRelationHistoryService.save(positionRelationHistory);
                } else {

                    // 交易分配
                    positionRelationHistory.setPositionQty(positionRelationHistory.getPositionQty().add(allocateQty));
                    positionRelationHistory.setUnrealizedPnl(accUnRealizedPln);
                    positionRelationHistory.setRealizedPnl(positionRelationHistory.getRealizedPnl().add(accRealizedPln));
                    positionRelationHistory.setCommissionAndFees(positionRelationHistory.getCommissionAndFees().add(accCommissionAnFees));

                    positionRelationHistoryService.updateById(positionRelationHistory);
                }
                positionExecution.setAllocateRemainQty(positionExecution.getAllocateRemainQty().subtract(detail.getAllocateQty()));
            }

            positionExecutionService.updateById(positionExecution);
        }

        return true;
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
