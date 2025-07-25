package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.domain.model.Media;
import com.orvian.travelapi.domain.repository.MediaRepository;
import com.orvian.travelapi.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    @Override
    public Object findAll() {
        return null;
    }

    @Override
    public Optional<Media> findById(UUID id) {
        return mediaRepository.findById(id);
    }

    @Override
    public Media create(Record media) {
        return null;
    }

    @Override
    public void update(UUID id, Record media) {

    }

    @Override
    public void delete(UUID id) {
        mediaRepository.deleteById(id);
    }
}
