package learningFlow.learningFlow_BE.service.auth.common;

import learningFlow.learningFlow_BE.repository.EmailVerificationTokenRepository;
import learningFlow.learningFlow_BE.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthTokenCleanupScheduler {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void cleanupExpiredEmailVerificationTokens() {
        emailVerificationTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
