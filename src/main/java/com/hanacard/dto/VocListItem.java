package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * VoC 목록 항목 DTO
 */
public class VocListItem {
    
    @JsonProperty("vocEventId")
    private Long vocEventId;
    
    @JsonProperty("sourceId")
    private String sourceId;
    
    @JsonProperty("consultingDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime consultingDate;
    
    @JsonProperty("bigCategoryName")
    private String bigCategoryName;
    
    @JsonProperty("consultingCategoryName")
    private String consultingCategoryName;
    
    @JsonProperty("clientAge")
    private String clientAge;
    
    @JsonProperty("clientGender")
    private String clientGender;
    
    @JsonProperty("analysisResult")
    private String analysisResult;
    
    // 생성자
    public VocListItem() {}
    
    public VocListItem(Long vocEventId, String sourceId, LocalDateTime consultingDate, 
                      String bigCategoryName, String consultingCategoryName, 
                      String clientAge, String clientGender, String analysisResult) {
        this.vocEventId = vocEventId;
        this.sourceId = sourceId;
        this.consultingDate = consultingDate;
        this.bigCategoryName = bigCategoryName;
        this.consultingCategoryName = consultingCategoryName;
        this.clientAge = clientAge;
        this.clientGender = clientGender;
        this.analysisResult = analysisResult;
    }
    
    // Getter & Setter
    public Long getVocEventId() {
        return vocEventId;
    }
    
    public void setVocEventId(Long vocEventId) {
        this.vocEventId = vocEventId;
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
    
    public String getBigCategoryName() {
        return bigCategoryName;
    }
    
    public void setBigCategoryName(String bigCategoryName) {
        this.bigCategoryName = bigCategoryName;
    }
    
    public String getConsultingCategoryName() {
        return consultingCategoryName;
    }
    
    public void setConsultingCategoryName(String consultingCategoryName) {
        this.consultingCategoryName = consultingCategoryName;
    }
    
    public String getClientAge() {
        return clientAge;
    }
    
    public void setClientAge(String clientAge) {
        this.clientAge = clientAge;
    }
    
    public String getClientGender() {
        return clientGender;
    }
    
    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }
    
    public String getAnalysisResult() {
        return analysisResult;
    }
    
    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
}
