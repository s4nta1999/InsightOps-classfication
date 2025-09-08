package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * API 응답 공통 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;
    private String message;

    // 생성자
    public ApiResponse() {}

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, String error, String message) {
        this.success = success;
        this.error = error;
        this.message = message;
    }

    // 정적 팩토리 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true);
    }

    public static <T> ApiResponse<T> error(String error, String message) {
        return new ApiResponse<>(false, error, message);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, error, null);
    }

    // Getter & Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
