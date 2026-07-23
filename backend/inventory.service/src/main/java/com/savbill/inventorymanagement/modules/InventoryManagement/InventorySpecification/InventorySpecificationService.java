package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistory;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistoryRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductParameterDefaultValueMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductParameterMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificatioParametersRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouse;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WarehouseManagementRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.modules.acl.model.ProductParameterDefaultValueMappingDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Inventory specification service.
 */
@Service
public class InventorySpecificationService extends ExBaseAbstractService<InventorySpecificationDto, InventorySpecification, Long> {

    /**
     * The Inward repository.
     */
    @Autowired
    InwardRepository inwardRepository;
    /**
     * The Specificatio parameters repo.
     */
    @Autowired
    SpecificatioParametersRepo specificatioParametersRepo;
    /**
     * The Inward mapper.
     */
    @Autowired
    InwardMapper inwardMapper;
    /**
     * The Specification parameters service.
     */
    @Autowired
    SpecificationParametersService specificationParametersService;
    /**
     * The Inventory specification repo.
     */
    @Autowired
    InventorySpecificationRepo inventorySpecificationRepo;
    /**
     * The Inventory specification mapper.
     */
    @Autowired
    InventorySpecificationMapper inventorySpecificationMapper;
    /**
     * The Product repository.
     */
    @Autowired
    ProductRepository productRepository;
    /**
     * The Item repository.
     */
    @Autowired
    ItemRepository itemRepository;
    /**
     * The Inventory specification history repo.
     */
    @Autowired
    InventorySpecificationHistoryRepo inventorySpecificationHistoryRepo;
    /**
     * The Inventory specification service.
     */
    @Autowired
    InventorySpecificationService inventorySpecificationService;
    /**
     * The Product parameter mapping repo.
     */
    @Autowired
    private ProductParameterMappingRepo productParameterMappingRepo;
    /**
     * The Cust inv params repo.
     */
    @Autowired
    private CustInvParamsRepo custInvParamsRepo;
    /**
     * The Item assign history mapping repo.
     */
    @Autowired
    ItemAssignHistoryMappingRepo itemAssignHistoryMappingRepo;
    /**
     * The Warehouse management repository.
     */
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;
    /**
     * The Customers repository.
     */
    @Autowired
    CustomersRepository customersRepository;

    /**
     * Instantiates a new Inventory specification service.
     * @param repository the repository
     * @param mapper the mapper
     */
    public InventorySpecificationService(InventorySpecificationRepo repository, InventorySpecificationMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[InventorySpecificationService]";
    }

