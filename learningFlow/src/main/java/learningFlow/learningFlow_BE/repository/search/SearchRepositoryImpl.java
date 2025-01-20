package learningFlow.learningFlow_BE.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.PageHandler;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.QCollectionEpisode;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
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
        BooleanExpression mediaTypeExp = createDynamicMediaType(condition.getMediaType());
        BooleanExpression difficultyExp = createDynamicDifficulty(condition.getDifficulties());
        BooleanExpression amountExp = createDynamicAmount(condition.getAmounts());

        return jpaQueryFactory
                .select(episode.collection)
                .from(episode)
                .where(cursorExp, keywordExp, mediaTypeExp, difficultyExp, amountExp)
                .groupBy(episode.collection.id)
                .orderBy(episode.collection.id.desc())
                .limit(pageable.getPageSize())
                .fetch();
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

    private BooleanExpression createDynamicMediaType(MediaType mediaType) {
        if (mediaType == null || mediaType == MediaType.NO_PREFERENCE) {
            return null;
        }

        return episode.collection.mediaType.eq(mediaType);
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
