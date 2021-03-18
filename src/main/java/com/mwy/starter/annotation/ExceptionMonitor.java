package com.mwy.starter.annotation;

import java.lang.annotation.*;

/**
 *  * 注解提供继承性，建议不要使用在baseService,baseController等基础base类上
 *  * 建议放在具体接口service、方法、baseMapper上
 *  * expandExcDetail       是否展开所有异常信息,默认true
 *  * ignoreExc             忽略的异常名,支持类名简写和全写,忽略大小写
 *  * custom                定制化信息,便于区分重要性和标识特殊性
 *  * notifyUsers           通知到的用户唯一标识(如邮件、企业微信等)，优先级高于全局系统配置(不配置默认使用全局)
 *  * @author Jack Ma
 *  * @since  v1.0.0
 *  * @date 2021-01-15
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface ExceptionMonitor {

    boolean expandExcDetail() default true;
    String[] ignoreExc() default {};
    String custom() default "";
    String notifyUsers() default "";
}
