package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.auth.local.LocalUserAuthService;
import learningFlow.learningFlow_BE.service.auth.oauth.OAuth2UserRegistrationService;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"}) // ✅ CORS 허용
@Tag(name = "Login", description = "회원가입/로그인/인증 관련 API")
@Slf4j
public class LoginController {

    private final LocalUserAuthService localUserAuthService;
    private final OAuth2UserRegistrationService OAuth2UserRegistrationService;

    @PostMapping("/register")
    @Operation(summary = "회원가입 초기 단계 API", description = """
           이메일과 비밀번호로 회원가입을 시작합니다.
           
           [필수 입력]
           - 이메일: 유효한 이메일 형식 (중복 불가)
           - 비밀번호: 8-16자, 영대소문자/숫자/특수문자(@$!%*?&) 각 1개 이상
           
           [처리 과정]
           1. 입력값 검증
           2. 이메일 중복 확인
           3. 인증 이메일 발송
           """)
    public ApiResponse<String> register(
            @Valid @RequestBody UserRequestDTO.InitialRegisterDTO request
    ) {
        localUserAuthService.initialRegister(request);
        return ApiResponse.onSuccess("인증 이메일이 발송되었습니다. 이메일을 확인해주세요.");
    }

    @GetMapping("/register/complete")
    @Operation(summary = "회원가입 완료 API", description = """
           이메일 인증 토큰을 검증합니다.
           
           [처리 과정]
           1. 토큰 유효성 검증
           2. 만료 여부 확인 (30분)
           3. 추가 정보 입력 페이지로 이동
           """)
    public ApiResponse<String> goCompleteRegister(
            @RequestParam String emailVerificationCode
    ) {
        localUserAuthService.validateRegistrationToken(emailVerificationCode);
        return ApiResponse.onSuccess("토큰이 유효. 추가 정보를 입력해주세요.");
    }

    @PostMapping(value = "/register/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원가입 완료 API", description = """
           회원가입에 필요한 추가 정보를 입력받습니다.
           
           [필수 입력]
           - 이름: 실명 또는 닉네임
           - 직업: STUDENT, ADULT, EMPLOYEE, JOB_SEEKER, OTHER
           - 관심분야: 다중선택 (APP_DEVELOPMENT, WEB_DEVELOPMENT, PROGRAMMING_LANGUAGE, DEEP_LEARNING, STATISTICS, DATA_ANALYSIS, UI_UX, PLANNING, BUSINESS_PRODUCTIVITY, FOREIGN_LANGUAGE, CAREER)
           - 선호 미디어: VIDEO, TEXT
           - 프로필 이미지 URL (이미지 업로드 API로 받은 URL)
           """)
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> completeRegister(
            @RequestParam String emailVerificationCode,
            @Valid @RequestBody UserRequestDTO.CompleteRegisterDTO request, // ✅ JSON 데이터 - application/json
            HttpServletResponse response
    ) {
        return ApiResponse.onSuccess(localUserAuthService.completeRegister(emailVerificationCode, request, response));
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = """
           이메일/비밀번호 로그인을 처리합니다.
           
           [필수 입력]
           - 이메일
           - 비밀번호
           - 자동로그인 여부(remember)
           
           [응답]
           - JWT 토큰 (쿠키)
           - 사용자 기본 정보
           """)
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> login(
            @Valid @RequestBody UserRequestDTO.UserLoginDTO request,
            HttpServletResponse response
    ) {
        log.info("/login 시작");
        return ApiResponse.onSuccess(localUserAuthService.login(request, response));
    }

    /**
     * localhost:8080/oauth2/authorization/google로 리다이렉트해서 로그인 수행
     * 이미 존재하는 회원인 경우 로그인, 존재하지 않는 회원인 경우 getAdditionalInfoPage() 호출하면서 회원가입 진행
     */
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 리다이렉트", description = "구글 로그인 페이지로 리다이렉트하는 API\n"+"리다이렉트해야하므로 swagger에서는 테스트 불가!")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    /**
     * 만약에 기존에 존재하던 회원이 아닌 경우에는
     * 우선 구글에서 받아올 수 있는 정보들만으로 임시회원 oauth2UserTemp를 생성해서 세션에 저장하고
     * getAdditionalInfoPage() 호출해서 프론트가
     * 회원가입에 필요하지만 구글에서 제공하지 않는 추가 필드 입력하는 페이지로 리다이렉트 한 다음에 해당 정보 전달해주면,
     * updateAdditionalInfo()에서 그 데이터 받아서 회원가입 완료
     */
    @GetMapping("/oauth2/additional-info")
    @Operation(summary = "추가 정보 입력 페이지", description = "OAuth2 회원가입 시 추가 정보 입력이 필요한 경우 리다이렉트되는 엔드포인트")
    public ApiResponse<?> getAdditionalInfoPage() {
        log.info("get info");
        return ApiResponse.onSuccess(OAuth2UserRegistrationService.getAdditionalInfoRequirements());
    }

