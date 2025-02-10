package learningFlow.learningFlow_BE.repository.collection;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CollectionRepositoryCustom {
    List<Collection> searchCollections(SearchRequestDTO.SearchConditionDTO condition, Pageable pageable);
    Integer getTotalCount(SearchRequestDTO.SearchConditionDTO condition);
    Integer getCountGreaterThanBookmark(Integer bookmarkCount, Long lastId, SearchRequestDTO.SearchConditionDTO condition);
    List<Collection> findTopBookmarkedCollections(int limit);
    List<Collection> findByInterestFieldAndPreferType(List<InterestField> interestFields,
                                                      MediaType preferType,
                                                      boolean matchInterest,
                                                      boolean matchPreferType,
                                                      int limit);
}
