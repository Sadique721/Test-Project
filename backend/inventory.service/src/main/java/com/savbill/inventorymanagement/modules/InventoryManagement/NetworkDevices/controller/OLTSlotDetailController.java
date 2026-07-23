package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.DeleteContant;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.DataNotFoundException;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.SlotService.OLTSlotService;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OLT_SLOT)
public class OLTSlotDetailController extends ExBaseAbstractController<OLTSlotDetailDTO> {
    public OLTSlotDetailController(OLTSlotService service) {
        super(service);
    }
    private static String MODULE = " [OLTSlotDetailController] ";
    @Autowired
    private OLTSlotService oltSlotService;
    OLTSlotDetailDTO oltSlotDetailDTO = new OLTSlotDetailDTO();
    @Autowired
    Tracer tracer;
    private static final Logger LOGGER = Logger.getLogger(OLTSlotDetailController.class);
    @GetMapping("/byNetworkId/{networkId}")
    public GenericDataDTO getEntityByNetworkId(@PathVariable Long networkId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch OLTSlot Details by network id : " +networkId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        try {
            return GenericDataDTO.getGenericDataDTO(oltSlotService.getEntityByNetworkId(networkId));
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR+ "Fetch OLTSlot Details by network id : " +networkId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch OLTSlot Details by network id : " +networkId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            boolean flag = oltSlotService.duplicateVerifyAtSaveInSloat(oltSlotDetailDTO.getName(), oltSlotDetailDTO.getNetworkId().intValue());
            if (flag) {
                OLTSlotDetailDTO oltSlotDetailDTO1 = oltSlotService.saveEntity(oltSlotDetailDTO);
                genericDataDTO.setData(oltSlotDetailDTO1);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "create olt slot"+ LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICE_SLOAT,
                //  AclConstants.OPERATION_SLOAT_ADD, req.getRemoteAddr(), null, oltSlotDetailDTO1.getId(), oltSlotDetailDTO1.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.SLOT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR  + "create olt slot"+LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            }
        }
         catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.PORT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR  + "create olt slot" + LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }else{
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR  + "create olt slot" + LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO update(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try{
            String oldname=oltSlotService.getEntityById(oltSlotDetailDTO.getId()).getName();
            boolean flag = oltSlotService.duplicateVerifyEditInSloat(oltSlotDetailDTO.getName(), oltSlotDetailDTO.getNetworkId().intValue(), oltSlotDetailDTO.getId().intValue());
            if (flag) {
                OLTSlotDetailDTO oldOltSlotDetailDTO = oltSlotService.getEntityById(oltSlotDetailDTO.getId());
                OLTSlotDetailDTO oltSlotDetailDTO1 = oltSlotService.updateEntity(oltSlotDetailDTO);
//                String updatedValues = CommonUtils.getUpdatedDiff(oltSlotDetailDTO1,oltSlotDetailDTO);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+LogConstant.REQUEST_FOR + "Update olt slot"+LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Update olt slot " + UpdateDiffFinder.getUpdatedDiff(oldOltSlotDetailDTO,oltSlotDetailDTO1)+ LogConstant.LOG_STATUS+LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                genericDataDTO.setData(oltSlotDetailDTO1);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);

                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICE_SLOAT,
                // AclConstants.OPERATION_SLOAT_ADD, req.getRemoteAddr(), null, oltSlotDetailDTO1.getId(), oltSlotDetailDTO1.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.SLOT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update olt slot" +  LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            }
        }
        catch(Exception e){
            if (e instanceof DataIntegrityViolationException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.PORT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update olt slot" + LogConstant.LOG_BY_NAME+ oltSlotDetailDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }else{
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update olt slot" + LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO delete(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if(oltSlotDetailDTO.getId()!=null){
              boolean flag=  oltSlotService.deleteVerification(oltSlotDetailDTO.getId().intValue());
              if(flag){
                  genericDataDTO.setResponseMessage("Suceess");
                  genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                  LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Olt slot details"+LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+" by Id : " +oltSlotDetailDTO.getId()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

              }else{
                  genericDataDTO.setResponseMessage(DeleteContant.SLOT_DELETE_EXIST);
                  genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                  LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Olt slot details"+LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+" by Id : " +oltSlotDetailDTO.getId()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

              }
            }
        }catch(Exception e){

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Olt slot details"+LogConstant.LOG_BY_NAME+oltSlotDetailDTO.getName()+" by Id : " +oltSlotDetailDTO.getId()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    get the First name of logged in user
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


    @Override
    public String getModuleNameForLog() {
        return "[OLTSlotDetail Controller]";
    }
}
