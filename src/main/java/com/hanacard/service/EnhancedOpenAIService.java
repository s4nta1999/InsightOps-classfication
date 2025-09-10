package com.hanacard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanacard.client.AdminApiClient;
import com.hanacard.client.DashboardApiClient;
import com.hanacard.dto.AnalysisResult;
import com.hanacard.dto.ClassificationRequest;
import com.hanacard.dto.ConsultingCategoryData;
import com.hanacard.dto.EnhancedClassificationResponse;
import com.hanacard.entity.ConsultingClassification;
import com.hanacard.repository.ConsultingClassificationRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 향상된 OpenAI 서비스
 * JSONB 구조를 활용한 분류 + 분석 + 저장 기능
 * 임시로 데이터베이스 의존성 제거
 */
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
public class EnhancedOpenAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedOpenAIService.class);
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.model:gpt-4o-mini}")
    private String model;
    
    // 임시로 Repository 의존성 제거
    // @Autowired
    // private ConsultingClassificationRepository repository;
    
    @Autowired
    private AdminApiClient adminApiClient;
    
    @Autowired
    private DashboardApiClient dashboardApiClient;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 상담 내용을 향상된 방식으로 처리하고 저장
     * 임시로 데이터베이스 저장 비활성화
     */
    public EnhancedClassificationResponse processAndSaveConsultingContent(
            ClassificationRequest request) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("향상된 상담 처리 시작: sourceId={}, contentLength={}", 
                       request.getSourceId(), request.getConsultingContent().length());
            
            // 1. 임시: 하드코딩된 카테고리 사용 (Admin API 호출 비활성화)
            List<ConsultingCategoryData> categories = getDefaultCategories();
            logger.info("기본 카테고리 {}건 사용 (테스트 모드)", categories.size());
            
            // 2. 동적 카테고리로 향상된 프롬프트 생성
            String prompt = buildEnhancedPromptWithDynamicCategories(request.getConsultingContent(), categories);
            String openAIResponse = callOpenAI(prompt);
            
            // 3. OpenAI 응답 파싱 (category_id 포함)
            EnhancedClassificationResponse response = parseEnhancedResponseWithCategoryId(openAIResponse, categories);
            
            // 4. 기본 정보 설정
            response.setSourceId(request.getSourceId());
            response.setConsultingContent(request.getConsultingContent());
            response.setConsultingDate(request.getConsultingDate());
            
            // 5. 처리 시간 계산
            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
            response.setProcessingTime(processingTime);
            
            // 6. 임시로 데이터베이스 저장 비활성화
            // ConsultingClassification entity = mapToEntity(response, request);
            // entity.setCreatedAt(LocalDateTime.now());
            // entity.setUpdatedAt(LocalDateTime.now());
            // ConsultingClassification savedEntity = repository.save(entity);
            // response.setId(savedEntity.getId());
            // response.setCreatedAt(savedEntity.getCreatedAt());
            
            // 임시 ID 설정
            response.setId(System.currentTimeMillis());
            response.setCreatedAt(LocalDateTime.now());
            
            // 7. Dashboard로 데이터 전송 (비동기) - 임시 비활성화
            // dashboardApiClient.postClassificationData(savedEntity);
            
            logger.info("향상된 상담 처리 완료: id={}, category={}, confidence={}, processingTime={}s", 
                       response.getId(), 
                       response.getClassification().getCategory(),
                       response.getClassification().getConfidence(), 
                       processingTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("향상된 상담 처리 중 오류 발생: sourceId={}", request.getSourceId(), e);
            throw new RuntimeException("상담 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 기본 카테고리 목록 반환 (테스트용)
     */
    private List<ConsultingCategoryData> getDefaultCategories() {
        List<ConsultingCategoryData> categories = new ArrayList<>();
        
        categories.add(new ConsultingCategoryData("23515d46", "이용내역 안내"));
        categories.add(new ConsultingCategoryData("23516275", "한도 안내"));
        categories.add(new ConsultingCategoryData("235163f1", "가상계좌 안내"));
        categories.add(new ConsultingCategoryData("23516494", "서비스 이용방법 안내"));
        categories.add(new ConsultingCategoryData("23516530", "결제대금 안내"));
        categories.add(new ConsultingCategoryData("235165c9", "약관 안내"));
        categories.add(new ConsultingCategoryData("23516651", "상품 안내"));
        categories.add(new ConsultingCategoryData("235166ea", "도난/분실 신청/해제"));
        categories.add(new ConsultingCategoryData("23516778", "승인취소/매출취소 안내"));
        categories.add(new ConsultingCategoryData("235167ff", "선결제/즉시출금"));
        
        return categories;
    }
    
    
    /**
     * OpenAI API 호출
     */
    private String callOpenAI(String prompt) {
        try {
            logger.info("OpenAI API 호출 시작 - 모델: {}, 프롬프트 길이: {}", model, prompt.length());
            
            // 타임아웃 설정 (60초)
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(2000)
                .temperature(0.3)
                .build();
            
            logger.info("OpenAI API 요청 전송 중...");
            String response = service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
            
            logger.info("OpenAI API 응답 수신 완료 - 응답 길이: {}", response.length());
            return response;
            
        } catch (Exception e) {
            logger.error("OpenAI API 호출 중 오류 발생 - API Key: {}...", 
                        apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) : "null", e);
            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }
    
    
    /**
     * 응답에서 JSON 추출
     */
    private String extractJsonFromResponse(String response) {
        // ```json ... ``` 형태인 경우 JSON 부분만 추출
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.lastIndexOf("```");
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // ``` ... ``` 형태인 경우
        if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.lastIndexOf("```");
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // JSON이 아닌 경우 그대로 반환
        return response.trim();
    }
    
    /**
     * 동적 카테고리 목록으로 향상된 프롬프트 생성
     */
    private String buildEnhancedPromptWithDynamicCategories(String content, List<ConsultingCategoryData> categories) {
        StringBuilder categoryList = new StringBuilder();
        for (ConsultingCategoryData category : categories) {
            categoryList.append("- ").append(category.getCategoryName())
                       .append(" (ID: ").append(category.getId()).append(")\n");
        }
        
        return String.format("""
            다음 상담 내용을 분석하여 적절한 카테고리로 분류하고 상세 분석을 제공해주세요.
            
            상담 내용: %s
            
            가능한 카테고리 목록:
            %s
            
            다음 JSON 형식으로 응답해주세요:
            {
              "classification": {
                "category": "정확한 카테고리명",
                "category_id": "카테고리ID",
                "confidence": 0.95,
                "alternative_categories": [
                  {
                    "category": "대안카테고리명",
                    "confidence": 0.05
                  }
                ]
              },
              "analysis": {
                "problem_situation": "고객이 겪고 있는 구체적인 문제 상황",
                "solution_approach": "문제 해결을 위한 구체적인 접근 방법",
                "expected_outcome": "해결 후 예상되는 결과"
              }
            }
            """, content, categoryList.toString());
    }
    
    /**
     * OpenAI 응답을 파싱하여 category_id 포함한 응답 생성
     */
    private EnhancedClassificationResponse parseEnhancedResponseWithCategoryId(String openAIResponse, List<ConsultingCategoryData> categories) {
        try {
            String jsonResponse = extractJsonFromResponse(openAIResponse);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            EnhancedClassificationResponse response = new EnhancedClassificationResponse();
            
            // 분류 정보 파싱 (category_id 포함)
            if (root.has("classification")) {
                JsonNode classification = root.get("classification");
                EnhancedClassificationResponse.ClassificationInfo classificationInfo = 
                    new EnhancedClassificationResponse.ClassificationInfo();
                
                String categoryName = classification.get("category").asText();
                classificationInfo.setCategory(categoryName);
                classificationInfo.setConfidence(classification.get("confidence").asDouble());
                
                // category_id 설정
                if (classification.has("category_id")) {
                    String categoryId = classification.get("category_id").asText();
                    classificationInfo.setCategoryId(categoryId);
                } else {
                    // category_id가 없으면 카테고리명으로 찾기
                    String categoryId = findCategoryIdByName(categoryName, categories);
                    classificationInfo.setCategoryId(categoryId);
                }
                
                // 대안 카테고리 파싱
                if (classification.has("alternative_categories")) {
                    JsonNode alternatives = classification.get("alternative_categories");
                    EnhancedClassificationResponse.AlternativeCategory[] altCategories = 
                        new EnhancedClassificationResponse.AlternativeCategory[alternatives.size()];
                    
                    for (int i = 0; i < alternatives.size(); i++) {
                        JsonNode alt = alternatives.get(i);
                        EnhancedClassificationResponse.AlternativeCategory altCat = 
                            new EnhancedClassificationResponse.AlternativeCategory();
                        altCat.setCategory(alt.get("category").asText());
                        altCat.setConfidence(alt.get("confidence").asDouble());
                        altCategories[i] = altCat;
                    }
                    
                    classificationInfo.setAlternativeCategories(altCategories);
                }
                
                response.setClassification(classificationInfo);
            }
            
            // 분석 정보 파싱
            if (root.has("analysis")) {
                JsonNode analysis = root.get("analysis");
                EnhancedClassificationResponse.AnalysisInfo analysisInfo = 
                    new EnhancedClassificationResponse.AnalysisInfo();
                
                analysisInfo.setProblemSituation(analysis.get("problem_situation").asText());
                analysisInfo.setSolutionApproach(analysis.get("solution_approach").asText());
                analysisInfo.setExpectedOutcome(analysis.get("expected_outcome").asText());
                
                response.setAnalysis(analysisInfo);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("OpenAI 응답 파싱 실패: {}", openAIResponse, e);
            throw new RuntimeException("응답 파싱 실패", e);
        }
    }
    
    /**
     * 카테고리명으로 카테고리 ID 찾기
     */
    private String findCategoryIdByName(String categoryName, List<ConsultingCategoryData> categories) {
        return categories.stream()
            .filter(category -> category.getCategoryName().equals(categoryName))
            .map(ConsultingCategoryData::getId)
            .findFirst()
            .orElse("23515d46"); // 기본값
    }
}