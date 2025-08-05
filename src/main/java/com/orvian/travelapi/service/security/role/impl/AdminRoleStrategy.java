package com.orvian.travelapi.service.security.role.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.service.security.role.RolePermissionStrategy;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminRoleStrategy implements RolePermissionStrategy {

    @Override
    public boolean canAccessUserData(User currentUser, UUID targetUserId) {
        log.debug("ADMIN accessing user data: {}", targetUserId);
        return true; // Admin acessa todos os dados
    }

    @Override
    public boolean canUpdateUser(User currentUser, UUID targetUserId) {
        log.debug("ADMIN updating user: {}", targetUserId);
        return true; // Admin pode atualizar qualquer usuário
    }

    @Override
    public boolean canCreateResourceForUser(User currentUser, UUID targetUserId, String resourceType) {
        log.debug("ADMIN creating {} for user: {}", resourceType, targetUserId);
        return true; // Admin pode criar para qualquer usuário
    }

    @Override
    public UUID getEffectiveUserIdForListing(User currentUser, UUID requestedUserId) {
        log.debug("ADMIN listing with filter: {}", requestedUserId);
        return requestedUserId; // null = todos, UUID = filtrado
    }

    @Override
    public boolean canAccessReservation(User currentUser, UUID reservationId) {
        log.debug("ADMIN accessing reservation: {}", reservationId);
        return true; // Admin acessa todas as reservas
    }

    @Override
    public boolean canCancelReservation(User currentUser, UUID reservationId) {
        log.debug("ADMIN can cancel any reservation: {}", reservationId);
        return true; // Admin pode cancelar qualquer reserva
    }

    @Override
    public String getRoleType() {
        return "ADMIN";
    }
}
