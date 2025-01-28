package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
@Validated
@Slf4j
@Tag(name = "Collection", description = "컬렉션 관련 API")
public class CollectionRestController {

    private final UserService userService;
    private final CollectionService collectionService;

    @GetMapping("/{collectionId}")
    @Operation(summary = "컬렉션 조회 API", description = "특정 컬렉션을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COLLECTION4001", description = "존재하지 않는 컬렉션입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({

            @Parameter(name = "collectionId", description = "컬렉션 ID"),
    })
    public ApiResponse<CollectionResponseDTO.CollectionPreviewDTO> getCollection(
            @PathVariable("collectionId") Long collectionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                collectionService.CollectionDetails(collectionId, principalDetails)
        );
    }

    @PostMapping("{collectionId}/bookmark")
    @Operation(summary = "북마크 설정,해제 API", description = "컬렉션의 북마크를 설정,해제하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COLLECTION4001", description = "컬렉션을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "collectionId", description = "컬렉션 ID"),
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
}
