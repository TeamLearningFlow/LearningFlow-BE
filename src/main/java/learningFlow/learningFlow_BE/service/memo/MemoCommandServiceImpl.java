package learningFlow.learningFlow_BE.service.memo;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;
import learningFlow.learningFlow_BE.domain.Memo;
import learningFlow.learningFlow_BE.domain.MemoId;
import learningFlow.learningFlow_BE.repository.MemoRepository;
import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoCommandServiceImpl implements MemoCommandService{
    private final MemoRepository memoRepository;

    @Override
    public Memo saveMemo(String loginId, Long episodeId, MemoRequestDTO.MemoJoinDTO request) {
        // 입력값 검증
        if (request.getContents() == null || request.getContents().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.MEMO_SAVE_FAILED);
        }
        MemoId memoId = new MemoId(episodeId, loginId);
        Memo memo = new Memo(memoId, request.getContents()); // 메모가 이미 존재하는 경우도, 존재하지 않는 경우도 새롭게 저장
        try {
            return memoRepository.save(memo);
        } catch (DataIntegrityViolationException e) {
            log.error("메모 저장 중 데이터베이스 오류 발생 - loginId={}, episodeId={}, error={}",
                    loginId, episodeId, e.getMessage());
            throw new GeneralException(ErrorStatus.MEMO_SAVE_FAILED, "메모 저장 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("메모 저장 중 알 수 없는 오류 발생 - loginId={}, episodeId={}, error={}",
                    loginId, episodeId, e.getMessage());
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
    }
}
