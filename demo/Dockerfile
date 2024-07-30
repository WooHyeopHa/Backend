# 베이스 이미지 설정
FROM bellsoft/liberica-openjdk-alpine:17

# JAR 파일을 /app.jar로 복사
# (여기서는 JAR 파일의 정확한 경로를 지정해야 합니다)
COPY build/libs/your-application.jar /app.jar

# 포트 노출
EXPOSE 8080

# 컨테이너 실행 시 자바 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
