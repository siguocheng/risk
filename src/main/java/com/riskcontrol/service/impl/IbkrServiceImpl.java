package com.riskcontrol.service.impl;

import com.ib.client.EClientSocket;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.service.IbkrService;
import com.riskcontrol.util.ReflectMapToObjUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class IbkrServiceImpl implements IbkrService {

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Resource
    EClientSocket m_client;

    @Override
    public AccountSummaryVo reqAccountSummary(AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException {

        AccountSummaryVo data = new AccountSummaryVo();

        // 取得账号信息
        int reqId = ibkrSynConfig.nextReqId();

        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId,future);

        String group = "All";
        if (StringUtils.isNotEmpty(accountSummaryBo.getGroup())) {
            group = accountSummaryBo.getGroup();
        }

        m_client.reqAccountSummary(reqId, group, "AccountType,NetLiquidation,TotalCashValue,SettledCash,AccruedCash,BuyingPower,EquityWithLoanValue,PreviousEquityWithLoanValue,GrossPositionValue,ReqTEquity,ReqTMargin,SMA,InitMarginReq,MaintMarginReq,AvailableFunds,ExcessLiquidity,Cushion,FullInitMarginReq,FullMaintMarginReq,FullAvailableFunds,FullExcessLiquidity,LookAheadNextChange,LookAheadInitMarginReq ,LookAheadMaintMarginReq,LookAheadAvailableFunds,LookAheadExcessLiquidity,HighestSeverity,DayTradesRemaining,Leverage");
        // 阻塞等待直到 accountSummaryEnd 回调完成
        Object obj = future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        Map<String,Object> result = (Map<String,Object>) obj;
        m_client.cancelAccountSummary(reqId);
        ReflectMapToObjUtil.mapToObject(result, data);


        // 取得日收益率
        int reqId1 = ibkrSynConfig.nextReqId();
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId1, future1);

        m_client.reqPnL(reqId1, data.getACCOUNT_ID(), "");
        Object obj1 = future1.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);
        Map<String,Object> result1 = (Map<String,Object>) obj1;
        ReflectMapToObjUtil.mapToObject(result1, data);


        return data;
    }
}
