package learningFlow.learningFlow_BE.service.user;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.UserHandler;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDTO getUserInfo(String loginId) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserInfoDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .job(user.getJob())
                .interestFields(user.getInterestFields())
                .gender(user.getGender())
                .preferType(user.getPreferType())
                .profileImageUrl(user.getImage() != null ? user.getImage().getImageURL() : null)
                .build();
    }

    @Transactional
    public UserInfoDTO updateUserInfo(String loginId, UpdateUserDTO updateUserDTO, MultipartFile imageFile) {
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // TODO: 이미지 업데이트 로직 추가 예정
        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("이미지 업데이트 요청 발생 - 추후 구현 예정");
        }

        // 각 필드가 null이 아닌 경우에만 업데이트
        if (updateUserDTO.getName() != null) {
            user.updateName(updateUserDTO.getName());
        }
        if (updateUserDTO.getJob() != null) {
            user.updateJob(updateUserDTO.getJob());
        }
        if (updateUserDTO.getInterestFields() != null && !updateUserDTO.getInterestFields().isEmpty()) {
            user.updateInterestFields(updateUserDTO.getInterestFields());
        }

        return getUserInfo(loginId);
    }
}