package com.diameter.service;


import com.diameter.dto.RatePackageGroupMappingDTOMessage;
import com.diameter.model.RatePackageGroupMapping;
import com.diameter.repository.RatePackageGroupMappingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatePackageGroupMappingService {

    @Autowired
    private RatePackageGroupMappingRepo ratePackageGroupMappingRepo;

    @Transactional
    public void saveMappings(RatePackageGroupMappingDTOMessage message) {
        if (message.getMappings() == null || message.getMappings().isEmpty()) return;

        List<RatePackageGroupMapping> mappings = message.getMappings().stream().map(item -> {
            RatePackageGroupMapping mapping = new RatePackageGroupMapping();
            mapping.setId(item.getId());
            mapping.setGroupId(item.getGroupId());
            mapping.setRatePackageId(item.getRatePackageId());
            mapping.setZoneMappingId(item.getZoneMappingId());
            mapping.setCheckedItem(item.getCheckedItem());
            mapping.setEffectiveDate(item.getEffectiveDate());
            mapping.setExpiryDate(item.getExpiryDate());
            mapping.setPackageType(item.getPackageType());
            mapping.setPulseValue(item.getPulseValue());
            mapping.setPulseUnit(item.getPulseUnit());
            mapping.setRoundingMode(item.getRoundingMode());
            return mapping;
        }).collect(Collectors.toList());

        ratePackageGroupMappingRepo.saveAll(mappings);
        log.info("[OCS] RatePackageGroupMapping SAVED | GroupId: {} | Count: {}",
                message.getGroupId(), mappings.size());
    }

    @Transactional
    public void updateMappings(RatePackageGroupMappingDTOMessage message) {
        // Delete old mappings for the group, then re-save new ones
        ratePackageGroupMappingRepo.deleteAllByGroupId(message.getGroupId());
        saveMappings(message);
        log.info("[OCS] RatePackageGroupMapping UPDATED | GroupId: {}", message.getGroupId());
    }

    @Transactional
    public void deleteMappings(RatePackageGroupMappingDTOMessage message) {
        ratePackageGroupMappingRepo.deleteAllByGroupId(message.getGroupId());
        log.info("[OCS] RatePackageGroupMapping DELETED | GroupId: {}", message.getGroupId());
    }
}