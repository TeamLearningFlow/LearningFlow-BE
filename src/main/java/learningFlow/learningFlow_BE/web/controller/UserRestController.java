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
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    private final AmazonS3Manager amazonS3Manager;

    @PostMapping(value = "/imgUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드 API", description = "회원가입 절차에서 이미지를 업로드하는 API")
    public ApiResponse<String> imageUpload(@RequestPart MultipartFile image) {
        String imgUrl = amazonS3Manager.uploadImageToS3(image);
        return ApiResponse.onSuccess(imgUrl); // ✅ 프론트에서 이 URL을 저장하고 사용
    }

    @PutMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 정보 수정 API", description = "사용자 정보를 수정하는 API\n" +
            "이때 같은 페이지에서 업로드한 이미지 url string을 DTO에 추가하여 회원가입을 진행함")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<UserInfoDTO> updateUserInfo(
            //@Valid @RequestBody UpdateUserDTO updateUserDTO,
            @RequestPart("updateUserDTO") @Valid UpdateUserDTO updateUserDTO, // ✅ JSON 데이터 - application/json
//            @RequestPart(required = false) MultipartFile imageFile, // ✅ 이미지 파일 업로드 - image/jpeg
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.updateUserInfo(principalDetails.getUser().getLoginId(), updateUserDTO)
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

    @GetMapping("/mypage")
    @Operation(summary = "마이 페이지 조회 API", description = "마이 페이지에서 최근 학습 목록, 완료 컬렉션 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<UserResponseDTO.UserMyPageResponseDTO> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(userService.getUserMyPageResponseDTO(principalDetails.getUser().getLoginId()));
    }

    @GetMapping("/likes")
    @Operation(summary = "좋아요한 컬렉션 조회 API", description = "사용자가 좋아요한 컬렉션 목록을 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "lastId", description = "마지막으로 조회된 컬렉션의 ID (첫 페이지는 0)"),
    })
    public ApiResponse<CollectionResponseDTO.SearchResultDTO> getBookmarkedCollections(
            @RequestParam(required = false, defaultValue = "0") Long lastId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.getBookmarkedCollections(principalDetails.getUser().getLoginId(), lastId)
        );
    }
}