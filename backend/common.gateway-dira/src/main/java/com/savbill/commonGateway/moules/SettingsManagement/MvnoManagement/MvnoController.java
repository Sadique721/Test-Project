package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.MessageConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.constants.Constants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.OTP.service.OTPManagmentService;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.*;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.*;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import com.savbill.commonGateway.moules.acl.repository.RoleAclRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.RabbitMqConstants;
import com.savbill.commonGateway.rabbitmq.messages.MvnoMessage;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO)
public class MvnoController extends ExBaseAbstractController<MvnoDTO> {
    private static String MODULE = " [MvnoController] ";

    @Autowired
    private MessageSender messageSender;

    @Autowired
    MvnoMapper mvnoMapper;
    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    RoleAclRepository roleAclRepository;

    @Autowired
    RoleRepository repository;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleACLEntryMapper roleACLEntryMapper;
    @Autowired
    MvnoService mvnoService;
    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomersService customers;


    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    StaffUserService staffUserService;


    @Autowired
    private OTPManagmentService otpManagmentService;


    private static final Logger LOGGER = LoggerFactory.getLogger(MvnoController.class);

    public MvnoController(MvnoService service) {
        super(service);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res) {
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

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            GenericDataDTO dataDTO = super.getEntityById(id, req,res);
            MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
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

    @Deprecated
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName",mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search MVNO using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search MVNO using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search MVNO using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "create ");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = mvnoService.duplicateVerifyAtSave(entityDTO.getName());
        boolean userflag = mvnoService.duplicateVerifyusernameAtSave(entityDTO.getUsername());
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (flag && userflag) {
                if (Objects.isNull(entityDTO.getMvnoPaymentDueDays()) || entityDTO.getMvnoPaymentDueDays() == null) {
                    entityDTO.setMvnoPaymentDueDays(10);
                }
                String mobileNumberLength = mvnoService.getMobileNumber(mvnoService.getLoggedInUser().getMvnoId(), RabbitMqConstants.MOBILE_NUMBERS);
                int min = 0;
                int max = 0;
                if (mobileNumberLength != null) {
                    Pattern pattern = Pattern.compile("\\\\d\\{(\\d+)(?:,(\\d+))?\\}");
                    Matcher matcher = pattern.matcher(mobileNumberLength);
                    if (matcher.find()) {
                        min = Integer.parseInt(matcher.group(1));
                        if (matcher.group(2) != null) {
                            max = Integer.parseInt(matcher.group(2));
                        } else {
                            max = min;
                        }
                    }
                }
                String phone = entityDTO.getPhone();
                if (phone == null || phone.length() < min || phone.length() > max) {
                    String message;
                    if (min == max) {
                        message = "Mobile number must be exactly " + min + " digits.";
                    } else {
                        message = "Mobile number must be between " + min + " and " + max + " digits.";
                    }
                    throw new CustomValidationException(APIConstants.FAIL, message, null);
                }
                    if(entityDTO.getPassword()!=null){
                        // Delegate password validation and encoding to the service
                        mvnoService.validateAndEncodePassword(entityDTO);
                    }
                    dataDTO = super.save(entityDTO, result, authentication, req,res);
                    MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
                    //send message
                    //MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(), mvnoDTO.getName(), mvnoDTO.getUsername(), mvnoDTO.getPassword(), mvnoDTO.getSuffix(), mvnoDTO.getDescription(),
                            //mvnoDTO.getEmail(), mvnoDTO.getPhone(), mvnoDTO.getStatus(), mvnoDTO.getLogfile(), mvnoDTO.getMvnoHeader(), mvnoDTO.getMvnoFooter(),mvnoDTO.getAddress(),mvnoDTO.getFullName(), false, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
                    //this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO); pending via kafka
                    //kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,mvnoMessage.getClass().getSimpleName()));
                    Mvno mvno = mvnoMapper.dtoToDomain(mvnoDTO, new CycleAvoidingMappingContext());
                    createDataSharedService.sendEntitySaveDataForAllMicroService(mvno);
                    staffUserService.saveWithMvno(mvnoService.mvnoToStaff(entityDTO),req);
                    clientServiceSrv.createClientService(mvno);
                    if(mvno.getIsTwoFactorEnabled()) {
                        otpManagmentService.createDefaultOTPProfile(mvno.getId().intValue());
                    }
                    createRole(mvno);
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Mvno" + LogConstants.LOG_BY_NAME + mvnoDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else if (!userflag) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MVNO_USERNAME_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Mvno" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MVNO_NAME_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Mvno" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            dataDTO.setResponseCode(ex.getErrCode());
            dataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Mvno" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            dataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Mvno" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } catch (AlreadyExistException e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.CONFLICT.value());
            dataDTO.setResponseMessage(e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("username");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_ADD, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    private void createRole(Mvno mvno) {
        String mvnoName = mvno.getName();
        List<String> rolenameList = Arrays.asList("View_Role", "NOC_Role", "Support_Role", "Franchise_Role", "LCO_Role");
        List<Long> idlist = Arrays.asList(2L, 3L, 4L, 5L, 6L);
        String createdByName = roleService.getLoggedInUser().getFullName();
        Integer createdById = getStaffId();
        List<RoleDTO> roleDTOs = IntStream.range(0, rolenameList.size())
                .parallel()
                .mapToObj(i -> {
                    Role role = new Role();
                    role.setRolename(rolenameList.get(i) + " " + mvnoName);
                    role.setSysRole(true);
                    role.setMvnoId(mvno.getId().intValue());
                    role.setStatus("Active");
                    role.setIsDelete(false);
                    role.setCreatedById(createdById);
                    role.setProduct(Constants.BSS);
                    role.setCreatedByName(createdByName);
                    role.setLastModifiedByName(createdByName);
                    role.setLastModifiedById(createdById);
                    Role savedRole = repository.save(role);
                    List<RoleACLEntry> existingRoleACLEntry = roleAclRepository.findAllByRole(idlist.get(i));
                    List<RoleACLEntryDTO> newRoleACLDto = existingRoleACLEntry.stream()
                            .map(existingEntry -> {
                                RoleACLEntry newEntry = new RoleACLEntry();
                                newEntry.setRole(savedRole);
                                newEntry.setRoleId(savedRole.getId());
                                newEntry.setCode(existingEntry.getCode());
                                newEntry.setMenuid(existingEntry.getMenuid());
                                newEntry.setProduct(Constants.BSS);
                                return roleACLEntryMapper.domainToDTO(newEntry, new CycleAvoidingMappingContext());
                            })
                            .collect(Collectors.toList());
                    RoleDTO roleDTO = roleMapper.domainToDTO(savedRole, new CycleAvoidingMappingContext());
                    roleDTO.setAclMenu(newRoleACLDto);
                    return roleDTO;
                })
                .collect(Collectors.toList());
        for (RoleDTO role: roleDTOs) {
            role.setSysRole(true);
            role.setMvnoId(mvno.getId().intValue());
            role.setStatus("Active");
            role.setIsDelete(false);
            role.setCreatedById(createdById);
            role.setLastModifiedById(createdById);
            role.setCreatedate(LocalDateTime.now());
            role.setUpdatedate(LocalDateTime.now());
            role.setProduct(Constants.BSS);
            roleService.sendToAllMicroservices(role);

        }
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "update ");
        MDC.put("userName" ,mvnoService.getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString() );
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = mvnoService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
        Mvno old = mvnoService.getMvnoById(entityDTO.getId());
        Mvno oldClone = new Mvno(old);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        long startTime = System.nanoTime();  // Start measuring
        try{
          if (flag) {
              Mvno mvnodata=mvnoService.getMvnoById(entityDTO.getId());
              entityDTO.setCustInvoiceRefId(mvnodata.getCustInvoiceRefId());
              dataDTO = super.update(entityDTO, result, authentication, req,res);
               mvnoDTO = (MvnoDTO) dataDTO.getData();
              //send message
              MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(), mvnoDTO.getName(), mvnoDTO.getUsername(), mvnoDTO.getPassword(), mvnoDTO.getSuffix(), mvnoDTO.getDescription(),
                      mvnoDTO.getEmail(), mvnoDTO.getPhone(), mvnoDTO.getStatus(), mvnoDTO.getLogfile(), mvnoDTO.getMvnoHeader(), mvnoDTO.getMvnoFooter() ,mvnoDTO.getAddress(), mvnoDTO.getFullName(),false,mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
              //this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO); pending via kafka
              kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,mvnoMessage.getClass().getSimpleName()));
              Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
              if(entityDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
                mvnoService.reActivateAllStaffAndCustomers(Math.toIntExact(old.getId()));

              if(mvno.getIsTwoFactorEnabled())
                  otpManagmentService.createDefaultOTPProfile(mvno.getId().intValue());

              createDataSharedService.updateEntityDataForAllMicroService(mvno);
              LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName() + LogConstants.REQUEST_BY +mvnoService. getLoggedInUser().getFirstName() + " , Updated Mvno Details" + UpdateDiffFinder.getUpdatedDiff(oldClone,mvno)+ LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );

          } else {
              dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
              dataDTO.setResponseMessage(MessageConstants.MVNO_NAME_EXITS);
              LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" )+ LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName() +LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

          }
      }catch (Exception ex ){
          LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" )+ LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+ "Mvno" +LogConstants.LOG_BY_NAME+ entityDTO.getName() +LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());


      }finally {
        MDC.remove("type");
        MDC.remove("username");
        MDC.remove("traceId");
        MDC.remove("spanId");
          long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
          res.addHeader("Server-Timing", "app;dur=" + durationInMs);
    }
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_EDIT, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody MvnoDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName" ,mvnoService.getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req,res);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        //send message
        long startTime = System.nanoTime();  // Start measuring
        try {
            MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(), mvnoDTO.getName(), mvnoDTO.getUsername(), mvnoDTO.getPassword(), mvnoDTO.getSuffix(), mvnoDTO.getDescription(),
                    mvnoDTO.getEmail(), mvnoDTO.getPhone(), mvnoDTO.getStatus(), mvnoDTO.getLogfile(), mvnoDTO.getMvnoHeader(), mvnoDTO.getMvnoFooter() ,mvnoDTO.getAddress(), mvnoDTO.getFullName(),true, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
            //this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO); pending via kafka
            kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,mvnoMessage.getClass().getSimpleName()));
            Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            createDataSharedService.deleteEntityDataForAllMicroService(mvno);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " )+ LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName()+LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex ){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " )+ LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName()+LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        }finally {
            MDC.remove("type");
            MDC.remove("username");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_DELETE, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoController]";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMvno(@PathVariable Long id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName" ,mvnoService.getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString() );
        HashMap<String, Object> response = new HashMap<>();
        MvnoDTO mvnoDTO = new MvnoDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            MvnoService mvnoService = SpringContext.getBean(MvnoService.class);
            MvnoDTO dto = mvnoService.getEntityById(id);
            if (dto != null) {
                mvnoService.getRepository().deleteById(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, "Mvno is deleted.");
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_COUNTRY, AclConstants.OPERATION_COUNTRY_DELETE,
//                        req.getRemoteAddr(), null, dto.getId().longValue(), dto.getName());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Mvno" +LogConstants.LOG_BY_NAME+ mvnoDTO.getName()+ LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                throw new CustomValidationException(APIConstants.FAIL, "Data Not Found", null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName() + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
            } else {
                ex.printStackTrace();
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom " + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Mvno"+LogConstants.LOG_BY_NAME+ mvnoDTO.getName()+ LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()) + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+ LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
            }
        }finally {
            MDC.remove("type");
            MDC.remove("username");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping(value = "/getMvnosBylatlog")
    public ResponseEntity<?> getMvnoByLatitudeAndLongitude(@RequestParam Double lat, @RequestParam Double longt, @RequestParam(required = false) Integer mvnoId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> resp = new HashMap<>();
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        Long startTime = System.currentTimeMillis() % 1000;
        System.out.println("********** Start Of fetch Mvno by lat and long time in millisecond: "+startTime);
        try {
            HashMap<String, Object> response = new HashMap<>();
            List<MvnoDTO> list = mvnoService.getListOfMvnoByLatAndLon(lat, longt,mvnoId);
            RESP_CODE = 200;
            if(!CollectionUtils.isEmpty(list)) {
                response.put("dataList",list);
                response.put("code",200);
            } else {
                response.put("dataList",new ArrayList<>());
                response.put("code",404);
            }
            MDC.remove("type");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch lattitude and longitude of location" + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            Long endTime = System.currentTimeMillis() % 1000;
            System.out.println("********** End Of fetch Mvno by lat and long time in millisecond: "+endTime+" difference start and end: "+(startTime-endTime));
            return apiResponse(RESP_CODE, response);
        } catch (Exception e) {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch lattitude and longitude of location" + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/mvnoIspToIsp")
    public GenericDataDTO mvnoIspToIsp(@RequestParam("oldMvnoid") Integer oldMvnoid, @RequestParam("newMvnoid") Integer newMvnoid, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            GenericDataDTO genericDataDTO = customers.callWorkFlowInProgressAPI(req.getHeader("Authorization"), oldMvnoid);
            dataDTO.setDataList(genericDataDTO.getDataList());
            if(dataDTO.getDataList()!=null && dataDTO.getDataList().size()!=0) {
                dataDTO.setResponseMessage("Some workflows are currently in progress. Please download the in-progress workflow data!");
                dataDTO.setResponseCode(HttpStatus.OK.value());
            }
            if(dataDTO.getDataList()==null || dataDTO.getDataList().size()==0){
                if ((oldMvnoid != 1) && (newMvnoid != 1)) {
                    mvnoService.updateMvnoIdIsptoIsp(oldMvnoid, newMvnoid);
                    createDataSharedService.updateMvnoISPData(oldMvnoid, newMvnoid);
                    dataDTO.setResponseMessage("MvnoId Updated successfully");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update mvnoId ISP " + oldMvnoid + " to ISP" + newMvnoid + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                } else {
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update mvnoId ISP " + oldMvnoid + " to ISP" + newMvnoid + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                }
            }
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(e.getErrCode());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update mvnoId ISP to ISP " + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.FAIL);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update mvnoId ISP to ISP " + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @GetMapping("/getMvnoNameAndIds")
    public GenericDataDTO getMvnoNameAndIds(HttpServletRequest req, HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());

        long startTime = System.nanoTime();  // Start measuring

        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            dataDTO.setDataList(mvnoService.getMvnoAndIds().getDataList());
            dataDTO.setResponseMessage("MvnoId Fetched Successfully");
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Mvno Name and IDs " + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return dataDTO;
        }catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Mvno Name and IDs" + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
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


    //This api is for testing pupose of fiegnclient please do not update it
    @GetMapping("/getTesstedFeignClient")
    public GenericDataDTO getTesstedFeignClient(HttpServletRequest req, @RequestParam(name = "mvnoid", required = false) Integer mvnoid) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            dataDTO.setData(customers.callWorkFlowInProgressAPI(req.getHeader("Authorization"),mvnoid));
            dataDTO.setResponseMessage("MvnoId Fetched Successfully");
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Mvno Name and IDs " + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return dataDTO;
        }catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Mvno Name and IDs" + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PutMapping("/setDefaultProfile/{id}")
    public GenericDataDTO setDefaultProfile (@PathVariable Long id,HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", mvnoService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) request.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
    try {
        mvnoService.updateMvnoProfile(id);
        dataDTO.setResponseMessage("Default Profile Set to All Mvno List Successfully...");
        dataDTO.setResponseCode(APIConstants.SUCCESS);
    }catch (Exception e){
        dataDTO.setResponseMessage("Error while Set Default Profile to All Mvno List...");
        dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
        LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Mvno Name and IDs" + LogConstants.REQUEST_BY + mvnoService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }
    return dataDTO;
    }

}

