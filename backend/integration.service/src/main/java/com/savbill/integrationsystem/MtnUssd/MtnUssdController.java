package com.savbill.integrationsystem.MtnUssd;

import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
public class MtnUssdController {

    private final Logger log = LoggerFactory.getLogger(MtnUssdController.class);

    @Autowired
    private MtnUssdService mtnUssdService;

    @PostMapping(value ="/ussd")
    public MtnUssdResponseDTO IntiateMtnUssdRequest(@RequestParam(name = "service") String service, @RequestBody MtnUssdDTO mtnUssdDTO, HttpServletRequest req) {
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            log.info("Initiate request from ussd with payload: "+mtnUssdDTO);
            mtnUssdService.validateMtnUssdRequest(mtnUssdDTO);
            mtnUssdResponseDTO=mtnUssdService.processingMtnUssdRequest(mtnUssdDTO , service);
        }catch (CustomValidationException e) {
            log.error("Error Processing ussd request due to  : "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode(e.getErrCode().toString());
            if(mtnUssdDTO.getSessionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnUssdDTO.getSessionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        }
        catch (Exception e) {
            log.error("Error Processing ussd request due to: "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode("1000");
            if(mtnUssdDTO.getSessionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnUssdDTO.getSessionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return mtnUssdResponseDTO;
    }

}
