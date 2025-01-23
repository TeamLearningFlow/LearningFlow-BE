package learningFlow.learningFlow_BE.repository.home;

import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.QCollection;
import learningFlow.learningFlow_BE.domain.QCollectionEpisode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HomeRepositoryImpl implements HomeRepository {
    private final JPAQueryFactory queryFactory;
    private final QCollection collection = QCollection.collection;
    private final QCollectionEpisode episode = QCollectionEpisode.collectionEpisode;

    @Override
    public List<Collection> findTopBookmarkedCollections(int limit) {
        return queryFactory
                .selectFrom(collection)
                .orderBy(collection.bookmarkCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<CollectionEpisode> findRecentEpisodes(Long collectionId, int currentEpisode, int limit) {
        int startEpisode = Math.max(1, currentEpisode - (limit - 1));

        return queryFactory
                .selectFrom(episode)
                .where(episode.collection.id.eq(collectionId)
                        .and(episode.episodeNumber.between(startEpisode, startEpisode + limit - 1)))
                .orderBy(episode.episodeNumber.asc())
                .fetch();
    }
}
