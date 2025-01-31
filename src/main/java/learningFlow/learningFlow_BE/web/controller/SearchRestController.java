package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.collection.CollectionService;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Validated
@Slf4j
@Tag(name = "Search", description = "검색 API")
public class SearchRestController {

    private final CollectionService collectionService;

    @GetMapping
    @Operation(summary = "강의 검색 API", description = "키워드로 강의 에피소드를 검색하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "keyword", description = "검색어 (컬렉션 제목, 크리에이터, 키워드, 에피소드 제목)"),
            @Parameter(name = "interestFields", description = "검색할 관심 분야"),
            @Parameter(name = "preferMediaType", description = "미디어 타입 필터 (1: 텍스트만, 2 : 텍스트 선호, 3: 상관 없음, 4: 영상 선호, 5: 영상만)"),
            @Parameter(name = "difficulties", description = "난이도 필터 (1: 입문, 2: 초급, 3: 중급, 4: 실무)"),
            @Parameter(name = "amounts", description = "강의량 필터 (SHORT(1-5), MEDIUM(5-10), LONG(11이상)"),
            @Parameter(name = "sortType", description = "정렬 기준 (0: 최신순(기본값), 1: 북마크순)"),
            @Parameter(name = "lastId", description = "마지막으로 조회된 컬렉션의 ID (첫 페이지는 0)")
    })
    public ApiResponse<CollectionResponseDTO.SearchResultDTO> searchEpisodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InterestField interestFields,
            @RequestParam(required = false) Integer preferMediaType,
            @RequestParam(required = false) List<Integer> difficulties,
            @RequestParam(required = false) List<String> amounts,
            @RequestParam(required = false, defaultValue = "0") Integer sortType,
            @RequestParam(required = false, defaultValue = "0") Long lastId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                collectionService.search(
                        CollectionConverter.toSearchConditionDTO(keyword, interestFields, preferMediaType, difficulties, amounts, sortType),
                        lastId, principalDetails)
        );
    }
}
