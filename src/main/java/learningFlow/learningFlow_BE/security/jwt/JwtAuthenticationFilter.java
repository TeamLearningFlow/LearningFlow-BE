package learningFlow.learningFlow_BE.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.service.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("🔍 [JwtAuthenticationFilter] 요청 수신: {}", request.getRequestURI());
        // 토큰 추출 : 기존 헤더사용에서 쿠키사용으로 변경
        String jwt = extractTokenFromCookies(request);
        log.info("🟡 [JwtAuthenticationFilter] 추출된 JWT: {}", jwt);

        // JWT 토큰이 있는 경우, URL 상관없이 검증 시도
        if (StringUtils.hasText(jwt)) {
            try {
                // 토큰 유효성 검사 및 인증 처리
                processToken(request, response, jwt);
                // 성공적으로 처리된 경우 다음 필터로 진행
                filterChain.doFilter(request, response);
                return;
            } catch (ExpiredJwtException e) {
                log.info("Access 토큰 만료");
                // 만료된 쿠키 삭제
                deleteCookie(response, JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME);

                // 허용된 URL이 아닌 경우에만 리프레시 시도
                if (!isPermitAllUrl(request.getRequestURI())) {
                    // 리프레시 토큰으로 갱신 시도
                    processExpiredToken(request, response);
                    return;
                }
                // 허용된 URL이면 인증 없이 계속 진행
            } catch (Exception e) {
                log.error("❌ [JwtAuthenticationFilter] 예외 발생: {}", e.getMessage(), e);

                // 허용된 URL이 아닌 경우에만 에러 응답
                if (!isPermitAllUrl(request.getRequestURI())) {
                    handleAuthenticationError(response, "인증 처리 중 오류가 발생했습니다.");
                    return;
                }
                // 허용된 URL이면 인증 없이 계속 진행
            }
        }

        // 토큰이 없거나 검증 실패한 경우
        if (!isPermitAllUrl(request.getRequestURI())) {
            // 비허용 URL에 토큰 없이 접근 시 401 에러
            handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
            return;
        }

        // 허용된 URL이거나 인증에 성공한 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    //토큰이 유효한지 검증
    private void processToken(HttpServletRequest request, HttpServletResponse response, String jwt) throws Exception {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(jwt)) {
            handleAuthenticationError(response, "유효하지 않은 토큰입니다.");
            return;
        }

        // 토큰 종류 확인
        String category = jwtTokenProvider.getCategory(jwt);
        if (!"access".equals(category)) {
            handleAuthenticationError(response, "유효한 액세스 토큰이 아닙니다.");
            return;
        }

        // 만료 여부 확인
        if (jwtTokenProvider.isExpired(jwt)) {
            // 만료된 쿠키 삭제
            deleteCookie(response, JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME);
            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        // 유효한 토큰 처리
        processValidToken(request, jwt);
    }

    //유효한 토큰 처리
    private void processValidToken(HttpServletRequest request, String jwt) {
        String email = jwtTokenProvider.getEmailFromToken(jwt);
        log.info("토큰에서 추출한 이메일: {}", email);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("인증 정보 SecurityContext에 저장");
    }

    private void processExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String refreshToken = request.getHeader("Refresh-Token");
        String refreshToken = extractRefreshTokenFromCookies(request);
        log.info("쿠키에서 가져온 Refresh Token: {}", refreshToken != null ? "존재" : "없음");

        if (StringUtils.hasText(refreshToken) && jwtTokenProvider.validateToken(refreshToken)) {

            // 리프레시 토큰 종류 확인
            String category = jwtTokenProvider.getCategory(refreshToken);
            if (!"refresh".equals(category)) {
                log.error("유효한 리프레시 토큰이 아닙니다.");
                handleAuthenticationError(response, "유효한 리프레시 토큰이 아닙니다.");
                return;
            }

            // 리프레시 토큰 만료 확인
            if (jwtTokenProvider.isExpired(refreshToken)) {
                log.error("리프레시 토큰이 만료되었습니다.");
                deleteCookie(response, JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);
                handleAuthenticationError(response, "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
                return;
            }

            // 새 액세스 토큰 발급
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
            log.info("새로 발급된 Access Token: {}", newAccessToken);

            // 쿠키에 새 액세스 토큰 저장
            Cookie accessCookie = jwtTokenProvider.createCookie(
                    JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME,
                    newAccessToken,
                    (int) jwtProperties.getAccessTokenValidityInSeconds()
            );
            response.addCookie(accessCookie);

            // 프론트에게 토큰 갱신 알림과 재시도 요청 -> 프론트에서 만약 401에러이고, 헤더에 Access-Token-Renewed가 있으면 재시도
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setHeader("Access-Token-Renewed", "true"); // 커스텀 헤더
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            ApiResponse.onSuccess("Access Token이 갱신되었습니다. 요청을 다시 시도해주세요.")
                    )
            );
        }
        else{
            if (!isPermitAllUrl(request.getRequestURI())) {
                log.error("❌ [JwtAuthenticationFilter] 인증되지 않은 요청 → 401 반환");
                handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
            }
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        log.error("🚨 [JwtAuthenticationFilter] 인증 실패: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        ApiResponse.onFailure("AUTH4001", message, null)
                )
        );
    }

    // 쿠키에서 Access Token 추출
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("요청에 쿠키가 없음");
            return null;
        }
        for (Cookie cookie : cookies) {
            if (JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                log.info("쿠키에서 Access Token 발견");
                return cookie.getValue();
            }
        }
        log.info("쿠키는 존재하지만, Access Token 쿠키 없음");
        return null;
    }

    // 쿠키에서 Refresh Token 추출
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 헤더에서 토큰 추출
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("🟡 [JwtAuthenticationFilter] Authorization 헤더: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        log.info("쿠키 삭제: {}", cookieName);
    }

    private boolean isPermitAllUrl(String requestURI) {
        return requestURI.equals("/") ||
                requestURI.equals("/login") ||
                requestURI.equals("/register") ||
                requestURI.equals("/login/google") ||
                requestURI.equals("/reset-password") ||
                requestURI.startsWith("/register/complete") ||
                requestURI.startsWith("/oauth/callback") ||
                requestURI.startsWith("/oauth2") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/find") ||
                requestURI.startsWith("/search") ||
                requestURI.startsWith("/user/imgUpload") || // 이미지 업로드는 인증 없이 허용
                requestURI.contains("/user/change-email") ||
                requestURI.matches("/collections/\\d+");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals("/image/upload") || path.equals("/favicon.ico");
        log.info("🛑 [JwtAuthenticationFilter] shouldNotFilter 실행: path={}, shouldSkip={}", path, shouldSkip);
        return shouldSkip;
    }
}