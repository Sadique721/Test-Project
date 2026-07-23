package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.ClientServiceEntity;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.helper.BulkVlanResponseDto;
import com.savbill.radius.helper.VlanManagementDto;
import com.savbill.radius.helper.VlanSearch;
import com.savbill.radius.mvno.Repository.MvnoRepository;
import com.savbill.radius.repository.ClientServiceRepository;
import com.savbill.radius.services.VlanAuditService;
import com.savbill.radius.services.VlanManagementService;
import com.savbill.radius.utils.CustomValidationException;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Radius VLAN Management", description = "REST APIs related to VLAN Management Entity!!!!", tags = "VLAN Management")
@RestController
@RequestMapping("/SavbillRadius")
public class VlanManagementController {
    private static final Logger log = LoggerFactory.getLogger(VlanManagementController.class);
    private static final String VLAN_LIST = "vlanList";
    private static final String VLAN = "vlan";
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private Tracer tracer;
    @Autowired
    private VlanManagementService vlanManagementService;

    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    VlanAuditService vlanAuditService;
    @Autowired
    public UpdateDiffFinder updateDiffFinder;

    @Autowired
    private MvnoRepository mvnoRepository;

    @ApiOperation(value = "Add new VLAN Entity")
    @PostMapping("/addVlan")
   @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addVlan(@RequestBody VlanManagementDto vlanManagementDto, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            VLANManagement vlanVo = vlanManagementService.save(vlanManagementDto, mvnoId);
            vlanAuditService.saveVlanAudit(vlanVo, vlanManagementDto.getLoggedInUser(),"Create",vlanManagementDto.getStaffId(),vlanVo.toString(), "");
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN, vlanVo);
            response.put(RadiusConstants.MESSAGE, "Vlan has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Vlan has been created successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = RadiusConstants.EXPECTATION_FAILED;
            response.put(RadiusConstants.ERROR_MESSAGE, ce.getMessage());
            log.error("Name " + vlanManagementDto.getVlanName() + " already in use " + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", response, ce.getStackTrace());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Vlan ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of VLANs in the system")
    @GetMapping("/vlans")
   @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findAllVlans(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<VLANManagement> vlanList = vlanManagementService.findAllVlans(mvnoId);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN_LIST, vlanList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of VLANs in the system")
    @PostMapping("/vlans/list")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findVlansList(@RequestBody PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            PageableResponse vlanList = vlanManagementService.findVlansList(mvnoId, paginationDTO);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN_LIST, vlanList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of customers by search in the system")
    @PostMapping("/vlans/search")
    public ResponseEntity<Map<String, Object>> searchVlans(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestBody VlanSearch vlanSearch, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            PageableResponse<VLANManagement> page = vlanManagementService.findAllVlansBySearch(mvnoId, vlanSearch, paginationDTO);
            int responseCode;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = RadiusConstants.NULL_VALUE;
                    response.put(RadiusConstants.ERROR_MESSAGE, "No Record found for given filter.");
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching VLANs,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(VLAN_LIST, page);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching VLANs," +LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while fetching radius customers with name : " + vlanSearch.getVlanName() + " " + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }




    @ApiOperation(value = "Get Vlan based on the given vlan id")
    @GetMapping("/findVlanById")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findVlanById(@RequestParam(name = "vlanId", required = true) Long vlanId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            VLANManagement vlanManagement = vlanManagementService.findVlanById(vlanId, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN, vlanManagement);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan with id :," + vlanId  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan with id,"+vlanId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update Vlan based on the vlan id")
    @PutMapping("/updateVlan")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> updateVlan(@RequestBody VlanManagementDto vlanManagementDto, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            VLANManagement clientVo = vlanManagementService.updateVlanManagement(vlanManagementDto, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN, clientVo);
            response.put(RadiusConstants.MESSAGE, "Vlan has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Vlan has been updated successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating Vlan," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete vlan as per the given vlan id")
    @DeleteMapping("/deleteVlan")
   @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteVlan(@RequestParam(name = "vlanId", required = true) Long vlanId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,@RequestParam(name = "staffId", required = true) Integer staffId,@RequestParam(name = "loggedInUser", required = true) String loggedInUser, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            VLANManagement vlanManagement = vlanManagementService.findVlanById(vlanId, mvnoId);
            vlanManagementService.deleteByVlanId(vlanId, mvnoId);
            vlanAuditService.saveVlanAudit(vlanManagement, loggedInUser,"Delete",staffId,vlanManagement.toString(), "");
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "Vlan has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Vlan has been deleted successfully.,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting Client," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @ApiOperation(value = "Bulk VLAN creation")
    @PostMapping("/addBulkVlan")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addBulkVlan(@RequestParam("file") MultipartFile file, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,@RequestParam(name = "staffId", required = true) Integer staffId,@RequestParam(name = "loggedInUser", required = true) String loggedInUser, HttpServletRequest request) {

        Long startTime = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String fileName = file.getOriginalFilename();

            if (fileName == null || !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx"))) {
                throw new Exception("Only CSV and XLSX files are allowed..!");
            }
            response = vlanManagementService.addBulkVlan(file, mvnoId,staffId,loggedInUser);

            return apiResponseController.apiResponse((Integer)response.get("responseCode"), response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Vlan ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @GetMapping("/vlan/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "filename", required = true) String filename,
                                                 @RequestParam(name = "mvnoId", required = true) Integer mvnoId) {
        try {
            ClientServiceEntity clientService = clientServiceRepository.getByNameAndMvnoId(RadiusConstants.BULK_VLAN_PATH, mvnoId);
            if (clientService != null) {
                Path file = Paths.get(clientService.getValue()).resolve(filename).normalize();
                Resource resource = new UrlResource(file.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
        return  null;
    }

    @ApiOperation(value = "Delete live users based on the given Ids")
    @DeleteMapping("/deleteMultipleVlan")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteMultiple(@RequestBody(required = true) List<Long> ids , @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "staffId", required = true) Integer staffId,@RequestParam(name = "loggedInUser", required = true) String loggedInUser, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        int delete = 0;
        try {
            delete = vlanManagementService.delete(ids, mvnoId,staffId,loggedInUser);
            response.put(RadiusConstants.MESSAGE, delete + " : Vlan profiles have been DELETED successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + delete + " : Vlan profiles have been DELETED successfully. " + ids + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting  Live users by id: "+ ids + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Bulk VLAN creation")
    @PostMapping("/updateBulkVlan")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT_EDIT+ "\")")
    public ResponseEntity<Map<String, Object>> updateBulkVlan(@RequestParam("file") MultipartFile file, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,@RequestParam(name = "staffId", required = true) Integer staffId,@RequestParam(name = "loggedInUser", required = true) String loggedInUser, HttpServletRequest request) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer addedListCount = 0;
        try {
            String fileName = file.getOriginalFilename();

            if (fileName == null || !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx"))) {
                throw new Exception("Only CSV and XLSX files are allowed..!");
            }
            addedListCount = vlanManagementService.updateBulkVlan(file, mvnoId,staffId,loggedInUser);
            Integer responseCode;
            if(addedListCount == 0){
                responseCode = RadiusConstants.EXPECTATION_FAILED;
                response.put(RadiusConstants.MESSAGE, "Process started, Please check audit for updates.");
            }else{
                responseCode = RadiusConstants.SUCCESS;
                response.put(RadiusConstants.MESSAGE, addedListCount + " Vlan profiles have been updated successfully");
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +addedListCount + " : Vlan have been updated successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            long endTime = System.currentTimeMillis();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Bulk Vlan ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
    @ApiOperation(value = "Get Vlan based on the given vlan id")
    @GetMapping("/exportVlan")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VLAN_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> exportVlan( @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<BulkVlanResponseDto> vlanManagement = vlanManagementService.exportVlan( mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN, vlanManagement);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan with id :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan with id," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/vlan/audit/download")
    public ResponseEntity<Resource> downloadVlanAuditFile(@RequestParam(name = "filename", required = true) String filename,
                                                 @RequestParam(name = "mvnoId", required = true) Integer mvnoId) {
        try {
            ClientServiceEntity clientService = clientServiceRepository.getByNameAndMvnoId(RadiusConstants.BULK_VLAN_PATH, mvnoId);
            if (clientService != null) {
                String mvnoName = mvnoRepository.findMvnoNameById(mvnoId.longValue());
                String subFolder =clientService.getValue() + "/" + mvnoName;
                Path file = Paths.get(subFolder).resolve(filename).normalize();
                Resource resource = new UrlResource(file.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
        return  null;
    }
}
