package learningFlow.learningFlow_BE.repository.collection;

import learningFlow.learningFlow_BE.domain.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long>, CollectionRepositoryCustom {
    List<Collection> findByIdIn(List<Long> ids);

    //N+1 문제 개선을 위한 패치 조인 쿼리
    @Query("SELECT DISTINCT c FROM Collection c " +
            "LEFT JOIN FETCH c.episodes e " +
            "LEFT JOIN FETCH e.resource " +
            "WHERE c.id = :collectionId")
    Optional<Collection> findByIdWithEpisodesAndResources(@Param("collectionId") Long collectionId);

    @Query("SELECT DISTINCT c FROM Collection c " +
            "LEFT JOIN FETCH c.episodes e " +
            "LEFT JOIN FETCH e.resource " +
            "WHERE c.id IN :collectionIds")
    List<Collection> findByIdInWithEpisodesAndResources(@Param("collectionIds") List<Long> collectionIds);

}
