package com.mwy.starter.Threads;

import com.mwy.starter.model.ExchangeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
@Slf4j
public class ExceptionProvider implements Runnable {

    private ExchangeMessage message;
    private LinkedBlockingQueue queue;

    public ExceptionProvider(ExchangeMessage message,LinkedBlockingQueue queue){
        this.message = message;
        this.queue = queue;
    }

    @Override
    public void run() {
        log.info("provider make a task！");
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            log.error("(exception-monitor)Exception Monitor provider invoke error->InterruptedException:"+e.getMessage());
        }
    }
}
