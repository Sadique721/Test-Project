package com.savbill.salescrmsbss.service.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

//import com.savbill.salescrmsbss.rabbitMq.MessageSender;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.FollowUpRemark;
import com.savbill.salescrmsbss.entity.LeadAudit;
import com.savbill.salescrmsbss.entity.LeadFollowUp;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.FollowUpRemarkDto;
import com.savbill.salescrmsbss.helper.LeadFollowUpDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.repository.FollowUpRemarkRepository;
import com.savbill.salescrmsbss.repository.LeadAuditRepository;
import com.savbill.salescrmsbss.repository.LeadFollowUpRepository;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.savbill.salescrmsbss.service.AbstractService;
import com.savbill.salescrmsbss.service.LeadFollowUpService;
import com.savbill.salescrmsbss.service.TeamUserMappingService;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;

@Service
public class LeadFollowUpServiceImpl extends AbstractService<LeadFollowUp, Long> implements LeadFollowUpService {

	public static final String MODULE = "[LeadFollowUpServiceImpl]";
	public static final String FOLLOW_UP_STATUS_VALUE = "Pending";
	
	private final Logger logger = LoggerFactory.getLogger(LeadSourceServiceImpl.class);
	
	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

	@Autowired
	private LeadFollowUpRepository leadFollowUpRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private LeadAuditRepository leadAuditRepository;

	@Autowired
	private FollowUpRemarkRepository followUpRemarkRepository;
	
	@Autowired
	private TeamUserMappingService teamUserMappingService;

	@Autowired
	private LeadFollowUpService leadFollowUpService;

	@PersistenceContext
	EntityManager entityManager;
//	@Autowired
//	private MessageSender messageSender;
	@Override
	public LeadFollowUpDto save(LeadFollowUpDto leadFollowUpDto, Integer staffId) {
		String SUBMODULE = MODULE + "save()";
		try {
			LeadFollowUp leadFollowUp = new LeadFollowUp(leadFollowUpDto, staffId);
			leadFollowUp.setStatus("Pending");
			leadFollowUp = this.leadFollowUpRepository.save(leadFollowUp);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			// save audit entry
			if (staffId != null) {
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffId);
				if (optionalStaffUser.isPresent()) {
					StaffUser staffUser = optionalStaffUser.get();
					String auditName = staffUser.getFirstname() + " did " + leadFollowUp.getFollowUpName() + " for lead on "
							+ dateFormat.format(leadFollowUp.getCreatedOn());
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName(leadFollowUp.getFollowUpName()+" has been Created");
					leadAudit.setLeadMasterId(leadFollowUp.getLeadMaster().getId());
//					if(leadFollowUpDto.getFollowUpDatetime()!=null) {
//						Optional<LeadMaster> leadMaster = leadMasterRepository.findById(leadFollowUp.getLeadMaster().getId());
//						if (leadMaster.isPresent()) {
//							leadMaster.get().setNextfollowuptime(leadFollowUpDto.getFollowUpDatetime().toLocalTime());
//							leadMaster.get().setNextfollowupdate(leadFollowUpDto.getFollowUpDatetime().toLocalDate());
//						}
//						leadMasterRepository.save(leadMaster.get());
//						LeadMasterPojo updatedLeadMasterPojo = new LeadMasterPojo(leadMaster.get());
//						LeadMasterPojoMessage leadMasterPojoMessage = new LeadMasterPojoMessage(updatedLeadMasterPojo);
//				       messageSender.send(leadMasterPojoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER);
//					}
					leadAuditRepository.save(leadAudit);
				}
			}
			logger.info("LeadFollowUp has been created successfully");
			return new LeadFollowUpDto(leadFollowUp);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public LeadFollowUpDto update(LeadFollowUpDto leadFollowUpDto,HttpServletRequest req) {
		Integer RESP_CODE = APIConstants.FAIL;
		LeadFollowUp existingFollowUp = this.leadFollowUpRepository.findById(leadFollowUpDto.getId()).get();
		try {
			LeadFollowUp leadFollowUp = new LeadFollowUp(leadFollowUpDto, null);
			leadFollowUp.setCreatedBy(existingFollowUp.getCreatedBy());
			leadFollowUp.setCreatedOn(existingFollowUp.getCreatedOn());
			leadFollowUp.setStaffUser(existingFollowUp.getStaffUser());
			leadFollowUp = this.leadFollowUpRepository.save(leadFollowUp);
			logger.info(
					"LeadFollowUp with old name : " + existingFollowUp.getFollowUpName() + " is updated to : "
							+ leadFollowUpDto.getFollowUpName()
							+ " updated Successfully; request: { From : {}, Request Url : {}}; Response : {{}}",
					req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE);
			return new LeadFollowUpDto(leadFollowUp);
		} catch (Exception ex) {
			logger.info(
					"Unable to Update LeadFollowUp with old name : " + existingFollowUp.getFollowUpName()
							+ " is updated to : " + leadFollowUpDto.getFollowUpName()
							+ " ; request: { From : {}, Request Url : {}}; Response : {{}};Exception:{}",
					req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE, ex.getMessage());
			throw ex;
		}
	}

	@Override
	public List<LeadFollowUpDto> findAll() {
		return this.leadFollowUpRepository.findAll().stream().map(data -> new LeadFollowUpDto(data))
				.collect(Collectors.toList());
	}

	@Override
	public void validateRequest(LeadFollowUpDto leadFollowUpDto, Integer operation) {
		if (leadFollowUpDto == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Required object is not set",
					null);
		}

		if (leadFollowUpDto != null && operation.equals(CommonConstants.OPERATION_ADD)) {
			if (leadFollowUpDto.getId() != null)
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"Id should not be present in the JSON body.", null);
		}

