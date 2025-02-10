package learningFlow.learningFlow_BE.repository.collection;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.CollectionHandler;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.QCollection;
import learningFlow.learningFlow_BE.domain.QCollectionEpisode;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CollectionRepositoryImpl implements CollectionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QCollectionEpisode episode = QCollectionEpisode.collectionEpisode;
    private final QCollection collection = QCollection.collection;

    @Override
    public List<Collection> searchCollections(SearchRequestDTO.SearchConditionDTO condition, Pageable pageable) {

        int skip = pageable.getPageNumber() * pageable.getPageSize();

        BooleanExpression searchConditions = createSearchConditions(condition);

/*
        if (lastId == 0L) {
            return jpaQueryFactory
                    .select(episode.collection)
                    .from(episode)
                    .where(searchConditions)
                    .groupBy(episode.collection.id)
                    .orderBy(createOrderSpecifier(condition.getSortType()))
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        Collection lastCollection = jpaQueryFactory
                .selectFrom(collection)
                .where(collection.id.eq(lastId))
                .fetchOne();

        if (lastCollection == null) {
            throw new CollectionHandler(ErrorStatus.COLLECTION_NOT_FOUND);
        }

        return searchNextPage(condition, lastCollection, pageable);
*/
        return jpaQueryFactory
                .select(episode.collection)
                .from(episode)
                .where(searchConditions)
                .groupBy(episode.collection.id)
                .orderBy(createOrderSpecifier(condition.getSortType()))
                .offset(skip)  // ✅ added: 시작 위치 설정
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier<?>[] createOrderSpecifier(Integer sortType) {  // 반환 타입을 배열로 변경
        if (sortType == null || sortType == 0) {
            return new OrderSpecifier[]{ episode.collection.id.desc() };
        }
        return new OrderSpecifier[]{
                episode.collection.bookmarkCount.desc(),
                episode.collection.id.desc()
        };
    }

    private BooleanExpression createSearchConditions(SearchRequestDTO.SearchConditionDTO condition) {
        return Expressions.allOf(
                createDynamicKeyword(condition.getKeyword()),
                createDynamicInterestFields(condition.getInterestFields()),
                createDynamicPreferMediaType(condition.getPreferMediaType()),
                createDynamicDifficulty(condition.getDifficulties()),
                createDynamicAmount(condition.getAmounts())
        );
    }

    @Override
    public Integer getTotalCount(SearchRequestDTO.SearchConditionDTO condition) {

        Long count = jpaQueryFactory
                .select(episode.collection.countDistinct())
                .from(episode)
                .where(createSearchConditions(condition))
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    private BooleanExpression createCursorCondition(Integer sortType, Collection lastCollection) {
        if (sortType == null || sortType == 0) {
            return episode.collection.id.lt(lastCollection.getId());
        }

        return episode.collection.bookmarkCount.lt(lastCollection.getBookmarkCount())
                .or(episode.collection.bookmarkCount.eq(lastCollection.getBookmarkCount())
                        .and(episode.collection.id.lt(lastCollection.getId())));
    }

    @Override
    public Integer getCountGreaterThanBookmark(Integer bookmarkCount, Long lastId, SearchRequestDTO.SearchConditionDTO condition) {

        BooleanExpression cursorCondition = createCountCursorCondition(condition.getSortType(), bookmarkCount, lastId);

        Long count = jpaQueryFactory
                .select(episode.collection.countDistinct())
                .from(episode)
                .where(createSearchConditions(condition), cursorCondition)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    private BooleanExpression createCountCursorCondition(Integer sortType, Integer bookmarkCount, Long lastId) {
        if (sortType == null || sortType == 0) {
            return episode.collection.id.gt(lastId);
        }

        return episode.collection.bookmarkCount.gt(bookmarkCount)
                .or(episode.collection.bookmarkCount.eq(bookmarkCount)
                        .and(episode.collection.id.gt(lastId)));
    }

    @Override
    public List<Collection> findTopBookmarkedCollections(int limit) {
        return jpaQueryFactory
                .selectFrom(collection)
                .orderBy(collection.bookmarkCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Collection> findByInterestFieldAndPreferType(
            List<InterestField> interestFields,
            MediaType preferType,
            boolean matchInterest,
            boolean matchPreferType,
            int limit
    ) {

        BooleanExpression interestCondition = matchInterest ?
                collection.interestField.in(interestFields) : collection.interestField.notIn(interestFields);

        BooleanExpression preferTypeCondition = null;
        if (preferType != MediaType.NO_PREFERENCE) {
            preferTypeCondition = preferType == MediaType.VIDEO ?
                    (matchPreferType ? collection.resourceTypeRatio.goe(50) : collection.resourceTypeRatio.lt(50)) :
                    (matchPreferType ? collection.resourceTypeRatio.lt(50) : collection.resourceTypeRatio.goe(50));
        }

        return jpaQueryFactory
                .selectFrom(collection)
                .where(interestCondition, preferTypeCondition)
                .orderBy(collection.bookmarkCount.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression createDynamicInterestFields(InterestField interestFields) {
        if (interestFields == null) {
            return null;
        }

        return episode.collection.interestField.eq(interestFields);
    }

    private BooleanExpression createDynamicKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return episode.collection.title.containsIgnoreCase(keyword)
                .or(episode.collection.creator.containsIgnoreCase(keyword))
                .or(episode.collection.keywords.any().containsIgnoreCase(keyword))
                .or(episode.episodeName.containsIgnoreCase(keyword));
    }

    private BooleanExpression createDynamicPreferMediaType(Integer preferMediaType) {

        if (preferMediaType == null) {
            return null;
        }

        return switch (preferMediaType) {
            case 1 -> episode.collection.resourceTypeRatio.eq(0);
            case 2 -> episode.collection.resourceTypeRatio.loe(50);
            case 4 -> episode.collection.resourceTypeRatio.goe(50);
            case 5 -> episode.collection.resourceTypeRatio.eq(100);
            default -> null;
        };
    }

    private BooleanExpression createDynamicDifficulty(List<Integer> difficulties) {
        if (difficulties == null) {
            return null;
        }

        return episode.collection.difficulty.any().in(difficulties);
    }

    private BooleanExpression createDynamicAmount(List<String> amounts) {
        if (amounts == null || amounts.isEmpty()) {
            return null;
        }

        BooleanExpression resultExpression = null;

        for (String amount : amounts) {
            BooleanExpression expression = switch (amount) {
                case "SHORT" -> episode.collection.amount.between(1, 5);
                case "MEDIUM" -> episode.collection.amount.between(6, 10);
                case "LONG" -> episode.collection.amount.goe(11);
                default -> null;
            };

            if (expression != null) {
                resultExpression = (resultExpression == null) ? expression : resultExpression.or(expression);
            }
        }

        return resultExpression;
    }
}
