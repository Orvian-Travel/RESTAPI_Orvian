package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.dto.admin.DashboardDTO;
import com.orvian.travelapi.controller.dto.admin.WeekRatingDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PaymentByPackageDTO;
import com.orvian.travelapi.domain.repository.RatingRepository;
import com.orvian.travelapi.domain.repository.TravelPackageRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminControllerImpl {
    private final TravelPackageRepository packageRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    
    @GetMapping("/dashboard-week")
    public ResponseEntity<DashboardDTO> dashboardWeek(){
        DashboardDTO dashboard = new DashboardDTO(
                userRepository.newUserThisWeek(),
                ratingRepository.ratingAVGThisWeek(),
                packageRepository.sumTotalByPackage()
        );
        return  ResponseEntity.ok(dashboard);
    }

}
