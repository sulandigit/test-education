package com.roncoo.education.common.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存性能测试
 * 测试多级缓存在高并发场景下的性能表现
 *
 * @author wujing
 */
@ExtendWith(MockitoExtension.class)
class CachePerformanceTest {

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    private CaffeineConfiguration caffeineConfiguration;
    private MultiLevelCacheManager cacheManager;
    private Cache cache;

    @BeforeEach
    void setUp() {
        caffeineConfiguration = new CaffeineConfiguration();
        caffeineConfiguration.setMaximumSize(10000L);
        caffeineConfiguration.setExpireAfterWrite(30L);
        
        cacheManager = new MultiLevelCacheManager(caffeineConfiguration, redisConnectionFactory);
        cache = cacheManager.getCache("performance-test");
    }

    @Test
    void testSingleThreadPerformance() {
        // Given
        int iterations = 10000;
        String keyPrefix = "key";
        String valuePrefix = "value";

        // When - 写入性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            cache.put(keyPrefix + i, valuePrefix + i);
        }
        long writeTime = System.currentTimeMillis() - startTime;

        // When - 读取性能测试
        startTime = System.currentTimeMillis();
        int hits = 0;
        for (int i = 0; i < iterations; i++) {
            Cache.ValueWrapper value = cache.get(keyPrefix + i);
            if (value != null) {
                hits++;
            }
        }
        long readTime = System.currentTimeMillis() - startTime;

        // Then
        assertTrue(writeTime < 5000, "写入性能应该在5秒内完成");
        assertTrue(readTime < 1000, "读取性能应该在1秒内完成");
        assertEquals(iterations, hits, "所有缓存项都应该被命中");

        System.out.printf("单线程性能测试结果:\n");
        System.out.printf("写入%d条记录耗时: %dms (平均%.2fms/条)\n", 
                iterations, writeTime, (double) writeTime / iterations);
        System.out.printf("读取%d条记录耗时: %dms (平均%.2fms/条)\n", 
                iterations, readTime, (double) readTime / iterations);
        System.out.printf("缓存命中率: %.2f%%\n", (double) hits / iterations * 100);
    }

    @Test
    void testConcurrentWritePerformance() throws InterruptedException {
        // Given
        int threadCount = 10;
        int iterationsPerThread = 1000;
        int totalIterations = threadCount * iterationsPerThread;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // When
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        String key = "thread" + threadId + "_key" + i;
                        String value = "thread" + threadId + "_value" + i;
                        cache.put(key, value);
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();

        // Then
        assertTrue(completed, "并发写入应该在30秒内完成");
        assertEquals(totalIterations, successCount.get(), "所有写入操作都应该成功");
        assertTrue(totalTime < 10000, "并发写入性能应该在10秒内完成");

        System.out.printf("并发写入性能测试结果:\n");
        System.out.printf("%d个线程并发写入%d条记录耗时: %dms\n", 
                threadCount, totalIterations, totalTime);
        System.out.printf("平均吞吐量: %.2f 操作/秒\n", 
                (double) totalIterations / totalTime * 1000);
    }

    @Test
    void testConcurrentReadPerformance() throws InterruptedException {
        // Given
        int dataSize = 1000;
        int threadCount = 10;
        int readsPerThread = 1000;
        
        // 预填充数据
        for (int i = 0; i < dataSize; i++) {
            cache.put("data" + i, "value" + i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger hitCount = new AtomicInteger(0);
        AtomicInteger totalReads = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // When
        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < readsPerThread; i++) {
                        String key = "data" + (i % dataSize);
                        Cache.ValueWrapper value = cache.get(key);
                        totalReads.incrementAndGet();
                        if (value != null) {
                            hitCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();

        // Then
        assertTrue(completed, "并发读取应该在30秒内完成");
        assertTrue(totalTime < 5000, "并发读取性能应该在5秒内完成");
        
        double hitRate = (double) hitCount.get() / totalReads.get();
        assertTrue(hitRate > 0.95, "缓存命中率应该大于95%");

        System.out.printf("并发读取性能测试结果:\n");
        System.out.printf("%d个线程并发读取%d次耗时: %dms\n", 
                threadCount, totalReads.get(), totalTime);
        System.out.printf("缓存命中率: %.2f%%\n", hitRate * 100);
        System.out.printf("平均吞吐量: %.2f 操作/秒\n", 
                (double) totalReads.get() / totalTime * 1000);
    }

    @Test
    void testMixedWorkloadPerformance() throws InterruptedException {
        // Given
        int threadCount = 20;
        int operationsPerThread = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);
        AtomicInteger hitCount = new AtomicInteger(0);

        // 预填充一些数据
        for (int i = 0; i < 100; i++) {
            cache.put("init" + i, "value" + i);
        }

        long startTime = System.currentTimeMillis();

        // When - 混合读写操作
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        if (i % 3 == 0) {
                            // 写操作 (33%)
                            String key = "mixed_" + threadId + "_" + i;
                            String value = "value_" + threadId + "_" + i;
                            cache.put(key, value);
                            writeCount.incrementAndGet();
                        } else {
                            // 读操作 (67%)
                            String key = (i % 2 == 0) ? "init" + (i % 100) : "mixed_" + threadId + "_" + (i - 1);
                            Cache.ValueWrapper result = cache.get(key);
                            readCount.incrementAndGet();
                            if (result != null) {
                                hitCount.incrementAndGet();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();

        // Then
        assertTrue(completed, "混合负载测试应该在60秒内完成");
        assertTrue(totalTime < 20000, "混合负载性能应该在20秒内完成");

        int totalOperations = readCount.get() + writeCount.get();
        double hitRate = (double) hitCount.get() / readCount.get();

        System.out.printf("混合负载性能测试结果:\n");
        System.out.printf("总操作数: %d (读: %d, 写: %d)\n", 
                totalOperations, readCount.get(), writeCount.get());
        System.out.printf("总耗时: %dms\n", totalTime);
        System.out.printf("缓存命中率: %.2f%%\n", hitRate * 100);
        System.out.printf("平均吞吐量: %.2f 操作/秒\n", 
                (double) totalOperations / totalTime * 1000);
    }

    @Test
    void testMemoryUsage() {
        // Given
        int largeDataSize = 50000;
        Runtime runtime = Runtime.getRuntime();

        // 记录初始内存使用
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // When - 写入大量数据
        for (int i = 0; i < largeDataSize; i++) {
            String key = "memory_test_" + i;
            String value = "large_value_" + i + "_" + "x".repeat(100); // 增加值的大小
            cache.put(key, value);
        }

        // 记录最终内存使用
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        // Then
        System.out.printf("内存使用测试结果:\n");
        System.out.printf("存储%d条记录的内存使用: %.2f MB\n", 
                largeDataSize, (double) memoryUsed / (1024 * 1024));
        System.out.printf("平均每条记录内存使用: %.2f KB\n", 
                (double) memoryUsed / largeDataSize / 1024);

        // 验证内存使用在合理范围内（这里只是示例，实际阈值需要根据业务需求调整）
        assertTrue(memoryUsed < 500 * 1024 * 1024, "内存使用应该小于500MB");
    }
}