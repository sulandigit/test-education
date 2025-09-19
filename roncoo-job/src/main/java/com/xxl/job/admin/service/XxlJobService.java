package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.Date;
import java.util.Map;

/**
 * XXL-Job Service Interface - Core Job Operations
 * 
 * This service interface defines all the core business operations for
 * managing jobs in the XXL-Job scheduling system.
 * 
 * Main functionalities:
 * - Job lifecycle management (create, update, delete, start, stop)
 * - Job listing with pagination and filtering
 * - Dashboard statistics and reporting
 * - Chart data generation for monitoring
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	/**
	 * Get paginated job list with filtering
	 *
	 * @param start pagination start index
	 * @param length number of records per page
	 * @param jobGroup job group filter
	 * @param triggerStatus trigger status filter
	 * @param jobDesc job description filter
	 * @param executorHandler executor handler filter
	 * @param author job author filter
	 * @return paginated job list data
	 */
	public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

	/**
	 * Add a new job to the system
	 *
	 * @param jobInfo job configuration information
	 * @return operation result with success/failure status
	 */
	public ReturnT<String> add(XxlJobInfo jobInfo);

	/**
	 * Update an existing job configuration
	 *
	 * @param jobInfo updated job information
	 * @return operation result with success/failure status
	 */
	public ReturnT<String> update(XxlJobInfo jobInfo);

	/**
	 * Remove a job from the system
	 * 
	 * This will stop the job if it's running and remove it from the database.
	 *
	 * @param id job ID to be removed
	 * @return operation result with success/failure status
	 */
	public ReturnT<String> remove(int id);

	/**
	 * Start a stopped job
	 *
	 * @param id job ID to be started
	 * @return operation result with success/failure status
	 */
	public ReturnT<String> start(int id);

	/**
	 * Stop a running job
	 *
	 * @param id job ID to be stopped
	 * @return operation result with success/failure status
	 */
	public ReturnT<String> stop(int id);

	/**
	 * Get dashboard statistics information
	 * 
	 * Provides summary statistics for the dashboard including job counts,
	 * execution statistics, and other key metrics.
	 *
	 * @return dashboard data map
	 */
	public Map<String,Object> dashboardInfo();

	/**
	 * Get chart data for job execution reports
	 * 
	 * Generates chart data for the specified date range to display
	 * job execution trends and statistics.
	 *
	 * @param startDate chart data start date
	 * @param endDate chart data end date
	 * @return chart data in the format expected by front-end charting library
	 */
	public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);

}
