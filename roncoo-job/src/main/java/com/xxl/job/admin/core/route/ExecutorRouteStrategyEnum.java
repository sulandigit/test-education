package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.route.strategy.*;
import com.xxl.job.admin.core.util.I18nUtil;

/**
 * Executor Route Strategy Enumeration
 * 
 * This enum defines all available routing strategies for job execution
 * across multiple executor instances. Each strategy implements a different
 * algorithm for selecting which executor should handle a particular job.
 * 
 * Available strategies:
 * - FIRST: Always route to the first available executor
 * - LAST: Always route to the last available executor  
 * - ROUND: Round-robin distribution across executors
 * - RANDOM: Random selection of executor
 * - CONSISTENT_HASH: Consistent hash-based routing
 * - LEAST_FREQUENTLY_USED: Route to least frequently used executor
 * - LEAST_RECENTLY_USED: Route to least recently used executor
 * - FAILOVER: Failover to next executor on failure
 * - BUSYOVER: Route to least busy executor
 * - SHARDING_BROADCAST: Broadcast to all executors for sharding
 * 
 * @author xuxueli on 17/3/10
 */
public enum ExecutorRouteStrategyEnum {

    /**
     * Route to the first available executor
     */
    FIRST(I18nUtil.getString("jobconf_route_first"), new ExecutorRouteFirst()),
    
    /**
     * Route to the last available executor
     */
    LAST(I18nUtil.getString("jobconf_route_last"), new ExecutorRouteLast()),
    
    /**
     * Round-robin routing across all executors
     */
    ROUND(I18nUtil.getString("jobconf_route_round"), new ExecutorRouteRound()),
    
    /**
     * Random executor selection
     */
    RANDOM(I18nUtil.getString("jobconf_route_random"), new ExecutorRouteRandom()),
    
    /**
     * Consistent hash-based routing for stable distribution
     */
    CONSISTENT_HASH(I18nUtil.getString("jobconf_route_consistenthash"), new ExecutorRouteConsistentHash()),
    
    /**
     * Route to least frequently used executor
     */
    LEAST_FREQUENTLY_USED(I18nUtil.getString("jobconf_route_lfu"), new ExecutorRouteLFU()),
    
    /**
     * Route to least recently used executor
     */
    LEAST_RECENTLY_USED(I18nUtil.getString("jobconf_route_lru"), new ExecutorRouteLRU()),
    
    /**
     * Failover routing - switch to next executor on failure
     */
    FAILOVER(I18nUtil.getString("jobconf_route_failover"), new ExecutorRouteFailover()),
    
    /**
     * Route to least busy executor based on active job count
     */
    BUSYOVER(I18nUtil.getString("jobconf_route_busyover"), new ExecutorRouteBusyover()),
    
    /**
     * Broadcast to all executors for sharding scenarios
     */
    SHARDING_BROADCAST(I18nUtil.getString("jobconf_route_shard"), null);

    /**
     * Constructor for route strategy enum
     * 
     * @param title display title for this strategy
     * @param router router implementation instance
     */
    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    /**
     * Display title for this routing strategy
     */
    private String title;
    
    /**
     * Router implementation instance
     */
    private ExecutorRouter router;

    /**
     * Get the display title for this routing strategy
     * 
     * @return localized title string
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Get the router implementation for this strategy
     * 
     * @return router instance or null for special strategies
     */
    public ExecutorRouter getRouter() {
        return router;
    }

    /**
     * Match a route strategy by name with fallback to default
     * 
     * @param name strategy name to match
     * @param defaultItem default strategy if no match found
     * @return matched strategy or default item
     */
    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
