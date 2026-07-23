package com.savbill.salescrmsbss.service.Impl;

import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.LeadSource;
import com.savbill.salescrmsbss.entity.LeadSubSource;
import com.savbill.salescrmsbss.entity.Mvno;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.LeadSourceDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.LeadSourceRepository;
import com.savbill.salescrmsbss.repository.LeadSubSourceRepository;
import com.savbill.salescrmsbss.repository.MvnoRepository;
import com.savbill.salescrmsbss.service.AbstractService;
import com.savbill.salescrmsbss.service.LeadSourceService;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeadSourceServiceImpl extends AbstractService<LeadSource, Long> implements LeadSourceService {

    public static final String MODULE = "[LeadSourceServiceImpl]";

	private final Logger logger = LoggerFactory.getLogger(LeadSourceServiceImpl.class);

	@Autowired
	private LeadSourceRepository leadSourceRepository;

	@Autowired
	private LeadSubSourceRepository leadSubSourceRepository;

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	private MvnoRepository mvnoRepository;
	
	@Override
	public void validateRequest(LeadSourceDto dto, Long mvnoId,Integer operation) {

		if (dto == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Required object is not set", null);
		}
		if (dto != null && operation.equals(CommonConstants.OPERATION_ADD)) {
			if (dto.getId() != null) {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id should not be present in the JSON body.",
						null);
			}
			if (!dto.getLeadSourceName().equalsIgnoreCase("")) {
				if (this.leadSourceRepository.countForAdd(dto.getLeadSourceName(), mvnoId) != 0) {
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "LeadSource already exit.", null);
				}
			}
		}
		if (dto != null && operation.equals(CommonConstants.OPERATION_UPDATE)) {
			if (!dto.getLeadSourceName().equalsIgnoreCase("")) {
				if (this.leadSourceRepository.findByLeadSourceNameAndMvnoIdAndIsDeleteFalseAndIdIsNot(dto.getLeadSourceName(), mvnoId, dto.getId()) != 0) {
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "LeadSource already exit.", null);
				}
			}
		}
		if (!(dto.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
				|| dto.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Inproper value for status.", null);
		}
		if (dto != null && dto.getLeadSourceName().equalsIgnoreCase("")) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Please enter name.", null);
		}
		if (dto != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
				|| operation.equals(CommonConstants.OPERATION_DELETE)) && dto.getId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.", null);
		}
	}

	@Override
	public LeadSourceDto saveLeadSource(LeadSourceDto leadSourceDto, Long mvnoId, Long buId) {
        String SUBMODULE = MODULE + "saveLeadSource()";
		try {
			if(mvnoId != null) {
				Optional<Mvno> optionalMvno = this.mvnoRepository.findById(mvnoId);
				if(!optionalMvno.isPresent()) {
					throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "MVNO is not set for Sales CRM module. Please configure that.", null);

				}
			}
			LeadSource leadSource = new LeadSource(leadSourceDto, mvnoId, buId);
			LeadSource savedLeadSource = this.leadSourceRepository.save(leadSource);
			savedLeadSource.getLeadSubSourceList().forEach(leadSubSource -> leadSubSource.setLeadSource(savedLeadSource));
			this.leadSubSourceRepository.saveAll(savedLeadSource.getLeadSubSourceList());
			leadSourceDto = new LeadSourceDto(savedLeadSource);
			logger.info("LeadSource has been created successfully");
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
		}
		return leadSourceDto;
	}

	@Override
	public LeadSourceDto updateLeadSource(LeadSourceDto leadSourceDto,HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        LeadSource exstingLeadSource = this.leadSourceRepository.findById(leadSourceDto.getId()).get();
		try {
			LeadSource leadSource = new LeadSource(leadSourceDto, exstingLeadSource.getMvnoId(),
					exstingLeadSource.getBuId());
			if (leadSourceDto.getLeadSubSourceDeletedIds() != null && leadSourceDto.getLeadSubSourceDeletedIds().size() > 0) {
				for (Long leadSubSourceId : leadSourceDto.getLeadSubSourceDeletedIds()) {
					LeadSubSource leadSubSource = this.leadSubSourceRepository.findById(leadSubSourceId).get();
					List<LeadMaster> leadMasterList = this.leadMasterRepository.findByLeadSubSourceId(leadSubSourceId);
					if (leadMasterList != null && leadMasterList.size() > 0) {
						throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
								"This operation will not allow as this "+leadSubSource.getLeadSubSourceName()+" LeadSubSource is used for Lead Master creation.",null);
					}
					this.leadSubSourceRepository.deleteById(leadSubSourceId);
				}
			}
			if (leadSource.getLeadSubSourceList() != null && leadSource.getLeadSubSourceList().size() > 0) {
				leadSource.getLeadSubSourceList().forEach(leadSubSource -> leadSubSource.setLeadSource(leadSource));
				this.leadSubSourceRepository.saveAll(leadSource.getLeadSubSourceList());
			}
			LeadSource savedLeadSource = this.leadSourceRepository.save(leadSource);
			leadSourceDto = new LeadSourceDto(savedLeadSource);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("LeadSource with old name : " + exstingLeadSource.getLeadSourceName() +  " is updated to : "+leadSourceDto.getLeadSourceName() +" updated Successfully; request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE);
		} catch (Exception ex) {
			logger.info("Unable to Update LeadSource with old name : " + exstingLeadSource.getLeadSourceName() +  " is updated to : "+leadSourceDto.getLeadSourceName() +" ; request: { From : {}, Request Url : {}}; Response : {{}};Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,ex.getMessage());
            throw ex;
		}
		return leadSourceDto;
	}

	@Override
	public void deleteLeadSource(Long leadSourceId) {
        String SUBMODULE = MODULE + "deleteLeadSource()";
			try {
				LeadSource leadSourceEntity = this.leadSourceRepository.findById(leadSourceId).get();
				if (Objects.nonNull(leadSourceEntity)) {
					List<LeadMaster> findByLeadMasterList = this.leadMasterRepository
							.findByLeadSourceId(leadSourceEntity.getId());
					if (findByLeadMasterList != null && findByLeadMasterList.size() > 0) {
						throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR,
								"This operation will not allow as this LeadSource is used for Lead Master creation.",null);
					}
					if (leadSourceEntity.getLeadSubSourceList() != null
							&& leadSourceEntity.getLeadSubSourceList().size() > 0) {
						this.leadSubSourceRepository.deleteAll(leadSourceEntity.getLeadSubSourceList());
					}
					leadSourceEntity.setIsDelete(true);
					leadSourceEntity.setLeadSubSourceList(null);
					this.leadSourceRepository.save(leadSourceEntity);
					logger.info("LeadSource has been deleted successfully: " + leadSourceEntity.getLeadSourceName());
				}
			} catch (Exception ex) {
				 logger.error(SUBMODULE + ex.getMessage(), ex);
		            throw ex;
			}
	}

	@Override
	public LeadSource findById(Long id) {
			Optional<LeadSource> leadSource = this.leadSourceRepository.findById(id);
			if (leadSource.isPresent()) {
				return leadSource.get();
			} else {
				return null;
			}
	}

	@Override
	public List<LeadSource> findByName(String name) {
		return this.leadSourceRepository.findByLeadSourceNameContaining(name).stream()
				.filter(e -> e.getIsDelete() == false).collect(Collectors.toList());
	}

