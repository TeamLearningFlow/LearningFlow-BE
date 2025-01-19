package learningFlow.learningFlow_BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCollectionEpisode is a Querydsl query type for CollectionEpisode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCollectionEpisode extends EntityPathBase<CollectionEpisode> {

    private static final long serialVersionUID = -297427987L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollectionEpisode collectionEpisode = new QCollectionEpisode("collectionEpisode");

    public final QCollection collection;

    public final StringPath episodeName = createString("episodeName");

    public final NumberPath<Integer> episodeNumber = createNumber("episodeNumber", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QResource resource;

    public QCollectionEpisode(String variable) {
        this(CollectionEpisode.class, forVariable(variable), INITS);
    }

    public QCollectionEpisode(Path<? extends CollectionEpisode> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCollectionEpisode(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCollectionEpisode(PathMetadata metadata, PathInits inits) {
        this(CollectionEpisode.class, metadata, inits);
    }

    public QCollectionEpisode(Class<? extends CollectionEpisode> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collection = inits.isInitialized("collection") ? new QCollection(forProperty("collection"), inits.get("collection")) : null;
        this.resource = inits.isInitialized("resource") ? new QResource(forProperty("resource")) : null;
    }

}

