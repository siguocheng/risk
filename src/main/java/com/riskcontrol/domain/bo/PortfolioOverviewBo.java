package com.riskcontrol.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riskcontrol.util.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Data
public class PortfolioOverviewBo {

    @Schema(description = "账户集合")
    private List<String> accountCodes;

    @Schema(description = "交易员集合")
    private List<String> tradeNames;

    @Schema(description = "策略集合")
    private List<String> strategyNames;

//    @Schema(description = "开始时间，默认当年第一天")
//    private LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());

    @Schema(description = "开始时间，默认当年第一天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate = DateUtil.localDateToString(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));

    @Schema(description = "结束时间，默认是当天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate = DateUtil.localDateToString(LocalDate.now());

    @Schema(description = "对标指数conid集合")
    private List<Integer> referenceIndexConids;
}
