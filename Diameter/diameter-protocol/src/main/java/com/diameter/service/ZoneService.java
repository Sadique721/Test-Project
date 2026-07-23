package com.diameter.service;

import com.diameter.dto.ZoneDTOMessage;
import com.diameter.model.Zone;
import com.diameter.repository.ZoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
public class ZoneService {

    @Autowired
    private ZoneRepository ZoneRepository;

    @Transactional
    public void saveZone(ZoneDTOMessage message) {
        Zone zone = Zone.builder()
                .zoneId(message.getZoneId())
                .zoneName(message.getZoneName())
                .prefixPattern(message.getPrefixPattern())
                .description(message.getDescription())
                .minLength(message.getMinLength())
                .maxLength(message.getMaxLength())
                .createdDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
        ZoneRepository.save(zone);
        log.info("[CPM] Zone SAVED from Kafka | Zone Name: {}", message.getZoneName());
    }

    @Transactional
    public void updateZone(ZoneDTOMessage message) {
        Zone zone = ZoneRepository.findById(message.getZoneId())
                .orElse(Zone.builder().zoneId(message.getZoneId()).build());

        zone.setZoneName(message.getZoneName());
        zone.setPrefixPattern(message.getPrefixPattern());
        zone.setDescription(message.getDescription());
        zone.setMinLength(message.getMinLength());
        zone.setMaxLength(message.getMaxLength());
        zone.setModifiedDate(LocalDateTime.now());

        ZoneRepository.save(zone);
        log.info("[CPM] Zone UPDATED from Kafka | Zone Name: {}", message.getZoneName());
    }

    @Transactional
    public void deleteZone(ZoneDTOMessage message) {
        Zone zone = ZoneRepository.findById(message.getZoneId())
                .orElse(Zone.builder().zoneId(message.getZoneId()).build());
        zone.setIsDeleted(true);
        ZoneRepository.save(zone);
        log.info("[CPM] Zone DELET");
    }
}