		if (leadFollowUpDto != null && leadFollowUpDto.getFollowUpName().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter followUp name.",
					null);
		}

		if (leadFollowUpDto != null && leadFollowUpDto.getFollowUpDatetime() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
					"Please select followUp datetime.", null);
		}

		if (leadFollowUpDto != null && leadFollowUpDto.getLeadMasterId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please select lead.", null);
		}

		if (leadFollowUpDto != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
				|| operation.equals(CommonConstants.OPERATION_DELETE)) && leadFollowUpDto.getId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.",
					null);
		}
	}

	@Override
	public LeadFollowUpDto findById(Long followUpId) {
		Optional<LeadFollowUp> followUp = this.leadFollowUpRepository.findById(followUpId);
		if (followUp.isPresent())
			return new LeadFollowUpDto(followUp.get());
		return null;
	}

	@Override
	public List<StaffUser> findStaffUserByLeadId(Long leadId) {
		String SUBMODULE = MODULE + "findStaffUserByLeadId()";
		try {
			List<StaffUser> staffUserList = new ArrayList<StaffUser>();
			Optional<LeadMaster> leadMaster = this.leadMasterRepository.findById(leadId);
			if (!leadMaster.isPresent())
				return Collections.emptyList();
			LeadMaster exstingLeadMaster = leadMaster.get();
			if (exstingLeadMaster.getMvnoId() != null && exstingLeadMaster.getBuId() != null)
				staffUserList = this.staffUserRepository.findByMvnoIdAndBusinessUnitNameList(exstingLeadMaster.getMvnoId(),
						exstingLeadMaster.getBuId());
			else if (exstingLeadMaster.getMvnoId() == null && exstingLeadMaster.getBuId() != null)
				staffUserList = this.staffUserRepository.findByBusinessUnitNameList(exstingLeadMaster.getBuId());
			else if (exstingLeadMaster.getMvnoId() != null && exstingLeadMaster.getBuId() == null)
				staffUserList = this.staffUserRepository.findByMvnoId(exstingLeadMaster.getMvnoId());
			else {
				StaffUser staffUser = new StaffUser();
				staffUser.setIsDelete(false);
				staffUserList = this.staffUserRepository.findAll(Example.of(staffUser));
			}
			return staffUserList;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public void closeFollowUpAudit(LeadFollowUp leadFollowUp) {
		if (leadFollowUp.getStaffUser() != null) {
			StaffUser staffUser = leadFollowUp.getStaffUser();
			String auditName = staffUser.getFirstname() + " closed  " + leadFollowUp.getFollowUpName()
					+ " for lead on " + dateFormat.format(LocalDateTime.now());
			LeadAudit leadAudit = new LeadAudit();
			leadAudit.setName(auditName);
			leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
			leadAudit.setAuditName(leadFollowUp.getFollowUpName() + " has been Closed");
			leadAudit.setLeadMasterId(leadFollowUp.getLeadMaster().getId());
			leadAuditRepository.save(leadAudit);
		}
	}
	@Override
	public void closeFollowUp(Long followUpId, String remarks, Integer staffId) {
		String SUBMODULE = MODULE + "closeFollowUp()";
		try {
			LeadFollowUp leadFollowUp = this.leadFollowUpRepository.findById(followUpId).get();
			leadFollowUp.setStatus("Closed");
			leadFollowUp.setRemarks(remarks);
			this.leadFollowUpRepository.save(this.leadFollowUpRepository.findById(followUpId).get());
			// save audit entry
			closeFollowUpAudit(leadFollowUp);
				logger.info("LeadFollowUp has been closed successfully");
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public void closeAndReScheduleFollowUp(Long followUpId, String remarks, LeadFollowUpDto leadFollowUpDto,
			Integer staffId) {
		String SUBMODULE = MODULE + "closeAndReScheduleFollowUp()";
		try {
			LeadFollowUp leadFollowUp = this.leadFollowUpRepository.findById(followUpId).get();
			leadFollowUp.setStatus("Closed");
			leadFollowUp.setRemarks(remarks);
			closeFollowUpAudit(leadFollowUp);
			this.leadFollowUpRepository.save(this.leadFollowUpRepository.findById(followUpId).get());
			leadFollowUpDto.setLeadMasterId(leadFollowUp.getLeadMaster().getId());
			save(leadFollowUpDto, staffId);
			// save audit entry
			if (staffId != null) {
				Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffId);
				if (optionalStaffUser.isPresent()) {
					StaffUser staffUser = optionalStaffUser.get();
					String auditName = staffUser.getFirstname() + " reschedule  " + leadFollowUp.getFollowUpName()
							+ " for lead on " + dateFormat.format(LocalDateTime.now());
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName(leadFollowUp.getFollowUpName() + " has been Reschedule");
					leadAudit.setLeadMasterId(leadFollowUp.getLeadMaster().getId());
					leadAuditRepository.save(leadAudit);
				}
			}
			logger.info("LeadFollowUp has been reschedule successfully");
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public FollowUpRemarkDto saveFollowUpRemark(FollowUpRemarkDto followUpRemarkDto) {
		String SUBMODULE = MODULE + "saveFollowUpRemark()";
		try {
			FollowUpRemark savedFollowUpRemark = this.followUpRemarkRepository.save(new FollowUpRemark(followUpRemarkDto));
			
			//save followup remark audit
			if (savedFollowUpRemark.getLeadFollowUp().getId() != null) {
				LeadFollowUp leadFollowUp = this.leadFollowUpRepository.findById(savedFollowUpRemark.getLeadFollowUp().getId()).get();
					StaffUser staffUser = leadFollowUp.getStaffUser();
					String auditName = staffUser.getFirstname()+" added follow up remark in "+leadFollowUp.getFollowUpName()+".Remark: "+savedFollowUpRemark.getRemark()+".";
					LeadAudit leadAudit = new LeadAudit();
					leadAudit.setName(auditName);
					leadAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
					leadAudit.setAuditName("Followup Remark Added");
					leadAudit.setLeadMasterId(leadFollowUp.getLeadMaster().getId());
					leadAuditRepository.save(leadAudit);
			}
			logger.info("LeadFollowUp Remark has been created successfully");
			return new FollowUpRemarkDto(savedFollowUpRemark);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public List<FollowUpRemark> findAllFollowUpRemarkByFollowUpId(Long followUpId) {
		String SUBMODULE = MODULE + "findAllFollowUpRemarkByFollowUpId()";
		try {
			LeadFollowUp leadFollowUp = new LeadFollowUp();
			leadFollowUp.setId(followUpId);
			return this.followUpRemarkRepository.findByLeadFollowUp(leadFollowUp);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public List<LeadFollowUpDto> findAllByLeadId(Long leadId) {
		String SUBMODULE = MODULE + "findAllByLeadId()";
		try {
			LeadFollowUp leadFollowUp = new LeadFollowUp();
			leadFollowUp.setLeadMaster(new LeadMaster(leadId));
			return this.leadFollowUpRepository.findByLeadMasterId(leadId).stream().map(data -> new LeadFollowUpDto(data))
					.collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public String generateNameOfTheFollowUp(Long leadId) {
		String generatedNameOfTheFollowUp = "";
		Optional<LeadMaster> leadMaster = this.leadMasterRepository.findById(leadId);
		if (leadMaster.isPresent()) {
			//LeadFollowUp leadFollowUp = this.leadFollowUpRepository.findTopByOrderByIdDesc();
			List<LeadFollowUp> leadFollowUp = this.leadFollowUpRepository.findByLeadMasterId(leadMaster.get().getId());
			if (leadFollowUp != null) {
				//int num = leadFollowUp.getId().intValue() + 1;
				String s= String.valueOf(leadFollowUp.size()+1);
				generatedNameOfTheFollowUp = leadMaster.get().getFirstname() + "_Followup" + s;
			}else {
				generatedNameOfTheFollowUp = leadMaster.get().getFirstname() + "_Followup" + 1;
			}
			return generatedNameOfTheFollowUp;
		}else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Lead not found for ID : "+leadId,null);
		}
	}

	@Override
	public Page<LeadFollowUpDto> findAllByAssignId(Long staffUserId,PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "findAllByAssignId()";
		try {
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			return this.leadFollowUpRepository.findByStaffUserIdAndStatus(staffUserId.intValue(),FOLLOW_UP_STATUS_VALUE, pageRequest).map(data -> new LeadFollowUpDto(data));
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Page<LeadFollowUpDto> findAllByAssignIdAndTeam(Long assignId, PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "findAllByAssignIdAndTeam()";
		try {
			Set<Long> staffIds = this.teamUserMappingService.findByStaffIds(assignId.intValue());
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			String queryForLeadFollowUp = "SELECT lf FROM LeadFollowUp lf WHERE lf.status='Pending'";
			String countQueryForLeadFollowUp = "SELECT count(lf.id) FROM LeadFollowUp lf WHERE lf.status='Pending'";
			Long mvnoId = Long.valueOf(getLoggedInUser().getMvnoId());

			if (staffIds != null && staffIds.size() > 0) {
				queryForLeadFollowUp += " AND lf.staffUser.id in (";
				countQueryForLeadFollowUp += " AND lf.staffUser.id in (";
				for (Long staffid : staffIds) {
					queryForLeadFollowUp += ""+staffid+",";
					countQueryForLeadFollowUp += ""+staffid+",";
				}
				queryForLeadFollowUp = removeLastChar(queryForLeadFollowUp);
				countQueryForLeadFollowUp = removeLastChar(countQueryForLeadFollowUp);
				queryForLeadFollowUp += ")";
				countQueryForLeadFollowUp += ")";
			}

			queryForLeadFollowUp += " order by lf.id DESC";
			Query q = entityManager.createQuery(queryForLeadFollowUp, LeadFollowUp.class);
			List<LeadFollowUp> leadFollowUpList = q.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
					.setMaxResults(pageRequest.getPageSize()).getResultList();
			List<LeadFollowUpDto> leadFollowUpDtoList = new ArrayList<LeadFollowUpDto>();
			for (LeadFollowUp leadFollowUp : leadFollowUpList) {
				if (mvnoId.equals(leadFollowUp.getLeadMaster().getMvnoId())) {
					leadFollowUpDtoList.add(new LeadFollowUpDto(leadFollowUp));
				}
			}
			Query queryTotal = entityManager.createQuery(countQueryForLeadFollowUp);
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadFollowUpDto>(leadFollowUpDtoList, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public String getFollowUpNameById(Long followUpId) {
		Optional<LeadFollowUp> leadFollowUpOptional = leadFollowUpRepository.findById(followUpId);

		if (leadFollowUpOptional.isPresent()) {
			// Replace getName() with the actual method to get the follow-up name from your entity
			return leadFollowUpOptional.get().getFollowUpName();
		}else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Lead not found for ID : "+followUpId,null);
		}
	}

	public String removeLastChar(String s) {
	    if (s == null || s.length() == 0) {
	        return s;
	    }
	    return s.substring(0, s.length()-1);
	}
	public LoggedInUser getLoggedInUser() {
		LoggedInUser user = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			user = null;
		}
		return user;
	}

}
