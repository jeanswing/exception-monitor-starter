package com.mwy.starter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jack Ma
 * @description 基础config文件
 * @date 2021-01-19
 **/
@ConditionalOnProperty(
        prefix = "monitor",
        name = "enable",
        havingValue = "true"
)
@Configuration
@Slf4j
public class BaseConfig {

    @Bean
    public void initBaseConfig(){ log.warn("Initialized [exception-monitor] base config successfully,start initialize all other configs..."); }
}
