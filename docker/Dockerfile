# 设置基础Java环境
FROM eclipse-temurin:17-jdk

# 作者信息
MAINTAINER sadjier

# 把项目jar包文件复制到镜像内部，并重命名
COPY sale_system-1.0.0.jar sale_system.jar

# 启动命令
ENTRYPOINT ["java","-jar","sale_system.jar"]

# 声明容器端口
EXPOSE 8080