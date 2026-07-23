package com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.*;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService extends ExBaseAbstractService<ProductCategoryDto, ProductCategory, Long> {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    MessageSender messageSender;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    @Autowired
    SpecificationParametersService specificationParametersService;
    @Autowired
    SpecificatioParametersRepo specificatioParametersRepo;
    @Autowired
    SpecificationParametersMapper specificationParametersMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryService.class);

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository, IBaseMapper<ProductCategoryDto, ProductCategory> mapper) {
        super(productCategoryRepository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductCategoryService]";
    }

//    GenericDataDTO getAllProductCategory() {
//        String SUBMODULE = getModuleNameForLog() + "[getAllProductCategory()]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            QProductCategory qProductCategory = QProductCategory.productCategory;
//            BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProductCategory.isDeleted.eq(false));
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            genericDataDTO.setDataList(IterableUtils.toList(this.productCategoryRepository.findAll(booleanExpression)));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

    /**
     * Save Product Category
     *
     * @param entity
     * @return
     * @throws Exception
     * @Author Darshan
     */
    @Override
    @Transactional
    public ProductCategoryDto saveEntity(ProductCategoryDto entity) throws Exception {
        try {
            entity.setMvnoId(getMvnoIdFromCurrentStaff());
            ProductCategoryDto productCategoryDto = new ProductCategoryDto();
//        try {
            productCategoryDto = super.saveEntity(entity);
            ProductCategory productCategory = productCategoryRepository.findById(productCategoryDto.getId()).orElse(null);

            if (productCategory != null) {
                specificationParametersService.saveEntity(productCategory, entity.getSpecificationParametersDTOList());
            }
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(productCategoryDto.getId());
            productCategoryDto.setSpecificationParametersDTOList(specificationParameters.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            //Todo: Code for Product Category for Integration
//        messageSender.send(productCategoryDto, RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN);
//        }
//        catch (CustomValidationException e) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
//        }
            return productCategoryDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update Product Category
     *
     * @param entity
     * @return
     * @throws Exception
     * @Author Darshan
     */
    @Override
    @Transactional
    public ProductCategoryDto updateEntity(ProductCategoryDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        try {
            productCategoryDto = super.updateEntity(entity);
            ProductCategory productCategory = productCategoryRepository.findById(productCategoryDto.getId()).orElse(null);
            if (productCategory != null) {
                specificationParametersService.updateEntity(productCategory, entity.getSpecificationParametersDTOList(),entity);
            }
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(productCategoryDto.getId());
            productCategoryDto.setSpecificationParametersDTOList(specificationParameters.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            productCategoryDto.getSpecificationParametersDTOList().forEach(x->{
                if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true))
                    x.setParamMultiValues(Arrays.asList(x.getParamValues().split(",", -1)));
            });

            //Todo: Code for Product Category for Integration
//            messageSender.send(productCategoryDto, RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return productCategoryDto;
    }

    /**
     * Get Product Category By Id
     *
     * @param id
     * @return
     * @Author Darshan
     */
    public ProductCategory getById(Long id) {
        return productCategoryRepository.findById(id).get();
    }

    /**
     * Find product category id is deleted or not
     *
     * @param id
     * @return
     * @Author Darshan
     */
    @Override
    public boolean deleteVerification(Integer id) {
        boolean flag = false;
        Long count = productRepository.countByProductCategoryIdAndIsDeletedIsFalse(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * Delete Product Category by Id
     *
     * @param id
     * @return
     * @throws Exception
     * @Author Darshan
     */
    public ProductCategory deleteEntity(Long id) throws Exception {
        String SUBMOULE = getModuleNameForLog() + "[deleteEntity()]";
        try {
            ProductCategory productCategory = productCategoryRepository.findById(id).orElse(null);
            if (productCategory != null) {
                List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(id);
                specificatioParametersRepo.deleteAll(specificationParameters);
                productCategory.setDeleteFlag(true);
                return productCategoryRepository.save(productCategory);
            } else {
                return null;
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            logger.error("Unable to delete product category with id " + id + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMOULE, HttpStatus.EXPECTATION_FAILED, e.getMessage());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    /**
     * Get Product Category List with Pagination
     *
     * @param pageNumber
     * @param customPageSize
     * @param sortBy
     * @param sortOrder
     * @param filterList
     * @return
     * @Author Darshan
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Page<ProductCategory> paginationList = null;
        List<ProductCategoryDto> productCategoryDtos = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = productCategoryRepository.findAllByIsDeletedIsFalse(pageRequest);
            } else {
                paginationList = productCategoryRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
            }
            if (paginationList.getSize() > 0) {
                productCategoryDtos = getProductCategoryDTO(paginationList);
            }
            if (productCategoryDtos.isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all product category :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(productCategoryDtos);
                genericDataDTO.setTotalRecords(paginationList.getTotalElements());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
                genericDataDTO.setTotalPages(paginationList.getTotalPages());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all product category :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product category :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     * Get Product Category DTO
     *
     * @param paginationList
     * @return
     * @Author Darshan
     */
    public List<ProductCategoryDto> getProductCategoryDTO(Page<ProductCategory> paginationList) {
        try {
            List<ProductCategoryDto> productCategoryDtos = new ArrayList<>();
            for (ProductCategory item : paginationList) {
                ProductCategoryDto productCategoryDto = new ProductCategoryDto();
                productCategoryDto.setId(item.getId());
                productCategoryDto.setName(item.getName());
                productCategoryDto.setUnit(item.getUnit());
                productCategoryDto.setType(item.getType());
                productCategoryDto.setDeviceType(item.getDeviceType());
                productCategoryDto.setStatus(item.getStatus());
                List<SpecificationParametersDTO> specificationParametersDTOS = specificationParametersService.getDTOByPCID(item.getId());
                productCategoryDto.setSpecificationParametersDTOList(specificationParametersDTOS);
                productCategoryDto.setMvnoId(item.getMvnoId());
                productCategoryDto.setIsDeleted(item.getIsDeleted());
                productCategoryDto.setProductId(item.getProductId());
                productCategoryDto.setHasMac(item.isHasMac());
                productCategoryDto.setHasCas(item.isHasCas());
                productCategoryDto.setHasSerial(item.isHasSerial());
                productCategoryDto.setHasTrackable(item.isHasTrackable());
                productCategoryDto.setHasPort(item.isHasPort());
                productCategoryDto.setDtvCategory(item.getDtvCategory());
                productCategoryDtos.add(productCategoryDto);
            }
            return productCategoryDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Product Category By Name or Type
     *
     * @param s1
     * @param pageRequest
     * @return
     * @Author Darshan
     */
    public GenericDataDTO searchProductCategory(String s1, PageRequest pageRequest, String operation) {
        String SUBMODULE = getModuleNameForLog() + " [getByNameOrType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<ProductCategory> productCategories = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                if (operation.equalsIgnoreCase("Name")) {
                    productCategories = productCategoryRepository.findAllByIsDeletedIsFalseAndNameContainingIgnoreCase(s1, pageRequest);
                }
                if (operation.equalsIgnoreCase("Type")) {
                    productCategories = productCategoryRepository.findAllByIsDeletedIsFalseAndTypeContainingIgnoreCase(s1, pageRequest);
                }
            } else {
                if (operation.equalsIgnoreCase("Name")) {
                    productCategories = productCategoryRepository.findAllByIsDeletedIsFalseAndNameContainingIgnoreCaseAndMvnoIdIn(s1, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                }
                if (operation.equalsIgnoreCase("Type")) {
                    productCategories = productCategoryRepository.findAllByIsDeletedIsFalseAndTypeContainingIgnoreCaseAndMvnoIdIn(s1, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                }
            }
            makeGenericResponse(genericDataDTO, productCategories);
            if (productCategories.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to search product category by name or type " + s1 + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Search product category by name or type " + s1 + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to search product category by name or type " + s1 + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     * Search Product Category
     *
     * @param filterList
     * @param page
     * @param pageSize
     * @param sortBy
     * @param sortOrder
     * @return
     * @Author Darshan
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterValue() != "") {
                        if (searchModel.getFilterColumn() != "") {
//                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                            return getByNameOrType(searchModel.getFilterValue(), pageRequest);
//                        }
                            if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Name")) {
                                return searchProductCategory(searchModel.getFilterValue(), pageRequest, "Name");
                            }
                            if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Type")) {
                                return searchProductCategory(searchModel.getFilterValue(), pageRequest, "Type");
                            }
                        } else {
                            return getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                        }
                    } else {
                        return getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                    }
                }
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            logger.error("Unable to serch product category :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), ex.getStackTrace());
        }
        return null;
    }

    /**
     * Get All Product Category By Type
     *
     * @param Type
     * @return
     * @Author Darshan
     */
    public GenericDataDTO getAllProductCategoriesByType(String Type) {
        String SUBMODULE = getModuleNameForLog() + "[getAllProductCategoriesByType()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductCategoryDto> productCategoryDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productCategoryDtoList = productCategoryRepository.findAllByTypeAndStatusAndIsDeletedIsFalse(Type, CommonConstants.ACTIVE_STATUS).stream().map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            } else {
                productCategoryDtoList = productCategoryRepository.findAllByTypeAndStatusAndIsDeletedIsFalseAndMvnoIdIn(Type, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            if (productCategoryDtoList.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all product category by type " + Type + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(productCategoryDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all product category by type " + Type + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product category by type " + Type + " : request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

//    /**
//     Get All
//     * @Author Darshan
//     * @return
//     */
//    public List<ProductCategory> getAllProductCategory(){
//        List<ProductCategory> list = new ArrayList<>();
//        list = productCategoryRepository.getall();
//        return list;
//    }

    /**
     * Get All Active Product Category
     *
     * @return
     * @Author Darshan
     */
    public GenericDataDTO getAllActiveProductCategories() {
        String SUBMODULE = getModuleNameForLog() + "[getAllActiveProductCategories()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductCategoryDto> productCategoryDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productCategoryDtoList = productCategoryRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream().map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            } else {
                productCategoryDtoList = productCategoryRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            productCategoryDtoList.sort(Comparator.comparing(ProductCategoryDto::getId).reversed());
            if (productCategoryDtoList.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all active product category :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(productCategoryDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all active product category :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active product category : request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     * Get all Active Product Category By Customer Bind
     *
     * @return
     * @Author Darshan
     */
    public GenericDataDTO getAllActiveProductCategoriesByCB() {
        String SUBMODULE = getModuleNameForLog() + "[getAllActiveProductCategoriesByCB()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductCategoryDto> productCategoryDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productCategoryDtoList = productCategoryRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS)
                        .stream()
                        .filter(productCategory -> productCategory.getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                productCategory.getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND))
                        .map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            } else {
                productCategoryDtoList = productCategoryRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1))
                        .stream()
                        .filter(productCategory -> productCategory.getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                productCategory.getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND))
                        .map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            if (productCategoryDtoList.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all product category by customer bind type :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(productCategoryDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all product category by customer bind type :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product category by customer bind type : request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

    /**
     * Find duplicate product category name at save and update
     *
     * @param productCategoryDto
     * @param operation
     * @return
     * @Author Darshan
     */
    public boolean duplicateVarification(ProductCategoryDto productCategoryDto, Integer operation) {
        try {
            boolean flag = false;
            String name = productCategoryDto.getName();
            if (name != null) {
                name = name.trim();
                Long count = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = productCategoryRepository.countByNameAndIsDeletedIsFalse(name);
                } else if (getMvnoIdFromCurrentStaff() != 1) {
                    count = productCategoryRepository.countByNameAndIsDeletedIsFalseAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (operation.equals(CommonConstants.OPERATION_ADD)) {
                    if (count == 0) {
                        flag = true;
                    }
                } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                    if (count >= 1) {
                        Long countEdit = null;
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            countEdit = productCategoryRepository.countByNameAndIdAndIsDeletedIsFalse(name, productCategoryDto.getId());
                        } else {
                            countEdit = productCategoryRepository.countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(name, productCategoryDto.getId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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

    /**
     * Validate Product Category at Save and Edit
     *
     * @param entityDTO
     */
    public void validateEntity(ProductCategoryDto entityDTO) {
        try {
            boolean isHasCas = entityDTO.isHasCas();
            boolean isHasMac = entityDTO.isHasMac();
            boolean isHasSerial = entityDTO.isHasSerial();
            boolean isHasPort = entityDTO.isHasPort();
            boolean isHasTrackable = entityDTO.isHasTrackable();
            if (isHasMac && !isHasSerial && !isHasTrackable) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Serial", null);
            }
            if (isHasTrackable && !isHasSerial) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Serial", null);
            }
            if (isHasPort) {
                if (!isHasMac && !isHasSerial) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Mac/ Has Serial", null);
                }
            }
            if (isHasCas) {
                if (isHasMac) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please only select Has Serial ", null);
                } else if (!isHasSerial) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Serial", null);
                }
            }
            if (isHasCas && (entityDTO.getDtvCategory() == null || entityDTO.getDtvCategory().equals(""))) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select dtv category", null);
            }
            if (entityDTO.type.equalsIgnoreCase("CustomerBind, NA") || entityDTO.type.equalsIgnoreCase("NA, NetworkBind") || entityDTO.type.equalsIgnoreCase("CustomerBind, NA, NetworkBind")) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "This combination of product category type is not valid", null);
            }
            List<String> paramNameList = new ArrayList<>();
            for (SpecificationParametersDTO item : entityDTO.getSpecificationParametersDTOList()) {
                if (paramNameList.contains(item.getParamName())) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ParamName is already exists", null);
                }

                if(item.getIsMultiValueParam()!=null && item.getIsMultiValueParam())
                {
                    item.getParamMultiValues().stream().forEach(data->{
                        if(data.length()>40)
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"40 Character value are allowed for Parameter "+item.getParamName(), null);
                    });
                }

                if (item.getParamName() != null) {
                    paramNameList.add(item.getParamName());
                }
            }
            if (!entityDTO.getSpecificationParametersDTOList().isEmpty()) {
                if (entityDTO.type.equalsIgnoreCase(CommonConstants.NA_Bind)) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Do not bind specification parameters if product category is NA type", null);
                }
                //if (!isHasMac && !isHasMac) {
                    //throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Do not bind specification parameters if product category has no any condition", null);
                //}
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get Entity By Id
     *
     * @param id
     * @return
     * @Author Kalp
     */
    @Override
    public ProductCategoryDto getEntityById(Long id) {
        try {
            ProductCategory item = productCategoryRepository.findById(id).orElse(null);
            ProductCategoryDto productCategoryDto = new ProductCategoryDto();
            productCategoryDto.setId(item.getId());
            productCategoryDto.setName(item.getName());
            productCategoryDto.setUnit(item.getUnit());
            productCategoryDto.setType(item.getType());
            productCategoryDto.setStatus(item.getStatus());
            productCategoryDto.setDeviceType(item.getDeviceType());
            List<SpecificationParametersDTO> specificationParametersDTOS = specificationParametersService.getDTOByPCID(item.getId());
            productCategoryDto.setSpecificationParametersDTOList(specificationParametersDTOS);
            productCategoryDto.setMvnoId(item.getMvnoId());
            productCategoryDto.setIsDeleted(item.getIsDeleted());
            productCategoryDto.setProductId(item.getProductId());
            productCategoryDto.setHasMac(item.isHasMac());
            productCategoryDto.setHasCas(item.isHasCas());
            productCategoryDto.setHasSerial(item.isHasSerial());
            productCategoryDto.setHasTrackable(item.isHasTrackable());
            productCategoryDto.setHasPort(item.isHasPort());
            productCategoryDto.setDtvCategory(item.getDtvCategory());
            return productCategoryDto;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }




}
