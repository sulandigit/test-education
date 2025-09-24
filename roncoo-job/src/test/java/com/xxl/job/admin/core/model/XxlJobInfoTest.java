package com.xxl.job.admin.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XxlJobInfo 模型类单元测试
 *
 * @author Test Generator
 */
public class XxlJobInfoTest {

    private XxlJobInfo xxlJobInfo;

    @BeforeEach
    void setUp() {
        xxlJobInfo = new XxlJobInfo();
    }

    @Test
    void testDefaultConstructor() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        assertNotNull(jobInfo);
        assertEquals(0, jobInfo.getId());
        assertEquals(0, jobInfo.getJobGroup());
        assertNull(jobInfo.getJobDesc());
    }

    @Test
    void testSetAndGetId() {
        int testId = 123;
        xxlJobInfo.setId(testId);
        assertEquals(testId, xxlJobInfo.getId());
    }

    @Test
    void testSetAndGetJobGroup() {
        int testJobGroup = 456;
        xxlJobInfo.setJobGroup(testJobGroup);
        assertEquals(testJobGroup, xxlJobInfo.getJobGroup());
    }

    @Test
    void testSetAndGetJobDesc() {
        String testJobDesc = "测试任务描述";
        xxlJobInfo.setJobDesc(testJobDesc);
        assertEquals(testJobDesc, xxlJobInfo.getJobDesc());
    }

    @Test
    void testSetAndGetAddTime() {
        Date testDate = new Date();
        xxlJobInfo.setAddTime(testDate);
        assertEquals(testDate, xxlJobInfo.getAddTime());
    }

    @Test
    void testSetAndGetUpdateTime() {
        Date testDate = new Date();
        xxlJobInfo.setUpdateTime(testDate);
        assertEquals(testDate, xxlJobInfo.getUpdateTime());
    }

    @Test
    void testSetAndGetAuthor() {
        String testAuthor = "test_author";
        xxlJobInfo.setAuthor(testAuthor);
        assertEquals(testAuthor, xxlJobInfo.getAuthor());
    }

    @Test
    void testSetAndGetAlarmEmail() {
        String testEmail = "test@example.com";
        xxlJobInfo.setAlarmEmail(testEmail);
        assertEquals(testEmail, xxlJobInfo.getAlarmEmail());
    }

    @Test
    void testSetAndGetScheduleType() {
        String testScheduleType = "CRON";
        xxlJobInfo.setScheduleType(testScheduleType);
        assertEquals(testScheduleType, xxlJobInfo.getScheduleType());
    }

    @Test
    void testSetAndGetScheduleConf() {
        String testScheduleConf = "0 0 12 * * ?";
        xxlJobInfo.setScheduleConf(testScheduleConf);
        assertEquals(testScheduleConf, xxlJobInfo.getScheduleConf());
    }

    @Test
    void testSetAndGetMisfireStrategy() {
        String testStrategy = "DO_NOTHING";
        xxlJobInfo.setMisfireStrategy(testStrategy);
        assertEquals(testStrategy, xxlJobInfo.getMisfireStrategy());
    }

    @Test
    void testSetAndGetExecutorRouteStrategy() {
        String testStrategy = "FIRST";
        xxlJobInfo.setExecutorRouteStrategy(testStrategy);
        assertEquals(testStrategy, xxlJobInfo.getExecutorRouteStrategy());
    }

    @Test
    void testSetAndGetExecutorHandler() {
        String testHandler = "testJobHandler";
        xxlJobInfo.setExecutorHandler(testHandler);
        assertEquals(testHandler, xxlJobInfo.getExecutorHandler());
    }

    @Test
    void testSetAndGetExecutorParam() {
        String testParam = "param1=value1";
        xxlJobInfo.setExecutorParam(testParam);
        assertEquals(testParam, xxlJobInfo.getExecutorParam());
    }

    @Test
    void testSetAndGetExecutorBlockStrategy() {
        String testStrategy = "SERIAL_EXECUTION";
        xxlJobInfo.setExecutorBlockStrategy(testStrategy);
        assertEquals(testStrategy, xxlJobInfo.getExecutorBlockStrategy());
    }

    @Test
    void testSetAndGetExecutorTimeout() {
        int testTimeout = 300;
        xxlJobInfo.setExecutorTimeout(testTimeout);
        assertEquals(testTimeout, xxlJobInfo.getExecutorTimeout());
    }

    @Test
    void testSetAndGetExecutorFailRetryCount() {
        int testRetryCount = 3;
        xxlJobInfo.setExecutorFailRetryCount(testRetryCount);
        assertEquals(testRetryCount, xxlJobInfo.getExecutorFailRetryCount());
    }

    @Test
    void testSetAndGetGlueType() {
        String testGlueType = "BEAN";
        xxlJobInfo.setGlueType(testGlueType);
        assertEquals(testGlueType, xxlJobInfo.getGlueType());
    }

    @Test
    void testSetAndGetGlueSource() {
        String testGlueSource = "glue source code";
        xxlJobInfo.setGlueSource(testGlueSource);
        assertEquals(testGlueSource, xxlJobInfo.getGlueSource());
    }

    @Test
    void testSetAndGetGlueRemark() {
        String testRemark = "glue remark";
        xxlJobInfo.setGlueRemark(testRemark);
        assertEquals(testRemark, xxlJobInfo.getGlueRemark());
    }

    @Test
    void testSetAndGetGlueUpdatetime() {
        Date testDate = new Date();
        xxlJobInfo.setGlueUpdatetime(testDate);
        assertEquals(testDate, xxlJobInfo.getGlueUpdatetime());
    }

    @Test
    void testSetAndGetChildJobId() {
        String testChildJobId = "1,2,3";
        xxlJobInfo.setChildJobId(testChildJobId);
        assertEquals(testChildJobId, xxlJobInfo.getChildJobId());
    }

    @Test
    void testSetAndGetTriggerStatus() {
        int testStatus = 1;
        xxlJobInfo.setTriggerStatus(testStatus);
        assertEquals(testStatus, xxlJobInfo.getTriggerStatus());
    }

    @Test
    void testSetAndGetTriggerLastTime() {
        long testTime = System.currentTimeMillis();
        xxlJobInfo.setTriggerLastTime(testTime);
        assertEquals(testTime, xxlJobInfo.getTriggerLastTime());
    }

    @Test
    void testSetAndGetTriggerNextTime() {
        long testTime = System.currentTimeMillis() + 60000;
        xxlJobInfo.setTriggerNextTime(testTime);
        assertEquals(testTime, xxlJobInfo.getTriggerNextTime());
    }

    @Test
    void testCompleteJobInfoObject() {
        // 测试一个完整的任务信息对象
        int id = 1;
        int jobGroup = 1;
        String jobDesc = "测试任务";
        Date now = new Date();
        String author = "admin";
        String alarmEmail = "admin@test.com";
        String scheduleType = "CRON";
        String scheduleConf = "0 0 12 * * ?";
        String executorHandler = "testHandler";
        int triggerStatus = 1;

        xxlJobInfo.setId(id);
        xxlJobInfo.setJobGroup(jobGroup);
        xxlJobInfo.setJobDesc(jobDesc);
        xxlJobInfo.setAddTime(now);
        xxlJobInfo.setUpdateTime(now);
        xxlJobInfo.setAuthor(author);
        xxlJobInfo.setAlarmEmail(alarmEmail);
        xxlJobInfo.setScheduleType(scheduleType);
        xxlJobInfo.setScheduleConf(scheduleConf);
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setTriggerStatus(triggerStatus);

        // 验证所有属性
        assertEquals(id, xxlJobInfo.getId());
        assertEquals(jobGroup, xxlJobInfo.getJobGroup());
        assertEquals(jobDesc, xxlJobInfo.getJobDesc());
        assertEquals(now, xxlJobInfo.getAddTime());
        assertEquals(now, xxlJobInfo.getUpdateTime());
        assertEquals(author, xxlJobInfo.getAuthor());
        assertEquals(alarmEmail, xxlJobInfo.getAlarmEmail());
        assertEquals(scheduleType, xxlJobInfo.getScheduleType());
        assertEquals(scheduleConf, xxlJobInfo.getScheduleConf());
        assertEquals(executorHandler, xxlJobInfo.getExecutorHandler());
        assertEquals(triggerStatus, xxlJobInfo.getTriggerStatus());
    }

    @Test
    void testNullValues() {
        // 测试空值设置
        xxlJobInfo.setJobDesc(null);
        xxlJobInfo.setAuthor(null);
        xxlJobInfo.setAlarmEmail(null);
        xxlJobInfo.setExecutorHandler(null);

        assertNull(xxlJobInfo.getJobDesc());
        assertNull(xxlJobInfo.getAuthor());
        assertNull(xxlJobInfo.getAlarmEmail());
        assertNull(xxlJobInfo.getExecutorHandler());
    }

    @Test
    void testEmptyStringValues() {
        // 测试空字符串设置
        String emptyString = "";
        xxlJobInfo.setJobDesc(emptyString);
        xxlJobInfo.setAuthor(emptyString);
        xxlJobInfo.setExecutorHandler(emptyString);

        assertEquals(emptyString, xxlJobInfo.getJobDesc());
        assertEquals(emptyString, xxlJobInfo.getAuthor());
        assertEquals(emptyString, xxlJobInfo.getExecutorHandler());
    }
}