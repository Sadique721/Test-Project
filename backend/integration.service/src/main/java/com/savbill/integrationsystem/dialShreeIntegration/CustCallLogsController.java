package com.savbill.integrationsystem.dialShreeIntegration;

import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.controller.APIResponseController;
import com.savbill.integrationsystem.core.exceptions.AlreadyExistException;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

@RestController()
@Api(value = "CustomerCallLogsController", description = "REST APIs related to Customer Call Logs Data !!!!", tags = "CustomerCallLogsController")
public class CustCallLogsController extends APIResponseController {

    @Autowired
    CustCallLogsService custCallLogsService;

    @Autowired
    ApiAuditsService apiAuditsService;

    private static final Logger logger = LoggerFactory.getLogger(CustCallLogsController.class);



    @PostMapping("/callLogs/save")
    public ResponseEntity<?>saveDialShreeCallLogs(@RequestBody Object dialShreeDTO, HttpServletRequest request) throws Exception, AlreadyExistException {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            if (dialShreeDTO == null && Objects.isNull(dialShreeDTO)) {
                RESP_CODE = APIConstants.NO_CONTENT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Request Dto for DialShree gets Null.");
                logger.error(":::::::::::::::Request Dto for DialShree gets Null::::::::::::::");
                return apiResponse(RESP_CODE, response);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String jsoonObj = objectMapper.writeValueAsString(dialShreeDTO);
            CustCallLogsDTO custCallLogsDTO = objectMapper.readValue(jsoonObj, CustCallLogsDTO.class);
            custCallLogsService.sendCallLogDataToCMS(custCallLogsDTO);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("message", "Request Data for DialShree Sent Successfully to CMS");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),dialShreeDTO,ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success","DIALSHREE-CALLBACK",((LinkedHashMap) dialShreeDTO).get("uniqueid").toString());
            logger.info(":::::::::::::::::::::Send Request Data for DialShree Successfully to CMS:::::::::::::::::::::");
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Something went Wromg while send data to CMS: {}", ce.getMessage());
        } catch (KafkaException e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            logger.error("Kafka-related error while sending Call Log Data to CMS:{} ", e.getMessage());
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Exception While Sending data to CMS:{} ", e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }


}
