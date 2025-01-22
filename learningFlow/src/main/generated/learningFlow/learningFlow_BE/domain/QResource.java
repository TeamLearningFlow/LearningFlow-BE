package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResource is a Querydsl query type for Resource
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResource extends EntityPathBase<Resource> {

    private static final long serialVersionUID = 2055422878L;

    public static final QResource resource = new QResource("resource");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath clientUrl = createString("clientUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ListPath<CollectionEpisode, QCollectionEpisode> episodes = this.<CollectionEpisode, QCollectionEpisode>createList("episodes", CollectionEpisode.class, QCollectionEpisode.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath resourceDetails = createString("resourceDetails");

    public final NumberPath<Integer> resourceQuantity = createNumber("resourceQuantity", Integer.class);

    public final StringPath title = createString("title");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.ResourceType> type = createEnum("type", learningFlow.learningFlow_BE.domain.enums.ResourceType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath url = createString("url");

    public QResource(String variable) {
        super(Resource.class, forVariable(variable));
    }

    public QResource(Path<? extends Resource> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResource(PathMetadata metadata) {
        super(Resource.class, metadata);
    }

}

