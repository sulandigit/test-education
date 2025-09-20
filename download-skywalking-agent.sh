#!/bin/bash

# SkyWalking Agent 下载脚本
# 此脚本用于下载并配置SkyWalking Java Agent

SKYWALKING_VERSION="8.16.0"
SKYWALKING_AGENT_URL="https://archive.apache.org/dist/skywalking/${SKYWALKING_VERSION}/apache-skywalking-apm-${SKYWALKING_VERSION}.tar.gz"
AGENT_DIR="/data/workspace/test-education/skywalking-agent"

echo "正在下载 SkyWalking Agent ${SKYWALKING_VERSION}..."

# 创建临时目录
mkdir -p /tmp/skywalking-download
cd /tmp/skywalking-download

# 下载 SkyWalking 发行版
curl -L -o "apache-skywalking-apm-${SKYWALKING_VERSION}.tar.gz" "${SKYWALKING_AGENT_URL}"

# 验证下载是否成功
if [ ! -f "apache-skywalking-apm-${SKYWALKING_VERSION}.tar.gz" ]; then
    echo "错误: SkyWalking Agent 下载失败"
    exit 1
fi

# 解压文件
tar -xzf "apache-skywalking-apm-${SKYWALKING_VERSION}.tar.gz"

# 创建 Agent 目录
mkdir -p "${AGENT_DIR}"

# 复制 Agent 文件
cp -r "apache-skywalking-apm-bin/agent/"* "${AGENT_DIR}/"

# 设置执行权限
chmod +x "${AGENT_DIR}/skywalking-agent.jar"

echo "SkyWalking Agent 已成功下载并配置到: ${AGENT_DIR}"

# 清理临时文件
cd /data/workspace/test-education
rm -rf /tmp/skywalking-download

# 显示Agent结构
echo "Agent 目录结构:"
ls -la "${AGENT_DIR}"

echo "SkyWalking Agent 配置完成!"