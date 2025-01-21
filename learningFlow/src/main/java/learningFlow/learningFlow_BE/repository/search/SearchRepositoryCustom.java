package learningFlow.learningFlow_BE.repository.search;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchRepositoryCustom {
    List<Collection> searchCollections(SearchRequestDTO.SearchConditionDTO condition, Long lastId, Pageable pageable);
    Integer getTotalCount(SearchRequestDTO.SearchConditionDTO condition);
    Integer getCountGreaterThanId(Long lastId, SearchRequestDTO.SearchConditionDTO condition);
}
