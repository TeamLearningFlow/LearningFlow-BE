package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import java.util.List;
import java.util.Optional;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    Optional<UserCollection> findByUserAndCollection(User user, Collection collection);
    List<UserCollection> findByUserAndStatusOrderByLastAccessedAtDesc(User user, UserCollectionStatus status);

    Optional<UserCollection> findFirstByUserAndStatusOrderByLastAccessedAtDesc(User user, UserCollectionStatus status);
}