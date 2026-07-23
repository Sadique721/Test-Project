package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
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
import java.util.List;

@RestController
@Api(value = "WarehouseManagementController", description = "REST APIs related to warehouse Entity!!!!", tags = "warehouse-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.WAREHOUSE_MANAGEMENT)
public class WarehouseManagementController extends ExBaseAbstractController<WareHouseDto> {

    private static final String MODULE = " [WarehouseManagementController] ";
    @Autowired
    WarehouseManagementServiceImpl warehouseManagementService;
    @Autowired
    private Tracer tracer;

    @Autowired
    private WarhouseMapper warhouseMapper;

    public WarehouseManagementController(WarehouseManagementServiceImpl warehouseManagementService,
                                         WarehouseManagementRepository warehouseManagementRepository) {
        super(warehouseManagementService);
        this.warehouseManagementRepository = warehouseManagementRepository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[WarehouseManagementController]";
    }

    private static final Logger LOGGER = Logger.getLogger(WarehouseManagementController.class);
    private final WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getUsername());
//        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
//        MDC.put("spanId",traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setDataList(warehouseManagementService
                    .getAllEntities());
            genericDataDTO.setTotalRecords(warehouseManagementService
                    .getAllEntities().size());
//            LOGGER.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}"+ getModuleNameForLog()+genericDataDTO.getResponseCode()+ genericDataDTO.getResponseCode());

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
//            LOGGER.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}"+getModuleNameForLog()+genericDataDTO.getResponseCode()+ genericDataDTO.getResponseCode()+ex.getMessage());
        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllActiveWarehouse")
    public GenericDataDTO getAllActiveWarehouse() {
//        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
        return warehouseManagementService.getAllActiveWarehouse();
    }

//    @GetMapping("/getAllWarehouseView")
//    public GenericDataDTO getAllWarehouseView() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(warehouseManagementService
//                    .getAllWarehouseView());
//            genericDataDTO.setTotalRecords(warehouseManagementService
//                    .getAllWarehouseView().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter, req);
            if (genericDataDTO.getDataList().isEmpty()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Warehouse Management By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            } else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Warehouse Management By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Warehouse Management By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + HttpStatus.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody WareHouseDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = warehouseManagementService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                // To compare parentSAIds and SAIds
                String warehouseOperation = "SaveWarehouseOperation";
                warehouseManagementService.validateEntity(entityDTO, warehouseOperation);
                genericDataDTO = super.save(entityDTO, result, req);
                WareHouseDto caseEntity = (WareHouseDto) genericDataDTO.getData();
//                warehouseManagementService.saveParentServicearea(entityDTO);
                warehouseManagementService.sharedWarehouseTeamMapping(caseEntity.getId(), CommonConstants.OPERATION_ADD);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create WareHouse" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.WAREHOUSE_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create WareHouse" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + MessageConstants.WAREHOUSE_NAME_EXITS + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (CustomValidationException exception){
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(exception.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create WareHouse" + LogConstant.LOG_BY_NAME+entityDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + exception.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception e){
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create WareHouse" + LogConstant.LOG_BY_NAME+entityDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.EXPECTATION_FAILED.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody WareHouseDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            WareHouse old = warehouseManagementService.getWareHouseById(entityDTO.getId());
            WareHouse oldClone = new WareHouse(old);
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = warehouseManagementService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
            if (flag) {
                // To compare parentSAIds and SAIds
                String warehouseOperation = "UpdateWarehouseOperation";
                warehouseManagementService.validateEntity(entityDTO, warehouseOperation);
                genericDataDTO = super.update(entityDTO, result, req);
                WareHouseDto caseEntity = (WareHouseDto) genericDataDTO.getData();
                warehouseManagementService.sharedWarehouseTeamMapping(caseEntity.getId(), CommonConstants.OPERATION_UPDATE);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update WareHouse" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Updated Details " + UpdateDiffFinder.getUpdatedDiff(oldClone, genericDataDTO.getData()) + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.WAREHOUSE_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Warehouse with old name : " + " " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + MessageConstants.WAREHOUSE_NAME_EXITS + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Warehouse :" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Warehouse :" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllParentServiceAreaList")
    public GenericDataDTO getAllParentServiceAreaList(HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setDataList(warehouseManagementService.getAllParentServiceAreas());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllParentServiceAreaListByWarehouseId" + "/{warehouseId}")
    public GenericDataDTO getAllParentServiceAreaListByWarehouseId(@PathVariable Integer warehouseId, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getAllParentServiceAreaListByWarehouseId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setDataList(warehouseManagementService.getAllParentServiceAreasByWarehouseId(warehouseId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All  Parent Service Area List By WareHouseId : " + warehouseId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All  Parent Service Area List By WareHouseId : " + warehouseId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_EDIT + "\")")
    @PostMapping("/getAllByWarehouseIds")
    public GenericDataDTO getAllByWarehouseIds(@RequestBody List<Long> warehouseIds, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getAllByWarehouseIds()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setDataList(warehouseManagementService.getAllByWarehouseIds(warehouseIds));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Ware House" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE_DELETE + "\")")
    @DeleteMapping("/delete/{id}")
    public GenericDataDTO delete(@PathVariable("id") Long id, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        WareHouseDto wareHouseDto = new WareHouseDto();
        try {
            WareHouseDto wareHouseDto1 = new WareHouseDto();
            String name = wareHouseDto1.getName();
            WareHouse wareHouse = warehouseManagementService.getWareHouseById(id);
            //check Inward Bind
            warehouseManagementService.getEntityForUpdateAndDelete(id);
            boolean verifiedByInward = warehouseManagementService.deleteVerification(Math.toIntExact(id));
            boolean verifiedRequest = warehouseManagementService.deleteVerificationInRequest(id);
            if (verifiedByInward || verifiedRequest) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.WAREHOUSE_DELETE_EXIST);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete WareHouse Management" + LogConstant.LOG_BY_NAME + wareHouse.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + DeleteContant.WAREHOUSE_DELETE_EXIST + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete WareHouse" + LogConstant.LOG_BY_NAME + wareHouse.getName() + " And By Id : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
                warehouseManagementRepository.deleteById(id);
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(id);
                wareHouseTeamsMappingRepo.deleteAll(wareHouseTeamsMappingList);
                warehouseManagementService.deleteProductWarehouseMapping(id);
                warehouseManagementService.sharedWarehouseTeamMapping(id, CommonConstants.OPERATION_DELETE);
                genericDataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            }

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Unable to Delete WareHouse Management" + LogConstant.LOG_BY_NAME + wareHouseDto.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping("/getWarhouseView/{id}")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE + "\")")
    public GenericDataDTO getWarehouseView(@PathVariable Long id) {
        return warehouseManagementService.getWarehouseView(id);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Warehouse.WAREHOUSE + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    //For get the first nmae of logged in user
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
