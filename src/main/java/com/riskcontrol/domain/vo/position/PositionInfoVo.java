package com.riskcontrol.domain.vo.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓信息VO（包含已分配数量）
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
public class PositionInfoVo {

    @Schema(description = "持仓ID")
    private Long id;

    @Schema(description = "合约ID")
    private Integer conid;

    @Schema(description = "账号代码")
    private String accountCode;

    @Schema(description = "模型代码")
    private String modelCode;

    @Schema(description = "总持仓数量")
    private BigDecimal totalPositionQty;

    @Schema(description = "已分配数量")
    private BigDecimal allocatedPositionQty;

    @Schema(description = "剩余可分配数量")
    private BigDecimal remainingPositionQty;

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
}
