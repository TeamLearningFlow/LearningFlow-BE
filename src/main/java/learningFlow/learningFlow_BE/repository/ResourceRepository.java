package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
