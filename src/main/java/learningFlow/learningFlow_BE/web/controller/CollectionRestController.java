package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.LoginHandler;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.collection.CollectionService;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.bookmark.BookmarkDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
@Validated
@Slf4j
@Tag(name = "Collection", description = "컬렉션 조회 및 좋아요 관련 API")
public class CollectionRestController {

    private final UserService userService;
    private final CollectionService collectionService;

    @GetMapping("/{collectionId}")
    @Operation(summary = "컬렉션 상세 조회 API", description = """
            특정 컬렉션의 상세 정보와 리소스 목록을 조회합니다.
            
            [조회 권한별 리소스 노출 범위]
            1. 비로그인/수강 전
               - 처음 4개 리소스만 조회 가능
               - 컬렉션 기본 정보 확인 가능
               
            2. 수강 중
               - 현재 회차부터 4개 리소스 조회 가능
               - 진행률, 시작일 확인
               
            3. 수강 완료
               - 전체 리소스 조회 가능
               - 완료일, 총 학습시간 확인 가능
            
            [응답 정보]
            - 기본 정보: 제목, 생성자, 키워드, 난이도 등
            - 학습 정보: 진행 상태, 진행률, 시작/완료일
            - 리소스 정보: 학습 자료 목록 (권한별 차등)
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CollectionPreviewResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 컬렉션입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "collectionId", description = "컬렉션 ID", example = "1")
    })
    public ApiResponse<CollectionResponseDTO.CollectionPreviewDTO> getCollection(
            @PathVariable("collectionId") Long collectionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                collectionService.CollectionDetails(collectionId, principalDetails)
        );
    }

    @PostMapping("{collectionId}/likes")
    @Operation(summary = "좋아요 설정/해제 API", description = """
            컬렉션에 대한 좋아요를 설정하거나 해제하는 API입니다.
            - 이미 좋아요한 경우: 좋아요 해제
            - 좋아요하지 않은 경우: 좋아요 설정
            
            [필수 조건]
            - 로그인한 사용자만 가능
            - 유효한 컬렉션 ID
            
            [응답 정보]
            isBookmarked (boolean)
            - true: 좋아요 설정됨
            - false: 좋아요 해제됨
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = BookmarkResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요한 서비스입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "컬렉션을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "collectionId", description = "컬렉션 ID", example = "1")
    })
    public ApiResponse<BookmarkDTO.BookmarkResponseDTO> toggleBookmark(
            @PathVariable("collectionId") Long collectionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            throw new LoginHandler(ErrorStatus.LOGIN_REQUIRED);
        }
        return ApiResponse.onSuccess(
                userService.toggleBookmark(principalDetails.getUser().getLoginId(), collectionId)
        );
    }

    @Schema(name = "CollectionPreviewResponse")
    private class CollectionPreviewResponse {
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

        @Schema(description = "난이도 (1:입문, 2:초급, 3:중급, 4:고급)", example = "[1, 2]")
        List<Integer> difficulties;

        @Schema(description = "학습 진행률(%)", example = "65")
        Integer progressRatePercentage;

        @Schema(description = "학습 상태", example = "IN_PROGRESS", allowableValues = {"BEFORE", "IN_PROGRESS", "COMPLETED"})
        String learningStatus;
    }

    @Schema(name = "BookmarkResponse")
    private class BookmarkResponse {
        @Schema(description = "좋아요 상태", example = "true")
        boolean isBookmarked;
    }
}