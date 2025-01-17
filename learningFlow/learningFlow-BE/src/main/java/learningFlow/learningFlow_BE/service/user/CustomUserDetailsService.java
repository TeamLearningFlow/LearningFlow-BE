package learningFlow.learningFlow_BE.service.user;

import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService - loadUserByUsername 호출됨: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: {}", username);
                    return new UsernameNotFoundException("해당 이메일을 가진 유저가 존재하지 않습니다: " + username);
                });

        return new PrincipalDetails(user);
    }
}
