package learningFlow.learningFlow_BE.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import learningFlow.learningFlow_BE.security.jwt.JwtTokenProvider;
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

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        //Authentication 객체가 존재하는 경우, 로그아웃 처리
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(null);

        // SecurityContext 초기화
        SecurityContextHolder.getContextHolderStrategy().clearContext();

        // 쿠키 삭제
        deleteTokenCookie(response, JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME);
        deleteTokenCookie(response, JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);

        log.info("로그아웃 처리 완료: {}",
                authentication != null ? authentication.getName() : "Unknown user");
    }

    private void deleteTokenCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        log.info("쿠키 삭제: {}", cookieName);
    }
}