/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package com.roncoo.education.common.elasticsearch;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 分页工具类
 * 用于将 Elasticsearch 搜索结果转换为分页对象
 * 支持高亮字段处理和对象转换
 * 
 * 主要功能：
 * 1. 将 SearchHits 转换为分页对象
 * 2. 处理高亮字段，将高亮内容设置到目标对象中
 * 3. 计算分页信息（总数量、总页数等）
 *
 * @param <T> 结果对象类型，必须实现 Serializable 接口
 * @author wujing
 * @date 2022/1/1
 */
@Slf4j
public final class EsPageUtil<T extends Serializable> implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 私有构造函数，防止实例化工具类
     */

    private EsPageUtil() {
    }

    /**
     * 将 Elasticsearch 搜索结果转换为分页对象
     * 自动处理高亮字段并计算分页信息
     * 
     * @param searchHits  Elasticsearch 搜索结果
     * @param pageCurrent 当前页码（从 1 开始）
     * @param pageSize    每页显示数量
     * @param classType   目标对象类型
     * @param <T>         结果对象类型
     * @return 包含分页信息的 Page 对象
     */
    public static <T extends Serializable> Page<T> transform(SearchHits<?> searchHits, int pageCurrent, int pageSize, Class<T> classType) {
        Page<T> pb = new Page<>();
        try {
            // 转换搜索结果为对象列表
            pb.setList(copyList(searchHits.getSearchHits(), classType));
        } catch (Exception e) {
            log.error("transform error", e);
        }
        // 设置分页参数
        pb.setPageCurrent(pageCurrent);
        pb.setPageSize(pageSize);
        pb.setTotalCount((int) searchHits.getTotalHits());
        pb.setTotalPage(PageUtil.countTotalPage(pb.getTotalCount(), pageSize));
        return pb;
    }

    /**
     * 将 SearchHit 列表转换为指定类型的对象列表
     * 支持高亮字段的处理，会将高亮的内容设置到相应的字段中
     * 
     * @param source 原始 SearchHit 列表
     * @param clazz  目标对象类型
     * @param <T>    结果对象类型
     * @return 转换后的对象列表
     */
    public static <T> List<T> copyList(List<? extends SearchHit<?>> source, Class<T> clazz) {
        if (source == null || source.size() == 0) {
            return Collections.emptyList();
        }
        List<T> res = new ArrayList<>(source.size());
        for (SearchHit sh : source) {
            try {
                // 创建目标对象实例
                T t = clazz.newInstance();
                // 复制基本属性
                BeanUtils.copyProperties(sh.getContent(), t);
                // 处理高亮字段
                Map<String, List<String>> ml = sh.getHighlightFields();
                for (Map.Entry<String, List<String>> entry : ml.entrySet()) {
                    // 获取对应的字段
                    Field field = clazz.getDeclaredField(entry.getKey());
                    // 修改字段访问权限
                    field.setAccessible(true);
                    // 设置高亮内容
                    field.set(t, highlightFieldToString(entry.getValue()));
                }
                res.add(t);
            } catch (Exception e) {
                log.error("copyList error", e);
            }
        }
        return res;
    }

    /**
     * 将高亮字段列表转换为字符串
     * 将多个高亮片段拼接成一个完整的字符串
     * 
     * @param field 高亮字段列表
     * @return 拼接后的高亮内容
     */
    private static String highlightFieldToString(List<String> field) {
        StringBuilder sb = new StringBuilder();
        for (String text : field) {
            sb.append(text);
        }
        return sb.toString();
    }

}
