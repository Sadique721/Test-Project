package com.savbill.ticketmanagement.core.modules.Notification.controller;



import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.NOTIFICATION)
public class NotificationController{ // extends ExBaseAbstractController<NotificationDTO> {
//    @Autowired
//    private NotificationService service;
//
//    private static String MODULE = " [NotificationController] ";
//    public NotificationController(NotificationService service) {
//        super(service);
//    }
//    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
//    @GetMapping("/getGenricNotification/{notification}")
//    public GenericDataDTO getNotificationByCategory(@PathVariable String notification) {
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        logger.info("fetching notification By Category is successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//        try {
//            if (notification.equalsIgnoreCase("email")) {
//                genericDataDTO.setData(service.findNotificationByCategory("generic", "active", true, false));
//            } else if (notification.equalsIgnoreCase("sms")) {
//                genericDataDTO.setData(service.findNotificationByCategory("generic", "active", false, true));
//            } else {
//                logger.error("Unable to fetch getNotification By Category  :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//                throw new Exception("Given type is must be sms or email");
//            }
//            genericDataDTO.setTotalRecords(1);
//        } catch (Exception e) {
//           // ApplicationLogger.logger.error(e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            if (e instanceof DataNotFoundException) {
//                genericDataDTO.setResponseMessage("Data Not Found");
//            } else {
//                genericDataDTO.setResponseMessage(e.getMessage());
//            }
//            logger.error("Unable to fetch getNotification By Category  :  request: { From : {}}; Response : {{};Exception :{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setDataList(null);
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @Override
//    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO delete(@RequestBody NotificationDTO entityDTO, Authentication authentication, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            NotificationDTO dtoData = service.getEntityById(entityDTO.getIdentityKey());
//            ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
//            if (dtoData.getCategory().equalsIgnoreCase("GENERIC")) {
//                genericDataDTO.setResponseMessage("Generic category can not be deleted");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                return genericDataDTO;
//            }
//            service.deleteEntity(entityDTO);
//            genericDataDTO.setData(entityDTO);
//            // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NOTIFICATION,
//            //      AclConstants.OPERATION_NOTIFICATION_DELETE, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
//
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            logger.info("Notification With name "+entityDTO.getName()+" is successfully deleted :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
//          //      ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//                logger.error("Unable To delete notification with name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            } else {
//         //       ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
//                logger.error("Unable To delete notification with name "+entityDTO.getName()+"   :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            }
//            logger.error("Unable To delete notification with name "+entityDTO.getName()+"   :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
////    @RequestMapping(value = "/croncheck", method = RequestMethod.GET)
////    public void cron() throws Exception {
////        everydayScheduler.cronJobSch();
////    }
//
//    @Override
//    @PostMapping("/list")
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "Notification Controller";
//    }
}
