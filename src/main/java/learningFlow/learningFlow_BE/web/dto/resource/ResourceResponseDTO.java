package learningFlow.learningFlow_BE.web.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ResourceResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResourceUrlDTO {
        String embeddedUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchResultResourceDTO {
        Long resourceId;
        String episodeName;
        String url;
        String resourceSource;
        Integer episodeNumber;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentlyWatchedEpisodeDTO {
        Long resourceId;
        String CollectionTitle;
        String resourceSource;
        Integer episodeNumber;
        String episodeName;
        String progressRatio;
    }
}
