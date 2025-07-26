package com.orvian.travelapi.service;

import java.util.UUID;

public interface CrudService<ID, T> {

    default Object findAll() {
        throw new UnsupportedOperationException("findAll() not implemented yet.");
    }

    default Object findAll(Integer pageNumber, Integer pageSize) {
        throw new UnsupportedOperationException("findAll(pageNumber, pageSize) not implemented yet.");
    }

    default Object findAll(Integer pageNumber, Integer pageSize, String sortBy) {
        throw new UnsupportedOperationException("findAll(pageNumber, pageSize, String sortBy) not implemented yet.");
    }

    default Object findAll(Integer pageNumber, Integer pageSize, UUID sortBy) {
        throw new UnsupportedOperationException("findAll(pageNumber, pageSize, UUID sortBy) not implemented yet.");
    }

    T create(Record dto);

    Object findById(ID id);

    void update(UUID id, Record dto);

    void delete(ID id);
}
