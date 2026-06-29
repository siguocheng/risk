package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioOverviewVo {

    @Schema(description = "交易员")
    private String traderName;

    @Schema(description = "本年本金")
    private BigDecimal yearCapital;

    @Schema(description = "市值")
    private BigDecimal grossPositionValue;

    @Schema(description = "delta敞口")
    private BigDecimal deltaExposure;

    @Schema(description = "现金")
    private BigDecimal availableFunds;

    @Schema(description = "总市值")
    private BigDecimal sumGrossPositionValue;

    @Schema(description = "贷款")
    private BigDecimal loan;

    @Schema(description = "已实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "费用")
    private BigDecimal cost;

    @Schema(description = "总盈亏")
    private BigDecimal pnl;

    @Schema(description = "增长率")
    private BigDecimal growthRate = new BigDecimal("0.5");

    @Schema(description = "delta增长率")
    private BigDecimal deltaGrowthRate = new BigDecimal("0.6");;

    @Schema(description = "超额收益率")
    private BigDecimal excessReturn = new BigDecimal("0.7");;

}
