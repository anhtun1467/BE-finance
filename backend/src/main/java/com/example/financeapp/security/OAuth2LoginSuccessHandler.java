package com.example.financeapp.security;

import com.example.financeapp.config.JwtUtil;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.UserRepository;
import com.example.financeapp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final String frontendCallbackUrl;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public OAuth2LoginSuccessHandler(String frontendCallbackUrl, JwtUtil jwtUtil, 
                                   UserRepository userRepository, EmailService emailService) {
        this.frontendCallbackUrl = frontendCallbackUrl;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Tạo mật khẩu mặc định theo format: "Google" + <username_from_email> + "@2024!"
     * Ví dụ: john.doe@gmail.com → GoogleJohndoe@2024!
     */
    private String generateDefaultPassword(String email) {
        // Lấy phần username từ email (trước dấu @)
        String username = email.split("@")[0];
        // Xóa các ký tự đặc biệt và chỉ giữ chữ cái, số
        username = username.replaceAll("[^a-zA-Z0-9]", "");
        // Viết hoa chữ cái đầu
        if (username.length() > 0) {
            username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
        }
        return "Google" + username + "@2024!";
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Lấy thông tin user từ Google OAuth2
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        
        // Kiểm tra và lưu user vào database
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        User user;
        
        if (existingUserOpt.isEmpty()) {
            // Tạo user mới nếu chưa tồn tại
            user = new User();
            user.setEmail(email);
            user.setFullName(name != null ? name : "Google User");
            
            // Tạo mật khẩu mặc định
            String defaultPassword = generateDefaultPassword(email);
            user.setPasswordHash(passwordEncoder.encode(defaultPassword));
            user.setHasDefaultPassword(true); // Đánh dấu đang dùng mật khẩu mặc định
            
            user.setProvider("google");
            user.setEnabled(true); // Google đã xác thực rồi
            user.setAvatar(picture);
            
            userRepository.save(user);
            
            // Gửi email thông báo mật khẩu mặc định
            try {
                emailService.sendDefaultPasswordEmail(email, user.getFullName(), defaultPassword);
            } catch (Exception e) {
                System.err.println("[OAuth2] Lỗi khi gửi email mật khẩu mặc định: " + e.getMessage());
                // Không throw exception để không làm gián đoạn luồng đăng nhập
            }
        } else {
            // Cập nhật thông tin nếu cần
            user = existingUserOpt.get();
            
            // Cập nhật avatar nếu có thay đổi
            if (picture != null && !picture.equals(user.getAvatar())) {
                user.setAvatar(picture);
            }
            
            // Cập nhật provider nếu chưa có
            if (user.getProvider() == null || user.getProvider().isEmpty()) {
                user.setProvider("google");
            }
            
            // Đảm bảo tài khoản được enable
            if (!user.isEnabled()) {
                user.setEnabled(true);
            }
            
            userRepository.save(user);
        }
        
        // Tạo JWT token
        String token = jwtUtil.generateToken(email);
        String redirect = frontendCallbackUrl + "?token=" +
                URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirect);
    }
}
