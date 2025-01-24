package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    List<UserCollection> findByUserAndStatusOrderByLastAccessedAtDesc(User user, UserCollectionStatus status);
}
