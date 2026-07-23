package com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.rabbitmq.master.SaveBusinessUnitSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateBusinessUnitSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessUnitService extends ExBaseAbstractService<BusinessUnitDTO, BusinessUnit, Long> {

    public BusinessUnitService(BusinessUnitRepository repository, BusinessUnitMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BusinessUnitService]";
    }

    @Autowired
    BusinessUnitRepository businessUnitRepository;
    private static final Logger logger = LoggerFactory.getLogger(BusinessUnitService.class);

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
            logger.info("BusinessUnit details created successfully with name " + message.getBuname());
            businessUnitRepository.save(businessUnit);
            logger.info("BusinessUnit details created successfully with name " + message.getBuname());
        } catch (CustomValidationException e) {
            logger.error("Unable to create business unit details with name " + message.getBuname(), e.getMessage());
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
        } catch (CustomValidationException e) {
            logger.error("Unable to update business unit details with name " + message.getBuname(), e.getMessage());
        }
    }
}
