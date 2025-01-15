package learningFlow.learningFlow_BE.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.EmailVerificationToken;
import learningFlow.learningFlow_BE.domain.PasswordResetToken;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import learningFlow.learningFlow_BE.repository.EmailVerificationTokenRepository;
import learningFlow.learningFlow_BE.repository.PasswordResetTokenRepository;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static learningFlow.learningFlow_BE.converter.UserConverter.toUserLoginResponseDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional
    public void initialRegister(UserRequestDTO.InitialRegisterDTO requestDTO) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        // 진행 중인 이메일 인증이 있는지 확인
        if (emailVerificationTokenRepository.existsByEmailAndVerifiedFalse(requestDTO.getEmail())) {
            throw new RuntimeException("이미 진행 중인 이메일 인증이 있습니다. 이메일을 확인해주세요.");
        }

        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 이메일 인증 토큰 저장
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .expiryDate(LocalDateTime.now().plusHours(24))
                .verified(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        // 인증 이메일 발송
        emailService.sendVerificationEmail(requestDTO.getEmail(), token);
    }

    @Transactional
    public UserResponseDTO.UserLoginResponseDTO completeRegister(UserRequestDTO.CompleteRegisterDTO requestDTO) {
        // 토큰 유효성 검증
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenAndVerifiedFalse(requestDTO.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (token.isExpired()) {
            emailVerificationTokenRepository.delete(token);
            throw new RuntimeException("만료된 토큰입니다. 회원가입을 다시 진행해주세요.");
        }

        // 로그인 ID 생성
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "LOCAL_" + uuid;

        // 새로운 유저 생성
        User user = User.builder()
                .loginId(loginId)
                .email(token.getEmail())
                .pw(token.getPassword())
                .name(requestDTO.getName())
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

        // 토큰 verified 처리
        emailVerificationTokenRepository.delete(token);

        return toUserLoginResponseDTO(savedUser);
    }

    public UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLoginDTO request,
                                                      HttpServletRequest httpRequest,
                                                      HttpServletResponse httpResponse) {
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authRequest);

            log.info("로그인 성공: email={}, authorities={}",
                    authentication.getName(),
                    authentication.getAuthorities());

            // SecurityContext에 인증 정보 저장
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 세션 생성 및 SecurityContext 저장
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            log.info("SecurityContext 저장 완료: sessionId={}", session.getId());

            // Remember Me 처리
            if (request.isRememberMe()) {
                rememberMeServices.loginSuccess(httpRequest, httpResponse, authentication);
            }

            // 응답 생성
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return toUserLoginResponseDTO(principalDetails.getUser());

        } catch (AuthenticationException e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @Transactional
    public void sendPasswordResetEmail(UserRequestDTO.FindPasswordDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일로 가입된 계정이 없습니다."));

        if (user.getSocialType() != SocialType.LOCAL) {
            throw new RuntimeException("구글 로그인으로 가입된 계정입니다.");
        }

        // 기존 토큰이 있다면 제거
        Optional<PasswordResetToken> existToken = tokenRepository.findByUser(user);
        if (existToken.isPresent()) {
            PasswordResetToken passwordResetToken = existToken.get();
            tokenRepository.delete(passwordResetToken);
            tokenRepository.flush();
        }

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(UserRequestDTO.ResetPasswordDTO request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("만료된 토큰입니다. 비밀번호 재설정을 다시 요청해주세요.");
        }

        User user = resetToken.getUser();
        user.setPw(passwordEncoder.encode(request.getNewPassword()));

        // 사용된 토큰 삭제
        tokenRepository.delete(resetToken);
        log.info("비밀번호 재설정 완료: {}", user.getEmail());
    }
}
