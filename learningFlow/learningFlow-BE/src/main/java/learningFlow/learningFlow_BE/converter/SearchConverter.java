package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;

import java.util.List;

public class SearchConverter {

    public static SearchRequestDTO.SearchConditionDTO toSearchConditionDTO(
            String keyword,
            MediaType mediaType,
            List<Integer> difficulties,
            List<String> amounts
    ) {
        return SearchRequestDTO.SearchConditionDTO.builder()
                .keyword(keyword)
                .mediaType(mediaType)
                .difficulties(difficulties)
                .amounts(amounts)
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

        int totalSeconds = collection.getEpisodes().stream()
                .map(CollectionEpisode::getResource)
                .mapToInt(Resource::getRuntime).sum();

        int totalHours = (int) Math.ceil(totalSeconds / 3600);

        return SearchResponseDTO.CollectionPreviewDTO.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .creator(collection.getCreator())
                .keywords(collection.getKeywords())
                .difficulties(collection.getDifficulty())
                .mediaType(collection.getMediaType())
                .amount(collection.getAmount())
                .runtime(totalHours)
                .build();
    }
}
