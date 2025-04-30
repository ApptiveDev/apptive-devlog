FROM openjdk:21-jdk-slim

WORKDIR /app

ENV TZ=Asia/Seoul

# JAR 복사
COPY ./build/libs/devlog-0.0.1-SNAPSHOT.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
