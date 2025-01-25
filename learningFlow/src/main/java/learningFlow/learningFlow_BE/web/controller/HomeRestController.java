package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.home.HomeService;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.HomeInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Slf4j
@Tag(name = "Home", description = "홈 화면 보여주는 API")
public class HomeRestController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "홈 화면 API", description = "홈 화면에 필요한 정보를 제공하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<HomeInfoDTO> getHome(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        log.info("홈 화면 API 호출");
        User user = principalDetails != null ? principalDetails.getUser() : null;
        log.info("사용자 상태: {}", user != null ? "로그인" : "비로그인");
        HomeInfoDTO homeInfo = homeService.getHomeInfo(user);
        return ApiResponse.onSuccess(homeInfo);
    }
}