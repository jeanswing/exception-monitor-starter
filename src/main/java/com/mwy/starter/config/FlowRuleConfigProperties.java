package com.mwy.starter.config;

import com.mwy.starter.enums.FlowRuleEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jack Ma
 * @description 限流规则配置
 * @date 2021-01-21
 **/
@ConfigurationProperties(prefix = "monitor.flow")
@Data
public class FlowRuleConfigProperties {
    private boolean enable = false;

    //规则类别
    //exception 按照异常类型
    //method 按照方法类型
    //class 按照类类型
    //total 按照邮件总数量
    private FlowRuleEnum ruleType = FlowRuleEnum.Total;

    //通知数量
    private Integer num = 200;
}
