package com.mwy.starter.core;

import com.mwy.starter.interfaces.IMonitorEventListener;
import com.mwy.starter.utils.BeanContext;
import com.mwy.starter.utils.StringUtils;
import java.util.Map;

/**
 * @author Jack Ma
 * @description
 * @date 2021-02-25
 **/
public abstract class AbstractConfigRefreshListener implements IMonitorEventListener {

    @Override
    public final void configRefreshEventRegisterPublishAll(String key,Object val) {
        if(StringUtils.isBlank(key)) return;
        Map<String,IMonitorEventListener> map;
        if((map=BeanContext.getBeanByType(IMonitorEventListener.class))==null||map.size()==0)return;
        map.values().forEach(p->p.eventPublish(key,val));
    }
}
