package com.orvian.travelapi.service;

import com.orvian.travelapi.controller.dto.PackageSearchResultDTO;
import com.orvian.travelapi.domain.model.TravelPackage;

import java.util.List;
import java.util.UUID;

public interface TravelPackageService extends CrudService<UUID, TravelPackage> {
    List<PackageSearchResultDTO> findAll();
}