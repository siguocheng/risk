package com.riskcontrol.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PeriodSegment {

    // 区间期初总资产
    private BigDecimal startAsset;
    // 区间期末总资产
    private BigDecimal endAsset;
    // 区间净现金流：入金为正，出金为负
    private BigDecimal cashFlow;

    public PeriodSegment(BigDecimal startAsset, BigDecimal endAsset, BigDecimal cashFlow) {
        this.startAsset = startAsset;
        this.endAsset = endAsset;
        this.cashFlow = cashFlow;
    }
}
