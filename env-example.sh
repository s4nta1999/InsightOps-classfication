# ============================================
# 하나카드 상담 분류 마이크로서비스 환경변수 예시
# ============================================

# 데이터베이스 설정
export SPRING_DATASOURCE_URL="jdbc:mysql://your-database-server:3306/your-database?serverTimezone=UTC&useSSL=true&allowPublicKeyRetrieval=true&requireSSL=true"
export SPRING_DATASOURCE_USERNAME="your-database-username"
export SPRING_DATASOURCE_PASSWORD="your-database-password"

# JPA 설정
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"

# OpenAI API 설정 (Azure 환경변수에서 관리)
# export OPENAI_API_KEY="your-openai-api-key-here"
export OPENAI_MODEL="gpt-4o-mini"

# 외부 API 설정
export ADMIN_API_BASE_URL="https://your-admin-service.azurewebsites.net"
export DASHBOARD_API_BASE_URL="https://your-dashboard-service.azurewebsites.net"

# Azure App Service 설정
export WEBSITES_PORT="8080"
export WEBSITES_ENABLE_APP_SERVICE_STORAGE="false"

# 로깅 설정
export LOGGING_LEVEL_ROOT="INFO"
export LOGGING_LEVEL_COM_HANACARD="DEBUG"


