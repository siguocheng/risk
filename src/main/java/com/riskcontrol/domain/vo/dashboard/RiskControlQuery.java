package com.riskcontrol.domain.vo.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RiskControlQuery {

    @Schema(description = "账户集合")
    private List<String> accountCodes;

    @Schema(description = "交易员集合")
    private List<String> tradeNames;

    @Schema(description = "策略集合")
    private List<String> strategyNames;

    @Schema(description = "开始时间，默认当年第一天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDat;

    @Schema(description = "结束时间，默认是当天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;
}
