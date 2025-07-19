package com.orvian.travelapi.service;

import java.util.Optional;
import java.util.UUID;

public interface CrudService<ID, T> {
    Object findAll();

    Optional<T> findById(ID id);

    T create(T entity);

    void update(UUID id, Record dto);

    void delete(ID id);
}
