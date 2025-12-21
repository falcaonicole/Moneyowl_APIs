package com.finance.moneyowl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SentOTPEmailServiceImpl {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("moneyowl11@gmail.com");
        message.setTo(toEmail);
        message.setSubject("MoneyOWL Registration Email Verification OTP");
        message.setText("Your OTP for MoneyOwl verification is: " + otp);

        mailSender.send(message);
    }
}
