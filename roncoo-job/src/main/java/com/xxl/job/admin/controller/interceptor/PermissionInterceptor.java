package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Permission Interceptor
 * 
 * This interceptor handles authentication and authorization for all
 * controller requests in the XXL-Job admin interface.
 * 
 * Features:
 * - Login validation based on @PermissionLimit annotation
 * - Administrator privilege checking
 * - Automatic redirection to login page for unauthenticated users
 * - Request attribute population with user information
 * 
 * The interceptor checks method-level @PermissionLimit annotations to determine:
 * - Whether login is required (limit = true/false)
 * - Whether admin privileges are needed (adminuser = true/false)
 * 
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	/**
	 * Login service for user authentication
	 */
	@Resource
	private LoginService loginService;

	/**
	 * Pre-handle method for permission checking
	 * 
	 * This method is called before the controller method execution to validate
	 * user permissions and handle authentication requirements.
	 * 
	 * @param request HTTP servlet request
	 * @param response HTTP servlet response  
	 * @param handler request handler (controller method)
	 * @return true to continue processing, false to stop
	 * @throws Exception if permission validation fails
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// Skip interception for non-controller methods
		if (!(handler instanceof HandlerMethod)) {
			return true;	// proceed with the next interceptor
		}

		// Check permission requirements from annotation
		boolean needLogin = true;
		boolean needAdminuser = false;
		HandlerMethod method = (HandlerMethod)handler;
		PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
		if (permission!=null) {
			needLogin = permission.limit();
			needAdminuser = permission.adminuser();
		}

		// Validate login requirement
		if (needLogin) {
			XxlJobUser loginUser = loginService.ifLogin(request, response);
			if (loginUser == null) {
				// Redirect to login page if not authenticated
				response.setStatus(302);
				response.setHeader("location", request.getContextPath()+"/toLogin");
				return false;
			}
			// Check admin privilege requirement
			if (needAdminuser && loginUser.getRole()!=1) {
				throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
			}
			// Store user information in request attributes
			request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);
		}

		return true;	// proceed with the next interceptor
	}
	
}
