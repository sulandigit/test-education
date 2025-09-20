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
 * Index Controller - Main Dashboard and Authentication Controller
 * 
 * This controller handles the main dashboard, authentication operations,
 * and basic navigation for the XXL-Job admin interface.
 * 
 * Features:
 * - Dashboard with job statistics and charts
 * - User login and logout functionality
 * - Chart data for job execution reports
 * - Help page navigation
 * - Date binding configuration
 * 
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	/**
	 * Job service for business operations
	 */
	@Resource
	private XxlJobService xxlJobService;
	
	/**
	 * Login service for authentication
	 */
	@Resource
	private LoginService loginService;


	/**
	 * Display the main dashboard with job statistics
	 * 
	 * @param model Spring model for view data
	 * @return view name for the dashboard
	 */
	@RequestMapping("/")
	public String index(Model model) {

		Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
		model.addAllAttributes(dashboardMap);

		return "index";
	}

	/**
	 * Get chart information for job execution reports
	 * 
	 * @param startDate start date for the chart data
	 * @param endDate end date for the chart data
	 * @return chart data in JSON format
	 */
    @RequestMapping("/chartInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(startDate, endDate);
        return chartInfo;
    }
	
	/**
	 * Display login page or redirect to dashboard if already logged in
	 * 
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @param modelAndView model and view object
	 * @return login page or redirect to dashboard
	 */
	@RequestMapping("/toLogin")
	@PermissionLimit(limit=false)
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response,ModelAndView modelAndView) {
		if (loginService.ifLogin(request, response) != null) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		return new ModelAndView("login");
	}
	
	/**
	 * Process user login request
	 * 
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @param userName user name for login
	 * @param password password for login
	 * @param ifRemember remember login option
	 * @return login result
	 */
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
		boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
		return loginService.login(request, response, userName, password, ifRem);
	}
	
	/**
	 * Process user logout request
	 * 
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response
	 * @return logout result
	 */
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		return loginService.logout(request, response);
	}
	
	/**
	 * Display help page
	 * 
	 * @return help page view name
	 */
	@RequestMapping("/help")
	public String help() {

		/*if (!PermissionInterceptor.ifLogin(request)) {
			return "redirect:/toLogin";
		}*/

		return "help";
	}

	/**
	 * Initialize data binder for date format conversion
	 * 
	 * @param binder web data binder for type conversion
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
