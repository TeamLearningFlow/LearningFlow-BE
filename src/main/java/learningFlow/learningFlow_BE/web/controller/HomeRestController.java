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
@Tag(name = "Home", description = "홈 화면 API")
public class HomeRestController {

    private final CollectionService collectionService;

    @GetMapping
    @Operation(summary = "홈 화면 조회", description = """
           사용자 상태(로그인/비로그인)에 따른 홈 화면 정보를 제공합니다.
           - 비로그인: 추천 컬렉션 목록
           - 로그인: 최근 학습 컬렉션 + 맞춤 추천 컬렉션 목록
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(schema = @Schema(implementation = HomeResponse.class))
            }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ApiResponse<?> getHome(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null || principalDetails.getUser() == null) {
            return ApiResponse.onSuccess(collectionService.getGuestHomeCollections());
        }
        return ApiResponse.onSuccess(collectionService.getUserHomeCollections(principalDetails.getUser()));
    }

    @Schema(name = "HomeResponse")
    private class HomeResponse {
        @Schema(name = "GuestHomeResponse")
        private class GuestHomeResponse {
            @Schema(description = "추천 컬렉션 목록")
            List<CollectionPreview> recommendedCollections;
        }

        @Schema(name = "UserHomeResponse")
        private class UserHomeResponse {
            @Schema(description = "최근 학습 컬렉션")
            CollectionPreview recentLearning;

            @Schema(description = "추천 컬렉션 목록")
            List<CollectionPreview> recommendedCollections;
        }

        @Schema(name = "CollectionPreview")
        private class CollectionPreview {
            @Schema(description = "컬렉션 ID", example = "1")
            Long collectionId;

            @Schema(description = "관심 분야", example = "DEVELOPMENT")
            InterestField interestField;

            @Schema(description = "제목", example = "스프링 부트 입문하기")
            String title;

            @Schema(description = "생성자", example = "김강사")
            String creator;

            @Schema(description = "키워드 목록", example = "[\"Spring\", \"Backend\"]")
            List<String> keywords;

            @Schema(description = "난이도 목록 (1:입문, 2:초급, 3:중급, 4:고급)", example = "[1, 2]")
            List<Integer> difficulties;

            @Schema(description = "텍스트 리소스 수", example = "5")
            Integer textCount;

            @Schema(description = "비디오 리소스 수", example = "3")
            Integer videoCount;

            @Schema(description = "좋아요 수", example = "128")
            Integer likesCount;

            @Schema(description = "학습 상태", example = "IN_PROGRESS")
            String learningStatus;

            @Schema(description = "학습 진행률", example = "65")
            Integer progressRatePercentage;
        }
    }
}