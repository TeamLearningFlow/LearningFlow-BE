package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1386433701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DatePath<java.time.LocalDate> birthDay = createDate("birthDay", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.Gender> gender = createEnum("gender", learningFlow.learningFlow_BE.domain.enums.Gender.class);

    public final QImage image;

    public final BooleanPath inactive = createBoolean("inactive");

    public final ListPath<learningFlow.learningFlow_BE.domain.enums.InterestField, EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField>> interestFields = this.<learningFlow.learningFlow_BE.domain.enums.InterestField, EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField>>createList("interestFields", learningFlow.learningFlow_BE.domain.enums.InterestField.class, EnumPath.class, PathInits.DIRECT2);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.Job> job = createEnum("job", learningFlow.learningFlow_BE.domain.enums.Job.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath name = createString("name");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.MediaType> preferType = createEnum("preferType", learningFlow.learningFlow_BE.domain.enums.MediaType.class);

    public final StringPath providerId = createString("providerId");

    public final StringPath pw = createString("pw");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.Role> role = createEnum("role", learningFlow.learningFlow_BE.domain.enums.Role.class);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.SocialType> socialType = createEnum("socialType", learningFlow.learningFlow_BE.domain.enums.SocialType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserCollection, QUserCollection> userCollections = this.<UserCollection, QUserCollection>createList("userCollections", UserCollection.class, QUserCollection.class, PathInits.DIRECT2);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.image = inits.isInitialized("image") ? new QImage(forProperty("image")) : null;
    }

}

