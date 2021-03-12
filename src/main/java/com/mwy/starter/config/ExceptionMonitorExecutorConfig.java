package com.mwy.starter.config;

import com.mwy.starter.core.ExceptionMonitorExecutor;
import com.mwy.starter.model.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

/**
 * @desc 任务队列
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@ConditionalOnBean({BaseConfig.class})
@Configuration
@Slf4j
public class ExceptionMonitorExecutorConfig {

    //生产者线程池
    @Bean("exceptionMonitorExecutor")
    public ExceptionMonitorExecutor exceptionExecutor(){
        ExceptionMonitorExecutor exceptionMonitorExecutor = new ExceptionMonitorExecutor();
        //默认任务队列
        LinkedBlockingQueue<ExchangeMessage> queue = new LinkedBlockingQueue();
        exceptionMonitorExecutor.setQueue(queue);

        //初始化启动生产者线程池
        ThreadPoolExecutor providePool = new ThreadPoolExecutor(// 自定义一个线程池
                3, // 核心线程
                6, // 最大线程8
                360, // 60s
                TimeUnit.SECONDS, //空闲时间
                new ArrayBlockingQueue(10),//任务队列
                Executors.defaultThreadFactory(),//默认线程工厂
                new ThreadPoolExecutor.AbortPolicy()//任务拒绝策略
        );
        providePool.prestartAllCoreThreads();//预启动所有核心线程
        log.warn("(exception-catch)Initializing ThreadPool:  [ProviderThreadPool] start......");
        exceptionMonitorExecutor.setProvidePool(providePool);

        ThreadPoolExecutor consumePool = new ThreadPoolExecutor(// 自定义一个线程池
                2, // 核心线程
                4, // 最大线程8
                360, // 60s
                TimeUnit.SECONDS, //空闲时间
                new ArrayBlockingQueue(10),//任务队列
                Executors.defaultThreadFactory(),//默认线程工厂
                new ThreadPoolExecutor.AbortPolicy()//任务拒绝策略
        );
        consumePool.prestartAllCoreThreads();//预启动所有核心线程
        log.warn("(exception-catch)Initializing ThreadPool:  [ConsumerThreadPool] start......");
        exceptionMonitorExecutor.setConsumePool(consumePool);
        //初始化启动消费者任务
        //consumePool.execute(new ExceptionConsumer(queue));
        return exceptionMonitorExecutor;
    }
}
