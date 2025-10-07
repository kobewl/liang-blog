package com.liang.blog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.liang.blog.mapper")
public class MyBatisPlusConfig {}
