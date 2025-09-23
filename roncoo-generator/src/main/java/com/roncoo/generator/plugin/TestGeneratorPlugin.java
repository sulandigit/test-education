package com.roncoo.generator.plugin;

import com.roncoo.generator.context.TemplateContext;
import com.roncoo.generator.engine.GeneratedFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 测试代码生成插件
 * 
 * @author roncoo-generator
 */
@Slf4j
public class TestGeneratorPlugin implements GeneratorPlugin {
    
    /**
     * 是否启用
     */
    private boolean enabled = true;
    
    /**
     * 测试框架
     */
    private String framework = "junit5";
    
    /**
     * 是否生成单元测试
     */
    private boolean generateUnitTest = true;
    
    /**
     * 是否生成集成测试
     */
    private boolean generateIntegrationTest = true;
    
    /**
     * 测试覆盖率目标
     */
    private int coverageTarget = 80;
    
    @Override
    public String getName() {
        return "TestGenerator";
    }
    
    @Override
    public String getDescription() {
        return "测试代码生成插件，用于生成单元测试和集成测试代码";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public int getOrder() {
        return 400; // 在文档生成后执行
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        if (properties != null) {
            enabled = (Boolean) properties.getOrDefault("enabled", true);
            framework = (String) properties.getOrDefault("framework", "junit5");
            generateUnitTest = (Boolean) properties.getOrDefault("generateUnitTest", true);
            generateIntegrationTest = (Boolean) properties.getOrDefault("generateIntegrationTest", true);
            coverageTarget = (Integer) properties.getOrDefault("coverageTarget", 80);
        }
        
        log.info("测试代码生成插件初始化完成 - 框架: {}, 单元测试: {}, 集成测试: {}, 覆盖率目标: {}%", 
                framework, generateUnitTest, generateIntegrationTest, coverageTarget);
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> generatedFiles) {
        if (!enabled) {
            return;
        }
        
        try {
            if (generateUnitTest) {
                List<GeneratedFile> unitTests = generateUnitTests(context);
                generatedFiles.addAll(unitTests);
                log.info("生成了 {} 个单元测试文件", unitTests.size());
            }
            
            if (generateIntegrationTest) {
                GeneratedFile integrationTest = generateIntegrationTest(context);
                if (integrationTest != null) {
                    generatedFiles.add(integrationTest);
                    log.info("生成集成测试文件: {}", integrationTest.getFilePath());
                }
            }
        } catch (Exception e) {
            log.error("生成测试代码失败", e);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 生成单元测试
     * 
     * @param context 模板上下文
     * @return 测试文件列表
     */
    private List<GeneratedFile> generateUnitTests(TemplateContext context) {
        List<GeneratedFile> testFiles = new java.util.ArrayList<>();
        String entityName = context.getTable().getEntityName();
        String packageName = context.getPackageName();
        
        // 生成DAO测试
        GeneratedFile daoTest = generateDaoTest(context);
        if (daoTest != null) {
            testFiles.add(daoTest);
        }
        
        // 生成Service测试
        GeneratedFile serviceTest = generateServiceTest(context);
        if (serviceTest != null) {
            testFiles.add(serviceTest);
        }
        
        // 生成Controller测试
        GeneratedFile controllerTest = generateControllerTest(context);
        if (controllerTest != null) {
            testFiles.add(controllerTest);
        }
        
        return testFiles;
    }
    
    /**
     * 生成DAO测试
     * 
     * @param context 模板上下文
     * @return DAO测试文件
     */
    private GeneratedFile generateDaoTest(TemplateContext context) {
        String entityName = context.getTable().getEntityName();
        String packageName = context.getPackageName();
        
        StringBuilder test = new StringBuilder();
        test.append("package ").append(packageName).append(".dao;\n\n");
        
        // 导入语句
        test.append("import org.junit.jupiter.api.Test;\n");
        test.append("import org.junit.jupiter.api.BeforeEach;\n");
        test.append("import org.springframework.boot.test.context.SpringBootTest;\n");
        test.append("import org.springframework.test.context.TestPropertySource;\n");
        test.append("import org.springframework.transaction.annotation.Transactional;\n");
        test.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        
        // 类定义
        test.append("/**\n");
        test.append(" * ").append(entityName).append("Dao 测试类\n");
        test.append(" * \n");
        test.append(" * @author ").append(context.getAuthor()).append("\n");
        test.append(" * @date ").append(context.getDate()).append("\n");
        test.append(" */\n");
        test.append("@SpringBootTest\n");
        test.append("@TestPropertySource(locations = \"classpath:application-test.properties\")\n");
        test.append("@Transactional\n");
        test.append("public class ").append(entityName).append("DaoTest {\n\n");
        
        test.append("    private ").append(entityName).append("Dao ").append(toLowerCase(entityName)).append("Dao;\n\n");
        
        test.append("    @BeforeEach\n");
        test.append("    void setUp() {\n");
        test.append("        // 初始化测试数据\n");
        test.append("    }\n\n");
        
        // 测试方法
        test.append("    @Test\n");
        test.append("    void testSave() {\n");
        test.append("        // TODO: 实现保存测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testFindById() {\n");
        test.append("        // TODO: 实现查询测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testUpdate() {\n");
        test.append("        // TODO: 实现更新测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testDelete() {\n");
        test.append("        // TODO: 实现删除测试\n");
        test.append("    }\n");
        
        test.append("}\n");
        
        String filePath = "src/test/java/" + packageName.replace('.', '/') + "/dao/" + entityName + "DaoTest.java";
        return new GeneratedFile(filePath, test.toString(), "DAO测试");
    }
    
    /**
     * 生成Service测试
     * 
     * @param context 模板上下文
     * @return Service测试文件
     */
    private GeneratedFile generateServiceTest(TemplateContext context) {
        String entityName = context.getTable().getEntityName();
        String packageName = context.getPackageName();
        
        StringBuilder test = new StringBuilder();
        test.append("package ").append(packageName).append(".service;\n\n");
        
        // 导入语句
        test.append("import org.junit.jupiter.api.Test;\n");
        test.append("import org.junit.jupiter.api.BeforeEach;\n");
        test.append("import org.mockito.Mock;\n");
        test.append("import org.mockito.MockitoAnnotations;\n");
        test.append("import static org.junit.jupiter.api.Assertions.*;\n");
        test.append("import static org.mockito.Mockito.*;\n\n");
        
        // 类定义
        test.append("/**\n");
        test.append(" * ").append(entityName).append("Service 测试类\n");
        test.append(" * \n");
        test.append(" * @author ").append(context.getAuthor()).append("\n");
        test.append(" * @date ").append(context.getDate()).append("\n");
        test.append(" */\n");
        test.append("public class ").append(entityName).append("ServiceTest {\n\n");
        
        test.append("    @Mock\n");
        test.append("    private ").append(entityName).append("Dao ").append(toLowerCase(entityName)).append("Dao;\n\n");
        
        test.append("    private ").append(entityName).append("Service ").append(toLowerCase(entityName)).append("Service;\n\n");
        
        test.append("    @BeforeEach\n");
        test.append("    void setUp() {\n");
        test.append("        MockitoAnnotations.openMocks(this);\n");
        test.append("        ").append(toLowerCase(entityName)).append("Service = new ").append(entityName).append("Service(").append(toLowerCase(entityName)).append("Dao);\n");
        test.append("    }\n\n");
        
        // 测试方法
        test.append("    @Test\n");
        test.append("    void testCreate() {\n");
        test.append("        // TODO: 实现创建测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testGetById() {\n");
        test.append("        // TODO: 实现查询测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testUpdate() {\n");
        test.append("        // TODO: 实现更新测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testDelete() {\n");
        test.append("        // TODO: 实现删除测试\n");
        test.append("    }\n");
        
        test.append("}\n");
        
        String filePath = "src/test/java/" + packageName.replace('.', '/') + "/service/" + entityName + "ServiceTest.java";
        return new GeneratedFile(filePath, test.toString(), "Service测试");
    }
    
    /**
     * 生成Controller测试
     * 
     * @param context 模板上下文
     * @return Controller测试文件
     */
    private GeneratedFile generateControllerTest(TemplateContext context) {
        String entityName = context.getTable().getEntityName();
        String packageName = context.getPackageName();
        
        StringBuilder test = new StringBuilder();
        test.append("package ").append(packageName).append(".controller;\n\n");
        
        // 导入语句
        test.append("import org.junit.jupiter.api.Test;\n");
        test.append("import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n");
        test.append("import org.springframework.boot.test.mock.mockito.MockBean;\n");
        test.append("import org.springframework.test.web.servlet.MockMvc;\n");
        test.append("import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;\n");
        test.append("import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;\n\n");
        
        // 类定义
        test.append("/**\n");
        test.append(" * ").append(entityName).append("Controller 测试类\n");
        test.append(" * \n");
        test.append(" * @author ").append(context.getAuthor()).append("\n");
        test.append(" * @date ").append(context.getDate()).append("\n");
        test.append(" */\n");
        test.append("@WebMvcTest(").append(entityName).append("Controller.class)\n");
        test.append("public class ").append(entityName).append("ControllerTest {\n\n");
        
        test.append("    private MockMvc mockMvc;\n\n");
        
        test.append("    @MockBean\n");
        test.append("    private ").append(entityName).append("Service ").append(toLowerCase(entityName)).append("Service;\n\n");
        
        // 测试方法
        test.append("    @Test\n");
        test.append("    void testCreate() throws Exception {\n");
        test.append("        // TODO: 实现创建接口测试\n");
        test.append("    }\n\n");
        
        test.append("    @Test\n");
        test.append("    void testGetById() throws Exception {\n");
        test.append("        // TODO: 实现查询接口测试\n");
        test.append("    }\n");
        
        test.append("}\n");
        
        String filePath = "src/test/java/" + packageName.replace('.', '/') + "/controller/" + entityName + "ControllerTest.java";
        return new GeneratedFile(filePath, test.toString(), "Controller测试");
    }
    
    /**
     * 生成集成测试
     * 
     * @param context 模板上下文
     * @return 集成测试文件
     */
    private GeneratedFile generateIntegrationTest(TemplateContext context) {
        String entityName = context.getTable().getEntityName();
        String packageName = context.getPackageName();
        
        StringBuilder test = new StringBuilder();
        test.append("package ").append(packageName).append(".integration;\n\n");
        
        // 导入语句
        test.append("import org.junit.jupiter.api.Test;\n");
        test.append("import org.springframework.boot.test.context.SpringBootTest;\n");
        test.append("import org.springframework.test.context.TestPropertySource;\n");
        test.append("import org.springframework.transaction.annotation.Transactional;\n\n");
        
        // 类定义
        test.append("/**\n");
        test.append(" * ").append(entityName).append(" 集成测试\n");
        test.append(" * \n");
        test.append(" * @author ").append(context.getAuthor()).append("\n");
        test.append(" * @date ").append(context.getDate()).append("\n");
        test.append(" */\n");
        test.append("@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)\n");
        test.append("@TestPropertySource(locations = \"classpath:application-test.properties\")\n");
        test.append("@Transactional\n");
        test.append("public class ").append(entityName).append("IntegrationTest {\n\n");
        
        test.append("    @Test\n");
        test.append("    void testCompleteWorkflow() {\n");
        test.append("        // TODO: 实现完整的业务流程测试\n");
        test.append("    }\n");
        
        test.append("}\n");
        
        String filePath = "src/test/java/" + packageName.replace('.', '/') + "/integration/" + entityName + "IntegrationTest.java";
        return new GeneratedFile(filePath, test.toString(), "集成测试");
    }
    
    /**
     * 转换为小写首字母
     * 
     * @param str 字符串
     * @return 小写首字母的字符串
     */
    private String toLowerCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}