    /**
     * updateAdditionalInfo()에서 프론트에서 전달한 추가 데이터와 세션에 저장된 oauth2UserTemp에
     * 추가 데이터를 저장해서 회원가입 완료.
     * 이때, 같은 페이지에서 업로드한 이미지 url string을 DTO에 추가하여 회원가입을 진행함.
     * 그리고 회원가입 완료되면 세션에 저장된 oauth2UserTemp는 삭제하고 인증 정보를 SecurityContextHolder에 추가해서 인증이 가능하게 한다.
     * @return UserResponseDTO.UserLoginResponseDTO
     */
    @PutMapping(value = "/oauth2/additional-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "추가 정보 입력 API", description = """
           OAuth2 회원가입의 추가 정보를 입력받습니다.
           
           [필수 입력]
           - 이름: 실명 또는 닉네임
           - 직업: STUDENT, ADULT, EMPLOYEE, JOB_SEEKER, OTHER
           - 관심분야: 다중선택 (APP_DEVELOPMENT, WEB_DEVELOPMENT, PROGRAMMING_LANGUAGE, DEEP_LEARNING, STATISTICS, DATA_ANALYSIS, UI_UX, PLANNING, BUSINESS_PRODUCTIVITY, FOREIGN_LANGUAGE, CAREER)
           - 선호 미디어: VIDEO, TEXT
           
           [선택 입력]
           - 프로필 이미지 URL
           
           [주의사항]
           - 이메일은 구글 계정 정보 사용
           - 이미지 미입력시 기본 이미지 사용
           """)
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> updateAdditionalInfo(
            @RequestParam String oauth2RegistrationCode,
            @RequestBody @Valid UserRequestDTO.AdditionalInfoDTO request, // ✅ JSON 데이터 - application/json
            HttpServletResponse response) {
        log.info("put info");
        return ApiResponse.onSuccess(OAuth2UserRegistrationService.updateAdditionalInfo(oauth2RegistrationCode, request, response));
    }
    //TODO: 해당 DTO에 안 맞으면 500에러 나는데, 400에러이고 왜 회원가입 안되는 건지 구체적인 에러 작성 필요.

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = """
           로그아웃을 처리합니다.
           
           [처리 내용]
           - JWT 토큰 무효화
           - 자동로그인 쿠키 삭제
           - 세션 정보 삭제
           """)
    public ApiResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그아웃 시작");
        return ApiResponse.onSuccess(localUserAuthService.logout(request, response));
    }


/*
   //일단은 사용X
   @PostMapping("/find/password")
   @Operation(summary = "비밀번호 재설정 요청 API", description = "비밀번호를 잃어버렸을 경우, 이메일을 통해 비밀번호 재설정 링크를 전송하는 API")
   public ApiResponse<String> requestPasswordReset(
           @Valid @RequestBody UserRequestDTO.FindPasswordDTO request
   ) {
       localUserAuthService.sendPasswordResetEmail(request);
       return ApiResponse.onSuccess("이메일이 성공적으로 발송되었습니다.");
   }
*/

/*
   //일단은 사용X
   @PostMapping("/reset-password")
   @Operation(summary = "비밀번호 재설정 API", description = "이메일로 받은 토큰을 통해 비밀번호를 재설정하는 API")
   public ApiResponse<String> resetPassword(
           @Valid @RequestBody UserRequestDTO.ResetPasswordDTO request
   ) {
       localUserAuthService.resetPassword(request);
       return ApiResponse.onSuccess("비밀번호 재설정이 완료되었습니다.");
   }
*/
}
