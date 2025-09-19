#!/bin/bash

# 领课教育平台单元测试执行脚本
# 使用此脚本运行完整的测试套件并生成覆盖率报告

# 设置错误处理
set -e

echo "=================================="
echo "领课教育平台单元测试执行开始"
echo "=================================="

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: Maven未安装，请先安装Maven"
    exit 1
fi

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: Java未安装，请先安装Java 8或更高版本"
    exit 1
fi

# 进入项目根目录
cd "$(dirname "$0")/.."

# 清理之前的构建
echo "清理之前的构建..."
mvn clean

# 运行用户服务的单元测试
echo "执行用户服务单元测试..."
mvn test -pl roncoo-education-service/roncoo-education-service-user

# 运行课程服务的单元测试（如果存在）
if [ -d "roncoo-education-service/roncoo-education-service-course/src/test" ]; then
    echo "执行课程服务单元测试..."
    mvn test -pl roncoo-education-service/roncoo-education-service-course
fi

# 运行系统服务的单元测试（如果存在）
if [ -d "roncoo-education-service/roncoo-education-service-system/src/test" ]; then
    echo "执行系统服务单元测试..."
    mvn test -pl roncoo-education-service/roncoo-education-service-system
fi

# 生成测试覆盖率报告
echo "生成测试覆盖率报告..."
mvn jacoco:report

# 聚合所有模块的覆盖率报告
echo "聚合覆盖率报告..."
mvn jacoco:report-aggregate

echo "=================================="
echo "测试执行完成！"
echo "=================================="

# 显示测试结果摘要
echo "测试结果位置:"
echo "- 用户服务测试报告: roncoo-education-service/roncoo-education-service-user/target/surefire-reports/"
echo "- 覆盖率报告: target/site/jacoco-aggregate/"

# 检查覆盖率目标
echo ""
echo "覆盖率目标验证:"
echo "- Dao层目标: 95%"
echo "- Biz层目标: 90%" 
echo "- Controller层目标: 85%"
echo "- 工具类目标: 100%"

# 可选：在浏览器中打开覆盖率报告
if command -v open &> /dev/null; then
    echo "正在打开覆盖率报告..."
    open target/site/jacoco-aggregate/index.html
elif command -v xdg-open &> /dev/null; then
    echo "正在打开覆盖率报告..."
    xdg-open target/site/jacoco-aggregate/index.html
fi

echo "测试执行脚本完成！"