package com.hanacard.controller;

import com.hanacard.dto.ApiResponse;
import com.hanacard.dto.ClassificationRequest;
import com.hanacard.dto.ClassificationResponse;
import com.hanacard.dto.EnhancedClassificationResponse;
import com.hanacard.entity.ConsultingClassification;
import com.hanacard.repository.ConsultingClassificationRepository;
import com.hanacard.service.EnhancedOpenAIService;
import com.hanacard.service.OpenAIService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 상담 분류 컨트롤러
 * 임시로 데이터베이스 의존성 제거
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassificationController {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationController.class);

    private final OpenAIService openAIService;
    private final EnhancedOpenAIService enhancedOpenAIService;
    private final ConsultingClassificationRepository repository;

    public ClassificationController(OpenAIService openAIService, 
                                 EnhancedOpenAIService enhancedOpenAIService,
                                 ConsultingClassificationRepository repository) {
        this.openAIService = openAIService;
        this.enhancedOpenAIService = enhancedOpenAIService;
        this.repository = repository;
    }

    /**
     * 상담 내용을 분류하는 메인 엔드포인트 (기존)
     */
    @PostMapping("/classify")
    public ResponseEntity<ApiResponse<ClassificationResponse>> classifyConsultingContent(
            @Valid @RequestBody ClassificationRequest request) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("상담 분류 요청 수신: sourceId={}, contentLength={}", 
                       request.getSourceId(), request.getConsultingContent().length());

            // OpenAI 서비스를 통한 분류 수행
            OpenAIService.ClassificationResult result = openAIService.classifyConsultingContent(
                request.getConsultingContent().trim()
            );

            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0; // 초 단위

            // 응답 데이터 구성
            ClassificationResponse response = new ClassificationResponse();
            response.setSourceId(request.getSourceId());
            response.setConsultingCategory(result.getCategory());
            response.setConfidence(result.getConfidence());
            response.setProcessingTime(processingTime);
            response.setConsultingDate(request.getConsultingDate());

            logger.info("상담 분류 완료: sourceId={}, category={}, confidence={}, processingTime={}s", 
                       request.getSourceId(), result.getCategory(), result.getConfidence(), processingTime);

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            logger.error("상담 분류 처리 중 오류 발생: sourceId={}", request.getSourceId(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("분류 처리 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 향상된 상담 분류 + 분석 + 저장 엔드포인트 (새로 추가)
     */
    @PostMapping("/enhanced-classify")
    public ResponseEntity<ApiResponse<EnhancedClassificationResponse>> enhancedClassify(
            @Valid @RequestBody ClassificationRequest request) {
        
        try {
            logger.info("향상된 상담 분류 요청 수신: sourceId={}, contentLength={}", 
                       request.getSourceId(), request.getConsultingContent().length());

            // 향상된 분류 서비스 호출
            EnhancedClassificationResponse response = enhancedOpenAIService.processAndSaveConsultingContent(request);

            logger.info("향상된 상담 분류 완료: id={}, category={}, confidence={}", 
                       response.getId(), 
                       response.getClassification().getCategory(), 
                       response.getClassification().getConfidence());

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            logger.error("향상된 상담 분류 처리 중 오류 발생: sourceId={}", request.getSourceId(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("향상된 분류 처리 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * MailContents용 API - 카테고리별 최근 분석 결과만 조회 (analysis_result만 반환)
     */
    @GetMapping("/normalization/voc_normalized")
    public ResponseEntity<ApiResponse<List<Object>>> getVocNormalized(
            @RequestParam String category_id,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            logger.info("MailContents API 호출: category_id={}, limit={}", category_id, limit);
            
            // 카테고리 ID로 최근 데이터 조회
            Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
            Page<ConsultingClassification> page = repository.findByCategoryIdOrderByCreatedAtDesc(category_id, pageable);
            
            List<Object> results = new ArrayList<>();
            
            for (ConsultingClassification entity : page.getContent()) {
                // analysis_result만 파싱해서 반환
                try {
                    if (entity.getAnalysisResult() != null && !entity.getAnalysisResult().isEmpty()) {
                        Object analysisResultObj = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(entity.getAnalysisResult(), Object.class);
                        results.add(analysisResultObj);
                    } else {
                        results.add(null);
                    }
                } catch (Exception e) {
                    logger.warn("analysis_result JSON 파싱 실패: id={}, error={}", entity.getId(), e.getMessage());
                    results.add(null);
                }
            }
            
            logger.info("MailContents API 응답: category_id={}, 조회된 데이터 수={}", category_id, results.size());
            
            return ResponseEntity.ok(ApiResponse.success(results));
            
        } catch (Exception e) {
            logger.error("MailContents API 오류 발생: category_id={}, limit={}", category_id, limit, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("데이터 조회 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 서비스 배포 상태 확인용 테스트 API (Admin 의존성 없음)
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testService() {
        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("status", "healthy");
            testData.put("timestamp", LocalDateTime.now());
            testData.put("service", "하나카드 상담 분류 마이크로서비스");
            testData.put("version", "2.0.0");
            testData.put("test_mode", true);
            testData.put("database_enabled", true);
            testData.put("admin_dependency", "disabled");
            testData.put("features", List.of("기본 분류", "향상된 분류 + 분석", "MailContents API"));
            testData.put("message", "서비스가 정상적으로 배포되었습니다! 🚀");

            return ResponseEntity.ok(ApiResponse.success(testData));
        } catch (Exception e) {
            logger.error("테스트 API 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("테스트 API 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 서비스 상태 확인 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        try {
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", "healthy");
            healthData.put("timestamp", LocalDateTime.now());
            healthData.put("service", "하나카드 상담 분류 마이크로서비스");
            healthData.put("version", "2.0.0");
            healthData.put("database_enabled", true);
            healthData.put("features", List.of("기본 분류", "향상된 분류 + 분석", "MailContents API"));

            return ResponseEntity.ok(ApiResponse.success(healthData));
        } catch (Exception e) {
            logger.error("헬스 체크 중 오류 발생", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서비스 상태 확인 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용 가능한 카테고리 목록 조회 엔드포인트
     * Note: 카테고리는 이제 Admin API에서 동적으로 조회됩니다.
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategories() {
        try {
            Map<String, Object> categoriesData = new HashMap<>();
            categoriesData.put("message", "카테고리는 Admin API에서 동적으로 관리됩니다.");
            categoriesData.put("admin_api_endpoint", "/api/admin/consulting_category");
            categoriesData.put("test_mode", true);

            return ResponseEntity.ok(ApiResponse.success(categoriesData));
        } catch (Exception e) {
            logger.error("카테고리 목록 조회 중 오류 발생", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("카테고리 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 루트 엔드포인트
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getRoot() {
        Map<String, Object> rootData = new HashMap<>();
        rootData.put("service", "하나카드 상담 분류 마이크로서비스");
        rootData.put("version", "2.0.0");
        rootData.put("status", "running");
        rootData.put("test_mode", true);
        rootData.put("database_enabled", true);
        rootData.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("test", "GET /api/test");
        endpoints.put("classify", "POST /api/classify");
        endpoints.put("enhanced-classify", "POST /api/enhanced-classify");
        endpoints.put("health", "GET /api/health");
        endpoints.put("categories", "GET /api/categories");
        endpoints.put("voc_normalized", "GET /api/normalization/voc_normalized?category_id={id}&limit={num}");
        
        rootData.put("endpoints", endpoints);
        
        return ResponseEntity.ok(rootData);
    }
}