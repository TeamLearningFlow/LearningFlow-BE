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
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT 필터 진입, URL: {}", request.getRequestURI());

        /**
         * 로그인, 회원가입 등 인증이 필요없는 경로는 토큰 검증을 건너뛰도록 설정
         * 인증 미필요 URL 체크
         */
        if (isPermitAllUrl(request.getRequestURI())) {
            log.info("인증이 필요없는 URL: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            log.info("요청에서 추출한 토큰: {}", jwt);

            if (StringUtils.hasText(jwt)) {
                //AccessToken 검증
                if (jwtTokenProvider.validateToken(jwt)) {
                    log.info("유효한 Access Token");

                    //로그아웃 요청이 아닌 경우에만 블랙리스트 체크
                    if (!request.getRequestURI().equals("/logout/test")) {
                        //TODO : 테스트 위해서 URI 설정을 /logout/test로 해놓음. 추후 수정 필요
                        //Redis에서 블랙리스트 체크하기 - 로그아웃된 사용자인지 여부 파악
                        Boolean isBlacklisted = redisTemplate.hasKey("BLACKLIST:" + jwt);
                        log.info("블랙리스트 체크 결과: {}", isBlacklisted);

                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            throw new RuntimeException("이미 로그아웃된 토큰입니다.");
                        }
                    }

                    //유효한 토큰이면 인증 처리
                    String email = jwtTokenProvider.getEmailFromToken(jwt);
                    log.info("토큰에서 추출한 이메일: {}", email);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("인증 정보 SecurityContext에 저장");
                } else {
                    log.info("Access Token이 만료되어 Refresh Token 확인을 시도");
                    // Access Token이 만료된 경우, Refresh Token 확인
                    String refreshToken = request.getHeader("Refresh-Token");
                    log.info("전달받은 Refresh Token: {}", refreshToken);

                    if (!StringUtils.hasText(refreshToken)) {
                        log.info("Refresh Token이 없음 - 재로그인 필요");
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        String error = new ObjectMapper().writeValueAsString(
                                ApiResponse.onFailure("401", "토큰이 만료되었습니다. 다시 로그인해주세요.", null)
                        );
                        response.getWriter().write(error);
                        return;
                    }

                    if (jwtTokenProvider.validateToken(refreshToken)) {
                        log.info("유효한 Refresh Token. 새로운 Access Token을 발급 시작");

                        // Refresh Token이 유효하면 새로운 Access Token 발급
                        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication
                                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        //새로운 AccessToken 발급 및 헤더에 추가
                        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
                        log.info("새로 발급된 Access Token: {}", newAccessToken);
                        response.addHeader("Authorization", "Bearer " + newAccessToken);

                        //새로운 토큰으로 인증 처리
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("새로운 Access Token으로 인증 정보를 업데이트 완료");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Security Context에서 사용자 인증을 설정할 수 없습니다.", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isPermitAllUrl(String requestURI) {
        return requestURI.equals("/login") ||
                requestURI.equals("/register") ||
                requestURI.startsWith("/register/complete") ||
                requestURI.equals("/login/google") ||
                requestURI.startsWith("/oauth2") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/find") ||
                requestURI.equals("/reset-password");
    }
}
