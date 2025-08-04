package com.orvian.travelapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.travelpackage.PaymentByPackageDTO;
import org.springframework.data.domain.Page;

import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.domain.model.TravelPackage;

public interface TravelPackageService extends CrudService<UUID, TravelPackage> {

    public Page<PackageSearchResultDTO> findAllBySearch(
            Integer pageNumber,
            Integer pageSize,
            String title,
            LocalDate startDate,
            Integer maxPeople);

    @Override
    Page<PackageSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, String title);

    @Override
    PackageSearchResultDTO findById(UUID id);

}
