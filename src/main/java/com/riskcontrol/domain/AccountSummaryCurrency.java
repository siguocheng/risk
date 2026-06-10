package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IBKR账户分币种资产明细
 */
@Data
@Schema(description = "IBKR账户分币种资产明细")
public class AccountSummaryCurrency extends BaseEntity {

    @Schema(description = "账户编号")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "应计现金")
    @TableField(value = "accrued_cash")
    private BigDecimal accruedCash;

    @Schema(description = "现金余额")
    @TableField(value = "cash_balance")
    private BigDecimal cashBalance;

    @Schema(description = "公司债券价值")
    @TableField(value = "corporate_bond_value")
    private BigDecimal corporateBondValue;

    @Schema(description = "加密资产价值")
    @TableField(value = "cryptocurrency")
    private BigDecimal cryptocurrency;

    @Schema(description = "币种代码")
    @TableField(value = "currency")
    private String currency;

    @Schema(description = "汇率")
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    @Schema(description = "基金价值")
    @TableField(value = "fund_value")
    private BigDecimal fundValue;

    @Schema(description = "期货期权价值")
    @TableField(value = "future_option_value")
    private BigDecimal futureOptionValue;

    @Schema(description = "期货盈亏")
    @TableField(value = "futures_pnl")
    private BigDecimal futuresPnl;

    @Schema(description = "外汇现金余额")
    @TableField(value = "fx_cash_balance")
    private BigDecimal fxCashBalance;

    @Schema(description = "发行方期权价值")
    @TableField(value = "issuer_option_value")
    private BigDecimal issuerOptionValue;

    @Schema(description = "货币基金价值")
    @TableField(value = "money_market_fund_value")
    private BigDecimal moneyMarketFundValue;

    @Schema(description = "共同基金价值")
    @TableField(value = "mutual_fund_value")
    private BigDecimal mutualFundValue;

    @Schema(description = "净股息")
    @TableField(value = "net_dividend")
    private BigDecimal netDividend;

    @Schema(description = "分币种净清算价值")
    @TableField(value = "net_liquidation_by_currency")
    private BigDecimal netLiquidationByCurrency;

    @Schema(description = "期权市值")
    @TableField(value = "option_market_value")
    private BigDecimal optionMarketValue;

    @Schema(description = "实际币种")
    @TableField(value = "real_currency")
    private String realCurrency;

    @Schema(description = "已实现盈亏")
    @TableField(value = "realized_pnl")
    private BigDecimal realizedPnl;

    @Schema(description = "股票市值")
    @TableField(value = "stock_market_value")
    private BigDecimal stockMarketValue;

    @Schema(description = "短期国债价值")
    @TableField(value = "t_bill_value")
    private BigDecimal tBillValue;

    @Schema(description = "长期国债价值")
    @TableField(value = "t_bond_value")
    private BigDecimal tBondValue;

    @Schema(description = "现金总额")
    @TableField(value = "total_cash_balance")
    private BigDecimal totalCashBalance;

    @Schema(description = "未实现盈亏")
    @TableField(value = "unrealized_pnl")
    private BigDecimal unrealizedPnl;

    @Schema(description = "认股权证价值")
    @TableField(value = "warrant_value")
    private BigDecimal warrantValue;
}
