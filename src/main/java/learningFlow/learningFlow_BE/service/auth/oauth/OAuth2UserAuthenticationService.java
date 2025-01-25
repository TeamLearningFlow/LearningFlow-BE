package learningFlow.learningFlow_BE.service.auth.oauth;

import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.*;
import learningFlow.learningFlow_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserAuthenticationService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.valueOf(registrationId.toUpperCase());

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        // 이메일로 기존 유저 찾기
        Optional<User> existingUser = userRepository.findByEmail(email);

        // 기존 유저가 있는 경우 바로 반환
        if (existingUser.isPresent()) {
            return new PrincipalDetails(existingUser.get(), oAuth2User.getAttributes());
        }

        // 새로운 유저의 경우 임시 정보만 담은 Principal 반환
        return new OAuth2UserTemp(oAuth2User.getAttributes(), email, name, providerId, socialType);
    }
}
