package learningFlow.learningFlow_BE.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.service.auth.oauth.OAuth2UserTemp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JwtProperties jwtProperties;
    private final SecretKey jwtSecretKey;

    public String createAccessToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getAccessTokenValidityInSeconds() * 1000);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("category", "access")
                .claim("loginId", user.getLoginId())
                .claim("role",user.getRole().name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(jwtSecretKey)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getRefreshTokenValidityInSeconds() * 1000);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("category", "refresh")
                .claim("loginId", user.getLoginId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(jwtSecretKey)
                .compact();
    }

    public String createTemporaryToken(OAuth2UserTemp oauth2UserTemp) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 1800000); // 30분

        return Jwts.builder()
                .subject(oauth2UserTemp.getEmail())
                .claim("name", oauth2UserTemp.getName())
                .claim("providerId", oauth2UserTemp.getProviderId())
                .claim("socialType", oauth2UserTemp.getSocialType().name())
                .claim("isTemporary", true)  // 임시 토큰 구분을 위한 클레임
                .issuedAt(now)
                .expiration(validity)
                .signWith(jwtSecretKey)
                .compact();
    }

    public Cookie createCookie(String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); //JavaScript에서 접근 불가
        cookie.setPath("/");       // 모든 경로에서 접근 가능
        cookie.setMaxAge(maxAgeSeconds);

        //cookie.setSecure(true);    // HTTPS에서만 전송, https 적용 전에는 주석처리

        return cookie;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰입니다. : {}", e.getMessage());
            return false;
        }
    }

    // 임시 토큰인지 확인하는 메소드
    public boolean isTemporaryToken(String token) {
        return Boolean.TRUE.equals(getClaims(token).get("isTemporary", Boolean.class));
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰 종류 확인
    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationFromToken(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public long getRemainingTime(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 토큰 만료 여부 확인
    public boolean isExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다: {}", e.getMessage());
            return true;
        } catch (JwtException e) {
            log.error("토큰 만료 검증 오류: {}", e.getMessage());
            return true; // 오류 발생 시 만료된 것으로 처리
        }
    }
}
