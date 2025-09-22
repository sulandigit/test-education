package com.roncoo.education.common.elasticsearch;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Elasticsearch 课程信息文档实体类
 * 用于在 Elasticsearch 中存储和搜索课程相关信息
 * 支持全文搜索、分类筛选、价格范围查询等功能
 *
 * 索引名：rc_es_course
 * 文档类型：课程文档
 * 
 * @author wuyun
 * @date 2022/1/1
 */
@Document(indexName = EsCourse.COURSE)
@Data
@Accessors(chain = true)
public class EsCourse implements Serializable {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Elasticsearch 索引名称常量
     */
    public static final String COURSE = "rc_es_course";

    /**
     * 课程主键 ID，也作为 Elasticsearch 文档 ID
     */
    @Id
    private Long id;
    /**
     * 课程创建时间
     */
    private Date gmtCreate;
    /**
     * 课程最后修改时间
     */
    private Date gmtModified;
    /**
     * 课程名称，支持全文搜索
     */
    private String courseName;
    /**
     * 课程封面图片 URL
     */
    private String courseLogo;
    /**
     * 课程所属分类 ID，用于分类筛选
     */
    private Long categoryId;
    /**
     * 课程是否免费标识
     * 1: 免费课程
     * 0: 收费课程
     */
    private Integer isFree;
    /**
     * 课程原价（未优惠时的价格）
     */
    private BigDecimal rulingPrice;
    /**
     * 课程优惠后的实际售价
     */
    private BigDecimal coursePrice;
    /**
     * 课程排序权重，数值越大排序越靠前
     */
    private Integer courseSort;

}
