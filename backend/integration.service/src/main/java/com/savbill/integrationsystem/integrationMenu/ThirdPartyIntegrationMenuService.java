package com.savbill.integrationsystem.integrationMenu;

import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.exceptions.AlreadyExistException;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.integrationMenuMapping.ThirdPartyIntegrationMenuMapping;
import com.savbill.integrationsystem.integrationMenuMapping.ThirdPartyIntegrationMenuMappingRepository;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThirdPartyIntegrationMenuService extends ExBaseAbstractService<ThirdPartyIntegrationMenuDto, ThirdPartyIntegrationMenu, Long> {
    public ThirdPartyIntegrationMenuService(JpaRepository<ThirdPartyIntegrationMenu, Long> repository, IBaseMapper<ThirdPartyIntegrationMenuDto, ThirdPartyIntegrationMenu> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ThirdPartyIntegrationMenuService]";
    }

    private static final String MODULE = "[ThirdPartyIntegrationMenuService]";

    @Autowired
    ThirdPartyIntegrationMenuRepository thirdPartyIntegrationMenuRepository;

    @Autowired
    ThirdPartyIntegrationMenuMappingRepository menuMappingRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    public PageRequest pageRequest = null;

    @Transactional
    public ThirdPartyIntegrationMenuDto save(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto) throws Exception, AlreadyExistException {
        String SUBMODULE = MODULE + "save()";
        try {
            thirdPartyIntegrationMenuDto.setMvnoId(Long.valueOf(getMvnoIdFromCurrentStaff()));
            ThirdPartyIntegrationMenu obj = convertPojoToModel(thirdPartyIntegrationMenuDto);
            ThirdPartyIntegrationMenu savedThirdPartyIntegrationMenu = saveThirdPartyIntegrationMenu(obj);
            List<ThirdPartyIntegrationMenuMapping> mappingList = obj.getThirdPartyIntegrationMenuMappingList();
            /**here it set third_party_menu_id in ThirdPartyIntegrationMenuMapping table**/
            mappingList = mappingList.stream().peek(thirdPartyIntegrationMenuMapping -> thirdPartyIntegrationMenuMapping.setThirdPartyIntegrationMenuId(savedThirdPartyIntegrationMenu.getId())).collect(Collectors.toList());
            List<ThirdPartyIntegrationMenuMapping> saveThirdPartyIntegrationMenuMappings = menuMappingRepository.saveAll(mappingList);
            /** set latest thirdpartyintegration menu mappings after save */
            savedThirdPartyIntegrationMenu.setThirdPartyIntegrationMenuMappingList(saveThirdPartyIntegrationMenuMappings);
            thirdPartyIntegrationMenuDto = convertModelToPojo(savedThirdPartyIntegrationMenu);
            kafkaMessageSender.send(new KafkaMessageData(thirdPartyIntegrationMenuDto,thirdPartyIntegrationMenuDto.getClass().getSimpleName(),"CREATE"));
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
            throw e;
        }
        return thirdPartyIntegrationMenuDto;
    }

    @Transactional
    public ThirdPartyIntegrationMenuDto update(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto, HttpServletRequest req) throws Exception {
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
            kafkaMessageSender.send(new KafkaMessageData(thirdPartyIntegrationMenuDto,thirdPartyIntegrationMenuDto.getClass().getSimpleName(),"UPDATE"));
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
                kafkaMessageSender.send(new KafkaMessageData(thirdPartyIntegrationMenuDto,thirdPartyIntegrationMenuDto.getClass().getSimpleName(),"DELETE"));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public Page<ThirdPartyIntegrationMenu> search(List<GenericSearchModel> filterList, Integer page, Integer
            pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "id", sortOrder);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        try {
            Specification<ThirdPartyIntegrationMenu> spec = Specification.where(null);

            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn() != null) {
                    String filterColumn = searchModel.getFilterColumn().trim();
                    String filterValue = searchModel.getFilterValue();

                    if (filterColumn.equalsIgnoreCase("any")) {
                        spec = spec.and((root, query, criteriaBuilder) ->
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterValue.toLowerCase() + "%")
                        );
                    }else {
                        spec = spec.and((root, query, criteriaBuilder) ->
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterValue.toLowerCase() + "%")
                        );
                    }
                    // Add more conditions based on other columns if needed
                } else {
                    throw new RuntimeException("Please Provide Search Column!");
                }
            }
            // Adding mvnoId and is_delete = false conditions to the specification
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("mvnoId"), mvnoId)
            );

            return thirdPartyIntegrationMenuRepository.findAll(spec, pageRequest);

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
        if((thirdPartyIntegrationMenuDto.getEventName() != null || !thirdPartyIntegrationMenuDto.getName().equalsIgnoreCase("")) && (thirdPartyIntegrationMenuDto.getClientName() != null || thirdPartyIntegrationMenuDto.getClientName().equalsIgnoreCase("") ) ){
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenusList =  thirdPartyIntegrationMenuRepository.findAllByEventNameAndClientNameAndMvnoId( thirdPartyIntegrationMenuDto.getEventName(), thirdPartyIntegrationMenuDto.getClientName(), mvnoId.longValue());
            if(!thirdPartyIntegrationMenusList.isEmpty()){
                throw new CustomValidationException(APIConstants.FAIL , "ThirdParty Integration Menu with Event Name : "+thirdPartyIntegrationMenuDto.getEventName() + " and Client Name : "+thirdPartyIntegrationMenuDto.getClientName()+ " already exist.",null);
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
        if (thirdPartyIntegrationMenu == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == thirdPartyIntegrationMenu.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return thirdPartyIntegrationMenu;
    }


    @Transactional
    public Page<ThirdPartyIntegrationMenu> getAllThirdPartyIntegrationMenuList(Integer pageNumber, Integer
            customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, "id", sortOrder);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (mvnoId == 1) {
            return thirdPartyIntegrationMenuRepository.findAllByIsDeleteFalse(pageRequest);
        }
        if (filterList == null || filterList.isEmpty()) {
            return thirdPartyIntegrationMenuRepository.findAllByMvnoIdAndIsDeleteFalse(pageRequest, Long.valueOf(mvnoId));
        } else {
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
        }
    }

    @Transactional
    public List<ThirdPartyIntegrationMenu> getAllActiveEntities() {
        return thirdPartyIntegrationMenuRepository.findAllByStatusAndIsDeleteFalse(APIConstants.ACTIVE_STATUS)
                .stream().filter(custAccountProfile -> custAccountProfile.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || custAccountProfile.getMvnoId() == null || custAccountProfile.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }


    public ThirdPartyIntegrationMenuDto findDefaultMenuFields(String eventName, String clientName) throws Exception {
        List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = thirdPartyIntegrationMenuRepository.findAllByEventNameAndClientNameAndMvnoIdIsNull(eventName, clientName);
        if (thirdPartyIntegrationMenus.isEmpty()) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Defualt Third Party Integration Menu is not found", null);
        }
        ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = convertModelToPojo(thirdPartyIntegrationMenus.get(0));
        return thirdPartyIntegrationMenuDto;
    }

    public List<ThirdPartyIntegrationMenuDto> findByEventName(String eventName) throws Exception {
        List<ThirdPartyIntegrationMenuDto> thirdPartyIntegrationMenuDtos =  new ArrayList<>();
        List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = thirdPartyIntegrationMenuRepository.findAllByEventNameAndMvnoId(eventName,getMvnoIdFromCurrentStaff().longValue());
        for(ThirdPartyIntegrationMenu thirdPartyIntegrationMenu : thirdPartyIntegrationMenus) {
            ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = convertModelToPojo(thirdPartyIntegrationMenu);
            thirdPartyIntegrationMenuDtos.add(thirdPartyIntegrationMenuDto);
        }
        return thirdPartyIntegrationMenuDtos;
    }

    @Transactional
    public ThirdPartyIntegrationMenu getById(Long id) {
        return thirdPartyIntegrationMenuRepository.findById(id).get();
    }

    public ThirdPartyIntegrationMenu getThirdPartyIntegrationMenuById(Long id) {
        ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = getById(id);
        if (getMvnoIdFromCurrentStaff() == 1 || (thirdPartyIntegrationMenu.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || thirdPartyIntegrationMenu.getMvnoId() == 1))
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
                thirdPartyIntegrationMenu.setMvnoId(Long.valueOf(getMvnoIdFromCurrentStaff()));
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
                thirdPartyIntegrationMenuDto.setMvnoId(Long.valueOf(getMvnoIdFromCurrentStaff()));
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

    /**@Author
     * Dhaval Khalasi
     * This will be get intigration parameter list in key value
     * **/
    public HashMap<String , String> getIntigrationParameter(String eventName , String clientName , Integer mvnoId){
        HashMap<String , String> parameterByIntigrationName = new HashMap<>();
        List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenuList = thirdPartyIntegrationMenuRepository.findAllByEventNameAndClientNameAndMvnoId(eventName , clientName , mvnoId.longValue());
        if(!thirdPartyIntegrationMenuList.isEmpty()){
            ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = thirdPartyIntegrationMenuList.get(0);
            for(ThirdPartyIntegrationMenuMapping thirdPartyIntegrationMenuMapping : thirdPartyIntegrationMenu.getThirdPartyIntegrationMenuMappingList()){
                parameterByIntigrationName.put(thirdPartyIntegrationMenuMapping.getThirdPartyParameterName() , thirdPartyIntegrationMenuMapping.getThirdPartyParameterValue());
            }

        }
        else{
            throw new RuntimeException("No Third party Configuration found with name and mvnoid");
        }
        return parameterByIntigrationName;

    }
}
