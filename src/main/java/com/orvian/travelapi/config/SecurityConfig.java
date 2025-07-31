package com.orvian.travelapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.orvian.travelapi.service.security.SecurityFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                // ===== ENDPOINTS PÚBLICOS =====
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Autenticação e registro público
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                // Health check e documentação
                .requestMatchers("/api/v1/health/**").permitAll()
                .requestMatchers(getDocumentationEndpoints()).permitAll()
                // ===== PACKAGES - VISUALIZAÇÃO PÚBLICA =====
                // Consulta de pacotes disponível para todos (incluindo não autenticados)
                .requestMatchers(HttpMethod.GET, "/api/v1/packages").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/packages/**").permitAll()
                // ===== OPERAÇÕES ADMINISTRATIVAS - SOMENTE ADMIN =====
                // Gestão completa de usuários - apenas ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                // Gestão de pacotes - apenas ADMIN pode modificar
                .requestMatchers(HttpMethod.POST, "/api/v1/packages/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/packages/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/packages/**").hasRole("ADMIN")
                // ===== VISUALIZAÇÕES - ADMIN E ATENDENTE =====
                // Listagem de usuários - ADMIN e ATENDENTE podem visualizar
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole("ADMIN", "ATENDENTE")
                .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("ADMIN", "ATENDENTE")
                // Visualização de reservas de outros - ADMIN e ATENDENTE
                .requestMatchers(HttpMethod.GET, "/api/v1/reservations").hasAnyRole("ADMIN", "ATENDENTE")
                .requestMatchers(HttpMethod.GET, "/api/v1/reservations/{id}").hasAnyRole("ADMIN", "ATENDENTE")
                // Visualização de pagamentos de outros - ADMIN e ATENDENTE
                .requestMatchers(HttpMethod.GET, "/api/v1/payments").hasAnyRole("ADMIN", "ATENDENTE")
                .requestMatchers(HttpMethod.GET, "/api/v1/payments/{id}").hasAnyRole("ADMIN", "ATENDENTE")
                // ===== OPERAÇÕES DE MODIFICAÇÃO - SOMENTE ADMIN =====
                // Reservas - apenas ADMIN pode criar/modificar/deletar
                .requestMatchers(HttpMethod.POST, "/api/v1/reservations").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/reservations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/reservations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/reservations/**").hasRole("ADMIN")
                // Pagamentos - apenas ADMIN pode modificar
                .requestMatchers(HttpMethod.POST, "/api/v1/payments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/payments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/payments/**").hasRole("ADMIN")
                // ===== ACESSO RESTRITO PARA USERS =====
                // USER precisa ser tratado no nível do controller com validação de ownership
                // Todas as outras requisições autenticadas passam por validação adicional
                .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Define os endpoints de documentação e monitoramento que devem ser
     * públicos
     */
    private String[] getDocumentationEndpoints() {
        return new String[]{
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/actuator/**"
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
