# 考试模块 (Exam Module)

## 项目简介

考试模块是领课教育系统的重要组成部分，提供了完整的在线考试功能，包括考试管理、试卷管理、题目管理、考试记录等核心功能。

## 功能特性

### 1. 考试管理
- 创建和管理考试
- 设置考试时间、时长、及格分数
- 支持练习模式和考试模式
- 考试次数限制设置
- 题目和选项顺序随机化

### 2. 试卷管理
- 创建和管理试卷
- 支持固定试卷和随机试卷
- 题目分值和难度设置

### 3. 题目管理
- 支持多种题型：单选题、多选题、判断题、填空题、简答题
- 题目解析功能
- 题目难度分级

### 4. 考试记录
- 详细的考试记录跟踪
- 答题记录和统计
- 自动阅卷功能
- 成绩统计分析

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.6.3
- **微服务**: Spring Cloud Alibaba 2021.0.1.0
- **数据库**: MySQL
- **ORM**: MyBatis
- **服务发现**: Nacos
- **API文档**: Swagger

### 模块结构
```
roncoo-education-service-exam/
├── src/main/java/com/roncoo/education/exam/
│   ├── dao/                    # 数据访问层
│   │   ├── impl/              # DAO实现
│   │   └── mapper/            # MyBatis Mapper
│   ├── service/               # 业务服务层
│   │   ├── admin/             # 管理端服务
│   │   ├── api/               # 前端API服务
│   │   └── feign/             # Feign接口服务
│   └── ExamServiceApplication.java
├── src/main/resources/
│   ├── bootstrap.properties   # 配置文件
│   └── mybatis/              # MyBatis映射文件
└── src/test/java/            # 单元测试
```

## 数据库设计

### 核心表结构
1. **exam**: 考试信息表
2. **exam_paper**: 试卷信息表
3. **exam_question**: 考试题目表
4. **exam_answer**: 题目答案选项表
5. **exam_record**: 考试记录表
6. **exam_question_record**: 答题记录表

详细表结构请参考 `exam_tables.sql` 文件。

## API接口

### 管理端接口 (/exam/admin/exam)
- `POST /page` - 考试信息分页查询
- `POST /save` - 添加考试信息
- `GET /view` - 查看考试信息
- `PUT /edit` - 修改考试信息
- `DELETE /delete` - 删除考试信息

### 前端接口 (/exam/api/exam)
- `GET /listByCourseId` - 根据课程ID获取考试列表
- `GET /detail` - 获取考试详情
- `POST /start` - 开始考试
- `POST /submit` - 提交考试

### Feign接口 (/exam/feign)
- `GET /exam/listByCourseId` - 根据课程ID获取考试列表
- `GET /exam/getById` - 根据ID获取考试信息
- `GET /examRecord/listByUserId` - 根据用户ID获取考试记录
- `GET /examRecord/getUserExamStats` - 获取用户考试统计信息

## 配置说明

### 服务配置
- **服务名**: service-exam
- **端口**: 7740
- **注册中心**: Nacos

### 数据库配置
通过Nacos配置中心管理数据库连接信息。

## 部署说明

### 1. 环境要求
- JDK 1.8+
- MySQL 8.0+
- Nacos 2.0+

### 2. 数据库初始化
执行 `exam_tables.sql` 脚本创建数据库表。

### 3. 配置文件
在Nacos配置中心添加相应的配置文件：
- `application.properties`
- `service-exam.properties`

### 4. 启动服务
```bash
java -jar roncoo-education-service-exam.jar
```

## 使用示例

### 创建考试
```java
AdminExamSaveReq req = new AdminExamSaveReq();
req.setExamName("Java基础考试");
req.setExamDesc("Java基础知识测试");
req.setCourseId(1L);
req.setPaperId(1L);
req.setDuration(60);
req.setPassScore(60);
req.setTotalScore(100);
req.setExamType(1);

Result<String> result = adminExamBiz.save(req);
```

### 开始考试
```java
ApiExamStartReq req = new ApiExamStartReq();
req.setUserId(1L);
req.setExamId(1L);

Result<ApiExamStartResp> result = apiExamBiz.startExam(req);
```

## 测试

项目包含完整的单元测试，覆盖了核心业务逻辑：
- DAO层测试
- 业务逻辑层测试
- 控制器层测试

运行测试：
```bash
mvn test
```

## 注意事项

1. **考试时间控制**: 需要实现考试超时自动提交功能
2. **并发处理**: 需要考虑高并发场景下的数据一致性
3. **自动阅卷**: 目前只提供了基础框架，需要完善自动阅卷算法
4. **权限控制**: 需要集成用户权限验证
5. **数据备份**: 考试数据需要定期备份

## 扩展功能

以下功能可在后续版本中实现：
1. 考试监控和防作弊
2. 成绩分析和报表
3. 题库管理功能
4. 在线阅卷功能
5. 移动端适配

## 联系信息

如有问题或建议，请联系开发团队。