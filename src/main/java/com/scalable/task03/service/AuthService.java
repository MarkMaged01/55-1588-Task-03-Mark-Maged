package com.scalable.task03.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.scalable.task03.model.User;
import com.scalable.task03.model.Role;
import com.scalable.task03.dto.AuthResponse;
import com.scalable.task03.dto.LoginRequest;
import com.scalable.task03.dto.RegisterRequest;
import com.scalable.task03.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // TODO: See Task 3 spec — AuthService.

    public AuthResponse register(RegisterRequest request) {
        // 1. Check if email exists
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // 2. Create and save user
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        // 3. Generate token and return response
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, 3600000L); // 1 hour expiration
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email (returns 401 if not found)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // 2. Verify password (returns 401 with EXACT SAME message if no match)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // 3. Generate token and return response
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, 3600000L);
    }
}
