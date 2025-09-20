package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.thread.*;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * XXL-Job Scheduler - Core Scheduling Engine
 * 
 * This is the main scheduler component that manages the lifecycle of all
 * job scheduling operations and related helper threads.
 * 
 * Responsibilities:
 * - Initialize and manage all helper threads (trigger, registry, monitor, etc.)
 * - Handle internationalization settings
 * - Manage executor client connections and caching
 * - Coordinate system startup and shutdown sequences
 * 
 * Helper threads managed:
 * - JobTriggerPoolHelper: Handles job triggering
 * - JobRegistryHelper: Monitors executor registration
 * - JobFailMonitorHelper: Monitors failed job executions
 * - JobCompleteHelper: Handles job completion callbacks
 * - JobLogReportHelper: Generates job execution reports
 * - JobScheduleHelper: Core scheduling logic
 * 
 * @author xuxueli 2018-10-28 00:18:17
 */

public class XxlJobScheduler  {
    /**
     * Logger instance for this scheduler
     */
    private static final Logger logger = LoggerFactory.getLogger(XxlJobScheduler.class);


    /**
     * Initialize the XXL-Job scheduler and all its components
     * 
     * This method starts all helper threads in the correct order:
     * 1. Initialize internationalization
     * 2. Start trigger pool for job execution
     * 3. Start registry monitor for executor management
     * 4. Start failure monitor for error handling
     * 5. Start completion monitor for callback handling
     * 6. Start log report generator
     * 7. Start main scheduling engine
     * 
     * @throws Exception if initialization fails
     */
    public void init() throws Exception {
        // Initialize internationalization
        initI18n();

        // Start admin trigger pool
        JobTriggerPoolHelper.toStart();

        // Start admin registry monitor
        JobRegistryHelper.getInstance().start();

        // Start admin fail-monitor
        JobFailMonitorHelper.getInstance().start();

        // Start admin lose-monitor (depends on JobTriggerPoolHelper)
        JobCompleteHelper.getInstance().start();

        // Start admin log report
        JobLogReportHelper.getInstance().start();

        // Start main scheduler (depends on JobTriggerPoolHelper)
        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init xxl-job admin success.");
    }

    
    /**
     * Destroy the scheduler and cleanup all resources
     * 
     * This method stops all helper threads in reverse order to ensure
     * proper cleanup and avoid dependency issues.
     * 
     * @throws Exception if cleanup fails
     */
    public void destroy() throws Exception {

        // Stop main scheduler
        JobScheduleHelper.getInstance().toStop();

        // Stop admin log report
        JobLogReportHelper.getInstance().toStop();

        // Stop admin lose-monitor
        JobCompleteHelper.getInstance().toStop();

        // Stop admin fail-monitor
        JobFailMonitorHelper.getInstance().toStop();

        // Stop admin registry
        JobRegistryHelper.getInstance().toStop();

        // Stop admin trigger pool
        JobTriggerPoolHelper.toStop();

    }

    // ---------------------- I18n ----------------------

    /**
     * Initialize internationalization settings
     * 
     * Sets up localized titles for all executor block strategy enums
     * based on the current locale configuration.
     */
    private void initI18n(){
        for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    // ---------------------- executor-client ----------------------
    
    /**
     * Cache for executor client connections
     */
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    
    /**
     * Get or create an executor client for the given address
     * 
     * This method implements a caching mechanism to reuse executor
     * client connections and avoid creating duplicate connections.
     * 
     * @param address executor address (IP:Port)
     * @return ExecutorBiz client instance
     * @throws Exception if client creation fails
     */
    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        // Validate address
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // Check cache first
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // Create new client and cache it
        executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
