package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Job Trigger Thread Pool Helper
 * 
 * This helper class manages thread pools for job triggering operations.
 * It implements a dual-pool strategy to handle both fast and slow job executions,
 * providing better resource utilization and system stability.
 * 
 * Features:
 * - Dual thread pool design (fast/slow) for different job types
 * - Automatic pool switching based on job timeout history
 * - Job timeout monitoring and statistics
 * - Thread pool lifecycle management
 * - Singleton pattern for system-wide access
 * 
 * Pool Selection Logic:
 * - Jobs with timeout history > 10 times/minute go to slow pool
 * - Other jobs use the fast pool for better responsiveness
 * 
 * @author xuxueli 2018-07-03 21:08:07
 */
public class JobTriggerPoolHelper {
    /**
     * Logger instance for this helper class
     */
    private static Logger logger = LoggerFactory.getLogger(JobTriggerPoolHelper.class);


    // ---------------------- trigger pool ----------------------

    /**
     * Fast trigger pool for normal job executions
     */
    private ThreadPoolExecutor fastTriggerPool = null;
    
    /**
     * Slow trigger pool for jobs with timeout history
     */
    private ThreadPoolExecutor slowTriggerPool = null;

    /**
     * Start the trigger thread pools
     * 
     * Initializes both fast and slow thread pools with appropriate
     * configurations for different job execution scenarios.
     */
    public void start(){
        // Initialize fast trigger pool
        fastTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode());
                    }
                });

        // Initialize slow trigger pool
        slowTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode());
                    }
                });
    }


    /**
     * Stop all trigger thread pools
     * 
     * Gracefully shuts down both thread pools and logs the completion.
     */
    public void stop() {
        //triggerPool.shutdown();
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
        logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }


    /**
     * Minimum time marker for timeout counting (in minutes)
     */
    private volatile long minTim = System.currentTimeMillis()/60000;     // ms > min
    
    /**
     * Job timeout count map for tracking job performance
     */
    private volatile ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();


    /**
     * Add a job trigger task to the appropriate thread pool
     * 
     * This method selects the appropriate thread pool based on the job's
     * timeout history and submits the trigger task for execution.
     * 
     * @param jobId job identifier
     * @param triggerType type of trigger (manual, cron, etc.)
     * @param failRetryCount number of retry attempts on failure
     * @param executorShardingParam sharding parameters for the executor
     * @param executorParam execution parameters
     * @param addressList specific executor addresses (optional)
     */
    public void addTrigger(final int jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {

        // Choose appropriate thread pool based on timeout history
        ThreadPoolExecutor triggerPool_ = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
        if (jobTimeoutCount!=null && jobTimeoutCount.get() > 10) {      // job-timeout 10 times in 1 min
            triggerPool_ = slowTriggerPool;
        }

        // Submit trigger task to selected pool
        triggerPool_.execute(new Runnable() {
            @Override
            public void run() {

                long start = System.currentTimeMillis();

                try {
                    // Execute job trigger
                    XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {

                    // Clean timeout count map if minute has changed
                    long minTim_now = System.currentTimeMillis()/60000;
                    if (minTim != minTim_now) {
                        minTim = minTim_now;
                        jobTimeoutCountMap.clear();
                    }

                    // Update timeout count if execution took too long
                    long cost = System.currentTimeMillis()-start;
                    if (cost > 500) {       // Job timeout threshold 500ms
                        AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                        if (timeoutCount != null) {
                            timeoutCount.incrementAndGet();
                        }
                    }

                }

            }
        });
    }



    // ---------------------- helper ----------------------

    /**
     * Singleton instance of the trigger pool helper
     */
    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    /**
     * Start the trigger pool helper
     */
    public static void toStart() {
        helper.start();
    }
    
    /**
     * Stop the trigger pool helper
     */
    public static void toStop() {
        helper.stop();
    }

    /**
     * Trigger a job execution
     * 
     * @param jobId job identifier
     * @param triggerType type of trigger
     * @param failRetryCount retry count (>=0: use this param, <0: use job config)
     * @param executorShardingParam sharding parameters
     * @param executorParam execution parameters (null: use job param, not null: override job param)
     * @param addressList specific executor addresses
     */
    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        helper.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }

}
