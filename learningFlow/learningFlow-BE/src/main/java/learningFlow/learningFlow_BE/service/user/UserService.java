package learningFlow.learningFlow_BE.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import learningFlow.learningFlow_BE.config.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.repository.UserRepository;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 추가 정보 입력 필요 여부와 필드 정보를 반환하는 메소드
    public Map<String, Object> getAdditionalInfoRequirements() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "추가 정보 입력이 필요합니다");
        response.put("requiredFields", Arrays.asList(
                "job", "interestFields", "birthDay", "gender", "preferType"
        ));

        return response;
    }

    @Transactional
    public UserResponseDTO.UserLoginResponseDTO updateAdditionalInfo(
            HttpServletRequest request,
            UserRequestDTO.AdditionalInfoDTO additionalInfo) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException("세션이 없습니다. 먼저 OAuth2 로그인을 진행해주세요.");
        }

        OAuth2UserTemp oauth2UserTemp = (OAuth2UserTemp) session.getAttribute("OAUTH2_USER_TEMP");
        if (oauth2UserTemp == null) {
            throw new RuntimeException("OAuth2 로그인 정보가 없습니다. 먼저 OAuth2 로그인을 진행해주세요.");
        }

        // 새로운 유저 생성
        String loginId = oauth2UserTemp.getSocialType().name() + "_" + oauth2UserTemp.getProviderId();

        User newUser = User.builder()
                .loginId(loginId)
                .email(oauth2UserTemp.getEmail())
                .name(oauth2UserTemp.getName())
                .providerId(oauth2UserTemp.getProviderId())
                .pw("OAUTH2_USER")
                .socialType(oauth2UserTemp.getSocialType())
                .job(additionalInfo.getJob())
                .interestFields(additionalInfo.getInterestFields())
                .birthDay(additionalInfo.getBirthDay())
                .gender(additionalInfo.getGender())
                .preferType(additionalInfo.getPreferType())
                .role(Role.USER)
                .inactive(false)
                .build();

        User savedUser = userRepository.save(newUser);

        session.removeAttribute("OAUTH2_USER_TEMP");

        // 새로운 Authentication 생성 및 설정
        PrincipalDetails principalDetails = new PrincipalDetails(savedUser, oauth2UserTemp.getAttributes());
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return UserResponseDTO.UserLoginResponseDTO.from(savedUser);
    }
}
