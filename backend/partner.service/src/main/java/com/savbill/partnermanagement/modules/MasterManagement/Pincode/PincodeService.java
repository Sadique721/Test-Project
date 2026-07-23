package com.savbill.partnermanagement.modules.MasterManagement.Pincode;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.rabbitmq.master.SavePincodeSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdatePincodeSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PincodeService extends ExBaseAbstractService<PincodeDTO, Pincode, Long> {

    public PincodeService(PincodeRepository repository, PincodeMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PincodeService]";
    }

    @Autowired
    PincodeRepository pincodeRepository;
    private static final Logger logger = LoggerFactory.getLogger(PincodeService.class);

    public void savePincodeEntity(SavePincodeSharedDataMessage message) throws Exception {
       logger.info("Pincode details creating with pincode " + message.getPincode());
        try {
            Pincode pincode = new Pincode();
            pincode.setId(message.getId());
            pincode.setCreatedById(message.getCreatedById());
            pincode.setLastModifiedById(message.getLastModifiedById());
            pincode.setPincode(message.getPincode());
            pincode.setStatus(message.getStatus());
            pincode.setIsDeleted(message.getIsDeleted());
            pincode.setCountryId(message.getCountryId());
            pincode.setCityId(message.getCityId());
            pincode.setStateId(message.getStateId());
            pincode.setMvnoId(message.getMvnoId());
            pincodeRepository.save(pincode);
            logger.info("Pincode details created successfully with pincode " + message.getPincode());
        } catch (CustomValidationException e) {
            logger.error("Unable to create pincode details with pincode " + message.getPincode(), e.getMessage());
        }
    }

    public void updatePincodeEntity(UpdatePincodeSharedDataMessage message) throws Exception {
        try {
            Pincode pincode = pincodeRepository.findById(message.getId()).orElse(null);
            if (pincode != null) {
                pincode.setId(message.getId());
                pincode.setPincode(message.getPincode());
                pincode.setCreatedById(message.getCreatedById());
                pincode.setLastModifiedById(message.getLastModifiedById());
                pincode.setStatus(message.getStatus());
                pincode.setIsDeleted(message.getIsDeleted());
                pincode.setCountryId(message.getCountryId());
                pincode.setCityId(message.getCityId());
                pincode.setStateId(message.getStateId());
                pincode.setMvnoId(message.getMvnoId());
                pincodeRepository.save(pincode);
                logger.info("Pincode details updated successfully with pincode " + message.getPincode());
            } else {
                Pincode pincode1 = new Pincode();
                pincode1.setId(message.getId());
                pincode1.setPincode(message.getPincode());
                pincode1.setCreatedById(message.getCreatedById());
                pincode1.setLastModifiedById(message.getLastModifiedById());
                pincode1.setStatus(message.getStatus());
                pincode1.setIsDeleted(message.getIsDeleted());
                pincode1.setCountryId(message.getCountryId());
                pincode1.setCityId(message.getCityId());
                pincode1.setStateId(message.getStateId());
                pincode1.setMvnoId(message.getMvnoId());
                pincodeRepository.save(pincode1);
                logger.info("Pincode details updated successfully with pincode " + message.getPincode());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update pincode details with pincode " + message.getPincode(), e.getMessage());
        }
    }
}
