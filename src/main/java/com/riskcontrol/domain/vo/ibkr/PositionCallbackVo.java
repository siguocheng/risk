package com.riskcontrol.domain.vo.ibkr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionCallbackVo {

    @Schema(description = "账号编号")
    private String accountCode;

    private String modelCode;

    @Schema(description = "持仓股数")
    private BigDecimal position;

    @Schema(description = "平均成本价")
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    private BigDecimal unrealizedPnl;

    private ContractCallbackVo contract;

    @Schema(description = "市场价格")
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    private BigDecimal realizedPnl;
}
