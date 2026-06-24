package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionAllocateHistory;

import java.util.List;

/**
 * 持仓分配历史Service接口
 *
 * @author zpc
 * @date 2026-06-22
 */
public interface IPositionAllocateHistoryService extends IService<PositionAllocateHistory> {

    List<PositionAllocateHistory> listPositionAllocateHistoryByKey(Long positionId, Long positionExecutionId);
}
