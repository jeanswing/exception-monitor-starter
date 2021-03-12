package com.mwy.starter.core;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
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
import java.lang.reflect.AnnotatedElement;
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
        AnnotatedElement ae = MethodSignature.class.cast(point.getSignature()).getMethod();
        //if (!AnnotationUtil.hasAnnotation(ae, ExceptionMonitor.class)) return;
        //获取注解参数
        ExceptionMonitorAnnotationModel model = JSON.parseObject(JSON.toJSONString(AnnotationUtil.getAnnotationValueMap(ae, ExceptionMonitor.class))
                , ExceptionMonitorAnnotationModel.class);

        Class expType = ex.getClass();

        //过滤ignore异常
        List<String> ignoreExceptionNames = model.getIgnoreExceptionName();

        if (CollectionUtil.isNotEmpty(ignoreExceptionNames)) {
            for (String ignoreExceptionName : ignoreExceptionNames) {
                String lowerName = ignoreExceptionName.toLowerCase();
                if (lowerName.equals(expType.getName().toLowerCase()) || lowerName.equals(expType.getTypeName().toLowerCase()) ||
                        lowerName.equals(expType.getSimpleName().toLowerCase()) || lowerName.equals(expType.getCanonicalName().toLowerCase())) {
                    return;
                }
            }
        }
        StackTraceElement trace = ex.getStackTrace()[0];
        ExchangeMessage message = new ExchangeMessage();

        message.setOccurTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).setEmail(model.getNoticeEmails())
                .setCustomizedInfo(model.getCustomizedInfo()).setClassName(trace.getClassName()).setExpClassType(ex.getClass().getName())
                .setFileName(trace.getFileName()).setMethodName(trace.getMethodName()).setExpLineNum(trace.getLineNumber());
        if(model.getShowExceptionDetail()){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ex.getStackTrace().length; i++) {
                sb.append(ex.getStackTrace()[i]).append("\n");
            }
            message.setExpCause(sb.toString());
        }

        exceptionMonitorPostHandler.handleException(message);
    }
}
