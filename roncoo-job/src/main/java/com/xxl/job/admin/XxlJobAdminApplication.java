package com.xxl.job.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * XXL-Job Admin Application - Main Entry Point
 * 
 * This is the main application class for the XXL-Job distributed task scheduling system.
 * It provides a web-based administration console for managing job scheduling, monitoring,
 * and configuration.
 * 
 * Key features:
 * - Job scheduling and management
 * - Executor group management
 * - Job execution monitoring and logging
 * - User authentication and authorization
 * - RESTful API for job operations
 * 
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobAdminApplication {

	/**
	 * Main method to start the XXL-Job Admin application
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
	}

}