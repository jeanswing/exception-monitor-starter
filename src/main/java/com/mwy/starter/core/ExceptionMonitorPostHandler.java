package com.mwy.starter.core;

import com.mwy.starter.Threads.ExceptionProvider;
import com.mwy.starter.config.BaseConfig;
import com.mwy.starter.config.BaseConfigProperties;
import com.mwy.starter.config.ExceptionMonitorExecutorConfig;
import com.mwy.starter.model.ExchangeMessage;
import com.mwy.starter.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
@Component
@EnableConfigurationProperties(BaseConfigProperties.class)
@Slf4j
@ConditionalOnBean({BaseConfig.class,ExceptionMonitorExecutorConfig.class})
public class ExceptionMonitorPostHandler {

    @Autowired
    BaseConfigProperties BaseConfigProperties;
    @Resource
    ExceptionMonitorExecutor exceptionMonitorExecutor;

    public void handleException(ExchangeMessage message){
        message.setWechat(BaseConfigProperties.getReceiveWechat()).setSysName(BaseConfigProperties.getSysName());
        String email = message.getEmail();
        if(StringUtils.isBlank(email)){
            message.setEmail(BaseConfigProperties.getReceiveEmail());
        }
        ExceptionProvider provider = new ExceptionProvider(message,exceptionMonitorExecutor.getQueue());
        //无配置过滤
        if(StringUtils.isBlank(message.getEmail())){
            return;
        }
        ThreadPoolExecutor providerPool = exceptionMonitorExecutor.getProvidePool();
        if(!providerPool.isShutdown()){
            providerPool.execute(provider);
        }
    }
}
