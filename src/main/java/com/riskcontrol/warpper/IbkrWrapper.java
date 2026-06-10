package com.riskcontrol.warpper;


import com.alibaba.fastjson2.JSONObject;
import com.ib.client.*;
import com.ib.client.protobuf.*;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryCallbackVO;
import com.riskcontrol.domain.vo.ibkr.BarData;
import com.riskcontrol.domain.vo.ibkr.ContractVo;
import com.riskcontrol.domain.vo.ibkr.PositionVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class IbkrWrapper implements EWrapper {

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {

    }

    @Override
    public void tickSize(int tickerId, int field, Decimal size) {

    }

    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {

    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {

    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {

    }

    @Override
    public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {

    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void openOrderEnd() {

    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {

        Map<String, Object> dataMap = accTempMap.computeIfAbsent(accountName, k-> new ConcurrentHashMap<>());
        if (StringUtils.isNotEmpty(currency)) {
            key = key + "_" + currency;
        }
        dataMap.put(key, value);

        System.out.println("updateAccountValue:" + key + "" + ":" + value);
    }

    @Override
    public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {

        List<PositionVo> list = posProtoMap.computeIfAbsent(accountName, k -> new CopyOnWriteArrayList<>());

        PositionVo item = new PositionVo();
        item.setAccountCode(accountName);
        item.setPosition(position.value());
        item.setAvgCost(averageCost);
        item.setMarketPrice(marketPrice);
        item.setMarketValue(marketValue);
        item.setUnrealizedPnl(unrealizedPNL);
        item.setRealizedPnl(realizedPNL);

        ContractVo contractData = this.convertProtoToContract(contract);

        item.setContract(contractData);

        list.add(item);
    }

    // proto合约 -> Contract 转换
    private ContractVo convertProtoToContract(Contract contract){
        ContractVo c = new ContractVo();
        c.setConId(contract.conid());
        c.setSymbol(contract.symbol());
        c.setSecType(contract.getSecType());
        c.setExchange(contract.exchange());
        c.setCurrency(contract.currency());
        c.setStrike(contract.strike());
        c.setRight(contract.getRight());
        c.setLastTradeDateOrContractMonth(contract.lastTradeDateOrContractMonth());
        c.setLastTradeDate(contract.lastTradeDate());
        c.setMultiplier(contract.multiplier());
        c.setPrimaryExch(contract.primaryExch());
        c.setLocalSymbol(contract.localSymbol());
        c.setTradingClass(contract.tradingClass());
        c.setSecIdType(contract.getSecIdType());
        c.setSecId(contract.secId());
        c.setDescription(contract.description());
        c.setComboLegs(contract.comboLegs());

        return c;
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        System.out.println("updateAccountTime:" + timeStamp);
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        System.out.println("accountDownloadEnd:" + accountName);
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(accountName);
        List<PositionVo> data = posProtoMap.remove(accountName);
        Map<String, Object> accountData = accTempMap.remove(accountName);
        accountData.put("position", data);
        if (future != null) {
            future.complete(accountData);
        }
    }

    @Override
    public void nextValidId(int orderId) {

    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {

    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {

    }

    @Override
    public void execDetailsEnd(int reqId) {

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {

    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, Decimal size, boolean isSmartDepth) {

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {

    }

    @Override
    public void managedAccounts(String accountsList) {

    }

    @Override
    public void receiveFA(int faDataType, String xml) {

    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        BarData data = new BarData();
        data.time = bar.time();
        data.open = bar.open();
        data.high = bar.high();
        data.low = bar.low();
        data.close = bar.close();
        data.volume = bar.volume().longValue();
        data.wap = bar.wap().longValue();
        data.count = bar.count();

        System.out.println("historicalData:" + JSONObject.toJSONString(data));
    }

    @Override
    public void scannerParameters(String xml) {

    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {

    }

    @Override
    public void scannerDataEnd(int reqId) {

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {

    }

    @Override
    public void currentTime(long time) {

    }

    @Override
    public void fundamentalData(int reqId, String data) {

    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {

    }

    @Override
    public void tickSnapshotEnd(int reqId) {

    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {

    }

    @Override
    public void commissionAndFeesReport(CommissionAndFeesReport commissionAndFeesReport) {

    }

    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {

    }

    @Override
    public void positionEnd() {

    }

    public final Map<Object, Map<String, Object>> accTempMap = new ConcurrentHashMap<>();

    public final Map<Object, List<AccountSummaryCallbackVO>> accountSummaryTempMap = new ConcurrentHashMap<>();

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        List<AccountSummaryCallbackVO> dataList = accountSummaryTempMap.computeIfAbsent(reqId, k-> new ArrayList<>());

        AccountSummaryCallbackVO accountSummaryCallbackVO = new AccountSummaryCallbackVO();
        accountSummaryCallbackVO.setAccount(account);
        accountSummaryCallbackVO.setTag(tag);
        accountSummaryCallbackVO.setValue(value);
        accountSummaryCallbackVO.setCurrency(currency);

        dataList.add(accountSummaryCallbackVO);
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);
        List<AccountSummaryCallbackVO> dataList = accountSummaryTempMap.remove(reqId);
        if(future != null){
            future.complete(dataList);
        }
    }

    @Override
    public void verifyMessageAPI(String apiData) {

    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {

    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {

    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {

    }

    @Override
    public void displayGroupList(int reqId, String groups) {

    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {

    }

    @Override
    public void error(Exception e) {

    }

    @Override
    public void error(String str) {

    }

    @Override
    public void error(int id, long errorTime, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(id);
        // 异常时清理临时缓存
        accTempMap.remove(id);
        if(future != null){
            future.completeExceptionally(new RuntimeException("IB["+errorCode+"]:"+errorMsg));
        }
        log.error("id:{},errorTime:{},errorCode:{},errorMsg:{},advancedOrderRejectJson:{}",id, errorTime, errorCode, errorMsg, advancedOrderRejectJson);
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectAck() {

    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos, double avgCost) {

    }

    @Override
    public void positionMultiEnd(int reqId) {

    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {
        System.out.println("accountUpdateMulti:" + key + ":" + value);
    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {
        System.out.println("accountUpdateMultiEnd");
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {

    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {

    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {

    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {

    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {

    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        System.out.println("historicalDataEnd:" + startDateStr + "-" + endDateStr);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {

    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {

    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {

    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {

    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {

    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {

    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {

    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {

    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {

    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {

    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {

    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {

    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {

    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {

    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        Map<String, Object> dataMap = new ConcurrentHashMap<>();
        dataMap.put("dailyPnL", dailyPnL);
        dataMap.put("unrealizedPnL", unrealizedPnL);
        dataMap.put("realizedPnL", realizedPnL);

        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);

        if(future != null){
            future.complete(dataMap);
        }
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
        System.out.println("pnlSingle");
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {

    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {

    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {

    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast, String exchange, String specialConditions) {

    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {

    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {

    }

    @Override
    public void orderBound(long permId, int clientId, int orderId) {

    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void completedOrdersEnd() {

    }

    @Override
    public void replaceFAEnd(int reqId, String text) {

    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {

    }

    @Override
    public void wshEventData(int reqId, String dataJson) {

    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {

    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {

    }

    @Override
    public void currentTimeInMillis(long timeInMillis) {

    }

    @Override
    public void orderStatusProtoBuf(OrderStatusProto.OrderStatus orderStatusProto) {

    }

    @Override
    public void openOrderProtoBuf(OpenOrderProto.OpenOrder openOrderProto) {

    }

    @Override
    public void openOrdersEndProtoBuf(OpenOrdersEndProto.OpenOrdersEnd openOrdersEndProto) {

    }

    @Override
    public void errorProtoBuf(ErrorMessageProto.ErrorMessage errorMessageProto) {

    }

    @Override
    public void execDetailsProtoBuf(ExecutionDetailsProto.ExecutionDetails executionDetailsProto) {

    }

    @Override
    public void execDetailsEndProtoBuf(ExecutionDetailsEndProto.ExecutionDetailsEnd executionDetailsEndProto) {

    }

    @Override
    public void completedOrderProtoBuf(CompletedOrderProto.CompletedOrder completedOrderProto) {

    }

    @Override
    public void completedOrdersEndProtoBuf(CompletedOrdersEndProto.CompletedOrdersEnd completedOrdersEndProto) {

    }

    @Override
    public void orderBoundProtoBuf(OrderBoundProto.OrderBound orderBoundProto) {

    }

    @Override
    public void contractDataProtoBuf(ContractDataProto.ContractData contractDataProto) {

    }

    @Override
    public void bondContractDataProtoBuf(ContractDataProto.ContractData contractDataProto) {

    }

    @Override
    public void contractDataEndProtoBuf(ContractDataEndProto.ContractDataEnd contractDataEndProto) {

    }

    @Override
    public void tickPriceProtoBuf(TickPriceProto.TickPrice tickPriceProto) {

    }

    @Override
    public void tickSizeProtoBuf(TickSizeProto.TickSize tickSizeProto) {

    }

    @Override
    public void tickOptionComputationProtoBuf(TickOptionComputationProto.TickOptionComputation tickOptionComputationProto) {

    }

    @Override
    public void tickGenericProtoBuf(TickGenericProto.TickGeneric tickGenericProto) {

    }

    @Override
    public void tickStringProtoBuf(TickStringProto.TickString tickStringProto) {

    }

    @Override
    public void tickSnapshotEndProtoBuf(TickSnapshotEndProto.TickSnapshotEnd tickSnapshotEndProto) {

    }

    @Override
    public void updateMarketDepthProtoBuf(MarketDepthProto.MarketDepth marketDepthProto) {

    }

    @Override
    public void updateMarketDepthL2ProtoBuf(MarketDepthL2Proto.MarketDepthL2 marketDepthL2Proto) {

    }

    @Override
    public void marketDataTypeProtoBuf(MarketDataTypeProto.MarketDataType marketDataTypeProto) {

    }

    @Override
    public void tickReqParamsProtoBuf(TickReqParamsProto.TickReqParams tickReqParamsProto) {

    }

    @Override
    public void updateAccountValueProtoBuf(AccountValueProto.AccountValue accounValueProto) {
//        System.out.println("updateAccountValueProtoBuf");
    }

    @Override
    public void updatePortfolioProtoBuf(PortfolioValueProto.PortfolioValue portfolioValueProto) {
        System.out.println("updatePortfolioProtoBuf");
    }

    @Override
    public void updateAccountTimeProtoBuf(AccountUpdateTimeProto.AccountUpdateTime accountUpdateTimeProto) {
        System.out.println("updateAccountTimeProtoBuf");
    }

    @Override
    public void accountDataEndProtoBuf(AccountDataEndProto.AccountDataEnd accountDataEndProto) {

    }

    @Override
    public void managedAccountsProtoBuf(ManagedAccountsProto.ManagedAccounts managedAccountsProto) {

    }



    @Override
    public void positionProtoBuf(PositionProto.Position positionProto) {

    }

    @Override
    public void positionEndProtoBuf(PositionEndProto.PositionEnd positionEndProto) {

    }

    @Override
    public void accountSummaryProtoBuf(AccountSummaryProto.AccountSummary accountSummaryProto) {

    }

    @Override
    public void accountSummaryEndProtoBuf(AccountSummaryEndProto.AccountSummaryEnd accountSummaryEndProto) {

    }

    // reqId -> 持仓列表缓存
    public final Map<Object, List<PositionVo>> posProtoMap = new ConcurrentHashMap<>();

    @Override
    public void positionMultiProtoBuf(PositionMultiProto.PositionMulti positionMultiProto) {
        int reqId = positionMultiProto.getReqId();
        List<PositionVo> list = posProtoMap.computeIfAbsent(reqId, k -> new CopyOnWriteArrayList<>());

        PositionVo item = new PositionVo();
        item.setAccountCode(positionMultiProto.getAccount());
        item.setModelCode(positionMultiProto.getModelCode());
        item.setPosition(new BigDecimal(positionMultiProto.getPosition()));
        item.setAvgCost(positionMultiProto.getAvgCost());

        // proto合约转IB Contract
        ContractVo contract = convertProtoToContract(positionMultiProto.getContract());
        item.setContract(contract);
        list.add(item);
    }

    // proto合约 -> Contract 转换
    private ContractVo convertProtoToContract(ContractProto.Contract protoContract){
        ContractVo c = new ContractVo();
        c.setConId(protoContract.getConId());
        c.setSymbol(protoContract.getSymbol());
        c.setSecType(protoContract.getSecType());
        c.setExchange(protoContract.getExchange());
        c.setCurrency(protoContract.getCurrency());
        c.setStrike(protoContract.getStrike());
        c.setRight(protoContract.getRight());
        c.setLastTradeDateOrContractMonth(protoContract.getLastTradeDate());

        return c;
    }

    @Override
    public void positionMultiEndProtoBuf(PositionMultiEndProto.PositionMultiEnd positionMultiEndProto) {
        int reqId = positionMultiEndProto.getReqId();
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);
        List<PositionVo> data = posProtoMap.remove(reqId);
        if (future != null) {
            future.complete(data == null ? new ArrayList<>() : data);
        }
    }

    @Override
    public void accountUpdateMultiProtoBuf(AccountUpdateMultiProto.AccountUpdateMulti accountUpdateMultiProto) {
        System.out.println(accountUpdateMultiProto.getKey() + ":"  +accountUpdateMultiProto.getValue());
        System.out.println("accountUpdateMultiProtoBuf");
    }

    @Override
    public void accountUpdateMultiEndProtoBuf(AccountUpdateMultiEndProto.AccountUpdateMultiEnd accountUpdateMultiEndProto) {
        System.out.println("accountUpdateMultiEndProtoBuf");
    }

    @Override
    public void historicalDataProtoBuf(HistoricalDataProto.HistoricalData historicalDataProto) {

    }

    @Override
    public void historicalDataUpdateProtoBuf(HistoricalDataUpdateProto.HistoricalDataUpdate historicalDataUpdateProto) {

    }

    @Override
    public void historicalDataEndProtoBuf(HistoricalDataEndProto.HistoricalDataEnd historicalDataEndProto) {

    }

    @Override
    public void realTimeBarTickProtoBuf(RealTimeBarTickProto.RealTimeBarTick realTimeBarTickProto) {

    }

    @Override
    public void headTimestampProtoBuf(HeadTimestampProto.HeadTimestamp headTimestampProto) {

    }

    @Override
    public void histogramDataProtoBuf(HistogramDataProto.HistogramData histogramDataProto) {

    }

    @Override
    public void historicalTicksProtoBuf(HistoricalTicksProto.HistoricalTicks historicalTicksProto) {

    }

    @Override
    public void historicalTicksBidAskProtoBuf(HistoricalTicksBidAskProto.HistoricalTicksBidAsk historicalTicksBidAskProto) {

    }

    @Override
    public void historicalTicksLastProtoBuf(HistoricalTicksLastProto.HistoricalTicksLast historicalTicksLastProto) {

    }

    @Override
    public void tickByTickDataProtoBuf(TickByTickDataProto.TickByTickData tickByTickDataProto) {

    }

    @Override
    public void updateNewsBulletinProtoBuf(NewsBulletinProto.NewsBulletin newsBulletinProto) {

    }

    @Override
    public void newsArticleProtoBuf(NewsArticleProto.NewsArticle newsArticleProto) {

    }

    @Override
    public void newsProvidersProtoBuf(NewsProvidersProto.NewsProviders newsProvidersProto) {

    }

    @Override
    public void historicalNewsProtoBuf(HistoricalNewsProto.HistoricalNews historicalNewsProto) {

    }

    @Override
    public void historicalNewsEndProtoBuf(HistoricalNewsEndProto.HistoricalNewsEnd historicalNewsEndProto) {

    }

    @Override
    public void wshMetaDataProtoBuf(WshMetaDataProto.WshMetaData wshMetaDataProto) {

    }

    @Override
    public void wshEventDataProtoBuf(WshEventDataProto.WshEventData wshEventDataProto) {

    }

    @Override
    public void tickNewsProtoBuf(TickNewsProto.TickNews tickNewsProto) {

    }

    @Override
    public void scannerParametersProtoBuf(ScannerParametersProto.ScannerParameters scannerParametersProto) {

    }

    @Override
    public void scannerDataProtoBuf(ScannerDataProto.ScannerData scannerDataProto) {

    }

    @Override
    public void fundamentalsDataProtoBuf(FundamentalsDataProto.FundamentalsData fundamentalsDataProto) {

    }

    @Override
    public void pnlProtoBuf(PnLProto.PnL pnlProto) {

    }

    @Override
    public void pnlSingleProtoBuf(PnLSingleProto.PnLSingle pnlSingleProto) {

    }

    @Override
    public void receiveFAProtoBuf(ReceiveFAProto.ReceiveFA receiveFAProto) {

    }

    @Override
    public void replaceFAEndProtoBuf(ReplaceFAEndProto.ReplaceFAEnd replaceFAEndProto) {

    }

    @Override
    public void commissionAndFeesReportProtoBuf(CommissionAndFeesReportProto.CommissionAndFeesReport commissionAndFeesReportProto) {

    }

    @Override
    public void historicalScheduleProtoBuf(HistoricalScheduleProto.HistoricalSchedule historicalScheduleProto) {

    }

    @Override
    public void rerouteMarketDataRequestProtoBuf(RerouteMarketDataRequestProto.RerouteMarketDataRequest rerouteMarketDataRequestProto) {

    }

    @Override
    public void rerouteMarketDepthRequestProtoBuf(RerouteMarketDepthRequestProto.RerouteMarketDepthRequest rerouteMarketDepthRequestProto) {

    }

    @Override
    public void secDefOptParameterProtoBuf(SecDefOptParameterProto.SecDefOptParameter secDefOptParameterProto) {

    }

    @Override
    public void secDefOptParameterEndProtoBuf(SecDefOptParameterEndProto.SecDefOptParameterEnd secDefOptParameterEndProto) {

    }

    @Override
    public void softDollarTiersProtoBuf(SoftDollarTiersProto.SoftDollarTiers softDollarTiersProto) {

    }

    @Override
    public void familyCodesProtoBuf(FamilyCodesProto.FamilyCodes familyCodesProto) {

    }

    @Override
    public void symbolSamplesProtoBuf(SymbolSamplesProto.SymbolSamples symbolSamplesProto) {

    }

    @Override
    public void smartComponentsProtoBuf(SmartComponentsProto.SmartComponents smartComponentsProto) {

    }

    @Override
    public void marketRuleProtoBuf(MarketRuleProto.MarketRule marketRuleProto) {

    }

    @Override
    public void userInfoProtoBuf(UserInfoProto.UserInfo userInfoProto) {

    }

    @Override
    public void nextValidIdProtoBuf(NextValidIdProto.NextValidId nextValidIdProto) {

    }

    @Override
    public void currentTimeProtoBuf(CurrentTimeProto.CurrentTime currentTimeProto) {

    }

    @Override
    public void currentTimeInMillisProtoBuf(CurrentTimeInMillisProto.CurrentTimeInMillis currentTimeInMillisProto) {

    }

    @Override
    public void verifyMessageApiProtoBuf(VerifyMessageApiProto.VerifyMessageApi verifyMessageApiProto) {

    }

    @Override
    public void verifyCompletedProtoBuf(VerifyCompletedProto.VerifyCompleted verifyCompletedProto) {

    }

    @Override
    public void displayGroupListProtoBuf(DisplayGroupListProto.DisplayGroupList displayGroupListProto) {

    }

    @Override
    public void displayGroupUpdatedProtoBuf(DisplayGroupUpdatedProto.DisplayGroupUpdated displayGroupUpdatedProto) {

    }

    @Override
    public void marketDepthExchangesProtoBuf(MarketDepthExchangesProto.MarketDepthExchanges marketDepthExchangesProto) {

    }

    @Override
    public void configResponseProtoBuf(ConfigResponseProto.ConfigResponse configResponseProto) {

    }

    @Override
    public void updateConfigResponseProtoBuf(UpdateConfigResponseProto.UpdateConfigResponse updateConfigResponseProto) {

    }
}
