package com.orvian.travelapi.service.security.role.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.service.impl.ReservationServiceImpl;
import com.orvian.travelapi.service.security.role.RolePermissionStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AtendenteRoleStrategy implements RolePermissionStrategy {

    private final ReservationServiceImpl reservationService;

    @Override
    public boolean canAccessUserData(User currentUser, UUID targetUserId) {
        log.debug("ATENDENTE accessing user data: {}", targetUserId);
        return true; // Atendente pode acessar dados de usuários
    }

    @Override
    public boolean canUpdateUser(User currentUser, UUID targetUserId) {
        log.debug("ATENDENTE cannot update users");
        return false; // Atendente não pode atualizar usuários
    }

    @Override
    public boolean canCreateResourceForUser(User currentUser, UUID targetUserId, String resourceType) {
        boolean isSelf = currentUser.getId().equals(targetUserId);
        log.debug("ATENDENTE creating {} for {}: {}", resourceType,
                isSelf ? "self" : "other", isSelf ? "ALLOWED" : "DENIED");
        return isSelf; // Atendente só pode criar para si mesmo
    }

    @Override
    public UUID getEffectiveUserIdForListing(User currentUser, UUID requestedUserId) {
        log.debug("ATENDENTE listing with filter: {}", requestedUserId);
        return requestedUserId; // null = todos, UUID = filtrado
    }

    @Override
    public boolean canAccessReservation(User currentUser, UUID reservationId) {
        log.debug("ATENDENTE accessing reservation: {}", reservationId);
        return true; // Atendente pode acessar todas as reservas
    }

    @Override
    public boolean canCancelReservation(User currentUser, UUID reservationId) {
        boolean isOwner = reservationService.isReservationOwnedByUser(reservationId, currentUser.getId());
        log.debug("ATENDENTE {} cancel reservation {}: {}",
                isOwner ? "CAN" : "CANNOT", reservationId,
                isOwner ? "OWNER" : "NOT_OWNER");
        return isOwner; // Atendente só pode cancelar próprias reservas
    }

    @Override
    public String getRoleType() {
        return "ATENDENTE";
    }
}
