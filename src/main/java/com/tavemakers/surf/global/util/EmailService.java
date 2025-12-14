package com.tavemakers.surf.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);        // 수신자 이메일
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
