package com.mwy.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jack Ma
 * @description 系统默认启动配置
 * @date 2021-01-18
 **/
@ConfigurationProperties(prefix = "monitor")
@Data
public class BaseConfigProperties {

    //是否启用
    private Boolean enable = true;
    //被监控系统名称
    private String sysName;
    //监控中心地址
    private String serverAddress;
    //消息接收人唯一标识
    private String receiveUser;
    //默认最大队列
    private Integer maxQueueSize = 30000;

    private final String keepAliveTest = "/monitor/keepAliveTest";

    private final String pushCreate = "/monitor/create";
}
