package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyReturnRateVo {

    @Schema(description = "合约ID")
    private Integer conid;

    @Schema(description = "股票简称")
    private String symbol;

    @Schema(description = "日期")
    private String dailyDate;

    @Schema(description = "收盘价")
    private BigDecimal priceClose;

    @Schema(description = "日收益")
    private BigDecimal dailyReturn;

    @Schema(description = "日收益率")
    private BigDecimal dailyReturnRate;
}
