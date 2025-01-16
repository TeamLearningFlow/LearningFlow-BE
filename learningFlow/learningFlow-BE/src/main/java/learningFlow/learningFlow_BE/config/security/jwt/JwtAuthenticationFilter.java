package learningFlow.learningFlow_BE.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        // 로그인, 회원가입 등 인증이 필요없는 경로는 토큰 검증을 건너뛰도록 설정
        if (isPermitAllUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 로그아웃 요청은 토큰 검증 스킵
        if (request.getRequestURI().equals("/logout/test")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                //Redis에서 블랙리스트 체크하기 - 로그아웃된 사용자인지 여부 파악
                Boolean isBlacklisted = redisTemplate.hasKey("BLACKLIST:" + jwt);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    throw new RuntimeException("이미 로그아웃된 토큰입니다.");
                }

                String email = jwtTokenProvider.getEmailFromToken(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
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
                requestURI.startsWith("/oauth2");
    }
}
