package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioOverviewData {

    @Schema(description = "收益数据")
    private List<PortfolioOverviewVo> portfolioOverviewList;

    @Schema(description = "图标")
    private List<ChartVo> chartList;
}
