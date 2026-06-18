package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.Decimal;
import lombok.Data;

@Data
public class OrderStatusCallbackVo {
    private int orderId;
    private String status;
    private Decimal filled;
    private Decimal remaining;
    private double avgFillPrice;
    private long permId;
    private int parentId;
    private double lastFillPrice;
    private int clientId;
    private String whyHeld;
    private double mktCapPrice;

    // 无参构造（可选，序列化/反射常用）
    public OrderStatusCallbackVo() {
    }

    public OrderStatusCallbackVo(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice){
        this.orderId = orderId;
        this.status = status;
        this.filled = filled;
        this.remaining = remaining;
        this.avgFillPrice = avgFillPrice;
        this.permId = permId;
        this.parentId = parentId;
        this.lastFillPrice = lastFillPrice;
        this.clientId = clientId;
        this.whyHeld = whyHeld;
        this.mktCapPrice = mktCapPrice;
    }
}
