package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * VoC 목록 조회 응답 DTO
 */
public class VocListResponse {
    
    @JsonProperty("data")
    private List<VocListItem> data;
    
    @JsonProperty("totalCount")
    private Long totalCount;
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("size")
    private Integer size;
    
    // 생성자
    public VocListResponse() {}
    
    public VocListResponse(List<VocListItem> data, Long totalCount, Integer page, Integer size) {
        this.data = data;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
    }
    
    // Getter & Setter
    public List<VocListItem> getData() {
        return data;
    }
    
    public void setData(List<VocListItem> data) {
        this.data = data;
    }
    
    public Long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
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
