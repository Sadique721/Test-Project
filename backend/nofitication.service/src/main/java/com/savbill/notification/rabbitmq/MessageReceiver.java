//package com.savbill.notification.rabbitmq;
//
//import brave.Tracer;
//import com.savbill.notification.BusinessUnit.service.BusinessUnitService;
//import com.savbill.notification.Mvno.domain.UpdateMvnoData;
//import com.savbill.notification.Mvno.service.MvnoService;
//import com.savbill.notification.rabbitmq.message.*;
//import com.savbill.notification.services.impl.CustomerServiceImpl;
//import com.savbill.notification.services.impl.NotificationAuditServiceImpl;
//import org.apache.log4j.Logger;
//import org.apache.log4j.MDC;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import com.savbill.notification.repository.RoleRepository;
//import com.savbill.notification.repository.RoleScreensRepository;
//import com.savbill.notification.repository.StaffRepository;
//import com.savbill.notification.services.EmailService;
//import com.savbill.notification.services.RoleService;
//import com.savbill.notification.services.SmsService;
//import com.savbill.notification.services.StaffService;
//import com.savbill.notification.services.SystemConfigService;
//
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Component
//public class MessageReceiver {
//
//
//	@Autowired
//	EmailService emailService;
//
//	@Autowired
//	SmsService smsService;
//
//	@Autowired
//	StaffService staffService;
//
//	@Autowired
//	RoleRepository roleRepository;
//
//	@Autowired
//	StaffRepository staffRepository;
//
//	@Autowired
//	RoleService roleService;
//
//	@Autowired
//	RoleScreensRepository roleScreensRepository;
//
//	@Autowired
//	SystemConfigService configService;
//
//	@Autowired
//	private CustomerServiceImpl customerService;
//
//	@Autowired
//	private MvnoService mvnoService;
//
//	@Autowired
//	private BusinessUnitService businessUnitService;
//
//	public static String user = null;
//
//	@Autowired
//	private NotificationAuditServiceImpl notificationAuditService;
//	@Autowired
//	private Tracer tracer;
//	private static Logger log = Logger.getLogger(MessageReceiver.class);
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_LOGIN_SUCCESS)
//	public void receiveLoginSuccessMessage(CustomerMessage message) {
//		try{
//			setUserProperties(message.getTraceId(), message.getSpanId());
//	//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			user = message.getCustomerData().get("userName").toString();
//			Boolean flag = customerService.isCustomerNotificationEnable((Integer) message.getCustomerData().get("id"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LOGIN_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName") , "Customer Login Success" , LocalDateTime.now() ,  message.toString());
//			}
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_LOGIN_FAILURE)
//	public void receiveLoginFailureMessage(CustomerMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
////		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnable((Integer) message.getCustomerData().get("id"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LOGIN_FAILURE, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName") , "Customer Login Failure" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS)
//	public void receiveRegistrationSuccessMessage(CustomerMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
////		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnable((Integer) message.getCustomerData().get("id"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName"), "Customer Registration Success", LocalDateTime.now(), message.toString());
//			}
////	smsService.sendSmsNotification(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS, message.getMessage(),
////			message.getCustomerData(), message.getSourceName(),message.getSmsTemplate(),message.getAppendUrl());
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_REGISTRATION_FAILURE)
//	public void receiveRegistrationFailureMessage(CustomerMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnable((Integer) message.getCustomerData().get("id"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_REGISTRATION_FAILURE, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName"), "Registration Failure", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_OTP_GENERATION)
//	public void receiveOtpGenerationMessage(OtpMessage message) {
//		setUserProperties(message.getTraceId(), message.getSpanId());
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//		// MDC.put(NotificationConstants.USER_NAME, message.getCurrentUser());
//		// MDC.put("traceId", message.getTraceId());
//		// MDC.put("spanId", message.getSpanId());
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_OTP_GENERATION, message.getMessage(),
//					message.getOtpData(), message.getSourceName(), message.getEmailTemplate(), message.getSmsTemplate(),
//					message.getAppendUrl(), message.isEmailConfigured(), message.isSmsConfigured());
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON)
//	public void receiveOtpGenerationMessageFromCommon(OtpMessage message) {
//		setUserProperties(message.getTraceId(), message.getSpanId());
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON, message.getMessage(),
//					message.getOtpData(), message.getSourceName(), message.getEmailTemplate(), message.getSmsTemplate(),
//					message.getAppendUrl(), message.isEmailConfigured(), message.isSmsConfigured());
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_VOUCHERCODE)
//	public void receiveVoucherCodeMessage(VoucherCodeMessage message) {
//		setUserProperties(message.getTraceId(), message.getSpanId());
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//		try {
//			smsService.sendSmsNotification(RabbitMqConstants.QUEUE_SEND_VOUCHERCODE, message.getMessage(),
//					message.getVoucherData(), message.getSourceName(), message.getSmsTemplate(),
//					message.getAppendUrl());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_SUCCESS)
//	public void receiveStaffList(StaffMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			staffService.saveRoleAndStaff(RabbitMqConstants.QUEUE_STAFF_SUCCESS, message.getStaff(),
//					message.getRoleScreenList(), message.isUpdate(), message.isDelete(), message.getActualName());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_SUCCESS)
//	public void receiveRoleList(RoleMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			roleService.saveRole(RabbitMqConstants.QUEUE_ROLE_SUCCESS, message.getRole(), message.getRoleScreenList(),
//					message.isUpdate(), message.isDelete(), message.getRoleName());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_USED_QUOTA)
//	public void receiveUsedQuotaMessage(CustomerMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			Boolean flag = customerService.isCustomerNotificationEnable((Integer) message.getCustomerData().get("id"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_USED_QUOTA, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName"), "Customer Used quota", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CREATE_SYSTEM_CONFIG_NOTIFICATION)
//	public void receiveConfigToAdd(ConfigMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			configService.saveAndUpdateSystemConfigFromCommon(message.getSystemConfig(), false);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_SYSTEM_CONFIG_NOTIFICATION)
//	public void receiveConfigToUpdate(ConfigMessage message) {
//		try {
//			setUserProperties(message.getTraceId(), message.getSpanId());
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			configService.saveAndUpdateSystemConfigFromCommon(message.getSystemConfig(), true);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	public void setUserProperties(String traceId, String spanId) {
////		if(message.getCurrentUser() != null)
////			MDC.put("userName", message.getCurrentUser());
//		if (traceId != null)
//			MDC.put("traceId", traceId);
//		if (spanId != null)
//			MDC.put("spanId", spanId);
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS)
//	public void receiveCustomerApprovalMessage(CustApprovalMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("approverName : " + message.getApproverTeam());
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Approval Success", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL)
//	public void receiveCustomerFailMessage(CustApprovalFailMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Approval Fail" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS)
//	public void receiveCustomerRenewMessage(CustomerRenewalMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("renew plan : " + message.getPlan());
//			//System.out.println("plan type : " + message.getPurchaseType());
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Renewal Message", LocalDateTime.now(), message.toString());
//			}
//		} catch (Exception e) {
//		e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS)
//	public void receiveCustomerRechargeMessage(CustomerRechargeMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("renew plan : " + message.getPlan());
//			//System.out.println("plan type : " + message.getPurchaseType());
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Recharge", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS)
//	public void receiveCustomerRegistrationMessage(CustomerRegistrationSuccessMsg message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			//System.out.println("registrationDate type : " + message.getRegistrationDate());
//			//System.out.println("planName : " + message.getPlanName());
//			//System.out.println("password : " + message.getPassword());
//			//Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			//if(flag) {
//			try {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS,
//						message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Registration Success", LocalDateTime.now(), message.toString());
//				//}
//			} catch (Exception e) {
//				e.getMessage();
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK)
//	public void receiveCustomerPaymentLinkMessage(PaymentLinkMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("customerName"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Payment Link", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS)
//	public void receiveCustomerPaymentSuccessMessage(PaymentSuccess message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("userName"));
//			//System.out.println("username: " + message.getUsername());
//			//System.out.println("userId: " + message.getUserId());
//			//System.out.println("reciptNo: " + message.getReciptNo());
//			//System.out.println("paymentAmount: " + message.getPaymentAmount());
//			//System.out.println("paymentDate: " + message.getPaymentDate());
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("userName"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("userName") , "Customer Payment Success" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS)
//	public void receiveTicketAssignSuccessMessage(TicketAssignMessege message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			//System.out.println("caseNumber: " + message.getCustomerData().get("caseNumber"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING)
//	public void reciveCustomerDunningMessage(CustomerDunningMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Dunning", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION)
//	public void reciveCustomerDunningMessage(CustomerDeactivationMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			//System.out.println("remarks : " + message.getRemarks());
//			//System.out.println("plans : " + message.getPlanname());
//			//System.out.println("date : " + message.getDate());
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Dunning Deactivation" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION)
//	public void reciveCustomerOtpRegistrationMessage(CustomerOtpRegistrationMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "OTP Registartion" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF)
//	public void reciveStaffDocumentExpiredMessage(StaffExpiredMassage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getStaffUserData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF, message.getMessage(),
//					message.getStaffUserData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF)
//	public void reciveFollowUpReminderMessageForStaff(FollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER)
//	public void reciveFollowUpReminderMessageForCustomer(FollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF)
//	public void reciveFollowUpOverDueMessageForStaff(FollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//	public void reciveFollowUpOverDueMessageForParentStaff(FollowUpMessage message) {
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//		emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF,
//				message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//				message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//				message.isSmsConfigured());
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_SEND_STATUS)
//	public void reciveStaffStatusMessage(StaffStatusChangeMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: "+message.getStaffUserData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_STAFF_SEND_STATUS, message.getMessage(), message.getStaffUserData(), message.getSourceName(),message.getEmailTemplate(),message.getSmsTemplate(),message.getAppendUrl(),message.isEmailConfigured(),message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF)
//	public void reciveNoLeadFollowUpReminderMessageForStaff(FollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF)
//	public void reciveNoLeadFollowUpReminderMessageForParentStaff(FollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF)
//	public void reciveNoFollowUpMessageForStaff(FollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF)
//	public void reciveNoFollowUpMessageForParentStaff(FollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE)
//	public void receiveCustomerTicketStatusChange(CustTicketStatusMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE,
//						message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Ticket Status Change", LocalDateTime.now(), message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE)
//	public void receiveWorkflowAssignActionMessage(WorkflowTicketMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)
//	public void receiveTicketPickMessageToTeam(WorkflowTicketMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG)
//	public void receiveFollowUpDetailsMsg(SendFollowUpRemarkMsg message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG)
//	public void receiveProblemDomainChangeMsg(SendFollowUpRemarkMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ETR)
//	public void receiveTicketETRMsg(TicketETRMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ETR,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF)
//	public void reciveCafFollowUpReminderMessageForStaff(CafFollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER)
//	public void reciveCafFollowUpReminderMessageForCustomer(CafFollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER,
//						message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF)
//	public void reciveCafFollowUpOverDueMessageForStaff(CafFollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//	public void reciveCafFollowUpOverDueMessageForParentStaff(CafFollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//
//	//Ticket Followup code
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF)
//	public void reciveTicketFollowUpReminderMessageForStaff(TicketFollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER)
//	public void reciveTicketFollowUpReminderMessageForCustomer(TicketFollowUpMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER,
//						message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF)
//	public void reciveTicketFollowUpOverDueMessageForStaff(TicketFollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//	public void reciveTicketFollowUpOverDueMessageForParentStaff(TicketFollowUpMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)
//	public void ticketCreationSuccess(TicketCreationMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("caseNumber : " + message.getCaseNumber());
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		} catch(Exception e) {
//			//System.out.println("receiveTicketCreationSuccess Msg Failed:"+e.getMessage());
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG)
//	public void ticketRescheduleSuccessEventMessage(TicketRescheduleMsg message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("staffPersonName : " + message.getStaffPersonName());
//			//System.out.println("caseNumber : " + message.getCustomerData().get("caseNumber"));
//			//System.out.println("FollowUpDateAndTime : " + message.getCustomerData().get("followUpDateAndTime"));
//
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER)
//	public void ticketTatReminderMessage(TicketTatReminderNotifcation message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("staffPersonName : " + message.getCustomerData().get("staffPersonName"));
//			//System.out.println("caseNumber : " + message.getCustomerData().get("caseNumber"));
//			//System.out.println("FollowUpDateAndTime : " + message.getCustomerData().get("followUpDateAndTime"));
//
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/**Message Reciver for advance notification**/
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION)
//	public void reciveCustomerAdvanceDunningMessage(CustomerDunningMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Dunning Advance Notification" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/**Message Reciver for dunning documnet notification**/
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT)
//	public void recivePartnerDunningDocumentMessage(PartnerExpiredDocumentMessage message) {
//		try {
//
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getStaffName());
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT, message.getMessage(),
//					message.getPartnerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	/**Message Reciver for dunning document deactivation notification**/
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION)
//	public void recivePartnerDunningDocumentDeactivationMessage(PartnerExpiredDocumentMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getStaffName());
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION, message.getMessage(),
//					message.getPartnerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	/**Message Reciver for dunning document deactivation staff notification**/
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF)
//	public void recivePartnerDunningDocumentDeactivationStaffMessage(StaffExpiredMassage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getStaffUserData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF, message.getMessage(),
//					message.getStaffUserData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	//Receive Customer status Inactive notification.
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION)
//	public void CustomerStatusInActiveMessage(CustomerStatusInActiveMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Status InActive" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Document Verification notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION)
//	public void CustDocumentVerificationMessage(CustDocumentVerificationMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Document Verify" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)
//	public void reciveEmailMessageForLeadQuotation(EmailMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotificationWithAttachment(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//
//	/*Receive Customer Service Active notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION)
//	public void CustServiceActiveMessage(CustServiceActiveMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Service Activation" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Service InActive notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION)
//	public void CustServiceInActiveMessage(CustServiceInActiveMsg message) {
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//		//System.out.println("username: " + message.getCustomerData().get("username"));
//		try {
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if (flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Customer Service Inactivation", LocalDateTime.now(), message.toString());
//			}
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	/*Receive Customer Change Password notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION)
//	public void CustChangePasswordMessage(CustChangePasswordMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Change Password" , LocalDateTime.now() ,  message.toString());
//
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT)
//	public void receiveCustomerDunningDocumentMessage(CustomerExpiredDocumentMessage message) {
//		try{
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getPartnerData().get("partnerName"));
//			if(flag) {
//
//				//System.out.println("Received Message From RabbitMq : <" + message + ">");
//				//System.out.println("username: " + message.getStaffName());
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT, message.getMessage(),
//						message.getPartnerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getPartnerData().get("partnerName") , "Customer Document Dunning" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Open address shifting notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION)
//	public void CustOpenAddressShiftingMessage(CustAddressShiftingMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Open Address Shifting" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Close address shifting notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION)
//	public void CustCloseAddressShiftingMessage(CustAddressShiftingMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Close Address Shifting" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Payment Verification notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION)
//	public void CustPaymentVerificationMessage(CustPaymentVerificationMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Payment Verify" , LocalDateTime.now() ,  message.toString());
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive Customer Ticket Close notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION)
//	public void CustTicketCloseMessage(CustTicketCloseMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) message.getCustomerData().get("username"));
//			if(flag) {
//				emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username") , "Customer Ticket Close" , LocalDateTime.now() ,  message.toString());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION)
//	public void receiveMessageCustomerApigw(CustomMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("Message : " + message);
//			customerService.saveSubscriber(message);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/*Receive lead creation notification*/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION)
//	public void leadCreationMessage(LeadCreationMsg message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("firstname: " + message.getCustomerData().get("firstname"));
//
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//			notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("firstname") , "Lead Creation Success" , LocalDateTime.now() ,  message.toString());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE)
//	public void receiveTicketTatMessageSuccessMessage(TicketAssignMessege message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			//System.out.println("caseNumber: " + message.getCustomerData().get("caseNumber"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//  /****/
//  @Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER)
//	public void receiveTicketRemarkCustomerMsg(SendFollowUpRemarkMsg message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/****/
//
//
//	/** Message Reciver for Customer Quota Notifcation started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER)
//	public void receiveCustomerQuotaNotificationMsg(CustomerQuotaNotificationMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/**Message Reciver for Customer Quota Notifcation ended**/
//
//
//   //Create MVNO from RabbitMQ
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//	public void receiveMessageCreateMVNO(SaveMvnoSharedDataMessage message) {
//		//System.out.println("Received Message From RabbitMq receiverMessage : <" + message + ">");
//		try {
//			mvnoService.saveMVNOEntity(message);
//			//System.out.println("MVNO Created Successfully From Rms");
//		} catch (Exception e) {
//			//System.out.println("receiveMessageCreateMVNO Failed :" +e.getMessage());
//			throw new RuntimeException(e);
//		}
//	}
//
//	//Update MVNO from RabbitMQ
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//	public void receiveMessageUpdateMVNO(UpdateMvnoSharedDataMessage message) {
//		//System.out.println("Received Message From RabbitMq receiverMessage : <" + message + ">");
//		try {
//			mvnoService.updateMVNOEntity(message);
//			//System.out.println("MVNO Updated Successfully From Rms");
//		} catch (Exception e) {
//			//System.out.println("receiveMessageUpdateMVNO Failed :" +e.getMessage());
//			throw new RuntimeException(e);
//		}
//	}
//
//
//	//Business Unit
//
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_NOTIFICATION)
//	public void receiveMessageForBusinessUnitCreate(SaveBusinessUnitSharedDataMessage message) {
//		//System.out.println("Received Message From RabbitMq For business unit Creation, receiveMessage : <" + message + ">");
//		//System.out.println("Message : " + message);
//		try {
//			businessUnitService.saveBusineeUnit(message);
//		}
//		catch(Exception e) {
//			//System.out.println("receiveMessageCustomerApigw Failed for business unit Creation :"+e.getMessage());
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION)
//	public void receiveMessageForBusinessUnitUpdate(UpdateBusinessUnitSharedDataMessage message) {
//		////System.out.println("Received Message From RabbitMq For business unit Update, receiveMessage : <" + message + ">");
//		//System.out.println("Message : " + message);
//		try {
//			businessUnitService.updateBusinessUnit(message);
//		}
//		catch(Exception e) {
//			//System.out.println("receiveMessageCustomerApigw Failed for business unit Update :"+e.getMessage());
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER)
//	public void receiveTicketExternalRemarkCustomerMsg(TicketExternalRemarkCustomerMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION)
//	public void receiveTicketExternalRemarkCustomerMsg(CustomerInvoiceMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotificationWithAttachmentForAllEvents(RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/** Message Reciver for Customer Quota Exhaust Notifcation started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER)
//	public void receiveCustomerQuotaExhuastNotificationMsg(CustomerQuotaNotificationMessage message) {
//		try{
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//	/**Message Reciver for Customer Quota Exhaust Notifcation ended**/
//
//	/**Recieve message for ticket alert started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF)
//	public void receiveTicketAlertForStaffMsg(TicketAlertStaffMessage message) {
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), null,
//					null, null, true, true);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	/**Recieve message for ticket alert ended**/
//	/**Recieve message for unregister customer Inquiery email to customer started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER)
//	public void receiveUnregisterCustomerInquireyToCustomerMessage(ImmediateAttentionForUnRegisterCustomerMessage message) {
//	try{
//		emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER,
//				message.getMessage(), message.getCustomerData(), message.getSourceName(),null,
//				null,null,true,true);
//	}catch (Exception e){
//		e.printStackTrace();
//	}
//
//	}
//	/**Recieve message unregister customer Inquiery email to customer ended**/
//
//	/**Recieve message for register customer Inquiery email to customer started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER)
//	public void receiveUnregisterCustomerInquireyToCustomerMessage(ImmediateAttentionForRegisterCustomerMessage message) {
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(),null,
//					null,null,true,true);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	/**Recieve message register customer Inquiery email to customer ended**/
//
//	/**Recieve message for unregister customer Inquiery email to Staff started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF)
//	public void receiveUnregisterCustomerInquireyToStaffMessage(ImmediateAttentionForUnRegisterCustomerToStaffMessage message) {
//		try{
//			//		System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(),null,
//					null,null,true,true);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	/**Recieve message unregister customer Inquiery email to customer ended**/
//	/**Recieve message for unpick ticket alert started**/
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF)
//	public void receiveUnpickTicketAlertForStaffMsg(UnPickTicketAlertStaffMessage message) {
//		try{
//			//		System.out.println("Received Message From RabbitMq : <" + message + ">");
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(),null,
//					null,null,true,true);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	/**Recieve message for unpick ticket alert ended**/
//
//
//	//@Transactional
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION)
//	public void receiveInventoryApprovalForStaffMsg(InventoryApprovalSuccessMsg message) {
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION,
//					message.getMessage(), message.getCustomerData(), message.getSourceName(), null,
//					null, null, true, false);
//		}catch(Exception e) {
//			e.printStackTrace();
//			//System.out.println("receiveInventoryApprovalForStaffMsg Failed for Inventory Approval :"+e.getMessage());
//		}
//	}
//
//
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE)
//	public void receiveTatSuccessMessageForCAF(TicketAssignMessege message) {
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE)
//	public void receiveTerminationSuccessMessageForCAF(TicketAssignMessege message) {
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	@Profile("rabbitmq")
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE)
//	public void receiveLEADTATSuccessMessageForCAF(TicketAssignMessege message) {
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//	String getUserName(Map<String, Object> data) {
//		String userName = null;
//		if (data.containsKey(RabbitMqConstants.USER_NAME) && data.get(RabbitMqConstants.USER_NAME) != null) {
//			userName = data.get(RabbitMqConstants.USER_NAME).toString();
//		} else if (data.containsKey(RabbitMqConstants.MOBILE_NUMBER) && data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
//			userName = data.get(RabbitMqConstants.MOBILE_NUMBER).toString();
//		} else if (data.containsKey(RabbitMqConstants.EMPLOYEE_NAME) && data.get("employeeName") != null) {
//			userName = data.get(RabbitMqConstants.EMPLOYEE_NAME).toString();
//		} else if (data.containsKey(RabbitMqConstants.CUSTMR_NAME) && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
//			userName = data.get(RabbitMqConstants.CUSTMR_NAME).toString();
//		} else if (data.containsKey(RabbitMqConstants.FIRSTNAME) && data.get(RabbitMqConstants.FIRSTNAME) != null) {
//			userName = data.get(RabbitMqConstants.FIRSTNAME).toString();
//		} else if (data.containsKey(RabbitMqConstants.CUSTMR_NAME) && data.get("customerName") != null) {
//			userName = data.get(RabbitMqConstants.CUSTMR_NAME).toString();
//		}
//		return userName;
//
//	}
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION)
//	public void reciveMvnoDunningMessageDunningMessage(MvnoDocumentDunningMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//					emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION, message.getMessage(),
//						message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//						message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//						message.isSmsConfigured());
//				notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Mvno Document Dunning", LocalDateTime.now(), message.toString());
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION)
//	public void reciveMvnoDeactivationMessageMessage(MvnoDocumentDunningMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//			notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "Mvno Document Dunning", LocalDateTime.now(), message.toString());
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION)
//	public void reciveMvnoAdvancePaymentMessageMessage(MvnoPaymentDunningMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//			notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "MVNO Payment", LocalDateTime.now(), message.toString());
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION)
//	public void reciveMvnoPaymentRemainderMessageMessage(MvnoPaymentDunningMessage message) {
//		try {
//			//System.out.println("Received Message From RabbitMq : <" + message + ">");
//			//System.out.println("username: " + message.getCustomerData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//			notificationAuditService.saveNotificationAudit((String) message.getCustomerData().get("username"), "MVNO Payment", LocalDateTime.now(), message.toString());
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	@Transactional
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_NOTIFICATION_ISP)
//	public void receiveMessageMvnoUpdateForIsp(UpdateMvnoData message) {
//		try{
//			mvnoService.updateMvnoIdIsptoIsp(message.getOldmvnoId(),message.getNewmvnoId());
//		}catch (Exception e){
//			log.error("RecievedMessage update failed: " + message);
//			e.printStackTrace();
//		}
//	}
//
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION)
//	public void reciveMvnoDeactivationMessageMessage(PlanExpiryNotificationMessage message) {
//		try {
//			System.out.println("Received Message From RabbitMq for Plan Expiry Notification : <" + message + ">");
//			System.out.println("username: " + message.getCustomerData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION)
//	public void receiveDeviceInputPortUsedNotificationForStaffMsg(DevicePortNotificationMessage message) {
//		System.out.println("Received Message From RabbitMq : <" + message + ">");
//		try {
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION,
//					message.getMessage(), message.getStaffData(), message.getSourceName(), null,
//					null, null, true, false);
//		}catch(Exception e) {
//			e.printStackTrace();
//			System.out.println("receiveDeviceInputPortUsedNotificationForStaffMsg Failed:- "+e.getMessage());
//		}
//	}
//	@Profile("rabbitmq")
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION)
//	public void reciveChangePlanMessageMessage(ChangePlanNotification message) {
//		try {
//			System.out.println("Received Message From RabbitMq for Plan Expiry Notification : <" + message + ">");
//			System.out.println("username: " + message.getCustomerData().get("username"));
//			emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION, message.getMessage(),
//					message.getCustomerData(), message.getSourceName(), message.getEmailTemplate(),
//					message.getSmsTemplate(), message.getAppendUrl(), message.isEmailConfigured(),
//					message.isSmsConfigured());
//
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//}
