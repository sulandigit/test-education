FROM openjdk:8-jdk-oracle
MAINTAINER roncoo

# 安装必要工具
RUN yum update -y && yum install -y curl && yum clean all

# 设置工作目录
WORKDIR /app

# 创建skywalking目录
RUN mkdir -p /skywalking-agent

# 复制应用JAR文件
ADD target/gateway.jar /app/gateway.jar

# 设置JVM参数和运行参数
ARG JAVA_OPTS="-Xmx128M"
ENV JAVA_OPTS=$JAVA_OPTS
ARG RUN_ARGS="--spring.profiles.active=prod"
ENV RUN_ARGS=$RUN_ARGS

# SkyWalking Agent环境变量
ENV SW_AGENT_NAME=education-gateway
ENV SW_AGENT_COLLECTOR_BACKEND_SERVICES=oap-server:11800
ENV SW_AGENT_SAMPLE=5
ENV SW_LOGGING_LEVEL=INFO

# 启动脚本
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -javaagent:/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=$SW_AGENT_NAME -Dskywalking.collector.backend_service=$SW_AGENT_COLLECTOR_BACKEND_SERVICES -jar /app/gateway.jar $RUN_ARGS"]
