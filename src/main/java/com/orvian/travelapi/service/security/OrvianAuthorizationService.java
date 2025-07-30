package com.orvian.travelapi.service.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.exception.AccessDeniedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por validar autorizações específicas do sistema Orvian
 * Implementa as regras de negócio para controle de acesso baseado em roles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrvianAuthorizationService {

    private final UserRepository userRepository;

    /**
     * Verifica se o usuário atual tem permissão para acessar um recurso
     * específico
     *
     * @param resourceOwnerId ID do proprietário do recurso
     * @param resourceType Tipo do recurso (reservation, payment, user)
     * @return true se tem permissão, false caso contrário
     */
    public boolean canAccessResource(UUID resourceOwnerId, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Tentativa de acesso sem autenticação para recurso: {}", resourceType);
            return false;
        }

        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));

        String userRole = currentUser.getRole();
        log.debug("Verificando acesso: usuário={}, role={}, recurso={}, proprietário={}",
                userEmail, userRole, resourceType, resourceOwnerId);

        return switch (userRole) {
            case "ADMIN" -> {
                // ADMIN tem acesso total a todos os recursos
                log.debug("Acesso liberado: usuário ADMIN");
                yield true;
            }
            case "ATENDENTE" -> {
                // ATENDENTE pode visualizar recursos de outros usuários
                log.debug("Acesso liberado: usuário ATENDENTE");
                yield true;
            }
            case "USER" -> {
                // USER só pode acessar seus próprios recursos
                boolean isOwner = currentUser.getId().equals(resourceOwnerId);
                log.debug("Verificação de propriedade para USER: isOwner={}", isOwner);
                yield isOwner;
            }
            default -> {
                log.warn("Role não reconhecida: {}", userRole);
                yield false;
            }
        };
    }

    /**
     * Verifica se o usuário atual pode realizar operações de modificação Apenas
     * ADMIN pode criar, atualizar ou deletar recursos
     *
     * @param operation Tipo de operação (CREATE, UPDATE, DELETE)
     * @param resourceType Tipo do recurso
     * @return true se pode modificar, false caso contrário
     */
    public boolean canModifyResource(String operation, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));

        String userRole = currentUser.getRole();

        // Apenas ADMIN pode realizar operações de modificação
        boolean canModify = "ADMIN".equals(userRole);

        log.debug("Verificação de modificação: usuário={}, role={}, operação={}, recurso={}, permitido={}",
                userEmail, userRole, operation, resourceType, canModify);

        return canModify;
    }

    /**
     * Obtém o usuário atualmente autenticado
     *
     * @return User object do usuário logado
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        // ✅ CORREÇÃO: Usar instanceof patterns (Java 16+) e tratar null
        Object principal = auth.getPrincipal();

        if (principal == null) {
            throw new AccessDeniedException("Principal é null");
        }

        return switch (principal) {
            case User user -> {
                // ✅ Pattern matching - Se já é um User, retornar direto
                log.debug("Principal já é objeto User: {}", user.getEmail());
                yield user;
            }
            case String userEmail -> {
                // ✅ Pattern matching - Se é String (email), buscar no banco
                log.debug("Principal é email, buscando usuário: {}", userEmail);
                yield userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado: " + userEmail));
            }
            default ->
                throw new AccessDeniedException("Tipo de principal não reconhecido: " + principal.getClass().getSimpleName());
        };
    }

    /**
     * Verifica se o usuário atual é ADMIN
     *
     * @return true se é ADMIN, false caso contrário
     */
    public boolean isCurrentUserAdmin() {
        try {
            User currentUser = getCurrentUser();
            return "ADMIN".equals(currentUser.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se o usuário atual é ATENDENTE ou ADMIN
     *
     * @return true se é ATENDENTE ou ADMIN, false caso contrário
     */
    public boolean isCurrentUserAttendenteOrAdmin() {
        try {
            // ✅ DEBUG: Verificar contexto de autenticação
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("DEBUG: Authentication object: {}", auth);
            log.info("DEBUG: Is authenticated: {}", auth != null ? auth.isAuthenticated() : "null auth");
            log.info("DEBUG: Principal name: {}", auth != null ? auth.getName() : "null auth");
            log.info("DEBUG: Authorities: {}", auth != null ? auth.getAuthorities() : "null auth");

            User currentUser = getCurrentUser();
            String role = currentUser.getRole();

            // ✅ DEBUG: Verificar dados do usuário
            log.info("DEBUG: Current user email: {}", currentUser.getEmail());
            log.info("DEBUG: Current user role from DB: {}", role);
            log.info("DEBUG: Current user ID: {}", currentUser.getId());

            boolean isAuthorized = "ADMIN".equals(role) || "ATENDENTE".equals(role);
            log.info("DEBUG: Is authorized (ADMIN or ATENDENTE): {}", isAuthorized);

            return isAuthorized;
        } catch (Exception e) {
            log.error("DEBUG: Error in isCurrentUserAttendenteOrAdmin: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Valida se o usuário pode acessar informações de outro usuário específico
     *
     * @param targetUserId ID do usuário alvo
     * @return true se pode acessar, false caso contrário
     */
    public boolean canAccessUserData(UUID targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));

        String currentRole = currentUser.getRole();

        return switch (currentRole) {
            case "ADMIN", "ATENDENTE" -> {
                // ADMIN e ATENDENTE podem acessar dados de qualquer usuário
                log.debug("{} accessing user data for userId: {}", currentRole, targetUserId);
                yield true;
            }
            case "USER" -> {
                // USER só pode acessar seus próprios dados
                boolean isOwnData = currentUser.getId().equals(targetUserId);
                log.debug("USER {} accessing own data: {}", currentUser.getEmail(), isOwnData);
                yield isOwnData;
            }
            default -> {
                log.warn("Unknown role: {}", currentRole);
                yield false;
            }
        };
    }

    public boolean canCreateResourceForUser(UUID targetUserId, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Unauthenticated user trying to create {} for user: {}", resourceType, targetUserId);
            return false;
        }

        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));

        String currentRole = currentUser.getRole();

        return switch (currentRole) {
            case "ADMIN" -> {
                // ADMIN pode criar para qualquer usuário
                log.info("ADMIN {} creating {} for user: {}", currentUser.getEmail(), resourceType, targetUserId);
                yield true;
            }
            case "USER", "ATENDENTE" -> {
                // USER/ATENDENTE só pode criar para si mesmo
                boolean isSelf = currentUser.getId().equals(targetUserId);

                if (isSelf) {
                    log.info("User {} creating {} for themselves", currentUser.getEmail(), resourceType);
                } else {
                    log.warn("User {} (role: {}) trying to create {} for different user: {}",
                            currentUser.getEmail(), currentRole, resourceType, targetUserId);
                }

                yield isSelf;
            }
            default -> {
                log.warn("Unknown role: {}", currentRole);
                yield false;
            }
        };
    }

    public UUID getEffectiveUserIdForListing(UUID requestedUserId) {
        try {
            // ✅ CORREÇÃO: Usar getCurrentUser() ao invés de auth.getName()
            User currentUser = getCurrentUser();
            String currentRole = currentUser.getRole();

            return switch (currentRole) {
                case "ADMIN", "ATENDENTE" -> {
                    // ADMIN e ATENDENTE podem usar o filtro solicitado
                    log.debug("{} listing resources with filter userId: {}", currentRole, requestedUserId);
                    yield requestedUserId; // null = todos, UUID específico = filtrado
                }
                case "USER" -> {
                    // USER sempre vê apenas os próprios recursos
                    log.debug("USER {} listing own resources only", currentUser.getEmail());
                    yield currentUser.getId();
                }
                default ->
                    throw new AccessDeniedException("Role não reconhecida: " + currentRole);
            };
        } catch (Exception e) {
            log.error("Error in getEffectiveUserIdForListing: {}", e.getMessage(), e);
            throw new AccessDeniedException("Erro ao determinar permissões de listagem");
        }
    }

    /**
     * ✅ Verifica se o usuário atual pode atualizar outro usuário ADMIN: pode
     * atualizar qualquer usuário USER: só pode atualizar seus próprios dados
     * ATENDENTE: não pode atualizar usuários
     *
     * @param targetUserId ID do usuário que se quer atualizar
     * @return true se pode atualizar, false caso contrário
     */
    public boolean canUpdateUser(UUID targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));

        String currentRole = currentUser.getRole();

        return switch (currentRole) {
            case "ADMIN" -> {
                // ADMIN pode atualizar qualquer usuário
                log.info("ADMIN {} updating user: {}", currentUser.getEmail(), targetUserId);
                yield true;
            }
            case "USER" -> {
                // USER só pode atualizar seus próprios dados
                boolean isOwnData = currentUser.getId().equals(targetUserId);
                if (isOwnData) {
                    log.info("USER {} updating own data", currentUser.getEmail());
                } else {
                    log.warn("USER {} trying to update different user: {}", currentUser.getEmail(), targetUserId);
                }
                yield isOwnData;
            }
            case "ATENDENTE" -> {
                // ATENDENTE não pode atualizar usuários
                log.warn("ATENDENTE {} trying to update user: {}", currentUser.getEmail(), targetUserId);
                yield false;
            }
            default -> {
                log.warn("Unknown role: {}", currentRole);
                yield false;
            }
        };
    }

}
