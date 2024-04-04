package com.sky.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName: MultiDataSourceConfig
 * @Desc: 多数据源配置
 * @Author: txm
 * @Date: 2022/12/11 9:59
 **/
//@Configuration
//public class MultiDataSourceConfig {
//
//    @Bean(name = "primaryDataSource")
//    @ConfigurationProperties(prefix = "datasource.primary.druid")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "secondaryDataSource")
//    @ConfigurationProperties(prefix = "datasource.secondary.druid")
//    public DataSource secondaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//}
