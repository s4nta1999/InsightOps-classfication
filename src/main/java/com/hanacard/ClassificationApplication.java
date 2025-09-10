package com.hanacard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 하나카드 상담 분류 마이크로서비스 메인 애플리케이션
 * 최소한의 설정으로 Azure App Service 테스트
 */
@SpringBootApplication
@RestController
public class ClassificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassificationApplication.class, args);
        System.out.println("🚀 하나카드 상담 분류 마이크로서비스가 시작되었습니다.");
    }
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "하나카드 상담 분류 마이크로서비스");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "서비스가 정상적으로 실행 중입니다! 🚀");
        return response;
    }
    
    @GetMapping("/api/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", "healthy");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "테스트 성공! 서비스가 정상 배포되었습니다! 🎉");
        return response;
    }
    
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}