package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionRelationHistoryMapper;
import com.riskcontrol.domain.PositionRelationHistory;
import com.riskcontrol.service.IPositionRelationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionRelationHistoryServiceImpl extends ServiceImpl<PositionRelationHistoryMapper, PositionRelationHistory> implements IPositionRelationHistoryService {

    @Override
    public List<PositionRelationHistory> listByKey(String accountCode, Integer conid, String strategyName, String traderName) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(accountCode), PositionRelationHistory::getAccountCode, accountCode)
                .eq(conid != null, PositionRelationHistory::getConid, conid)
                .eq(StringUtils.hasText(strategyName), PositionRelationHistory::getStrategyName, strategyName)
                .eq(StringUtils.hasText(traderName), PositionRelationHistory::getTraderName, traderName)
                .orderByDesc(PositionRelationHistory::getCreateTime);

        return this.list(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateByKey(PositionRelationHistory history) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionRelationHistory::getDataDate, history.getDataDate())
                .eq(PositionRelationHistory::getAccountCode, history.getAccountCode())
                .eq(PositionRelationHistory::getConid, history.getConid())
                .eq(PositionRelationHistory::getStrategyName, history.getStrategyName())
                .eq(PositionRelationHistory::getTraderName, history.getTraderName());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(history, queryWrapper);
        } else {
            return this.save(history);
        }
    }
}
