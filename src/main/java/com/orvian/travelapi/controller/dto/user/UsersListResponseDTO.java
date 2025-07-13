package com.orvian.travelapi.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "UsersListResponseDTO",
        description = "Data Transfer Object for a list of users",
        title = "Users List Response DTO"
)
public record UsersListResponseDTO(
        @Schema(name = "usersList", description = "List of user search results", requiredMode = Schema.RequiredMode.REQUIRED)
        List<UserSearchResultDTO> usersList
) {
}
