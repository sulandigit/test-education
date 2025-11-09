package com.roncoo.generator.exception;

/**
 * 代码生成器基础异常类
 * 
 * @author roncoo-generator
 */
public class GeneratorException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    /**
     * 错误上下文信息
     */
    private Object context;
    
    public GeneratorException(String message) {
        super(message);
    }
    
    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GeneratorException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public GeneratorException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public GeneratorException(String errorCode, String message, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public GeneratorException(String errorCode, String message, Throwable cause, Object context) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getContext() {
        return context;
    }
    
    /**
     * 获取详细的错误信息
     * 
     * @return 详细错误信息
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }
        
        sb.append(getMessage());
        
        if (context != null) {
            sb.append(" (上下文: ").append(context).append(")");
        }
        
        return sb.toString();
    }
}