package com.savbill.inventorymanagement.modules.MasterManagement.Pincode;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping.ServiceAreaPincodeRel;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping.ServiceAreaPincodeRelRepository;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SavePincodeSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdatePincodeSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    @Autowired
    PincodeMapper pincodeMapper;
    private static final Logger logger = Logger.getLogger(PincodeService.class);

    public void savePincodeEntity(SavePincodeSharedDataMessage message) throws Exception {
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
            logger.error("Unable to create pincode details with pincode " + message.getPincode() + " , Error: " + e.getMessage());
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
            logger.error("Unable to update pincode details with pincode " + message.getPincode() + " , Error: " + e.getMessage());
        }
    }

    public List<PincodeDTO> getPincodeListByServiceId(List<Long> serviceAreaIds) throws Exception{
        try {
            List<Pincode> pincodeList = new ArrayList<>();
            List<PincodeDTO> pincodeDTOS = new ArrayList<>();
            List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = serviceAreaPincodeRelRepository.findAllByIsDeletedIsFalseAndServiceAreaIdIn(serviceAreaIds);
            List<Long> pincodes = serviceAreaPincodeRelList.stream().map(ServiceAreaPincodeRel::getPincodeId).collect(Collectors.toList());
            if (getLoggedInUserId() == 1) {
                pincodeList = pincodeRepository.findAllByIsDeletedIsFalseAndStatusAndIdIn(CommonConstants.ACTIVE_STATUS, pincodes);
            } else {
                pincodeList = pincodeRepository.findAllByIsDeletedIsFalseAndStatusAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, pincodes, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            if (!pincodes.isEmpty()) {
                pincodeDTOS = pincodeList.stream().map(pincode -> pincodeMapper.domainToDTO(pincode, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            return pincodeDTOS;
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }
}
