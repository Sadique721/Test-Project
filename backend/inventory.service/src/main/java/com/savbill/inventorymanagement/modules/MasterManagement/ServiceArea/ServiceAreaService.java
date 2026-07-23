package com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.Pincode;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping.ServiceAreaPincodeRel;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping.ServiceAreaPincodeRelRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveServiceAreaSharedDataMessge;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateServiceAreaSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServiceAreaService extends ExBaseAbstractService<ServiceAreaDTO, ServiceArea, Long> {
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    public ServiceAreaService(ServiceAreaRepository repository, ServiceAreaMapper mapper) {
        super(repository, mapper);
    }

    private static final Logger logger = Logger.getLogger(ServiceAreaService.class);

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    /**
     * Get Service Area Id By Staff User Service Area Mapping
     *
     * @return
     * @Author Darshan
     */
    public List<Integer> getServiceAreaByStaffId() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(staffUserServiceAreaMappingList.get(i).getServiceId());
        }
        return result;
    }

    public List<Integer> getServiceAreaByStaffIdImprove() {
        return staffUserServiceAreaMappingRepository.findServiceIdsByStaffId(getLoggedInUserId());
    }


    // Common method for find Service Area Id List Based on StaffId with Long
    public List<Long> getServiceAreaByStaffIdLong() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
        }
        return result;
    }

    /**
     * Get Lit of Service areas By Staff User Id
     *
     * @return
     * @Author Darshan
     */
    public List<ServiceAreaDTO> getAllServiceAreaByStaffId() {

        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        if (!staffUserServiceAreaMappingList.isEmpty()) {
            for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
                result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
            }
        } else {
            List<ServiceArea> serviceArea = serviceAreaRepository.findAll();
            if (!serviceArea.isEmpty()) {
                for (int i = 0; i < serviceArea.size(); i++) {
                    result.add(serviceArea.get(i).getId());
                }
            }
        }
        List<ServiceArea> serviceAreaList = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() == 1) {
            serviceAreaList = serviceAreaRepository.findAllByIdInAndStatusAndIsDeletedIsFalse(result, CommonConstants.ACTIVE_STATUS);
        } else {
            serviceAreaList = serviceAreaRepository.findAllByIdInAndStatusAndIsDeletedIsFalseAndMvnoIdIn(result, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
        for (ServiceArea serviceArea : serviceAreaList) {
            ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
            serviceAreaDTOS.add(serviceAreaDTO);
        }
//        List<ServiceAreaDTO> serviceAreaList = serviceAreaMapper.domainToDTO(, new CycleAvoidingMappingContext());
        return serviceAreaDTOS;
    }

    public void saveServiceAreaEntity(SaveServiceAreaSharedDataMessge message) throws Exception {
        try {
            ServiceArea serviceArea = new ServiceArea();
            serviceArea.setId(message.getId());
            serviceArea.setName(message.getName());
            serviceArea.setStatus(message.getStatus());
            serviceArea.setIsDeleted(message.getIsDeleted());
            serviceArea.setMvnoId(message.getMvnoId());
            serviceArea.setLatitude(message.getLatitude());
            serviceArea.setLongitude(message.getLongitude());
            serviceArea.setCityid(message.getCityid());
            serviceArea.setAreaId(message.getAreaId());
            serviceArea.setCreatedById(message.getCreatedById());
            ServiceArea save = serviceAreaRepository.save(serviceArea);
            updateSAPincodeRel(save, message.getPincodeList());
            if (message.getStaffSAMap() == true) {
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                staffUserServiceAreaMapping.setStaffId(message.getCreatedById());
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
                if (message.getCreatedById() != 1) {
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping1.setServiceId(message.getId().intValue());
                    staffUserServiceAreaMapping1.setStaffId(1);
                    staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
                    staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
                    staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
                }
                staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
                logger.info("Staff User Service Area details save successfully with  service area name " + message.getName());
            }
            logger.info("Service Area details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create service area details with name" + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateServiceAreaEntity(UpdateServiceAreaSharedDataMessage message) throws Exception {
        try {
            ServiceArea serviceArea = serviceAreaRepository.findById(message.getId()).orElse(null);
            if (serviceArea != null) {
                serviceArea.setId(message.getId());
                serviceArea.setName(message.getName());
                serviceArea.setStatus(message.getStatus());
                serviceArea.setIsDeleted(message.getIsDeleted());
                serviceArea.setMvnoId(message.getMvnoId());
                serviceArea.setLatitude(message.getLatitude());
                serviceArea.setLongitude(message.getLongitude());
                serviceArea.setCityid(message.getCityid());
                serviceArea.setAreaId(message.getAreaId());
                serviceArea.setLastModifiedById(message.getUpdatedById());
                ServiceArea save = serviceAreaRepository.save(serviceArea);
                updateSAPincodeRel(save, message.getPincodeList());
                logger.info("Service Area details updated successfully with name " + message.getName());
            } else {
                ServiceArea serviceArea1 = new ServiceArea();
                serviceArea1.setId(message.getId());
                serviceArea1.setName(message.getName());
                serviceArea1.setStatus(message.getStatus());
                serviceArea1.setIsDeleted(message.getIsDeleted());
                serviceArea1.setMvnoId(message.getMvnoId());
                serviceArea1.setLatitude(message.getLatitude());
                serviceArea1.setLongitude(message.getLongitude());
                ;
                serviceArea1.setCityid(message.getCityid());
                serviceArea1.setAreaId(message.getAreaId());
                serviceArea1.setLastModifiedById(message.getUpdatedById());
                ServiceArea save = serviceAreaRepository.save(serviceArea1);
                updateSAPincodeRel(save, message.getPincodeList());
                logger.info("Service Area details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update service area details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    private void updateSAPincodeRel(ServiceArea save, List<Pincode> pincodeList) {
        List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = serviceAreaPincodeRelRepository.findAllByIsDeletedIsFalseAndServiceAreaId(save.getId());
        if (!serviceAreaPincodeRelList.isEmpty()) {
            serviceAreaPincodeRelRepository.deleteAll(serviceAreaPincodeRelList);
        }
        for (Pincode pincode : pincodeList) {
            ServiceAreaPincodeRel serviceAreaPincodeRel = new ServiceAreaPincodeRel();
            serviceAreaPincodeRel.setServiceAreaId(save.getId());
            serviceAreaPincodeRel.setPincodeId(pincode.getId());
            serviceAreaPincodeRelRepository.save(serviceAreaPincodeRel);
        }
    }

    public ServiceArea getByID(Long id) {
        Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
        if (serviceAreaOptional.isPresent())
            return serviceAreaRepository.findById(id).get();
        return null;
    }

    public List<StaffUserServiceAreaMapping> assignStaffToServiceArea(List<StaffUserServiceAreaMapping> mappingList) {
        try {
            if (mappingList == null || mappingList.isEmpty()) {
                logger.warn("No staff-user-service area mappings received to assign.");
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
            List<StaffUserServiceAreaMapping> savedMappings = staffUserServiceAreaMappingRepository.saveAll(mappingList);
            logger.info("Saved " + savedMappings.size() + " staff-service area mappings received via Kafka.");
            return savedMappings;
        } catch (CustomValidationException e) {
            logger.error("Unable to saved staff service area mapping received via Kafka with error: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
