package com.mwy.starter.config;

import com.mwy.starter.core.FlowRulePostHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Ma
 * @description 限流规则初始化配置
 * @date 2021-01-21
 **/
@Configuration
@ConditionalOnBean(BaseConfig.class)
@EnableConfigurationProperties(FlowRuleConfigProperties.class)
@Slf4j
@ConditionalOnProperty(
        prefix = "monitor.flow",
        name = "enable",
        havingValue = "true"
)
public class FlowRuleConfig {

    @Autowired
    FlowRuleConfigProperties flowRuleConfigProperties;

    @Bean("flowRulePostHandler")
    public FlowRulePostHandler flowRuleHandler(){
        FlowRulePostHandler flowRule = new FlowRulePostHandler();
        if(flowRuleConfigProperties.isEnable()){
            flowRule.setRuleMap(new ConcurrentHashMap<>(30000));
        }

        Integer num = flowRuleConfigProperties.getNum();
        if(num==null || num<=0){
            log.warn("(exception-monitor)config [monitor.flow.num] is unavailable，set it 300 default");
            flowRuleConfigProperties.setNum(300);
        }

        Executors.newSingleThreadScheduledExecutor().schedule(()->{
            if(flowRule!=null){
                flowRule.setRuleMap(new ConcurrentHashMap<>(30000));
            }
        }, 24, TimeUnit.HOURS);
        return flowRule;
    }
}
