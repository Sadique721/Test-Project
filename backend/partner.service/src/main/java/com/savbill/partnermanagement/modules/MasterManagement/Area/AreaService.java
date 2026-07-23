package com.savbill.partnermanagement.modules.MasterManagement.Area;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.rabbitmq.master.SaveAreaSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateAreaSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService extends ExBaseAbstractService<AreaDTO, Area, Long> {

    public AreaService(AreaRepository repository, AreaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[AreaService]";
    }

    @Autowired
    AreaRepository areaRepository;
    private static final Logger logger = LoggerFactory.getLogger(AreaService.class);

    public void saveAreaEntiry(SaveAreaSharedDataMessage message) throws Exception {
        try {
            Area area = new Area();
            area.setId(Long.valueOf(message.getId()));
            area.setName(message.getName());
            area.setIsDeleted(message.getIsDeleted());
            area.setCountryId(message.getCountryId());
            area.setStateId(message.getStateId());
            area.setPincode(message.getPincode());
            area.setMvnoId(message.getMvnoId());
            area.setStatus(message.getStatus());
            area.setCreatedById(message.getCreatedById());
            area.setLastModifiedById(message.getLastModifiedById());
            areaRepository.save(area);
            logger.info("Area details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create area details with name " + message.getName(), e.getMessage());
        }
    }

    public void updateAreaEntiry(UpdateAreaSharedDataMessage message) throws Exception {
        try {
            Area area = areaRepository.findById(message.getId()).orElse(null);
            if (area != null) {
                area.setId(Long.valueOf(message.getId()));
                area.setName(message.getName());
                area.setIsDeleted(message.getIsDeleted());
                area.setCountryId(message.getCountryId());
                area.setStateId(message.getStateId());
                area.setPincode(message.getPincode());
                area.setMvnoId(message.getMvnoId());
                area.setStatus(message.getStatus());
                area.setCreatedById(message.getCreatedById());
                area.setLastModifiedById(message.getLastModifiedById());
                areaRepository.save(area);
                logger.info("Area details updated successfully with name " + message.getName());
            } else {
                Area area1 = new Area();
                area1.setId(Long.valueOf(message.getId()));
                area1.setName(message.getName());
                area1.setIsDeleted(message.getIsDeleted());
                area1.setCountryId(message.getCountryId());
                area1.setStateId(message.getStateId());
                area1.setPincode(message.getPincode());
                area1.setMvnoId(message.getMvnoId());
                area1.setStatus(message.getStatus());
                area1.setCreatedById(message.getCreatedById());
                area1.setLastModifiedById(message.getLastModifiedById());
                areaRepository.save(area1);
                logger.info("Area details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update area details with name " + message.getName(), e.getMessage());
        }
    }
}
