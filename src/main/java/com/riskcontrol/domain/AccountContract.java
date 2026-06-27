package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AccountContract extends BaseEntity {

    @Schema(description = "账户编号")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "合约唯一 ID")
    @TableField(value = "conid")
    private int conid;

    @Schema(description = "股票简称")
    @TableField(value = "symbol")
    private String symbol;

    @Schema(description = "类型 STK = 股票（Stock）、OPT期权、FUT期货、FX外汇")
    @TableField(value = "sec_type")
    private String secType;

    // SMART （IB 智能路由）
    @Schema(description = "交易所，NASDAQ 纳斯达克")
    @TableField(value = "exchange")
    private String exchange;

    @Schema(description = "结算币种")
    @TableField(value = "currency")
    private String currency;

    // ==========================================期权
    @Schema(description = "行权价，仅期权 / 权证有效；股票固定为 0.0")
    @TableField(value = "strike")
    private BigDecimal strike;

    @Schema(description = "期权类型：C看涨 / P看跌；股票留空字符串")
    @TableField(value = "opt_right")
    private String optRight;

    @Schema(description = "到期日 / 合约月份，仅期货、期权有效；股票留空")
    @TableField(value = "last_trade_date_or_contract_month")
    private String lastTradeDateOrContractMonth;

    @TableField(value = "last_trade_date")
    private String lastTradeDate;

    // 股票期权：代表 1 张期权对应多少股股票，美股常规 100
    // 期货：合约规格乘数
    // 示例：乘数 = 100 → 行权 1 张期权，交割 100 股标的
    @Schema(description = "合约乘数（每手合约对应标的数量）")
    @TableField(value = "multiplier")
    private String multiplier;

    // ==========================================交易所
    // 主上市交易所（标的原生交易所）
    // exchange：你实际交易的市场
    // primaryExch：标的原生挂牌市场
    @Schema(description = "主上市交易所")
    @TableField(value = "primary_exch")
    private String primaryExch;

    // 交易所本地代码
    @Schema(description = "交易所本地代码")
    @TableField(value = "local_symbol")
    private String localSymbol;

    @Schema(description = "交易品类分组")
    @TableField(value = "trading_class")
    private String tradingClass;

    // ==========================================证券身份标识
    // 外部证券编码类型
    @Schema(description = "外部证券编码类型")
    @TableField(value = "sec_id_type")
    private String secIdType;

    @TableField(value = "sec_id")
    private String secId;

    // 合约中文
    @Schema(description = "合约中文")
    @TableField(value = "description")
    private String description;

    // 发行方 ID
    @Schema(description = "发行方 ID")
    @TableField(value = "issuer_id")
    private String issuerId;

    @Schema(description = "历史数据最新的时间")
    @TableField(value = "contract_market_last_date")
    private LocalDate contractMarketLastDate;

    public AccountContract(){

    }

    public AccountContract(com.ib.client.Contract ibContract) {
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
        this.accountCode = null;
    }
}
