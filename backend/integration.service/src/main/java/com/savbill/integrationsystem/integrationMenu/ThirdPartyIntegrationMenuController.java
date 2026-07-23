package com.savbill.integrationsystem.integrationMenu;


import com.savbill.integrationsystem.core.controller.APIResponseController;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.dto.ValidationData;
import com.savbill.integrationsystem.core.exceptions.AlreadyExistException;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL + URLConstants.THIRD_PARTY_INTEGRATION_MENU)
@Api(value = "ThirdPartyIntegrationMenuController", description = "REST APIs related to Third Party Integration Menus !!!!", tags = "ThirdPartyIntegrationMenuController")
public class ThirdPartyIntegrationMenuController extends APIResponseController {

    @Autowired
    ThirdPartyIntegrationMenuService thirdPartyIntegrationMenuService;

    private static final Logger logger = LoggerFactory.getLogger("ThirdPartyIntegrationMenuController.class");


    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMenu(@Valid @RequestBody ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto, HttpServletRequest req) throws Exception, AlreadyExistException {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            thirdPartyIntegrationMenuService.validateSaveRequest(thirdPartyIntegrationMenuDto);
            thirdPartyIntegrationMenuDto = thirdPartyIntegrationMenuService.save(thirdPartyIntegrationMenuDto);
            response.put("thirdPartyIntegrationMenuDto", thirdPartyIntegrationMenuDto);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Create Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }catch (AlreadyExistException e) {
            RESP_CODE = HttpStatus.CONFLICT.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Create Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Create Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMenu(@Valid @RequestBody ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto, @PathVariable Long id, HttpServletRequest req) throws Exception, AlreadyExistException {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            thirdPartyIntegrationMenuDto.setId(id);
            thirdPartyIntegrationMenuService.validateUpdateRequest(thirdPartyIntegrationMenuDto);
            thirdPartyIntegrationMenuDto = thirdPartyIntegrationMenuService.update(thirdPartyIntegrationMenuDto, req);
            response.put("thirdPartyIntegrationMenuDto", thirdPartyIntegrationMenuDto);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Update Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Update Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE, response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();

        try {
            thirdPartyIntegrationMenuService.delete(id);
            response.put(APIConstants.MESSAGE, "Successfully deleted");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + id + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Delete Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Delete Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchMenu(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = null;
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            thirdPartyIntegrationMenus = thirdPartyIntegrationMenuService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (thirdPartyIntegrationMenus.isEmpty()) {
                // Set the response code to 204 No Content
                RESP_CODE = HttpStatus.NO_CONTENT.value();

                // Set the response body to indicate no records found
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("CustAccountProfileList", new ArrayList<>());
                response.put("statusCode", RESP_CODE);

                // Log the event as successful with no content
//                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword: " +
//                        requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
//                        LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND +
//                        LogConstants.LOG_STATUS_CODE + RESP_CODE);

                // Return the response with 200 OK status, but indicate no content in the body
                return apiResponse(HttpStatus.OK.value(), response, thirdPartyIntegrationMenus);
            }

            if (0 < thirdPartyIntegrationMenus.getSize()) {
                response.put("CustAccountProfile", thirdPartyIntegrationMenuService.convertResponseModelIntoPojo(thirdPartyIntegrationMenus.getContent()));
            } else {
                response.put("CustAccountProfileList", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Third-PartyIntegrationMenu using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Search Third-PartyIntegrationMenu using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Perform Search Third-PartyIntegrationMenu using keyword : using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response, thirdPartyIntegrationMenus);
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getAllWithPagination", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMenusByPagination(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = null;
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            thirdPartyIntegrationMenus = thirdPartyIntegrationMenuService.getAllThirdPartyIntegrationMenuList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                    requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != thirdPartyIntegrationMenus && 0 < thirdPartyIntegrationMenus.getSize())
                response.put("thirdPartyIntegrationMenusList", thirdPartyIntegrationMenuService.convertResponseModelIntoPojo(thirdPartyIntegrationMenus.getContent()));
            else
                response.put("thirdPartyIntegrationMenusList", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE, response, thirdPartyIntegrationMenus);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    public ResponseEntity<?> getAllMenus(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus = thirdPartyIntegrationMenuService.getAllActiveEntities();
            response.put("thirdPartyIntegrationMenusList", thirdPartyIntegrationMenuService.convertResponseModelIntoPojo(thirdPartyIntegrationMenus).stream()
                    .sorted(Comparator.comparing(ThirdPartyIntegrationMenuDto::getId).reversed()).collect(Collectors.toList()));
            RESP_CODE = APIConstants.SUCCESS;

            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While fetch All Third-PartyIntegrationMenu list" + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMenuById(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ThirdPartyIntegrationMenu thirdPartyIntegrationMenu = thirdPartyIntegrationMenuService.getThirdPartyIntegrationMenuById(id);
            if (thirdPartyIntegrationMenu == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "CustAccountProfile Not Found!");
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch  Third-PartyIntegrationMenu " + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                response.put("thirdPartyIntegrationMenusList", thirdPartyIntegrationMenuService.convertModelToPojo(thirdPartyIntegrationMenu));
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetch Third-PartyIntegrationMenu by" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetch Third-PartyIntegrationMenu" + LogConstants.LOG_BY_NAME + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.REQUEST_BY +thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getParamsByEventAndClientName")
    @ApiModelProperty("An API for get defualt parameter for given thirdPartyIntegrationMenu")
    public ResponseEntity<?> findAllParameterByPaymentGatewayName(@RequestParam("eventName") String eventName, @RequestParam("clientName") String clientName, HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto = thirdPartyIntegrationMenuService.findDefaultMenuFields(eventName, clientName);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("thirdPartyIntegrationMenuData", thirdPartyIntegrationMenuDto);
            response.put("message", "ThirdPartyIntegrationMenu list found successfully");
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get Third-Party Integration parameter by Name : " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while get Third-Party Integration  parameter by Name: " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while get Third-Party Integration parameter by Name: " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getThirdPartyConfigurationByEvent")
    @ApiModelProperty("An API will get third party configration by event name")
    public ResponseEntity<?> getThirdPartyConfigurationByEvent(@RequestParam("eventName") String eventName, HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            List<ThirdPartyIntegrationMenuDto> thirdPartyIntegrationMenuDtos = thirdPartyIntegrationMenuService.findByEventName(eventName);
            if(thirdPartyIntegrationMenuDtos.isEmpty()){
                RESP_CODE = APIConstants.NO_CONTENT_FOUND;
                response.put("thirdPartyIntegrationMenuData" , thirdPartyIntegrationMenuDtos);
                response.put("message","No third party configuration found");
            }
            else {
                RESP_CODE = APIConstants.SUCCESS;
                response.put("thirdPartyIntegrationMenuData", thirdPartyIntegrationMenuDtos);
                response.put("message", "ThirdPartyIntegrationMenu list found successfully");
            }
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get Third-Party Integration parameter by Name : " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while get Third-Party Integration  parameter by Name: " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + ce.getErrCode());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while get Third-Party Integration parameter by Name: " + LogConstants.REQUEST_BY + thirdPartyIntegrationMenuService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return apiResponse(RESP_CODE, response);
    }
}

