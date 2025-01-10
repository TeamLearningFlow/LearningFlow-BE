package learningFlow.learningFlow_BE.web.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CollectionResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionListDto {
        List<CollectionDto> collectionList;
        Integer listSize;
        boolean isFirst;
        boolean isLast;
        Integer totalPage;
        Long totalElements;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionDto {
        String title;
        // TODO: collection 조회 시 조회할 필드 들 구체적으로 정해서 넣어야 합니다.
    }
}
