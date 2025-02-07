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
@Tag(name = "Search", description = "컬렉션 검색 API")
public class SearchRestController {

    private final CollectionService collectionService;

    @GetMapping
    @Operation(summary = "컬렉션 검색", description = """
           다양한 조건으로 컬렉션을 검색하고 결과를 반환합니다.
           
           [검색 조건]
           1. 키워드 검색
              - 컬렉션 제목
              - 크리에이터 이름
              - 키워드 태그
              - 에피소드 제목
              
           2. 카테고리 필터
              - 관심 분야: APP_DEVELOPMENT, WEB_DEVELOPMENT, PROGRAMMING_LANGUAGE, DEEP_LEARNING, STATISTICS, DATA_ANALYSIS, UI_UX, PLANNING, BUSINESS_PRODUCTIVITY, FOREIGN_LANGUAGE, CAREER
              
           3. 미디어 타입 필터
              - 1: 텍스트만
              - 2: 텍스트 선호
              - 3: 상관없음
              - 4: 영상 선호
              - 5: 영상만
              
           4. 난이도 필터 (복수 선택 가능)
              - 1: 입문
              - 2: 초급
              - 3: 중급
              - 4: 실무 -> 삭제됨, 백엔드 데이터상으로는 존재하지만 프론트엔드에서는 사용하지 않음
              
           5. 학습량 필터 (복수 선택 가능)
              - SHORT: 1-5회차
              - MEDIUM: 6-10회차
              - LONG: 11회차 이상
              
           6. 정렬 옵션
              - 0: 최신순 (기본값)
              - 1: 좋아요순
              
           [페이지네이션]
           - 무한 스크롤 방식
           - lastId: 마지막으로 조회된 컬렉션 ID
           - 첫 페이지는 lastId=0
           - 8개씩 조회
           
           [응답 정보]
           - searchResults: 검색된 컬렉션 목록
           - lastId: 마지막 컬렉션 ID
           - hasNext: 다음 페이지 존재 여부
           - currentPage: 현재 페이지
           - totalPages: 전체 페이지 수
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "keyword", description = "검색어", example = "스프링부트"),
            @Parameter(name = "interestFields", description = "관심 분야", example = "APP_DEVELOPMENT"),
            @Parameter(name = "preferMediaType", description = "선호 미디어 타입 (1:텍스트만, 2:텍스트선호, 3:상관없음, 4:영상선호, 5:영상만)", example = "3"),
            @Parameter(name = "difficulties", description = "난이도 목록 (1:입문, 2:초급, 3:중급, 4:실무)", example = "[1, 2]"),
            @Parameter(name = "amounts", description = "학습량 (SHORT, MEDIUM, LONG)", example = "[\"SHORT\", \"MEDIUM\"]"),
            @Parameter(name = "sortType", description = "정렬 기준 (0:최신순, 1:좋아요순)", example = "0"),
            @Parameter(name = "lastId", description = "마지막 컬렉션 ID (첫 페이지: 0)", example = "0")
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