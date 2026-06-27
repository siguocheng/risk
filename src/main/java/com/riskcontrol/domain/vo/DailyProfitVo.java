package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyProfitVo {

    @Schema(description = "日期")
    private String dailyDate;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "已实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "综合收益")
    private BigDecimal totalPnl;
}
