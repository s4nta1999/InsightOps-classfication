package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Admin API 응답 DTO
 */
public class AdminApiResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private AdminData data;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // 생성자
    public AdminApiResponse() {}
    
    // Getter & Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public AdminData getData() { return data; }
    public void setData(AdminData data) { this.data = data; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    /**
     * Admin API 응답의 data 필드
     */
    public static class AdminData {
        
        @JsonProperty("tableName")
        private String tableName;
        
        @JsonProperty("columns")
        private List<String> columns;
        
        @JsonProperty("data")
        private List<ConsultingCategoryData> data;
        
        @JsonProperty("totalCount")
        private int totalCount;
        
        @JsonProperty("timestamp")
        private String timestamp;
        
        // 생성자
        public AdminData() {}
        
        // Getter & Setter
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }
        
        public List<ConsultingCategoryData> getData() { return data; }
        public void setData(List<ConsultingCategoryData> data) { this.data = data; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
