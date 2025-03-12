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
        log.info("📌 [JWT 필터] ================ 요청 시작: {} {}================", request.getMethod(), request.getRequestURI());

        try {
            // ✅ 쿠키에서 토큰 추출 시도
            String accessToken = extractTokenFromCookies(request, JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME);
            String refreshToken = extractTokenFromCookies(request, JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);

            log.info("🔍 [JWT 필터] 액세스 토큰: {}", accessToken != null ? "존재함" : "없음");
            log.info("🔍 [JWT 필터] 리프레시 토큰: {}", refreshToken != null ? "존재함" : "없음");

            boolean authenticated = false;
            boolean isPermitAllUrl = isPermitAllUrl(request.getRequestURI());
            log.info("🔍 [JWT 필터] 현재 URL '{}' - 인증 필수 여부: {}", request.getRequestURI(), !isPermitAllUrl ? "필수" : "불필요");

            // ✅ 액세스 토큰 처리 로직
            if (StringUtils.hasText(accessToken)) {
                log.info("🔄 [JWT 필터] 액세스 토큰 검증 시작");
                try {
                    // 액세스 토큰 검증 및 처리
                    authenticated = processAccessToken(request, accessToken);
                    log.info("✅ [JWT 필터] 액세스 토큰 검증 결과: {}", authenticated ? "성공" : "실패");
                } catch (ExpiredJwtException e) {
                    log.warn("⏰ [JWT 필터] 액세스 토큰 만료됨 - 리프레시 토큰으로 재발급 시도");
                    // 만료된 액세스 토큰 쿠키 삭제
                    deleteCookie(response, JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME);

                    // 리프레시 토큰이 있으면 액세스 토큰 재발급 시도
                    if (StringUtils.hasText(refreshToken)) {
                        log.info("🔄 [JWT 필터] 리프레시 토큰으로 액세스 토큰 재발급 시도");
                        authenticated = refreshAccessToken(request, response, refreshToken);
                        log.info("🔄 [JWT 필터] 토큰 재발급 결과: {}", authenticated ? "성공" : "실패");

                        if (authenticated) {
                            // 인증 성공 & 토큰 재발급했으므로 현재 요청은 다시 진행하도록 함
                            log.info("✅ [JWT 필터] 토큰 재발급 성공 - 요청 계속 진행");
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } else {
                        log.warn("❌ [JWT 필터] 액세스 토큰 만료됨 & 리프레시 토큰 없음");
                    }
                }
            } else if (StringUtils.hasText(refreshToken)) {
                // 액세스 토큰은 없지만 리프레시 토큰이 있는 경우
                log.info("🔄 [JWT 필터] 액세스 토큰 없음, 리프레시 토큰으로 액세스 토큰 발급 시도");
                authenticated = refreshAccessToken(request, response, refreshToken);
                log.info("🔄 [JWT 필터] 토큰 발급 결과: {}", authenticated ? "성공" : "실패");

                if (authenticated) {
                    // 인증 성공 & 토큰 재발급했으므로 현재 요청은 다시 진행
                    log.info("✅ [JWT 필터] 토큰 발급 성공 - 요청 계속 진행");
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                log.info("ℹ️ [JWT 필터] 액세스 토큰 & 리프레시 토큰 모두 없음");
            }

            // ✅ 인증 실패 & 회원 전용 URL인 경우 401 에러 반환
            if (!authenticated && !isPermitAllUrl) {
                log.warn("⛔ [JWT 필터] 인증 실패 & 인증 필수 URL - 접근 거부");
                handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
                return;
            }

            // 인증 성공했거나 허용된 URL인 경우 다음 필터로 진행
            if (authenticated) {
                log.info("✅ [JWT 필터] 인증 성공 - 요청 계속 진행");
            } else if (isPermitAllUrl) {
                log.info("🔓 [JWT 필터] 인증 불필요 URL - 비회원으로 요청 계속 진행");
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("⚠️ [JWT 필터] 예외 발생: {}", e.getMessage(), e);

            // 허용된 URL이 아닌 경우에만 에러 응답
            if (!isPermitAllUrl(request.getRequestURI())) {
                log.error("⛔ [JWT 필터] 인증 필수 URL에서 예외 발생 - 접근 거부");
                handleAuthenticationError(response, "인증 처리 중 오류가 발생했습니다.");
                return;
            }

            // 허용된 URL이면 다음 필터로 진행
            log.info("🔓 [JWT 필터] 인증 불필요 URL에서 예외 발생 - 비회원으로 요청 계속 진행");
            filterChain.doFilter(request, response);
        } finally {
            log.info("📌 [JWT 필터] ================ 요청 처리 완료: {} {} ================", request.getMethod(), request.getRequestURI());
        }
    }

    // ✅ 액세스 토큰 검증 및 처리
    private boolean processAccessToken(HttpServletRequest request, String accessToken) {
        log.info("🔎 [JWT 필터] 액세스 토큰 검증 과정 시작");

        // 액세스 토큰 검증
        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.error("❌ [JWT 필터] 액세스 토큰 검증 실패 - 유효하지 않은 토큰");
            return false;
        }
        log.info("✓ [JWT 필터] 액세스 토큰 서명 유효성 검증 성공");

        // 토큰 종류 확인
        String category = jwtTokenProvider.getCategory(accessToken);
        if (!"access".equals(category)) {
            log.error("❌ [JWT 필터] 액세스 토큰 종류 검증 실패 - 현재 종류: {}", category);
            return false;
        }
        log.info("✓ [JWT 필터] 액세스 토큰 종류 검증 성공");

        // 만료 여부 확인 (이 부분이 실행되면 이미 validateToken에서 검증되었다는 의미)
        if (jwtTokenProvider.isExpired(accessToken)) {
            log.error("❌ [JWT 필터] 액세스 토큰 만료됨");
            throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다");
        }
        log.info("✓ [JWT 필터] 액세스 토큰 만료 여부 검증 성공");

        // 사용자 정보 추출 및 인증 정보 설정
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        log.info("✓ [JWT 필터] 토큰에서 추출한 이메일: {}", email);

        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            log.info("✓ [JWT 필터] DB에서 사용자 정보 조회 성공: {}", email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("✅ [JWT 필터] 인증 정보 저장 완료: 사용자 {}, 권한: {}", email, userDetails.getAuthorities());
            return true;
        } catch (Exception e) {
            log.error("❌ [JWT 필터] 사용자 정보 조회 또는 인증 설정 실패: {}", e.getMessage());
            return false;
        }
    }

    // ✅ 리프레시 토큰으로 액세스 토큰 재발급
    private boolean refreshAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws IOException {
        log.info("🔄 [JWT 필터] 리프레시 토큰으로 액세스 토큰 재발급 과정 시작");

        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("❌ [JWT 필터] 리프레시 토큰 검증 실패 - 유효하지 않은 토큰");
            return false;
        }
        log.info("✓ [JWT 필터] 리프레시 토큰 서명 유효성 검증 성공");

        // 리프레시 토큰 종류 확인
        String category = jwtTokenProvider.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            log.error("❌ [JWT 필터] 리프레시 토큰 종류 검증 실패 - 현재 종류: {}", category);
            deleteCookie(response, JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);
            return false;
        }
        log.info("✓ [JWT 필터] 리프레시 토큰 종류 검증 성공");

        // 리프레시 토큰 만료 확인
        if (jwtTokenProvider.isExpired(refreshToken)) {
            log.error("❌ [JWT 필터] 리프레시 토큰 만료됨");
            deleteCookie(response, JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);
            return false;
        }
        log.info("✓ [JWT 필터] 리프레시 토큰 만료 여부 검증 성공");

        try {
            // 사용자 정보 추출 및 인증 정보 설정
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            log.info("✓ [JWT 필터] 리프레시 토큰에서 추출한 이메일: {}", email);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            log.info("✓ [JWT 필터] DB에서 사용자 정보 조회 성공: {}", email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 새 액세스 토큰 생성
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
            log.info("✓ [JWT 필터] 새 액세스 토큰 생성 성공: 사용자 {}", email);

            // 쿠키에 저장
            Cookie accessCookie = jwtTokenProvider.createCookie(
                    JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME,
                    newAccessToken,
                    (int) jwtProperties.getAccessTokenValidityInSeconds()
            );
            response.addCookie(accessCookie);
            log.info("✓ [JWT 필터] 새 액세스 토큰 쿠키 설정 완료 (유효시간: {}초)", jwtProperties.getAccessTokenValidityInSeconds());

            // 인증 정보 설정
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("✅ [JWT 필터] 인증 정보 저장 완료: 사용자 {}, 권한: {}", email, userDetails.getAuthorities());

            return true;
        } catch (Exception e) {
            log.error("❌ [JWT 필터] 액세스 토큰 재발급 과정 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    // ✅ 인증 오류 처리
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        log.error("⛔ [JWT 필터] 인증 실패 처리: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        ApiResponse.onFailure("AUTH4001", message, null)
                )
        );
        log.info("✓ [JWT 필터] 401 응답 전송 완료: {}", message);
    }

    // ✅ 쿠키에서 토큰 추출 (개선된 메소드)
    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("🍪 [JWT 필터] 요청에 쿠키가 없음");
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                log.info("🍪 [JWT 필터] 쿠키 '{}' 발견", cookieName);
                return cookie.getValue();
            }
        }

        log.info("🍪 [JWT 필터] 쿠키 '{}' 발견되지 않음", cookieName);
        return null;
    }

    // ✅ 쿠키 삭제
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        log.info("🍪 [JWT 필터] 쿠키 삭제 완료: {}", cookieName);
    }

    // ✅ 허용된 URL 확인
    private boolean isPermitAllUrl(String requestURI) {
        boolean isPermit = requestURI.equals("/") ||
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

        if (isPermit) {
            log.info("🔓 [JWT 필터] URL '{}': 인증 불필요 URL", requestURI);
        } else {
            log.info("🔒 [JWT 필터] URL '{}': 인증 필수 URL", requestURI);
        }

        return isPermit;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals("/image/upload") || path.equals("/favicon.ico");
        log.info("🔄 [JWT 필터] shouldNotFilter 실행: path={}, 필터 적용 여부: {}", path, !shouldSkip ? "적용" : "적용 안함");
        return shouldSkip;
    }
}