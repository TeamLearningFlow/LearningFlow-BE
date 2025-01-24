package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.HomeInfoDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.RecentLearningDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.EpisodeDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO.CollectionPreviewDTO;

import java.util.List;
import java.util.stream.Collectors;

public class HomeConverter {
    public static HomeInfoDTO toHomeInfoDTO(List<CollectionPreviewDTO> recommendedCollections, RecentLearningDTO recentLearning) {
        return HomeInfoDTO.builder()
                .recommendedCollections(recommendedCollections)
                .recentLearning(recentLearning)
                .build();
    }

    public static RecentLearningDTO toRecentLearningDTO(Collection collection, int currentEpisode, boolean isCompleted, List<CollectionEpisode> episodes) {
        return RecentLearningDTO.builder()
                .collectionId(collection.getId())
                .title(collection.getTitle())
                .currentEpisode(currentEpisode)
                .isCompleted(isCompleted)
                .episodes(toEpisodeDTOList(episodes))
                .build();
    }

    public static List<EpisodeDTO> toEpisodeDTOList(List<CollectionEpisode> episodes) {
        return episodes.stream()
                .map(HomeConverter::toEpisodeDTO)
                .collect(Collectors.toList());
    }

    public static EpisodeDTO toEpisodeDTO(CollectionEpisode episode) {
        return EpisodeDTO.builder()
                .episodeNumber(episode.getEpisodeNumber())
                .episodeName(episode.getEpisodeName())
                .build();
    }
}