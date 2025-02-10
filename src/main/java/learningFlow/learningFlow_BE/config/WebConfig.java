/*
package learningFlow.learningFlow_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 개발 중일 때만 사용
                // 또는 특정 출처만 허용
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:8081",
                        "http://onboarding.p-e.kr:8080",
                        "http://54.180.118.227",
                        "https://accounts.google.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(86400L);
    }
}
*/
