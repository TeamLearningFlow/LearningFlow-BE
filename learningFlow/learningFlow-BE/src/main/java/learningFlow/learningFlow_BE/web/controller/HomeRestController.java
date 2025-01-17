package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.HomeInfoDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static learningFlow.learningFlow_BE.converter.UserConverter.toUserLoginResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Validated
@Slf4j
@Tag(name = "Home", description = "홈 화면 보여주는 API")
public class HomeRestController {


    @GetMapping
    @Operation(summary = "홈 화면 API", description = "홈 화면에 필요한 정보를 제공하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<HomeInfoDTO> getHome(
//            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // TODO : 추후 구현될 로직을 위한 주석

        /**
         * 1. 현재 로그인한 사용자 정보 조회
         * 2. 사용자 기반 추천 컬렉션 목록 조회
         * 3. 사용자의 최근 수강 컬렉션 목록 조회
         * 4. HomeInfoDto로 변환하여 반환
         */

        /**
         * Spring Security 사용하기 때문에 @AuthenticationPrincipal를 통해 바로 현재 인증된 사용자의 정보를 가져올 수 있음
         */

//        User user = principalDetails.getUser();

        return ApiResponse.onSuccess(null);
    }


    @GetMapping("/test")
    @Operation(summary = "홈 화면 테스트용 API", description = "로그인, 로그아웃 상태 유지되는지 확인할 수 있는 API")
    public ApiResponse<UserResponseDTO.UserLoginResponseDTO> getHomeTest(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        log.info("/home/test 시작");

        if (principalDetails != null) {
            log.info("인증된 사용자: {}", principalDetails.getUsername());
            return ApiResponse.onSuccess(toUserLoginResponseDTO(principalDetails.getUser()));
        }

        log.info("인증되지 않은 사용자");
        return ApiResponse.onSuccess(null);
    }
}