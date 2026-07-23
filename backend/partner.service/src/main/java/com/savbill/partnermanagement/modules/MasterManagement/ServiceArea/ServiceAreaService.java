package com.savbill.partnermanagement.modules.MasterManagement.ServiceArea;

import com.savbill.partnermanagement.core.constants.CommonConstants;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.partnermanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.partnermanagement.rabbitmq.master.SaveServiceAreaSharedDataMessge;
import com.savbill.partnermanagement.rabbitmq.master.UpdateServiceAreaSharedDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Slf4j
@Service
public class ServiceAreaService extends ExBaseAbstractService<ServiceAreaDTO, ServiceArea, Long> {
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    ServiceAreaMapper serviceAreaMapper;
    public ServiceAreaService(ServiceAreaRepository repository, ServiceAreaMapper mapper) {
        super(repository, mapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ServiceAreaService.class);
    @Override
    public String getModuleNameForLog() {
        return null;
    }

    /**
     Get Service Area Id By Staff User Service Area Mapping
     * @Author Darshan
     * @return
     */
    public List<Integer> getServiceAreaByStaffId() {
        logger.info("Inside getServiceAreaByStaffId");
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
       logger.info("staffUserServiceAreaMappingList: " + staffUserServiceAreaMappingList);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(staffUserServiceAreaMappingList.get(i).getServiceId());
        }
        logger.info("result: " + result);
        return result;
    }

    // Common method for find Service Area Id List Based on StaffId with Long
    public List<Long> getServiceAreaByStaffIdLong() {
        logger.info("Inside getServiceAreaByStaffId");
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
        }
        logger.info("result: " + result);
        return result;
    }

    /**
     Get Lit of Service areas By Staff User Id
     * @Author Darshan
     * @return
     */
    public List<ServiceAreaDTO> getAllServiceAreaByStaffId() {
logger.info("Inside getAllServiceAreaByStaffId");
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        logger.info("staffUserServiceAreaMappingList: " + staffUserServiceAreaMappingList);
        if(staffUserServiceAreaMappingList.size() != 0) {
            for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
                result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
            }
        } else {
            List <ServiceArea> serviceArea = serviceAreaRepository.findAll();
            logger.info("serviceArea: " + serviceArea);
            if(serviceArea.size() != 0) {
                for (int i = 0; i < serviceArea.size(); i++) {
                    result.add(serviceArea.get(i).getId());
                }
            }
        }
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdInAndStatusAndIsDeletedIsFalse(result, CommonConstants.ACTIVE_STATUS);
        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
        for(ServiceArea serviceArea : serviceAreaList){
            ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
            serviceAreaDTOS.add(serviceAreaDTO);
        }
        logger.info("Result : " + result);
        logger.info("serviceAreaDTOS: " + serviceAreaDTOS);
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
            serviceArea.setPincodeList(message.getPincodeList());
            serviceArea.setCityid(message.getCityid());
            serviceArea.setAreaId(message.getAreaId());
            serviceArea.setCreatedById(message.getCreatedById());
            serviceAreaRepository.save(serviceArea);
            logger.info("Service Area details saved successfully with name " + message.getName());
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
            logger.error("Unable to create service area details with name" + message.getName(), e.getMessage());
        }
    }

    public void updateServiceAreaEntity(UpdateServiceAreaSharedDataMessage message) throws Exception {
        try {
            ServiceArea serviceArea = serviceAreaRepository.findById(message.getId()).orElse(null);
            if(serviceArea != null) {
                serviceArea.setId(message.getId());
                serviceArea.setName(message.getName());
                serviceArea.setStatus(message.getStatus());
                serviceArea.setIsDeleted(message.getIsDeleted());
                serviceArea.setMvnoId(message.getMvnoId());
                serviceArea.setLatitude(message.getLatitude());
                serviceArea.setLongitude(message.getLongitude());
                serviceArea.setPincodeList(message.getPincodeList());
                serviceArea.setCityid(message.getCityid());
                serviceArea.setAreaId(message.getAreaId());
                serviceArea.setLastModifiedById(message.getUpdatedById());
                serviceAreaRepository.save(serviceArea);
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
                serviceArea1.setPincodeList(message.getPincodeList());
                serviceArea1.setCityid(message.getCityid());
                serviceArea1.setAreaId(message.getAreaId());
                serviceArea1.setLastModifiedById(message.getUpdatedById());
                serviceAreaRepository.save(serviceArea1);
                logger.info("Service Area details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update service area details with name " + message.getName(), e.getMessage());
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
        List<StaffUserServiceAreaMapping> savedMappings = staffUserServiceAreaMappingRepository.saveAll(mappingList);
        log.info("Saved {} staff-service area mappings received via Kafka.", savedMappings.size());

        return savedMappings;
    }
}
