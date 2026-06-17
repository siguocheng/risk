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
                GenericTickListEnum.MODEL_OPTION_COMPUTATION
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

        Contract contract = new Contract();
        contract.conid(890256592);
//        contract.symbol("TSLA");
//        contract.secType("OPT");
        contract.exchange("AMEX");
//        contract.currency("USD");
//        contract.localSymbol("TSLA  260622C00395000");

        // 调用行情订阅
        // 价格回调 tickPrice
        // 成交量回调 tickSize
        // 期权计算值 tickOptionComputation
        m_client.reqMktData(
                reqId,
                contract,
                "", // 行情类型
                true, // 是否只获取一次,true：只返回一次 false：持续推送
                false, // 美国监管快照,给false就可以
                new ArrayList<>()
        );
    }
}
