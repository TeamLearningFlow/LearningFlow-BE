package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserEpisodeProgressId is a Querydsl query type for UserEpisodeProgressId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserEpisodeProgressId extends BeanPath<UserEpisodeProgressId> {

    private static final long serialVersionUID = 2042814056L;

    public static final QUserEpisodeProgressId userEpisodeProgressId = new QUserEpisodeProgressId("userEpisodeProgressId");

    public final NumberPath<Long> collectionEpisodeId = createNumber("collectionEpisodeId", Long.class);

    public final StringPath userId = createString("userId");

    public QUserEpisodeProgressId(String variable) {
        super(UserEpisodeProgressId.class, forVariable(variable));
    }

    public QUserEpisodeProgressId(Path<? extends UserEpisodeProgressId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEpisodeProgressId(PathMetadata metadata) {
        super(UserEpisodeProgressId.class, metadata);
    }

}

