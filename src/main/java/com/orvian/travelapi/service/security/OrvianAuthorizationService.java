package com.orvian.travelapi.service.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.impl.ReservationServiceImpl;

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
    private final ReservationServiceImpl reservationService;

    /**
     * Verifica se o usuário atual pode realizar operações de modificação Apenas
     * ADMIN pode criar, atualizar ou deletar recursos
     */
    public boolean canModifyResource(String operation, String resourceType) {
        try {
            User currentUser = getCurrentUser();
            boolean canModify = "ADMIN".equals(currentUser.getRole());

            log.debug("User {} (role: {}) attempting {} on {}: {}",
                    currentUser.getEmail(), currentUser.getRole(), operation, resourceType,
                    canModify ? "ALLOWED" : "DENIED");

            return canModify;
        } catch (Exception e) {
            log.error("Error in canModifyResource: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o usuário atualmente autenticado
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new AccessDeniedException("Principal é null");
        }

        return switch (principal) {
            case User user -> {
                log.debug("Principal é objeto User: {}", user.getEmail());
                yield user;
            }
            case String userId -> {
                log.debug("Principal é ID do usuário: {}", userId);
                try {
                    UUID userUuid = UUID.fromString(userId);
                    yield userRepository.findById(userUuid)
                    .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado: " + userId));
                } catch (IllegalArgumentException e) {
                    throw new AccessDeniedException("ID de usuário inválido: " + userId);
                }
            }
            default -> {
                log.error("Tipo de principal não reconhecido: {}", principal.getClass().getSimpleName());
                throw new AccessDeniedException("Tipo de principal não reconhecido");
            }
        };
    }

    /**
     * Verifica se o usuário atual é ADMIN
     */
    public boolean isCurrentUserAdmin() {
        try {
            return "ADMIN".equals(getCurrentUser().getRole());
        } catch (Exception e) {
            log.debug("Error checking admin status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se o usuário atual é ATENDENTE ou ADMIN
     */
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
     * Valida se o usuário pode acessar informações de outro usuário específico
     */
    public boolean canAccessUserData(UUID targetUserId) {
        try {
            User currentUser = getCurrentUser();
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN", "ATENDENTE" -> {
                    log.debug("{} accessing user data: {}", role, targetUserId);
                    yield true;
                }
                case "USER" -> {
                    boolean isOwn = currentUser.getId().equals(targetUserId);
                    log.debug("USER accessing own data: {}", isOwn);
                    yield isOwn;
                }
                default -> {
                    log.warn("Unknown role: {}", role);
                    yield false;
                }
            };
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
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN" -> {
                    log.debug("ADMIN creating {} for user: {}", resourceType, targetUserId);
                    yield true;
                }
                case "USER", "ATENDENTE" -> {
                    boolean isSelf = currentUser.getId().equals(targetUserId);
                    log.debug("{} creating {} for {}: {}", role, resourceType,
                            isSelf ? "self" : "other", isSelf ? "ALLOWED" : "DENIED");
                    yield isSelf;
                }
                default -> {
                    log.warn("Unknown role: {}", role);
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Error in canCreateResourceForUser: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Determina o userId efetivo para listagens (aplicando regras de
     * autorização)
     */
    public UUID getEffectiveUserIdForListing(UUID requestedUserId) {
        try {
            User currentUser = getCurrentUser();
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN", "ATENDENTE" -> {
                    log.debug("{} listing with filter: {}", role, requestedUserId);
                    yield requestedUserId; // null = todos, UUID = filtrado
                }
                case "USER" -> {
                    log.debug("USER listing own resources only");
                    yield currentUser.getId(); // Sempre apenas próprios recursos
                }
                default ->
                    throw new AccessDeniedException("Role não reconhecida: " + role);
            };
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
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN" -> {
                    log.debug("ADMIN updating user: {}", targetUserId);
                    yield true;
                }
                case "USER" -> {
                    boolean isOwn = currentUser.getId().equals(targetUserId);
                    log.debug("USER updating {}: {}", isOwn ? "own data" : "other data",
                            isOwn ? "ALLOWED" : "DENIED");
                    yield isOwn;
                }
                case "ATENDENTE" -> {
                    log.debug("ATENDENTE cannot update users");
                    yield false;
                }
                default -> {
                    log.warn("Unknown role: {}", role);
                    yield false;
                }
            };
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
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN", "ATENDENTE" -> {
                    log.debug("{} accessing reservation: {}", role, reservationId);
                    yield true;
                }
                case "USER" -> {
                    boolean isOwner = reservationService.isReservationOwnedByUser(reservationId, currentUser.getId());
                    log.debug("USER accessing reservation: {}", isOwner ? "OWNER" : "NOT_OWNER");
                    yield isOwner;
                }
                default -> {
                    log.warn("Unknown role: {}", role);
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Error checking reservation access: {}", e.getMessage());
            return false;
        }
    }

    public boolean canCancelReservation(UUID reservationId) {
        try {
            User currentUser = getCurrentUser();
            String role = currentUser.getRole();

            return switch (role) {
                case "ADMIN" -> {
                    log.debug("ADMIN can cancel any reservation: {}", reservationId);
                    yield true;
                }
                case "USER", "ATENDENTE" -> {
                    boolean isOwner = reservationService.isReservationOwnedByUser(reservationId, currentUser.getId());
                    log.debug("{} {} cancel reservation {}: {}", role,
                            isOwner ? "CAN" : "CANNOT", reservationId,
                            isOwner ? "OWNER" : "NOT_OWNER");
                    yield isOwner;
                }
                default -> {
                    log.warn("Unknown role attempting to cancel reservation: {}", role);
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Error checking reservation cancellation permission: {}", e.getMessage(), e);
            return false;
        }
    }
}
