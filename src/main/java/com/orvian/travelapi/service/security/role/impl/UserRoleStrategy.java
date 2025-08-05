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
public class UserRoleStrategy implements RolePermissionStrategy {

    private final ReservationServiceImpl reservationService;

    @Override
    public boolean canAccessUserData(User currentUser, UUID targetUserId) {
        boolean isOwn = currentUser.getId().equals(targetUserId);
        log.debug("USER accessing own data: {}", isOwn);
        return isOwn; // Usuário só acessa próprios dados
    }

    @Override
    public boolean canUpdateUser(User currentUser, UUID targetUserId) {
        boolean isOwn = currentUser.getId().equals(targetUserId);
        log.debug("USER updating {}: {}", isOwn ? "own data" : "other data",
                isOwn ? "ALLOWED" : "DENIED");
        return isOwn; // Usuário só pode atualizar a si mesmo
    }

    @Override
    public boolean canCreateResourceForUser(User currentUser, UUID targetUserId, String resourceType) {
        boolean isSelf = currentUser.getId().equals(targetUserId);
        log.debug("USER creating {} for {}: {}", resourceType,
                isSelf ? "self" : "other", isSelf ? "ALLOWED" : "DENIED");
        return isSelf; // Usuário só pode criar para si mesmo
    }

    @Override
    public UUID getEffectiveUserIdForListing(User currentUser, UUID requestedUserId) {
        log.debug("USER listing own resources only");
        return currentUser.getId(); // Sempre apenas próprios recursos
    }

    @Override
    public boolean canAccessReservation(User currentUser, UUID reservationId) {
        boolean isOwner = reservationService.isReservationOwnedByUser(reservationId, currentUser.getId());
        log.debug("USER accessing reservation: {}", isOwner ? "OWNER" : "NOT_OWNER");
        return isOwner; // Usuário só acessa próprias reservas
    }

    @Override
    public boolean canCancelReservation(User currentUser, UUID reservationId) {
        boolean isOwner = reservationService.isReservationOwnedByUser(reservationId, currentUser.getId());
        log.debug("USER {} cancel reservation {}: {}",
                isOwner ? "CAN" : "CANNOT", reservationId,
                isOwner ? "OWNER" : "NOT_OWNER");
        return isOwner; // Usuário só pode cancelar próprias reservas
    }

    @Override
    public String getRoleType() {
        return "USER";
    }
}
