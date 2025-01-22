package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCollection is a Querydsl query type for Collection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCollection extends EntityPathBase<Collection> {

    private static final long serialVersionUID = -819230162L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollection collection = new QCollection("collection");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Integer> bookmarkCount = createNumber("bookmarkCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath creator = createString("creator");

    public final StringPath detailInformation = createString("detailInformation");

    public final ListPath<Integer, NumberPath<Integer>> difficulty = this.<Integer, NumberPath<Integer>>createList("difficulty", Integer.class, NumberPath.class, PathInits.DIRECT2);

    public final ListPath<CollectionEpisode, QCollectionEpisode> episodes = this.<CollectionEpisode, QCollectionEpisode>createList("episodes", CollectionEpisode.class, QCollectionEpisode.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QImage image;

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField> interestField = createEnum("interestField", learningFlow.learningFlow_BE.domain.enums.InterestField.class);

    public final ListPath<String, StringPath> keywords = this.<String, StringPath>createList("keywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> resourceTypeRatio = createNumber("resourceTypeRatio", Integer.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserCollection, QUserCollection> userCollections = this.<UserCollection, QUserCollection>createList("userCollections", UserCollection.class, QUserCollection.class, PathInits.DIRECT2);

    public QCollection(String variable) {
        this(Collection.class, forVariable(variable), INITS);
    }

    public QCollection(Path<? extends Collection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCollection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCollection(PathMetadata metadata, PathInits inits) {
        this(Collection.class, metadata, inits);
    }

    public QCollection(Class<? extends Collection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.image = inits.isInitialized("image") ? new QImage(forProperty("image")) : null;
    }

}

