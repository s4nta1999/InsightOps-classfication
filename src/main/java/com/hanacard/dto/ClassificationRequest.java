package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 상담 분류 요청 DTO (voc_raw 구조에 맞춤)
 */
public class ClassificationRequest {

    @NotBlank(message = "source_id는 필수입니다.")
    @Size(max = 255, message = "source_id는 255자를 초과할 수 없습니다.")
    @JsonProperty("source_id")
    private String sourceId;

    @NotNull(message = "상담 날짜/시간은 필수입니다.")
    @JsonProperty("consulting_date")
    private LocalDateTime consultingDate;

    @NotBlank(message = "고객 성별은 필수입니다.")
    @JsonProperty("client_gender")
    private String clientGender;

    @NotNull(message = "고객 연령대는 필수입니다.")
    @JsonProperty("client_age")
    private Integer clientAge;

    @NotNull(message = "상담 턴 수는 필수입니다.")
    @JsonProperty("consulting_turns")
    private Integer consultingTurns;

    @NotNull(message = "상담 길이는 필수입니다.")
    @JsonProperty("consulting_length")
    private Integer consultingLength;

    @NotBlank(message = "상담 내용은 필수입니다.")
    @Size(max = 10000, message = "상담 내용은 10000자를 초과할 수 없습니다.")
    @JsonProperty("consulting_content")
    private String consultingContent;

    // 생성자
    public ClassificationRequest() {}

    public ClassificationRequest(String sourceId, String consultingContent) {
        this.sourceId = sourceId;
        this.consultingContent = consultingContent;
    }

    // Getter & Setter
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

}