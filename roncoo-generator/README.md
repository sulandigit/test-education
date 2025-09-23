# Roncoo Generator 使用指南

## 概述

Roncoo Generator 是一个功能强大的代码生成器，专为 roncoo 教育平台的微服务架构设计。新版本采用模块化架构，支持插件扩展，提供灵活的配置管理。

## 主要特性

- 🎯 **模块化架构**：配置管理层、代码生成引擎、模板管理层、数据源适配层、插件系统
- 🔧 **配置驱动**：通过 YAML 配置文件控制生成行为，无需修改代码
- 🚀 **性能优化**：支持并发处理、模板缓存、数据库连接池
- 🔌 **插件系统**：内置代码格式化、文档生成、测试代码生成等插件
- 📊 **智能验证**：全面的配置验证和详细的错误诊断
- 🎨 **代码质量**：自动代码格式化、Swagger 注解生成、测试代码生成

## 快速开始

### 1. 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 2. 配置数据库

确保数据库连接信息正确：

```yaml
database:
  url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf-8&useSSL=false
  username: your_username
  password: your_password
  tableNames:
    - user
    - course
    - order
```

### 3. 配置项目信息

```yaml
project:
  projectName: your-project-name
  packagePrefix: com.yourcompany.yourproject
  packageName: module
  author: your-name
```

### 4. 运行代码生成

```java
// 加载配置
GeneratorConfig config = ConfigLoader.loadFromYaml("generator.yml");

// 创建生成引擎
GeneratorEngine engine = new GeneratorEngine(config);

try {
    // 执行生成
    GenerationResult result = engine.generate();
    
    // 输出结果
    System.out.println(result.getSummary());
} finally {
    // 关闭资源
    engine.close();
}
```

## 配置说明

### 数据库配置

| 配置项 | 说明 | 示例 |
|-------|------|------|
| `url` | 数据库连接URL | `jdbc:mysql://localhost:3306/db` |
| `username` | 数据库用户名 | `root` |
| `password` | 数据库密码 | `123456` |
| `tableNames` | 要生成的表名列表 | `[user, course]` 或 `[%]` |
| `tablePrefix` | 表名前缀 | `t_` |

### 项目配置

| 配置项 | 说明 | 示例 |
|-------|------|------|
| `projectName` | 项目名称 | `user-service` |
| `packagePrefix` | 包名前缀 | `com.roncoo.education` |
| `packageName` | 模块包名 | `user` |
| `author` | 作者信息 | `张三` |

### 模板配置

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| `templatePath` | 模板路径 | `template` |
| `engine` | 模板引擎 | `FREEMARKER` |
| `cacheEnabled` | 是否启用缓存 | `true` |

### 插件配置

内置插件：

1. **CodeFormatterPlugin**：代码格式化
2. **SwaggerGeneratorPlugin**：Swagger 文档生成
3. **DocumentGeneratorPlugin**：API 和数据库文档生成
4. **TestGeneratorPlugin**：测试代码生成

## 模板开发

### 模板目录结构

```
template/
├── core/           # 核心模板
│   ├── dao/
│   ├── service/
│   └── controller/
├── extension/      # 扩展模板
│   ├── admin/
│   ├── auth/
│   └── feign/
└── custom/         # 自定义模板
```

### 模板变量

常用模板变量：

| 变量 | 说明 | 示例 |
|------|------|------|
| `${table.tableName}` | 表名 | `user` |
| `${table.entityName}` | 实体类名 | `User` |
| `${table.tableComment}` | 表注释 | `用户表` |
| `${table.columns}` | 字段列表 | `List<ColumnMetadata>` |
| `${project.packageName}` | 包名 | `com.roncoo.education.user` |
| `${project.author}` | 作者 | `张三` |
| `${project.date}` | 生成日期 | `2024-01-01` |

### 模板示例

实体类模板 (`entity.java.ftl`)：

```freemarker
package ${project.packageName}.entity;

<#list table.importPackages as pkg>
import ${pkg};
</#list>
import lombok.Data;

/**
 * ${table.tableComment!}
 * 
 * @author ${project.author}
 * @date ${project.date}
 */
@Data
public class ${table.entityName} {

<#list table.columns as column>
    /**
     * ${column.columnComment!}
     */
    private ${column.propertyType} ${column.propertyName};

</#list>
}
```

## 插件开发

### 实现插件接口

```java
public class CustomPlugin implements GeneratorPlugin {
    
    @Override
    public String getName() {
        return "CustomPlugin";
    }
    
    @Override
    public void initialize(Map<String, Object> properties) {
        // 初始化插件
    }
    
    @Override
    public void afterGeneration(TemplateContext context, List<GeneratedFile> files) {
        // 代码生成后处理
    }
}
```

### 注册插件

在配置文件中添加：

```yaml
plugins:
  - name: CustomPlugin
    className: com.yourcompany.CustomPlugin
    enabled: true
    order: 500
    properties:
      customProperty: value
```

## 最佳实践

### 1. 配置管理

- 为不同环境创建不同的配置文件
- 使用环境变量管理敏感信息
- 定期验证配置文件的有效性

### 2. 模板组织

- 按功能模块组织模板
- 使用描述性的模板文件名
- 保持模板的简洁和可读性

### 3. 代码质量

- 启用代码格式化插件
- 生成完整的测试代码
- 添加详细的注释和文档

### 4. 性能优化

- 启用模板缓存
- 合理配置数据库连接池
- 使用并发生成提高效率

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接参数是否正确
   - 确认网络连通性

2. **模板渲染错误**
   - 检查模板语法是否正确
   - 验证模板变量是否存在
   - 查看详细的错误日志

3. **文件生成失败**
   - 检查输出目录权限
   - 确认磁盘空间是否充足
   - 验证文件路径是否正确

### 日志配置

配置日志级别获取详细信息：

```properties
logging.level.com.roncoo.generator=DEBUG
```

## 从旧版本迁移

### 配置迁移

1. 将 `config.properties` 转换为 `generator.yml`
2. 更新包结构和类名
3. 调整模板路径和文件名

### 代码迁移

1. 更新导入语句
2. 替换旧的 API 调用
3. 适配新的配置结构

### 模板迁移

1. 更新模板变量名称
2. 调整模板目录结构
3. 利用新的模板功能

## 技术支持

- 官方网站：https://www.roncoo.com
- 文档地址：https://docs.roncoo.com/generator
- 问题反馈：https://github.com/roncoo/roncoo-generator/issues

## 版本历史

### v2.0.0 (当前版本)
- 全新模块化架构
- 插件系统支持
- YAML 配置文件
- 性能优化和并发支持

### v1.x.x (legacy)
- 基础代码生成功能
- MyBatis Plus 集成
- Freemarker 模板引擎