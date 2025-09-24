# Java 17 环境配置指南

## 环境状态检查

当前环境中未检测到Java安装。Spring Boot 3.x要求Java 17或更高版本。

## Java 17 安装步骤

### Ubuntu/Debian 系统

```bash
# 1. 更新包管理器
sudo apt update

# 2. 安装OpenJDK 17
sudo apt install openjdk-17-jdk

# 3. 验证安装
java -version
javac -version

# 4. 配置JAVA_HOME (添加到 ~/.bashrc 或 ~/.profile)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 5. 重新加载环境变量
source ~/.bashrc
```

### CentOS/RHEL 系统

```bash
# 1. 安装OpenJDK 17
sudo yum install java-17-openjdk-devel

# 或者使用 dnf (较新版本)
sudo dnf install java-17-openjdk-devel

# 2. 配置JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### 多版本Java管理

如果系统中有多个Java版本，可以使用update-alternatives管理：

```bash
# 配置默认Java版本
sudo update-alternatives --config java
sudo update-alternatives --config javac

# 添加Java 17到alternatives
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-17-openjdk-amd64/bin/javac 1
```

## Maven 配置更新

项目POM文件已更新Java版本配置：

```xml
<properties>
    <java.version>17</java.version>
    <!-- 其他属性... -->
</properties>
```

## IDE 配置

### IntelliJ IDEA
1. File → Project Structure → Project Settings → Project
2. Project SDK: 选择Java 17
3. Project language level: 17

### Eclipse
1. Project → Properties → Java Build Path → Libraries
2. 移除旧的JRE，添加Java 17 JRE

### VS Code
在 `.vscode/settings.json` 中配置：
```json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "/usr/lib/jvm/java-17-openjdk-amd64",
            "default": true
        }
    ]
}
```

## 编译验证命令

安装Java 17后，使用以下命令验证项目编译：

```bash
# 清理并编译项目
mvn clean compile

# 验证依赖解析
mvn dependency:tree

# 运行测试
mvn test
```

## 常见问题解决

### 1. Java版本冲突
```bash
# 检查当前Java版本
java -version
javac -version

# 检查JAVA_HOME
echo $JAVA_HOME

# 检查PATH中的Java
which java
```

### 2. Maven不识别Java 17
确保Maven版本支持Java 17：
- Maven 3.8.1+ 推荐用于Java 17

### 3. 编译错误处理
如果遇到编译错误，可能需要：
1. 清理Maven缓存：`mvn clean`
2. 更新Maven wrapper：`./mvnw wrapper:wrapper -Dmaven=3.8.6`
3. 重新导入IDE项目

## 下一步操作

Java 17安装完成后，继续执行升级计划：
1. ✅ Java环境配置
2. ⏭️ Spring Boot 2.7.x中间版本升级
3. ⏭️ Jakarta EE命名空间迁移
4. ⏭️ Spring Boot 3.x核心升级

---

*配置指南生成时间：2025-09-24*