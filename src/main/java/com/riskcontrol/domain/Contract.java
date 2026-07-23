package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.util.OccOptionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

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
@TableName("contract")
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity implements Serializable {

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

    @Schema(description = "行权价，仅期权/权证有效；股票固定为 0.0")
    @TableField(value = "strike")
    private BigDecimal strike;

    @Schema(description = "期权类型：C看涨 / P看跌；股票留空字符串")
    @TableField(value = "opt_right")
    private String optRight;

    @Schema(description = "到期日/合约月份，仅期货、期权有效；股票留空")
    @TableField(value = "last_trade_date_or_contract_month")
    private String lastTradeDateOrContractMonth;

    @Schema(description = "最后交易日期")
    @TableField(value = "last_trade_date")
    private String lastTradeDate;

    @Schema(description = "合约乘数（每手合约对应标的数量）")
    @TableField(value = "multiplier")
    private String multiplier;

    @Schema(description = "主上市交易所")
    @TableField(value = "primary_exch")
    private String primaryExch;

    @Schema(description = "交易所本地代码")
    @TableField(value = "local_symbol")
    private String localSymbol;

    @Schema(description = "交易品类分组")
    @TableField(value = "trading_class")
    private String tradingClass;

    @Schema(description = "外部证券编码类型")
    @TableField(value = "sec_id_type")
    private String secIdType;

    @Schema(description = "证券ID")
    @TableField(value = "sec_id")
    private String secId;

    @Schema(description = "合约中文")
    @TableField(value = "description")
    private String description;

    @Schema(description = "发行方 ID")
    @TableField(value = "issuer_id")
    private String issuerId;

    @Schema(description = "行情历史数据最新的时间")
    @TableField(value = "contract_market_last_date")
    private LocalDate contractMarketLastDate;

    @Schema(description = "简称")
    @TableField(value = "short_name")
    private String shortName;


    public Contract(){

    }

    public Contract(com.ib.client.Contract ibContract) {
        // 基础核心字段
        this.conid = ibContract.conid();
        this.symbol = ibContract.symbol();
        this.secType = ibContract.secType().getApiString();
        this.exchange = ibContract.exchange();
        this.currency = ibContract.currency();

        // 交易所相关
        this.primaryExch = ibContract.primaryExch();
        this.localSymbol = ibContract.localSymbol();
        this.tradingClass = ibContract.tradingClass();

        // 证券外部编码
        this.secIdType = ibContract.secIdType().getApiString();
        this.secId = ibContract.secId();

        // 乘数 IB 原生是 String
        this.multiplier = ibContract.multiplier();

        // 期权行权价 转BigDecimal，空值置0
        double ibStrike = ibContract.strike();
        this.strike = BigDecimal.valueOf(ibStrike);

        // 期权看涨看跌
        this.optRight = ibContract.right().getApiString();

        // 到期年月/合约月份
        this.lastTradeDateOrContractMonth = ibContract.lastTradeDateOrContractMonth();
        // IB无单独lastTradeDate，复用到期字段或置空
        this.lastTradeDate = ibContract.lastTradeDateOrContractMonth();

        // IB Contract 无 description、issuerId、accountCode、contractHistoryLastDate
        // 这几个业务字段由业务层单独赋值，构造器仅映射IB原生数据
        this.description = ibContract.description();
        this.issuerId = ibContract.issuerId();

        // 期权
        if (StringUtils.isNotEmpty(localSymbol) && secType.equals(SetTypeEnum.OPT.getCode())) {
            this.shortName = OccOptionUtil.occToShortName(localSymbol);
        }
//        else if (secType.equals(SetTypeEnum.FUT.getCode())){
//            this.shortName = OccOptionUtil.futShortName(symbol, lastTradeDate);
//        }

    }
}