package com.hanacard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanacard.constants.ConsultingCategories;
import com.hanacard.dto.AnalysisResult;
import com.hanacard.dto.ClassificationRequest;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 향상된 OpenAI 서비스
 * JSONB 구조를 활용한 분류 + 분석 + 저장 기능
 */
@Service
public class EnhancedOpenAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedOpenAIService.class);
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.model:gpt-4o-mini}")
    private String model;
    
    @Autowired
    private ConsultingClassificationRepository repository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 상담 내용을 향상된 방식으로 처리하고 저장
     */
    public EnhancedClassificationResponse processAndSaveConsultingContent(
            ClassificationRequest request) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("향상된 상담 처리 시작: sourceId={}, contentLength={}", 
                       request.getSourceId(), request.getConsultingContent().length());
            
            // 1. 향상된 프롬프트로 OpenAI API 호출
            String prompt = buildEnhancedPrompt(request.getConsultingContent());
            String openAIResponse = callOpenAI(prompt);
            
            // 2. OpenAI 응답 파싱
            EnhancedClassificationResponse response = parseEnhancedResponse(openAIResponse);
            
            // 3. 기본 정보 설정
            response.setSourceId(request.getSourceId());
            response.setConsultingContent(request.getConsultingContent());
            response.setConsultingDate(request.getConsultingDate());
            
            // 4. 처리 시간 계산
            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
            response.setProcessingTime(processingTime);
            
            // 5. 데이터베이스에 저장
            ConsultingClassification entity = mapToEntity(response, request);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            
            ConsultingClassification savedEntity = repository.save(entity);
            response.setId(savedEntity.getId());
            response.setCreatedAt(savedEntity.getCreatedAt());
            
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
     * 향상된 프롬프트 생성
     */
    private String buildEnhancedPrompt(String content) {
        return String.format("""
            다음 상담 내용을 분석하여 JSON 형식으로 답변해주세요:
            
            {
              "classification": {
                "category": "23개 카테고리 중 하나",
                "confidence": "0.0~1.0 사이 값",
                "alternative_categories": [
                  {"category": "대안 카테고리", "confidence": "신뢰도"}
                ]
              },
              "analysis": {
                "problem_situation": "문제 상황을 1-2문장으로 요약",
                "solution_approach": "해결 방안을 1-2문장으로 설명",
                "expected_outcome": "예상 결과를 1문장으로 제시",
                "urgency_level": "긴급도 (low/medium/high)",
                "priority_score": "우선순위 점수 (1.0~10.0)"
              },
              "extracted_info": {
                "card_type": "카드 타입",
                "issue_type": "문제 유형",
                "location": "위치 정보",
                "client_emotion": "고객 감정 상태"
              }
            }
            
            사용 가능한 카테고리: %s
            
            상담 내용: %s
            
            주의사항:
            - 모든 필드는 한국어로 답변
            - 문제상황은 구체적이고 명확하게
            - 해결방안은 실용적이고 실행 가능하게
            - 예상결과는 긍정적이고 구체적으로
            - JSON 형식을 정확히 지켜주세요
            """, 
            String.join(", ", ConsultingCategories.getAllCategories()),
            content);
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
     * OpenAI 응답 파싱
     */
    private EnhancedClassificationResponse parseEnhancedResponse(String openAIResponse) {
        try {
            // JSON 추출 (```json ... ``` 형태일 수 있음)
            String jsonContent = extractJsonFromResponse(openAIResponse);
            
            // JSON 파싱
            JsonNode root = objectMapper.readTree(jsonContent);
            
            EnhancedClassificationResponse response = new EnhancedClassificationResponse();
            
            // 분류 정보 파싱
            if (root.has("classification")) {
                JsonNode classification = root.get("classification");
                EnhancedClassificationResponse.ClassificationInfo classificationInfo = 
                    new EnhancedClassificationResponse.ClassificationInfo();
                
                classificationInfo.setCategory(classification.get("category").asText());
                classificationInfo.setConfidence(classification.get("confidence").asDouble());
                
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
     * 응답을 엔티티로 매핑
     */
    private ConsultingClassification mapToEntity(EnhancedClassificationResponse response, ClassificationRequest request) {
        ConsultingClassification entity = new ConsultingClassification();
        
        // 기본 정보 설정 (voc_raw 구조에 맞춤)
        entity.setSourceId(request.getSourceId());
        entity.setConsultingContent(request.getConsultingContent());
        entity.setProcessingTime(response.getProcessingTime());
        entity.setConsultingDate(request.getConsultingDate());
        
        // 원본 데이터 직접 매핑 (voc_raw → voc_normalized)
        entity.setClientGender(request.getClientGender());
        entity.setClientAge(request.getClientAge());
        entity.setConsultingTurns(request.getConsultingTurns());
        entity.setConsultingLength(request.getConsultingLength());
        
        // 분류 결과를 별도 컬럼에 저장
        if (response.getClassification() != null) {
            entity.setConsultingCategory(response.getClassification().getCategory());
        }
        
        // AI 분석 결과만 JSON으로 저장 (기본 정보 제외)
        try {
            AnalysisResult analysisResult = convertToAnalysisResult(response);
            // JSONB 컬럼에는 JSON 객체를 직접 저장
            entity.setAnalysisResult(objectMapper.writeValueAsString(analysisResult));
        } catch (Exception e) {
            logger.error("JSON 변환 실패", e);
            throw new RuntimeException("JSON 변환 실패", e);
        }
        
        return entity;
    }
    
    /**
     * EnhancedClassificationResponse를 AnalysisResult로 변환
     */
    private AnalysisResult convertToAnalysisResult(EnhancedClassificationResponse response) {
        // ClassificationInfo 변환
        AnalysisResult.ClassificationInfo classificationInfo = new AnalysisResult.ClassificationInfo();
        if (response.getClassification() != null) {
            classificationInfo.setCategory(response.getClassification().getCategory());
            classificationInfo.setConfidence(response.getClassification().getConfidence());
            
            // AlternativeCategory 배열 변환
            if (response.getClassification().getAlternativeCategories() != null) {
                AnalysisResult.AlternativeCategory[] altCategories = new AnalysisResult.AlternativeCategory[response.getClassification().getAlternativeCategories().length];
                for (int i = 0; i < response.getClassification().getAlternativeCategories().length; i++) {
                    altCategories[i] = new AnalysisResult.AlternativeCategory();
                    altCategories[i].setCategory(response.getClassification().getAlternativeCategories()[i].getCategory());
                    altCategories[i].setConfidence(response.getClassification().getAlternativeCategories()[i].getConfidence());
                }
                classificationInfo.setAlternativeCategories(altCategories);
            }
        }
        
        // AnalysisInfo 변환
        AnalysisResult.AnalysisInfo analysisInfo = new AnalysisResult.AnalysisInfo();
        if (response.getAnalysis() != null) {
            analysisInfo.setProblemSituation(response.getAnalysis().getProblemSituation());
            analysisInfo.setSolutionApproach(response.getAnalysis().getSolutionApproach());
            analysisInfo.setExpectedOutcome(response.getAnalysis().getExpectedOutcome());
        }
        
        return new AnalysisResult(classificationInfo, analysisInfo);
    }
}
