package com.orvian.travelapi.service.security.role;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.orvian.travelapi.service.exception.AccessDeniedException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolePermissionFactory {

    private final List<RolePermissionStrategy> roleStrategies;

    private Map<String, RolePermissionStrategy> getStrategyMap() {
        return roleStrategies.stream()
                .collect(Collectors.toMap(
                        RolePermissionStrategy::getRoleType,
                        Function.identity()
                ));
    }

    public RolePermissionStrategy getStrategy(String roleType) {
        RolePermissionStrategy strategy = getStrategyMap().get(roleType);

        if (strategy == null) {
            throw new AccessDeniedException("Role não reconhecida: " + roleType);
        }

        return strategy;
    }
}
