package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TraderModifiedHistory extends BaseEntity {

    @Schema(description = "交易员ID")
    @TableField(value = "trader_id")
    private Long traderId;

    @Schema(description = "原交易员名称")
    @TableField(value = "org_trader_name")
    private String orgTraderName;

    @Schema(description = "最新本金")
    @TableField(value = "org_capital")
    private BigDecimal orgCapital;

    @Schema(description = "当前交易员名称")
    @TableField(value = "current_trader_name")
    private String currentTraderName;

    @Schema(description = "当前本金")
    @TableField(value = "current_capital")
    private BigDecimal currentCapital;
}