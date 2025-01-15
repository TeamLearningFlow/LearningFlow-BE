package learningFlow.learningFlow_BE.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.url}")
    private String baseUrl;

    public void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[OnBoarding] 비밀번호 재설정");
            message.setText(
                    "안녕하세요, OnBoarding입니다.\n\n" +
                            "비밀번호 재설정을 위해 아래 링크를 클릭해주세요:\n\n" +
                            baseUrl + "/reset-password?token=" + token + "\n\n" +
                            "이 링크는 24시간 동안 유효합니다.\n" +
                            "본인이 요청하지 않은 경우 이 이메일을 무시해주세요."
            );

            emailSender.send(message);
            log.info("비밀번호 재설정 이메일 발송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }
}
