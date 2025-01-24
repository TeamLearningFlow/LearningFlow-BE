package learningFlow.learningFlow_BE.service.home;

import learningFlow.learningFlow_BE.converter.HomeConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.repository.home.HomeRepository;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.collection.CollectionService;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO.CollectionPreviewDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.HomeInfoDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO.RecentLearningDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// TODO: 추천 알고리즘 개선 예정
// - 현재: 북마크 수 기준 정렬, 사용자 선호 매체/관심사 기반 추천
// - 개선: ???
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {
    private final HomeRepository homeRepository;
    private final UserCollectionRepository userCollectionRepository;
    private final CollectionService collectionService;

    private static final int RECOMMENDED_SIZE = 6;
    private static final int RECENT_EPISODES_SIZE = 4;

    public HomeInfoDTO getHomeInfo(User user) {
        List<Collection> topCollections = homeRepository.findTopBookmarkedCollections(RECOMMENDED_SIZE, user);
        List<CollectionPreviewDTO> recommendedCollections = topCollections.stream()
                .map(collection -> collectionService.CollectionDetails(
                        collection.getId(),
                        user != null ? SecurityContextHolder.getContext().getAuthentication() != null ? (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null : null
                ))
                .toList();

        RecentLearningDTO recentLearning = user != null ? getRecentLearning(user) : null;

        return HomeConverter.toHomeInfoDTO(recommendedCollections, recentLearning);
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

        return HomeConverter.toRecentLearningDTO(
                recentCollection.getCollection(),
                recentCollection.getUserCollectionStatus(),
                isCollectionCompleted(recentCollection),
                recentEpisodes
        );
    }

    private boolean isCollectionCompleted(UserCollection userCollection) {
        return userCollection.getUserCollectionStatus() >=
                userCollection.getCollection().getEpisodes().size();
    }
}