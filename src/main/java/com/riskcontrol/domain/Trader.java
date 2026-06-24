package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易员实体类
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class Trader extends BaseEntity {

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "本金")
    @TableField(value = "capital")
    private BigDecimal capital;
}