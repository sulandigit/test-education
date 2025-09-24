package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Index Controller - Main entry point and authentication management
 * 
 * This controller handles the main dashboard, user authentication, and system navigation.
 * It serves as the primary entry point for the XXL-JOB admin interface and manages:
 * - Dashboard display with job statistics
 * - User login and logout functionality
 * - Chart data for job execution statistics
 * - Help page navigation
 * - Session management and authentication
 *
 * @author xuxueli 2015-12-19 16:13:16
 * @since 1.0.0
 */
@Controller
public class IndexController {

	@Resource
	private XxlJobService xxlJobService;
	@Resource
	private LoginService loginService;


	/**
	 * Display the main dashboard page
	 * 
	 * Loads and displays the main dashboard with job execution statistics,
	 * including job counts, success rates, and other system metrics.
	 *
	 * @param model Spring MVC model to hold dashboard data
	 * @return view name "index" for the dashboard template
	 */
	@RequestMapping("/")
	public String index(Model model) {
		// Load dashboard statistics and metrics
		Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
		model.addAllAttributes(dashboardMap);

		return "index";
	}

	/**
	 * Get chart data for job execution statistics
	 * 
	 * Retrieves statistical data for rendering charts on the dashboard,
	 * including job execution trends and success/failure rates within the specified date range.
	 *
	 * @param startDate start date for the statistics period
	 * @param endDate end date for the statistics period
	 * @return ReturnT containing chart data as a map
	 */
	@RequestMapping("/chartInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
		// Retrieve chart data for the specified date range
		ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(startDate, endDate);
		return chartInfo;
	}
	
	/**
	 * Display the login page
	 * 
	 * Shows the login form for user authentication. If the user is already logged in,
	 * redirects to the main dashboard instead of showing the login page.
	 *
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @param modelAndView Spring MVC ModelAndView object
	 * @return ModelAndView for login page or redirect to dashboard
	 */
	@RequestMapping("/toLogin")
	@PermissionLimit(limit=false)
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response,ModelAndView modelAndView) {
		// Check if user is already logged in
		if (loginService.ifLogin(request, response) != null) {
			// Redirect to dashboard if already authenticated
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		// Show login page if not authenticated
		return new ModelAndView("login");
	}
	
	/**
	 * Process user login request
	 * 
	 * Handles user authentication with username and password validation.
	 * Supports "remember me" functionality for persistent login sessions.
	 *
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @param userName user's login name
	 * @param password user's password
	 * @param ifRemember "remember me" checkbox value ("on" if checked)
	 * @return ReturnT with login result message
	 */
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
		// Parse "remember me" option
		boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
		// Perform login authentication
		return loginService.login(request, response, userName, password, ifRem);
	}
	
	/**
	 * Process user logout request
	 * 
	 * Handles user logout by clearing session data and authentication cookies.
	 * This invalidates the current user session and requires re-authentication for future access.
	 *
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @return ReturnT with logout result message
	 */
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		// Perform logout and clear session
		return loginService.logout(request, response);
	}
	
	/**
	 * Display the help page
	 * 
	 * Shows the help documentation and user guide for the XXL-JOB admin interface.
	 * This page provides instructions and documentation for using the system.
	 *
	 * @return view name "help" for the help page template
	 */
	@RequestMapping("/help")
	public String help() {
		// Note: Permission check is commented out - help page is accessible to all users
		/*if (!PermissionInterceptor.ifLogin(request)) {
			return "redirect:/toLogin";
		}*/

		return "help";
	}

	/**
	 * Initialize data binding for date parameters
	 * 
	 * Configures custom date format binding for HTTP request parameters.
	 * Sets up automatic conversion of date strings to Date objects using the
	 * "yyyy-MM-dd HH:mm:ss" format.
	 *
	 * @param binder WebDataBinder for configuring parameter binding
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// Configure date format for parameter binding
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		// Register custom date editor for automatic date conversion
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
