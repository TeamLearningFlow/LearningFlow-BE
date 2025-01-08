package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = true)
    private String email;

    @Column(name = "provider_id", nullable = true)
    private String providerId;

    @Column(nullable = false)
    private String pw;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Job job;

    @Column(name = "interest_field", nullable = true)
    private String interestField;

    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Lob
    @Column(name = "profile_photo", nullable = true)
    private byte[] profilePhoto;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean inactive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "prefer_type", nullable = false)
    private MediaType preferType;
}
