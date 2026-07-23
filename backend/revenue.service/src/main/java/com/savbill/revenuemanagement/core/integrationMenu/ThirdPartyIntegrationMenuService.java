package com.savbill.revenuemanagement.core.integrationMenu;


import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.exceptions.AlreadyExistException;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.integrationMenuMapping.ThirdPartyIntegrationMenuMapping;
import com.savbill.revenuemanagement.core.integrationMenuMapping.ThirdPartyIntegrationMenuMappingRepository;
import com.savbill.revenuemanagement.core.security.constants.Constants;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThirdPartyIntegrationMenuService{


    private static final String MODULE = "[ThirdPartyIntegrationMenuService]";

    @Autowired
    ThirdPartyIntegrationMenuRepository thirdPartyIntegrationMenuRepository;

    @Autowired
    ThirdPartyIntegrationMenuMappingRepository menuMappingRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private SubscriberService subscriberService;
    public PageRequest pageRequest = null;

    @Transactional
    public ThirdPartyIntegrationMenuDto save(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto) throws Exception, AlreadyExistException {
        String SUBMODULE = MODULE + "save()";
        try {
            thirdPartyIntegrationMenuDto.setMvnoId(thirdPartyIntegrationMenuDto.getMvnoId());
            ThirdPartyIntegrationMenu obj = convertPojoToModel(thirdPartyIntegrationMenuDto);
            ThirdPartyIntegrationMenu savedThirdPartyIntegrationMenu = saveThirdPartyIntegrationMenu(obj);
            List<ThirdPartyIntegrationMenuMapping> mappingList = obj.getThirdPartyIntegrationMenuMappingList();
            /**here it set third_party_menu_id in ThirdPartyIntegrationMenuMapping table**/
            mappingList = mappingList.stream().peek(thirdPartyIntegrationMenuMapping -> thirdPartyIntegrationMenuMapping.setThirdPartyIntegrationMenuId(savedThirdPartyIntegrationMenu.getId())).collect(Collectors.toList());
            List<ThirdPartyIntegrationMenuMapping> saveThirdPartyIntegrationMenuMappings = menuMappingRepository.saveAll(mappingList);
            /** set latest thirdpartyintegration menu mappings after save */
            savedThirdPartyIntegrationMenu.setThirdPartyIntegrationMenuMappingList(saveThirdPartyIntegrationMenuMappings);
            thirdPartyIntegrationMenuDto = convertModelToPojo(savedThirdPartyIntegrationMenu);
        } catch (Exception | AlreadyExistException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
            throw e;
        }
        return thirdPartyIntegrationMenuDto;
    }

    @Transactional
    public ThirdPartyIntegrationMenuDto update(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto) throws Exception {
        String SUBMODULE = MODULE + "update()";
        List<ThirdPartyIntegrationMenuMapping> oldthThirdPartyIntegrationMenuMappingList = menuMappingRepository.findAllByThirdPartyMenuId(thirdPartyIntegrationMenuDto.getId());
        /**delete old ThirdPartyIntegrationMenuMapping list**/
        menuMappingRepository.deleteInBatch(oldthThirdPartyIntegrationMenuMappingList);
        /** Fetch the existing entity */
        ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = getById(thirdPartyIntegrationMenuDto.getId());
        try {
            thirdPartyIntegrationMenu.setName(thirdPartyIntegrationMenuDto.getName());
            List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappingList = thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings();
            /**set third_party_menu_id in ThirdPartyIntegrationMenuMapping list also set null in mapping id because of primary key **/
            ThirdPartyIntegrationMenuDto finalThirdPartyIntegrationMenuDto = thirdPartyIntegrationMenuDto;
            thirdPartyIntegrationMenuMappingList = thirdPartyIntegrationMenuMappingList.stream().peek(thirdPartyIntegrationMenuMapping -> {
                thirdPartyIntegrationMenuMapping.setThirdPartyIntegrationMenuId(finalThirdPartyIntegrationMenuDto.getId());
                thirdPartyIntegrationMenuMapping.setIntegrationMenuMappingId(null);
            }).collect(Collectors.toList());
            List<ThirdPartyIntegrationMenuMapping> saveThirdPartyIntegrationMenuMappingsList = menuMappingRepository.saveAll(thirdPartyIntegrationMenuMappingList);
            thirdPartyIntegrationMenu.setThirdPartyIntegrationMenuMappingList(saveThirdPartyIntegrationMenuMappingsList);
            thirdPartyIntegrationMenu.setStatus(thirdPartyIntegrationMenuDto.getStatus());
            /**Save the updated entity */
            thirdPartyIntegrationMenuRepository.save(thirdPartyIntegrationMenu);
            thirdPartyIntegrationMenuDto = convertModelToPojo(thirdPartyIntegrationMenu);
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
        }
        return thirdPartyIntegrationMenuDto;
    }

    @Transactional
    public void delete(Long id) throws Exception {
        String SUBMODULE = MODULE + "delete()";
        try {
            ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = getThirdPartyIntegrationMenuUpdateAndDelete(id);
            List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappingList = thirdPartyIntegrationMenu.getThirdPartyIntegrationMenuMappingList();
            ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = convertModelToPojo(thirdPartyIntegrationMenu);
            if (thirdPartyIntegrationMenuDto != null) {
                thirdPartyIntegrationMenu = thirdPartyIntegrationMenuRepository.findById(id)
                        .orElseThrow(() -> new Exception("ThirdPartyIntegrationMenu not found"));
                thirdPartyIntegrationMenu.setIsDelete(true);
                thirdPartyIntegrationMenuRepository.save(thirdPartyIntegrationMenu);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    @Transactional
    public ThirdPartyIntegrationMenu saveThirdPartyIntegrationMenu(ThirdPartyIntegrationMenu
                                                                           thirdPartyIntegrationMenu) throws Exception, AlreadyExistException {
        if (thirdPartyIntegrationMenuRepository.existsByNameAndIsDeleteFalse(thirdPartyIntegrationMenu.getName().trim())) {
            throw new AlreadyExistException("The ThirdPartyIntegrationMenu with the name " + thirdPartyIntegrationMenu.getName() + " already exists.");
        }

        thirdPartyIntegrationMenu.setMvnoId(thirdPartyIntegrationMenu.getMvnoId());

        return thirdPartyIntegrationMenuRepository.save(thirdPartyIntegrationMenu);
    }

    public void validateSaveRequest(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto){
        if(thirdPartyIntegrationMenuDto.getName().isEmpty() || thirdPartyIntegrationMenuDto.getName() == null || thirdPartyIntegrationMenuDto.getName().equalsIgnoreCase("")){
            throw new CustomValidationException(APIConstants.FAIL,"Name can't be empty",null);
        }
        if(thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings().isEmpty()){
            throw new CustomValidationException(APIConstants.FAIL,"ThirdParty Integration Menu Mapping list can't be empty",null);
        }
        if(!thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings().isEmpty()){
            for(ThirdPartyIntegrationMenuMapping thirdPartyIntegrationMenuMapping : thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings()){
                if(thirdPartyIntegrationMenuMapping.getThirdPartyParameterName() == null || thirdPartyIntegrationMenuMapping.getThirdPartyParameterName().equalsIgnoreCase("")||thirdPartyIntegrationMenuMapping.getThirdPartyParameterValue() == null || thirdPartyIntegrationMenuMapping.getThirdPartyParameterValue().equalsIgnoreCase("")){
                    throw new CustomValidationException(APIConstants.FAIL,"ThirdParty Integration Menu Mapping Parameter : "+ thirdPartyIntegrationMenuMapping.getThirdPartyParameterName() +" can't be empty.",null);
                }
            }
        }
    }

    public void validateUpdateRequest(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto){
        if(thirdPartyIntegrationMenuDto.getName().isEmpty() || thirdPartyIntegrationMenuDto.getName() == null || thirdPartyIntegrationMenuDto.getName().equalsIgnoreCase("")){
            throw new CustomValidationException(APIConstants.FAIL,"Name can't be empty",null);
        }
        if(thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings().isEmpty()){
            throw new CustomValidationException(APIConstants.FAIL,"ThirdParty Integration Menu Mapping list can't be empty",null);
        }
        if(!thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings().isEmpty()){
            for(ThirdPartyIntegrationMenuMapping thirdPartyIntegrationMenuMapping : thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings()){
                if(thirdPartyIntegrationMenuMapping.getThirdPartyParameterName() == null || thirdPartyIntegrationMenuMapping.getThirdPartyParameterName().equalsIgnoreCase("")||thirdPartyIntegrationMenuMapping.getThirdPartyParameterValue() == null || thirdPartyIntegrationMenuMapping.getThirdPartyParameterValue().equalsIgnoreCase("")){
                    throw new CustomValidationException(APIConstants.FAIL,"ThirdParty Integration Menu Mapping Parameter : "+ thirdPartyIntegrationMenuMapping.getThirdPartyParameterName() +" can't be empty.",null);
                }
            }
        }
        if(thirdPartyIntegrationMenuDto.getId() == null){
            throw new CustomValidationException(APIConstants.FAIL,"ThirdParty Integration Menu Id can't be null",null);
        }
    }

    public ThirdPartyIntegrationMenu getThirdPartyIntegrationMenuUpdateAndDelete(Long id) {
        ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = getById(id);
        if (thirdPartyIntegrationMenu == null || !(subscriberService.getMvnoIdFromCurrentStaff() == 1 || subscriberService.getMvnoIdFromCurrentStaff().intValue() == thirdPartyIntegrationMenu.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return thirdPartyIntegrationMenu;
    }




    @Transactional
    public List<ThirdPartyIntegrationMenu> getAllActiveEntities() {
        return thirdPartyIntegrationMenuRepository.findAllByStatusAndIsDeleteFalse(APIConstants.ACTIVE_STATUS)
                .stream().filter(custAccountProfile -> custAccountProfile.getMvnoId() == subscriberService.getMvnoIdFromCurrentStaff().intValue() || custAccountProfile.getMvnoId() == null || custAccountProfile.getMvnoId() == 1 || subscriberService.getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }


    public ThirdPartyIntegrationMenuDto findDefaultMenuFields(String eventName, String clientName) throws Exception {
        List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = thirdPartyIntegrationMenuRepository.findAllByEventNameAndClientNameAndMvnoIdIsNull(eventName, clientName);
        if (thirdPartyIntegrationMenus.isEmpty()) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Defualt Third Party Integration Menu is not found", null);
        }
        ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = convertModelToPojo(thirdPartyIntegrationMenus.get(0));
        return thirdPartyIntegrationMenuDto;
    }

    @Transactional
    public ThirdPartyIntegrationMenu getById(Long id) {
        return thirdPartyIntegrationMenuRepository.findById(id).get();
    }

    public ThirdPartyIntegrationMenu getThirdPartyIntegrationMenuById(Long id) {
        ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = getById(id);
        if (subscriberService.getMvnoIdFromCurrentStaff() == 1 || (thirdPartyIntegrationMenu.getMvnoId().intValue() == subscriberService.getMvnoIdFromCurrentStaff().intValue() || thirdPartyIntegrationMenu.getMvnoId() == 1))
            return thirdPartyIntegrationMenu;
        return null;
    }

    @Transactional
    public ThirdPartyIntegrationMenu convertPojoToModel(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto) throws
            Exception {
        String SUBMODULE = MODULE + " [convertThirdPartyMenuPojoToThirdPartyMenuModel()] ";
        ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = null;
        try {
            if (thirdPartyIntegrationMenuDto != null) {
                thirdPartyIntegrationMenu = new ThirdPartyIntegrationMenu();
                if (thirdPartyIntegrationMenuDto.getId() != null) {
                    thirdPartyIntegrationMenu.setId(thirdPartyIntegrationMenuDto.getId());
                }
                thirdPartyIntegrationMenu.setName(thirdPartyIntegrationMenuDto.getName());
                thirdPartyIntegrationMenu.setEventName(thirdPartyIntegrationMenuDto.getEventName());
                thirdPartyIntegrationMenu.setClientName(thirdPartyIntegrationMenuDto.getClientName());
                List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappingList = thirdPartyIntegrationMenuDto.getThirdPartyIntegrationMenuMappings();
                thirdPartyIntegrationMenu.setThirdPartyIntegrationMenuMappingList(thirdPartyIntegrationMenuMappingList);
                thirdPartyIntegrationMenu.setStatus(thirdPartyIntegrationMenuDto.getStatus());
                thirdPartyIntegrationMenu.setMvnoId(thirdPartyIntegrationMenuDto.getMvnoId().longValue());
                thirdPartyIntegrationMenu.setIsDelete(thirdPartyIntegrationMenuDto.isDelete());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return thirdPartyIntegrationMenu;
    }

    @Transactional
    public ThirdPartyIntegrationMenuDto convertModelToPojo(ThirdPartyIntegrationMenu thirdPartyIntegrationMenu) throws
            Exception {
        String SUBMODULE = MODULE + " [convertThirdPartyMenuModelToThirdPartyMenuPojo()] ";
        ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = null;
        try {
            if (thirdPartyIntegrationMenu != null) {
                thirdPartyIntegrationMenuDto = new ThirdPartyIntegrationMenuDto();
                if (thirdPartyIntegrationMenu.getId() != null) {
                    thirdPartyIntegrationMenuDto.setId(thirdPartyIntegrationMenu.getId());
                }
                thirdPartyIntegrationMenuDto.setName(thirdPartyIntegrationMenu.getName());
                thirdPartyIntegrationMenuDto.setEventName(thirdPartyIntegrationMenu.getEventName());
                thirdPartyIntegrationMenuDto.setClientName(thirdPartyIntegrationMenu.getClientName());
                thirdPartyIntegrationMenuDto.setThirdPartyIntegrationMenuMappings(thirdPartyIntegrationMenu.getThirdPartyIntegrationMenuMappingList());
                thirdPartyIntegrationMenuDto.setStatus(thirdPartyIntegrationMenu.getStatus());
                thirdPartyIntegrationMenuDto.setMvnoId(thirdPartyIntegrationMenu.getMvnoId());
                thirdPartyIntegrationMenuDto.setDelete(thirdPartyIntegrationMenu.getIsDelete());
                return thirdPartyIntegrationMenuDto;
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return thirdPartyIntegrationMenuDto;
    }

    public List<ThirdPartyIntegrationMenuDto> convertResponseModelIntoPojo
            (List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<ThirdPartyIntegrationMenuDto> pojoListRes = new ArrayList<>();
        try {
            if (thirdPartyIntegrationMenus != null && !thirdPartyIntegrationMenus.isEmpty()) {
                for (ThirdPartyIntegrationMenu thirdPartyIntegrationMenu : thirdPartyIntegrationMenus) {
                    pojoListRes.add(convertModelToPojo(thirdPartyIntegrationMenu));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

}
