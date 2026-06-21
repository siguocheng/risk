package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionHistory;

/**
 * 持仓列表历史Service接口
 *
 * @author zpc
 * @date 2026-06-20
 */
public interface IPositionHistoryService extends IService<PositionHistory> {

    /**
     * 根据account_code、conid、position_date保存或更新
     *
     * @param positionHistory 持仓历史
     * @return 是否成功
     */
    boolean saveOrUpdatePositionHistory(PositionHistory positionHistory);
}
