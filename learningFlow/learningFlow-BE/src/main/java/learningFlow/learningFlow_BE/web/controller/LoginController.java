package learningFlow.learningFlow_BE.web.controller;

import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.web.dto.login.LoginRequestDto;
import learningFlow.learningFlow_BE.web.dto.login.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "Login", description = "회원가입, 로그인 관련 API")
public class LoginController {

    @PostMapping("/register")
    @Operation(summary = "일반 회원가입 API", description = "이메일과 비밀번호를 통한 일반 회원가입을 처리하는 API")
    public ApiResponse<LoginResponseDto.LoginResultDto> register(@RequestBody LoginRequestDto.RegisterDto request) {
        //TODO: 회원가입 구현
        // 일반 회원가입 처리
        /**
         * 회원가입의 경우 회원가입을 한 후에 저렇게 Dto를 보여주는게 아닌 home 화면으로 리다이렉트가 맞는것 같은데...
         * 의논 필요
         */
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "이메일과 비밀번호를 통한 일반 로그인을 처리하는 API")
    public ApiResponse<LoginResponseDto.LoginResultDto> login(@RequestBody LoginRequestDto.LoginDto request) {
        //TODO : 로그인 구현..
        /**
         * 그런데 여기서 로그인 후에 저렇게 Dto를 보여주는게 맞는지 의문..
         * home 화면으로 리다이렉트 하는게 맞는거 같은데..
         */
        // 일반 로그인 처리
        return ApiResponse.onSuccess(null);
    }

    /**
     * OAuth2 로그인은 Spring Security에서 처리되므로 별도의 컨트롤러 메서드가 필요 없다고 하는데... 아직 잘 모르겠어요 ㅜㅜ
     * oauth2/authorization/google로 리다이렉트되어 자동으로 처리된다고는 하는데.. 잘 모르는 영역..
     */
}
