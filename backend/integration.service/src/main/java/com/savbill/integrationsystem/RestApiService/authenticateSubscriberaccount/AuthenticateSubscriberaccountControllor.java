package com.savbill.integrationsystem.RestApiService.authenticateSubscriberaccount;

import com.savbill.integrationsystem.RestApiService.authenticateUser.AuthenticateUserRestControllor;
import com.savbill.integrationsystem.RestApiService.authenticateUser.LoginPojo;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsauthenticatesubscriber.AuthenticateSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import java.rmi.RemoteException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AuthenticateSubscriberaccountControllor {
    @Autowired
    private CustomerRepository customerRepository;

    private final Logger logger = LoggerFactory.getLogger(AuthenticateUserRestControllor.class);

    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RadiusClientService radiusClientService;

    @PostMapping("/getAuthenticateSubscriber")
    public GenericDataDTO AuthenticateSubscriber(@RequestBody AuthenticateSubscribeDTO request) throws Exception {
        return getWsAuthenticateSubscribers(request);
    }

    public GenericDataDTO getWsAuthenticateSubscribers(@RequestPayload AuthenticateSubscribeDTO request) throws Exception {
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName().trim();
        String password = request.getPassword();
        GenericDataDTO response = new GenericDataDTO();

        logger.debug("Received request with username: {} and password: {}", userName, password);
        if (userName == null || userName.isEmpty()) {
            logger.warn("Input Username is Empty or Null");
            response.setResponseCode(SoapConstants.EMPTY);
            response.setResponseMessage("Input Username is Empty or Null");
            return response;
        } else if (password.trim() == null || password.trim().isEmpty()) {
            logger.warn("Input Password is Empty or Null");
            response.setResponseCode(SoapConstants.EMPTY);
            response.setResponseMessage("Input Password is Empty or Null");
            return response;
        }
        try {

            LoginPojo pojo = new LoginPojo();
            pojo.setUsername(userName.toLowerCase().trim());
            pojo.setPassword(password);
            logger.info("Validating Customer Details From Radius...");
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(pojo.getUsername(), SoapConstants.MVNOID);

            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) genericDataDTO.getData();
                logger.info("Validating user credentials From CMS...");
                Boolean loginValidator = cmsClientService.getAuthenticateUser(pojo, token);
                String custPassword = (String) map.get("password");

                if (custPassword != null && !custPassword.equals(password)) {
                    logger.info("Password mismatch for user: {}", userName);
                    response.setResponseCode(SoapConstants.SUCCESS_CODE);
                    response.setResponseMessage(SoapConstants.SUCCESS);
                    response.setData(false);
                    return response;
                } else {
                    logger.info("User credentials validated successfully: {}", userName);
                    response.setResponseCode(SoapConstants.SUCCESS_CODE);
                    response.setResponseMessage(SoapConstants.SUCCESS);
                    response.setData(loginValidator);
                    return response;
                }
            }
            logger.warn("Username is not available in SPR Table: {}", userName);
            response.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
            response.setResponseMessage("Username is not available in SPR Table ");
            return response;
        } catch (RuntimeException e) {
            logger.error("RuntimeException occurred while processing the request", e.getMessage());
            response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("SubscriberProfileWebServiceException while calling production API");
            return response;
        } catch (RemoteException e) {
            logger.error("RemoteException occurred while calling external SOAP service", e.getMessage());
            response.setResponseCode(SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE);
            response.setResponseMessage("AxisFault Exception due to technical issue");
            return response;
        } catch (Exception e) {
            logger.error("Exception occurred", e);
            response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("Exception");
            return response;
        }
    }
}
