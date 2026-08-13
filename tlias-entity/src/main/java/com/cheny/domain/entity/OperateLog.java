package com.cheny.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 操作日志表
 * @TableName operate_log
 */
@TableName(value ="operate_log")
@Data
public class OperateLog {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 操作人ID
     */
    private Integer operateEmpId;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作类名
     */
    private String className;

    /**
     * 操作方法名
     */
    private String methodName;

    /**
     * 操作方法参数
     */
    private String methodParams;

    /**
     * 返回值
     */
    private String returnValue;

    /**
     * 方法执行耗时, 单位:ms
     */
    private Long costTime;

    /**
     * 操作人姓名（非数据库字段，查询时关联填充）
     */
    @TableField(exist = false)
    private String operateEmpName;

}