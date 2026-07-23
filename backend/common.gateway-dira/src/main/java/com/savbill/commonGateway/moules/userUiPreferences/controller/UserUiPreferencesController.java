package com.savbill.commonGateway.moules.userUiPreferences.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.userUiPreferences.model.UserUiPreferencesDTO;
import com.savbill.commonGateway.moules.userUiPreferences.service.UserUiPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.USER_UI_PREFERENCE)
public class UserUiPreferencesController extends ApiBaseController {
    private static String MODULE = " [UserUiPreferencesController] ";

    @Autowired
    private Tracer tracer;

    @Autowired
    private UserUiPreferencesService userUiPreferencesService;

    private final Logger LOGGER = LoggerFactory.getLogger(UserUiPreferencesController.class);

    @PostMapping("/list")
    public ResponseEntity<?> getUserPreferencesList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {

        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<UserUiPreferencesDTO> userUiPreferenceList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            userUiPreferenceList = userUiPreferencesService.fetchAllUserPrefrencesByPagination(requestDTO);
            if (null != userUiPreferenceList && 0 < userUiPreferenceList.getSize())
                response.put("userUiList", userUiPreferenceList);
            else
                response.put("userUiList", new ArrayList<>());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All userUiPreference list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);

        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(), ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All userUiPreference list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All userUiPreference list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return apiResponse(respCode, response, userUiPreferenceList);
    }

    @PostMapping("/save")
    public ResponseEntity<?> createUserUiPreference(@RequestBody UserUiPreferencesDTO pojo, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        try {
            pojo.setDelete(false);
            pojo = userUiPreferencesService.saveUserUi(pojo);
            response.put("UserUiPreference", pojo);
            response.put(APIConstants.MESSAGE, "Successfully Created");
            respCode = APIConstants.SUCCESS;
            req.getRequestURL();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    @PostMapping("/copy")
    public ResponseEntity<?> copyUserUiPreference(@RequestParam(name = "oldMvnoId") Integer oldMvnoId, @RequestParam(name = "newMvnoId") Integer newMvnoId, @RequestParam(name = "pageName") String pageName, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        UserUiPreferencesDTO pojo = userUiPreferencesService.getByMvnoIdAndPageName(oldMvnoId, pageName);
        if (pojo != null) {
            String mvnoName = pojo.getMvnoName();
            try {
                pojo = userUiPreferencesService.copyUserUi(pojo, newMvnoId);
                response.put("UserUiPreference", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Created");
                respCode = APIConstants.SUCCESS;
                req.getRequestURL();
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            } catch (CustomValidationException ce) {
                respCode = ce.getErrCode();
                response.put(APIConstants.ERROR_TAG, ce.getMessage());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
            } catch (Exception ex) {
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
            } finally {
                MDC.remove("type");
                MDC.remove("userName");
                MDC.remove("traceId");
                MDC.remove("spanId");
            }
        } else {
            respCode = APIConstants.NOT_FOUND;
            response.put(APIConstants.ERROR_TAG, "Given Mvno User Preferences not Available");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + oldMvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "Given Mvno User Preferences not Available" + LogConstants.LOG_STATUS_CODE + respCode);
        }

        return apiResponse(respCode, response);
    }

    @GetMapping("/findByMvnoAndPage")
    public ResponseEntity<?> getUiPreferencebyMvnoAndPageName(@RequestParam(name = "mvnoId") Integer mvnoId, @RequestParam(name = "pageName") String pageName, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        try {
            UserUiPreferencesDTO pojo = userUiPreferencesService.getByMvnoIdAndPageName(mvnoId, pageName);
            response.put("UserUiPreference", pojo);
            response.put(APIConstants.MESSAGE, "Successfully Created");
            respCode = APIConstants.SUCCESS;
            req.getRequestURL();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + pojo.getMvnoName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    @GetMapping("/findByMvno")
    public ResponseEntity<?> getUiPreferencebyMvno(@RequestParam(name = "mvnoId") Integer mvnoId, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<UserUiPreferencesDTO> pojo = userUiPreferencesService.getByMvnoId(mvnoId);
            if(!CollectionUtils.isEmpty(pojo)) {
                response.put("UserUiPreferenceList", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Fetch");
                respCode = APIConstants.SUCCESS;
            } else {
                response.put("Error", "List Not found by mvno");
                response.put(APIConstants.ERROR_TAG, "User Ui Not found");
                respCode = APIConstants.NOT_FOUND;
            }
            req.getRequestURL();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch UserUiPreference" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME  + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "copy UserUiPreference" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }
}
