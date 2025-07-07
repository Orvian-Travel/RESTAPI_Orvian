package com.orvian.travelapi.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<ID, T> {
    List<T> findAll();

    Optional<T> findById(ID id);

    T create(T entity);

    T update(T entity);

    void delete(ID id);
}
