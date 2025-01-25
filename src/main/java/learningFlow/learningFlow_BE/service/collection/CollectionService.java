package learningFlow.learningFlow_BE.service.collection;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.converter.HomeConverter;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.repository.collection.CollectionRepository;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserCollectionRepository userCollectionRepository;
    private static final int PAGE_SIZE = 8;
    private static final int HOME_COLLECTION_SIZE = 6;

    public CollectionResponseDTO.CollectionPreviewDTO CollectionDetails(Long collectionId, PrincipalDetails principalDetails) {

        Authentication authentication = (principalDetails != null) ? SecurityContextHolder.getContext().getAuthentication() : null;

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionHandler(ErrorStatus.COLLECTION_NOT_FOUND));

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        }

        return CollectionConverter.toCollectionPreviewDTO(collection, currentUser);
    }

    public CollectionResponseDTO.SearchResultDTO search(SearchRequestDTO.SearchConditionDTO condition, Long lastId, PrincipalDetails principalDetails) {

        Authentication authentication = (principalDetails != null) ? SecurityContextHolder.getContext().getAuthentication() : null;

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Collection> collections = collectionRepository.searchCollections(condition, lastId, pageRequest);

        if (collections.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(collections, null, false, 0, 0, null);
        }

        Collection lastCollection = collections.getLast();
        boolean hasNext = hasNextPage(condition, lastCollection);

        Integer totalCount = collectionRepository.getTotalCount(condition);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        int currentPage = calculateCurrentPage(condition, lastCollection);

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        }

        return CollectionConverter.toSearchResultDTO(collections, lastCollection.getId(), hasNext, totalPages, currentPage, currentUser);
    }

    public HomeResponseDTO.GuestHomeInfoDTO getGuestHomeCollections() {
        List<Collection> collections = collectionRepository.findTopBookmarkedCollections(HOME_COLLECTION_SIZE);

        return HomeConverter.convertToGuestHomeInfoDTO(CollectionConverter.convertToHomeCollection(collections));
    }

    public HomeResponseDTO.UserHomeInfoDTO getUserHomeCollections(User user) {
        // 최근 학습 컬렉션 조회
        HomeResponseDTO.RecentLearningDTO recentLearning = getRecentLearning(user);

        // 추천 컬렉션 목록 조회

        // 1. interestField와 preferType 모두 충족
        List<Collection> recommendedCollections = new ArrayList<>(collectionRepository.findByInterestFieldAndPreferType(
                user.getInterestFields(), user.getPreferType(), true, true, HOME_COLLECTION_SIZE
        ));

        // 2. interestField만 충족
        if (recommendedCollections.size() < HOME_COLLECTION_SIZE) {
            recommendedCollections.addAll(
                    collectionRepository.findByInterestFieldAndPreferType(
                            user.getInterestFields(), user.getPreferType(), true, false,
                            HOME_COLLECTION_SIZE - recommendedCollections.size()
                    )
            );
        }

        // 3. preferType만 충족
        if (recommendedCollections.size() < HOME_COLLECTION_SIZE) {
            recommendedCollections.addAll(
                    collectionRepository.findByInterestFieldAndPreferType(
                            user.getInterestFields(), user.getPreferType(), false, true,
                            HOME_COLLECTION_SIZE - recommendedCollections.size()
                    )
            );
        }

        // 4. 모두 불충족
        if (recommendedCollections.size() < HOME_COLLECTION_SIZE) {
            recommendedCollections.addAll(
                    collectionRepository.findByInterestFieldAndPreferType(
                            user.getInterestFields(), user.getPreferType(), false, false,
                            HOME_COLLECTION_SIZE - recommendedCollections.size()
                    )
            );
        }

        return HomeConverter.convertToUserHomeInfoDTO(recentLearning, recommendedCollections, user, HOME_COLLECTION_SIZE);
    }

    private HomeResponseDTO.RecentLearningDTO getRecentLearning(User user) {
        return userCollectionRepository
                .findFirstByUserAndStatusOrderByLastAccessedAtDesc(user, UserCollectionStatus.IN_PROGRESS)
                .map(ResourceConverter::toRecentLearningDTO)
                .orElse(null);
    }

    private int calculateCurrentPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection) {
        return collectionRepository.getCountGreaterThanBookmark(lastCollection.getBookmarkCount(),lastCollection.getId(), condition) / PAGE_SIZE + 1;
    }

    private boolean hasNextPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection) {
        return !collectionRepository.searchNextPage(condition, lastCollection, PageRequest.of(0, 1)).isEmpty();
    }
}
