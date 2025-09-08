package com.hanacard.service;

import com.hanacard.constants.ConsultingCategories;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * OpenAI API 연동 서비스
 */
@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final OpenAiService openAiService;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    public OpenAIService(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model,
            @Value("${openai.max-tokens:100}") int maxTokens,
            @Value("${openai.temperature:0.1}") double temperature) {
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenAI API 키가 설정되지 않았습니다.");
        }

        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        
        logger.info("OpenAI 서비스가 초기화되었습니다. 모델: {}, Max Tokens: {}, Temperature: {}", 
                   model, maxTokens, temperature);
    }

    /**
     * 상담 내용을 분석하여 카테고리로 분류
     */
    public ClassificationResult classifyConsultingContent(String content) {
        try {
            logger.debug("상담 내용 분류 시작: {}", content.substring(0, Math.min(content.length(), 100)) + "...");

            String systemPrompt = createSystemPrompt();
            String userPrompt = createUserPrompt(content);

            List<ChatMessage> messages = List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userPrompt)
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

            String response = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("OpenAI API 응답이 비어있습니다.");
            }

            // 응답에서 카테고리 추출
            String category = extractCategory(response);
            double confidence = calculateConfidence(response);

            logger.info("상담 내용 분류 완료: 카테고리={}, 신뢰도={}", category, confidence);

            return new ClassificationResult(category, confidence);

        } catch (Exception e) {
            logger.error("OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("상담 내용 분류 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 시스템 프롬프트 생성
     */
    private String createSystemPrompt() {
        return String.format("""
            당신은 하나카드 상담 내용을 분석하여 적절한 상담 카테고리로 분류하는 전문가입니다.
            
            다음 %d개의 상담 카테고리 중에서 가장 적합한 하나를 선택해야 합니다:
            
            %s
            
            분류 규칙:
            1. 상담 내용의 주요 주제와 목적을 파악하세요
            2. 가장 적합한 카테고리를 하나만 선택하세요
            3. 정확히 위의 카테고리명과 일치해야 합니다
            4. 불확실한 경우 상담 내용의 핵심 키워드를 기반으로 판단하세요
            
            응답 형식: "카테고리명" (따옴표 포함)
            """, 
            ConsultingCategories.CATEGORY_COUNT,
            String.join("\n", ConsultingCategories.CATEGORIES.stream()
                .map(cat -> String.format("%d. %s", ConsultingCategories.CATEGORIES.indexOf(cat) + 1, cat))
                .toList())
        );
    }

    /**
     * 사용자 프롬프트 생성
     */
    private String createUserPrompt(String content) {
        return String.format("""
            다음 하나카드 상담 내용을 분석하여 적절한 상담 카테고리로 분류해주세요:
            
            상담 내용:
            %s
            
            위 상담 내용에 가장 적합한 카테고리를 선택하여 응답해주세요.
            """, content);
    }

    /**
     * 응답에서 카테고리 추출
     */
    private String extractCategory(String response) {
        String cleanResponse = response.trim().replaceAll("[\"\"']", "");
        
        // 정확히 일치하는 카테고리 찾기
        if (ConsultingCategories.isValidCategory(cleanResponse)) {
            return cleanResponse;
        }

        // 부분 일치하는 카테고리 찾기
        for (String category : ConsultingCategories.CATEGORIES) {
            if (cleanResponse.contains(category) || category.contains(cleanResponse)) {
                logger.warn("부분 일치로 카테고리 찾음: 응답={}, 카테고리={}", response, category);
                return category;
            }
        }

        // 기본값으로 "이용내역 안내" 반환
        logger.warn("카테고리를 찾을 수 없어 기본값을 사용합니다. 응답: {}", response);
        return "이용내역 안내";
    }

    /**
     * 신뢰도 계산
     */
    private double calculateConfidence(String response) {
        String cleanResponse = response.trim().replaceAll("[\"\"']", "");
        
        // 정확히 일치하는 경우 높은 신뢰도
        if (ConsultingCategories.isValidCategory(cleanResponse)) {
            return 0.95;
        }

        // 부분 일치하는 경우 중간 신뢰도
        boolean hasPartialMatch = ConsultingCategories.CATEGORIES.stream()
            .anyMatch(category -> cleanResponse.contains(category) || category.contains(cleanResponse));

        if (hasPartialMatch) {
            return 0.8;
        }

        // 기본값 사용하는 경우 낮은 신뢰도
        return 0.6;
    }

    /**
     * 분류 결과 클래스
     */
    public static class ClassificationResult {
        private final String category;
        private final double confidence;

        public ClassificationResult(String category, double confidence) {
            this.category = category;
            this.confidence = confidence;
        }

        public String getCategory() {
            return category;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}
