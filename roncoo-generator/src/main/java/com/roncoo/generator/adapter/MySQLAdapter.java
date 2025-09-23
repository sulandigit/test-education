package com.roncoo.generator.adapter;

import com.roncoo.generator.config.DatabaseConfig;
import com.roncoo.generator.context.*;
import com.roncoo.generator.exception.GeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

/**
 * MySQL数据库适配器
 * 
 * @author roncoo-generator
 */
@Slf4j
public class MySQLAdapter implements DatabaseAdapter {
    
    /**
     * 数据库配置
     */
    private final DatabaseConfig config;
    
    /**
     * 数据库连接
     */
    private Connection connection;
    
    /**
     * MySQL类型映射
     */
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    
    static {
        // 字符串类型
        TYPE_MAPPING.put("CHAR", "String");
        TYPE_MAPPING.put("VARCHAR", "String");
        TYPE_MAPPING.put("TEXT", "String");
        TYPE_MAPPING.put("LONGTEXT", "String");
        TYPE_MAPPING.put("MEDIUMTEXT", "String");
        TYPE_MAPPING.put("TINYTEXT", "String");
        
        // 数值类型
        TYPE_MAPPING.put("TINYINT", "Integer");
        TYPE_MAPPING.put("SMALLINT", "Integer");
        TYPE_MAPPING.put("MEDIUMINT", "Integer");
        TYPE_MAPPING.put("INT", "Integer");
        TYPE_MAPPING.put("INTEGER", "Integer");
        TYPE_MAPPING.put("BIGINT", "Long");
        TYPE_MAPPING.put("FLOAT", "Float");
        TYPE_MAPPING.put("DOUBLE", "Double");
        TYPE_MAPPING.put("DECIMAL", "BigDecimal");
        TYPE_MAPPING.put("NUMERIC", "BigDecimal");
        
        // 日期时间类型
        TYPE_MAPPING.put("DATE", "LocalDate");
        TYPE_MAPPING.put("TIME", "LocalTime");
        TYPE_MAPPING.put("DATETIME", "LocalDateTime");
        TYPE_MAPPING.put("TIMESTAMP", "LocalDateTime");
        TYPE_MAPPING.put("YEAR", "Integer");
        
        // 二进制类型
        TYPE_MAPPING.put("BINARY", "byte[]");
        TYPE_MAPPING.put("VARBINARY", "byte[]");
        TYPE_MAPPING.put("BLOB", "byte[]");
        TYPE_MAPPING.put("LONGBLOB", "byte[]");
        TYPE_MAPPING.put("MEDIUMBLOB", "byte[]");
        TYPE_MAPPING.put("TINYBLOB", "byte[]");
        
        // 其他类型
        TYPE_MAPPING.put("BIT", "Boolean");
        TYPE_MAPPING.put("JSON", "String");
    }
    
    /**
     * 构造函数
     * 
     * @param config 数据库配置
     */
    public MySQLAdapter(DatabaseConfig config) {
        this.config = config;
    }
    
