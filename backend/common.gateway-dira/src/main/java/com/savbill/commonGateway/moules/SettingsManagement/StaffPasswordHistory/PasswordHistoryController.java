package com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory;

import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.exceptions.CustomMessageException;
import com.savbill.commonGateway.spring.SpringContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PASSWORD_POLICY_HISTORY)
public class PasswordHistoryController extends ApiBaseController {

    @PostMapping("/GeneratePassword")
    @Operation(
            operationId = "GeneratePassword",
            summary = "Save the password and record history",
            description = "This method saves the password and updates the password history. Throws an exception if the password matches the last 5 passwords."
    )
    public ResponseEntity<?> GeneratePassword(@Valid @RequestBody PasswordHistoryDTO passwordHistoryDTO, HttpServletRequest req) {
        Integer respCode;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PasswordHistoryService passwordHistoryService = SpringContext.getBean(PasswordHistoryService.class);
            PasswordHistory savedPasswordHistory = passwordHistoryService.GeneratePassword(passwordHistoryDTO);
            response.put("passwordHistory", savedPasswordHistory);
            response.put(APIConstants.MESSAGE, "Password Generated Successfully");
            respCode = APIConstants.SUCCESS;
        } catch (AlreadyExistException e) {
            respCode = HttpStatus.CONFLICT.value();
            response.put(APIConstants.MESSAGE, e.getMessage());
        } catch (CustomMessageException cex) {
            respCode = HttpStatus.CONFLICT.value();
            response.put(APIConstants.MESSAGE, cex.getMessage());
        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (DataIntegrityViolationException exc) {
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exc.getMessage());
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponse(respCode, response);
    }

}

