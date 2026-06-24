package com.riskcontrol.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioOverviewDetail {

    private String tradeName;

    private BigDecimal positionQty;
    private BigDecimal avgCost;

    private BigDecimal marketPrice;

    private BigDecimal delta;

    private BigDecimal multiplier;
}
