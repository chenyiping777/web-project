package com.cheny.config;

import com.cheny.interceptor.TokenInterceptor;
import com.cheny.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    // 注册自定义类型转换器：把查询参数里的数字编码绑定到 IEnum 枚举（如 ?degree=4 → DegreeEnum.BACHELOR）
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IEnumConverterFactory());
    }

    // 注册拦截器：除登录/注册外，所有接口都需要 token 校验,前端携带 token
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") // 拦截所有接口
                .excludePathPatterns(
                        "/user/login",       // 登录接口放行
                        "/user/register"     // 注册放行
                );
    }

    /**
     * 跨域配置：用 CorsFilter（过滤器层）而不是 addCorsMappings（MVC 层）。
     * 原因：MVC 层的预检请求（OPTIONS）仍会走拦截器链，被 TokenInterceptor 拦成 401，
     *       浏览器看到预检失败会直接阻断真实请求；
     *       CorsFilter 在过滤器层提前处理预检并返回 200，拦截器不会再拦截到 OPTIONS。
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // 允许任意来源（file:// 的 Origin 为 null 也放行）
        config.setAllowedMethods(List.of("*"));        // 允许任意请求方式 GET/POST/PUT/DELETE...
        config.setAllowedHeaders(List.of("*"));        // 允许携带任意请求头（包括 token）
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
