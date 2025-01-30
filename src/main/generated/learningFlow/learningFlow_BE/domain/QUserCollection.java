package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCollection is a Querydsl query type for UserCollection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCollection extends EntityPathBase<UserCollection> {

    private static final long serialVersionUID = -1884551079L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCollection userCollection = new QUserCollection("userCollection");

    public final QCollection collection;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastAccessedAt = createDate("lastAccessedAt", java.time.LocalDate.class);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus> status = createEnum("status", learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus.class);

    public final QUser user;

    public final NumberPath<Integer> userCollectionStatus = createNumber("userCollectionStatus", Integer.class);

    public QUserCollection(String variable) {
        this(UserCollection.class, forVariable(variable), INITS);
    }

    public QUserCollection(Path<? extends UserCollection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCollection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCollection(PathMetadata metadata, PathInits inits) {
        this(UserCollection.class, metadata, inits);
    }

    public QUserCollection(Class<? extends UserCollection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collection = inits.isInitialized("collection") ? new QCollection(forProperty("collection")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

