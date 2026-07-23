package com.savbill.integrationsystem.mvno;

import com.savbill.integrationsystem.RestApiService.recordpayment.SearchPaymentPojo;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL)
public class CommonApiGatewayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApiGatewayController.class);

    @Autowired
    private  ApiGatewayClient apiGatewayClient;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private RevenueClient revenueClient;

//    @Autowired
//    CmsClientService cmsClientService;

    @Autowired
    CmsClient cmsClient;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "This API will Save Mvno via APigateway")
    @PostMapping ("/saveMvno")
    public GenericDataDTO saveMvno(@Valid  @RequestBody IspDto saveMvno, HttpServletRequest request)  {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            MvnoDTO mvnoDTO = mvnoService.getMvnoDto(saveMvno);
            String token = request.getHeader("authorization");
            if(token!=null){
                if(mvnoId==1){
                    dataDTO = apiGatewayClient.saveMvno(mvnoDTO, token);
                }else {
                    throw new CustomValidationException(HttpStatus.SC_BAD_REQUEST, "authorization token is invalid !!", null);
                }
            }
            else{
                throw new CustomValidationException(HttpStatus.SC_BAD_REQUEST,"authorization token not found !!",null );
            }
            RESP_CODE = dataDTO.getResponseCode();
            LOGGER.info(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        } catch (CustomValidationException ce) {
            dataDTO.setResponseCode(ce.getErrCode());
            dataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin"  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        }
        catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        }

    }

    @ApiOperation(value = "This API will Save Mvno via APigateway")
    @PostMapping ("/recordPayment")
    public GenericDataDTO recordPayment(@Valid  @RequestBody PaymentDto paymentDto, HttpServletRequest request ) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            String token = request.getHeader("authorization");
            if(token!=null){
               // if(mvnoId==1){
                    dataDTO = revenueClient.ispRecordPayment(paymentDto,token);
                //}else {
                    //throw new CustomValidationException(HttpStatus.SC_BAD_REQUEST, "authorization token is invalid !!", null);
                //}
            }else{
                throw new CustomValidationException(HttpStatus.SC_BAD_REQUEST,"authorization token not found !!",null );
            }
            RESP_CODE = dataDTO.getResponseCode();
            Thread.sleep(2000);
            if (RESP_CODE.equals(APIConstants.SUCCESS)){
                SearchPaymentPojo searchPaymentPojo = mvnoService.approvePayment(dataDTO,paymentDto);
                ResponseEntity<?> responseEntity = cmsClient.approvePayment(searchPaymentPojo,token);
//                ResponseEntity<?> responseEntity = cmsClientService.approvePayment(searchPaymentPojo,token);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        } catch (CustomValidationException ce) {
            dataDTO.setResponseCode(ce.getErrCode());
            dataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin"  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        } catch (Exception e) {
            dataDTO.setResponseCode(RESP_CODE);
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + "NetSuite Isp Api" + LogConstants.REQUEST_FOR + "creating Mvno" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return dataDTO;
        }

    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff response:{},exception:{}" ,APIConstants.FAIL,e.getStackTrace());
        }
        return mvnoId;
    }


}
