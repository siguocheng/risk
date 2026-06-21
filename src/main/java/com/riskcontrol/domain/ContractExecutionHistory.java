package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合约成交历史记录实体类
 *
 * @author zpc
 * @date 2026-06-21
 */
@Data
@TableName("contract_execution_history")
@EqualsAndHashCode(callSuper = true)
public class ContractExecutionHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原成交主表contract_execution的主键ID")
    @TableField(value = "contract_execution_id")
    private Long contractExecutionId;

    @Schema(description = "成交ID execId")
    @TableField(value = "exec_id")
    private String execId;

    @Schema(description = "成交数量，支持小数合约")
    @TableField(value = "qty")
    private BigDecimal qty;

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "策略名称")
    @TableField(value = "strategy_name")
    private String strategyName;
}
