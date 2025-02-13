package learningFlow.learningFlow_BE.web.dto.collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class CollectionResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        List<CollectionPreviewDTO> searchResults;
        Boolean hasNext; // 다음 페이지 존재 여부
        Integer currentPage; //현재 페이지
        Integer totalPages; //전체 페이지 수
        int totalCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionPreviewDTO {
        Long collectionId;
        String imageUrl;
        InterestField interestField;
        String title;
        String creator;
        List<String> keywords;
        List<Integer> difficulties;
        Integer amount;
        Integer runtime; //TODO : 필드 이름 수정 필요, 해당 컬렉션 수강 전체에 드느 시간이기 때문에 좀 더 좋은 필드 이름으로..
        Integer textCount;
        Integer videoCount;
        List<ResourceResponseDTO.SearchResultResourceDTO> resource;
        Integer likesCount; //북마크 -> 좋아요로 이름만 변경 
        boolean isLiked; //북마크 -> 좋아요로 이름만 변경
        Integer progressRatePercentage;
        String progressRatio;
        String learningStatus;

        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate startDate;
        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate completedDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionLearningInfo {
        String learningStatus;
        Integer progressRate;
        Integer currentEpisode;
        LocalDate startDate;
        LocalDate completedDate;
        List<ResourceResponseDTO.SearchResultResourceDTO> resourceDTOList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletedCollectionDTO {
        Long collectionId;
        InterestField interestField;
        String collectionTitle;
        String creator;
        List<String> keywords;
        List<Integer> difficulties;
        Integer runtime; //TODO : 필드 이름 수정 필요, 해당 컬렉션 수강 전체에 드느 시간이기 때문에 좀 더 좋은 필드 이름으로..
        //현재 데이터로 테스트할 시에 0으로 나오는게 정상 db에서 실제 사용자가 수강한 시간을 기록해주는 study_duration 필드에 값이 들어았어야함.

        LocalDate lastAccessedTime;
    }
}
