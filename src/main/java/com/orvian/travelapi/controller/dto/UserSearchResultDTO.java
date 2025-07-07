package com.orvian.travelapi.controller.dto;

import java.time.LocalDate;

public record UserSearchResultDTO(
    String id,
    String name,
    String email,
    String phone,
    String document,
    LocalDate birthDate
) {
}
