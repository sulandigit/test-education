package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Job Code Controller - Job script code management
 * 
 * This controller handles job script code editing, versioning, and management
 * for jobs that use GLUE (script-based) execution modes. It provides comprehensive
 * code management functionality and serves as the primary interface for:
 * - Job script code editing and validation
 * - Code version history management and tracking
 * - GLUE type validation and configuration
 * - Code backup and restoration capabilities
 * - Permission validation for code access
 *
 * Note: Only applies to jobs with GLUE execution types (not BEAN types).
 *
 * @author xuxueli 2015-12-19 16:13:16
 * @since 1.0.0
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController {
	
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;

	/**
	 * Display the job code editing interface
	 * 
	 * Shows the code editor interface for GLUE-type jobs with syntax highlighting
	 * and version history. Validates job existence, GLUE type compatibility,
	 * and user permissions before allowing access.
	 *
	 * @param request HTTP servlet request for permission validation
	 * @param model Spring MVC model for view data
	 * @param jobId job ID to edit code for
	 * @return view name for job code editor page
	 * @throws RuntimeException if job is invalid or not GLUE type
	 */
	@RequestMapping
	public String index(HttpServletRequest request, Model model, int jobId) {
		// Load job information and code history
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
		List<XxlJobLogGlue> jobLogGlues = xxlJobLogGlueDao.findByJobId(jobId);

		// Validate job exists
		if (jobInfo == null) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		
		// Validate job is GLUE type (not BEAN type)
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
		}

		// Validate user has permission to access this job group
		JobInfoController.validPermission(request, jobInfo.getJobGroup());

		// Add GLUE type options for editor
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		// Add job data and code history to model
		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}
	
	/**
	 * Save job script code with version tracking
	 * 
	 * Updates the job's script code and creates a backup version for history tracking.
	 * Validates input parameters and maintains a rolling backup of the last 30 versions.
	 *
	 * @param model Spring MVC model (unused but kept for consistency)
	 * @param id job ID to update code for
	 * @param glueSource the script source code content
	 * @param glueRemark description/comment for this code version
	 * @return ReturnT indicating save operation success or failure
	 */
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
		// Validate remark is provided
		if (glueRemark==null) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")) );
		}
		// Validate remark length (4-100 characters)
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
		}
		
		// Load and validate job exists
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		
		// Update job with new code and metadata
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());
		exists_jobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(exists_jobInfo);

		// Create version backup log entry
		XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
		xxlJobLogGlue.setJobId(exists_jobInfo.getId());
		xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		xxlJobLogGlue.setGlueSource(glueSource);
		xxlJobLogGlue.setGlueRemark(glueRemark);
		xxlJobLogGlue.setAddTime(new Date());
		xxlJobLogGlue.setUpdateTime(new Date());
		xxlJobLogGlueDao.save(xxlJobLogGlue);

		// Maintain backup limit - keep only latest 30 versions
		xxlJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);

		return ReturnT.SUCCESS;
	}
	
}
