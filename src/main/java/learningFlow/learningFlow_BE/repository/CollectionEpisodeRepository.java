package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectionEpisodeRepository extends JpaRepository<CollectionEpisode, Long> {

    List<CollectionEpisode> findByCollection(Collection collection);
}