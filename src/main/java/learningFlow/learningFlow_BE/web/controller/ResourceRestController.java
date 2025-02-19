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
import learningFlow.learningFlow_BE.service.embed.YoutubeUrlEmbedService;
import learningFlow.learningFlow_BE.service.lambda.LambdaService;
import learningFlow.learningFlow_BE.service.memo.MemoCommandService;
import learningFlow.learningFlow_BE.service.resource.ResourceService;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import learningFlow.learningFlow_BE.web.dto.memo.MemoResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/resources/{episodeId}")
@Slf4j
@Tag(name = "Resource", description = "Collection 내에 특정 resource 관련해서 기능하는 API")
public class ResourceRestController {

    private final MemoCommandService memoCommandService;
    private final ResourceService resourceService;
    private final YoutubeUrlEmbedService youtubeUrlEmbedService;
    private final LambdaService lambdaService;

    @GetMapping("/youtube")
    @Operation(summary = "강의 시청, 강좌로 이동 API", description = """
           영상 리소스 조회 및 시청 처리 API입니다.
           
           [제공 정보]
           - 컬렉션 정보: 제목, 관심분야
           - 에피소드 정보: 영상 URL, 제목, 회차
           - 학습 진도: 현재 진도율, 총 진도
           - 작성된 메모 내용
           
           [처리 내용]
           - 시청 기록 저장
           - 컬렉션 진도 업데이트
           - 최근 학습 내역 갱신
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episodeId", description = "시청할 강의 에피소드 ID", example = "1")
    })
    public ApiResponse<ResourceResponseDTO.ResourceUrlDTO> watchEpisode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episodeId") Long episodeId)
    {
        String loginId = principalDetails.getUser().getLoginId();
        UserEpisodeProgress userEpisodeProgress = resourceService.getUserEpisodeProgress(episodeId, loginId);
        Collection collection = resourceService.getCollection(episodeId);
        Optional<Memo> memo = resourceService.getMemoContents(episodeId);
        Resource resource = youtubeUrlEmbedService.getResource(episodeId);
        ResourceResponseDTO.ResourceUrlDTO response =
                ResourceConverter.watchEpisode(collection, userEpisodeProgress, resource, memo);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/blog")
    @Operation(summary = "블로그 글 조회 API", description = """
           텍스트(블로그) 리소스 조회 API입니다.
           
           [제공 정보]
           - 컬렉션 정보: 제목, 관심분야
           - 블로그 글 정보: 제목, 내용, 회차
           - 학습 진도: 현재 스크롤 위치, 총 높이
           - 작성된 메모 내용
           
           [처리 내용]
           - 조회 기록 저장
           - 컬렉션 진도 업데이트
           - 최근 학습 내역 갱신
           
           [주의사항]
           블로그 내용은 별도 API(/blog/content)를 통해 제공
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "episodeId", description = "조회할 블로그 에피소드 ID", example = "1")
    })
    public ApiResponse<ResourceResponseDTO.ResourceBlogUrlDTO> watchBlogEpisode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episodeId") Long episodeId) {

        String loginId = principalDetails.getUser().getLoginId();
        UserEpisodeProgress userEpisodeProgress = resourceService.getUserEpisodeProgress(episodeId, loginId);
        Collection collection = resourceService.getCollection(episodeId);
        Optional<Memo> memo = resourceService.getMemoContents(episodeId);
        String resourceTitle = resourceService.getResource(episodeId).getTitle();
        String blogSourceUrl = "/resources/" + episodeId + "/blog/content";
        ResourceResponseDTO.ResourceBlogUrlDTO response = ResourceConverter.watchBlogEpisode(collection, userEpisodeProgress, blogSourceUrl, resourceTitle, memo);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/blog/content")
    @Operation(summary = "blog HTML 반환 API", description = """
           블로그 글의 HTML 컨텐츠를 반환하는 API입니다.
           /resources/{episodeId}/blog 호출 이후 사용됩니다.
           
           [처리 내용]
           - Lambda를 통한 블로그 크롤링
           - HTML 컨텐츠 가공 및 반환
           
           [응답 형식]
           - S3 객체 URL 반환
           
           [파라미터]
           - width: 컨텐츠 영역 너비 (기본값: 982)
           - height: 컨텐츠 영역 높이 (기본값: 552)
           """)
    public ApiResponse<String> getBlogEpisodeContent(
            @PathVariable("episodeId") Long episodeId,
            @RequestParam(defaultValue = "982") int width,
            @RequestParam(defaultValue = "552") int height) {
        Resource resource = resourceService.getResource(episodeId);
        if (resource.getClientUrl() != null) {
            return ApiResponse.onSuccess(resource.getClientUrl());
        }
        return ApiResponse.onSuccess(lambdaService.invokeLambda(resource.getUrl(), width, height, resource));
    }

    @PostMapping("/save-progress")
    @Operation(summary = "강의 진도 저장 API", description = """
           리소스 학습 진도를 저장하는 API입니다.
           
           [입력 정보]
           - resourceType: 리소스 유형 (VIDEO/TEXT)
           - progress:
             * VIDEO: 재생 시간(초)
             * TEXT: 스크롤 위치(px)
           
           [처리 내용]
           - 진도율 계산 및 저장
           - 컬렉션 전체 진도 업데이트
           - 학습 완료 여부 확인
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다."),
    })
    public ApiResponse<ResourceResponseDTO.ProgressResponseDTO> saveProgress(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episodeId") Long episodeId,
            @Valid @RequestBody ResourceRequestDTO.ProgressRequestDTO request) {
        String loginId = principalDetails.getUser().getLoginId();
        Boolean isCompleted = resourceService.saveProgress(request, loginId, episodeId);
        ResourceResponseDTO.ProgressResponseDTO response = ResourceConverter.toSaveProgressResponse(request, isCompleted);
        return ApiResponse.onSuccess(response);
    }
    @PostMapping("/update-complete")
    @Operation(summary = "에피소드 수강 상태 변환", description = """
           episode 수강 완료일 경우 수강 초기화, 수강 완료 상태가 아닐 경우 수강 완료로 표기하는 API 입니다.
           
           [처리 내용]
           - 에피소드 수강 상태 확인 후 변경
           
           [응답 정보]
           - 바뀐 수강 상태
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RESOURCE4001", description = "강의 에피소드를 찾을 수 없습니다."),
    })
    public ApiResponse<ResourceResponseDTO.changeEpisodeIsCompleteDTO> updateEpisodeStatus(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @PathVariable("episodeId") Long episodeId){
        String loginId = principalDetails.getUser().getLoginId();
        log.info("로그인 상태 확인 {}", loginId);
        Boolean isComplete = resourceService.changeEpisodeComplete(episodeId, loginId);
        ResourceResponseDTO.changeEpisodeIsCompleteDTO response = ResourceConverter.toChangeEpisodeIsCompleteDTO(isComplete);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/memo")
    @Operation(summary = "강의 메모 생성 API", description = """
           리소스에 대한 메모를 작성하는 API입니다.
           
           [입력 정보]
           - contents: 메모 내용 (필수)
           
           [처리 내용]
           - 메모 저장
           - 이전 메모가 있을 경우 덮어쓰기
           - 메모 작성 시간 기록
           
           [응답 정보]
           - 저장된 메모 내용
           """)
    public ApiResponse<MemoResponseDTO.MemoInfoDTO> createMemo(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("episodeId") Long episodeId,
            @Valid @RequestBody MemoRequestDTO.MemoJoinDTO request) {
        String loginId = principalDetails.getUser().getLoginId();
        log.info("로그인 상태 확인 {}", loginId);
        memoCommandService.saveMemo(loginId, episodeId, request);
        MemoResponseDTO.MemoInfoDTO response = MemoConverter.createMemo(request);
        return ApiResponse.onSuccess(response); // 성공 시 200 OK 반환
    }
}