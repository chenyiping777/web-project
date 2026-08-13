package com.cheny.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


// 职位枚举:数据库 emp.job 是 tinyint 存编码,这里统一维护"编码 <-> 中文描述"映射
// 工作类型很多,1/2 只是目前的一部分,新增类型时在此追加即可(注意 code 不要重复)
@Getter
public enum JobEnum implements IEnum<Integer> {
    TEACHER(1, "班主任"),
    LECTURER(2, "讲师"),
    STUDENT_AFFAIRS_MANAGER(3, "学工主管"),
    TEACHING_MANAGER(4, "教学主管"),
    PRINCIPAL(5, "校长");



    private final Integer code;
    private final String desc;

    JobEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 前端传数字编码时反序列化(如 subject: 3)
    // 前端传数字编码时反序列化 (如 job: 3)
    @JsonCreator
    public static JobEnum of (Integer code) {
        if (code == null) {
            return null;
        }
        for (JobEnum job : values ()) {
            if (job.code.equals (code)) {
                return job;
            }
        }
// 数据库可能存了未列举的编码，此时返回 null, 由上层按需处理
        return null;
    }
    // 写入数据库的编码
    @Override
    public Integer getValue () {
        return this.code;
    }

    // 返回前端的中文描述
    @JsonValue
    public String getDesc () {
        return desc;
    }
}