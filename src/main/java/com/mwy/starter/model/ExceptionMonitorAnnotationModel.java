package com.mwy.starter.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jack Ma
 * @description
 * @date 2021-01-18
 **/
@Data
@Accessors(chain = true)
public class ExceptionMonitorAnnotationModel implements Serializable {

    private String custom;

    private Boolean expandExcDetail;

    private List<String> ignoreExc;

    private String notifyUsers;
}
