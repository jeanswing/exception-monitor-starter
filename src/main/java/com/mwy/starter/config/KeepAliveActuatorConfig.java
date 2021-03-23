package com.mwy.starter.config;

import com.mwy.starter.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Configuration
@EnableConfigurationProperties(BaseConfigProperties.class)
@ConditionalOnBean(BaseConfig.class)
@ConditionalOnProperty(
        prefix = "monitor",
        name = "server-address"
)
@Slf4j
public class KeepAliveActuatorConfig {

    @Autowired
    BaseConfigProperties configProperties;

    //心跳测试是否可用
    public static volatile boolean alive = false;

    @Bean
    public void keepAlive(){
        log.warn("(exception-monitor)Initializing KeepAliveThread:  [KeepAliveThread] start......");
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(()->{
            alive = HttpClientUtil.connectForGet(configProperties.getServerAddress()+configProperties.getKeepAliveTest());
            log.info("(exception-monitor) keep alive:{}",alive?"connected":"unconnected");
        }, 5, 3, TimeUnit.SECONDS);
    }
}
