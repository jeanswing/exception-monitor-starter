package com.mwy.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import static com.mwy.starter.utils.Constants.*;

/**
 * @author Jack Ma
 * @description 系统默认启动配置
 * @date 2021-01-18
 **/
@ConfigurationProperties(prefix = Default_Config_Prefix)
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
    private Integer maxQueueSize = Default_Task_Queue_Size;

    private final String keepAliveTest = Monitor_Keep_Alive_Url;

    private final String pushCreate = Monitor_Create_Url;
}
