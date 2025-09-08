package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 상담 분류 응답 DTO
 */
public class ClassificationResponse {

    @JsonProperty("source_id")
    private String sourceId;

    @JsonProperty("consulting_category")
    private String consultingCategory;

    private Double confidence;

    @JsonProperty("processing_time")
    private Double processingTime;

    @JsonProperty("consulting_date")
    private LocalDate consultingDate;

    @JsonProperty("consulting_time")
    private LocalTime consultingTime;

    // 생성자
    public ClassificationResponse() {}

    public ClassificationResponse(String sourceId, String consultingCategory, Double confidence, Double processingTime) {
        this.sourceId = sourceId;
        this.consultingCategory = consultingCategory;
        this.confidence = confidence;
        this.processingTime = processingTime;
    }

    // Getter & Setter
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getConsultingCategory() {
        return consultingCategory;
    }

    public void setConsultingCategory(String consultingCategory) {
        this.consultingCategory = consultingCategory;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
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
}
