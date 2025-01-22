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
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.UserHandler;
import learningFlow.learningFlow_BE.converter.MemoConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.UserEpisodeProgress;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.memo.MemoCommandService;
import learningFlow.learningFlow_BE.service.resource.ResourceService;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import learningFlow.learningFlow_BE.web.dto.memo.MemoResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/resources")
@Slf4j
@Tag(name = "Resource", description = "Collection 내에 특정 resource 관련해서 기능하는 API")
public class ResourceRestController {
    private final MemoCommandService memoCommandService;
    private final ResourceService resourceService;
    @GetMapping("/{episode-id}")
    @Operation(summary = "강의 시청, 강좌로 이동 API", description = "강의 에피소드를 시청하기 위해 강좌로 이동하는 API, 그리고 강의를 시청 처리하는 로직도 포함")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episode-id", description = "시청할 강의 에피소드 ID")
    })
    public ApiResponse<ResourceResponseDTO.ResourceUrlDTO> watchEpisode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episode-id") Long episodeId) {
        /**
         * 강의 시청 하는 API로 강좌로 이동하는 API 이기 때문에 일단 Resource의 Url을 반환하게 해놓았어요.
         */
        String loginId = principalDetails.getUser().getLoginId();
        UserEpisodeProgress userEpisodeProgress = resourceService.getUserEpisodeProgress(episodeId, loginId);
        Collection collection = resourceService.getCollection(episodeId);
        // TODO: 강의 시청 로직 구현, 반환 DTO로 converting
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/{episode-id}/memo")
    @Operation(summary = "강의 메모 생성 API", description = "강의 에피소드에 메모를 추가하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episode-id", description = "메모를 추가할 강의 에피소드 ID")
    })
    public ApiResponse<MemoResponseDTO.MemoInfoDTO> createMemo(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episode-id") Long episodeId,
            @Valid @RequestBody MemoRequestDTO.MemoJoinDTO request) {
        String loginId = principalDetails.getUser().getLoginId();
        log.info("로그인 상태 확인 {}", loginId);
        memoCommandService.saveMemo(loginId, episodeId, request);
        return ApiResponse.onSuccess(MemoConverter.createMemo(request)); // 성공 시 200 OK 반환
    }
}