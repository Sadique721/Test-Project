package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService extends ExBaseAbstractService<VendorDto,Vendor,Long> {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private VendorMapper vendorMapper;

    @PersistenceContext
    EntityManager entityManager;

    public VendorService(VendorRepo vendorRepo, VendorMapper vendorMapper) {
        super(vendorRepo, vendorMapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
    @Override
    public String getModuleNameForLog() {
        return "[VendorService]";
    }

    /**
     Find Vendor/Manufacturer By Id
     * @Author Darshan
     * @param id
     * @return
     */
    public VendorDto getVendor(Long id ) {
        String SUBMODULE = getModuleNameForLog() + " [getVendor()]";
        VendorDto vendorDto;
        try {
            vendorDto = new VendorDto();
            Vendor vendor = vendorRepo.findById(id).orElse(null);
            if (vendor != null) {
                vendorDto = vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext());
            }
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            logger.error("Unable to fetch manufacturer with id " + id + " : request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, HttpStatus.EXPECTATION_FAILED.value(), exception.getMessage());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), exception.getMessage(), null);
        }
        return vendorDto;
    }

//    public GenericDataDTO getAll(PaginationRequestDTO requestDTO) {
//        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
//        GenericDataDTO genericDataDTO=new GenericDataDTO();
//        try {
//            PageRequest pageRequest1=PageRequest.of(requestDTO.getPage(),requestDTO.getPageSize());
//            QVendor qVendor=QVendor.vendor;
//            BooleanExpression booleanExpression = qVendor.isNotNull()
//                    .and(qVendor.isDeleted.eq(false));
//            Page<Vendor> page=vendorRepo.findAll(booleanExpression, pageRequest1);
//            genericDataDTO.setDataList(page.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(page.getTotalElements());
//            genericDataDTO.setPageRecords(page.getNumberOfElements());
//            genericDataDTO.setCurrentPageNumber(page.getNumber() + 1);
//            genericDataDTO.setTotalPages(page.getTotalPages());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return genericDataDTO;
//    }

    /**
     Save Vendor
     * @Author Darshan
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public VendorDto saveEntity(VendorDto entity) throws Exception {
        return super.saveEntity(entity);
    }

    /**
     Find All Active Vendor
     * @Author Darshan
     * @return
     */
    public GenericDataDTO findAllVendor(){
        String SUBMODULE = getModuleNameForLog() + " [findAllVendor()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<VendorDto> vendorDtos = null;
        try{
            if (getMvnoIdFromCurrentStaff() == 1) {
                vendorDtos = vendorRepo.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream().map(vendor -> vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            } else {
                vendorDtos = vendorRepo.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(vendor -> vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            vendorDtos.sort(Comparator.comparing(VendorDto::getId).reversed());
            genericDataDTO.setDataList(vendorDtos);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all active manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Find vendorid id is deleted or not
     * @Author Darshan
     * @param id
     * @return
     */
    @Override
    public boolean deleteVerification(Integer id) {
        boolean flag = false;
        Long count = null;
        count = productRepository.countByVendorIdAndIsDeletedIsFalse(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     Find duplicate vendor name at save and update
     * @Author Darshan
     * @param vendorDto
     * @param operation
     * @return
     */
    public boolean duplicateVarification(VendorDto vendorDto, Integer operation) {
        try {
            boolean flag = false;
            String name = vendorDto.getName();
            if(name != null) {
                name = name.trim();
                Long count = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = vendorRepo.countByNameAndIsDeletedIsFalse(name);
                } else if (getMvnoIdFromCurrentStaff() != 1){
                    count = vendorRepo.countByNameAndIsDeletedIsFalseAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (operation.equals(CommonConstants.OPERATION_ADD)) {
                    if (count == 0) {
                        flag = true;
                    }
                } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                    if (count >= 1) {
                        Long countEdit = null;
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            countEdit = vendorRepo.countByNameAndIdAndIsDeletedIsFalse(name, vendorDto.getId());
                        } else {
                            countEdit = vendorRepo.countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(name, vendorDto.getId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
     Get Vendor List By Pagination
     * @Author Darshan
     * @param pageNumber
     * @param customPageSize
     * @param sortBy
     * @param sortOrder
     * @param filterList
     * @return
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Page<Vendor> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = vendorRepo.findAllByIsDeletedIsFalse(pageRequest);
            } else {
                paginationList = vendorRepo.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
            }
            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
            if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Search Vendor with Pagination
     * @Author
     * @param filterList
     * @param page
     * @param pageSize
     * @param sortBy
     * @param sortOrder
     * @return
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getVendorList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            logger.error("Unable to serch manufacturer :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, HttpStatus.EXPECTATION_FAILED.value(), HttpStatus.EXPECTATION_FAILED.getReasonPhrase(), ex.getStackTrace());
        }
        return null;
    }

    /**
     Get Vendor List
     * @Author Darshan
     * @param name
     * @param pageRequest
     * @return
     */
    public GenericDataDTO getVendorList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getVendorList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<Vendor> vendorList;
            if (getMvnoIdFromCurrentStaff() == 1)
                vendorList = vendorRepo.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                vendorList = vendorRepo.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != vendorList && 0 < vendorList.getSize()) {
                makeGenericResponse(genericDataDTO, vendorList);
            }
            if (vendorList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to search manufacturer by name " + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Search manufacturer by name " + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to seatch manufacturer by name" + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Delete Vendor
     * @Author Darshan
     * @paramvendorDto
     * @throws Exception
     */
    public Vendor deleteEntity(Long id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteEntity()] ";
        try {
            Vendor vendor = vendorRepo.findById(id).orElse(null);
            if (vendor!= null) {
                vendor.setDeleted(true);
                return vendorRepo.save(vendor);
            } else {
                return null;
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            logger.error("Unable to delete manufacturer with id " + id + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, HttpStatus.EXPECTATION_FAILED, e.getMessage());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(),null);
        }
    }

    public Vendor getById(Long id){
        return vendorRepo.findById(id).get();
    }
}