//	@Override
//	public Page<LeadSourceDto> findAll(Long mvnoId,PaginationRequestDTO paginationRequestDTO) {
//		String SUBMODULE = MODULE + "findAll()";
//		try {
//			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
//					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//					paginationRequestDTO.getSortOrder());
//			Page<LeadSource> page = null;
//			LeadSource leadSource = new LeadSource();
//			leadSource.setIsDelete(false);
//			leadSource.setMvnoId(mvnoId);
//			page = this.leadSourceRepository.findByMvnoIdAndIsDeleteFalse(mvnoId, pageRequest);
//			if (page != null)
//				return page.map(this::convertToDto);
//		} catch (Exception ex) {
//			 logger.error(SUBMODULE + ex.getMessage(), ex);
//	            throw ex;
//		}
//		return null;
//	}
	
	@Override
	public List<LeadSourceDto> findAll() {
		String SUBMODULE = MODULE + "findAll()";
		List<LeadSource> leadSourceList = null;
		try {
			List<LeadSourceDto> leadSourceDtoList = new ArrayList<>();
			LeadSource leadSource = new LeadSource();
			leadSource.setIsDelete(false);
			leadSourceList = this.leadSourceRepository.findByIsDeleteFalse().stream().filter(data->data.getStatus().equalsIgnoreCase("Active")).collect(Collectors.toList());
			if (leadSourceList != null) {
				leadSourceList.forEach(e->leadSourceDtoList.add(convertToDto(e)));
				return leadSourceDtoList;
			}
		} catch (Exception ex) {
			 logger.error(SUBMODULE + ex.getMessage(), ex);
	            throw ex;
		}
		return null;
	}

