package com.cheny.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 学科枚举：1:语文, 2:数学, 3:英语, 4:物理, 5:化学, 6:生物
 */
@Getter
public enum SubjectEnum implements IEnum<Integer> {

    //枚举常量，预创建好的固定实例对象  SubjectEnum CHINESE = new SubjectEnum(1,"语文")
    CHINESE(1, "语文"),
    MATH(2, "数学"),
    ENGLISH(3, "英语"),
    PHYSICS(4, "物理"),
    CHEMISTRY(5, "化学"),
    BIOLOGY(6, "生物");

    /** 数据库存储值 */
    private final Integer code;
    /** 展示文本 */
    private final String desc;

    SubjectEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * MP数据库映射：存入数据库的值
     */
    @Override
    public Integer getValue() {
        return this.code;
    }

    /**
     * Jackson序列化：接口返回中文文本 "语文"/"数学"
     * ⚠️ @JsonValue 放在getDesc()，不要放在code字段
     */
    @JsonValue
    public String getDesc() {
        return desc;
    }

    /**
     * Jackson 反序列化:前端传 subject:1(数字 code)时也能映射到对应枚举
     */
    @JsonCreator
    public static SubjectEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SubjectEnum value : SubjectEnum.values()) {//values()枚举自带方法，返回全部实例数组
            if (value.getCode().equals(code)) {
                return value;//返回枚举实例对象
            }
        }
        throw new IllegalArgumentException("未知的学科编码：" + code);
    }
}