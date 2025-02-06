package learningFlow.learningFlow_BE.domain.uuid;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUuid is a Querydsl query type for Uuid
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUuid extends EntityPathBase<Uuid> {

    private static final long serialVersionUID = 1905925310L;

    public static final QUuid uuid1 = new QUuid("uuid1");

    public final learningFlow.learningFlow_BE.domain.QBaseEntity _super = new learningFlow.learningFlow_BE.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath uuid = createString("uuid");

    public QUuid(String variable) {
        super(Uuid.class, forVariable(variable));
    }

    public QUuid(Path<? extends Uuid> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUuid(PathMetadata metadata) {
        super(Uuid.class, metadata);
    }

}

