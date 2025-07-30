package com.orvian.travelapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orvian.travelapi.controller.dto.auth.LoginRequestDTO;
import com.orvian.travelapi.controller.dto.auth.LoginResponseDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.exception.NotFoundException;
import com.orvian.travelapi.service.security.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        log.info("Login request received with email: {}", dto.email());

        try {
            // Buscar usuário
            User user = userRepository.findByEmail(dto.email())
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", dto.email());
                        return new NotFoundException("User not found with email: " + dto.email());
                    });

            log.info("User found: {}, role: {}", user.getName(), user.getRole());

            // Verificar senha
            if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
                log.warn("Invalid password for user: {}", dto.email());
                throw new BadCredentialsException("Invalid credentials");
            }

            log.info("Password validated successfully for user: {}", dto.email());

            // Gerar token
            String token = tokenService.generateToken(user);
            log.info("Token generated successfully for user: {}", dto.email());

            return ResponseEntity.ok(new LoginResponseDTO(token, user.getName()));

        } catch (NotFoundException | BadCredentialsException e) {
            log.error("Authentication failed for email: {} - Reason: {}", dto.email(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {} - Error: {}", dto.email(), e.getMessage(), e);
            throw new RuntimeException("Login process failed", e);
        }
    }
}
