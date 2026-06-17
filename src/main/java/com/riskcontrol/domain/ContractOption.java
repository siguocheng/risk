package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 期权合约希腊值数据实体类
 *
 * @author zpc
 * @date 2026-06-17
 */
@Data
public class ContractOption extends BaseEntity {

    @Schema(description = "合约唯一ID")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "隐含波动率 IV")
    @TableField(value = "implied_vol")
    private BigDecimal impliedVol;

    @Schema(description = "德尔塔")
    @TableField(value = "delta")
    private BigDecimal delta;

    @Schema(description = "期权理论价")
    @TableField(value = "opt_price")
    private BigDecimal optPrice;

    @Schema(description = "股息现值")
    @TableField(value = "pv_dividend")
    private BigDecimal pvDividend;

    @Schema(description = "伽马")
    @TableField(value = "gamma")
    private BigDecimal gamma;

    @Schema(description = "维加")
    @TableField(value = "vega")
    private BigDecimal vega;

    @Schema(description = "西塔")
    @TableField(value = "theta")
    private BigDecimal theta;

    @Schema(description = "标的价格")
    @TableField(value = "und_price")
    private BigDecimal undPrice;
}
