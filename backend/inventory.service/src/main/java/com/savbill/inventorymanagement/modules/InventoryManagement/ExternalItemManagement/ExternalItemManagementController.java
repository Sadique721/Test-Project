package com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement;

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
import com.savbill.inventorymanagement.modules.Customers.CustomerService;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerService;
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
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.EXTERNAL_ITEM_MANAGEMENT)
public class ExternalItemManagementController extends ExBaseAbstractController<ExternalItemManagementDTO> {
//    @Autowired
//    AuditLogService auditLogService;

    @Autowired
    private Tracer tracer;
    @Autowired
    private ExternalItemManagementService externalItemManagementService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PartnerService partnerService;

    @Autowired
    private ExternalItemManagementMapper externalItemManagementMapper;

    private static final Logger LOGGER= Logger.getLogger(ExternalItemManagementController.class);

    public ExternalItemManagementController(ExternalItemManagementService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemManagementController]";
    }
    ExternalItemManagementDTO externalItemManagementDTO = new ExternalItemManagementDTO();

    //Save
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM_CREATE +"\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody ExternalItemManagementDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if(entityDTO.getOwnershipType() == null)
                entityDTO.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
            ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.saveEntity(entityDTO);
            genericDataDTO.setData(externalItemManagementDTO);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_EXTERNAL_ITEM_MANAGEMENT, AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ADD, req.getRemoteAddr(), null, externalItemManagementDTO.getId(), externalItemManagementDTO.getExternalItemGroupNumber().toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create External item" +LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create External item" +LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Get External Item Group Details By Product And ServiceAreaId
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getExternalItemGroupDetailsByProductAndServiceAreaId")
    public GenericDataDTO getExternalItemGroupDetailsByProductAndServiceAreaId(@RequestParam(name = "productId")Long productId, @RequestParam(name = "serviceAreaId")Long serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(externalItemManagementService.getExtrenalItemDetailsByProductAndServiceAreaId(productId, serviceAreaId));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Extrenal item" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Extrenal item" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Update
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM_EDIT +"\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody ExternalItemManagementDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            externalItemManagementService.getEntityForUpdateAndDelete(entityDTO.getId());
            ExternalItemManagement old = externalItemManagementService.getExternalItemById(entityDTO.getId());
            ExternalItemManagement oldClone = new ExternalItemManagement(old);
            ExternalItemManagementDTO existingExternalItem = externalItemManagementService.getEntityById(entityDTO.getId());
            ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.updateEntity(entityDTO);
            genericDataDTO.setData(externalItemManagementDTO);
            ExternalItemManagement externalItemManagement = externalItemManagementMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update External item"+LogConstant.LOG_BY_NAME+entityDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Updated Item "+ UpdateDiffFinder.getUpdatedDiff(oldClone , externalItemManagement)+ LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update External item" +LogConstant.LOG_BY_NAME+ entityDTO.getExternalItemGroupNumber() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ce.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update External item"+LogConstant.LOG_BY_NAME+entityDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.EXPECTATION_FAILED.value());

        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Get All External Item Group By Product And Staff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllExternalItemGroupByProductAndStaff")
    public GenericDataDTO getAllExternalItemByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All External Items By Product and Staff" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(externalItemManagementService.getAllExternalItemByProductAndStaff(productId, ownerId));
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All External Items By Product and Staff" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //Delete
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM_DELETE +"\")")
    @DeleteMapping("/delete/{id}")
    public GenericDataDTO delete(@PathVariable Long id, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            externalItemManagementService.getEntityForUpdateAndDelete(id);
            ExternalItemManagementDTO entityDTO = externalItemManagementService.getEntityById(id);
            boolean flag = externalItemManagementService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                genericDataDTO = super.delete(entityDTO, req);
                ExternalItemManagementDTO externalItemManagementDTO = (ExternalItemManagementDTO) genericDataDTO.getData();
                if (externalItemManagementDTO != null)
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_EXTERNAL_ITEM_MANAGEMENT,
//                        AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_DELETE, req.getRemoteAddr(), null, externalItemManagementDTO.getId(), externalItemManagementDTO.getExternalItemGroupNumber());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete External Item"+LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.EXTERNAL_ITEM_NUMBER_DELETE_EXIST);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +  "Delete External Item"+LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NOT_FOUND + LogConstant.LOG_STATUS_CODE +  HttpStatus.NOT_ACCEPTABLE.value());
            }
        }catch(Exception ex){
                 genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                 genericDataDTO.setResponseMessage(ex.getMessage());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +  "Delete External Item"+LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber()  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NOT_FOUND +APIConstants.ERROR_MESSAGE+ex.getMessage() + LogConstant.LOG_STATUS_CODE  + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

@PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM_CREATE +"\")")
@PutMapping("/externalItemApproval")
    public GenericDataDTO externalItemGroupApproval(@Valid @RequestBody ExternalItemManagementDTO externalItemManagementDTO, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
    try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(externalItemManagementService.saveExternalItemGroupApproval(externalItemManagementDTO.getId(), externalItemManagementDTO.getApprovalStatus(), externalItemManagementDTO.getApprovalRemark()));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "External item Group Approved" + LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "External item Group Approved"+LogConstant.LOG_BY_NAME+externalItemManagementDTO.getExternalItemGroupNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Search
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM +"\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk consumption using keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS +LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            genericDataDTO= super.search(page, pageSize, sortOrder, sortBy, filter , req);

            if (genericDataDTO.getDataList()!=null && genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.No_RECORD_FOUND);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption using keyword : " +filter.getFilter().get(0).getFilterValue()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_NO_RECORD_FOUND  +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    //Get All PopManagement With Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM +"\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
        return super.getAll(requestDTO);
    }
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.External_Item.EXTERNAL_ITEM + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        ExternalItemManagementDTO externalItemManagementDTO = (ExternalItemManagementDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH, AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, popManagementDTO.getId(), popManagementDTO.getName());
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
    @GetMapping("/getAllItemByExternalItemBaseOnStatus")
    public GenericDataDTO getAllExternalItemBaseOnStatus(@RequestParam(name = "ownerId") Long ownerId,@RequestParam(name = "ownershipType") String ownershipType, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(externalItemManagementService.getAllExtenralItemBaseOnStatus(ownerId,ownershipType));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Item Based On Status By OwnerId : "+ownerId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
         } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Item Base On StatusBy OwnerId : "+ownerId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

         }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
    @PostMapping("/getAllCustomerBasedOnLoginStaffServiceArea")
    public GenericDataDTO getAllCustomerBasedOnLoginStaffServiceArea( @RequestBody List<Long> serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(externalItemManagementService.getAllCustomerBasedOnServiceArea(serviceAreaId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Customer Based On Login Staff By serviceAreaId : "+ serviceAreaId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch all Customers Based on Login Staff By serviceAreaId : "+ serviceAreaId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
    @PostMapping("/getCustomerListServiceArea")
    public GenericDataDTO getCustomerListServiceArea(@RequestParam Long serviceAreaId, @RequestBody PaginationRequestDTO requestDTO) {
        return customerService.getCustomerListServiceArea(serviceAreaId, requestDTO);
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
    @PostMapping("/getPartnerListServiceArea")
    public GenericDataDTO getPartnerListServiceArea(@RequestParam Integer serviceAreaId, @RequestBody PaginationRequestDTO requestDTO) {
        return partnerService.getPartnerListServiceArea(serviceAreaId, requestDTO);
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @PostMapping("/searchCustomerListServiceArea")
    public GenericDataDTO searchCustomerListServiceArea(@RequestParam Long serviceAreaId, @RequestBody PaginationRequestDTO requestDTO) {
        return customerService.searchCustomersByServiceArea(serviceAreaId, requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @PostMapping("/searchPartnerListServiceArea")
    public GenericDataDTO searchPartnerListServiceArea(@RequestParam Integer serviceAreaId, @RequestBody PaginationRequestDTO requestDTO) {
        return partnerService.searchPartnersByServiceArea(serviceAreaId, requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
    }
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