//	@Override
//	public Page<LeadSource> search(Long mvnoId,PaginationRequestDTO paginationRequestDTO) {
//		String SUBMODULE = MODULE + "search()";
//		try {
//			PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
//					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//					paginationRequestDTO.getSortOrder());
//			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0)
//				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("leadSourceName"))
//					return this.leadSourceRepository.findByLeadSourceNameContainingAndMvnoIdAndIsDelete(
//							paginationRequestDTO.getFilters().get(0).getFilterValue(), mvnoId,false, pageRequest);
//			LeadSource leadSource = new LeadSource();
//			leadSource.setIsDelete(false);
//			leadSource.setMvnoId(mvnoId);
//			return this.leadSourceRepository.findAll(Example.of(leadSource), pageRequest);
//		} catch (Exception ex) {
//			 logger.error(SUBMODULE + ex.getMessage(), ex);
//	            throw ex;
//		}
//	}

	public LeadSourceDto convertToDto(LeadSource leadSource) {
		return new LeadSourceDto(leadSource);
	}
	
	@Override
	public List<LeadSourceDto> findAll(Long mvnoId,Long buId){
		List<LeadSourceDto> leadSourceDtoList = new ArrayList<LeadSourceDto>();
		String SUBMODULE = MODULE + "findAll()";
		try {
			String queryForLeadSource = "SELECT ls FROM LeadSource ls WHERE ls.isDelete = false";
			
			if (mvnoId != null) {
				if(mvnoId!=1){
					queryForLeadSource += " AND ( ls.mvnoId IS NULL or ls.mvnoId = 1 OR ls.mvnoId=" + mvnoId + ")";
				}
			} else {
				queryForLeadSource += " AND (ls.mvnoId IS NULL)";
			}
			
			if (buId != null) {
				queryForLeadSource += " AND (ls.buId IS NULL OR ls.buId=" + buId + ")";
			} else {
				queryForLeadSource += " AND (ls.buId IS NULL)";
			}

			Query q = entityManager.createQuery(queryForLeadSource, LeadSource.class);
			List<LeadSource> leadSourceList = q.getResultList();
			for (LeadSource leadSource : leadSourceList) {
				leadSourceDtoList.add(new LeadSourceDto(leadSource));
			}
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
		}
		return leadSourceDtoList;
	}

