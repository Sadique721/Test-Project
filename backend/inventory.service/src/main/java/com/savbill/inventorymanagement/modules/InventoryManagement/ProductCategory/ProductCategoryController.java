package com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.PRODUCT_CATEGORY)
public class ProductCategoryController extends ExBaseAbstractController<ProductCategoryDto> {
    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        super(productCategoryService);
    }
    @Autowired
    Tracer tracer;

    @Override
    public String getModuleNameForLog() {
        return "[ProductCategoryController]";
    }

    private static final Logger LOGGER = Logger.getLogger(ProductCategoryController.class);

    /**
     * Save Product API
     *
     * @param entityDTO
     * @param result
     * @param req
     * @return
     * @throws Exception
     * @Author Darshan
     */
    @PreAuthorize("validatePermission(\"" +  ACLMenuConstants.Product_Category.PRODUCT_CATEGORY_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody ProductCategoryDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[save()]";
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            productCategoryService.validateEntity(entityDTO);
            boolean flag = productCategoryService.duplicateVarification(entityDTO, CommonConstants.OPERATION_ADD);
            if (entityDTO.getName().length() > 250 || entityDTO.getUnit().length() > 100) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
            } else {
                if (flag) {
                    dataDTO.setData(super.save(entityDTO, result, req));
                    dataDTO.setResponseCode(HttpStatus.OK.value());
                    dataDTO.setResponseMessage(MessageConstants.CREATE_SUCCESSFUL);
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product Category"+LogConstant.LOG_BY_NAME+entityDTO.getName() +  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                } else {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.PRODUCT_CATEGORY_NAME_EXITS);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product Category" +LogConstant.LOG_BY_NAME+entityDTO.getName()+  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL );

                }
            }
        } catch (CustomValidationException ce) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product Category"+LogConstant.LOG_BY_NAME+entityDTO.getName() +  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE +ce.getMessage()+LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value() );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    /**
     * Update Product Category API
     *
     * @param entityDTO
     * @param result
     * @param req
     * @return
     * @throws Exception
     * @Author Darshan
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" +  ACLMenuConstants.Product_Category.PRODUCT_CATEGORY_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody ProductCategoryDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[update()]";
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        ProductCategory old = productCategoryService.getById(entityDTO.getId());
        ProductCategory oldClone = new ProductCategory(old);
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            productCategoryService.getEntityForUpdateAndDelete(entityDTO.getId());
            productCategoryService.validateEntity(entityDTO);
            boolean flag = productCategoryService.duplicateVarification(entityDTO, CommonConstants.OPERATION_UPDATE);
            if (entityDTO.getName().length() > 250 || entityDTO.getUnit().length() > 100) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
            } else {
                if (flag) {
                    dataDTO.setData(super.update(entityDTO, result, req));
                    dataDTO.setResponseCode(HttpStatus.OK.value());
                    dataDTO.setResponseMessage(MessageConstants.UPDATE_SUCCESSFUL);
                    ProductCategory productCategory = productCategoryMapper.dtoToDomain(entityDTO , new CycleAvoidingMappingContext());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Product category" +   LogConstant.LOG_BY_NAME+entityDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Updated Product Category "+UpdateDiffFinder.getUpdatedDiff(oldClone , productCategory)+ LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                } else {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.PRODUCT_CATEGORY_NAME_EXITS);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Product category" +  LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED+ LogConstant.LOG_STATUS_CODE + APIConstants.FAIL );

                }
            }
        } catch (CustomValidationException ce) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Update Product category" +  LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE +ce.getMessage()+LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return dataDTO;
    }

    /**
     * Delete Product Category API
     *
     * @param id
     * @param req
     * @return
     * @throws Exception
     * @Author Darshan
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product_Category.PRODUCT_CATEGORY_DELETE +"\")")
    @DeleteMapping("/delete/{id}")
    public GenericDataDTO delete(@PathVariable("id") Long id, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[delete()]";
        GenericDataDTO dataDTO = new GenericDataDTO();
        ProductCategoryDto productCategoryDto1=new ProductCategoryDto();
        try {
            productCategoryService.getEntityForUpdateAndDelete(id);
            boolean flag = productCategoryService.deleteVerification(id.intValue());
            TraceContext traceContext =tracer.currentSpan().context();
            MDC.put("type", "Delete");
            MDC.put("userName", getLoggedInUser().getUsername());
            MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
            MDC.put("spanId", traceContext.spanIdString());

            if (flag) {
                ProductCategory productCategory = productCategoryService.deleteEntity(id);
                ProductCategoryDto productCategoryDto = productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext());
                dataDTO.setData(productCategoryDto);
                dataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Product category"+LogConstant.LOG_BY_NAME+productCategoryDto.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            } else {
                ProductCategory productCategory = productCategoryService.getById(id);
                productCategoryDto1 = productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext());
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(DeleteContant.PRODUCT_CATEGORY_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Product category"+LogConstant.LOG_BY_NAME+productCategoryDto1.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR+DeleteContant.PRODUCT_CATEGORY_EXITS+LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (CustomValidationException ex) {
            ProductCategory productCategory = productCategoryService.getById(id);
            productCategoryDto1 = productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext());
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Product category"+LogConstant.LOG_BY_NAME+productCategoryDto1.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE +HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;
    }

//    /**
//     Search Product Category By Name
//     * @Author Darshan
//     * @param requestDTO
//     * @return
//     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\""  + AclConstants.OPERATION_PRODUCT_CATEGORY_VIEW + "\")")
//    @PostMapping("/searchByNameCategory")
//    public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = productCategoryService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }

    /**
     * Get All Active Product Category By Type
     *
     * @param Type
     * @return
     * @Author Darshan
     */
    @GetMapping("/getAllProductCategoriesByType")
    public GenericDataDTO getAllProductCategoriesByType(@Valid @RequestParam String Type) {
        return productCategoryService.getAllProductCategoriesByType(Type);
    }

//    @GetMapping("/getallproductbycustomerbind")
//    public List<ProductCategory> getAllProduct() {
//        return productCategoryService.getAllProductCategory();
//    }

    /**
     * Get All Active Product Category
     *
     * @return
     * @Author Darshan
     */
    @GetMapping("/getAllActiveProductCategories")
    public GenericDataDTO getAllActiveProductCategories() {
        return productCategoryService.getAllActiveProductCategories();
    }

    /**
     * Get All Active Customer Binded Product Category
     *
     * @return
     * @Author Darshan
     */
    @GetMapping("/getAllActiveProductCategoriesByCB")
    public GenericDataDTO getAllActiveProductCategoriesByCB() {
        return productCategoryService.getAllActiveProductCategoriesByCB();
    }
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product_Category.PRODUCT_CATEGORY + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
        return super.getAll(requestDTO);
    }
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product_Category.PRODUCT_CATEGORY +  "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
    try {
        ProductCategoryDto productCategoryDto = (ProductCategoryDto) genericDataDTO.getData();
        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Entity by id : " +id  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS+LogConstant.LOG_STATUS_CODE +APIConstants.SUCCESS);
    }catch (Exception ex){
        LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Entity by id : " +id  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE +HttpStatus.NOT_ACCEPTABLE.value());

    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product_Category.PRODUCT_CATEGORY +"\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter ,HttpServletRequest req) {
                 TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req);
            if(genericDataDTO.getDataList().isEmpty()) {

                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Product Category By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Product Category By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
              LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search Product Category By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ APIConstants.ERROR_MESSAGE+ ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

//For get the user First Name
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
