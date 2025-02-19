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

    public static final QCollection collection = new QCollection("collection");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Integer> bookmarkCount = createNumber("bookmarkCount", Integer.class);

    public final StringPath collectionImgUrl = createString("collectionImgUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath creator = createString("creator");

    public final StringPath detailInformation = createString("detailInformation");

    public final ListPath<Integer, NumberPath<Integer>> difficulty = this.<Integer, NumberPath<Integer>>createList("difficulty", Integer.class, NumberPath.class, PathInits.DIRECT2);

    public final ListPath<CollectionEpisode, QCollectionEpisode> episodes = this.<CollectionEpisode, QCollectionEpisode>createList("episodes", CollectionEpisode.class, QCollectionEpisode.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField> interestField = createEnum("interestField", learningFlow.learningFlow_BE.domain.enums.InterestField.class);

    public final ListPath<String, StringPath> keywords = this.<String, StringPath>createList("keywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> resourceTypeRatio = createNumber("resourceTypeRatio", Integer.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserCollection, QUserCollection> userCollections = this.<UserCollection, QUserCollection>createList("userCollections", UserCollection.class, QUserCollection.class, PathInits.DIRECT2);

    public QCollection(String variable) {
        super(Collection.class, forVariable(variable));
    }

    public QCollection(Path<? extends Collection> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCollection(PathMetadata metadata) {
        super(Collection.class, metadata);
    }

}

