package learningFlow.learningFlow_BE.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.service.user.AuthService;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "일반 회원가입 API", description = "이메일과 비밀번호를 통한 일반 회원가입을 처리하는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> register(
            @Valid @RequestBody UserRequestDTO.UserRegisterDTO request
    ) {
        return ApiResponse.onSuccess(authService.register(request));
        //TODO: 회원가입 후 로그인 창으로 리다이렉트 하는게 나을것 같은데 이 부분은 아직 설정 안함...
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "이메일과 비밀번호를 통한 일반 로그인을 처리하는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> login(
            @Valid @RequestBody UserRequestDTO.UserLoginDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        return ApiResponse.onSuccess(authService.login(request, httpRequest, httpResponse));
        //TODO: 로그인 후에도 /home으로 리다이렉트 되는게 나을 것 같은데 이 부분도 설정 안함
    }


    /**
     * localhost:8080/oauth2/authorization/google로 리다이렉트해서 로그인 수행
     * 이미 존재하는 회원인 경우 로그인, 존재하지 않는 회원인 경우 getAdditionalInfoPage() 호출하면서 회원가입 진행
     */
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 리다이렉트", description = "구글 로그인 페이지로 리다이렉트하는 API")
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
}
