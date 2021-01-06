package com.cskt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: 设置全局跨域 跨域类
 * @author: Mr.阿九
 * @createTime: 2021-01-06 13:42
 **/
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "HEAD",
                        "OPTIONS")
                .maxAge(3600)
                .allowedHeaders("*");
    }

}
