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

    @Schema(description = "日收益")
    @TableField(value = "daily_pnl")
    private BigDecimal dailyPnl;

    @Schema(description = "持仓股数")
    @TableField(value = "cal_position_qty")
    private BigDecimal calPositionQty;

    @Schema(description = "平均成本价")
    @TableField(value = "cal_avg_cost")
    private BigDecimal calAvgCost;

    @Schema(description = "未实现盈亏")
    @TableField(value = "cal_unrealized_pnl")
    private BigDecimal calUnrealizedPnl;

    @Schema(description = "实现盈亏")
    @TableField(value = "cal_realized_pnl")
    private BigDecimal calRealizedPnl;

    @Schema(description = "计算日未实现收益")
    @TableField(value = "cal_daily_unrealized_pnl")
    private BigDecimal calDailyUnrealizedPnl;

    @Schema(description = "计算日已实现收益")
    @TableField(value = "cal_daily_realized_pnl")
    private BigDecimal calDailyRealizedPnl;

    @Schema(description = "最后一次交易操作的id")
    @TableField(value = "position_execution_id")
    private Long positionExecutionId;

    @Schema(description = "累计佣金及各项费用")
    @TableField(value = "acc_commission_and_fees")
    private BigDecimal accCommissionAndFees;

    @Schema(description = "日收益的日期")
    @TableField(value = "pnl_daily_date")
    private String pnlDailyDate;
}
