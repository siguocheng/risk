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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class IbkrServiceImpl implements IbkrService {


    @Override
    public AccountSummaryVo reqAccountSummary(AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException {

        return new AccountSummaryVo();
    }

    @Override
    public List<PositionCallbackVo> reqPosition(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {

        return new ArrayList<>();
    }

    @Override
    public List<PositionCallbackVo> reqPortfolio(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {

        return null;
    }
}
