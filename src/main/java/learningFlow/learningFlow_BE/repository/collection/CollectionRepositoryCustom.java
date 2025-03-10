package learningFlow.learningFlow_BE.repository.collection;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CollectionRepositoryCustom {
    List<Collection> searchCollections(SearchRequestDTO.SearchConditionDTO condition, Long lastId, Pageable pageable);
    Integer getTotalCount(SearchRequestDTO.SearchConditionDTO condition);
    List<Collection> searchNextPage(SearchRequestDTO.SearchConditionDTO condition, Collection lastCollection, Pageable pageable);
    Integer getCountGreaterThanBookmark(Integer bookmarkCount, Long lastId, SearchRequestDTO.SearchConditionDTO condition);
}
