package com.savbill.commonGateway.moules.PartnerManagement;

import com.savbill.commonGateway.common.model.PaginationDetails;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class PartnerController {

    @Autowired
    PartnerService partnerService;
    private static String MODULE = " [PartnerController] ";
    private static final Logger logger= LoggerFactory.getLogger(PartnerController.class);

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PARTNER_ALL + "\",\"" + AclConstants.OPERATION_PARTNER_VIEW + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.Partners.PARTNER + "\")")
    @GetMapping("/partner/all")
    public ResponseEntity<?> getAllPartnerListWithoutPagination() throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        long start = System.currentTimeMillis();
        try {
            List<PartnerPojo> partnerList = partnerService.getAllActiveEntities();
            response.put("partnerlist", partnerList);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Fetching All PartnerList Without Pagination  :  request: { From : {}}; Response : {{]}", MODULE, RESP_CODE, response);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch getAllPartnerListWithoutPagination :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch getAllPartnerListWithoutPagination :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        long end = System.currentTimeMillis();
        System.out.println("**** total time for Controller ***"+(end-start));
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/partner/getAll")
    public ResponseEntity<?> getAllPartnerList() throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        long start = System.currentTimeMillis();
        try {
            List<PartnerPojo> partnerList = partnerService.getAllActiveEntitiesWithoutPartnerType();
            response.put("partnerlist", partnerList);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Fetching All PartnerList Without Pagination  :  request: { From : {}}; Response : {{]}", MODULE, RESP_CODE, response);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch getAllPartnerListWithoutPagination :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch getAllPartnerListWithoutPagination :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        long end = System.currentTimeMillis();
        System.out.println("**** total time for Controller ***"+(end-start));
        return apiResponse(RESP_CODE, response);
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }
    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }
}
