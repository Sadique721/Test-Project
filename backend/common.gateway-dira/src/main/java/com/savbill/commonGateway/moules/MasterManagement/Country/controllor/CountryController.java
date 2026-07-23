package com.savbill.commonGateway.moules.MasterManagement.Country.controllor;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.model.CountryPojo;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
//import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(UrlConstants.BASE_API_URL)
public class CountryController extends ApiBaseController {


    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private Tracer tracer;

//    private final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
        private final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

    // Country
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY + "\")")
    @PostMapping("/country/list")
    public ResponseEntity<?> getCountryList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) throws Exception {

        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Country> countryList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getAttribute(LogConstants.TRACE_ID).toString());

        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        CountryPojo pojo = new CountryPojo();
        try {
            CountryService countryService = SpringContext.getBean(CountryService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            countryList = countryService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != countryList && 0 < countryList.getSize()) response.put("countryList", countryService.convertResponseModelIntoPojo(countryList.getContent()));
            else response.put("countryList", new ArrayList<>());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Country list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All Country list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All Country list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return apiResponse(respCode, response, countryList);
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
  @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY + "\")")
    @PostMapping("/country/search")
    public ResponseEntity<?> searchCountry(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) {

        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Country> countryList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search country using keyword"+requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
                return apiResponse(respCode, response);
            }
            CountryService countryService = SpringContext.getBean(CountryService.class);
            countryList = countryService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (countryList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("countryList", new ArrayList<>());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search country using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +Response);
                return apiResponse(Response, response, countryList);
            }
            if (null != countryList && 0 < countryList.getSize()) {
                response.put("countryList", countryService.convertResponseModelIntoPojo(countryList.getContent()));
            } else {
                response.put("countryList", new ArrayList<>());
            }
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search country using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search country using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        } catch (RuntimeException re) {
            LOGGER.error(re.getMessage(),re);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search country using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage()+ LogConstants.LOG_STATUS_CODE +respCode);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search country using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return apiResponse(respCode, response, countryList);
    }

    @GetMapping("/country/all")
    public ResponseEntity<?> getAllCountryList(HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        long startTime = System.nanoTime();  // Start measuring
        MDC.put("spanId",traceContext.spanIdString());
        CountryPojo pojo  = new CountryPojo();
        try {
            CountryService countryService = SpringContext.getBean(CountryService.class);
            List<Country> countryList = countryService.getAllActiveEntities();
            response.put("countryList", countryService.convertResponseModelIntoPojo(countryList).stream().sorted(Comparator.comparing(CountryPojo::getId).reversed()).collect(Collectors.toList()));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "fetch All Country list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Country list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All Country list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return apiResponse(respCode, response);
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY + "\")")
    @GetMapping("/country/{id}")
    public ResponseEntity<?> getCountryById(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        CountryPojo pojo = new CountryPojo();
        long startTime = System.nanoTime();  // Start measuring
        try {
            CountryService countryService = SpringContext.getBean(CountryService.class);
            Country country = countryService.get(id);
            if (country == null) {
                respCode = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Country Not Found!");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch country "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response);
            } else {
                response.put("countryData", countryService.convertCountryModelToCountryPojo(country));
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch country"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
            }

        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch country"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch country"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY_CREATE+ "\")")
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_ADD + "\")")
    @PostMapping("/country")
    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryPojo pojo, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            CountryService countryService = SpringContext.getBean(CountryService.class);
            countryService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = countryService.duplicateVerifyAtSave(pojo.getName());
            String url = req.getRequestURI();
            if (pojo.getName().length() > 250) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Input size is Exceeded"+ LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response, null);
            } else {
                if (flag) {
                    pojo = countryService.save(pojo);
                    response.put("country", pojo);
                    response.put(APIConstants.MESSAGE, "Successfully Created");
                    respCode = APIConstants.SUCCESS;
                    req.getRequestURL();
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
                } else {
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_COUNTRY_MANAGEMENT);
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Country with same name already exist "+ LogConstants.LOG_STATUS_CODE+ respCode);
                    return apiResponse(respCode, response, null);

                }
            }
            //CountryMessage countryMessage = new CountryMessage(pojo.getId(), pojo.getName(), pojo.getStatus(), pojo.getIsDelete(), pojo.getMvnoId());
            //this.messageSender.send(countryMessage, RabbitMqConstants.QUEUE_COUNTRY);
        } catch (DataIntegrityViolationException exc) {
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + exc.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY_EDIT+ "\")")
    @PutMapping("/country/{id}")
    public ResponseEntity<?> updateCountry(@Valid @RequestBody CountryPojo pojo, @PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            CountryService countryService = SpringContext.getBean(CountryService.class);
            pojo.setId(id);
            countryService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            boolean flag = countryService.duplicateVerifyAtEdit(pojo.getName(), pojo.getId());
            if (flag) {
                pojo = countryService.update(pojo, req);
                response.put("country", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Updated");
                respCode = APIConstants.SUCCESS;
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response, null);
            }

        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.COUNTRY_DELETE + "\")")
    @DeleteMapping("/country/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {

        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());

        CountryService countryService = SpringContext.getBean(CountryService.class);
        long startTime = System.nanoTime();  // Start measuring
        try {
            Country country = countryService.getCountryForUpdateAndDelete(id);
            CountryPojo pojo = countryService.convertCountryModelToCountryPojo(country);
            if (country != null) {
                countryService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                countryService.deleteCountry(id);
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete country"+LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
            }

        } catch (CustomValidationException ce) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete country"+LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                Country country = countryService.getCountryForUpdateAndDelete(id);
                CountryPojo pojo = countryService.convertCountryModelToCountryPojo(country);
                respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

            } else {
                Country country = countryService.getCountryForUpdateAndDelete(id);
                CountryPojo pojo = countryService.convertCountryModelToCountryPojo(country);
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete country"+LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
            }
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return apiResponse(respCode, response);
    }

    @GetMapping(value = "/country/excel")
    public void countryExcel(HttpServletResponse response) throws Exception {
        CountryService service = SpringContext.getBean(CountryService.class);
        exportToExcel(service, response);
    }

    @GetMapping(value = "/country/pdf")
    public void countryPDF(HttpServletResponse response) throws Exception {
        CountryService service = SpringContext.getBean(CountryService.class);
        exportToPDF(service, response);
    }
}
