package com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.InventoryDashboard.DashboardController;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
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
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.BULKCONSUMPTION)
@Api(value = "BulkConsumptionController", description = "REST APIs related to BulkConsumption Entity!!!!", tags = "bulk_Consumption_Controller")
public class BulkConsumptionController extends ExBaseAbstractController<BulkConsumptionDto> {

    @Autowired
    BulkConsumptionServiceImp bulkConsumptionService;

    @Autowired
    InOutWardMACService inOutWardMACService;

    public BulkConsumptionController(BulkConsumptionServiceImp productBundleService) {
        super(productBundleService);
    }

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    BulkConsumptionMapper bulkConsumptionMapper;

    @Autowired
    Tracer tracer;

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class);

    @Override
    public String getModuleNameForLog() {
        {
            return "[BulkConsumptionController]";
        }
    }

    @Override
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION_CREATE +"\")")
    public GenericDataDTO save(@RequestBody BulkConsumptionDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (entityDTO.getBulkConsumptionName().length() > 250) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Bulk-Consuption" + LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName()  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() +APIConstants.ERROR_MESSAGE+ MessageConstants.INPUT_SIZE_ERROR+ LogConstant.LOG_STATUS  + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE);
            } else {
                boolean flag = bulkConsumptionService.duplicateVerifyAtSave(entityDTO.getBulkConsumptionName());
                bulkConsumptionService.validateBulkConsumption(entityDTO);
                if (flag) {
                    if (getMvnoIdFromCurrentStaff() != null) {
                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                    }
                    BulkConsumptionDto bulkConsumptionDto = bulkConsumptionService.saveEntity(entityDTO);
                    genericDataDTO.setData(bulkConsumptionDto);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Bulk-Consuption"+LogConstant.LOG_BY_NAME+entityDTO.getBulkConsumptionName()  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE+ APIConstants.SUCCESS);
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.BULK_COSUMPTION);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Bulk-Consuption"+LogConstant.LOG_BY_NAME+entityDTO.getBulkConsumptionName()  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+LogConstant.LOG_ERROR + APIConstants.NOT_FOUND+LogConstant.LOG_STATUS_CODE  + HttpStatus.NOT_ACCEPTABLE.value());
                }
            }

        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Bulk-Consuption"+LogConstant.LOG_BY_NAME+entityDTO.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + e.getMessage() +  LogConstant.LOG_STATUS_CODE+ HttpStatus.NOT_ACCEPTABLE.value());
        }catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Bulk-Consuption"+LogConstant.LOG_BY_NAME+entityDTO.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + e.getMessage() +  LogConstant.LOG_STATUS_CODE+ HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @Override
  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION_EDIT +"\")")
    public GenericDataDTO update(@RequestBody BulkConsumptionDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            bulkConsumptionService.getEntityForUpdateAndDelete(entityDTO.getId());
            BulkConsumption old = bulkConsumptionService.getBulkConsumptionById(entityDTO.getId());
            BulkConsumption oldClone = new BulkConsumption(old);
            boolean flag = bulkConsumptionService.duplicateVerifyAtEdit(entityDTO.getBulkConsumptionName(), entityDTO.getId());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                //dataDTO = super.update(entityDTO, result, authentication, req);
                BulkConsumptionDto bulkConsumptionDto = bulkConsumptionService.updateEntity(entityDTO);
                dataDTO.setData(bulkConsumptionDto);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                dataDTO.setResponseMessage(MessageConstants.UPDATE_SUCCESSFUL);
                BulkConsumption bulkConsumption = bulkConsumptionMapper.dtoToDomain(entityDTO , new CycleAvoidingMappingContext());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Bulk-Consumptions" +LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() +" Updated Details "+ UpdateDiffFinder.getUpdatedDiff(oldClone , bulkConsumption) +LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Bulk-Consumptions"+LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + MessageConstants.PRODUCT_NAME_EXITS +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Bulk-Consumptions" +LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }



    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION_DELETE +"\")")
    public GenericDataDTO delete(@RequestBody BulkConsumptionDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("spanId", traceContext.spanIdString());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        try {
            bulkConsumptionService.getEntityForUpdateAndDelete(entityDTO.getId());
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            dataDTO = super.delete(entityDTO, req);
            bulkConsumptionService.deleteBulkConsumption(entityDTO);
            dataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Bulk-Consumption" + LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Bulk-Consumption"+LogConstant.LOG_BY_NAME + entityDTO.getBulkConsumptionName() + entityDTO.getId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
     }


     @PostMapping("/searchByNamebybulkconsumption")
     @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION +"\")")
     public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
         TraceContext traceContext =tracer.currentSpan().context();
         MDC.put("type", "Search");
         MDC.put("userName", getLoggedInUser().getUsername());
         MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
         MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = bulkConsumptionService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk consumption using keyword : "+requestDTO.getFilters().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS +LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            if (genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.No_RECORD_FOUND);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption using keyword" +requestDTO.getFilters().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_NO_RECORD_FOUND  +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption using keyword" +requestDTO.getFilters().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/approveStatus")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION_CREATE +"\")")
    public GenericDataDTO  approveStatus(@Valid @RequestBody BulkConsumptionDto bulkConsumptionDto, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(bulkConsumptionService.saveInwardApproval(bulkConsumptionDto.getId(), bulkConsumptionDto.getApprovalStatus(), bulkConsumptionDto.getApprovalRemark()));

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve Bulk-Consumption"+LogConstant.LOG_BY_NAME+bulkConsumptionDto.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve Bulk-Consumption"+LogConstant.LOG_BY_NAME+bulkConsumptionDto.getBulkConsumptionName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            throw new RuntimeException(ex.getMessage());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    @GetMapping("/getById")
   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION +"\")")
    public GenericDataDTO  findByBulkId(@RequestParam("id") Long id, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            BulkConsumptionDto bulkConsumption=bulkConsumptionService.findByBulkId(id);
            genericDataDTO.setData(bulkConsumption);
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption By Id : "+id+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption By Id : "+id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Bulk-Consumption By Id : "+id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getBulkConsumptionMapping")
  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION_VIEW_MAC +"\")")
    public GenericDataDTO getBulkConsumptionMacMapping(@RequestParam(name = "bulkconsumptionId") Long bulkconsumptionId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Bulk Consumption Mapping By Id : "+ bulkconsumptionId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getByBulkConsumptionId(bulkconsumptionId));
            genericDataDTO.setTotalRecords(inOutWardMACService.getByBulkConsumptionId(bulkconsumptionId).size());
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Consumption Mapping By Id : " + bulkconsumptionId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
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
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Bulk_Consumption.BULK_CONSUMPTION +"\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
        return super.getAll(requestDTO);
    }


//For get loggen in user firstname
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
