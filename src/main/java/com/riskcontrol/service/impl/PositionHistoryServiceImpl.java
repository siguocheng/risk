package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionHistoryMapper;
import com.riskcontrol.domain.PositionHistory;
import com.riskcontrol.service.IPositionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 持仓列表历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionHistoryServiceImpl extends ServiceImpl<PositionHistoryMapper, PositionHistory> implements IPositionHistoryService {

    @Override
    public boolean saveOrUpdatePositionHistory(PositionHistory positionHistory) {
        LambdaQueryWrapper<PositionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionHistory::getAccountCode, positionHistory.getAccountCode())
                .eq(PositionHistory::getConid, positionHistory.getConid())
                .eq(PositionHistory::getPositionDate, positionHistory.getPositionDate());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(positionHistory, queryWrapper);
        } else {
            positionHistory.setId(null);
            // 不存在则新增
            return this.save(positionHistory);
        }
    }
}
