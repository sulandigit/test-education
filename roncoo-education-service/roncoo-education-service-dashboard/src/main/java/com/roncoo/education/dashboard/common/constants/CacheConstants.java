package com.roncoo.education.dashboard.common.constants;

/**
 * 缓存相关常量
 *
 * @author wujing
 * @date 2025-09-20
 */
public class CacheConstants {

    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX = "dashboard:";

    /**
     * 指标缓存前缀
     */
    public static final String METRIC_PREFIX = CACHE_PREFIX + "metric:";

    /**
     * 热点数据缓存前缀
     */
    public static final String HOT_DATA_PREFIX = CACHE_PREFIX + "hot:";

    /**
     * 列表数据缓存前缀
     */
    public static final String LIST_PREFIX = CACHE_PREFIX + "list:";

    /**
     * 实时数据缓存键
     */
    public static final String REALTIME_DATA_KEY = "realtime_data";

    /**
     * 趋势数据缓存键前缀
     */
    public static final String TREND_DATA_PREFIX = "trend_data:";

    /**
     * 缓存过期时间（秒）
     */
    public static class ExpireTime {
        /**
         * 实时数据过期时间：5秒
         */
        public static final int REALTIME_DATA = 5;

        /**
         * 热点数据过期时间：10分钟
         */
        public static final int HOT_DATA = 600;

        /**
         * 列表数据过期时间：5分钟
         */
        public static final int LIST_DATA = 300;

        /**
         * 趋势数据过期时间：30分钟
         */
        public static final int TREND_DATA = 1800;

        /**
         * 用户统计过期时间：1分钟
         */
        public static final int USER_STATS = 60;

        /**
         * 课程统计过期时间：5分钟
         */
        public static final int COURSE_STATS = 300;

        /**
         * 订单统计过期时间：1分钟
         */
        public static final int ORDER_STATS = 60;

        /**
         * 系统状态过期时间：30秒
         */
        public static final int SYSTEM_STATUS = 30;
    }

    /**
     * 缓存键名称
     */
    public static class CacheKey {
        /**
         * 在线用户数
         */
        public static final String ONLINE_USERS = "online_users";

        /**
         * 今日新增用户数
         */
        public static final String DAILY_NEW_USERS = "daily_new_users";

        /**
         * 用户总数
         */
        public static final String TOTAL_USERS = "total_users";

        /**
         * 热门课程
         */
        public static final String HOT_COURSES = "hot_courses";

        /**
         * 今日订单数
         */
        public static final String DAILY_ORDERS = "daily_orders";

        /**
         * 今日收入
         */
        public static final String DAILY_REVENUE = "daily_revenue";

        /**
         * 本月收入
         */
        public static final String MONTHLY_REVENUE = "monthly_revenue";

        /**
         * 支付方式分布
         */
        public static final String PAYMENT_TYPES = "payment_types";

        /**
         * 用户地域分布
         */
        public static final String USER_LOCATIONS = "user_locations";

        /**
         * 系统状态
         */
        public static final String SYSTEM_STATUS = "system_status";

        /**
         * 今日视频播放次数
         */
        public static final String DAILY_VIDEO_VIEWS = "daily_video_views";
    }

}