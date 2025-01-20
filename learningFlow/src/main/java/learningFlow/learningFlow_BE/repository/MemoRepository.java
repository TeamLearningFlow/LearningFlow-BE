package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, MemoId> {
}
