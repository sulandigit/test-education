package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Controller - User management and administration
 * 
 * This controller handles all user account management operations including user creation,
 * modification, deletion, and password management. It provides comprehensive user lifecycle
 * management with administrative controls and serves as the primary interface for:
 * - User CRUD operations (Create, Read, Update, Delete)
 * - User authentication and password management
 * - User role and permission configuration
 * - Administrative user controls and validation
 * - Password encryption and security handling
 *
 * Note: Most operations require admin privileges for security.
 *
 * @author xuxueli 2019-05-04 16:39:50
 * @since 1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    /**
     * Display the user management index page
     * 
     * Shows the main user management interface with job group information.
     * Only accessible to admin users for security purposes.
     *
     * @param model Spring MVC model for view data
     * @return view name for user management page
     */
    @RequestMapping
    @PermissionLimit(adminuser = true)
    public String index(Model model) {

        // Load all job groups for permission assignment
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    /**
     * Get paginated list of users with filtering
     * 
     * Retrieves a paginated list of users with support for filtering by username and role.
     * Removes password information for security before returning data.
     * Only accessible to admin users.
     *
     * @param start pagination start index (default: 0)
     * @param length page size (default: 10)
     * @param username username filter (partial match)
     * @param role user role filter
     * @return map containing user list data and pagination information
     */
    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username, int role) {

        // Execute paginated query with filters
        List<XxlJobUser> list = xxlJobUserDao.pageList(start, length, username, role);
        int list_count = xxlJobUserDao.pageListCount(start, length, username, role);

        // Remove password information for security
        if (list!=null && list.size()>0) {
            for (XxlJobUser item: list) {
                item.setPassword(null);
            }
        }

        // Package result for DataTable format
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// Total record count
        maps.put("recordsFiltered", list_count);	// Filtered record count
        maps.put("data", list);  					// Paginated data list
        return maps;
    }

    /**
     * Add a new user
     * 
     * Creates a new user account with comprehensive validation of username and password.
     * Encrypts password using MD5 hashing and checks for username uniqueness.
     * Only accessible to admin users.
     *
     * @param xxlJobUser user object containing user information
     * @return ReturnT indicating add operation success or failure with validation messages
     */
    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> add(XxlJobUser xxlJobUser) {

        // Validate username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        
        // Validate password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        
        // Encrypt password with MD5 hash
        xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));

        // Check for duplicate username
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat") );
        }

        // Save new user
        xxlJobUserDao.save(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    /**
     * Update an existing user
     * 
     * Updates user information with validation and security checks.
     * Prevents users from modifying their own accounts to avoid privilege escalation.
     * Only accessible to admin users.
     *
     * @param request HTTP servlet request for user context
     * @param xxlJobUser user object containing updated information
     * @return ReturnT indicating update operation success or failure
     */
    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) {

        // Prevent users from modifying their own accounts
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getUsername().equals(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        // Validate and encrypt password if provided
        if (StringUtils.hasText(xxlJobUser.getPassword())) {
            xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
            if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
            }
            // Encrypt password with MD5 hash
            xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));
        } else {
            // Keep existing password if not provided
            xxlJobUser.setPassword(null);
        }

        // Update user information
        xxlJobUserDao.update(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    /**
     * Remove (delete) a user
     * 
     * Deletes a user account with validation to prevent self-deletion.
     * Only accessible to admin users.
     *
     * @param request HTTP servlet request for user context
     * @param id user ID to remove
     * @return ReturnT indicating removal operation success or failure
     */
    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> remove(HttpServletRequest request, int id) {

        // Prevent users from deleting their own accounts
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getId() == id) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        // Delete the user account
        xxlJobUserDao.delete(id);
        return ReturnT.SUCCESS;
    }

    /**
     * Update user password
     * 
     * Allows users to change their own password with validation and encryption.
     * Available to all authenticated users (not admin-only).
     *
     * @param request HTTP servlet request for user context
     * @param password new password to set
     * @return ReturnT indicating password update success or failure
     */
    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password){

        // Validate password input
        if (password==null || password.trim().length()==0){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "Password cannot be empty");
        }
        password = password.trim();
        if (!(password.length()>=4 && password.length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        // Encrypt password with MD5 hash
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // Get current logged-in user
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

        // Load user from database and update password
        XxlJobUser existUser = xxlJobUserDao.loadByUserName(loginUser.getUsername());
        existUser.setPassword(md5Password);
        xxlJobUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

}
