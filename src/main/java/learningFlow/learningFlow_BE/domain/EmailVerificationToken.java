package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_verification_token")
public class EmailVerificationToken extends BaseEntity {

    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)  // optional = true 제거
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
