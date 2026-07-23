package com.savbill.commonGateway.moules.MvnoFeedBackConfig.Controller;

import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Domain.FeedbackConfig;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Service.FeedbackConfigService;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.FEEDBACK_CONFIG)
public class FeedbackConfigController {

    public static final String RESPONSE_CODE = "responseCode";
    @Autowired
    private FeedbackConfigService service;

    @PostMapping("/save")
    public GenericDataDTO create(@RequestBody FeedbackConfig config, HttpServletRequest request) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            boolean isValid = service.validateIsEventUniqueForSave(config);
            if(!isValid){
                dataDTO.setResponseMessage("Event with this name is already present for this mvno.");
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return dataDTO;
            }
            FeedbackConfig created = service.create(config);
            dataDTO.setData(created);
            dataDTO.setResponseCode(HttpStatus.CREATED.value());
            dataDTO.setResponseMessage("Feedback config created successfully");
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage("Error creating FeedbackConfig. Please contact support");
            log.error("Failed to create FeedbackConfig: {}", e.getMessage(), e);
        }
        return dataDTO;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        HashMap<String,Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        try {
            List<FeedbackConfig> list = service.getAll(getMvnoIdFromCurrentStaff());
            if (list.isEmpty()) {
                responseCode = APIConstants.NOT_FOUND;
                response.put(RESPONSE_CODE,responseCode);
                response.put(APIConstants.MESSAGE,"No FeedbackConfigs available");
            }else {
                responseCode = APIConstants.SUCCESS;
            }
            response.put(RESPONSE_CODE,responseCode);
            response.put("FeedbackConfigList",list);
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.OK);
        } catch (Exception e) {
            responseCode = APIConstants.INTERNAL_SERVER_ERROR;
            log.error("Failed to fetch FeedbackConfigs: {}", e.getMessage(), e);
            response.put(RESPONSE_CODE,responseCode);
            response.put(APIConstants.ERROR_MESSAGE,"Error fetching FeedbackConfigs. Please try again later.");
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Optional<FeedbackConfig> optionalConfig = service.getById(id);
            if (optionalConfig.isPresent()) {
                RESP_CODE = APIConstants.SUCCESS;
                response.put("data",optionalConfig.get());
                response.put("responseCode",RESP_CODE);
                return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.OK);
            } else {
                log.warn("FeedbackConfig not found with ID: {}", id);
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                response.put("responseCode",RESP_CODE);
                response.put(APIConstants.MESSAGE,"FeedbackConfig not found with ID: " + id);
                return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error retrieving FeedbackConfig by ID {}: {}", id, e.getMessage(), e);
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put("responseCode",RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE,"Error retrieving FeedbackConfig. Please contact support.");
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FeedbackConfig config) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {

            if(!service.validateForUpdate(config,id)){
                RESP_CODE = HttpStatus.NOT_MODIFIED.value();
                response.put(RESPONSE_CODE,RESP_CODE);
                response.put(APIConstants.MESSAGE , "Feedback Config with " + config.getEvent() + " name is already present");
                return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.OK);
            }
            FeedbackConfig updated = service.update(id, config);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("FeedbackConfig",updated);
            response.put(APIConstants.MESSAGE , "FeedbackConfig update Successfully");
            response.put("responseCode",RESP_CODE);
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("FeedbackConfig update failed for ID {}: {}", id, e.getMessage());
            RESP_CODE = HttpStatus.NOT_FOUND.value();
            response.put("responseCode",RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unexpected error updating FeedbackConfig with ID {}: {}", id, e.getMessage(), e);
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put("responseCode",RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE,"Error updating FeedbackConfig. Please contact support.");
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            service.delete(id);
            response.put("responseCode",RESP_CODE);
            response.put(APIConstants.MESSAGE , "FeedbackConfig delete Successfully");
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting FeedbackConfig with ID {}: {}", id, e.getMessage(), e);
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put("responseCode",RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE,e.getMessage());
            return new ResponseEntity<HashMap<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);

        }
        return mvnoId;
    }

    public Integer getStaffId() {
        Integer staffId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                staffId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getStaffId" + e.getMessage(), e);
        }
        return staffId;
    }
    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}" , APIConstants.FAIL,e.getStackTrace());
        }
        return mvnoIds;
    }
}
