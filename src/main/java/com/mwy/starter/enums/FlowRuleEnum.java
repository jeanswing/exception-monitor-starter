package com.mwy.starter.enums;

/**
 * @author Jack Ma
 * @desc
 * @date 2021-01-21
 **/
public enum FlowRuleEnum {

    //规则类别
    //exception 按照异常类型
    //method 按照方法类型
    //class 按照类类型
    //total 按照邮件总数量

    Exception("exception"),
    Method("method"),
    Class("class"),
    Total("total");

    private String ruleType;
    FlowRuleEnum(String ruleType){
        this.ruleType = ruleType;
    }
}
