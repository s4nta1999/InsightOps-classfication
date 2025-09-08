package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate consultingDate;
    
    @JsonProperty("consulting_time")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime consultingTime;
    
    @JsonProperty("created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    
    // JSONB 구조화된 응답
    @JsonProperty("classification")
    private ClassificationInfo classification;
    
    @JsonProperty("analysis")
    private AnalysisInfo analysis;
    
    @JsonProperty("extracted_info")
    private ExtractedInfo extractedInfo;
    
    @JsonProperty("metadata")
    private MetadataInfo metadata;
    
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
    
    public LocalDate getConsultingDate() {
        return consultingDate;
    }
    
    public void setConsultingDate(LocalDate consultingDate) {
        this.consultingDate = consultingDate;
    }
    
    public LocalTime getConsultingTime() {
        return consultingTime;
    }
    
    public void setConsultingTime(LocalTime consultingTime) {
        this.consultingTime = consultingTime;
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
    
    public ExtractedInfo getExtractedInfo() {
        return extractedInfo;
    }
    
    public void setExtractedInfo(ExtractedInfo extractedInfo) {
        this.extractedInfo = extractedInfo;
    }
    
    public MetadataInfo getMetadata() {
        return metadata;
    }
    
    public void setMetadata(MetadataInfo metadata) {
        this.metadata = metadata;
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
        
        @JsonProperty("urgency_level")
        private String urgencyLevel;
        
        @JsonProperty("priority_score")
        private Double priorityScore;
        
        // getter, setter
        public String getProblemSituation() { return problemSituation; }
        public void setProblemSituation(String problemSituation) { this.problemSituation = problemSituation; }
        
        public String getSolutionApproach() { return solutionApproach; }
        public void setSolutionApproach(String solutionApproach) { this.solutionApproach = solutionApproach; }
        
        public String getExpectedOutcome() { return expectedOutcome; }
        public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
        
        public String getUrgencyLevel() { return urgencyLevel; }
        public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }
        
        public Double getPriorityScore() { return priorityScore; }
        public void setPriorityScore(Double priorityScore) { this.priorityScore = priorityScore; }
    }
    
    public static class ExtractedInfo {
        @JsonProperty("card_type")
        private String cardType;
        
        @JsonProperty("issue_type")
        private String issueType;
        
        @JsonProperty("location")
        private String location;
        
        @JsonProperty("client_emotion")
        private String clientEmotion;
        
        // getter, setter
        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }
        
        public String getIssueType() { return issueType; }
        public void setIssueType(String issueType) { this.issueType = issueType; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getClientEmotion() { return clientEmotion; }
        public void setClientEmotion(String clientEmotion) { this.clientEmotion = clientEmotion; }
    }
    
    public static class MetadataInfo {
        @JsonProperty("client_info")
        private ClientInfo clientInfo;
        
        @JsonProperty("system_info")
        private SystemInfo systemInfo;
        
        @JsonProperty("custom_fields")
        private CustomFields customFields;
        
        // getter, setter
        public ClientInfo getClientInfo() { return clientInfo; }
        public void setClientInfo(ClientInfo clientInfo) { this.clientInfo = clientInfo; }
        
        public SystemInfo getSystemInfo() { return systemInfo; }
        public void setSystemInfo(SystemInfo systemInfo) { this.systemInfo = systemInfo; }
        
        public CustomFields getCustomFields() { return customFields; }
        public void setCustomFields(CustomFields customFields) { this.customFields = customFields; }
    }
    
    public static class ClientInfo {
        @JsonProperty("gender")
        private String gender;
        
        @JsonProperty("age_group")
        private String ageGroup;
        
        @JsonProperty("consulting_turns")
        private String consultingTurns;
        
        @JsonProperty("consulting_length")
        private Integer consultingLength;
        
        // getter, setter
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getAgeGroup() { return ageGroup; }
        public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
        
        public String getConsultingTurns() { return consultingTurns; }
        public void setConsultingTurns(String consultingTurns) { this.consultingTurns = consultingTurns; }
        
        public Integer getConsultingLength() { return consultingLength; }
        public void setConsultingLength(Integer consultingLength) { this.consultingLength = consultingLength; }
    }
    
    public static class SystemInfo {
        @JsonProperty("api_version")
        private String apiVersion;
        
        @JsonProperty("model_version")
        private String modelVersion;
        
        @JsonProperty("processing_environment")
        private String processingEnvironment;
        
        // getter, setter
        public String getApiVersion() { return apiVersion; }
        public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
        
        public String getModelVersion() { return modelVersion; }
        public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
        
        public String getProcessingEnvironment() { return processingEnvironment; }
        public void setProcessingEnvironment(String processingEnvironment) { this.processingEnvironment = processingEnvironment; }
    }
    
    public static class CustomFields {
        @JsonProperty("department")
        private String department;
        
        @JsonProperty("agent_id")
        private String agentId;
        
        @JsonProperty("channel")
        private String channel;
        
        // getter, setter
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
    }
}
