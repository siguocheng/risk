package com.riskcontrol.domain.vo.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓分页返回VO
 *
 * @author zpc
 * @date 2026-06-22
 */
@Data
public class PositionPage {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "合约ID")
    private Integer conid;

    @Schema(description = "账号代码")
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

    @Schema(description = "未分配数量")
    private BigDecimal remainQty;

    @Schema(description = "资产名称")
    private String symbol;
}
