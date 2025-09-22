package com.xxl.job.admin.controller.resolver;

import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.admin.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web Exception Resolver - Global exception handling and error response
 * 
 * This resolver provides centralized exception handling for the entire web application.
 * It intercepts unhandled exceptions from controllers and formats them into appropriate
 * responses based on the request type (JSON API vs. web page). The resolver ensures
 * consistent error handling and user-friendly error messages.
 *
 * Key features:
 * - Automatic detection of JSON vs. HTML response requirements
 * - Standardized error response format using ReturnT
 * - Comprehensive logging for debugging and monitoring
 * - Special handling for application-specific XxlJobException
 * - Error message sanitization for web display
 *
 * @author xuxueli 2016-1-6 19:22:18
 * @since 1.0.0
 */
@Component
public class WebExceptionResolver implements HandlerExceptionResolver {
	private static transient Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

	/**
	 * Resolve exceptions and generate appropriate error responses
	 * 
	 * Handles all unhandled exceptions from controllers and generates appropriate
	 * error responses. Automatically detects whether to return JSON or HTML based
	 * on the handler method's @ResponseBody annotation.
	 *
	 * @param request HTTP servlet request context
	 * @param response HTTP servlet response for error output
	 * @param handler the handler method that threw the exception
	 * @param ex the exception that was thrown
	 * @return ModelAndView containing error information or null for JSON responses
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		// Log non-application exceptions for debugging
		if (!(ex instanceof XxlJobException)) {
			logger.error("WebExceptionResolver:{}", ex);
		}

		// Determine response format based on handler method annotation
		boolean isJson = false;
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod)handler;
			// Check if method is annotated with @ResponseBody (JSON response)
			ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
			if (responseBody != null) {
				isJson = true;
			}
		}

		// Create standardized error result
		ReturnT<String> errorResult = new ReturnT<String>(ReturnT.FAIL_CODE, ex.toString().replaceAll("\n", "<br/>"));

		// Generate appropriate response
		ModelAndView mv = new ModelAndView();
		if (isJson) {
			// Return JSON error response for API endpoints
			try {
				response.setContentType("application/json;charset=utf-8");
				response.getWriter().print(JacksonUtil.writeValueAsString(errorResult));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			return mv;
		} else {
			// Return HTML error page for web requests
			mv.addObject("exceptionMsg", errorResult.getMsg());
			mv.setViewName("/common/common.exception");
			return mv;
		}
	}
	
}