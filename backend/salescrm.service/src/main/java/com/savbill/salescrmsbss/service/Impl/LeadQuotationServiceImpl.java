package com.savbill.salescrmsbss.service.Impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.savbill.salescrmsbss.kafka.KafkaMessageData;
import com.savbill.salescrmsbss.kafka.KafkaMessageSender;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.CustPlanMappping;
import com.savbill.salescrmsbss.entity.Email;
import com.savbill.salescrmsbss.entity.Event;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.LeadServiceMapping;
import com.savbill.salescrmsbss.entity.QQuotationDetails;
import com.savbill.salescrmsbss.entity.QTemplateNotification;
import com.savbill.salescrmsbss.entity.QuotationCircuitMapping;
import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.entity.QuotationPODoc;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.entity.TemplateNotification;
import com.savbill.salescrmsbss.entity.pojo.CreateLeadQuotationDTO;
import com.savbill.salescrmsbss.entity.pojo.EmailAuditingDTO;
import com.savbill.salescrmsbss.entity.pojo.QuotationDetailsDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.LeadQuotationWfDTO;
//import com.savbill.salescrmsbss.rabbitMq.MessageSender;
import com.savbill.salescrmsbss.rabbitMq.RabbitMqConstants;
import com.savbill.salescrmsbss.rabbitMq.message.SendLeadQuotationMessage;
import com.savbill.salescrmsbss.repository.CustPlanMapppingRepository;
import com.savbill.salescrmsbss.repository.EmailRepository;
import com.savbill.salescrmsbss.repository.EventRepository;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.LeadServiceMappingRepository;
import com.savbill.salescrmsbss.repository.NotificationTemplateRepository;
import com.savbill.salescrmsbss.repository.QuotationCircuitMappingRepository;
import com.savbill.salescrmsbss.repository.QuotationDetailsRepository;
import com.savbill.salescrmsbss.repository.QuotationPODocRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.LeadQuotationService;
import com.savbill.salescrmsbss.service.NotificationService;
import com.savbill.salescrmsbss.utils.ClientServiceConstant;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.DocumentConstants;
import com.savbill.salescrmsbss.utils.PDFGenerator;
import com.savbill.salescrmsbss.utils.ReportConstants;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class LeadQuotationServiceImpl implements LeadQuotationService {

	private final Logger log = LoggerFactory.getLogger(LeadQuotationServiceImpl.class);

	private String PATH;

	@Autowired
	private QuotationDetailsRepository quotationDetailsRepository;

	@Autowired
	private QuotationCircuitMappingRepository quotationCircuitMappingRepository;

	@Autowired
	private LeadServiceMappingRepository leadServiceMappingRepository;

	@Autowired
	private CustPlanMapppingRepository custPlanMapppingRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private StaffUserRepository staffUserRepo;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private NotificationTemplateRepository templateNotificationRepo;

	@Autowired
	private QuotationPODocRepository quotationPODocRepository;

	@Autowired
	private PDFGenerator pdfGenerator;

	public String getModuleNameForLog() {
		return "[LeadQuotationServiceImpl]";
	}

//	@Autowired
//	private MessageSender messageSender;

	@Autowired
	private KafkaMessageSender kafkaMessageSender;

	@Transactional
	@Override
	public QuotationDetails createLeadQuotationByCircuit(CreateLeadQuotationDTO createLeadQuotationDTO) {
		QuotationDetails quotationDetails = null;
		List<LeadServiceMapping> leadServiceMapList = leadServiceMappingRepository
				.findAllById(createLeadQuotationDTO.getLeadServiceMappingIdList());
		if (leadServiceMapList != null && leadServiceMapList.size() > 0) {
			quotationDetails = new QuotationDetails();
			quotationDetails.setLeadId(leadServiceMapList.get(0).getLeadId());
			quotationDetails.setVersionId(generateQuotationDetailsVersionNumber(leadServiceMapList.get(0).getLeadId()));
			quotationDetails.setIsDeleted(false);
			quotationDetails.setStatus(SalesCrmsConstants.QUOTATION_STATUS_NEW_ACTIVATION);
			quotationDetails.setValidity(createLeadQuotationDTO.getValidity());
			quotationDetails.setValidityUnit(createLeadQuotationDTO.getValidityUnit());
			quotationDetails.setInstallationValidity((createLeadQuotationDTO.getInstallationValidity()));
			quotationDetails.setInstallationUnit(createLeadQuotationDTO.getInstallationUnit());
			quotationDetails = quotationDetailsRepository.save(quotationDetails);
			for (LeadServiceMapping leadServiceMapping : leadServiceMapList) {
				QuotationCircuitMapping qcM = createQuotationCircuitMappingByQuotationAndLeadServiceMapping(
						quotationDetails, leadServiceMapping);
				if (qcM != null)
					quotationDetails.getQuotationCircuitMappingList().add(qcM);
			}
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"leadServiceMapList is not found for leadServiceMappingIdList:", null);
		}
		return quotationDetails;
	}

	@Transactional
	public QuotationCircuitMapping createQuotationCircuitMappingByQuotationAndLeadServiceMapping(
			QuotationDetails quotationDetails, LeadServiceMapping leadServiceMapping) {
		QuotationCircuitMapping quotationCircuitMapping = null;

		Optional<LeadMaster> leadOp = leadMasterRepository.findById(quotationDetails.getLeadId());

		if (leadOp.isPresent()) {

			LeadMaster leadMaster = leadOp.get();

			List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findAllByLeadMasterAndPlanId(leadMaster,
					leadServiceMapping.getPlanId().intValue());

			if (custPlanMapppingList != null && custPlanMapppingList.size()>0) {

				quotationCircuitMapping = new QuotationCircuitMapping();

				if (custPlanMapppingList.get(0).getOfferPrice() != null) {
					quotationCircuitMapping.setOfferPrice(custPlanMapppingList.get(0).getOfferPrice());
				} else {
					quotationCircuitMapping.setOfferPrice(custPlanMapppingList.get(0).getNewAmount());
				}

				quotationCircuitMapping.setTaxAmount(custPlanMapppingList.get(0).getTaxAmount());

				if (quotationDetails != null)
					quotationCircuitMapping.setQuotationDetails(quotationDetails);

				if (leadServiceMapping != null)
					quotationCircuitMapping.setLeadServiceMappingId(leadServiceMapping.getId());

				quotationCircuitMapping = quotationCircuitMappingRepository.save(quotationCircuitMapping);

			} else {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"CustPlanMappping is not for with lead master id and plan id :", null);
			}
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"LeadMaster is not found for ID :" + quotationDetails.getLeadId(), null);
		}

		return quotationCircuitMapping;
	}

	@Override
	public List<QuotationDetailsDTO> findListOfQuotationDetailsByLeadId(Long leadId) {
		List<QuotationDetailsDTO> respList = new ArrayList<>();

		List<QuotationDetails> list = quotationDetailsRepository.findAllByLeadId(leadId);
		if (list != null && list.size() > 0)
			list.sort(Comparator.comparing(QuotationDetails::getCreatedAt));

		Optional<LeadMaster> leadMasterOp = leadMasterRepository.findById(leadId);

		if (leadMasterOp.isPresent()) {
			LeadMaster leadMasterObj = leadMasterOp.get();
			for (int i = list.size()-1; i >= 0; i--) {
				QuotationDetailsDTO dto = new QuotationDetailsDTO();
				QuotationDetails obj = list.get(i);
				if (leadMasterObj.getFirstname() != null)
					if (leadMasterObj.getLastname() != null)
						dto.setOrgName(leadMasterObj.getFirstname() + " " + leadMasterObj.getLastname());
					else
						dto.setOrgName(leadMasterObj.getFirstname());
				else if (leadMasterObj.getLastname() != null)
					dto.setOrgName(leadMasterObj.getLastname());
				else
					dto.setOrgName("");
				dto.setVersionId(obj.getVersionId());
				dto.setQuotationName("Quotation_" + (i+1));
				List<Long> leadServiceMappingIds = new ArrayList<>();
				List<String> services = new ArrayList<>();
				obj.getQuotationCircuitMappingList()
						.forEach(instance -> leadServiceMappingIds.add(instance.getLeadServiceMappingId()));

				for (Long leadServiceId : leadServiceMappingIds) {
					LeadServiceMapping leadServiceMapping = leadServiceMappingRepository.findById(leadServiceId).get();
					services.add(leadServiceMapping.getServiceName());
				}
				dto.setServices(services);
				dto.setCreatedOn(obj.getCreatedAt());
				dto.setLeadId(obj.getLeadId());
				dto.setQuotationDetailId(obj.getId());
				dto.setStatus(obj.getStatus());
				dto.setNextApproveStaffId(obj.getNextApproveStaffId());
				dto.setNextTeamMappingId(obj.getNextTeamMappingId());
				dto.setFinalApproved(obj.getFinalApproved());

				if (obj.getId() != null) {
					List<QuotationPODoc> poDocList = quotationPODocRepository.findAllByQuotationDetailId(obj.getId());
					if (poDocList != null && poDocList.size() > 0) {
						dto.setQuotationPODoc(poDocList.get(0));
					}
				}
				respList.add(dto);
			}
		}
		return respList;
	}

	public Long generateQuotationDetailsVersionNumber(Long leadId) {
		Long versionId;
		List<QuotationDetailsDTO> list = findListOfQuotationDetailsByLeadId(leadId);
		if (list != null && list.size() > 0) {
			List<Long> fieldsIdList = list.stream().map(QuotationDetailsDTO::getVersionId).collect(Collectors.toList());
			if (fieldsIdList != null && fieldsIdList.size() > 0) {
				versionId = Collections.max(fieldsIdList) + 1L;
			} else {
				versionId = 1L;
			}
		} else {
			versionId = 1L;
		}
		return versionId;
	}

	public String getPdfNameWithDate() {
		String localDateString = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern(ReportConstants.REPORT_FINALNAME_FORMAT));
		return ReportConstants.REPORT_FILENAME + "-" + localDateString + ".pdf";
	}

	@Override
	public void sendEmailWithQuotationDetails(EmailAuditingDTO emailDTO) throws IOException {
		// TODO Auto-generated method stub

		Event eventObj = new Event();
		String fileOriginalName = null;
		String filePath = null;
		String subFolderName = null;
		String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";

//		EmailAuditingDTO emailDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(emailPojo,
//				new TypeReference<EmailAuditingDTO>() {
//				});

		System.out.println("emailDTO : " + emailDTO);
		PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.LEAD_QUOTATION_PATH).get(0).getValue();
		// PATH = "E:\\Users\\savbill\\leaddoc\\";
		fileOriginalName = getPdfNameWithDate();

		List<QuotationDetails> quotationList = new ArrayList<>();
		if (emailDTO.getQuotationId() != null) {
			QQuotationDetails qQuotationDetail = QQuotationDetails.quotationDetails;
			BooleanExpression expOfQuotation = qQuotationDetail.isNotNull();
			expOfQuotation = expOfQuotation.and(qQuotationDetail.id.eq(emailDTO.getQuotationId()));
			quotationList = (List<QuotationDetails>) quotationDetailsRepository.findAll(expOfQuotation);
		}

		if (quotationList != null && quotationList.size() > 0)
			subFolderName = quotationList.get(0).getId() + "/";

		filePath = PATH + subFolderName;
		log.debug(SUBMODULE + ":File Path:" + filePath);

		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		File file = pdfGenerator.generatePdfReportForMail(emailDTO.getQuotationId(), filePath+fileOriginalName);

