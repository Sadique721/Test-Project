//package com.savbill.salescrmsbss.rabbitMq;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import com.savbill.salescrmsbss.entity.*;
//import com.savbill.salescrmsbss.rabbitMq.message.*;
//import com.savbill.salescrmsbss.repository.*;
//import com.savbill.salescrmsbss.service.*;
//import com.savbill.salescrmsbss.service.Impl.PlanGroupService;
//import com.savbill.salescrmsbss.service.LeadQuotationService;
//import com.savbill.salescrmsbss.service.QuickInvoiceService;
//import com.savbill.salescrmsbss.service.RolesService;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.savbill.salescrmsbss.service.ClientServiceSrv;
//import com.savbill.salescrmsbss.service.Impl.LeadMasterServiceImpl;
//
//import javax.transaction.Transactional;
//
//@Component
//public class MessageReceiver {
//
//	private static Log log = LogFactory.getLog(MessageReceiver.class);
//
//	@Autowired
//	private CountryRepository countryRepository;
//
//	@Autowired
//	private RolesService rolesService;
//	@Autowired
//	private StateRepository stateRepository;
//
//	@Autowired
//	private CityRepository cityRepository;
//
//	@Autowired
//	private PincodeRepository pincodeRepository;
//
//	@Autowired
//	private AreaRepository areaRepository;
//
//	@Autowired
//	private ServiceAreaRepository serviceAreaRepository;
//
//	@Autowired
//	private PartnerRepository partnerRepository;
//
//	@Autowired
//	private ClientServiceSrv clientServiceSrv;
//
//	@Autowired
//	private PlanGroupRepository planGroupRepository;
//
//	@Autowired
//	private NetworkDevicesRepository networkDevicesRepository;
//
//	@Autowired
//	private RoleRepository roleRepository;
//
//	@Autowired
//	private CustomACLEntryRepository customACLEntryRepository;
//
//	@Autowired
//	private StaffUserRepository staffUserRepository;
//
//	@Autowired
//	private BusinessUnitRepository businessUnitRepository;
//
//	@Autowired
//	private LeadMasterServiceImpl leadMasterService;
//
//	@Autowired
//	private BranchRepository branchRepository;
//
//	@Autowired
//	private CustomersRepository customersRepository;
//
//	@Autowired
//	private MvnoRepository mvnoRepository;
//
//	@Autowired
//	private PopManagementRepository popManagementRepository;
//
//	@Autowired
//	private TeamsRepository teamsRepository;
//
//	@Autowired
//	private TeamUserMappingRepository teamUserMappingRepository;
//
//	@Autowired
//	private CustPlanMapppingRepository custPlanMapppingRepository;
//
//
//	@Autowired
//	private LeadQuotationService leadQuotationService;
//
//	@Autowired
//	private QuickInvoiceService quickInvoiceService;
//
//	@Autowired
//	private PlanGroupService planGroupService;
//	@Autowired
//	private MvnoServices mvnoServices;
//	@Autowired
//	private CustomerServices customerServices;
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_COUNTRY)
//	public void receiveCountry(SaveCountrySharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.countryRepository.save(new Country(message));
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_STATE)
//	public void receiveState(SaveStateSharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.stateRepository.save(new State(message));
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CITY)
//	public void receiveCity(SaveCitySharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.cityRepository.save(new City(message));
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PINCODE)
//	public void receivePincode(SavePincodeSharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.pincodeRepository.save(new Pincode(message));
//	}
//
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_AREA)
//	public void receiveArea(SaveAreaSharedDataMessage message) {
//		Pincode pincode = null;
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		Optional<Pincode> findById = this.pincodeRepository.findById(message.getPincode().getId().longValue());
//		if(findById.isPresent()) pincode = findById.get();
//		this.areaRepository.save(new Area(message));
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA)
//	public void receiveServiceArea(ServiceAreaMessage message) {
//		Area area = null;
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		Optional<Area> findById = this.areaRepository.findById(message.getAreaId());
//		if(findById.isPresent()) area = findById.get();
//		this.serviceAreaRepository.save(new ServiceArea(message,area));
//	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER)
////	public void receivePartner(PartnerMessage message) {
////		log.info("Received Message From RabbitMq : <" + message + ">");
////		this.partnerRepository.save(new Partner(message));
////	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_CLIENT_SERVICE)
////	public void receiveClientService(ClientServiceMessage message) {
////		log.info("Received Message From RabbitMq : <" + message + ">");
////		this.clientServiceSrv.save(new ClientService(message));
////	}
//
//	/*@RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_GROUP)
//	public void receivePlanGroup(PlanGroupMsg message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.planGroupRepository.save(new PlanGroup(message));
//	}*/
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_NETWORK_DEVICES)
////	public void receiveNetworkDevices(NetworkDevicesMessage message) {
////		ServiceArea serviceArea = null;
////		log.info("Received Message From RabbitMq : <" + message + ">");
////		Optional<ServiceArea> findById = this.serviceAreaRepository.findById(message.getServiceareaId());
////		if(findById != null) serviceArea = findById.get();
////		this.networkDevicesRepository.save(new NetworkDevices(message,serviceArea));
////	}
//
//	@RabbitListener(queues = RabbitMqConstants.ROLE)
//	public void receiveRole(RoleMessage message) {
//		List<CustomACLEntry> customACLEntryList = new ArrayList<CustomACLEntry>();
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		Role savedRole = this.roleRepository.save(new Role(message));
//		for (CustomACLEntry customACLEntry : message.getAclEntryList()) {
//			customACLEntry.setRole(savedRole);
//			customACLEntryList.add(customACLEntry);
//		}
//		this.customACLEntryRepository.saveAll(customACLEntryList);
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_USER)
//	public void receiveStaffUser(UserMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		StaffUser staffUser = this.staffUserRepository.save(new StaffUser(message));
//		if(message.getTeamMessageList() != null &&
//				message.getTeamMessageList().size()>0) {
//			List<TeamUserMapping> teamUserMappingList = new ArrayList<TeamUserMapping>();
//			for (TeamsMessage teamsMessage : message.getTeamMessageList()) {
//				TeamUserMapping teamUserMapping = new TeamUserMapping();
//				teamUserMapping.setStaffId(staffUser.getId());
//				teamUserMapping.setTeamId(teamsMessage.getId());
//				teamUserMappingList.add(teamUserMapping);
//			}
//			this.teamUserMappingRepository.saveAll(teamUserMappingList);
//		}
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT)
//	public void receiveBusinessUnit(BusinessUnitMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.businessUnitRepository.save(new BusinessUnit(message));
//	}
//
//
//	//lead for workflow
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL)
//	public void receiveStaffForLead(SendApproverForLeadMsg message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//			this.leadMasterService.updateLeadApprover(message.getLeadFlowApproverData());
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_SEND_UPDATE_LEAD_INFO)
//	public void receiveUpdateForLead(SendUpdatedLeadInfo message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadMasterService.updateLeadApproverInfo(message.getLeadFlowApproverUpdatedData());
//	}
//
////	@RabbitListener(queues =  RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_DTO)
////	public void receiveTeamHierarchyDTOForLead(SendTeamHierarchyDTO message) {
////		log.info("Received Message From  RabbitMq : <" + message + ">");
////		this.leadMasterService.getLeadStatus(null,message.getTeamHierarchyDTO());
////	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH)
//	public void receiveBranch(BranchMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.branchRepository.save(new Branch(message));
//	}
//
////	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER)
////	public void receivepartner(PartnerMessage message) {
////		log.info("Received Message From  RabbitMq : <" + message + ">");
////		this.partnerRepository.save(new Partner(message));
////	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA)
//	public void receiveServicearea(ServiceAreaMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.serviceAreaRepository.save(new ServiceArea(message,null));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER)
//	public void receiveCustomer(CustomerMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.customersRepository.save(new Customers(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE)
//	public void receiveClientService(ClientServiceMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.clientServiceSrv.update(new ClientService(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_MVNO)
//	public void receiveMvno(MvnoMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.mvnoRepository.save(new Mvno(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT)
//	public void receivePopManagement(PopManagementMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.popManagementRepository.save(new PopManagement(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE)
//	public void receiveApiGWCustStatusUpdate(LeadStatusMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadMasterService.updateLeadCustStatus(new LeadStatusMessage(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS)
//	public void receiveTeams(TeamsMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.teamsRepository.save(new Teams(message));
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE)
//	public void receiveTeams(SendLeadAssignMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadMasterService.updateLeadAssignApproverInfo(message.getLeadMgmtWfDTO());
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.UPDATE_PLAN_PRICES_IN_CRM)
//	public void receiveUpdatePrice(UpdatePlanPricesMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findByPlanId(message.getPlanId().intValue());
//		if(custPlanMapppingList!=null && !custPlanMapppingList.isEmpty()) {
//			custPlanMapppingList = custPlanMapppingList.stream().map(custPlanMappping -> {
//					custPlanMappping.setOfferPrice(message.getOfferPriceUpdated());
//					custPlanMappping.setTaxAmount(message.getTaxAmountUpdated());
//					return custPlanMappping;
//					}
//			).collect(Collectors.toList());
//			custPlanMapppingRepository.saveAll(custPlanMapppingList);
//		}
//
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_LEAD_CAF_CONVERTION)
//	public void receiveLeadConvertion(LeadMasterPojoMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadMasterService.updateLeadStatus(message);
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION)
//	public void receieveApproverDetailsForQuotation(SendLeadQuotationMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadQuotationService.updateLeadQuotationApprover(message.getLeadQuotationWfDTO());
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE)
//	public void receiveTeams(SendLeadQuotationMessage message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		this.leadQuotationService.updateLeadQuotationAssignApproverInfo(message.getLeadQuotationWfDTO());
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM)
//	public void receivePlanGroup(PlanGroupMsg message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		System.out.println("Message : " + message);
//		try {
//			planGroupService.save(message);
//			System.out.println("success..!!");
//		} catch (Exception e) {
//			log.info("receiveMessageApigw Failed :" + e.getMessage());
//		}
//	}
//
//	@RabbitListener(queues =  RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM_UPDATE)
//	public void receiveUpdatedPlanGroup(PlanGroupMsg message) {
//		log.info("Received Message From  RabbitMq : <" + message + ">");
//		System.out.println("Message : " + message);
//		try {
//			planGroupService.update(message);
//			System.out.println("success..!!");
//		} catch (Exception e) {
//			log.info("receiveMessageApigw Failed :" + e.getMessage());
//		}
//	}
//
////	@RabbitListener(queues =  RabbitMqConstants.QUEUE_APIGW_LEAD_MILESTONES_MAPPING)
////	public void receiveQuickInvoicePojo(QuickInvoicePojoMessage message) {
////		log.info("Received Message From  RabbitMq : <" + message + ">");
////		QuickInvoicePojo quickInvoicePojo = new QuickInvoicePojo(message);
////		quickInvoiceService.saveLeadMasterWithMilestones(quickInvoicePojo);
////	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SALESCRM)
//	public void savePartner(SavePartnerSharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.partnerRepository.save(new Partner(message));
//	}
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM)
//	public void updatePartner(UpdatePartnerSharedDataMessage message) {
//		log.info("Received Message From RabbitMq : <" + message + ">");
//		this.partnerRepository.save(new Partner(message));
//	}
////Create Client Service from RabbitMQ
//@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//public void receiveMessageCreateClientService(SaveClientServMessge message) {
//	log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//	try {
//		clientServiceSrv.saveSharedClientService(message);
//		log.info("Client Service Created Successfully From Rms");
//	} catch (Exception e) {
//		log.error("receiveMessageCreateClientService Failed :" +e.getMessage());
//		throw new RuntimeException(e);
//	}
//}
//
//	//Update Client Service from RabbitMQ
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//	public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
//		log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//		try {
//			clientServiceSrv.updateSharedClientService(message);
//			log.info("Client Service Updated Successfully From Rms");
//		} catch (Exception e) {
//			log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
//			throw new RuntimeException(e);
//		}
//	}
//
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CRM)
//	public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
//		log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//		try {
//			rolesService.saveRole(message);
//			log.info("Client Service Updated Successfully From Rms");
//		} catch (Exception e) {
//			log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
//			throw new RuntimeException(e);
//		}
//
//	}
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CRM)
//	public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
//		log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//		try {
//			rolesService.deleteRole(message);
//			log.info("Client Service Updated Successfully From Rms");
//		} catch (Exception e) {
//			log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
//			throw new RuntimeException(e);
//		}
//
//	}
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM)
//	public void receiveMessageForCustomersUpdate(UpdateCustomerShareDataMessage message) {
//		log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
//		System.out.println("Message : " + message);
//		try {
//			customerServices.updateCustomers(message);
//		}
//		catch(Exception e) {
//			log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
//		}
//	}
//
//	@Transactional
//	@RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_SALES_CRM_ISP)
//	public void receiveMessageForMvnoIdUpdateISP(UpdateMvnoData message) {
//		log.info("Received Message From RabbitMq For Mvnoid Update, receiveMessage : <" + message + ">");
//		System.out.println("Message : " + message);
//		try {
//			mvnoServices.UpdateMvnoidISP(message.getOldmvnoId(), message.getNewmvnoId());
//		}
//		catch(Exception e) {
//			log.info("receiveMessageCustomerApigw Failed for Mvno Update :"+e.getMessage());
//		}
//	}
//	}
//
