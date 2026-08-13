package com.cheny.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

/**
 * 工作经历表
 * @TableName emp_expr
 */
@TableName(value = "emp_expr")

@Data
public class EmpExpr {
    /**
     * ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date begin;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date end;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位
     */
    private String job;

    /**
     * 关联的员工ID
     */
    private Integer empId;


}