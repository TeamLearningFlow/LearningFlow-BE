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

    private static final int BOOKMARK_PAGE_SIZE = 8;

    public UserInfoDTO getUserInfo(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.convertToUserInfoDTO(user);
    }

    @Transactional
    public UserInfoDTO updateUserInfo(String loginId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

//        if (imageFile != null && !imageFile.isEmpty()) {
//            log.info("이미지 업데이트 요청 발생");
//            String imageUrl = s3Manager.uploadImageToS3(imageFile);
//            // user 엔티티에 이미지 URL 업데이트
//            user.updateImage(imageUrl);
//        }

        // 각 필드가 null이 아닌 경우에만 업데이트
        if(updateUserDTO.getImgUrl() != null){
            user.updateImage(updateUserDTO.getImgUrl());
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

        return getUserInfo(loginId);
    }

//    private String uploadImageToS3(MultipartFile imageFile) {
//        try {
//            // UUID 생성 및 저장
//            String imageUuid = UUID.randomUUID().toString();
//            Uuid savedUuid = uuidRepository.save(Uuid.builder()
//                    .uuid(imageUuid).build());
//
//            // 이미지 업로드
//            String imageKey = s3Manager.generateKeyName(savedUuid); // KeyName 생성
//            String imageUrl = s3Manager.uploadFile(imageKey, imageFile); // 업로드된 URL 반환
//
//            // 업로드 성공 여부 확인
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                throw new GeneralException(ErrorStatus._BAD_REQUEST); // 업로드 실패 시 예외 처리
//            }
//
//            return imageUrl; // 성공 시 URL 반환
//
//        } catch (GeneralException e) {
//            log.error("이미지 업로드 실패: {}", e.getMessage());
//            throw e; // GeneralException은 그대로 전달
//        } catch (Exception e) {
//            log.error("이미지 업로드 중 내부 오류 발생: {}", e.getMessage());
//            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR); // 기타 예외 처리
//        }
//    }

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

    public CollectionResponseDTO.SearchResultDTO getBookmarkedCollections(String loginId, Long lastId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 북마크된 컬렉션 ID 목록 가져오기
        List<Long> bookmarkedIds = user.getBookmarkedCollectionIds();

        if (bookmarkedIds.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(new ArrayList<>(), null, false, 0, 0, user, new HashMap<>());
        }

        // lastId 이후의 컬렉션만 필터링
        List<Collection> collections;
        if (lastId == 0) {
            collections = collectionRepository.findByIdIn(
                    bookmarkedIds.stream()
                            .limit(BOOKMARK_PAGE_SIZE)
                            .toList()
            );
        } else {
            int startIndex = bookmarkedIds.indexOf(lastId) + 1;
            if (startIndex == 0 || startIndex >= bookmarkedIds.size()) {
                return CollectionConverter.toSearchResultDTO(new ArrayList<>(), null, false, 0, 0, user, new HashMap<>());
            }
            collections = collectionRepository.findByIdIn(
                    bookmarkedIds.stream()
                            .skip(startIndex)
                            .limit(BOOKMARK_PAGE_SIZE)
                            .toList()
            );
        }

        if (collections.isEmpty()) {
            return CollectionConverter.toSearchResultDTO(collections, null, false, 0, 0, user, new HashMap<>());
        }
        Long lastCollectionId = collections.getLast().getId();
        boolean hasNext = (bookmarkedIds.indexOf(lastCollectionId) + 1) < bookmarkedIds.size();

        int totalPages = (int) Math.ceil((double) bookmarkedIds.size() / BOOKMARK_PAGE_SIZE);
        int currentPage = (lastId == 0) ? 1 : (bookmarkedIds.indexOf(lastId) / BOOKMARK_PAGE_SIZE) + 2;

        Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap = collections.stream()
                .collect(Collectors.toMap(
                        Collection::getId,
                        collection -> collectionService.getLearningInfo(collection, user, false)
                ));

        return CollectionConverter.toSearchResultDTO(
                collections,
                lastCollectionId,
                hasNext,
                totalPages,
                currentPage,
                user,
                learningInfoMap
        );
    }

    public UserResponseDTO.UserMyPageResponseDTO getUserMyPageResponseDTO(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        List<UserCollection> inProgressUserCollectionList
                = userCollectionRepository.findByUserAndStatusOrderByCompletedTimeDesc(user, UserCollectionStatus.IN_PROGRESS);

        List<ResourceResponseDTO.RecentlyWatchedEpisodeDTO> recentlyWatchedEpisodeDTOList = inProgressUserCollectionList.stream()
                .map(userCollection -> {
                    CollectionEpisode currentEpisode = userCollection.getCollection().getEpisodes().stream()
                            .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                            .findFirst()
                            .orElse(null);

                    if (currentEpisode == null) {
                        return null;
                    }

                    UserEpisodeProgressId progressId = new UserEpisodeProgressId(currentEpisode.getId(), loginId);
                    UserEpisodeProgress userEpisodeProgress = userEpisodeProgressRepository.findById(progressId).orElse(null);

                    return ResourceConverter.convertToRecentlyWatchedEpisodeDTO(userCollection, userEpisodeProgress);
                })
                .filter(Objects::nonNull)
                .toList();


        List<UserCollection> completedUserCollectionList
                = userCollectionRepository.findByUserAndStatusOrderByCompletedTimeDesc(user, UserCollectionStatus.COMPLETED);

        List<CollectionResponseDTO.CollectionPreviewDTO> completedCollectionList = completedUserCollectionList.stream()
                .map(userCollection -> {
                    CollectionResponseDTO.CollectionLearningInfo learningInfo = collectionService.getLearningInfo(userCollection.getCollection(), user, false);
                    return CollectionConverter.toCollectionPreviewDTO(userCollection.getCollection(), learningInfo, user);
                })
                .toList();

        return UserConverter.convertToUserMyPageResponseDTO(user, recentlyWatchedEpisodeDTOList, completedCollectionList);
    }
}