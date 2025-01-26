package learningFlow.learningFlow_BE.repository.collection;

import learningFlow.learningFlow_BE.domain.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long>, CollectionRepositoryCustom {
    List<Collection> findByIdIn(List<Long> ids);
}
