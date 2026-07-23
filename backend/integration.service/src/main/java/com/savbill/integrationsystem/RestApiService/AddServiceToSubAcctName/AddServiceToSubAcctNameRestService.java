package com.savbill.integrationsystem.RestApiService.AddServiceToSubAcctName;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetosubacctname.AddServiceToSubAcctName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.soap.SOAPException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class AddServiceToSubAcctNameRestService {

    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public GenericDataDTO handleAddServiceToSubAcctRequest(AddServiceToSubAccDto request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            log.info("Request received handleAddServiceToSubAcctRequest for customer: {}", request.getString1());
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            WsAddServiceToAccount wsAddServiceToAccount = new WsAddServiceToAccount();
            String userName = request.getString1().trim();
            String serviceID = request.getString2().trim();
            if (userName == null || userName.isEmpty()) {
                log.warn("Username is empty or null");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("Username is Empty or Null");
                return genericDataDTO;
            }
            if (serviceID == null || serviceID.isEmpty()) {
                log.warn("Service ID is empty or null");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("Service ID is Empty or Null");
                return genericDataDTO;
            }
            if (serviceID == "SUSPENDUSER") {
                log.info("Received Suspend user's service ID, returning success response.");
                genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                return genericDataDTO;
            }
            if (userName != null && serviceID.trim() != null) {
                wsAddServiceToAccount.setUserName(userName);
                wsAddServiceToAccount.setServiceId(serviceID.trim());
                log.debug("Calling CMS Client Service with username: {} and service ID: {}", userName, serviceID);
                ResponseEntity<?> responseEntity = cmsClientService.AddServiceToAccountAccount(wsAddServiceToAccount, mvnoId, token);
                Object responseData = responseEntity.getBody();
                log.debug("Response from CMS Client Service: {}", responseData);
                if (responseData instanceof LinkedHashMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) responseData;

                    if (responseMap.containsKey("message") && "Username Not available".equals(responseMap.get("message"))) {
                        log.warn("Username not available in SPR table");
                        genericDataDTO.setResponseCode(SoapConstants.NO_RECOED_UPDATE_CODE);
                        genericDataDTO.setResponseMessage("Not Updated Record in SPR table due to Technical Issue");
                        return genericDataDTO;
                    } else if (responseMap.containsKey("message") && "ServiceId Not available".equals(responseMap.get("message"))) {
                        log.warn("Service ID not available in the In SPR table");
                        genericDataDTO.setResponseCode(SoapConstants.NOT_PRESENT);
                        genericDataDTO.setResponseMessage(SoapConstants.SERVICE_ID_NOT_AVAILABLE);
                        return genericDataDTO;
                    } else if (responseMap.get("deActivateResponse") != null) {
                        log.info("Service successfully deactivated");
                        genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                        genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                        return genericDataDTO;
                    }
                }
            }
            log.warn("No valid response received, returning null.");
            return null;
        } catch (SOAPException e) {
            log.error("SOAPException occurred: {}", e.getMessage());
            genericDataDTO.setResponseCode(SoapConstants.SQL_EXCPTION_CODE);
            genericDataDTO.setResponseMessage(SoapConstants.SQL_EXCEPTION);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Unexpected exception occurred: {}", e.getMessage());
            genericDataDTO.setResponseCode(SoapConstants.INTERNAL_ERROR);
            genericDataDTO.setResponseMessage("Exception");
            return genericDataDTO;
        }
    }
}
