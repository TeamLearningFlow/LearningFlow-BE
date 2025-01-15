package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByTokenAndVerifiedFalse(String token);
    void deleteByExpiryDateBefore(LocalDateTime now);
    boolean existsByEmailAndVerifiedFalse(String email);
}
