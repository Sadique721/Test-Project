package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.DeleteContant;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.exceptions.DataNotFoundException;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPPoolDtls;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper.IPPoolDtlsMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPPoolDtlsRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.service.IPPoolDtlsService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.service.IPPoolService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.utils.SubnetUtils;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.IP_POOL)
public class IPPoolController extends ExBaseAbstractController<IPPoolDTO> {

    @Autowired
    private IPPoolService ipPoolService;

    @Autowired
    private IPPoolDtlsService ipPoolDtlsService;

    @Autowired
    private IPPoolDtlsRepository ipPoolDtlsRepository;
    @Autowired
    Tracer tracer;
    @Autowired
    private IPPoolDtlsMapper ipPoolDtlsMapper;
//    @Autowired
//    AuditLogService auditLogService;

    public IPPoolController(IPPoolService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolController]";
    }

    IPPoolDTO ipPoolDTO = new IPPoolDTO();
    private static final Logger LOGGER = Logger.getLogger(IPPoolController.class);
    @Deprecated
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
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
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter,req);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search IpPool By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search IpPool By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search IpPool By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstant.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Ip_Address.IP_DELETE +"\")")
    @Override
    public GenericDataDTO delete(@RequestBody IPPoolDTO entityDTO, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = ipPoolService.deleteVerification(entityDTO.getPoolId().intValue());
        try {

            if (flag) {
                genericDataDTO = super.delete(entityDTO, req);
                IPPoolDTO ipPool = (IPPoolDTO) genericDataDTO.getData();
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Delete Ip pool"+ LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                // AclConstants.OPERATION_IP_POOL_DELETE, req.getRemoteAddr(), null, ipPool.getPoolId().longValue(), ipPool.getPoolName());
            } else {
                genericDataDTO.setResponseMessage(DeleteContant.IP_POOL_DELETE_EXIST);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());

//            logger.error("Unable to Delete ip Pool with id "+entityDTO.getPoolId()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Delete Ip pool"+ LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + DeleteContant.IP_POOL_DELETE_EXIST+APIConstants.FAIL + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
            }finally {
                MDC.remove("type");
                MDC.remove("userName");
                MDC.remove("traceId");
                MDC.remove("spanId");
            }

        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Ip_Address.IP +"\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

         GenericDataDTO genericDataDTO = super.getEntityById(id, req);

         IPPoolDTO ipPool = (IPPoolDTO) genericDataDTO.getData();
         //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
         // AclConstants.OPERATION_IP_POOL_VIEW, req.getRemoteAddr(), null, ipPool.getPoolId().longValue(), ipPool.getPoolName());
        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Entity By Id: "+ id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

         MDC.remove("type");
         MDC.remove("userName");
         MDC.remove("traceId");
         MDC.remove("spanId");


        return genericDataDTO;
    }

