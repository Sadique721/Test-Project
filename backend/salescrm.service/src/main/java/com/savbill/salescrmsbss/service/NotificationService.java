package com.savbill.salescrmsbss.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.savbill.salescrmsbss.kafka.KafkaConstant;
import com.savbill.salescrmsbss.kafka.KafkaMessageData;
import com.savbill.salescrmsbss.kafka.KafkaMessageSender;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.savbill.salescrmsbss.entity.ClientService;
import com.savbill.salescrmsbss.entity.LeadFollowUp;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.entity.TemplateNotification;
import com.savbill.salescrmsbss.helper.LeadFollowUpDto;
//import com.savbill.salescrmsbss.rabbitMq.MessageSender;
import com.savbill.salescrmsbss.rabbitMq.RabbitMqConstants;
import com.savbill.salescrmsbss.rabbitMq.message.EmailMessage;
import com.savbill.salescrmsbss.rabbitMq.message.FollowUpMessage;
import com.savbill.salescrmsbss.repository.LeadFollowUpRepository;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.NotificationTemplateRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.google.gson.Gson;

@Service
public class NotificationService {

	public static final String MODULE = "[NotificationService]";

	private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");
	public static final String FOLLOW_UP_REMIDER_TIME_NAME = "followUpReminderTime";
	public static final String NO_LEAD_FOLLOWUP_SEND_NOTIFICATION_TIME_IN_DAYS = "noLeadFollowupSendNotificationTimeInDays";

//	@Autowired
//	private MessageSender messageSender;

	@Autowired
	private LeadFollowUpRepository leadFollowUpRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private NotificationTemplateRepository notificationTemplateRepository;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private LeadFollowUpService leadFollowUpService;

	@Autowired
	private KafkaMessageSender kafkaMessageSender;

