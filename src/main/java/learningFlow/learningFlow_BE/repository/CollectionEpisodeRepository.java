package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionEpisodeRepository extends JpaRepository<CollectionEpisode, Long> {
}