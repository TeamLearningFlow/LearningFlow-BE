package learningFlow.learningFlow_BE.repository.home;

import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
     * - 선호 매체/관심사 기반 추천
     *  1. 관심분야 + 선호타입 50% 이상
     *  2. 관심분야 + 선호타입 50% 미만
     *  3. 비관심분야 + 선호타입 50% 이상
     *  4. 비관심분야 + 선호타입 50% 미만
     * TODO: 추천 알고리즘 개선 예정(최적화 필요)
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
        List<Collection> result = new ArrayList<>();
        List<InterestField> userInterests = user.getInterestFields();

        // 1. 관심분야 + 선호타입 50% 이상
        result.addAll(queryFactory
                .select(collection)
                .from(collection)
                .where(collection.interestField.in(userInterests)
                        .and(preferVideo ?
                                collection.resourceTypeRatio.goe(50) :
                                collection.resourceTypeRatio.lt(50)))
                .orderBy(collection.bookmarkCount.desc())
                .fetch());

        // 2. 관심분야 + 선호타입 50% 미만
        if (result.size() < limit) {
            result.addAll(queryFactory
                    .select(collection)
                    .from(collection)
                    .where(collection.interestField.in(userInterests)
                            .and(collection.notIn(result))
                            .and(preferVideo ?
                                    collection.resourceTypeRatio.lt(50) :
                                    collection.resourceTypeRatio.goe(50)))
                    .orderBy(collection.bookmarkCount.desc())
                    .limit(limit - result.size())
                    .fetch());
        }

        // 3. 비관심분야 + 선호타입 50% 이상
        if (result.size() < limit) {
            result.addAll(queryFactory
                    .select(collection)
                    .from(collection)
                    .where(collection.interestField.notIn(userInterests)
                            .and(collection.notIn(result))
                            .and(preferVideo ?
                                    collection.resourceTypeRatio.goe(50) :
                                    collection.resourceTypeRatio.lt(50)))
                    .orderBy(collection.bookmarkCount.desc())
                    .limit(limit - result.size())
                    .fetch());
        }

        // 4. 비관심분야 + 선호타입 50% 미만
        if (result.size() < limit) {
            result.addAll(queryFactory
                    .select(collection)
                    .from(collection)
                    .where(collection.interestField.notIn(userInterests)
                            .and(collection.notIn(result))
                            .and(preferVideo ?
                                    collection.resourceTypeRatio.lt(50) :
                                    collection.resourceTypeRatio.goe(50)))
                    .orderBy(collection.bookmarkCount.desc())
                    .limit(limit - result.size())
                    .fetch());
        }

        return result.subList(0, Math.min(result.size(), limit));
    }


    private List<Collection> findCollectionsByBookmarkCount(int limit) {
        return queryFactory
                .selectFrom(collection)
                .orderBy(collection.bookmarkCount.desc())
                .limit(limit)
                .fetch();
    }
}