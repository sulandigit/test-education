package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Job Group Controller - Executor group management
 * 
 * This controller handles all executor group (job group) related operations including
 * group creation, modification, deletion, and registry management. It provides
 * comprehensive executor group lifecycle management and serves as the primary interface for:
 * - Executor group CRUD operations (Create, Read, Update, Delete)
 * - Automatic and manual executor registration management
 * - Executor address list configuration and validation
 * - Group permissions and access control
 * - Registry service integration and monitoring
 *
 * @author xuxueli 2016-10-02 20:52:56
 * @since 1.0.0
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobRegistryDao xxlJobRegistryDao;

	/**
	 * Display the job group management index page
	 * 
	 * Shows the main job group management interface for creating, editing,
	 * and managing executor groups.
	 *
	 * @param model Spring MVC model for view data
	 * @return view name for job group management page
	 */
	@RequestMapping
	public String index(Model model) {
		return "jobgroup/jobgroup.index";
	}

	/**
	 * Get paginated list of job groups with filtering
	 * 
	 * Retrieves a paginated list of job groups with support for filtering by
	 * application name and title. Used for displaying groups in the management interface.
	 *
	 * @param request HTTP servlet request for context
	 * @param start pagination start index (default: 0)
	 * @param length page size (default: 10)
	 * @param appname application name filter (partial match)
	 * @param title group title filter (partial match)
	 * @return map containing job group list data and pagination information
	 */
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String appname, String title) {

		// Execute paginated query with filters
		List<XxlJobGroup> list = xxlJobGroupDao.pageList(start, length, appname, title);
		int list_count = xxlJobGroupDao.pageListCount(start, length, appname, title);

		// Package result for DataTable format
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", list_count);		// Total record count
		maps.put("recordsFiltered", list_count);	// Filtered record count
		maps.put("data", list);  					// Paginated data list
		return maps;
	}

	/**
	 * Save (create) a new job group
	 * 
	 * Creates a new executor group with comprehensive validation of input parameters.
	 * Validates application name, title, and address configuration based on the address type.
	 *
	 * @param xxlJobGroup job group object containing group configuration
	 * @return ReturnT indicating save operation success or failure with validation messages
	 */
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(XxlJobGroup xxlJobGroup){

		// Validate application name
		if (xxlJobGroup.getAppname()==null || xxlJobGroup.getAppname().trim().length()==0) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
			return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length") );
		}
		if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
			return new ReturnT<String>(500, "AppName"+I18nUtil.getString("system_unvalid") );
		}
		
		// Validate group title
		if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
			return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_title")+I18nUtil.getString("system_unvalid") );
		}
		
		// Validate address configuration for manual registration type
		if (xxlJobGroup.getAddressType()!=0) {
			if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
				return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
				return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList")+I18nUtil.getString("system_unvalid") );
			}

			// Validate individual addresses in the list
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		// Set update timestamp and save
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.save(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	/**
	 * Update an existing job group
	 * 
	 * Updates an existing executor group with validation and automatic registry handling.
	 * Supports both automatic registration (type 0) and manual address configuration (type 1).
	 *
	 * @param xxlJobGroup job group object containing updated configuration
	 * @return ReturnT indicating update operation success or failure with validation messages
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobGroup xxlJobGroup){
		// Validate application name
		if (xxlJobGroup.getAppname()==null || xxlJobGroup.getAppname().trim().length()==0) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input")+"AppName") );
		}
		if (xxlJobGroup.getAppname().length()<4 || xxlJobGroup.getAppname().length()>64) {
			return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length") );
		}
		
		// Validate group title
		if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")) );
		}
		
		if (xxlJobGroup.getAddressType() == 0) {
			// Address type 0 = Automatic registration
			List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
			String addressListStr = null;
			if (registryList!=null && !registryList.isEmpty()) {
				// Sort and format address list
				Collections.sort(registryList);
				addressListStr = "";
				for (String item:registryList) {
					addressListStr += item + ",";
				}
				// Remove trailing comma
				addressListStr = addressListStr.substring(0, addressListStr.length()-1);
			}
			xxlJobGroup.setAddressList(addressListStr);
		} else {
			// Address type 1 = Manual input
			if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
				return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			// Validate each address in the manual list
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		// Set update timestamp and save changes
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.update(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	/**
	 * Find registered executor addresses by application name
	 * 
	 * Retrieves all currently registered executor addresses for a given application name
	 * from the registry service. Filters out expired registrations based on the dead timeout.
	 *
	 * @param appnameParam application name to search for
	 * @return list of registered executor addresses for the application
	 */
	private List<String> findRegistryByAppName(String appnameParam){
		// Build application-to-address mapping from registry
		HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
		// Get all active registry entries (not expired)
		List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
		if (list != null) {
			for (XxlJobRegistry item: list) {
				// Only process executor type registrations
				if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
					String appname = item.getRegistryKey();
					List<String> registryList = appAddressMap.get(appname);
					if (registryList == null) {
						registryList = new ArrayList<String>();
					}

					// Add unique addresses to the list
					if (!registryList.contains(item.getRegistryValue())) {
						registryList.add(item.getRegistryValue());
					}
					appAddressMap.put(appname, registryList);
				}
			}
		}
		// Return address list for the specified application
		return appAddressMap.get(appnameParam);
	}

	/**
	 * Remove (delete) a job group
	 * 
	 * Deletes a job group with validation to ensure no jobs are associated with it
	 * and that it's not the last remaining group in the system.
	 *
	 * @param id job group ID to remove
	 * @return ReturnT indicating removal operation success or failure with validation messages
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id){

		// Validate: Check if any jobs are still associated with this group
		int count = xxlJobInfoDao.pageListCount(0, 10, id, -1,  null, null, null);
		if (count > 0) {
			return new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_0") );
		}

		// Validate: Ensure at least one group remains in the system
		List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_1") );
		}

		// Proceed with deletion
		int ret = xxlJobGroupDao.remove(id);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	/**
	 * Load job group by ID
	 * 
	 * Retrieves a specific job group by its ID for editing or display purposes.
	 *
	 * @param id job group ID to load
	 * @return ReturnT containing the job group object or failure status
	 */
	@RequestMapping("/loadById")
	@ResponseBody
	public ReturnT<XxlJobGroup> loadById(int id){
		// Load job group from database
		XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
		// Return success with data or failure status
		return jobGroup!=null?new ReturnT<XxlJobGroup>(jobGroup):new ReturnT<XxlJobGroup>(ReturnT.FAIL_CODE, null);
	}

}
