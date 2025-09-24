package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.dao.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * XxlJobServiceImpl 服务类单元测试
 *
 * @author Test Generator
 */
@ExtendWith(MockitoExtension.class)
public class XxlJobServiceImplTest {

    @Mock
    private XxlJobGroupDao xxlJobGroupDao;

    @Mock
    private XxlJobInfoDao xxlJobInfoDao;

    @Mock
    private XxlJobLogDao xxlJobLogDao;

    @Mock
    private XxlJobLogGlueDao xxlJobLogGlueDao;

    @Mock
    private XxlJobLogReportDao xxlJobLogReportDao;

    @InjectMocks
    private XxlJobServiceImpl xxlJobService;

    private XxlJobInfo jobInfo;
    private XxlJobGroup jobGroup;

    @BeforeEach
    void setUp() {
        jobGroup = new XxlJobGroup();
        jobGroup.setId(1);
        jobGroup.setAppname("test-executor");
        jobGroup.setTitle("测试执行器");

        jobInfo = new XxlJobInfo();
        jobInfo.setId(1);
        jobInfo.setJobGroup(1);
        jobInfo.setJobDesc("测试任务");
        jobInfo.setAuthor("admin");
        jobInfo.setAlarmEmail("admin@test.com");
        jobInfo.setScheduleType(ScheduleTypeEnum.CRON.name());
        jobInfo.setScheduleConf("0 0 12 * * ?");
        jobInfo.setMisfireStrategy("DO_NOTHING");
        jobInfo.setExecutorRouteStrategy("FIRST");
        jobInfo.setExecutorHandler("testJobHandler");
        jobInfo.setExecutorParam("");
        jobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        jobInfo.setExecutorTimeout(0);
        jobInfo.setExecutorFailRetryCount(0);
        jobInfo.setGlueType(GlueTypeEnum.BEAN.name());
        jobInfo.setChildJobId("");
        jobInfo.setTriggerStatus(0);
    }

    @Test
    void testPageList_success() {
        // Mock data
        List<XxlJobInfo> mockList = Arrays.asList(jobInfo);
        when(xxlJobInfoDao.pageList(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(mockList);
        when(xxlJobInfoDao.pageListCount(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(1);

        // Execute
        Map<String, Object> result = xxlJobService.pageList(0, 10, 1, 0, "", "", "");

        // Verify
        assertNotNull(result);
        assertEquals(1, result.get("recordsTotal"));
        assertEquals(1, result.get("recordsFiltered"));
        assertEquals(mockList, result.get("data"));

        verify(xxlJobInfoDao).pageList(0, 10, 1, 0, "", "", "");
        verify(xxlJobInfoDao).pageListCount(0, 10, 1, 0, "", "", "");
    }

    @Test
    void testAdd_success() {
        // Mock dependencies
        when(xxlJobGroupDao.load(1)).thenReturn(jobGroup);
        when(xxlJobInfoDao.save(any(XxlJobInfo.class))).thenAnswer(invocation -> {
            XxlJobInfo job = invocation.getArgument(0);
            job.setId(100);
            return 1;
        });

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        assertEquals("100", result.getContent());

        verify(xxlJobGroupDao).load(1);
        verify(xxlJobInfoDao).save(any(XxlJobInfo.class));
    }

    @Test
    void testAdd_invalidJobGroup() {
        // Mock invalid job group
        when(xxlJobGroupDao.load(1)).thenReturn(null);

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertNotNull(result);
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("jobgroup"));

        verify(xxlJobGroupDao).load(1);
        verify(xxlJobInfoDao, never()).save(any());
    }

    @Test
    void testAdd_emptyJobDesc() {
        jobInfo.setJobDesc("");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("jobdesc"));
    }

    @Test
    void testAdd_emptyAuthor() {
        jobInfo.setAuthor("");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("author"));
    }

    @Test
    void testAdd_invalidScheduleType() {
        jobInfo.setScheduleType("INVALID_TYPE");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("schedule_type"));
    }

