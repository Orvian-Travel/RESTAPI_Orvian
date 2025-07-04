package com.orvian.travelapi.controller.dto;

public record CreateUserDTO(
        String name,
        String email,
        String password,
        String phone,
        String document,
        String birthDate,
        String role
) {
}