//	@Override
//	public Page<LeadSourceDto> findAll(Long mvnoId,List<Long> buId,PaginationRequestDTO paginationRequestDTO){
//		String SUBMODULE = MODULE + "findAll()";
//		Page<LeadSourceDto> leadSourceDtoList = null;
//		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
//				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//				paginationRequestDTO.getSortOrder());
//		try {
//			String queryForLeadSource = "SELECT ls FROM LeadSource ls WHERE ls.isDelete = false";
//			String countQueryForLeadSource = "SELECT count(ls.id) FROM LeadSource ls WHERE ls.isDelete = false";
//
//			if (mvnoId != null) {
//				if(mvnoId!=1){
//					queryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId IS '1' OR ls.mvnoId=" + mvnoId + ")";
//					countQueryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId=" + mvnoId + ")";
//				}
//			} else {
//				queryForLeadSource += " AND (ls.mvnoId IS NULL)";
//				countQueryForLeadSource += " AND (ls.mvnoId IS NULL)";
//			}
//
//			if (buId != null) {
//				queryForLeadSource += " AND (ls.buId IS NULL OR ls.buId IN" + buId + ")";
//				countQueryForLeadSource += " AND (ls.buId IS NULL OR ls.buId IN" + buId + ")";
//			}
//			queryForLeadSource +=" order by ls.id DESC";
//			Query q = entityManager.createQuery(queryForLeadSource, LeadSource.class);
//			List<LeadSource> leadSourceList = q.getResultList();
//			List<LeadSourceDto> leadSourceDtos = new ArrayList<LeadSourceDto>();
//			for (LeadSource leadSource : leadSourceList) {
//				leadSourceDtos.add(new LeadSourceDto(leadSource));
//			}
//			Query queryTotal = entityManager.createQuery(countQueryForLeadSource);
//			long countResult = (long) queryTotal.getSingleResult();
//			return new PageImpl<LeadSourceDto>(leadSourceDtos, PageRequest.of(0, pageRequest.getPageSize()), countResult);
//		} catch (Exception ex) {
//			logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//		}
//
////		return leadSourceDtoList;
//	}


	@Override
	public Page<LeadSourceDto> findAll(Long mvnoId, List<Long> buIds, PaginationRequestDTO paginationRequestDTO) {
		String SUBMODULE = MODULE + "findAll()";
		Page<LeadSourceDto> leadSourceDtoList = null;
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {

			String queryForLeadSource = "SELECT ls FROM LeadSource ls WHERE ls.isDelete = false";
			String countQueryForLeadSource = "SELECT COUNT(ls.id) FROM LeadSource ls WHERE ls.isDelete = false";

			if (mvnoId != null) {
				if (mvnoId != 1) {
					queryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId = 1 OR ls.mvnoId = " + mvnoId + ")";
					countQueryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId = " + mvnoId + ")";
				}
			} else {
				queryForLeadSource += " AND (ls.mvnoId IS NULL)";
				countQueryForLeadSource += " AND (ls.mvnoId IS NULL)";
			}

			if (buIds != null && !buIds.isEmpty()) {
				queryForLeadSource += " AND (ls.buId IN :buIds OR ls.buId IS NULL)";
				countQueryForLeadSource += " AND (ls.buId IN :buIds OR ls.buId IS NULL )";
			}

			queryForLeadSource += " ORDER BY ls.id DESC";
			TypedQuery<LeadSource> q = entityManager.createQuery(queryForLeadSource, LeadSource.class);

			if (buIds != null && !buIds.isEmpty()) {
				q.setParameter("buIds", buIds);
			}

			List<LeadSource> leadSourceList = q.getResultList();
			List<LeadSourceDto> leadSourceDtos = new ArrayList<>();
			for (LeadSource leadSource : leadSourceList) {
				leadSourceDtos.add(new LeadSourceDto(leadSource));
			}
			Query queryTotal = entityManager.createQuery(countQueryForLeadSource);
			//long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<>(leadSourceDtos, PageRequest.of(0, pageRequest.getPageSize()), leadSourceDtos.size());
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}


	@Override
	public Page<LeadSource> search(Long mvnoId,List<Long> buId,PaginationRequestDTO paginationRequestDTO){
		String SUBMODULE = MODULE + "search()";
		Page<LeadSource> leadSourcePageList = null;
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
				paginationRequestDTO.getSortOrder());
		try {
			String queryForLeadSource = "SELECT ls FROM LeadSource ls WHERE ls.isDelete = false";
			String countQueryForLeadSource = "SELECT count(ls.id) FROM LeadSource ls WHERE ls.isDelete = false";

			if (mvnoId != null) {
				queryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId IS '1' OR ls.mvnoId=" + mvnoId + ")";
				countQueryForLeadSource += " AND (ls.mvnoId IS NULL OR ls.mvnoId=" + mvnoId + ")";
			} else {
				queryForLeadSource += " AND (ls.mvnoId IS NULL)";
				countQueryForLeadSource += " AND (ls.mvnoId IS NULL)";
			}

			if (buId != null && !buId.isEmpty()) {
				queryForLeadSource += " AND (ls.buId IS NULL OR ls.buId IN :buId)";
				countQueryForLeadSource += " AND (ls.buId IS NULL OR ls.buId IN :buId)";
			} else {
				queryForLeadSource += " AND (ls.buId IS NULL)";
				countQueryForLeadSource += " AND (ls.buId IS NULL)";
			}
			
			if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0)
				if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("leadSourceName")) {
					queryForLeadSource += " AND lower(ls.leadSourceName) LIKE '%" + paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
					countQueryForLeadSource += " AND lower(ls.leadSourceName) LIKE '%" + paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
				}			
			Query q = entityManager.createQuery(queryForLeadSource, LeadSource.class);
			Query queryTotal = entityManager.createQuery(countQueryForLeadSource);
			if((buId != null && !buId.isEmpty())){
				q = q.setParameter("buId",buId);
				queryTotal = queryTotal.setParameter("buId",buId);
			}

			List<LeadSource> leadSourceList = q.getResultList();
			long countResult = (long) queryTotal.getSingleResult();
			return new PageImpl<LeadSource>(leadSourceList, PageRequest.of(0, pageRequest.getPageSize()), countResult);
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
		}

//		return leadSourcePageList;
	}


}
