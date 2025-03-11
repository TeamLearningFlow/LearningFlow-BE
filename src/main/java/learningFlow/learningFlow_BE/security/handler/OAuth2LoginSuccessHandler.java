package learningFlow.learningFlow_BE.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.security.jwt.JwtProperties;
import learningFlow.learningFlow_BE.security.jwt.JwtTokenProvider;
import learningFlow.learningFlow_BE.service.auth.oauth.OAuth2UserTemp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공!");

        if (authentication.getPrincipal() instanceof OAuth2UserTemp oAuth2UserTemp) {
            // 신규 회원: 임시 토큰 생성
            String temporaryToken = jwtTokenProvider.createTemporaryToken(oAuth2UserTemp);

            // 추가 정보 입력 페이지로 리다이렉트 - 임시 토큰은 쿼리 파라미터로 전달
            //String redirectUrl = frontendUrl + "/landing?oauth2RegistrationCode=" + temporaryToken; //배포용
            String redirectUrl = "http://localhost:8080/oauth2/additional-info?oauth2RegistrationCode=" + temporaryToken; //로컬
            response.sendRedirect(redirectUrl);
            return;
        }

        //인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 기존 사용자인 경우
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        //Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        log.info("Access 토큰 발급 : {}", accessToken);

        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        log.info("자동 로그인 활성화, Refresh Token 발급 : {}", refreshToken);

        // 쿠키 생성 및 응답에 추가
        Cookie accessCookie = jwtTokenProvider.createCookie(
                JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                (int) jwtProperties.getAccessTokenValidityInSeconds()  // 24시간
        );

        Cookie refreshCookie = jwtTokenProvider.createCookie(
                JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                (int) jwtProperties.getRefreshTokenValidityInSeconds()  // 30일
        );

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 메인 페이지로 리다이렉트 - 배포용
        //response.sendRedirect(frontendUrl);

        // 메인 페이지로 리다이렉트 - 로컬
        response.sendRedirect("/");
    }
}
