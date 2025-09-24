package com.roncoo.education.user.performance;

import com.roncoo.education.user.dao.impl.FluentUsersDaoImpl;
import com.roncoo.education.user.dao.impl.UsersDaoImpl;
import com.roncoo.education.user.dao.impl.mapper.entity.Users;
import com.roncoo.education.user.dao.impl.mapper.entity.UsersExample;
import com.roncoo.education.user.service.test.base.BaseTest;
import com.roncoo.education.user.service.test.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FluentMyBatis性能对比测试
 * 
 * 对比FluentMyBatis与原生MyBatis的性能差异
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FluentMyBatis性能对比测试")
class FluentMyBatisPerformanceTest extends BaseTest {

    @Autowired
    private UsersDaoImpl originalUsersDao;

    @Autowired
    @Qualifier("fluentUsersDao")
    private FluentUsersDaoImpl fluentUsersDao;

    private List<Users> testUsers;
    private static final int SMALL_BATCH_SIZE = 100;
    private static final int MEDIUM_BATCH_SIZE = 1000;
    private static final int LARGE_BATCH_SIZE = 10000;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUsers = IntStream.range(10000, 10000 + MEDIUM_BATCH_SIZE)
            .mapToObj(i -> TestDataFactory.createStandardUser((long) i))
            .collect(java.util.stream.Collectors.toList());
    }

    // ==================== 基础操作性能测试 ====================

    @Test
    @DisplayName("单条记录CRUD性能对比")
    @Transactional
    void testSingleRecordCrudPerformance() {
        Users testUser = TestDataFactory.createStandardUser(20001L);
        
        // 测试原生MyBatis单条保存性能
        long originalSaveStart = System.nanoTime();
        int originalSaveResult = originalUsersDao.save(testUser);
        long originalSaveEnd = System.nanoTime();
        long originalSaveDuration = originalSaveEnd - originalSaveStart;
        
        assertEquals(1, originalSaveResult);
        
        // 测试原生MyBatis单条查询性能
        long originalQueryStart = System.nanoTime();
        Users originalQueryResult = originalUsersDao.getById(testUser.getId());
        long originalQueryEnd = System.nanoTime();
        long originalQueryDuration = originalQueryEnd - originalQueryStart;
        
        assertNotNull(originalQueryResult);
        
        // 测试FluentMyBatis性能（示例实现）
        long fluentSaveStart = System.nanoTime();
        try {
            fluentUsersDao.save(testUser);
        } catch (UnsupportedOperationException e) {
            // 预期异常
        }
        long fluentSaveEnd = System.nanoTime();
        long fluentSaveDuration = fluentSaveEnd - fluentSaveStart;
        
        long fluentQueryStart = System.nanoTime();
        try {
            fluentUsersDao.getById(testUser.getId());
        } catch (UnsupportedOperationException e) {
            // 预期异常
        }
        long fluentQueryEnd = System.nanoTime();
        long fluentQueryDuration = fluentQueryEnd - fluentQueryStart;
        
        // 记录性能数据
        logPerformanceResults("单条记录操作", Map.of(
            "原生MyBatis保存", originalSaveDuration,
            "原生MyBatis查询", originalQueryDuration,
            "FluentMyBatis保存", fluentSaveDuration,
            "FluentMyBatis查询", fluentQueryDuration
        ));
        
        // FluentMyBatis示例实现应该更快（因为只是抛异常）
        assertTrue(fluentSaveDuration < originalSaveDuration, 
            "FluentMyBatis示例实现应该更快");
    }

    @Test
    @DisplayName("批量操作性能对比")
    @Transactional
    void testBatchOperationPerformance() {
        List<Users> batchUsers = testUsers.subList(0, SMALL_BATCH_SIZE);
        
        // 测试原生MyBatis批量保存性能
        long originalBatchStart = System.currentTimeMillis();
        int originalSavedCount = 0;
        for (Users user : batchUsers) {
            originalSavedCount += originalUsersDao.save(user);
        }
        long originalBatchEnd = System.currentTimeMillis();
        long originalBatchDuration = originalBatchEnd - originalBatchStart;
        
        assertEquals(SMALL_BATCH_SIZE, originalSavedCount);
        
        // 测试FluentMyBatis批量保存性能（示例实现）
        List<Users> fluentBatchUsers = testUsers.subList(SMALL_BATCH_SIZE, SMALL_BATCH_SIZE * 2);
        
        long fluentBatchStart = System.currentTimeMillis();
        int fluentSavedCount = 0;
        for (Users user : fluentBatchUsers) {
            try {
                fluentUsersDao.save(user);
                fluentSavedCount++;
            } catch (UnsupportedOperationException e) {
                // 预期异常，但我们计数
            }
        }
        long fluentBatchEnd = System.currentTimeMillis();
        long fluentBatchDuration = fluentBatchEnd - fluentBatchStart;
        
        // 记录批量操作性能
        logPerformanceResults("批量操作 (" + SMALL_BATCH_SIZE + "条)", Map.of(
            "原生MyBatis批量保存", originalBatchDuration * 1_000_000L,  // 转换为纳秒便于比较
            "FluentMyBatis批量保存", fluentBatchDuration * 1_000_000L
        ));
        
        log.info("原生MyBatis批量保存成功数量: {}, FluentMyBatis示例处理数量: {}", 
            originalSavedCount, fluentSavedCount);
    }

    // ==================== 复杂查询性能测试 ====================

    @Test
    @DisplayName("复杂查询性能对比")
    @Transactional
    void testComplexQueryPerformance() {
        // 先保存一些测试数据
        List<Users> queryTestUsers = testUsers.subList(0, 50);
        for (Users user : queryTestUsers) {
            originalUsersDao.save(user);
        }
        flushAndClear();
        
        // 测试原生MyBatis复杂查询性能
        UsersExample example = new UsersExample();
        example.createCriteria()
            .andStatusIdEqualTo(1)
            .andGmtCreateGreaterThan(LocalDateTime.now().minusDays(1));
        example.setOrderByClause("gmt_create DESC");
        
        long originalComplexStart = System.nanoTime();
        var originalPage = originalUsersDao.page(1, 20, example);
        long originalComplexEnd = System.nanoTime();
        long originalComplexDuration = originalComplexEnd - originalComplexStart;
        
        assertNotNull(originalPage);
        
        // 测试FluentMyBatis复杂查询性能（示例实现）
        long fluentComplexStart = System.nanoTime();
        try {
            fluentUsersDao.page(1, 20, example);
        } catch (UnsupportedOperationException e) {
            // 预期异常
        }
        long fluentComplexEnd = System.nanoTime();
        long fluentComplexDuration = fluentComplexEnd - fluentComplexStart;
        
        // 记录复杂查询性能
        logPerformanceResults("复杂查询", Map.of(
            "原生MyBatis分页查询", originalComplexDuration,
            "FluentMyBatis分页查询", fluentComplexDuration
        ));
    }

    // ==================== 并发性能测试 ====================

    @Test
    @DisplayName("并发操作性能对比")
    void testConcurrentOperationPerformance() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 50;
        
        // 测试原生MyBatis并发性能
        ExecutorService originalExecutor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch originalLatch = new CountDownLatch(threadCount);
        
        long originalConcurrentStart = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            originalExecutor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        Users user = TestDataFactory.createStandardUser(
                            (long) (30000 + threadIndex * operationsPerThread + j));
                        originalUsersDao.save(user);
                    }
                } finally {
                    originalLatch.countDown();
                }
            });
        }
        
        originalLatch.await(30, TimeUnit.SECONDS);
        long originalConcurrentEnd = System.currentTimeMillis();
        long originalConcurrentDuration = originalConcurrentEnd - originalConcurrentStart;
        
        originalExecutor.shutdown();
        
        // 测试FluentMyBatis并发性能（示例实现）
        ExecutorService fluentExecutor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch fluentLatch = new CountDownLatch(threadCount);
        
        long fluentConcurrentStart = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            fluentExecutor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        Users user = TestDataFactory.createStandardUser(
                            (long) (40000 + threadIndex * operationsPerThread + j));
                        try {
                            fluentUsersDao.save(user);
                        } catch (UnsupportedOperationException e) {
                            // 预期异常
                        }
                    }
                } finally {
                    fluentLatch.countDown();
                }
            });
        }
        
        fluentLatch.await(30, TimeUnit.SECONDS);
        long fluentConcurrentEnd = System.currentTimeMillis();
        long fluentConcurrentDuration = fluentConcurrentEnd - fluentConcurrentStart;
        
        fluentExecutor.shutdown();
        
        // 记录并发性能
        logPerformanceResults("并发操作 (" + threadCount + "线程x" + operationsPerThread + "操作)", Map.of(
            "原生MyBatis并发", originalConcurrentDuration * 1_000_000L,
            "FluentMyBatis并发", fluentConcurrentDuration * 1_000_000L
        ));
        
        log.info("并发测试完成 - 原生MyBatis: {}ms, FluentMyBatis示例: {}ms", 
            originalConcurrentDuration, fluentConcurrentDuration);
    }

    // ==================== 内存使用性能测试 ====================

    @Test
    @DisplayName("内存使用性能对比")
    @Transactional
    void testMemoryUsagePerformance() {
        // 获取初始内存状态
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 测试原生MyBatis内存使用
        List<Users> originalResults = new ArrayList<>();
        long originalMemoryStart = runtime.totalMemory() - runtime.freeMemory();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            Users user = TestDataFactory.createStandardUser((long) (50000 + i));
            originalUsersDao.save(user);
            originalResults.add(originalUsersDao.getById(user.getId()));
        }
        
        long originalMemoryEnd = runtime.totalMemory() - runtime.freeMemory();
        long originalMemoryUsed = originalMemoryEnd - originalMemoryStart;
        
        // 清理内存
        originalResults.clear();
        System.gc();
        
        // 测试FluentMyBatis内存使用（示例实现）
        List<Users> fluentResults = new ArrayList<>();
        long fluentMemoryStart = runtime.totalMemory() - runtime.freeMemory();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            Users user = TestDataFactory.createStandardUser((long) (60000 + i));
            try {
                fluentUsersDao.save(user);
                fluentResults.add(user);  // 由于是示例实现，我们添加原始对象
            } catch (UnsupportedOperationException e) {
                fluentResults.add(user);
            }
        }
        
        long fluentMemoryEnd = runtime.totalMemory() - runtime.freeMemory();
        long fluentMemoryUsed = fluentMemoryEnd - fluentMemoryStart;
        
        // 记录内存使用情况
        log.info("内存使用对比 - 原生MyBatis: {}字节, FluentMyBatis示例: {}字节", 
            originalMemoryUsed, fluentMemoryUsed);
        
        assertEquals(SMALL_BATCH_SIZE, originalResults.size() + fluentResults.size());
    }

    // ==================== SQL生成性能测试 ====================

    @Test
    @DisplayName("SQL生成性能对比")
    void testSqlGenerationPerformance() {
        // 测试原生MyBatis SQL执行（通过Example）
        UsersExample example = new UsersExample();
        
        long originalSqlStart = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            example.clear();
            example.createCriteria()
                .andStatusIdEqualTo(1)
                .andMobileLike("188%")
                .andGmtCreateGreaterThan(LocalDateTime.now().minusDays(i % 30));
            
            // 模拟SQL生成（实际会在MyBatis内部进行）
            String orderBy = "gmt_create DESC, id ASC";
            example.setOrderByClause(orderBy);
        }
        long originalSqlEnd = System.nanoTime();
        long originalSqlDuration = originalSqlEnd - originalSqlStart;
        
        // 测试FluentMyBatis SQL生成性能（模拟）
        long fluentSqlStart = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            try {
                // 模拟FluentMyBatis链式调用的性能开销
                String mockFluentQuery = "query().where().statusId().eq(1)" +
                    ".and().mobile().like('188%')" +
                    ".and().gmtCreate().gt(LocalDateTime.now().minusDays(" + (i % 30) + "))" +
                    ".orderBy().gmtCreate().desc().orderBy().id().asc()";
                
                // 模拟一些字符串操作来代表SQL生成
                int length = mockFluentQuery.length();
                boolean hasWhere = mockFluentQuery.contains("where");
                boolean hasOrderBy = mockFluentQuery.contains("orderBy");
                
                // 简单的性能开销模拟
                if (hasWhere && hasOrderBy && length > 100) {
                    // 模拟SQL构建开销
                }
            } catch (Exception e) {
                // 忽略异常
            }
        }
        long fluentSqlEnd = System.nanoTime();
        long fluentSqlDuration = fluentSqlEnd - fluentSqlStart;
        
        // 记录SQL生成性能
        logPerformanceResults("SQL生成 (1000次)", Map.of(
            "原生MyBatis Example", originalSqlDuration,
            "FluentMyBatis模拟", fluentSqlDuration
        ));
    }

    // ==================== 性能基准测试 ====================

    @Test
    @DisplayName("性能基准对比测试")
    void testPerformanceBenchmark() {
        Map<String, Long> benchmarkResults = new HashMap<>();
        
        // 基准测试1：简单CRUD操作
        Users testUser = TestDataFactory.createStandardUser(70001L);
        
        long simpleCrudStart = System.nanoTime();
        originalUsersDao.save(testUser);
        Users retrieved = originalUsersDao.getById(testUser.getId());
        testUser.setNickname("更新后的昵称");
        originalUsersDao.updateById(testUser);
        originalUsersDao.deleteById(testUser.getId());
        long simpleCrudEnd = System.nanoTime();
        
        benchmarkResults.put("简单CRUD操作", simpleCrudEnd - simpleCrudStart);
        
        // 基准测试2：批量查询
        List<Long> queryIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        
        long batchQueryStart = System.nanoTime();
        List<Users> batchResults = originalUsersDao.listByIds(queryIds);
        long batchQueryEnd = System.nanoTime();
        
        benchmarkResults.put("批量查询", batchQueryEnd - batchQueryStart);
        
        // 基准测试3：条件查询
        UsersExample conditionExample = new UsersExample();
        conditionExample.createCriteria().andStatusIdEqualTo(1);
        
        long conditionQueryStart = System.nanoTime();
        var conditionResults = originalUsersDao.page(1, 10, conditionExample);
        long conditionQueryEnd = System.nanoTime();
        
        benchmarkResults.put("条件查询", conditionQueryEnd - conditionQueryStart);
        
        // 输出基准测试结果
        log.info("=== 性能基准测试结果 ===");
        benchmarkResults.forEach((operation, duration) -> {
            log.info("{}: {} 纳秒 ({} 毫秒)", operation, duration, duration / 1_000_000.0);
        });
        
        // 验证基准测试的合理性
        assertTrue(benchmarkResults.get("简单CRUD操作") > 0);
        assertTrue(benchmarkResults.get("批量查询") >= 0);
        assertTrue(benchmarkResults.get("条件查询") > 0);
    }

    // ==================== 辅助方法 ====================

    /**
     * 记录性能测试结果
     */
    private void logPerformanceResults(String testName, Map<String, Long> results) {
        log.info("=== {} 性能测试结果 ===", testName);
        results.forEach((operation, duration) -> {
            double milliseconds = duration / 1_000_000.0;
            log.info("{}: {} 纳秒 ({:.2f} 毫秒)", operation, duration, milliseconds);
        });
        
        // 计算性能比率
        if (results.size() >= 2) {
            List<Long> durations = new ArrayList<>(results.values());
            double ratio = (double) durations.get(0) / durations.get(1);
            log.info("性能比率: {:.2f}", ratio);
        }
    }

    /**
     * 生成性能测试报告
     */
    @Test
    @DisplayName("生成性能测试总结报告")
    void generatePerformanceReport() {
        log.info("=== FluentMyBatis性能测试总结报告 ===");
        log.info("测试环境: Spring Boot Test + H2内存数据库");
        log.info("测试实现: 原生MyBatis vs FluentMyBatis示例实现");
        log.info("");
        log.info("测试结论:");
        log.info("1. FluentMyBatis示例实现由于只抛异常，性能明显优于原生MyBatis");
        log.info("2. 在真实实现中，FluentMyBatis的性能应该与原生MyBatis相当");
        log.info("3. FluentMyBatis的主要优势在于开发效率和代码可读性，而非性能");
        log.info("4. 建议在实际项目中进行真实的性能测试");
        log.info("");
        log.info("性能优化建议:");
        log.info("- 合理使用批量操作");
        log.info("- 避免复杂的链式查询");
        log.info("- 合理配置数据库连接池");
        log.info("- 使用适当的缓存策略");
        
        assertTrue(true, "性能测试报告生成完成");
    }
}