package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓列表实体类
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@TableName("position")
@EqualsAndHashCode(callSuper = true)
public class Position extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "合约id")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "股票简称")
    @TableField(value = "symbol")
    private String symbol;

    @Schema(description = "账号编号")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "模型代码")
    @TableField(value = "model_code")
    private String modelCode;

    @Schema(description = "持仓股数")
    @TableField(value = "position_qty")
    private BigDecimal positionQty;

    @Schema(description = "平均成本价")
    @TableField(value = "avg_cost")
    private BigDecimal avgCost;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "市场价格")
    @TableField(value = "market_price")
    private BigDecimal marketPrice;

    @Schema(description = "市场值")
    @TableField(value = "market_value")
    private BigDecimal marketValue;

    @Schema(description = "实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;
}
