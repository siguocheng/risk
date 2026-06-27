package com.riskcontrol;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.ExecutionFilter;
import com.ib.client.TagValue;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.config.PolygonOptionClient;
import com.riskcontrol.constant.ReqIdConstant;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.vo.ibkr.BarData;
import com.riskcontrol.enums.GenericTickListEnum;
import com.riskcontrol.service.IContractMarketHistoryService;
import com.riskcontrol.service.IContractService;
import com.riskcontrol.util.BigDecimalUtil;
import com.riskcontrol.util.DateUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest
public class MethodTest {

    @Resource
    private EClientSocket m_client;

    @Test
    public void reqMktData() throws IOException {
        // 订阅期权全套Greeks+成交量OI+盯市价
        String genericTicks = GenericTickListEnum.joinTickIds(
                GenericTickListEnum.CLOSE_IMPLIED_VOLATILITY
//                ,GenericTickListEnum.OPTION_VOLUME,
//                GenericTickListEnum.OPEN_INTEREST,
//                GenericTickListEnum.MARK_PRICE
        );

        int reqId = 2001;

        //1 = Realtime
        //2 = Frozen
        //3 = Delayed
        //4 = Delayed Frozen
        m_client.reqMarketDataType(3);

//        Contract contract = new Contract();
//        contract.conid(890256592);
////        contract.symbol("TSLA");
////        contract.secType("OPT");
//        contract.exchange("AMEX");
////        contract.currency("USD");
////        contract.localSymbol("TSLA  260622C00395000");

        Contract aaplCall = new Contract();
        aaplCall.symbol("TSLA");
        aaplCall.secType("OPT");
        aaplCall.exchange("AMEX");
        aaplCall.currency("USD");
        aaplCall.lastTradeDateOrContractMonth("20260622"); // 到期日
        aaplCall.strike(395); // 行权价220
        aaplCall.right("C"); // 看涨Call
        aaplCall.multiplier("100"); // 1张期权对应100股

        // 调用行情订阅
        // 价格回调 tickPrice
        // 成交量回调 tickSize
        // 期权计算值 tickOptionComputation
        m_client.reqMktData(
                reqId,
                aaplCall,
                "13", // 行情类型
                true, // 是否只获取一次,true：只返回一次 false：持续推送
                false, // 美国监管快照,给false就可以
                new ArrayList<>()
        );

        System.in.read();
    }

    @Test
    public void reqExecutions() throws IOException {
        int reqId = 567;
        m_client.reqExecutions(reqId, new ExecutionFilter());

        System.in.read();
    }

    @Resource
    PolygonOptionClient polygonOptionClient;

    @Test
    public void opta() throws IOException {
        String json = polygonOptionClient.getOptionSnapshot("TSLA");

        System.out.println(json);
    }

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Resource
    IContractMarketHistoryService contractMarketService;

    @Resource
    IContractService contractService;

    @Test
    public void reqHistoricalData() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        com.ib.client.Contract ibContract = new com.ib.client.Contract();

//        int conid = 34426421;
//
//        ibContract.symbol("VIX");
//        ibContract.secType("IND");
//        ibContract.exchange("CBOE");
//        ibContract.currency("USD");


//        int conid = 719582;
//        ibContract.symbol("SPX");
//        ibContract.secType("IND");
//        ibContract.exchange("CBOE");
//        ibContract.currency("USD");

        int conid = 4970027;
        ibContract.symbol("NDX");
        ibContract.secType("IND");
        ibContract.exchange("NASDAQ");
        ibContract.currency("USD");

        LocalDate yesterday = LocalDate.now().minusDays(1);

        int useRTH = 1;                   // 1仅常规交易时段 0包含盘前盘后交易时段

        int formatDate = 1;
        boolean keepUpToDate = false;// 不持续更新

        String durationStr = "1 Y";

        List<TagValue> tagList = null;

        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(ReqIdConstant.HistoricalDataReqId, future);

        String endDateTime = DateUtil.toIbkrUtcEndTime(yesterday);
        String barSize = "1 day";         // 日K线 1 secs / 1 min / 5 mins / 1 hour / 1 day
        String whatToShow = "TRADES";     // 取成交价格 MIDPOINT(中间价)、BID、ASK、TRADES(成交)



        m_client.reqHistoricalData(ReqIdConstant.HistoricalDataReqId, ibContract, endDateTime, durationStr, barSize, whatToShow, useRTH, formatDate, keepUpToDate, tagList);
        Object obj = future.get(60 * 1000, TimeUnit.MILLISECONDS);

        m_client.cancelHistogramData(conid);

        List<BarData> result = (List<BarData>) obj;

        Map<Integer, String> conMap = new HashMap<>();

        List<ContractMarketHistory> historyList = new ArrayList<>();
        for (BarData barData : result) {
            ContractMarketHistory history = new ContractMarketHistory();
            history.setSymbol(ibContract.symbol());
            history.setConid(conid);
            history.setDailyDate(barData.getTime());
            history.setPriceOpen(BigDecimalUtil.doubleToDecimal(barData.getOpen()));
            history.setPriceHigh(BigDecimalUtil.doubleToDecimal(barData.getHigh()));
            history.setPriceLow(BigDecimalUtil.doubleToDecimal(barData.getLow()));
            history.setPriceClose(BigDecimalUtil.doubleToDecimal(barData.getClose()));
            history.setPriceWap(BigDecimalUtil.doubleToDecimal(barData.getWap()));

            history.setDealCount(barData.getCount());
            history.setDealVolume(barData.getVolume());
            historyList.add(history);

            contractMarketService.saveOrUpdateContractMarket(history);
        }
    }


}
