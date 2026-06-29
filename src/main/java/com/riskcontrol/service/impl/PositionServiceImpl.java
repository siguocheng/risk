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
    private final IPositionAllocateHistoryService positionAllocateHistoryService;

    private final IPositionExecutionService positionExecutionService;
    private final IAccountContractService contractService;

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
    public boolean allocatePosition(PositionAllocateRequest request) {

        Integer operateType = request.getOperateType(); // 操作类型 1持仓分配 2交易分配

        List<PositionAllocateHistory> historyList = new ArrayList<>();
        Position position;

        BigDecimal avgPln = BigDecimal.ZERO; // 每一股的盈亏
        if (operateType == 1) {
            position = this.getById(request.getId());

            avgPln = position.getUnrealizedPnl().divide(position.getPositionQty(), 4, RoundingMode.DOWN);
        } else if (operateType == 2) {
            PositionExecution positionExecution = positionExecutionService.getById(request.getId());
            avgPln = positionExecution.getRealizedPnl().divide(positionExecution.getShares(), 2, RoundingMode.DOWN);
        }

        for (PositionAllocateItem detail : request.getDetails()) {

            PositionAllocateHistory history = new PositionAllocateHistory();

            BeanUtils.copyProperties(detail, history);
            String strategyName = detail.getStrategyName();
            String traderName = detail.getTraderName();
            BigDecimal allocateQty = detail.getAllocateQty(); // 分配数量

            // 持仓分配
            if (operateType == 1) {
                history.setPositionId(request.getId());
                history.setUnrealizedPnl(avgPln.multiply(allocateQty));
            }
            // 持仓交易分配
            else if (operateType == 2) {
                history.setPositionExecutionId(request.getId());
                history.setRealizedPnl(avgPln.multiply(allocateQty));
            }

            positionAllocateHistoryService.saveOrUpdate(history);

            // 修改：更新position_relation表
            PositionRelation relation = positionRelationService.getPositionRelationByKey(detail.getAccountCode(), detail.getConid(), strategyName, traderName);
            if (relation == null) {
                // 新增：直接保存到position_relation表
                relation = new PositionRelation();
                relation.setAccountCode(detail.getAccountCode());
                relation.setConid(detail.getConid());
                relation.setStrategyName(strategyName);
                relation.setTraderName(traderName);
                relation.setPositionQty(allocateQty);
                if (operateType == 1) {
                    relation.setUnrealizedPnl(history.getUnrealizedPnl());
                    relation.setRealizedPnl(BigDecimal.ZERO);
                } else if (operateType == 2) {
                    relation.setUnrealizedPnl(BigDecimal.ZERO);
                    relation.setRealizedPnl(history.getRealizedPnl());
                }

                positionRelationService.save(relation);
            } else {
                if (operateType == 1) {
                    relation.setUnrealizedPnl(relation.getUnrealizedPnl().add(history.getUnrealizedPnl()));
                } else if (operateType == 2) {
                    relation.setRealizedPnl(relation.getRealizedPnl().add(history.getRealizedPnl()));
                }
                relation.setPositionQty(relation.getPositionQty().add(allocateQty));

                positionRelationService.updateById(relation);
            }
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
}
