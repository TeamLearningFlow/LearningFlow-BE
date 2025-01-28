package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEpisodeProgress is a Querydsl query type for UserEpisodeProgress
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEpisodeProgress extends EntityPathBase<UserEpisodeProgress> {

    private static final long serialVersionUID = 820001901L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEpisodeProgress userEpisodeProgress = new QUserEpisodeProgress("userEpisodeProgress");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> currentProgress = createNumber("currentProgress", Integer.class);

    public final NumberPath<Integer> episodeNumber = createNumber("episodeNumber", Integer.class);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.ResourceType> resourceType = createEnum("resourceType", learningFlow.learningFlow_BE.domain.enums.ResourceType.class);

    public final NumberPath<Integer> totalProgress = createNumber("totalProgress", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUserEpisodeProgressId userEpisodeProgressId;

    public QUserEpisodeProgress(String variable) {
        this(UserEpisodeProgress.class, forVariable(variable), INITS);
    }

    public QUserEpisodeProgress(Path<? extends UserEpisodeProgress> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserEpisodeProgress(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserEpisodeProgress(PathMetadata metadata, PathInits inits) {
        this(UserEpisodeProgress.class, metadata, inits);
    }

    public QUserEpisodeProgress(Class<? extends UserEpisodeProgress> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userEpisodeProgressId = inits.isInitialized("userEpisodeProgressId") ? new QUserEpisodeProgressId(forProperty("userEpisodeProgressId")) : null;
    }

}

