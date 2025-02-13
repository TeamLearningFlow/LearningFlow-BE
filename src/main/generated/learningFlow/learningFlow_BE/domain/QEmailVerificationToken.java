package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmailVerificationToken is a Querydsl query type for EmailVerificationToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailVerificationToken extends EntityPathBase<EmailVerificationToken> {

    private static final long serialVersionUID = -1162432526L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmailVerificationToken emailVerificationToken = new QEmailVerificationToken("emailVerificationToken");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expiryDate = createDateTime("expiryDate", java.time.LocalDateTime.class);

    public final StringPath password = createString("password");

    public final StringPath token = createString("token");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public final BooleanPath verified = createBoolean("verified");

    public QEmailVerificationToken(String variable) {
        this(EmailVerificationToken.class, forVariable(variable), INITS);
    }

    public QEmailVerificationToken(Path<? extends EmailVerificationToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmailVerificationToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmailVerificationToken(PathMetadata metadata, PathInits inits) {
        this(EmailVerificationToken.class, metadata, inits);
    }

    public QEmailVerificationToken(Class<? extends EmailVerificationToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

