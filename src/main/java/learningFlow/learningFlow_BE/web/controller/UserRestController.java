package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.LoginHandler;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.bookmark.BookmarkDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@Slf4j
@Tag(name = "User", description = "사용자 관련 API")
public class UserRestController {

    private final UserService userService;

    @PutMapping
    @Operation(summary = "사용자 정보 수정 API", description = "사용자 정보를 수정하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<UserInfoDTO> updateUserInfo(
            @Valid @RequestBody UpdateUserDTO updateUserDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.updateUserInfo(principalDetails.getUser().getLoginId(), updateUserDTO, imageFile)
        );
    }

    @GetMapping
    @Operation(summary = "사용자 정보 조회 API", description = "로그인한 사용자의 정보를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<UserInfoDTO> getUserInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.getUserInfo(principalDetails.getUser().getLoginId())
        );
    }

    @PostMapping("/bookmark")
    @Operation(summary = "북마크 토글 API", description = "컬렉션의 북마크를 설정/해제하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COLLECTION4001", description = "컬렉션을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<BookmarkDTO.BookmarkResponseDTO> toggleBookmark(
            @RequestBody @Valid BookmarkDTO.BookmarkRequestDTO request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            throw new LoginHandler(ErrorStatus.LOGIN_REQUIRED);
        }
        return ApiResponse.onSuccess(userService.toggleBookmark(principalDetails.getUser().getLoginId(), request.getCollectionId()
        ));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "북마크한 컬렉션 조회 API", description = "사용자가 북마크한 컬렉션 목록을 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "lastId", description = "마지막으로 조회된 컬렉션의 ID (첫 페이지는 0)"),
    })
    public ApiResponse<SearchResponseDTO.SearchResultDTO> getBookmarkedCollections(
            @RequestParam(required = false, defaultValue = "0") Long lastId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            throw new LoginHandler(ErrorStatus.LOGIN_REQUIRED);
        }
        return ApiResponse.onSuccess(userService.getBookmarkedCollections(principalDetails.getUser().getLoginId(), lastId));
    }
}