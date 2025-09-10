package com.hanacard.client;

import com.hanacard.entity.ConsultingClassification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard 서비스 API 클라이언트
 * 분류 완료된 데이터를 Dashboard로 전송
 */
@Component
public class DashboardApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardApiClient.class);
    
    @Value("${DASHBOARD_API_BASE_URL:http://dashboard-service:8080}")
    private String dashboardApiBaseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public DashboardApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * 분류 완료된 데이터를 Dashboard로 비동기 전송
     */
    @Async("dashboardExecutor")
    public void postClassificationData(ConsultingClassification entity) {
        String url = dashboardApiBaseUrl + "/api/classifications/receive";
        
        try {
            // 모든 컬럼 데이터를 포함한 Map 생성 (컬럼명 그대로)
            Map<String, Object> postData = createPostData(entity);
            
            // HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // POST 요청 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(postData, headers);
            
            // POST 전송
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
            
            logger.info("✅ Dashboard로 데이터 전송 성공: id={}, status={}", 
                       entity.getId(), responseEntity.getStatusCode());
                       
        } catch (Exception e) {
            logger.error("❌ Dashboard 전송 실패: id={}, error={}", 
                        entity.getId(), e.getMessage(), e);
            // Dashboard 전송 실패해도 메인 로직은 계속 진행
        }
    }
    
    /**
     * 엔티티를 Dashboard 전송용 Map으로 변환 (컬럼명 그대로)
     */
    private Map<String, Object> createPostData(ConsultingClassification entity) {
        Map<String, Object> data = new HashMap<>();
        
        // 기본 정보 (컬럼명 그대로)
        data.put("id", entity.getId());
        data.put("source_id", entity.getSourceId());
        data.put("consulting_date", entity.getConsultingDate());
        data.put("client_gender", entity.getClientGender());
        data.put("client_age", entity.getClientAge());
        data.put("consulting_turns", entity.getConsultingTurns());
        data.put("consulting_length", entity.getConsultingLength());
        data.put("consulting_content", entity.getConsultingContent());
        
        // AI 처리 정보
        data.put("processing_time", entity.getProcessingTime());
        data.put("consulting_category", entity.getConsultingCategory());
        data.put("category_id", entity.getCategoryId()); // 추후 추가될 필드
        
        // analysis_result를 JSON 객체로 변환
        try {
            if (entity.getAnalysisResult() != null) {
                Object analysisResultObj = objectMapper.readValue(entity.getAnalysisResult(), Object.class);
                data.put("analysis_result", analysisResultObj);
            }
        } catch (Exception e) {
            logger.warn("analysis_result JSON 파싱 실패: {}", e.getMessage());
            data.put("analysis_result", entity.getAnalysisResult()); // 문자열 그대로
        }
        
        // 시스템 정보
        data.put("created_at", entity.getCreatedAt());
        data.put("updated_at", entity.getUpdatedAt());
        
        return data;
    }
    
    /**
     * Dashboard 서비스 연결 테스트
     */
    public boolean testConnection() {
        try {
            String url = dashboardApiBaseUrl + "/api/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            boolean isConnected = response.getStatusCode().is2xxSuccessful();
            logger.info("Dashboard 연결 테스트: {}", isConnected ? "성공" : "실패");
            return isConnected;
            
        } catch (Exception e) {
            logger.error("Dashboard 연결 테스트 실패: {}", e.getMessage());
            return false;
        }
    }
}
