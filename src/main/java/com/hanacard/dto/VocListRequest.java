package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * VoC 목록 조회 요청 DTO
 */
public class VocListRequest {
    
    @NotNull(message = "시작 날짜는 필수입니다.")
    @JsonProperty("startDate")
    private LocalDate startDate;
    
    @NotNull(message = "종료 날짜는 필수입니다.")
    @JsonProperty("endDate")
    private LocalDate endDate;
    
    @JsonProperty("page")
    private Integer page = 1;
    
    @JsonProperty("size")
    private Integer size = 10000;
    
    // 생성자
    public VocListRequest() {}
    
    public VocListRequest(LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page;
        this.size = size;
    }
    
    // Getter & Setter
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
}
