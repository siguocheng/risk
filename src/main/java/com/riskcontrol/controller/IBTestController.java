package com.riskcontrol.controller;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.bo.ibkr.PositionBo;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/ib")
public class IBTestController {

    @Resource
    EClientSocket m_client;

    @PostMapping(value = {"/pc/test"})
    public ResultBean<List<PositionCallbackVo>> historyData(@RequestBody PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {

        m_client.reqMktData();

        return new ResultBean<>(null);
    }
}
