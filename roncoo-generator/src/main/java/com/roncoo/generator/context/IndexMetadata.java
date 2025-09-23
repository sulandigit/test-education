package com.roncoo.generator.context;

import lombok.Data;

/**
 * 索引元数据
 * 
 * @author roncoo-generator
 */
@Data
public class IndexMetadata {
    
    /**
     * 索引名称
     */
    private String indexName;
    
    /**
     * 索引字段列表
     */
    private java.util.List<String> columnNames;
    
    /**
     * 是否唯一索引
     */
    private Boolean unique = false;
    
    /**
     * 索引类型（BTREE, HASH等）
     */
    private String indexType;
    
    /**
     * 索引注释
     */
    private String comment;
}