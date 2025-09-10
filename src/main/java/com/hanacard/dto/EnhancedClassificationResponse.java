package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * 향상된 상담 분류 응답 DTO
 * JSONB 구조를 반영한 응답 형태
 */
public class EnhancedClassificationResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("source_id")
    private String sourceId;
    
    @JsonProperty("consulting_content")
    private String consultingContent;
    
    @JsonProperty("processing_time")
    private Double processingTime;
    
    @JsonProperty("consulting_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime consultingDate;
    
    @JsonProperty("created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    
    // AI 분석 결과만 저장 (기본 정보는 테이블 컬럼에 저장)
    @JsonProperty("classification")
    private ClassificationInfo classification;
    
    @JsonProperty("analysis")
    private AnalysisInfo analysis;
    
    // 생성자
    public EnhancedClassificationResponse() {}
    
    // Getter & Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getConsultingContent() {
        return consultingContent;
    }
    
    public void setConsultingContent(String consultingContent) {
        this.consultingContent = consultingContent;
    }
    
    public Double getProcessingTime() {
        return processingTime;
    }
    
    public void setProcessingTime(Double processingTime) {
        this.processingTime = processingTime;
    }
    
    public LocalDateTime getConsultingDate() {
        return consultingDate;
    }
    
    public void setConsultingDate(LocalDateTime consultingDate) {
        this.consultingDate = consultingDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public ClassificationInfo getClassification() {
        return classification;
    }
    
    public void setClassification(ClassificationInfo classification) {
        this.classification = classification;
    }
    
    public AnalysisInfo getAnalysis() {
        return analysis;
    }
    
    public void setAnalysis(AnalysisInfo analysis) {
        this.analysis = analysis;
    }
    
    
    // 내부 클래스들
    public static class ClassificationInfo {
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("category_id")
        private String categoryId;
        
        @JsonProperty("confidence")
        private Double confidence;
        
        @JsonProperty("alternative_categories")
        private AlternativeCategory[] alternativeCategories;
        
        // getter, setter
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public AlternativeCategory[] getAlternativeCategories() { return alternativeCategories; }
        public void setAlternativeCategories(AlternativeCategory[] alternativeCategories) { this.alternativeCategories = alternativeCategories; }
    }
    
    public static class AlternativeCategory {
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("confidence")
        private Double confidence;
        
        // getter, setter
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
    }
    
    public static class AnalysisInfo {
        @JsonProperty("problem_situation")
        private String problemSituation;
        
        @JsonProperty("solution_approach")
        private String solutionApproach;
        
        @JsonProperty("expected_outcome")
        private String expectedOutcome;
        
        
        // getter, setter
        public String getProblemSituation() { return problemSituation; }
        public void setProblemSituation(String problemSituation) { this.problemSituation = problemSituation; }
        
        public String getSolutionApproach() { return solutionApproach; }
        public void setSolutionApproach(String solutionApproach) { this.solutionApproach = solutionApproach; }
        
        public String getExpectedOutcome() { return expectedOutcome; }
        public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
        
    }
    
}
