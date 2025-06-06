# 최신 21-jdk-alpine 이미지로부터 시작
FROM openjdk:21-jdk-slim

EXPOSE 8080

# 작업 디렉토리를 /app으로 설정
WORKDIR /app

# 현재 디렉토리의 모든 파일을 컨테이너의 /app 디렉토리로 복사
COPY . .

#ENV SPRING_PROFILES_ACTIVE=prod
# 빌드된 JAR 파일을 컨테이너로 복사
ARG JAR_FILE=build/libs/devlog-0.0.1-SNAPSHOT.jar
RUN mv ${JAR_FILE} app.jar

# 컨테이너가 실행될 때 실행될 명령어 지정
# 오류남! 왜냐하면, spring.profiles.active=prod를 사용하려면, application.properties에 spring.profiles.active=prod를 추가해야함
ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","app.jar"]