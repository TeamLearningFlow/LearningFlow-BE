package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, MemoId> {
    @Query("SELECT m FROM Memo m WHERE m.id.collectionEpisodeId = :episodeId")
    Optional<Memo> findByEpisodeId(@Param("episodeId") Long episodeId);

    @Modifying
    @Query("DELETE FROM Memo m WHERE m.id.userId = :loginId")
    void deleteAllByUserId(@Param("loginId") String loginId);
}