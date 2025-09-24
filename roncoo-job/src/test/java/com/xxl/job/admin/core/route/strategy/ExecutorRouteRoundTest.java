package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExecutorRouteRound 轮询路由策略单元测试
 *
 * @author Test Generator
 */
public class ExecutorRouteRoundTest {

    private ExecutorRouteRound routeRound;
    private TriggerParam triggerParam;

    @BeforeEach
    void setUp() {
        routeRound = new ExecutorRouteRound();
        triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
    }

    @Test
    void testRoute_singleAddress() {
        // 测试单个地址
        List<String> addressList = Collections.singletonList("127.0.0.1:9999");
        
        ReturnT<String> result = routeRound.route(triggerParam, addressList);
        
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertEquals("127.0.0.1:9999", result.getContent());
    }

    @Test
    void testRoute_twoAddresses() {
        // 测试两个地址的轮询
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999"
        );
        
        // 连续调用多次，应该轮询
        String[] results = new String[4];
        for (int i = 0; i < 4; i++) {
            results[i] = routeRound.route(triggerParam, addressList).getContent();
        }
        
        // 验证所有结果都在地址列表中
        for (String result : results) {
            assertTrue(addressList.contains(result));
        }
        
        // 由于轮询的特性，应该有规律可循（但初始值是随机的）
        // 所以我们主要验证所有地址都被使用过
        boolean hasFirst = false, hasSecond = false;
        for (String result : results) {
            if (result.equals("127.0.0.1:9999")) {
                hasFirst = true;
            }
            if (result.equals("127.0.0.2:9999")) {
                hasSecond = true;
            }
        }
        
        // 在4次调用中，两个地址都应该被选中过
        assertTrue(hasFirst, "First address should be selected");
        assertTrue(hasSecond, "Second address should be selected");
    }

    @Test
    void testRoute_threeAddresses() {
        // 测试三个地址的轮询
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        // 连续调用6次（两轮完整轮询）
        String[] results = new String[6];
        for (int i = 0; i < 6; i++) {
            results[i] = routeRound.route(triggerParam, addressList).getContent();
        }
        
        // 验证所有结果都在地址列表中
        for (String result : results) {
            assertTrue(addressList.contains(result));
        }
        
        // 验证所有地址都被使用过
        boolean[] addressUsed = new boolean[3];
        for (String result : results) {
            for (int i = 0; i < addressList.size(); i++) {
                if (result.equals(addressList.get(i))) {
                    addressUsed[i] = true;
                }
            }
        }
        
        for (boolean used : addressUsed) {
            assertTrue(used, "All addresses should be used");
        }
    }

    @Test
    void testRoute_differentJobIds() {
        // 测试不同的jobId有独立的轮询计数器
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999"
        );
        
        // Job 1
        triggerParam.setJobId(1);
        String job1Result1 = routeRound.route(triggerParam, addressList).getContent();
        String job1Result2 = routeRound.route(triggerParam, addressList).getContent();
        
        // Job 2
        triggerParam.setJobId(2);
        String job2Result1 = routeRound.route(triggerParam, addressList).getContent();
        String job2Result2 = routeRound.route(triggerParam, addressList).getContent();
        
        // 所有结果都应该在地址列表中
        assertTrue(addressList.contains(job1Result1));
        assertTrue(addressList.contains(job1Result2));
        assertTrue(addressList.contains(job2Result1));
        assertTrue(addressList.contains(job2Result2));
    }

    @Test
    void testRoute_manyRequests() {
        // 测试大量请求的分布
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        int[] counts = new int[3];
        int totalRequests = 300; // 100轮完整轮询
        
        for (int i = 0; i < totalRequests; i++) {
            String result = routeRound.route(triggerParam, addressList).getContent();
            
            for (int j = 0; j < addressList.size(); j++) {
                if (result.equals(addressList.get(j))) {
                    counts[j]++;
                    break;
                }
            }
        }
        
        // 验证分布相对均匀（允许一定偏差，因为初始值是随机的）
        int expectedCount = totalRequests / addressList.size();
        for (int count : counts) {
            // 允许10%的偏差
            assertTrue(count >= expectedCount * 0.9 && count <= expectedCount * 1.1,
                    "Count should be approximately " + expectedCount + ", but was " + count);
        }
    }

    @Test
    void testRoute_sameJobMultipleCalls() {
        // 测试同一个job的多次调用
        List<String> addressList = Arrays.asList(
                "server1:9999",
                "server2:9999"
        );
        
        String[] results = new String[10];
        for (int i = 0; i < 10; i++) {
            results[i] = routeRound.route(triggerParam, addressList).getContent();
        }
        
        // 验证所有结果都有效
        for (String result : results) {
            assertTrue(addressList.contains(result));
        }
        
        // 计算每个地址的使用次数
        int count1 = 0, count2 = 0;
        for (String result : results) {
            if (result.equals("server1:9999")) {
                count1++;
            } else {
                count2++;
            }
        }
        
        // 两个地址的使用次数应该比较接近（轮询特性）
        int diff = Math.abs(count1 - count2);
        assertTrue(diff <= 1, "Round robin should distribute calls evenly, difference: " + diff);
    }

    @Test
    void testRoute_largeAddressList() {
        // 测试较大的地址列表
        List<String> addressList = Arrays.asList(
                "server1:9999", "server2:9999", "server3:9999", "server4:9999", "server5:9999"
        );
        
        // 调用地址列表大小的2倍次数
        int callCount = addressList.size() * 2;
        for (int i = 0; i < callCount; i++) {
            ReturnT<String> result = routeRound.route(triggerParam, addressList);
            assertTrue(addressList.contains(result.getContent()));
        }
    }
}