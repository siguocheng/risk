package com.riskcontrol.task;

import com.alibaba.fastjson2.JSONObject;
import com.ib.client.EClientSocket;
import com.ib.client.ExecutionFilter;
import com.ib.client.TagValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.constant.AccountKey;
import com.riskcontrol.constant.ReqIdConstant;
import com.riskcontrol.domain.*;
import com.riskcontrol.domain.vo.CommissionAndFeesReportCallbackVo;
import com.riskcontrol.domain.vo.ExecutionCallbackVo;
import com.riskcontrol.domain.vo.ibkr.*;
import com.riskcontrol.service.*;
import com.riskcontrol.util.BigDecimalUtil;
import com.riskcontrol.util.DateUtil;
import com.riskcontrol.util.RiskMetricsUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
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
    IContractDailyPnlService contractDailyPnlService;

    @Resource
    IContractHistoryService contractHistoryService;

    @Resource
    IContractOptionService contractOptionService;

    @Resource
    IIbOrderService ibOrderService;

    @Resource
    IContractExecutionService contractExecutionService;

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

        log.info("synAccountCurrency start");
        int reqId = ReqIdConstant.reqAccountSummaryId;

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
        log.info("synAccountCurrency end");
    }

    /**
     * 同步账号整体信息、持仓列表、收益信息
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synAccount() throws ExecutionException, InterruptedException, TimeoutException {

        log.info("synAccount synAccount");
        List<AccountCurrency> accountList = accountCurrencyService.list();
        for (AccountCurrency accountCurrency : accountList) {

            String accountCode = accountCurrency.getAccountCode();
            log.info("synAccount:{} start", accountCode);

            String currency = accountCurrency.getCurrency();
            CompletableFuture<Object> future = new CompletableFuture<>();
            ibkrSynConfig.FUTURE_MAP.put(accountCode, future);

            // updateAccountValue
            // updatePortfolio
            // updateAccountTime
            // accountDownloadEnd
            m_client.reqAccountUpdates(true, accountCode);
            Map<String,Object> result  = (Map<String,Object>)future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);

            m_client.reqAccountUpdates(false, accountCode);
            // 持仓信息
            List<PositionCallbackVo> positions = (List<PositionCallbackVo>)result.remove("position");

            for (PositionCallbackVo positionCallbackVo : positions) {
                Position position = new Position();
                BeanUtils.copyProperties(positionCallbackVo, position);
                position.setPositionQty(positionCallbackVo.getPosition());
                position.setAvgCost(BigDecimal.valueOf(positionCallbackVo.getAvgCost()));
                position.setUnrealizedPnl(BigDecimal.valueOf(positionCallbackVo.getUnrealizedPnl()));
                position.setMarketPrice(BigDecimal.valueOf(positionCallbackVo.getMarketPrice()));
                position.setMarketValue(BigDecimal.valueOf(positionCallbackVo.getMarketValue()));
                position.setRealizedPnl(BigDecimal.valueOf(positionCallbackVo.getRealizedPnl()));
                position.setConid(positionCallbackVo.getConid());
                positionService.saveOrUpdatePosition(position);

                ContractCallbackVo contractCallbackVo = positionCallbackVo.getContract();

                Contract contract = new Contract();
                BeanUtils.copyProperties(contractCallbackVo, contract);
                contract.setAccountCode(accountCode);
                contractService.saveOrUpdateContract(contract);

                this.synSinglePnl(accountCode,"" , positionCallbackVo.getConid());
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

            this.handleAccountSummary(accountCode, singleKeyMap);

            this.handleAccountSummaryCurrency(accountCode, currencyMap, multiKeyMap);

            this.synPnl(accountCode, "");
        }
        log.info("synAccount end");
    }

    public void handleAccountSummaryCurrency(String accountCode, Map<String,Object> currencyMap, Map<String,Object> multiKeyMap){
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

    /**
     *
     * @param accountCode
     * @param singleKeyMap
     */
    public void handleAccountSummary(String accountCode, Map<String,Object> singleKeyMap){
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setAccountCode(accountCode);
        accountSummary.setAccountReady((String)singleKeyMap.get("AccountReady"));
        accountSummary.setAccountType((String)singleKeyMap.get("AccountType"));
        accountSummary.setCushion(new BigDecimal((String)singleKeyMap.get("Cushion")));
        accountSummary.setLookAheadNextChange(Long.valueOf((String)singleKeyMap.get("LookAheadNextChange")));
        accountSummary.setNlvAndMarginInReview((String)singleKeyMap.get("NLVAndMarginInReview"));
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
        accountSummary.setTotalCashValue(new BigDecimal((String)singleKeyMap.get("TotalCashValue")));

        accountSummaryService.saveOrUpdateAccountSummary(accountSummary);
    }

    /**
     * 同步账号的收益
     * @param accountCode
     * @param modelCode
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synPnl(String accountCode, String modelCode) throws ExecutionException, InterruptedException, TimeoutException {

        int reqId = ReqIdConstant.reqPnLId;
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId, future1);
        // 最后一个参数是modelCode
        m_client.reqPnL(reqId, accountCode, modelCode);
        Object obj = future1.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        m_client.cancelPnL(reqId);

        PnlCallbackVo result = (PnlCallbackVo) obj;

        AccountDailyPnl accountDailyPnl = new AccountDailyPnl();
        accountDailyPnl.setAccountCode(accountCode);
        accountDailyPnl.setDailyPnl(BigDecimal.valueOf(result.getDailyPnL()));
        accountDailyPnl.setUnrealizedPnl(BigDecimal.valueOf(result.getUnrealizedPnL()));
        accountDailyPnl.setRealizedPnl(BigDecimal.valueOf(result.getRealizedPnL()));
        accountDailyPnl.setDailyDate(LocalDate.now());

        accountDailyPnlService.saveOrUpdateAccountDailyPnl(accountDailyPnl);


    }

    /**
     * 同步合约的收益
     * @param accountCode
     * @param modelCode
     * @param conid
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synSinglePnl(String accountCode, String modelCode, int conid) throws ExecutionException, InterruptedException, TimeoutException {
        int reqId = ReqIdConstant.reqPnLSingleId;
        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId, future);
        // 最后一个参数是modelCode
        m_client.reqPnLSingle(reqId, accountCode, modelCode, conid);
        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        m_client.cancelPnLSingle(reqId);
        ContractSinglePnlCallbackVo result = (ContractSinglePnlCallbackVo) obj;

        ContractDailyPnl contractDailyPnl = new ContractDailyPnl();

        contractDailyPnl.setAccountCode(accountCode);
        contractDailyPnl.setConid(conid);
        contractDailyPnl.setDailyPnl(BigDecimalUtil.doubleToDecimal(result.getDailyPnL()));
        contractDailyPnl.setUnrealizedPnl(BigDecimalUtil.doubleToDecimal(result.getUnrealizedPnL()));
        contractDailyPnl.setRealizedPnl(BigDecimalUtil.doubleToDecimal(result.getRealizedPnL()));
        contractDailyPnl.setDailyDate(LocalDate.now());

        contractDailyPnlService.saveOrUpdateContractDailyPnl(contractDailyPnl);
    }

    public void synContractHistory() throws ExecutionException, InterruptedException, TimeoutException {

        List<Contract> list = contractService.list();

        LocalDate yesterday = LocalDate.now().minusDays(1);

        String endDateTime = DateUtil.toIbkrUtcEndTime(yesterday);          // 空 = 取最新数据 20260608 23:59:59
        String durationStr = "1 Y";       // 回溯 1 个月 1 D(1 天)、1 W(1 周)、1 M(1 月)、1 Y(1 年)
        String barSize = "1 day";         // 日K线 1 secs / 1 min / 5 mins / 1 hour / 1 day
        String whatToShow = "TRADES";     // 取成交价格 MIDPOINT(中间价)、BID、ASK、TRADES(成交)
        int useRTH = 1;                   // 1仅常规交易时段 0包含盘前盘后交易时段

        int formatDate = 1;
        boolean keepUpToDate = false;// 不持续更新

        for (Contract contract : list) {
            com.ib.client.Contract ibContract = new com.ib.client.Contract();
            int conid = contract.getConid();
            ibContract.conid(conid);
            ibContract.symbol(contract.getSymbol());
            ibContract.exchange(contract.getExchange());
            ibContract.secType(contract.getSecType());
            ibContract.currency(contract.getCurrency());

            List<TagValue> tagList = null;

            LocalDate contractHistoryLastDate = contract.getContractHistoryLastDate();
            if (contractHistoryLastDate != null) {
                LocalDate now = LocalDate.now().minusDays(1);
                long days = ChronoUnit.DAYS.between(contractHistoryLastDate, now);
                if (days == 0){
                    continue;
                }
                durationStr = days + " D";
            }

            CompletableFuture<Object> future = new CompletableFuture<>();
            ibkrSynConfig.FUTURE_MAP.put(ReqIdConstant.HistoricalDataReqId, future);

            m_client.reqHistoricalData(ReqIdConstant.HistoricalDataReqId, ibContract, endDateTime, durationStr, barSize, whatToShow, useRTH, formatDate, keepUpToDate, tagList);
            Object obj = future.get(60 * 1000, TimeUnit.MILLISECONDS);

            m_client.cancelHistogramData(conid);

            List<BarData> result = (List<BarData>) obj;

            List<ContractHistory> historyList = new ArrayList<>();
            for (BarData barData : result) {
                ContractHistory history = new ContractHistory();
                history.setConid(conid);
                history.setTime(barData.getTime());
                history.setPriceOpen(BigDecimalUtil.doubleToDecimal(barData.getOpen()));
                history.setPriceHigh(BigDecimalUtil.doubleToDecimal(barData.getHigh()));
                history.setPriceLow(BigDecimalUtil.doubleToDecimal(barData.getLow()));
                history.setPriceClose(BigDecimalUtil.doubleToDecimal(barData.getClose()));
                history.setPriceWap(BigDecimalUtil.doubleToDecimal(barData.getWap()));

                history.setDealCount(barData.getCount());
                history.setDealVolume(barData.getVolume());
                historyList.add(history);

                contractHistoryService.saveOrUpdateContractHistory(history);
            }

            contract.setContractHistoryLastDate(yesterday);

            contractService.updateById(contract);
        }
    }

    /**
     * 计算var
     */
    public void calcVar(){
        for (Position position : positionService.list()) {
            double marketValue = position.getMarketValue().doubleValue();// 市值
            int conid = position.getConid();
            System.out.printf("conid:" + conid);
            // 取得日价格
            double[] prices = contractHistoryService.queryContractHistoryPriceCloseByConid(conid);
            // 95% var
            double calcParamVarValue95 = RiskMetricsUtil.calcParamVar(prices, marketValue, RiskMetricsUtil.Z_95);
            double calcHistoryVarValue95 = RiskMetricsUtil.calcHistoryVar(prices, marketValue, RiskMetricsUtil.Z_ALPHA_95);
            double calcMonteCarloVarValue95 = RiskMetricsUtil.calcMonteCarloVar(prices, marketValue, RiskMetricsUtil.Z_ALPHA_95);

            // 99% var
            double calcParamVarValue99 = RiskMetricsUtil.calcParamVar(prices, marketValue, RiskMetricsUtil.Z_99);
            double calcHistoryVarValue99 = RiskMetricsUtil.calcHistoryVar(prices, marketValue, RiskMetricsUtil.Z_ALPHA_99);
            double calcMonteCarloVarValue99 = RiskMetricsUtil.calcMonteCarloVar(prices, marketValue, RiskMetricsUtil.Z_ALPHA_99);

            // 95% cvar
            double calcParamCVaRValue95 = RiskMetricsUtil.calcParamCVaR(prices, marketValue, RiskMetricsUtil.Z_95, RiskMetricsUtil.Z_ALPHA_95);
            double calcHistoryCVaRValue95 = RiskMetricsUtil.calcHistoryCVaR(prices, marketValue, RiskMetricsUtil.Z_ALPHA_95);
            double calcMonteCarloCVaRValue95 = RiskMetricsUtil.calcMonteCarloCVaR(prices, marketValue, RiskMetricsUtil.Z_ALPHA_95);

            // 99% cvar
            double calcParamCVaRValue99 = RiskMetricsUtil.calcParamCVaR(prices, marketValue, RiskMetricsUtil.Z_99, RiskMetricsUtil.Z_ALPHA_99);
            double calcHistoryCVaRValue99 = RiskMetricsUtil.calcHistoryCVaR(prices, marketValue, RiskMetricsUtil.Z_ALPHA_99);
            double calcMonteCarloCVaRValue99 = RiskMetricsUtil.calcMonteCarloCVaR(prices, marketValue, RiskMetricsUtil.Z_ALPHA_99);

            System.out.printf("参数法 | 95%%置信 1日VaR: %.2f 美元%n", calcParamVarValue95);
            System.out.printf("历史模拟 | 95%%置信 1日VaR: %.2f 美元%n", calcHistoryVarValue95);
            System.out.printf("蒙特卡洛 | 95%%置信 1日VaR: %.2f 美元%n", calcMonteCarloVarValue95);

            System.out.printf("参数法 | 99%%置信 1日VaR: %.2f 美元%n", calcParamVarValue99);
            System.out.printf("历史模拟 | 99%%置信 1日VaR: %.2f 美元%n", calcHistoryVarValue99);
            System.out.printf("蒙特卡洛 | 99%%置信 1日VaR: %.2f 美元%n%n", calcMonteCarloVarValue99);

            System.out.printf("历史模拟 | 95%%置信 1日CVaR: %.2f 美元%n", calcParamCVaRValue95);
            System.out.printf("参数法   | 95%%置信 1日CVaR: %.2f 美元%n", calcHistoryCVaRValue95);
            System.out.printf("蒙特卡洛 | 95%%置信 1日CVaR: %.2f 美元%n", calcMonteCarloCVaRValue95);

            System.out.printf("历史模拟 | 99%%置信 1日CVaR: %.2f 美元%n", calcParamCVaRValue99);
            System.out.printf("参数法   | 99%%置信 1日CVaR: %.2f 美元%n", calcHistoryCVaRValue99);
            System.out.printf("蒙特卡洛 | 99%%置信 1日CVaR: %.2f 美元%n%n", calcMonteCarloCVaRValue99);

            // 计算 10日
            int holdDay = 10;
            double tenDayVar = RiskMetricsUtil.convertToTDay(calcParamVarValue95, holdDay);
            double tenDayCvar = RiskMetricsUtil.convertToTDay(calcParamCVaRValue95, holdDay);

            System.out.printf("【%d日持有期】95%%置信 VaR: %.2f 美元%n", holdDay, tenDayVar);
            System.out.printf("【%d日持有期】95%%置信 CVaR: %.2f 美元%n", holdDay, tenDayCvar);
        }
    }

    /**
     * 同步期权希腊值数据
     * @param conid 合约ID
     * @param symbol 股票代码
     * @param exchange 交易所
     * @param currency 币种
     * @param lastTradeDateOrContractMonth 到期日
     * @param strike 行权价
     * @param right 期权类型 C=看涨 P=看跌
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synContractOption(int conid, String symbol, String exchange, String currency,
                                  String lastTradeDateOrContractMonth, BigDecimal strike, String right) throws ExecutionException, InterruptedException, TimeoutException {

        log.info("synContractOption start, conid={}", conid);

        // 构建期权合约
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        ibContract.conid(conid);
        ibContract.symbol(symbol);
        ibContract.secType("OPT");
        ibContract.exchange(exchange);
        ibContract.currency(currency);
        ibContract.lastTradeDateOrContractMonth(lastTradeDateOrContractMonth);
        ibContract.strike(strike.doubleValue());
        ibContract.right(right);

        int reqId = ibkrSynConfig.nextReqId();
        CompletableFuture<Object> future = ibkrSynConfig.setAndGetCompletableFuture(reqId);

        // 请求期权希腊值数据
        // genericTickList: 106 表示请求期权希腊值数据
        String genericTickList = "106";
        boolean snapshot = false;
        boolean regulatorySnapshot = false;
        List<TagValue> mktDataOptions = null;

        m_client.reqMktData(reqId, ibContract, genericTickList, snapshot, regulatorySnapshot, mktDataOptions);

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        m_client.cancelMktData(reqId);

        ContractOptionCallbackVo result = (ContractOptionCallbackVo) obj;

        // 保存到数据库
        ContractOption contractOption = new ContractOption();
        contractOption.setConid(conid);
        contractOption.setImpliedVol(BigDecimal.valueOf(result.getImpliedVol()));
        contractOption.setDelta(BigDecimal.valueOf(result.getDelta()));
        contractOption.setOptPrice(BigDecimal.valueOf(result.getOptPrice()));
        contractOption.setPvDividend(BigDecimal.valueOf(result.getPvDividend()));
        contractOption.setGamma(BigDecimal.valueOf(result.getGamma()));
        contractOption.setVega(BigDecimal.valueOf(result.getVega()));
        contractOption.setTheta(BigDecimal.valueOf(result.getTheta()));
        contractOption.setUndPrice(BigDecimal.valueOf(result.getUndPrice()));

        contractOptionService.saveOrUpdateContractOption(contractOption);

        log.info("synContractOption end, conid={}, impliedVol={}", conid, result.getImpliedVol());
    }

    /**
     * 同步所有期权合约的希腊值数据
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synAllContractOptions() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synAllContractOptions start");

        // 查询所有期权类型的合约
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getSecType, "OPT");
        List<Contract> optionContracts = contractService.list(queryWrapper);

        for (Contract contract : optionContracts) {
            try {
                this.synContractOption(
                        contract.getConid(),
                        contract.getSymbol(),
                        contract.getExchange(),
                        contract.getCurrency(),
                        contract.getLastTradeDateOrContractMonth(),
                        contract.getStrike(),
                        contract.getOptRight()
                );
            } catch (Exception e) {
                log.error("synContractOption error, conid={}", contract.getConid(), e);
            }
        }

        log.info("synAllContractOptions end, total={}", optionContracts.size());
    }

    /**
     * 同步当前客户端的开放订单
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synOpenOrders() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synOpenOrders start");

        int reqId = ibkrSynConfig.nextReqId();
        CompletableFuture<Object> future = ibkrSynConfig.setAndGetCompletableFuture(reqId);

        // 请求当前客户端的开放订单
        m_client.reqOpenOrders();

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        List<IbOrderCallbackVo> result = (List<IbOrderCallbackVo>) obj;

        log.info("synOpenOrders end, total={}", result != null ? result.size() : 0);
    }

    /**
     * 同步所有账户的开放订单
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synAllOpenOrders() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synAllOpenOrders start");

        int reqId = ReqIdConstant.openOrderReqId;
        CompletableFuture<Object> future = ibkrSynConfig.setAndGetCompletableFuture(reqId);

        // 请求所有账户的开放订单
        // openOrder
        //orderStatus
        //openOrderEnd
        m_client.reqAllOpenOrders();

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        List<Object> result = (List<Object>) obj;

        for (Object o : result) {
            if (o instanceof IbOrderCallbackVo) {
                IbOrder ibOrder = new IbOrder((IbOrderCallbackVo)o);
                ibOrderService.saveOrUpdateByPermId(ibOrder);
            } else if (o instanceof OrderStatusCallbackVo) {
                System.out.println("order status:" + JSONObject.toJSONString(o));
            }
        }

        log.info("synAllOpenOrders end, total={}", result != null ? result.size() : 0);
    }

    /**
     * 同步已完成的订单
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synCompletedOrders() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synCompletedOrders start");

        int reqId = ReqIdConstant.completedOrderReqId;
        CompletableFuture<Object> future = ibkrSynConfig.setAndGetCompletableFuture(reqId);

        // 请求已完成的订单
        // completedOrder
        // completedOrdersEnd
        m_client.reqCompletedOrders(false); // true:只返回当前这条 API 连接（当前 clientId） 发起成交的订单 false:当日账户下全部已完成订单

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        List<IbOrderCallbackVo> result = (List<IbOrderCallbackVo>) obj;

        for (IbOrderCallbackVo ibOrderCallbackVo : result) {
            IbOrder ibOrder = new IbOrder(ibOrderCallbackVo);
            ibOrderService.saveOrUpdateByPermId(ibOrder);
        }

        log.info("synCompletedOrders end, total={}", result != null ? result.size() : 0);
    }

    /**
     * 同步所有订单（开放订单 + 已完成订单）
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void synAllOrders() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synAllOrders start");

        // 先同步所有开放订单
        this.synAllOpenOrders();

        // 再同步已完成订单
        this.synCompletedOrders();

        log.info("synAllOrders end");
    }

    public void synExecutions() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("synExecutions start");

        int reqId = ReqIdConstant.reqExecutions;
        CompletableFuture<Object> future = ibkrSynConfig.setAndGetCompletableFuture(reqId);

        // execDetails
        // execDetailsEnd
        m_client.reqExecutions(reqId, new ExecutionFilter());

        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        List<Object> result = (List<Object>) obj;

        List<ExecutionCallbackVo> executions = new ArrayList<>();
        List<CommissionAndFeesReportCallbackVo> commissionAndFeesReports = new ArrayList<>();

        for (Object o : result) {
            if (o instanceof ExecutionCallbackVo){
                executions.add((ExecutionCallbackVo)o);
            } else if (o instanceof CommissionAndFeesReportCallbackVo) {
                commissionAndFeesReports.add((CommissionAndFeesReportCallbackVo)o);
            }
        }

        // 将commissionAndFeesReports按execId建立索引
        Map<String, CommissionAndFeesReportCallbackVo> commissionMap = new HashMap<>();
        for (CommissionAndFeesReportCallbackVo report : commissionAndFeesReports) {
            commissionMap.put(report.getExecId(), report);
        }

        // 合并数据并保存到数据库
        List<ContractExecution> executionList = new ArrayList<>();
        for (ExecutionCallbackVo execution : executions) {
            ContractExecution contractExecution = new ContractExecution();

            // 从ExecutionCallbackVo设置字段
            contractExecution.setOrderId(execution.getOrderId());
            contractExecution.setClientId(execution.getClientId());
            contractExecution.setExecId(execution.getExecId());
            contractExecution.setTime(execution.getTime());
            contractExecution.setAcctNumber(execution.getAcctNumber());
            contractExecution.setExchange(execution.getExchange());
            contractExecution.setSide(execution.getSide());
            contractExecution.setShares(execution.getShares().value());
            contractExecution.setPrice(BigDecimal.valueOf(execution.getPrice()));
            contractExecution.setPermId(execution.getPermId());
            contractExecution.setLiquidation(execution.getLiquidation());
            contractExecution.setCumQty(execution.getCumQty().value());
            contractExecution.setAvgPrice(BigDecimal.valueOf(execution.getAvgPrice()));
            contractExecution.setOrderRef(execution.getOrderRef());
            contractExecution.setEvRule(execution.getEvRule());
            contractExecution.setEvMultiplier(BigDecimal.valueOf(execution.getEvMultiplier()));
            contractExecution.setModelCode(execution.getModelCode());
            contractExecution.setLastLiquidity(execution.getLastLiquidity() != null ? execution.getLastLiquidity().name() : "");
            contractExecution.setPendingPriceRevision(execution.isPendingPriceRevision());
            contractExecution.setSubmitter(execution.getSubmitter());
            contractExecution.setOptExerciseOrLapseType(execution.getOptExerciseOrLapseType() != null ? execution.getOptExerciseOrLapseType().name() : "");

            // 从CommissionAndFeesReportCallbackVo合并字段
            CommissionAndFeesReportCallbackVo commissionReport = commissionMap.get(execution.getExecId());
            if (commissionReport != null) {
                contractExecution.setCommissionAndFees(BigDecimal.valueOf(commissionReport.getCommissionAndFees()));
                contractExecution.setCurrency(commissionReport.getCurrency());
                contractExecution.setRealizedPnl(BigDecimal.valueOf(commissionReport.getRealizedPNL()));
                contractExecution.setYield(BigDecimal.valueOf(commissionReport.getYield()));
                contractExecution.setYieldRedemptionDate((long) commissionReport.getYieldRedemptionDate());
            }

            executionList.add(contractExecution);
        }

        // 批量保存到数据库
        if (!executionList.isEmpty()) {
            for (ContractExecution execution : executionList) {
                contractExecutionService.saveOrUpdateByExecId(execution);
            }
            log.info("synExecutions saved {} records", executionList.size());
        }

        log.info("synExecutions end, total executions={}, total commissions={}", executions.size(), commissionAndFeesReports.size());
    }
}
