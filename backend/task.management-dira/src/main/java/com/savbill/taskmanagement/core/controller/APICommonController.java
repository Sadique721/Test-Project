package com.savbill.taskmanagement.core.controller;

import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseFeedbackRel;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.security.spring.SpringContext;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

import static com.savbill.taskmanagement.core.modules.common.AuditableListener.MODULE;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE)
public class APICommonController extends ApiBaseController {


    @PostMapping( "/rating")
    public ResponseEntity<?> ticketRating(@Valid @RequestBody CaseFeedbackRel caseFeedbackDTO) {
        MDC.put("type", "crete");
        String SUBMODULE = " [ticketRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            CaseService caseService = SpringContext.getBean(CaseService.class);
            response.put("ticketDetails", caseService.ticketRating(caseFeedbackDTO));
            response.put("message", "Rating successfully");
            RESP_CODE = APIConstants.SUCCESS;
            ApplicationLogger.logger.info("ticketRating   " + caseFeedbackDTO.getGeneral_remarks() + ":  request: { From : {}}; Response : {{}}", MODULE, response, RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Unable to rate ticket " + caseFeedbackDTO.getTicketid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Unable to Unable to rate ticket " + caseFeedbackDTO.getTicketid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


}
