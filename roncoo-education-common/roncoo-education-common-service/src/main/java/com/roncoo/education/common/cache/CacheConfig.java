package com.roncoo.education.common.cache;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 保持原有的自定义KeyGenerator，与多级缓存配置兼容
 *
 * @author wujing
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 自定义key. 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     */
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append("::");
            sb.append(method.getName());
            for (Object obj : objects) {
                if (obj != null) {
                    sb.append(obj.toString());
                }
            }
            return sb.toString();
        };
    }

}
