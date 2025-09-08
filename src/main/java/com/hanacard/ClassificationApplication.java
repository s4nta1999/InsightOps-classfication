package com.hanacard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 하나카드 상담 분류 마이크로서비스 메인 애플리케이션
 */
@SpringBootApplication
public class ClassificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassificationApplication.class, args);
        System.out.println("🚀 하나카드 상담 분류 마이크로서비스가 시작되었습니다.");
        System.out.println("📚 사용 가능한 엔드포인트:");
        System.out.println("   - POST /api/classify - 상담 내용 분류");
        System.out.println("   - GET /api/health - 서비스 상태 확인");
        System.out.println("   - GET /api/categories - 카테고리 목록 조회");
    }
}
