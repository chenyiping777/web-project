package com.cheny.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 最高学历枚举 1:初中, 2:高中, 3:大专, 4:本科, 5:硕士, 6:博士
 */
@Getter
public enum DegreeEnum implements IEnum<Integer> {

    JUNIOR_HIGH(1, "初中"),
    SENIOR_HIGH(2, "高中"),
    COLLEGE(3, "大专"),
    BACHELOR(4, "本科"),
    MASTER(5, "硕士"),
    DOCTOR(6, "博士");


    private final Integer code;
    private final String desc;

    DegreeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 前端传数字编码时反序列化
    @JsonCreator
    public static DegreeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (DegreeEnum degree : values()) {
            if (degree.code.equals(code)) {
                return degree;
            }
        }
        // 非法编码返回null，业务自行处理异常
        return null;
    }

    // MyBatis-Plus 数据库存储编码
    @Override
    public Integer getValue() {
        return this.code;
    }

    // 序列化给前端展示中文
    @JsonValue
    public String getDesc() {
        return desc;
    }
}
