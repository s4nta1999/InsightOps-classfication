package com.hanacard.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 상담 분류 결과 엔티티
 * 일반 컬럼과 JSONB를 혼합한 하이브리드 구조
 */
@Entity
@Table(name = "consulting_classifications")
public class ConsultingClassification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;
    
    @Column(name = "consulting_content", nullable = false, columnDefinition = "TEXT")
    private String consultingContent;
    
    @Column(name = "processing_time")
    private Double processingTime;
    
    @Column(name = "consulting_date")
    private LocalDate consultingDate;
    
    @Column(name = "consulting_time")
    private LocalTime consultingTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // TEXT 컬럼들 (JSON 문자열로 저장)
    @Column(name = "analysis_result", nullable = false, columnDefinition = "TEXT")
    private String analysisResult; // JSON 문자열로 저장
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON 문자열로 저장
    
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
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
