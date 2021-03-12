package com.mwy.starter.core;

import com.mwy.starter.model.ExchangeMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
@Accessors(chain = true)
public class ExceptionMonitorExecutor {

    public LinkedBlockingQueue<ExchangeMessage> queue;
    //生产者线程池
    public ThreadPoolExecutor providePool;
    //消费者线程池
    public ThreadPoolExecutor consumePool;
}
