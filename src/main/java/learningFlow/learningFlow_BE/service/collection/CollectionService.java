package learningFlow.learningFlow_BE.service.collection;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.converter.HomeConverter;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.repository.collection.CollectionRepository;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

        CollectionResponseDTO.CollectionLearningInfo learningInfo = getLearningInfo(collection, currentUser);

        return CollectionConverter.toCollectionPreviewDTO(collection, learningInfo, currentUser);
    }

    public CollectionResponseDTO.SearchResultDTO search(SearchRequestDTO.SearchConditionDTO condition, Long lastId, PrincipalDetails principalDetails) {

        Authentication authentication = (principalDetails != null) ? SecurityContextHolder.getContext().getAuthentication() : null;

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Collection> collections = collectionRepository.searchCollections(condition, lastId, pageRequest);

        if (collections.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(collections, null, false, 0, 0, null, null);
        }

        Collection lastCollection = collections.getLast();
        boolean hasNext = hasNextPage(condition, lastCollection);

        Integer totalCount = collectionRepository.getTotalCount(condition);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        int currentPage = calculateCurrentPage(condition, lastCollection);

        User currentUser;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        } else {
            currentUser = null;
        }

        Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap = collections.stream()
                .collect(Collectors.toMap(
                        Collection::getId,
                        collection -> getLearningInfo(collection, currentUser)
                ));

        return CollectionConverter.toSearchResultDTO(
                collections,
                lastCollection.getId(),
                hasNext,
                totalPages,
                currentPage,
                currentUser,
                learningInfoMap
        );
    }

    public CollectionResponseDTO.CollectionLearningInfo getLearningInfo(Collection collection, User user) {
        if (user == null) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("BEFORE")
                    .progressRate(null)
                    .resourceDTOList(getFilteredResources(collection, null, 0))
                    .build();
        }

        Optional<UserCollection> userCollection = userCollectionRepository.findByUserAndCollection(user, collection);

        if (userCollection.isEmpty()) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("BEFORE")
                    .progressRate(null)
                    .resourceDTOList(getFilteredResources(collection, null, 0))
                    .build();
        }

        UserCollection realUserCollection = userCollection.get();
        if (realUserCollection.getStatus() == UserCollectionStatus.COMPLETED) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("COMPLETED")
                    .progressRate(100)
                    .completedTime(realUserCollection.getLastAccessedAt())
                    .resourceDTOList(getFilteredResources(collection, user, realUserCollection.getUserCollectionStatus()))
                    .build();
        }

        int progressRate = calculateProgressRate(realUserCollection);
        return CollectionResponseDTO.CollectionLearningInfo.builder()
                .learningStatus("IN_PROGRESS")
                .progressRate(progressRate)
                .currentEpisode(realUserCollection.getUserCollectionStatus())
                .resourceDTOList(getFilteredResources(collection, user, realUserCollection.getUserCollectionStatus()))
                .build();
    }
    // added: 진도율 계산 메소드

    private int calculateProgressRate(UserCollection userCollection) {
        return (int) ((double) userCollection.getUserCollectionStatus() /
                userCollection.getCollection().getEpisodes().size() * 100);
    }

    private List<ResourceResponseDTO.SearchResultResourceDTO> getFilteredResources(
            Collection collection, User user, int currentEpisode) {
        List<CollectionEpisode> episodes = collection.getEpisodes();
        List<CollectionEpisode> filteredEpisodes;

        //수강 완료일때는 resource에 뭐 담을 필요 없음
        if (user != null) {
            Optional<UserCollection> userCollection = userCollectionRepository.findByUserAndCollection(user, collection);
            if (userCollection.isPresent() && userCollection.get().getStatus() == UserCollectionStatus.COMPLETED) {
                return new ArrayList<>();
            }
        }

        if (user == null || currentEpisode == 0) {
            // 비회원이거나 수강 전인 경우 처음 3개
            filteredEpisodes = episodes.stream()
                    .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                    .limit(3)
                    .toList();
        } else {
            // 수강 중인 경우 현재 회차부터 3개
            filteredEpisodes = episodes.stream()
                    .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                    .filter(ep -> ep.getEpisodeNumber() >= currentEpisode)
                    .limit(3)
                    .toList();
        }

        return filteredEpisodes.stream()
                .map(ResourceConverter::convertToResourceDTO)
                .toList();
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

        Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap = recommendedCollections.stream()
                .collect(Collectors.toMap(
                        Collection::getId,
                        collection -> getLearningInfo(collection, user)
                ));

        return HomeConverter.convertToUserHomeInfoDTO(
                recentLearning,
                recommendedCollections,
                user,
                HOME_COLLECTION_SIZE,
                learningInfoMap
        );
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
