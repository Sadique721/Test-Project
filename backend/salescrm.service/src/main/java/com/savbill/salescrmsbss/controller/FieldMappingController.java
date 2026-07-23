package com.savbill.salescrmsbss.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.entity.Screen;
import com.savbill.salescrmsbss.entity.pojo.FielmappingDto;
import com.savbill.salescrmsbss.entity.pojo.GenericDataDTO;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.service.Impl.FieldmappingServiceImpl;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.savbill.salescrmsbss.utils.LogConstants;
import com.savbill.salescrmsbss.utils.URLConstant;
//import com.savbill.apigw.modules.acl.constants.AclConstants;
import io.swagger.annotations.Api;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = URLConstant.ROOT_ENDPOINT + URLConstant.FIELD_MAPPING)
@Api(value = "FieldMappingController", description = "REST APIs related to FieldMapping Entity!!!!", tags = "field_mapping_controller")
public class FieldMappingController extends BaseController {
    @Autowired
    Tracer tracer;
    private static final Logger LOGGER = Logger.getLogger(FieldMappingController.class);
//	private static String SUBMODULE = " [FieldMappingController] ";

    public String getModuleNameForLog() {
        return "[FieldMappingController]";
    }

    @Autowired
    private FieldmappingServiceImpl fieldmappingService;

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getTemplates")
    public GenericDataDTO getTemplate(@RequestParam("id") Long id, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

//		String SUBMODULE = getModuleNameForLog() + " [getTemplate()] ";
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Templete" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);


        try {
            genericDataDTO.setDataList((List) fieldmappingService.getTemplate(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Templete" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);


        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Templete" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getFields")
    public GenericDataDTO getFields(HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
        try {
            String authHeaderString = request.getHeader("Authorization");
            genericDataDTO.setDataList(fieldmappingService.getFields(getBUId(authHeaderString)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Fields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Fields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_ADD + "\")")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody FielmappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        //		String SUBMODULE = getModuleNameForLog() + " [save()] ";
        try {
            List<String> errors = new ArrayList<>();
            if (result.getFieldErrors().size() != 0) {
                if ((result.getFieldError().getCode().equalsIgnoreCase("NotBlank")) || (result.getFieldError().getCode().equalsIgnoreCase("NotNull")) || (result.getFieldError().getCode().equalsIgnoreCase("NotEmpty")) || (result.getFieldError().getCode().equalsIgnoreCase("Null")) || (result.getFieldError().getCode().equalsIgnoreCase("Digits"))) {
                    errors.addAll(result.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(errors.toString());

                    LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save entity" + LogConstants.LOG_BY_NAME + entityDTO.getFieldName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

                }
            } else if ((result.getFieldErrors().size() == 0)) {
                FielmappingDto dtoData = fieldmappingService.saveEntity(entityDTO);
                genericDataDTO.setData(dtoData);
                genericDataDTO.setTotalRecords(1);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save entity" + LogConstants.LOG_BY_NAME + entityDTO.getFieldName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save entity" + LogConstants.LOG_BY_NAME + entityDTO.getFieldName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_ADD + "\")")
    @PostMapping(value = "/addTemplate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO addTemplate(@Valid @RequestBody List<FielmappingDto> entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Save");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [saveList()] ";
        try {
            String authTokenHeader = req.getHeader("Authorization");
            List<FielmappingDto> dtoDataList = fieldmappingService.saveEntityList(entityDTO, getBUId(authTokenHeader));
            genericDataDTO.setDataList(dtoDataList);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Add Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Add Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getbutypes")
    public GenericDataDTO getbutypes(@RequestParam("buid") Long id, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        //		String SUBMODULE = getModuleNameForLog() + " [getbutypes()] ";

        try {
            genericDataDTO.setDataList(fieldmappingService.getbutypes(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch BU Types" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);


        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch BU Types" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getPlanFieldsByServiceid/{serviceId}")
    public GenericDataDTO getPlanFieldsByServiceId(@PathVariable Long serviceId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getPlanFieldsByServiceId()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());


        try {
            genericDataDTO.setDataList(fieldmappingService.getPlanFieldsByServiceId(serviceId));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//			logger.info(
//					"Fetching Plan Fields By  Service Id  :  request: { From : {}, Request Url : {}}; Response : {{}}",
//					getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching PlanFields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//			logger.error(
//					"Unable To Fetch Plan Fields By Service Id   :  request: { From : {}, Request Url : {}}; Response : {{}}",
//					getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching PlanFields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getAvailableAndBoundedFields")
    public GenericDataDTO getAvailableAndBoundedFields(@RequestParam("screen") Screen screen, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());


        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";

        String authHeaderString = request.getHeader("Authorization");

        try {
            genericDataDTO.setDataList(fieldmappingService.getAvailableAndBoundedFields(screen.name(), getBUId(authHeaderString)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Available And Bounded Fields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Available And Bounded Fields" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getCustomerTemplate")
    public GenericDataDTO getAvailableAndBoundedField(@RequestParam("screen") Screen screen, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getCustomerTemplate()] ";
//		logger.info(getModuleNameForLog() + "--" + "Fetching Available And Bounded Fields .Data[" + screen.toString()
//				+ "]");
        try {
            String authHeaderString = request.getHeader("Authorization");
            genericDataDTO.setDataList(fieldmappingService.getCustomerTemplate(screen.name(), getBUId(authHeaderString)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//			logger.info(
//					"Fetching Available And Bounded Fields  :  request: { From : {}, Request Url : {}}; Response : {{}}",
//					getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get customer Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get customer Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getModuleWiseTemplate")
    public GenericDataDTO getModuleWiseFields(@RequestParam("screen") Screen screen, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getModuleWiseFields()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());


        try {
            String authHeaderString = request.getHeader("Authorization");
            genericDataDTO.setDataList(fieldmappingService.getModuleWiseFields(screen.name(), getBUId(authHeaderString)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Module Wise Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
//			logger.error(
//					"Unable To Fetching Module Wise Fields   :  request: { From : {}, Request Url : {}}; Response : {{}}",
//					getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),
//					exception.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Module Wise Templates" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL + genericDataDTO.getResponseMessage());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getPresentAddressByCustomerId")
    public GenericDataDTO getPresentAddressByCustomerId(@RequestParam Integer customerId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getPresentAddressByCustomerId()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Present Address" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        try {
            genericDataDTO.setData(fieldmappingService.getPresentAddressByCustomerId(customerId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Present Address " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
//			logger.error(
//					"Unable To Fetch PresentAddress By CustomerId   :  request: { From : {}, Request Url : {}}; Response : {{}}",
//					getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),
//					exception.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Present Address" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\""
//			+ AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/fieldDetailsByParam")
    public GenericDataDTO getFieldDetailsByParam(@RequestParam Long paramId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//		String SUBMODULE = getModuleNameForLog() + " [getFieldDetailsByParam()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Field Detail" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        try {
            genericDataDTO.setDataList(fieldmappingService.getFieldDetailsByParam(paramId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Field Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Field Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //get logger in user first name
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
