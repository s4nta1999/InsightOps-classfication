package com.hanacard.service;

import com.hanacard.dto.ClassificationRequest;
import com.hanacard.dto.EnhancedClassificationResponse;
import com.hanacard.entity.VocRaw;
import com.hanacard.repository.ConsultingClassificationRepository;
import com.hanacard.repository.VocRawRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VoC 배치 처리 서비스
 */
@Service
public class VocBatchService {
    
    private static final Logger logger = LoggerFactory.getLogger(VocBatchService.class);
    
    @Autowired
    private VocRawRepository vocRawRepository;
    
    @Autowired
    private ConsultingClassificationRepository consultingClassificationRepository;
    
    @Autowired
    private EnhancedOpenAIService enhancedOpenAIService;
    
    /**
     * VoC Raw 데이터를 배치로 처리하여 voc_normalized에 저장
     */
    @Transactional
    public Map<String, Object> processVocBatch(int batchSize) {
        logger.info("VoC 배치 처리 시작: batchSize={}", batchSize);
        
        Map<String, Object> result = new HashMap<>();
        int processedCount = 0;
        int errorCount = 0;
        long startTime = System.currentTimeMillis();
        
        try {
            // 미처리 데이터 조회
            Pageable pageable = PageRequest.of(0, batchSize);
            Page<VocRaw> unprocessedPage = vocRawRepository.findByProcessedFalse(pageable);
            List<VocRaw> unprocessedData = unprocessedPage.getContent();
            
            logger.info("미처리 데이터 조회 완료: {}건", unprocessedData.size());
            
            if (unprocessedData.isEmpty()) {
                result.put("message", "처리할 데이터가 없습니다.");
                result.put("processedCount", 0);
                result.put("errorCount", 0);
                result.put("processingTime", 0);
                return result;
            }
            
            // 배치 처리
            for (VocRaw rawData : unprocessedData) {
                try {
                    // 1. ClassificationRequest로 변환
                    ClassificationRequest request = convertToClassificationRequest(rawData);
                    
                    // 2. AI 분류 수행
                    enhancedOpenAIService.processAndSaveConsultingContent(request);
                    
                    // 3. 처리 완료 표시
                    rawData.setProcessed(true);
                    rawData.setProcessedAt(LocalDateTime.now());
                    vocRawRepository.save(rawData);
                    
                    processedCount++;
                    logger.debug("데이터 처리 완료: id={}, sourceId={}", rawData.getId(), rawData.getSourceId());
                    
                } catch (Exception e) {
                    errorCount++;
                    logger.error("데이터 처리 실패: id={}, sourceId={}, error={}", 
                               rawData.getId(), rawData.getSourceId(), e.getMessage());
                }
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            result.put("message", "배치 처리 완료");
            result.put("processedCount", processedCount);
            result.put("errorCount", errorCount);
            result.put("processingTime", processingTime);
            result.put("processingTimeSeconds", processingTime / 1000.0);
            
            logger.info("VoC 배치 처리 완료: processed={}, errors={}, time={}ms", 
                       processedCount, errorCount, processingTime);
            
        } catch (Exception e) {
            logger.error("VoC 배치 처리 중 오류 발생", e);
            result.put("error", "배치 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * voc_normalized 테이블 초기화
     */
    @Transactional
    public Map<String, Object> clearNormalizedData() {
        logger.info("voc_normalized 테이블 초기화 시작");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            long count = consultingClassificationRepository.count();
            consultingClassificationRepository.deleteAll();
            
            result.put("message", "voc_normalized 테이블이 초기화되었습니다.");
            result.put("deletedCount", count);
            
            logger.info("voc_normalized 테이블 초기화 완료: {}건 삭제", count);
            
        } catch (Exception e) {
            logger.error("voc_normalized 테이블 초기화 중 오류 발생", e);
            result.put("error", "테이블 초기화 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 배치 처리 상태 조회
     */
    public Map<String, Object> getBatchStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 전체 통계
            long totalRawCount = vocRawRepository.countAll();
            long unprocessedCount = vocRawRepository.countUnprocessed();
            long processedCount = vocRawRepository.countProcessed();
            long normalizedCount = consultingClassificationRepository.count();
            
            status.put("totalRawCount", totalRawCount);
            status.put("unprocessedCount", unprocessedCount);
            status.put("processedCount", processedCount);
            status.put("normalizedCount", normalizedCount);
            status.put("processingProgress", totalRawCount > 0 ? (double) processedCount / totalRawCount * 100 : 0);
            
            // 최근 처리된 데이터
            Pageable pageable = PageRequest.of(0, 5);
            List<VocRaw> recentProcessed = vocRawRepository.findRecentlyProcessed(pageable);
            status.put("recentProcessed", recentProcessed);
            
        } catch (Exception e) {
            logger.error("배치 상태 조회 중 오류 발생", e);
            status.put("error", "상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return status;
    }
    
    /**
     * 처리 비용 예상
     */
    public Map<String, Object> estimateProcessingCost() {
        Map<String, Object> costEstimate = new HashMap<>();
        
        try {
            long unprocessedCount = vocRawRepository.countUnprocessed();
            
            // GPT-4o-mini 비용 (2025년 기준)
            double costPerToken = 0.00015 / 1000; // $0.00015 per 1K tokens
            int estimatedTokensPerRequest = 2000; // 평균 토큰 수
            double costPerRequest = costPerToken * estimatedTokensPerRequest;
            
            double totalCost = unprocessedCount * costPerRequest;
            double totalCostKRW = totalCost * 1300; // USD to KRW (대략적)
            
            costEstimate.put("unprocessedCount", unprocessedCount);
            costEstimate.put("estimatedTokensPerRequest", estimatedTokensPerRequest);
            costEstimate.put("costPerRequestUSD", costPerRequest);
            costEstimate.put("totalCostUSD", totalCost);
            costEstimate.put("totalCostKRW", totalCostKRW);
            costEstimate.put("note", "비용은 예상치이며, 실제 토큰 사용량에 따라 달라질 수 있습니다.");
            
        } catch (Exception e) {
            logger.error("비용 예상 계산 중 오류 발생", e);
            costEstimate.put("error", "비용 예상 계산 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return costEstimate;
    }
    
    /**
     * VocRaw를 ClassificationRequest로 변환
     */
    private ClassificationRequest convertToClassificationRequest(VocRaw rawData) {
        ClassificationRequest request = new ClassificationRequest();
        request.setSourceId(rawData.getSourceId());
        request.setConsultingDate(rawData.getConsultingDate());
        request.setClientGender(rawData.getClientGender());
        request.setClientAge(rawData.getClientAge());
        request.setConsultingTurns(rawData.getConsultingTurns());
        request.setConsultingLength(rawData.getConsultingLength());
        request.setConsultingContent(rawData.getConsultingContent());
        return request;
    }
}
