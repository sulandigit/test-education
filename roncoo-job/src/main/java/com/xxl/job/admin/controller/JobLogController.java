package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Job Log Controller - Job execution log management
 * 
 * This controller handles all job execution log operations including log viewing,
 * filtering, searching, and management. It provides comprehensive job log functionality
 * and serves as the primary interface for:
 * - Job execution log display and pagination
 * - Log detail viewing and real-time log streaming
 * - Job execution termination (kill operations)
 * - Log cleanup and maintenance operations
 * - Job group filtering and permission validation
 * - Log status monitoring and analysis
 *
 * @author xuxueli 2015-12-19 16:13:16
 * @since 1.0.0
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {
	private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;

	/**
	 * Display the job log management index page
	 * 
	 * Loads the main job log management interface with job groups and job information.
	 * Filters job groups based on user permissions and validates access to specific jobs.
	 *
	 * @param request HTTP servlet request for user context
	 * @param model Spring MVC model for view data
	 * @param jobId specific job ID to filter logs (default: 0 for all jobs)
	 * @return view name for job log management page
	 * @throws XxlJobException if no accessible job groups found
	 * @throws RuntimeException if specified job ID is invalid
	 */
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

		// Load all available job groups
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

		// Filter job groups based on user permissions
		List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList==null || jobGroupList.size()==0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);

		// Load specific job information if jobId is provided
		if (jobId > 0) {
			XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
			if (jobInfo == null) {
				throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
			}

			model.addAttribute("jobInfo", jobInfo);

			// Validate user permission for the job group
			JobInfoController.validPermission(request, jobInfo.getJobGroup());
		}

		return "joblog/joblog.index";
	}

	/**
	 * Get jobs by job group
	 * 
	 * Retrieves all jobs belonging to a specific job group.
	 * Used for populating job selection dropdowns in the UI.
	 *
	 * @param jobGroup job group ID to get jobs for
	 * @return ReturnT containing list of jobs in the specified group
	 */
	@RequestMapping("/getJobsByGroup")
	@ResponseBody
	public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup){
		// Retrieve jobs for the specified group
		List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
		return new ReturnT<List<XxlJobInfo>>(list);
	}
	
	/**
	 * Get paginated list of job execution logs with filtering
	 * 
	 * Retrieves a paginated list of job execution logs with support for filtering by
	 * job group, specific job, log status, and time range. Validates user permissions
	 * before allowing access to log data.
	 *
	 * @param request HTTP servlet request for permission validation
	 * @param start pagination start index (default: 0)
	 * @param length page size (default: 10)
	 * @param jobGroup job group filter
	 * @param jobId specific job filter (0 for all jobs)
	 * @param logStatus log status filter (success/failure)
	 * @param filterTime time range filter in "yyyy-MM-dd HH:mm:ss - yyyy-MM-dd HH:mm:ss" format
	 * @return map containing log list data and pagination information
	 */
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										int jobGroup, int jobId, int logStatus, String filterTime) {

		// Validate user permission for the job group
		JobInfoController.validPermission(request, jobGroup);	// Only admin can query all; regular users can only query permitted jobGroups
		
		// Parse time filter parameters
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (filterTime!=null && filterTime.trim().length()>0) {
			String[] temp = filterTime.split(" - ");
			if (temp.length == 2) {
				// Parse start and end dates from filter string
				triggerTimeStart = DateUtil.parseDateTime(temp[0]);
				triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
			}
		}
		
		// Execute paginated query
		List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		
		// Package result for DataTable format
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// Total record count
	    maps.put("recordsFiltered", list_count);	// Filtered record count
	    maps.put("data", list);  					// Paginated data list
		return maps;
	}

	/**
	 * Display job log detail page
	 * 
	 * Shows detailed information for a specific job execution log entry,
	 * including trigger status, handle status, executor address, and timing information.
	 *
	 * @param id job log ID to display details for
	 * @param model Spring MVC model for view data
	 * @return view name for job log detail page
	 * @throws RuntimeException if log ID is invalid
	 */
	@RequestMapping("/logDetailPage")
	public String logDetailPage(int id, Model model){

		// Load job log entry and validate
		ReturnT<String> logStatue = ReturnT.SUCCESS;
		XxlJobLog jobLog = xxlJobLogDao.load(id);
		if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
		}

		// Add log details to model for display
        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getId());
		return "joblog/joblog.detail";
	}

	/**
	 * Get real-time job execution log content
	 * 
	 * Retrieves log content from the executor in real-time, supporting progressive
	 * log loading and streaming. Used for displaying live log output in the UI.
	 *
	 * @param executorAddress executor address where the job is running
	 * @param triggerTime job trigger time for log identification
	 * @param logId job log ID
	 * @param fromLineNum starting line number for log retrieval
	 * @return ReturnT containing log content and pagination information
	 */
	@RequestMapping("/logDetailCat")
	@ResponseBody
	public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum){
		try {
			// Get executor client and retrieve log content
			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
			ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

			// Check if log has reached the end
            if (logResult.getContent()!=null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                XxlJobLog jobLog = xxlJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                	// Mark log as complete if job has finished
                    logResult.getContent().setEnd(true);
                }
            }

			return logResult;
		} catch (Exception e) {
			// Handle communication errors with executor
			logger.error(e.getMessage(), e);
			return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}

	/**
	 * Terminate (kill) a running job execution
	 * 
	 * Sends a kill signal to the executor to terminate a currently running job.
	 * Updates the job log with termination status and handle information.
	 *
	 * @param id job log ID for the execution to terminate
	 * @return ReturnT indicating kill operation success or failure
	 */
	@RequestMapping("/logKill")
	@ResponseBody
	public ReturnT<String> logKill(int id){
		// Load job log and validate
		XxlJobLog log = xxlJobLogDao.load(id);
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
		if (jobInfo==null) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
		}

		// Send kill request to executor
		ReturnT<String> runResult = null;
		try {
			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
			runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
		} catch (Exception e) {
			// Handle communication errors
			logger.error(e.getMessage(), e);
			runResult = new ReturnT<String>(500, e.getMessage());
		}

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			// Update log with kill status
			log.setHandleCode(ReturnT.FAIL_CODE);
			log.setHandleMsg( I18nUtil.getString("joblog_kill_log_byman")+":" + (runResult.getMsg()!=null?runResult.getMsg():""));
			log.setHandleTime(new Date());
			// Complete the job log entry
			XxlJobCompleter.updateHandleInfoAndFinish(log);
			return new ReturnT<String>(runResult.getMsg());
		} else {
			return new ReturnT<String>(500, runResult.getMsg());
		}
	}

	/**
	 * Clear (delete) job execution logs
	 * 
	 * Removes job execution logs based on the specified cleanup type.
	 * Supports various cleanup strategies including time-based and count-based deletion.
	 * Processes deletion in batches to avoid database performance issues.
	 *
	 * @param jobGroup job group filter for log cleanup
	 * @param jobId specific job filter for log cleanup (0 for all jobs)
	 * @param type cleanup type:
	 *             1 - Clear logs older than 1 month
	 *             2 - Clear logs older than 3 months
	 *             3 - Clear logs older than 6 months
	 *             4 - Clear logs older than 1 year
	 *             5 - Keep latest 1000 logs
	 *             6 - Keep latest 10000 logs
	 *             7 - Keep latest 30000 logs
	 *             8 - Keep latest 100000 logs
	 *             9 - Clear all logs
	 * @return ReturnT indicating cleanup operation success or failure
	 */
	@RequestMapping("/clearLog")
	@ResponseBody
	public ReturnT<String> clearLog(int jobGroup, int jobId, int type){

		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -1);	// Clear log data from 1 month ago
		} else if (type == 2) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -3);	// Clear log data from 3 months ago
		} else if (type == 3) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -6);	// Clear log data from 6 months ago
		} else if (type == 4) {
			clearBeforeTime = DateUtil.addYears(new Date(), -1);	// Clear log data from 1 year ago
		} else if (type == 5) {
			clearBeforeNum = 1000;		// Keep latest 1000 log entries
		} else if (type == 6) {
			clearBeforeNum = 10000;		// Keep latest 10000 log entries
		} else if (type == 7) {
			clearBeforeNum = 30000;		// Keep latest 30000 log entries
		} else if (type == 8) {
			clearBeforeNum = 100000;	// Keep latest 100000 log entries
		} else if (type == 9) {
			clearBeforeNum = 0;			// Clear all log data
		} else {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
		}

		// Process deletion in batches to avoid performance issues
		List<Long> logIds = null;
		do {
			// Find log IDs to delete (1000 at a time)
			logIds = xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
			if (logIds!=null && logIds.size()>0) {
				// Delete the batch of logs
				xxlJobLogDao.clearLog(logIds);
			}
		} while (logIds!=null && logIds.size()>0);

		return ReturnT.SUCCESS;
	}

}
