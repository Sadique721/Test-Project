package com.savbill.commonGateway.moules.MasterManagement.Branch.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.BranchServiceAreaMapping;
import com.savbill.commonGateway.moules.MasterManagement.Branch.mapper.BranchMapper;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.BranchDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.CustomBranchDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchServiceAreaMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.Branch.service.BranchService;
import com.savbill.commonGateway.moules.MasterManagement.BranchService.model.BranchServiceMappingEntity;
import com.savbill.commonGateway.moules.MasterManagement.BranchService.repository.BranchServiceMappingRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.BranchMessage;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BRANCH_MANAGEMENT)
public class BranchController extends ExBaseAbstractController<BranchDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BranchController.class);
//    @Autowired
//    AuditLogService auditLogService;
    private static String MODULE = " [BranchController] ";
    @Autowired
    BranchService branchService;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private BranchServiceMappingRepository branchServiceMappingRepository;

    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    public BranchController(BranchService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BranchController]";
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req ,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            Page<Branch> branchPage = branchService.getBranches(
                    requestDTO.getPage(),
                    requestDTO.getPageSize()
            );

            if (branchPage.hasContent()) {
                genericDataDTO.setDataList(branchPage.getContent());
                genericDataDTO.setTotalRecords((int) branchPage.getTotalElements());
                genericDataDTO.setPageRecords(branchPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(branchPage.getNumber() + 1);
                genericDataDTO.setTotalPages(branchPage.getTotalPages());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
            } else {
                genericDataDTO.setDataList(new ArrayList());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody BranchDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Create");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [save()] ";
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
                if(entityDTO.getSharing_percentage()!=null) {
                    double  number = entityDTO.getSharing_percentage();

                    if (number % 1 != 0) {
                        dataDTO.setResponseMessage("Fraction Value not allowed");
                        dataDTO = new GenericDataDTO();
                        dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                        dataDTO.setResponseMessage(MessageConstants.Fraction_Value);
                        }

                    if (entityDTO.getRevenue_sharing() && (entityDTO.getSharing_percentage() < 0 || entityDTO.getSharing_percentage() > 100)) {
                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        dataDTO.setResponseMessage("Revenue sharing percentage must be less than 100 or greater than 0");
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Revenue sharing percentage must be less than 100 or greater than 0", null);
                    }
                }
                    if (getMvnoIdFromCurrentStaff() != null) {
                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                    }
//                    if(getBUIdsFromCurrentStaff()!=null){
//                        entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0).intValue());
//                    }
                    boolean flag = branchService.duplicateVerifyAtSave(entityDTO.getName());
                    boolean isCodeExist = true;
                    String isBranchCodeEmpty = entityDTO.getBranch_code();
                    if(Objects.nonNull(isBranchCodeEmpty) && !isBranchCodeEmpty.isEmpty()){
                        isCodeExist  = branchService.verifyDuplicateCodeAtSave(entityDTO.getBranch_code());
                    }

                    if (flag && isCodeExist) {
                        dataDTO = super.save(entityDTO, result, authentication, req,res);
                        BranchDTO branchDTO = (BranchDTO) dataDTO.getData();

//                        //send message
                        BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted(), branchDTO.getMvnoId());
                        //this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH);
                        //BranchMessageIn branchMessageIn = new BranchMessageIn(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(),branchDTO.getBranch_code(), branchDTO.getIsDeleted(), branchDTO.getMvnoId());
                        //this.messageSender.send(branchMessageIn, RabbitMqConstants.QUEUE_BRANCH_SUCCESS);
                        kafkaMessageSender.send(new KafkaMessageData(branchMessage,branchMessage.getClass().getSimpleName()));
                        //Common micoroservice data share call
                        Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
//                        branch.setCreatedById(entityDTO.getCreatedById());
//                        branch.setLastModifiedById(entityDTO.getLastModifiedById());
                        createDataSharedService.sendEntitySaveDataForAllMicroService(branch);
                        respCode = APIConstants.SUCCESS;
                        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_BY + "create branch"+LogConstants.LOG_BY_NAME+entityDTO.getName()+LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
                        //                        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                                AclConstants.OPERATION_BRANCH_ADD, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
                    } else if (!flag) {
                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
                        respCode = HttpStatus.NOT_ACCEPTABLE.value();
                        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getBranch_code() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                    }else {
                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        dataDTO.setResponseMessage(MessageConstants.BRANCH_CODE_EXITS);
                        respCode = HttpStatus.EXPECTATION_FAILED.value();
                        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_BY + "create branch"+LogConstants.LOG_BY_NAME+entityDTO.getName() +LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                    }

                    return dataDTO;
                } catch(Exception ex){
                    dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                    dataDTO.setResponseMessage(ex.getMessage());
                    respCode = HttpStatus.EXPECTATION_FAILED.value();
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_BY + "create branch"+LogConstants.LOG_BY_NAME+entityDTO.getName() +LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR +ex.getMessage()+ respCode);

            }finally {

                MDC.remove("type");
                MDC.remove("userName");
                MDC.remove("traceId");
                MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
            }

         return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody BranchDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Update");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