    @Test
    void testAdd_invalidCronExpression() {
        jobInfo.setScheduleConf("invalid cron");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("Cron"));
    }

    @Test
    void testAdd_fixRateSchedule() {
        // Mock dependencies
        when(xxlJobGroupDao.load(1)).thenReturn(jobGroup);
        when(xxlJobInfoDao.save(any(XxlJobInfo.class))).thenAnswer(invocation -> {
            XxlJobInfo job = invocation.getArgument(0);
            job.setId(100);
            return 1;
        });

        jobInfo.setScheduleType(ScheduleTypeEnum.FIX_RATE.name());
        jobInfo.setScheduleConf("30"); // 30秒

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
    }

    @Test
    void testAdd_invalidFixRateValue() {
        jobInfo.setScheduleType(ScheduleTypeEnum.FIX_RATE.name());
        jobInfo.setScheduleConf("0"); // 无效值

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
    }

    @Test
    void testAdd_invalidGlueType() {
        jobInfo.setGlueType("INVALID_GLUE_TYPE");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("gluetype"));
    }

    @Test
    void testAdd_emptyExecutorHandler() {
        jobInfo.setExecutorHandler("");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("JobHandler"));
    }

    @Test
    void testAdd_validChildJobId() {
        // Mock dependencies
        when(xxlJobGroupDao.load(1)).thenReturn(jobGroup);
        when(xxlJobInfoDao.loadById(2)).thenReturn(new XxlJobInfo());
        when(xxlJobInfoDao.save(any(XxlJobInfo.class))).thenAnswer(invocation -> {
            XxlJobInfo job = invocation.getArgument(0);
            job.setId(100);
            return 1;
        });

        jobInfo.setChildJobId("2");

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        verify(xxlJobInfoDao).loadById(2);
    }

    @Test
    void testAdd_invalidChildJobId() {
        jobInfo.setChildJobId("999");
        when(xxlJobInfoDao.loadById(999)).thenReturn(null);

        // Execute
        ReturnT<String> result = xxlJobService.add(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("childJobId"));
    }

    @Test
    void testUpdate_success() {
        // Mock dependencies
        XxlJobInfo existingJob = new XxlJobInfo();
        existingJob.setId(1);
        existingJob.setTriggerStatus(0);
        existingJob.setScheduleType(ScheduleTypeEnum.CRON.name());
        existingJob.setScheduleConf("0 0 12 * * ?");

        when(xxlJobGroupDao.load(1)).thenReturn(jobGroup);
        when(xxlJobInfoDao.loadById(1)).thenReturn(existingJob);
        when(xxlJobInfoDao.update(any(XxlJobInfo.class))).thenReturn(1);

        // Execute
        ReturnT<String> result = xxlJobService.update(jobInfo);

        // Verify
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        verify(xxlJobInfoDao).update(any(XxlJobInfo.class));
    }

    @Test
    void testUpdate_jobNotFound() {
        when(xxlJobInfoDao.loadById(1)).thenReturn(null);

        // Execute
        ReturnT<String> result = xxlJobService.update(jobInfo);

        // Verify
        assertEquals(ReturnT.FAIL_CODE, result.getCode());
        assertTrue(result.getMsg().contains("not_found"));
    }

    @Test
    void testRemove_success() {
        // Mock existing job
        when(xxlJobInfoDao.loadById(1)).thenReturn(jobInfo);
        when(xxlJobInfoDao.delete(1)).thenReturn(1);
        when(xxlJobLogDao.delete(1)).thenReturn(1);
        when(xxlJobLogGlueDao.deleteByJobId(1)).thenReturn(1);

        // Execute
        ReturnT<String> result = xxlJobService.remove(1);

        // Verify
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        verify(xxlJobInfoDao).delete(1);
        verify(xxlJobLogDao).delete(1);
        verify(xxlJobLogGlueDao).deleteByJobId(1);
    }

    @Test
    void testRemove_jobNotFound() {
        when(xxlJobInfoDao.loadById(1)).thenReturn(null);

        // Execute
        ReturnT<String> result = xxlJobService.remove(1);

        // Verify - should still return success even if job not found
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
    }

    @Test
    void testStop_success() {
        // Mock existing job
        when(xxlJobInfoDao.loadById(1)).thenReturn(jobInfo);
        when(xxlJobInfoDao.update(any(XxlJobInfo.class))).thenReturn(1);

        // Execute
        ReturnT<String> result = xxlJobService.stop(1);

        // Verify
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        verify(xxlJobInfoDao).update(any(XxlJobInfo.class));
    }

    @Test
    void testDashboardInfo_success() {
        // Mock data
        when(xxlJobInfoDao.findAllCount()).thenReturn(10);
        
        XxlJobLogReport logReport = new XxlJobLogReport();
        logReport.setRunningCount(5);
        logReport.setSucCount(15);
        logReport.setFailCount(2);
        when(xxlJobLogReportDao.queryLogReportTotal()).thenReturn(logReport);

        List<XxlJobGroup> groupList = new ArrayList<>();
        XxlJobGroup group1 = new XxlJobGroup();
        group1.setRegistryList(Arrays.asList("127.0.0.1:9999", "127.0.0.2:9999"));
        groupList.add(group1);
        when(xxlJobGroupDao.findAll()).thenReturn(groupList);

        // Execute
        Map<String, Object> result = xxlJobService.dashboardInfo();

        // Verify
        assertNotNull(result);
        assertEquals(10, result.get("jobInfoCount"));
        assertEquals(22, result.get("jobLogCount")); // 5+15+2
        assertEquals(15, result.get("jobLogSuccessCount"));
        assertEquals(2, result.get("executorCount"));
    }

    @Test
    void testDashboardInfo_nullLogReport() {
        // Mock data
        when(xxlJobInfoDao.findAllCount()).thenReturn(5);
        when(xxlJobLogReportDao.queryLogReportTotal()).thenReturn(null);
        when(xxlJobGroupDao.findAll()).thenReturn(new ArrayList<>());

        // Execute
        Map<String, Object> result = xxlJobService.dashboardInfo();

        // Verify
        assertNotNull(result);
        assertEquals(5, result.get("jobInfoCount"));
        assertEquals(0, result.get("jobLogCount"));
        assertEquals(0, result.get("jobLogSuccessCount"));
        assertEquals(0, result.get("executorCount"));
    }

    @Test
    void testChartInfo_success() {
        // Mock data
        Date startDate = new Date();
        Date endDate = new Date();
        
        List<XxlJobLogReport> logReportList = new ArrayList<>();
        XxlJobLogReport report1 = new XxlJobLogReport();
        report1.setTriggerDay(startDate);
        report1.setRunningCount(3);
        report1.setSucCount(7);
        report1.setFailCount(1);
        logReportList.add(report1);
        
        when(xxlJobLogReportDao.queryLogReport(startDate, endDate)).thenReturn(logReportList);

        // Execute
        ReturnT<Map<String, Object>> result = xxlJobService.chartInfo(startDate, endDate);

        // Verify
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        
        Map<String, Object> data = result.getContent();
        assertNotNull(data);
        assertTrue(data.containsKey("triggerDayList"));
        assertTrue(data.containsKey("triggerDayCountRunningList"));
        assertTrue(data.containsKey("triggerDayCountSucList"));
        assertTrue(data.containsKey("triggerDayCountFailList"));
        assertEquals(3, data.get("triggerCountRunningTotal"));
        assertEquals(7, data.get("triggerCountSucTotal"));
        assertEquals(1, data.get("triggerCountFailTotal"));
    }

    @Test
    void testChartInfo_emptyData() {
        // Mock empty data
        Date startDate = new Date();
        Date endDate = new Date();
        
        when(xxlJobLogReportDao.queryLogReport(startDate, endDate)).thenReturn(new ArrayList<>());

        // Execute
        ReturnT<Map<String, Object>> result = xxlJobService.chartInfo(startDate, endDate);

        // Verify
        assertNotNull(result);
        assertEquals(ReturnT.SUCCESS_CODE, result.getCode());
        
        Map<String, Object> data = result.getContent();
        assertNotNull(data);
        assertEquals(0, data.get("triggerCountRunningTotal"));
        assertEquals(0, data.get("triggerCountSucTotal"));
        assertEquals(0, data.get("triggerCountFailTotal"));
    }
}