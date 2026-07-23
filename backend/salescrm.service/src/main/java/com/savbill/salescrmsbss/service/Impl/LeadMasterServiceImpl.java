package com.savbill.salescrmsbss.service.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.kafka.KafkaMessageData;
import com.savbill.salescrmsbss.kafka.KafkaMessageSender;
import com.savbill.salescrmsbss.rabbitMq.message.*;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.rabbitMq.message.*;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.utils.ApplicationLogger;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.savbill.salescrmsbss.entity.pojo.ChargePojo;
import com.savbill.salescrmsbss.entity.pojo.CreditDocumentPojo;
import com.savbill.salescrmsbss.entity.pojo.CustChargeDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.CustomerDocDetailsDTO;
import com.savbill.salescrmsbss.entity.pojo.CustomerDocDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.DebitDocumentPojo;
import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;
import com.savbill.salescrmsbss.entity.pojo.LeadMasterPojo;
import com.savbill.salescrmsbss.entity.pojo.LeadSourcePojo;
import com.savbill.salescrmsbss.entity.pojo.LinkAcceptanceDTO;
import com.savbill.salescrmsbss.entity.pojo.PostpaidPlanChargePojo;
import com.savbill.salescrmsbss.entity.pojo.PostpaidPlanPojo;
import com.savbill.salescrmsbss.entity.pojo.Productplanmappingdto;
import com.savbill.salescrmsbss.entity.pojo.SearchLeadByBuidDTO;
import com.savbill.salescrmsbss.entity.pojo.SendLeadDocConvertPojo;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import com.savbill.salescrmsbss.helper.LeadNotesDto;
import com.savbill.salescrmsbss.helper.LeadRejectDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
//import com.savbill.salescrmsbss.rabbitMq.MessageSender;
import com.savbill.salescrmsbss.service.AbstractService;
import com.savbill.salescrmsbss.service.CafNoSequenceService;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.LeadAuditService;
import com.savbill.salescrmsbss.service.LeadDocDetailsService;
import com.savbill.salescrmsbss.service.LeadMasterSequenceService;
import com.savbill.salescrmsbss.service.LeadMasterService;
import com.savbill.salescrmsbss.service.TeamUserMappingService;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;

@Service
public class LeadMasterServiceImpl extends AbstractService<LeadMaster, Long> implements LeadMasterService {

	public static final String MODULE = "[LeadMasterServiceImpl]";

	public static final String LEAD_REOPEN_IN_DAYS = "reOpenLeadInDays";

	private final Logger logger = LoggerFactory.getLogger(LeadMasterServiceImpl.class);

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private RecordPaymentRepository recordPaymentRepository;

	@Autowired
	private CustomerLedgerRepository customerLedgerRepository;

	@Autowired
	private CustPlanMapppingRepository custPlanMapppingRepository;

	@Autowired
	private CustomerAddressRepository customerAddressRepository;

	@Autowired
	private DebitDocumentRepository debitDocumentRepository;

	@Autowired
	private CreditDocumentRepository creditDocumentRepository;

	@Autowired
	private CustChargeDetailsRepository custChargeDetailsRepository;

	@Autowired
	private CustomerDocDetailsRepository customerDocDetailsRepository;

	@Autowired
	private CustMacMapppingRepository custMacMapppingRepository;

	@Autowired
	private CustLedgerDtlsRepository custLedgerDtlsRepository;

	@Autowired
	private LeadNotesRepository leadNotesRepository;

	@Autowired
	private LeadAuditService leadAuditService;

	@Autowired
	private StaffUserRepository staffUserRepository;

//	@Autowired
//	private MessageSender messageSender;

	@Autowired
	private LeadSourceRepository leadSourceRepository;

	@Autowired
	private LeadSubSourceRepository leadSubSourceRepository;

	@Autowired
	private LeadDocDetailsService leadDocDetailsService;

	@Autowired
	private StateRepository stateRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private LeadMasterSequenceService leadMasterSequenceService;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private MvnoRepository mvnoRepository;

	@Autowired
	private LeadFollowUpRepository leadFollowUpRepository;

	@Autowired
	private TeamUserMappingService teamUserMappingService;

	@Autowired
	private CafNoSequenceService cafNoSequenceService;

	@Autowired
	private LeadGeneralAuditRepository leadGeneralAuditRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private LeadServiceMappingRepository leadServiceMappingRepository;

	@Autowired
	private QuotationDetailsRepository quotationDetailsRepository;

	@Autowired
	private PostpaidPlanRepository postpaidPlanRepository;

	@Autowired
	private ChargeRepository chargeRepository;

	@Autowired
	private PostpaidPlanChargeRepository postpaidPlanChargeRepository;

	@Autowired
	private ProductPlanMappingRepository productPlanMappingRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	ServiceAreaRepository serviceAreaRepository;

	@Autowired
    BranchRepository branchRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	PlanGroupRepository planGroupRepository;
	@Autowired
	RejectReasonRepository rejectReasonRepository;
	@Autowired
	private QuotationCircuitMappingRepository quotationCircuitMappingRepository;
	@Autowired
	private KafkaMessageSender kafkaMessageSender;