    @Override
    public Connection connect() throws GeneratorException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(config.getDriverClassName());
                connection = DriverManager.getConnection(
                        config.getUrl(),
                        config.getUsername(),
                        config.getPassword()
                );
                log.info("MySQL数据库连接成功");
            }
            return connection;
        } catch (Exception e) {
            throw new GeneratorException("连接MySQL数据库失败", e);
        }
    }
    
    @Override
    public void close() throws GeneratorException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("MySQL数据库连接已关闭");
            }
        } catch (SQLException e) {
            throw new GeneratorException("关闭MySQL数据库连接失败", e);
        }
    }
    
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }
    
    @Override
    public List<String> getTableNames(String pattern) throws GeneratorException {
        List<String> tableNames = new ArrayList<>();
        
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            // 处理通配符
            String searchPattern = pattern.replace("%", "%");
            
            try (ResultSet rs = metaData.getTables(schema, null, searchPattern, new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }
            }
            
            log.debug("获取到 {} 个表名", tableNames.size());
            
        } catch (SQLException e) {
            throw new GeneratorException("获取表名列表失败", e);
        }
        
        return tableNames;
    }
    
    @Override
    public TableMetadata getTableMetadata(String tableName) throws GeneratorException {
        try {
            TableMetadata table = new TableMetadata();
            table.setTableName(tableName);
            
            // 获取表注释
            String tableComment = getTableComment(tableName);
            table.setTableComment(tableComment);
            
            // 生成实体类名
            String entityName = convertToEntityName(tableName);
            table.setEntityName(entityName);
            
            // 获取字段信息
            List<ColumnMetadata> columns = getColumnMetadata(tableName);
            table.setColumns(columns);
            
            // 获取主键信息
            List<String> primaryKeys = getPrimaryKeys(tableName);
            table.setPrimaryKeys(primaryKeys);
            
            // 获取索引信息
            List<IndexMetadata> indexes = getIndexMetadata(tableName);
            table.setIndexes(indexes);
            
            // 获取外键信息
            List<ForeignKeyMetadata> foreignKeys = getForeignKeyMetadata(tableName);
            table.setForeignKeys(foreignKeys);
            
            // 获取表的其他信息
            fillTableExtraInfo(table);
            
            log.debug("获取表 {} 的元数据成功", tableName);
            
            return table;
            
        } catch (Exception e) {
            throw new GeneratorException("获取表 " + tableName + " 的元数据失败", e);
        }
    }
    
    @Override
    public List<ColumnMetadata> getColumnMetadata(String tableName) throws GeneratorException {
        List<ColumnMetadata> columns = new ArrayList<>();
        
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            try (ResultSet rs = metaData.getColumns(schema, null, tableName, null)) {
                while (rs.next()) {
                    ColumnMetadata column = new ColumnMetadata();
                    
                    // 基本信息
                    column.setColumnName(rs.getString("COLUMN_NAME"));
                    column.setColumnComment(rs.getString("REMARKS"));
                    column.setDataType(rs.getString("TYPE_NAME"));
                    column.setColumnSize(rs.getInt("COLUMN_SIZE"));
                    column.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    column.setDefaultValue(rs.getString("COLUMN_DEF"));
                    column.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
                    
                    // 生成属性名
                    String propertyName = convertToPropertyName(column.getColumnName());
                    column.setPropertyName(propertyName);
                    
                    // 转换Java类型
                    JavaTypeInfo typeInfo = convertToJavaType(column.getDataType(), 
                            column.getColumnSize(), column.getDecimalDigits());
                    column.setPropertyType(typeInfo.getShortName());
                    column.setJavaType(typeInfo.getFullName());
                    
                    // 检查是否自增
                    column.setAutoIncrement(isAutoIncrement(tableName, column.getColumnName()));
                    
                    columns.add(column);
                }
            }
            
            // 设置主键标记
            List<String> primaryKeys = getPrimaryKeys(tableName);
            for (ColumnMetadata column : columns) {
                column.setPrimaryKey(primaryKeys.contains(column.getColumnName()));
            }
            
            log.debug("获取表 {} 的字段信息成功，共 {} 个字段", tableName, columns.size());
            
        } catch (SQLException e) {
            throw new GeneratorException("获取表 " + tableName + " 的字段信息失败", e);
        }
        
        return columns;
    }
    
    @Override
    public List<IndexMetadata> getIndexMetadata(String tableName) throws GeneratorException {
        Map<String, IndexMetadata> indexMap = new HashMap<>();
        
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            try (ResultSet rs = metaData.getIndexInfo(schema, null, tableName, false, true)) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    
                    // 跳过主键索引
                    if ("PRIMARY".equals(indexName)) {
                        continue;
                    }
                    
                    IndexMetadata index = indexMap.computeIfAbsent(indexName, k -> {
                        IndexMetadata idx = new IndexMetadata();
                        idx.setIndexName(indexName);
                        idx.setUnique(!rs.getBoolean("NON_UNIQUE"));
                        idx.setIndexType(rs.getString("TYPE"));
                        idx.setColumnNames(new ArrayList<>());
                        return idx;
                    });
                    
                    String columnName = rs.getString("COLUMN_NAME");
                    if (columnName != null) {
                        index.getColumnNames().add(columnName);
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new GeneratorException("获取表 " + tableName + " 的索引信息失败", e);
        }
        
        return new ArrayList<>(indexMap.values());
    }
    
    @Override
    public List<ForeignKeyMetadata> getForeignKeyMetadata(String tableName) throws GeneratorException {
        List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
        
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            try (ResultSet rs = metaData.getImportedKeys(schema, null, tableName)) {
                while (rs.next()) {
                    ForeignKeyMetadata fk = new ForeignKeyMetadata();
                    fk.setForeignKeyName(rs.getString("FK_NAME"));
                    fk.setColumnName(rs.getString("FKCOLUMN_NAME"));
                    fk.setReferencedTableName(rs.getString("PKTABLE_NAME"));
                    fk.setReferencedColumnName(rs.getString("PKCOLUMN_NAME"));
                    fk.setDeleteRule(getForeignKeyRule(rs.getShort("DELETE_RULE")));
                    fk.setUpdateRule(getForeignKeyRule(rs.getShort("UPDATE_RULE")));
                    
                    foreignKeys.add(fk);
                }
            }
            
        } catch (SQLException e) {
            throw new GeneratorException("获取表 " + tableName + " 的外键信息失败", e);
        }
        
        return foreignKeys;
    }
    
    @Override
    public Map<String, String> getTypeMapping() {
        return new HashMap<>(TYPE_MAPPING);
    }
    
    @Override
    public JavaTypeInfo convertToJavaType(String dbType, Integer precision, Integer scale) {
        String upperType = dbType.toUpperCase();
        
        // 特殊处理
        if ("TINYINT".equals(upperType) && precision != null && precision == 1) {
            return new JavaTypeInfo("Boolean", "java.lang.Boolean");
        }
        
        if ("DECIMAL".equals(upperType) || "NUMERIC".equals(upperType)) {
            return new JavaTypeInfo("BigDecimal", "java.math.BigDecimal");
        }
        
        if ("DATE".equals(upperType)) {
            return new JavaTypeInfo("LocalDate", "java.time.LocalDate");
        }
        
        if ("TIME".equals(upperType)) {
            return new JavaTypeInfo("LocalTime", "java.time.LocalTime");
        }
        
        if ("DATETIME".equals(upperType) || "TIMESTAMP".equals(upperType)) {
            return new JavaTypeInfo("LocalDateTime", "java.time.LocalDateTime");
        }
        
        // 默认映射
        String javaType = TYPE_MAPPING.get(upperType);
        if (javaType == null) {
            javaType = "String";
        }
        
        // 生成完整类名
        String fullName = getFullJavaTypeName(javaType);
        
        return new JavaTypeInfo(javaType, fullName);
    }
    
    @Override
    public List<String> getPrimaryKeys(String tableName) throws GeneratorException {
        List<String> primaryKeys = new ArrayList<>();
        
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            try (ResultSet rs = metaData.getPrimaryKeys(schema, null, tableName)) {
                while (rs.next()) {
                    primaryKeys.add(rs.getString("COLUMN_NAME"));
                }
            }
            
        } catch (SQLException e) {
            throw new GeneratorException("获取表 " + tableName + " 的主键信息失败", e);
        }
        
        return primaryKeys;
    }
    
    @Override
    public boolean tableExists(String tableName) throws GeneratorException {
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            String schema = config.getSchema();
            
            try (ResultSet rs = metaData.getTables(schema, null, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new GeneratorException("检查表 " + tableName + " 是否存在失败", e);
        }
    }
    
    @Override
    public String getDatabaseVersion() throws GeneratorException {
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            return metaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            throw new GeneratorException("获取数据库版本失败", e);
        }
    }
    
    @Override
    public String getDatabaseProductName() throws GeneratorException {
        try {
            DatabaseMetaData metaData = connect().getMetaData();
            return metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new GeneratorException("获取数据库产品名称失败", e);
        }
    }
    
    /**
     * 获取表注释
     * 
     * @param tableName 表名
     * @return 表注释
     * @throws SQLException SQL异常
     */
    private String getTableComment(String tableName) throws SQLException {
        String sql = "SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setString(1, config.getSchema());
            ps.setString(2, tableName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TABLE_COMMENT");
                }
            }
        }
        
        return null;
    }
    
    /**
     * 填充表的额外信息
     * 
     * @param table 表元数据
     * @throws SQLException SQL异常
     */
    private void fillTableExtraInfo(TableMetadata table) throws SQLException {
        String sql = "SELECT ENGINE, TABLE_COLLATION, CREATE_TIME, UPDATE_TIME " +
                    "FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setString(1, config.getSchema());
            ps.setString(2, table.getTableName());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    table.setEngine(rs.getString("ENGINE"));
                    table.setCollation(rs.getString("TABLE_COLLATION"));
                    table.setCreateTime(rs.getTimestamp("CREATE_TIME"));
                    table.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                }
            }
        }
    }
    
    /**
     * 检查字段是否自增
     * 
     * @param tableName 表名
     * @param columnName 字段名
     * @return 是否自增
     * @throws SQLException SQL异常
     */
    private boolean isAutoIncrement(String tableName, String columnName) throws SQLException {
        String sql = "SELECT EXTRA FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setString(1, config.getSchema());
            ps.setString(2, tableName);
            ps.setString(3, columnName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String extra = rs.getString("EXTRA");
                    return extra != null && extra.toLowerCase().contains("auto_increment");
                }
            }
        }
        
        return false;
    }
    
    /**
     * 转换为实体类名
     * 
     * @param tableName 表名
     * @return 实体类名
     */
    private String convertToEntityName(String tableName) {
        // 移除表前缀
        String prefix = config.getTablePrefix();
        if (prefix != null && tableName.startsWith(prefix)) {
            tableName = tableName.substring(prefix.length());
        }
        
        return toPascalCase(tableName);
    }
    
    /**
     * 转换为属性名
     * 
     * @param columnName 字段名
     * @return 属性名
     */
    private String convertToPropertyName(String columnName) {
        return toCamelCase(columnName);
    }
    
    /**
     * 转换为帕斯卡命名法
     * 
     * @param str 字符串
     * @return 帕斯卡命名法字符串
     */
    private String toPascalCase(String str) {
        return toCamelCase(str, true);
    }
    
    /**
     * 转换为驼峰命名法
     * 
     * @param str 字符串
     * @return 驼峰命名法字符串
     */
    private String toCamelCase(String str) {
        return toCamelCase(str, false);
    }
    
    /**
     * 转换为驼峰命名法
     * 
     * @param str 字符串
     * @param upperFirst 首字母是否大写
     * @return 驼峰命名法字符串
     */
    private String toCamelCase(String str, boolean upperFirst) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpper = upperFirst;
        
        for (char c : str.toCharArray()) {
            if (c == '_' || c == '-') {
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
    
    /**
     * 获取完整的Java类型名
     * 
     * @param shortName 短类型名
     * @return 完整类型名
     */
    private String getFullJavaTypeName(String shortName) {
        switch (shortName) {
            case "BigDecimal":
                return "java.math.BigDecimal";
            case "LocalDate":
                return "java.time.LocalDate";
            case "LocalTime":
                return "java.time.LocalTime";
            case "LocalDateTime":
                return "java.time.LocalDateTime";
            case "Date":
                return "java.util.Date";
            default:
                return "java.lang." + shortName;
        }
    }
    
    /**
     * 获取外键规则描述
     * 
     * @param rule 规则代码
     * @return 规则描述
     */
    private String getForeignKeyRule(short rule) {
        switch (rule) {
            case DatabaseMetaData.importedKeyCascade:
                return "CASCADE";
            case DatabaseMetaData.importedKeySetNull:
                return "SET NULL";
            case DatabaseMetaData.importedKeyRestrict:
                return "RESTRICT";
            case DatabaseMetaData.importedKeyNoAction:
                return "NO ACTION";
            default:
                return "UNKNOWN";
        }
    }
}