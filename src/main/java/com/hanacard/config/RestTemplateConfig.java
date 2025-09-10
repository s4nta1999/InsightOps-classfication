package com.hanacard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * 외부 API 호출용 (Admin, Dashboard)
 */
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10초
        // setReadTimeout is deprecated, use connectionRequestTimeout instead
        factory.setConnectionRequestTimeout(30000); // 30초
        return new RestTemplate(factory);
    }
}
