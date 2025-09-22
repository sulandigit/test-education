package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Job Information Controller - Core job management functionality
 * 
 * This controller handles all job-related operations including job creation, modification,
 * scheduling, and execution management. It provides comprehensive job lifecycle management
 * and serves as the primary interface for:
 * - Job CRUD operations (Create, Read, Update, Delete)
 * - Job scheduling and trigger management
 * - Job status control (start, stop, pause)
 * - Job execution parameter configuration
 * - Permission-based job group filtering
 * - Schedule time calculation and validation
 *
 * @author xuxueli 2015-12-19 16:13:16
 * @since 1.0.0
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobService xxlJobService;
	
	/**
	 * Display the job management index page
	 * 
	 * Loads the main job management interface with job groups, routing strategies,
	 * scheduling types, and other configuration options. Filters job groups based
	 * on user permissions.
	 *
	 * @param request HTTP servlet request for user context
	 * @param model Spring MVC model for view data
	 * @param jobGroup selected job group ID (default: -1 for all groups)
	 * @return view name for job management page
	 * @throws XxlJobException if no accessible job groups found
	 */
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// Add enumeration dictionaries for UI dropdowns
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // Routing strategy options
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue type dictionary
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // Block handling strategy dictionary
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// Schedule type options
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// Misfire strategy options

		// Load all available job groups
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

		// Filter job groups based on user permissions
		List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList==null || jobGroupList.size()==0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	/**
	 * Filter job groups based on user role and permissions
	 * 
	 * Filters the list of job groups according to the current user's role and permissions.
	 * Admin users (role=1) can access all job groups, while regular users can only access
	 * job groups specified in their permission configuration.
	 *
	 * @param request HTTP servlet request containing user session information
	 * @param jobGroupList_all complete list of all job groups
	 * @return filtered list of job groups accessible to the current user
	 */
	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all){
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupList_all!=null && jobGroupList_all.size()>0) {
			// Get current logged-in user
			XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
			if (loginUser.getRole() == 1) {
				// Admin users have access to all job groups
				jobGroupList = jobGroupList_all;
			} else {
				// Regular users: filter by permission configuration
				List<String> groupIdStrs = new ArrayList<>();
				if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
					// Parse comma-separated group IDs from user permissions
					groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
				}
				// Add only permitted job groups
				for (XxlJobGroup groupItem:jobGroupList_all) {
					if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
						jobGroupList.add(groupItem);
					}
				}
			}
		}
		return jobGroupList;
	}
	/**
	 * Validate user permission for accessing a specific job group
	 * 
	 * Checks if the current user has permission to access operations on the specified job group.
	 * Throws a runtime exception if the user lacks the required permissions.
	 *
	 * @param request HTTP servlet request containing user session information
	 * @param jobGroup job group ID to validate access for
	 * @throws RuntimeException if user lacks permission for the job group
	 */
	public static void validPermission(HttpServletRequest request, int jobGroup) {
		// Get current logged-in user and validate permissions
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
		}
	}
	
	/**
	 * Get paginated list of jobs with filtering
	 * 
	 * Retrieves a paginated list of jobs with support for filtering by job group,
	 * trigger status, description, executor handler, and author. Used for displaying
	 * jobs in the management interface with search and pagination capabilities.
	 *
	 * @param start pagination start index (default: 0)
	 * @param length page size (default: 10)
	 * @param jobGroup job group filter
	 * @param triggerStatus trigger status filter
	 * @param jobDesc job description filter (partial match)
	 * @param executorHandler executor handler filter (partial match)
	 * @param author job author filter (partial match)
	 * @return map containing job list data and pagination information
	 */
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
		
		// Delegate to service layer for paginated job retrieval
		return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
	}
	
	/**
	 * Add a new job
	 * 
	 * Creates a new job configuration with the provided job information.
	 * Validates job parameters and creates the job in the scheduling system.
	 *
	 * @param jobInfo job information object containing all job configuration details
	 * @return ReturnT indicating success or failure with error message
	 */
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		// Delegate to service layer for job creation
		return xxlJobService.add(jobInfo);
	}
	
	/**
	 * Update an existing job
	 * 
	 * Modifies an existing job configuration with the provided job information.
	 * Validates changes and updates the job in the scheduling system.
	 *
	 * @param jobInfo job information object containing updated job configuration
	 * @return ReturnT indicating success or failure with error message
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		// Delegate to service layer for job update
		return xxlJobService.update(jobInfo);
	}
	
	/**
	 * Remove (delete) a job
	 * 
	 * Permanently deletes a job from the system. This will stop the job
	 * if it's currently running and remove all associated configuration.
	 *
	 * @param id job ID to be removed
	 * @return ReturnT indicating success or failure with error message
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id) {
		// Delegate to service layer for job removal
		return xxlJobService.remove(id);
	}
	
	/**
	 * Stop (pause) a job
	 * 
	 * Stops a currently running or scheduled job. The job configuration remains
	 * in the system but will not be triggered until manually started again.
	 *
	 * @param id job ID to be stopped
	 * @return ReturnT indicating success or failure with error message
	 */
	@RequestMapping("/stop")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		// Delegate to service layer for job stopping
		return xxlJobService.stop(id);
	}
	
	/**
	 * Start a job
	 * 
	 * Starts a previously stopped job or activates a newly created job.
	 * The job will begin executing according to its configured schedule.
	 *
	 * @param id job ID to be started
	 * @return ReturnT indicating success or failure with error message
	 */
	@RequestMapping("/start")
	@ResponseBody
	public ReturnT<String> start(int id) {
		// Delegate to service layer for job starting
		return xxlJobService.start(id);
	}
	
	/**
	 * Manually trigger a job execution
	 * 
	 * Immediately triggers a job execution outside of its normal schedule.
	 * Allows for manual job execution with custom parameters and target executor addresses.
	 *
	 * @param id job ID to trigger
	 * @param executorParam custom execution parameters (optional)
	 * @param addressList specific executor addresses to run on (optional)
	 * @return ReturnT indicating trigger success
	 */
	@RequestMapping("/trigger")
	@ResponseBody
	//@PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
		// Ensure executor parameter is not null
		if (executorParam == null) {
			executorParam = "";
		}

		// Trigger job execution manually
		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}

	/**
	 * Calculate next trigger times for a job schedule
	 * 
	 * Calculates and returns the next 5 trigger times based on the provided
	 * schedule configuration. Used for schedule validation and preview.
	 *
	 * @param scheduleType type of schedule (CRON, FIX_RATE, etc.)
	 * @param scheduleConf schedule configuration (cron expression or interval)
	 * @return ReturnT containing list of next 5 trigger times as formatted strings
	 */
	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

		// Create temporary job info for schedule calculation
		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		List<String> result = new ArrayList<>();
		try {
			// Calculate next 5 trigger times
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
				if (lastTime != null) {
					// Format and add to result list
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					// No more valid trigger times
					break;
				}
			}
		} catch (Exception e) {
			// Handle schedule configuration errors
			logger.error(e.getMessage(), e);
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
		}
		return new ReturnT<List<String>>(result);

	}
	
}