//    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Ip_Address.IP +"\")")
@Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
        return super.getAll(requestDTO);
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Ip_Address.IP_CREATE +"\")")
    @PostMapping(value = {"/saveIPPool"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody IPPoolDTO ipPoolDTO, BindingResult result, Authentication authentication, HttpServletRequest req) {

        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean flag = ipPoolService.duplicateVerifyAtSave(ipPoolDTO.getPoolName());
            if(ipPoolDTO.getPoolId() != null) {
            	flag = true;
            }
            if (flag) {
                ipPoolDTO = ipPoolService.saveIPPool(ipPoolDTO);
                //  ipPoolDtlsService.saveEntity(ipPoolDtlsDTO);
                genericDataDTO.setData(ipPoolDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                genericDataDTO.setTotalRecords(1);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"create IP Pool"+ LogConstant.LOG_BY_NAME + ipPoolDTO.getPoolName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //     AclConstants.OPERATION_IP_POOL_ADD, req.getRemoteAddr(), null, ipPoolDTO.getPoolId(), ipPoolDTO.getPoolName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.IPPOOL_NAME_EXITS);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"create IP Pool"+ LogConstant.LOG_BY_NAME +ipPoolDTO.getPoolName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR  +LogConstant.LOG_NO_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"create IP Pool"+ LogConstant.LOG_BY_NAME +ipPoolDTO.getPoolName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"create IP Pool"+ LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Ip_Address.IP_EDIT +"\")")
    @PostMapping(value = {"/updateIPPool"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody IPPoolDTO ipPoolDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        boolean flag = ipPoolService.duplicateVerifyAtEdit(ipPoolDTO.getPoolName(), ipPoolDTO.getPoolId().intValue());
        try {
        if (flag) {
            ipPoolService.getEntityForUpdateAndDelete(ipPoolDTO.getPoolId());
            ipPoolDTO = ipPoolService.saveIPPool(ipPoolDTO);
            genericDataDTO.setData(ipPoolDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setTotalRecords(1);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update IP Pool"+ LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.IPPOOL_NAME_EXITS);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update IP Pool"+LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_NO_RECORD_FOUND  +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            return genericDataDTO;
        }
        } catch (CustomValidationException ex){
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update IP Pool"+LogConstant.LOG_BY_NAME+ipPoolDTO.getPoolName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @PostMapping("/ipFind")
    public GenericDataDTO ipAddressFind(@RequestBody IpFind ipFind, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (ipFind.getIpaddress() != null) {
                IpAddressFindResDTO ippoolreqDtl = ipPoolDtlsService.findByIpAddress(ipFind.getIpaddress());
                genericDataDTO.setData(ippoolreqDtl);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Ip Address"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //      AclConstants.OPERATION_IP_POOL_VIEW, req.getRemoteAddr(), null, ippoolreqDtl.getPoolDetailsId(), ippoolreqDtl.getCustomerName());

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Please Provide Ip Address");
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Ip Address"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Ip Address"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_EDIT + "\")")
    @PostMapping("/ipFind/update")
    public GenericDataDTO ipAddressUpdate(@RequestBody IPPoolDtlsDTO ipPoolDtlsDTO, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            IPPoolDtlsDTO ipPoolDtlsDTOold = ipPoolDtlsService.updateIPAddress(ipPoolDtlsDTO);
            if (ipPoolDtlsDTO.getPoolDetailsId() != null) {

                IPPoolDtlsDTO ipPoolDtlsDTO1 = ipPoolDtlsService.updateIPAddress(ipPoolDtlsDTO);
                genericDataDTO.setData(ipPoolDtlsDTO1);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update ip address"+LogConstant.LOG_BY_NAME+ipPoolDtlsDTO1.getIpAddress()+ " And Updated Details : "+UpdateDiffFinder.getUpdatedDiff(ipPoolDtlsDTOold,ipPoolDtlsDTO1) + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //     AclConstants.OPERATION_IP_POOL_EDIT, req.getRemoteAddr(), null, ipPoolDtlsDTO.getPoolDetailsId(), ipPoolDtlsDTO.getIpAddress());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update ip address"+LogConstant.LOG_BY_NAME+ipPoolDtlsDTO.getIpAddress()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL );

            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update ip address"+LogConstant.LOG_BY_NAME+ipPoolDtlsDTO.getIpAddress()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping(value = "/getPoolByIp")
    public GenericDataDTO getIPDetailsByNetworkIp(@RequestParam("networkIp") String networkIp, Authentication authentication, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            IPDetailsDTO ipDetailsDTO = new IPDetailsDTO();
            SubnetUtils subnetUtils = new SubnetUtils(networkIp);
            ipDetailsDTO.setNetMask(subnetUtils.getNetmask());
            ipDetailsDTO.setNetworkIp(subnetUtils.getNetworkIp());
            ipDetailsDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
            ipDetailsDTO.setFirstHost(subnetUtils.getFirstIp());
            ipDetailsDTO.setLastHost(subnetUtils.getLastIp());
            ipDetailsDTO.setTotalHost(subnetUtils.getNumberOfHosts());
            ipDetailsDTO.setIpRange(subnetUtils.getHostAddressRange());
            genericDataDTO.setData(ipDetailsDTO);
            genericDataDTO.setTotalRecords(1);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Ip Details by network Ip: "+ networkIp + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );


        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Ip Details by network Ip: "+ networkIp + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_VIEW + "\")")
//    @GetMapping("/searchIpAddress")
//    public GenericDataDTO searchIpAddress(@RequestParam("ipAddress") String ipAddress, Authentication authentication) {
//        ApplicationLogger.logger.info(getModuleNameForLog() + " [searchIpAddress]");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            List<IPPoolDTO> ipPoolList = new ArrayList<>();
//            for (IPPoolDtls ipPoolDtls : ipPoolDtlsService.findByIpAddress(ipAddress))
//                ipPoolList.add(ipPoolService.getEntityById(ipPoolDtls.getPoolId()));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(ipPoolList);
//            genericDataDTO.setTotalRecords(ipPoolList.size());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getNonAllocatedIp")
    public GenericDataDTO getNonAllocatedIp(@RequestParam("poolId") Long poolId, Authentication authentication,
                                            @RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "poolDetailsId") String sortBy, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Page<IPPoolDtls> ipPoolDtls = ipPoolDtlsService.findNonAllocatedPoolId(poolId, page - 1, pageSize, sortBy, sortOrder);
            genericDataDTO.setDataList(ipPoolDtls.getContent());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setTotalRecords(ipPoolDtls.getTotalElements());
            genericDataDTO.setPageRecords(ipPoolDtls.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(ipPoolDtls.getNumber() + 1);
            genericDataDTO.setTotalPages(ipPoolDtls.getTotalPages());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch NonAllocated Ip Address"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch NonAllocated Ip Address"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @PostMapping("/blockIP/{poolDetailsId}/{custId}")
    public GenericDataDTO blockIp(@PathVariable Long poolDetailsId, @PathVariable Long custId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            IPPoolDtlsDTO ipPoolDtlsDTO = ipPoolDtlsService.getEntityById(poolDetailsId);
            if (ipPoolDtlsDTO == null) {
                genericDataDTO.setResponseMessage("Given ip is not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update Block ip for pool detail"+LogConstant.LOG_BY_NAME+ipPoolDtlsDTO.getIpAddress()+" id: "+ poolDetailsId + LogConstant.LOG_BY_NAME + ipPoolDTO.getPoolName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND  +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(ipPoolDtlsService.blockIp(ipPoolDtlsDTO, custId));

            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
            //       AclConstants.OPERATION_IP_POOL_EDIT, req.getRemoteAddr(), null, custId.longValue(), ipPoolDtlsDTO.getIpAddress());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update Block ip for pool detail id: "+ poolDetailsId +  LogConstant.LOG_BY_NAME + ipPoolDTO.getPoolName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update Block ip for pool detail id: "+ poolDetailsId + LogConstant.LOG_BY_NAME + ipPoolDTO.getPoolName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping("/searchPoolId")
    public GenericDataDTO searchPoolId(@RequestParam("poolId") Long poolId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<IPPoolDtls> ipPoolDtls = ipPoolDtlsService.findByPoolId(poolId);
            genericDataDTO.setDataList(ipPoolDtls);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setTotalRecords(ipPoolDtls.size());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search ip pool for pool id: "+ poolId +  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search ip pool for pool id: "+ poolId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }


//For Get Logged in user by first name
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
