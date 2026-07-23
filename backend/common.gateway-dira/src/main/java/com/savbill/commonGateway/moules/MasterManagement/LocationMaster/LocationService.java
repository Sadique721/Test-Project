package com.savbill.commonGateway.moules.MasterManagement.LocationMaster;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.LocationMessage;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.domain.PlanService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LocationMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    LocationMasterRepository locationMasterRepository;

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);

    public void saveLocationMasterEntity(LocationMessage message) throws Exception{
        try {

            Optional<LocationMaster> optionalLocation = locationMasterRepository.findById(message.getLocationMasterId());

            LocationMaster location;
            if (optionalLocation.isPresent()) {
                location = optionalLocation.get();
            } else {
                location = new LocationMaster();
            }
            location.setLocationMasterId(message.getLocationMasterId());
            location.setName(message.getName());
            location.setCheckItem(message.getCheckItem());
            location.setStatus(message.getStatus());
            location.setMvnoId(message.getMvnoId());
            location.setLocationIdentifyAttribute(message.getLocationIdentifyAttribute());
            location.setLastmodifiedDate(message.getLastmodifiedDate());
            location.setLocationIdentifyValue(message.getLocationIdentifyValue());

            locationMasterRepository.save(location);
            logger.info("location created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create location details with name " + message.getName(), e.getMessage());
        }
    }

}
