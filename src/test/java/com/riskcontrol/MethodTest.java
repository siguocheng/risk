package com.riskcontrol;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.ExecutionFilter;
import com.riskcontrol.enums.GenericTickListEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
public class MethodTest {

    @Resource
    private EClientSocket m_client;

    @Test
    public void reqMktData() throws IOException {
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
                "", // 行情类型
                false, // 是否只获取一次,true：只返回一次 false：持续推送
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


}
