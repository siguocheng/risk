package com.riskcontrol.task;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ib.client.EClientSocket;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.constant.AccountKey;
import com.riskcontrol.dao.AccountCurrencyMapper;
import com.riskcontrol.domain.*;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryCallbackVO;
import com.riskcontrol.domain.vo.ibkr.ContractCallbackVo;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import com.riskcontrol.service.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class IbReconnectTask {

    @Resource
    private EClientSocket m_client;

    @Value("${ibkr.host}")
    private String host;
    @Value("${ibkr.port}")
    private int port;
    @Value("${ibkr.clientid}")
    private int clientId;

    @Resource
    IAccountCurrencyService accountCurrencyService;

    @Resource
    IAccountDailyPnlService accountDailyPnlService;

    @Resource
    IContractService contractService;

    @Resource
    IAccountSummaryService accountSummaryService;

    @Resource
    IPositionService positionService;

    @Resource
    IAccountSummaryCurrencyService accountSummaryCurrencyService;


    @Resource
    IbkrSynConfig ibkrSynConfig;

    // 30秒检测一次连接状态
    @Scheduled(fixedDelay = 30000)
    public void checkConnect(){
        if(!m_client.isConnected()){
            m_client.eConnect(host,port,clientId);
            System.out.println("IB触发自动重连:"+m_client.isConnected());
        }
    }

    // 维护账号对应的基本币
    public void synAccountCurrency() throws ExecutionException, InterruptedException, TimeoutException {

        int reqId = 999;

        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId,future);

        String group = "All";
        // AccountType,NetLiquidation,TotalCashValue,SettledCash,AccruedCash,BuyingPower,EquityWithLoanValue,PreviousEquityWithLoanValue,GrossPositionValue,ReqTEquity,ReqTMargin,SMA,InitMarginReq,MaintMarginReq,AvailableFunds,ExcessLiquidity,Cushion,FullInitMarginReq,FullMaintMarginReq,FullAvailableFunds,FullExcessLiquidity,LookAheadNextChange,LookAheadInitMarginReq ,LookAheadMaintMarginReq,LookAheadAvailableFunds,LookAheadExcessLiquidity,HighestSeverity,DayTradesRemaining,Leverage
        m_client.reqAccountSummary(reqId, group, "NetLiquidation");

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        List<AccountSummaryCallbackVO> result = (List<AccountSummaryCallbackVO>) obj;
        m_client.cancelAccountSummary(reqId);

        for (AccountSummaryCallbackVO accountSummaryCallbackVO : result) {
            AccountCurrency accountCurrency = new AccountCurrency();
            accountCurrency.setCurrency(accountSummaryCallbackVO.getCurrency());
            accountCurrency.setAccountCode(accountSummaryCallbackVO.getAccount());
            accountCurrencyService.saveOrUpdateAccountCurrency(accountCurrency);
        }
    }

    public void synAccount() throws ExecutionException, InterruptedException, TimeoutException {
        List<AccountCurrency> accountList = accountCurrencyService.list();
        for (AccountCurrency accountCurrency : accountList) {
            String accountCode = accountCurrency.getAccountCode();
            String currency = accountCurrency.getCurrency();
            CompletableFuture<Object> future = new CompletableFuture<>();
            ibkrSynConfig.FUTURE_MAP.put(accountCode, future);

            // updateAccountValue
            // updatePortfolio
            // updateAccountTime
            // accountDownloadEnd
            m_client.reqAccountUpdates(true, accountCode);

            Map<String,Object> result  = (Map<String,Object>)future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);

            // 持仓信息
            List<PositionCallbackVo> positions = (List<PositionCallbackVo>)result.remove("position");

            for (PositionCallbackVo positionCallbackVo : positions) {
                Position position = new Position();
                BeanUtils.copyProperties(positionCallbackVo, position);
                positionService.saveOrUpdatePosition(position);

                ContractCallbackVo contractCallbackVo = positionCallbackVo.getContract();

                Contract contract = new Contract();
                BeanUtils.copyProperties(contractCallbackVo, contract);
                contractService.saveOrUpdateContract(contract);
            }

            // 将key中带-P,-S后缀的key移除掉
            result.entrySet().removeIf(data -> data.getKey().contains("-P") || data.getKey().contains("-S"));

            Map<String,Object> singleKeyMap = new HashMap<>();
            Map<String,Object> multiKeyMap = new HashMap<>();
            Map<String,Object> currencyMap = new HashMap<>();

            for (String s : result.keySet()) {
                String[] s1 = s.split("_");

                if (AccountKey.singleKey.contains(s1[0])) {
                    singleKeyMap.put(s1[0], result.get(s));
                }
                if (AccountKey.multiKey.contains(s1[0])) {
                    multiKeyMap.put(s, result.get(s));
                    currencyMap.put(s1[1], null);
                }
            }

            AccountSummary accountSummary = new AccountSummary();
            accountSummary.setAccountCode(accountCode);
            accountSummary.setAccountReady((boolean)singleKeyMap.get("AccountReady"));
            accountSummary.setAccountType((String)singleKeyMap.get("AccountType"));
            accountSummary.setCushion(new BigDecimal((String)singleKeyMap.get("Cushion")));
            accountSummary.setLookAheadNextChange((Long)singleKeyMap.get("LookAheadNextChange"));
            accountSummary.setNlvAndMarginInReview((boolean)singleKeyMap.get("NLVAndMarginInReview"));
            accountSummary.setSettledCashByDate((String)singleKeyMap.get("SettledCashByDate"));
            accountSummary.setAccruedDividend(new BigDecimal((String)singleKeyMap.get("AccruedDividend")));
            accountSummary.setAvailableFunds(new BigDecimal((String)singleKeyMap.get("AvailableFunds")));
            accountSummary.setBillable(new BigDecimal((String)singleKeyMap.get("Billable")));
            accountSummary.setEquityWithLoanValue(new BigDecimal((String)singleKeyMap.get("EquityWithLoanValue")));
            accountSummary.setExcessLiquidity(new BigDecimal((String)singleKeyMap.get("ExcessLiquidity")));
            accountSummary.setFullAvailableFunds(new BigDecimal((String)singleKeyMap.get("FullAvailableFunds")));
            accountSummary.setFullExcessLiquidity(new BigDecimal((String)singleKeyMap.get("FullExcessLiquidity")));
            accountSummary.setFullInitMarginReq(new BigDecimal((String)singleKeyMap.get("FullInitMarginReq")));

            accountSummary.setFullMaintMarginReq(new BigDecimal((String)singleKeyMap.get("FullMaintMarginReq")));
            accountSummary.setGrossPositionValue(new BigDecimal((String)singleKeyMap.get("GrossPositionValue")));
            accountSummary.setGuarantee(new BigDecimal((String)singleKeyMap.get("Guarantee")));
            accountSummary.setIncentiveCoupons(new BigDecimal((String)singleKeyMap.get("IncentiveCoupons")));
            accountSummary.setIndianStockHaircut(new BigDecimal((String)singleKeyMap.get("IndianStockHaircut")));

            accountSummary.setInitMarginReq(new BigDecimal((String)singleKeyMap.get("InitMarginReq")));
            accountSummary.setLookAheadAvailableFunds(new BigDecimal((String)singleKeyMap.get("LookAheadAvailableFunds")));
            accountSummary.setLookAheadExcessLiquidity(new BigDecimal((String)singleKeyMap.get("LookAheadExcessLiquidity")));
            accountSummary.setLookAheadInitMarginReq(new BigDecimal((String)singleKeyMap.get("LookAheadInitMarginReq")));
            accountSummary.setLookAheadMaintMarginReq(new BigDecimal((String)singleKeyMap.get("LookAheadMaintMarginReq")));

            accountSummary.setMaintMarginReq(new BigDecimal((String)singleKeyMap.get("MaintMarginReq")));
            accountSummary.setNetLiquidation(new BigDecimal((String)singleKeyMap.get("NetLiquidation")));
            accountSummary.setNetLiquidationUncertainty(new BigDecimal((String)singleKeyMap.get("NetLiquidationUncertainty")));
            accountSummary.setPaSharesValue(new BigDecimal((String)singleKeyMap.get("PASharesValue")));
            accountSummary.setPhysicalCertificateValue(new BigDecimal((String)singleKeyMap.get("PhysicalCertificateValue")));
            accountSummary.setPostExpirationExcess(new BigDecimal((String)singleKeyMap.get("PostExpirationExcess")));
            accountSummary.setPostExpirationMargin(new BigDecimal((String)singleKeyMap.get("PostExpirationMargin")));
            accountSummary.setTotalDebitCardPendingCharges(new BigDecimal((String)singleKeyMap.get("TotalDebitCardPendingCharges")));
            accountSummary.setBuyingPower(new BigDecimal((String)singleKeyMap.get("BuyingPower")));

            accountSummaryService.saveOrUpdateAccountSummary(accountSummary);

            for (String s : currencyMap.keySet()) {
                AccountSummaryCurrency accountSummaryCurrency = new AccountSummaryCurrency();
                accountSummaryCurrency.setCurrency(s);
                accountSummaryCurrency.setAccountCode(accountCode);
                accountSummaryCurrency.setAccountOrGroup((String)multiKeyMap.get("AccountOrGroup" + "_" + s));
                accountSummaryCurrency.setAccruedCash(new BigDecimal((String)multiKeyMap.get("AccruedCash" + "_" + s)));
                accountSummaryCurrency.setCashBalance(new BigDecimal((String)multiKeyMap.get("CashBalance" + "_" + s)));
                accountSummaryCurrency.setCorporateBondValue(new BigDecimal((String)multiKeyMap.get("CorporateBondValue" + "_" + s)));
                accountSummaryCurrency.setCryptocurrency(new BigDecimal((String)multiKeyMap.get("Cryptocurrency" + "_" + s)));
                accountSummaryCurrency.setExchangeRate(new BigDecimal((String)multiKeyMap.get("ExchangeRate" + "_" + s)));
                accountSummaryCurrency.setFundValue(new BigDecimal((String)multiKeyMap.get("FundValue" + "_" + s)));
                accountSummaryCurrency.setFutureOptionValue(new BigDecimal((String)multiKeyMap.get("FutureOptionValue" + "_" + s)));
                accountSummaryCurrency.setFuturesPnl(new BigDecimal((String)multiKeyMap.get("FuturesPNL" + "_" + s)));
                accountSummaryCurrency.setFxCashBalance(new BigDecimal((String)multiKeyMap.get("FxCashBalance" + "_" + s)));
                accountSummaryCurrency.setIssuerOptionValue(new BigDecimal((String)multiKeyMap.get("IssuerOptionValue" + "_" + s)));
                accountSummaryCurrency.setMoneyMarketFundValue(new BigDecimal((String)multiKeyMap.get("MoneyMarketFundValue" + "_" + s)));
                accountSummaryCurrency.setMutualFundValue(new BigDecimal((String)multiKeyMap.get("MutualFundValue" + "_" + s)));
                accountSummaryCurrency.setNetDividend(new BigDecimal((String)multiKeyMap.get("NetDividend" + "_" + s)));
                accountSummaryCurrency.setNetLiquidationByCurrency(new BigDecimal((String)multiKeyMap.get("NetLiquidationByCurrency" + "_" + s)));
                accountSummaryCurrency.setOptionMarketValue(new BigDecimal((String)multiKeyMap.get("OptionMarketValue" + "_" + s)));
                accountSummaryCurrency.setRealizedPnl(new BigDecimal((String)multiKeyMap.get("RealizedPnL" + "_" + s)));
                accountSummaryCurrency.setStockMarketValue(new BigDecimal((String)multiKeyMap.get("StockMarketValue" + "_" + s)));
                accountSummaryCurrency.setTBillValue(new BigDecimal((String)multiKeyMap.get("TBillValue" + "_" + s)));
                accountSummaryCurrency.setTBondValue(new BigDecimal((String)multiKeyMap.get("TBondValue" + "_" + s)));
                accountSummaryCurrency.setTotalCashBalance(new BigDecimal((String)multiKeyMap.get("TotalCashBalance" + "_" + s)));
                accountSummaryCurrency.setUnrealizedPnl(new BigDecimal((String)multiKeyMap.get("UnrealizedPnL" + "_" + s)));
                accountSummaryCurrency.setWarrantValue(new BigDecimal((String)multiKeyMap.get("WarrantValue" + "_" + s)));

                accountSummaryCurrencyService.saveOrUpdateAccountSummaryCurrency(accountSummaryCurrency);

            }
        }

    }

}
