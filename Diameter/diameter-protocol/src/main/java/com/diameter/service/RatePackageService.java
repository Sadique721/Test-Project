package com.diameter.service;

import com.diameter.dto.RatePackageDTOMessage;
import com.diameter.model.PulseManagement;
import com.diameter.model.RatePackage;
import com.diameter.repository.PulseManagementRepository;
import com.diameter.repository.RatePackageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RatePackageService {
    @Autowired
    private RatePackageRepository ocsRatePackageRepository;

    @Autowired
    private PulseManagementRepository pulseRepository;
    @Transactional
    public void savePackage(RatePackageDTOMessage message) {
        PulseManagement pulse = pulseRepository.findById(message.getPulseId())
                .orElseThrow(() -> new RuntimeException("Pulse not found with id: " + message.getPulseId()));

        RatePackage pkg = RatePackage.builder()
                .id(message.getRatePackageId())
                .pulse(pulse)
                .packageName(message.getPackageName())
                .serviceType(message.getServiceType())
                .packageType(message.getPackageType())
                .createdDate(message.getCreatedDate())
                .createdDate(message.getCreatedDate())
                .modifiedBy(message.getModifiedBy())
                .modifiedDate(message.getModifiedDate())
                .isDeleted(false)
                .isPulseApplied(message.getIsPulseApplied() != null ? message.getIsPulseApplied() : false)
                .ratePerPulse(message.getRatePerPulse())
                .roundingMode(message.getRoundingMode())
                .build();
        ocsRatePackageRepository.save(pkg);
        log.info("[CPM] RatePackage SAVED | ID: {} | Name: {}",
                pkg.getId(), pkg.getPackageName());
    }

    @Transactional
    public void updatePackage(RatePackageDTOMessage message) {
        RatePackage pkg = ocsRatePackageRepository.findById(message.getRatePackageId()).orElse(null);
        PulseManagement pulse = pulseRepository.findById(message.getPulseId())
                .orElseThrow(() -> new RuntimeException("Pulse not found with id: " + message.getPulseId()));

        pkg.setPackageName(message.getPackageName());
        pkg.setPulse(pulse);
        pkg.setPackageName(message.getPackageName());
        pkg.setServiceType(message.getServiceType());
        pkg.setPackageType(message.getPackageType());
        pkg.setCreatedBy(message.getCreatedBy());
        pkg.setCreatedDate(message.getCreatedDate());
        pkg.setModifiedBy(message.getModifiedBy());
        pkg.setModifiedDate(message.getModifiedDate());
        pkg.setIsDeleted(message.getIsDeleted() != null ? message.getIsDeleted() : false);
        pkg.setIsPulseApplied(message.getIsPulseApplied() != null ? message.getIsPulseApplied() : false);
        pkg.setRatePerPulse(message.getRatePerPulse());
        pkg.setRoundingMode(message.getRoundingMode());

        ocsRatePackageRepository.save(pkg);
        log.info("[CPM] RatePackage UPDATED | ID: {} | Name: {}",
                pkg.getId(), pkg.getPackageName());
    }

    @Transactional
    public void deletePackage(RatePackageDTOMessage message) {
        RatePackage pkg = ocsRatePackageRepository.findById(message.getRatePackageId()).orElse(null);
        pkg.setIsDeleted(true);
        ocsRatePackageRepository.save(pkg);
        log.info("[CPM] RatePackage DELETED | ID: {}", message.getRatePackageId());
    }

}
