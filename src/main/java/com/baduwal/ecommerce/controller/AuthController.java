package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.repo.UserRepository;
import com.baduwal.ecommerce.security.JwtUtil;
import com.baduwal.ecommerce.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository UserRepository,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepository = UserRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    // ============= Request/Response Records =============
    record SignupRequest(
            @NotBlank(message = "Name is required") String name,
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "Password is required") String password
    ) {}


    record LoginRequest(
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "Password is required") String password
    ) {}

    record VerifyOtpRequest(
            @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "OTP is required") String otp
    ) {}

    record SetPasswordRequest(
            @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "OTP is required") String otp,
            @NotBlank(message = "Password is required") String password
    ) {}

    record ChangePasswordRequest(
            @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Old password is required") String oldPassword,
            @NotBlank(message = "New password is required") String newPassword
    ) {}

    // ============= Authentication Endpoints =============
    /*
     * Register a new user and send OTP for email verification
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        // Check if email already exists
        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success",false,
                                    "message", "Email already in use"));
        }

        // Create user with encoded password
        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(req.password);

        // Register user(this should send otp)
        boolean isRegistered = userService.registerUser(user);

        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success",true,
                            "message", "User registered successfully. Please verify your email with OPT sent.",
                            "email", user.getEmail()
                    ));
        }

       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(Map.of(
                       "success",false,
                       "message","Failed to register user. Please try again."
               ));
    }

    /**
     * Verify OTP sent to user's email
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        boolean verified = userService.verifyOtp(req.email(), req.otp());

        if (verified) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email verified successfully. You can now login."
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", "Invalid OTP or OTP expired"
                ));
    }



    /**
     * Login user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            System.out.println("=== BEFORE AUTHENTICATION ===");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );

            System.out.println("=== AFTER AUTHENTICATION ===");

            User user = (User) authentication.getPrincipal();
            System.out.println("User ID: " + user.getId());
            System.out.println("User Name: " + user.getName());
            System.out.println("User Email: " + user.getEmail());

            String token = jwtUtil.generateJwtToken(req.email());
            System.out.println("Token generated: " + token.substring(0, 20) + "...");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail()
                    )
            ));
        } catch (BadCredentialsException e) {
            if (e.getMessage().contains("not verified")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", "Email not verified. Please verify your email first.",
                                "requiresVerification", true
                        ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred during login"));
        }
    }



    // ============= Password Management Endpoints =============
    /*
     * Request password reset (sends OTP to email)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Email is required"
                    ));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Don't reveal if email exists for security reasons
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "If the email exists, an OTP has been sent."
            ));
        }

        // Trigger OTP generation and sending
        userService.sendOtp(user); // Reuse the OTP sending logic

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "If the email exists, an OTP has been sent."
        ));
    }

    /**
     * Set new password using OTP (for forgot password flow)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody SetPasswordRequest req) {
        boolean success = userService.setNewPassword(req.email(), req.otp(), req.password());

        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password reset successfully. You can now login with your new password."
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", "Invalid OTP or OTP expired"
                ));
    }

    /**
     * Change password for authenticated user (requires old password)
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        User user = new User();
        user.setEmail(req.email());

        String result = userService.changePassword(req.oldPassword(), req.newPassword(), user);

        if ("Password changed successfully".equals(result)) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", result
                ));
    }

    /**
     * Resend OTP for email verification
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Email is required"
                    ));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "User not found"
                    ));
        }

        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Email already verified"
                    ));
        }

        boolean otpSent = userService.registerUser(user);

        if (otpSent) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP resent successfully"
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "success", false,
                        "message", "Failed to resend OTP"
                ));
    }

}
