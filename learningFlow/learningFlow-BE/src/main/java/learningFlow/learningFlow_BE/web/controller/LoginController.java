package learningFlow.learningFlow_BE.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.service.user.AuthService;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "Login", description = "회원가입, 로그인 관련 API")
public class LoginController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "회원가입 초기 단계 API", description = "이메일과 비밀번호를 입력받아 인증 이메일을 발송하는 API")
    public ApiResponse<String> register(
            @Valid @RequestBody UserRequestDTO.InitialRegisterDTO request
    ) {
        authService.initialRegister(request);
        return ApiResponse.onSuccess("인증 이메일이 발송되었습니다. 이메일을 확인해주세요.");
    }

    @PostMapping("/register/complete")
    @Operation(summary = "회원가입 완료 API", description = "이메일 인증 후 추가 정보를 입력받아 회원가입을 완료하는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> completeRegister(
            @Valid @RequestBody UserRequestDTO.CompleteRegisterDTO request
    ) {
        return ApiResponse.onSuccess(authService.completeRegister(request));
        //TODO: 회원가입 후 로그인 창으로 리다이렉트 하는게 나을것 같은데 이 부분은 아직 설정 안함(리다이렉트 설정 시 스웨거 테스트 불편)
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "이메일과 비밀번호를 통한 일반 로그인을 처리하는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> login(
            @Valid @RequestBody UserRequestDTO.UserLoginDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        return ApiResponse.onSuccess(authService.login(request, httpRequest, httpResponse));
        //TODO: 로그인 후에도 /home으로 리다이렉트 되는게 나을 것 같은데 이 부분 설정 안함(리다이렉트 설정 시 스웨거 테스트 불편)
    }

    /**
     * localhost:8080/oauth2/authorization/google로 리다이렉트해서 로그인 수행
     * 이미 존재하는 회원인 경우 로그인, 존재하지 않는 회원인 경우 getAdditionalInfoPage() 호출하면서 회원가입 진행
     */
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 리다이렉트", description = "구글 로그인 페이지로 리다이렉트하는 API")
    public void googleLogin(
            @RequestParam(defaultValue = "false") boolean rememberMe,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("OAUTH2_REMEMBER_ME", rememberMe);

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
    @Operation(summary = "추가 정보 입력 페이지", description = "OAuth2 회원가입 후 추가 정보 입력이 필요한 경우 리다이렉트되는 엔드포인트")
    public ApiResponse<?> getAdditionalInfoPage() {

        return ApiResponse.onSuccess(userService.getAdditionalInfoRequirements());
    }

    /**
     * updateAdditionalInfo()에서 프론트에서 전달한 추가 데이터와 세션에 저장된 oauth2UserTemp에
     * 추가 데이터를 저장해서 회원가입 완료.
     * 그리고 회원가입 완료되면 세션에 저장된 oauth2UserTemp는 삭제하고 인증 정보를 SecurityContextHolder에 추가해서 인증이 가능하게 한다.
     * @return UserResponseDTO.UserLoginResponseDTO
     */
    @PutMapping("/oauth2/additional-info")
    @Operation(summary = "추가 정보 입력 API", description = "OAuth2 회원가입 후 추가 정보를 입력하는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> updateAdditionalInfo(
            @Valid @RequestBody UserRequestDTO.AdditionalInfoDTO request,
            HttpServletRequest httpRequest) {  // HttpServletRequest 추가

        return ApiResponse.onSuccess(userService.updateAdditionalInfo(httpRequest, request));
    }

    @PostMapping("/find/password")
    @Operation(summary = "비밀번호 재설정 요청 API", description = "이메일을 통해 비밀번호 재설정 링크를 전송하는 API")
    public ApiResponse<String> requestPasswordReset(
            @Valid @RequestBody UserRequestDTO.FindPasswordDTO request
    ) {
        authService.sendPasswordResetEmail(request);
        return ApiResponse.onSuccess("이메일이 성공적으로 발송되었습니다.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정 API", description = "이메일로 받은 토큰을 통해 비밀번호를 재설정하는 API")
    public ApiResponse<String> resetPassword(
            @Valid @RequestBody UserRequestDTO.ResetPasswordDTO request
    ) {
        authService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호 재설정이 완료되었습니다.");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃 실행하는 API, 실행 후 홈 화면으로 리다이렉트")
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/home";
    }

    @PostMapping("/logout/test") // 테스트 전용 API
    @Operation(summary = "로그아웃 테스트 용 API", description = "로그 아웃 테스트 후 리다이렉트 수행 안하고 스웨거에서 확인 가능하게 문자열 출력해주는 API")
    public ResponseEntity<String> testLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
}
