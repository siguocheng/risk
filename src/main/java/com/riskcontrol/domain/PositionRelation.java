package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 策略和交易员和账号和持仓之间的关系实体类
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
@TableName("position_relation")
@Schema(description = "策略和交易员和账号和持仓之间的关系")
public class PositionRelation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
