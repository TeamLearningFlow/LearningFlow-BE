package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    List<UserCollection> findByUserAndStatusOrderByLastAccessedAtDesc(User user, UserCollectionStatus status);

    @Query("SELECT uc FROM UserCollection uc WHERE uc.user = :user ORDER BY uc.lastAccessedAt DESC")
    List<UserCollection> findRecentByUser(@Param("user") User user, Pageable pageable);
}