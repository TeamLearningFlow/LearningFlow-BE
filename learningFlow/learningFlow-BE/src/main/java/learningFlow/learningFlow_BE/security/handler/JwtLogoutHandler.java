package learningFlow.learningFlow_BE.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String jwt = getJwtFromRequest(request);
        addToBlacklist(jwt);
    }

    public void addToBlacklist(String token) {
        log.info("토큰 블랙 리스트 추가 시도");

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            log.info("유효한 토큰 발견: {}", token);
            long expiration = jwtTokenProvider.getExpirationFromToken(token);
            long now = System.currentTimeMillis();
            long remainingTime = (expiration - now) / 1000;

            redisTemplate.opsForValue()
                    .set("BLACKLIST:" + token, "true", remainingTime, TimeUnit.SECONDS);
            log.info("토큰이 블랙리스트에 추가됨");
        } else {
            log.info("유효하지 않은 토큰");
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
