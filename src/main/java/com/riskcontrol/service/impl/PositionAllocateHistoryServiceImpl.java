package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionAllocateHistoryMapper;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 持仓分配历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionAllocateHistoryServiceImpl extends ServiceImpl<PositionAllocateHistoryMapper, PositionAllocateHistory> implements IPositionAllocateHistoryService {

    @Override
    public List<PositionAllocateHistory> listPositionAllocateHistoryByKey(Long positionId, Long positionExecutionId) {
        LambdaQueryWrapper<PositionAllocateHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(positionId != null, PositionAllocateHistory::getPositionId, positionId)
                .eq(positionExecutionId != null, PositionAllocateHistory::getPositionExecutionId, positionExecutionId)
                .orderByDesc(PositionAllocateHistory::getCreateTime);

        return this.list(queryWrapper);
    }
}
