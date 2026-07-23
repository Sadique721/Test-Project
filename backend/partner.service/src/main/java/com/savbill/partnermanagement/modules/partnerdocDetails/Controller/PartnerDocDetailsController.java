package com.savbill.partnermanagement.modules.partnerdocDetails.Controller;


import com.savbill.partnermanagement.auditLog.service.AuditLogService;
import com.savbill.partnermanagement.common.FileSystemService;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.core.controller.ExBaseAbstractController;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.PaginationRequestDTO;
import com.savbill.partnermanagement.core.dto.ValidationData;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.exceptions.DataNotFoundException;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.partnerdocDetails.Service.PartnerDocDetailsService;
import com.savbill.partnermanagement.modules.partnerdocDetails.Service.PartnerPaymentService;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerCreditDocument;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerDebitDocument;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerDocDeleteModel;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerdocDTO;
import com.savbill.partnermanagement.security.spring.SpringContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_DOC)
public class PartnerDocDetailsController extends ExBaseAbstractController<PartnerdocDTO> {
    private static String MODULE = " [PartnerDocDetailsController] ";

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    PartnerDocDetailsService partnerDocDetailsService;

    @Autowired
    PartnerService partnerService;

    private String PATH;

    @Autowired
    private Tracer tracer;


    private final Logger log = Logger.getLogger(PartnerDocDetailsController.class);

