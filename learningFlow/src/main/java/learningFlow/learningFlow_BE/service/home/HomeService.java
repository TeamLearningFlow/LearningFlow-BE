package learningFlow.learningFlow_BE.service.home;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.repository.home.HomeRepository;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.EpisodeDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.HomeInfoDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.RecentLearningDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.RecommendedCollectionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {
    private final HomeRepository homeRepository;
    private final UserCollectionRepository userCollectionRepository;

    private static final int RECOMMENDED_SIZE = 6;
    private static final int RECENT_EPISODES_SIZE = 4;

    public HomeInfoDTO getHomeInfo(User user) {
        List<RecommendedCollectionDTO> recommendedCollections = getRecommendedCollections();
        RecentLearningDTO recentLearning = user != null ? getRecentLearning(user) : null;

        return HomeInfoDTO.builder()
                .recommendedCollections(recommendedCollections)
                .recentLearning(recentLearning)
                .build();
    }

    private List<RecommendedCollectionDTO> getRecommendedCollections() {
        // TODO: 추후 추천 알고리즘 개선 예정
        List<Collection> collections = homeRepository.findTopBookmarkedCollections(RECOMMENDED_SIZE);

        return collections.stream()
                .map(collection -> RecommendedCollectionDTO.builder()
                        .collectionId(collection.getId())
                        .title(collection.getTitle())
                        .creator(collection.getCreator())
                        .image(collection.getImage())
                        .difficulty(collection.getDifficulty().getFirst()) // 우선 0번째 사용하게
                        .category(collection.getInterestField().toString())
                        .detailInformation(collection.getDetailInformation())
                        .collectionUrl("/collections/" + collection.getId())
                        .build())
                .collect(Collectors.toList());
    }

    private RecentLearningDTO getRecentLearning(User user) {
        UserCollection recentCollection = userCollectionRepository
                .findRecentByUser(user, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        if (recentCollection == null) {
            return null;
        }

        List<CollectionEpisode> recentEpisodes = homeRepository.findRecentEpisodes(
                recentCollection.getCollection().getId(),
                recentCollection.getUserCollectionStatus(),
                RECENT_EPISODES_SIZE
        );

        return RecentLearningDTO.builder()
                .collectionId(recentCollection.getCollection().getId())
                .title(recentCollection.getCollection().getTitle())
                .currentEpisode(recentCollection.getUserCollectionStatus())
                .isCompleted(isCollectionCompleted(recentCollection))
                .episodes(convertToEpisodeDTO(recentEpisodes))
                .build();
    }

    private boolean isCollectionCompleted(UserCollection userCollection) {
        return userCollection.getUserCollectionStatus() >=
                userCollection.getCollection().getEpisodes().size();
    }

    private List<EpisodeDTO> convertToEpisodeDTO(List<CollectionEpisode> episodes) {
        return episodes.stream()
                .map(episode -> EpisodeDTO.builder()
                        .episodeNumber(episode.getEpisodeNumber())
                        .episodeName(episode.getEpisodeName())
                        .build())
                .collect(Collectors.toList());
    }
}
