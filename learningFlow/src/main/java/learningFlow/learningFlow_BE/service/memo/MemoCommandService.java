package learningFlow.learningFlow_BE.service.memo;

import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;

public interface MemoCommandService {
    public void saveMemo(String loginId,Long episodeId, MemoRequestDTO.MemoJoinDTO request);
}
