package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemoId is a Querydsl query type for MemoId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QMemoId extends BeanPath<MemoId> {

    private static final long serialVersionUID = -1164649595L;

    public static final QMemoId memoId = new QMemoId("memoId");

    public final NumberPath<Long> collectionEpisodeId = createNumber("collectionEpisodeId", Long.class);

    public final StringPath userId = createString("userId");

    public QMemoId(String variable) {
        super(MemoId.class, forVariable(variable));
    }

    public QMemoId(Path<? extends MemoId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemoId(PathMetadata metadata) {
        super(MemoId.class, metadata);
    }

}

