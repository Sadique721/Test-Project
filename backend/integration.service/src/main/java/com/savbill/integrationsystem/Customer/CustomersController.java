package com.savbill.integrationsystem.Customer;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.billgen.service.CustomerService;
import com.savbill.integrationsystem.core.controller.APIResponseController;
import com.savbill.integrationsystem.core.dto.CustomerResponseDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.deviceveri.service.CustomersService;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL)
public class CustomersController extends APIResponseController {

    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);


    public String getModuleNameForLog() {

        return "[CustomersController]";

    }

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customer/getCustomersByMobile")
    public ResponseEntity<?> getCustomerByMobile(@RequestParam(name = "mobileNo",required = false) String mobileNo,@RequestParam(name = "email",required = false) String email, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",customerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            if(email != null){
                return cmsClient.getCustomerByEmail(email,req.getHeader("Authorization"));
            }
            else {
                return cmsClient.getCustomerByMobileNo(mobileNo, req.getHeader("Authorization"));
            }
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }


    @ApiOperation(value = "This API will fetch customer with only ")
    @GetMapping("/customer/getCustomersByAccount")
    public ResponseEntity<?> getCustomersByAccount(@RequestParam(name = "accountNo",required = true) String accountNo, @RequestHeader("Authorization") String token, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",customerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            Integer responseCode = APIConstants.SUCCESS;
            CustomerResponseDTO customer = customersService.getCustomerByAccountNo(accountNo,token);
            response.put("status", HttpStatus.OK.value());
            response.put("customer",customer);
            response.put("msg","Customer found successfully");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "GET" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.FIND_CUSTOMER, accountNo);
            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = ce.getErrCode();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            response.put("status",ce.getErrCode());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "GET" , "Failure", PaymentGatewayConfigurationConstant.AUDITCONSTANT.FIND_CUSTOMER, accountNo);
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response) , headers , responseTime , requestInitiationTime , null , 1, "GET" , "Failure", PaymentGatewayConfigurationConstant.AUDITCONSTANT.FIND_CUSTOMER, accountNo);
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }
}
