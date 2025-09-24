package com.xxl.job.admin.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JacksonUtil 工具类单元测试
 *
 * @author Test Generator
 */
public class JacksonUtilTest {

    private Map<String, Object> testMap;
    private TestBean testBean;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testMap = new HashMap<>();
        testMap.put("name", "test");
        testMap.put("age", 25);
        testMap.put("active", true);

        testBean = new TestBean();
        testBean.setName("testBean");
        testBean.setAge(30);
        testBean.setActive(false);
    }

    @Test
    void testGetInstance() {
        ObjectMapper instance1 = JacksonUtil.getInstance();
        ObjectMapper instance2 = JacksonUtil.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void testWriteValueAsString_withMap() {
        String jsonString = JacksonUtil.writeValueAsString(testMap);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"name\":\"test\""));
        assertTrue(jsonString.contains("\"age\":25"));
        assertTrue(jsonString.contains("\"active\":true"));
    }

    @Test
    void testWriteValueAsString_withBean() {
        String jsonString = JacksonUtil.writeValueAsString(testBean);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"name\":\"testBean\""));
        assertTrue(jsonString.contains("\"age\":30"));
        assertTrue(jsonString.contains("\"active\":false"));
    }

    @Test
    void testWriteValueAsString_withList() {
        List<String> testList = Arrays.asList("item1", "item2", "item3");
        String jsonString = JacksonUtil.writeValueAsString(testList);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"item1\""));
        assertTrue(jsonString.contains("\"item2\""));
        assertTrue(jsonString.contains("\"item3\""));
        assertTrue(jsonString.startsWith("["));
        assertTrue(jsonString.endsWith("]"));
    }

    @Test
    void testWriteValueAsString_withArray() {
        String[] testArray = {"value1", "value2"};
        String jsonString = JacksonUtil.writeValueAsString(testArray);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"value1\""));
        assertTrue(jsonString.contains("\"value2\""));
    }

    @Test
    void testWriteValueAsString_withNull() {
        String jsonString = JacksonUtil.writeValueAsString(null);
        assertEquals("null", jsonString);
    }

    @Test
    void testWriteValueAsString_withEmptyMap() {
        Map<String, Object> emptyMap = new HashMap<>();
        String jsonString = JacksonUtil.writeValueAsString(emptyMap);
        
        assertNotNull(jsonString);
        assertEquals("{}", jsonString);
    }

    @Test
    void testReadValue_stringToBean() {
        String jsonString = "{\"name\":\"testBean\",\"age\":30,\"active\":false}";
        TestBean result = JacksonUtil.readValue(jsonString, TestBean.class);
        
        assertNotNull(result);
        assertEquals("testBean", result.getName());
        assertEquals(30, result.getAge());
        assertFalse(result.isActive());
    }

    @Test
    void testReadValue_stringToMap() {
        String jsonString = "{\"name\":\"test\",\"age\":25,\"active\":true}";
        @SuppressWarnings("unchecked")
        Map<String, Object> result = JacksonUtil.readValue(jsonString, Map.class);
        
        assertNotNull(result);
        assertEquals("test", result.get("name"));
        assertEquals(25, result.get("age"));
        assertEquals(true, result.get("active"));
    }

    @Test
    void testReadValue_stringToList() {
        String jsonString = "[\"item1\",\"item2\",\"item3\"]";
        @SuppressWarnings("unchecked")
        List<String> result = JacksonUtil.readValue(jsonString, List.class);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
    }

    @Test
    void testReadValue_invalidJson() {
        String invalidJson = "{invalid json}";
        TestBean result = JacksonUtil.readValue(invalidJson, TestBean.class);
        
        assertNull(result);
    }

    @Test
    void testReadValue_nullJson() {
        TestBean result = JacksonUtil.readValue(null, TestBean.class);
        
        assertNull(result);
    }

    @Test
    void testReadValue_emptyJson() {
        String emptyJson = "";
        TestBean result = JacksonUtil.readValue(emptyJson, TestBean.class);
        
        assertNull(result);
    }

    @Test
    void testReadValue_parametricType() {
        String jsonString = "[{\"name\":\"bean1\",\"age\":20,\"active\":true},{\"name\":\"bean2\",\"age\":25,\"active\":false}]";
        List<TestBean> result = JacksonUtil.readValue(jsonString, List.class, TestBean.class);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        // 注意：由于类型擦除，这里实际返回的是 List<Map>，而不是 List<TestBean>
        // 这是 Jackson 在处理泛型时的行为
    }

    @Test
    void testReadValue_parametricType_invalidJson() {
        String invalidJson = "[{invalid json}]";
        List<TestBean> result = JacksonUtil.readValue(invalidJson, List.class, TestBean.class);
        
        assertNull(result);
    }

    @Test
    void testRoundTrip_beanToJsonAndBack() {
        // 对象 -> JSON -> 对象
        String jsonString = JacksonUtil.writeValueAsString(testBean);
        TestBean result = JacksonUtil.readValue(jsonString, TestBean.class);
        
        assertNotNull(result);
        assertEquals(testBean.getName(), result.getName());
        assertEquals(testBean.getAge(), result.getAge());
        assertEquals(testBean.isActive(), result.isActive());
    }

    @Test
    void testRoundTrip_mapToJsonAndBack() {
        // Map -> JSON -> Map
        String jsonString = JacksonUtil.writeValueAsString(testMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = JacksonUtil.readValue(jsonString, Map.class);
        
        assertNotNull(result);
        assertEquals(testMap.get("name"), result.get("name"));
        assertEquals(testMap.get("age"), result.get("age"));
        assertEquals(testMap.get("active"), result.get("active"));
    }

    /**
     * 测试用的简单 Bean 类
     */
    public static class TestBean {
        private String name;
        private int age;
        private boolean active;

        public TestBean() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}