	@Override
	@Transactional
	public LeadMasterPojo save(LeadMasterPojo leadMasterPojo, Long mvnoId, Long buId, Long staffId) {
		String SUBMODULE = MODULE + "save()";
		if(buId!=null){
		Optional<BusinessUnit> businessUnitOp = businessUnitRepository.findById(buId);
		if(businessUnitOp.isPresent()){
			BusinessUnit businessUnit = businessUnitOp.get();
			if(businessUnit!= null && businessUnit.getPlanBindingType()!= null) {
				if (businessUnit.getPlanBindingType().equalsIgnoreCase("On-Demand")) {
					if (leadMasterPojo.getIsLeadQuickInv() != null && leadMasterPojo.getIsLeadQuickInv())
						leadMasterPojo.setLeadIdentity("Project");
					else
						leadMasterPojo.setLeadIdentity("Enterprise");
				} else {
					leadMasterPojo.setIsLeadQuickInv(false);
					leadMasterPojo.setLeadIdentity("Retail");
				}
			}
		}
		}
//		if(mvnoId!=null){
//			leadMasterPojo.setMvnoId(mvnoId);
//		}


		if(leadMasterPojo.getBranchId() == null) {
			Branch branch = getBranchByMvno(mvnoId);
			leadMasterPojo.setBranchId(branch.getId());
			leadMasterPojo.setBranchName(branch.getName());
			leadMasterPojo.setLeadBranchId(branch.getId());
		}
		try {

//			if(leadMasterPojo.getServiceareaid() != null) {
//				Optional<ServiceArea> serviceArea = serviceAreaRepository.findById(leadMasterPojo.getServiceareaid());
//				if(serviceArea.isPresent()) {
//					mvnoId = Long.valueOf(serviceArea.get().getMvnoId());
//				}
//			}
			if(leadMasterPojo.getMvnoId()!=null){
				leadMasterPojo.setMvnoId(leadMasterPojo.getMvnoId());
			}else {
				if (mvnoId != null) {
					Optional<Mvno> optionalMvno = this.mvnoRepository.findById(mvnoId);
					if (!optionalMvno.isPresent()) {
						throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
								"MVNO is not set for Sales CRM module. Please configure that.", null);

					}
					if(staffId==null) {
						Optional<StaffUser> staffUser = staffUserRepository.findByUsername(optionalMvno.get().getUsername());
						if (staffUser.isPresent()) {
							staffId = Long.valueOf(staffUser.get().getId());
						}
					}
				}
			}
			if(leadMasterPojo!= null && leadMasterPojo.getIsLeadQuickInv()== null)
				leadMasterPojo.setIsLeadQuickInv(false);
			LeadMaster leadMaster = new LeadMaster(leadMasterPojo, mvnoId, buId, staffId);
			leadMaster.setLeadStatus("Inquiry");
//			if (leadMaster.getFeasibility() != null && leadMaster.getFeasibility().equalsIgnoreCase("N/A"))
//				leadMaster.setFeasibilityRequired("NA");
//			else
//			if(leadMaster.getFeasibility()!= null)
				leadMaster.setFeasibilityRequired(leadMaster.getFeasibility());

			StaffUser creatredBy =staffUserRepository.findById(staffId.intValue()).orElse(null);
			if(creatredBy!=null)
			{
				leadMaster.setCreatedByName(creatredBy.getUsername());
			}else{
				leadMaster.setCreatedByName("-");
			}
//			leadMaster.setCreatedByName(String.valueOf(staffUserRepository.findByName(staffId));
			if (leadMasterPojo.getPaymentDetails() != null) {
				leadMaster.setPaymentDetails(
						this.recordPaymentRepository.save(new RecordPayment(leadMasterPojo.getPaymentDetails())));
			}
			if (leadMasterPojo.getCustLeger() != null) {
				leadMaster.setCustLeger(
						this.customerLedgerRepository.save(new CustomerLedger(leadMasterPojo.getCustLeger())));
			}
			if (leadMasterPojo.getParentExperience() != null) {
				leadMaster.setParentExperience(leadMasterPojo.getParentExperience());
			}

			LeadMaster savedLeadMaster = this.leadMasterRepository.save(leadMaster);
			LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
			leadMgmtWfDTO.setIsLeadFromCWSC(leadMasterPojo.getIsLeadFromCWSC());
			leadMgmtWfDTO.setBuId(savedLeadMaster.getBuId());
			leadMgmtWfDTO.setFirstname(savedLeadMaster.getFirstname());
			leadMgmtWfDTO.setStatus("Inquiry");
			leadMgmtWfDTO.setNextTeamMappingId(null);
			leadMgmtWfDTO.setNextApproveStaffId(null);
			leadMgmtWfDTO.setId(savedLeadMaster.getId());
			leadMgmtWfDTO.setMvnoId(savedLeadMaster.getMvnoId());
			leadMgmtWfDTO.setServiceareaid(savedLeadMaster.getServiceareaid());
			leadMgmtWfDTO.setCurrentLoggedInStaffId(staffId.intValue());
			SendSaveLeadData sendSaveLeadData = new SendSaveLeadData(leadMgmtWfDTO);
			// save All LeadMaster Lists Entity

			savedLeadMaster = saveAllLeadMasterListsEntity(leadMaster, savedLeadMaster);

//			messageSender.send(sendSaveLeadData, RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA);
			// save audit entry

			saveLeadAudit(savedLeadMaster, staffId);

			LeadMasterPojo updatedLeadMasterPojo = new LeadMasterPojo(savedLeadMaster);

			try {
				// send message
				List<LeadDocDetailsDTO> leadDocDetailsDTOList = new ArrayList<LeadDocDetailsDTO>();
				List<LeadDocDetails> leadDocDetailsList = this.leadDocDetailsService
						.findDocsByLeadId(savedLeadMaster.getId());
				if(leadDocDetailsList!= null && leadDocDetailsList.size() >0) {
					for (LeadDocDetails leadDocDetails : leadDocDetailsList) {
						leadDocDetailsDTOList.add(new LeadDocDetailsDTO(leadDocDetails));
					}
					updatedLeadMasterPojo.setLeadDocDetailsList(leadDocDetailsDTOList);
				}
				if(savedLeadMaster.getLeadSource()!= null && savedLeadMaster.getLeadSource().getId() != null) {
					LeadSourcePojo leadSourcePojo = new LeadSourcePojo(
							this.leadSourceRepository.findById(savedLeadMaster.getLeadSource().getId()).get());
					updatedLeadMasterPojo.setLeadSourcePojo(leadSourcePojo);
				}
				updatedLeadMasterPojo.setCreatedBy(savedLeadMaster.getCreatedBy());
				updatedLeadMasterPojo.setCreatedByName(savedLeadMaster.getCreatedByName());
				updatedLeadMasterPojo.setNextApproveStaffId(savedLeadMaster.getNextApproveStaffId());
				updatedLeadMasterPojo.setStatus("Inquiry");
				if(savedLeadMaster != null && savedLeadMaster.getIsLeadQuickInv()!= null)
					updatedLeadMasterPojo.setIsLeadQuickInv(savedLeadMaster.getIsLeadQuickInv() ==1?true:false);
				LeadMasterPojoMessage leadMasterPojoMessage = new LeadMasterPojoMessage(updatedLeadMasterPojo);
				leadMasterPojoMessage.setIsLeadFromCWSC(leadMasterPojo.getIsLeadFromCWSC());
//				this.messageSender.send(leadMasterPojoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER);
				kafkaMessageSender.send(new KafkaMessageData(leadMasterPojoMessage,LeadMasterPojoMessage.class.getSimpleName()));

			} catch (Exception e) {
				logger.error("Error While send Lead Message : ", e.getMessage());
			}
			logger.info("Lead has been created successfully");
			return updatedLeadMasterPojo;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public Branch getBranchByMvno(Long mvnoId) {
		List<Branch> branches = branchRepository.findAllByIsDeletedFalseAndMvnoId(mvnoId.intValue());
		if(!CollectionUtils.isEmpty(branches)) {
			return branches.get(0);
		} else {
			throw new RuntimeException("Branch Not Found For Mvno..!");
		}
	}
//	public void saveLeadAudit(LeadMaster leadMaster,Long staffId) {
//		StaffUser staffUser = this.staffUserRepository.findById(staffId.intValue()).get();
//		String auditName = staffUser.getFirstname() + " created Lead for Customer " + leadMaster.getFirstname()
//				+ " " + leadMaster.getLastname() + " on "
//				+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(leadMaster.getCreatedOn());
//		LeadAudit leadAudit = new LeadAudit();
//		leadAudit.setName(auditName);
//		leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
//		leadAudit.setAuditName("Lead has been created");
//		leadAudit.setLeadMasterId(leadMaster.getId());
//		this.leadAuditService.save(leadAudit);
//	}
	public void saveLeadAudit(LeadMaster leadMaster, Long staffId) {
		String SUBMODULE = MODULE + "saveLeadAudit()";
		try {
			if (staffId != null) {
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffId.intValue());
				if (optionalStaffUser.isPresent()) {
					String customername = "";
					StaffUser staffUser = optionalStaffUser.get();
					if (!StringUtils.isEmpty(leadMaster.getLastname()))
						customername += leadMaster.getFirstname() + " " + leadMaster.getLastname();
					else
						customername += leadMaster.getFirstname();
					String auditName = staffUser.getFirstname() + " created Lead for Customer " + customername + " on "
							+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(leadMaster.getCreatedOn());
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName("Lead has been created");
					leadAudit.setLeadMasterId(leadMaster.getId());
					this.leadAuditService.save(leadAudit);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void saveLeadAuditforEditLead(LeadMaster leadMaster, Long staffId) {
		String SUBMODULE = MODULE + "saveLeadAuditforEditLead()";
		try {
			if (staffId != null) {
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffId.intValue());
				if (optionalStaffUser.isPresent()) {
					String customername = "";
					StaffUser staffUser = optionalStaffUser.get();
					if (!StringUtils.isEmpty(leadMaster.getLastname()))
						customername += leadMaster.getFirstname() + " " + leadMaster.getLastname();
					else
						customername += leadMaster.getFirstname();
					String auditName = staffUser.getFirstname() + " update Lead for Customer " + customername + " on "
							+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(LocalDateTime.now());
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName("Lead has been updated");
					leadAudit.setLeadMasterId(leadMaster.getId());
					this.leadAuditService.save(leadAudit);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public LeadMasterPojo findById(Long leadMasterId) {

		Optional<LeadMaster> leadMaster = this.leadMasterRepository.findById(leadMasterId);
		if (leadMaster.isPresent()) {
			LeadMaster lead = leadMaster.get();
			LeadMasterPojo pojo = new LeadMasterPojo(lead);
			if (lead != null && lead.getNextApproveStaffId() != null) {
				Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
				if (optionalStaffUser.isPresent()) {
					StaffUser staffUser = optionalStaffUser.get();
					if (staffUser != null && staffUser.getUsername() != null) {
						pojo.setAssigneeName(staffUser.getUsername());
					}
				}
			}
			return pojo;
		} else {
			return null;
		}
	}

	@Override
	public void deleteLeadMaster(Long leadMasterId) {
		String SUBMODULE = MODULE + "deleteLeadMaster()";
		try {
			LeadMaster leadMasterEntity = this.leadMasterRepository.findById(leadMasterId).get();
			if (Objects.nonNull(leadMasterEntity)) {
				leadMasterEntity.setDeleted(true);
				this.leadMasterRepository.save(leadMasterEntity);
				// add audit log
				if (leadMasterEntity.getCreatedBy() != null) {
					Optional<StaffUser> optionalStaffUser = this.staffUserRepository
							.findById(Integer.parseInt(leadMasterEntity.getCreatedBy()));
					if (optionalStaffUser.isPresent()) {
						String customername = "";
						StaffUser staffUser = optionalStaffUser.get();
						if (!StringUtils.isEmpty(leadMasterEntity.getLastname()))
							customername += leadMasterEntity.getFirstname() + " " + leadMasterEntity.getLastname();
						else
							customername += leadMasterEntity.getFirstname();
						String auditName = staffUser.getFirstname() + " delete Lead for Customer " + customername
								+ " on "
								+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(LocalDateTime.now());
						LeadAudit leadAudit = new LeadAudit();
						leadAudit.setName(auditName);
						leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
						leadAudit.setAuditName("Lead has been created");
						leadAudit.setLeadMasterId(leadMasterEntity.getId());
						this.leadAuditService.save(leadAudit);
					}
				}
				logger.info("Lead has been deleted successfully: " + leadMasterEntity.getFirstname());
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public void validateRequest(LeadMasterPojo leadMasterPojo, Integer operation) {

		if (leadMasterPojo == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Required object is not set",
					null);
		}

		if (leadMasterPojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
			if (leadMasterPojo.getId() != null)
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"Id should not be present in the JSON body.", null);
		}

		if (leadMasterPojo != null && leadMasterPojo.getFirstname().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter firstname.",
					null);
		}

		if (leadMasterPojo != null && leadMasterPojo.getLeadNo().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter leadNo.", null);
		}

		if (leadMasterPojo != null && leadMasterPojo.getMobile().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter mobileno.",
					null);
		}

		if (leadMasterPojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
				|| operation.equals(CommonConstants.OPERATION_DELETE)) && leadMasterPojo.getId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.",
					null);
		}
	}

	public LeadMaster saveAllLeadMasterListsEntity(LeadMaster leadMaster, LeadMaster savedLeadMaster) {
		String SUBMODULE = MODULE + "saveAllLeadMasterListsEntity()";
		try {
			String plangroup_name;
			if (savedLeadMaster.getPlanMappingList() != null && savedLeadMaster.getPlanMappingList().size() > 0) {

				if(savedLeadMaster.getPlangroupid()!=null){
					plangroup_name=planGroupRepository.findAllByPlanGroupId(savedLeadMaster.getPlangroupid());
				} else {
                    plangroup_name = null;
                }
                leadMaster.getPlanMappingList().forEach(custPlanMapping -> {
					custPlanMapping.setLeadMaster(savedLeadMaster);
					custPlanMapping.setPlanName(plangroup_name);
					custPlanMapping.setPlangroupid(leadMaster.getPlangroupid());
				});
				savedLeadMaster
						.setPlanMappingList(this.custPlanMapppingRepository.saveAll(leadMaster.getPlanMappingList()));
			} else {
                plangroup_name = null;
            }

            // save addresslist
			if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
				leadMaster.getAddressList().forEach(custAddress -> custAddress.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setAddressList(this.customerAddressRepository.saveAll(leadMaster.getAddressList()));
			}

			// save debitDocList
			if (leadMaster.getDebitDocList() != null && leadMaster.getDebitDocList().size() > 0) {
				leadMaster.getDebitDocList().forEach(debitDoc -> debitDoc.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setDebitDocList(this.debitDocumentRepository.saveAll(leadMaster.getDebitDocList()));
			}

			// save creditDocuments
			if (leadMaster.getCreditDocuments() != null && leadMaster.getCreditDocuments().size() > 0) {
				leadMaster.getCreditDocuments().forEach(creditDoc -> creditDoc.setLeadMaster(savedLeadMaster));
				savedLeadMaster
						.setCreditDocuments(this.creditDocumentRepository.saveAll(leadMaster.getCreditDocuments()));
			}

			// save overChargeList
			if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
				leadMaster.getOverChargeList().forEach(overcharge -> overcharge.setLeadMaster(savedLeadMaster));
				savedLeadMaster
						.setOverChargeList(this.custChargeDetailsRepository.saveAll(leadMaster.getOverChargeList()));
			}

			// save indiChargeList
			if (leadMaster.getIndiChargeList() != null && leadMaster.getIndiChargeList().size() > 0) {
				leadMaster.getIndiChargeList().forEach(indicharge -> indicharge.setLeadMaster(savedLeadMaster));
				savedLeadMaster
						.setIndiChargeList(this.custChargeDetailsRepository.saveAll(leadMaster.getIndiChargeList()));
			}

			// save custDocList
			if (leadMaster.getCustDocList() != null && leadMaster.getCustDocList().size() > 0) {
				leadMaster.getCustDocList().forEach(custDoc -> custDoc.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setCustDocList(this.customerDocDetailsRepository.saveAll(leadMaster.getCustDocList()));
			}

			// save custMacMapppingList
			if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
				leadMaster.getCustMacMapppingList()
						.forEach(custMacMapping -> custMacMapping.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setCustMacMapppingList(
						this.custMacMapppingRepository.saveAll(leadMaster.getCustMacMapppingList()));
			}

			// save ledgerDtls
			if (leadMaster.getLedgerDtls() != null && leadMaster.getLedgerDtls().size() > 0) {
				leadMaster.getLedgerDtls().forEach(ledgerDtls -> ledgerDtls.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setLedgerDtls(this.custLedgerDtlsRepository.saveAll(leadMaster.getLedgerDtls()));
			}


			return savedLeadMaster;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	@Override
	public List<LeadMasterPojo> findByMobileNo(Long mvnoId, List<Long> buId, String mobileNo) {
		String SUBMODULE = MODULE + "findByMobileNo()";
		try {
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			List<LeadMaster> leadMasterList = null;
			QLeadMaster qLeadMaster = QLeadMaster.leadMaster;
			BooleanExpression booleanExpression ;

			if ((buId.size() == 0 || buId.isEmpty() || buId == null) && (mvnoId != null)) {
				booleanExpression = qLeadMaster.isNotNull().and(qLeadMaster.mobile.eq(mobileNo)).and(qLeadMaster.mvnoId.eq(mvnoId));
				leadMasterList = IterableUtils.toList(this.leadMasterRepository.findAll(booleanExpression));
			} else {
				booleanExpression = qLeadMaster.isNotNull().and(qLeadMaster.mobile.eq(mobileNo)).and(qLeadMaster.mvnoId.eq(mvnoId)).and(qLeadMaster.buId.in((buId)));
				leadMasterList = IterableUtils.toList(this.leadMasterRepository.findAll());
			}
			leadMasterList.forEach(data -> leadMasterPojoList.add(new LeadMasterPojo(data)));
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = this.staffUserRepository
							.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
			});
			return leadMasterPojoList;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

//	@Override
//	public Page<LeadMasterPojo> search(PaginationRequestDTO paginationRequestDTO,Long mvnoid,Long buId) {
//		String SUBMODULE = MODULE + "search()";
//		try {
//			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
//					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//					paginationRequestDTO.getSortOrder());
//			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
//				String filterValue = paginationRequestDTO.getFilters().get(0).getFilterValue();
//				if(mvnoid == 1) {
//					switch (paginationRequestDTO.getFilters().get(0).getFilterColumn()) {
//						case "name":
//							return this.leadMasterRepository.searchEntity(filterValue, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "mobile":
//							return this.leadMasterRepository.findByMobileContainingAndIsDeleted(filterValue, false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "status":
//							return this.leadMasterRepository.findByLeadStatusAndIsDeleted(filterValue, false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "createdBy":
//							return this.leadMasterRepository.findByCreatedByAndIsDeleted(filterValue, false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "lastUpdateOn":
//							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//							String fromDate = filterValue + " 00:00:01";
//							String toDate = filterValue + " 23:59:59";
//							return this.leadMasterRepository
//									.searchLastModifiedOn(LocalDatpoeTime.parse(fromDate, formatter),
//											LocalDateTime.parse(toDate, formatter), pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						default:
//							break;
//					}
//				}else{
//					switch (paginationRequestDTO.getFilters().get(0).getFilterColumn()) {
//						case "name":
//							return this.leadMasterRepository.searchEntity(filterValue, mvnoid,pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "mobile":
//							return this.leadMasterRepository.findByMobileContainingAndMvnoIdAndIsDeleted(filterValue,mvnoid, false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "status":
//							return this.leadMasterRepository.findByLeadStatusAndMvnoIdAndIsDeleted(filterValue, mvnoid,false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "createdBy":
//							return this.leadMasterRepository.findByCreatedByAndMvnoIdAndIsDeleted(filterValue, mvnoid,false, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						case "lastUpdateOn":
//							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//							String fromDate = filterValue + " 00:00:01";
//							String toDate = filterValue + " 23:59:59";
//							return this.leadMasterRepository
//									.searchLastModifiedOnAndMvnoId(LocalDateTime.parse(fromDate, formatter),
//											LocalDateTime.parse(toDate, formatter),mvnoid, pageRequest)
//									.map(data -> new LeadMasterPojo(data));
//						default:
//							break;
//					}
//				}
//			}
//			LeadMaster leadMaster = new LeadMaster();
//			leadMaster.setDeleted(false);
//			Page<LeadMasterPojo> pojoPageList =  this.leadMasterRepository.findAll(Example.of(leadMaster), pageRequest)
//					.map(data -> new LeadMasterPojo(data));
//			pojoPageList.getContent().forEach(lead->{
//		        if(lead != null && lead.getNextApproveStaffId() != null) {
//		        	StaffUser staffUser = staffUserRepository.getOne(lead.getNextApproveStaffId());
//		        	if(staffUser != null && staffUser.getUsername() != null) {
//			        	lead.setAssigneeName(staffUser.getUsername());
//		        	}
//		        }
//		    });
//			return pojoPageList;
//		} catch (Exception ex) {
//			logger.error(SUBMODULE + ex.getMessage(), ex);
//			throw ex;
//		}
//	}

	@Override
	public LeadNotesDto saveNotes(LeadNotesDto leadNotesDto, Long staffId) {
		String SUBMODULE = MODULE + "saveNotes()";
		try {
			LeadNotes leadNotes = new LeadNotes(leadNotesDto, staffId);
			StaffUser createdby =staffUserRepository.findById(staffId.intValue()).orElse(null);
			if(createdby!=null)
			{
				leadNotes.setCreatedByName(createdby.getUsername());
			}else{
				leadNotes.setCreatedByName("-");
			}

			// save audit for lead notes
			if (staffId != null) {
				Optional<StaffUser> optionalStaffuser = this.staffUserRepository.findById(staffId.intValue());
				if (optionalStaffuser.isPresent()) {
					StaffUser staffUser = optionalStaffuser.get();
					String auditName = staffUser.getFirstname() + " added lead note in Lead.Note: "
							+ leadNotesDto.getNotes() + ".";
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName("Lead Note Added");
					leadAudit.setLeadMasterId(leadNotesDto.getLeadMasterId());
					this.leadAuditService.save(leadAudit);
				}
			}
			logger.info("LeadNote has been added successfully");
			return new LeadNotesDto(this.leadNotesRepository.save(leadNotes));
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public LeadMasterPojo update(LeadMasterPojo leadMasterPojo) {

		LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMasterPojo.getId()).get();
		LeadMaster leadMaster = new LeadMaster(leadMasterPojo, exstingLeadMaster.getMvnoId(),
				exstingLeadMaster.getBuId(), null);
		if (leadMasterPojo.getPaymentDetails() != null) {
			leadMaster.setPaymentDetails(
					this.recordPaymentRepository.save(new RecordPayment(leadMasterPojo.getPaymentDetails())));
		}
		if (leadMasterPojo.getCustLeger() != null) {
			leadMaster.setCustLeger(
					this.customerLedgerRepository.save(new CustomerLedger(leadMasterPojo.getCustLeger())));
		}
		if (leadMasterPojo.getParentExperience() != null) {
			leadMaster.setParentExperience(leadMasterPojo.getParentExperience());
		}
		//ANG-6683 fixes please not write below three lines out side condition
		if (leadMaster.getIsCustCaf().equalsIgnoreCase("yes")){
			leadMaster.setCstatus("New Activation");
			leadMaster.setCafConvertedStaffId(leadMasterPojo.getApproveStaffId());
			leadMaster.setCafConvertedDate(LocalDate.now());
		}

		leadMaster.setCreatedBy(exstingLeadMaster.getCreatedBy());
		leadMaster.setCreatedByName(exstingLeadMaster.getCreatedByName());
		leadMaster.setCreatedOn(exstingLeadMaster.getCreatedOn());
		leadMaster.setFeasibilityRequired(leadMasterPojo.getFeasibility());
		LeadMaster savedLeadMaster = this.leadMasterRepository.save(leadMaster);
		// delete extsting entry
		deleteLeadMasterEntityList(savedLeadMaster);
		// save All LeadMaster Lists Entity
		savedLeadMaster.setPlanMappingList(leadMaster.getPlanMappingList());

		savedLeadMaster = saveAllLeadMasterListsEntity(leadMaster, savedLeadMaster);
		saveLeadAuditforEditLead(savedLeadMaster, Long.parseLong(exstingLeadMaster.getCreatedBy()));
		if (leadMasterPojo.getIsCustomerCafeIsUpdated() != null
				&& leadMasterPojo.getIsCustomerCafeIsUpdated() == true) {
			List<LeadDocDetails> leadDodcList = leadDocDetailsService.findDocsByLeadId(savedLeadMaster.getId());
			if (leadDodcList != null && leadDodcList.size() > 0 && leadMasterPojo.getCustomerId() != null) {
				SendLeadDocConvertPojo pojoData = new SendLeadDocConvertPojo();
				pojoData.setCustomerDocDetailsDTOList(
						convertLeadDocToCustDocList(leadDodcList, leadMasterPojo.getCustomerId()));
//				messageSender.send(pojoData, RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT);
				kafkaMessageSender.send(new KafkaMessageData(pojoData, SendLeadDocConvertPojo.class.getSimpleName()));
			}
		}
		LeadMasterPojo updatedLeadMasterPojo = new LeadMasterPojo(savedLeadMaster);
		try {
			// send message
			List<LeadDocDetailsDTO> leadDocDetailsDTOList = new ArrayList<LeadDocDetailsDTO>();
			List<LeadDocDetails> leadDocDetailsList = this.leadDocDetailsService
					.findDocsByLeadId(savedLeadMaster.getId());
			for (LeadDocDetails leadDocDetails : leadDocDetailsList) {
				leadDocDetailsDTOList.add(new LeadDocDetailsDTO(leadDocDetails));
			}
			// updatedLeadMasterPojo.setStatus("Inquiry");

			updatedLeadMasterPojo.setLeadDocDetailsList(leadDocDetailsDTOList);
			updatedLeadMasterPojo.setCreatedBy(savedLeadMaster.getCreatedBy());
			updatedLeadMasterPojo.setCreatedByName(savedLeadMaster.getCreatedByName());
			LeadSourcePojo leadSourcePojo = convertLeadSourceEntityToLeadSourcePojo(
					leadSourceRepository.findById(leadMasterPojo.getLeadSourceId()).orElse(null));
			updatedLeadMasterPojo.setLeadSourcePojo(leadSourcePojo);
			updatedLeadMasterPojo.setLeadSourceName(leadSourcePojo.getLeadSourceName());
			LeadMasterPojoMessage leadMasterPojoMessage = new LeadMasterPojoMessage(updatedLeadMasterPojo);
//			this.messageSender.send(leadMasterPojoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER);
			kafkaMessageSender.send(new KafkaMessageData(leadMasterPojoMessage,LeadMasterPojoMessage.class.getSimpleName()));
			System.out.println("message sent");
		} catch (Exception e) {
			logger.error("Error While send Lead Message : ", e.getMessage());
		}
		return updatedLeadMasterPojo;
	}

	public List<CustomerDocDetailsDTO> convertLeadDocToCustDocList(List<LeadDocDetails> leadDodcList,
			Integer customerId) {
		List<CustomerDocDetailsDTO> customerDocDetailsDTOList = new ArrayList<CustomerDocDetailsDTO>();
		if (leadDodcList != null && leadDodcList.size() > 0) {
			for (LeadDocDetails leadDocDetails : leadDodcList) {
				CustomerDocDetailsDTO customerDocDetailsDTO = new CustomerDocDetailsDTO();
				customerDocDetailsDTO.setDocId(leadDocDetails.getDocId());
				customerDocDetailsDTO.setCustId(customerId);
				customerDocDetailsDTO.setDocType(leadDocDetails.getDocType());
				customerDocDetailsDTO.setDocSubType(leadDocDetails.getDocSubType());
				customerDocDetailsDTO.setRemark(leadDocDetails.getRemark());
				customerDocDetailsDTO.setMode(leadDocDetails.getMode());
				customerDocDetailsDTO.setDocStatus(leadDocDetails.getDocStatus());
				customerDocDetailsDTO.setFilename(leadDocDetails.getFilename());
				customerDocDetailsDTO.setUniquename(leadDocDetails.getUniquename());
				customerDocDetailsDTO.setIsDelete(leadDocDetails.getIsDelete());
				customerDocDetailsDTO.setDocumentNumber(leadDocDetails.getDocumentNumber());
				customerDocDetailsDTO.setStartDateAsString(leadDocDetails.getStartDate().toString());
				customerDocDetailsDTO.setEndDateAsString(leadDocDetails.getEndDate().toString());
				if (leadDocDetails.getLeadMaster() != null) {
					customerDocDetailsDTO.setLeadId(leadDocDetails.getLeadMaster().getId());
				}
				if (leadDocDetails.getLeadMaster() != null
						&& leadDocDetails.getLeadMaster().getNextApproveStaffId() != null) {
					customerDocDetailsDTO.setCreatedById(leadDocDetails.getLeadMaster().getNextApproveStaffId());
				}
				if (leadDocDetails.getLeadMaster() != null && leadDocDetails.getLeadMaster().getCreatedBy() != null) {
					customerDocDetailsDTO.setCreatedByName(leadDocDetails.getLeadMaster().getCreatedBy());
				}
				if (leadDocDetails.getLeadMaster() != null && leadDocDetails.getLeadMaster().getMvnoId() != null) {
					customerDocDetailsDTO.setMvnoId(leadDocDetails.getLeadMaster().getMvnoId().intValue());
				}
				customerDocDetailsDTO.setStaffId(leadDocDetails.getLeadMaster().getNextApproveStaffId());
				customerDocDetailsDTOList.add(customerDocDetailsDTO);

			}
		}
		return customerDocDetailsDTOList;
	}

	public void deleteLeadMasterEntityList(LeadMaster leadMaster) {
		String SUBMODULE = MODULE + "deleteLeadMasterEntityList()";
		try {
			List<CustPlanMappping> custPlanMapppingList = this.custPlanMapppingRepository
					.findByLeadMasterId(leadMaster.getId());
			if (custPlanMapppingList != null && custPlanMapppingList.size() > 0)
				this.custPlanMapppingRepository.deleteAll(custPlanMapppingList);

			List<CustomerAddress> customerAddressList = this.customerAddressRepository
					.findByLeadMasterId(leadMaster.getId());
			if (customerAddressList != null && customerAddressList.size() > 0)
				this.customerAddressRepository.deleteAll(customerAddressList);

			List<DebitDocument> debitDocumentList = this.debitDocumentRepository.findByLeadMasterId(leadMaster.getId());
			if (debitDocumentList != null && debitDocumentList.size() > 0)
				this.debitDocumentRepository.deleteAll(debitDocumentList);

			List<CreditDocument> creditDocumentList = this.creditDocumentRepository
					.findByLeadMasterId(leadMaster.getId());
			if (creditDocumentList != null && creditDocumentList.size() > 0)
				this.creditDocumentRepository.deleteAll(creditDocumentList);

			List<CustChargeDetails> custChargeDetailList = this.custChargeDetailsRepository
					.findByLeadMasterId(leadMaster.getId());
			if (custChargeDetailList != null && custChargeDetailList.size() > 0)
				this.custChargeDetailsRepository.deleteAll(custChargeDetailList);

			List<CustomerDocDetails> customerDocDetails = this.customerDocDetailsRepository
					.findByLeadMasterId(leadMaster.getId());
			if (customerDocDetails != null && customerDocDetails.size() > 0)
				this.customerDocDetailsRepository.deleteAll(customerDocDetails);

			List<CustMacMappping> custMacMapppingList = this.custMacMapppingRepository
					.findByLeadMasterId(leadMaster.getId());
			if (custMacMapppingList != null && custMacMapppingList.size() > 0)
				this.custMacMapppingRepository.deleteAll(custMacMapppingList);

			List<CustLedgerDtls> custLedgerDtls = this.custLedgerDtlsRepository.findByLeadMasterId(leadMaster.getId());
			if (custLedgerDtls != null && custLedgerDtls.size() > 0)
				this.custLedgerDtlsRepository.deleteAll(custLedgerDtls);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Page<LeadMasterPojo> findAll(PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "findAll()";
		try {
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			LeadMaster leadMaster = new LeadMaster();
			leadMaster.setDeleted(false);
			leadMaster.setLeadStatus("Inquiry");
			Page<LeadMasterPojo> pojoPageList = this.leadMasterRepository
					.findByLeadStatusAndIsDeleted("Inquiry", false, pageRequest).map(data -> new LeadMasterPojo(data));
			pojoPageList.getContent().forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
			});
			return pojoPageList;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

//	@Override
//	public Page<LeadMasterPojo> findAll(Long mvnoId,PaginationRequestDTO paginationRequestDTO) {
//		String SUBMODULE = MODULE + "findAll()";
//		try {
//			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
//					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//					paginationRequestDTO.getSortOrder());
//			LeadMaster leadMaster = new LeadMaster();
//			leadMaster.setDeleted(false);
//			leadMaster.setLeadStatus("Inquiry");
//			Page<LeadMasterPojo> pojoPageList =  this.leadMasterRepository.findByLeadStatusAndMvnoIdAndIsDeleted("Inquiry",mvnoId,false, pageRequest)
//					.map(data -> new LeadMasterPojo(data));
//			pojoPageList.getContent().forEach(lead->{
//				if(lead != null && lead.getNextApproveStaffId() != null) {
//					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
//					if(optionalStaffUser.isPresent()) {
//						StaffUser staffUser = optionalStaffUser.get();
//						if(staffUser != null && staffUser.getUsername() != null) {
//							lead.setAssigneeName(staffUser.getUsername());
//						}
//					}
//				}
//			});
//			return pojoPageList;
//		} catch (Exception ex) {
//			logger.error(SUBMODULE + ex.getMessage(), ex);
//			throw ex;
//		}
//	}

	@Override
	public void rejectLead(LeadRejectDto leadRejectDto) {
		String SUBMODULE = MODULE + "rejectLead()";
		try {
			LeadMaster leadMaster = this.leadMasterRepository.findById(leadRejectDto.getLeadMasterId()).get();
			leadMaster.setLeadStatus("Rejected");
			leadMaster.setRejectLeadTime(LocalDateTime.now());
			leadMaster.setRejectReason(new RejectReason(leadRejectDto.getRejectReasonId()));

			if (leadRejectDto.getRejectSubReasonId() != null)
				leadMaster.setRejectSubReason(new RejectSubReason(leadRejectDto.getRejectSubReasonId()));

			leadMaster.setRemarks(leadRejectDto.getRemark());
			this.leadMasterRepository.save(leadMaster);

			// add audit data
			if (leadMaster.getCreatedBy() != null && !leadMaster.getCreatedBy().equalsIgnoreCase("")) {
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository
						.findById(Integer.parseInt(leadMaster.getCreatedBy()));
				if (optionalStaffUser.isPresent()) {
					StaffUser staffUser = optionalStaffUser.get();
					String auditName = staffUser.getFirstname() + " close Lead for Customer "
							+ leadMaster.getFirstname() + " " + leadMaster.getLastname() + " on "
							+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(LocalDateTime.now());
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName("Lead has been closed");
					leadAudit.setLeadMasterId(leadMaster.getId());
					this.leadAuditService.save(leadAudit);
				}
			}
			logger.info("Lead has been closed successfully");
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public LeadMasterPojo saveCampaignManager(LeadMasterPojo leadMasterPojo, Long mvnoId, Long buId, Long staffId) {
		if (leadMasterPojo.getLeadSourceName().equalsIgnoreCase("CampaignManager")) {
			Optional<LeadSource> optionalLeadSource = this.leadSourceRepository
					.findByLeadSourceNameAndIsDeleteFalse(leadMasterPojo.getLeadSourceName());
			if (optionalLeadSource.isPresent()) {
				LeadSource leadSource = optionalLeadSource.get();
				leadMasterPojo.setLeadSourceId(leadSource.getId());
				leadMasterPojo.setLeadSourceName(leadMasterPojo.getLeadSourceName());
				for (LeadSubSource leadSubSource : leadSource.getLeadSubSourceList()) {
					if (leadSubSource.getLeadSubSourceName().equalsIgnoreCase(leadMasterPojo.getLeadSubSourceName())) {
						leadMasterPojo.setLeadSubSourceId(leadSubSource.getId());
					}
				}
				if (leadMasterPojo.getLeadSubSourceId() == null) {
					LeadSubSource leadSubSource = new LeadSubSource();
					leadSubSource.setLeadSubSourceName(leadMasterPojo.getLeadSubSourceName());
					leadSubSource.setLeadSource(leadSource);
					LeadSubSource savedLeadSubSource = this.leadSubSourceRepository.save(leadSubSource);
					leadMasterPojo.setLeadSubSourceId(savedLeadSubSource.getId());
				}
			}
		}
		return save(leadMasterPojo, mvnoId, buId, staffId);
	}

	// Lead mgmt staff find
//	public void getCountOfApprovalLeadReuqestByStaff(Integer staffUserId, Integer leadId) {
//		Integer count = Math.toIntExact(leadMasterRepository.findMinimumApprovalReuqestByStaff(staffUserId, leadId));
//		// put send message over here
//		HashMap<Integer, Long> countListmap = new HashMap<Integer, Long>();
//		if (count != null) {
//			countListmap.put(staffUserId, count.longValue());
//		}
//		SendCountMessage sendCountMessage = new SendCountMessage(countListmap);
//		messageSender.send(sendCountMessage, RabbitMqConstants.QUEUE_COUNT_FOR_STAFF);
//
//
//	}
	public void updateLeadApprover(LeadMgmtWfDTO leadMgmtWfDTO) {
		String SUBMODULE = MODULE + "updateLeadApprover()";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter timeformatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");

		try {
			if (leadMgmtWfDTO.getIsForLeadAssign() == null || leadMgmtWfDTO.getIsForLeadAssign() == false) {
				LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMgmtWfDTO.getId()).get();
				if (leadMgmtWfDTO.isFinalApproved()) {
					// exstingLeadMaster.setLeadStatus("Converted");
					exstingLeadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
				} else
					exstingLeadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
				exstingLeadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());
				exstingLeadMaster.setFinalApproved(leadMgmtWfDTO.isFinalApproved());
				if (leadMgmtWfDTO.getStatus() != null)
					exstingLeadMaster.setLeadStatus(leadMgmtWfDTO.getStatus());
				if(leadMgmtWfDTO.getNextfollowuptime()!=null) {
					if (leadMgmtWfDTO.getNextfollowuptime().length() == 8) {
						exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(), timeformatter1));
					} else if (leadMgmtWfDTO.getNextfollowuptime().length() == 5) {
						exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(), timeformatter));
					} else {
						System.out.println("Invalid time format");
					}
				}
				if(leadMgmtWfDTO.getNextfollowupdate()!=null){
					LocalDate localDate = LocalDate.parse(leadMgmtWfDTO.getNextfollowupdate(), formatter);
					exstingLeadMaster.setNextfollowupdate(localDate);
				}

				saveLeadWFAudit(exstingLeadMaster, leadMgmtWfDTO.getCurrentLoggedInStaffId(),
						leadMgmtWfDTO.getTeamName(), leadMgmtWfDTO.getFlag(), leadMgmtWfDTO.getNextApproveStaffId());
				if (leadMgmtWfDTO.getFlag().equalsIgnoreCase("Rejected")) {
					// LeadMaster leadMaster =
					// this.leadMasterRepository.findById(leadRejectDto.getLeadMasterId()).get();
					exstingLeadMaster.setLeadStatus("Rejected");
					exstingLeadMaster.setRejectLeadTime(LocalDateTime.now());
					if(leadMgmtWfDTO.getRejectedReasonMasterId()==null){
						List<RejectReason> rejectReasonList=rejectReasonRepository.findAll();
						exstingLeadMaster.setRejectReason(new RejectReason(rejectReasonList.get(0).getId()));
					}else{
						exstingLeadMaster.setRejectReason(new RejectReason(leadMgmtWfDTO.getRejectedReasonMasterId()));

					}


					// if (leadRejectDto.getRejectSubReasonId() != null)
					// leadMaster.setRejectSubReason(new
					// RejectSubReason(leadRejectDto.getRejectSubReasonId()));

					// leadMaster.setRemarks(leadRejectDto.getRemark());
					this.leadMasterRepository.save(exstingLeadMaster);

					// add audit data

					if (exstingLeadMaster.getCreatedBy() != null
							&& !exstingLeadMaster.getCreatedBy().equalsIgnoreCase("")) {
						Optional<StaffUser> optionalStaffUser = this.staffUserRepository
								.findById(Integer.parseInt(exstingLeadMaster.getCreatedBy()));
						if (optionalStaffUser.isPresent()) {
							StaffUser staffUser = optionalStaffUser.get();
							String auditName = staffUser.getFirstname() + " close Lead for Customer "
									+ exstingLeadMaster.getFirstname() + " " + exstingLeadMaster.getLastname() + " on "
									+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(LocalDateTime.now());
							LeadAudit leadAudit = new LeadAudit();
							leadAudit.setName(auditName);
							leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
							leadAudit.setAuditName("Lead has been closed");
							leadAudit.setLeadMasterId(exstingLeadMaster.getId());
							this.leadAuditService.save(leadAudit);
						}
					}
				} else {
					if (leadMgmtWfDTO.isFinalApproved() && leadMgmtWfDTO.getNextTeamMappingId() == null) {
//						exstingLeadMaster.setLeadStatus("Converted");
					}
					// exstingLeadMaster.setLeadStatus("Converted");
					leadMasterRepository.save(exstingLeadMaster);
				}
			} else if (leadMgmtWfDTO.getIsForLeadAssign().equals(true)) {
				if (leadMgmtWfDTO.getOperation().equalsIgnoreCase(CommonConstants.LEAD_CHANGE_ASSIGNEE)) {

					LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMgmtWfDTO.getId()).get();

					exstingLeadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
//					exstingLeadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());

					leadMasterRepository.save(exstingLeadMaster);

					LeadGeneralAudit leadGeneralAudit = new LeadGeneralAudit();
					leadGeneralAudit.setRemarkType(leadMgmtWfDTO.getRemarkType());
					leadGeneralAudit.setOperation(leadMgmtWfDTO.getOperation());
					leadGeneralAudit.setLeadId(leadMgmtWfDTO.getId());
					leadGeneralAudit.setRemark(leadMgmtWfDTO.getRemark());
					leadGeneralAudit.setOldValue(leadMgmtWfDTO.getOldValue());
					leadGeneralAudit.setNewValue(leadMgmtWfDTO.getNewValue());
					leadGeneralAudit.setCreateDateString(leadMgmtWfDTO.getCreateDateString());
					leadGeneralAudit.setCreatedBy(leadMgmtWfDTO.getCreatedBy());
					leadGeneralAudit.setUpdateDateString(leadMgmtWfDTO.getUpdateDateString());
					leadGeneralAudit.setLastUpdatedBy(leadMgmtWfDTO.getLastUpdatedBy());
					leadGeneralAudit.setEntityType(leadMgmtWfDTO.getEntityType());

					leadGeneralAuditRepository.save(leadGeneralAudit);

				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public LeadMgmtWfDTO updateCustomerLeadAssignment(LeadMgmtWfDTO leadMgmtWfDTO) {
		String SUBMODULE = MODULE + "updateCustomerLeadAssignment()";
		try {
			Optional<LeadMaster> leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId());

			// get all the required details for next approver eg status, approverid,
			// staffid, buid,mvnoid
			LeadMgmtWfDTO leadMgmtWfDTOSend = new LeadMgmtWfDTO();
			leadMgmtWfDTOSend.setServiceareaid(leadMaster.get().getServiceareaid());
			leadMgmtWfDTOSend.setNextApproveStaffId(leadMaster.get().getNextTeamMappingId());
			if (leadMgmtWfDTO.getFlag().equalsIgnoreCase("Rejected")) {
				leadMgmtWfDTOSend.setStatus("Rejected");
			} else {
				leadMgmtWfDTOSend.setStatus(leadMaster.get().getStatus());
			}
			leadMgmtWfDTOSend.setNextApproveStaffId(leadMaster.get().getNextApproveStaffId());
			leadMgmtWfDTOSend.setMvnoId(leadMaster.get().getMvnoId());
			leadMgmtWfDTOSend.setBuId(leadMaster.get().getBuId());
			leadMgmtWfDTOSend.setFlag(leadMgmtWfDTO.getFlag());
			leadMgmtWfDTOSend.setRemark(leadMgmtWfDTO.getRemark());
			leadMgmtWfDTOSend.setId(leadMaster.get().getId());
			leadMgmtWfDTOSend.setCurrentLoggedInStaffId(Integer.parseInt(leadMaster.get().getCreatedBy()));

			// add all the details in the rqmessage

			SendUpdateLeadData sendUpdateLeadData = new SendUpdateLeadData(leadMgmtWfDTOSend);
//			messageSender.send(sendUpdateLeadData, RabbitMqConstants.QUEUE_SEND_APPROVER_UPDATE_DETAIL);
			kafkaMessageSender.send(new KafkaMessageData(sendUpdateLeadData, SendUpdateLeadData.class.getSimpleName()));


			// send messege to the apigetway

			return leadMgmtWfDTO;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Transactional
	public void updateLeadApproverInfo(LeadMgmtWfDTO leadMgmtWfDTO) {
		String SUBMODULE = MODULE + "updateCustomerLeadAssignment()";
		try {
			DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
			DateTimeFormatter timeformatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
			LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMgmtWfDTO.getId()).get();
			exstingLeadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
			exstingLeadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());
			if (leadMgmtWfDTO.getNextfollowuptime().length() == 8) {
				exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(),timeformatter1));
			} else if (leadMgmtWfDTO.getNextfollowuptime().length() == 5) {
				exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(),timeformatter));
			} else {
				System.out.println("Invalid time format");
			}

			leadMasterRepository.save(exstingLeadMaster);
			saveLeadWFAudit(exstingLeadMaster, leadMgmtWfDTO.getCurrentLoggedInStaffId(), leadMgmtWfDTO.getTeamName(),
					leadMgmtWfDTO.getFlag(), leadMgmtWfDTO.getNextApproveStaffId());
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void saveLeadWFAudit(LeadMaster leadMaster, Integer staffId, String teamName, String flag,
			Integer approvedStaffId) {
		String SUBMODULE = MODULE + "saveLeadWFAudit()";
		try {
			if (staffId != null) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffId);
				Optional<StaffUser> approvedStaffUser = this.staffUserRepository.findById(approvedStaffId);
				if (optionalStaffUser.isPresent()) {
					String remark = "";
					StaffUser staffUser = optionalStaffUser.get();
					if (flag.equals("Approved")) {
						if (approvedStaffUser.isPresent()) {
							remark += approvedStaffUser.get().getFirstname() + " " + flag + " " + "a lead" + " "
									+ leadMaster.getId() + " " + "for" + teamName + " " + "on" + " " + dtf.format(now);

						}
					} else
						remark += approvedStaffUser.get().getFirstname() + " " + flag + " " + "a lead" + " "
								+ leadMaster.getId() + " " + "for" + " " + teamName + " " + "on" + " "
								+ dtf.format(now);
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(remark);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					if (flag.equals("Approved"))
						leadAudit.setAuditName("APPROVE_LEAD");
					else if (flag.equals("Rejected"))
						leadAudit.setAuditName("REJECT_LEAD");
					else
						leadAudit.setAuditName("ASSIGN_LEAD");
					leadAudit.setLeadMasterId(leadMaster.getId());
					this.leadAuditService.save(leadAudit);
				}
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public void reopenLead(Long leadId, Long staffId) {
		String SUBMODULE = MODULE + "reopenLead()";
		try {
			LeadMaster leadMaster = this.leadMasterRepository.findById(leadId).get();
			leadMaster.setLeadStatus("Re-Inquiry");
			leadMaster.setNextTeamMappingId(null);
			leadMaster.setNextApproveStaffId(Math.toIntExact(staffId));
			leadMaster.setFinalApproved(false);
//			this.leadMasterRepository.save(leadMaster);
			// Setting values in leadMgmtWfDTO and send to the apigetway for workflow
			LeadMaster savedLeadMaster = this.leadMasterRepository.save(leadMaster);
			LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
			leadMgmtWfDTO.setBuId(savedLeadMaster.getBuId());
			leadMgmtWfDTO.setFirstname(savedLeadMaster.getFirstname());
			leadMgmtWfDTO.setStatus("Re-Inquiry");
			leadMgmtWfDTO.setNextTeamMappingId(null);
			leadMgmtWfDTO.setNextApproveStaffId(Math.toIntExact(staffId));
			leadMgmtWfDTO.setId(savedLeadMaster.getId());
			leadMgmtWfDTO.setMvnoId(savedLeadMaster.getMvnoId());
			leadMgmtWfDTO.setServiceareaid(savedLeadMaster.getServiceareaid());
			leadMgmtWfDTO.setCurrentLoggedInStaffId(staffId.intValue());
			SendSaveLeadData sendSaveLeadData = new SendSaveLeadData(leadMgmtWfDTO);
//			messageSender.send(sendSaveLeadData, RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA);
			kafkaMessageSender.send(new KafkaMessageData(sendSaveLeadData,SendSaveLeadData.class.getSimpleName(),"REOPEN_LEAD"));

			addAuditForReInquiry(leadMaster);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public void convertLeadToCustomerCafAndSendToCustomerCafEntry(LeadMasterPojo leadMasterPojo) {
		CustomerPojoMessage customerPojoMessage = new CustomerPojoMessage();
		customerPojoMessage.setUsername(leadMasterPojo.getUsername());
		customerPojoMessage.setPassword(leadMasterPojo.getPassword());
		customerPojoMessage.setFirstname(leadMasterPojo.getFirstname());
		customerPojoMessage.setLastname(leadMasterPojo.getLastname());
		customerPojoMessage.setEmail(leadMasterPojo.getEmail());
		customerPojoMessage.setTitle(leadMasterPojo.getTitle());
		customerPojoMessage.setCustname(leadMasterPojo.getCustname());
		customerPojoMessage.setContactperson(leadMasterPojo.getContactperson());
		customerPojoMessage.setPan(leadMasterPojo.getPan());
		customerPojoMessage.setGst(leadMasterPojo.getGst());
		customerPojoMessage.setAadhar(leadMasterPojo.getAadhar());
		customerPojoMessage.setStatus("NewActivation");
		customerPojoMessage.setFailcount(leadMasterPojo.getFailcount());
		customerPojoMessage.setAcctno(leadMasterPojo.getAcctno());
		customerPojoMessage.setCusttype(leadMasterPojo.getCusttype());
		customerPojoMessage.setPhone(leadMasterPojo.getPhone());
		customerPojoMessage.setBillday(leadMasterPojo.getBillday());
		customerPojoMessage.setPartnerid(leadMasterPojo.getPartnerid());
		customerPojoMessage.setOnuid(leadMasterPojo.getOnuid());
		customerPojoMessage.setAddresstype(leadMasterPojo.getAddresstype());
		customerPojoMessage.setAddress1(leadMasterPojo.getAddress1());
		customerPojoMessage.setAddress2(leadMasterPojo.getAddress2());
		customerPojoMessage.setCity(leadMasterPojo.getCity());
		customerPojoMessage.setState(leadMasterPojo.getState());
		customerPojoMessage.setCountry(leadMasterPojo.getCountry());
		customerPojoMessage.setPincode(leadMasterPojo.getPincode());
		customerPojoMessage.setArea(leadMasterPojo.getArea());
		customerPojoMessage.setOutstanding(leadMasterPojo.getOutstanding());
		customerPojoMessage.setOldpassword1(leadMasterPojo.getOldpassword1());
		customerPojoMessage.setOldpassword2(leadMasterPojo.getOldpassword2());
		customerPojoMessage.setOldpassword3(leadMasterPojo.getOldpassword3());
		customerPojoMessage.setNewpassword(leadMasterPojo.getNewpassword());
		customerPojoMessage.setSelfcarepwd(leadMasterPojo.getSelfcarepwd());
		customerPojoMessage.setLastpasswordchangestring(leadMasterPojo.getLastpasswordchangestring());
		if (leadMasterPojo.getPlanMappingList() != null && leadMasterPojo.getPlanMappingList().size() > 0) {
			List<CustPlanMapppingPojoMessage> custPlanMapppingPojoMessageList = new ArrayList<>();
			for (CustPlanMapppingPojo custPlanMapppingPojo : leadMasterPojo.getPlanMappingList()) {
				custPlanMapppingPojoMessageList.add(new CustPlanMapppingPojoMessage(custPlanMapppingPojo));
			}
			customerPojoMessage.setPlanMappingMessageList(custPlanMapppingPojoMessageList);
		}
		customerPojoMessage.setAddressList(leadMasterPojo.getAddressList());
		customerPojoMessage.setRadiusprofileIds(leadMasterPojo.getRadiusprofileIds());
		if (leadMasterPojo.getDebitDocList() != null && leadMasterPojo.getDebitDocList().size() > 0) {
			List<DebitDocumentPojoMessage> debitDocumentPojoMessageList = new ArrayList<>();
			for (DebitDocumentPojo debitDocumentPojo : leadMasterPojo.getDebitDocList()) {
				debitDocumentPojoMessageList.add(new DebitDocumentPojoMessage(debitDocumentPojo));
			}
			customerPojoMessage.setDebitDocMessageList(debitDocumentPojoMessageList);
		}
		if (leadMasterPojo.getDebitDocList() != null && leadMasterPojo.getDebitDocList().size() > 0) {
			List<DebitDocumentPojoMessage> debitDocumentPojoMessageList = new ArrayList<>();
			for (DebitDocumentPojo debitDocumentPojo : leadMasterPojo.getDebitDocList()) {
				debitDocumentPojoMessageList.add(new DebitDocumentPojoMessage(debitDocumentPojo));
			}
			customerPojoMessage.setDebitDocMessageList(debitDocumentPojoMessageList);
		}
		if (leadMasterPojo.getCreditDocuments() != null && leadMasterPojo.getCreditDocuments().size() > 0) {
			List<CreditDocumentPojoMessage> creditDocumentPojoMessageList = new ArrayList<>();
			for (CreditDocumentPojo creditDocumentPojo : leadMasterPojo.getCreditDocuments()) {
				creditDocumentPojoMessageList.add(new CreditDocumentPojoMessage(creditDocumentPojo));
			}
			customerPojoMessage.setCreditDocumentMessages(creditDocumentPojoMessageList);
		}
		if (leadMasterPojo.getOverChargeList() != null && leadMasterPojo.getOverChargeList().size() > 0) {
			List<CustChargeDetailsPojoMessage> custChargeDetailsPojoMessageList = new ArrayList<>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMasterPojo.getOverChargeList()) {
				custChargeDetailsPojoMessageList.add(new CustChargeDetailsPojoMessage(custChargeDetailsPojo));
			}
			customerPojoMessage.setOverChargeList(custChargeDetailsPojoMessageList);
		}
		if (leadMasterPojo.getCustDocList() != null && leadMasterPojo.getCustDocList().size() > 0) {
			List<CustomerDocDetailsDTOMessage> customerDocDetailsDTOMessageList = new ArrayList<>();
			for (CustomerDocDetailsDTO customerDocDetailsDTO : convertToCustomerDocDetailsDTOList(
					leadMasterPojo.getCustDocList())) {
				customerDocDetailsDTOMessageList.add(new CustomerDocDetailsDTOMessage(customerDocDetailsDTO));
			}
			customerPojoMessage.setCustDocList(customerDocDetailsDTOMessageList);
		}
		if (leadMasterPojo.getIndiChargeList() != null && leadMasterPojo.getIndiChargeList().size() > 0) {
			List<CustChargeDetailsPojoMessage> custChargeDetailsPojoMessageList = new ArrayList<>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMasterPojo.getIndiChargeList()) {
				custChargeDetailsPojoMessageList.add(new CustChargeDetailsPojoMessage(custChargeDetailsPojo));
			}
			customerPojoMessage.setIndiChargeList(custChargeDetailsPojoMessageList);
		}
		customerPojoMessage.setCustLeger(leadMasterPojo.getCustLeger());
		customerPojoMessage.setCustMacMapppingList(leadMasterPojo.getCustMacMapppingList());
		customerPojoMessage.setLedgerDtls(leadMasterPojo.getLedgerDtls());
		if (leadMasterPojo.getPaymentDetails() != null)
			customerPojoMessage.setPaymentDetails(new RecordPaymentPojoMessage(leadMasterPojo.getPaymentDetails()));
		customerPojoMessage.setFlashMsg(leadMasterPojo.getFlashMsg());
		customerPojoMessage.setMactelflag(leadMasterPojo.getMactelflag());
		customerPojoMessage.setMobile(leadMasterPojo.getMobile());
		customerPojoMessage.setCountryCode(leadMasterPojo.getCountryCode());
		customerPojoMessage.setCafno(leadMasterPojo.getCafno());
		customerPojoMessage.setAltmobile(leadMasterPojo.getAltmobile());
		customerPojoMessage.setAltphone(leadMasterPojo.getAltphone());
		customerPojoMessage.setAltemail(leadMasterPojo.getAltemail());
		customerPojoMessage.setFax(leadMasterPojo.getFax());
		customerPojoMessage.setResellerid(leadMasterPojo.getResellerid());
		customerPojoMessage.setSalesrepid(leadMasterPojo.getSalesrepid());
		customerPojoMessage.setVoicesrvtype(leadMasterPojo.getVoicesrvtype());
		customerPojoMessage.setVoiceprovision(leadMasterPojo.getVoiceprovision());
		customerPojoMessage.setDidno(leadMasterPojo.getDidno());
		customerPojoMessage.setChilddidno(leadMasterPojo.getChilddidno());
		customerPojoMessage.setIntercomno(leadMasterPojo.getIntercomno());
		customerPojoMessage.setIntercomgrp(leadMasterPojo.getIntercomgrp());
		customerPojoMessage.setOnlinerenewalflag(leadMasterPojo.getOnlinerenewalflag());
		customerPojoMessage.setVoipenableflag(leadMasterPojo.getVoipenableflag());
		customerPojoMessage.setCustcategory(leadMasterPojo.getCustcategory());
		customerPojoMessage.setWalletbalance(leadMasterPojo.getWalletbalance());
		customerPojoMessage.setNetworktype(leadMasterPojo.getNetworktype());
		customerPojoMessage.setDefaultpoolid(leadMasterPojo.getDefaultpoolid());
		customerPojoMessage.setServiceareaid(leadMasterPojo.getServiceareaid());
		customerPojoMessage.setNetworkdevicesid(leadMasterPojo.getNetworkdevicesid());
		customerPojoMessage.setOltslotid(leadMasterPojo.getOltslotid());
		customerPojoMessage.setOltportid(leadMasterPojo.getOltportid());
		customerPojoMessage.setStrconntype(leadMasterPojo.getStrconntype());
		customerPojoMessage.setStroltname(leadMasterPojo.getStroltname());
		customerPojoMessage.setStrslotname(leadMasterPojo.getStrslotname());
		customerPojoMessage.setStrportname(leadMasterPojo.getStrportname());
		customerPojoMessage.setOldBNGRouterinterface(leadMasterPojo.getOldBNGRouterinterface());
		customerPojoMessage.setOldVSIName(leadMasterPojo.getOldVSIName());
		customerPojoMessage.setASNNumber(leadMasterPojo.getASNNumber());
		customerPojoMessage.setBNGRouterinterface(leadMasterPojo.getBNGRouterinterface());
		customerPojoMessage.setBNGRoutername(leadMasterPojo.getBNGRoutername());
		customerPojoMessage.setIPPrefixes(leadMasterPojo.getIPPrefixes());
		customerPojoMessage.setIPV6Prefixes(leadMasterPojo.getIPV6Prefixes());
		customerPojoMessage.setLANIP(leadMasterPojo.getLANIP());
		customerPojoMessage.setLANIPV6(leadMasterPojo.getLANIPV6());
		customerPojoMessage.setLLAccountid(leadMasterPojo.getLLAccountid());
		customerPojoMessage.setLLConnectiontype(leadMasterPojo.getLLConnectiontype());
		customerPojoMessage.setLLExpirydate(leadMasterPojo.getLLExpirydate());
		customerPojoMessage.setLLMedium(leadMasterPojo.getLLMedium());
		customerPojoMessage.setLLServiceid(leadMasterPojo.getLLServiceid());
		customerPojoMessage.setMACADDRESS(leadMasterPojo.getMACADDRESS());
		customerPojoMessage.setPeerip(leadMasterPojo.getPeerip());
		customerPojoMessage.setPOOLIP(leadMasterPojo.getPOOLIP());
		customerPojoMessage.setQOS(leadMasterPojo.getQOS());
		customerPojoMessage.setRDExport(leadMasterPojo.getRDExport());
		customerPojoMessage.setRDValue(leadMasterPojo.getRDValue());
		customerPojoMessage.setVLANID(leadMasterPojo.getVLANID());
		customerPojoMessage.setVRFName(leadMasterPojo.getVRFName());
		customerPojoMessage.setVSIID(leadMasterPojo.getVSIID());
		customerPojoMessage.setVSIName(leadMasterPojo.getVSIName());
		customerPojoMessage.setWANIP(leadMasterPojo.getWANIP());
		customerPojoMessage.setWANIPV6(leadMasterPojo.getWANIPV6());
		customerPojoMessage.setBillentityname(leadMasterPojo.getBillentityname());
		customerPojoMessage.setAddparam1(leadMasterPojo.getAddparam1());
		customerPojoMessage.setAddparam2(leadMasterPojo.getAddparam2());
		customerPojoMessage.setAddparam3(leadMasterPojo.getAddparam3());
		customerPojoMessage.setAddparam4(leadMasterPojo.getAddparam4());
		customerPojoMessage.setPurchaseorder(leadMasterPojo.getPurchaseorder());
		customerPojoMessage.setRemarks(leadMasterPojo.getRemarks());
		customerPojoMessage.setAllowedIPAddress(leadMasterPojo.getAllowedIPAddress());
		customerPojoMessage.setOldWANIP(leadMasterPojo.getOldWANIP());
		customerPojoMessage.setOldLLAccountid(leadMasterPojo.getOldLLAccountid());
		customerPojoMessage.setIsDeleted(leadMasterPojo.isDeleted());
		customerPojoMessage.setCreateDateString(leadMasterPojo.getCreateDateString());
		customerPojoMessage.setUpdateDateString(leadMasterPojo.getUpdateDateString());
		customerPojoMessage.setLatitude(leadMasterPojo.getLatitude());
		customerPojoMessage.setLongitude(leadMasterPojo.getLongitude());
		customerPojoMessage.setUrl(leadMasterPojo.getUrl());
		customerPojoMessage.setGis_code(leadMasterPojo.getGisCode());
		customerPojoMessage.setSalesremark(leadMasterPojo.getSalesremark());
		customerPojoMessage.setServicetype(leadMasterPojo.getServicetype());
		customerPojoMessage.setIsCustCaf(leadMasterPojo.getIsCustCaf());
		customerPojoMessage.setPreviousCafApprover(leadMasterPojo.getPreviousCafApprover());
		customerPojoMessage.setNextCafApprover(leadMasterPojo.getNextCafApprover());
		customerPojoMessage.setServiceareaName(leadMasterPojo.getServiceareaName());
		customerPojoMessage.setCafApproveStatus(leadMasterPojo.getCafApproveStatus());
		if (leadMasterPojo.getMvnoId() != null) {
			customerPojoMessage.setMvnoId(leadMasterPojo.getMvnoId().intValue());
		}
		customerPojoMessage.setTinNo(leadMasterPojo.getTinNo());
		customerPojoMessage.setPassportNo(leadMasterPojo.getPassportNo());
		customerPojoMessage.setDunningCategory(leadMasterPojo.getDunningCategory());
		customerPojoMessage.setPlangroupid(leadMasterPojo.getPlangroupid());
		customerPojoMessage.setParentCustomerId(leadMasterPojo.getParentCustomerId());
		customerPojoMessage.setParentCustomerName(leadMasterPojo.getParentCustomerName());
		customerPojoMessage.setInvoiceType(leadMasterPojo.getInvoiceType());
		customerPojoMessage.setCalendarType(leadMasterPojo.getCalendarType());
		if (leadMasterPojo.getDiscount() != null)
			customerPojoMessage.setDiscount(leadMasterPojo.getDiscount());
		customerPojoMessage.setBuId(leadMasterPojo.getBuId());
		customerPojoMessage.setLeadSource(leadMasterPojo.getLeadSourceName());
		customerPojoMessage.setFeasibilityRequired(leadMasterPojo.getFeasibilityRequired());
		if (leadMasterPojo.getDesignation() != null)
			customerPojoMessage.setDesignation(leadMasterPojo.getDesignation());
;		// set lead Document
//		this.messageSender.send(customerPojoMessage, RabbitMqConstants.QUEUE_SEND_CONVERT_CUSTOMER_CAF_POJO);
		kafkaMessageSender.send(new KafkaMessageData(customerPojoMessage, CustomerPojoMessage.class.getSimpleName()));
	}

//	public List<TeamHierarchyDTO> getLeadStatus(LeadMasterPojo leadMasterPojo, List<TeamHierarchyDTO> teamHierarchyDTOList){
//		if(leadMasterPojo !=null && teamHierarchyDTOList==null){
//			LeadMaster leadMaster = leadMasterRepository.findById(leadMasterPojo.getId()).get();
//			LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
//			leadMgmtWfDTO.setNextLeadApprover(leadMaster.getNextApproverId());
//			leadMgmtWfDTO.setId(leadMaster.getId());
//			leadMgmtWfDTO.setBuId(leadMaster.getBuId());
//			leadMgmtWfDTO.setMvnoId(leadMaster.getMvnoId());
//
//			SendLeadStatusReq sendLeadStatusReq = new SendLeadStatusReq();
//			sendLeadStatusReq.setLeadMgmtWfDTO(leadMgmtWfDTO);
//			messageSender.send(sendLeadStatusReq,RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_INFO);
//		}
//		else if(Objects.isNull(leadMasterPojo) && !teamHierarchyDTOList.isEmpty()){
//			return teamHierarchyDTOList;
//		}
//		//recieve data
//
//		return null;
//	}

//	@Override
//	public List<TeamHierarchyDTO> getTeamHierarchyDTO(List<TeamHierarchyDTO> teamHierarchyDTOList) {
//		return null;
//	}

//	public List<TeamHierarchyDTO> getTeamHierarchyDTO(List<TeamHierarchyDTO> teamHierarchyDTOList){
//
//		List<TeamHierarchyDTO> teamHierarchyDTOS = new ArrayList<>();
//
//		return teamHierarchyDTOS;
//
//	}

	public List<CustomerDocDetailsDTO> convertToCustomerDocDetailsDTOList(
			List<CustomerDocDetailsPojo> customerDocDetailsPojoList) {
		List<CustomerDocDetailsDTO> customerDocDetailsDTOList = new ArrayList<CustomerDocDetailsDTO>();
		for (CustomerDocDetailsPojo customerDocDetailsPojo : customerDocDetailsPojoList) {
			CustomerDocDetailsDTO customerDocDetailsDTO = new CustomerDocDetailsDTO();
			customerDocDetailsDTO.setDocId(customerDocDetailsPojo.getDocId());
			customerDocDetailsDTO.setCustId(customerDocDetailsPojo.getLeadMasterId().intValue());
			customerDocDetailsDTO.setDocType(customerDocDetailsPojo.getDocType());
			customerDocDetailsDTO.setDocSubType(customerDocDetailsPojo.getDocSubType());
			customerDocDetailsDTO.setRemark(customerDocDetailsPojo.getRemark());
			customerDocDetailsDTO.setMode(customerDocDetailsPojo.getMode());
			customerDocDetailsDTO.setDocStatus(customerDocDetailsPojo.getDocStatus());
			customerDocDetailsDTO.setFilename(customerDocDetailsPojo.getFilename());
			customerDocDetailsDTO.setUniquename(customerDocDetailsPojo.getUniquename());
			customerDocDetailsDTO.setIsDelete(customerDocDetailsPojo.getIsDelete());
			customerDocDetailsDTO.setDocumentNumber(customerDocDetailsPojo.getDocumentNumber());
			customerDocDetailsDTO.setMvnoId(customerDocDetailsPojo.getMvnoId());
			customerDocDetailsDTO.setStartDateAsString(customerDocDetailsPojo.getStartDate().toString());
			customerDocDetailsDTO.setEndDateAsString(customerDocDetailsPojo.getEndDate().toString());
			customerDocDetailsDTOList.add(customerDocDetailsDTO);
		}
		return customerDocDetailsDTOList;
	}

	public void addAuditForReInquiry(LeadMaster leadMaster) {
		if (leadMaster.getCreatedBy() != null && !leadMaster.getCreatedBy().equalsIgnoreCase("")) {
			Optional<StaffUser> optionalStaffUser = this.staffUserRepository
					.findById(Integer.parseInt(leadMaster.getCreatedBy()));
			if (optionalStaffUser.isPresent()) {
				StaffUser staffUser = optionalStaffUser.get();
				String auditName = staffUser.getFirstname() + " ReInquiry Lead for Customer "
						+ leadMaster.getFirstname() + " " + leadMaster.getLastname() + " on "
						+ DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(LocalDateTime.now());
				LeadAudit leadAudit = new LeadAudit();
				leadAudit.setName(auditName);
				leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
				leadAudit.setAuditName("Lead has been ReInquiry");
				leadAudit.setLeadMasterId(leadMaster.getId());
				this.leadAuditService.save(leadAudit);
			}
		}
	}

	@Override
	public Page<LeadMasterPojo> search(Long mvnoId, List<Long> buId, PaginationRequestDTO paginationRequestDTO,
			String fromConvertedDate, String toConvertedDate) {
		String SUBMODULE = MODULE + "search()";
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {
			String queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false";
			String countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false";

			if (mvnoId != 1) {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId = 1 OR lm.mvnoId=" + mvnoId + ")";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL  OR lm.mvnoId = 1 OR lm.mvnoId=" + mvnoId + ")";
			}

			if (buId != null && !buId.isEmpty() && buId.size()>0) {
				queryForLeadMaster += " AND (lm.buId IN :buIds)";
				countQueryForLeadMaster += " AND (lm.buId IN :buIds)";
			}
//			else {
//				queryForLeadMaster += " AND (lm.buId IS NULL)";
//				countQueryForLeadMaster += " AND (lm.buId IS NULL)";
//			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("name")) {
					queryForLeadMaster += " AND lower(CONCAT(lm.title, ' ', lm.firstname, ' ', lm.lastname)) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
					queryForLeadMaster += " AND lower(CONCAT(lm.title, ' ', lm.firstname, ' ', lm.lastname)) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("mobile")) {
					queryForLeadMaster += " AND lower(lm.mobile) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
					countQueryForLeadMaster += " AND lower(lm.mobile) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("status")) {
					if (!paginationRequestDTO.getFilters().get(0).getFilterValue().equalsIgnoreCase("Converted")) {
						queryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						countQueryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
					}
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("createdBy")) {
					queryForLeadMaster += " AND lm.createdBy = '"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
					countQueryForLeadMaster += " AND lm.createdBy = '"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("leadSourceName")) {
				Optional<LeadSource> leadSourceList=leadSourceRepository.findByLeadSourceNameAndIsDeleteFalse(paginationRequestDTO.getFilters().get(0).getFilterValue());
					queryForLeadMaster += " AND lead_source_id = '"
							+ leadSourceList.get().getId()+ "'";
					countQueryForLeadMaster += " AND lead_source_id = '"
							+ leadSourceList.get().getId() + "'";
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().trim().equalsIgnoreCase("serviceArea")) {
					List<ServiceArea> serviceAreaList=serviceAreaRepository.findAllByNameContainingIgnoreCase(paginationRequestDTO.getFilters().get(0).getFilterValue());
					if (serviceAreaList.size() > 0) {
						queryForLeadMaster += " AND serviceareaid = '"
								+ serviceAreaList.get(0).getId() + "'";
						countQueryForLeadMaster += " AND serviceareaid = '"
								+ serviceAreaList.get(0).getId() + "'";
					}else {
						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
								"Not Record Found:", null);
					}
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().trim().equalsIgnoreCase("Lead Assigne Name")) {
					Optional<StaffUser> staffUser=staffUserRepository.findByUsername(paginationRequestDTO.getFilters().get(0).getFilterValue());
					if (staffUser.get() !=null) {
						queryForLeadMaster += " AND next_approve_staff_id = '"
								+ staffUser.get().getId() + "'";
						countQueryForLeadMaster += " AND next_approve_staff_id = '"
								+ staffUser.get().getId() + "'";
					}else {
						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
								"Not Record Found:", null);
					}
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().trim().equalsIgnoreCase("Branch")) {
					List<Branch> branchList=branchRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(paginationRequestDTO.getFilters().get(0).getFilterValue());
					if (branchList.size() > 0) {
						queryForLeadMaster += " AND branch_id = '"
								+ branchList.get(0).getId() + "'";
						countQueryForLeadMaster += " AND branch_id = '"
								+ branchList.get(0).getId() + "'";
					}else {
						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
								"Not Record Found:", null);
					}
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().trim().equalsIgnoreCase("Partner")) {
					List<Partner> partnerList=partnerRepository.findByNameAndIsDeleteFalse(paginationRequestDTO.getFilters().get(0).getFilterValue());
					if (partnerList.size() > 0) {
						queryForLeadMaster += " AND partnerid = '"
								+ partnerList.get(0).getId() + "'";
						countQueryForLeadMaster += " AND partnerid = '"
								+ partnerList.get(0).getId() + "'";
					}else {
						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
								"Not Record Found:", null);
					}
				}
			}
//			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
//				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("Province")) {
//					List<State> stateList=stateRepository.findByname(paginationRequestDTO.getFilters().get(0).getFilterValue());
//					if (stateList.size() > 0) {
//						queryForLeadMaster += " AND state = '"
//								+ stateList.get(0).getId() + "'";
//						countQueryForLeadMaster += " AND state = '"
//								+ stateList.get(0).getId() + "'";
//					}else {
//						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
//								"Not Record Found:", null);
//					}
//				}
//			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().trim().equalsIgnoreCase("plangroupname")) {
					List<PlanGroup> planGroupList=planGroupRepository.findByplanGroupName(paginationRequestDTO.getFilters().get(0).getFilterValue());
					if (planGroupList.size() > 0) {
						queryForLeadMaster += " AND plangroupid = '"
								+ planGroupList.get(0).getPlanGroupId() + "'";
						countQueryForLeadMaster += " AND plangroupid = '"
								+ planGroupList.get(0).getPlanGroupId() + "'";
					}else {
						throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
								"Not Record Found:", null);
					}
				}
			}
//			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
//				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("Plan")) {
//					List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findByPlanId(Integer.valueOf((paginationRequestDTO.getFilters().get(0).getFilterValue())));
//					for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
//						queryForLeadMaster += " AND planId  = '"
//								+ custPlanMappping.getId() + "'";
//						countQueryForLeadMaster += " AND planId  = '"
//								+ custPlanMappping.getId() + "'";
//					}
//				}
//			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("lastUpdateOn")) {
					String filterValue = paginationRequestDTO.getFilters().get(0).getFilterValue();
					String fromDate = filterValue + " 00:00:01";
					String toDate = filterValue + " 23:59:59";
					queryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate + "' AND lm.lastModifiedOn <= '"
							+ toDate + "'";
					countQueryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate + "' AND lm.lastModifiedOn <= '"
							+ toDate + "'";
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("status")) {
					if (paginationRequestDTO.getFilters().get(0).getFilterValue().equalsIgnoreCase("Converted")) {
						queryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						countQueryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						String filterValue = paginationRequestDTO.getFilters().get(0).getFilterValue();
						String fromDate = fromConvertedDate + " 00:00:01";
						String toDate = toConvertedDate + " 23:59:59";
						if (fromConvertedDate != null && toConvertedDate != null) {
							queryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate
									+ "' AND lm.lastModifiedOn <= '" + toDate + "'";
							countQueryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate
									+ "' AND lm.lastModifiedOn <= '" + toDate + "'";
						}
					}
				}
			}
			queryForLeadMaster += " order by lm.id DESC";
			Query q = entityManager.createQuery(queryForLeadMaster, LeadMaster.class);
			if(buId!=null && !buId.isEmpty()){
				q.setParameter("buIds",buId);
			}
			List<LeadMaster> leadMasterList = q.getResultList();
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			for (LeadMaster leadMaster : leadMasterList) {
				ClientService clientService = clientServiceSrv.getByNameAndMvnoId(LEAD_REOPEN_IN_DAYS,mvnoId);
				if (clientService != null)
					leadMasterPojoList.add(new LeadMasterPojo(leadMaster, Long.valueOf(clientService.getValue())));
			}
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
				if (lead != null && lead.getCafConvertedStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getCafConvertedStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setCafCovertedStaffName(staffUser.getUsername());
						}
					}
				}
				if(lead!=null){
					Mvno mvno = mvnoRepository.findById(lead.getMvnoId()).orElse(null);
					if(mvno!=null){
						lead.setMvnoName(mvno.getName());
					}
				}
			});
			Query queryTotal = entityManager.createQuery(countQueryForLeadMaster);
			if(buId!=null && !buId.isEmpty()){
				queryTotal.setParameter("buIds",buId);
			}
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadMasterPojo>(leadMasterPojoList, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Page<LeadMasterPojo> findAll(Long mvnoId, List<Long> buIds, List<Integer> serviceareaid,
										PaginationRequestDTO paginationRequestDTO,Integer loggedInUserId) {
		String SUBMODULE = MODULE + "findAll()";
//		StringBuilder serviceAreaIdsList = new StringBuilder();
//		StringBuilder staffIdList = new StringBuilder();
//
//		String queryForLeadMaster= "";
//		String countQueryForLeadMaster = "";
		QLeadMaster qLeadMaster=QLeadMaster.leadMaster;
		BooleanExpression booleanExpression=qLeadMaster.isNotNull().and(qLeadMaster.isDeleted.eq(false));
		// Get Staff IDs
		List<Integer> staffIds = getStaffIds(loggedInUserId);
//		if (staffIds == null) {
//			staffIds = new ArrayList<>();
//		}

List<Long> mvnoIdlist=new ArrayList<>();
		mvnoIdlist.add(1L);
		// Prepare service area ID string
//		for (int i = 0; i < serviceareaid.size(); i++) {
//			if (i > 0) {
//				serviceAreaIdsList.append(", ");
//			}
//			serviceAreaIdsList.append(serviceareaid.get(i));
//		}
//		for (int i = 0; i < staffIds.size(); i++) {
//			if (i > 0) {
//				staffIdList.append(", ");
//			}
//			staffIdList.append(staffIds.get(i));
//		}

		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());

		try {
		List<String> statusList=new ArrayList<>();
			statusList.add("Inquiry");
			statusList.add("Re-Inquiry");
			statusList.add("Rejected");
			ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT, mvnoId);

			if(clientService!=null && clientService.getValue().equalsIgnoreCase("true")){
//				booleanExpression=booleanExpression.and(qLeadMaster.leadStatus.in(statusList))
//						.and(qLeadMaster.nextApproveStaffId.in(staffIds)).or(qLeadMaster.nextApproveStaffId.isNull()).or(qLeadMaster.createdBy.eq(String.valueOf(loggedInUserId)));
                booleanExpression = booleanExpression
                        .and(qLeadMaster.leadStatus.in(statusList))
                        .and(qLeadMaster.finalApproved.eq(false))
                        .and(qLeadMaster.cafConvertedDate.isNull())
                        .and(qLeadMaster.nextApproveStaffId.in(staffIds)
                                        .or(qLeadMaster.nextApproveStaffId.isNull())
                                        .or(qLeadMaster.createdBy.eq(String.valueOf(loggedInUserId))));
				// Only updated WHERE condition here ⬇
//				queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry' OR lm.leadStatus='Rejected') AND " +
//						"(lm.nextApproveStaffId IN  (" + staffIdList.toString() + ") or lm.nextApproveStaffId IS NULL    OR lm.createdBy = "+loggedInUserId +")";
//
//				countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry' OR lm.leadStatus='Rejected') AND " +
//						"(lm.nextApproveStaffId IN (" + staffIdList.toString() + ") or lm.nextApproveStaffId IS NULL OR lm.createdBy = "+loggedInUserId +")";

			}else{
				booleanExpression=booleanExpression.and(qLeadMaster.leadStatus.in(statusList));
//				queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry' OR lm.leadStatus='Rejected')";
//				countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry' OR lm.leadStatus='Rejected')";

			}
			if (mvnoId != null) {
				if (mvnoId != 1) {
					mvnoIdlist.add(mvnoId);
					booleanExpression=booleanExpression.and(qLeadMaster.mvnoId.in(mvnoIdlist));
//					queryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId IS '1' OR lm.mvnoId=" + mvnoId + ")";
//					countQueryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId=" + mvnoId + ")";
				}
			}
			StaffUser loggedinuser=staffUserRepository.findById(loggedInUserId).orElse(null);
			if(Objects.nonNull(loggedinuser) && Objects.nonNull(loggedinuser.getLcoId())){
				booleanExpression=booleanExpression.and(qLeadMaster.partnerid.eq(loggedinuser.getLcoId()));
			}

			if (buIds != null && !buIds.isEmpty()) {
				booleanExpression=booleanExpression.and(qLeadMaster.buId.in(buIds));
//				queryForLeadMaster += " AND (lm.buId IN :buIds)";
//				countQueryForLeadMaster += " AND (lm.buId IN :buIds)";
			}
		if (!serviceareaid.isEmpty() && buIds != null && !buIds.isEmpty() ) {
//				queryForLeadMaster += " AND (lm.serviceareaid IN (" + serviceAreaIdsList.toString() + "))";
//				countQueryForLeadMaster += " AND (lm.serviceareaid IN (" + serviceAreaIdsList.toString() + "))";
			List<Long> serviceAreaIdsLong = serviceareaid.stream()
					.map(Integer::longValue)
					.collect(Collectors.toList());
			booleanExpression=booleanExpression.and(qLeadMaster.serviceareaid.in( serviceAreaIdsLong));
			}
			QBusinessUnit qBusinessUnit = QBusinessUnit.businessUnit;
			booleanExpression = booleanExpression.and(
					qLeadMaster.buId.isNull()  // If buId is null, then it will be shown
							.or(qBusinessUnit.planBindingType.ne("On-Demand"))
			);

//			else if(!serviceareaid.isEmpty() && buIds != null && !buIds.isEmpty()){
//			queryForLeadMaster += " AND (lm.serviceareaid IN (" + serviceAreaIdsList.toString() + "))";
//			countQueryForLeadMaster += " AND (lm.serviceareaid IN (" + serviceAreaIdsList.toString() + "))";
//		}

//			queryForLeadMaster += " order by lm.id DESC";
//			Query q = entityManager.createQuery(queryForLeadMaster, LeadMaster.class);




//			if (buIds != null && !buIds.isEmpty()) {
//				q.setParameter("buIds", buIds);
//			}
//			List<LeadMaster> leadMasterList = q.getResultList();
//			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
//			for (LeadMaster leadMaster : leadMasterList) {
//				ClientService clientServices = clientServiceSrv.getByNameAndMvnoId(LEAD_REOPEN_IN_DAYS,leadMaster.getMvnoId());
//				if (clientServices != null)
//					leadMasterPojoList.add(new LeadMasterPojo(leadMaster, Long.valueOf(clientServices.getValue())));
//			}
//			leadMasterPojoList.forEach(lead -> {
//				if (lead != null && lead.getNextApproveStaffId() != null) {
//					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
//					if (optionalStaffUser.isPresent()) {
//						StaffUser staffUser = optionalStaffUser.get();
//						if (staffUser != null && staffUser.getUsername() != null) {
//							lead.setAssigneeName(staffUser.getUsername());
//						}
//					}
//				}
//				if (lead != null && lead.getCafConvertedStaffId() != null) {
//					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getCafConvertedStaffId());
//					if (optionalStaffUser.isPresent()) {
//						StaffUser staffUser = optionalStaffUser.get();
//						if (staffUser != null && staffUser.getUsername() != null) {
//							lead.setCafCovertedStaffName(staffUser.getUsername());
//						}
//					}
//				}
//				if (lead != null) {
//					List<LeadFollowUp> leadFollowUpList = leadFollowUpRepository.findByLeadMasterId(lead.getId());
//					if (leadFollowUpList != null) {
//						lead.setLeadFollowUpCount(leadFollowUpList.size());
//					}
//					Mvno mvno = mvnoRepository.findById(lead.getMvnoId()).orElse(null);
//					if(mvno!=null){
//						lead.setMvnoName(mvno.getName());
//					}
//
//				}
//			});
//			Query queryTotal = entityManager.createQuery(countQueryForLeadMaster);
			//long countResult = (long) queryTotal.getSingleResult();
			QMvno qMvno=QMvno.mvno;
			QStaffUser qNextApproveStaffUser = new QStaffUser("nextApproveStaffUser");
//			QStaffUser qAssignedStaffUser = new QStaffUser("assignedStaffUser");
			QLeadSource qLeadSource=QLeadSource.leadSource;
			QLeadSubSource qLeadSubSource=QLeadSubSource.leadSubSource;
			QCustomers qCustomers=QCustomers.customers;
			QPartner qPartner=QPartner.partner;
			QBranch qBranch=QBranch.branch;
			QServiceArea qServiceArea=QServiceArea.serviceArea;
			JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
			QueryResults<LeadMasterPojo> queryResults = queryFactory
					.select(Projections.constructor(
							LeadMasterPojo.class,
							qLeadMaster.title,
							qLeadMaster.firstname,
							qLeadMaster.lastname,
							qLeadMaster.leadNo,
							qLeadMaster.mobile,
							qLeadSource.leadSourceName,
							qLeadSubSource.leadSubSourceName,
							qLeadSubSource.id,
							qBranch.name,
							qCustomers.firstname,
							qPartner.name,
							qServiceArea.name,
							qNextApproveStaffUser.firstname,
							qLeadSource.id,
							qLeadMaster.leadAgentId,
							qBranch.id,
							qCustomers.id,
							qPartner.id,
							qServiceArea.id,
							qLeadMaster.nextApproveStaffId,
							qLeadMaster.leadStatus,
							qLeadMaster.cstatus,
							qNextApproveStaffUser.username,
							qMvno.name,
							qLeadMaster.createdBy,
							qLeadMaster.createdByName,
							qLeadMaster.cafConvertedDate,
							qLeadMaster.nextApproveStaffId,
							qLeadMaster.id,
							qLeadMaster.finalApproved,
							qLeadMaster.buId,
							qLeadMaster.nextTeamMappingId,
							qLeadMaster.username,
							qLeadMaster.serviceareaid,
							qNextApproveStaffUser.username,
							qLeadMaster.status,
							qLeadMaster.nextfollowupdate,
							qLeadMaster.nextfollowuptime,
							qLeadMaster.mvnoId
					))
					.from(qLeadMaster)
					.leftJoin(qMvno).on(qLeadMaster.mvnoId.eq(qMvno.id))
					.leftJoin(qNextApproveStaffUser).on(qLeadMaster.nextApproveStaffId.eq(qNextApproveStaffUser.id.intValue())).fetchJoin()
					.leftJoin(qLeadSource).on(qLeadMaster.leadSource.eq(qLeadSource)).fetchJoin()
					.leftJoin(qLeadSubSource).on(qLeadMaster.leadSubSource.id.eq(qLeadSubSource.id)).fetchJoin()
//					.leftJoin(qAssignedStaffUser).on(qLeadMaster.staffUser.eq(qAssignedStaffUser)).fetchJoin()
					.leftJoin(qCustomers).on(qLeadMaster.customers.id.eq(qCustomers.id)).fetchJoin()
					.leftJoin(qPartner).on(qLeadMaster.partnerid.eq(qPartner.id)).fetchJoin()
					.leftJoin(qBranch).on(qLeadMaster.branch.id.eq(qBranch.id)).fetchJoin()
					.leftJoin(qServiceArea).on(qLeadMaster.serviceArea.eq(qServiceArea)).fetchJoin()
					.leftJoin(qBusinessUnit).on(qLeadMaster.buId.eq(qBusinessUnit.id))
					.where(booleanExpression)
					.orderBy(qLeadMaster.id.desc())
					.offset((paginationRequestDTO.getPage() - 1) * pageRequest.getPageSize())
					.limit(pageRequest.getPageSize())
					.fetchResults();

			List<LeadMasterPojo> leadMasterPojos = queryResults.getResults();
			long totalRecords = queryResults.getTotal();

			return new PageImpl<>(leadMasterPojos, PageRequest.of(paginationRequestDTO.getPage() - 1, paginationRequestDTO.getPageSize()), totalRecords);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public String generateLeadNo() {
		String SUBMODULE = MODULE + "generateLeadNo()";
		try {
			String leadNo = "M-";
			List<LeadMasterSequence> leadMasterSequenceList = this.leadMasterSequenceService.findAll();
			LeadMasterSequence leadMasterSequence = leadMasterSequenceList.get(0);
			leadNo += leadMasterSequence.getId();
			// update sequence
			Integer newId = Integer.parseInt(leadMasterSequence.getId()) + 1;
			this.leadMasterSequenceService.updateSeq(leadMasterSequence.getId(), newId.toString());
			return leadNo;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Page<LeadNotesDto> findAllLeadNoteWithPagination(PaginationRequestDTO paginationRequestDTO, Long id) {
		String SUBMODULE = MODULE + "findAllLeadNoteWithPagination()";
		try {
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());

			Optional<LeadMaster> optionalLead = leadMasterRepository.findById(id);
			if (optionalLead.isPresent()) {
				Page<LeadNotesDto> pojoPageList = this.leadNotesRepository
						.findByLeadMaster(optionalLead.get(), pageRequest).map(data -> new LeadNotesDto(data));
				return pojoPageList;
			} else {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"Lead master is not found for ID : " + id, null);
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public LeadMasterPojo assignWorkFlow(Long leadMasterId, Long staffId) {
		Optional<LeadMaster> optionalLeadMaster = this.leadMasterRepository.findById(leadMasterId);
		LeadMaster exstingLeadMaster = new LeadMaster();
		if (optionalLeadMaster.isPresent()) {
			// Setting values in leadMgmtWfDTO and send to the apigetway for workflow
			exstingLeadMaster = optionalLeadMaster.get();
			LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
			leadMgmtWfDTO.setBuId(exstingLeadMaster.getBuId());
			leadMgmtWfDTO.setFirstname(exstingLeadMaster.getFirstname());
			leadMgmtWfDTO.setStatus(exstingLeadMaster.getLeadStatus());
			leadMgmtWfDTO.setNextTeamMappingId(null);
			leadMgmtWfDTO.setNextApproveStaffId(null);
			leadMgmtWfDTO.setId(exstingLeadMaster.getId());
			leadMgmtWfDTO.setMvnoId(exstingLeadMaster.getMvnoId());
			leadMgmtWfDTO.setServiceareaid(exstingLeadMaster.getServiceareaid());
			leadMgmtWfDTO.setCurrentLoggedInStaffId(staffId.intValue());
			SendSaveLeadData sendSaveLeadData = new SendSaveLeadData(leadMgmtWfDTO);
//			messageSender.send(sendSaveLeadData, RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA);
			kafkaMessageSender.send(new KafkaMessageData(sendSaveLeadData,SendSaveLeadData.class.getSimpleName(),"ASSIGN_WORKFLOW"));

		}
		return new LeadMasterPojo(exstingLeadMaster);
	}

	@Override
	public Page<LeadMasterPojo> findByCurrentUser(PaginationRequestDTO paginationRequestDTO, Long staffId, Long mvnoId,
			List<Long> buId) {
		String SUBMODULE = MODULE + "findByCurrentUser()";
		try {
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			String queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry')";
			String countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry')";

			if (staffId != null) {
				queryForLeadMaster += " AND lm.nextApproveStaffId=" + staffId + "";
				countQueryForLeadMaster += " AND lm.nextApproveStaffId=" + staffId + "";
			}

			if (mvnoId != null) {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId=" + mvnoId +" or mvnoId=1 )";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId=" + mvnoId + ")";
			} else {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL)";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL)";
			}

			if (buId != null && !buId.isEmpty() && buId.size()>0) {
				StringJoiner joiner = new StringJoiner(",", "(", ")");
				for (Long num : buId) {
					joiner.add(String.valueOf(num));
				}
				queryForLeadMaster += " AND (lm.buId IS NULL OR lm.buId IN " + joiner.toString() + ")";
				countQueryForLeadMaster += " AND (lm.buId IS NULL OR lm.buId IN " + joiner.toString() + ")";
			} else {
				queryForLeadMaster += " AND (lm.buId IS NULL)";
				countQueryForLeadMaster += " AND (lm.buId IS NULL)";
			}

			queryForLeadMaster += " order by lm.id DESC";
			Query q = entityManager.createQuery(queryForLeadMaster, LeadMaster.class);
			List<LeadMaster> leadMasterList = q.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
					.setMaxResults(pageRequest.getPageSize()).getResultList();
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			for (LeadMaster leadMaster : leadMasterList) {
				leadMasterPojoList.add(new LeadMasterPojo(leadMaster));
			}
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
			});
			Query queryTotal = entityManager.createQuery(countQueryForLeadMaster);
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadMasterPojo>(leadMasterPojoList, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}


    @Override
    public int getLeadCountForCurrentUser(Integer nextApproveStaffId) {
        return leadMasterRepository.countNewActivation(nextApproveStaffId);
    }

	@Override
	public Page<LeadMasterPojo> findByCurrentUserTeamLeadList(PaginationRequestDTO paginationRequestDTO, Long staffId,
			Long mvnoId, List<Long> buIds) {
		String SUBMODULE = MODULE + "findByCurrentUserTeamLeadList()";
		try {
			Set<Long> staffIds = this.teamUserMappingService.findByStaffIds(staffId.intValue());
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			String queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry')";
			String countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false AND (lm.leadStatus='Inquiry' OR lm.leadStatus='Re-Inquiry')";

			if (staffIds != null && staffIds.size() > 0) {
				queryForLeadMaster += " AND lm.nextApproveStaffId in(";
				countQueryForLeadMaster += " AND lm.nextApproveStaffId in(";
				for (Long staffid : staffIds) {
					queryForLeadMaster += "" + staffid + ",";
					countQueryForLeadMaster += "" + staffid + ",";
				}
				queryForLeadMaster = removeLastChar(queryForLeadMaster);
				countQueryForLeadMaster = removeLastChar(countQueryForLeadMaster);
				queryForLeadMaster += ")";
				countQueryForLeadMaster += ")";
			}

			if (mvnoId != null) {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId=" + mvnoId + ")";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId=" + mvnoId + ")";
			} else {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL)";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL)";
			}

			if (buIds != null && !buIds.isEmpty() && buIds.size()>0) {
				StringJoiner joiner = new StringJoiner(",", "(", ")");
				for (Long num : buIds) {
					joiner.add(String.valueOf(num));
				}
	//			String result = joiner.toString();

				queryForLeadMaster += " AND (lm.buId IS NULL OR lm.buId IN " + joiner.toString() + ")";
				countQueryForLeadMaster += " AND (lm.buId IS NULL OR lm.buId IN " + joiner.toString() + ")";
			} else {
				queryForLeadMaster += " AND (lm.buId IS NULL)";
				countQueryForLeadMaster += " AND (lm.buId IS NULL)";
			}

			queryForLeadMaster += " order by lm.id DESC";
			Query q = entityManager.createQuery(queryForLeadMaster, LeadMaster.class);
			List<LeadMaster> leadMasterList = q.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
					.setMaxResults(pageRequest.getPageSize()).getResultList();
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			for (LeadMaster leadMaster : leadMasterList) {
				leadMasterPojoList.add(new LeadMasterPojo(leadMaster));
			}
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
				if (lead != null) {
					List<LeadFollowUp> leadFollowUpList = leadFollowUpRepository.findByLeadMasterId(lead.getId());
					if (leadFollowUpList != null) {
						lead.setLeadFollowUpCount(leadFollowUpList.size());
					}
				}
			});
			Query queryTotal = entityManager.createQuery(countQueryForLeadMaster);
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadMasterPojo>(leadMasterPojoList, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public List<LeadMasterPojo> findByusername(Long mvnoId, Long buId, String username) {
		String SUBMODULE = MODULE + "findByusername()";
		try {
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			List<LeadMaster> leadMasterList = null;
			if (buId == null && mvnoId != null) {
				leadMasterList = this.leadMasterRepository.findAllByUsernameAndMvnoId(username, mvnoId);
			} else {
				leadMasterList = this.leadMasterRepository.findAllByUsernameAndMvnoIdAndBuId(username, mvnoId, buId);
			}
			leadMasterList.forEach(data -> leadMasterPojoList.add(new LeadMasterPojo(data)));
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = this.staffUserRepository
							.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
			});
			return leadMasterPojoList;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public String removeLastChar(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, s.length() - 1);
	}

	@Override
	public String generateCafNo() {
		String SUBMODULE = MODULE + "generateCafNo()";
		try {
			String cafNo = "";
			List<CafNoSequence> cafNoSequenceList = this.cafNoSequenceService.findAll();
			CafNoSequence cafNoSequence = cafNoSequenceList.get(0);
			cafNo += cafNoSequence.getId();
			// update sequence
			Integer newId = Integer.parseInt(cafNoSequence.getId()) + 1;
			this.cafNoSequenceService.updateSeq(cafNoSequence.getId(), newId.toString());
			return cafNo;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	@Transactional
	public LeadMasterPojo newService(LeadMasterPojo pojo) {
		if (pojo.getId() != null) {
			Optional<LeadMaster> leadMaster = leadMasterRepository.findById(pojo.getId());
			if (leadMaster.isPresent()) {
				leadMaster.get().setFailcount(pojo.getFailcount());
				leadMaster.get().setCusttype(pojo.getCusttype());
				leadMaster.get().setCountryCode(pojo.getCountryCode());
				leadMaster.get().setCafno(pojo.getCafno());
				leadMaster.get().setCalendarType(pojo.getCalendarType());
				leadMaster.get().setPartnerid(pojo.getPartnerid());
				leadMaster.get().setServiceareaid(pojo.getServiceareaid());
				leadMaster.get().setId(pojo.getId());

				LeadMaster savedLeadMaster = leadMasterRepository.save(leadMaster.get());

				if (pojo.getPlanMappingList() != null && !pojo.getPlanMappingList().isEmpty()) {
					List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
					for (CustPlanMapppingPojo custPlanMapppingPojo : pojo.getPlanMappingList()) {
						LeadServiceMapping leadServiceMapping = new LeadServiceMapping(custPlanMapppingPojo);
						leadServiceMapping.setLeadId(pojo.getId());
						leadServiceMapping.setPlanId(custPlanMapppingPojo.getPlanId().longValue());
						leadServiceMapping = generateConnectionNumber(leadServiceMapping);
						LeadServiceMapping leadServiceMappingSaved = leadServiceMappingRepository
								.save(leadServiceMapping);

						PostpaidPlan plan = new PostpaidPlan(custPlanMapppingPojo.getPostpaidPlanPojo());
						plan = postpaidPlanRepository.save(plan);

						if (custPlanMapppingPojo.getPostpaidPlanPojo() != null
								&& custPlanMapppingPojo.getPostpaidPlanPojo().getProductplanmappingList() != null
								&& !custPlanMapppingPojo.getPostpaidPlanPojo().getProductplanmappingList().isEmpty()) {
							for (Productplanmappingdto productplanmappingdto : custPlanMapppingPojo
									.getPostpaidPlanPojo().getProductplanmappingList()) {
								Product product = new Product();
								product.setApigwProductId(productplanmappingdto.getProductId());
								product.setName(productplanmappingdto.getName());
								product = productRepository.save(product);

								ProductPlanMapping productPlanMapping = new ProductPlanMapping();
								productPlanMapping.setProduct(product);
								productPlanMapping.setProduct_type(productplanmappingdto.getProduct_type());
								productPlanMapping.setProductCategoryId(productplanmappingdto.getProductCategoryId());
								productPlanMapping.setOwnershipType(productplanmappingdto.getOwnershipType());
								productPlanMapping.setQuantity(productplanmappingdto.getProductQuantity());
								productPlanMapping.setRevisedCharge(productplanmappingdto.getRevisedCharge());
								productPlanMapping.setApigwProductPlanMappingId(productplanmappingdto.getId());
								productPlanMapping.setPostPaidPlan(plan);
								productPlanMapping.setPlanName(plan.getName());

								productPlanMappingRepository.save(productPlanMapping);
							}
						}

						if (custPlanMapppingPojo.getPostpaidPlanPojo() != null
								&& custPlanMapppingPojo.getPostpaidPlanPojo().getChargeList() != null
								&& !custPlanMapppingPojo.getPostpaidPlanPojo().getChargeList().isEmpty()) {
							for (PostpaidPlanChargePojo postpaidPlanChargePojo : custPlanMapppingPojo
									.getPostpaidPlanPojo().getChargeList()) {
								Charge charge = new Charge(postpaidPlanChargePojo.getCharge());
								charge.setIsDelete(false);
								charge = chargeRepository.save(charge);

								PostpaidPlanCharge postpaidPlanCharge = new PostpaidPlanCharge(postpaidPlanChargePojo);
								postpaidPlanCharge.setCharge(charge);
								postpaidPlanCharge.setPlan(plan);
								postpaidPlanChargeRepository.save(postpaidPlanCharge);
							}
						}

						CustPlanMappping custPlanMappping = new CustPlanMappping(custPlanMapppingPojo);
						custPlanMappping.setLeadMaster(savedLeadMaster);
						custPlanMappping.setStatus(SalesCrmsConstants.QUOTATION_STATUS_NEW_ACTIVATION);
						CustPlanMappping custPlanMapppingSaved = custPlanMapppingRepository.save(custPlanMappping);
						custPlanMapppingSaved.setLinkAcceptanceDTO(new LinkAcceptanceDTO(leadServiceMappingSaved));
						custPlanMapppingList.add(custPlanMapppingSaved);

					}
					savedLeadMaster.setPlanMappingList(custPlanMapppingList);

				}
				return new LeadMasterPojo(savedLeadMaster);
			} else {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"leadMaster not found for id:" + pojo.getId(), null);
			}

		}
		return null;

	}

	@Override
	public List<LeadServiceMapping> findCircuitDetailsByLeadId(Long leadId) {
		List<LeadServiceMapping> leadServiceMappingList = leadServiceMappingRepository.findByLeadId(leadId);
		leadServiceMappingList.sort(Comparator.comparing(LeadServiceMapping::getCreatedate).reversed());
		return leadServiceMappingList;
	}

	@Override
	public List<CustPlanMapppingPojo> findFinalServicesForLeadToCAFConvertion(Long leadId) {
		List<CustPlanMapppingPojo> custPlanMapppingPojos = new ArrayList<>();
		List<QuotationDetails> quotationDetails = quotationDetailsRepository.findAllByLeadId(leadId);
		if (!quotationDetails.isEmpty()) {
			QuotationDetails finalQuotation = quotationDetails.stream()
					.max(Comparator.comparing(QuotationDetails::getVersionId)).get();
			if (finalQuotation != null) {
				List<LeadServiceMapping> leadServiceMappingList = finalQuotation.getQuotationCircuitMappingList()
						.stream().map(qcm -> leadServiceMappingRepository.findById(qcm.getLeadServiceMappingId()).get())
						.collect(Collectors.toList());
				if (!leadServiceMappingList.isEmpty()) {
					for (LeadServiceMapping leadServiceMapping : leadServiceMappingList) {
						Long planId = leadServiceMapping.getPlanId();
						Optional<LeadMaster> leadMaster = leadMasterRepository.findById(leadServiceMapping.getLeadId());
						List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findAllByLeadMasterAndPlanId(leadMaster.get(), planId.intValue());
						if(custPlanMapppingList!=null && custPlanMapppingList.size()>0) {
							CustPlanMapppingPojo custPlanMapppingPojo = new CustPlanMapppingPojo(custPlanMapppingList.get(0));
							custPlanMapppingPojo.setLinkAcceptanceDTO(new LinkAcceptanceDTO(leadServiceMapping));
							custPlanMapppingPojos.add(custPlanMapppingPojo);
						}
					}
				}
			}
		} else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"leadServiceMapping not found for lead id:" + leadId, null);
		}
		return custPlanMapppingPojos;
	}

	@Override
	public LeadMasterPojo findLeadServiceMappingById(Long leadServiceMappingId) {
		LeadMasterPojo leadMasterPojo = new LeadMasterPojo();
				LeadServiceMapping leadServiceMapping = leadServiceMappingRepository.findById(leadServiceMappingId)
				.orElse(null);
		LinkAcceptanceDTO linkAcceptanceDTO = new LinkAcceptanceDTO(leadServiceMapping);
		PostpaidPlan postpaidPlan = new PostpaidPlan();
		if(leadServiceMapping!= null && leadServiceMapping.getPlanId()!= null) {
			postpaidPlan = postpaidPlanRepository.findByApiGatewayPlanId(leadServiceMapping.getPlanId());
		}
		if(postpaidPlan!= null) {
			PostpaidPlanPojo postpaidPlanPojo = new PostpaidPlanPojo(postpaidPlan);

			List<PostpaidPlanCharge> postpaidPlanChargeList = postpaidPlanChargeRepository
					.findByPlan_Id(postpaidPlan.getId());
			List<PostpaidPlanChargePojo> postpaidPlanChargePojos = new ArrayList<>();
			for (PostpaidPlanCharge postpaidPlanCharge : postpaidPlanChargeList) {
				PostpaidPlanChargePojo postpaidPlanChargePojo = new PostpaidPlanChargePojo(postpaidPlanCharge);
				postpaidPlanChargePojo.setPlan(postpaidPlanPojo);
				postpaidPlanChargePojo.setCharge(new ChargePojo(postpaidPlanCharge.getCharge()));
				postpaidPlanChargePojos.add(postpaidPlanChargePojo);
			}
			postpaidPlanPojo.setChargeList(postpaidPlanChargePojos);

			List<ProductPlanMapping> productPlanMappingList = productPlanMappingRepository
					.findByPostPaidPlan_id(postpaidPlan.getId());
			List<Productplanmappingdto> productplanmappingdtos = new ArrayList<>();
			for (ProductPlanMapping productPlanMapping : productPlanMappingList) {
				Productplanmappingdto productplanmappingdto = new Productplanmappingdto(productPlanMapping);
				productplanmappingdtos.add(productplanmappingdto);
			}
			postpaidPlanPojo.setProductplanmappingList(productplanmappingdtos);

			leadMasterPojo = findById(leadServiceMapping.getLeadId());
			List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
			if(leadMasterPojo!= null && leadMasterPojo.getPlanMappingList()!= null) {
				for (CustPlanMapppingPojo custPlanMapppingPojo : leadMasterPojo.getPlanMappingList()) {
					if (custPlanMapppingPojo.getPlanId().equals(postpaidPlanPojo.getId())) {
						custPlanMapppingPojo.setLinkAcceptanceDTO(linkAcceptanceDTO);
						custPlanMapppingPojo.setPostpaidPlanPojo(postpaidPlanPojo);
						custPlanMapppingPojoList.add(custPlanMapppingPojo);
					}
				}
				leadMasterPojo.setPlanMappingList(custPlanMapppingPojoList);
			}
		}
		return leadMasterPojo;
	}

	@Override
	@Transactional
	public LeadMasterPojo updateLeadService(LeadMasterPojo pojo, Long leadServiceMappingId) {
		Optional<LeadServiceMapping> leadServiceMappingOptional = leadServiceMappingRepository
				.findById(leadServiceMappingId);
		if (!leadServiceMappingOptional.isPresent()) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"leadServiceMapping not found for lead service mapping id:" + leadServiceMappingId, null);
		} else {
			if (pojo.getId() != null) {
				Optional<LeadMaster> leadMaster = leadMasterRepository.findById(pojo.getId());
				if (leadMaster.isPresent()) {
					leadMaster.get().setFailcount(pojo.getFailcount());
					leadMaster.get().setCusttype(pojo.getCusttype());
					leadMaster.get().setCountryCode(pojo.getCountryCode());
					leadMaster.get().setCafno(pojo.getCafno());
					leadMaster.get().setCalendarType(pojo.getCalendarType());
					leadMaster.get().setPartnerid(pojo.getPartnerid());
					leadMaster.get().setServiceareaid(pojo.getServiceareaid());
					leadMaster.get().setId(pojo.getId());

					LeadMaster savedLeadMaster = leadMasterRepository.save(leadMaster.get());
					LeadServiceMapping leadServiceMapping = leadServiceMappingOptional.get();

					if (pojo.getPlanMappingList() != null && !pojo.getPlanMappingList().isEmpty()) {
						List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
						for (CustPlanMapppingPojo custPlanMapppingPojo : pojo.getPlanMappingList()) {
							LeadServiceMapping leadServiceMappingUpdated = new LeadServiceMapping(custPlanMapppingPojo);
							leadServiceMappingUpdated.setId(leadServiceMappingId);
							leadServiceMappingUpdated.setLeadId(savedLeadMaster.getId().longValue());
							leadServiceMappingUpdated.setConnectionNo(leadServiceMapping.getConnectionNo());
							leadServiceMappingUpdated = leadServiceMappingRepository.save(leadServiceMappingUpdated);

							PostpaidPlan existingPlan = postpaidPlanRepository.findByApiGatewayPlanId(
									custPlanMapppingPojo.getPostpaidPlanPojo().getId().longValue());
							PostpaidPlan plan = new PostpaidPlan(custPlanMapppingPojo.getPostpaidPlanPojo());
							plan.setId(existingPlan.getId());
							postpaidPlanRepository.save(plan);

							if (custPlanMapppingPojo.getPostpaidPlanPojo() != null
									&& custPlanMapppingPojo.getPostpaidPlanPojo().getProductplanmappingList() != null
									&& !custPlanMapppingPojo.getPostpaidPlanPojo().getProductplanmappingList()
									.isEmpty()) {
								for (Productplanmappingdto productplanmappingdto : custPlanMapppingPojo
										.getPostpaidPlanPojo().getProductplanmappingList()) {
									Product product = productRepository.findById(productplanmappingdto.getProductId())
											.orElse(null);
									if (product == null) {
										product = new Product();
									} else {
										product.setId(product.getId());
									}
									product.setName(productplanmappingdto.getName());
									product = productRepository.save(product);

									ProductPlanMapping productPlanMapping = productPlanMappingRepository
											.findByApigwProductPlanMappingId(productplanmappingdto.getId());
									if (productPlanMapping == null) {
										productPlanMapping = new ProductPlanMapping();
									} else {
										productPlanMapping.setId(productPlanMapping.getId());
									}
									productPlanMapping.setProduct(product);
									productPlanMapping.setProduct_type(productplanmappingdto.getProduct_type());
									productPlanMapping
											.setProductCategoryId(productplanmappingdto.getProductCategoryId());
									productPlanMapping.setOwnershipType(productplanmappingdto.getOwnershipType());
									productPlanMapping.setQuantity(productplanmappingdto.getProductQuantity());
									productPlanMapping.setRevisedCharge(productplanmappingdto.getRevisedCharge());
									productPlanMapping.setApigwProductPlanMappingId(productplanmappingdto.getId());
									productPlanMapping.setPostPaidPlan(plan);
									productPlanMapping.setPlanName(plan.getName());

									productPlanMappingRepository.save(productPlanMapping);
								}
							}

							if (custPlanMapppingPojo.getPostpaidPlanPojo() != null
									&& custPlanMapppingPojo.getPostpaidPlanPojo().getChargeList() != null
									&& !custPlanMapppingPojo.getPostpaidPlanPojo().getChargeList().isEmpty()) {
								for (PostpaidPlanChargePojo postpaidPlanChargePojo : custPlanMapppingPojo
										.getPostpaidPlanPojo().getChargeList()) {

									Charge charge = chargeRepository.findByApiGatewayChargeId(
											postpaidPlanChargePojo.getCharge().getId().longValue());
									if (charge == null) {
										charge = new Charge(postpaidPlanChargePojo.getCharge());
									} else {
										charge.setId(charge.getId());
										Charge chargeExisting = chargeRepository.findByApiGatewayChargeId(postpaidPlanChargePojo.getCharge().getId().longValue());
										Charge chargeUpdated = new Charge(postpaidPlanChargePojo.getCharge());
										if (chargeExisting != null) {
											chargeUpdated.setId(chargeExisting.getId());
										}
										chargeUpdated.setIsDelete(false);
										chargeUpdated = chargeRepository.save(chargeUpdated);

										List<PostpaidPlanCharge> postpaidPlanChargeList = postpaidPlanChargeRepository
												.findByPlan_IdAndCharge_Id(plan.getId(), charge.getId());
										if (postpaidPlanChargeList != null && !postpaidPlanChargeList.isEmpty()) {
											postpaidPlanChargeRepository.deleteAll(postpaidPlanChargeList);
										}

										PostpaidPlanCharge postpaidPlanCharge = postpaidPlanChargeRepository
												.findByApiGatewayPlanChargeId(postpaidPlanChargePojo.getId().longValue());
										if (postpaidPlanCharge == null) {
											postpaidPlanCharge = new PostpaidPlanCharge(postpaidPlanChargePojo);
										} else {
											postpaidPlanCharge.setId(postpaidPlanCharge.getId());
										}
										postpaidPlanCharge.setCharge(charge);
										postpaidPlanCharge.setPlan(plan);
										postpaidPlanChargeRepository.save(postpaidPlanCharge);
									}
								}

								CustPlanMappping custPlanMappping = new CustPlanMappping(custPlanMapppingPojo);
								custPlanMappping.setId(custPlanMapppingPojo.getId());
								CustPlanMappping custPlanMapppingSaved = custPlanMapppingRepository.save(custPlanMappping);
								custPlanMapppingSaved
										.setLinkAcceptanceDTO(new LinkAcceptanceDTO(leadServiceMappingUpdated));
								custPlanMapppingList.add(custPlanMapppingSaved);
							}
							savedLeadMaster.setPlanMappingList(custPlanMapppingList);

						}
						return new LeadMasterPojo(savedLeadMaster);
					}
				}
			}
		}
			return null;
	}

	@Override
	public List<LeadMasterPojo> findAllByBuidList(SearchLeadByBuidDTO searchLeadByBuidDTO) {
		List<LeadMasterPojo> leadMasterPojoList = new ArrayList<>();
		List<LeadMaster> leadMasterList = new ArrayList<>();
		if (searchLeadByBuidDTO.getBuidIdList() != null && !searchLeadByBuidDTO.getBuidIdList().isEmpty()) {
			for (Long buid : searchLeadByBuidDTO.getBuidIdList()) {
				leadMasterList.addAll(leadMasterRepository.findAllByBuIdAndIsDeleted(buid, false));
			}
		}
		if (leadMasterList != null && !leadMasterList.isEmpty()) {

			leadMasterList.sort(Comparator.comparing(LeadMaster::getCreatedOn).reversed());
			leadMasterPojoList = leadMasterList.stream().map(lead -> new LeadMasterPojo(lead))
					.collect(Collectors.toList());
		}
		return leadMasterPojoList;
	}

	@Transactional
	public void updateLeadAssignApproverInfo(LeadMgmtWfDTO leadMgmtWfDTO) {
		String SUBMODULE = MODULE + "updateCustomerLeadAssignment()";
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
			DateTimeFormatter timeformatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
			LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadMgmtWfDTO.getId()).get();
			exstingLeadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
			exstingLeadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());

			if(leadMgmtWfDTO.getNextfollowuptime()!=null) {
				if (leadMgmtWfDTO.getNextfollowuptime().length() == 8) {
					exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(), timeformatter1));
				} else if (leadMgmtWfDTO.getNextfollowuptime().length() == 5) {
					exstingLeadMaster.setNextfollowuptime(LocalTime.parse(leadMgmtWfDTO.getNextfollowuptime(), timeformatter));
				} else {
					System.out.println("Invalid time format");
				}
			}
			if(leadMgmtWfDTO.getNextfollowupdate()!=null){
				LocalDate localDate = LocalDate.parse(leadMgmtWfDTO.getNextfollowupdate(), formatter);
				exstingLeadMaster.setNextfollowupdate(localDate);
			}
			leadMasterRepository.save(exstingLeadMaster);
			saveLeadWFAudit(exstingLeadMaster, leadMgmtWfDTO.getCurrentLoggedInStaffId(), leadMgmtWfDTO.getTeamName(),
					leadMgmtWfDTO.getFlag(), leadMgmtWfDTO.getNextApproveStaffId());
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Transactional
	public void updateLeadCustStatus(LeadStatusMessage leadStatusMessage) {
		String SUBMODULE = MODULE + "updateLeadCustStatus()";
		try {
			LeadMaster exstingLeadMaster = this.leadMasterRepository.findById(leadStatusMessage.getLeadId()).get();
			exstingLeadMaster.setCstatus(leadStatusMessage.getStatus());
			leadMasterRepository.save(exstingLeadMaster);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public LeadServiceMapping generateConnectionNumber(LeadServiceMapping mapping) {
		List<LeadServiceMapping> mappings = new ArrayList<>();
		mappings = leadServiceMappingRepository.findAll().stream()
				.filter(n -> n.getConnectionNo() != null && !n.getConnectionNo().isEmpty())
				.collect(Collectors.toList());
		if (mappings.size() > 0) {
			mappings.stream().sorted(Collections.reverseOrder());
			String preServiceNo = mappings.get(mappings.size() - 1).getConnectionNo();
			if (preServiceNo != null && !preServiceNo.equals("") && preServiceNo.length() != 0) {
				StringBuilder stringBuilder = new StringBuilder(preServiceNo);
				String id = stringBuilder.substring(8);
				long num = Long.parseLong(id);
				num = num + 1;
				String var = String.format("%09d", num);
				String newNum = CommonConstants.PREFIX_SER;
				String stringBuilders = newNum + LocalDateTime.now().getYear() + var;
				mapping.setConnectionNo(stringBuilders);
			}
			// else
			// mapping.setConnectionNo(CaseConstants.PREFIX_SER+LocalDateTime.now().getYear()+"000000001");
		} else
			mapping.setConnectionNo(CommonConstants.PREFIX_SER + LocalDateTime.now().getYear() + "000000001");
		return mapping;
	}

	@Transactional
	public void updateLeadStatus(LeadMasterPojoMessage leadMasterPojoMessage) {
		try {
			System.out.println("Lead to Caf conversion");
			LeadMaster leadMaster = leadMasterRepository.findById(leadMasterPojoMessage.getId()).get();
			leadMaster.setLeadStatus("Converted");
			leadMaster.setCafConvertedDate(LocalDate.now());
			leadMaster.setCafConvertedStaffId(leadMasterPojoMessage.getCurrentLoggedInStaffId());
			leadMasterRepository.save(leadMaster);
			System.out.println("Lead to Caf conversion");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	public LeadSourcePojo convertLeadSourceEntityToLeadSourcePojo(LeadSource leadSource) {
		LeadSourcePojo leadSourcePojo = new LeadSourcePojo();

		leadSourcePojo.setLeadSourceName(leadSource.getLeadSourceName());
		if (leadSource.getStatus() != null)
			leadSourcePojo.setStatus(leadSource.getStatus());
		leadSourcePojo.setId(leadSource.getId());
		leadSourcePojo.setIsDelete(leadSource.getIsDelete());
		if (leadSource.getBuId() != null)
			leadSourcePojo.setBuId(leadSource.getBuId());
		leadSourcePojo.setMvnoId(leadSource.getMvnoId());

		return leadSourcePojo;
	}

	@Override
	public Page<LeadMasterPojo> enterpriseSearch(Long mvnoId, PaginationRequestDTO paginationRequestDTO,
			String fromConvertedDate, String toConvertedDate) {
		String SUBMODULE = MODULE + "search()";
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {
			String queryForLeadMaster = "SELECT lm FROM LeadMaster lm WHERE lm.isDeleted = false";
			String countQueryForLeadMaster = "SELECT count(lm.id) FROM LeadMaster lm WHERE lm.isDeleted = false";

			if (mvnoId != null) {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId =1 OR lm.mvnoId=" + mvnoId + ")";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL OR lm.mvnoId =1 OR lm.mvnoId=" + mvnoId + ")";
			} else {
				queryForLeadMaster += " AND (lm.mvnoId IS NULL)";
				countQueryForLeadMaster += " AND (lm.mvnoId IS NULL)";
			}

			String buIds = "";
			Integer count = 0;
			if (paginationRequestDTO.getBuids() != null && paginationRequestDTO.getBuids().size() > 0) {
				for (Long businessUnit : paginationRequestDTO.getBuids()) {
					count += 1;
					String temp = String.valueOf(businessUnit);
					buIds += temp;
					if (count <= (paginationRequestDTO.getBuids().size() - 1))
						buIds += ",";
				}
				System.out.println(buIds);
			}

			if (buIds != null) {
				queryForLeadMaster += " AND (lm.buId IN (" + buIds + "))";
				countQueryForLeadMaster += " AND (lm.buId IN (" + buIds + "))";
			}
//			else {
//				queryForLeadMaster += " AND (lm.buId IS NULL)";
//				countQueryForLeadMaster += " AND (lm.buId IS NULL)";
//			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("name")) {
					queryForLeadMaster += " AND (lower(lm.firstname) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase()
							+ "%' OR lower(lm.lastname) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%')";
					countQueryForLeadMaster += " AND (lower(lm.firstname) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase()
							+ "%' OR lower(lm.lastname) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%')";
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("mobile")) {
					queryForLeadMaster += " AND lower(lm.mobile) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
					countQueryForLeadMaster += " AND lower(lm.mobile) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%'";
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("status")) {
					if (!paginationRequestDTO.getFilters().get(0).getFilterValue().equalsIgnoreCase("Converted")) {
						queryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						countQueryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
					}
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("createdBy")) {
					queryForLeadMaster += " AND lm.createdBy = '"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
					countQueryForLeadMaster += " AND lm.createdBy = '"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
				}
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("lastUpdateOn")) {
					String filterValue = paginationRequestDTO.getFilters().get(0).getFilterValue();
					String fromDate = filterValue + " 00:00:01";
					String toDate = filterValue + " 23:59:59";
					queryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate + "' AND lm.lastModifiedOn <= '"
							+ toDate + "'";
					countQueryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate + "' AND lm.lastModifiedOn <= '"
							+ toDate + "'";
				}
			}
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("status")) {
					if (paginationRequestDTO.getFilters().get(0).getFilterValue().equalsIgnoreCase("Converted")) {
						queryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						countQueryForLeadMaster += " AND lm.leadStatus = '"
								+ paginationRequestDTO.getFilters().get(0).getFilterValue() + "'";
						String filterValue = paginationRequestDTO.getFilters().get(0).getFilterValue();
						String fromDate = fromConvertedDate + " 00:00:01";
						String toDate = toConvertedDate + " 23:59:59";
						if (fromConvertedDate != null && toConvertedDate != null) {
							queryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate
									+ "' AND lm.lastModifiedOn <= '" + toDate + "'";
							countQueryForLeadMaster += "AND lm.lastModifiedOn >= '" + fromDate
									+ "' AND lm.lastModifiedOn <= '" + toDate + "'";
						}
					}
				}
			}

			queryForLeadMaster += " order by lm.id DESC";
			Query q = entityManager.createQuery(queryForLeadMaster, LeadMaster.class);
			List<LeadMaster> leadMasterList = q.getResultList();
			List<LeadMasterPojo> leadMasterPojoList = new ArrayList<LeadMasterPojo>();
			for (LeadMaster leadMaster : leadMasterList) {
				ClientService clientService = clientServiceSrv.getByNameAndMvnoId(LEAD_REOPEN_IN_DAYS,mvnoId);
				if (clientService != null)
					leadMasterPojoList.add(new LeadMasterPojo(leadMaster, Long.valueOf(clientService.getValue())));
			}
			leadMasterPojoList.forEach(lead -> {
				if (lead != null && lead.getNextApproveStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getNextApproveStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setAssigneeName(staffUser.getUsername());
						}
					}
				}
				if (lead != null && lead.getCafConvertedStaffId() != null) {
					Optional<StaffUser> optionalStaffUser = staffUserRepository.findById(lead.getCafConvertedStaffId());
					if (optionalStaffUser.isPresent()) {
						StaffUser staffUser = optionalStaffUser.get();
						if (staffUser != null && staffUser.getUsername() != null) {
							lead.setCafCovertedStaffName(staffUser.getUsername());
						}
					}
				}
			});

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0) {
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("serviceNames")) {
					if (paginationRequestDTO.getFilters().get(0).getFilterValue() != null
							&& !paginationRequestDTO.getFilters().get(0).getFilterValue().equalsIgnoreCase("")) {
						String services = paginationRequestDTO.getFilters().get(0).getFilterValue();
						List<String> serviceList = Arrays.asList(services);
						List<LeadServiceMapping> leadServiceMappingList = new ArrayList<>();
						for (String item : serviceList) {
							leadServiceMappingList.addAll(leadServiceMappingRepository.findAllByServiceName(item));
						}
						leadServiceMappingList.forEach(service -> {
							if (service.getLeadId() != null)
								leadMasterPojoList.stream().filter(obj -> obj.getId() == service.getLeadId())
										.collect(Collectors.toList());
						});
					}
				}
			}

			Query queryTotal = entityManager.createQuery(countQueryForLeadMaster);
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadMasterPojo>(leadMasterPojoList, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public List<CustPlanMapppingPojo> verifyPlansWithQuotationApproval(LeadMasterPojo leadMasterPojo) {

		List<CustPlanMapppingPojo> finalPlanMappingList = new ArrayList<>();

		if (leadMasterPojo != null && leadMasterPojo.getId() != null) {
			List<QuotationDetails> quotationDetailList = quotationDetailsRepository.findAllByLeadId(leadMasterPojo.getId());

			if (!quotationDetailList.isEmpty()) {
				Long versionNumber = quotationDetailList.stream()
						.filter(QuotationDetails::getFinalApproved)
						.mapToLong(QuotationDetails::getVersionId)
						.max()
						.orElse(0L);

				List<Long> leadServiceMapIds = quotationDetailList.stream()
						.filter(item -> item.getVersionId() == versionNumber)
						.findFirst()
						.map(quotationCircuitMappingRepository::findAllByQuotationDetails)
						.orElse(Collections.emptyList())
						.stream()
						.map(QuotationCircuitMapping::getLeadServiceMappingId)
						.collect(Collectors.toList());

				leadMasterPojo.getPlanMappingList().stream()
						.filter(item -> {
							Integer leadPlanId = item != null ? item.getPlanId() : null;
							return leadPlanId != null && leadServiceMapIds.stream()
									.anyMatch(leadServiceMapId -> leadServiceMappingRepository.findById(leadServiceMapId)
											.map(leadServiceMapping -> {
												item.setLinkAcceptanceDTO(new LinkAcceptanceDTO(leadServiceMapping));
												return leadPlanId.equals(Integer.parseInt(String.valueOf(leadServiceMapping.getPlanId())));
											})
											.orElse(false));
						})
						.forEach(finalPlanMappingList::add);
			}
		}
		return finalPlanMappingList;
	}

	@Override
	public String getLeadNameById(Long leadId) {
		Optional<LeadMaster> leadMasterOptional = leadMasterRepository.findById(leadId);

		if (leadMasterOptional.isPresent()) {
			return leadMasterOptional.get().getFirstname();
		}else {
			return null;
		}
	}

	public List<Integer>getStaffIds(Integer loggedInUserId){
		try{
			List<Integer> staffIds = staffUserRepository.findAllByParentStaffId(loggedInUserId);
			staffIds.add(loggedInUserId);
			return  staffIds;
		}catch (Exception e){
			ApplicationLogger.logger.error("something went wrong while fetching staffids to display leadmaster");
		}
		return null;
	}



}
