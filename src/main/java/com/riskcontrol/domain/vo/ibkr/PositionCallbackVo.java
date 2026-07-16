package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.Contract;
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
    private Double avgCost;

    @Schema(description = "未实现盈亏")
    private Double unrealizedPnl;

    @Schema(description = "市场价格")
    private Double marketPrice;

    @Schema(description = "市场值")
    private Double marketValue;

    @Schema(description = "实现盈亏")
    private Double realizedPnl;

    @Schema(description = "合约唯一 ID")
    private Integer conid;

    @Schema(description = "合约唯一 ID")
    private Contract contract;

    @Schema(description = "股票简称")
    private String symbol;

    @Schema(description = "类型 STK=股票、OPT=期权、FUT=期货、FX=外汇")
    private String secType;

    @Schema(description = "合约乘数")
    private String multiplier;
}
