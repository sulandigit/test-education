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
 * ExecutorRouteFirst 路由策略单元测试
 *
 * @author Test Generator
 */
public class ExecutorRouteFirstTest {

    private ExecutorRouteFirst routeFirst;
    private TriggerParam triggerParam;

    @BeforeEach
    void setUp() {
        routeFirst = new ExecutorRouteFirst();
        triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
    }

    @Test
    void testRoute_singleAddress() {
        // 测试单个地址
        List<String> addressList = Collections.singletonList("127.0.0.1:9999");
        
        ReturnT<String> result = routeFirst.route(triggerParam, addressList);
        
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertEquals("127.0.0.1:9999", result.getContent());
    }

    @Test
    void testRoute_multipleAddresses() {
        // 测试多个地址，应该总是返回第一个
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        ReturnT<String> result = routeFirst.route(triggerParam, addressList);
        
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertEquals("127.0.0.1:9999", result.getContent());
    }

    @Test
    void testRoute_consistency() {
        // 测试多次调用的一致性
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999",
                "127.0.0.3:9999"
        );
        
        String firstResult = routeFirst.route(triggerParam, addressList).getContent();
        String secondResult = routeFirst.route(triggerParam, addressList).getContent();
        String thirdResult = routeFirst.route(triggerParam, addressList).getContent();
        
        assertEquals(firstResult, secondResult);
        assertEquals(secondResult, thirdResult);
        assertEquals("127.0.0.1:9999", firstResult);
    }

    @Test
    void testRoute_withDifferentJobId() {
        // 测试不同的jobId，应该都返回第一个地址
        List<String> addressList = Arrays.asList(
                "127.0.0.1:9999",
                "127.0.0.2:9999"
        );
        
        triggerParam.setJobId(1);
        String result1 = routeFirst.route(triggerParam, addressList).getContent();
        
        triggerParam.setJobId(2);
        String result2 = routeFirst.route(triggerParam, addressList).getContent();
        
        assertEquals("127.0.0.1:9999", result1);
        assertEquals("127.0.0.1:9999", result2);
        assertEquals(result1, result2);
    }
}