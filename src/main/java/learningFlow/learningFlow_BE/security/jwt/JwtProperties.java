package learningFlow.learningFlow_BE.security.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties {
    private long accessTokenValidityInSeconds = 3600; // 1시간
    //private long refreshTokenValidityInSeconds = 604800; // 1주일
}
