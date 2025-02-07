package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            홈 화면 정보를 제공하는 API입니다.
            
            [비로그인 사용자]
            - 인기 컬렉션 6개 제공
            - 각 컬렉션당 처음 4개 리소스 미리보기
            
            [로그인 사용자]
            1. 최근 학습 컬렉션
               - 가장 최근 학습 중인 컬렉션
               - 현재 진행률, 최근 리소스 정보
               
            2. 맞춤 추천 컬렉션 6개
               - 관심분야/선호타입 기반 추천
               - 추천 우선순위:
                 a) 관심분야 + 선호타입 일치
                 b) 관심분야만 일치
                 c) 선호타입만 일치
                 d) 인기순
            """)
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