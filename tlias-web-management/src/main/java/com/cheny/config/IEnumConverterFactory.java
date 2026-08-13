package com.cheny.config;

import com.baomidou.mybatisplus.annotation.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 把「查询参数 / 表单参数」里的编码值转换成对应的 IEnum 枚举实例。
 *
 * 背景：
 * Spring MVC 对 @ModelAttribute 绑定的枚举字段，默认用 Enum.valueOf(枚举名) 转换，
 * 只能识别枚举常量名（如 ?degree=BACHELOR），无法识别数字编码（如 ?degree=4）。
 * 而 @JsonCreator 只对 Jackson 解析 JSON 请求体生效，对 query/form 参数不生效。
 *
 * 本工厂对所有实现 {@link IEnum} 的枚举统一处理：
 * 先按 getValue()（数据库编码）匹配，再兜底按枚举 name() 匹配，都匹配不上返回 null。
 * 这样 ?degree=4、?subject=1 等数字编码就能正确绑定到 DegreeEnum / SubjectEnum。
 */
public class IEnumConverterFactory implements ConverterFactory<String, IEnum<?>> {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends IEnum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            String value = source.trim();
            // 1) 按数据库编码 getValue() 匹配
            for (T constant : targetType.getEnumConstants()) {
                Object code = constant.getValue();
                if (code != null && String.valueOf(code).equals(value)) {
                    return constant;
                }
            }
            // 2) 兜底：按枚举常量名匹配
            try {
                return (T) Enum.valueOf((Class) targetType, value);
            } catch (IllegalArgumentException e) {
                // 无法匹配返回 null，Spring 会按 typeMismatch 处理
                return null;
            }
        };
    }
}
