package com.riskcontrol.warpper;


import com.alibaba.fastjson2.JSONObject;
import com.ib.client.*;
import com.ib.client.protobuf.*;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.constant.ReqIdConstant;
import com.riskcontrol.dao.IbOrderMapper;
import com.riskcontrol.domain.ContractOption;
import com.riskcontrol.domain.IbOrder;
import com.riskcontrol.domain.vo.CommissionAndFeesReportCallbackVo;
import com.riskcontrol.domain.vo.ExecutionCallbackVo;
import com.riskcontrol.domain.vo.ibkr.*;
import com.riskcontrol.service.IContractOptionService;
import com.riskcontrol.service.IIbOrderService;
import com.riskcontrol.util.IbValueUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class IbkrWrapper implements EWrapper {

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Resource
    IContractOptionService contractOptionService;

    @Resource
    IIbOrderService ibOrderService;

    /**
     * 统一空方法打印自身方法名
     */
    private void printCurrentMethod() {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        System.out.println(methodName);
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
        System.out.println("tickPrice");
    }

    @Override
    public void tickSize(int tickerId, int field, Decimal size) {
        System.out.println("tickSize");
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        log.info("tickOptionComputation: tickerId={}, field={}, impliedVol={}, delta={}, optPrice={}, undPrice={}",
                tickerId, field, impliedVol, delta, optPrice, undPrice);

        // 检查是否有有效数据（impliedVol为Double.MAX_VALUE表示无数据）
        if (impliedVol == Double.MAX_VALUE || impliedVol < 0) {
            log.warn("tickOptionComputation: 无有效期权数据, tickerId={}", tickerId);
            return;
        }

        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.get(tickerId);
        if (future != null) {
            ContractOptionCallbackVo callbackVo = new ContractOptionCallbackVo();
            callbackVo.setTickerId(tickerId);
            callbackVo.setImpliedVol(impliedVol);
            callbackVo.setDelta(delta);
            callbackVo.setOptPrice(optPrice);
            callbackVo.setPvDividend(pvDividend);
            callbackVo.setGamma(gamma);
            callbackVo.setVega(vega);
            callbackVo.setTheta(theta);
            callbackVo.setUndPrice(undPrice);
            future.complete(callbackVo);
        }
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        printCurrentMethod();
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        printCurrentMethod();
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {
        printCurrentMethod();
    }

//    public final Map<Object, List<OrderStatusCallbackVo>> orderStatusMap = new ConcurrentHashMap<>();

    @Override
    public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        log.info("orderStatus: orderId={}, status={}, filled={}, remaining={}, avgFillPrice={}, permId={}", 
                orderId, status, filled, remaining, avgFillPrice, permId);

        List<Object> list = listDataMap.computeIfAbsent(ReqIdConstant.openOrderReqId,k -> new CopyOnWriteArrayList<>());
        OrderStatusCallbackVo orderStatusCallbackVo = new OrderStatusCallbackVo(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice);

        list.add(orderStatusCallbackVo);
    }

//    public final Map<Object, List<IbOrderCallbackVo>> orderMap = new ConcurrentHashMap<>();

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        log.info("openOrder: orderId={}, symbol={}, action={}, orderType={}, totalQuantity={}", 
                orderId, contract.symbol(), order.action(), order.orderType(), order.totalQuantity());

        List<Object> list = listDataMap.computeIfAbsent(ReqIdConstant.openOrderReqId,k -> new CopyOnWriteArrayList<>());

        list.add(new IbOrderCallbackVo(orderId, contract, order, orderState));
    }

    @Override
    public void openOrderEnd() {
        log.info("openOrderEnd");
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(ReqIdConstant.openOrderReqId);
        List<Object> ordersAndOrderStatus = listDataMap.remove(ReqIdConstant.openOrderReqId);
        if (future != null) {
            future.complete(ordersAndOrderStatus);
        }

    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        log.info("completedOrder: permId={}, symbol={}, action={}, status={}", 
                order.permId(), contract.symbol(), order.action(), orderState.status());
        List<Object> list = listDataMap.computeIfAbsent(ReqIdConstant.completedOrderReqId, k -> new CopyOnWriteArrayList<>());
        IbOrderCallbackVo ibOrderCallbackVo = new IbOrderCallbackVo(-1, contract, order, orderState);

        list.add(ibOrderCallbackVo);
    }

    @Override
    public void completedOrdersEnd() {
        log.info("completedOrdersEnd");
        // 遍历所有等待中的Future并完成
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(ReqIdConstant.completedOrderReqId);
        List<Object> ibOrderCallbackVos = listDataMap.get(ReqIdConstant.completedOrderReqId);
        if (future != null) {
            future.complete(ibOrderCallbackVos);
        }
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

        System.out.println("updatePortfolio");
        List<PositionCallbackVo> list = posProtoMap.computeIfAbsent(accountName, k -> new CopyOnWriteArrayList<>());

        PositionCallbackVo item = new PositionCallbackVo();
        item.setAccountCode(accountName);
        item.setPosition(position.value());
        item.setAvgCost(averageCost);
        item.setMarketPrice(marketPrice);
        item.setMarketValue(marketValue);
        item.setUnrealizedPnl(unrealizedPNL);
        item.setRealizedPnl(realizedPNL);
        item.setConid(contract.conid());

        item.setContract(contract);

        list.add(item);

        System.out.println("updatePortfolio:" + JSONObject.toJSONString(item));
    }

    // proto合约 -> Contract 转换
    private ContractCallbackVo convertProtoToContract(Contract contract){
        ContractCallbackVo c = new ContractCallbackVo();
        c.setConid(contract.conid());
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
        List<PositionCallbackVo> data = posProtoMap.remove(accountName);
        Map<String, Object> accountData = accTempMap.remove(accountName);
        
        if (data == null) {
            accountData.put("position", new ArrayList<>());
        } else {
            accountData.put("position", data);
        }
        
        if (future != null) {
            future.complete(accountData);
        }
        
    }

    @Override
    public void nextValidId(int orderId) {
        printCurrentMethod();
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        printCurrentMethod();
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        printCurrentMethod();
    }

    @Override
    public void contractDetailsEnd(int reqId) {
        printCurrentMethod();
    }

    public final Map<Object, List<Object>> listDataMap = new ConcurrentHashMap<>();

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        printCurrentMethod();
        List<Object> dataList = listDataMap.computeIfAbsent(reqId, k-> new ArrayList<>());

        ExecutionCallbackVo executionCallbackVo = new ExecutionCallbackVo(contract, execution);
        dataList.add(executionCallbackVo);
    }

    @Override
    public void execDetailsEnd(int reqId) {
        printCurrentMethod();
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);
        List<Object> dataList1 = listDataMap.remove(reqId);
        List<Object> dataList2 = listDataMap.remove(ReqIdConstant.commissionAndFeesReportReqId);
        dataList1.addAll(dataList2);
        if(future != null){
            future.complete(dataList1);
        }
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {
        printCurrentMethod();
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, Decimal size, boolean isSmartDepth) {
        printCurrentMethod();
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        printCurrentMethod();
    }

    @Override
    public void managedAccounts(String accountsList) {
        printCurrentMethod();
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        printCurrentMethod();
    }

    public final Map<Object, List<BarData>> historicalDataMap = new ConcurrentHashMap<>();

    @Override
    public void historicalData(int reqId, Bar bar) {

        List<BarData> list = historicalDataMap.computeIfAbsent(reqId, k -> new CopyOnWriteArrayList<>());

        BarData data = new BarData();
        data.time = bar.time();
        data.open = bar.open();
        data.high = bar.high();
        data.low = bar.low();
        data.close = bar.close();
        data.volume = bar.volume().longValue();
        data.wap = bar.wap().longValue();
        data.count = bar.count();

        list.add(data);

        System.out.println("historicalData:" + JSONObject.toJSONString(data));
    }

    @Override
    public void scannerParameters(String xml) {
        printCurrentMethod();
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        printCurrentMethod();
    }

    @Override
    public void scannerDataEnd(int reqId) {
        printCurrentMethod();
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {
        printCurrentMethod();
    }

    @Override
    public void currentTime(long time) {
        printCurrentMethod();
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        printCurrentMethod();
    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
        printCurrentMethod();
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        printCurrentMethod();
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        printCurrentMethod();
    }

    @Override
    public void commissionAndFeesReport(CommissionAndFeesReport commissionAndFeesReport) {
        printCurrentMethod();

        CommissionAndFeesReportCallbackVo commissionAndFeesReportCallbackVo= new CommissionAndFeesReportCallbackVo(commissionAndFeesReport);

        List<Object> dataList = listDataMap.computeIfAbsent(ReqIdConstant.commissionAndFeesReportReqId, k-> new ArrayList<>());

        dataList.add(commissionAndFeesReportCallbackVo);
    }

    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {
        printCurrentMethod();
    }

    @Override
    public void positionEnd() {
        printCurrentMethod();
    }

    public final Map<Object, Map<String, Object>> accTempMap = new ConcurrentHashMap<>();

    public final Map<Object, List<AccountSummaryCallbackVO>> accountSummaryTempMap = new ConcurrentHashMap<>();

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        System.out.println("accountSummary");
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
        printCurrentMethod();
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
        printCurrentMethod();
    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
        printCurrentMethod();
    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
        printCurrentMethod();
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
        printCurrentMethod();
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
        printCurrentMethod();
    }

    @Override
    public void error(Exception e) {
        printCurrentMethod();
    }

    @Override
    public void error(String str) {
        printCurrentMethod();
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
        printCurrentMethod();
    }

    @Override
    public void connectAck() {
        printCurrentMethod();
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos, double avgCost) {
        printCurrentMethod();
    }

    @Override
    public void positionMultiEnd(int reqId) {
        printCurrentMethod();
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
        printCurrentMethod();
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
        printCurrentMethod();
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        printCurrentMethod();
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        printCurrentMethod();
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        printCurrentMethod();
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);
        List<BarData> data = historicalDataMap.remove(reqId);
        if (future != null) {
            future.complete(data == null ? new ArrayList<>() : data);
        }
        System.out.println("historicalDataEnd:" + startDateStr + "-" + endDateStr);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        printCurrentMethod();
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {
        printCurrentMethod();
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {
        printCurrentMethod();
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
        printCurrentMethod();
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        printCurrentMethod();
    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
        printCurrentMethod();
    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
        printCurrentMethod();
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
        printCurrentMethod();
    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        printCurrentMethod();
    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {
        printCurrentMethod();
    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        printCurrentMethod();
    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
        printCurrentMethod();
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
        printCurrentMethod();
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
        printCurrentMethod();
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        PnlCallbackVo pnlCallbackVo = new PnlCallbackVo();
        pnlCallbackVo.setDailyPnL(dailyPnL);
        pnlCallbackVo.setUnrealizedPnL(unrealizedPnL);
        pnlCallbackVo.setRealizedPnL(realizedPnL);
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);

        if(future != null){
            future.complete(pnlCallbackVo);
        }
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
        System.out.println("pnlSingle");
        ContractSinglePnlCallbackVo singlePnlCallbackVo = new ContractSinglePnlCallbackVo();

        if (Math.abs(realizedPnL - Double.MAX_VALUE) < 1e-15) {
            System.out.println("【警告】realizedPnL 无有效数据，为IB占位值");
            // 业务赋值：0
            realizedPnL = 0.0;
        }

        if (Math.abs(unrealizedPnL - Double.MAX_VALUE) < 1e-15) {
            System.out.println("【警告】realizedPnL 无有效数据，为IB占位值");
            // 业务赋值：0
            unrealizedPnL = 0.0;
        }

        singlePnlCallbackVo.setPos(pos);
        singlePnlCallbackVo.setDailyPnL(dailyPnL);
        singlePnlCallbackVo.setUnrealizedPnL(unrealizedPnL);
        singlePnlCallbackVo.setRealizedPnL(realizedPnL);
        singlePnlCallbackVo.setValue(value);
        CompletableFuture<Object> future = ibkrSynConfig.FUTURE_MAP.remove(reqId);

        if(future != null){
            future.complete(singlePnlCallbackVo);
        }
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {
        printCurrentMethod();
    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
        printCurrentMethod();
    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
        printCurrentMethod();
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast, String exchange, String specialConditions) {
        printCurrentMethod();
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {
        printCurrentMethod();
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        printCurrentMethod();
    }

    @Override
    public void orderBound(long permId, int clientId, int orderId) {
        printCurrentMethod();
    }

    @Override
    public void replaceFAEnd(int reqId, String text) {
        printCurrentMethod();
    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {
        printCurrentMethod();
    }

    @Override
    public void wshEventData(int reqId, String dataJson) {
        printCurrentMethod();
    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {
        printCurrentMethod();
    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {
        printCurrentMethod();
    }

    @Override
    public void currentTimeInMillis(long timeInMillis) {
        printCurrentMethod();
    }

    // ====================== Protobuf 系列回调 ======================
    @Override
    public void orderStatusProtoBuf(OrderStatusProto.OrderStatus orderStatusProto) {
        printCurrentMethod();
    }

    @Override
    public void openOrderProtoBuf(OpenOrderProto.OpenOrder openOrderProto) {
        printCurrentMethod();
    }

    @Override
    public void openOrdersEndProtoBuf(OpenOrdersEndProto.OpenOrdersEnd openOrdersEndProto) {
        printCurrentMethod();
    }

    @Override
    public void errorProtoBuf(ErrorMessageProto.ErrorMessage errorMessageProto) {
        printCurrentMethod();
    }

    @Override
    public void execDetailsProtoBuf(ExecutionDetailsProto.ExecutionDetails executionDetailsProto) {
        printCurrentMethod();
    }

    @Override
    public void execDetailsEndProtoBuf(ExecutionDetailsEndProto.ExecutionDetailsEnd executionDetailsEndProto) {
        printCurrentMethod();
    }

    @Override
    public void completedOrderProtoBuf(CompletedOrderProto.CompletedOrder completedOrderProto) {
        printCurrentMethod();
    }

    @Override
    public void completedOrdersEndProtoBuf(CompletedOrdersEndProto.CompletedOrdersEnd completedOrdersEndProto) {
        printCurrentMethod();
    }

    @Override
    public void orderBoundProtoBuf(OrderBoundProto.OrderBound orderBoundProto) {
        printCurrentMethod();
    }

    @Override
    public void contractDataProtoBuf(ContractDataProto.ContractData contractDataProto) {
        printCurrentMethod();
    }

    @Override
    public void bondContractDataProtoBuf(ContractDataProto.ContractData contractDataProto) {
        printCurrentMethod();
    }

    @Override
    public void contractDataEndProtoBuf(ContractDataEndProto.ContractDataEnd contractDataEndProto) {
        printCurrentMethod();
    }

    @Override
    public void tickPriceProtoBuf(TickPriceProto.TickPrice tickPriceProto) {
        printCurrentMethod();
    }

    @Override
    public void tickSizeProtoBuf(TickSizeProto.TickSize tickSizeProto) {
        printCurrentMethod();
    }

    @Override
    public void tickOptionComputationProtoBuf(TickOptionComputationProto.TickOptionComputation tickOptionComputationProto) {
        printCurrentMethod();
    }

    @Override
    public void tickGenericProtoBuf(TickGenericProto.TickGeneric tickGenericProto) {
        printCurrentMethod();
    }

    @Override
    public void tickStringProtoBuf(TickStringProto.TickString tickStringProto) {
        printCurrentMethod();
    }

    @Override
    public void tickSnapshotEndProtoBuf(TickSnapshotEndProto.TickSnapshotEnd tickSnapshotEndProto) {
        printCurrentMethod();
    }

    @Override
    public void updateMarketDepthProtoBuf(MarketDepthProto.MarketDepth marketDepthProto) {
        printCurrentMethod();
    }

    @Override
    public void updateMarketDepthL2ProtoBuf(MarketDepthL2Proto.MarketDepthL2 marketDepthL2Proto) {
        printCurrentMethod();
    }

    @Override
    public void marketDataTypeProtoBuf(MarketDataTypeProto.MarketDataType marketDataTypeProto) {
        printCurrentMethod();
    }

    @Override
    public void tickReqParamsProtoBuf(TickReqParamsProto.TickReqParams tickReqParamsProto) {
        printCurrentMethod();
    }

    @Override
    public void updateAccountValueProtoBuf(AccountValueProto.AccountValue accounValueProto) {
//        System.out.println("updateAccountValueProtoBuf");
        printCurrentMethod();
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
        printCurrentMethod();
    }

    @Override
    public void managedAccountsProtoBuf(ManagedAccountsProto.ManagedAccounts managedAccountsProto) {
        printCurrentMethod();
    }

    @Override
    public void positionProtoBuf(PositionProto.Position positionProto) {
        printCurrentMethod();
    }

    @Override
    public void positionEndProtoBuf(PositionEndProto.PositionEnd positionEndProto) {
        printCurrentMethod();
    }

    @Override
    public void accountSummaryProtoBuf(AccountSummaryProto.AccountSummary accountSummaryProto) {
        printCurrentMethod();
    }

    @Override
    public void accountSummaryEndProtoBuf(AccountSummaryEndProto.AccountSummaryEnd accountSummaryEndProto) {
        printCurrentMethod();
    }

    // reqId -> 持仓列表缓存
    public final Map<Object, List<PositionCallbackVo>> posProtoMap = new ConcurrentHashMap<>();

    @Override
    public void positionMultiProtoBuf(PositionMultiProto.PositionMulti positionMultiProto) {
        printCurrentMethod();
    }

    @Override
    public void positionMultiEndProtoBuf(PositionMultiEndProto.PositionMultiEnd positionMultiEndProto) {
        printCurrentMethod();
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
        printCurrentMethod();
    }

    @Override
    public void historicalDataUpdateProtoBuf(HistoricalDataUpdateProto.HistoricalDataUpdate historicalDataUpdateProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalDataEndProtoBuf(HistoricalDataEndProto.HistoricalDataEnd historicalDataEndProto) {
        printCurrentMethod();
    }

    @Override
    public void realTimeBarTickProtoBuf(RealTimeBarTickProto.RealTimeBarTick realTimeBarTickProto) {
        printCurrentMethod();
    }

    @Override
    public void headTimestampProtoBuf(HeadTimestampProto.HeadTimestamp headTimestampProto) {
        printCurrentMethod();
    }

    @Override
    public void histogramDataProtoBuf(HistogramDataProto.HistogramData histogramDataProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalTicksProtoBuf(HistoricalTicksProto.HistoricalTicks historicalTicksProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalTicksBidAskProtoBuf(HistoricalTicksBidAskProto.HistoricalTicksBidAsk historicalTicksBidAskProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalTicksLastProtoBuf(HistoricalTicksLastProto.HistoricalTicksLast historicalTicksLastProto) {
        printCurrentMethod();
    }

    @Override
    public void tickByTickDataProtoBuf(TickByTickDataProto.TickByTickData tickByTickDataProto) {
        printCurrentMethod();
    }

    @Override
    public void updateNewsBulletinProtoBuf(NewsBulletinProto.NewsBulletin newsBulletinProto) {
        printCurrentMethod();
    }

    @Override
    public void newsArticleProtoBuf(NewsArticleProto.NewsArticle newsArticleProto) {
        printCurrentMethod();
    }

    @Override
    public void newsProvidersProtoBuf(NewsProvidersProto.NewsProviders newsProvidersProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalNewsProtoBuf(HistoricalNewsProto.HistoricalNews historicalNewsProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalNewsEndProtoBuf(HistoricalNewsEndProto.HistoricalNewsEnd historicalNewsEndProto) {
        printCurrentMethod();
    }

    @Override
    public void wshMetaDataProtoBuf(WshMetaDataProto.WshMetaData wshMetaDataProto) {
        printCurrentMethod();
    }

    @Override
    public void wshEventDataProtoBuf(WshEventDataProto.WshEventData wshEventDataProto) {
        printCurrentMethod();
    }

    @Override
    public void tickNewsProtoBuf(TickNewsProto.TickNews tickNewsProto) {
        printCurrentMethod();
    }

    @Override
    public void scannerParametersProtoBuf(ScannerParametersProto.ScannerParameters scannerParametersProto) {
        printCurrentMethod();
    }

    @Override
    public void scannerDataProtoBuf(ScannerDataProto.ScannerData scannerDataProto) {
        printCurrentMethod();
    }

    @Override
    public void fundamentalsDataProtoBuf(FundamentalsDataProto.FundamentalsData fundamentalsDataProto) {
        printCurrentMethod();
    }

    @Override
    public void pnlProtoBuf(PnLProto.PnL pnlProto) {
        printCurrentMethod();
    }

    @Override
    public void pnlSingleProtoBuf(PnLSingleProto.PnLSingle pnlSingleProto) {
        printCurrentMethod();
    }

    @Override
    public void receiveFAProtoBuf(ReceiveFAProto.ReceiveFA receiveFAProto) {
        printCurrentMethod();
    }

    @Override
    public void replaceFAEndProtoBuf(ReplaceFAEndProto.ReplaceFAEnd replaceFAEndProto) {
        printCurrentMethod();
    }

    @Override
    public void commissionAndFeesReportProtoBuf(CommissionAndFeesReportProto.CommissionAndFeesReport commissionAndFeesReportProto) {
        printCurrentMethod();
    }

    @Override
    public void historicalScheduleProtoBuf(HistoricalScheduleProto.HistoricalSchedule historicalScheduleProto) {
        printCurrentMethod();
    }

    @Override
    public void rerouteMarketDataRequestProtoBuf(RerouteMarketDataRequestProto.RerouteMarketDataRequest rerouteMarketDataRequestProto) {
        printCurrentMethod();
    }

    @Override
    public void rerouteMarketDepthRequestProtoBuf(RerouteMarketDepthRequestProto.RerouteMarketDepthRequest rerouteMarketDepthRequestProto) {
        printCurrentMethod();
    }

    @Override
    public void secDefOptParameterProtoBuf(SecDefOptParameterProto.SecDefOptParameter secDefOptParameterProto) {
        printCurrentMethod();
    }

    @Override
    public void secDefOptParameterEndProtoBuf(SecDefOptParameterEndProto.SecDefOptParameterEnd secDefOptParameterEndProto) {
        printCurrentMethod();
    }

    @Override
    public void softDollarTiersProtoBuf(SoftDollarTiersProto.SoftDollarTiers softDollarTiersProto) {
        printCurrentMethod();
    }

    @Override
    public void familyCodesProtoBuf(FamilyCodesProto.FamilyCodes familyCodesProto) {
        printCurrentMethod();
    }

    @Override
    public void symbolSamplesProtoBuf(SymbolSamplesProto.SymbolSamples symbolSamplesProto) {
        printCurrentMethod();
    }

    @Override
    public void smartComponentsProtoBuf(SmartComponentsProto.SmartComponents smartComponentsProto) {
        printCurrentMethod();
    }

    @Override
    public void marketRuleProtoBuf(MarketRuleProto.MarketRule marketRuleProto) {
        printCurrentMethod();
    }

    @Override
    public void userInfoProtoBuf(UserInfoProto.UserInfo userInfoProto) {
        printCurrentMethod();
    }

    @Override
    public void nextValidIdProtoBuf(NextValidIdProto.NextValidId nextValidIdProto) {
        printCurrentMethod();
    }

    @Override
    public void currentTimeProtoBuf(CurrentTimeProto.CurrentTime currentTimeProto) {
        printCurrentMethod();
    }

    @Override
    public void currentTimeInMillisProtoBuf(CurrentTimeInMillisProto.CurrentTimeInMillis currentTimeInMillisProto) {
        printCurrentMethod();
    }

    @Override
    public void verifyMessageApiProtoBuf(VerifyMessageApiProto.VerifyMessageApi verifyMessageApiProto) {
        printCurrentMethod();
    }

    @Override
    public void verifyCompletedProtoBuf(VerifyCompletedProto.VerifyCompleted verifyCompletedProto) {
        printCurrentMethod();
    }

    @Override
    public void displayGroupListProtoBuf(DisplayGroupListProto.DisplayGroupList displayGroupListProto) {
        printCurrentMethod();
    }

    @Override
    public void displayGroupUpdatedProtoBuf(DisplayGroupUpdatedProto.DisplayGroupUpdated displayGroupUpdatedProto) {
        printCurrentMethod();
    }

    @Override
    public void marketDepthExchangesProtoBuf(MarketDepthExchangesProto.MarketDepthExchanges marketDepthExchangesProto) {
        printCurrentMethod();
    }

    @Override
    public void configResponseProtoBuf(ConfigResponseProto.ConfigResponse configResponseProto) {
        printCurrentMethod();
    }

    @Override
    public void updateConfigResponseProtoBuf(UpdateConfigResponseProto.UpdateConfigResponse updateConfigResponseProto) {
        printCurrentMethod();
    }
}