package com.xxl.job.admin.core.exception;

/**
 * XXL-Job Custom Exception
 * 
 * This is the main exception class for the XXL-Job scheduling system.
 * It extends RuntimeException to provide specific error handling for
 * job-related operations and system failures.
 * 
 * Used for:
 * - Job configuration validation errors
 * - Scheduling operation failures
 * - System state inconsistencies
 * - Business logic violations
 * 
 * @author xuxueli 2019-05-04 23:19:29
 */
public class XxlJobException extends RuntimeException {

    /**
     * Default constructor for XxlJobException
     */
    public XxlJobException() {
    }
    
    /**
     * Constructor with error message
     * 
     * @param message descriptive error message
     */
    public XxlJobException(String message) {
        super(message);
    }

}
