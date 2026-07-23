package com.savbill.taskmanagement.core.modules.Notification.service;



import com.savbill.taskmanagement.core.constants.SearchConstants;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Notification.domain.Notification;
import com.savbill.taskmanagement.core.modules.Notification.mapper.NotificationMapper;
import com.savbill.taskmanagement.core.modules.Notification.model.NotificationDTO;
import com.savbill.taskmanagement.core.modules.Notification.repository.NotificationRepository;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUp;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Repository.TicketFollowUpRepository;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Service.TicketFollowUpService;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.taskmanagement.rabbitmq.messages.CafFollowUpMessage;
import com.savbill.taskmanagement.rabbitmq.messages.TicketFollowUpMessage;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService extends ExBaseAbstractService<NotificationDTO, Notification, Long> {


	public static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

	public static final String CAF_FOLLOW_UP_REMIDER_TIME_NAME = "cafFollowUpReminderTime";

	public static final String TICKET_FOLLOW_UP_REMIDER_TIME_NAME = "ticketFollowUpReminderTime";

	@Autowired
	private NotificationRepository repository;

	@Autowired
	private NotificationMapper mapper;



	@Autowired
	private NotificationTemplateRepository notificationTemplateRepository;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

//	@Autowired
//	private MessageSender messageSender;
	@Autowired
    private KafkaMessageSender kafkaMessageSender;

	@Autowired
	TicketFollowUpService ticketFollowUpService;

	@Autowired
	private TicketFollowUpRepository ticketFollowUpRepository;

	@Autowired
	private StaffUserRepository staffUserRepository;


	@Autowired
	CaseRepository caseRepository;



	public NotificationService(NotificationRepository repository, NotificationMapper mapper) {
		super(repository, mapper);
		this.repository = repository;
		sortColMap.put("id", "notification_id");
	}

	public List<NotificationDTO> findNotificationByCategory(String category, String status, Boolean email_enabled,
			Boolean sms_enabled) {
		return this.repository.findAllByCategoryAndStatus(category, status).stream()
				.filter(data -> data.getEmail_enabled() == email_enabled && data.getSms_enabled() == sms_enabled)
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
	}

	public List<NotificationDTO> findNotificationByCategory(String category, String status) {
		return this.repository.findAllByCategoryAndStatus(category, status).stream()
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
	}

	public NotificationDTO findByName(String name) {
		return this.repository.findAllByName(name).stream()
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).findAny().orElse(null);
	}

	public GenericDataDTO getByName(String name, PageRequest pageRequest) {
		String SUBMODULE = getModuleNameForLog() + " [getByName()] ";
		try {
			GenericDataDTO genericDataDTO = new GenericDataDTO();
			Page<Notification> notificationList = repository.findAllByNameOrStatus(pageRequest, name);
			if (null != notificationList && 0 < notificationList.getSize()) {
				makeGenericResponse(genericDataDTO, notificationList);
			}
			return genericDataDTO;
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy,
								 Integer sortOrder) {
		String SUBMODULE = getModuleNameForLog() + " [search()] ";
		try {
			PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
			if (null != filterList && 0 < filterList.size()) {
				for (GenericSearchModel searchModel : filterList) {
					if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
						return getByName(searchModel.getFilterValue(), pageRequest);
					}
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
		return null;
	}



	@Override
	public String getModuleNameForLog() {
		return "[Notification Service]";
	}


	//TicketFollowUP Schedulers


	@Scheduled(cron = "${cronJobTimeForOverDueTicketFollowUp}")
	public void sendTicketFollowUpOverDueNotification() {
		System.out.println("***** cronJobTimeForOverDueTicketFollowUp Started !!! *****");
		String SUBMODULE = getModuleNameForLog() + " [sendTicketFollowUpOverDueNotification()] ";
		try {
			Pageable pageRequest = PageRequest.of(0, 200);
			Page<TicketFollowUp> onePage = this.ticketFollowUpService.findByIsMissedAndIsSendAndStatus(pageRequest);
			pageRequest = pageRequest.next();
			onePage.forEach(tikeFollowUpEntity -> {
				Case aCase = caseRepository.findById(tikeFollowUpEntity.getTicket().getCaseId()).orElse(null);
				if (aCase != null) {
					if(!aCase.getCaseStatus().equalsIgnoreCase("Closed") && !aCase.getCaseStatus().equalsIgnoreCase("Raise and Close") ) {
						if (tikeFollowUpEntity.getFollowUpDatetime().isBefore(LocalDateTime.now())) {
							// send reminder notification
							String caseNumber = "";
							if (!StringUtils.isEmpty(tikeFollowUpEntity.getTicket().getCaseNumber()))
								caseNumber += tikeFollowUpEntity.getTicket().getCaseNumber();
							else
								caseNumber += tikeFollowUpEntity.getTicket().getCaseId();
							String staffPersonName = tikeFollowUpEntity.getStaffUser().getFirstname() + " "
									+ tikeFollowUpEntity.getStaffUser().getLastname();
							String followUpDateTime = tikeFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
							if (tikeFollowUpEntity.getTicket().getBuId() != null) {
								sendStaffNotificationForTicketOverDue(staffPersonName, caseNumber,
										tikeFollowUpEntity.getStaffUser().getEmail(), tikeFollowUpEntity.getStaffUser().getPhone(),
										tikeFollowUpEntity.getTicket().getMvnoId(), null, followUpDateTime, tikeFollowUpEntity.getTicket().getBuId());
								if (tikeFollowUpEntity.getStaffUser().getParentStaffId() != null) {
									StaffUser parentStaffuser = staffUserRepository.findById(tikeFollowUpEntity.getStaffUser().getParentStaffId()).orElse(null);
									String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();

									sendParentStaffNotificationForTicketOverDue(staffPersonName, caseNumber, parentStaffuser.getEmail(),
											parentStaffuser.getPhone(), tikeFollowUpEntity.getTicket().getMvnoId(), null, followUpDateTime,
											parentStaffName, tikeFollowUpEntity.getTicket().getBuId());
								}
							} else {
								sendStaffNotificationForTicketOverDue(staffPersonName, caseNumber,
										tikeFollowUpEntity.getStaffUser().getEmail(), tikeFollowUpEntity.getStaffUser().getPhone(),
										tikeFollowUpEntity.getTicket().getMvnoId(), null, followUpDateTime, null);
								if (tikeFollowUpEntity.getStaffUser().getParentStaffId() != null) {
									StaffUser parentStaffuser = staffUserRepository.findById(tikeFollowUpEntity.getStaffUser().getParentStaffId()).orElse(null);
									String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();

									sendParentStaffNotificationForTicketOverDue(staffPersonName, caseNumber, parentStaffuser.getEmail(),
											parentStaffuser.getPhone(), tikeFollowUpEntity.getTicket().getMvnoId(), null, followUpDateTime,
											parentStaffName, null);
								}
							}
							tikeFollowUpEntity.setIsMissed(true);
							tikeFollowUpEntity.setIsSend(true);
							this.ticketFollowUpRepository.save(tikeFollowUpEntity);
						}
					}
				}

			});
			System.out.println("***** cronJobTimeForOverDueTicketFollowUp Ended !!! *****");
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

//	@Scheduled(cron = "${cronJobTimeForReminderTicketFollowUp}")
//	public void sendTicketFollowUpReminderNotification() {
//		String SUBMODULE = getModuleNameForLog() + " [sendCafFollowUpReminderNotification()] ";
//		System.out.println("***** cronJobTimeForReminderTicketFollowUp Started !!! *****");
//
//		try {
//
//			ClientService clientService = clientServiceSrv.getByName(TICKET_FOLLOW_UP_REMIDER_TIME_NAME);
//			Integer ticketFollowUpTime = null;
//			if (clientService == null) {
//				ticketFollowUpTime = 15;
//			} else {
//				ticketFollowUpTime = Integer.parseInt(clientService.getValue());
//			}
//			LocalDateTime toTime = LocalDateTime.now().plusMinutes(ticketFollowUpTime);
//			Pageable pageRequest = PageRequest.of(0, 200);
//			Page<TicketFollowUp> onePage = this.ticketFollowUpRepository.findByFollowUpDatetimeBetween(LocalDateTime.now(),
//					toTime, pageRequest);
//			pageRequest = pageRequest.next();
//			onePage.forEach(ticketFollowUpEntity -> {
//				Case aCase = caseRepository.findById(ticketFollowUpEntity.getTicket().getCaseId()).orElse(null);
//				if (aCase != null) {
//					if(!aCase.getCaseStatus().equalsIgnoreCase("Closed") && !aCase.getCaseStatus().equalsIgnoreCase("Raise and Close")){
//						if (!ticketFollowUpEntity.getStatus().equalsIgnoreCase("Closed")) {
//							Long followTime = Duration.between(LocalDateTime.now(), ticketFollowUpEntity.getFollowUpDatetime())
//									.toMinutes();
//							// send reminder notification
//							String caseNumber = "";
//							if (!StringUtils.isEmpty(ticketFollowUpEntity.getTicket().getCaseNumber()))
//								caseNumber += ticketFollowUpEntity.getTicket().getCaseNumber();
//							else
//								caseNumber += ticketFollowUpEntity.getTicket().getCaseId();
//							String staffPersonName = ticketFollowUpEntity.getStaffUser().getFirstname() + " "
//									+ ticketFollowUpEntity.getStaffUser().getLastname();
//							String followUpDateTime = ticketFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
//							if (ticketFollowUpEntity.getTicket().getBuId() != null) {
//								sendCustomerNotificationForTicketReminder(staffPersonName, caseNumber,
//										ticketFollowUpEntity.getTicket().getStaffUser().getEmail(), ticketFollowUpEntity.getTicket().getStaffUser().getPhone(),
//										ticketFollowUpEntity.getTicket().getMvnoId().intValue(), followTime.intValue(),
//										followUpDateTime, ticketFollowUpEntity.getTicket().getBuId(),ticketFollowUpEntity.getTicket().getStaffUser().getUsername(), aCase.getStaffAdditionalEmail());
//								sendStaffNotificationForTicketReminder(staffPersonName, caseNumber,
//										ticketFollowUpEntity.getStaffUser().getEmail(), ticketFollowUpEntity.getStaffUser().getPhone(),
//										ticketFollowUpEntity.getTicket().getMvnoId().intValue(), followTime.intValue(),
//										followUpDateTime, ticketFollowUpEntity.getTicket().getBuId(),ticketFollowUpEntity.getTicket().getStaffUser().getUsername());
//							} else {
//								sendCustomerNotificationForTicketReminder(staffPersonName, caseNumber,
//										ticketFollowUpEntity.getTicket().getStaffUser().getEmail(), ticketFollowUpEntity.getTicket().getStaffUser().getPhone(),
//										ticketFollowUpEntity.getTicket().getMvnoId().intValue(), followTime.intValue(),
//										followUpDateTime, null,ticketFollowUpEntity.getTicket().getStaffUser().getUsername(), aCase.getStaffAdditionalEmail());
//								sendStaffNotificationForTicketReminder(staffPersonName, caseNumber,
//										ticketFollowUpEntity.getStaffUser().getEmail(), ticketFollowUpEntity.getStaffUser().getPhone(),
//										ticketFollowUpEntity.getTicket().getMvnoId().intValue(), followTime.intValue(),
//										followUpDateTime, null,ticketFollowUpEntity.getTicket().getStaffUser().getUsername());
//							}
//							ticketFollowUpEntity.setSendReminderNotification(true);
//							this.ticketFollowUpRepository.save(ticketFollowUpEntity);
//						}
//					}
//
//				}
//			});
//			System.out.println("***** cronJobTimeForReminderTicketFollowUp Ended !!! *****");
//		} catch (Exception ex) {
//			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//		}
//	}

	public void sendStaffNotificationForTicketOverDue(String staffPersonName, String customername, String email, String phone,
												Integer mvnoId, Integer followUpTime, String followUpDateTime,Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForTicketStaff(TemplateNotification templateNotification, String email, String phone,
												String headerName, String caseNumber, Integer mvnoId, Integer followUpTime, String followUpDateTime,
												String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured,Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					TicketFollowUpMessage ticketFollowUpMessage = new TicketFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, caseNumber, staffPersonName, parentStaffPersonName, null,buId);
					ticketFollowUpMessage.setEmailConfigured(isEmailConfigured);
					ticketFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(ticketFollowUpMessage);
//					messageSender.send(ticketFollowUpMessage,RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(ticketFollowUpMessage, TicketFollowUpMessage.class.getSimpleName(),KafkaConstant.TROUBLE_TASK_FOLLOW_UP_OVER_DUE_STAFF));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}

	}

	public void sendParentStaffNotificationForTicketOverDue(String staffPersonName, String caseNumber, String email,
													  String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String parentStaffPersonName,Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [sendParentStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true, true,buId);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true,
							false,buId);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, false,
							true,buId);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForTicketParentStaff(TemplateNotification templateNotification, String email,
													  String phone, String headerName, String caseNumber, Integer mvnoId, Integer followUpTime,
													  String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
													  boolean isSmsConfigured,Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForParentStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					TicketFollowUpMessage ticketFollowUpMessage = new TicketFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, caseNumber, staffPersonName, parentStaffPersonName, null,buId);
					ticketFollowUpMessage.setEmailConfigured(isEmailConfigured);
					ticketFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(ticketFollowUpMessage);
//					messageSender.send(ticketFollowUpMessage, RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(ticketFollowUpMessage,TicketFollowUpMessage.class.getSimpleName(),KafkaConstant.TROUBLE_TASK_FOLLOW_UP_OVER_DUE_PARENT_STAFF));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendCustomerNotificationForTicketReminder(String staffPersonName, String customername, String email,
													String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime,Long buId, String custUsername, String altEmail) {
		String SUBMODULE = getModuleNameForLog() + " [sendCustomerNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE);
			if (optionalTemplate.isPresent()) {
//				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId, custUsername, altEmail);
//				} else if (optionalTemplate.get().isEmailEventConfigured()) {
//					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
//							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
//							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId, custUsername, altEmail);
//				} else if (optionalTemplate.get().isSmsEventConfigured()) {
//					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
//							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
//							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId,custUsername, null);
//				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendReminderNotificationForTicketCustomer(TemplateNotification templateNotification, String email,
													String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
													String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
													boolean isSmsConfigured,Long buId, String custUsername, String altEmail) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForCustomer()] ";
		try {
			if (templateNotification != null) {
//				if (templateNotification.isEmailEventConfigured()) {
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null,buId,custUsername, altEmail);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage, RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(),KafkaConstant.TROUBLE_TASK_FOLLOW_UP_REMINDER_CUSTOMER));
//				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendStaffNotificationForTicketReminder(String staffPersonName, String customername, String email,
												 String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime,Long buId,String custUsername) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
					sendReminderNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId,custUsername);
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendReminderNotificationForTicketStaff(TemplateNotification templateNotification, String email, String phone,
												 String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
												 String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured,Long buId, String custUsername) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (isEmailConfigured) {
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null,buId,custUsername, null);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage, RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(), KafkaConstant.TROUBLE_TASK_FOLLOW_UP_REMINDER_STAFF));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}


}
