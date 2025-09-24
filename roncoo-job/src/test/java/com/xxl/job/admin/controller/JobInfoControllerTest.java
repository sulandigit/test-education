package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JobInfoController 控制器单元测试
 *
 * @author Test Generator
 */
@ExtendWith(MockitoExtension.class)
public class JobInfoControllerTest {

    @Mock
    private XxlJobGroupDao xxlJobGroupDao;

    @Mock
    private XxlJobService xxlJobService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private JobInfoController jobInfoController;

    private MockMvc mockMvc;
    private XxlJobUser adminUser;
    private XxlJobUser normalUser;
    private List<XxlJobGroup> jobGroupList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobInfoController).build();

        // 设置管理员用户
        adminUser = new XxlJobUser();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setRole(1); // 管理员角色

        // 设置普通用户
        normalUser = new XxlJobUser();
        normalUser.setId(2);
        normalUser.setUsername("user");
        normalUser.setRole(0); // 普通用户角色
        normalUser.setPermission("1,2"); // 只能访问组1和组2

        // 设置任务组列表
        jobGroupList = new ArrayList<>();
        XxlJobGroup group1 = new XxlJobGroup();
        group1.setId(1);
        group1.setAppname("group1");
        group1.setTitle("测试组1");

        XxlJobGroup group2 = new XxlJobGroup();
        group2.setId(2);
        group2.setAppname("group2");
        group2.setTitle("测试组2");

        XxlJobGroup group3 = new XxlJobGroup();
        group3.setId(3);
        group3.setAppname("group3");
        group3.setTitle("测试组3");

        jobGroupList.add(group1);
        jobGroupList.add(group2);
        jobGroupList.add(group3);
    }

    @Test
    void testIndex_adminUser() throws Exception {
        // Mock 管理员用户
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(adminUser);
        when(xxlJobGroupDao.findAll()).thenReturn(jobGroupList);

        // 执行
        String result = jobInfoController.index(request, model, -1);

        // 验证
        assertEquals("jobinfo/jobinfo.index", result);
        verify(model).addAttribute("JobGroupList", jobGroupList);
        verify(model).addAttribute("jobGroup", -1);
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }

    @Test
    void testIndex_normalUser() throws Exception {
        // Mock 普通用户
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);
        when(xxlJobGroupDao.findAll()).thenReturn(jobGroupList);

        // 执行
        String result = jobInfoController.index(request, model, 1);

        // 验证
        assertEquals("jobinfo/jobinfo.index", result);
        
        // 验证只有权限范围内的组被添加
        verify(model).addAttribute(eq("JobGroupList"), argThat(list -> {
            @SuppressWarnings("unchecked")
            List<XxlJobGroup> groups = (List<XxlJobGroup>) list;
            return groups.size() == 2 && 
                   groups.stream().anyMatch(g -> g.getId() == 1) &&
                   groups.stream().anyMatch(g -> g.getId() == 2) &&
                   groups.stream().noneMatch(g -> g.getId() == 3);
        }));
    }

    @Test
    void testFilterJobGroupByRole_adminUser() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(adminUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, jobGroupList);

        assertEquals(3, result.size());
        assertEquals(jobGroupList, result);
    }

    @Test
    void testFilterJobGroupByRole_normalUser() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, jobGroupList);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getId() == 1));
        assertTrue(result.stream().anyMatch(g -> g.getId() == 2));
        assertFalse(result.stream().anyMatch(g -> g.getId() == 3));
    }

    @Test
    void testFilterJobGroupByRole_emptyPermission() {
        normalUser.setPermission("");
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, jobGroupList);

        assertEquals(0, result.size());
    }

    @Test
    void testFilterJobGroupByRole_nullPermission() {
        normalUser.setPermission(null);
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, jobGroupList);

        assertEquals(0, result.size());
    }

    @Test
    void testValidPermission_adminUser() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(adminUser);

        // 管理员用户应该可以访问任何组
        assertDoesNotThrow(() -> {
            JobInfoController.validPermission(request, 1);
            JobInfoController.validPermission(request, 999);
        });
    }

    @Test
    void testValidPermission_normalUserWithPermission() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);

        // 普通用户访问有权限的组应该成功
        assertDoesNotThrow(() -> {
            JobInfoController.validPermission(request, 1);
            JobInfoController.validPermission(request, 2);
        });
    }

    @Test
    void testValidPermission_normalUserWithoutPermission() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);

        // 普通用户访问无权限的组应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            JobInfoController.validPermission(request, 3);
        });
    }

    @Test
    void testPageList() throws Exception {
        // Mock 返回数据
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("recordsTotal", 100);
        mockData.put("recordsFiltered", 50);
        mockData.put("data", Arrays.asList(new XxlJobInfo()));

        when(xxlJobService.pageList(anyInt(), anyInt(), anyInt(), anyInt(), 
                anyString(), anyString(), anyString())).thenReturn(mockData);

        // 执行测试
        mockMvc.perform(get("/jobinfo/pageList")
                .param("start", "0")
                .param("length", "10")
                .param("jobGroup", "1")
                .param("triggerStatus", "1")
                .param("jobDesc", "test")
                .param("executorHandler", "testHandler")
                .param("author", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordsTotal").value(100))
                .andExpect(jsonPath("$.recordsFiltered").value(50));

        verify(xxlJobService).pageList(0, 10, 1, 1, "test", "testHandler", "admin");
    }

    @Test
    void testPageList_defaultParameters() throws Exception {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("recordsTotal", 0);
        mockData.put("recordsFiltered", 0);
        mockData.put("data", Collections.emptyList());

        when(xxlJobService.pageList(anyInt(), anyInt(), anyInt(), anyInt(), 
                anyString(), anyString(), anyString())).thenReturn(mockData);

        // 不传参数，使用默认值
        mockMvc.perform(get("/jobinfo/pageList")
                .param("jobGroup", "1")
                .param("triggerStatus", "0"))
                .andExpect(status().isOk());

        // 验证使用了默认参数
        verify(xxlJobService).pageList(0, 10, 1, 0, null, null, null);
    }

    @Test
    void testAdd() throws Exception {
        ReturnT<String> mockResult = ReturnT.SUCCESS;
        when(xxlJobService.add(any(XxlJobInfo.class))).thenReturn(mockResult);

        mockMvc.perform(post("/jobinfo/add")
                .param("jobDesc", "测试任务")
                .param("author", "admin")
                .param("scheduleType", "CRON")
                .param("scheduleConf", "0 0 12 * * ?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(xxlJobService).add(any(XxlJobInfo.class));
    }

    @Test
    void testAdd_failure() throws Exception {
        ReturnT<String> mockResult = new ReturnT<>(500, "添加失败");
        when(xxlJobService.add(any(XxlJobInfo.class))).thenReturn(mockResult);

        mockMvc.perform(post("/jobinfo/add")
                .param("jobDesc", "")
                .param("author", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("添加失败"));
    }

    @Test
    void testPageList_directCall() {
        // 直接调用控制器方法
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("recordsTotal", 5);
        mockData.put("data", Arrays.asList(new XxlJobInfo()));

        when(xxlJobService.pageList(0, 10, 1, 0, "", "", "")).thenReturn(mockData);

        Map<String, Object> result = jobInfoController.pageList(0, 10, 1, 0, "", "", "");

        assertNotNull(result);
        assertEquals(5, result.get("recordsTotal"));
        verify(xxlJobService).pageList(0, 10, 1, 0, "", "", "");
    }

    @Test
    void testAdd_directCall() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setJobDesc("测试任务");
        jobInfo.setAuthor("admin");

        ReturnT<String> mockResult = new ReturnT<>("100");
        when(xxlJobService.add(jobInfo)).thenReturn(mockResult);

        ReturnT<String> result = jobInfoController.add(jobInfo);

        assertNotNull(result);
        assertEquals("100", result.getContent());
        verify(xxlJobService).add(jobInfo);
    }

    @Test
    void testIndex_emptyJobGroupList() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(normalUser);
        when(xxlJobGroupDao.findAll()).thenReturn(Collections.emptyList());

        // 应该抛出异常
        assertThrows(Exception.class, () -> {
            jobInfoController.index(request, model, -1);
        });
    }

    @Test
    void testFilterJobGroupByRole_nullJobGroupList() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(adminUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFilterJobGroupByRole_emptyJobGroupList() {
        when(request.getAttribute(LoginService.LOGIN_IDENTITY_KEY)).thenReturn(adminUser);

        List<XxlJobGroup> result = JobInfoController.filterJobGroupByRole(request, Collections.emptyList());

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}