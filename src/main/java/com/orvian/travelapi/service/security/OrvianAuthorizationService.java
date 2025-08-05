package com.orvian.travelapi.service.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.security.role.RolePermissionFactory;
import com.orvian.travelapi.service.security.role.RolePermissionStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrvianAuthorizationService {

    private final UserRepository userRepository;
    private final RolePermissionFactory rolePermissionFactory;

    /**
     * Obtém o usuário atual autenticado do contexto de segurança
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getName())) {
                throw new AccessDeniedException("Usuário não autenticado");
            }

            String userIdStr = authentication.getName();
            UUID userId = UUID.fromString(userIdStr);

            return userRepository.findById(userId)
                    .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado: " + userId));

        } catch (IllegalArgumentException e) {
            log.error("ID de usuário inválido no contexto de segurança: {}", e.getMessage());
            throw new AccessDeniedException("ID de usuário inválido no contexto de segurança");
        } catch (Exception e) {
            log.error("Erro ao obter usuário atual: {}", e.getMessage());
            throw new AccessDeniedException("Erro interno ao verificar autenticação");
        }
    }

    /**
     * Verifica se o usuário atual pode realizar operações de modificação
     */
    public boolean canModifyResource(String operation, String resourceType) {
        try {
            User currentUser = getCurrentUser();
            String userRole = currentUser.getRole();

            // ✅ PERMITIR EXPORTAÇÕES PARA ADMIN E ATENDENTE
            if ("EXPORT".equals(operation)) {
                boolean canExport = "ADMIN".equals(userRole) || "ATENDENTE".equals(userRole);

                log.debug("User {} (role: {}) attempting {} on {}: {}",
                        currentUser.getEmail(), userRole, operation, resourceType,
                        canExport ? "ALLOWED" : "DENIED");

                return canExport;
            }

            // ✅ OUTRAS MODIFICAÇÕES: APENAS ADMIN
            boolean canModify = "ADMIN".equals(userRole);

            log.debug("User {} (role: {}) attempting {} on {}: {}",
                    currentUser.getEmail(), userRole, operation, resourceType,
                    canModify ? "ALLOWED" : "DENIED");

            return canModify;
        } catch (Exception e) {
            log.error("Error in canModifyResource: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida se o usuário pode acessar informações de outro usuário específico
     */
    public boolean canAccessUserData(UUID targetUserId) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.canAccessUserData(currentUser, targetUserId);
        } catch (Exception e) {
            log.error("Error in canAccessUserData: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se pode criar recursos para um usuário específico
     */
    public boolean canCreateResourceForUser(UUID targetUserId, String resourceType) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.canCreateResourceForUser(currentUser, targetUserId, resourceType);
        } catch (Exception e) {
            log.error("Error in canCreateResourceForUser: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Determina o userId efetivo para listagens
     */
    public UUID getEffectiveUserIdForListing(UUID requestedUserId) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.getEffectiveUserIdForListing(currentUser, requestedUserId);
        } catch (Exception e) {
            log.error("Error in getEffectiveUserIdForListing: {}", e.getMessage());
            throw new AccessDeniedException("Erro ao determinar permissões de listagem");
        }
    }

    /**
     * Verifica se o usuário pode atualizar outro usuário
     */
    public boolean canUpdateUser(UUID targetUserId) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.canUpdateUser(currentUser, targetUserId);
        } catch (Exception e) {
            log.error("Error in canUpdateUser: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se o usuário pode acessar uma reserva específica
     */
    public boolean canAccessReservation(UUID reservationId) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.canAccessReservation(currentUser, reservationId);
        } catch (Exception e) {
            log.error("Error checking reservation access: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se o usuário pode cancelar uma reserva
     */
    public boolean canCancelReservation(UUID reservationId) {
        try {
            User currentUser = getCurrentUser();
            RolePermissionStrategy strategy = rolePermissionFactory.getStrategy(currentUser.getRole());

            return strategy.canCancelReservation(currentUser, reservationId);
        } catch (Exception e) {
            log.error("Error checking reservation cancellation permission: {}", e.getMessage());
            return false;
        }
    }

    // Métodos de conveniência mantidos para compatibilidade
    public boolean isCurrentUserAdmin() {
        try {
            return "ADMIN".equals(getCurrentUser().getRole());
        } catch (Exception e) {
            log.debug("Error checking admin status: {}", e.getMessage());
            return false;
        }
    }

    public boolean isCurrentUserAttendenteOrAdmin() {
        try {
            String role = getCurrentUser().getRole();
            return "ADMIN".equals(role) || "ATENDENTE".equals(role);
        } catch (Exception e) {
            log.debug("Error checking attendant/admin status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o usuário atual (método público para uso em outros serviços se
     * necessário)
     */
    public User getCurrentUserPublic() {
        return getCurrentUser();
    }
}
