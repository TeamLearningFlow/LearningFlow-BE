package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.UserEpisodeProgress;
import learningFlow.learningFlow_BE.domain.UserEpisodeProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserEpisodeProgressRepository extends JpaRepository<UserEpisodeProgress, UserEpisodeProgressId> {
    @Modifying
    @Query("DELETE FROM UserEpisodeProgress uep WHERE uep.id.userId = :loginId")
    void deleteAllByUserId(@Param("loginId") String loginId);
}