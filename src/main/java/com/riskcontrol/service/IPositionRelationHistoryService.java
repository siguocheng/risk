package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionRelationHistory;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史Service接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface IPositionRelationHistoryService extends IService<PositionRelationHistory> {

    List<PositionRelationHistory> listByKey(String accountCode, Integer conid, String strategyName, String traderName);

    boolean saveOrUpdateByKey(PositionRelationHistory history);
}
