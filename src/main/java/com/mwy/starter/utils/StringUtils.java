package com.mwy.starter.utils;

/**
 * @author Jack Ma
 * @description 字符串工具类(避免包导入)
 * @date 2021-01-21
 **/
public class StringUtils {

    public static boolean isBlank(String str){
        return str==null || "".equals(str) || str.length()==0;
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }
}
