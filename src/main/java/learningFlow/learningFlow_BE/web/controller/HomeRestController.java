package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.collection.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
@Tag(name = "Home", description = "홈 화면 보여주는 API")
public class HomeRestController {

    private final CollectionService collectionService;

    @GetMapping
    @Operation(summary = "홈 화면 API", description = """
            사용자의 인증 상태에 따라 맞춤형 홈 화면을 제공합니다.
            ---
            [인증 헤더]\n
            [비로그인 사용자]
            - 인증 헤더 없음
            
            [로그인 사용자]
            - Authorization: Bearer {access_token} (필수)
            - Refresh-Token: {refresh_token} (필수)
            
            ---
            [응답 정보]\n
            [비로그인 사용자 응답]
            - Authorization 헤더 없음
            1. 추천 컬렉션 (6개)
                - 북마크 수 기준 상위 6개 컬렉션 제공
                - 각 컬렉션의 첫 4개 에피소드 프리뷰 제공
            
            [로그인 사용자 응답]
            - 유효한 Authorization 토큰 필요
            1. 최근 학습 컬렉션
               - 가장 최근 'IN_PROGRESS' 상태의 컬렉션
               - 학습 시작일, 현재 진행률, 현재 에피소드 정보 포함
               - 현재 에피소드부터 최대 4개의 에피소드 정보 제공
               
            2. 추천 컬렉션 (6개)
               추천 우선순위:
               1) 관심분야 + 선호타입 모두 일치
               2) 관심분야만 일치
               3) 선호타입만 일치
               4) 북마크 수 기준
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER4001",
                    description = "사용자를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<?> getHome(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null || principalDetails.getUser() == null) {
            log.info("비회원");
            return ApiResponse.onSuccess(collectionService.getGuestHomeCollections());
        } else {
            log.info("회원");
            return ApiResponse.onSuccess(collectionService.getUserHomeCollections(principalDetails.getUser()));
        }
    }
}