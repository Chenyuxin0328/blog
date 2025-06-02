package com.chenyuxin.rear.config;

import com.chenyuxin.rear.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有 API 请求
                .excludePathPatterns("/auth/**","/user/register","/user/avatar","/user/username");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173", "http://115.190.94.156:5173") // 根据你实际部署情况改
                .allowedMethods("*")
                .allowedHeaders("*") // 推荐使用 *，避免 Authorization 被忽略
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
