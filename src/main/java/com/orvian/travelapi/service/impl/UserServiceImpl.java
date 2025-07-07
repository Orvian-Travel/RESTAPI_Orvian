package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User create(User user) {
        validateCreationAndUpdate(user);
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        validateCreationAndUpdate(user);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    private void validateCreationAndUpdate(User user) {
        if (isDuplicateUser(user)) {
            throw new DuplicatedRegistryException("User already registered");
        }
    }

    private boolean isDuplicateUser(User user) {
        Optional<User> userOptional = userRepository.findByEmailOrDocumentOrPhone(user.getEmail(), user.getDocument(), user.getPhone());

        if (user.getId() == null) {
            return userOptional.isPresent();
        }

        return !user.getId().equals(userOptional.get().getId()) && userOptional.isPresent();
    }
}
