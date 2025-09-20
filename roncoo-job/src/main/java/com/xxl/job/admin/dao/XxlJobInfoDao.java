package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Job Information Data Access Object
 * 
 * This DAO interface provides data access operations for job information management.
 * It handles CRUD operations and specialized queries for the XXL-Job scheduling system.
 * 
 * Features:
 * - Paginated job listing with multiple filter criteria
 * - Basic CRUD operations (create, read, update, delete)
 * - Schedule-related queries for job timing management
 * - Group-based job queries
 * - Statistical count operations
 * 
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

	/**
	 * Get paginated job list with filtering criteria
	 * 
	 * @param offset pagination offset
	 * @param pagesize number of records per page
	 * @param jobGroup job group filter
	 * @param triggerStatus trigger status filter
	 * @param jobDesc job description filter
	 * @param executorHandler executor handler filter
	 * @param author job author filter
	 * @return list of job information matching criteria
	 */
	public List<XxlJobInfo> pageList(@Param("offset") int offset,
									 @Param("pagesize") int pagesize,
									 @Param("jobGroup") int jobGroup,
									 @Param("triggerStatus") int triggerStatus,
									 @Param("jobDesc") String jobDesc,
									 @Param("executorHandler") String executorHandler,
									 @Param("author") String author);
									 
	/**
	 * Get total count of jobs matching the filtering criteria
	 * 
	 * @param offset pagination offset
	 * @param pagesize number of records per page
	 * @param jobGroup job group filter
	 * @param triggerStatus trigger status filter
	 * @param jobDesc job description filter
	 * @param executorHandler executor handler filter
	 * @param author job author filter
	 * @return total count of matching jobs
	 */
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("jobGroup") int jobGroup,
							 @Param("triggerStatus") int triggerStatus,
							 @Param("jobDesc") String jobDesc,
							 @Param("executorHandler") String executorHandler,
							 @Param("author") String author);
	
	/**
	 * Save a new job information record
	 * 
	 * @param info job information to be saved
	 * @return number of affected rows
	 */
	public int save(XxlJobInfo info);

	/**
	 * Load job information by ID
	 * 
	 * @param id job ID
	 * @return job information or null if not found
	 */
	public XxlJobInfo loadById(@Param("id") int id);
	
	/**
	 * Update an existing job information record
	 * 
	 * @param xxlJobInfo job information to be updated
	 * @return number of affected rows
	 */
	public int update(XxlJobInfo xxlJobInfo);
	
	/**
	 * Delete a job information record
	 * 
	 * @param id job ID to be deleted
	 * @return number of affected rows
	 */
	public int delete(@Param("id") long id);

	/**
	 * Get all jobs belonging to a specific job group
	 * 
	 * @param jobGroup job group ID
	 * @return list of jobs in the specified group
	 */
	public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

	/**
	 * Get total count of all jobs in the system
	 * 
	 * @return total job count
	 */
	public int findAllCount();

	/**
	 * Query jobs that are ready for scheduling based on next trigger time
	 * 
	 * @param maxNextTime maximum next trigger time threshold
	 * @param pagesize maximum number of jobs to return
	 * @return list of jobs ready for scheduling
	 */
	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize );

	/**
	 * Update job scheduling information (trigger times, etc.)
	 * 
	 * @param xxlJobInfo job information with updated scheduling data
	 * @return number of affected rows
	 */
	public int scheduleUpdate(XxlJobInfo xxlJobInfo);


}
