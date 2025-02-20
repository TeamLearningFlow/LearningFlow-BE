package learningFlow.learningFlow_BE.service.collection;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.converter.HomeConverter;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.repository.UserEpisodeProgressRepository;
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

    private final UserEpisodeProgressRepository userEpisodeProgressRepository;
    private final CollectionRepository collectionRepository;
    private final UserCollectionRepository userCollectionRepository;
    private static final int PAGE_SIZE = 8;
    private static final int HOME_COLLECTION_SIZE = 6;

    public CollectionResponseDTO.CollectionPreviewDTO CollectionDetails(Long collectionId, PrincipalDetails principalDetails) {

        Authentication authentication = (principalDetails != null) ? SecurityContextHolder.getContext().getAuthentication() : null;

        Collection collection = collectionRepository.findByIdWithEpisodesAndResources(collectionId) //N+1 문제 개선
                .orElseThrow(() -> new CollectionHandler(ErrorStatus.COLLECTION_NOT_FOUND));

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        }

        CollectionResponseDTO.CollectionLearningInfo learningInfo = getLearningInfo(collection, currentUser, true);

        return CollectionConverter.toCollectionPreviewDTO(collection, learningInfo, currentUser);
    }

    private List<ResourceResponseDTO.SearchResultResourceDTO> getAllResources(
            Collection collection
    ) {
        List<CollectionEpisode> episodes = collection.getEpisodes();

        // 모든 에피소드를 에피소드 번호 순으로 정렬
        return episodes.stream()
                .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                .map(ResourceConverter::convertToResourceDTO)
                .toList();
    }

    private List<ResourceResponseDTO.SearchResultResourceDTO> getAllResources(
            Collection collection,
            User user
    ) {
        List<CollectionEpisode> episodes = collection.getEpisodes();

        return episodes.stream()
                .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                .map(episode -> {
                    UserEpisodeProgress progress = null;
                    if (user != null) {
                        UserEpisodeProgressId progressId = new UserEpisodeProgressId(episode.getId(), user.getLoginId());
                        progress = userEpisodeProgressRepository.findById(progressId).orElse(null);
                    }
                    return ResourceConverter.convertToResourceDTO(episode, progress);
                })
                .toList();
    }

    public CollectionResponseDTO.SearchResultDTO search(SearchRequestDTO.SearchConditionDTO condition, Integer page, PrincipalDetails principalDetails) {

        Authentication authentication = (principalDetails != null) ? SecurityContextHolder.getContext().getAuthentication() : null;

        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE);
        List<Collection> collections = collectionRepository.searchCollections(condition, pageRequest);

        if (collections.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(collections, false, 0, 0, null, null, 0);
        }

        Collection lastCollection = collections.getLast();

        Integer totalCount = collectionRepository.getTotalCount(condition);

        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        boolean hasNext = page < totalPages;

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
                        collection -> getLearningInfo(collection, currentUser, false),
                        (existing, replacement) -> replacement  // added: 중복 키가 있을 경우 새 값 사용
                ));

        return CollectionConverter.toSearchResultDTO(
                collections,
                hasNext,
                totalPages,
                currentPage,
                currentUser,
                learningInfoMap,
                totalCount
        );
    }

    public CollectionResponseDTO.CollectionLearningInfo getLearningInfo(
            Collection collection,
            User user,
            boolean isDetailView
    ) {
        if (user == null) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("BEFORE")
                    .progressRate(null)
                    .resourceDTOList(isDetailView ?
                            getAllResourcesWithProgress(collection, null, 0) :
                            getFilteredResources(collection, null, 0))
                    .build();
        }

        Optional<UserCollection> userCollection = userCollectionRepository.findByUserAndCollection(user, collection);

        if (userCollection.isEmpty()) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("BEFORE")
                    .progressRate(null)
                    .resourceDTOList(isDetailView ?
                            getAllResourcesWithProgress(collection, null, 0) :
                            getFilteredResources(collection, null, 0))
                    .build();
        }

        UserCollection realUserCollection = userCollection.get();
        if (realUserCollection.getStatus() == UserCollectionStatus.COMPLETED) {
            return CollectionResponseDTO.CollectionLearningInfo.builder()
                    .learningStatus("COMPLETED")
                    .progressRate(100)
                    .progressRatio(calculateProgressRatio(realUserCollection))
                    .startDate(realUserCollection.getCreatedAt().toLocalDate())
                    .completedDate(realUserCollection.getCompletedTime())
                    .resourceDTOList(isDetailView ?
                            getAllResourcesWithProgress(collection, user, realUserCollection.getUserCollectionStatus()) :
                            new ArrayList<>())
                    .build();
        }

        int progressRate = calculateProgressRate(realUserCollection);
        return CollectionResponseDTO.CollectionLearningInfo.builder()
                .learningStatus("IN_PROGRESS")
                .progressRate(progressRate)
                .progressRatio(calculateProgressRatio(realUserCollection))
                .startDate(realUserCollection.getCreatedAt().toLocalDate())
                .currentEpisode(realUserCollection.getUserCollectionStatus())
                .resourceDTOList(isDetailView ?
                        getAllResourcesWithProgress(collection, user, realUserCollection.getUserCollectionStatus()) :
                        getFilteredResources(collection, user, realUserCollection.getUserCollectionStatus()))
                .build();
    }

    private List<ResourceResponseDTO.SearchResultResourceDTO> getAllResourcesWithProgress(
            Collection collection,
            User user,
            int currentEpisodeNumber
    ) {
        return collection.getEpisodes().stream()
                .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                .map(episode -> {
                    UserEpisodeProgress progress = null;
                    if (user != null) {
                        progress = userEpisodeProgressRepository.findById(
                                new UserEpisodeProgressId(episode.getId(), user.getLoginId())
                        ).orElse(null);
                    }
                    return ResourceConverter.convertToResourceDTO(
                            episode,
                            progress,
                            currentEpisodeNumber
                    );
                })
                .toList();
    }

    private int calculateProgressRate(UserCollection userCollection) {
/*
        return (int) Math.round((double) userCollection.getUserCollectionStatus() /
                userCollection.getCollection().getEpisodes().size() * 100);
*/
        if (userCollection.getStatus() == UserCollectionStatus.COMPLETED) {
            return 100;
        }

        Collection collection = userCollection.getCollection();
        User user = userCollection.getUser();

        // 전체 에피소드 수
        int totalEpisodes = collection.getEpisodes().size();

        // 완료된 에피소드 수 계산
        long completedEpisodes = collection.getEpisodes().stream()
                .map(episode -> userEpisodeProgressRepository.findById(
                        new UserEpisodeProgressId(episode.getId(), user.getLoginId())
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(progress ->
                        progress.getIsComplete() ||
                                (progress.getCurrentProgress() != null && progress.getCurrentProgress() >= 100)
                )
                .count();
/*                .filter(UserEpisodeProgress::getIsComplete)
                .count();*/

        return (int) Math.round((double) completedEpisodes / totalEpisodes * 100);
    }

    private String calculateProgressRatio(UserCollection userCollection) {
        if (userCollection.getStatus() == UserCollectionStatus.COMPLETED) {
            int totalEpisodes = userCollection.getCollection().getEpisodes().size();
            return String.format("%d / %d회차 (100%%)", totalEpisodes, totalEpisodes);
        }

        Collection collection = userCollection.getCollection();
        User user = userCollection.getUser();

        int totalEpisodes = collection.getEpisodes().size();
        int currentEpisode = userCollection.getUserCollectionStatus();

        long completedEpisodes = collection.getEpisodes().stream()
                .map(episode -> userEpisodeProgressRepository.findById(
                        new UserEpisodeProgressId(episode.getId(), user.getLoginId())
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(progress ->
                        progress.getIsComplete() ||
                                (progress.getCurrentProgress() != null && progress.getCurrentProgress() >= 100)
                )
                .count();
/*                .filter(UserEpisodeProgress::getIsComplete)
                .count();*/

        double progressPercentage = (double) completedEpisodes / totalEpisodes * 100;

        return String.format("%d / %d회차 (%.0f%%)",
                currentEpisode,
                totalEpisodes,
                progressPercentage);
    }

    private List<ResourceResponseDTO.SearchResultResourceDTO> getFilteredResources(
            Collection collection, User user, int currentEpisode
    ) {
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
            // 비회원이거나 수강 전인 경우 처음 4개
            filteredEpisodes = episodes.stream()
                    .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                    .limit(4)
                    .toList();
        } else {
            // 수강 중인 경우 현재 회차부터 3개
            filteredEpisodes = episodes.stream()
                    .sorted(Comparator.comparing(CollectionEpisode::getEpisodeNumber))
                    .filter(ep -> ep.getEpisodeNumber() >= currentEpisode)
                    .limit(4)
                    .toList();
        }
/*        return filteredEpisodes.stream()
                .map(ResourceConverter::convertToResourceDTO)
                .toList();*/
        return filteredEpisodes.stream()
                .map(episode -> {
                    UserEpisodeProgress progress = null;
                    if (user != null) {
                        progress = userEpisodeProgressRepository.findById(
                                new UserEpisodeProgressId(episode.getId(), user.getLoginId())
                        ).orElse(null);
                    }
                    return ResourceConverter.convertToResourceDTO(
                            episode,
                            progress,
                            currentEpisode
                    );
                })
                .toList();
    }

    public HomeResponseDTO.GuestHomeInfoDTO getGuestHomeCollections() {
        List<Collection> collections = collectionRepository.findTopBookmarkedCollections(HOME_COLLECTION_SIZE);

        List<CollectionResponseDTO.CollectionPreviewDTO> GuestCollectionList = collections.stream()
                .map(collection -> {
                    CollectionResponseDTO.CollectionLearningInfo defaultLearningInfo =
                            CollectionResponseDTO.CollectionLearningInfo.builder()
                                    .learningStatus("BEFORE")
                                    .progressRate(null)
                                    .resourceDTOList(getFilteredResources(collection, null, 0))
                                    .build();
                    return CollectionConverter.toCollectionPreviewDTO(collection, defaultLearningInfo, null);
                })
                .toList();

        return HomeConverter.convertToGuestHomeInfoDTO(GuestCollectionList);
    }

    public HomeResponseDTO.UserHomeInfoDTO getUserHomeCollections(User user) {
        // 최근 학습 컬렉션 조회
        CollectionResponseDTO.CollectionPreviewDTO recentLearning = getRecentLearning(user);

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
                        collection -> getLearningInfo(collection, user, false),
                        (existing, replacement) -> replacement  // added: 중복 키가 있을 경우 새 값 사용
                ));

        return HomeConverter.convertToUserHomeInfoDTO(
                recentLearning,
                recommendedCollections,
                user,
                HOME_COLLECTION_SIZE,
                learningInfoMap
        );
    }

    private CollectionResponseDTO.CollectionPreviewDTO getRecentLearning(User user) {

        return userCollectionRepository
                .findFirstByUserAndStatusOrderByUpdatedAtDesc(user, UserCollectionStatus.IN_PROGRESS)
                .map(userCollection -> {
                    Collection collection = userCollection.getCollection();

                    List<CollectionEpisode> episodes = collection.getEpisodes();
                    int totalEpisodes = episodes.size();
                    int currentEpisode = userCollection.getUserCollectionStatus();
                    int nextEpisode = currentEpisode + 1;

                    List<CollectionEpisode> selectedEpisodes = new ArrayList<>();
                    if (totalEpisodes <= 4) {
                        // 4회차 이하의 컬렉션은 전체 표시
                        selectedEpisodes.addAll(episodes);
                    } else {
                        // 5회차 이상의 컬렉션
                        // today 에피소드(다음 에피소드)가 가능한 2번째에 오도록 계산
                        int idealStart = nextEpisode - 2; // today가 2번째에 오기 위한 이상적인 시작 인덱스

                        // 실제 시작 인덱스 계산 (0 이상, totalEpisodes-4 이하)
                        int startIdx = Math.max(0, Math.min(idealStart, totalEpisodes - 4));

                        // 4개의 에피소드 선택
                        for (int i = 0; i < 4; i++) {
                            selectedEpisodes.add(episodes.get(startIdx + i));
                        }
                    }

                    CollectionResponseDTO.CollectionLearningInfo learningInfo = CollectionResponseDTO.CollectionLearningInfo.builder()
                            .learningStatus("IN_PROGRESS")
                            .progressRate(calculateProgressRate(userCollection))
                            .startDate(userCollection.getCreatedAt().toLocalDate())
                            .currentEpisode(currentEpisode)
                            .resourceDTOList(ResourceConverter.convertToResourceDTOWithToday(
                                    selectedEpisodes,
                                    nextEpisode,
                                    currentEpisode
                            ))
                            .build();

                    return CollectionConverter.toCollectionPreviewDTO(collection, learningInfo, user);
                })
                .orElse(null);
    }

    private int calculateCurrentPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection) {
        return collectionRepository.getCountGreaterThanBookmark(lastCollection.getBookmarkCount(),lastCollection.getId(), condition) / PAGE_SIZE + 1;
    }
}
