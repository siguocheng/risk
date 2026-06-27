package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合约实体类
 *
 * @author zpc
 * @date 2026-06-26
 */
@Data
@TableName("contract_market")
@EqualsAndHashCode(callSuper = true)
public class ContractMarket extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "合约唯一 ID")
    @TableField(value = "conid")
    private Integer conid;

    @Schema(description = "股票简称")
    @TableField(value = "symbol")
    private String symbol;

    @Schema(description = "类型 STK=股票、OPT=期权、FUT=期货、FX=外汇")
    @TableField(value = "sec_type")
    private String secType;

    @Schema(description = "交易所，NASDAQ 纳斯达克")
    @TableField(value = "exchange")
    private String exchange;

    @Schema(description = "结算币种")
    @TableField(value = "currency")
    private String currency;

    @Schema(description = "行情历史数据最新的时间")
    @TableField(value = "contract_market_last_date")
    private LocalDate contractMarketLastDate;

    @Schema(description = "对标指数 1是")
    @TableField(value = "reference_index")
    private Integer referenceIndex;

    @Schema(description = "交易所本地代码")
    @TableField(value = "local_symbol")
    private String localSymbol;

    public ContractMarket(){

    }

    public ContractMarket(com.ib.client.Contract ibContract) {
        // 基础核心字段
        this.conid = ibContract.conid();
        this.symbol = ibContract.symbol();
        this.secType = ibContract.secType().getApiString();
        this.exchange = ibContract.exchange();
        this.currency = ibContract.currency();
        this.localSymbol = ibContract.localSymbol();
    }
}