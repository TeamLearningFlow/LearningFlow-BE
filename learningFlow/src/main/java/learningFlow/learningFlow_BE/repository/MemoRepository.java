package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemoRepository extends JpaRepository<Memo, MemoId> {
    @Query("SELECT m FROM Memo m WHERE m.id.collectionEpisodeId = :episodeId")
    Memo findByEpisodeId(@Param("episodeId") Long episodeId);
}
