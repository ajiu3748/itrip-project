#运行项目需要基于java镜像
FROM java:8
# 设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
#拷贝文件到容器
ADD target/itrip-gateway-0.0.1-SNAPSHOT.jar /itrip-gateway-0.0.1-SNAPSHOT.jar
#配置容器启动后执行的命令
ENTRYPOINT ["java","-jar","/itrip-gateway-0.0.1-SNAPSHOT.jar","--spring.profiles.active=test"]