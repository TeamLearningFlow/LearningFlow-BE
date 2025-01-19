package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailVerificationToken is a Querydsl query type for EmailVerificationToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailVerificationToken extends EntityPathBase<EmailVerificationToken> {

    private static final long serialVersionUID = -1162432526L;

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

    public final BooleanPath verified = createBoolean("verified");

    public QEmailVerificationToken(String variable) {
        super(EmailVerificationToken.class, forVariable(variable));
    }

    public QEmailVerificationToken(Path<? extends EmailVerificationToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailVerificationToken(PathMetadata metadata) {
        super(EmailVerificationToken.class, metadata);
    }

}

