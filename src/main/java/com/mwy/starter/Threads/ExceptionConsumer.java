package com.mwy.starter.Threads;

import com.alibaba.fastjson.JSON;
import com.mwy.starter.config.BaseConfigProperties;
import com.mwy.starter.config.FlowRuleConfigProperties;
import com.mwy.starter.core.FlowRulePostHandler;
import com.mwy.starter.interfaces.IExceptionNotify;
import com.mwy.starter.model.ExchangeMessage;
import com.mwy.starter.utils.BeanContext;
import com.mwy.starter.utils.HttpClientUtil;
import com.mwy.starter.utils.StringUtils;
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
public class ExceptionConsumer implements Runnable {

    private LinkedBlockingQueue<ExchangeMessage> queue;

    public ExceptionConsumer(LinkedBlockingQueue<ExchangeMessage> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            try {
                if(queue!=null && !queue.isEmpty()){
                    /*if(!KeepAliveActuatorConfig.alive){
                        log.error("(exception-catch)can not connect to [Monitor Server Centre]......");
                        return;
                    }*/
                    ExchangeMessage message = queue.take();
                    //过滤规则
                    FlowRuleConfigProperties ruleConfigProperties = BeanContext.getBean(FlowRuleConfigProperties.class);
                    if(ruleConfigProperties.isEnable()){
                        FlowRulePostHandler flowRulePostHandler = BeanContext.getBean(FlowRulePostHandler.class);
                        message = flowRulePostHandler.ruleHandler(message);
                    }
                    log.info("消费者消费信息开始,{}",JSON.toJSONString(message));
                    if(message!=null && StringUtils.isNotBlank(message.getEmail())){
                        exchangeMessage(message);
                    }
                }
            } catch (InterruptedException e) {
                log.error("(exception-catch)Exception Monitor consumer invoke error->InterruptedException:"+e.getMessage());
            } catch (Exception e){
                //优化点：如有必要，基于消费失败的补偿措施，重新put会陷入死循环
                e.printStackTrace();
                log.info("(exception-catch)Exception Monitor consumer invoke error->Exception:"+e.getMessage());
            }
        }
    }

    private void exchangeMessage(ExchangeMessage message){
        BaseConfigProperties configProperties = BeanContext.getBean(BaseConfigProperties.class);
        //获取RestTemplate
        if(StringUtils.isBlank(configProperties.getServerAddress())){
            IExceptionNotify notifyImpl = BeanContext.getBean(IExceptionNotify.class);
            if(notifyImpl!=null){
                notifyImpl.expsCollect(message);
                notifyImpl.expsNotify(message);
            }
        }else{
            String url = configProperties.getServerAddress()+configProperties.getPushCreate();
            log.info("(exception-catch)request url:{}",url);
            String resp = HttpClientUtil.doPostJson(url,JSON.toJSONString(message));
            log.info("(exception-catch)response json:{}",JSON.toJSONString(resp));
        }
    }
}
