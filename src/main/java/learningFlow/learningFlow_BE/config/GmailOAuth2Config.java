package learningFlow.learningFlow_BE.config;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Configuration
public class GmailOAuth2Config {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.refresh.token}")
    private String refreshToken;

    @Bean
    @Primary // 기존 JavaMailSender 대신 이 빈을 우선 사용
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.debug", true);

        // OAuth2 설정
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth.xoauth2.disable", "false");
        props.put("mail.smtp.from", username);
        props.put("mail.smtp.list-help", "mailto:" + username);

        // OAuth2 인증기 설정
        mailSender.setSession(Session.getInstance(props, new OAuth2Authenticator(
                username,
                clientId,
                clientSecret,
                refreshToken)));

        return mailSender;
    }

    // OAuth 2.0 인증 처리 클래스
    private static class OAuth2Authenticator extends Authenticator {
        private final String username;
        private final String clientId;
        private final String clientSecret;
        private final String refreshToken;
        private String accessToken;
        private long expirationTimeMillis;

        public OAuth2Authenticator(String username, String clientId, String clientSecret, String refreshToken) {
            this.username = username;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.refreshToken = refreshToken;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            if (accessToken == null || System.currentTimeMillis() > expirationTimeMillis) {
                refreshAccessToken();
            }

            return new PasswordAuthentication(username, accessToken);
        }

        private void refreshAccessToken() {
            try {
                ClientParametersAuthentication clientAuth =
                        new ClientParametersAuthentication(clientId, clientSecret);

                TokenResponse tokenResponse =
                        new RefreshTokenRequest(
                                new NetHttpTransport(),
                                GsonFactory.getDefaultInstance(),
                                new GenericUrl("https://oauth2.googleapis.com/token"),
                                refreshToken)
                                .setClientAuthentication(clientAuth)
                                .execute();

                accessToken = tokenResponse.getAccessToken();
                expirationTimeMillis = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);
            } catch (IOException e) {
                throw new RuntimeException("OAuth 액세스 토큰 갱신 실패: " + e.getMessage(), e);
            }
        }
    }
}
