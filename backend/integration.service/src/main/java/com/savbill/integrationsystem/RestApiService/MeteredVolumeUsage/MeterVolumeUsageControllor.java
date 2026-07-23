package com.savbill.integrationsystem.RestApiService.MeteredVolumeUsage;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.meteredVolumeUsageForSubAcctName.MeteredVolumeUsageForSubsAccNameDTO;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceDto;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummaryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class MeterVolumeUsageControllor {

    @Autowired
    private CustomerRepository customerRepository;

//    private final log logger = LoggerFactory.getLogger(ChangeServiceControllor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RadiusClientService radiusClientService;

    @PostMapping("/getMeteredVolumeUsage")
    public GenericResponse<Object> getMeteredVolumeUsage(@RequestBody MeteredVolumeUsageDTO request) throws Exception {
        Map<String, Object> response = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Long mvnoId = SoapConstants.MVNOID;
        String userName = request.getUserName();
        MeteredVolumeUsageForSubsAccNameDTO userUsageSummary = null;

        log.info("Received request to get metered volume usage for username: {}", userName);

        if (userName == null || userName.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            log.warn("Input username is empty or null. Returning response code: {}, message: {}", responsecode, responseMessage);
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            genericResponse.setData(response);
            return genericResponse;
        }

        try {
            userUsageSummary = getUserUsageSummary(request);
            if (userUsageSummary != null) {
                responsecode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                log.info("Successfully fetched metered volume usage for username: {}", userName);
                response.put("MeteredVolumeUsage", userUsageSummary);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responsecode);
                genericResponse.setData(response);
                return genericResponse;
            }
        } catch (SQLException e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "SQL Exception during request processing.";
            responsecode = HttpStatus.EXPECTATION_FAILED.value();
            responseMessage = exceptionMessage;
            log.debug("SQLException encountered while processing request for username: {}: {}", userName, exceptionMessage, e);
            response.put("MeteredVolumeUsage", userUsageSummary);
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            genericResponse.setData(response);
            return genericResponse;
        } catch (RuntimeException e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Runtime Exception during request processing.";
            responsecode = HttpStatus.EXPECTATION_FAILED.value();
            responseMessage = exceptionMessage;
            log.debug("RuntimeException encountered while processing request for username: {}: {}", userName, exceptionMessage, e);
            response.put("MeteredVolumeUsage", userUsageSummary);
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            genericResponse.setData(response);
            return genericResponse;
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            responsecode = HttpStatus.EXPECTATION_FAILED.value();
            responseMessage = exceptionMessage;
            log.debug("Exception encountered while processing request for username: {}: {}", userName, exceptionMessage, e);
            response.put("MeteredVolumeUsage", userUsageSummary);
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            genericResponse.setData(response);
            return genericResponse;
        }

        log.warn("Unexpected flow reached in getMeteredVolumeUsage method.");
        return genericResponse;
    }

    public MeteredVolumeUsageForSubsAccNameDTO getUserUsageSummary(MeteredVolumeUsageDTO request) throws Exception {
        MeteredVolumeUsageForSubsAccNameDTO volumeUsageForSubsAccNameDTO = new MeteredVolumeUsageForSubsAccNameDTO();
        WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

        try {
            String SubscriberId = request.getUserName();
            Long mvnoId = SoapConstants.MVNOID;

            log.info("Fetching balance API data for subscriber: {}", SubscriberId);
            // Get data from radius client
            GenericDataDTO genericDataDTO = radiusClientService.GetBalanceApi(SubscriberId, mvnoId);
            GetBalanceDto dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()), GetBalanceDto.class);

            if (dataMessage == null) {
                throw new Exception("Input username is not available in session table.");
            }
            log.info("Fetched balance data for username: {}", SubscriberId);
            // Map fetched data into DTO
            volumeUsageForSubsAccNameDTO.setPlanName(dataMessage.getPlanName());
            volumeUsageForSubsAccNameDTO.setPlanId(dataMessage.getPlanId());
            volumeUsageForSubsAccNameDTO.setAggregateBytesLimit(dataMessage.getTotalQuota());
            volumeUsageForSubsAccNameDTO.setAggregateBytesRemaining(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()));
            volumeUsageForSubsAccNameDTO.setAggregateBytesUsed(dataMessage.getUsedQuota());
            volumeUsageForSubsAccNameDTO.setInBytesLimit(0.0);
            volumeUsageForSubsAccNameDTO.setInBytesRemaining(0.0);
            volumeUsageForSubsAccNameDTO.setInBytesUsed(0.0);
            volumeUsageForSubsAccNameDTO.setOutBytesLimit(0.0);
            volumeUsageForSubsAccNameDTO.setOutBytesRemaining(0.0);
            volumeUsageForSubsAccNameDTO.setOutBytesUsed(0.0);
            volumeUsageForSubsAccNameDTO.setUploadOctate(Double.valueOf(dataMessage.getDownloadQuota()));
            volumeUsageForSubsAccNameDTO.setDownloadOctate(Double.valueOf(dataMessage.getUploadQuota()));
            volumeUsageForSubsAccNameDTO.setQuotaUnit(dataMessage.getQuotaUnit());

        } catch (Exception e) {
            log.debug("Error while fetching user usage summary for username: {}", request.getUserName(), e);
            throw e;
        }
        return volumeUsageForSubsAccNameDTO;
    }
}
