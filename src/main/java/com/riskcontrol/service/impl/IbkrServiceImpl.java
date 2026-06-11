package com.riskcontrol.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ib.client.EClientSocket;
import com.ib.client.protobuf.CancelPositionsMultiProto;
import com.ib.client.protobuf.PositionsMultiRequestProto;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.bo.ibkr.PositionBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import com.riskcontrol.service.IbkrService;
import com.riskcontrol.util.ReflectMapToObjUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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
//        int reqId = ibkrSynConfig.nextReqId();
        int reqId = 999;

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

    @Override
    public List<PositionCallbackVo> reqPosition(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {

        // 1.生成全局唯一reqId
        int reqId = 998;
        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId, future);

        // 2.组装新版请求对象 PositionsMultiRequest
        PositionsMultiRequestProto.PositionsMultiRequest req = PositionsMultiRequestProto.PositionsMultiRequest.newBuilder()
                .setReqId(reqId)
                .setAccount("")
                .setModelCode("Core")
                .build();

        // 场景1：查【全部模型/全部持仓】 → 只setReqId+Account，删掉setModelCode("")
//        PositionsMultiRequest reqAll = PositionsMultiRequest.newBuilder()
//                .setReqId(reqId)
//                .setAccount(account)
//                // .setModelCode("") 删掉这行！！空串非法
//                .build();

        m_client.reqPositionsMultiProtoBuf(req);

        List<PositionCallbackVo> resList = (List<PositionCallbackVo>) future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);

        CancelPositionsMultiProto.CancelPositionsMulti cancelPositionsMulti = CancelPositionsMultiProto.CancelPositionsMulti.newBuilder().setReqId(reqId).build();
        m_client.cancelPositionsMultiProtoBuf(cancelPositionsMulti);

        return resList;
    }

    @Override
    public List<PositionCallbackVo> reqPortfolio(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {
        int reqId = 997;
//        CompletableFuture<Object> future = new CompletableFuture<>();
//        ibkrSynConfig.FUTURE_MAP.put(reqId, future);
//
//        AccountUpdatesMultiRequestProto.AccountUpdatesMultiRequest param =
//                AccountUpdatesMultiRequestProto.AccountUpdatesMultiRequest.newBuilder().setReqId(reqId).setAccount("DUQ346350").build();
//        m_client.reqAccountUpdatesMultiProtoBuf(param);
//
//        CancelAccountUpdatesMultiProto.CancelAccountUpdatesMulti cancelAccountUpdatesMulti
//                = CancelAccountUpdatesMultiProto.CancelAccountUpdatesMulti.newBuilder().setReqId(reqId).build();
//        m_client.cancelAccountUpdatesMultiProtoBuf(cancelAccountUpdatesMulti);
//
//        // 盈亏
//        m_client.reqPnLSingle(reqId + 2, "DUQ346350", "", 265598);

//        m_client.reqAccountUpdatesMulti(reqId, "","", true);

        String accountCode = "DUQ346350";

        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(accountCode, future);

        m_client.reqAccountUpdates(true, accountCode);

        Map<String,Object> result  = (Map<String,Object>)future.get(ibkrSynConfig.timeout, TimeUnit.MILLISECONDS);

        System.out.println(JSONObject.toJSONString(result));
        return null;
    }
}
