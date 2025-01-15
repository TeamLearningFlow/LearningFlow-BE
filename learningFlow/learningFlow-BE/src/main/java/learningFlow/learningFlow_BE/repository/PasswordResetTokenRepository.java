package learningFlow.learningFlow_BE.repository;

import learningFlow.learningFlow_BE.domain.PasswordResetToken;
import learningFlow.learningFlow_BE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime date);
    Optional<PasswordResetToken> findByUser(User user);
}
