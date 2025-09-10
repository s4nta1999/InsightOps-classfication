# ============================================
# 하나카드 상담 분류 마이크로서비스 환경변수 예시
# ============================================

# 데이터베이스 설정
export SPRING_DATASOURCE_URL="jdbc:mysql://insightops-admin.mysql.database.azure.com:3306/normalization?serverTimezone=UTC&useSSL=true&allowPublicKeyRetrieval=true&requireSSL=true"
export SPRING_DATASOURCE_USERNAME="detoxjun"
export SPRING_DATASOURCE_PASSWORD="DetoxJunson@"

# JPA 설정
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"

# OpenAI API 설정
export OPENAI_API_KEY="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"
export OPENAI_MODEL="gpt-4o-mini"

# 외부 API 설정
export ADMIN_API_BASE_URL="https://insightops-admin-bnbchyhyc3hzb8ge.koreacentral-01.azurewebsites.net"
export DASHBOARD_API_BASE_URL="https://insightops-dashboard-backend-addpfcand3dabye3.koreacentral-01.azurewebsites.net"

# Azure App Service 설정
export WEBSITES_PORT="8080"
export WEBSITES_ENABLE_APP_SERVICE_STORAGE="false"

# 로깅 설정
export LOGGING_LEVEL_ROOT="INFO"
export LOGGING_LEVEL_COM_HANACARD="DEBUG"


