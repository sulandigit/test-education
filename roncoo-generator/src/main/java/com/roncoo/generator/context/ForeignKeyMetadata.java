package com.roncoo.generator.context;

import lombok.Data;

/**
 * 外键元数据
 * 
 * @author roncoo-generator
 */
@Data
public class ForeignKeyMetadata {
    
    /**
     * 外键名称
     */
    private String foreignKeyName;
    
    /**
     * 本表字段名
     */
    private String columnName;
    
    /**
     * 引用表名
     */
    private String referencedTableName;
    
    /**
     * 引用字段名
     */
    private String referencedColumnName;
    
    /**
     * 删除规则（CASCADE, SET NULL, RESTRICT等）
     */
    private String deleteRule;
    
    /**
     * 更新规则（CASCADE, SET NULL, RESTRICT等）
     */
    private String updateRule;
}