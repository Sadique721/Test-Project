package com.savbill.integrationsystem.RestApiService.authenticateUser;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsauthenticateuser.WsAuthenticateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AuthenticateUserRestControllor {
    @Autowired
    private CustomerRepository customerRepository;

    private final Logger logger = LoggerFactory.getLogger(AuthenticateUserRestControllor.class);

    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/getAuthenticateUser")
    public GenericResponse<Object> getAuthenticateUser(@RequestBody AuthenticateUserRequest request) {
        WsAuthenticateUserResponse resp = new WsAuthenticateUserResponse();
        Map<String, Object> response = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        resp.setRequestId(requestId);
        logger.info("Request: {} Received for Authenticate : ", request);
        if (userName == null || userName.isEmpty()) {
            logger.warn("UserName is empty or Null.");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null";
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responseCode);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            return genericResponse;
        } else if (request.getPassword() == null || request.getPassword().isEmpty()) {
            logger.warn("Password is empty or Null.");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Password is Empty or null";
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responseCode);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            return genericResponse;
        }
        try {
            LoginPojo pojo = new LoginPojo();
            pojo.setUsername(request.getUserName().toLowerCase().trim());
            pojo.setPassword(request.getPassword());
            logger.info("Cms Client Calling For Authenticate user: {}", userName);
            Boolean loginValidator = cmsClientService.getAuthenticateUser(pojo, token);
            if (loginValidator) {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = "Username and Password is matched-AUTHENTICATED";
                logger.info("User:{} Authenticated successfully ResponseCode: {}", userName, responseCode);
            } else {
                responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
                responseMessage = "Input password is not match with Username ";
                logger.info("User:{} User Not Authenticated With ResponseCode: {}", userName, responseCode);
            }

            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responseCode);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            return genericResponse;
        } catch (Exception e) {
            logger.info("Exception occurred while sending request Message: {}", e.getMessage());
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responseCode);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
        }
        return genericResponse;
    }
}
