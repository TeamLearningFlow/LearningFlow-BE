package learningFlow.learningFlow_BE.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("🔍 [JwtAuthenticationFilter] 요청 수신: {}", request.getRequestURI());
        String jwt = getJwtFromRequest(request);
        log.info("🟡 [JwtAuthenticationFilter] 추출된 JWT: {}", jwt);

        try {
            if (StringUtils.hasText(jwt)) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    log.info("유효한 Access Token");

                    String email = jwtTokenProvider.getEmailFromToken(jwt);
                    log.info("토큰에서 추출한 이메일: {}", email);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("인증 정보 SecurityContext에 저장");

                } else {
                    log.info("Access Token이 만료되어 Refresh Token 확인을 시도");
                    String refreshToken = request.getHeader("Refresh-Token");
                    log.info("전달받은 Refresh Token: {}", refreshToken);

                    if (!StringUtils.hasText(refreshToken)) {
                        if (!isPermitAllUrl(request.getRequestURI())) {
                            handleAuthenticationError(response, "토큰이 만료되었습니다. 다시 로그인해주세요.");
                            return;
                        }
                    } else if (jwtTokenProvider.validateToken(refreshToken)) {
                        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
                        log.info("새로 발급된 Access Token: {}", newAccessToken);
                        response.addHeader("Authorization", "Bearer " + newAccessToken);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("새로운 Access Token으로 인증 정보를 업데이트 완료");
                    }
                }
            } else if (!isPermitAllUrl(request.getRequestURI())) {
                log.error("❌ [JwtAuthenticationFilter] 인증되지 않은 요청 → 401 반환");
                handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
                return;
            }
        } catch (Exception e) {
            log.error("❌ [JwtAuthenticationFilter] 예외 발생: {}", e.getMessage(), e);
            if (!isPermitAllUrl(request.getRequestURI())) {
                handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
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

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("🟡 [JwtAuthenticationFilter] Authorization 헤더: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isPermitAllUrl(String requestURI) {
        return requestURI.equals("/") ||
                requestURI.equals("/login") ||
                requestURI.equals("/register") ||
                requestURI.startsWith("/register/complete") ||
                requestURI.equals("/login/google") ||
                requestURI.startsWith("/oauth2") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/find") ||
                requestURI.startsWith("/search") ||
                requestURI.equals("/reset-password") ||
                requestURI.startsWith("/user/imgUpload");  // 이미지 업로드는 인증 없이 허용
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals("/image/upload") || isPermitAllUrl(path);
        log.info("🛑 [JwtAuthenticationFilter] shouldNotFilter 실행: path={}, shouldSkip={}", path, shouldSkip);
        return shouldSkip;
    }


}