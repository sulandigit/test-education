# 领课教育平台单元测试执行指南

## 概述

本文档提供了领课教育平台单元测试的完整执行指南，包括测试运行、覆盖率分析和报告生成。

## 测试架构概览

### 测试金字塔分层
```
集成测试 (10%)
    ├── API端到端测试
    ├── 缓存集成测试
    └── 外部服务集成测试

服务层测试 (30%)
    ├── Controller层测试
    ├── Service集成测试
    └── 缓存层测试

单元测试 (60%)
    ├── Dao层测试
    ├── Biz层测试
    ├── 工具类测试
    └── 枚举常量测试
```

## 测试覆盖范围

### 用户服务模块测试
- **Dao层测试**: 9个测试类，150+ 测试用例
- **Biz层测试**: 6个测试类，120+ 测试用例  
- **Controller层测试**: 3个测试类，80+ 测试用例
- **工具类测试**: 5个测试类，60+ 测试用例
- **集成测试**: 3个测试类，40+ 测试用例

### 测试覆盖率目标

| 层级 | 覆盖率目标 | 关注重点 |
|------|------------|----------|
| Dao层 | 95% | CRUD操作、复杂查询、事务处理 |
| Biz层 | 90% | 业务逻辑、异常处理、边界条件 |
| Controller层 | 85% | 请求验证、响应格式、权限控制 |
| 工具类 | 100% | 算法正确性、边界值处理 |

## 快速开始

### 环境要求
- Java 8 或更高版本
- Maven 3.6 或更高版本
- Redis（用于缓存测试）

### 运行所有测试
```bash
# 使用提供的脚本（推荐）
./run-tests.sh

# 或手动执行
mvn clean test
```

### 运行特定模块测试
```bash
# 只运行用户服务测试
mvn test -pl roncoo-education-service/roncoo-education-service-user

# 运行特定测试类
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=UsersDaoTest

# 运行测试套件
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=UserServiceTestSuite
```

## 测试分类执行

### Dao层测试
```bash
# 运行所有Dao测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest="*DaoTest"

# 运行特定Dao测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=UsersDaoTest
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=OrderInfoDaoTest
```

### Biz层测试
```bash
# 运行所有Biz层测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest="*BizTest"

# 运行特定Biz测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=ApiUsersBizTest
```

### Controller层测试
```bash
# 运行所有Controller测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest="*ControllerTest"

# 运行特定Controller测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=ApiUsersControllerTest
```

### 工具类和枚举测试
```bash
# 运行工具类测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=NOUtilTest

# 运行枚举测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=EnumsTest
```

### 集成测试
```bash
# 运行所有集成测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest="*IntegrationTest"

# 运行缓存集成测试
mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=CacheIntegrationTest
```

## 测试报告生成

### 生成覆盖率报告
```bash
# 生成JaCoCo覆盖率报告
mvn clean test jacoco:report

# 聚合多模块覆盖率报告
mvn clean test jacoco:report-aggregate
```

### 报告位置
- **Surefire测试报告**: `target/surefire-reports/`
- **JaCoCo覆盖率报告**: `target/site/jacoco/`
- **聚合覆盖率报告**: `target/site/jacoco-aggregate/`

### HTML报告查看
```bash
# 在浏览器中打开覆盖率报告
open target/site/jacoco-aggregate/index.html  # macOS
xdg-open target/site/jacoco-aggregate/index.html  # Linux
start target/site/jacoco-aggregate/index.html  # Windows
```

## 持续集成配置

### Jenkins Pipeline示例
```groovy
pipeline {
    agent any
    
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco-aggregate',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
    }
}
```

### GitHub Actions示例
```yaml
name: Unit Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      redis:
        image: redis:6
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 8
      uses: actions/setup-java@v2
      with:
        java-version: '8'
        distribution: 'adopt'
    
    - name: Cache Maven packages
      uses: actions/cache@v2
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Run tests
      run: mvn clean test
    
    - name: Generate coverage report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v2
```

## 测试配置

### 测试环境配置
测试使用独立的配置文件：`src/test/resources/application-test.yml`

关键配置项：
- 使用H2内存数据库
- Redis使用15号数据库（避免冲突）
- 日志级别设为DEBUG
- 禁用真实短信发送

### Mock配置
- 外部服务调用使用Mock
- 文件上传使用测试桩
- 支付接口使用模拟响应

## 常见问题解决

### 测试失败排查
1. **数据库连接失败**
   ```bash
   # 检查H2数据库配置
   grep -r "h2" src/test/resources/
   ```

2. **Redis连接失败**
   ```bash
   # 启动Redis服务
   redis-server --port 6379 --daemonize yes
   ```

3. **依赖注入失败**
   ```bash
   # 检查Spring上下文配置
   mvn test -pl roncoo-education-service/roncoo-education-service-user -Dtest=BaseTest
   ```

### 性能优化
1. **并行执行测试**
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <configuration>
           <parallel>methods</parallel>
           <threadCount>4</threadCount>
       </configuration>
   </plugin>
   ```

2. **跳过慢速测试**
   ```bash
   mvn test -DexcludedGroups=integration
   ```

## 覆盖率分析

### 查看覆盖率详情
1. 打开 `target/site/jacoco-aggregate/index.html`
2. 点击包名查看详细覆盖情况
3. 红色表示未覆盖，绿色表示已覆盖

### 覆盖率要求验证
```bash
# 检查是否达到目标覆盖率
mvn jacoco:check
```

## 最佳实践

### 测试编写规范
1. 使用 `@DisplayName` 提供清晰的测试描述
2. 遵循 Given-When-Then 模式
3. 一个测试方法只测试一个场景
4. 使用有意义的断言消息

### 测试数据管理
1. 使用测试数据工厂生成测试数据
2. 每个测试方法后清理数据
3. 避免测试间的数据依赖

### Mock使用原则
1. 只Mock外部依赖
2. 验证重要的交互
3. 避免过度Mock

## 维护和更新

### 定期检查
- 每月检查测试覆盖率趋势
- 识别未覆盖的关键代码路径
- 更新过时的测试用例

### 重构指导
- 代码重构后及时更新测试
- 保持测试和业务代码同步
- 定期清理无用的测试代码

---

## 联系方式
如有测试相关问题，请联系开发团队或查看项目文档。