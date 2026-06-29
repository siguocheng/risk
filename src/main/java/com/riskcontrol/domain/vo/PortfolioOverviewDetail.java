package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioOverviewDetail {

    private String traderName;

    private BigDecimal capital;

    private BigDecimal positionQty;
    private BigDecimal avgCost;

    private BigDecimal marketPrice;

    private BigDecimal delta;

    private BigDecimal multiplier;

    private String secType;

    @Schema(description = "已实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    private BigDecimal commissionAndFees;
}
