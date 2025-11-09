package com.roncoo.generator.context;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表元数据
 * 
 * @author roncoo-generator
 */
@Data
public class TableMetadata {
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 表注释
     */
    private String tableComment;
    
    /**
     * 实体类名
     */
    private String entityName;
    
    /**
     * 字段列表
     */
    private List<ColumnMetadata> columns;
    
    /**
     * 主键字段列表
     */
    private List<String> primaryKeys;
    
    /**
     * 索引信息
     */
    private List<IndexMetadata> indexes;
    
    /**
     * 外键信息
     */
    private List<ForeignKeyMetadata> foreignKeys;
    
    /**
     * 表类型（TABLE, VIEW等）
     */
    private String tableType = "TABLE";
    
    /**
     * 表所属模式/数据库
     */
    private String schema;
    
    /**
     * 表的创建时间
     */
    private java.util.Date createTime;
    
    /**
     * 表的更新时间
     */
    private java.util.Date updateTime;
    
    /**
     * 表的存储引擎
     */
    private String engine;
    
    /**
     * 表的字符集
     */
    private String charset;
    
    /**
     * 表的排序规则
     */
    private String collation;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
    
    /**
     * 获取主键字段元数据
     * 
     * @return 主键字段列表
     */
    public List<ColumnMetadata> getPrimaryKeyColumns() {
        if (columns == null || primaryKeys == null) {
            return java.util.Collections.emptyList();
        }
        
        return columns.stream()
                .filter(col -> primaryKeys.contains(col.getColumnName()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取非主键字段元数据
     * 
     * @return 非主键字段列表
     */
    public List<ColumnMetadata> getNonPrimaryKeyColumns() {
        if (columns == null || primaryKeys == null) {
            return columns == null ? java.util.Collections.emptyList() : columns;
        }
        
        return columns.stream()
                .filter(col -> !primaryKeys.contains(col.getColumnName()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据字段名获取字段元数据
     * 
     * @param columnName 字段名
     * @return 字段元数据
     */
    public ColumnMetadata getColumnByName(String columnName) {
        if (columns == null) {
            return null;
        }
        
        return columns.stream()
                .filter(col -> col.getColumnName().equals(columnName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查表是否有主键
     * 
     * @return 是否有主键
     */
    public boolean hasPrimaryKey() {
        return primaryKeys != null && !primaryKeys.isEmpty();
    }
    
    /**
     * 获取导入的包列表
     * 
     * @return 导入包列表
     */
    public List<String> getImportPackages() {
        if (columns == null) {
            return java.util.Collections.emptyList();
        }
        
        return columns.stream()
                .map(ColumnMetadata::getJavaType)
                .filter(javaType -> javaType != null && javaType.contains("."))
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
}