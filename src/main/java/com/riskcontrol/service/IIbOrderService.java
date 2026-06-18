package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.IbOrder;

/**
 * IB TWS 期权/股票订单Service接口
 *
 * @author zpc
 * @date 2026-06-17
 */
public interface IIbOrderService extends IService<IbOrder> {
    boolean saveOrUpdateByPermId(IbOrder ibOrder);
}