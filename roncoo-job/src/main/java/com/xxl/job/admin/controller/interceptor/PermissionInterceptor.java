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
 * Permission Interceptor - Authentication and authorization enforcement
 * 
 * This interceptor enforces access control based on the @PermissionLimit annotation.
 * It validates user authentication and authorization before allowing access to
 * protected controller methods. The interceptor implements a comprehensive
 * security model with support for:
 *
 * - User login validation and session management
 * - Admin privilege verification for sensitive operations
 * - Automatic redirection to login page for unauthenticated users
 * - Integration with the LoginService for user state management
 *
 * @author xuxueli 2015-12-12 18:09:04
 * @since 1.0.0
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private LoginService loginService;

	/**
	 * Pre-process requests to enforce access control
	 * 
	 * Validates user authentication and authorization based on the @PermissionLimit
	 * annotation on the target controller method. Handles login requirements and
	 * admin privilege checks before allowing request processing to continue.
	 *
	 * @param request HTTP servlet request for user identification
	 * @param response HTTP servlet response for redirections
	 * @param handler the handler method being invoked
	 * @return true to continue processing, false to halt the request
	 * @throws Exception if permission validation fails
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// Skip non-controller handlers (e.g., static resources)
		if (!(handler instanceof HandlerMethod)) {
			return true;	// proceed with the next interceptor
		}

		// Extract permission requirements from method annotation
		boolean needLogin = true;
		boolean needAdminuser = false;
		HandlerMethod method = (HandlerMethod)handler;
		PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
		if (permission!=null) {
			needLogin = permission.limit();
			needAdminuser = permission.adminuser();
		}

		// Enforce login requirement if needed
		if (needLogin) {
			// Check if user is authenticated
			XxlJobUser loginUser = loginService.ifLogin(request, response);
			if (loginUser == null) {
				// Redirect unauthenticated users to login page
				response.setStatus(302);
				response.setHeader("location", request.getContextPath()+"/toLogin");
				return false;
			}
			
			// Enforce admin privilege requirement if needed
			if (needAdminuser && loginUser.getRole()!=1) {
				throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
			}
			
			// Store authenticated user in request for controller access
			request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);
		}

		return true;	// proceed with the next interceptor
	}
	
}
