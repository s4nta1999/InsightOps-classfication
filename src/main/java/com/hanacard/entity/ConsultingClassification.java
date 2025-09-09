package com.hanacard.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 상담 분류 결과 엔티티 (voc_raw 구조에 맞춤)
 * 일반 컬럼과 MySQL JSON을 혼합한 하이브리드 구조
 */
@Entity
@Table(name = "voc_normalized")
public class ConsultingClassification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;
    
    @Column(name = "consulting_date", nullable = false)
    private LocalDateTime consultingDate;
    
    @Column(name = "client_gender", nullable = false, length = 10)
    private String clientGender;
    
    @Column(name = "client_age", nullable = false)
    private Integer clientAge;
    
    @Column(name = "consulting_turns", nullable = false)
    private Integer consultingTurns;
    
    @Column(name = "consulting_length", nullable = false)
    private Integer consultingLength;
    
    @Column(name = "consulting_content", nullable = false, columnDefinition = "TEXT")
    private String consultingContent;
    
    @Column(name = "processing_time")
    private Double processingTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 분류 결과 (별도 컬럼으로 저장)
    @Column(name = "consulting_category", length = 100)
    private String consultingCategory;
    
    // AI 분석 결과만 JSON으로 저장
    @Column(name = "analysis_result", nullable = false, columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private String analysisResult; // MySQL JSON으로 저장
    
    // 생성자
    public ConsultingClassification() {}
    
    public ConsultingClassification(String sourceId, String consultingContent) {
        this.sourceId = sourceId;
        this.consultingContent = consultingContent;
    }
    
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
    
    public String getClientGender() {
        return clientGender;
    }
    
    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }
    
    public Integer getClientAge() {
        return clientAge;
    }
    
    public void setClientAge(Integer clientAge) {
        this.clientAge = clientAge;
    }
    
    public Integer getConsultingTurns() {
        return consultingTurns;
    }
    
    public void setConsultingTurns(Integer consultingTurns) {
        this.consultingTurns = consultingTurns;
    }
    
    public Integer getConsultingLength() {
        return consultingLength;
    }
    
    public void setConsultingLength(Integer consultingLength) {
        this.consultingLength = consultingLength;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getAnalysisResult() {
        return analysisResult;
    }
    
    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
    
    public String getConsultingCategory() {
        return consultingCategory;
    }
    
    public void setConsultingCategory(String consultingCategory) {
        this.consultingCategory = consultingCategory;
    }
    
    // 편의 메서드들
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
