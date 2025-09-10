package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Admin API에서 받아오는 상담 카테고리 데이터
 */
public class ConsultingCategoryData {
    
    @JsonProperty("category_id")
    private String id;
    
    @JsonProperty("consulting_category")
    private String categoryName;
    
    @JsonProperty("assignee_id")
    private String assigneeId;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    // 생성자
    public ConsultingCategoryData() {}
    
    public ConsultingCategoryData(String id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
    
    public ConsultingCategoryData(String id, String categoryName, String assigneeId, String createdAt, String updatedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.assigneeId = assigneeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}