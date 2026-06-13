package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合约历史行情表
 */
@Data
@TableName("contract_history")
@Schema(description = "合约历史行情表")
public class ContractHistory extends BaseEntity{

    @TableField("conid")
    @Schema(description = "合约唯一ID")
    private Integer conid;

    @TableField("time")
    @Schema(description = "行情时间")
    private String time;

    @TableField("price_open")
    @Schema(description = "开盘价")
    private BigDecimal priceOpen;

    @TableField("price_high")
    @Schema(description = "最高价")
    private BigDecimal priceHigh;

    @TableField("price_low")
    @Schema(description = "最低价")
    private BigDecimal priceLow;

    @TableField("price_close")
    @Schema(description = "收盘价")
    private BigDecimal priceClose;

    @TableField("price_wap")
    @Schema(description = "加权平均价WAP")
    private BigDecimal priceWap;

    @TableField("deal_count")
    @Schema(description = "成交笔数")
    private Integer dealCount;

    @TableField("deal_volume")
    @Schema(description = "成交量")
    private Long dealVolume;
}