package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.core.util.FtlUtil;
import com.xxl.job.admin.core.util.I18nUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Cookie Interceptor - Cookie and template utility provider
 * 
 * This interceptor enhances the Spring MVC model with cookie information
 * and static utility methods for template processing. It executes after
 * controller methods to prepare data for view rendering.
 *
 * Key functionalities:
 * - Extracts HTTP cookies and makes them available to templates as a map
 * - Provides static utility methods (like I18nUtil) to Freemarker templates
 * - Enhances template rendering capabilities with request context data
 *
 * @author xuxueli 2015-12-12 18:09:04
 * @since 1.0.0
 */
@Component
public class CookieInterceptor implements AsyncHandlerInterceptor {

	/**
	 * Post-process the model and view after controller execution
	 * 
	 * Extracts cookies from the HTTP request and adds them to the model for template access.
	 * Also provides static utility classes to the template engine for enhanced functionality.
	 *
	 * @param request HTTP servlet request containing cookies
	 * @param response HTTP servlet response (unused)
	 * @param handler the executed handler (unused)
	 * @param modelAndView the ModelAndView to enhance with cookie data and utilities
	 * @throws Exception if processing fails
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// Extract and map cookies for template access
		if (modelAndView!=null && request.getCookies()!=null && request.getCookies().length>0) {
			HashMap<String, Cookie> cookieMap = new HashMap<String, Cookie>();
			for (Cookie ck : request.getCookies()) {
				// Create a name-to-cookie mapping for easy template access
				cookieMap.put(ck.getName(), ck);
			}
			modelAndView.addObject("cookieMap", cookieMap);
		}

		// Provide static utility methods to templates
		if (modelAndView != null) {
			// Make I18nUtil available as a static model in templates
			modelAndView.addObject("I18nUtil", FtlUtil.generateStaticModel(I18nUtil.class.getName()));
		}

	}
	
}
