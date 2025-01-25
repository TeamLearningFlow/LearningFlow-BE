package learningFlow.learningFlow_BE.web.dto.home;

import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
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
    public static class UserHomeInfoDTO {
        RecentLearningDTO recentLearning;
        List<CollectionResponseDTO.CollectionPreviewDTO> recommendedCollections;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestHomeInfoDTO {
        List<CollectionResponseDTO.CollectionPreviewDTO> recommendedCollections;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentLearningDTO {
        private CollectionResponseDTO.CompletedCollectionDTO collection;
        private List<ResourceResponseDTO.SearchResultResourceDTO> resources;
        private String progressRatio;
    }
}
