package com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffUserLocationMappService {

    @Autowired
    StaffLocationMappingRepo staffLocationMappingRepo;

    /**
     * Method: Save Staff Location Mapping
     * @param staffUserLocationMappingDto
     * @return
     */
    public StaffUserLocationMappingDto save(StaffUserLocationMappingDto staffUserLocationMappingDto) {
        try {
            List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(staffUserLocationMappingDto.getStaffId());
            if (!staffUserLocationMappings.isEmpty()) {
                StaffUserLocationMapping staffUserLocationMapping = new StaffUserLocationMapping();
                staffUserLocationMapping.setStaffId(staffUserLocationMappingDto.getStaffId());
                staffUserLocationMapping.setLocationId(staffUserLocationMappingDto.getLocationId());
                staffUserLocationMapping.setLocationName(staffUserLocationMappingDto.getLocationName());
                staffLocationMappingRepo.save(staffUserLocationMapping);
            }
            return staffUserLocationMappingDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
