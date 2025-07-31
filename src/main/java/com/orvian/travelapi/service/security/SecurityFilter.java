package com.orvian.travelapi.service.security;

import com.auth0.jwt.JWT;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        log.info("DEBUG: Token recovered: {}", token != null ? "TOKEN_EXISTS" : "NO_TOKEN");

        var login = tokenService.validateToken(token);
        log.info("DEBUG: Login from token: {}", login);

        if (login != null) {
            User user = userRepository.findByEmail(login).orElseThrow(() -> new NotFoundException("User not found with email: " + login));
            String role = JWT.decode(token).getClaim("role").asString();

            // ✅ DEBUG: Verificar dados extraídos
            log.info("DEBUG: User from DB - Email: {}, Role: {}", user.getEmail(), user.getRole());
            log.info("DEBUG: Role from token: {}", role);

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            log.info("DEBUG: Authorities created: {}", authorities);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("DEBUG: Authentication set in SecurityContext");
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
