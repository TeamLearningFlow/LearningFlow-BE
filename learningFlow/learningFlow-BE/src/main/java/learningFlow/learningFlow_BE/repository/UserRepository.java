package learningFlow.learningFlow_BE.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import learningFlow.learningFlow_BE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@Email(message = "올바른 이메일 형식이어야 합니다") @NotBlank(message = "이메일은 필수 입력값입니다") String email);
}
