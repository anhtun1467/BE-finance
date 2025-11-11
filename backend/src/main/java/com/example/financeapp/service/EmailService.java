package com.example.financeapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    // Đăng ký
    public void sendRegistrationVerificationEmail(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Xác minh tài khoản đăng ký");
        msg.setText("Mã xác minh: " + code + "\nHiệu lực 10 phút.");
        mailSender.send(msg);
    }

    // Khôi phục mật khẩu
    public void sendPasswordResetEmail(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Khôi phục mật khẩu");
        msg.setText("Mã xác thực: " + code + "\nHiệu lực 10 phút.\nBỏ qua nếu không yêu cầu.");
        mailSender.send(msg);
    }

    // Gửi mật khẩu mặc định cho Google users
    public void sendDefaultPasswordEmail(String to, String fullName, String defaultPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Thông tin đăng nhập - Finance App");
        msg.setText(String.format(
            "Xin chào %s,\n\n" +
            "Bạn vừa đăng nhập vào Finance App bằng tài khoản Google.\n\n" +
            "Để tiện cho việc đăng nhập lần sau, chúng tôi đã tạo cho bạn một mật khẩu mặc định:\n\n" +
            "Email: %s\n" +
            "Mật khẩu: %s\n\n" +
            "Bạn có thể sử dụng mật khẩu này để đăng nhập trực tiếp hoặc tiếp tục đăng nhập bằng Google.\n\n" +
            "⚠️ Chúng tôi khuyến nghị bạn nên đổi mật khẩu ngay sau khi đăng nhập để bảo mật tài khoản.\n\n" +
            "Trân trọng,\n" +
            "Finance App Team",
            fullName, to, defaultPassword
        ));
        mailSender.send(msg);
    }
}
