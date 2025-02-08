package learningFlow.learningFlow_BE.service.auth.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationEmailService {

    private final JavaMailSender emailSender;

    @Value("${app.frontend-url}")
    private String baseUrl;

    public void sendVerificationEmail(String email, String token) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OnBoarding] 이메일 인증");

            String htmlContent = """
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <title></title>
                <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                <meta name="viewport" content="width=device-width" />
                <style type="text/css">
                    @media only screen and (min-width: 620px) {
                        .wrapper { min-width: 600px !important; }
                    }
                    body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%%; }
                    .wrapper { background-color: #f0f0f0; }
                    .header { background-color: #5e52ff; }
                    .btn { 
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #5e52ff;
                        color: #ffffff;
                        text-decoration: none;
                        border-radius: 4px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <table class="wrapper" style="border-collapse: collapse; width: 100%%;">
                    <tr>
                        <td align="center">
                            <div style="max-width: 600px; margin: 0 auto;">
                                <div style="text-align: center; padding: 20px;">
                                    <img src="https://i.imgur.com/qqvcW0H.jpg" alt="OnBoarding" style="max-width: 369px; width: 100%%;"/>
                                </div>
                                
                                <div style="background-color: #ffffff; padding: 40px 20px; text-align: center;">
                                    <h1 style="color: #565656; font-size: 28px; margin-bottom: 20px;">
                                        회원가입을 위해 메일을 인증해주세요
                                    </h1>
                                    
                                    <p style="color: #787778; font-size: 16px; line-height: 24px; margin-bottom: 30px;">
                                        안녕하세요, OnBoarding입니다.<br/>
                                        회원가입을 완료하기 위해 메일을 인증해주세요.<br/>
                                        버튼을 누르면 자동으로 인증 후 추가 정보 입력 페이지로 이동합니다.
                                    </p>
                                    
                                    <a href="%s/register/complete?token=%s" 
                                       class="btn" 
                                       style="background-color: #5e52ff; color: #ffffff; text-decoration: none; padding: 12px 24px; border-radius: 4px; font-weight: bold; display: inline-block; margin: 20px 0;">
                                        이메일 인증하기
                                    </a>
                                    
                                    <p style="color: #787778; font-size: 14px; font-style: italic; margin-top: 30px;">
                                        이 메일은 24시간 동안 유효합니다.<br/>
                                        본인이 요청하지 않은 경우, 이 메일을 무시해주세요.
                                    </p>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(baseUrl, token);

            helper.setText(htmlContent, true);
            emailSender.send(message);
            log.info("이메일 인증 메일 발송 완료: {}", email);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    public void sendPasswordResetEmail(String email, String token) {

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OnBoarding] 비밀번호 재설정");

            String htmlContent = """
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <title></title>
                <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                <meta name="viewport" content="width=device-width" />
                <style type="text/css">
                    @media only screen and (min-width: 620px) {
                        .wrapper { min-width: 600px !important; }
                    }
                    body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%%; }
                    .wrapper { background-color: #f0f0f0; }
                    .header { background-color: #5e52ff; }
                    .btn { 
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #5e52ff;
                        color: #ffffff;
                        text-decoration: none;
                        border-radius: 4px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <table class="wrapper" style="border-collapse: collapse; width: 100%%;">
                    <tr>
                        <td align="center">
                            <div style="max-width: 600px; margin: 0 auto;">
                                <div style="text-align: center; padding: 20px;">
                                    <img src="https://i.imgur.com/qqvcW0H.jpg" alt="OnBoarding" style="max-width: 369px; width: 100%%;"/>
                                </div>
                                
                                <div style="background-color: #ffffff; padding: 40px 20px; text-align: center;">
                                    <h1 style="color: #565656; font-size: 28px; margin-bottom: 20px;">
                                        비밀번호 재설정을 위해 메일을 인증해주세요
                                    </h1>
                                    
                                    <p style="color: #787778; font-size: 16px; line-height: 24px; margin-bottom: 30px;">
                                        안녕하세요, OnBoarding입니다.<br/>
                                        비밀번호 재설정을 위해 메일을 인증해주세요<br/>
                                        버튼을 누르면 자동으로 인증 후 비밀번호 재설정 페이지로 이동합니다.
                                    </p>
                                    
                                    <a href="%s/change-password?token=%s" 
                                       class="btn" 
                                       style="background-color: #5e52ff; color: #ffffff; text-decoration: none; padding: 12px 24px; border-radius: 4px; font-weight: bold; display: inline-block; margin: 20px 0;">
                                        이메일 인증하기
                                    </a>
                                    
                                    <p style="color: #787778; font-size: 14px; font-style: italic; margin-top: 30px;">
                                        이 메일은 24시간 동안 유효합니다.<br/>
                                        본인이 요청하지 않은 경우, 이 메일을 무시해주세요.
                                    </p>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(baseUrl, token);

            helper.setText(htmlContent, true);
            emailSender.send(message);
            log.info("이메일 인증 메일 발송 완료: {}", email);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }
}