//		boolean isCreated = file.createNewFile();
//		if (!isCreated) {
//			throw new FileNotCreatedException();
//		}
//		MultipartFile file1 = fileUtility.getFileFromArray(fileOriginalName, file);
//
//		if (file1 != null)
//			fileOriginalName = fileUtility.saveFileToServer(file1, filePath);

		Optional<Event> existingEventList = eventRepository
				.findByEventName(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION);

		if (existingEventList.isPresent()) {
			eventObj = existingEventList.get();
		}
		Email emailObj = new Email(emailDTO);
		emailObj.setFileName(fileOriginalName);
		emailObj.setFilePath(filePath);
		StaffUser staffUser = staffUserRepo.findById(Integer.parseInt(String.valueOf(emailObj.getStaffId()))).get();
		List<Email> allEmailObjs = new ArrayList<>();
		for (String email : emailDTO.getCustMailAddresses()) {

			emailObj.setEmailAddress(email);
			emailObj.setCreatedOn(LocalDateTime.now());
			emailObj.setLastModifiedOn(LocalDateTime.now());
			emailObj.setEvent(eventObj);
			// emailObj.setMvnoId(eventObj.getMvnoId());
			emailObj.setEmailContent(emailDTO.getBody());

			if (quotationList != null && quotationList.size() > 0) {
				emailObj.setQuotationDetails(quotationList.get(0));
			}
			emailObj.setStaffId(
					staffUser != null && staffUser.getId() != null ? Long.parseLong(String.valueOf(staffUser.getId()))
							: null);
			emailObj = emailRepository.save(emailObj);
			allEmailObjs.add(emailObj);
		}
