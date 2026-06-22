package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 持仓分配历史实体类
 *
 * @author zpc
 * @date 2026-06-22
 */
@Data
@TableName("position_allocate_history")
@EqualsAndHashCode(callSuper = true)
public class PositionAllocateHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "持仓表position的id")
    @TableField(value = "position_id")
    private Long positionId;

    @Schema(description = "交易持仓表position_execution的id")
    @TableField(value = "position_execution_id")
    private Long positionExecutionId;

    @Schema(description = "账号代码")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "合约ID")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "策略名称")
    @TableField(value = "strategy_name")
    private String strategyName;

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "分配数量（正数增加，负数减少）")
    @TableField(value = "allocate_qty")
    private BigDecimal allocateQty;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "已实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;
}
