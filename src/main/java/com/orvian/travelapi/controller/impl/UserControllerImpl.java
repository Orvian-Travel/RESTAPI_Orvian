package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.CreateUserDTO;
import com.orvian.travelapi.controller.dto.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.UpdateUserDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements GenericController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserDTO dto) {
        User user = userMapper.toEntity(dto);
        userService.create(user);

        URI location = gerarHeaderLocation(user.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO dto) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            new ResponseErrorDTO(
                                    HttpStatus.NOT_FOUND.value(),
                                    "User not found",
                                    List.of())
                    );
        }

        User user = userOptional.get();
        userMapper.updateEntityFromDto(dto, user);

        userService.update(user);

        return ResponseEntity.noContent().build();
    }
}
