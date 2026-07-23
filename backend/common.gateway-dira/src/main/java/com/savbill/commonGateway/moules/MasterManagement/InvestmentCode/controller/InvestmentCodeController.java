package com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.DTO.InvestmentCodeDto;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.repository.InvestmentCodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.service.InvestmentCodeService;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.INVESTMENT_CODE)
public class InvestmentCodeController extends ExBaseAbstractController<InvestmentCodeDto> {


    //    @Autowired//    AuditLogService auditLogService;

    public InvestmentCodeController(InvestmentCodeService service) {
        super(service);
    }

    @Autowired
    InvestmentCodeService investmentCodeService;

    @Autowired
    InvestmentCodeRepository investmentCodeRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;


    private InvestmentCodeDto investmentCodeDto;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvestmentCodeController.class);
    @Autowired
    private Tracer tracer;

    private static String MODULE = " [InvestmentCodeController] ";
//    private final ServiceAreaRepository serviceRepository;
//    //private final PlanServiceRepository planServiceRepository;
//
//    public InvestmentCodeController(InvestmentCodeService service,
//                                    ServiceRepository serviceRepository,
//                                    PlanServiceRepository planServiceRepository) {
//        super(service);
//        this.serviceRepository = serviceRepository;
//        this.planServiceRepository = planServiceRepository;
//    }

    @Override
    public String getModuleNameForLog() {
        return " [InvestmentCodeController] ";
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.INVESTMENT_CODE_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody InvestmentCodeDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        int respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", investmentCodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = investmentCodeService.duplicateVerifyAtSave(entityDTO.getIcname());
            boolean flagcode = investmentCodeService.duplicateVerifyAtSaveForCode(entityDTO.getIccode());
            if (flag && flagcode) {
                respCode = APIConstants.SUCCESS;
                dataDTO = super.save(entityDTO, result, authentication, req,res);
                InvestmentCodeDto investmentCodeDto = (InvestmentCodeDto) dataDTO.getData();
                InvestmentCode investmentCode = investmentCodeService.convertDtoToDomain(investmentCodeDto);
                createDataSharedService.sendEntitySaveDataForAllMicroService(investmentCode);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            } else if (!flag) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.IC_NAME_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Investment Code" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_STATUS_CODE + respCode);

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.IC_CODE_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_STATUS_CODE + respCode);
            }
            return dataDTO;
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("request Form") + LogConstants.REQUEST_FOR + "Create InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.INVESTMENT_CODE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody InvestmentCodeDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        int respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", investmentCodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.spanIdString());
        MDC.put("spanId", traceContext.traceIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            investmentCodeService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = investmentCodeService.duplicateVerifyAtEdit(entityDTO.getIcname(), entityDTO.getId());
            if (flag) {
                InvestmentCode oldname = investmentCodeService.getById(entityDTO.getId());
                InvestmentCode oldClone = new InvestmentCode(oldname);

                dataDTO = super.update(entityDTO, result, authentication, req,res);
                InvestmentCodeDto investmentCodeDto = (InvestmentCodeDto) dataDTO.getData();
                InvestmentCode investmentCode = investmentCodeService.convertDtoToDomain(investmentCodeDto);
                createDataSharedService.updateEntityDataForAllMicroService(investmentCode);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update InvestmentCode" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + " , Updated InvestmentCode Details " + UpdateDiffFinder.getUpdatedDiff(oldClone, investmentCode) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.IC_NAME_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + respCode);
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            respCode = e.getErrCode();
            String entity1 = entityDTO.getIcname();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update InvestmentCode" + LogConstants.LOG_BY_NAME + entity1 + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update InvestmentCode" + LogConstants.LOG_BY_NAME + investmentCodeDto.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + respCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }
        return dataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\",\"" +
//            AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\",\"" +
//            AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" +
//            AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<InvestmentCodeDto> list = investmentCodeService.getAllEntities().stream().filter(investmentCodeDto -> !investmentCodeDto.getIsDeleted() && investmentCodeDto.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.INVESTMENT_CODE + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAll(requestDTO,req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.INVESTMENT_CODE + "\")")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") List<GenericSearchModel> page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String
                                         sortBy, @RequestBody Integer filter, HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return investmentCodeService.search(page, pageSize, sortOrder, sortBy, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.INVESTMENT_CODE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody InvestmentCodeDto entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", investmentCodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            investmentCodeService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = investmentCodeService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req,res);
                InvestmentCodeDto investmentCodeDto = (InvestmentCodeDto) dataDTO.getData();
                InvestmentCode investmentCode = investmentCodeService.convertDtoToDomain(investmentCodeDto);
                createDataSharedService.deleteEntityDataForAllMicroService(investmentCode);

                if (investmentCodeDto != null) {
                    // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                    //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());

                    respCode = APIConstants.SUCCESS;
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Investment Code" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.INVESTMENT_NAME_DELETE_EXIST);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Investment Code" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + respCode);
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete InvestmentCode" + LogConstants.LOG_BY_NAME + entityDTO.getIcname() + LogConstants.REQUEST_BY + investmentCodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;

    }

    @GetMapping(value = "/getIcNames")
    public List<InvestmentCode> getIcNameByBuid() {
        try {
            List<Long> BuIds = investmentCodeService.getBUIdsFromCurrentStaff();
            List<InvestmentCode> investmentCodeList = new ArrayList<>();
            if (!BuIds.isEmpty()) {
                investmentCodeList = investmentCodeService.getIcname(BuIds);
                investmentCodeList = investmentCodeService.removebindedInvestmet(investmentCodeList);
            } else {
                investmentCodeList = investmentCodeService.getAllIcname();
                investmentCodeList = investmentCodeService.removebindedInvestmet(investmentCodeList);
            }
            investmentCodeList = investmentCodeList.stream().filter(investmentCode -> investmentCode.getMvnoId() == getMvnoIdFromCurrentStaff() || investmentCode.getMvnoId() == 1).collect(Collectors.toList());
            return investmentCodeList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