    /**
     * Save entity list.
     * @param inwardid the inwardid
     * @param productid the productid
     * @param specificationParametersDTOList the specification parameters dto list
     * @return the list
     * @throws Exception the exception
     */
    public List<InventorySpecification> saveEntity(Long inwardid, Long productid, List<SpecificationParametersDTO> specificationParametersDTOList) throws Exception {
        List<InventorySpecification> inventorySpecificationList = new ArrayList<>();
        try {
            if (inwardid != null) {
                //Fetch all specific parameters List
                Inward inward = inwardRepository.findById(inwardid).orElse(null);
                Product product = productRepository.findById(productid).orElse(null);
                // iterate specific parameters and set inventoryspecific fields
                for (SpecificationParametersDTO parameters : specificationParametersDTOList) {
                    SpecificationParameters specificationParameters = specificatioParametersRepo.findById(parameters.getId()).orElse(null);
                    InventorySpecification inventorySpecification = new InventorySpecification();
                    inventorySpecification.setInward(inward);
                    inventorySpecification.setInvenSpecId(inward.getId());
                    inventorySpecification.setSpecificationParameters(specificationParameters);
                    inventorySpecification.setParamValue(parameters.getParamValue());
                    inventorySpecificationList.add(inventorySpecification);
                }
                return (inventorySpecificationRepo.saveAll(inventorySpecificationList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return inventorySpecificationList;
    }

    /**
     * Update entity list.
     * @param inwardid the inwardid
     * @param productid the productid
     * @param specificationParametersDTOList the specification parameters dto list
     * @return the list
     * @throws Exception the exception
     */
    public List<InventorySpecification> updateEntity(Long inwardid, Long productid, List<SpecificationParametersDTO> specificationParametersDTOList) throws Exception {
        List<InventorySpecification> updatedInvetorySpecificationList = new ArrayList<>();
        try {
            if (inwardid != null) {
                //Fetch all specific parameters List
                Inward inward = inwardRepository.findById(inwardid).orElse(null);
                Product product = productRepository.findById(productid).orElse(null);
                // iterate specific parameters and set inventoryspecific fields
                for (SpecificationParametersDTO parameters : specificationParametersDTOList) {
                    SpecificationParameters specificationParameters = specificatioParametersRepo.findById(parameters.getId()).orElse(null);
                    QInventorySpecification qInventorySpecification = QInventorySpecification.inventorySpecification;
                    BooleanExpression booleanExpression = qInventorySpecification.specificationParameters.id.eq(parameters.getId()).and(qInventorySpecification.inward.id.eq(inwardid));
                    InventorySpecification inventorySpecification = inventorySpecificationRepo.findOne(booleanExpression).orElse(null);
//                    InventorySpecification inventorySpecification = inventorySpecificationRepo.findBySpecificationParametersId(parameters.getId());
                    inventorySpecification.setInward(inward);
                    inventorySpecification.setInvenSpecId(inward.getId());
                    inventorySpecification.setSpecificationParameters(specificationParameters);
                    inventorySpecification.setParamValue(parameters.getParamValue());
                    updatedInvetorySpecificationList.add(inventorySpecification);
                }
                return (inventorySpecificationRepo.saveAll(updatedInvetorySpecificationList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return updatedInvetorySpecificationList;
    }

    /**
     * Gets all inventory spec by inward id.
     * @param inwardid the inwardid
     * @return the all inventory spec by inward id
     */
    public GenericDataDTO getAllInventorySpecByInwardId(Long inwardid) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInward_Id(inwardid);
            if (inventorySpecificationList != null) {
                List<InventorySpecificationDto> inventorySpecificationDtoList = new ArrayList<>();
                for (InventorySpecification inventorySpecification : inventorySpecificationList) {
                    SpecificationParametersDTO specificationParametersDTO = specificationParametersService.getEntityById(inventorySpecification.specificationParameters.getId());
                    InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();
                    inventorySpecificationDto.setId(inventorySpecification.getId());
                    inventorySpecificationDto.setInwardId(inwardid);
                    inventorySpecificationDto.setParamId(specificationParametersDTO.getId());
                    inventorySpecificationDto.setParamName(specificationParametersDTO.getParamName());
                    inventorySpecificationDto.setIsMandatory(specificationParametersDTO.getIsMandatory());
//                    inventorySpecificationDto.setSpecificationParametersDTO(specificationParametersDTO);
                    inventorySpecificationDto.setParamValue(inventorySpecification.getParamValue());
                    inventorySpecificationDto.setInvenSpecId(inventorySpecification.getInvenSpecId());
                    inventorySpecificationDtoList.add(inventorySpecificationDto);
                }
                genericDataDTO.setDataList(inventorySpecificationDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Gets all inventory spec by inward spec id.
     * @param itemId the item id
     * @return the all inventory spec by inward spec id
     */
    public GenericDataDTO getAllInventorySpecByInwardSpecId(Long itemId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Long invenSpecId = itemRepository.findInvSpecIdBySerializedItemId(itemId);
            List<InventorySpecificationDto> finalDtoList = new ArrayList<>();
            List<InventorySpecificationDto> inventorySpecificationDtoList = new ArrayList<>();
            if (invenSpecId != null) {
                List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInvenSpecId(invenSpecId);
                List<InventorySpecificationHistory> inventorySpecificationHistory = inventorySpecificationHistoryRepo.findAllByItemIdAndStatus(itemId, CommonConstants.NEW);
                if (!inventorySpecificationList.isEmpty() && inventorySpecificationHistory.isEmpty()) {
                    for (InventorySpecification inventorySpecification : inventorySpecificationList) {
                        InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();
                        SpecificationParametersDTO specificationParametersDTO = specificationParametersService.getEntityById(inventorySpecification.specificationParameters.getId());
                        inventorySpecificationDto.setId(inventorySpecification.getId());
                        inventorySpecificationDto.setParamId(inventorySpecification.getSpecificationParameters().getId());
                        inventorySpecificationDto.setInwardId(inventorySpecification.getInward().getId());
                        inventorySpecificationDto.setMvnoId(inventorySpecification.getSpecificationParameters().getMvnoId());
                        inventorySpecificationDto.setInvenSpecId(inventorySpecification.getInvenSpecId());
                        inventorySpecificationDto.setParamValue(inventorySpecification.getParamValue());
                        inventorySpecificationDto.setParamName(specificationParametersDTO.getParamName());
                        inventorySpecificationDto.setIsMandatory(specificationParametersDTO.getIsMandatory());
                        if (specificationParametersDTO.getIsMultiValueParam() != null && specificationParametersDTO.getIsMultiValueParam().equals(true))
                            inventorySpecificationDto.setIsMultiValueParam(true);
                        else
                            inventorySpecificationDto.setIsMultiValueParam(false);

                        if (specificationParametersDTO.getIsMultiValueParam().equals(true) && specificationParametersDTO.getParamValues() != null)
                            inventorySpecificationDto.setParamMultiValues(Arrays.asList(specificationParametersDTO.getParamValues().split(",", -1)));
                        inventorySpecificationDto.setDefaultValue(specificationParametersDTO.getDefaultValue());
                        inventorySpecificationDto.setParamValues(specificationParametersDTO.getParamValues());
                        inventorySpecificationDtoList.add(inventorySpecificationDto);
                    }
                    finalDtoList.addAll(inventorySpecificationDtoList);
                }
                if (!inventorySpecificationList.isEmpty() && !inventorySpecificationHistory.isEmpty()) {
                    List<Long> ids = inventorySpecificationHistory.stream().map(InventorySpecificationHistory::getInvenId).collect(Collectors.toList());
                    List<InventorySpecification> invenSpecByIdsByHistory = inventorySpecificationRepo.findAllByIdIn(ids);
                    Long inwardId = invenSpecByIdsByHistory.get(0).inward.getId();
                    List<Long> paramIdsByInward = inventorySpecificationRepo.findAllByInward_Id(inwardId).stream()
                            .map(spec -> spec.getSpecificationParameters().getId())
                            .distinct()
                            .collect(Collectors.toList());
                    List<Long> paramIds = invenSpecByIdsByHistory.stream()
                            .map(spec -> spec.getSpecificationParameters().getId())
                            .distinct()
                            .collect(Collectors.toList());
                    for (InventorySpecificationHistory invenByHistory : inventorySpecificationHistory) {
                        InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();
                        InventorySpecification inventorySpecification = inventorySpecificationRepo.findById(invenByHistory.getInvenId()).orElse(null);
                        SpecificationParametersDTO specificationParametersDTO = specificationParametersService.getEntityById(inventorySpecification.getSpecificationParameters().getId());
                        inventorySpecificationDto.setId(inventorySpecification.getId());
                        inventorySpecificationDto.setParamId(inventorySpecification.getSpecificationParameters().getId());
                        inventorySpecificationDto.setInwardId(inventorySpecification.getInward().getId());
                        inventorySpecificationDto.setMvnoId(inventorySpecification.getSpecificationParameters().getMvnoId());
                        inventorySpecificationDto.setInvenSpecId(inventorySpecification.getInvenSpecId());
                        inventorySpecificationDto.setParamValue(inventorySpecification.getParamValue());
                        inventorySpecificationDto.setParamName(specificationParametersDTO.getParamName());
                        inventorySpecificationDto.setIsMandatory(specificationParametersDTO.getIsMandatory());
                        if (specificationParametersDTO.getIsMultiValueParam() != null && specificationParametersDTO.getIsMultiValueParam().equals(true))
                            inventorySpecificationDto.setIsMultiValueParam(true);
                        else
                            inventorySpecificationDto.setIsMultiValueParam(false);

                        if (specificationParametersDTO.getIsMultiValueParam().equals(true) && specificationParametersDTO.getParamValues() != null)
                            inventorySpecificationDto.setParamMultiValues(Arrays.asList(specificationParametersDTO.getParamValues().split(",", -1)));
                        finalDtoList.add(inventorySpecificationDto);
                    }
                    paramIdsByInward.removeAll(paramIds);
                    if (!paramIdsByInward.isEmpty()) {
                        for (Long id : paramIdsByInward) {
                            InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();
                            InventorySpecification inventorySpecification = inventorySpecificationRepo.findAllBySpecificationParameters_IdAndInvenSpecId(id, inwardId);
                            SpecificationParametersDTO specificationParametersDTO = specificationParametersService.getEntityById(inventorySpecification.getSpecificationParameters().getId());
                            inventorySpecificationDto.setId(inventorySpecification.getId());
                            inventorySpecificationDto.setParamId(inventorySpecification.getSpecificationParameters().getId());
                            inventorySpecificationDto.setInwardId(inventorySpecification.getInward().getId());
                            inventorySpecificationDto.setMvnoId(inventorySpecification.getSpecificationParameters().getMvnoId());
                            inventorySpecificationDto.setInvenSpecId(inventorySpecification.getInvenSpecId());
                            inventorySpecificationDto.setParamValue(inventorySpecification.getParamValue());
                            inventorySpecificationDto.setParamName(specificationParametersDTO.getParamName());
                            inventorySpecificationDto.setIsMandatory(specificationParametersDTO.getIsMandatory());
                            if (specificationParametersDTO.getIsMultiValueParam() != null && specificationParametersDTO.getIsMultiValueParam().equals(true))
                                inventorySpecificationDto.setIsMultiValueParam(true);
                            else
                                inventorySpecificationDto.setIsMultiValueParam(false);

                            if (specificationParametersDTO.getIsMultiValueParam().equals(true) && specificationParametersDTO.getParamValues() != null)
                                inventorySpecificationDto.setParamMultiValues(Arrays.asList(specificationParametersDTO.getParamValues().split(",", -1)));
                            finalDtoList.add(inventorySpecificationDto);
                        }
                    }
                }
                genericDataDTO.setDataList(finalDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setDataList(finalDtoList);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Update specification value inventory specification.
     * @param itemId the item id
     * @param id the id
     * @param newParamValue the new param value
     * @return the inventory specification
     * @throws Exception the exception
     */
    @Transactional
    public InventorySpecification updateSpecificationValue(Long itemId, Long id, String newParamValue) throws Exception {
        InventorySpecification finalSavedData = new InventorySpecification();
        InventorySpecification existingInventorySpecification = inventorySpecificationRepo.findById(id).orElse(null);
        try {
            if (existingInventorySpecification != null) {
                String existingParamValue = existingInventorySpecification.getParamValue();
                if (!newParamValue.equals(existingParamValue)) {
                    // if paramValue is changed then update
                    InventorySpecification newInventorySpecification = new InventorySpecification();
                    newInventorySpecification.setSpecificationParameters(existingInventorySpecification.getSpecificationParameters());
                    newInventorySpecification.setParamValue(newParamValue);
                    newInventorySpecification.setInward(existingInventorySpecification.getInward());
                    newInventorySpecification.setInvenSpecId(existingInventorySpecification.getInvenSpecId());
                    InventorySpecification savedNewData = inventorySpecificationRepo.save(newInventorySpecification);
                    newInventorySpecification.setInvenSpecId(savedNewData.getId());
                    finalSavedData = inventorySpecificationRepo.save(newInventorySpecification);
                    Item item = itemRepository.findById(itemId).orElse(null);
                    if (item != null) {
                        item.setInvenSpecId(finalSavedData.getInvenSpecId());
                        Item finalSavedItemData = itemRepository.save(item);
                    }
                    InventorySpecificationHistory existinginventorySpecificationHistory = inventorySpecificationHistoryRepo.findAllByItemIdAndParamIdAndParamValueAndStatus(itemId, existingInventorySpecification.getSpecificationParameters().getId(), existingParamValue, CommonConstants.NEW);
                    if (existinginventorySpecificationHistory != null) {
                        existinginventorySpecificationHistory.setStatus(CommonConstants.OLD);
                        inventorySpecificationHistoryRepo.save(existinginventorySpecificationHistory);
                    } else {
                        // set exisisting entry in Inventory Specification History
                        InventorySpecificationHistory existingInvSpecHistory = new InventorySpecificationHistory();
                        existingInvSpecHistory.setItemId(itemId);
                        existingInvSpecHistory.setInvenId(existingInventorySpecification.getId());
                        existingInvSpecHistory.setParamId(existingInventorySpecification.getSpecificationParameters().getId());
                        existingInvSpecHistory.setParamValue(existingInventorySpecification.getParamValue());
                        existingInvSpecHistory.setIsMandatory(existingInventorySpecification.getSpecificationParameters().getIsMandatory());
                        existingInvSpecHistory.setCreatedById(getLoggedInUserId());
                        existingInvSpecHistory.setLastModifiedById(getLoggedInUserId());
                        existingInvSpecHistory.setCreatedByName(getLoggedInUser().getUsername());
                        existingInvSpecHistory.setLastModifiedByName(getLoggedInUser().getUsername());
                        existingInvSpecHistory.setStatus(CommonConstants.OLD);
                        inventorySpecificationHistoryRepo.save(existingInvSpecHistory);
                    }
                    // set new entry in Inventory Specification History
                    InventorySpecificationHistory newinventorySpecificationHistory = new InventorySpecificationHistory();
                    newinventorySpecificationHistory.setItemId(itemId);
                    newinventorySpecificationHistory.setInvenId(finalSavedData.getId());
                    newinventorySpecificationHistory.setParamId(finalSavedData.getSpecificationParameters().getId());
                    newinventorySpecificationHistory.setParamValue(finalSavedData.getParamValue());
                    newinventorySpecificationHistory.setIsMandatory(finalSavedData.getSpecificationParameters().getIsMandatory());
                    newinventorySpecificationHistory.setCreatedById(getLoggedInUserId());
                    newinventorySpecificationHistory.setLastModifiedById(getLoggedInUserId());
                    newinventorySpecificationHistory.setCreatedByName(getLoggedInUser().getUsername());
                    newinventorySpecificationHistory.setLastModifiedByName(getLoggedInUser().getUsername());
                    newinventorySpecificationHistory.setStatus(CommonConstants.NEW);
                    inventorySpecificationHistoryRepo.save(newinventorySpecificationHistory);

                    Optional<ItemAssignHistoryMapping> exisitingitemAssignHistoryMapping = itemAssignHistoryMappingRepo.findLatestByItemId(itemId);
                    if (exisitingitemAssignHistoryMapping.isPresent()) {
                        ItemAssignHistoryMapping olditemAssignHistoryMapping = exisitingitemAssignHistoryMapping.get();
                        ItemAssignHistoryMapping itemAssignHistoryMapping = new ItemAssignHistoryMapping();
                        itemAssignHistoryMapping.setItemId(itemId);
                        itemAssignHistoryMapping.setOwnerId(olditemAssignHistoryMapping.getOwnerId());
                        itemAssignHistoryMapping.setOwnerType(olditemAssignHistoryMapping.getOwnerType());
                        itemAssignHistoryMapping.setSpecificationHistoryId(newinventorySpecificationHistory.getId());
                        itemAssignHistoryMapping.setCreatedate(LocalDateTime.now());
                        itemAssignHistoryMappingRepo.save(itemAssignHistoryMapping);
                    }
                }
            } else {
                finalSavedData = existingInventorySpecification;
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return finalSavedData;
    }

    /**
     * Gets cust specific params.
     * @param parameterDto the parameter dto
     * @return the cust specific params
     */
    public InvenotryCustParamsDto getCustSpecificParams(InvenotryCustParamsDto parameterDto) {
        try {
            InvenotryCustParamsDto response = new InvenotryCustParamsDto();
            List<ProductParameterDefaultValueMappingDTO> parameters = new ArrayList<>();
            if (!CollectionUtils.isEmpty(parameterDto.getSerializedItemIds())) {
                List<Long> serializedItemIds = parameterDto.getSerializedItemIds();
                List<Long> invenSpecId = itemRepository.getInvSpecIdBySerializedItemIds(serializedItemIds);
                if (!CollectionUtils.isEmpty(invenSpecId)) {
                    List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInwardIn(invenSpecId);
                    for (InventorySpecification parameters1 : inventorySpecificationList) {
                        Optional<SpecificationParameters> specificationParameters = specificatioParametersRepo.findById(parameters1.getSpecificationParameters().getId());
                        if (specificationParameters.isPresent()) {
                            parameters.add(new ProductParameterDefaultValueMappingDTO(specificationParameters.get(), parameters1.getParamValue()));
                        }
                    }
                } else {
    //                System.out.println(" No Inward found for given serialized items");
                }
            }
            if (!CollectionUtils.isEmpty(parameters))
                response.setParameters(parameters);
            else
                response.setParameters(null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets product params.
     * @param productParameterDefaultValueMappings the product parameter default value mappings
     * @param parameters the parameters
     * @return the product params
     */
    public List<ProductParameterDefaultValueMappingDTO> getProductParams(List<ProductParameterDefaultValueMapping> productParameterDefaultValueMappings, SpecificationParameters parameters) {
        return productParameterDefaultValueMappings.stream().map(productParameterDefaultValueMapping -> new ProductParameterDefaultValueMappingDTO(parameters, productParameterDefaultValueMapping.getDefaultValue())).collect(Collectors.toList());
    }

    /**
     * Gets cust specific params by service.
     * @param custServMapId the cust serv map id
     * @return the cust specific params by service
     */
    public GenericDataDTO getCustSpecificParamsByService(Long custServMapId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<InventorySpecificationDto> inventorySpecificationDtoList = new ArrayList<>();
        try {
            List<CustInvParams> custInvParams = custInvParamsRepo.findAllByCustSerMapId(custServMapId);
            if (custInvParams != null && !CollectionUtils.isEmpty(custInvParams)) {
                inventorySpecificationDtoList = custInvParams.stream().map(custInvParams1 -> new InventorySpecificationDto(custInvParams1.getParamValue(), custInvParams1.getParamName(), true, custInvParams1.getCustSerMapId())).collect(Collectors.toList());
                genericDataDTO.setDataList(inventorySpecificationDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Gets cust specific params by cust inv map id.
     * @param custInvMapId the cust inv map id
     * @return the cust specific params by cust inv map id
     */
    public GenericDataDTO getCustSpecificParamsByCustInvMapId(Long custInvMapId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<InventorySpecificationDto> inventorySpecificationDtoList = new ArrayList<>();
        try {
            List<CustInvParams> custInvParams = custInvParamsRepo.findAllByCustomerInventoryId(custInvMapId);
            if (custInvParams != null && !CollectionUtils.isEmpty(custInvParams)) {
                inventorySpecificationDtoList = custInvParams.stream().map(custInvParams1 -> new InventorySpecificationDto(custInvParams1.getParamValue(), custInvParams1.getParamName(), true, custInvParams1.getCustSerMapId())).collect(Collectors.toList());
                genericDataDTO.setDataList(inventorySpecificationDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Find all item history list.
     * @param itemId the item id
     * @param paramId the param id
     * @return the list
     */
    public List<ItemAssignHistoryMappingDto> findAllItemHistory(Long itemId, Long paramId) {
        try {
            List<ItemAssignHistoryMapping> itemAssignHistoryMappings = itemAssignHistoryMappingRepo.findAllByItemId(itemId);
            List<ItemAssignHistoryMappingDto> dtos = new ArrayList<>();
            for (ItemAssignHistoryMapping itemAssignHistoryMapping : itemAssignHistoryMappings) {
                Optional<InventorySpecificationHistory> inventorySpecificationHistory = inventorySpecificationHistoryRepo.findById(itemAssignHistoryMapping.getSpecificationHistoryId());
                if (inventorySpecificationHistory.isPresent() && Objects.equals(inventorySpecificationHistory.get().getParamId(), paramId)) {
                    ItemAssignHistoryMappingDto dto = new ItemAssignHistoryMappingDto();
                    dto.setId(itemAssignHistoryMapping.getId());
                    dto.setItemId(itemAssignHistoryMapping.getItemId());
                    dto.setOwnerType(itemAssignHistoryMapping.getOwnerType());
                    dto.setOwnerId(itemAssignHistoryMapping.getOwnerId());
                    dto.setCreatedDate(itemAssignHistoryMapping.getCreatedate());
                    inventorySpecificationHistory.ifPresent(specificationHistory -> dto.setCurrentParamValue(specificationHistory.getParamValue()));
                    String currentAssignee;
                    switch (itemAssignHistoryMapping.getOwnerType()) {
                        case CommonConstants.WAREHOUSE:
                            currentAssignee = warehouseManagementRepository.findById(itemAssignHistoryMapping.getOwnerId())
                                    .map(WareHouse::getName) // Assuming WareHouse has a getName() method
                                    .orElse("Unknown Warehouse");
                            break;
                        case CommonConstants.STAFF:
                            currentAssignee = staffUserRepository.findById(itemAssignHistoryMapping.getOwnerId().intValue())
                                    .map(StaffUser::getUsername) // Assuming StaffUser has a getFullName() method
                                    .orElse("Unknown Staff");
                            break;

                        case CommonConstants.CUSTOMER:
                            currentAssignee = customersRepository.findById(itemAssignHistoryMapping.getOwnerId().intValue())
                                    .map(Customers::getUsername) // Assuming Customers has a getName() method
                                    .orElse("Unknown Customer");
                            break;

                        default:
                            currentAssignee = "Unknown Assignee";
                            break;
                    }
                    dto.setCurrentAssignee(currentAssignee);
                    dtos.add(dto);
                }
            }
            if (!dtos.isEmpty()) {
                Collections.reverse(dtos);
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

