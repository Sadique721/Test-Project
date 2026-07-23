package com.savbill.integrationsystem.aggreagtionReport.controller;

import com.savbill.integrationsystem.aggreagtionReport.service.AggregationReportService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.MultipleBodyForPost;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController(value = "AggregationReportController")
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.AGGREGATION_REPORT)
//@Api(value = "NAVMasterController", description = "REST APIs related to NAV Master !!!!", tags = "NAVMasterController")
public class AggregationReportController {
    @Autowired
    AggregationReportService aggregationReportService;

    String getModuleNameForLog() {
        return "AggregationReportController[]";
    }

    private static final Logger logger = LoggerFactory.getLogger(AggregationReportController.class);

    @PostMapping(value = "/fetchAggregationReport")
    public GenericDataDTO fetchAggregationReport(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "startDate") String startDate, @RequestParam(name = "endDate") String endDate, @RequestParam(name = "navMasterId") Long navMasterId, HttpServletRequest request) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authTokenHeader = request.getHeader("Authorization");
            genericDataDTO = aggregationReportService.fetchAggregationReport(startDate, endDate, navMasterId, paginationRequestDTO, getMvnoId(authTokenHeader));
        } catch (CustomValidationException ce) {
            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/pushAggregationReport")
    public GenericDataDTO pushAggregationReport(@RequestBody List<Object> billGenFinalData, @RequestParam(name = "navMasterId") Long navMasterId, HttpServletRequest request) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authTokenHeader = request.getHeader("Authorization");
            genericDataDTO = aggregationReportService.push(billGenFinalData, navMasterId, getMvnoId(authTokenHeader));
//            logger.info("Fetching all data for  " + navMasterId + " " + navMasterId + " " + navMasterId + " " + navMasterId + " :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode());
        } catch (CustomValidationException ce) {
//            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.info("Failed to fetch all data for  " + navMasterId + " " + navMasterId + " " + navMasterId + " " + navMasterId + " :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
//            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.info("Failed to fetch all data for  " + navMasterId + " " + navMasterId + " " + navMasterId + " " + navMasterId + " :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/fetchAggregationPushedReport")
    public GenericDataDTO fetchAggregationPushedReport(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "startDate") String startDate, @RequestParam(name = "endDate") String endDate,
                                                       @RequestParam(name = "navMasterId") Long navMasterId, HttpServletRequest request) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authTokenHeader = request.getHeader("Authorization");
            genericDataDTO = aggregationReportService.fetchAggregationPushedReport(paginationRequestDTO, startDate, endDate, navMasterId, getMvnoId(authTokenHeader));
        } catch (CustomValidationException ce) {
            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/getRawDataOfFinalData")
    public GenericDataDTO getRawDataOfFinalData(@RequestBody MultipleBodyForPost multipleBodyForPost, @RequestParam(name = "isPushed") boolean isPushed) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = aggregationReportService.getRawDataOfFinalData(multipleBodyForPost.getPaginationRequestDTO(), multipleBodyForPost.getBillGenFinalData(), multipleBodyForPost.getNavMaster(), isPushed);
        } catch (CustomValidationException ce) {
            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    public Long getMvnoId(String encodedToken) throws IOException {
        String decodedToken = getDecoded(encodedToken);
        Long mavnoId = null;
        if (decodedToken != null) {
            JSONObject primaryObject = new JSONObject(decodedToken);
            JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
            mavnoId = mainObj.getLong("mvnoId");
        }
        return mavnoId;
    }

    public Long getStaffId(String encodedToken) throws UnsupportedEncodingException {
        String decodedToken = getDecoded(encodedToken);
        Long staffId = null;
        if (decodedToken != null) {
            JSONObject primaryObject = new JSONObject(decodedToken);
            JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
            staffId = mainObj.getLong("userId");
        }
        return staffId;
    }

    public Long getBUId(String encodedToken) throws UnsupportedEncodingException {
        String decodedToken = getDecoded(encodedToken);
        Long buId = null;
        if (decodedToken != null) {
            JSONObject primaryObject = new JSONObject(decodedToken);
            JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
            JSONArray buIds = mainObj.getJSONArray("buIds");
            if (buIds != null && buIds.length() > 0) {
                buId = buIds.getLong(0);
            }
        }
        return buId;
    }

    public String getDecoded(String encodedToken) throws UnsupportedEncodingException {
        String[] pieces = encodedToken.split("\\.");
        String b64payload = pieces[1];
        return new String(Base64.decodeBase64(b64payload), "UTF-8");
    }
}
