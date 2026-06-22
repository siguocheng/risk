package com.riskcontrol.config;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.riskcontrol.warpper.IbkrWrapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class IbkrConfig {

    @Autowired
    private IbkrWrapper ibWrapper;

    @Value("${ibkr.host}")
    private String host;
    @Value("${ibkr.port}")
    private int port;
    @Value("${ibkr.clientid}")
    private int clientId;

    // 容器全局唯一 m_client 实例，对应EClientSocket m_client
    private EClientSocket m_client;

    EJavaSignal m_signal;


    @Bean(destroyMethod = "eDisconnect") // Bean销毁自动断开IB连接
    public EClientSocket ibClientSocket() {
        // EClientSocket构造入参：EWrapper回调实现
        m_signal = new EJavaSignal();

        m_client = new EClientSocket(ibWrapper, m_signal);
        // 发起连接
        if(!m_client.isConnected()){
            m_client.eConnect(host, port, clientId);
            if(m_client.isConnected()){
                System.out.println("IB TWS/Gateway连接成功！");
            }else{
                System.out.println("IB TWS/Gateway连接失败！");
//                throw new RuntimeException("IB连接失败，请检查Gateway/TWS是否开启、端口放行、API权限开启");
            }
        }

        EReader m_reader = new EReader(m_client, m_signal);
        m_reader.start();

        // 【重中之重】手动启动IB消息处理线程！原生TWS Demo必写，很多人漏掉
        new Thread(() -> {
            while (m_client.isConnected()) {
                try {
                    m_signal.waitForSignal();
                    m_reader.processMsgs();
                } catch (Exception e) {
                    log.error("IB消息轮询异常", e);
                }
            }
        }, "IB-Msg-Thread-" + clientId).start();
        return m_client;
    }

    // 项目关闭前主动断开
    @PreDestroy
    public void closeConn(){
        if(m_client != null && m_client.isConnected()){
            m_client.eDisconnect();
        }
    }


}
