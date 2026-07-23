package com.savbill.radius.services.impl;

import com.savbill.radius.entity.BusinessUnit;
import com.savbill.radius.kafka.message.SaveBusinessUnitSharedDataMessage;
import com.savbill.radius.kafka.message.UpdateBusinessUnitSharedDataMessage;
import com.savbill.radius.repository.BusinessUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Transient;

@Service
public class BusinessUnitServiceImpl {
    @Autowired
    BusinessUnitRepository businessUnitRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessUnitServiceImpl.class);
    
    @Transient
    public void saveBusinessUnitEntity(SaveBusinessUnitSharedDataMessage message) throws Exception {
        try {
            BusinessUnit businessUnit = new BusinessUnit();
            businessUnit.setId(Long.valueOf(message.getId()));
            businessUnit.setBuname(message.getBuname());
            businessUnit.setBucode(message.getBucode());
            businessUnit.setStatus(message.getStatus());
            businessUnit.setPlanBindingType(message.getPlanBindingType());
            businessUnit.setCreatedById(message.getCreatedById());
            businessUnit.setLastModifiedById(message.getLastModifiedById());
            businessUnit.setIsDeleted(message.getIsDeleted());
            businessUnit.setMvnoId(message.getMvnoId());
            businessUnitRepository.save(businessUnit);
            logger.info("BusinessUnit details created successfully with name " + message.getBuname());
        } catch (Exception e) {
            logger.error("Unable to create business unit details with name " + message.getBuname()+ e.getMessage());
        }
    }

    public void updateBusinessUnitEntity(UpdateBusinessUnitSharedDataMessage message) throws Exception {
        try {
            BusinessUnit businessUnit = businessUnitRepository.findById(message.getId()).orElse(null);
            if (businessUnit != null) {
                businessUnit.setId(Long.valueOf(message.getId()));
                businessUnit.setBuname(message.getBuname());
                businessUnit.setBucode(message.getBucode());
                businessUnit.setStatus(message.getStatus());
                businessUnit.setCreatedById(message.getCreatedById());
                businessUnit.setLastModifiedById(message.getLastModifiedById());
                businessUnit.setPlanBindingType(message.getPlanBindingType());
                businessUnit.setIsDeleted(message.getIsDeleted());
                businessUnit.setMvnoId(message.getMvnoId());
                businessUnitRepository.save(businessUnit);
                logger.info("BusinessUnit details updated successfully with name " + message.getBuname());
            } else {
                BusinessUnit businessUnit1 = new BusinessUnit();
                businessUnit1.setId(Long.valueOf(message.getId()));
                businessUnit1.setBuname(message.getBuname());
                businessUnit1.setBucode(message.getBucode());
                businessUnit1.setStatus(message.getStatus());
                businessUnit1.setCreatedById(message.getCreatedById());
                businessUnit1.setLastModifiedById(message.getLastModifiedById());
                businessUnit1.setPlanBindingType(message.getPlanBindingType());
                businessUnit1.setIsDeleted(message.getIsDeleted());
                businessUnit1.setMvnoId(message.getMvnoId());
                businessUnitRepository.save(businessUnit1);
                logger.info("BusinessUnit details updated successfully with name " + message.getBuname());
            }
        } catch (Exception e) {
            logger.error("Unable to update business unit details with name " + message.getBuname());
        }
    }
}
