package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 분석 결과만 저장하는 DTO
 * 기본 정보는 테이블 컬럼에 저장되므로 제외
 */
public class AnalysisResult {
    
    @JsonProperty("classification")
    private ClassificationInfo classification;
    
    @JsonProperty("analysis")
    private AnalysisInfo analysis;
    
    // 생성자
    public AnalysisResult() {}
    
    public AnalysisResult(ClassificationInfo classification, AnalysisInfo analysis) {
        this.classification = classification;
        this.analysis = analysis;
    }
    
    // Getter & Setter
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
        
        @JsonProperty("confidence")
        private Double confidence;
        
        @JsonProperty("alternative_categories")
        private AlternativeCategory[] alternativeCategories;
        
        // getter, setter
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
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
