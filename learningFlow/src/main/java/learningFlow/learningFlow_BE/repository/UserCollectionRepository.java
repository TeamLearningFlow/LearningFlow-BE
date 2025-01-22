package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    Optional<UserCollection> findByUserAndCollection(User user, Collection collection);
}
