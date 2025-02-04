package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO;

import java.util.List;
import java.util.Map;

public class HomeConverter {

    public static HomeResponseDTO.GuestHomeInfoDTO convertToGuestHomeInfoDTO(
            List<CollectionResponseDTO.CollectionPreviewDTO> collectionPreviewList
    ) {
        return HomeResponseDTO.GuestHomeInfoDTO.builder()
                .recommendedCollections(collectionPreviewList)
                .build();
    }

    public static HomeResponseDTO.UserHomeInfoDTO convertToUserHomeInfoDTO(
            HomeResponseDTO.RecentLearningDTO recentLearning,
            List<Collection> recommendedCollections,
            User user,
            int size,
            Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap
    ) {
        List<CollectionResponseDTO.CollectionPreviewDTO> recommendedPreviewDTOs = recommendedCollections.stream()
                .distinct()
                .map(collection -> CollectionConverter.toCollectionPreviewDTO(
                        collection,
                        learningInfoMap.get(collection.getId()),
                        user
                ))
                .limit(size)
                .toList();

        return HomeResponseDTO.UserHomeInfoDTO.builder()
                .recentLearning(recentLearning)
                .recommendedCollections(recommendedPreviewDTOs)
                .build();
    }
}