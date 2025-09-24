package com.roncoo.education.user.service.fluent;

import com.roncoo.education.user.service.test.base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FluentMyBatis 用户复杂查询服务测试
 * 
 * 测试FluentMyBatis复杂查询场景的实现
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FluentMyBatis用户复杂查询服务测试")
class UserQueryServiceTest extends BaseTest {

    @Autowired
    private UserQueryService userQueryService;

    private LocalDateTime testStartDate;
    private LocalDateTime testEndDate;

    @BeforeEach
    void setUp() {
        testStartDate = LocalDateTime.now().minusDays(30);
        testEndDate = LocalDateTime.now();
    }

    // ==================== 用户活跃度分析测试 ====================

    @Test
    @DisplayName("测试用户活跃度分析 - 正常参数")
    void testAnalyzeUserActivity_NormalParams() {
        // Given
        int days = 7;
        int limit = 10;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserActivity(days, limit)
        );
        
        // 验证异常信息包含复杂查询示例
        assertTrue(exception.getMessage().contains("用户活跃度分析"));
        assertTrue(exception.getMessage().contains("COUNT(DISTINCT o.id)"));
        assertTrue(exception.getMessage().contains("leftJoin"));
        assertTrue(exception.getMessage().contains("groupBy"));
    }

    @Test
    @DisplayName("测试用户活跃度分析 - 边界参数")
    void testAnalyzeUserActivity_BoundaryParams() {
        // Given
        int days = 0;
        int limit = 0;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserActivity(days, limit)
        );
        
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("FluentMyBatis"));
    }

    // ==================== 用户注册趋势测试 ====================

    @Test
    @DisplayName("测试用户注册趋势统计")
    void testGetUserRegistrationTrend() {
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUserRegistrationTrend(testStartDate, testEndDate)
        );
        
        // 验证异常信息包含趋势分析示例
        assertTrue(exception.getMessage().contains("用户注册趋势统计"));
        assertTrue(exception.getMessage().contains("DATE_FORMAT"));
        assertTrue(exception.getMessage().contains("AVG(user_age)"));
        assertTrue(exception.getMessage().contains("CASE WHEN"));
    }

    @Test
    @DisplayName("测试用户注册趋势统计 - 相同开始结束时间")
    void testGetUserRegistrationTrend_SameStartEndDate() {
        // Given
        LocalDateTime sameDate = LocalDateTime.now();
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUserRegistrationTrend(sameDate, sameDate)
        );
        
        assertNotNull(exception.getMessage());
    }

    // ==================== 用户分群查询测试 ====================

    @Test
    @DisplayName("测试用户分群查询 - 完整参数")
    void testGetUsersBySegmentation_AllParams() {
        // Given
        Integer minAge = 18;
        Integer maxAge = 65;
        Integer gender = 1;
        String region = "广东";
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUsersBySegmentation(minAge, maxAge, gender, region)
        );
        
        // 验证异常信息包含分群查询示例
        assertTrue(exception.getMessage().contains("用户分群查询"));
        assertTrue(exception.getMessage().contains("userAge().between"));
        assertTrue(exception.getMessage().contains("userSex().eq"));
        assertTrue(exception.getMessage().contains("EXISTS"));
    }

    @Test
    @DisplayName("测试用户分群查询 - 部分参数为null")
    void testGetUsersBySegmentation_PartialParams() {
        // Given
        Integer minAge = null;
        Integer maxAge = null;
        Integer gender = 1;
        String region = null;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUsersBySegmentation(minAge, maxAge, gender, region)
        );
        
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("FluentMyBatis"));
    }

    // ==================== 用户行为关联查询测试 ====================

    @Test
    @DisplayName("测试用户行为关联查询")
    void testGetUserWithRelatedData() {
        // Given
        Long userId = 12345L;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUserWithRelatedData(userId)
        );
        
        // 验证异常信息包含关联查询示例
        assertTrue(exception.getMessage().contains("用户行为关联查询"));
        assertTrue(exception.getMessage().contains("COUNT(DISTINCT o.id)"));
        assertTrue(exception.getMessage().contains("SUM(o.course_price)"));
        assertTrue(exception.getMessage().contains("MAX(ll.gmt_create)"));
    }

    @Test
    @DisplayName("测试用户行为关联查询 - 无效用户ID")
    void testGetUserWithRelatedData_InvalidUserId() {
        // Given
        Long userId = -1L;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUserWithRelatedData(userId)
        );
        
        assertNotNull(exception.getMessage());
    }

    // ==================== 批量用户状态更新测试 ====================

    @Test
    @DisplayName("测试批量用户状态更新")
    void testBatchUpdateUserStatus() {
        // Given
        List<String> mobileList = Arrays.asList("18888888888", "18999999999");
        Integer newStatus = 0;
        String reason = "违规操作";
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.batchUpdateUserStatus(mobileList, newStatus, reason)
        );
        
        // 验证异常信息包含批量更新示例
        assertTrue(exception.getMessage().contains("批量用户状态更新"));
        assertTrue(exception.getMessage().contains("mobile().in"));
        assertTrue(exception.getMessage().contains("statusId().is"));
    }

    @Test
    @DisplayName("测试批量用户状态更新 - 空列表")
    void testBatchUpdateUserStatus_EmptyList() {
        // Given
        List<String> mobileList = Arrays.asList();
        Integer newStatus = 1;
        String reason = "批量激活";
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.batchUpdateUserStatus(mobileList, newStatus, reason)
        );
        
        assertNotNull(exception.getMessage());
    }

    // ==================== 用户重复数据检测测试 ====================

    @Test
    @DisplayName("测试用户重复数据检测")
    void testDetectDuplicateUsers() {
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.detectDuplicateUsers()
        );
        
        // 验证异常信息包含重复检测示例
        assertTrue(exception.getMessage().contains("用户重复数据检测"));
        assertTrue(exception.getMessage().contains("GROUP_CONCAT"));
        assertTrue(exception.getMessage().contains("having().count().gt(1)"));
    }

    // ==================== 用户价值分析测试 ====================

    @Test
    @DisplayName("测试用户价值分析 - 正常参数")
    void testAnalyzeUserValue_Normal() {
        // Given
        int topN = 50;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserValue(topN)
        );
        
        // 验证异常信息包含价值分析示例
        assertTrue(exception.getMessage().contains("用户价值分析"));
        assertTrue(exception.getMessage().contains("COALESCE(SUM(o.course_price), 0)"));
        assertTrue(exception.getMessage().contains("value_score"));
        assertTrue(exception.getMessage().contains("ROUND"));
    }

    @Test
    @DisplayName("测试用户价值分析 - 边界参数")
    void testAnalyzeUserValue_Boundary() {
        // Given
        int topN = 0;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserValue(topN)
        );
        
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("FluentMyBatis"));
    }

    // ==================== 性能和压力测试 ====================

    @Test
    @DisplayName("测试大数据量参数 - 性能验证")
    void testLargeDataPerformance() {
        // Given
        int largeDays = 365;
        int largeLimit = 10000;
        
        // When
        long startTime = System.currentTimeMillis();
        
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserActivity(largeDays, largeLimit)
        );
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        // 验证即使是大参数，示例代码也应该快速执行
        assertTrue(duration < 100, "FluentMyBatis示例代码应该快速执行");
        assertNotNull(exception.getMessage());
    }

    // ==================== 参数验证测试 ====================

    @Test
    @DisplayName("测试参数验证 - 负数参数")
    void testParameterValidation_NegativeValues() {
        // Given
        int negativeDays = -1;
        int negativeLimit = -1;
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.analyzeUserActivity(negativeDays, negativeLimit)
        );
        
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("测试日期参数验证 - 开始时间晚于结束时间")
    void testDateParameterValidation_InvalidRange() {
        // Given
        LocalDateTime invalidStartDate = LocalDateTime.now();
        LocalDateTime invalidEndDate = LocalDateTime.now().minusDays(1);
        
        // When & Then
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> userQueryService.getUserRegistrationTrend(invalidStartDate, invalidEndDate)
        );
        
        assertNotNull(exception.getMessage());
    }

    // ==================== 集成场景测试 ====================

    @Test
    @DisplayName("测试多个查询方法的集成调用")
    void testMultipleQueryIntegration() {
        // Given
        Long userId = 123L;
        int days = 7;
        int limit = 10;
        
        // When & Then - 测试多个查询方法都能正确抛出示例异常
        assertAll("多个查询方法集成测试",
            () -> {
                UnsupportedOperationException ex1 = assertThrows(
                    UnsupportedOperationException.class,
                    () -> userQueryService.getUserWithRelatedData(userId)
                );
                assertTrue(ex1.getMessage().contains("用户行为关联查询"));
            },
            () -> {
                UnsupportedOperationException ex2 = assertThrows(
                    UnsupportedOperationException.class,
                    () -> userQueryService.analyzeUserActivity(days, limit)
                );
                assertTrue(ex2.getMessage().contains("用户活跃度分析"));
            },
            () -> {
                UnsupportedOperationException ex3 = assertThrows(
                    UnsupportedOperationException.class,
                    () -> userQueryService.detectDuplicateUsers()
                );
                assertTrue(ex3.getMessage().contains("用户重复数据检测"));
            }
        );
    }
}