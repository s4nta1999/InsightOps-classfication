package com.hanacard.controller;

import com.hanacard.constants.ConsultingCategories;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 상담 분류 컨트롤러
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
     * 저장된 상담 결과 조회 엔드포인트 (새로 추가)
     */
    @GetMapping("/classify/{id}")
    public ResponseEntity<ApiResponse<EnhancedClassificationResponse>> getClassificationResult(
            @PathVariable Long id) {
        
        try {
            logger.info("상담 결과 조회 요청: id={}", id);
            
            ConsultingClassification entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("상담 결과를 찾을 수 없습니다: " + id));
            
            // 엔티티를 응답 DTO로 변환
            EnhancedClassificationResponse response = mapEntityToResponse(entity);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("상담 결과 조회 중 오류 발생: id={}", id, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("상담 결과 조회 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 상담 이력 조회 엔드포인트 (새로 추가)
     */
    @GetMapping("/classify/history")
    public ResponseEntity<ApiResponse<Page<EnhancedClassificationResponse>>> getClassificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            logger.info("상담 이력 조회 요청: page={}, size={}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<ConsultingClassification> entities = repository.findAll(pageable);
            
            // 엔티티 페이지를 응답 DTO 페이지로 변환
            Page<EnhancedClassificationResponse> responses = entities.map(this::mapEntityToResponse);
            
            return ResponseEntity.ok(ApiResponse.success(responses));
            
        } catch (Exception e) {
            logger.error("상담 이력 조회 중 오류 발생", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("상담 이력 조회 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 카테고리별 통계 조회 엔드포인트 (새로 추가)
     */
    @GetMapping("/classify/statistics/category")
    public ResponseEntity<ApiResponse<List<Object[]>>> getCategoryStatistics() {
        
        try {
            logger.info("카테고리별 통계 조회 요청");
            
            List<Object[]> statistics = repository.getCategoryStatistics();
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("카테고리별 통계 조회 중 오류 발생", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("카테고리별 통계 조회 중 오류가 발생했습니다.", e.getMessage()));
        }
    }

    /**
     * 긴급도별 통계 조회 엔드포인트 (새로 추가)
     */

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
            healthData.put("features", List.of("기본 분류", "향상된 분류 + 분석", "데이터베이스 저장", "통계 조회"));

            return ResponseEntity.ok(ApiResponse.success(healthData));
        } catch (Exception e) {
            logger.error("헬스 체크 중 오류 발생", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서비스 상태 확인 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용 가능한 카테고리 목록 조회 엔드포인트
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategories() {
        try {
            Map<String, Object> categoriesData = new HashMap<>();
            categoriesData.put("categories", ConsultingCategories.getAllCategories());
            categoriesData.put("count", ConsultingCategories.CATEGORY_COUNT);

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
        rootData.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("classify", "POST /api/classify");
        endpoints.put("enhanced-classify", "POST /api/enhanced-classify");
        endpoints.put("get-result", "GET /api/classify/{id}");
        endpoints.put("history", "GET /api/classify/history");
        endpoints.put("category-stats", "GET /api/classify/statistics/category");
        endpoints.put("urgency-stats", "GET /api/classify/statistics/urgency");
        endpoints.put("health", "GET /api/health");
        endpoints.put("categories", "GET /api/categories");
        
        rootData.put("endpoints", endpoints);
        
        return ResponseEntity.ok(rootData);
    }

    /**
     * 엔티티를 응답 DTO로 변환하는 헬퍼 메서드
     */
    private EnhancedClassificationResponse mapEntityToResponse(ConsultingClassification entity) {
        // TODO: JSONB 필드를 파싱하여 응답 DTO로 변환하는 로직 구현
        // 현재는 기본 정보만 반환
        EnhancedClassificationResponse response = new EnhancedClassificationResponse();
        response.setId(entity.getId());
        response.setSourceId(entity.getSourceId());
        response.setConsultingContent(entity.getConsultingContent());
        response.setProcessingTime(entity.getProcessingTime());
        response.setConsultingDate(entity.getConsultingDate());
        response.setCreatedAt(entity.getCreatedAt());
        
        // JSONB 파싱 로직은 별도 구현 필요
        return response;
    }
}
