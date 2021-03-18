package com.mwy.starter.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.mwy.starter.annotation.ExceptionMonitor;
import com.mwy.starter.config.BaseConfig;
import com.mwy.starter.model.ExceptionMonitorAnnotationModel;
import com.mwy.starter.model.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-15
 **/
@Aspect
@Component
@ConditionalOnBean({BaseConfig.class})
@Slf4j
public class ExceptionMonitorActuator {

    @Autowired
    ExceptionMonitorPostHandler exceptionMonitorPostHandler;

    @AfterThrowing(throwing="ex",value = "@annotation(com.mwy.starter.annotation.ExceptionMonitor) || @within(com.mwy.starter.annotation.ExceptionMonitor)")
    public void catchException(JoinPoint point, Throwable ex){

        Class expType = ex.getClass();
        StackTraceElement trace = ex.getStackTrace()[0];
        ExchangeMessage message = new ExchangeMessage();
        ExceptionMonitorAnnotationModel model = this.getAnnotationInfo((MethodSignature) point.getSignature());

        List<String> ignoreExcs = model.getIgnoreExc();
        if (CollectionUtil.isNotEmpty(ignoreExcs)) {
            for (String ignoreExc : ignoreExcs) {
                String lowerName = ignoreExc.toLowerCase();
                if (lowerName.equals(expType.getName().toLowerCase()) || lowerName.equals(expType.getTypeName().toLowerCase()) ||
                        lowerName.equals(expType.getSimpleName().toLowerCase()) || lowerName.equals(expType.getCanonicalName().toLowerCase())) {
                    return;
                }
            }
        }

        message.setOccurTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).setReceiveUser(model.getNotifyUsers())
                .setCustom(model.getCustom()).setClassName(trace.getClassName()).setExpClassType(ex.getClass().getName())
                .setFileName(trace.getFileName()).setMethodName(trace.getMethodName()).setExpLineNum(trace.getLineNumber());
        if(model.getExpandExcDetail()){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ex.getStackTrace().length; i++) {
                sb.append(ex.getStackTrace()[i]).append("\n");
            }
            message.setExpCause(sb.toString());
        }

        exceptionMonitorPostHandler.handleException(message);
    }

    private ExceptionMonitorAnnotationModel getAnnotationInfo(MethodSignature signature){
        ExceptionMonitor at;
        if((at = signature.getMethod().getAnnotation(ExceptionMonitor.class)) == null){
            at = (ExceptionMonitor) signature.getDeclaringType().getAnnotation(ExceptionMonitor.class);
        }
        ExceptionMonitorAnnotationModel model = new ExceptionMonitorAnnotationModel().setCustom(at.custom())
                .setIgnoreExc(Arrays.asList(at.ignoreExc())).setNotifyUsers(at.notifyUsers())
                .setExpandExcDetail(at.expandExcDetail());
        return model;
    }
}
