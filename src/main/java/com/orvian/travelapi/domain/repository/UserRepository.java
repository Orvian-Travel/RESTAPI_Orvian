package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
