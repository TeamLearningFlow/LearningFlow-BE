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

    private int calculateCurrentPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection) {
        return collectionRepository.getCountGreaterThanBookmark(lastCollection.getBookmarkCount(),lastCollection.getId(), condition) / PAGE_SIZE + 1;
    }

    private boolean hasNextPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection) {
        return !collectionRepository.searchNextPage(condition, lastCollection, PageRequest.of(0, 1)).isEmpty();
    }
}
