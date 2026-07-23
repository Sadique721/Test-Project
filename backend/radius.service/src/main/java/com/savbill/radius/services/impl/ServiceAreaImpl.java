package com.savbill.radius.services.impl;

import com.savbill.radius.entity.ServiceArea;
import com.savbill.radius.entity.StaffUserServiceAreaMapping;
import com.savbill.radius.kafka.message.ServiceAreaMessage;
import com.savbill.radius.repository.ServiceAreaRepo;
import com.savbill.radius.repository.StaffUserServiceAreaMappingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ServiceAreaImpl {
    @Autowired
    ServiceAreaRepo serviceAreaRepo;
    @Autowired
    private StaffUserServiceAreaMappingRepo staffUserServiceAreaMappingRepo;

    public ServiceArea saveServiceArea(ServiceAreaMessage message) {
        if (message.getCustomerData() != null) {
            ServiceArea serviceArea = new ServiceArea(message);
            ServiceArea serviceArea1 = serviceAreaRepo.save(serviceArea);
            return serviceArea1;
        } else {
            throw new IllegalArgumentException("No Service Area Found");
        }
    }

    public List<StaffUserServiceAreaMapping> assignStaffToServiceArea(List<StaffUserServiceAreaMapping> mappingList) {
        if (mappingList == null || mappingList.isEmpty()) {
            log.warn("No staff-user-service area mappings received to assign.");
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        for (StaffUserServiceAreaMapping mapping : mappingList) {
            if (mapping.getCreatedOn() == null) {
                mapping.setCreatedOn(now);
            }
            if (mapping.getLastmodifiedOn() == null) {
                mapping.setLastmodifiedOn(now);
            }
        }
        List<StaffUserServiceAreaMapping> savedMappings = staffUserServiceAreaMappingRepo.saveAll(mappingList);
        log.info("Saved {} staff-service area mappings received via Kafka.", savedMappings.size());

        return savedMappings;
    }
}
