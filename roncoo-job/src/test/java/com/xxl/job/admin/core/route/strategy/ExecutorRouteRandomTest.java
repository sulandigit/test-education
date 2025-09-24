package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExecutorRouteRandom 路由策略单元测试
 *
 * @author Test Generator
 */
public class ExecutorRouteRandomTest {

    private ExecutorRouteRandom routeRandom;
    private TriggerParam triggerParam;

    @BeforeEach
    void setUp() {
        routeRandom = new ExecutorRouteRandom();
        triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
    }

    @Test
    void testRoute_singleAddress() {
        // 测试单个地址
        List<String> addressList = Collections.singletonList("127.0.0.1:9999");
        
        ReturnT<String> result = routeRandom.route(triggerParam, addressList);
        
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertEquals("127.0.0.1:9999", result.getContent());
    }

    @Test
    void testRoute_multipleAddresses() {
        // 测试多个地址
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        ReturnT<String> result = routeRandom.route(triggerParam, addressList);
        
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertTrue(addressList.contains(result.getContent()));
    }

    @Test
    void testRoute_randomness() {
        // 测试随机性 - 多次调用应该能得到不同的结果
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999",
                "127.0.0.4:9999"
        );
        
        Set<String> results = new HashSet<>();
        
        // 执行多次，收集结果
        for (int i = 0; i < 100; i++) {
            ReturnT<String> result = routeRandom.route(triggerParam, addressList);
            results.add(result.getContent());
        }
        
        // 应该能得到多个不同的地址（随机性）
        // 注意：理论上有可能100次都选中同一个地址，但概率极低
        assertTrue(results.size() > 1, "Random routing should produce different results");
        
        // 所有结果都应该在原始地址列表中
        for (String result : results) {
            assertTrue(addressList.contains(result));
        }
    }

    @Test
    void testRoute_distribution() {
        // 测试分布均匀性
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999"
        );
        
        int[] counts = new int[2];
        int totalRuns = 1000;
        
        for (int i = 0; i < totalRuns; i++) {
            ReturnT<String> result = routeRandom.route(triggerParam, addressList);
            String address = result.getContent();
            
            if (address.equals("127.0.0.1:9999")) {
                counts[0]++;
            } else if (address.equals("127.0.0.2:9999")) {
                counts[1]++;
            }
        }
        
        // 检查分布是否合理（允许一定的偏差）
        double ratio1 = (double) counts[0] / totalRuns;
        double ratio2 = (double) counts[1] / totalRuns;
        
        // 每个地址的选中率应该在合理范围内（0.3-0.7之间）
        assertTrue(ratio1 > 0.3 && ratio1 < 0.7, "Address 1 ratio: " + ratio1);
        assertTrue(ratio2 > 0.3 && ratio2 < 0.7, "Address 2 ratio: " + ratio2);
    }

    @Test
    void testRoute_withDifferentJobId() {
        // 测试不同的jobId对随机选择的影响
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        Set<String> results1 = new HashSet<>();
        Set<String> results2 = new HashSet<>();
        
        // 使用不同的jobId进行多次测试
        triggerParam.setJobId(1);
        for (int i = 0; i < 50; i++) {
            results1.add(routeRandom.route(triggerParam, addressList).getContent());
        }
        
        triggerParam.setJobId(2);
        for (int i = 0; i < 50; i++) {
            results2.add(routeRandom.route(triggerParam, addressList).getContent());
        }
        
        // 两组结果都应该包含在原始地址列表中
        for (String result : results1) {
            assertTrue(addressList.contains(result));
        }
        for (String result : results2) {
            assertTrue(addressList.contains(result));
        }
    }

    @Test
    void testRoute_edgeCase_twoAddresses() {
        // 边界情况测试 - 两个地址
        List<String> addressList = Arrays.asList(
                "server1:9999",
                "server2:9999"
        );
        
        for (int i = 0; i < 10; i++) {
            ReturnT<String> result = routeRandom.route(triggerParam, addressList);
            assertTrue(addressList.contains(result.getContent()));
        }
    }
}