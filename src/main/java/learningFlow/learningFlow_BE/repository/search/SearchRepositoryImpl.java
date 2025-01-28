package learningFlow.learningFlow_BE.repository.search;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.PageHandler;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.QCollectionEpisode;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QCollectionEpisode episode = QCollectionEpisode.collectionEpisode;

    @Override
    public List<Collection> searchCollections(SearchRequestDTO.SearchConditionDTO condition, Long lastId, Pageable pageable) {

        if (lastId == null) {
            throw new PageHandler(ErrorStatus.NO_MORE_COLLECTION);
        }

        BooleanExpression cursorExp = createCursorExp(lastId);
        BooleanExpression keywordExp = createDynamicKeyword(condition.getKeyword());
        BooleanExpression interestFieldExp = createDynamicInterestFields(condition.getInterestFields());
        BooleanExpression preferMediaTypeExp = createDynamicPreferMediaType(condition.getPreferMediaType());
        BooleanExpression difficultyExp = createDynamicDifficulty(condition.getDifficulties());
        BooleanExpression amountExp = createDynamicAmount(condition.getAmounts());

        return jpaQueryFactory
                .select(episode.collection)
                .from(episode)
                .where(cursorExp, keywordExp, interestFieldExp, preferMediaTypeExp, difficultyExp, amountExp)
                .groupBy(episode.collection.id)
                .orderBy(episode.collection.id.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Integer getTotalCount(SearchRequestDTO.SearchConditionDTO condition) {
        BooleanExpression keywordExp = createDynamicKeyword(condition.getKeyword());
        BooleanExpression interestFieldExp = createDynamicInterestFields(condition.getInterestFields());
        BooleanExpression preferMediaTypeExp = createDynamicPreferMediaType(condition.getPreferMediaType());
        BooleanExpression difficultyExp = createDynamicDifficulty(condition.getDifficulties());
        BooleanExpression amountExp = createDynamicAmount(condition.getAmounts());

        Long count = jpaQueryFactory
                .select(episode.collection.countDistinct())
                .from(episode)
                .where(keywordExp, interestFieldExp, preferMediaTypeExp, difficultyExp, amountExp)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public Integer getCountGreaterThanId(Long lastId, SearchRequestDTO.SearchConditionDTO condition) {
        BooleanExpression keywordExp = createDynamicKeyword(condition.getKeyword());
        BooleanExpression interestFieldExp = createDynamicInterestFields(condition.getInterestFields());
        BooleanExpression preferMediaTypeExp = createDynamicPreferMediaType(condition.getPreferMediaType());
        BooleanExpression difficultyExp = createDynamicDifficulty(condition.getDifficulties());
        BooleanExpression amountExp = createDynamicAmount(condition.getAmounts());

        // lastId보다 큰 ID를 가진 컬렉션만 카운트
        BooleanExpression greaterThanExp = lastId == 0L ? null : episode.collection.id.gt(lastId);

        Long count = jpaQueryFactory
                .select(episode.collection.countDistinct())
                .from(episode)
                .where(greaterThanExp, keywordExp, interestFieldExp, preferMediaTypeExp, difficultyExp, amountExp)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    private BooleanExpression createDynamicInterestFields(InterestField interestFields) {
        if (interestFields == null) {
            return null;
        }

        return episode.collection.interestField.eq(interestFields);
    }

    private BooleanExpression createCursorExp(Long lastId) {
        if (lastId == 0L) return null;
        return episode.collection.id.lt(lastId);
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
