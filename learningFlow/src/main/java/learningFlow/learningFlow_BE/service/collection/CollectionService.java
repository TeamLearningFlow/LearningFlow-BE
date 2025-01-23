package learningFlow.learningFlow_BE.service.collection;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.converter.CollectionConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.repository.collection.CollectionRepository;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private static final int PAGE_SIZE = 8;

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

        Long lastCollectionId = collections.getLast().getId();
        boolean hasNext = hasNextPage(condition, lastCollectionId);

        Integer totalCount = collectionRepository.getTotalCount(condition);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        int currentPage = calculateCurrentPage(lastId, condition);

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        }

        return CollectionConverter.toSearchResultDTO(collections, lastCollectionId, hasNext, totalPages, currentPage, currentUser);
    }

    private int calculateCurrentPage(Long lastId, SearchRequestDTO.SearchConditionDTO condition) {
        if (lastId == 0L) {
            return 1; // 첫 페이지
        }

        // lastId보다 큰 ID를 가진 컬렉션의 수를 조회
        Integer greaterCount = collectionRepository.getCountGreaterThanId(lastId, condition);

        // 페이지 번호 계산 (1부터 시작)
        return greaterCount / PAGE_SIZE + 2;
    }

    private boolean hasNextPage(SearchRequestDTO.SearchConditionDTO condition, long lastCollectionId) {
        List<Collection> nextCollections = collectionRepository.searchCollections(
                condition,
                lastCollectionId,
                PageRequest.of(0, 1)
        );
        return !nextCollections.isEmpty();
    }
}
