package com.hanacard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VoC Raw 데이터 엔티티
 * voc_raw 테이블과 매핑
 */
@Entity
@Table(name = "voc_raw")
public class VocRaw {
    
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
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "processed", nullable = false)
    private Boolean processed = false;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    // 생성자
    public VocRaw() {}
    
    public VocRaw(String sourceId, LocalDateTime consultingDate, String clientGender, 
                  Integer clientAge, Integer consultingTurns, Integer consultingLength, 
                  String consultingContent) {
        this.sourceId = sourceId;
        this.consultingDate = consultingDate;
        this.clientGender = clientGender;
        this.clientAge = clientAge;
        this.consultingTurns = consultingTurns;
        this.consultingLength = consultingLength;
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
    
    public String getConsultingContent() {
        return consultingContent;
    }
    
    public void setConsultingContent(String consultingContent) {
        this.consultingContent = consultingContent;
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
    
    public Boolean getProcessed() {
        return processed;
    }
    
    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
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
