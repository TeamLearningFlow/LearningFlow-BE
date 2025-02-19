package learningFlow.learningFlow_BE.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // Authorization 헤더 제거
        response.setHeader("Authorization", "");
        response.setHeader("Refresh-Token", "");

        response.setHeader("Access-Control-Expose-Headers", "Authorization, Refresh-Token");

        log.info("현재 Authorization 헤더 값: {}", response.getHeader("Authorization"));
        log.info("현재 Refresh-Token 헤더 값: {}", response.getHeader("Refresh-Token"));

        log.info("로그아웃 처리 완료: {}",
                authentication != null ? authentication.getName() : "Unknown user");
    }
}