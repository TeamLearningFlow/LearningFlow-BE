package learningFlow.learningFlow_BE.repository.home;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.User;

import java.util.List;

public interface HomeRepository {
    List<Collection> findTopBookmarkedCollections(int limit, User user);
    List<CollectionEpisode> findRecentEpisodes(Long collectionId, int currentEpisode, int limit);
}
