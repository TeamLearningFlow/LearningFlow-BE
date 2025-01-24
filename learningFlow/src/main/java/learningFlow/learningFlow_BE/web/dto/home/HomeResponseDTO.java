package learningFlow.learningFlow_BE.web.dto.home;

import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class HomeResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeInfoDTO {
        List<CollectionResponseDTO.CollectionPreviewDTO> recommendedCollections;
        RecentLearningDTO recentLearning;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentLearningDTO {
        private Long collectionId;
        private String title;
        private Integer currentEpisode;
        private boolean isCompleted;
        private List<EpisodeDTO> episodes;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeDTO {
        private Integer episodeNumber;
        private String episodeName;
    }
}
