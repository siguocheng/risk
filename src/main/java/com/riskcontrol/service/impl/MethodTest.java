package com.riskcontrol.service.impl;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.riskcontrol.enums.GenericTickListEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MethodTest {

    @Resource
    private EClientSocket m_client;

    public void reqMktData() {
        // 订阅期权全套Greeks+成交量OI+盯市价
        String genericTicks = GenericTickListEnum.joinTickIds(
                GenericTickListEnum.MODEL_OPTION_COMPUTATION,
                GenericTickListEnum.OPTION_VOLUME,
                GenericTickListEnum.OPEN_INTEREST,
                GenericTickListEnum.MARK_PRICE
        );

        int reqId = 2001;

        // 构建股票合约 AAPL
        Contract contract = new Contract();
        contract.symbol("AAPL");
        contract.secType("STK");
        contract.exchange("SMART");
        contract.currency("USD");

        // 调用行情订阅
        m_client.reqMktData(
                reqId,
                contract,
                genericTicks,
                false,
                false,
                new ArrayList<>()
        );
    }
}
