package com.mwy.starter.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
public class ExceptionMonitorAnnotationModel implements Serializable {

    private String customizedInfo;

    private Boolean showExceptionDetail;

    private List<String> ignoreExceptionName;

    private String noticeEmails;
}
