package learningFlow.learningFlow_BE.service.search;

import learningFlow.learningFlow_BE.converter.SearchConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.repository.search.SearchRepositoryCustom;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepositoryCustom searchRepository;
    private static final int PAGE_SIZE = 8;

    public SearchResponseDTO.SearchResultDTO search(SearchRequestDTO.SearchConditionDTO condition, Long lastId, Authentication authentication) {

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Collection> collections = searchRepository.searchCollections(condition, lastId, pageRequest);


        if (collections.isEmpty()) {
            return SearchConverter.toSearchResultDTO(collections, null, false, 0, 0, null);
        }

        Long lastCollectionId = collections.getLast().getId();
        boolean hasNext = hasNextPage(condition, lastCollectionId);

        Integer totalCount = searchRepository.getTotalCount(condition);
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        int currentPage = calculateCurrentPage(lastId, condition);

        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        }

        return SearchConverter.toSearchResultDTO(collections, lastCollectionId, hasNext, totalPages, currentPage, currentUser);
    }

    private int calculateCurrentPage(Long lastId, SearchRequestDTO.SearchConditionDTO condition) {
        if (lastId == 0L) {
            return 1; // 첫 페이지
        }

        // lastId보다 큰 ID를 가진 컬렉션의 수를 조회
        Integer greaterCount = searchRepository.getCountGreaterThanId(lastId, condition);

        // 페이지 번호 계산 (1부터 시작)
        return greaterCount / PAGE_SIZE + 2;
    }

    private boolean hasNextPage(SearchRequestDTO.SearchConditionDTO condition, long lastCollectionId) {
        List<Collection> nextCollections = searchRepository.searchCollections(
                condition,
                lastCollectionId,
                PageRequest.of(0, 1)
        );

        return !nextCollections.isEmpty();
    }
}
