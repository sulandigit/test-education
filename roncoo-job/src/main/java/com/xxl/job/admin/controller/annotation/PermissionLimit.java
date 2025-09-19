package com.xxl.job.admin.controller.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permission Limit Annotation
 * 
 * This annotation is used to control access permissions for controller methods.
 * It provides login validation and admin-only access control.
 * 
 * Usage:
 * - @PermissionLimit(limit=false) - Skip login check
 * - @PermissionLimit(adminuser=true) - Require admin privileges
 * 
 * @author xuxueli 2015-12-12 18:29:02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionLimit {
	
	/**
	 * Enable login validation (default: true)
	 * Set to false to skip authentication check
	 */
	boolean limit() default true;

	/**
	 * Require administrator privileges (default: false)
	 * Set to true to restrict access to admin users only
	 *
	 * @return true if admin privileges required
	 */
	boolean adminuser() default false;

}