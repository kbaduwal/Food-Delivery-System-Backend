package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.controller.AuthController;
import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        // For demo: return first user in DB, or create a demo user if none exists.
//        Optional<User> u = userRepository.findAll().stream().findFirst();
//        if (u.isPresent()) {
//            return u.get();
//        }
//
//        //Demo purpose adjustment is done in User class
//        User demo = new User("Demo User", "demo@example.com", "password");
//        return userRepository.save(demo);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String &&((String)auth.getPrincipal()).equals("anonymousUser")) {
            // fallback for demo: return first user
            return userRepository.findAll().stream().findFirst().orElseThrow();
        }

        String email = auth.getName();
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow();
    }
}
