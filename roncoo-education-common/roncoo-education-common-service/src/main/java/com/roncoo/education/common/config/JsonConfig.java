package com.roncoo.education.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JSON 序列化配置类
 * 配置 Jackson ObjectMapper，将 Long 类型字段序列化为字符串
 * 防止前端 JavaScript 处理大数字时精度丢失问题
 * 
 * @author fengyw
 * @date 2022/1/1
 */
@Configuration
public class JsonConfig {

    /**
     * 自定义 Jackson ObjectMapper Bean
     * 配置 Long 类型字段序列化为字符串，防止 JavaScript 大数字精度丢失
     * 
     * @param builder Jackson2ObjectMapperBuilder 实例
     * @return 配置好的 ObjectMapper
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.createXmlMapper(false).build();
        // 创建简单模块，用于添加自定义序列化器
        SimpleModule simpleModule = new SimpleModule();
        // 将 Long 类型和 long 原始类型都序列化为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);
        return mapper;
    }

}
