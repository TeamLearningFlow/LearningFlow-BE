package learningFlow.learningFlow_BE.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.service.user.OAuth2UserTemp;
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

    private final JwtProperties jwtProperties;
    private final SecretKey jwtSecretKey;

    public String createAccessToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getAccessTokenValidityInSeconds() * 1000);

        return Jwts.builder()
                .subject(user.getEmail())
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
                .issuedAt(now)
                .expiration(validity)
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {

        return getClaims(token).getSubject();
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

    public long getExpirationFromToken(String token) {
        return getClaims(token).getExpiration().getTime();
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

    // 임시 토큰인지 확인하는 메소드
    public boolean isTemporaryToken(String token) {

        return Boolean.TRUE.equals(getClaims(token).get("isTemporary", Boolean.class));
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getRemainingTime(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
