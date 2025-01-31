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
import learningFlow.learningFlow_BE.converter.MemoConverter;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.UserEpisodeProgress;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.embed.BlogEmbedService;
import learningFlow.learningFlow_BE.service.embed.YoutubeUrlEmbedService;
import learningFlow.learningFlow_BE.service.memo.MemoCommandService;
import learningFlow.learningFlow_BE.service.resource.ResourceService;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import learningFlow.learningFlow_BE.web.dto.memo.MemoResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/resources")
@Slf4j
@Tag(name = "Resource", description = "Collection 내에 특정 resource 관련해서 기능하는 API")
public class ResourceRestController {
    private final MemoCommandService memoCommandService;
    private final ResourceService resourceService;
    private final YoutubeUrlEmbedService youtubeUrlEmbedService;
    private final BlogEmbedService blogEmbedService;

    @GetMapping("/{episode-id}/youtube")
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

        String loginId = principalDetails.getUser().getLoginId();
        UserEpisodeProgress userEpisodeProgress = resourceService.getUserEpisodeProgress(episodeId, loginId);
        Collection collection = resourceService.getCollection(episodeId);
        Optional<Memo> memo = resourceService.getMemoContents(episodeId);
        Resource resource = youtubeUrlEmbedService.getResource(episodeId);

        return ApiResponse.onSuccess(ResourceConverter.watchEpisode(collection, userEpisodeProgress, resource, memo));
    }

    @GetMapping("/{episode-id}/blog")
    @Operation(summary = "강의 시청, 강좌로 이동 API", description = "강의 에피소드를 시청하기 위해 강좌로 이동하는 API, 그리고 강의를 시청 처리하는 로직도 포함")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episode-id", description = "시청할 강의 에피소드 ID")
    })
    public ApiResponse<ResourceResponseDTO.ResourceBlogUrlDTO> watchBlogEpisode (
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episode-id") Long episodeId) {

        String loginId = principalDetails.getUser().getLoginId();
        UserEpisodeProgress userEpisodeProgress = resourceService.getUserEpisodeProgress(episodeId, loginId);
        Collection collection = resourceService.getCollection(episodeId);
        Optional<Memo> memo = resourceService.getMemoContents(episodeId);
        String resourceTitle = resourceService.getResourceTitle(episodeId);
        String blogSourceUrl = "/resources/" + episodeId + "/blog/content";
        return ApiResponse.onSuccess(ResourceConverter.watchBlogEpisode(collection, userEpisodeProgress, blogSourceUrl, resourceTitle, memo));
    }

    // Gzip으로 HTML을 반환하는 API
    @GetMapping("{episode-id}/blog/content")
    @Operation(summary = "blog HTML 반환 API", description = "/resources/{episode-id}/blog 호출 이후 호출하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @Parameters({
            @Parameter(name = "episode-id", description = "시청할 강의 에피소드 ID")
    })
    public ResponseEntity<byte[]> getBlogEpisodeContent(@PathVariable("episode-id") Long episodeId) {
        CompletableFuture<byte[]> blogSource = blogEmbedService.getBlogSource(episodeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // 바이너리 파일 반환
        headers.add(HttpHeaders.CONTENT_ENCODING, "gzip");  // 올바르게 설정

        try {
            byte[] blogContent = blogSource.get();  // 예외 처리 추가
            headers.setContentLength(blogContent.length);
            return new ResponseEntity<>(blogContent, headers, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            log.error("블로그 데이터를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]); // 빈 응답 반환
        }
    }

    @PostMapping("/{episode-id}/save-progress")
    @Operation(summary = "강의 진도 저장 API", description = "강의 진도 저장 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episode-id", description = "시청할 강의 에피소드 ID")
    })
    public ApiResponse<ResourceResponseDTO.ProgressResponseDTO> saveProgress(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episode-id") Long episodeId,
            @Valid @RequestBody ResourceRequestDTO.ProgressRequestDTO request) {
        String loginId = principalDetails.getUser().getLoginId();
        resourceService.saveProgress(request, loginId, episodeId);

        return ApiResponse.onSuccess(ResourceConverter.toSaveProgressResponse(request));
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