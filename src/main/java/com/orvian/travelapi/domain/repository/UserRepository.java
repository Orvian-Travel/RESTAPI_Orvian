package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/*
       Repository padrão para qualquer entidade no Spring Boot.
 */

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailOrDocumentOrPhone(String email, String document, String phone);

    Page<User> findAll(Specification<User> spec, Pageable pageRequest);
}
