package com.orvian.travelapi.service;

import java.util.UUID;

public interface CrudService<ID, T> {
    Object findAll();

    T create(Record dto);

    Object findById(ID id);

    void update(UUID id, Record dto);

    void delete(ID id);
}
