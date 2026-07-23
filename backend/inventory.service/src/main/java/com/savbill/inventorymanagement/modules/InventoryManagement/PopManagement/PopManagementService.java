package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PopManagementService extends ExBaseAbstractService<PopManagementDTO, PopManagement, Long> {

    public PopManagementService(PopManagementRepository repository, PopManagementMapper mapper) {
        super(repository, mapper);
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Override
    public String getModuleNameForLog() {
        return "[PopManagementService]";
    }

    private static final Logger logger = LoggerFactory.getLogger(PopManagementService.class);

    @Autowired
    public PopManagementRepository popManagementRepository;

    @Autowired
    public ServiceAreaRepository serviceAreaRepository;

    @Autowired
    public InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    PopManagementMapper popManagementMapper;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    private DocumentationPluginsBootstrapper bootstrapper;

    @Autowired
    private List<RequestHandlerProvider> handlerProviders;
    @Autowired
    PopServiceAreaMappingRepo popServiceAreaMappingRepo;

    /**
     Search Pop Management
     * @Author Darshan
     * @param filterList
     * @param page
     * @param pageSize
     * @param sortBy
     * @param sortOrder
     * @return
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn() != "") {
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                            return getPopManagementByName(searchModel.getFilterValue(), pageRequest1);
                        }
                    } else {
                        return getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                    }
                }
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            logger.error("Unable to serch pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, HttpStatus.EXPECTATION_FAILED.value(), HttpStatus.EXPECTATION_FAILED.getReasonPhrase(), ex.getStackTrace());
        }
        return null;
    }

    /**
     Search Pop Management By Name
     * @Author Darshan
     * @param s1
     * @param pageRequest
     * @return
     */
    public GenericDataDTO getPopManagementByName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + "[getPopManagementByName()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PopManagement> paginationList = null;
        List<Long> Ids = null;
        try {
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if (serviceAreaIds.size() != 0) {
                Ids = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds).stream().map(PopServiceAreaMapping::getPopId).collect(Collectors.toList());
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                if (Ids != null) {
                    paginationList = popManagementRepository.findAllByIdInAndIsDeletedIsFalseAndNameContainingIgnoreCase(Ids, pageRequest, s1);
                } else {
                    paginationList = popManagementRepository.findAllByIsDeletedIsFalseAndNameContainingIgnoreCase(pageRequest, s1);
                }
            } else {
                if (Ids != null) {
                    paginationList = popManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdInAndNameContainingIgnoreCase(Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest, s1);
                } else {
                    paginationList = popManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdInAndNameContainingIgnoreCase(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest, s1);
                }
            }
            if (null != paginationList && 0 < paginationList.getSize()) {
                paginationList.stream().forEach(x -> {
                    String latitude = x.getLatitude();
                    String longitude = x.getLongitude();
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    if (latitude != null && !latitude.isEmpty() && isNumeric(latitude))
                        x.setLatitude(df.format(Double.parseDouble(latitude)));
                    if (longitude != null && !longitude.isEmpty() && isNumeric(longitude))
                        x.setLongitude(df.format(Double.parseDouble(longitude)));
                });
                makeGenericResponse(genericDataDTO, paginationList);
            }
            if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all pop management by name " + s1 + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all pop managementby name " + s1 + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all pop managementby name " + s1 + "  :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get List Of POP Management with Pagination
     * @Author Darshan
     * @param page
     * @param size
     * @param sortBy
     * @param sortOrder
     * @param filterList
     * @return
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PopManagement> paginationList = null;
//        List<PopManagement> popManagementList = new ArrayList<>();
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        List<Long> Ids = null;
        try {
            // Common method for find Service Area List Based on StaffId
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = popManagementRepository.findAllByIsDeletedIsFalse(pageRequest);
            } else {
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    Ids = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds).stream().map(PopServiceAreaMapping::getPopId).collect(Collectors.toList());
                    paginationList = popManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdIn(Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                } else {
                    paginationList = popManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                }
            }
            if (paginationList.getSize() > 0) {
                paginationList.getContent().stream().forEach(x -> {
                    String latitude = x.getLatitude();
                    String longitude = x.getLongitude();
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    if (latitude != null && !latitude.isEmpty() && isNumeric(latitude))
                        x.setLatitude(df.format(Double.parseDouble(latitude)));
                    if (longitude != null && !longitude.isEmpty() && isNumeric(longitude))
                        x.setLongitude(df.format(Double.parseDouble(longitude)));
                });
                makeGenericResponse(genericDataDTO, paginationList);
            }
            if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Delete POP Verification
     * @Author Darshan
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Long count = customersRepository.countByPopidAndIsDeletedIsFalse(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     Save POP Management
     * @Author Darshan
     * @param popManagementDTO
     * @return
     * @throws Exception
     */
    @Override
    public PopManagementDTO saveEntity(PopManagementDTO popManagementDTO) throws Exception {
        try {
            popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            if (popManagementDTO.getServiceAreaIdsList() != null) {
                if (popManagementDTO.getServiceAreaIdsList() != null && popManagementDTO.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList())
                            .stream()
                            .map(serviceArea -> serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext()))
                            .collect(Collectors.toList());
                    popManagementDTO.setServiceAreaNameList(serviceAreaDTOS.stream().map(ServiceAreaDTO::getName).collect(Collectors.toList()));
                }
            }
            return super.saveEntity(popManagementDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Update POP Management
     * @Author Darshan
     * @param popManagementDTO
     * @return
     * @throws Exception
     */
    @Override
    public PopManagementDTO updateEntity(PopManagementDTO popManagementDTO) throws Exception {
        try {
            getEntityForUpdateAndDelete(popManagementDTO.getId());
            popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            if (popManagementDTO.getServiceAreaIdsList() != null) {
                if (popManagementDTO.getServiceAreaIdsList() != null && popManagementDTO.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList())
                            .stream()
                            .map(serviceArea -> serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext()))
                            .collect(Collectors.toList());
                    popManagementDTO.setServiceAreaNameList(serviceAreaDTOS.stream().map(ServiceAreaDTO::getName).collect(Collectors.toList()));
                }
            }
            return super.updateEntity(popManagementDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Get All Pop Management Without Pagination
     * @Author Darshan
     * @return
     */
    public List<PopManagementDTO> getAllEntities() {
        String SUBMODULE = getModuleNameForLog() + " [getAllWithoutPagination()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<PopManagement> popManagementList = new ArrayList<>();
        List<PopManagementDTO> popManagementDTOS = new ArrayList<>();
        List<Long> Ids = null;
        try {
//             Common method for find Service Area List Based on StaffId
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if (serviceAreaIds.size() != 0) {
//            List<PopServiceAreaMapping> popServiceAreaMappingList = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds);
                Ids = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds).stream().map(PopServiceAreaMapping::getPopId).collect(Collectors.toList());
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                if (Ids != null) {
                    popManagementList = popManagementRepository.findAllByStatusAndIdInAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS, Ids);
                } else {
                    popManagementList = popManagementRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS);
                }
            } else {
                if (Ids != null) {
                    popManagementList = popManagementRepository.findAllByStatusAndIdInAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    popManagementList = popManagementRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            popManagementDTOS = popManagementList.stream().map(popManagement -> popManagementMapper.domainToDTO(popManagement, new CycleAvoidingMappingContext()))
                    .sorted(Comparator.comparing(PopManagementDTO::getId).reversed()).collect(Collectors.toList());
            popManagementDTOS.stream().forEach(x -> {
                String latitude = x.getLatitude();
                String longitude = x.getLongitude();
                DecimalFormat df = new DecimalFormat("0.0000000");
                if (latitude != null && !latitude.isEmpty() && isNumeric(latitude))
                    x.setLatitude(df.format(Double.parseDouble(latitude)));
                if (longitude != null && !longitude.isEmpty() && isNumeric(longitude))
                    x.setLongitude(df.format(Double.parseDouble(longitude)));
            });

            if (popManagementDTOS.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all pop management :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return popManagementDTOS;
    }

    /**
     Validate POP at Save and Update
     * @Author Darshan
     * @param entityDto
     */
    public void validatePOP(PopManagementDTO entityDto) {
        try {
            String SUBMODULE = getModuleNameForLog() + " [validatePOP()] ";
            List<InventoryMapping> inventoryMappings = inventoryMappingRepo.findAllByIsDeletedIsFalse().stream()
                    .filter(data -> data.getOwnerId() != null && data.getOwnerId().equals(entityDto.getId()) &&
                            data.getOwnerType() != null && data.getOwnerType().equals(CommonConstants.POP) &&
                            (data.getApprovalStatus() != null && (data.getApprovalStatus().equals(CommonConstants.INVENTORY_MAPPING.PENDING) ||
                                    data.getApprovalStatus().equals(CommonConstants.INVENTORY_MAPPING.APPROVE))))
                    .collect(Collectors.toList());
            if (!inventoryMappings.isEmpty()) {
                logger.error("Module: {} - Unable to delete pop management with id : {}", SUBMODULE, entityDto.getId());
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), CommonConstants.POP_ASSIGN.POP_DELETE, null);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     Duplicate Verification of POP and Save and Update
     * @Author Darshan
     * @param popManagementDTO
     * @param operation
     * @return
     */
    public boolean duplicateVarification(PopManagementDTO popManagementDTO, Integer operation) {
        try {
            boolean flag = false;
            String name = popManagementDTO.getName();
            if (name != null) {
                name = name.trim();
                Long count = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = popManagementRepository.countByNameAndIsDeletedIsFalse(name);
                } else if (getMvnoIdFromCurrentStaff() != 1) {
                    count = popManagementRepository.countByNameAndIsDeletedIsFalseAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (operation.equals(CommonConstants.OPERATION_ADD)) {
                    if (count == 0) {
                        flag = true;
                    }
                } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                    if (count >= 1) {
                        Long countEdit = null;
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            countEdit = popManagementRepository.countByNameAndIdAndIsDeletedIsFalse(name, popManagementDTO.getId());
                        } else {
                            countEdit = popManagementRepository.countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(name, popManagementDTO.getId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                        }
                        if (countEdit == 1) {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public PopManagement getPOPManagement(long id) {
        return popManagementRepository.findById(id).get();
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public int countApis() {
        bootstrapper.start();

        return handlerProviders.stream()
                .filter(WebMvcRequestHandlerProvider.class::isInstance)
                .flatMap(provider -> provider.requestHandlers().stream())
                .map(handler -> handler.getPatternsCondition().getPatterns())
                .mapToInt(Set::size)
                .sum();
    }
}
