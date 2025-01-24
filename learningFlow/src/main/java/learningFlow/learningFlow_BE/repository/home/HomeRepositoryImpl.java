package learningFlow.learningFlow_BE.repository.home;

import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO: 성능 최적화 검토 필요
// - Collection 조회 시 N+1 문제 확인
// - 인덱스 추가 검토: bookmarkCount
@Repository
@RequiredArgsConstructor
public class HomeRepositoryImpl implements HomeRepository {
    private final JPAQueryFactory queryFactory;
    private final QCollection collection = QCollection.collection;
    private final QCollectionEpisode episode = QCollectionEpisode.collectionEpisode;
    private final QResource resource = QResource.resource;

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

    /**
     * 사용자 선호 매체/관심사 기반 추천
     * - 사용자가 로그인하지 않은 경우 또는 선호 매체가 없는 경우 북마크 수 기준으로 추천
     * - 선호하는 매체가 있는 경우 해당 매체의 컬렉션들을 먼저 조회(북마크 수 기준)
     * - 선호하는 매체의 컬렉션이 부족한 경우 나머지 컬렉션들로 채움(북마크 수 기준)
     * @param limit
     * @param user
     * @return
     */
    @Override
    public List<Collection> findTopBookmarkedCollections(int limit, User user) {
        if (user == null || user.getPreferType() == MediaType.NO_PREFERENCE) {
            return findCollectionsByBookmarkCount(limit);
        }

        boolean preferVideo = user.getPreferType() == MediaType.VIDEO;

        // 선호하는 타입의 컬렉션들 먼저 조회
        List<Collection> preferredCollections = queryFactory
                .select(collection)
                .from(collection)
                .leftJoin(collection.episodes, episode)
                .leftJoin(episode.resource, resource)
                .groupBy(collection)
                .having(
                        preferVideo ?
                                resource.type.eq(ResourceType.VIDEO).count().multiply(100)
                                        .divide(resource.count()).goe(50L) :
                                resource.type.eq(ResourceType.TEXT).count().multiply(100)
                                        .divide(resource.count()).goe(50L)
                )
                .orderBy(collection.bookmarkCount.desc())
                .fetch();

        // 부족하면 나머지 컬렉션들로 채움
        if (preferredCollections.size() < limit) {
            List<Collection> remainingCollections = queryFactory
                    .selectFrom(collection)
                    .where(collection.notIn(preferredCollections))
                    .orderBy(collection.bookmarkCount.desc())
                    .limit(limit - preferredCollections.size())
                    .fetch();

            preferredCollections.addAll(remainingCollections);
        }

        return preferredCollections.subList(0, Math.min(preferredCollections.size(), limit));
    }

    private List<Collection> findCollectionsByBookmarkCount(int limit) {
        return queryFactory
                .selectFrom(collection)
                .orderBy(collection.bookmarkCount.desc())
                .limit(limit)
                .fetch();
    }
}