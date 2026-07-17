package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("trader_capital")
public class TraderCapital extends BaseEntity {

    @Schema(description = "日期")
    @TableField(value = "daily_date")
    private String dailyDate;

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "最新本金")
    @TableField(value = "capital")
    private BigDecimal capital;
}