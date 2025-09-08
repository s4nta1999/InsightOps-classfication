# 하나카드 상담 분류 마이크로서비스

## 📋 프로젝트 개요

하나카드 상담 내용을 AI로 자동 분류하고 분석하는 마이크로서비스입니다.

## 🚀 주요 기능

- **AI 기반 상담 분류**: OpenAI GPT-4o-mini를 활용한 25개 카테고리 자동 분류
- **상세 분석**: 문제 상황, 해결 방안, 예상 결과 분석
- **정보 추출**: 카드 타입, 이슈 타입, 고객 감정 등 자동 추출
- **데이터 저장**: PostgreSQL을 활용한 분류 결과 저장 및 조회
- **통계 분석**: 카테고리별, 긴급도별 통계 제공

## 🛠 기술 스택

- **Backend**: Spring Boot 3.x, Java 17
- **Database**: PostgreSQL 15 (JSONB 지원)
- **AI**: OpenAI GPT-4o-mini
- **Container**: Docker, Docker Compose
- **ORM**: JPA/Hibernate

## 📊 API 엔드포인트

### 기본 분류
- `POST /api/classify` - 기본 상담 분류

### 향상된 분류 + 저장
- `POST /api/enhanced-classify` - 상담 분류 및 DB 저장
- `GET /api/classify/{id}` - 분류 결과 조회
- `GET /api/classify/history` - 분류 이력 조회

### 통계
- `GET /api/classify/statistics/category` - 카테고리별 통계
- `GET /api/classify/statistics/urgency` - 긴급도별 통계

### 시스템
- `GET /api/health` - 서비스 상태 확인
- `GET /api/categories` - 카테고리 목록 조회
- `GET /api/` - 서비스 정보

## 🐳 Docker 실행

### Docker Compose 사용
```bash
# 환경변수 설정
export OPENAI_API_KEY=your_api_key_here

# 서비스 실행
docker-compose up -d
```

### Docker Hub 이미지 사용
```bash
docker run -d --name hanacard \
  -p 8082:8080 \
  -e OPENAI_API_KEY=your_api_key_here \
  s4nta1207/hanacard-classification:latest
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/hanacard/
│   │   ├── controller/     # REST API 컨트롤러
│   │   ├── service/        # 비즈니스 로직
│   │   ├── entity/         # JPA 엔티티
│   │   ├── repository/     # 데이터 접근 계층
│   │   ├── dto/           # 데이터 전송 객체
│   │   ├── constants/     # 상수 정의
│   │   └── utils/         # 유틸리티
│   └── resources/
│       ├── application.yml # 설정 파일
│       └── application-docker.yml
├── test/                  # 테스트 코드
front/                     # 프론트엔드 (API 테스트용)
docker-compose.yml         # Docker Compose 설정
Dockerfile                 # Docker 이미지 빌드
```

## 🗄 데이터베이스 구조

### consulting_classifications 테이블
- `id`: 고유 식별자
- `source_id`: 상담 소스 ID
- `consulting_content`: 상담 내용
- `analysis_result`: 분석 결과 (JSON)
- `metadata`: 메타데이터 (JSON)
- `consulting_date/time`: 상담 날짜/시간
- `created_at/updated_at`: 생성/수정 시간

## 🔧 개발 환경 설정

### 로컬 개발
```bash
# Maven 빌드
mvn clean package

# Spring Boot 실행
mvn spring-boot:run
```

### 테스트
```bash
# 단위 테스트
mvn test

# 통합 테스트
mvn verify
```

## 📈 배포

### Docker Hub
- 이미지: `s4nta1207/hanacard-classification:latest`
- 자동 빌드 및 배포 설정

### Azure 배포
- Azure Container Instances
- Azure Database for PostgreSQL
- Azure App Service

## 📝 라이선스

MIT License

## 👥 기여자

- s4nta1999

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 생성해 주세요.
