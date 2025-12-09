package com.baduwal.ecommerce.security;

import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("=== AUTHENTICATION ATTEMPT ===");
        System.out.println("Email: " + email);
        System.out.println("Password provided: " + password);

        // Single database query
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found");
                    return new BadCredentialsException("Invalid email or password");
                });

        System.out.println("✅ User found: " + user.getEmail());
        System.out.println("User verified: " + user.isVerified());
        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Password starts with $2a$: " + user.getPassword().startsWith("$2a$"));

        // Check if user is verified
        if (!user.isVerified()) {
            System.out.println("❌ User not verified");
            throw new BadCredentialsException("Email not verified");
        }

        // Check password
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Password matches: " + passwordMatches);

        if (!passwordMatches) {
            System.out.println("❌ Password does not match");
            throw new BadCredentialsException("Invalid email or password");
        }

        System.out.println("✅ Authentication successful!");

        return new UsernamePasswordAuthenticationToken(
                user,
                password,
                new ArrayList<>()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}