//        if(getBUIdsFromCurrentStaff()!=null){
//            entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0).intValue());
//        }
        GenericDataDTO dataDTO = new GenericDataDTO();
        Branch old1= branchService.getById(entityDTO.getId());
        Branch old1Clone = new Branch(old1);
        boolean flag = branchService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
        boolean isCodeExist =true;
        String isBranchCodeEmpty = entityDTO.getBranch_code();
        if(Objects.nonNull(isBranchCodeEmpty) && !isBranchCodeEmpty.isEmpty()){
            isCodeExist = branchService.verifuDuplicateCodeAtEdit(entityDTO.getBranch_code(), entityDTO.getId());
        }
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (entityDTO.getRevenue_sharing()) {
                List<BranchServiceMappingEntity> branchServiceMappingEntityList = entityDTO.getBranchServiceMappingEntityList();
                branchServiceMappingEntityList.stream().forEach(branchServiceMapping -> {

                    if ((branchServiceMapping.getRevenueShareper() < 0) || (branchServiceMapping.getRevenueShareper()) > 100) {
                        throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Revenue sharing percentage must be less than or equal to 100", null);
//                dataDTO.setResponseMessage("Revenue sharing percentage must be less than or equal to 100");
                    }
                });
//            else {
                if (flag && isCodeExist) {
//                    BranchDTO branchDTO = entityDTO;
                    dataDTO = super.update(entityDTO, result, authentication, req,res);
                    BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
                    if (dataDTO != null) {
                        //send message
                        //BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted(), branchDTO.getMvnoId());
                        //this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH);
                        //Common micoroservice data share call
                        Branch branch = branchMapper.dtoToDomain(branchDTO, new CycleAvoidingMappingContext());
                        branch.setCreatedById(entityDTO.getCreatedById());
                        branch.setLastModifiedById(entityDTO.getLastModifiedById());
                        createDataSharedService.updateEntityDataForAllMicroService(branch);
                        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update branch"+ LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + " , Updated Branch Management Details " +UpdateDiffFinder.getUpdatedDiff(old1Clone,branch) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

//                        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                                AclConstants.OPERATION_BRANCH_EDIT, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
                    }
                    } else if (!isCodeExist) {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.BRANCH_CODE_EXITS);
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getBranch_code() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }else {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update branch"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE +respCode);
                }

            }
        else {
                if (flag && isCodeExist) {
                    BranchDTO branchDTO = entityDTO;
                    dataDTO = super.update(entityDTO, result, authentication, req,res);
                    if (dataDTO != null) {
                        //send message
                        //BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted(), branchDTO.getMvnoId());
                        //this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH);
                        Branch branch = branchMapper.dtoToDomain(branchDTO, new CycleAvoidingMappingContext());
                        branch.setCreatedById(entityDTO.getCreatedById());
                        branch.setLastModifiedById(entityDTO.getLastModifiedById());
                        createDataSharedService.updateEntityDataForAllMicroService(branch);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                            AclConstants.OPERATION_BRANCH_EDIT, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
                        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update branch"+ LogConstants.LOG_BY_NAME +entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + " Updated branch Details " + UpdateDiffFinder.getUpdatedDiff(old1Clone,branch) + LogConstants.LOG_STATUS + " "+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                    } else {
                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
                        respCode = HttpStatus.NOT_ACCEPTABLE.value();
                        LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update branch"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE +respCode);
                    }
                } else if (!isCodeExist) {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.BRANCH_CODE_EXITS);
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getBranch_code() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }else {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update branch"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE +respCode);

                }
            }
        }catch (Exception ex){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestForm") + LogConstants.REQUEST_FOR + " update branch"+ LogConstants.LOG_BY_NAME+ entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
            return dataDTO;
    }




