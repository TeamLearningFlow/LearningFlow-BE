package learningFlow.learningFlow_BE.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
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

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공!");

        // Principal 타입 확인, 첫 로그인인 경우 회원가입으로 이동
        if (authentication.getPrincipal() instanceof OAuth2UserTemp oAuth2UserTemp) {
            String temporaryToken = jwtTokenProvider.createTemporaryToken(oAuth2UserTemp);
            String redirectUrl = frontendUrl + "/oauth2/additional-info?oauth2RegistrationCode=" + temporaryToken;
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
//        response.setHeader("Authorization", "Bearer " + accessToken);

        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        log.info("자동 로그인 활성화, Refresh Token 발급 : {}", refreshToken);
//        response.setHeader("Refresh-Token", refreshToken);

        // 헤더 설정 확인 로깅
        log.info("Authorization Header: {}", response.getHeader("Authorization"));
        log.info("Refresh-Token Header: {}", response.getHeader("Refresh-Token"));

/*
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Refresh-Token");
*/

/*
        UserResponseDTO.UserLoginResponseDTO loginResponse =
                toUserLoginResponseDTO(principalDetails.getUser());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = new ObjectMapper().writeValueAsString(ApiResponse.onSuccess(loginResponse));
        response.getWriter().write(jsonResponse);
*/
//        response.setStatus(HttpStatus.OK.value());

        // HTTP-Only 쿠키 설정 (Refresh Token)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS에서만
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")  // 필요하면 Lax, Strict
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Refresh-Token");

        // ⭐️ 팝업 창을 닫고 부모 창에 메시지 전달하는 스크립트 (프론트엔드 도메인 사용)
/*
        String redirectScript = "<script>" +
                "  window.opener.postMessage({" +
                "    accessToken: '" + accessToken + "'," +
                "    refreshToken: 'refreshToken'" + "  }, 'https://onboarding-kappa.vercel.app');" + // ⭐️ 프론트엔드 도메인과 포트
                "  window.close();" +
                "</script>";
*/

        String redirectScript = "<script>" +
                "  window.opener.postMessage({" +
                "    accessToken: '" + accessToken + "'" +
                "  }, 'https://onboarding-kappa.vercel.app');" + // 프론트엔드 origin
                "  window.close();" +
                "</script>";
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(redirectScript);
    }
}
