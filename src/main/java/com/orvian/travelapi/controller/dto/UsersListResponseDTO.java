package com.orvian.travelapi.controller.dto;

import java.util.List;

public record UsersListResponseDTO(
        List<UserSearchResultDTO> usersList
) {
}
