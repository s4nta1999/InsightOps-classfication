package com.hanacard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 상담 분류 요청 DTO
 */
public class ClassificationRequest {

    @NotBlank(message = "source_id는 필수입니다.")
    @Size(max = 100, message = "source_id는 100자를 초과할 수 없습니다.")
    @JsonProperty("source_id")
    private String sourceId;

    @NotBlank(message = "상담 내용은 필수입니다.")
    @Size(max = 10000, message = "상담 내용은 10000자를 초과할 수 없습니다.")
    @JsonProperty("consulting_content")
    private String consultingContent;

    @JsonProperty("consulting_date")
    private LocalDate consultingDate;

    @JsonProperty("consulting_time")
    private LocalTime consultingTime;

    private Metadata metadata;

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

    public String getConsultingContent() {
        return consultingContent;
    }

    public void setConsultingContent(String consultingContent) {
        this.consultingContent = consultingContent;
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

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 메타데이터 클래스 (client_gender, client_age 제거됨)
     */
    public static class Metadata {
        @JsonProperty("consulting_turns")
        private String consultingTurns;

        @JsonProperty("consulting_length")
        private Integer consultingLength;

        // 생성자
        public Metadata() {}

        // Getter & Setter
        public String getConsultingTurns() {
            return consultingTurns;
        }

        public void setConsultingTurns(String consultingTurns) {
            this.consultingTurns = consultingTurns;
        }

        public Integer getConsultingLength() {
            return consultingLength;
        }

        public void setConsultingLength(Integer consultingLength) {
            this.consultingLength = consultingLength;
        }
    }
}