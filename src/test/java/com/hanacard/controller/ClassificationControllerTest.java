package com.hanacard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanacard.dto.ClassificationRequest;
import com.hanacard.service.OpenAIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassificationController.class)
class ClassificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAIService openAIService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("healthy"));
    }

    @Test
    void testCategoriesEndpoint() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.count").value(23));
    }

    @Test
    void testClassifyEndpoint() throws Exception {
        // Mock OpenAI 서비스 응답
        OpenAIService.ClassificationResult mockResult = 
            new OpenAIService.ClassificationResult("이용내역 안내", 0.95);
        when(openAIService.classifyConsultingContent(anyString())).thenReturn(mockResult);

        ClassificationRequest request = new ClassificationRequest("TEST001", "카드 사용 내역을 확인하고 싶습니다.");

        mockMvc.perform(post("/api/classify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.consulting_category").value("이용내역 안내"));
    }

    @Test
    void testClassifyEndpointValidation() throws Exception {
        // source_id가 없는 요청
        ClassificationRequest invalidRequest = new ClassificationRequest();
        invalidRequest.setConsultingContent("카드 사용 내역을 확인하고 싶습니다.");

        mockMvc.perform(post("/api/classify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
