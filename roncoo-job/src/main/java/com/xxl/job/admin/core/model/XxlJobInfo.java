package com.xxl.job.admin.core.model;

import java.util.Date;

/**
 * XXL-Job Information Model
 * 
 * This class represents a scheduled job configuration with all its properties
 * including scheduling settings, execution parameters, and status information.
 * 
 * Main components:
 * - Basic job information (ID, description, author)
 * - Scheduling configuration (type, CRON expression, misfire strategy)
 * - Executor settings (route strategy, handler, parameters, timeout)
 * - Glue code configuration for dynamic job scripts
 * - Trigger status and timing information
 * 
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {
	
	/**
	 * Primary key ID
	 */
	private int id;
	
	/**
	 * Executor group ID
	 */
	private int jobGroup;
	
	/**
	 * Job description
	 */
	private String jobDesc;
	
	/**
	 * Job creation time
	 */
	private Date addTime;
	
	/**
	 * Job last update time
	 */
	private Date updateTime;
	
	/**
	 * Job author/owner
	 */
	private String author;
	
	/**
	 * Alarm email addresses
	 */
	private String alarmEmail;

	/**
	 * Schedule type (CRON, FIX_RATE, etc.)
	 */
	private String scheduleType;
	
	/**
	 * Schedule configuration (CRON expression or interval)
	 */
	private String scheduleConf;
	
	/**
	 * Misfire handling strategy
	 */
	private String misfireStrategy;

	/**
	 * Executor routing strategy
	 */
	private String executorRouteStrategy;
	
	/**
	 * Executor handler name
	 */
	private String executorHandler;
	
	/**
	 * Executor parameters
	 */
	private String executorParam;
	
	/**
	 * Executor blocking strategy
	 */
	private String executorBlockStrategy;
	
	/**
	 * Execution timeout in seconds
	 */
	private int executorTimeout;
	
	/**
	 * Retry count on failure
	 */
	private int executorFailRetryCount;
	
	/**
	 * Glue type for dynamic scripts
	 */
	private String glueType;
	
	/**
	 * Glue source code
	 */
	private String glueSource;
	
	/**
	 * Glue remarks/comments
	 */
	private String glueRemark;
	
	/**
	 * Glue last update time
	 */
	private Date glueUpdatetime;

	/**
	 * Child job IDs (comma-separated)
	 */
	private String childJobId;

	/**
	 * Trigger status: 0=stopped, 1=running
	 */
	private int triggerStatus;
	
	/**
	 * Last trigger time
	 */
	private long triggerLastTime;
	
	/**
	 * Next trigger time
	 */
	private long triggerNextTime;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlarmEmail() {
		return alarmEmail;
	}

	public void setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getScheduleConf() {
		return scheduleConf;
	}

	public void setScheduleConf(String scheduleConf) {
		this.scheduleConf = scheduleConf;
	}

	public String getMisfireStrategy() {
		return misfireStrategy;
	}

	public void setMisfireStrategy(String misfireStrategy) {
		this.misfireStrategy = misfireStrategy;
	}

	public String getExecutorRouteStrategy() {
		return executorRouteStrategy;
	}

	public void setExecutorRouteStrategy(String executorRouteStrategy) {
		this.executorRouteStrategy = executorRouteStrategy;
	}

	public String getExecutorHandler() {
		return executorHandler;
	}

	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}

	public String getExecutorParam() {
		return executorParam;
	}

	public void setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
	}

	public String getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}

	public void setExecutorBlockStrategy(String executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
	}

	public int getExecutorTimeout() {
		return executorTimeout;
	}

	public void setExecutorTimeout(int executorTimeout) {
		this.executorTimeout = executorTimeout;
	}

	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}

	public void setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
	}

	public String getGlueType() {
		return glueType;
	}

	public void setGlueType(String glueType) {
		this.glueType = glueType;
	}

	public String getGlueSource() {
		return glueSource;
	}

	public void setGlueSource(String glueSource) {
		this.glueSource = glueSource;
	}

	public String getGlueRemark() {
		return glueRemark;
	}

	public void setGlueRemark(String glueRemark) {
		this.glueRemark = glueRemark;
	}

	public Date getGlueUpdatetime() {
		return glueUpdatetime;
	}

	public void setGlueUpdatetime(Date glueUpdatetime) {
		this.glueUpdatetime = glueUpdatetime;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public int getTriggerStatus() {
		return triggerStatus;
	}

	public void setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
	}

	public long getTriggerLastTime() {
		return triggerLastTime;
	}

	public void setTriggerLastTime(long triggerLastTime) {
		this.triggerLastTime = triggerLastTime;
	}

	public long getTriggerNextTime() {
		return triggerNextTime;
	}

	public void setTriggerNextTime(long triggerNextTime) {
		this.triggerNextTime = triggerNextTime;
	}
}
