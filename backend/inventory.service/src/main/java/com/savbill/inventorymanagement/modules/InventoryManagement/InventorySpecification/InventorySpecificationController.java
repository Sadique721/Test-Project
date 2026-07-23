package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistoryRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingDto;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * The type Inventory specification controller.
 */
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.INVENTORY_SPECIFICATION)
public class InventorySpecificationController {
    /**
     * The Inventory specification service.
     */
    @Autowired
    InventorySpecificationService inventorySpecificationService;
    /**
     * The Tracer.
     */
    @Autowired
    Tracer tracer;
    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(InventorySpecificationController.class);

    /**
     * The Inventory specification history repo.
     */
    @Autowired
    InventorySpecificationHistoryRepo inventorySpecificationHistoryRepo;
    /**
     * The Inventory specification dto.
     */
    InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();

    /**
     * Gets all inventory spec by inward id.
     * @param inward_id the inward id
     * @return the all inventory spec by inward id
     */
    @GetMapping("/getAllInventorySpecByInwardId")
    public GenericDataDTO getAllInventorySpecByInwardId(@Valid @RequestParam Long inward_id) {
        return inventorySpecificationService.getAllInventorySpecByInwardId(inward_id);

    }

    /**
     * Gets all inventory spec by item id.
     * @param itemId the item id
     * @return the all inventory spec by item id
     */
    @GetMapping("/getAllInventorySpecByItemId")
    public GenericDataDTO getAllInventorySpecByItemId(@Valid @RequestParam Long itemId) {
        return inventorySpecificationService.getAllInventorySpecByInwardSpecId(itemId);
    }

    /**
     * Update specification value generic data dto.
     * @param dto the dto
     * @param req the req
     * @return the generic data dto
     * @throws Exception the exception
     */
    @PostMapping("/updateSpecificationValue")
    public GenericDataDTO updateSpecificationValue(@RequestBody UpdateInvenSpecValueDTO dto, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        InventorySpecificationDto inventorySpecificationDto = new InventorySpecificationDto();
        try {
            if (dto.getItemId() != null && dto.getInvenId() != null) {
                genericDataDTO.setData(inventorySpecificationService.updateSpecificationValue(dto.getItemId(), dto.getInvenId(), dto.getNewParamValue()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Specification Value " + LogConstant.LOG_BY_NAME + dto.getItemId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_EXTENDED.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Specification Value " + LogConstant.LOG_BY_NAME + dto.getItemId() + inventorySpecificationDto.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Specification Value " + LogConstant.LOG_BY_NAME + dto.getItemId() + inventorySpecificationDto.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Specification Value " + LogConstant.LOG_BY_NAME + dto.getItemId() + inventorySpecificationDto.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    /**
     * Gets all parameter history by param id.
     * @param itemId the item id
     * @param paramId the param id
     * @param req the req
     * @return the all parameter history by param id
     */
    @GetMapping("/getAllParameterHistoryByParamId/{itemId}/{paramId}")
    public GenericDataDTO getAllParameterHistoryByParamId(@PathVariable Long itemId, @PathVariable Long paramId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<ItemAssignHistoryMappingDto> histories = inventorySpecificationService.findAllItemHistory(itemId, paramId);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(histories);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter History for inward Id: " + itemId + "And paramid: " + paramId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter History for inward Id: " + itemId + "And paramid: " + paramId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter History for inward Id: " + itemId + "And paramid: " + paramId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     * Gets logged in user.
     * @return the logged in user
     */
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    /**
     * Find all params by cust id generic data dto.
     * @param parametersDTO the parameters dto
     * @param req the req
     * @return the generic data dto
     */
    @PostMapping("/customerParam")
    public GenericDataDTO findAllParamsByCustId(@RequestBody InvenotryCustParamsDto parametersDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        if (req.getHeader(LogConstant.TRACE_ID) != null)
            MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        else
            MDC.put(LogConstant.TRACE_ID, UUID.randomUUID());

        MDC.put("spanId", traceContext.spanIdString());
        try {
            InvenotryCustParamsDto custParamsDto = inventorySpecificationService.getCustSpecificParams(parametersDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(custParamsDto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by Item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     * Find all params by cust serv id generic data dto.
     * @param custServiceId the cust service id
     * @param req the req
     * @return the generic data dto
     */
    @GetMapping("/custParam")
    public GenericDataDTO findAllParamsByCustServId(@RequestParam Long custServiceId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        if (req.getHeader(LogConstant.TRACE_ID) != null)
            MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        else
            MDC.put(LogConstant.TRACE_ID, UUID.randomUUID());

        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO = inventorySpecificationService.getCustSpecificParamsByService(custServiceId);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by Item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     * Find all params by cust inv mapp id generic data dto.
     * @param custInvId the cust inv id
     * @param req the req
     * @return the generic data dto
     */
    @GetMapping("/custParamByMappingId")
    public GenericDataDTO findAllParamsByCustInvMappId(@RequestParam Long custInvId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        if (req.getHeader(LogConstant.TRACE_ID) != null)
            MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        else
            MDC.put(LogConstant.TRACE_ID, UUID.randomUUID());

        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO = inventorySpecificationService.getCustSpecificParamsByCustInvMapId(custInvId);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Parameter by Item ids" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

}
