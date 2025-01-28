package learningFlow.learningFlow_BE.web.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkRequestDTO{
        Long collectionId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkResponseDTO {
        boolean isBookmarked;  // true: 북마크 됨, false: 북마크 해제됨
    }
}
