# Multi-stage build를 사용한 최적화된 Docker 이미지
FROM maven:3.9.5-eclipse-temurin-17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Maven 의존성 파일 복사 (캐싱 최적화)
COPY pom.xml .
COPY src ./src

# 애플리케이션 빌드 (CI/CD에서는 이미 빌드된 JAR 사용)
COPY target/microservice-classification-1.0.0.jar target/

# 실행 단계
FROM eclipse-temurin:17-jre

# 메타데이터 설정
LABEL maintainer="하나카드 상담 분류 마이크로서비스"
LABEL version="1.0.0"
LABEL description="하나카드 상담 내용을 AI로 분류하는 마이크로서비스"

# 작업 디렉토리 설정
WORKDIR /app

# 보안을 위한 non-root 사용자 생성
RUN groupadd -r appuser && useradd -r -g appuser appuser

# JAR 파일 복사
COPY --from=build /app/target/microservice-classification-1.0.0.jar app.jar

# 소유권 변경
RUN chown -R appuser:appuser /app

# non-root 사용자로 전환
USER appuser

# 포트 노출
EXPOSE 8080

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
