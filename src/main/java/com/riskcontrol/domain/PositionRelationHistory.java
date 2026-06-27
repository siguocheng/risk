package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 策略和交易员和账号和持仓之间的关系历史实体类
 *
 * @author zpc
 * @date 2026-06-26
 */
@Data
@TableName("position_relation_history")
@EqualsAndHashCode(callSuper = true)
public class PositionRelationHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "数据日期")
    @TableField(value = "data_date")
    private LocalDate dataDate;

    @Schema(description = "账号id")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "合约id")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "策略名称")
    @TableField(value = "strategy_name")
    private String strategyName;

    @Schema(description = "交易员")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "持仓股数")
    @TableField(value = "position_qty")
    private BigDecimal positionQty;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;
}
