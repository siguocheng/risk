package com.riskcontrol.task;

import com.alibaba.fastjson2.JSONObject;
import com.ib.client.EClientSocket;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryCallbackVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    IbkrSynConfig ibkrSynConfig;

    // 30秒检测一次连接状态
    @Scheduled(fixedDelay = 30000)
    public void checkConnect(){
        if(!m_client.isConnected()){
            m_client.eConnect(host,port,clientId);
            System.out.println("IB触发自动重连:"+m_client.isConnected());
        }
    }

    public void accountCurrency() throws ExecutionException, InterruptedException, TimeoutException {

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

        }
    }

}
