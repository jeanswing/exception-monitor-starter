package com.mwy.starter.core;

import com.mwy.starter.config.BaseConfigProperties;
import com.mwy.starter.config.FlowRuleConfigProperties;
import com.mwy.starter.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jack Ma
 * @description
 * @date 2021-03-01
 **/
@Component
public class ConfigRefreshListener extends AbstractConfigRefreshListener{

    @Autowired
    BaseConfigProperties baseConfigProperties;
    @Autowired
    FlowRuleConfigProperties flowRuleConfigProperties;

    @Override
    public void eventPublish(String key, Object value) {
        if(StringUtils.isBlank(key) || !key.startsWith("monitor.")) return;
        boolean isFlow = key.startsWith("monitor.flow.");
        key = this.dealHumpKey(key,isFlow);
        try {
            if(isFlow){
                this.handleProperty(key,value,flowRuleConfigProperties);
            }else{
                this.handleProperty(key,value,baseConfigProperties);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eventPublish(Map<String, Object> params) {}


    private void handleProperty(String key,Object val,Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.getName().toLowerCase().equals(key)){
                field.setAccessible(true);
                field.set(obj, val);
            }
        }
    }

    private String dealHumpKey(String key,boolean isFlow){
        if(isFlow){
            key = key.split("monitor.flow.")[1].replace("-","");
        }else{
            key = key.split("monitor.")[1].replace("-","");
        }
        return key.toLowerCase();
    }
}
