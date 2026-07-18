package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.riskcontrol.util.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合约历史行情表
 */
@Data
@TableName("contract_market_history")
@Schema(description = "合约历史行情表")
public class ContractMarketHistory extends BaseEntity{

    @TableField("conid")
    @Schema(description = "合约唯一ID")
    private Integer conid;

    @TableField("symbol")
    @Schema(description = "股票简称")
    private String symbol;

    @TableField("daily_date")
    @Schema(description = "行情时间")
    private String dailyDate;

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

    @Schema(description = "隐含波动率 IV")
    @TableField(value = "implied_vol")
    private BigDecimal impliedVol;

    @Schema(description = "delta")
    @TableField(value = "delta")
    private BigDecimal delta;

    @Schema(description = "gamma")
    @TableField(value = "gamma")
    private BigDecimal gamma;

    @Schema(description = "vega")
    @TableField(value = "vega")
    private BigDecimal vega;

    @Schema(description = "theta")
    @TableField(value = "theta")
    private BigDecimal theta;

    @Schema(description = "持仓的市场价格")
    @TableField(value = "position_market_price")
    private BigDecimal positionMarketPrice;

    @Schema(description = "类型 STK=股票、OPT=期权、FUT=期货、FX=外汇")
    @TableField(value = "sec_type")
    private String secType;
}