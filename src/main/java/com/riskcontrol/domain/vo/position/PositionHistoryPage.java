package com.riskcontrol.domain.vo.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionHistoryPage {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "持仓日期")
    private String positionDate;

    @Schema(description = "合约id")
    private Integer conid;

    @Schema(description = "合约简称")
    private String symbol;

    @Schema(description = "账号编号")
    private String accountCode;

    @Schema(description = "模型代码")
    private String modelCode;

    @Schema(description = "持仓股数")
    private BigDecimal positionQty;

    @Schema(description = "平均成本价")
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    @Schema(description = "市场价格")
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    private BigDecimal realizedPnl;

    @Schema(description = "日收益")
    private BigDecimal dailyPnl;

    @Schema(description = "修改时间")
    private String modifiedTime;

    @Schema(description = "合约类型")
    private String secType;

    @Schema(description = "合约乘数")
    private String multiplier;
}