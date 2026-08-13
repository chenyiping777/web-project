package com.cheny.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GenderEnum implements IEnum<Integer> {//IEnum<T> 是 MyBatis‑Plus 提供的枚举数据库映射接口。
    MALE(1, "男"),
    FEMALE(2, "女");

    // 删掉 @EnumValue，IEnum方案不需要这个注解
    private final Integer code;
    private final String desc;

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Jackson反序列化：前端传数字1/2，转成枚举对象
     */
    @JsonCreator
    public static GenderEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (GenderEnum gender : values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("未知的性别编码: " + code);
    }

    /**
     * MP数据库映射：存入数据库的值
     */

    // IEnum接口的getValue()方法，返回数据库存的值,对应泛型
    @Override
    public Integer getValue() {
        return this.code;
    }

    /**
     * Jackson序列化：返回前端输出 "男"/"女"，不是数字
     */
    @JsonValue
    public String getDesc() {
        return desc;
    }

//    正常查询实体 empMapper.selectById(id)
//    Emp 实体属性：private GenderEnum gender;
//    数据库读出 1
//    MP 内部：拿到数据库的值1，扫描你的枚举GenderEnum所有实例，调用每个枚举的getValue()做匹配
//    找到 MALE.getValue()=1，直接返回 GenderEnum.MALE 对象赋值给 emp.gender。
//    底层：MP 内置了一个IEnum 类型处理器 EnumTypeHandler，
//    只要实体属性是实现 IEnum 的枚举，mybatis 会自动装配这个处理器，
//    自动完成数据库值 ↔ Java 枚举对象转换。
}