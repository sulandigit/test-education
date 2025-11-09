package com.roncoo.generator.adapter;

import com.roncoo.generator.context.TableMetadata;
import com.roncoo.generator.context.ColumnMetadata;
import com.roncoo.generator.context.IndexMetadata;
import com.roncoo.generator.context.ForeignKeyMetadata;
import com.roncoo.generator.exception.GeneratorException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 数据库适配器接口
 * 
 * @author roncoo-generator
 */
public interface DatabaseAdapter {
    
    /**
     * 建立数据库连接
     * 
     * @return 数据库连接
     * @throws GeneratorException 生成异常
     */
    Connection connect() throws GeneratorException;
    
    /**
     * 关闭数据库连接
     * 
     * @throws GeneratorException 生成异常
     */
    void close() throws GeneratorException;
    
    /**
     * 获取数据库类型
     * 
     * @return 数据库类型
     */
    DatabaseType getDatabaseType();
    
    /**
     * 根据模式获取表名列表
     * 
     * @param pattern 表名模式（支持通配符%）
     * @return 表名列表
     * @throws GeneratorException 生成异常
     */
    List<String> getTableNames(String pattern) throws GeneratorException;
    
    /**
     * 获取表元数据
     * 
     * @param tableName 表名
     * @return 表元数据
     * @throws GeneratorException 生成异常
     */
    TableMetadata getTableMetadata(String tableName) throws GeneratorException;
    
    /**
     * 获取表的字段元数据
     * 
     * @param tableName 表名
     * @return 字段元数据列表
     * @throws GeneratorException 生成异常
     */
    List<ColumnMetadata> getColumnMetadata(String tableName) throws GeneratorException;
    
    /**
     * 获取表的索引信息
     * 
     * @param tableName 表名
     * @return 索引信息列表
     * @throws GeneratorException 生成异常
     */
    List<IndexMetadata> getIndexMetadata(String tableName) throws GeneratorException;
    
    /**
     * 获取表的外键信息
     * 
     * @param tableName 表名
     * @return 外键信息列表
     * @throws GeneratorException 生成异常
     */
    List<ForeignKeyMetadata> getForeignKeyMetadata(String tableName) throws GeneratorException;
    
    /**
     * 获取数据库类型到Java类型的映射
     * 
     * @return 类型映射表
     */
    Map<String, String> getTypeMapping();
    
    /**
     * 将数据库字段类型转换为Java类型
     * 
     * @param dbType 数据库类型
     * @param precision 精度
     * @param scale 标度
     * @return Java类型信息
     */
    JavaTypeInfo convertToJavaType(String dbType, Integer precision, Integer scale);
    
    /**
     * 获取表的主键字段
     * 
     * @param tableName 表名
     * @return 主键字段列表
     * @throws GeneratorException 生成异常
     */
    List<String> getPrimaryKeys(String tableName) throws GeneratorException;
    
    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return 是否存在
     * @throws GeneratorException 生成异常
     */
    boolean tableExists(String tableName) throws GeneratorException;
    
    /**
     * 获取数据库版本信息
     * 
     * @return 版本信息
     * @throws GeneratorException 生成异常
     */
    String getDatabaseVersion() throws GeneratorException;
    
    /**
     * 获取数据库产品名称
     * 
     * @return 产品名称
     * @throws GeneratorException 生成异常
     */
    String getDatabaseProductName() throws GeneratorException;
    
    /**
     * 数据库类型枚举
     */
    enum DatabaseType {
        MYSQL("MySQL"),
        POSTGRESQL("PostgreSQL"),
        ORACLE("Oracle"),
        SQL_SERVER("SQL Server"),
        H2("H2"),
        SQLITE("SQLite"),
        OTHER("Other");
        
        private final String displayName;
        
        DatabaseType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static DatabaseType fromProductName(String productName) {
            if (productName == null) {
                return OTHER;
            }
            
            String lower = productName.toLowerCase();
            if (lower.contains("mysql")) {
                return MYSQL;
            } else if (lower.contains("postgresql")) {
                return POSTGRESQL;
            } else if (lower.contains("oracle")) {
                return ORACLE;
            } else if (lower.contains("sql server")) {
                return SQL_SERVER;
            } else if (lower.contains("h2")) {
                return H2;
            } else if (lower.contains("sqlite")) {
                return SQLITE;
            }
            
            return OTHER;
        }
    }
    
    /**
     * Java类型信息内部类
     */
    class JavaTypeInfo {
        /**
         * Java类型名称（简短）
         */
        private String shortName;
        
        /**
         * Java类型全限定名
         */
        private String fullName;
        
        /**
         * 是否需要导入
         */
        private boolean needsImport;
        
        public JavaTypeInfo(String shortName, String fullName) {
            this.shortName = shortName;
            this.fullName = fullName;
            this.needsImport = fullName != null && fullName.contains(".");
        }
        
        public String getShortName() {
            return shortName;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public boolean isNeedsImport() {
            return needsImport;
        }
    }
}