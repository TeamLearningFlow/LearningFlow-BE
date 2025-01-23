package learningFlow.learningFlow_BE.repository.home;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;

import java.util.List;

public interface HomeRepository {
    List<Collection> findTopBookmarkedCollections(int limit);
    List<CollectionEpisode> findRecentEpisodes(Long collectionId, int currentEpisode, int limit);
}
