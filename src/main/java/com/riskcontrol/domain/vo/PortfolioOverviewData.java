package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PortfolioOverviewData {

    @Schema(description = "收益数据")
    private List<PortfolioOverviewVo> portfolioOverviewList;

    @Schema(description = "图标")
    private List<ChartVo> chartList;

    @Schema(description = "增长率")
    private BigDecimal growthRate;

    @Schema(description = "delta增长率")
    private BigDecimal deltaGrowthRate;

    @Schema(description = "超额收益率")
    private BigDecimal excessReturn;

    @Schema(description = "收益额")
    private BigDecimal profitAmount;
}
