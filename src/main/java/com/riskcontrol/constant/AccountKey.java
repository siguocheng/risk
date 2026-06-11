package com.riskcontrol.constant;

import java.util.Set;
import java.util.TreeSet;

public class AccountKey {

    public final static Set<String> singleKey = new TreeSet<>();

    public final static Set<String> multiKey = new TreeSet<>();

    static {
        singleKey.add("AccountCode");
        singleKey.add("AccountReady");
        singleKey.add("AccountType");
        singleKey.add("Cushion");
        singleKey.add("LookAheadNextChange");
        singleKey.add("NLVAndMarginInReview");
        singleKey.add("SettledCashByDate");

        // _HKD
        singleKey.add("AccruedDividend");
        singleKey.add("AvailableFunds");
        singleKey.add("Billable");
        singleKey.add("EquityWithLoanValue");
        singleKey.add("ExcessLiquidity");
        singleKey.add("FullAvailableFunds");
        singleKey.add("FullExcessLiquidity");
        singleKey.add("FullInitMarginReq");
        singleKey.add("FullMaintMarginReq");
        singleKey.add("Guarantee");
        singleKey.add("IncentiveCoupons");
        singleKey.add("IndianStockHaircut");
        singleKey.add("InitMarginReq");
        singleKey.add("LookAheadAvailableFunds");
        singleKey.add("LookAheadExcessLiquidity");
        singleKey.add("LookAheadInitMarginReq");
        singleKey.add("LookAheadMaintMarginReq");
        singleKey.add("MaintMarginReq");
        singleKey.add("NetLiquidation");
        singleKey.add("NetLiquidationUncertainty");
        singleKey.add("PASharesValue");
        singleKey.add("PhysicalCertificateValue");
        singleKey.add("PostExpirationExcess");
        singleKey.add("PostExpirationMargin");
        singleKey.add("TotalCashValue");
        singleKey.add("TotalDebitCardPendingCharges");
        singleKey.add("BuyingPower");

        multiKey.add("AccountOrGroup");
        multiKey.add("AccruedCash");
        multiKey.add("CashBalance");
        multiKey.add("CorporateBondValue");
        multiKey.add("Cryptocurrency");
        multiKey.add("Currency");
        multiKey.add("ExchangeRate");
        multiKey.add("FundValue");
        multiKey.add("FutureOptionValue");
        multiKey.add("FuturesPNL");
        multiKey.add("FxCashBalance");
        multiKey.add("IssuerOptionValue");
        multiKey.add("MoneyMarketFundValue");
        multiKey.add("MutualFundValue");
        multiKey.add("NetDividend");
        multiKey.add("NetLiquidationByCurrency");
        multiKey.add("OptionMarketValue");
        multiKey.add("RealCurrency");
        multiKey.add("RealizedPnL");
        multiKey.add("StockMarketValue");
        multiKey.add("TBillValue");
        multiKey.add("TBondValue");
        multiKey.add("TotalCashBalance");
        multiKey.add("UnrealizedPnL");
        multiKey.add("WarrantValue");
    }
}
