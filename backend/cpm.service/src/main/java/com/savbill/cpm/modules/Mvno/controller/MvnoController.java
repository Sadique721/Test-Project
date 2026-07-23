package com.savbill.cpm.modules.Mvno.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.modules.Mvno.domain.Mvno;
import com.savbill.cpm.modules.Mvno.mapper.MvnoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.Mvno.model.MvnoDTO;
import com.savbill.cpm.modules.Mvno.service.MvnoService;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.MvnoMessage;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO)
public class MvnoController extends ExBaseAbstractController<MvnoDTO> {
    private static String MODULE = " [MvnoController] ";
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private MessageSender messageSender;
@Autowired
private KafkaMessageSender kafkaMessageSender;
    @Autowired
    MvnoMapper mvnoMapper;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private Tracer tracer;
    
    public MvnoController(MvnoService service) {
        super(service);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        return super.getAll(requestDTO, req);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_VIEW, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @Deprecated
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.save(entityDTO, result, authentication, req);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.sendEntitySaveDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_ADD, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.update(entityDTO, result, authentication, req);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
      //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.updateEntityDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_EDIT, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody MvnoDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
      //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),true, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.deleteEntityDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_DELETE, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoController]";
    }


    //This api is only for testing the mvno deactivation functionality and not used any where.
    @GetMapping("/updateMvnoStatus")
    ResponseEntity<?> updateMvnoStatus (@RequestParam(required = true) Integer mvnoId){

        Set<Long> mvnoids=  new HashSet<>();
        mvnoids.add(mvnoId.longValue());
        mvnoService.changeMvnoStatus(mvnoids,"InActive");
        return null;

    }

}
