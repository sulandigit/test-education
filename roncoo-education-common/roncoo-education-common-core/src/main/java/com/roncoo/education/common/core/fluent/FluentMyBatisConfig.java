package com.roncoo.education.common.core.fluent;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * FluentMyBatis 配置类
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
@Configuration
public class FluentMyBatisConfig {

    /**
     * 定义mybatis的SqlSessionFactoryBean
     * 
     * @param dataSource 数据源
     * @return SqlSessionFactoryBean实例
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        
        // 设置XML mapper文件位置（保持与现有MyBatis兼容）
        bean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath*:/mybatis/**/*Mapper.xml")
        );
        
        // 设置类型别名包路径
        bean.setTypeAliasesPackage("com.roncoo.education.*.dao.impl.mapper.entity");
        
        return bean;
    }

    /**
     * 定义mybatis的MapperScannerConfigurer
     * FluentMyBatis会自动扫描Mapper接口
     * 
     * @return MapperFactory实例
     */
    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }
}