    public PartnerDocDetailsController(PartnerDocDetailsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerDocDetailsController]";
    }

    @Override
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody PartnerdocDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            entityDTO.setLastModifiedById(getMvnoIdFromCurrentStaff());
        }
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to update Partner-Doc-Details" + entityDTO.getUniquename() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "unable to fatch updated entity " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to update Partner-Doc-Details" + entityDTO.getUniquename() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "AUnable to update entity " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            PartnerdocDTO partnerdocDTO = partnerDocDetailsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            //String updatedValues = CommonUtils.getUpdatedDiff(partnerdocDTO,entityDTO);
            entityDTO.setUniquename(partnerdocDTO.getUniquename());
            genericDataDTO.setData(partnerDocDetailsService.updateEntity(entityDTO));
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else if (ex instanceof CustomValidationException) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/deletePartnerDoc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO deletePartnerDoc(@RequestBody PartnerDocDeleteModel partnerDocDeleteModel, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String name = partnerService.get(partnerDocDeleteModel.getPartnerId()).getName();
        String SUBMODULE = getModuleNameForLog() + " [deletePartnerDoc()] ";
        try {
            if (null == partnerDocDeleteModel) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Document List Not Found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (partnerDocDeleteModel.getDocIdList() == null || 0 == partnerDocDeleteModel.getDocIdList().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Document List Not Found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (partnerDocDeleteModel.getPartnerId() == null) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseMessage("Please Provide Partner!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Partner List Not Found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (SubscriberConstants.DELETED_SUCCESSFULLY.equalsIgnoreCase(partnerDocDetailsService.deleteDocument
                    (partnerDocDeleteModel.getDocIdList(), partnerDocDeleteModel.getPartnerId()))) {
                genericDataDTO.setResponseMessage(SubscriberConstants.DELETED_SUCCESSFULLY);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("Problem in deletion!");
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                return genericDataDTO;
            }
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete PartnerDoc for partnerId : " + partnerDocDeleteModel.getPartnerId() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = UrlConstants.UPLOAD_DOC_PARTNER, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadDocForPartner(@RequestParam String docDetailsList, @RequestParam(value = "file", required = false) MultipartFile[] file, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PartnerPojo partnerPojo = new PartnerPojo();
        try {
            if (null != docDetailsList) {

                List<PartnerdocDTO> partnerdocDTOList = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(docDetailsList, new TypeReference<List<PartnerdocDTO>>() {
                        });

                if (null == partnerdocDTOList || 0 == partnerdocDTOList.size()) {
                    genericDataDTO.setResponseMessage("Please provide document details!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                    logger.error("Unable to Upload DocFor Partner "+docDetailsList+"   Response : {module: {}}; Response : {{},message:{};",getModuleNameForLog(), APIConstants.FAIL,genericDataDTO.getResponseMessage());
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Unable to Upload DocFor Partner" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }

                if (partnerdocDTOList.get(0).getFilename() == null || partnerdocDTOList.get(0).getFilename().equalsIgnoreCase("")) {
                    genericDataDTO.setResponseMessage("Please Upload File!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                    logger.error("Unable to Upload DocFor Partner "+docDetailsList+"   Response : {module: {}}; Response : {{},message:{};",getModuleNameForLog(), APIConstants.FAIL,genericDataDTO.getResponseMessage());
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Unable to Upload DocFor Partner" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }

                if (null != partnerdocDTOList && 0 < partnerdocDTOList.size()) {
                    genericDataDTO.setDataList(partnerDocDetailsService.uploadDocument(partnerdocDTOList, file));
                    genericDataDTO.setResponseMessage("Documents uploaded successfully.");
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    RESP_CODE = APIConstants.SUCCESS;
//                    logger.info("Partner with  "+docDetailsList+" is Uploded Successfully :  request: { From : {}, }; Response : {{}};message:{}",getModuleNameForLog(),APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }
            genericDataDTO.setResponseMessage("Please provide document details!");
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            if (e instanceof DataNotFoundException) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (e instanceof RuntimeException) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "upload Doccuments for Partner" + LogConstants.LOG_BY_NAME + partnerPojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            MDC.remove("type");
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @GetMapping(value = UrlConstants.DOC_BY_PARTNER + "/{partnerId}")
    public GenericDataDTO getDocByPartner(@PathVariable Integer partnerId, HttpServletRequest req) {
        String name = partnerService.get(partnerId).getName();
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getDocByPartner()] ";
        try {
            if (null == partnerId) {
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Partner");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Doccuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Partner id is null" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Doccuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(partnerDocDetailsService.findDocsByPartnerId(partnerId));
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Doccuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PARTNER_DOC_ALL + "\",\"" + AclConstants.OPERATION_PARTNER_DOC_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody PartnerdocDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String name = partnerService.get(entityDTO.getPartnerId()).getName();
        boolean flag = partnerDocDetailsService.deleteVerification(entityDTO.getPartnerId());
        try {
            if (flag) {
                partnerDocDetailsService.deleteEntity(entityDTO);
                PartnerdocDTO partnerdocDTO = (PartnerdocDTO) dataDTO.getData();
                if (partnerdocDTO != null) {
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                            AclConstants.OPERATION_CUSTUMER_DOC_DELETE, req.getRemoteAddr(), null, partnerdocDTO.getDocId(), partnerdocDTO.getDocType());
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                } else {
                    dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                    dataDTO.setResponseMessage(DeleteContant.CUSTUMER_DOC_EXITS);
                    RESP_CODE = APIConstants.NOT_FOUND;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }

            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner-Doc-Details" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }


    @GetMapping(value = "/partnerPaymentHistory/{partnerId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer partnerId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner Payment History" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Unable to find payment History For Customer" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Partner partners = partnerService.get(partnerId);
            if (partners == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner Payment History" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PartnerCreditDocument> paymentHistories = partnerService.getByLcoId(partnerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner Payment History" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner Payment History" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllPartnerCreditList")
    public GenericDataDTO getAllPartnerCredit(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getAllPartnerBalance()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            PartnerPaymentService partnerPaymentService = SpringContext.getBean(PartnerPaymentService.class);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All partner Credit list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return partnerPaymentService.getAllPartnerCredit(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All partner Credit list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping(value = "/partnerInvoiceHistory/{partnerId}")
    public GenericDataDTO getPartnerInvoiceHistory(@PathVariable Integer partnerId, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerInvoiceHistory" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Unable to Fetch PartnerInvoiceHistory" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Partner partners = partnerService.get(partnerId);
            if (partners == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerInvoiceHistory" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PartnerDebitDocument> paymentHistories = partnerService.getByPartnerId(partnerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerInvoiceHistory" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerInvoiceHistory" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PostMapping("/getAllPartnerInvoiceList")
    public GenericDataDTO getAllPartnerInvoice(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getAllPartnerBalance()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PARTNER, AclConstants.OPERATION_PARTNER_DELETE, req.getRemoteAddr(), null, partner.getId().longValue(), partner.getName());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All partnerInvoice" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            PartnerPaymentService partnerPaymentService = SpringContext.getBean(PartnerPaymentService.class);
            return partnerPaymentService.getAllPartnerInvoice(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All PartnerInvoice" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @RequestMapping(value = "/document/download/{docId}/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer partnerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Partner partner = partnerService.get(partnerId);
            if (null == partner) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "download Ducuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to Download document  for customer" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            PartnerdocDTO docDetailsDTO = partnerDocDetailsService.getEntityById(docId);
            if (null == docDetailsDTO) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "download Ducuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to Download document  for customer" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = SpringContext.getBean(FileSystemService.class);
            resource = service.getPartnerDoc(partner.getName().trim(), docDetailsDTO.getUniquename());
            String contentType = "application/octet-stream";
            if (resource != null || resource.exists()) {
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "download Ducuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "download Ducuments " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to Download document  for customer" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "download Ducuments" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        MDC.remove("type");
        return null;
    }
}
