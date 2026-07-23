package com.diameter.service;

import com.diameter.dto.PulseManagementDTOMessage;
import com.diameter.model.PulseManagement;
import com.diameter.repository.PulseManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class PulseManagementService {
    @Autowired
    private PulseManagementRepository PulseManagementRepository;

    @Transactional
    public void savePulse(PulseManagementDTOMessage message) {
        PulseManagement pulse = PulseManagement
                .builder()
                .id(message.getPulseId())
                .pulseName(message.getPulseName())
                .pulseUnit(message.getPulseUnit())
                .pulseValue(message.getPulseValue())
                .createdBy(message.getCreatedBy())
                .createdDate(message.getCreatedDate())
                .modifiedBy(message.getModifiedBy())
                .modifiedDate(message.getModifiedDate())
                .isDeleted(Boolean.valueOf("false"))

                .build();
        PulseManagementRepository
                .save(pulse);
        log.info("[CPM] Pulse SAVED | ID: {} | Name: {}",
                pulse.getId(), pulse.getPulseName());
    }

    @Transactional
    public void updatePulse(PulseManagementDTOMessage message) {
        PulseManagement
                pulse = PulseManagementRepository
                .findByid((message.getPulseId()));
        pulse.setPulseName(message.getPulseName());
        pulse.setPulseUnit(message.getPulseUnit());
        pulse.setPulseValue(message.getPulseValue());
        pulse.setCreatedBy(message.getCreatedBy());
        pulse.setCreatedDate(message.getCreatedDate());
        pulse.setModifiedBy(message.getModifiedBy());
        pulse.setModifiedDate(message.getModifiedDate());
        PulseManagementRepository
                .save(pulse);
        log.info("[CPM] Pulse UPDATED | ID: {} | Name: {}",
                pulse.getId(), pulse.getPulseName());
    }

    @Transactional
    public void deletePulse(PulseManagementDTOMessage message) {
        PulseManagement data= PulseManagementRepository.findByid(message.getPulseId());
        data.setIsDeleted(true);
        PulseManagementRepository.save(data);
        log.info("[CPM] Pulse DELETED | ID: {}", message.getPulseId());
    }

    public PulseManagement getPulse(Long pulseId) {
        return PulseManagementRepository
                .findById(pulseId).orElse(null);
    }
}
