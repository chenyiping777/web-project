package com.cheny.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OperateLogVo {
    private Integer id;
    private Integer operateEmpId;
    private String operateEmpName; //关联emp查询出来
    private Date operateTime;
    private String className;
    private String methodName;
    private String methodParams;
    private String returnValue;
    private Long costTime;
}
