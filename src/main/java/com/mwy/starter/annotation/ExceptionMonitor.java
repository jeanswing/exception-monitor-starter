package com.mwy.starter.annotation;

import java.lang.annotation.*;

/**
 *  * 用于捕捉类、方法出现的各种异常信息，及时反馈到Monitor中心用于统计、提示、警告等处理
 *  * 注解提供继承性，建议不要使用在baseService,baseController等基础base类上
 *  * 建议放在具体接口service、方法、baseMapper上
 *  * showExceptionDetail  是否提供异常更详情信息(包含所有异常)
 *  * ignoreExceptionName  需要忽略的异常类名,支持类名称简写和全路径名称,默认忽略大小写
 *  * customizedInfo       自定义定制化信息,便于定位或者特殊标出
 *  * noticeEmails         异常邮件提示地址，多个使用英文逗号隔开，优先级高于全局系统配置(不配置默认使用全局)
 *  * @author Jack Ma
 *  * @since  v1.0.0
 *  * @date 2021-01-15
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface ExceptionMonitor {

    boolean showExceptionDetail() default true;
    String[] ignoreExceptionName() default {};
    String customizedInfo() default "";
    String noticeEmails() default "";
}
