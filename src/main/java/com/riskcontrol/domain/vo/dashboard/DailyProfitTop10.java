package com.riskcontrol.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收益排行前10返回结果
 *
 * @author zpc
 * @date 2026-07-11
 */
@Data
public class DailyProfitTop10 {

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "合约简称")
    private String symbol;

    @Schema(description = "实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "总收益（实现盈亏+未实现盈亏）")
    private BigDecimal totalPnl;
}