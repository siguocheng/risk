package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * IB订单回调数据VO
 *
 * @author zpc
 * @date 2026-06-17
 */
@Data
public class IbOrderCallbackVo {

    private int orderId;
    private Contract contract;
    private Order order;
    private OrderState orderState;


    public IbOrderCallbackVo(int orderId, Contract contract, Order order, OrderState orderState){
        this.orderId = orderId;
        this.contract = contract;
        this.order = order;
        this.orderState = orderState;
    }
}
