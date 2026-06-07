package com.riskcontrol.controller;

import com.ib.client.EClientSocket;
import com.ib.client.protobuf.PositionMultiProto;
import com.ib.client.protobuf.PositionsMultiRequestProto;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.config.IbkrConfig;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.domain.vo.ibkr.PositionItem;
import com.riskcontrol.service.IbkrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Tag(description = "IB接口", name = "IB接口")
@RestController
@RequestMapping("/ibkr")
public class IbkrController {

    @Resource
    EClientSocket m_client;

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Resource
    IbkrService ibkrService;

    @Operation(summary = "账户信息", description = "账户信息")
    @PostMapping(value = {"/pc/account-summary"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public ResultBean<AccountSummaryVo> reqAccountSummary(@RequestBody AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException {
        return new ResultBean<>(ibkrService.reqAccountSummary(accountSummaryBo));
    }


    @Operation(summary = "持仓列表", description = "持仓列表")
    @PostMapping(value = {"/pc/position"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public List<PositionItem> reqPosition() throws ExecutionException, InterruptedException, TimeoutException {

        // 1.生成全局唯一reqId
        int reqId = ibkrSynConfig.nextReqId();
        CompletableFuture<Object> future = new CompletableFuture<>();
        ibkrSynConfig.FUTURE_MAP.put(reqId, future);

        // 2.组装新版请求对象 PositionsMultiRequest
        PositionsMultiRequestProto.PositionsMultiRequest req = PositionsMultiRequestProto.PositionsMultiRequest.newBuilder()
                .setReqId(reqId)
                .setAccount("")
//                .setModelCode("Core")
                .build();

        // 场景1：查【全部模型/全部持仓】 → 只setReqId+Account，删掉setModelCode("")
//        PositionsMultiRequest reqAll = PositionsMultiRequest.newBuilder()
//                .setReqId(reqId)
//                .setAccount(account)
//                // .setModelCode("") 删掉这行！！空串非法
//                .build();

        m_client.reqPositionsMultiProtoBuf(req);

        List<PositionItem> resList = (List<PositionItem>) future.get(5* 1000, TimeUnit.MILLISECONDS);
        m_client.cancelPositionsMulti(reqId);

        return resList;
    }
}
