# 하나카드 상담 분류 마이크로서비스

## 📋 프로젝트 개요

하나카드 상담 내용을 AI로 자동 분류하고 분석하는 마이크로서비스입니다. OpenAI GPT-4o-mini를 활용하여 상담 내용을 25개 카테고리로 분류하고, 문제 상황, 해결 방안, 예상 결과를 분석합니다.

## 🚀 주요 기능

- **AI 기반 상담 분류**: OpenAI GPT-4o-mini를 활용한 25개 카테고리 자동 분류
- **상세 분석**: 문제 상황, 해결 방안, 예상 결과 분석
- **정보 추출**: 카드 타입, 이슈 타입, 고객 감정 등 자동 추출
- **데이터 저장**: PostgreSQL을 활용한 분류 결과 저장 및 조회
- **통계 분석**: 카테고리별, 긴급도별 통계 제공
- **RESTful API**: 9개 엔드포인트로 완전한 API 서비스 제공

## 🛠 기술 스택

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: PostgreSQL 15 (TEXT 컬럼으로 JSON 저장)
- **AI**: OpenAI GPT-4o-mini
- **Container**: Docker, Docker Compose
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven

## 📊 API 엔드포인트 (9개)

### 🔍 분류 API
- `POST /api/classify` - 기본 상담 분류 (DB 저장 없음)
- `POST /api/enhanced-classify` - 향상된 상담 분류 + DB 저장 ⭐

### 📋 조회 API
- `GET /api/classify/{id}` - 특정 분류 결과 조회
- `GET /api/classify/history` - 분류 이력 조회 (페이징 지원)
- `GET /api/categories` - 25개 상담 카테고리 목록 조회

### 📈 통계 API
- `GET /api/classify/statistics/category` - 카테고리별 분류 통계
- `GET /api/classify/statistics/urgency` - 긴급도별 분류 통계

### 🔧 시스템 API
- `GET /api/health` - 서비스 상태 및 버전 정보
- `GET /api/` - 서비스 기본 정보 및 엔드포인트 목록

## 🏷️ 지원 카테고리 (25개)

1. 도난/분실 신청/해제
2. 이용내역 안내
3. 승인취소/매출취소 안내
4. 한도상향 접수/처리
5. 선결제/즉시출금
6. 한도 안내
7. 가상계좌 안내
8. 결제계좌 안내/변경
9. 서비스 이용방법 안내
10. 결제대금 안내
11. 연체대금 즉시출금
12. 포인트/마일리지 전환등록
13. 증명서/확인서 발급
14. 가상계좌 예약/취소
15. 단기카드대출 안내/실행
16. 장기카드대출 안내
17. 정부지원 바우처 (등유, 임신 등)
18. 이벤트 안내
19. 심사 진행사항 안내
20. 도시가스
21. 일부결제 대금이월약정 안내
22. 일부결제대금이월약정 해지
23. 결제일 안내/변경
24. **약관 안내** (최신 추가)
25. **상품 안내** (최신 추가)

## 🐳 Docker 실행

### 🚀 빠른 시작 (Docker Compose)
```bash
# 1. 환경변수 설정
export OPENAI_API_KEY=your_api_key_here

# 2. 서비스 실행 (PostgreSQL + Spring Boot)
docker-compose up -d

# 3. 서비스 확인
curl http://localhost:8082/api/health
```

### 📦 Docker Hub 이미지 직접 사용
```bash
# 단일 컨테이너 실행 (외부 DB 필요)
docker run -d --name hanacard \
  -p 8082:8080 \
  -e OPENAI_API_KEY=your_api_key_here \
  s4nta1207/hanacard-classification:latest
```

### 🌐 프론트엔드 테스트
```bash
# 프론트엔드 서버 실행 (별도 터미널)
cd front
python3 -m http.server 3000

# 브라우저에서 접속
# http://localhost:3000
```

