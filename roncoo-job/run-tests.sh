#!/bin/bash

# roncoo-job 单元测试运行脚本
# 运行前请确保已安装 Java 8+ 和 Maven 3+

echo "==================== roncoo-job 单元测试运行脚本 ===================="
echo "检查 Java 环境..."

# 检查 Java 版本
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java。请安装 Java 8 或更高版本。"
    exit 1
fi

java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Java 版本: $java_version"

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到 Maven。请安装 Maven 3.0 或更高版本。"
    exit 1
fi

mvn_version=$(mvn -version | head -n 1)
echo "Maven 版本: $mvn_version"

echo ""
echo "开始运行单元测试..."
echo "===================="

# 进入项目目录
cd "$(dirname "$0")"

# 编译项目
echo "1. 编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "编译失败，请检查代码。"
    exit 1
fi

# 编译测试代码
echo ""
echo "2. 编译测试代码..."
mvn test-compile

if [ $? -ne 0 ]; then
    echo "测试代码编译失败，请检查测试代码。"
    exit 1
fi

# 运行测试
echo ""
echo "3. 运行单元测试..."
mvn test

test_result=$?

echo ""
echo "===================="
if [ $test_result -eq 0 ]; then
    echo "✅ 所有测试运行成功！"
    echo ""
    echo "测试报告位置:"
    echo "- Surefire 报告: target/surefire-reports/"
    echo "- 详细日志: target/surefire-reports/TEST-*.xml"
else
    echo "❌ 测试运行失败，请检查测试结果。"
    echo ""
    echo "失败详情请查看:"
    echo "- target/surefire-reports/ 目录下的报告文件"
fi

echo ""
echo "测试覆盖的组件:"
echo "- XxlJobInfo 模型类"
echo "- JacksonUtil 工具类"
echo "- CronExpression 调度表达式"
echo "- XxlJobServiceImpl 服务实现"
echo "- ExecutorRoute* 路由策略"
echo "- JobInfoController 控制器"

echo ""
echo "如需查看测试覆盖率，可以运行:"
echo "mvn jacoco:report"
echo "然后查看 target/site/jacoco/index.html"

exit $test_result