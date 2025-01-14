package learningFlow.learningFlow_BE.config.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.service.user.OAuth2UserTemp;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공!");

        // Principal 타입 확인
        if (authentication.getPrincipal() instanceof OAuth2UserTemp) {
            // 세션에 OAuth2UserTemp 저장
            HttpSession session = request.getSession();
            session.setAttribute("OAUTH2_USER_TEMP", authentication.getPrincipal());

            response.sendRedirect("/oauth2/additional-info");
            return;
        }

        // 기존 사용자인 경우
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO.UserLoginResponseDTO loginResponse =
                UserResponseDTO.UserLoginResponseDTO.from(principalDetails.getUser());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = new ObjectMapper().writeValueAsString(ApiResponse.onSuccess(loginResponse));
        response.getWriter().write(jsonResponse);
    }
}
