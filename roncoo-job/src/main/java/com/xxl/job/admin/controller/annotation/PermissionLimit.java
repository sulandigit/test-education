package com.xxl.job.admin.controller.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permission Limit Annotation - Access control and authorization
 * 
 * This annotation provides declarative access control for controller methods.
 * It allows fine-grained permission management including login requirements
 * and admin-only access restrictions. The annotation is processed by the
 * permission interceptor to enforce access controls.
 *
 * Usage examples:
 * - @PermissionLimit() - Requires user login (default)
 * - @PermissionLimit(limit=false) - No login required (public access)
 * - @PermissionLimit(adminuser=true) - Requires admin privileges
 *
 * @author xuxueli 2015-12-12 18:29:02
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionLimit {
	
	/**
	 * Login requirement control (default: enabled)
	 * 
	 * Determines whether user authentication is required to access the method.
	 * When set to true (default), users must be logged in to access the endpoint.
	 * When set to false, the endpoint is publicly accessible without authentication.
	 *
	 * @return true if login is required, false for public access
	 */
	boolean limit() default true;

	/**
	 * Administrator privilege requirement
	 * 
	 * When set to true, requires the authenticated user to have administrator
	 * privileges to access the method. This provides an additional layer of
	 * access control beyond basic authentication.
	 *
	 * @return true if admin privileges are required, false otherwise
	 */
	boolean adminuser() default false;

}