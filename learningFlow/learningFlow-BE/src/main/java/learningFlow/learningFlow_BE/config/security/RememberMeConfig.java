package learningFlow.learningFlow_BE.config.security;

import learningFlow.learningFlow_BE.service.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@RequiredArgsConstructor
public class RememberMeConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(
                "uniqueAndSecret",
                customUserDetailsService
        );
        rememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 7);
        return rememberMeServices;
    }
}
