package com.mwy.starter.core;

import cn.hutool.core.collection.CollectionUtil;
import com.mwy.starter.config.FlowRuleConfigProperties;
import com.mwy.starter.enums.FlowRuleEnum;
import com.mwy.starter.model.ExchangeMessage;
import com.mwy.starter.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-21
 **/
@Data
public class FlowRulePostHandler {

    public ConcurrentHashMap<String,Integer> ruleMap;

    @Autowired
    FlowRuleConfigProperties ruleConfigProperties;

    public ExchangeMessage ruleHandler(ExchangeMessage message){

        FlowRuleEnum ruleType = ruleConfigProperties.getRuleType();
        List<String> receiveUsers = Arrays.asList(message.getReceiveUser().split(","));
        String ruleKey = null;//默认total类型
        Integer num = ruleConfigProperties.getNum();
        switch (ruleType){
            case Exception: ruleKey = message.getExpClassType(); break;
            case Class: ruleKey = message.getClassName(); break;
            case Method: ruleKey = message.getMethodName(); break;
            default:break;
        }
        //过滤规则
        if(ruleMap != null){
            if(CollectionUtil.isNotEmpty(receiveUsers)){
                StringBuilder esb = new StringBuilder();
                for (String receiveUser : receiveUsers) {
                    String key = ruleKey+receiveUser;
                    Integer keyCounter = ruleMap.get(key);
                    if(keyCounter==null) keyCounter=0;
                    if(keyCounter<num){
                        ruleMap.put(key,++keyCounter);
                        esb.append(receiveUser).append(",");
                    }
                }
                String lastEmail = esb.toString();
                message.setReceiveUser(StringUtils.isBlank(lastEmail)?null:lastEmail.substring(0,lastEmail.length()-1));
            }
        }
        return message;
    }
}
