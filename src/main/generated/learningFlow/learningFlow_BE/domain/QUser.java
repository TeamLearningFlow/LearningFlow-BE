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

    public static final QUser user = new QUser("user");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<Long, NumberPath<Long>> bookmarkedCollectionIds = this.<Long, NumberPath<Long>>createList("bookmarkedCollectionIds", Long.class, NumberPath.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final BooleanPath inactive = createBoolean("inactive");

    public final ListPath<learningFlow.learningFlow_BE.domain.enums.InterestField, EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField>> interestFields = this.<learningFlow.learningFlow_BE.domain.enums.InterestField, EnumPath<learningFlow.learningFlow_BE.domain.enums.InterestField>>createList("interestFields", learningFlow.learningFlow_BE.domain.enums.InterestField.class, EnumPath.class, PathInits.DIRECT2);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.Job> job = createEnum("job", learningFlow.learningFlow_BE.domain.enums.Job.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath name = createString("name");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.MediaType> preferType = createEnum("preferType", learningFlow.learningFlow_BE.domain.enums.MediaType.class);

    public final StringPath profileImgUrl = createString("profileImgUrl");

    public final StringPath providerId = createString("providerId");

    public final StringPath pw = createString("pw");

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.Role> role = createEnum("role", learningFlow.learningFlow_BE.domain.enums.Role.class);

    public final EnumPath<learningFlow.learningFlow_BE.domain.enums.SocialType> socialType = createEnum("socialType", learningFlow.learningFlow_BE.domain.enums.SocialType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserCollection, QUserCollection> userCollections = this.<UserCollection, QUserCollection>createList("userCollections", UserCollection.class, QUserCollection.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

