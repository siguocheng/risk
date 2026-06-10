package com.riskcontrol.domain.vo.ibkr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionVo {

    @Schema(description = "账号编号")
    private String accountCode;

    private String modelCode;

    @Schema(description = "持仓股数")
    private BigDecimal position;

    @Schema(description = "平均成本价")
    private Double avgCost;

    @Schema(description = "未实现盈亏")
    private Double unrealizedPnl;

    private ContractVo contract;

    @Schema(description = "市场价格")
    private Double marketPrice;

    @Schema(description = "市场值")
    private Double marketValue;

    @Schema(description = "实现盈亏")
    private Double realizedPnl;
}
