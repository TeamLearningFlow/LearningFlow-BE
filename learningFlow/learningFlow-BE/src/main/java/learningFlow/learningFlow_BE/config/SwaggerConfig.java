package learningFlow.learningFlow_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30) // OpenAPI 3.0 적용
                .select()
                .apis(RequestHandlerSelectors.basePackage("learningFlow.learningFlow_BE.web.controller")) // Controller 경로 설정
                .paths(PathSelectors.any())
                .build();
    }
}