	@Scheduled(cron = "${cronJobTimeForNoFollowUp}")
	public void sendNoFollowUpNotification() {
		String SUBMODULE = MODULE + "sendNoFollowUpNotification()";
		logger.info("XXXXXXXXXXXX----------CRON TIME_FOR_REMINDER_NO_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		try {
			Pageable pageRequest = PageRequest.of(0, 200);
			LeadFollowUp leadFollowUp = new LeadFollowUp();
			leadFollowUp.setNoFollowupAction(false);
			Page<LeadFollowUp> onePage = this.leadFollowUpRepository.findByIsNoFollowupAction(false, pageRequest);
			pageRequest = pageRequest.next();
			onePage.forEach(leadFollowUpEntity -> {
				Duration duration = Duration.between(leadFollowUpEntity.getCreatedOn(), LocalDateTime.now());
				long leadCreatedDays = duration.toDays();
				ClientService clientService = clientServiceSrv
						.getByNameAndMvnoId(NO_LEAD_FOLLOWUP_SEND_NOTIFICATION_TIME_IN_DAYS, leadFollowUpEntity.getLeadMaster().getMvnoId());
				int noLeadFollowupSendNotificationTimeInDays = Integer.parseInt(clientService.getValue());
				if (noLeadFollowupSendNotificationTimeInDays <= leadCreatedDays) {
					if (leadFollowUpEntity.getStatus() != null
							&& leadFollowUpEntity.getStatus().equalsIgnoreCase("Pending")) {
						// send notification
						String customerName = "";
						if (!StringUtils.isEmpty(leadFollowUpEntity.getLeadMaster().getLastname()))
							customerName += leadFollowUpEntity.getLeadMaster().getFirstname() + " "
									+ leadFollowUpEntity.getLeadMaster().getLastname();
						else
							customerName += leadFollowUpEntity.getLeadMaster().getFirstname();
						StaffUser staffUser = this.staffUserRepository
								.findById(leadFollowUpEntity.getStaffUser().getId()).get();
						String staffPersonName = staffUser.getFirstname() + " " + staffUser.getLastname();
						if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
							sendStaffNotificationForNoFollowUp(staffPersonName, customerName,
									leadFollowUpEntity.getFollowUpName(), staffUser.getEmail(), staffUser.getPhone(),
									leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), leadFollowUpEntity.getLeadMaster().getBuId().intValue());
						}else{
							sendStaffNotificationForNoFollowUp(staffPersonName, customerName,
									leadFollowUpEntity.getFollowUpName(), staffUser.getEmail(), staffUser.getPhone(),
									leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), null);
						}

						if (staffUser.getStaffUserparentId() != null) {
							StaffUser parentStaffUser = this.staffUserRepository
									.findById(staffUser.getStaffUserparentId()).get();
							String parentStaffPersonName = parentStaffUser.getFirstname() + " "
									+ parentStaffUser.getLastname();
							if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
								sendParentStaffNotificationForNoFollowUp(parentStaffPersonName, staffPersonName,
										customerName, leadFollowUpEntity.getFollowUpName(), parentStaffUser.getEmail(),
										parentStaffUser.getPhone(), leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(),leadFollowUpEntity.getLeadMaster().getBuId().intValue());
							}else {
								sendParentStaffNotificationForNoFollowUp(parentStaffPersonName, staffPersonName,
										customerName, leadFollowUpEntity.getFollowUpName(), parentStaffUser.getEmail(),
										parentStaffUser.getPhone(), leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(),null);
							}

						}
						leadFollowUpEntity.setNoFollowupAction(true);
						this.leadFollowUpRepository.save(leadFollowUpEntity);
					}
				}
			});
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	@Scheduled(cron = "${cronJobTimeForNoLeadFollowUp}")
	public void sendNoLeadFollowUpNotification() {
		String SUBMODULE = MODULE + "sendNoLeadFollowUpNotification()";
		logger.info("XXXXXXXXXXXX----------CRON TIME_FOR_REMINDER_NO_LEAD_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		try {
			Pageable pageRequest = PageRequest.of(0, 200);
			Page<LeadMaster> onePage = this.leadMasterRepository.findByNoLeadFollowupSendNotification(false,
					pageRequest);
			pageRequest = pageRequest.next();
			onePage.forEach(leadMasterEntity -> {
				Duration duration = Duration.between(leadMasterEntity.getCreatedOn(), LocalDateTime.now());
				long leadCreatedDays = duration.toDays();
				ClientService clientService = clientServiceSrv
						.getByNameAndMvnoId(NO_LEAD_FOLLOWUP_SEND_NOTIFICATION_TIME_IN_DAYS, leadMasterEntity.getMvnoId());
				int noLeadFollowupSendNotificationTimeInDays = Integer.parseInt(clientService.getValue());
				if (noLeadFollowupSendNotificationTimeInDays <= leadCreatedDays) {
					if (leadMasterEntity.getLeadStatus() != null
							&& leadMasterEntity.getLeadStatus().equalsIgnoreCase("Inquiry")) {
						List<LeadFollowUpDto> leadFollowUpDtoList = this.leadFollowUpService
								.findAllByLeadId(leadMasterEntity.getId());
						if (leadFollowUpDtoList == null || leadFollowUpDtoList.isEmpty()) {
							// send notification
							String customerName = "";
							if (!StringUtils.isEmpty(leadMasterEntity.getLastname()))
								customerName += leadMasterEntity.getFirstname() + " " + leadMasterEntity.getLastname();
							else
								customerName += leadMasterEntity.getFirstname();
							StaffUser staffUser = this.staffUserRepository
									.findById(Integer.parseInt(leadMasterEntity.getCreatedBy())).get();
							String staffPersonName = staffUser.getFirstname() + " " + staffUser.getLastname();
							if(leadMasterEntity.getBuId()!=null){
								sendStaffNotificationForNoLeadFollowUp(staffPersonName, customerName, staffUser.getEmail(),
										staffUser.getPhone(), leadMasterEntity.getMvnoId().intValue(),leadMasterEntity.getBuId().intValue());
							}else{
								sendStaffNotificationForNoLeadFollowUp(staffPersonName, customerName, staffUser.getEmail(),
										staffUser.getPhone(), leadMasterEntity.getMvnoId().intValue(),null);
							}
							if (staffUser.getStaffUserparentId() != null) {
								StaffUser parentStaffUser = this.staffUserRepository
										.findById(staffUser.getStaffUserparentId()).get();
								String parentStaffPersonName = parentStaffUser.getFirstname() + " "
										+ parentStaffUser.getLastname();
								if(leadMasterEntity.getBuId()!=null){
									sendParentStaffNotificationForNoLeadFollowUp(parentStaffPersonName, staffPersonName,
											customerName, parentStaffUser.getEmail(), parentStaffUser.getPhone(),
											leadMasterEntity.getMvnoId().intValue(),leadMasterEntity.getBuId().intValue());
								}else{
									sendParentStaffNotificationForNoLeadFollowUp(parentStaffPersonName, staffPersonName,
											customerName, parentStaffUser.getEmail(), parentStaffUser.getPhone(),
											leadMasterEntity.getMvnoId().intValue(),null);
								}

							}
							LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMasterEntity.getId())
									.get();
							exstingLeadMaster.setNoLeadFollowupSendNotification(true);
							this.leadMasterRepository.save(exstingLeadMaster);
						}
					}
				}
			});
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	@Scheduled(cron = "${cronJobTimeForOverDueFollowUp}")
	public void sendFollowUpOverDueNotification() {
		String SUBMODULE = MODULE + "sendFollowUpOverDueNotification()";
		logger.info("XXXXXXXXXXXX----------CRON TIME_FOR_REMINDER_OVERDUE_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		try {
			Pageable pageRequest = PageRequest.of(0, 200);
			LeadFollowUp leadFollowUp = new LeadFollowUp();
			leadFollowUp.setIsMissed(false);
			leadFollowUp.setIsSend(false);
			Page<LeadFollowUp> onePage = this.leadFollowUpRepository.findByIsMissedAndIsSendAndStatus(false, false,
					"Pending", pageRequest);
			pageRequest = pageRequest.next();
			onePage.forEach(leadFollowUpEntity -> {
				if (leadFollowUpEntity.getFollowUpDatetime().isBefore(LocalDateTime.now())) {
					// send reminder notification
					String customerName = "";
					if (!StringUtils.isEmpty(leadFollowUpEntity.getLeadMaster().getLastname()))
						customerName += leadFollowUpEntity.getLeadMaster().getFirstname() + " "
								+ leadFollowUpEntity.getLeadMaster().getLastname();
					else
						customerName += leadFollowUpEntity.getLeadMaster().getFirstname();
					String staffPersonName = leadFollowUpEntity.getStaffUser().getFirstname() + " "
							+ leadFollowUpEntity.getStaffUser().getLastname();
					String followUpDateTime = leadFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
					if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
						sendStaffNotificationForOverDue(staffPersonName, customerName,
								leadFollowUpEntity.getStaffUser().getEmail(), leadFollowUpEntity.getStaffUser().getPhone(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), null, followUpDateTime,leadFollowUpEntity.getLeadMaster().getBuId().intValue());
					}else{
						sendStaffNotificationForOverDue(staffPersonName, customerName,
								leadFollowUpEntity.getStaffUser().getEmail(), leadFollowUpEntity.getStaffUser().getPhone(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), null, followUpDateTime,null);
					}

					if (leadFollowUpEntity.getStaffUser().getStaffUserparentId() != null) {
						StaffUser parentStaffuser = this.staffUserRepository
								.findById(leadFollowUpEntity.getStaffUser().getStaffUserparentId()).get();
						String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();
						if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
							sendParentStaffNotificationForOverDue(staffPersonName, customerName, parentStaffuser.getEmail(),
									parentStaffuser.getPhone(), leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), null, followUpDateTime,
									parentStaffName,leadFollowUpEntity.getLeadMaster().getBuId().intValue() );
						}else{
							sendParentStaffNotificationForOverDue(staffPersonName, customerName, parentStaffuser.getEmail(),
									parentStaffuser.getPhone(), leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), null, followUpDateTime,
									parentStaffName,null );
						}

					}

					leadFollowUpEntity.setIsMissed(true);
					leadFollowUpEntity.setIsSend(true);
					this.leadFollowUpRepository.save(leadFollowUpEntity);
				}
			});
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Scheduled(cron = "${cronJobTimeForReminderFollowUp}")
	public void sendFollowUpReminderNotification() {
		String SUBMODULE = MODULE + "sendFollowUpReminderNotification()";
		logger.info("XXXXXXXXXXXX----------CRON TIME_FOR_REMINDER_LEAD_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		try {
			ClientService clientService = clientServiceSrv.getByNameAndMvnoId(FOLLOW_UP_REMIDER_TIME_NAME, getLoggedInMvnoId().longValue());
			Integer followUpTime = null;
			if (clientService == null) {
				followUpTime = 15;
			}else{
				followUpTime = Integer.parseInt(clientService.getValue());
			}
			LocalDateTime toTime = LocalDateTime.now().plusMinutes(followUpTime);
			Pageable pageRequest = PageRequest.of(0, 200);
			Page<LeadFollowUp> onePage = this.leadFollowUpRepository.findByFollowUpDatetimeBetween(LocalDateTime.now(),
					toTime, pageRequest);
			logger.info("Total FollowUps Found: {}", onePage.getTotalElements());

			pageRequest = pageRequest.next();
			onePage.forEach(leadFollowUpEntity -> {
				if (!leadFollowUpEntity.getStatus().equalsIgnoreCase("Closed")) {
					Long followTime = Duration.between(LocalDateTime.now(), leadFollowUpEntity.getFollowUpDatetime())
							.toMinutes();
					// send reminder notification
					String customerName = "";
					if (!StringUtils.isEmpty(leadFollowUpEntity.getLeadMaster().getLastname()))
						customerName += leadFollowUpEntity.getLeadMaster().getFirstname() + " "
								+ leadFollowUpEntity.getLeadMaster().getLastname();
					else
						customerName += leadFollowUpEntity.getLeadMaster().getFirstname();
					String staffPersonName = leadFollowUpEntity.getStaffUser().getFirstname() + " "
							+ leadFollowUpEntity.getStaffUser().getLastname();
					String followUpDateTime = leadFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
					if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
						sendCustomerNotificationForReminder(staffPersonName, customerName,
								leadFollowUpEntity.getLeadMaster().getEmail(),
								leadFollowUpEntity.getLeadMaster().getMobile(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), followTime.intValue(),
								followUpDateTime,leadFollowUpEntity.getLeadMaster().getBuId().intValue());
					}else{
						sendCustomerNotificationForReminder(staffPersonName, customerName,
								leadFollowUpEntity.getLeadMaster().getEmail(),
								leadFollowUpEntity.getLeadMaster().getMobile(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), followTime.intValue(),
								followUpDateTime,null);
					}
					if(leadFollowUpEntity.getLeadMaster().getBuId()!=null){
						sendStaffNotificationForReminder(staffPersonName, customerName,
								leadFollowUpEntity.getStaffUser().getEmail(), leadFollowUpEntity.getStaffUser().getPhone(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), followTime.intValue(),
								followUpDateTime,leadFollowUpEntity.getLeadMaster().getBuId().intValue());
					}else{
						sendStaffNotificationForReminder(staffPersonName, customerName,
								leadFollowUpEntity.getStaffUser().getEmail(), leadFollowUpEntity.getStaffUser().getPhone(),
								leadFollowUpEntity.getLeadMaster().getMvnoId().intValue(), followTime.intValue(),
								followUpDateTime,null);
					}

					leadFollowUpEntity.setSendReminderNotification(true);
					this.leadFollowUpRepository.save(leadFollowUpEntity);
				}
			});
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendCustomerNotificationForReminder(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, Integer buId) {
		Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
				.findByTemplateName(RabbitMqConstants.FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE);
		if (optionalTemplate.isPresent()) {
			if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
				sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
						RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
						followUpTime, followUpDateTime, staffPersonName, null, true, true,buId);
			} else if (optionalTemplate.get().isEmailEventConfigured()) {
				sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
						RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
						followUpTime, followUpDateTime, staffPersonName, null, true, false,buId);
			} else if (optionalTemplate.get().isSmsEventConfigured()) {
				sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
						RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
						followUpTime, followUpDateTime, staffPersonName, null, false, true,buId);
			}
		}
	}

	public void sendStaffNotificationForNoLeadFollowUp(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer buId) {
		String SUBMODULE = MODULE + "sendStaffNotificationForNoLeadFollowUp()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							mvnoId, staffPersonName, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							mvnoId, staffPersonName, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							mvnoId, staffPersonName, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendParentStaffNotificationForNoLeadFollowUp(String parentPersonName, String staffPersonName,
			String customername, String email, String phone, Integer mvnoId,Integer buId) {
		String SUBMODULE = MODULE + "sendParentStaffNotificationForNoLeadFollowUp()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, staffPersonName, parentPersonName, true, true, buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, staffPersonName, parentPersonName, true, false, buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendNoleadFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, staffPersonName, parentPersonName, false, true, buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendStaffNotificationForReminder(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, Integer buId) {
		String SUBMODULE = MODULE + "sendStaffNotificationForReminder()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendStaffNotificationForOverDue(String staffPersonName, String customername, String email, String phone,
			Integer mvnoId, Integer followUpTime, String followUpDateTime, Integer buId) {
		String SUBMODULE = MODULE + "sendStaffNotificationForOverDue()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendParentStaffNotificationForOverDue(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String parentStaffPersonName, Integer buId) {
		String SUBMODULE = MODULE + "sendParentStaffNotificationForOverDue()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendOverDueNotificationForStaff(TemplateNotification templateNotification, String email, String phone,
			String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
			String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendOverDueNotificationForStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
//			Gson gson = new Gson();
//			gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage,
//							RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF));


				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendOverDueNotificationForParentStaff(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
			String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
			boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendOverDueNotificationForParentStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null, buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendReminderNotificationForCustomer(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
			String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
			boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendReminderNotificationForCustomer()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER));

				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendReminderNotificationForStaff(TemplateNotification templateNotification, String email, String phone,
			String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
			String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured,Integer buId) {
		String SUBMODULE = MODULE + "sendReminderNotificationForStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF);
					System.out.println("*********Followup notification sent successfully**************"+followUpMessage);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(), KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF));

				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendNoleadFollowUpReminderNotificationForStaff(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, Integer mvnoId, String staffPersonName,
			boolean isEmailConfigured, boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendNoleadFollowUpReminderNotificationForStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, null, null,
							customerName, staffPersonName, null, null,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public void sendNoleadFollowUpReminderNotificationForParentStaff(TemplateNotification templateNotification,
			String email, String phone, String headerName, String customerName, Integer mvnoId, String staffPersonName,
			String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured,Integer buId) {
		String SUBMODULE = MODULE + "sendNoleadFollowUpReminderNotificationForParentStaff()";
		try {

			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, null, null,
							customerName, staffPersonName, parentStaffPersonName, null,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendStaffNotificationForNoFollowUp(String staffPersonName, String customername, String followUpName,
			String email, String phone, Integer mvnoId, Integer buId) {
		String SUBMODULE = MODULE + "sendStaffNotificationForNoFollowUp()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.NO_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendNoFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							followUpName, mvnoId, staffPersonName, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendNoFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							followUpName, mvnoId, staffPersonName, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendNoFollowUpReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername,
							followUpName, mvnoId, staffPersonName, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendNoFollowUpReminderNotificationForStaff(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, String followUpName, Integer mvnoId,
			String staffPersonName, boolean isEmailConfigured, boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendNoFollowUpReminderNotificationForStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, null, null,
							customerName, staffPersonName, null, followUpName,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendParentStaffNotificationForNoFollowUp(String parentStaffPersonName, String staffPersonName,
			String customername, String followUpName, String email, String phone, Integer mvnoId, Integer buId) {
		String SUBMODULE = MODULE + "sendParentStaffNotificationForNoFollowUp()";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.NO_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendNoFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							followUpName, mvnoId, staffPersonName, parentStaffPersonName, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendNoFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							followUpName, mvnoId, staffPersonName, parentStaffPersonName, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendNoFollowUpReminderNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							followUpName, mvnoId, staffPersonName, parentStaffPersonName, false, true,buId);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendNoFollowUpReminderNotificationForParentStaff(TemplateNotification templateNotification,
			String email, String phone, String headerName, String customerName, String followUpName, Integer mvnoId,
			String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured, Integer buId) {
		String SUBMODULE = MODULE + "sendNoFollowUpReminderNotificationForParentStaff()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					FollowUpMessage followUpMessage = new FollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, email, mvnoId, null, null,
							customerName, staffPersonName, parentStaffPersonName, followUpName,buId);
					followUpMessage.setEmailConfigured(isEmailConfigured);
					followUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(followUpMessage);
//					messageSender.send(followUpMessage, RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(followUpMessage,FollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void sendEmailToCustomerWithLeadQuotation(TemplateNotification templateNotification,
			List<String> custMailIds, String phone, String headerName, String customerName, Long mvnoId,
			boolean isEmailConfigured, boolean isSmsConfigured, String staffMail, String fileName, String filePath,
			List<String> circuits, Integer buId) {
		String SUBMODULE = MODULE + "sendEmailToCustomerWithLeadQuotation()";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					EmailMessage emailMessage = new EmailMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SALES_CRMS_BSS, phone, customerName, custMailIds, mvnoId,
							null, staffMail, fileName, filePath, circuits,buId);
					emailMessage.setEmailConfigured(isEmailConfigured);
					emailMessage.setSmsConfigured(isSmsConfigured);

					Gson gson = new Gson();
					gson.toJson(emailMessage);
//					messageSender.send(emailMessage, RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION);
					kafkaMessageSender.send(new KafkaMessageData(emailMessage, EmailMessage.class.getSimpleName()));
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}
	public Integer getLoggedInMvnoId() {
		int loggedInMvnoId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
			}
		} catch (Exception e) {
			loggedInMvnoId = -1;
		}
		return loggedInMvnoId;
	}

}
