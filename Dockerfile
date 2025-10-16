FROM eclipse-temurin:21-jdk

WORKDIR /app

# JAR 파일 복사
ARG JAR_FILE=./target/1lluwa-product-service.jar
COPY ${JAR_FILE} /app/1lluwa-product-service.jar

# 로그 디렉토리 (호스트와 마운트 가능)
VOLUME /home/be10/be10-team1/logs

# 포트 노출
EXPOSE 10307 10308

# 환경변수
ENV SERVER_PORT=10307

# 실행
ENTRYPOINT ["java", "-jar", "/app/1lluwa-auth-service.jar", "--spring.profiles.active=prod"]
CMD ["--server.port=${SERVER_PORT}"]