package com.riskcontrol.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IbkrSynConfig {

    @Value("${ibkr.timeout}")
    public Long timeout;

    // reqId -> Future 全局映射，解决多并发请求回调匹配
    public static final Map<Integer, CompletableFuture<Object>> FUTURE_MAP = new ConcurrentHashMap<>();
    // 自增reqId生成器
    private final AtomicInteger reqIdGen = new AtomicInteger(1000);

    // 获取自增reqId
    public int nextReqId(){
        return reqIdGen.incrementAndGet();
    }

    public CompletableFuture<Object> setAndGetCompletableFuture(int reqId){
        CompletableFuture<Object> future = new CompletableFuture<>();
        FUTURE_MAP.put(reqId,future);
        return future;
    }
}
