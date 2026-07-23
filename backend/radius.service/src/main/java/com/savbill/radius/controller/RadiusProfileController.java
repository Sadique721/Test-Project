package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.helper.RadiusProfileDto;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.services.RadiusProfileService;
import com.savbill.radius.services.impl.TableMetadataService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Radius Profile Management", description = "REST APIs related to Radius Profile Entity!!!!", tags = "Radius Profile")
@RestController
@RequestMapping("/SavbillRadius")
public class RadiusProfileController {
    private static final String RADIUS_PROFILE_LIST = "radiusProfileList";
    private static final String RADIUS_PROFILE = "radiusProfile";
    @Autowired
    private RadiusProfileService radiusProfileService;
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private TableMetadataService tableMetadataService;
    @Autowired
    private Tracer tracer;

    @Autowired
    private CacheConfigService cacheConfigService;
    private static final Logger log = LoggerFactory.getLogger(RadiusProfileController.class);

    @ApiOperation(value = "Get list of radius profiles in the system")
    @GetMapping("/all")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROFILES + "\")")
    public ResponseEntity<Map<String, Object>> findAllRadiusProfiles(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(RADIUS_PROFILE_LIST, radiusProfileService.findAll(mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Profiles has been fetched successfully" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Radius Profiles: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get radius profile based on the given radius profile id")
    @GetMapping("/findById")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROFILES + "\")")
    public ResponseEntity<Map<String, Object>> findRadiusProfileById(
            @RequestParam(name = "radiusProfileId", required = true) Long radiusProfileId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(RADIUS_PROFILE, radiusProfileService.findById(radiusProfileId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Profiles has been fetched successfully of id" + radiusProfileId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Radius Profile with id: " + radiusProfileId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get radius profile based on the given radius profile name")
    @GetMapping("/findByName")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findRadiusProfileByName(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<RadiusProfile> radiusProfileList = radiusProfileService.searchByName(name, mvnoId);
            Integer responseCode = 0;
            if (radiusProfileList.isEmpty()) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(RADIUS_PROFILE_LIST, radiusProfileList);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Profiles has been fetched successfully with name" + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Radius Profile with name: " + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Add new radius profile")
    @PostMapping("/add")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROFILES_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addRadiusProfile(
            @RequestParam String entityDTO,
            @RequestParam(name = "mvnoId") Integer mvnoId
            , @RequestParam(value = "keystoreDoc", required = false) MultipartFile[] keyStoreFile
            , @RequestParam(value = "trustStoreDoc", required = false) MultipartFile[] trustStoreFile, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        ObjectMapper mapper = new ObjectMapper();
        RadiusProfileDto radiusProfile = new RadiusProfileDto();

        try {
            if (null != entityDTO) {

                radiusProfile = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(entityDTO, new TypeReference<RadiusProfileDto>() {
                        });

                response.put(RADIUS_PROFILE, radiusProfileService.save(radiusProfile, mvnoId, trustStoreFile, keyStoreFile));
                response.put(RadiusConstants.MESSAGE, "Radius profile has been added successfully.");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Profiles has been created successfully with name" + radiusProfile.getName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                //cacheConfigService.reloadCache(AAAConstant.AUTHPROFILECONFIG_CACHE);
                //cacheConfigService.reloadCache(AAAConstant.DYNAAUTHPROFILECONFIG_CACHE);
                return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
            }
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating Radius Profiles: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update radius profile")
    @PutMapping("/update")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROFILES_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> updateRadiusProfile(
            @RequestParam String entityDTO,
            @RequestParam(name = "mvnoId") Integer mvnoId
            , @RequestParam(value = "keystoreDoc", required = false) MultipartFile[] keyStoreFile
            , @RequestParam(value = "trustStoreDoc", required = false) MultipartFile[] trustStoreFile, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        ObjectMapper mapper = new ObjectMapper();
        RadiusProfileDto radiusProfile = new RadiusProfileDto();
        try {
            if (null != entityDTO) {
                radiusProfile = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(entityDTO, new TypeReference<RadiusProfileDto>() {
                        });

                response.put(RADIUS_PROFILE, radiusProfileService.update(radiusProfile, mvnoId, request, trustStoreFile, keyStoreFile));
                response.put(RadiusConstants.MESSAGE, "Radius profile has been updated successfully.");
            }
            //cacheConfigService.reloadCache(AAAConstant.AUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.DYNAAUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.ACCTPROFILECONFIG_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating Radius Profiles: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete radius profile")
    @DeleteMapping("/delete")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROFILES_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteRadiusProfile(
            @RequestParam(name = "radiusProfileId", required = true) Long radiusProfileId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            radiusProfileService.deleteById(radiusProfileId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Radius profile has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Profiles has been deleted successfully with id" + radiusProfileId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.AUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.DYNAAUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.ACCTPROFILECONFIG_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting Radius Profile with id: " + radiusProfileId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/changeStatus")
    @ApiOperation(value = "Change radius profile status based on the given name and status value")
//	@PreAuthorize("@roleAccesses.hasPermission('profile','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeCustomerStatus(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String message = radiusProfileService.changeRadiusProfileStatus(name, status, mvnoId, request);
            response.put(RadiusConstants.MESSAGE, message);
            //cacheConfigService.reloadCache(AAAConstant.AUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.DYNAAUTHPROFILECONFIG_CACHE);
            //cacheConfigService.reloadCache(AAAConstant.ACCTPROFILECONFIG_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while changing radius profile status " + name + " " + e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while changing  Radius Profile status: " + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/api/columns")
    public List<String> getColumnNames(@RequestParam String entityName) throws ClassNotFoundException {
        // Load the entity class using the entityName parameter
        Class<?> entityClass = Class.forName("com.savbill.radius.entity." + entityName);
        return tableMetadataService.getColumnNames(entityClass);
    }
}
