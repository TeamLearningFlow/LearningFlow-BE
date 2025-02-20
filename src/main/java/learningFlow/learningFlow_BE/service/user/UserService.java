package learningFlow.learningFlow_BE.service.user;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.UserHandler;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.converter.UserConverter;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import learningFlow.learningFlow_BE.domain.uuid.UuidRepository;
import learningFlow.learningFlow_BE.repository.MemoRepository;
import learningFlow.learningFlow_BE.repository.UserCollectionRepository;
import learningFlow.learningFlow_BE.repository.UserEpisodeProgressRepository;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.repository.collection.CollectionRepository;
import learningFlow.learningFlow_BE.service.collection.CollectionService;
import learningFlow.learningFlow_BE.web.dto.bookmark.BookmarkDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final UserCollectionRepository userCollectionRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final CollectionService collectionService;
    private final UserEpisodeProgressRepository userEpisodeProgressRepository;
    private final MemoRepository memoRepository;

    private static final int BOOKMARK_PAGE_SIZE = 12;

    public UserInfoDTO getUserInfo(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.convertToUserInfoDTO(user);
    }

    @Transactional
    public UserInfoDTO updateUserInfo(String loginId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 각 필드가 null이 아닌 경우에만 업데이트
        if(updateUserDTO.getProfileImgUrl() != null){
            user.updateImage(updateUserDTO.getProfileImgUrl());
        }

        if(updateUserDTO.getBannerImgUrl() != null){
            user.updateBannerImage(updateUserDTO.getBannerImgUrl());
        }

        if (updateUserDTO.getName() != null) {
            user.updateName(updateUserDTO.getName());
        }
        if (updateUserDTO.getJob() != null) {
            user.updateJob(updateUserDTO.getJob());
        }
        if (updateUserDTO.getInterestFields() != null && !updateUserDTO.getInterestFields().isEmpty()) {
            user.updateInterestFields(updateUserDTO.getInterestFields());
        }
        if (updateUserDTO.getPreferType() != null) {
            user.updatePreferType(updateUserDTO.getPreferType());
        }

        return getUserInfo(loginId);
    }


    @Transactional
    public BookmarkDTO.BookmarkResponseDTO toggleBookmark(String loginId, Long collectionId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionHandler(ErrorStatus.COLLECTION_NOT_FOUND));

        boolean isCurrentlyBookmarked = user.hasBookmarked(collectionId);

        if (isCurrentlyBookmarked) {
            user.removeBookmark(collectionId);
            collection.decrementBookmarkCount();
        } else {
            user.addBookmark(collectionId);
            collection.incrementBookmarkCount();
        }

        return new BookmarkDTO.BookmarkResponseDTO(!isCurrentlyBookmarked);
    }

    public CollectionResponseDTO.SearchResultDTO getBookmarkedCollections(
            String loginId,
            Integer sortType,
            Integer page) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        List<Long> bookmarkedIds = user.getBookmarkedCollectionIds();
        int totalCount = bookmarkedIds.size();

        // 북마크가 없는 경우
        if (totalCount == 0) {
            return CollectionConverter.toSearchResultDTO(
                    new ArrayList<>(),
                    false,
                    0,
                    page,
                    user,
                    new HashMap<>(),
                    0
            );
        }

        int totalPages = (int) Math.ceil((double) totalCount / BOOKMARK_PAGE_SIZE);

        // 페이지가 범위를 벗어나면 마지막 페이지 데이터 반환
        if (page > totalPages) {
            page = totalPages;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, BOOKMARK_PAGE_SIZE);
        List<Collection> collections = collectionRepository.findBookmarkedCollections(
                bookmarkedIds,
                sortType,
                pageRequest
        );

        if (collections.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(
                    collections,
                    false,
                    totalPages,
                    page,
                    user,
                    new HashMap<>(),
                    totalCount
            );
        }

        boolean hasNext = page < totalPages;

        Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap = collections.stream()
                .collect(Collectors.toMap(
                        Collection::getId,
                        collection -> collectionService.getLearningInfo(collection, user, false)
                ));

        return CollectionConverter.toSearchResultDTO(
                collections,
                hasNext,
                totalPages,
                page,
                user,
                learningInfoMap,
                totalCount
        );
    }

    public UserResponseDTO.UserMyPageResponseDTO getUserMyPageResponseDTO(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        List<UserCollection> inProgressUserCollectionList
                = userCollectionRepository.findByUserAndStatusOrderByUpdatedAtDesc(user, UserCollectionStatus.IN_PROGRESS);

        List<ResourceResponseDTO.RecentlyWatchedEpisodeDTO> recentlyWatchedEpisodeDTOList = inProgressUserCollectionList.stream()
                .map(userCollection -> {
                    CollectionEpisode currentEpisode = userCollection.getCollection().getEpisodes().stream()
                            .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                            .findFirst()
                            .orElse(null);

                    if (currentEpisode == null) {
                        return null;
                    }

                    // added: 완료된 에피소드 수 계산
                    long completedEpisodes = userCollection.getCollection().getEpisodes().stream()
                            .map(episode -> userEpisodeProgressRepository.findById(
                                    new UserEpisodeProgressId(episode.getId(), loginId)
                            ))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(progress ->
                                    progress.getIsComplete() ||
                                            (progress.getCurrentProgress() != null && progress.getCurrentProgress() >= 100)
                            )
                            .count();

                    // added: 전체 에피소드 수
                    int totalEpisodes = userCollection.getCollection().getEpisodes().size();

                    // added: 진행률 계산
                    double progressPercentage = (double) completedEpisodes / totalEpisodes * 100;


                    UserEpisodeProgressId progressId = new UserEpisodeProgressId(currentEpisode.getId(), loginId);
                    UserEpisodeProgress userEpisodeProgress = userEpisodeProgressRepository.findById(progressId).orElse(null);

                    return ResourceConverter.convertToRecentlyWatchedEpisodeDTO(
                            userCollection,
                            userEpisodeProgress,
                            currentEpisode,
                            totalEpisodes,
                            progressPercentage
                    );
                })
                .filter(Objects::nonNull)
                .toList();


        List<UserCollection> completedUserCollectionList
                = userCollectionRepository.findByUserAndStatusOrderByUpdatedAtDesc(user, UserCollectionStatus.COMPLETED);

        List<CollectionResponseDTO.CollectionPreviewDTO> completedCollectionList = completedUserCollectionList.stream()
                .map(userCollection -> {
                    CollectionResponseDTO.CollectionLearningInfo learningInfo = collectionService.getLearningInfo(userCollection.getCollection(), user, false);
                    return CollectionConverter.toCollectionPreviewDTO(userCollection.getCollection(), learningInfo, user);
                })
                .toList();

        return UserConverter.convertToUserMyPageResponseDTO(user, recentlyWatchedEpisodeDTOList, completedCollectionList);
    }

    @Transactional
    public void withdrawUser(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        try {
            // 유저의 메모 삭제
            memoRepository.deleteAllByUserId(loginId);

            // 유저의 학습 진도 삭제
            userEpisodeProgressRepository.deleteAllByUserId(loginId);

            // 유저의 컬렉션 관계 삭제
            userCollectionRepository.deleteAllByUserId(loginId);

            // 유저 완전 삭제
            userRepository.delete(user);

        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.WITHDRAWAL_FAILED);
        }
    }

}