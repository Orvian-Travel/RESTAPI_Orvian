package com.orvian.travelapi.service.security.role;

import java.util.UUID;

import com.orvian.travelapi.domain.model.User;

/**
 * Strategy Pattern para gerenciar permissões baseadas em roles Cada role
 * implementa sua própria lógica de autorização
 */
public interface RolePermissionStrategy {

    /**
     * Verifica se pode acessar dados de outro usuário
     */
    boolean canAccessUserData(User currentUser, UUID targetUserId);

    /**
     * Verifica se pode atualizar outro usuário
     */
    boolean canUpdateUser(User currentUser, UUID targetUserId);

    /**
     * Verifica se pode criar recursos para outro usuário
     */
    boolean canCreateResourceForUser(User currentUser, UUID targetUserId, String resourceType);

    /**
     * Determina userId efetivo para listagens
     */
    UUID getEffectiveUserIdForListing(User currentUser, UUID requestedUserId);

    /**
     * Verifica se pode acessar reserva
     */
    boolean canAccessReservation(User currentUser, UUID reservationId);

    /**
     * Verifica se pode cancelar reserva
     */
    boolean canCancelReservation(User currentUser, UUID reservationId);

    /**
     * Retorna o tipo de role que esta estratégia representa
     */
    String getRoleType();
}
