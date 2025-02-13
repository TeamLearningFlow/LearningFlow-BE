package learningFlow.learningFlow_BE.service.auth.local;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;
import learningFlow.learningFlow_BE.domain.uuid.Uuid;
import learningFlow.learningFlow_BE.domain.uuid.UuidRepository;
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.LoginHandler;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.security.handler.JwtLogoutHandler;
import learningFlow.learningFlow_BE.security.jwt.JwtTokenProvider;
import learningFlow.learningFlow_BE.domain.EmailVerificationToken;
import learningFlow.learningFlow_BE.domain.PasswordResetToken;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import learningFlow.learningFlow_BE.repository.EmailVerificationTokenRepository;
import learningFlow.learningFlow_BE.repository.PasswordResetTokenRepository;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.service.auth.common.UserVerificationEmailService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static learningFlow.learningFlow_BE.converter.UserConverter.toUserLoginResponseDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserVerificationEmailService userVerificationEmailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

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
        //TODO: 현재는 진행중이던 이메일이면 500에러가 나는데 400에러가 나야함!

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
        userVerificationEmailService.sendVerificationEmail(requestDTO.getEmail(), token);
    }

    @Transactional
    public EmailVerificationToken validateRegistrationToken(String emailVerificationCode) {
        // 토큰 유효성 검증
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByTokenAndVerifiedFalse(emailVerificationCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (verificationToken.isExpired()) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("만료된 토큰입니다. 회원가입을 다시 진행해주세요.");
        }

        return verificationToken;
    }

    @Transactional
    public UserResponseDTO.UserLoginResponseDTO completeRegister(
            String emailVerificationCode,
            UserRequestDTO.CompleteRegisterDTO requestDTO,
            HttpServletResponse response
    ) {
//        String profileImgUrl = null;
        //이메일 토큰 검증
        EmailVerificationToken verificationToken = validateRegistrationToken(emailVerificationCode);

        // 로그인 ID 생성
        String loginuuid = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "LOCAL_" + loginuuid;

//        //이미지 파일
//        if (imageFile != null && !imageFile.isEmpty()) {
//            log.info("이미지 업로드 요청 발생");
//            imageUrl = s3Manager.uploadImageToS3(imageFile);
//            // user 엔티티에 이미지 URL 업데이트
//            //user.updateImage(imageUrl);
//        }


        // 새로운 유저 생성
        User user = User.builder()
                .loginId(loginId)
                .email(verificationToken.getEmail())
                .pw(verificationToken.getPassword())
                .name(requestDTO.getName())
                .job(requestDTO.getJob())
                .interestFields(requestDTO.getInterestFields())
                .preferType(requestDTO.getPreferType())
                .socialType(SocialType.LOCAL)
                .role(Role.USER)
                .profileImgUrl(requestDTO.getImgProfileUrl())
                .inactive(false)
                .build();

        User savedUser = userRepository.save(user);

        // 토큰 verified 처리
        emailVerificationTokenRepository.delete(verificationToken);

        return toUserLoginResponseDTO(savedUser);
    }

//    private String uploadImageToS3(MultipartFile imageFile) {
//        if (imageFile == null || imageFile.isEmpty()) {
//            throw new GeneralException(ErrorStatus.IMAGE_FORMAT_BADREQUEST); // 이미지 파일 검증 실패
//        }
//
//        try {
//            // UUID 생성 및 저장
//            String imageUuid = UUID.randomUUID().toString();
//            Uuid savedUuid = uuidRepository.save(Uuid.builder()
//                    .uuid(imageUuid).build());
//
//            // 이미지 업로드
//            String imageKey = s3Manager.generateKeyName(savedUuid); // KeyName 생성
//            String imageUrl = s3Manager.uploadFile(imageKey, imageFile); // 업로드된 URL 반환
//
//            // 업로드 성공 여부 확인
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                throw new GeneralException(ErrorStatus._BAD_REQUEST); // 업로드 실패 시 예외 처리
//            }
//
//            return imageUrl; // 성공 시 URL 반환
//
//        } catch (GeneralException e) {
//            log.error("이미지 업로드 실패: {}", e.getMessage());
//            throw e; // GeneralException은 그대로 전달
//        } catch (Exception e) {
//            log.error("이미지 업로드 중 내부 오류 발생: {}", e.getMessage());
//            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR); // 기타 예외 처리
//        }
//    }





    public UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLoginDTO request,
                                                      HttpServletResponse response) {
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authRequest);

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 응답 생성
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            log.info("사용된 JWT access 토큰: Authorization={}", "Bearer " + accessToken);
            response.addHeader("Authorization", "Bearer " + accessToken);

            if (request.isRemember()) {
                String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
                log.info("자동 로그인 활성화: Refresh Token 발급");
                log.info("사용된 JWT refresh 토큰: Refresh Token={}", refreshToken);
                response.addHeader("Refresh-Token", refreshToken);
            }

            log.info("로그인 성공: email={}, authorities={}",
                    authentication.getName(),
                    authentication.getAuthorities());

            return toUserLoginResponseDTO(principalDetails.getUser());
        } catch (AuthenticationException e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @Transactional
    public String sendPasswordResetEmail(PrincipalDetails principalDetails) {

        if (principalDetails == null || principalDetails.getUser() == null) {
            throw new LoginHandler(ErrorStatus.USER_NOT_FOUND);
        }
        User user = principalDetails.getUser();

/*
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일로 가입된 계정이 없습니다."));
*/

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
        userVerificationEmailService.sendPasswordResetEmail(user.getEmail(), token);

        return "비밀번호 재설정 링크를 담은 이메일이 성공적으로 발송되었습니다.";
    }

    @Transactional
    public PasswordResetToken validatePasswordResetToken(String passwordResetCode) {
        PasswordResetToken resetToken = tokenRepository.findByToken(passwordResetCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("만료된 토큰입니다. 비밀번호 재설정을 다시 요청해주세요.");
        }

        return resetToken;
    }

    @Transactional
    public String resetPassword(
            String passwordResetCode,
            UserRequestDTO.ResetPasswordDTO request
    ) {
        PasswordResetToken resetToken = validatePasswordResetToken(passwordResetCode);

        User user = resetToken.getUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPw())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPw())) {
            throw new RuntimeException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        // 사용된 토큰 삭제
        tokenRepository.delete(resetToken);
        log.info("비밀번호 재설정 완료: {}", user.getEmail());

        return "비밀번호 재설정이 완료되었습니다.";
    }

    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

            log.info("로그아웃 전 Authorization 헤더: {}", request.getHeader("Authorization"));
            log.info("로그아웃 전 Refresh-Token 헤더: {}", request.getHeader("Refresh-Token"));

            jwtLogoutHandler.logout(request, response, authentication);

            log.info("로그아웃 후 Authorization 헤더: {}", response.getHeader("Authorization"));
            log.info("로그아웃 후 Refresh-Token 헤더: {}", response.getHeader("Refresh-Token"));
            log.info("로그아웃 완료: {}", authentication.getName());

            return "로그아웃 성공";
        } else {
            log.info("이미 로그아웃된 상태입니다");
            return "이미 로그아웃된 상태입니다";
        }
    }
}
