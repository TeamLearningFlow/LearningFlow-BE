package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;

import java.util.List;

public class SearchConverter {

    public static SearchRequestDTO.SearchConditionDTO toSearchConditionDTO(
            String keyword,
            MediaType mediaType,
            Integer difficulty,
            Integer amount
    ) {
        return SearchRequestDTO.SearchConditionDTO.builder()
                .keyword(keyword)
                .mediaType(mediaType)
                .difficulty(difficulty)
                .amount(amount)
                .build();
    }

    public static SearchResponseDTO.SearchResultDTO toSearchResultDTO(List<Collection> collections, Long lastId, boolean hasNext) {
        List<SearchResponseDTO.CollectionPreviewDTO> list
                = collections.stream().map(SearchConverter::toCollectionPreviewDTO).toList();

        return SearchResponseDTO.SearchResultDTO.builder()
                .searchResults(list)
                .lastId(lastId)
                .hasNext(hasNext)
                .build();
    }

    public static SearchResponseDTO.CollectionPreviewDTO toCollectionPreviewDTO(Collection collection) {
        return SearchResponseDTO.CollectionPreviewDTO.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .creator(collection.getCreator())
                .keywords(collection.getKeywords())
                .difficulty(collection.getDifficulty())
                .amount(collection.getAmount())
                .build();
    }
}
