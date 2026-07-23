package com.savbill.integrationsystem.Migration.Controller;


import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.Migration.Service.MigrationService;
import com.savbill.integrationsystem.billgen.service.CustomerService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL +"/migration")
public class MigrationController {
    Logger logger = LoggerFactory.getLogger(MigrationController.class);
    @Autowired
    CMSClient cmsClient;
    @Autowired
    private MigrationService migrationService;

    @Autowired
    CustomerService customerService;



    @PostMapping(value ="/uploadPlanXl", consumes = "multipart/form-data")
    public GenericDataDTO createPlan(HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            Integer responseCode = APIConstants.SUCCESS;
            genericDataDTO= migrationService.planCreateFromXLS(file,req);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PostMapping(value ="/uploadCusromerXl", consumes = "multipart/form-data")
    public GenericDataDTO createCustomer(HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            Integer responseCode = APIConstants.SUCCESS;
            genericDataDTO=migrationService.customerCreateFromXLS(file, req);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @ApiOperation(value = "This API will update plan By sheet")
    @PostMapping("/bulkPlanUpload")
    public ResponseEntity<?> updatePlanBySheet(@RequestPart("file") MultipartFile file, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        try {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer" + LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            ResponseEntity<Object> cmsResponse = migrationService.migrateUpdatePlan(file, req.getHeader("Authorization"));

            Object fileContent = cmsResponse.getBody();
            String message = (String) ((LinkedHashMap) fileContent).get("details");

            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            response.put("status", HttpStatus.OK.toString());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (CustomValidationException ce) {
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer" + LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return ResponseEntity.ok(
                    Response.builder()
                            .status(HttpStatus.EXPECTATION_FAILED.value())
                            .body((Response.Body) null)
                            .reason(ce.getMessage())
                            .build()
            );
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customere" + LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return ResponseEntity.ok(
                    Response.builder()
                            .status(responseCode)
                            .body((Response.Body) null)
                            .reason(e.getMessage())
                            .build()
            );
        }
    }

    @ApiOperation(value = "This API will download plan sheet")
    @GetMapping("/downloadPlan")
    public ResponseEntity<?> downloadPlanSheet(HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        try {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download Plan "+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            Response feignResponse = migrationService.migrateDownloadPlan(req.getHeader("Authorization"));

            InputStream inputStream = feignResponse.body().asInputStream();

            // Return as a downloadable file
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulk_plan.xlsx\"")
                    .body(new InputStreamResource(inputStream));
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Download Plan"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return migrationService.apiResponse(responseCode, response,null);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Download Plan"+ LogConstants.REQUEST_BY + customerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return migrationService.apiResponse(responseCode, response,null);
        }
    }


}