//		Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
//				.findByTemplateName(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION);

		QTemplateNotification qTemplate = QTemplateNotification.templateNotification;

		BooleanExpression exp1 = qTemplate.isNotNull();
		exp1 = exp1
				.and(qTemplate.templateName.eq(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION));
		List<TemplateNotification> templateList = (List<TemplateNotification>) templateNotificationRepo.findAll(exp1);

		if (templateList.size() > 0) {
			TemplateNotification obj = templateList.get(0);

			String custName = null;
			List<String> circuits = new ArrayList<String>();
			LeadMaster leadMaster = leadMasterRepository.findById(quotationList.get(0).getLeadId()).get();

			if (obj != null && obj.isEmailEventConfigured() || obj.isSmsEventConfigured()) {
				if (emailObj.getEmailContent() != null && !emailObj.getEmailContent().equalsIgnoreCase("")) {

					obj.setEmailTemplateData(emailObj.getEmailContent());
					obj.setEmailEventConfigured(true);
					obj.setSmsEventConfigured(false);
					obj = templateNotificationRepo.save(obj);

					custName = leadMaster.getFirstname() != null ? leadMaster.getFirstname() : "";
					List<QuotationCircuitMapping> circuitMappingList = quotationCircuitMappingRepository
							.findAllByQuotationDetails(quotationList.get(0));

					if (circuitMappingList != null && circuitMappingList.size() > 0) {
						for (QuotationCircuitMapping circuit : circuitMappingList) {
							Optional<LeadServiceMapping> serviceObj = leadServiceMappingRepository
									.findById(circuit.getLeadServiceMappingId());
							if (serviceObj.isPresent()) {
								circuits.add(serviceObj.get().getServiceName());
							}
						}
					}
				} else {
					obj.setEmailTemplateData(CommonConstants.DEFAULT_LEAD_QUOTATION_EMAIL_TEMPLATE_FORMAT);
					obj.setEmailEventConfigured(true);
					obj.setSmsEventConfigured(false);
					obj = templateNotificationRepo.save(obj);
				}

				notificationService.sendEmailToCustomerWithLeadQuotation(obj, emailDTO.getCustMailAddresses(),
						leadMaster.getMobile(), emailObj.getMessage(), custName, leadMaster.getMvnoId(),
						obj.isEmailEventConfigured(), obj.isSmsEventConfigured(), staffUser.getEmail(),
						fileOriginalName, filePath, circuits,leadMaster.getBuId().intValue());
			}
		}

	}

	public MultipartFile getFileFromArray(String fileName, MultipartFile file) {

		try {
			Integer allowedFileSize = clientServiceSrv.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE, getLoggedInMvnoId().longValue()) != null
					? Integer.parseInt(clientServiceSrv.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE, getLoggedInMvnoId().longValue()).getValue())
					: 2;
			if (file.getSize() > (allowedFileSize * 1024 * 1024))
				throw new RuntimeException(
						"File size limit exceeds. Please provide document within " + allowedFileSize + "MB");
			if (file.getOriginalFilename().equalsIgnoreCase(fileName)) {
				return file;
			}

		} catch (Exception ex) {
			throw ex;
		}
		return null;
	}

	@Override
	public QuotationDetailsDTO assignWorkFlow(Long quotationId, Long staffId, Long buid, Long mvnoId) {
		Optional<QuotationDetails> optionalQuotationDetails = this.quotationDetailsRepository.findById(quotationId);
		QuotationDetails existingQuotationDetails = new QuotationDetails();
		if (optionalQuotationDetails.isPresent()) {
			// Setting values in leadMgmtWfDTO and send to the apigetway for workflow
			existingQuotationDetails = optionalQuotationDetails.get();
			LeadQuotationWfDTO leadQuotationWfDTO = new LeadQuotationWfDTO();
			leadQuotationWfDTO.setBuId(buid);
			leadQuotationWfDTO.setStatus(existingQuotationDetails.getStatus());
			leadQuotationWfDTO.setNextTeamMappingId(null);
			leadQuotationWfDTO.setNextApproveStaffId(null);
			leadQuotationWfDTO.setQuotationId(existingQuotationDetails.getId());
			leadQuotationWfDTO.setMvnoId(mvnoId);
			leadQuotationWfDTO.setCurrentLoggedInStaffId(staffId.intValue());
			leadQuotationWfDTO.setApproveRequest(false);
			leadQuotationWfDTO.setFinalApproved(false);
			SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(leadQuotationWfDTO);
//			messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_LEAD_QUOTATION_WF);
			kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
		}
		return new QuotationDetailsDTO(existingQuotationDetails);
	}

	@Override
	public void updateLeadQuotationApprover(LeadQuotationWfDTO leadQuotationWfDTO) {
		QuotationDetails existingQuotationDetails = this.quotationDetailsRepository
				.findById(leadQuotationWfDTO.getQuotationId()).get();
		if (leadQuotationWfDTO.getFinalApproved()) {
			existingQuotationDetails.setNextApproveStaffId(null);
			existingQuotationDetails.setStatus(SalesCrmsConstants.ACTIVE);
		} else
			existingQuotationDetails.setNextApproveStaffId(leadQuotationWfDTO.getNextApproveStaffId());
		existingQuotationDetails.setNextTeamMappingId(leadQuotationWfDTO.getNextTeamMappingId());
		existingQuotationDetails.setFinalApproved(leadQuotationWfDTO.getFinalApproved());
		if (leadQuotationWfDTO.getStatus() != null)
			existingQuotationDetails.setStatus(leadQuotationWfDTO.getStatus());
		existingQuotationDetails.setId(leadQuotationWfDTO.getQuotationId());

		this.quotationDetailsRepository.save(existingQuotationDetails);

	}

	@Transactional
	public void updateLeadQuotationAssignApproverInfo(LeadQuotationWfDTO leadQuotationWfDTO) {

		QuotationDetails existingQuotationDetails = this.quotationDetailsRepository
				.findById(leadQuotationWfDTO.getQuotationId()).get();
		existingQuotationDetails.setNextApproveStaffId(leadQuotationWfDTO.getNextApproveStaffId());
		existingQuotationDetails.setNextTeamMappingId(leadQuotationWfDTO.getNextTeamMappingId());
		quotationDetailsRepository.save(existingQuotationDetails);
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
