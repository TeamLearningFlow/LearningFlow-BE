package learningFlow.learningFlow_BE.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;

    @Transactional
    public UserResponseDTO.UserLoginResponseDTO register(UserRequestDTO.UserRegisterDTO requestDTO) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        // 로그인 ID 생성 (LOCAL_UUID 앞 8자리)
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "LOCAL_" + uuid;

        User user = User.builder()
                .loginId(loginId)
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .pw(passwordEncoder.encode(requestDTO.getPassword()))
                .job(requestDTO.getJob())
                .interestFields(requestDTO.getInterestFields())
                .birthDay(requestDTO.getBirthDay())
                .gender(requestDTO.getGender())
                .preferType(requestDTO.getPreferType())
                .socialType(SocialType.LOCAL)
                .role(Role.USER)
                .inactive(false)
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDTO.UserLoginResponseDTO.from(savedUser);
    }

    public UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLoginDTO request,
                                                      HttpServletRequest httpRequest,
                                                      HttpServletResponse httpResponse) {
        try {
            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authRequest);

            // Remember Me 처리
            if (request.isRememberMe()) {
                rememberMeServices.loginSuccess(httpRequest, httpResponse, authentication);
            }

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 응답 생성
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return UserResponseDTO.UserLoginResponseDTO.from(principalDetails.getUser());

        } catch (AuthenticationException e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }
}
