package learningFlow.learningFlow_BE.service.user;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.UserHandler;
import learningFlow.learningFlow_BE.converter.SearchConverter;
import learningFlow.learningFlow_BE.converter.UserConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.repository.CollectionRepository;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.bookmark.BookmarkDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final CollectionRepository collectionRepository;

    private static final int BOOKMARK_PAGE_SIZE = 8;

    public UserInfoDTO getUserInfo(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return userConverter.convertToUserInfoDTO(user);
    }

    @Transactional
    public UserInfoDTO updateUserInfo(String loginId, UpdateUserDTO updateUserDTO, MultipartFile imageFile) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // TODO: 이미지 업데이트 로직 추가 예정
        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("이미지 업데이트 요청 발생 - 추후 구현 예정");
        }

        // 각 필드가 null이 아닌 경우에만 업데이트
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

    @Transactional(readOnly = true)
    public SearchResponseDTO.SearchResultDTO getBookmarkedCollections(String loginId, Long lastId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 북마크된 컬렉션 ID 목록 가져오기
        List<Long> bookmarkedIds = user.getBookmarkedCollectionIds();

        if (bookmarkedIds.isEmpty()) {
            return SearchConverter.toSearchResultDTO(new ArrayList<>(), null, false, 0, 0, user);
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
                return SearchConverter.toSearchResultDTO(new ArrayList<>(), null, false, 0, 0, user);
            }
            collections = collectionRepository.findByIdIn(
                    bookmarkedIds.stream()
                            .skip(startIndex)
                            .limit(BOOKMARK_PAGE_SIZE)
                            .toList()
            );
        }

        if (collections.isEmpty()) {
            return SearchConverter.toSearchResultDTO(collections, null, false, 0, 0, user);
        }

        Long lastCollectionId = collections.getLast().getId();
        boolean hasNext = (bookmarkedIds.indexOf(lastCollectionId) + 1) < bookmarkedIds.size();

        int totalPages = (int) Math.ceil((double) bookmarkedIds.size() / BOOKMARK_PAGE_SIZE);
        int currentPage = (lastId == 0) ? 1 : (bookmarkedIds.indexOf(lastId) / BOOKMARK_PAGE_SIZE) + 2;

        return SearchConverter.toSearchResultDTO(
                collections,
                lastCollectionId,
                hasNext,
                totalPages,
                currentPage,
                user
        );
    }
}