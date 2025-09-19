package com.xxl.job.admin.core.conf;

import com.xxl.job.admin.core.alarm.JobAlarmer;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * XXL-Job Admin Configuration
 * 
 * Central configuration class for the XXL-Job administration system.
 * This class manages the core scheduler initialization, system properties,
 * and provides access to DAOs and services throughout the application.
 * 
 * Features:
 * - Initializes and manages the XXL-Job scheduler lifecycle
 * - Provides centralized access to system configuration properties
 * - Manages database access objects (DAOs) and services
 * - Handles internationalization settings
 * - Configures email notification settings
 * - Manages thread pool configurations for job triggering
 * 
 * @author xuxueli 2017-04-28
 */

@Component
public class XxlJobAdminConfig implements InitializingBean, DisposableBean {

    /**
     * Singleton instance of the admin configuration
     */
    private static XxlJobAdminConfig adminConfig = null;
    
    /**
     * Get the singleton instance of admin configuration
     * 
     * @return XxlJobAdminConfig instance
     */
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }


    // ---------------------- XxlJobScheduler ----------------------

    /**
     * Core job scheduler instance
     */
    private XxlJobScheduler xxlJobScheduler;

    /**
     * Initialize the admin configuration and start the scheduler
     * Called after all properties are set by Spring
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        xxlJobScheduler = new XxlJobScheduler();
        xxlJobScheduler.init();
    }

    /**
     * Clean up resources when the bean is being destroyed
     */
    @Override
    public void destroy() throws Exception {
        xxlJobScheduler.destroy();
    }


    // ---------------------- Configuration Properties ----------------------

    /**
     * Internationalization setting (zh_CN, zh_TC, en)
     */
    @Value("${xxl.job.i18n}")
    private String i18n;

    /**
     * Access token for API authentication
     */
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    /**
     * Email sender address for notifications
     */
    @Value("${spring.mail.from}")
    private String emailFrom;

    /**
     * Maximum number of threads in the fast trigger pool
     */
    @Value("${xxl.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    /**
     * Maximum number of threads in the slow trigger pool
     */
    @Value("${xxl.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

    /**
     * Number of days to retain job logs
     */
    @Value("${xxl.job.logretentiondays}")
    private int logretentiondays;

    // ---------------------- DAO and Service Dependencies ----------------------

    // ---------------------- DAO and Service Dependencies ----------------------

    /**
     * Data access object for job execution logs
     */
    @Resource
    private XxlJobLogDao xxlJobLogDao;
    
    /**
     * Data access object for job information
     */
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    
    /**
     * Data access object for executor registry
     */
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    
    /**
     * Data access object for executor groups
     */
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    
    /**
     * Data access object for job log reports
     */
    @Resource
    private XxlJobLogReportDao xxlJobLogReportDao;
    
    /**
     * Java mail sender for email notifications
     */
    @Resource
    private JavaMailSender mailSender;
    
    /**
     * Database data source
     */
    @Resource
    private DataSource dataSource;
    
    /**
     * Job alarm service for notifications
     */
    @Resource
    private JobAlarmer jobAlarmer;

    // ---------------------- Getter Methods ----------------------

    /**
     * Get internationalization setting with validation
     * 
     * @return valid i18n setting (defaults to zh_CN if invalid)
     */
    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    /**
     * Get access token for API authentication
     * 
     * @return access token string
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Get email sender address
     * 
     * @return email from address
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * Get fast trigger pool max size with minimum validation
     * 
     * @return trigger pool size (minimum 200)
     */
    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    /**
     * Get slow trigger pool max size with minimum validation
     * 
     * @return trigger pool size (minimum 100)
     */
    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    /**
     * Get log retention days with validation
     * 
     * @return log retention days (-1 if less than 7 to disable cleanup)
     */
    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;  // Limit greater than or equal to 7, otherwise close
        }
        return logretentiondays;
    }

    /**
     * Get job log data access object
     * 
     * @return XxlJobLogDao instance
     */
    public XxlJobLogDao getXxlJobLogDao() {
        return xxlJobLogDao;
    }

    /**
     * Get job info data access object
     * 
     * @return XxlJobInfoDao instance
     */
    public XxlJobInfoDao getXxlJobInfoDao() {
        return xxlJobInfoDao;
    }

    /**
     * Get executor registry data access object
     * 
     * @return XxlJobRegistryDao instance
     */
    public XxlJobRegistryDao getXxlJobRegistryDao() {
        return xxlJobRegistryDao;
    }

    /**
     * Get executor group data access object
     * 
     * @return XxlJobGroupDao instance
     */
    public XxlJobGroupDao getXxlJobGroupDao() {
        return xxlJobGroupDao;
    }

    /**
     * Get job log report data access object
     * 
     * @return XxlJobLogReportDao instance
     */
    public XxlJobLogReportDao getXxlJobLogReportDao() {
        return xxlJobLogReportDao;
    }

    /**
     * Get Java mail sender
     * 
     * @return JavaMailSender instance
     */
    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * Get database data source
     * 
     * @return DataSource instance
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Get job alarm service
     * 
     * @return JobAlarmer instance
     */
    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }

}
