package com.cskt.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @description: 分页配置文件
 * @author: Mr.阿九
 * @createTime: 2021-01-05 15:51
 **/
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.cskt.mapper"})
public class MyBatisPlusConfig {
    /**
     * 配置分页插件，从3.4.0开始使用新的配置方式
     * @return
     */
    @Bean
    public MybatisPlusInterceptor plusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new
                MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new
                PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
