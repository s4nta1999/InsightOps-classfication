# 하나카드 상담 분류 마이크로서비스 (Spring Boot)

이 프로젝트는 하나카드 상담 내용(`consulting_content`)을 분석하여 미리 정의된 `consulting_category` 중 하나로 분류하는 Spring Boot 기반 마이크로서비스입니다.

## 🚀 **Spring Boot 전환 완료**

기존 Node.js + Express.js 프로젝트를 **Spring Boot 3.2.0** 기반으로 성공적으로 전환했습니다.

### ✅ **전환된 주요 기능**
- **상담 내용 분류**: GPT-4o-mini API를 활용한 AI 기반 분류
- **23개 상담 카테고리**: 하나카드 상담 유형별 자동 분류
- **RESTful API**: 표준 HTTP 메서드를 사용한 API 설계
- **보안 강화**: Spring Security 기반 API 키 관리
- **에러 처리**: 전역 예외 처리 및 상세한 에러 메시지

## 🏗️ **프로젝트 구조**

```
src/
├── main/
│   ├── java/com/hanacard/
│   │   ├── ClassificationApplication.java     # 메인 애플리케이션
│   │   ├── controller/
│   │   │   └── ClassificationController.java  # API 컨트롤러
│   │   ├── service/
│   │   │   └── OpenAIService.java            # OpenAI API 연동 서비스
│   │   ├── dto/
│   │   │   ├── ClassificationRequest.java     # 요청 DTO
│   │   │   ├── ClassificationResponse.java    # 응답 DTO
│   │   │   └── ApiResponse.java              # 공통 응답 DTO
│   │   ├── constants/
│   │   │   └── ConsultingCategories.java     # 상담 카테고리 상수
│   │   ├── config/
│   │   │   ├── OpenAIConfig.java             # OpenAI 설정
│   │   │   └── WebConfig.java                # 웹 설정
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java   # 전역 예외 처리
│   └── resources/
│       ├── application.yml                    # 메인 설정
│       └── application-dev.yml                # 개발 환경 설정
└── test/
    └── java/com/hanacard/
        ├── ClassificationApplicationTests.java # 통합 테스트
        └── controller/
            └── ClassificationControllerTest.java # 컨트롤러 테스트
```

## 🔧 **기술 스택**

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web MVC**
- **Spring Validation**
- **Spring Actuator**
- **OpenAI GPT-4o-mini API**
- **Maven**
- **JUnit 5**

## 📋 **API 엔드포인트**

### 1. **POST `/api/classify` - 상담 내용 분류**
상담 내용을 AI로 분석하여 카테고리로 분류

**Request Body:**
```json
{
  "source_id": "200001",
  "consulting_content": "카드 분실 신고를 하고 싶습니다.",
  "consulting_date": "2025-01-27",
  "consulting_time": "14:30",
  "metadata": {
    "client_gender": "여자",
    "client_age": "30대",
    "consulting_turns": "40",
    "consulting_length": 202
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "source_id": "200001",
    "consulting_category": "도난/분실 신청/해제",
    "confidence": 0.95,
    "processing_time": 1.2,
    "consulting_date": "2025-01-27",
    "consulting_time": "14:30"
  }
}
```

### 2. **GET `/api/health` - 서비스 상태 확인**
마이크로서비스의 상태 및 가용성 확인

### 3. **GET `/api/categories` - 카테고리 목록 조회**
사용 가능한 상담 카테고리 목록 제공

### 4. **GET `/` - 루트 엔드포인트**
서비스 정보 및 사용 가능한 엔드포인트 안내

## 🚀 **실행 방법**

### **1. 환경 설정**
```bash
# 환경 변수 설정
export OPENAI_API_KEY="your-openai-api-key"
export OPENAI_MODEL="gpt-4o-mini"
export SERVER_PORT=8080
```

### **2. 프로젝트 빌드 및 실행**
```bash
# 의존성 설치 및 빌드
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run

# 또는 JAR 파일로 실행
java -jar target/microservice-classification-1.0.0.jar
```

### **3. 개발 모드 실행**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔒 **보안 및 설정**

### **환경 변수**
- `OPENAI_API_KEY`: OpenAI API 키 (필수)
- `OPENAI_MODEL`: 사용할 AI 모델 (기본값: gpt-4o-mini)
- `SERVER_PORT`: 서버 포트 (기본값: 8080)

### **보안 기능**
- **API 키 검증**: OpenAI API 키 유효성 검사
- **입력 검증**: Spring Validation을 통한 요청 데이터 검증
- **CORS 설정**: 웹 브라우저 보안 정책 준수
- **에러 처리**: 민감한 정보 노출 방지

## 🧪 **테스트**

### **단위 테스트 실행**
```bash
mvn test
```

### **통합 테스트 실행**
```bash
mvn verify
```

## 📊 **모니터링**

### **Actuator 엔드포인트**
- `/actuator/health`: 서비스 상태
- `/actuator/info`: 서비스 정보
- `/actuator/metrics`: 성능 메트릭

### **로깅**
- SLF4J + Logback을 통한 구조화된 로깅
- 개발/운영 환경별 로그 레벨 설정

## 🔄 **기존 Node.js 프로젝트와의 차이점**

| 구분 | Node.js (기존) | Spring Boot (현재) |
|------|----------------|-------------------|
| **언어** | TypeScript/JavaScript | Java 17 |
| **프레임워크** | Express.js | Spring Boot |
| **런타임** | Node.js | JVM |
| **보안** | 기본 보안 | Spring Security 기반 |
| **에러 처리** | 미들웨어 | 전역 예외 처리 |
| **검증** | 수동 검증 | Bean Validation |
| **테스트** | Jest | JUnit 5 + MockMvc |
| **설정** | 환경 변수 | YAML + ConfigurationProperties |

## 🎯 **장점**

1. **보안 강화**: Spring Security 기반 안전한 API 키 관리
2. **안정성**: JVM의 안정성과 Spring Boot의 검증된 아키텍처
3. **확장성**: Spring Cloud로 마이크로서비스 확장 가능
4. **기업 환경 적합**: Java 생태계와의 호환성
5. **에러 처리**: 체계적인 예외 처리 및 로깅

## 🚧 **향후 개선 계획**

1. **Spring Security**: JWT 기반 인증/인가 추가
2. **데이터베이스**: 상담 분류 결과 저장 및 분석
3. **캐싱**: Redis를 통한 응답 캐싱
4. **모니터링**: Prometheus + Grafana 연동
5. **API 문서**: Swagger/OpenAPI 문서화

## 📞 **지원**

프로젝트 관련 문의사항이나 개선 제안이 있으시면 언제든 연락주세요.

---

**Spring Boot 전환 완료!** 🎉
기존 Node.js 프로젝트의 모든 문제점을 해결하고, 더 안전하고 안정적인 마이크로서비스를 구축했습니다.
