package com.cheny.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.cheny.domain.enums.SubjectEnum;
import lombok.Data;

/**
 * 班级表
 * @TableName clazz
 */

@TableName(value ="clazz")
@Data
public class Clazz {
    /**
     * ID,主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 班级名称
     */
    private String name;

    /**
     * 班级教室
     */
    private String room;

    /**
     * 开课时间
     */
    private Date beginDate;

    /**
     * 结课时间
     */
    private Date endDate;

    /**
     * 班主任ID, 关联员工表ID
     */
    private Integer masterId;

    /**
     * 学科, 1:语文, 2:数学, 3:英语, 4:物理, 5:化学, 6: 生物
     */
    private SubjectEnum subject;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
