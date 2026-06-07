package com.riskcontrol.domain.vo.ibkr;

import com.riskcontrol.enums.AccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountSummaryVo {

    private final static String LEDGER_ALL = "$LEDGER:ALL";
    private final static String LEDGER_USD = "$LEDGER:USD";
    private final static String LEDGER_HKD = "$LEDGER:HKD";
    private final static String LEDGER_BASE = "$LEDGER:BASE";

    private final static String USD_SUFFIX = "USD";
    private final static String HKD_SUFFIX = "HKD";
    private final static String BASE_SUFFIX = "BASE";

    @Schema(description = "账户id")
    private String ACCOUNT_ID;

    @Schema(description = "账户类型")
    private String AccountType;

    @Schema(description = "账户类型名称")
    private String AccountTypName;

    @Schema(description = "净流动性")
    private String NetLiquidation; // 净流动性

    @Schema(description = "总现金")
    private String TotalCashValue; // 总现金

    private String SettledCash;

    @Schema(description = "应计现金：应付 / 应收利息净额")
    private String AccruedCash; // 应计现金：应付 / 应收利息净额

    @Schema(description = "购买力")
    private String BuyingPower; // 购买力

    private String EquityWithLoanValue;

    private String PreviousEquityWithLoanValue;

    @Schema(description = "持仓总市值")
    private String GrossPositionValue; // 持仓总市值

    private String ReqTEquity;

    private String ReqTMargin;

    private String SMA;

    @Schema(description = "初始保证金")
    private String InitMarginReq; // 初始保证金

    @Schema(description = "维持保证金要求")
    private String MaintMarginReq; // 维持保证金要求

    @Schema(description = "可用资金")
    private String AvailableFunds; // 可用资金

    @Schema(description = "剩余流动性")
    private String ExcessLiquidity; // 剩余流动性

    @Schema(description = "安全垫比例")
    private String Cushion; // 安全垫比例

    private String FullInitMarginReq;

    private String FullMaintMarginReq;

    private String FullAvailableFunds;

    private String FullExcessLiquidity;

    private String LookAheadExcessLiquidity;
    private String HighestSeverity;

    @Schema(description = "日内交易次数剩余")
    private String DayTradesRemaining; // 日内交易次数剩余
    private String Leverage;

    private String LookAheadNextChange;

    private String LookAheadInitMarginReq; // 前瞻保证金（考虑隔夜 / 到期变化，你这里无变化）
    private String LookAheadMaintMarginReq;
    private String LookAheadAvailableFunds;

    @Schema(description = "当日盈亏")
    private Double dailyPnL;

    @Schema(description = "未实现盈亏")
    private Double unrealizedPnL;

    @Schema(description = "已实现盈亏")
    private Double realizedPnL;

    @Schema(description = "当日盈亏率")
    private Double dailyPnlPct;

    public Double getDailyPnlPct(){
        return dailyPnL / Double.valueOf(NetLiquidation);
    }

    public String getAccountTypName(){
        return AccountTypeEnum.getValueByKey(this.AccountType);
    }
}
