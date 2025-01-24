package learningFlow.learningFlow_BE.web.dto.collection;

import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CollectionResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        List<CollectionPreviewDTO> searchResults;
        Long lastId;     // 마지막 컬렉션의 ID
        Boolean hasNext; // 다음 페이지 존재 여부
        Integer currentPage; //현재 페이지
        Integer totalPages; //전체 페이지 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionPreviewDTO {
        Long id;
        InterestField interestField;
        String title;
        String creator;
        List<String> keywords;
        List<Integer> difficulties;
        Integer amount;
        Integer runtime;
        Integer textCount;
        Integer videoCount;
        List<ResourceResponseDTO.SearchResultResourceDTO> resource;
        Integer bookmarkCount;
        boolean isBookmarked;
    }
}
