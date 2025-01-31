package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.UserEpisodeProgress;
import learningFlow.learningFlow_BE.domain.UserEpisodeProgressId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEpisodeProgressRepository extends JpaRepository<UserEpisodeProgress, UserEpisodeProgressId> {
}
