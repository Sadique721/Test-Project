package com.savbill.commonGateway.moules.PaymentConfig.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.BaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.controller.APIResponseController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.PaymentConfig.model.ChangeStatusDTO;
import com.savbill.commonGateway.moules.PaymentConfig.model.PaymentConfigDTO;
import com.savbill.commonGateway.moules.PaymentConfig.service.PaymentConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Api(value = "PaymentConfigController", description = "REST APIs related for Payment Config!!!!", tags = "Payment-Config-Controller")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.PAYMENT_CONFIG)
public class PaymentConfigController {

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private BaseController baseController;

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomersService customersService;

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentConfigController.class);

    @PostMapping( "/create")
    @ApiModelProperty("An api for create Payment Configuration")
    public ResponseEntity<?> savePaymentConfiguration(@Valid @RequestBody PaymentConfigDTO paymentConfigDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = " [paymentConfiguration()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            paymentConfigService.validateSaveRequest(paymentConfigDTO);
            PaymentConfigDTO returnPaymentConfigDto = paymentConfigService.savePaymentConfig(paymentConfigDTO);
            response.put("paymentConfig", returnPaymentConfigDto);
            response.put("message", "Payment Config add successfully");
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add payment configuration : " +paymentConfigDTO.getPaymentConfigName()+ LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"add payment configuration: "+paymentConfigDTO.getPaymentConfigName()+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"add payment configuration: "+paymentConfigDTO.getPaymentConfigName()+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PutMapping( "/update")
    @ApiModelProperty("An api for update payment configuration")
    public ResponseEntity<?> updatePaymentConfiguration(@Valid @RequestBody PaymentConfigDTO paymentConfigDTO , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = " [paymentConfig()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            paymentConfigService.validateUpdateRequest(paymentConfigDTO);
            PaymentConfigDTO returnPaymentConfigDto = paymentConfigService.updatePaymentConfig(paymentConfigDTO);
            response.put("paymentConfig", returnPaymentConfigDto);
            response.put("message", "Payment Config update successfully");
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"update payment configuration By Id :"+LogConstants.LOG_BY_NAME+paymentConfigDTO.getPaymentConfigName() + LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"update payment configuration by Id:"+ LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"update payment configuration by Id:"+ LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @GetMapping( "/findByPaymentConfigId")
    @ApiModelProperty("An API for find payment config by payment config id")
    public ResponseEntity<?> findPaymentConfigByConfigId(@RequestParam Long paymentConfigId , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = " [paymentConfig()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            PaymentConfigDTO paymentConfigDTO = paymentConfigService.findPaymentConfigById(paymentConfigId);
            if(paymentConfigDTO != null) {
                RESP_CODE = APIConstants.SUCCESS;
                response.put("paymentConfig", paymentConfigDTO);
                response.put("message", "Payment Config Find successfully");
            }
            else{
                RESP_CODE = 204;
                response.put("message", "Payment Config not found By payment config id");
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"find payment config by Id :"+LogConstants.LOG_BY_NAME+paymentConfigDTO.getPaymentConfigName() + LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"find payment configuration by Id:"+ LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"find payment configuration by Id:"+ LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @DeleteMapping( "/delete")
    @ApiModelProperty("Hard delete Payment config by Id")
    public ResponseEntity<?> deletePaymentConfig(@RequestParam Long paymentConfigId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            paymentConfigService.deletePaymentConfig(paymentConfigId);
            response.put("message" , "Payment Config delete Successfully");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"delete payment configuration by Id :" + paymentConfigId+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete payment configuration by Id: "+paymentConfigId+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete payment configuration by Id: "+ paymentConfigId +LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PostMapping( "/findByAllPaymentConfig")
    @ApiModelProperty("An API for find all payment config")
    public GenericDataDTO findAllPaymentConfig(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = paymentConfigService.findAllPaymentConfig(requestDTO);
            if(genericDataDTO.getPageRecords() > 0) {
                genericDataDTO.setResponseMessage("Payment Config Find successfully");
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            }
            else{
                genericDataDTO.setResponseCode(204);
                genericDataDTO.setResponseMessage("Payment Config List is not found");
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"find all payment configuration :"+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
             genericDataDTO.setResponseCode(ce.getErrCode());
             genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"find all payment configuration: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+ce.getErrCode());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"find all payment configuration: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping( "/getParameterByName")
    @ApiModelProperty("An API for get defualt parameter for given payment gateway")
    public ResponseEntity<?> findAllParameterByPaymentGatewayName(@RequestParam("name") String name , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            PaymentConfigDTO paymentConfigDTO = paymentConfigService.findAllPaymentGatewayParameterByName(name);
                RESP_CODE = APIConstants.SUCCESS;
                response.put("paymentConfig", paymentConfigDTO);
                response.put("message", "Payment gateway parameter list found successfully");

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"get Payment gateway parameter by Name : "+name+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"get Payment gateway parameter by Name: "+name+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"get Payment gateway parameter by Name: "+name+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PutMapping( "/changeStatus")
    @ApiModelProperty("An API for to change status of payment gateway ")
    public ResponseEntity<?> changePaymentGatewayStatus(@Valid @RequestBody ChangeStatusDTO changeStatusDTO , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = " [paymentConfig()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            paymentConfigService.validateChangeStatusRequest(changeStatusDTO);
            PaymentConfigDTO paymentConfigDTO = paymentConfigService.changeStatus(changeStatusDTO);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("paymentConfig", paymentConfigDTO);
            response.put("message", "Payment gateway status change successfully");

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Payment gateway configuration status change : "+paymentConfigDTO.getPaymentConfigName()+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Payment gateway configuration status change: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Payment gateway configuration status change: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }
    @GetMapping( "/getActivePaymentConfig")
    @ApiModelProperty("An API for get active payment config by given mvnoId")
    public ResponseEntity<?> getActivePaymentConfig(@RequestParam("paymentGatewayFor") String paymentGatewayFor, @RequestParam(name="mvnoId",required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", paymentConfigService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Long mvnoID;
            if(mvnoId != null){
                mvnoID = mvnoId;
            }else{
                mvnoID = customersService.getMvnoIdFromCurrentStaff().longValue();
            }
            List<PaymentConfigDTO> paymentConfigDTO = paymentConfigService.getActivePaymentConfig(paymentGatewayFor, mvnoID);
            if(!paymentConfigDTO.isEmpty()) {
                RESP_CODE = APIConstants.SUCCESS;
                response.put("activePaymentConfig", paymentConfigDTO);
                response.put("message", "Active Payment Configs found successfully");
            }
            else{
                RESP_CODE =  204;
                response.put("message","Active payment Configs not found");
            }

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Get Active Payment gateway configuration: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Get Active Payment gateway configuration: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Get Active Payment gateway configuration: "+LogConstants.REQUEST_BY + paymentConfigService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

}
