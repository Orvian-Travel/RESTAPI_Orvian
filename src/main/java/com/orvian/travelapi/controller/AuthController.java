package com.orvian.travelapi.controller;

import com.orvian.travelapi.annotation.Password;
import com.orvian.travelapi.controller.dto.auth.LoginRequestDTO;
import com.orvian.travelapi.controller.dto.auth.LoginResponseDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.PasswordResetService;
import com.orvian.travelapi.service.exception.NotFoundException;
import com.orvian.travelapi.service.security.TokenService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        log.info("Login request received with email: {}", dto.email());
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + dto.email()));
        if (passwordEncoder.matches(dto.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(token, user.getName()));
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam @Email String email) {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String token,
            @RequestParam @Password String newPassword
    ) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }
}
