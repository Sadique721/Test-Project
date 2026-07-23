package com.savbill.commonGateway.common.controller;

import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.model.ClientServicePojo;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.MenuConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class ClientServiceController extends ApiBaseController {
    private static String MODULE = " [MvnoController] ";
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceController.class);
//    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.SYSTEM_CONFIG + "\")")
    @GetMapping("/system/configuration/")
    public ResponseEntity<?> getSystemConfigInfo() throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ClientServiceSrv clientServiceSrv2 = SpringContext.getBean(ClientServiceSrv.class);
            List<ClientService> clientServiceSrv2List = clientServiceSrv2.getAllEntity();
            response.put("clientlist", clientServiceSrv2.convertResponseModelIntoPojo(clientServiceSrv2List));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Fetching System Configurations  : response{} ", RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch system configurations :   Error :{} ;Exception:{}", RESP_CODE, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch system configurations :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.SYSTEM_CONFIG_EDIT + "\")")
    @PutMapping("/system/configuration/{id}")
    public ResponseEntity<?> updateSystemConfigInfo(@Valid @RequestBody ClientServicePojo pojo,
                                                    @PathVariable Integer id) throws Exception {
        MDC.put("type", "Update");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ClientServiceSrv clientServiceSrv2 = SpringContext.getBean(ClientServiceSrv.class);
            pojo.setId(id);
            ClientService cl = clientServiceSrv.get(id);
//            String updatedValues = CommonUtils.getUpdatedDiff(clientServiceSrv2.convertConfigurationModelToConfigurationPojo(cl), pojo);
            pojo.setName(cl.getName());
            pojo.setMvnoId(cl.getMvnoId());
            clientServiceSrv2.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            boolean flag = clientServiceSrv2.duplicateVarification(pojo, CommonConstants.OPERATION_UPDATE);
            if (flag) {
                pojo = clientServiceSrv2.update(pojo);
                response.put("ClientService", pojo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("Updating System configuration with  " + pojo.getName() + "   :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Name is Already in Use", null);
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Update System configuration with name " + pojo.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Updating System configuration with name " + pojo.getName() + "  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CLIENT_SERVICE_ALL + "\",\"" + AclConstants.OPERATION_CLIENT_SERVICE_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Settings.SYSTEM_CONFIG_CREATE + "\")")
    @PostMapping("/system/configuration/")
    public ResponseEntity<?> updateAllSystemConfigInfo(@Valid @RequestBody ClientServicePojo pojo) throws Exception {
        MDC.put("type", "Update");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ClientServiceSrv clientServiceSrv2 = SpringContext.getBean(ClientServiceSrv.class);
            clientServiceSrv2.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = clientServiceSrv2.duplicateVarification(pojo, CommonConstants.OPERATION_ADD);
            if (flag) {
                pojo = clientServiceSrv2.save(pojo, CommonConstants.OPERATION_ADD);
                response.put("ClientService", pojo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("Updating Systen config info " + clientServiceSrv2.get(pojo.getId()).getName() + "  to " + pojo.getName() + "  :  request: { From : {}}; Response : {{}}", RESP_CODE, response);
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Name is Already in Use", null);
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to  update system all config info " + pojo.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to update all  system config info " + pojo.getName() + " : :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }
//    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.SYSTEM_CONFIG + "\")")
    @GetMapping("/system/configuration" + UrlConstants.SEARCH_SYSTEM_CONFIG_BY_NAME)
    public ResponseEntity<?> gsearchConfigurationByName(@RequestParam(value = "name") String name) {
        MDC.put("type", "Fetch");
        String SUBMODULE = " [getConfigurationByName()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
       // GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            response.put("clientlist",clientServiceSrv.getByClientServicePojoNamelike(name));
            logger.info("Fetching System configuration :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, LogConstants.LOG_SUCCESS);
        } catch (Exception ex) {
            RESP_CODE = APIConstants.SUCCESS;
            //		ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch System configuration :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, ex.getMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/system/configuration" + UrlConstants.GET_SYSTEM_CONFIG_BY_NAME)
    public GenericDataDTO getConfigurationByName(@RequestParam(value = "name") String name) {
        MDC.put("type", "Fetch");
        String SUBMODULE = " [getConfigurationByName()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(clientServiceSrv.getByClientServicePojoName(name));
            logger.info("Fetching System configuration :  request: { From : {}}; Response : {{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch System configuration :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
