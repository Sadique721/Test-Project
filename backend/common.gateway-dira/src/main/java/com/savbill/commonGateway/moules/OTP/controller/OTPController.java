package com.savbill.commonGateway.moules.OTP.controller;

import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.Customers.domain.Customers;
import com.savbill.commonGateway.moules.Customers.repository.CustomerRepository;
import com.savbill.commonGateway.moules.OTP.dto.GenerateOtpDto;
import com.savbill.commonGateway.moules.OTP.dto.OTPGenerateDTO;
import com.savbill.commonGateway.moules.OTP.dto.ValidateOtpDto;
import com.savbill.commonGateway.moules.OTP.service.OTPService;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Api(value = "OTP Management", description = "REST APIs related to Generate and Validate OTP !!!!", tags = "OTP")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OTP)
public class OTPController extends ApiBaseController {

    private static final String OTP = "otp";

    private static String MODULE = " [APIController] ";

    @Autowired
    private OTPService otpService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(OTPController.class);


    @ApiOperation(value = "Generate new OTP")
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generateOTP(@RequestBody OTPGenerateDTO generateOtp, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            StaffUser staffUser = new StaffUser();
            Mvno stafMvno = new Mvno();
            Customers customer = new Customers();
            String mobileNo = "";
            String emailId = "";
            String countryCode = "";
            Integer mvnoId;
            Long buId;
            if(generateOtp.getOtpForStaff()){
                staffUser = staffUserRepository.findStaffByUsername(generateOtp.getUsername());
                if(staffUser!=null) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    if (encoder.matches(generateOtp.getPassword(), staffUser.getPassword())) {
                        stafMvno = mvnoRepository.findById(Long.valueOf(staffUser.getMvnoId())).orElse(null);
                        mobileNo = staffUser.getPhone();
                        emailId = staffUser.getEmail();
                        countryCode = staffUser.getCountryCode();
                        mvnoId = staffUser.getMvnoId();
                        if(staffUser.getBusinessUnit() != null){
                            buId = staffUser.getBusinessUnit().getId();
                        }else{
                            buId = null;
                        }
                    }else{
                        throw new CustomValidationException(APIConstants.NO_CONTENT_FOUND,"Try to Login With Correct Credentials!",null);
                    }
                }else{
                    throw new CustomValidationException(APIConstants.NO_CONTENT_FOUND,"Username is invalid please check and try again!!!",null);
                }
            }else{
                Integer mvno = getMvnoIdFromCurrentStaff();
                customer = customerRepository.findCustomersByUsernameAndMvnoId(generateOtp.getUsername(), mvno);
                stafMvno = mvnoRepository.findById(Long.valueOf(customer.getMvnoId())).orElse(null);
                mobileNo = customer.getMobile();
                emailId = customer.getEmail();
                countryCode = customer.getCountryCode();
                mvnoId = customer.getMvnoId();
                buId = customer.getBuId();
            }
            if(stafMvno.getIsTwoFactorEnabled()!=null && stafMvno.getIsTwoFactorEnabled()){
                GenerateOtpDto generateOtpDto;
                if(stafMvno.getAuthEventName().equalsIgnoreCase("OTP BY EMAIL")){
                    generateOtpDto = new GenerateOtpDto(countryCode,"","OTP", emailId);
                }else{
                    generateOtpDto = new GenerateOtpDto(countryCode,mobileNo,"OTP", null);
                }
                otpService.generateOTP(generateOtpDto,mvnoId, buId,stafMvno.getAuthEventName());
                String email = otpService.maskEmail(generateOtpDto.getEmailId());
                response.put(OTP,"OTP sent to " + email );
                response.put("IsOTPRequired",true);
            }else{
                response.put("IsOTPRequired",false);
            }
            RESP_CODE = APIConstants.SUCCESS;
            //logger.info("Generating OTP is successfull for "+generateOtp.getEmailId()+" :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),RESP_CODE);

        }catch (CustomValidationException e){
            ApplicationLogger.logger.error(MODULE + e.getMessage(), e);
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            //response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put(APIConstants.ERROR_TAG,e.getMessage());
            //logger.error("Unable Generate Otp for "+generateOtp.getEmailId()+"  :  request: { From : {}}; Response : {{};}Exception:{}", request.getHeader("requestFrom"),RESP_CODE,ex.getStackTrace());

        }
        catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            //response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put(APIConstants.ERROR_TAG,"OTP profile is not configured to send OTP, Kindly connect to Administrator");
            //logger.error("Unable Generate Otp for "+generateOtp.getEmailId()+"  :  request: { From : {}}; Response : {{};}Exception:{}", request.getHeader("requestFrom"),RESP_CODE,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }


    @ApiOperation(value = "Validate OTP")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpDto validateOtpDto, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            otpService.validateOTP(validateOtpDto, mvnoId);
            response.put(OTP, "OTP is valid.");
            RESP_CODE = APIConstants.SUCCESS;
            //logger.info("Validating Otp For email "+validateOtpDto.getEmailId()+" is successfull :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),APIConstants.SUCCESS);
        }  catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put(APIConstants.MESSAGE,ex.getMessage());

            //logger.error("Unable to validate OTP "+validateOtpDto.getEmailId()+"  :  request: { From : {}}; Response : {{};Exception:{}}", request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);

        }
        return mvnoId;
    }



}
