package learningFlow.learningFlow_BE.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/proxy/blog") // 프록시 서버 API에 대해 CORS 허용
                        .allowedOrigins("*") // 모든 도메인에서 접근 가능 (필요하면 특정 도메인만 허용 가능)
                        .allowedMethods("GET")
                        .allowedHeaders("*");
            }
        };
    }
}
