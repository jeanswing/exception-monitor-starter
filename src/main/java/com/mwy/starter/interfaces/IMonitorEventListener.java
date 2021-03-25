package com.mwy.starter.interfaces;

import java.util.Map;

/**
 * @author Jack Ma
 * @description
 * @date 2021-02-20
 **/
public interface IMonitorEventListener{

    void eventPublish(String key, Object value);

    void eventPublish(Map<String,Object> params);

    void configRefreshEventRegisterPublishAll(String key, Object value);
}
