package learningFlow.learningFlow_BE.service.memo;

import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import learningFlow.learningFlow_BE.repository.MemoRepository;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoCommandServiceImpl implements MemoCommandService{
    private final MemoRepository memoRepository;

    @Override
    public void saveMemo(String loginId, Long episodeId, MemoRequestDTO.MemoJoinDTO request) {
        MemoId memoId = new MemoId(episodeId, loginId);
        Memo memo = new Memo(memoId, request.getContents());

        memoRepository.save(memo);
    }
}
