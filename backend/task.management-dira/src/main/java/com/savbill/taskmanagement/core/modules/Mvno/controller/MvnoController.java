package com.savbill.taskmanagement.core.modules.Mvno.controller;


//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO)
public class MvnoController {

//    private static String MODULE = " [MvnoController] ";
//    @Autowired
//    AuditLogService auditLogService;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    public MvnoController(MvnoService service) {
//        super(service);
//    }
//
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_VIEW, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
//        return dataDTO;
//
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @Deprecated
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.save(entityDTO, result, authentication, req);
//        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
//        //send message
//        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
//        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false);
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_ADD, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
//        return dataDTO;
//    }
//
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.update(entityDTO, result, authentication, req);
//        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
//      //send message
//        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
//        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false);
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_EDIT, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
//        return dataDTO;
//    }
//
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody MvnoDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
//        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
//      //send message
//        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
//        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),true);
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
//                AclConstants.OPERATION_MVNO_DELETE, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
//        return dataDTO;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[MvnoController]";
//    }

}
