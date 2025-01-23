package learningFlow.learningFlow_BE.service.memo;

import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import learningFlow.learningFlow_BE.repository.MemoRepository;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemoCommandServiceImpl implements MemoCommandService{
    private final MemoRepository memoRepository;

    @Override
    public Memo saveMemo(String loginId, Long episodeId, MemoRequestDTO.MemoJoinDTO request) {
        MemoId memoId = new MemoId(episodeId, loginId);
        Memo memo = new Memo(memoId, request.getContents()); // 메모가 이미 존재하는 경우도, 존재하지 않는 경우도 새롭게 저장
        return memoRepository.save(memo);
    }
}
