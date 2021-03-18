package com.mwy.starter.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import java.io.Serializable;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
@Accessors(chain = true)
@Component
public class ExchangeMessage implements Serializable{
    //系统名称
    private String sysName;
    //接收用户标识
    private String receiveUser;
    //异常所在文件名
    private String fileName;
    //异常所在类名
    private String className;
    //方法名
    private String methodName;
    //异常行数
    private Integer expLineNum;
    //异常类型
    private String expClassType;
    //异常更详情内容
    private String expCause;
    //定制化信息
    private String custom;
    //发生时间
    private String occurTime;
}