### 📊 샘플 데이터 적재
```bash
# 1. 서비스 실행 후 데이터 적재
cd data
python3 load_sample_data.py

# 2. 테스트용으로 일부 데이터만 적재
python3 load_sample_data.py --max-files 10

# 3. Docker Compose로 자동 데이터 적재
docker-compose -f data/docker-compose-with-data.yml up -d
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

### voc_normalized 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| `id` | BIGINT | 고유 식별자 (Primary Key) |
| `source_id` | VARCHAR(100) | 상담 소스 ID |
| `consulting_content` | TEXT | 상담 내용 (원본 텍스트) |
| `processing_time` | DOUBLE | AI 처리 시간 (초) |
| `consulting_date` | DATE | 상담 날짜 |
| `consulting_time` | TIME | 상담 시간 |
| `client_gender` | VARCHAR(10) | 고객 성별 |
| `client_age` | VARCHAR(10) | 고객 연령대 |
| `consulting_turns` | INTEGER | 상담 턴 수 |
| `consulting_length` | INTEGER | 상담 길이 |
| `consulting_category` | VARCHAR(100) | **상담 카테고리 (별도 컬럼)** |
| `created_at` | TIMESTAMP | 레코드 생성 시간 |
| `updated_at` | TIMESTAMP | 레코드 수정 시간 |
| `analysis_result` | JSONB | **AI 분석 결과 (JSONB)** |

### 📊 analysis_result JSON 구조 (간소화됨)
```json
{
  "classification": {
    "confidence": 0.95,
    "alternative_categories": [
      {
        "category": "카드 정지/해제",
        "confidence": 0.03
      },
      {
        "category": "인증 관련",
        "confidence": 0.02
      }
    ]
  },
  "analysis": {
    "problem_situation": "고객이 카드 도난 신고 후 정지 해제 요청",
    "solution_approach": "신분증 인증 후 카드 정지 해제 처리",
    "expected_outcome": "카드 정상 사용 가능"
  }
}
```

**참고**: `category`는 `consulting_category` 컬럼에 별도 저장되므로 JSON에서 제거됨

## 🔧 개발 환경 설정

### 🔐 환경변수 설정
```bash
# 1. 환경변수 파일 복사
cp env-example.sh env-local.sh

# 2. 환경변수 파일 편집 (실제 값으로 수정)
nano env-local.sh

# 3. 환경변수 로드
source env-local.sh

# 4. 환경변수 확인
echo $OPENAI_API_KEY
```

### 로컬 개발
```bash
# 1. 환경변수 로드
source env-local.sh

# 2. Maven 빌드
mvn clean package

# 3. Spring Boot 실행
mvn spring-boot:run
```

### 테스트
```bash
# 단위 테스트
mvn test

# 통합 테스트
mvn verify
```

## 📈 배포 정보

### 🐳 Docker Hub
- **이미지**: `s4nta1207/hanacard-classification:latest`
- **상태**: ✅ 업로드 완료
- **버전**: 최신 (약관 안내, 상품 안내 카테고리 포함)

### ☁️ 클라우드 배포 옵션
- **Azure Container Instances**: 단일 컨테이너 배포
- **Azure Container Apps**: 마이크로서비스 배포
- **Azure App Service**: 웹 앱으로 배포
- **Azure Database for PostgreSQL**: 관리형 데이터베이스

### 🔗 GitHub 저장소
- **URL**: [https://github.com/s4nta1999/InsightOps-classfication.git](https://github.com/s4nta1999/InsightOps-classfication.git)
- **상태**: ✅ 코드 업로드 완료
- **문서**: 상세한 README.md 포함

## 🧪 API 테스트 예시

### 기본 분류 요청
```bash
curl -X POST http://localhost:8082/api/classify \
  -H "Content-Type: application/json" \
  -d '{
    "source_id": "test001",
    "consulting_content": "카드 도난 신고하고 싶습니다."
  }'
```

### 향상된 분류 + 저장 요청
```bash
curl -X POST http://localhost:8082/api/enhanced-classify \
  -H "Content-Type: application/json" \
  -d '{
    "source_id": "test001",
    "consulting_content": "카드 도난 신고하고 싶습니다.",
    "consulting_date": "2025-01-15",
    "consulting_time": "14:30",
    "client_gender": "여자",
    "client_age": "30대",
    "consulting_turns": 40,
    "consulting_length": 202
  }'
```

## 📝 라이선스

MIT License

## 👥 기여자

- s4nta1999

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 생성해 주세요.
