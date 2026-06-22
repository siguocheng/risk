package com.riskcontrol.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PortfolioOverviewBo {

    @Schema(description = "账户")
    private List<String> accountCodes;

    @Schema(description = "交易员")
    private List<String> tradeNames;

    @Schema(description = "策略")
    private List<String> strategyName;

    @Schema(description = "开始时间，默认是当天")
    private LocalDate startDate;

    @Schema(description = "结束时间，默认是当天")
    private LocalDate endDate;
}
