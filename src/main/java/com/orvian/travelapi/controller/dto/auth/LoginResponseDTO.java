package com.orvian.travelapi.controller.dto.auth;

public record LoginResponseDTO (
        String token,
        String name
){
}
