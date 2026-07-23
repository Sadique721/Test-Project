package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.*;
import com.savbill.inventorymanagement.core.dto.*;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.Oltslots;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.NetworkDeviceService;
import com.savbill.inventorymanagement.modules.acl.constants.AclConstants;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.savbill.inventorymanagement.utils.GoogleMaps;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.NETWORK_DEVICE)
public class NetworkDeviceController extends ExBaseAbstractController<NetworkDeviceDTO> {
    private static String MODULE = " [NetworkDeviceController] ";
    @Autowired
    private NetworkDeviceService networkDeviceService;
    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;
    @Autowired
    private GoogleMaps googleMaps;

    NetworkDeviceDTO networkDeviceDTO = new NetworkDeviceDTO();
    @Autowired
    Tracer tracer;
    private static Logger LOGGER = Logger.getLogger(NetworkDeviceController.class);

    public NetworkDeviceController(NetworkDeviceService service) {
        super(service);
    }

    //@Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE + "\")")
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
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.No_RECORD_FOUND);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Network Device By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Network Device By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search network Device By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setDataList(networkDeviceService
                    .getAllEntities());
            genericDataDTO.setTotalRecords(networkDeviceService
                    .getAllEntities().size());

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
//        return super.getAllWithoutPagination();
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody NetworkDeviceDTO entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        boolean flag = networkDeviceService.deleteVerification(entityDTO.getId().intValue());
        try {
            if (flag) {
                entityDTO.setServiceAreaNameList(null);
                genericDataDTO = super.delete(entityDTO, req);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                NetworkDeviceDTO networkDevices = (NetworkDeviceDTO) genericDataDTO.getData();
                if (networkDevices != null)
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
//                        AclConstants.OPERATION_NETWORK_DEVICES_DELETE, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
//            logger.info("Deleting Network devices With name "+entityDTO.getName()+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network-Device" + LogConstant.LOG_BY_NAME + networkDevices.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
//            logger.error("Unable to Delete network device with name "+entityDTO.getName()+"  : Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network-Device" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody NetworkDeviceDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String name = networkDeviceService.getEntityToUpdate(entityDTO.getId()).getName();
        try {
            NetworkDeviceDTO casMasterDTO = networkDeviceService.getEntityById(entityDTO.getId());
            boolean flag = networkDeviceService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                String updatedValues = UpdateDiffFinder.getUpdatedDiff(casMasterDTO, entityDTO);

                entityDTO.setAvailableInPorts(casMasterDTO.getAvailableInPorts());
                entityDTO.setTotalInPorts(casMasterDTO.getTotalInPorts());
                entityDTO.setAvailablePorts(casMasterDTO.getAvailablePorts());
                entityDTO.setAvailableOutPorts(casMasterDTO.getAvailableOutPorts());
                entityDTO.setTotalOutPorts(casMasterDTO.getTotalOutPorts());

                NetworkDeviceDTO networkDevices = networkDeviceService.updateEntity(entityDTO);

//                String updatedValues = CommonUtils.getUpdatedDiff(networkDevices,entityDTO);
                genericDataDTO.setData(networkDevices);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
//                        AclConstants.OPERATION_NETWORK_DEVICES_EDIT, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
//                logger.info("Updating network is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + networkDevices.getName() + " , Updated Details " + updatedValues + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
//                logger.error("Unable To update Network devices With name "+name+"  :  Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NOT_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

            }
            return genericDataDTO;
        } catch (Exception ex) {
            //
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable To update Network devices With name "+name+"  :  Response : {{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody NetworkDeviceDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        NetworkDeviceDTO networkDeviceDTO1 = new NetworkDeviceDTO();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean flag = networkDeviceService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                NetworkDeviceDTO networkDevices = networkDeviceService.saveEntity(entityDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                genericDataDTO.setData(networkDevices);
//                logger.info("crating Network devices With name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "crating Network devices" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
//                        AclConstants.OPERATION_NETWORK_DEVICES_ADD, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
//                logger.error("Unable crete network devices With name "+entityDTO.getName()+"   :   Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
//                logger.error("Unable crete network devices With name "+entityDTO.getName()+"  :   Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "crating Network devices" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable crete network devices With name "+entityDTO.getName()+"  :   Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "crating Network devices" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getEntityById(id, req);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            NetworkDeviceDTO networkDevices = (NetworkDeviceDTO) genericDataDTO.getData();
            if (networkDevices != null) {
                DecimalFormat df = new DecimalFormat("0.0000000");

                if (networkDevices.getLatitude() != null && !networkDevices.getLatitude().isEmpty() && isNumeric(networkDevices.getLatitude()))
                    networkDevices.setLatitude(df.format(Double.parseDouble(networkDevices.getLatitude())));

                if (networkDevices.getLongitude() != null && !networkDevices.getLongitude().isEmpty() && isNumeric(networkDevices.getLongitude()))
                    networkDevices.setLongitude(df.format(Double.parseDouble(networkDevices.getLongitude())));
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Network Device" + LogConstant.LOG_BY_NAME + networkDevices.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Network Device" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[NetworkDeviceController]";
    }

    @GetMapping("/byServiceId/{serviceId}")
    public GenericDataDTO getEntityByServiceId(@PathVariable Long serviceId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch service id : " + serviceId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return GenericDataDTO.getGenericDataDTO(networkDeviceService.getNetworkDevicesByServiceAreaId(serviceId));
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch service id : " + serviceId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE_EDIT + "\")")
    @PostMapping("/updateNetwork")
    public GenericDataDTO updateNetworkDevice(@RequestBody NetworkDeviceDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [updateNetworkDevice()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean flag = networkDeviceService.duplicateVerifyAtEdit(requestDTO.getName(), requestDTO.getId().intValue());
            if (flag) {
                if (null == requestDTO.getId()) {
                    genericDataDTO.setResponseMessage("Please provide Network id!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + requestDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                    return genericDataDTO;
                }

                NetworkDevices networkDevices = networkDeviceService.getEntityToUpdate(requestDTO.getId());
//                String updatedValues = CommonUtils.getUpdatedDiff(networkDevices,requestDTO);
                if (null == networkDevices) {
                    genericDataDTO.setResponseMessage("Network Id is Not Available or not permitted to update");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + requestDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

                    return genericDataDTO;
                }
                List<Oltslots> oltSlots = networkDevices.getOltslotsList();
                networkDeviceService.UpdateNetworkDevice(requestDTO, oltSlots);
                NetworkDeviceDTO dtoResponse = networkDeviceService.getEntityById(requestDTO.getId());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                genericDataDTO.setData(dtoResponse);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + requestDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
//                        AclConstants.OPERATION_NETWORK_DEVICES_EDIT, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + requestDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + MessageConstants.NETWORK_DEVICE_NAME_EXITS + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Network Device" + LogConstant.LOG_BY_NAME + requestDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE + "\")")
    @Override
    @PostMapping("/list")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
        return super.getAll(requestDTO);
        //        return networkDeviceService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
    }

    @GetMapping("/hierarchy")
    public GenericDataDTO getHierarchy(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        try {
            genericDataDTO.setData(networkDeviceService.getHierarchy(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/deviceHierarchy")
    public GenericDataDTO getDeviceHierarchy(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(networkDeviceService.getDeviceHierarchy(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getOLTDeviceByPopId")
    public GenericDataDTO getOLTDeviceByPopId(@RequestParam Long id,HttpServletRequest req){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getOLTDeviceByPopId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        }catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getDNSplitterByOltId")
    public GenericDataDTO getDNSplitterByOltId(@RequestParam Long id,HttpServletRequest req){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getDNSplitterByOltId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        }catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getSNSplitterByDNSplitterId")
    public GenericDataDTO getSNSplitterByDNSplitterId(@RequestParam Long id,HttpServletRequest req){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getSNSplitterByDNSplitterId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        }catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("/parentDeviceMapping")
    public GenericDataDTO parentDeviceMapping(@Valid @RequestBody DeviceMappingDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<NetworkDeviceBindingsDTO> networkDeviceBindings = networkDeviceService.saveParentDeviceBindings(entityDTO);
            genericDataDTO.setDataList(networkDeviceBindings);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create parent device mapping By Device Id : " + entityDTO.getDeviceId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create parent device Mapping By Device Id : " + entityDTO.getDeviceId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("/deviceChildParentBinding")
    public GenericDataDTO deviceChildParentBinding(@Valid @RequestBody DevicePortMappingDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            List<NetworkDeviceBindingsDTO> networkDeviceBindings = networkDeviceService.deviceChildParentBinding(entityDTO);
            genericDataDTO.setDataList(networkDeviceBindings);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Device Child Parent Binding" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Device Child Parent Binding" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/boundParents")
    public GenericDataDTO boundParents(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.boundParents(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All bound parent network" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All bound parent network" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Network_Device.NETWORK_DEVICE_PARENT_MAPPING + "\")")
    @GetMapping("/availableParents")
    public GenericDataDTO availableParents(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.availableParents(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Network device parent Mapping" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Network device parent Mapping" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_DELETE + "\")")
    @DeleteMapping("/deleteDeviceMapping")
    public GenericDataDTO deleteDeviceMapping(@RequestParam Long id, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseMessage(networkDeviceService.deleteDeviceMapping(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Device Mapping" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete device mapping" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/checkPortAvailability")
    public GenericDataDTO checkPortAvailability(@RequestParam("parentDeviceId") Long parentDeviceId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(Arrays.asList(networkDeviceService.getPortsAvailability(parentDeviceId).toArray()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All available Ports" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All available Ports" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getAllInwardByProduct")
    public GenericDataDTO getAllInwardByProduct(@RequestParam(name = "productId") Long productId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(networkDeviceService.getAllInwardByProduct(productId));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Inward By Product" + productId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Inward By Product" + productId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PostMapping("/searchNetworkDevices")
    public GenericDataDTO searchNetworkDevices(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchNetworkDevicesPojo entity, HttpServletRequest req) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (entity != null) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                requestDTO = setDefaultPaginationValues(requestDTO);
                genericDataDTO = networkDeviceService.searchNetworkDevices(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Network Devices" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
            if (genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.No_RECORD_FOUND);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Search Network Devices" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NULL_VALUE + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
            }
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Network Devices" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Network Devices" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getNetworkDevicesByDeviceType")
    public GenericDataDTO getNetworkDevicesByDeviceType(@RequestParam(name = "deviceType") String deviceType) throws Exception {
        return networkDeviceService.findNetworkDevicesByType(deviceType);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("saveMappingData")
    public GenericDataDTO save(@RequestBody NetworkDeviceBindDTO networkDeviceBindDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            NetworkDeviceBind networkDeviceBindDTO1 = networkDeviceService.saveNetworks(networkDeviceBindDTO);
            genericDataDTO.setData(networkDeviceBindDTO1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Mapped Successfully");
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Mapping Data" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Mapping Data" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getAllMappingDataByDeviceId")
    public GenericDataDTO MappingData(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getAllMappingData(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch MappingData By Device Id : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch MappingData By Device Id : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //For get the user first name
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


    @PostMapping(value = "/getNearbyDevices")
    public ResponseEntity<?> getNearbyDevices(@RequestBody LocationVo locationVo, HttpServletRequest req) throws Exception {
        HashMap<String, Object> resp = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            resp = googleMaps.getNearbyDevices(locationVo, mvnoId);
            Integer responseCode = Integer.parseInt(resp.get(DocumentConstants.STATUS_CODE).toString());
            MDC.remove("type");
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetching nearby devices " + locationVo + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return apiResponse(responseCode, resp);
        } catch (Exception e) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetching nearby devices " + locationVo + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PutMapping(value = "/bindNetworkDevice")
    public ResponseEntity<?> bindNetworkDevice(@RequestParam Integer customerId, @RequestParam Long networkDeviceId,
                                               HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();

        Integer RESP_CODE = APIConstants.FAIL;
        try {
            //String updatedValues = CommonUtils.getUpdatedDiff(customersService.convertCustomersModelToCustomersPojo(cs),convertCityModelToCityPojo(old1));
            response.put("customer", networkDeviceService.bindNetworkDevices(customerId, networkDeviceId));
            RESP_CODE = APIConstants.SUCCESS;
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_EDIT,
//                    req.getRemoteAddr(), null, customerId.longValue(), "");
//            logger.info(" binding NetworkDevice with customer"+ :  request: { From : {}}; Response : {{}}+", req.getHeader("requestFrom"), RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unable to bindNetworkDevice with customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unable to bindNetworkDevice with customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/getNetworkDeviceBindByCurrentDeviceId")
    public GenericDataDTO getNetworkDeviceBindByCurrentDeviceId(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setData(networkDeviceService.getNetworkDeviceBindByCurrentDeviceId(id));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device by CurrentDeviceId" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Network Device by CurrentDeviceId" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @DeleteMapping("/deleteNetworkDeviceBindById")
    public GenericDataDTO deleteNetworkDeviceBindById(@RequestParam Long id, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseMessage(networkDeviceService.deleteNetworkDeviceBindById(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network device Bind" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network device Bind" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PutMapping("/changeNetworkDeviceBinding")
    public GenericDataDTO changeNetworkDeviceBinding(@RequestBody NetworkDeviceBindDTO bindDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseMessage(networkDeviceService.changeNetworkDeviceBinding(bindDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Change Network device Binding " + LogConstant.LOG_BY_NAME + bindDTO.getId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Change Network device Binding " + LogConstant.LOG_BY_NAME + bindDTO.getId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PutMapping("/updateNetworkDeviceBindById")
    public GenericDataDTO updateNetworkDeviceBindById(@RequestBody NetworkDeviceBindDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            //genericDataDTO.setResponseMessage(networkDeviceService.deleteNetworkDeviceBindById(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network device Bind" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Network device Bind" + LogConstant.LOG_BY_NAME + networkDeviceDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getNetworkDeviceByInventoryMappingId")
    public GenericDataDTO getNetworkDeviceByInventoryMappingId(@RequestParam Long id, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(networkDeviceService.getNetworkDeviceByInventoryMappingId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch Network Device by Inventory Mapping Id By : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch Network Device by Inventory Mapping Id By : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}", APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getDeviceHierarchyMappingById")
    public GenericDataDTO getDeviceHierarchyMappingById(@RequestParam Long id, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getDeviceHierarchyMappingById(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch MappingData By Device Id : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Fetch MappingData By Device Id : " + id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getCustDeviceHierarchy")
    public GenericDataDTO getCustDeviceHierarchy(@RequestParam Long itemId, @RequestParam Long custId, @RequestParam Long custInvenId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(networkDeviceService.getTargetDeviceHierarchy(itemId, custId, custInvenId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Device Hierarchy" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Device Hierarchy" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/getCustDeviceHierarchyList")
    public GenericDataDTO getCustDeviceHierarchyList(@RequestParam Long itemId, @RequestParam Long custId, @RequestParam Long custInvenId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(networkDeviceService.getTargetDeviceHierarchyList(itemId, custId, custInvenId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Device Hierarchy List" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Device Hierarchy List" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getDeviceDetailsByItemIdAndCustInvenId")
    public GenericDataDTO getDeviceDetailsByItemIdAndCustInvenId(@RequestParam Long itemId, @RequestParam Long custInventoryId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        try {
            genericDataDTO.setData(networkDeviceService.getDeviceDetailsByItemId(itemId, custInventoryId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Network Device" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
