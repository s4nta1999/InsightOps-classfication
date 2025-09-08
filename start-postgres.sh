#!/bin/bash

echo "🚀 하나카드 상담 분류 마이크로서비스 시작"
echo "=========================================="

# 환경 변수 파일 확인
if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다."
    echo "📝 env-docker-example.txt를 .env로 복사하고 OpenAI API 키를 설정하세요."
    echo "cp env-docker-example.txt .env"
    echo "그리고 .env 파일에서 OPENAI_API_KEY를 설정하세요."
    exit 1
fi

# OpenAI API 키 확인
if grep -q "your-openai-api-key-here" .env; then
    echo "❌ .env 파일에서 OpenAI API 키를 설정하세요."
    exit 1
fi

echo "✅ 환경 변수 설정 확인 완료"

# 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker-compose down -v

# PostgreSQL과 마이크로서비스 시작
echo "🐳 PostgreSQL과 마이크로서비스 시작 중..."
docker-compose up -d

# PostgreSQL 시작 대기
echo "⏳ PostgreSQL 시작 대기 중..."
sleep 10

# PostgreSQL 연결 확인
echo "🔍 PostgreSQL 연결 확인 중..."
if docker exec hanacard-postgres pg_isready -U hanacard_user -d hanacard_db; then
    echo "✅ PostgreSQL 연결 성공"
else
    echo "❌ PostgreSQL 연결 실패"
    docker-compose logs postgres
    exit 1
fi

# 마이크로서비스 시작 대기
echo "⏳ 마이크로서비스 시작 대기 중..."
sleep 20

# 마이크로서비스 상태 확인
echo "🔍 마이크로서비스 상태 확인 중..."
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ 마이크로서비스 시작 성공"
    echo ""
    echo "🌐 서비스 접속 정보:"
    echo "   - 마이크로서비스: http://localhost:8080"
    echo "   - API 문서: http://localhost:8080/api/"
    echo "   - 헬스체크: http://localhost:8080/api/health"
    echo ""
    echo "🗄️  데이터베이스 접속 정보:"
    echo "   - PostgreSQL: localhost:5432"
    echo "   - 데이터베이스: hanacard_db"
    echo "   - 사용자: hanacard_user"
    echo ""
    echo "📊 샘플 API 테스트:"
    echo "   curl http://localhost:8080/api/categories"
    echo "   curl http://localhost:8080/api/classify/statistics/category"
else
    echo "❌ 마이크로서비스 시작 실패"
    docker-compose logs microservice-classification
    exit 1
fi

echo ""
echo "🎉 모든 서비스가 정상적으로 시작되었습니다!"
