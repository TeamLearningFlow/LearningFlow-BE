package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.web.dto.memo.MemoRequestDTO;
import learningFlow.learningFlow_BE.web.dto.memo.MemoResponseDTO;

public class MemoConverter {
    public static MemoResponseDTO.MemoInfoDTO createMemo(MemoRequestDTO.MemoJoinDTO request){
        return MemoResponseDTO.MemoInfoDTO.builder()
                .memoContents(request.getContents())
                .build();
    }
}
