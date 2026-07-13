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

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {

    private static final int MAX_HISTORY_CHECK_DAYS = 365;

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
            return this.update(position, queryWrapper);
        } else {
            return this.save(position);
        }
    }

    private void checkBeforeExecutionDate(PositionExecution positionExecution) {
        String accountCode = positionExecution.getAccountCode();
        Integer conid = positionExecution.getConid();
        LocalDate currentDate = DateUtil.stringToLocalDate(positionExecution.getExecutionDate());
        
        for (int i = 1; i <= MAX_HISTORY_CHECK_DAYS; i++) {
            LocalDate checkDate = currentDate.minusDays(i);
            String executionDate = DateUtil.localDateToString(checkDate);
            
            List<PositionExecution> positionExecutions = positionExecutionService.listPositionExecutionByKey(accountCode, conid, executionDate);
            if (positionExecutions.size() > 0) {
                for (PositionExecution execution : positionExecutions) {
                    BigDecimal allocateRemainQty = execution.getAllocateRemainQty();
                    if (allocateRemainQty != null && allocateRemainQty.compareTo(BigDecimal.ZERO) == 1) {
                        throw new BusinessException("当前交易日之前存在未分配完的数量，请先完成历史交易日的分配");
                    }
                }
                return;
            }
        }
    }

    private BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, scale, roundingMode);
    }

    private BigDecimal getOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean allocatePosition(PositionAllocateRequest request) {

        Integer operateType = request.getOperateType();

        if (operateType == 1) {
            return this.allocatePositionByPosition(request);
        } else if (operateType == 2) {
            return this.allocatePositionByExecution(request);
        } else {
            throw new BusinessException("不支持的操作类型");
        }
    }

    private Boolean allocatePositionByPosition(PositionAllocateRequest request) {
        Position position = this.getById(request.getId());
        if (position == null) {
            throw new BusinessException("持仓记录不存在");
        }

        List<PositionAllocateItem> positionAllocateList = request.getDetails();
        if (CollectionUtils.isEmpty(positionAllocateList)) {
            throw new BusinessException("分配明细不能为空");
        }

        BigDecimal accAllocateQty = positionAllocateList.stream()
                .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal positionQty = getOrDefault(position.getPositionQty(), BigDecimal.ZERO);
        if (accAllocateQty.compareTo(positionQty) > 0) {
            throw new BusinessException("分配数量超过持仓数量");
        }

        List<PositionRelation> positionRelationList = new ArrayList<>();

        List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(request.getId(), null);
        if (!CollectionUtils.isEmpty(positionAllocateHistories)) {
            for (PositionAllocateHistory positionAllocateHistory : positionAllocateHistories) {
                String accountCode = positionAllocateHistory.getAccountCode();
                int conid = positionAllocateHistory.getConid();
                String strategyName = positionAllocateHistory.getStrategyName();
                String traderName = positionAllocateHistory.getTraderName();
                PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);

                if (positionRelation != null) {
                    BigDecimal allocateQty = getOrDefault(positionAllocateHistory.getAllocateQty(), BigDecimal.ZERO);
                    positionRelation.setPositionQty(getOrDefault(positionRelation.getPositionQty(), BigDecimal.ZERO).subtract(allocateQty));
                    positionRelationService.updateById(positionRelation);
                    positionRelationList.add(positionRelation);
                }
            }
            positionAllocateHistoryService.delPositionAllocateHistoryByKey(request.getId(), null);
        }

        String dailyDate = DateUtil.localDateToString(LocalDate.now());

        for (PositionAllocateItem detail : positionAllocateList) {
            PositionAllocateHistory allocateHistory = new PositionAllocateHistory();
            BeanUtils.copyProperties(detail, allocateHistory);
            String strategyName = detail.getStrategyName();
            String traderName = detail.getTraderName();
            String accountCode = detail.getAccountCode();
            Integer conid = detail.getConid();
            BigDecimal allocateQty = getOrDefault(detail.getAllocateQty(), BigDecimal.ZERO);

            if (allocateQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            allocateHistory.setPositionId(request.getId());
            positionAllocateHistoryService.save(allocateHistory);

            PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);
            if (positionRelation == null) {
                positionRelation = new PositionRelation();
                positionRelation.setDailyDate(dailyDate);
                positionRelation.setAccountCode(accountCode);
                positionRelation.setConid(conid);
                positionRelation.setStrategyName(strategyName);
                positionRelation.setTraderName(traderName);
                positionRelation.setPositionQty(allocateQty);
                positionRelation.setUnrealizedPnl(BigDecimal.ZERO);
                positionRelation.setRealizedPnl(BigDecimal.ZERO);
                positionRelation.setDailyUnrealizedPnl(BigDecimal.ZERO);
                positionRelation.setDailyRealizedPnl(BigDecimal.ZERO);
                positionRelation.setCommissionAndFees(BigDecimal.ZERO);
                positionRelation.setMarketPrice(position.getMarketPrice());
                positionRelation.setAvgCost(position.getAvgCost());

                positionRelationService.save(positionRelation);
            } else {
                positionRelation.setDailyDate(dailyDate);
                positionRelation.setPositionQty(positionRelation.getPositionQty().add(allocateQty));
                positionRelation.setMarketPrice(position.getMarketPrice());
                positionRelation.setAvgCost(position.getAvgCost());

                positionRelationService.updateById(positionRelation);
            }

            positionRelationList.add(positionRelation);
        }

        for (PositionRelation positionRelation : positionRelationList) {
            this.handlePositionRelationHistory(positionRelation);
        }

        return true;
    }

    private Boolean allocatePositionByExecution(PositionAllocateRequest request) {
        PositionExecution positionExecution = positionExecutionService.getById(request.getId());
        if (positionExecution == null) {
            throw new BusinessException("交易记录不存在");
        }

        LambdaQueryWrapper<PositionExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PositionExecution::getAccountCode, positionExecution.getAccountCode())
                .eq(PositionExecution::getConid, positionExecution.getConid())
                .lt(PositionExecution::getExecutionDate, positionExecution.getExecutionDate())
                .last("limit 1");

        PositionExecution execution = positionExecutionService.getOne(wrapper);

        if (execution != null) {
            this.checkBeforeExecutionDate(positionExecution);
        }

        BigDecimal avgRealizedPln = BigDecimal.ZERO;
        BigDecimal avgUnRealizedPln = BigDecimal.ZERO;
        BigDecimal avgCommissionAnFees = BigDecimal.ZERO;

        String executionDate = positionExecution.getExecutionDate();

        List<PositionAllocateItem> positionAllocateList = request.getDetails();
        if (CollectionUtils.isEmpty(positionAllocateList)) {
            throw new BusinessException("分配明细不能为空");
        }

        BigDecimal accAllocateQty = positionAllocateList.stream()
                .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shares = getOrDefault(positionExecution.getShares(), BigDecimal.ZERO);
        if (accAllocateQty.compareTo(shares) > 0) {
            throw new BusinessException("分配数量超过交易数量");
        }

        avgRealizedPln = safeDivide(positionExecution.getCalExecutionRealizedPnl(), shares, 2, RoundingMode.DOWN);
        avgUnRealizedPln = safeDivide(positionExecution.getCalExecutionUnrealizedPnl(), shares, 2, RoundingMode.DOWN);
        avgCommissionAnFees = safeDivide(positionExecution.getCommissionAndFees(), shares, 2, RoundingMode.DOWN);

        List<PositionRelation> positionRelationList = new ArrayList<>();

        this.resetPositionRelation(positionExecution, positionRelationList);

        PositionHistory positionHistory = positionHistoryService.getPositionHistoryByKey(executionDate, positionExecution.getConid(), positionExecution.getAccountCode());
        if (positionHistory == null) {
            throw new BusinessException("持仓历史记录不存在");
        }

        BigDecimal avgCalUnrealizedPnlPosition = safeDivide(positionHistory.getCalUnrealizedPnl(), positionHistory.getCalPositionQty(), 2, RoundingMode.DOWN);
        BigDecimal avgCalDailyUnrealizedPnlPosition = safeDivide(positionHistory.getCalDailyUnrealizedPnl(), positionHistory.getCalPositionQty(), 2, RoundingMode.DOWN);

        LambdaQueryWrapper<PositionRelation> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(PositionRelation::getAccountCode, positionExecution.getAccountCode())
                .eq(PositionRelation::getConid, positionExecution.getConid())
                .last("limit 1");
        PositionRelation one = positionRelationService.getOne(wrapper1);

        if (one != null) {
            if (!one.getDailyDate().equals(executionDate)) {
                LambdaQueryWrapper<PositionRelation> updateWrapper = new LambdaQueryWrapper<>();
                updateWrapper.eq(PositionRelation::getAccountCode, positionExecution.getAccountCode())
                        .eq(PositionRelation::getConid, positionExecution.getConid());
                List<PositionRelation> list = positionRelationService.list(updateWrapper);
                for (PositionRelation positionRelation : list) {
                    positionRelation.setDailyDate(executionDate);
                    this.handlePositionRelationHistory(positionRelation);
                }
            }
        }

        for (PositionAllocateItem detail : positionAllocateList) {
            PositionAllocateHistory allocateHistory = new PositionAllocateHistory();
            BeanUtils.copyProperties(detail, allocateHistory);
            String strategyName = detail.getStrategyName();
            String traderName = detail.getTraderName();
            String accountCode = detail.getAccountCode();
            Integer conid = detail.getConid();
            BigDecimal allocateQty = getOrDefault(detail.getAllocateQty(), BigDecimal.ZERO);

            BigDecimal accRealizedPln = avgRealizedPln.multiply(allocateQty);
            BigDecimal accUnRealizedPln = avgUnRealizedPln.multiply(allocateQty);
            BigDecimal accCommissionAnFees = avgCommissionAnFees.multiply(allocateQty);

            allocateHistory.setPositionExecutionId(request.getId());
            allocateHistory.setRealizedPnl(accRealizedPln);
            allocateHistory.setUnrealizedPnl(accUnRealizedPln);
            allocateHistory.setCommissionAndFees(accCommissionAnFees);
            positionAllocateHistoryService.save(allocateHistory);

            if (positionExecution.getSide().equals(TradeSideEnum.SLD.name())) {
                allocateQty = allocateQty.negate();
            }

            PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);
            if (positionRelation == null) {
                positionRelation = new PositionRelation();
                positionRelation.setDailyDate(executionDate);
                positionRelation.setAccountCode(accountCode);
                positionRelation.setConid(conid);
                positionRelation.setStrategyName(strategyName);
                positionRelation.setTraderName(traderName);

                positionRelation.setUnrealizedPnl(avgCalUnrealizedPnlPosition.multiply(allocateQty));
                positionRelation.setRealizedPnl(accRealizedPln);
                positionRelation.setPositionQty(allocateQty);
                positionRelation.setDailyUnrealizedPnl(accUnRealizedPln);
                positionRelation.setDailyRealizedPnl(accRealizedPln);
                positionRelation.setCommissionAndFees(accCommissionAnFees);
                positionRelation.setMarketPrice(positionHistory.getCalMarketPrice());
                positionRelation.setAvgCost(positionHistory.getCalAvgCost());

                positionRelationService.save(positionRelation);
            } else {
                positionRelation.setDailyDate(executionDate);
                positionRelation.setRealizedPnl(getOrDefault(positionRelation.getRealizedPnl(), BigDecimal.ZERO).add(accRealizedPln));
                positionRelation.setPositionQty(getOrDefault(positionRelation.getPositionQty(), BigDecimal.ZERO).add(allocateQty));
                positionRelation.setUnrealizedPnl(avgCalUnrealizedPnlPosition.multiply(positionRelation.getPositionQty()));
                positionRelation.setDailyUnrealizedPnl(avgCalDailyUnrealizedPnlPosition.multiply(positionRelation.getPositionQty()));
                positionRelation.setDailyRealizedPnl(getOrDefault(positionRelation.getDailyRealizedPnl(), BigDecimal.ZERO).add(accRealizedPln));
                positionRelation.setCommissionAndFees(getOrDefault(positionRelation.getCommissionAndFees(), BigDecimal.ZERO).add(accCommissionAnFees));
                positionRelation.setMarketPrice(positionHistory.getCalMarketPrice());
                positionRelation.setAvgCost(positionHistory.getCalAvgCost());

                positionRelationService.updateById(positionRelation);
            }

            positionRelationList.add(positionRelation);
        }

        positionExecution.setAllocateRemainQty(shares.subtract(accAllocateQty));
        positionExecutionService.updateById(positionExecution);

        for (PositionRelation positionRelation : positionRelationList) {
            this.handlePositionRelationHistory(positionRelation);
        }

        return true;
    }

    private void handlePositionRelationHistory(PositionRelation positionRelation){
        PositionRelationHistory positionRelationHistory = new PositionRelationHistory(positionRelation);
        positionRelationHistory.setId(null);
        positionRelationHistoryService.saveOrUpdateByKey(positionRelationHistory);
    }

    private void resetPositionRelation(PositionExecution positionExecution, List<PositionRelation> positionRelationList){
        List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(null, positionExecution.getId());
        if (!CollectionUtils.isEmpty(positionAllocateHistories)) {

            for (PositionAllocateHistory positionAllocateHistory : positionAllocateHistories) {
                String accountCode = positionAllocateHistory.getAccountCode();
                int conid = positionAllocateHistory.getConid();
                String strategyName = positionAllocateHistory.getStrategyName();
                String traderName = positionAllocateHistory.getTraderName();
                PositionRelation positionRelation = positionRelationService.getPositionRelationByKey(accountCode, conid, strategyName, traderName);

                if (positionRelation == null) {
                    continue;
                }

                BigDecimal allocateQty = getOrDefault(positionAllocateHistory.getAllocateQty(), BigDecimal.ZERO);

                if (positionExecution.getSide().equals(TradeSideEnum.SLD.name())) {
                    allocateQty = allocateQty.negate();
                }

                positionRelation.setRealizedPnl(getOrDefault(positionRelation.getRealizedPnl(), BigDecimal.ZERO)
                        .subtract(getOrDefault(positionAllocateHistory.getRealizedPnl(), BigDecimal.ZERO)));
                positionRelation.setPositionQty(getOrDefault(positionRelation.getPositionQty(), BigDecimal.ZERO)
                        .subtract(allocateQty));
                positionRelation.setDailyUnrealizedPnl(getOrDefault(positionRelation.getDailyUnrealizedPnl(), BigDecimal.ZERO)
                        .subtract(getOrDefault(positionAllocateHistory.getUnrealizedPnl(), BigDecimal.ZERO)));
                positionRelation.setDailyRealizedPnl(getOrDefault(positionRelation.getDailyRealizedPnl(), BigDecimal.ZERO)
                        .subtract(getOrDefault(positionAllocateHistory.getRealizedPnl(), BigDecimal.ZERO)));
                positionRelation.setCommissionAndFees(getOrDefault(positionRelation.getCommissionAndFees(), BigDecimal.ZERO)
                        .subtract(getOrDefault(positionAllocateHistory.getCommissionAndFees(), BigDecimal.ZERO)));

                positionRelationService.updateById(positionRelation);

                positionRelationList.add(positionRelation);
            }
        }

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
            BigDecimal sum = positionAllocateHistories.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setRemainQty(getOrDefault(vo.getPositionQty(), BigDecimal.ZERO).subtract(sum));

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