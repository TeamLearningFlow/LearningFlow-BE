package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    Optional<UserCollection> findByUserAndCollection(User user, Collection collection);
    List<UserCollection> findByUserAndStatusOrderByUpdatedAtDesc(User user, UserCollectionStatus status);
    Optional<UserCollection> findFirstByUserAndStatusOrderByUpdatedAtDesc(User user, UserCollectionStatus status);

    @Modifying
    @Query("DELETE FROM UserCollection uc WHERE uc.user.loginId = :loginId")
    void deleteAllByUserId(@Param("loginId") String loginId);
}