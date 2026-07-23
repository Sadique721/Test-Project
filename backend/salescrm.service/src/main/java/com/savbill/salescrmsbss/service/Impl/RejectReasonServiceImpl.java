package com.savbill.salescrmsbss.service.Impl;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.Mvno;
import com.savbill.salescrmsbss.entity.RejectReason;
import com.savbill.salescrmsbss.entity.RejectSubReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.helper.RejectReasonDto;
import com.savbill.salescrmsbss.helper.RejectSubReasonDto;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.MvnoRepository;
import com.savbill.salescrmsbss.repository.RejectReasonRepository;
import com.savbill.salescrmsbss.repository.RejectSubReasonRepository;
import com.savbill.salescrmsbss.service.AbstractService;
import com.savbill.salescrmsbss.service.RejectReasonService;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;

@Service
public class RejectReasonServiceImpl extends AbstractService<RejectReason, Long> implements RejectReasonService {

	public static final String MODULE = "[RejectReasonServiceImpl]";

	private final Logger logger = LoggerFactory.getLogger(LeadSourceServiceImpl.class);

	@Autowired
	private RejectReasonRepository rejectReasonRepository;

	@Autowired
	private RejectSubReasonRepository rejectSubReasonRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private MvnoRepository mvnoRepository;
	
	@Override
	public RejectReasonDto saveRejectReason(RejectReasonDto rejectReasonDto, Long mvnoId, Long buId) {
		String SUBMODULE = MODULE + "saveRejectReason()";
		try {
			if(mvnoId != null) {
				Optional<Mvno> optionalMvno = this.mvnoRepository.findById(mvnoId);
				if(!optionalMvno.isPresent()) {
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "MVNO is not set for Sales CRM module. Please configure that.", null);

				}
			}
			RejectReason rejectReason = new RejectReason(rejectReasonDto, mvnoId, buId);
			RejectReason savedRejectReason = this.rejectReasonRepository.save(rejectReason);
			if (savedRejectReason.getRejectSubReasonList() != null
					&& savedRejectReason.getRejectSubReasonList().size() > 0) {
				savedRejectReason.getRejectSubReasonList()
						.forEach(rejectSubReason -> rejectSubReason.setRejectReason(savedRejectReason));
				this.rejectSubReasonRepository.saveAll(savedRejectReason.getRejectSubReasonList());
			}
			logger.info("RejectReason has been created successfully");
			return new RejectReasonDto(savedRejectReason);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public RejectReasonDto updateRejectReason(RejectReasonDto rejectReasonDto, HttpServletRequest req) {
		Integer RESP_CODE = APIConstants.FAIL;
		RejectReason exstingRejectReason = this.rejectReasonRepository.findById(rejectReasonDto.getId()).get();
		try {
			RejectReason rejectReason = new RejectReason(rejectReasonDto, exstingRejectReason.getMvnoId(),
					exstingRejectReason.getBuId());
			if (rejectReasonDto.getRejectSubReasonDeletedIds() != null
					&& rejectReasonDto.getRejectSubReasonDeletedIds().size() > 0) {
				for (Long rejectSubReasonId : rejectReasonDto.getRejectSubReasonDeletedIds()) {
					Optional<RejectSubReason> optionalRejectSubReason = this.rejectSubReasonRepository
							.findById(rejectSubReasonId);
					if (optionalRejectSubReason.isPresent()) {
						RejectSubReason rejectSubReason = optionalRejectSubReason.get();
						List<LeadMaster> leadMasterList = this.leadMasterRepository
								.findByRejectSubReasonId(rejectSubReasonId);
						if (leadMasterList != null && leadMasterList.size() > 0) {
							throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
									"This operation will not allow as this " + rejectSubReason.getName()
											+ " RejectSubReason is used for Lead Master creation.",
									null);
						}
						this.rejectSubReasonRepository.deleteById(rejectSubReasonId);
					}
				}
			}
			if (rejectReason.getRejectSubReasonList() != null && rejectReason.getRejectSubReasonList().size() > 0) {
				rejectReason.getRejectSubReasonList()
						.forEach(rejectSubReason -> rejectSubReason.setRejectReason(rejectReason));
				this.rejectSubReasonRepository.saveAll(rejectReason.getRejectSubReasonList());
			}
			RejectReason updatedRejectReason = this.rejectReasonRepository.save(rejectReason);
			RESP_CODE = APIConstants.SUCCESS;
			logger.info(
					"RejectReason with old name : " + exstingRejectReason.getName() + " is updated to : "
							+ rejectReasonDto.getName()
							+ " updated Successfully; request: { From : {}, Request Url : {}}; Response : {{}}",
					req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE);
			return new RejectReasonDto(updatedRejectReason);
		} catch (Exception ex) {
			logger.info(
					"Unable to Update RejectReason with old name : " + exstingRejectReason.getName()
							+ " is updated to : " + rejectReasonDto.getName()
							+ " ; request: { From : {}, Request Url : {}}; Response : {{}};Exception:{}",
					req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE, ex.getMessage());
			throw ex;
		}
	}

