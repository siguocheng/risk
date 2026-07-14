package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionExecutionInOut;

/**
 * 交易出入库Service接口
 *
 * @author zpc
 * @date 2026-07-05
 */
public interface IPositionExecutionInOutService extends IService<PositionExecutionInOut> {

    Boolean saveOrUpdatePositionExecutionInOut(PositionExecutionInOut positionExecutionInOut);
}