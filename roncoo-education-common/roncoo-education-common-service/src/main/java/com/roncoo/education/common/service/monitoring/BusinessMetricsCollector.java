package com.roncoo.education.common.service.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 领课教育业务指标收集器
 * 提供统一的业务指标记录和管理功能
 * 
 * @author 领课教育
 */
@Component
public class BusinessMetricsCollector {

    @Autowired
    private MeterRegistry meterRegistry;

    // 用户相关指标
    private Counter userRegisterCounter;
    private Counter userLoginCounter;
    private AtomicInteger activeUserGauge = new AtomicInteger(0);

    // 课程相关指标
    private Counter coursePlayCounter;
    private Counter coursePlayErrorCounter;
    private Timer courseDurationTimer;
    private Counter coursePurchaseCounter;

    // 订单相关指标
    private Counter orderCreateCounter;
    private Counter orderSuccessCounter;
    private Counter paymentCounter;
    private Counter paymentSuccessCounter;
    private Counter paymentErrorCounter;

    // 系统相关指标
    private Counter apiRequestCounter;
    private Counter apiErrorCounter;
    private Timer apiResponseTimer;

    @PostConstruct
    public void initMetrics() {
        // 初始化用户相关指标
        initUserMetrics();
        
        // 初始化课程相关指标
        initCourseMetrics();
        
        // 初始化订单相关指标
        initOrderMetrics();
        
        // 初始化系统相关指标
        initSystemMetrics();
    }

    /**
     * 初始化用户相关指标
     */
    private void initUserMetrics() {
        this.userRegisterCounter = Counter.builder("education_user_register_total")
                .description("用户注册总数")
                .tag("service", "user")
                .register(meterRegistry);

        this.userLoginCounter = Counter.builder("education_user_login_total")
                .description("用户登录总数")
                .tag("service", "user")
                .register(meterRegistry);

        Gauge.builder("education_user_active_count")
                .description("当前活跃用户数")
                .tag("service", "user")
                .register(meterRegistry, this, BusinessMetricsCollector::getActiveUserCount);
    }

    /**
     * 初始化课程相关指标
     */
    private void initCourseMetrics() {
        this.coursePlayCounter = Counter.builder("education_course_play_total")
                .description("课程播放总次数")
                .tag("service", "course")
                .register(meterRegistry);

        this.coursePlayErrorCounter = Counter.builder("education_course_play_error_total")
                .description("课程播放错误总次数")
                .tag("service", "course")
                .register(meterRegistry);

        this.courseDurationTimer = Timer.builder("education_course_duration_seconds")
                .description("课程学习时长分布")
                .tag("service", "course")
                .register(meterRegistry);

        this.coursePurchaseCounter = Counter.builder("education_course_purchase_total")
                .description("课程购买总数")
                .tag("service", "course")
                .register(meterRegistry);
    }

    /**
     * 初始化订单相关指标
     */
    private void initOrderMetrics() {
        this.orderCreateCounter = Counter.builder("education_order_create_total")
                .description("订单创建总数")
                .tag("service", "order")
                .register(meterRegistry);

        this.orderSuccessCounter = Counter.builder("education_order_success_total")
                .description("订单成功总数")
                .tag("service", "order")
                .register(meterRegistry);

        this.paymentCounter = Counter.builder("education_payment_total")
                .description("支付请求总数")
                .tag("service", "payment")
                .register(meterRegistry);

        this.paymentSuccessCounter = Counter.builder("education_payment_success_total")
                .description("支付成功总数")
                .tag("service", "payment")
                .register(meterRegistry);

        this.paymentErrorCounter = Counter.builder("education_payment_error_total")
                .description("支付失败总数")
                .tag("service", "payment")
                .register(meterRegistry);
    }

    /**
     * 初始化系统相关指标
     */
    private void initSystemMetrics() {
        this.apiRequestCounter = Counter.builder("education_api_request_total")
                .description("API请求总数")
                .register(meterRegistry);

        this.apiErrorCounter = Counter.builder("education_api_error_total")
                .description("API错误总数")
                .register(meterRegistry);

        this.apiResponseTimer = Timer.builder("education_api_response_seconds")
                .description("API响应时间分布")
                .register(meterRegistry);
    }

    // 用户指标记录方法
    public void recordUserRegister() {
        userRegisterCounter.increment();
    }

    public void recordUserRegister(String source) {
        Counter.builder("education_user_register_total")
                .tag("source", source)
                .register(meterRegistry)
                .increment();
    }

    public void recordUserLogin() {
        userLoginCounter.increment();
    }

    public void recordUserLogin(String loginType) {
        Counter.builder("education_user_login_total")
                .tag("type", loginType)
                .register(meterRegistry)
                .increment();
    }

    public void setActiveUserCount(int count) {
        activeUserGauge.set(count);
    }

    public int getActiveUserCount() {
        return activeUserGauge.get();
    }

    // 课程指标记录方法
    public void recordCoursePlay() {
        coursePlayCounter.increment();
    }

    public void recordCoursePlay(String courseType) {
        Counter.builder("education_course_play_total")
                .tag("course_type", courseType)
                .register(meterRegistry)
                .increment();
    }

    public void recordCoursePlayError() {
        coursePlayErrorCounter.increment();
    }

    public void recordCoursePlayError(String errorType) {
        Counter.builder("education_course_play_error_total")
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
    }

    public Timer.Sample startCourseTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordCourseDuration(Timer.Sample sample) {
        sample.stop(courseDurationTimer);
    }

    public void recordCoursePurchase() {
        coursePurchaseCounter.increment();
    }

    public void recordCoursePurchase(String courseCategory) {
        Counter.builder("education_course_purchase_total")
                .tag("category", courseCategory)
                .register(meterRegistry)
                .increment();
    }

    // 订单指标记录方法
    public void recordOrderCreate() {
        orderCreateCounter.increment();
    }

    public void recordOrderCreate(String orderType) {
        Counter.builder("education_order_create_total")
                .tag("type", orderType)
                .register(meterRegistry)
                .increment();
    }

    public void recordOrderSuccess() {
        orderSuccessCounter.increment();
    }

    public void recordPayment() {
        paymentCounter.increment();
    }

    public void recordPaymentSuccess() {
        paymentSuccessCounter.increment();
    }

    public void recordPaymentSuccess(String paymentMethod) {
        Counter.builder("education_payment_success_total")
                .tag("method", paymentMethod)
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentError() {
        paymentErrorCounter.increment();
    }

    public void recordPaymentError(String errorCode) {
        Counter.builder("education_payment_error_total")
                .tag("error_code", errorCode)
                .register(meterRegistry)
                .increment();
    }

    // API指标记录方法
    public void recordApiRequest() {
        apiRequestCounter.increment();
    }

    public void recordApiRequest(String endpoint, String method) {
        Counter.builder("education_api_request_total")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .increment();
    }

    public void recordApiError() {
        apiErrorCounter.increment();
    }

    public void recordApiError(String endpoint, String errorCode) {
        Counter.builder("education_api_error_total")
                .tag("endpoint", endpoint)
                .tag("error_code", errorCode)
                .register(meterRegistry)
                .increment();
    }

    public Timer.Sample startApiTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordApiResponse(Timer.Sample sample, String endpoint) {
        sample.stop(Timer.builder("education_api_response_seconds")
                .tag("endpoint", endpoint)
                .register(meterRegistry));
    }
}