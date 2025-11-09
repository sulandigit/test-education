package com.roncoo.generator.exception;

/**
 * 配置异常类
 * 
 * @author roncoo-generator
 */
public class ConfigurationException extends GeneratorException {
    
    private static final long serialVersionUID = 1L;
    
    public ConfigurationException(String message) {
        super("CONFIG_ERROR", message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super("CONFIG_ERROR", message, cause);
    }
    
    public ConfigurationException(String message, Object context) {
        super("CONFIG_ERROR", message, context);
    }
    
    public ConfigurationException(String message, Throwable cause, Object context) {
        super("CONFIG_ERROR", message, cause, context);
    }
}