package learningFlow.learningFlow_BE.service.search;

import learningFlow.learningFlow_BE.converter.SearchConverter;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.repository.search.SearchRepositoryCustom;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepositoryCustom searchRepository;
    private static final int PAGE_SIZE = 8;

    public SearchResponseDTO.SearchResultDTO search(SearchRequestDTO.SearchConditionDTO condition, Long lastId) {

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Collection> collections = searchRepository.searchCollections(condition, lastId, pageRequest);


        if (collections.isEmpty()) {
            return SearchConverter.toSearchResultDTO(collections, null, false);
        }

        Long lastCollectionId = collections.getLast().getId();
        boolean hasNext = hasNextPage(condition,lastCollectionId);

        return SearchConverter.toSearchResultDTO(collections, lastCollectionId, hasNext);
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
