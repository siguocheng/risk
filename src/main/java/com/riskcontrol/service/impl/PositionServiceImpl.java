package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionMapper;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.service.IPositionRelationService;
import com.riskcontrol.service.IPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

            avgPln = position.getUnrealizedPnl().divide(position.getPositionQty(), 2, RoundingMode.HALF_UP);
        } else if (operateType == 2) {
            PositionExecution positionExecution = positionExecutionService.getById(request.getId());
            avgPln = positionExecution.getRealizedPnl().divide(positionExecution.getShares(), 2, RoundingMode.HALF_UP);
        }

        for (PositionAllocateHistory detail : request.getDetails()) {
            String strategyName = detail.getStrategyName();
            String traderName = detail.getTraderName();
            BigDecimal allocateQty = detail.getAllocateQty(); // 分配数量

            // 持仓分配
            if (operateType == 1) {
                detail.setPositionId(request.getId());
                detail.setUnrealizedPnl(avgPln.multiply(allocateQty));
            }
            // 持仓交易分配
            else if (operateType == 2) {
                detail.setPositionExecutionId(request.getId());
                detail.setRealizedPnl(avgPln.multiply(allocateQty));
            }

            positionAllocateHistoryService.saveOrUpdate(detail);

            // 修改：更新position_relation表
            LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PositionRelation::getAccountCode, detail.getAccountCode())
                    .eq(PositionRelation::getConid, detail.getConid())
                    .eq(PositionRelation::getStrategyName, strategyName)
                    .eq(PositionRelation::getTraderName, traderName);

            PositionRelation relation = positionRelationService.getOne(queryWrapper);
            if (relation == null) {
                // 新增：直接保存到position_relation表
                relation = new PositionRelation();
                relation.setAccountCode(detail.getAccountCode());
                relation.setConid(detail.getConid());
                relation.setStrategyName(strategyName);
                relation.setTraderName(traderName);
                relation.setPositionQty(allocateQty);
                if (operateType == 1) {
                    relation.setUnrealizedPnl(detail.getUnrealizedPnl());
                } else if (operateType == 2) {
                    relation.setRealizedPnl(detail.getRealizedPnl());
                }

                positionRelationService.save(relation);
            } else {
                if (operateType == 1) {
                    relation.setUnrealizedPnl(detail.getUnrealizedPnl());
                } else if (operateType == 2) {
                    relation.setRealizedPnl(detail.getRealizedPnl());
                }
                relation.setPositionQty(relation.getPositionQty().add(allocateQty));

                positionRelationService.updateById(relation);
            }
        }

        return true;
    }
}
