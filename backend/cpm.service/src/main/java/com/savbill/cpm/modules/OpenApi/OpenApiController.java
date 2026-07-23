package com.savbill.cpm.modules.OpenApi;

import brave.Tracer;
import com.savbill.cpm.constants.LogConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.CustomerPayment;
import com.savbill.cpm.modules.MtnPayment.model.MtnBuyPlanDTO;
import com.savbill.cpm.modules.MtnPayment.model.MtnPlanFetchDTO;
import com.savbill.cpm.modules.MtnPayment.model.MtnUssdDataResponse;
import com.savbill.cpm.modules.MtnPayment.model.MtnUssdResponseDTO;
import com.savbill.cpm.modules.MtnPayment.service.MtnPaymentService;
import com.savbill.cpm.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.service.common.ShorterService;
import com.savbill.cpm.utils.APIConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(value = "OPEN API", description = "REST APIs related that will expose to intigration !!!!", tags = "OPEN")
@RestController
@RequestMapping(UrlConstants.OPEN_API)
public class OpenApiController {

    private static final Logger log = LoggerFactory.getLogger(OpenApiController.class);

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ShorterService shorterService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @PostMapping(value ="/mtn/ussd/planFetch")
    public MtnUssdResponseDTO IntiateMtnUssdPlanFetchRequest(@RequestBody MtnPlanFetchDTO mtnPlanFetchDTO, HttpServletRequest req) {
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            log.info("Initiate request from ussd plan fetch with payload: "+mtnPlanFetchDTO);
            mtnUssdResponseDTO = mtnPaymentService.getPlanByServiceForMtnUssd(mtnPlanFetchDTO);

        }catch (CustomValidationException e) {
            log.error("Error Processing ussd request due to  : "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode(e.getErrCode().toString());
            if(mtnPlanFetchDTO.getTransactionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnPlanFetchDTO.getTransactionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        }
        catch (Exception e) {
            log.error("Error Processing ussd request due to: "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode("1100");
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request for plan fetch");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return mtnUssdResponseDTO;
    }

    @PostMapping(value ="/mtn/ussd/buyPlan")
    public MtnUssdResponseDTO IntiateMtnUssdBuyPlanRequest(@RequestBody MtnBuyPlanDTO mtnBuyPlanDTO, HttpServletRequest req) {
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            log.info("Initiate request from ussd plan buy with payload: "+mtnBuyPlanDTO);
            mtnPaymentService.generateTokenForMtnUSSD(mtnBuyPlanDTO.getUsername() , mtnBuyPlanDTO.getPassword());
            mtnPaymentService.validateOcdRequest(mtnBuyPlanDTO);
            CustomerPayment customerPayment =  mtnPaymentService.intiatePayment(mtnBuyPlanDTO);
            mtnPaymentService.SendOcsRequest(mtnBuyPlanDTO , customerPayment);
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchased Successfully");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
            mtnUssdResponseDTO.setStatusCode("0000");
            mtnUssdResponseDTO.setStatusMessage("Success");
            mtnUssdResponseDTO.setTransactionId(mtnBuyPlanDTO.getTransactionId());
        }catch (CustomValidationException e) {
            log.error("Error Processing ussd request due to  : "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode(e.getErrCode().toString());
            if(mtnBuyPlanDTO.getTransactionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnBuyPlanDTO.getTransactionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchase was Unsuccessful");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        }
        catch (Exception e) {
            log.error("Error Processing ussd request due to: "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode("2100");
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchase was Unsuccessful");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return mtnUssdResponseDTO;
    }

    @GetMapping("/getPaymentDetailsByHash")
    public ResponseEntity<Map<String, Object>> getPaymentDetailsByHash(
            @RequestParam(name = "hash") String hash, HttpServletRequest req) {

        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            Map<String, Object> serviceResponse = shorterService.getShorterByHash(hash);
            RESP_CODE = (Integer) serviceResponse.get("status");
            response.putAll(serviceResponse);
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return ResponseEntity.ok(response);
    }

}

