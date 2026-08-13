package com.cheny.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import com.cheny.domain.enums.GenderEnum;
import com.cheny.domain.enums.JobEnum;
import lombok.Data;

/**
 * 员工表
 * @TableName emp
 */
@TableName(value ="emp")
@Data
public class Emp {
    /**
     * ID,主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别, 1:男, 2:女
     */
    private GenderEnum gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 职位(数据库 tinyint 编码,1/2/3... 对应 JobEnum 中的枚举)
     */
    private JobEnum job;

    /**
     * 薪资
     */
    private Integer salary;

    /**
     * 头像
     */
    private String image;

    /**
     * 入职日期
     */
    private LocalDateTime entryDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    private Integer deptId;
}