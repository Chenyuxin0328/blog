# 使用官方轻量 JDK 镜像
FROM openjdk:17

# 设置时区为中国（可选）
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制当前目录下的 jar 包到容器中
COPY blog-rear-0.0.1-SNAPSHOT.jar app.jar

# 使用 prod 配置运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
