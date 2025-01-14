package learningFlow.learningFlow_BE.service.user;

import learningFlow.learningFlow_BE.domain.enums.SocialType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class OAuth2UserTemp implements OAuth2User {
    private Map<String, Object> attributes;
    private String email;
    private String name;
    private String providerId;
    private SocialType socialType;
    private boolean isNewUser = true;  // 새로운 사용자임을 나타내는 플래그

    public OAuth2UserTemp(Map<String, Object> attributes, String email,
                          String name, String providerId, SocialType socialType) {
        this.attributes = attributes;
        this.email = email;
        this.name = name;
        this.providerId = providerId;
        this.socialType = socialType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return name;
    }
}
