package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.repo.UserRepository;
import com.baduwal.ecommerce.utils.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
public class UserServiceImpl implements UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailUtil emailUtil;

    public static final int OTP_VALID_DURATION = 5; // minutes

    @Override
    public boolean registerUser(User user) {

        // 🔥 1. Check duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        // 🔥 2. Check duplicate phone only if provided
        if (user.getPhoneNumber() != null &&
                userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return false;
        }

        // 🔥 3. Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 🔥 4. Generate OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setTokenExpiration(generateOtpTime());
        user.setVerified(false);

        // 🔥 5. Save user
        userRepository.save(user);

        // 🔥 6. Send OTP email
        String body = "Your verification OTP is: " + otp + "\nIt is valid for 5 minutes.";
        emailUtil.sendEmail(user.getEmail(), "Verify your email", body);

        return true;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userByEmail = userRepository.findByEmail(email);

        if (userByEmail.isEmpty()) return false;

        User user = userByEmail.get();

        if (user.getOtp() != null &&
                user.getOtp().equals(otp) &&
                user.getTokenExpiration() != null &&
                user.getTokenExpiration().isAfter(LocalDateTime.now())) {

            user.setVerified(true);
            user.setOtp(null);
            user.setTokenExpiration(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public boolean setNewPassword(String email, String otp, String newPassword) {

        // 🔥 Must validate OTP first
        if (!verifyOtp(email, otp)) {
            return false;
        }

        Optional<User> userByEmail = userRepository.findByEmail(email);

        if (userByEmail.isEmpty()) return false;

        User user = userByEmail.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    @Override
    public String changePassword(String oldPassword, String newPassword, User user) {

        try {
            Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());

            if (userByEmail.isEmpty()) {
                return "User not found";
            }

            User existingUser = userByEmail.get();

            // 🔥 Compare old password
            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                return "Invalid Old Password";
            }

            // 🔥 Validate new password
            String validation = validatePasswordStrength(newPassword);
            if (!validation.equals("Valid")) return validation;

            // 🔥 New password should not match old
            if (passwordEncoder.matches(newPassword, existingUser.getPassword())) {
                return "New password must be different from old password";
            }

            // 🔥 Update password
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(existingUser);

            return "Password changed successfully";

        } catch (Exception e) {
            log.error("Error changing password for user: {}", user.getEmail(), e);
            return "Error changing password";
        }
    }

    // ===========================================================
    // Helper Functions
    // ===========================================================

    private Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Optional<User> findUserByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private LocalDateTime generateOtpTime() {
        return LocalDateTime.now().plusMinutes(OTP_VALID_DURATION);
    }

    private String validatePasswordStrength(String password) {

        if (password == null || password.trim().isEmpty())
            return "Password cannot be empty";

        if (password.length() < 8)
            return "Password must be at least 8 characters";

        if (!password.matches(".*[A-Z].*"))
            return "Password must contain an uppercase letter";

        if (!password.matches(".*[a-z].*"))
            return "Password must contain a lowercase letter";

        if (!password.matches(".*\\d.*"))
            return "Password must contain a digit";

        if (!password.matches(".*[@$!%*&].*"))
            return "Password must contain a special character";

        return "Valid";
    }

    @Override
    public void sendOtp(User user) {
        String otp = generateOtp();
        user.setOtp(otp);
        user.setTokenExpiration(generateOtpTime());
        userRepository.save(user);

        String body = "Your verification OTP is: " + otp + "\nIt is valid for 5 minutes.";
        emailUtil.sendEmail(user.getEmail(), "Verify your email", body);
    }

}

