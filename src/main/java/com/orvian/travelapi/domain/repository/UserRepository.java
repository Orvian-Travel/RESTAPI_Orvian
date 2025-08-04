package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailOrDocumentOrPhone(String email, String document, String phone);

    Page<User> findAll(Specification<User> spec, Pageable pageRequest);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) AS novos_usuarios\n" +
            "FROM TB_USERS\n" +
            "WHERE CREATED_AT >= DATEADD(DAY, -7, GETDATE());",nativeQuery = true)
    Integer newUserThisWeek();
}
