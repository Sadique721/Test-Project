package com.savbill.cpm.modules.Charge.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.cpm.constants.LogConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.controller.api.ApiBaseController;
import com.savbill.cpm.controller.postpaid.ChargeController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.postpaid.ServiceChargeMapping;
import com.savbill.cpm.pojo.api.ChargePojo;
import com.savbill.cpm.service.postpaid.ChargeService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.spring.LoggedInUserService;
import com.savbill.cpm.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class ChargeController2 extends ApiBaseController {

    @Autowired
    ChargeService chargeService;
    @Autowired
    private LoggedInUserService loggedInUserService;
    @Autowired
    private Tracer tracer;
    private final Logger log = LoggerFactory.getLogger(ChargeController.class);
    @GetMapping("/charge2/{service}")
    public GenericDataDTO getChargeByService(@RequestParam("service") String service){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        String SUB_MODULE = getModuleNameForLog() + "[]";
        genericDataDTO.setResponseMessage("Success");
        try {
            List<ServiceChargeMapping> list = chargeService.getchargeByService(service);
            genericDataDTO.setDataList(list);
        }
        catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }

    @GetMapping("/chargeByMvno/{mvnoId}")
    public GenericDataDTO getChargeByMvno(@PathVariable("mvnoId") Integer mvnoId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        String SUB_MODULE = getModuleNameForLog() + "[]";
        genericDataDTO.setResponseMessage("Success");
        try {
            List<ChargePojo> list = chargeService.getChargeByMvno(mvnoId);
            if(!CollectionUtils.isEmpty(list))
                genericDataDTO.setDataList(list);
            else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("Failed to load data");
            }

        }
        catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }

    @GetMapping("/charge/{price}/{priceCompare}")
    public ResponseEntity<?> findChargebyMvnoAndPriceCompare(@PathVariable(name = "price") Integer price
            , @PathVariable(name = "priceCompare") String priceCompare
            , @RequestParam(name = "isDeleted") boolean isDeleted, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        LoggedInUser loggedInUser = loggedInUserService.getLoggedInUser();
        MDC.put("type", "Fetch");
        MDC.put("userName", loggedInUser.getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<ChargePojo> chargePojoList = chargeService.getchargeByPriceCompare(price, priceCompare, isDeleted);
            response.put("charges", chargePojoList);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Charges Username" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Charges Username" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Charges Username" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    public String getModuleNameForLog() {
        return null;
    }
}
