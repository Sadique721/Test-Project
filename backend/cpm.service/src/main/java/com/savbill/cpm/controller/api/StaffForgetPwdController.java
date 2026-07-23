package com.savbill.cpm.controller.api;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.pojo.api.ForgotPwdPojo;
import com.savbill.cpm.pojo.api.GenerateOtpDto;
import com.savbill.cpm.pojo.api.PasswordDto;
import com.savbill.cpm.pojo.api.ValidateOtpDto;
import com.savbill.cpm.service.common.OTPService;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;

@RestController
@RequestMapping("/staff")
public class StaffForgetPwdController extends ApiBaseController{
	private static final Logger logger = LoggerFactory.getLogger(StaffForgetPwdController.class);
	private static String MODULE = " [APIController] ";
	private static final String OTP = "otp";
	
	@Autowired
	private MessagesPropertyConfig messagesProperty;
	@Autowired
	AuditLogService auditLogService;
	@Autowired
    private OTPService otpService;
	
	@PostMapping("/getStaffContactByUserName")
	public ResponseEntity<?> getStaffContactByUserName(
			@Valid @RequestBody ForgotPwdPojo pojo) throws Exception {

		Integer RESP_CODE = APIConstants.FAIL;
		HashMap<String, Object> response = new HashMap<>();
		try {
			StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
			StaffUser staffUser = staffUserService.getByUserName(pojo.getUsername());
			GenerateOtpDto generateOtpDto = new GenerateOtpDto();
			if (staffUser == null) {
				RESP_CODE = APIConstants.NULL_VALUE;
				response.put(APIConstants.MESSAGE, "No Records Found!");
			} else {
				generateOtpDto.setCountryCode(staffUser.getCountryCode());
				generateOtpDto.setEmailId(staffUser.getEmail());
				generateOtpDto.setMobileNumber(staffUser.getPhone());
				try {
		            otpService.generateOTP(generateOtpDto);
		             RESP_CODE = APIConstants.SUCCESS;
		            response.put(OTP,"OTP has been generated successfully.");
		         } catch (Exception ex) {
		             ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
		             ex.printStackTrace();
		             RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
		             response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
		         }
		         
		} return apiResponse(RESP_CODE,response);
			}catch (Exception e) {
			RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
			response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			return apiResponse(RESP_CODE, response);
		}
	}

	/*
	 * @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL
	 * + "\",\"" + AclConstants.OPERATION_STAFF_USER_CHANGE_PASSWORD + "\")")
	 */
	@PutMapping("/resetpassword")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordDto pojo,
			@RequestBody ValidateOtpDto validateOtpDto, HttpServletRequest req)
			throws Exception {

		Integer RESP_CODE = APIConstants.FAIL;
		HashMap<String, Object> response = new HashMap<>();
		try {
	            otpService.validateOTP(validateOtpDto);
	            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
				StaffUser staffUser = staffUserService.resetPassword(pojo);
				response.put(CommonConstants.RESPONSE_MESSAGE,
						messagesProperty.get("api.staffuser.changepassword.success"));
				RESP_CODE = APIConstants.SUCCESS;
				auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER,
						AclConstants.OPERATION_STAFF_USER_CHANGE_PASSWORD, req.getRemoteAddr(), null,
						staffUser.getId().longValue(), pojo.getUserName());
			
		} catch (CustomValidationException ce) {
			ApplicationLogger.logger.error(MODULE + ce.getMessage(), ce);
			ce.printStackTrace();
			RESP_CODE = ce.getErrCode();
			response.put(APIConstants.ERROR_TAG, ce.getMessage());
		} catch (Exception ex) {
			ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
			ex.printStackTrace();
			RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
			response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
		}
		return apiResponse(RESP_CODE, response);
	}

}