//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody BranchDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = branchService.deleteVerification(entityDTO.getId().intValue());
        boolean flag2 = branchService.deleteVerificationForRegion(entityDTO.getId().intValue());
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Delete");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try{

        if(flag && flag2) {
            dataDTO = super.delete(entityDTO, authentication, req,res);
            List<BranchServiceAreaMapping> branchServiceAreaMappingList =branchServiceAreaMappingRepository.findAllByBranchId(entityDTO.getId().intValue());
            branchServiceAreaMappingRepository.deleteAll(branchServiceAreaMappingList);
            List<BranchServiceMappingEntity> branchServiceMappingEntityList = branchServiceMappingRepository.findAllByBranchId(entityDTO.getId());
         //   branchServiceMappingRepository.deleteAll(branchServiceMappingEntityList);
            BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
            if(branchDTO != null) {
            	 //send message
                //BranchMessage branchMessage = new BranchMessage(branchDTO.getId(),branchDTO.getName(),branchDTO.getStatus(),true, branchDTO.getMvnoId());
                //this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH);

                //Common micoroservice data share call
                Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
                branch.setIsDeleted(true);
                branch.setCreatedById(entityDTO.getCreatedById());
                branch.setLastModifiedById(entityDTO.getLastModifiedById());
                createDataSharedService.deleteEntityDataForAllMicroService(branch);
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete branch"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);

//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                        AclConstants.OPERATION_BRANCH_DELETE, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
            }
            if (!branchServiceMappingEntityList.isEmpty()) {
                branchServiceMappingEntityList.stream().forEach(branchServiceMapping -> branchServiceMapping.setIsDeleted(true));
                branchServiceMappingRepository.saveAll(branchServiceMappingEntityList);
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            dataDTO.setResponseMessage(DeleteContant.BRANCH_DELETE_EXIST);
            respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete branch"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);

        }}catch (Exception ex){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete branch" +LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Search");
        MDC.put("userName",branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Branch using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);

            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Branch using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Branch using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        } return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BRANCH + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
    long startTime = System.nanoTime();  // Start measuring
    try {
        GenericDataDTO dataDTO = super.getEntityById(id, req,res);
        BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
        return dataDTO;
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
    }
}

    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAllWithoutPagination(req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    //Get BranchIds by ServiceAreas
    @GetMapping("/getBranchByServiceArea")
    public GenericDataDTO getBranchByServiceArea(HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getBranchByServiceArea());
            genericDataDTO.setTotalRecords(branchService.getBranchByServiceArea().size());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        }
        catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @GetMapping("/getAllServiceAreaByBranchId/{branchId}")
    public GenericDataDTO getAllServiceAreaByBranchId(@PathVariable Integer branchId, HttpServletRequest req){
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getAllServiceAreaByBranchId(branchId));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all services area by branch"+LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all services area by branch"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }




    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @PostMapping("/getAllBranchesByServiceAreaId")
    public GenericDataDTO getAllBranachesByServiceAreaID(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
        long startTime = System.currentTimeMillis();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getAllBranchesByServiceAreaId(serviceAreaId));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        return genericDataDTO;
    }

    @PostMapping("/getAllBranchesByServiceAreaId/withSpecificParam")
    public GenericDataDTO getAllBranachesByServiceAreaIDWithSpecificParam(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getAllBranchesByServieAreaIdWithSpecificParam(serviceAreaId));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @GetMapping("/getAllBranchesByServiceArea/{serviceAreaId}")
    public GenericDataDTO getAllBranachesByServiceArea(@PathVariable Integer serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getAllBranchesByServieAreaId(Arrays.asList(serviceAreaId)));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch all branch by service area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllBranchesByforPartnerServiceAreaId")
    public GenericDataDTO getAllBranachesforPartnerByServiceAreaID(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type","Fetch");
        MDC.put("userName", branchService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        BranchDTO branchDTO = new BranchDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BranchService branchService = SpringContext.getBean(BranchService.class);
            genericDataDTO.setDataList(branchService.getAllBranachesforPartnerByServiceAreaID(serviceAreaId));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Branches By for Partner Service Area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Branches By for Partner Service Area"+ LogConstants.REQUEST_BY + branchService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(path = "/findAll")
    public GenericDataDTO getAllBranches(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
//            List<BranchDTO> list = branchService.getAllBranches().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 ).collect(Collectors.toList());
            List<CustomBranchDTO> list = branchService.getAllBranchesWithMVNOID();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            ApplicationLogger.logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            ApplicationLogger.logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }

}
