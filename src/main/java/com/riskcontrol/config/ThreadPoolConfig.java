package com.riskcontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
// 开启@Async注解异步支持，必须加
@EnableAsync
public class ThreadPoolConfig {

    @Bean("taskExecutor") // 指定Bean名称，多线程池时用来区分
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 1.核心线程数：常驻线程
        executor.setCorePoolSize(8);
        // 2.最大线程数：核心+扩容线程上限
        executor.setMaxPoolSize(20);
        // 3.队列容量：阻塞队列，任务超出核心线程后存入队列
        executor.setQueueCapacity(200);
        // 4.空闲线程存活时间(秒)：扩容线程空闲多久销毁
        executor.setKeepAliveSeconds(60);
        // 5.线程前缀名称，方便日志排查
        executor.setThreadNamePrefix("biz-task-");

        // 拒绝策略：任务满了之后处理策略
        // AbortPolicy：直接抛异常(默认)
        // CallerRunsPolicy：由调用线程执行任务
        // DiscardPolicy：丢弃任务
        // DiscardOldestPolicy：丢弃队列最老任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束再关闭容器
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭等待超时时间
        executor.setAwaitTerminationSeconds(60);

        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
