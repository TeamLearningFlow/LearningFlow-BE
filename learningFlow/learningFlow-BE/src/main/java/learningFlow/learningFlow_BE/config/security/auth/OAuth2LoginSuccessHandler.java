package learningFlow.learningFlow_BE.config.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.config.security.jwt.JwtTokenProvider;
import learningFlow.learningFlow_BE.service.user.OAuth2UserTemp;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static learningFlow.learningFlow_BE.converter.UserConverter.toUserLoginResponseDTO;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공!");

        // Principal 타입 확인
        if (authentication.getPrincipal() instanceof OAuth2UserTemp oAuth2UserTemp) {

            String temporaryToken = jwtTokenProvider.createTemporaryToken(oAuth2UserTemp);

            String redirectUrl = "/oauth2/additional-info?token=" + temporaryToken;
            response.sendRedirect(redirectUrl);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 기존 사용자인 경우
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        log.info("Access Token: {}", accessToken);
        log.info("Refresh Token: {}", refreshToken);

        // 토큰을 헤더에 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);

        // 헤더 설정 확인 로깅
        log.info("Authorization Header: {}", response.getHeader("Authorization"));
        log.info("Refresh-Token Header: {}", response.getHeader("Refresh-Token"));

        UserResponseDTO.UserLoginResponseDTO loginResponse =
                toUserLoginResponseDTO(principalDetails.getUser());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = new ObjectMapper().writeValueAsString(ApiResponse.onSuccess(loginResponse));
        response.getWriter().write(jsonResponse);
    }
}