	@Override
	public RejectReasonDto findById(Long id) {
		Optional<RejectReason> rejectReason = this.rejectReasonRepository.findById(id);
		if (rejectReason.isPresent()) {
			return new RejectReasonDto(rejectReason.get());
		} else {
			return null;
		}
	}

	@Override
	public RejectReason getByID(Long id) {
		return rejectReasonRepository.findById(id).get();
	}

	@Override
	public void validateRequest(RejectReasonDto dto, Long mvnoId, Integer operation) {
		if (dto == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Required object is not set",
					null);
		}
		if (dto != null && operation.equals(CommonConstants.OPERATION_ADD)) {
			if (dto.getId() != null)
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
						"Id should not be present in the JSON body.", null);
			if (!dto.getName().equalsIgnoreCase("")) {
				List<RejectReason> rejectReasonList = this.rejectReasonRepository
						.findByNameAndMvnoIdAndIsDelete(dto.getName(), mvnoId, false);
				if (rejectReasonList != null && rejectReasonList.size() > 0)
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
							"RejectReason already exit.", null);
			}
		}
		if (!(dto.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
				|| dto.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Inproper value for status.",
					null);
		}
		if (dto != null && dto.getName().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter name.", null);
		}
		if (dto != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
				|| operation.equals(CommonConstants.OPERATION_DELETE)) && dto.getId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.",
					null);
		}else{
			Boolean flag = duplicateValidation(dto, mvnoId);
			if (flag) {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "RejectReason already exist.", null);
			}
		}
	}

	@Override
	public void deleteRejectReason(Long rejectReasonId) {
		String SUBMODULE = MODULE + "deleteRejectReason()";
		try {
			RejectReason rejectReasonEntity = this.rejectReasonRepository.findById(rejectReasonId).get();
			if (Objects.nonNull(rejectReasonEntity)) {
				List<LeadMaster> findByLeadMasterList = this.leadMasterRepository
						.findByRejectReasonId(rejectReasonEntity.getId());
				if (findByLeadMasterList != null && findByLeadMasterList.size() > 0) {
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
							"This operation will not allow as this RejectReason is used for Lead Master creation.",
							null);
				}
				if (rejectReasonEntity.getRejectSubReasonList() != null
						&& rejectReasonEntity.getRejectSubReasonList().size() > 0) {
					this.rejectSubReasonRepository.deleteAll(rejectReasonEntity.getRejectSubReasonList());
				}
				rejectReasonEntity.setIsDelete(true);
				rejectReasonEntity.setRejectSubReasonList(null);
				this.rejectReasonRepository.save(rejectReasonEntity);
				logger.info("LeadSource has been deleted successfully: " + rejectReasonEntity.getName());
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Page<RejectReasonDto> search(PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "search()";
		try {
			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
					paginationRequestDTO.getSortOrder());
			Page<RejectReason> page = null;
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0)
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("name"))
					page = this.rejectReasonRepository.findByNameContainingAndIsDelete(
							paginationRequestDTO.getFilters().get(0).getFilterValue(), false, pageRequest);
			RejectReason rejectReason = new RejectReason();
			rejectReason.setIsDelete(false);
			if (page == null)
				page = this.rejectReasonRepository.findAll(Example.of(rejectReason), pageRequest);
			if (page != null)
				return page.map(this::convertToDto);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
		return null;
	}

	@Override
	public Page<RejectReasonDto> search(Long mvnoId, List<Long> buId, PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "search()";
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {
			String queryForRejectReason = "SELECT rr FROM RejectReason rr WHERE rr.isDelete = false";
			String countQueryForRejectReason = "SELECT count(rr.id) FROM RejectReason rr WHERE rr.isDelete = false";

			if (mvnoId != null) {
				queryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId IS '1' OR rr.mvnoId=" + mvnoId + ")";
				countQueryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId=" + mvnoId + ")";
			} else {
				queryForRejectReason += " AND (rr.mvnoId IS NULL)";
				countQueryForRejectReason += " AND (rr.mvnoId IS NULL)";
			}

			if (buId != null && !buId.isEmpty() && buId.size()>0) {
				queryForRejectReason += " AND (rr.buId IS NULL OR r.buId IN :buIds)";
				countQueryForRejectReason += " AND (rr.buId IS NULL OR rr.buId IN :buIds)";
			} else {
				queryForRejectReason += " AND (rr.buId IS NULL)";
				countQueryForRejectReason += " AND (rr.buId IS NULL)";
			}

			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0)
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("name")) {
					queryForRejectReason += " AND lower(rr.name) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
					countQueryForRejectReason += " AND lower(rr.name) LIKE '%"
							+ paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
				}
			Query q = entityManager.createQuery(queryForRejectReason, RejectReason.class);
			List<RejectReason> rejectReasonList = q.getResultList();
			List<RejectReasonDto> rejectReasonDtos = new ArrayList<RejectReasonDto>();
			for (RejectReason rejectReason : rejectReasonList) {
				rejectReasonDtos.add(new RejectReasonDto(rejectReason));
			}
			Query queryTotal = entityManager.createQuery(countQueryForRejectReason);
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<RejectReasonDto>(rejectReasonDtos, PageRequest.of(0, pageRequest.getPageSize()),
					countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public RejectReasonDto convertToDto(RejectReason rejectReason) {
		RejectReasonDto rejectReasonDto = new RejectReasonDto();
		rejectReasonDto.setId(rejectReason.getId());
		rejectReasonDto.setName(rejectReason.getName());
		rejectReasonDto.setStatus(rejectReason.getStatus());
		if (rejectReason.getRejectSubReasonList() != null && rejectReason.getRejectSubReasonList().size() > 0) {
			List<RejectSubReasonDto> rejectSubReasonDtoList = new ArrayList<RejectSubReasonDto>();
			for (RejectSubReason rejectSubReason : rejectReason.getRejectSubReasonList()) {
				rejectSubReasonDtoList.add(new RejectSubReasonDto(rejectSubReason));
			}
			rejectReasonDto.setRejectSubReasonDtoList(rejectSubReasonDtoList);
		}
		return rejectReasonDto;
	}

	@Override
	public Page<RejectReasonDto> findAll(PaginationRequestDTO paginationRequestDTO) {
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		Page<RejectReason> page = null;
		RejectReason rejectReason = new RejectReason();
		rejectReason.setIsDelete(false);
		page = this.rejectReasonRepository.findAll(Example.of(rejectReason), pageRequest);
		if (page != null)
			return page.map(this::convertToDto);
		return null;
	}

	@Override
	public Page<RejectReasonDto> findAll(Long mvnoId, List<Long> buIds, PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "findAll()";
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {
			String queryForRejectReason = "SELECT rr FROM RejectReason rr WHERE rr.isDelete = false";
			String countQueryForRejectReason = "SELECT count(rr.id) FROM RejectReason rr WHERE rr.isDelete = false";

			if (mvnoId != null) {
				if (mvnoId != 1) {
					queryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId IS '1' OR rr.mvnoId = " + mvnoId + ")";
					countQueryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId = " + mvnoId + ")";
				}
			} else {
				queryForRejectReason += " AND (rr.mvnoId IS NULL)";
				countQueryForRejectReason += " AND (rr.mvnoId IS NULL)";
			}

			if (buIds != null && !buIds.isEmpty()) {
				// Use a parameterized query for buIds using the IN clause
				queryForRejectReason += " AND (rr.buId IN :buIds)";
				countQueryForRejectReason += " AND (rr.buId IN :buIds)";
			}
			queryForRejectReason += " ORDER BY rr.id DESC";
			TypedQuery<RejectReason> q = entityManager.createQuery(queryForRejectReason, RejectReason.class);

			if (buIds != null && !buIds.isEmpty()) {
				// Set the buIds parameter
				q.setParameter("buIds", buIds);
			}

			List<RejectReason> rejectReasonList = q.getResultList();
			List<RejectReasonDto> rejectReasonDtos = new ArrayList<>();
			for (RejectReason rejectReason : rejectReasonList) {
				rejectReasonDtos.add(new RejectReasonDto(rejectReason));
			}
			Query queryTotal = entityManager.createQuery(countQueryForRejectReason);
			//long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<>(rejectReasonDtos, PageRequest.of(0, pageRequest.getPageSize()), rejectReasonDtos.size());
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public boolean isDuplicate(String name) {
		String reasonName = name.toLowerCase();
		Integer count = rejectReasonRepository.findByNameEqualsIgnoreCase(reasonName);
		if(count>=1){
			return true;
		}
		return false;
	}


	public List<RejectReason> findAllRejectedReasonsList() {
		String SUBMODULE = MODULE + "findAllRejectedReasonsList()";
		List<RejectReason> rejectReasonList = new ArrayList<>();
		rejectReasonList = rejectReasonRepository.findAllRejectedReasonsList();
		return rejectReasonList;
	}
	private Boolean duplicateValidation(RejectReasonDto dto , Long mvnoId) {
		Boolean flag = false;
		Integer count;
		if (mvnoId == 1) count = this.rejectReasonRepository.duplicateVerifyAtSave(dto.getName());
		else count = this.rejectReasonRepository.duplicateVerifyAtSave(dto.getName(), Arrays.asList(mvnoId, 1));
		if (count >= 1) {
			Integer countEdit;
			if (mvnoId == 1)
				countEdit = this.rejectReasonRepository.duplicateVerifyAtEdit(dto.getName(), dto.getId());
			else
				countEdit = this.rejectReasonRepository.duplicateVerifyAtEdit(dto.getName(), dto.getId(), Arrays.asList(mvnoId, 1));
			if (countEdit == 1){
				flag = false;
			}else {
				flag = true;
			}
		}
		return flag;
	}

}
