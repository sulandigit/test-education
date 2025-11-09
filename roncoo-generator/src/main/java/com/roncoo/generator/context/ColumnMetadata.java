package com.roncoo.generator.context;

import lombok.Data;

import java.util.Map;

/**
 * 字段元数据
 * 
 * @author roncoo-generator
 */
@Data
public class ColumnMetadata {
    
    /**
     * 字段名
     */
    private String columnName;
    
    /**
     * 字段注释
     */
    private String columnComment;
    
    /**
     * 属性名（Java中的字段名）
     */
    private String propertyName;
    
    /**
     * 数据库字段类型
     */
    private String columnType;
    
    /**
     * 数据库字段类型（不含长度）
     */
    private String dataType;
    
    /**
     * Java字段类型
     */
    private String propertyType;
    
    /**
     * Java字段类型（完整包名）
     */
    private String javaType;
    
    /**
     * 字段长度
     */
    private Integer columnSize;
    
    /**
     * 小数位数
     */
    private Integer decimalDigits;
    
    /**
     * 是否允许为空
     */
    private Boolean nullable = true;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 是否为主键
     */
    private Boolean primaryKey = false;
    
    /**
     * 是否自增
     */
    private Boolean autoIncrement = false;
    
    /**
     * 字段在表中的位置
     */
    private Integer ordinalPosition;
    
    /**
     * 字段精度
     */
    private Integer precision;
    
    /**
     * 字段标度
     */
    private Integer scale;
    
    /**
     * 字符集
     */
    private String characterSet;
    
    /**
     * 排序规则
     */
    private String collation;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
    
    /**
     * 是否为日期时间类型
     * 
     * @return 是否为日期时间类型
     */
    public boolean isDateTimeType() {
        if (javaType == null) {
            return false;
        }
        
        return javaType.equals("java.util.Date") ||
               javaType.equals("java.time.LocalDate") ||
               javaType.equals("java.time.LocalDateTime") ||
               javaType.equals("java.time.LocalTime") ||
               javaType.equals("java.sql.Date") ||
               javaType.equals("java.sql.Time") ||
               javaType.equals("java.sql.Timestamp");
    }
    
    /**
     * 是否为数值类型
     * 
     * @return 是否为数值类型
     */
    public boolean isNumericType() {
        if (propertyType == null) {
            return false;
        }
        
        return propertyType.equals("Integer") ||
               propertyType.equals("Long") ||
               propertyType.equals("Double") ||
               propertyType.equals("Float") ||
               propertyType.equals("BigDecimal") ||
               propertyType.equals("Short") ||
               propertyType.equals("Byte");
    }
    
    /**
     * 是否为字符串类型
     * 
     * @return 是否为字符串类型
     */
    public boolean isStringType() {
        return "String".equals(propertyType);
    }
    
    /**
     * 是否为布尔类型
     * 
     * @return 是否为布尔类型
     */
    public boolean isBooleanType() {
        return "Boolean".equals(propertyType) || "boolean".equals(propertyType);
    }
    
    /**
     * 是否需要导入包
     * 
     * @return 是否需要导入包
     */
    public boolean needsImport() {
        return javaType != null && javaType.contains(".");
    }
    
    /**
     * 获取字段的getter方法名
     * 
     * @return getter方法名
     */
    public String getGetterMethodName() {
        if (propertyName == null || propertyName.isEmpty()) {
            return null;
        }
        
        String prefix = isBooleanType() ? "is" : "get";
        return prefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
    
    /**
     * 获取字段的setter方法名
     * 
     * @return setter方法名
     */
    public String getSetterMethodName() {
        if (propertyName == null || propertyName.isEmpty()) {
            return null;
        }
        
        return "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
    
    /**
     * 获取字段的校验注解
     * 
     * @return 校验注解列表
     */
    public java.util.List<String> getValidationAnnotations() {
        java.util.List<String> annotations = new java.util.ArrayList<>();
        
        // 非空校验
        if (!nullable) {
            if (isStringType()) {
                annotations.add("@NotBlank(message = \"" + columnComment + "不能为空\")");
            } else {
                annotations.add("@NotNull(message = \"" + columnComment + "不能为空\")");
            }
        }
        
        // 长度校验
        if (isStringType() && columnSize != null) {
            annotations.add("@Size(max = " + columnSize + ", message = \"" + columnComment + "长度不能超过" + columnSize + "个字符\")");
        }
        
        // 数值范围校验
        if (isNumericType() && precision != null) {
            if (scale != null && scale > 0) {
                annotations.add("@Digits(integer = " + (precision - scale) + ", fraction = " + scale + 
                              ", message = \"" + columnComment + "格式不正确\")");
            }
        }
        
        return annotations;
    }
}