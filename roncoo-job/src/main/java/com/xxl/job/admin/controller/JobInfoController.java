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
 * Job Information Controller - Job Management Operations
 * 
 * This controller handles all job-related operations including creation,
 * modification, deletion, scheduling, and monitoring of jobs.
 * 
 * Features:
 * - Job list display with pagination and filtering
 * - Job creation and configuration
 * - Job scheduling and trigger management
 * - Job status control (start/stop)
 * - Permission validation for job operations
 * - Next execution time calculation
 * 
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	/**
	 * Logger instance for this controller
	 */
	private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	/**
	 * Data access object for job groups
	 */
	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	
	/**
	 * Service for job-related business operations
	 */
	@Resource
	private XxlJobService xxlJobService;
	
	/**
	 * Display job information index page with enums and job groups
	 * 
	 * @param request HTTP servlet request
	 * @param model Spring model for view data
	 * @param jobGroup selected job group ID (default: -1)
	 * @return view name for job info index page
	 */
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// Add enum dictionaries to model
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // Route strategy list
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue type dictionary
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // Block strategy dictionary
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// Schedule type
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// Misfire strategy

		// Get all executor groups
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

		// Filter groups by user role
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
	 * @param request HTTP servlet request containing login user
	 * @param jobGroupList_all complete list of job groups
	 * @return filtered job groups based on user permissions
	 */
	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all){
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupList_all!=null && jobGroupList_all.size()>0) {
			XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
			if (loginUser.getRole() == 1) {
				// Admin user - access to all groups
				jobGroupList = jobGroupList_all;
			} else {
				// Regular user - filter by permissions
				List<String> groupIdStrs = new ArrayList<>();
				if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
					groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
				}
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
	 * Validate user permission for a specific job group
	 * 
	 * @param request HTTP servlet request containing login user
	 * @param jobGroup job group ID to validate permission for
	 * @throws RuntimeException if user doesn't have permission
	 */
	public static void validPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
		}
	}
	
	/**
	 * Get paginated job list with filtering options
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
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
		
		return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
	}
	
	/**
	 * Add a new job
	 * 
	 * @param jobInfo job information to be added
	 * @return operation result
	 */
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}
	
	/**
	 * Update an existing job
	 * 
	 * @param jobInfo job information to be updated
	 * @return operation result
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}
	
	/**
	 * Remove a job
	 * 
	 * @param id job ID to be removed
	 * @return operation result
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}
	
	/**
	 * Stop a running job
	 * 
	 * @param id job ID to be stopped
	 * @return operation result
	 */
	@RequestMapping("/stop")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}
	
	/**
	 * Start a stopped job
	 * 
	 * @param id job ID to be started
	 * @return operation result
	 */
	@RequestMapping("/start")
	@ResponseBody
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}
	
	/**
	 * Manually trigger a job execution
	 * 
	 * @param id job ID to be triggered
	 * @param executorParam execution parameters
	 * @param addressList specific executor address list
	 * @return trigger result
	 */
	@RequestMapping("/trigger")
	@ResponseBody
	//@PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
		// Force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}

	/**
	 * Calculate next trigger times for a given schedule configuration
	 * 
	 * @param scheduleType schedule type (CRON, FIX_RATE, etc.)
	 * @param scheduleConf schedule configuration
	 * @return list of next 5 trigger times
	 */
	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		List<String> result = new ArrayList<>();
		try {
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
		}
		return new ReturnT<List<String>>(result);

	}
	
}
