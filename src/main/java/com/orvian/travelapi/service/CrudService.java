package com.orvian.travelapi.service;

import java.util.UUID;

public interface CrudService<ID, T> {

    // default Object findAll() {
    //     return findAll();
    // }
    // default Object findAll(Integer pageNumber, Integer pageSize) {
    //     return findAll(pageNumber, pageSize);
    // }
    // default Object findAll(Integer pageNumber, Integer pageSize, String sortBy) {
    //     return findAll(pageNumber, pageSize, sortBy);
    // }
    default Object findAll() {
        return findAll(null, null, null);
    }

    default Object findAll(Integer pageNumber, Integer pageSize) {
        return findAll(pageNumber, pageSize, null);
    }

    default Object findAll(Integer pageNumber, Integer pageSize, String sortBy) {
        // Implementação padrão, pode lançar exceção ou retornar null/lista vazia
        return null;
    }

    T create(Record dto);

    Object findById(ID id);

    void update(UUID id, Record dto);

    void delete(ID id);
}
