package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
