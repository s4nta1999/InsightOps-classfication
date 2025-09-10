package com.hanacard.client;

import com.hanacard.dto.AdminApiResponse;
import com.hanacard.dto.ConsultingCategoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin 서비스 API 클라이언트 (엄격한 방식)
 * Admin API 실패 시 분류 작업 중단
 */
@Component
public class AdminApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminApiClient.class);
    
    @Value("${ADMIN_API_BASE_URL:http://admin-service:8080}")
    private String adminApiBaseUrl;
    
    private final RestTemplate restTemplate;
    
    // 카테고리 캐싱 (성능 최적화)
    private List<ConsultingCategoryData> cachedCategories;
    private LocalDateTime lastCacheUpdate;
    private static final Duration CACHE_DURATION = Duration.ofMinutes(30);
    
    public AdminApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Admin API에서 상담 카테고리 목록 조회
     * 실패 시 예외 발생으로 분류 작업 중단
     */
    public List<ConsultingCategoryData> getConsultingCategories() {
        // 캐시가 유효하면 사용 (성능 최적화)
        if (isCacheValid()) {
            logger.debug("캐시된 카테고리 사용: {}건", cachedCategories.size());
            return cachedCategories;
        }
        
        // Admin API 호출
        return callAdminApiStrict();
    }
    
    private boolean isCacheValid() {
        return cachedCategories != null && 
               lastCacheUpdate != null && 
               Duration.between(lastCacheUpdate, LocalDateTime.now()).compareTo(CACHE_DURATION) < 0;
    }
    
    private List<ConsultingCategoryData> callAdminApiStrict() {
        String url = adminApiBaseUrl + "/api/admin/consulting_category";
        
        try {
            logger.info("Admin API 호출: {}", url);
            ResponseEntity<AdminApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, null, AdminApiResponse.class
            );
            
            AdminApiResponse apiResponse = response.getBody();
            
            // 응답 검증
            if (apiResponse == null) {
                logger.error("❌ Admin API 응답이 null입니다");
                throw new RuntimeException("Admin API에서 응답을 받지 못했습니다");
            }
            
            if (!apiResponse.isSuccess()) {
                logger.error("❌ Admin API 응답 실패: {}", apiResponse.getMessage());
                throw new RuntimeException("카테고리 조회 실패: " + apiResponse.getMessage());
            }
            
            if (apiResponse.getData() == null) {
                logger.error("❌ Admin API 데이터가 null입니다");
                throw new RuntimeException("카테고리 데이터를 받지 못했습니다");
            }
            
            List<ConsultingCategoryData> categories = apiResponse.getData().getData();
            if (categories == null || categories.isEmpty()) {
                logger.error("❌ 카테고리 목록이 비어있습니다");
                throw new RuntimeException("사용 가능한 카테고리가 없습니다");
            }
            
            // 성공 시 캐시 업데이트
            cachedCategories = categories;
            lastCacheUpdate = LocalDateTime.now();
            
            logger.info("✅ Admin API에서 카테고리 조회 성공: {}건", categories.size());
            return categories;
            
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                // 이미 처리된 비즈니스 예외는 그대로 전파
                throw e;
            }
            
            // 네트워크 오류 등 기술적 예외 처리
            logger.error("❌ Admin API 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Admin 서비스에 연결할 수 없습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 캐시 강제 갱신
     */
    public void refreshCategoryCache() {
        logger.info("카테고리 캐시 강제 갱신");
        cachedCategories = null;
        lastCacheUpdate = null;
        callAdminApiStrict();
    }
    
    /**
     * 현재 캐시 상태 확인
     */
    public boolean isCacheAvailable() {
        return cachedCategories != null && !cachedCategories.isEmpty();
    }
    
    /**
     * 캐시된 카테고리 수 반환
     */
    public int getCachedCategoryCount() {
        return cachedCategories != null ? cachedCategories.size() : 0;
    }
}
