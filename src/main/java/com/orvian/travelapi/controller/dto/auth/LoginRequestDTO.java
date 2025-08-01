package com.orvian.travelapi.controller.dto.auth;

public record LoginRequestDTO(
        String email,
        String password
) {
}
