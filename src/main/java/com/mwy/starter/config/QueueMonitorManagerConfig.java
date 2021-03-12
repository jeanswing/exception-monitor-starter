package com.mwy.starter.config;

import com.mwy.starter.Threads.ExceptionConsumer;
import com.mwy.starter.core.ExceptionMonitorExecutor;
import com.mwy.starter.model.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-19
 **/
@Configuration
@EnableConfigurationProperties(BaseConfigProperties.class)
@AutoConfigureAfter({ExceptionMonitorExecutor.class,ExceptionMonitorExecutorConfig.class})
@ConditionalOnBean({BaseConfig.class})
@Slf4j
public class QueueMonitorManagerConfig {

    @Autowired
    BaseConfigProperties configProperties;
    @Resource
    ExceptionMonitorExecutor exceptionMonitorExecutor;

    @Bean
    public void queueMonitorThreadCreate(){
        log.warn("(exception-catch)Initializing QueueMonitorManagerThread:  [QueueMonitorManagerThread] start......");
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(()->{
            LinkedBlockingQueue<ExchangeMessage> queue = exceptionMonitorExecutor.queue;
            int queueSize = queue.size();
            if(queueSize>0)log.info(" queue size:{}", queueSize);
            if(queueSize>=0 && queueSize<configProperties.getMaxQueueSize()){
                //provider线程池
                if(exceptionMonitorExecutor.providePool.isShutdown()){
                    exceptionMonitorExecutor.providePool.prestartAllCoreThreads();
                }
                if(queueSize>0){
                    //启动consumer线程池
                    ThreadPoolExecutor consumePool = exceptionMonitorExecutor.consumePool;
                    if(!consumePool.isShutdown()){
                        consumePool.execute(new ExceptionConsumer(exceptionMonitorExecutor.getQueue()));
                    }else{
                        consumePool.prestartAllCoreThreads();
                    }
                }
            }else{
                //任务超长，开启自我保护模式
                log.info("(exception-catch)the task queue is out of range, start self-protection...");
                if(!exceptionMonitorExecutor.providePool.isShutdown()){
                    exceptionMonitorExecutor.providePool.shutdown();
                }
            }
        }, 5, 2, TimeUnit.SECONDS);
    }
}
