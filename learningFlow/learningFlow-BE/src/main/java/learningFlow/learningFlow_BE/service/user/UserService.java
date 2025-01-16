package learningFlow.learningFlow_BE.service.user;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.config.security.jwt.JwtTokenProvider;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static learningFlow.learningFlow_BE.converter.UserConverter.toUserLoginResponseDTO;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // 추가 정보 입력 필요 여부와 필드 정보를 반환하는 메소드
    public Map<String, Object> getAdditionalInfoRequirements() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "추가 정보 입력이 필요합니다");
        response.put("requiredFields", Arrays.asList(
                "job", "interestFields", "birthDay", "gender", "preferType"
        ));

        return response;
    }

    @Transactional
    public UserResponseDTO.UserLoginResponseDTO updateAdditionalInfo(
            String temporaryToken,
            UserRequestDTO.AdditionalInfoDTO additionalInfo,
            HttpServletResponse response) {

        if (!jwtTokenProvider.validateToken(temporaryToken) || !jwtTokenProvider.isTemporaryToken(temporaryToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Claims claims = jwtTokenProvider.getClaims(temporaryToken);
        String email = claims.getSubject();
        String name = claims.get("name", String.class);
        String providerId = claims.get("providerId", String.class);
        SocialType socialType = SocialType.valueOf(claims.get("socialType", String.class));


        User newUser = User.builder()
                .loginId(socialType.name() + "_" + providerId)
                .email(email)
                .name(name)
                .providerId(providerId)
                .pw("OAUTH2_USER")
                .socialType(socialType)
                .job(additionalInfo.getJob())
                .interestFields(additionalInfo.getInterestFields())
                .birthDay(additionalInfo.getBirthDay())
                .gender(additionalInfo.getGender())
                .preferType(additionalInfo.getPreferType())
                .role(Role.USER)
                .inactive(false)
                .build();

        User savedUser = userRepository.save(newUser);

        //정식으로 JWT 토큰 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new PrincipalDetails(savedUser),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        response.addHeader("Authorization", "Bearer" + accessToken);
        response.addHeader("Refersh-Token", refreshToken);

        redisTemplate.opsForValue()
                .set("BLACKLIST:" + temporaryToken, "true",
                        jwtTokenProvider.getRemainingTime(temporaryToken),
                        TimeUnit.MILLISECONDS);

        return toUserLoginResponseDTO(savedUser);
    }
}
