package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.savbill.radius.dto.CDRSearchDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.helper.AcctCdrSearchDTO;
import com.savbill.radius.helper.AcctShowDTO;
import com.savbill.radius.repository.CustomersRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.repository.AcctCdrRepository;
import com.savbill.radius.services.AcctCdrService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import static com.savbill.radius.utils.RadiusUtils.notNullNotEmpty;

@Service
public class AcctCdrServiceImpl implements AcctCdrService {
	
	@Autowired
	private AcctCdrRepository acctCdrRepository;

	@Autowired
	StaffServiceImpl staffService;

	@Autowired
	CustomersRepository customersRepository;

	@Autowired
	private EntityManager entityManager;
	
	private static final Logger log = LoggerFactory.getLogger(AcctCdrServiceImpl.class);
	public Page<AcctCdr> findAcctCrdUsingFilter(CDRSearchDTO cdrSearchDTO, Integer mvnoId) {
		
		try {
			QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
			BooleanExpression boolExp = qAcctCdr.isNotNull();

			if (cdrSearchDTO != null) {

				if (notNullNotEmpty(cdrSearchDTO.getUserName()))
					boolExp = boolExp.and(qAcctCdr.userName.contains(cdrSearchDTO.getUserName()));

				if (notNullNotEmpty(cdrSearchDTO.getFramedIpAddress()))
					boolExp = boolExp.and(qAcctCdr.framedIpAddress.contains(cdrSearchDTO.getFramedIpAddress()));

				if (notNullNotEmpty(cdrSearchDTO.getNasIpAddress()))
					boolExp = boolExp.and(qAcctCdr.nasIpAddress.contains(cdrSearchDTO.getNasIpAddress()));

				if (notNullNotEmpty(cdrSearchDTO.getClassAttribute()))
					boolExp = boolExp.and(qAcctCdr.acctClass.contains(cdrSearchDTO.getClassAttribute()));

				if (notNullNotEmpty(cdrSearchDTO.getAcctStatusType()))
					boolExp = boolExp.and(qAcctCdr.acctStatusType.contains(cdrSearchDTO.getAcctStatusType()));

				if (notNullNotEmpty(cdrSearchDTO.getNasIdentifier()))
					boolExp = boolExp.and(qAcctCdr.nasIdentifier.contains(cdrSearchDTO.getNasIdentifier()));

				if (notNullNotEmpty(cdrSearchDTO.getNasPortId()))
					boolExp = boolExp.and(qAcctCdr.nasPortId.contains(cdrSearchDTO.getNasPortId()));

				if (notNullNotEmpty(cdrSearchDTO.getNasPortType()))
					boolExp = boolExp.and(qAcctCdr.nasPortType.contains(cdrSearchDTO.getNasPortType()));

				if (notNullNotEmpty(cdrSearchDTO.getFramedIpv6Address()))
					boolExp = boolExp.and(qAcctCdr.framedipv6address.contains(cdrSearchDTO.getFramedIpv6Address()));

				if (notNullNotEmpty(cdrSearchDTO.getFramedRoute()))
					boolExp = boolExp.and(qAcctCdr.framedRoute.contains(cdrSearchDTO.getFramedRoute()));

				if (notNullNotEmpty(cdrSearchDTO.getAcctSessionId()))
					boolExp = boolExp.and(qAcctCdr.acctSessionId.contains(cdrSearchDTO.getAcctSessionId()));

				if (notNullNotEmpty(cdrSearchDTO.getAcctMultiSessionId()))
					boolExp = boolExp.and(qAcctCdr.acctMultiSessionId.contains(cdrSearchDTO.getAcctMultiSessionId()));


				if (notNullNotEmpty(cdrSearchDTO.getFromDate())) {
					boolExp = boolExp.and(qAcctCdr.createdDate.eq(Timestamp.valueOf(cdrSearchDTO.getFromDate() + " 00:00:00"))
							.or(qAcctCdr.createdDate.after(Timestamp.valueOf(cdrSearchDTO.getFromDate() + " 00:00:00"))));
				}

				if (notNullNotEmpty(cdrSearchDTO.getToDate())) {
					boolExp = boolExp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(cdrSearchDTO.getToDate() + " 23:59:59"))
							.or(qAcctCdr.lastmodifiedDate.before(Timestamp.valueOf(cdrSearchDTO.getToDate() + " 23:59:59"))));
				}
				if (Objects.nonNull(cdrSearchDTO.getCustId())) {
					boolExp = boolExp.and(qAcctCdr.custid.eq(cdrSearchDTO.getCustId().toString()));
				}


				if(cdrSearchDTO.getPage() > 0) {
					cdrSearchDTO.setPage(cdrSearchDTO.getPage() - 1);
				}
			}

			if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1) {
				boolExp = boolExp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

			}

		    if(cdrSearchDTO == null || cdrSearchDTO.getSize() < 1) {
                return new PageImpl<AcctCdr>((List<AcctCdr>) acctCdrRepository.findAll(boolExp));
		    }

	    	Pageable pageable = PageRequest.of(cdrSearchDTO.getPage(), cdrSearchDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
			return  acctCdrRepository.findAll(boolExp, pageable);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	@Override
	public Page<AcctCdr> findAllAcctCdr(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request) {
		try {
			QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
			BooleanExpression exp = qAcctCdr.isNotNull();
			
			if (mvnoId != null && mvnoId != 1) {
				exp = exp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			} else if(mvnoId == 1 && paginationDTO == null) {
				Page<AcctCdr> page = new PageImpl<AcctCdr>(acctCdrRepository.findAll());
				return page;
			}
			if(paginationDTO.getPage() > 0) {
				paginationDTO.setPage(paginationDTO.getPage() - 1);
			}
			Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
			if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) 
			{
				exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
						.or(qAcctCdr.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
			}
			if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
						.or(qAcctCdr.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
			}
//			Integer staffIdFromApigw=Integer.valueOf(MDC.get("staffIdFromApigw").toString());
//			if(staffIdFromApigw != null && staffIdFromApigw != 0) {
//				List<Long> serviceAreaIds=staffService.ListOfIds(staffIdFromApigw);
//				if(!CollectionUtils.isEmpty(serviceAreaIds)) {
//					List<String> userName=customersRepository.findByServiceAreaIds(serviceAreaIds);
////					Set<String> userName=customers.stream().filter(customers1 -> serviceAreaIds.contains(customers1.getServicearea())).map(Customers::getUsername).collect(Collectors.toSet());
//					if(!CollectionUtils.isEmpty(userName)){
//						exp=exp.and(qAcctCdr.userName.in(userName));
//					}
//				}
//			}
			Page<AcctCdr> page = acctCdrRepository.findAll(exp, pageable);
			return page;
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteAcctCdrById(Long id, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		try {
			getAcctCdrById(id, mvnoId);
			acctCdrRepository.deleteById(id);
			log.info("AcctCdr deleted succefully: "+id);
		} catch (RuntimeException e) {
			log.error("Error while deleting AcctCdr: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public Page<AcctShowDTO> findAcctCdrByRequest(AcctCdrSearchDTO acctCdrSearchDTO , Integer mvnoId) {
		try {
			List<AcctShowDTO> acctCdrList = new ArrayList<>();
			List<AcctShowDTO> acctShowDTOListForBackup =  new ArrayList<>();
			long totalrecords = 0L;
			QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
			BooleanExpression exp = qAcctCdr.isNotNull();
			if(!(StringUtils.isBlank(acctCdrSearchDTO.getStartDate()) || acctCdrSearchDTO.getStartDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(acctCdrSearchDTO.getStartDate() + " 00:00:00"))
						.or(qAcctCdr.lastmodifiedDate.after(Timestamp.valueOf(acctCdrSearchDTO.getStartDate() + " 00:00:00"))));
			}
			if(!(StringUtils.isBlank(acctCdrSearchDTO.getEndDate()) || acctCdrSearchDTO.getEndDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(acctCdrSearchDTO.getEndDate() + " 23:59:59"))
						.or(qAcctCdr.lastmodifiedDate.before(Timestamp.valueOf(acctCdrSearchDTO.getEndDate() + " 23:59:59"))));
			}
			if(Objects.nonNull(acctCdrSearchDTO.getSearchDate())  && !acctCdrSearchDTO.getSearchDate().equalsIgnoreCase("")){
				exp = exp.and(qAcctCdr.createdDate.after(Timestamp.valueOf(acctCdrSearchDTO.getSearchDate()+ " 00:00:00")))
						.and(qAcctCdr.createdDate.before(Timestamp.valueOf(acctCdrSearchDTO.getSearchDate()+ " 23:59:59")));
			}
			if(acctCdrSearchDTO.getCustId() != null){
				exp = exp.and(qAcctCdr.custid.eq(acctCdrSearchDTO.getCustId().toString()));
			}
			JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
			QueryResults<AcctShowDTO>  queryResults = queryFactory
					                                  .select(Projections.constructor(AcctShowDTO.class,
															qAcctCdr.userName,
															  qAcctCdr.createdDate,
															  qAcctCdr.acctInputOctets,
															  qAcctCdr.acctOutputOctets,
															  qAcctCdr.acctSessionId,
															  qAcctCdr.acctSessionTime)).from(qAcctCdr).where(exp).orderBy(qAcctCdr.cdrId.desc()).offset((acctCdrSearchDTO.getPage()-1) * acctCdrSearchDTO.getPageSize()).limit(acctCdrSearchDTO.getPageSize()).fetchResults();
            List<AcctShowDTO> acctShowDTOList = queryResults.getResults();
			totalrecords = queryResults.getTotal();

			if((Objects.nonNull(acctCdrSearchDTO.getTimeFrame()) && !acctCdrSearchDTO.getTimeFrame().equalsIgnoreCase("") )|| (Objects.nonNull(acctCdrSearchDTO.getSearchDate()) && !acctCdrSearchDTO.getSearchDate().equalsIgnoreCase(""))){
			  QAcctCdrBackup qAcctCdrBackup = QAcctCdrBackup.acctCdrBackup;
			  BooleanExpression acctbackupexp = qAcctCdrBackup.isNotNull();
			  if(Objects.nonNull(acctCdrSearchDTO.getSearchDate())  && !acctCdrSearchDTO.getSearchDate().equalsIgnoreCase("")){
				acctbackupexp = acctbackupexp.and(qAcctCdrBackup.createdDate.after(Timestamp.valueOf(acctCdrSearchDTO.getSearchDate()+ " 00:00:00")))
						                     .and(qAcctCdrBackup.createdDate.before(Timestamp.valueOf(acctCdrSearchDTO.getSearchDate()+ " 23:59:59")));
			  }
			  if(Objects.nonNull(acctCdrSearchDTO.getCustId())){
                acctbackupexp = acctbackupexp.and(qAcctCdrBackup.custid.equalsIgnoreCase(acctCdrSearchDTO.getCustId().toString()));
			  }
			  if(Objects.nonNull(acctCdrSearchDTO.getTimeFrame()) &&acctCdrSearchDTO.getTimeFrame().equalsIgnoreCase("Week")){
                acctbackupexp =acctbackupexp.and(qAcctCdrBackup.createdDate.eq(Timestamp.valueOf(LocalDate.now() + " 00:00:00"))
						                    .or(qAcctCdrBackup.createdDate.after(Timestamp.valueOf(LocalDate.now().minusWeeks(1L)+ " 00:00:00"))));
			  }
			  if(Objects.nonNull(acctCdrSearchDTO.getTimeFrame()) && acctCdrSearchDTO.getTimeFrame().equalsIgnoreCase("Month")){
				  acctbackupexp =acctbackupexp.and(qAcctCdrBackup.createdDate.eq(Timestamp.valueOf(LocalDate.now().toString()+ " 00:00:00"))
						  .or(qAcctCdrBackup.createdDate.after(Timestamp.valueOf(LocalDate.now().minusMonths(1) +  " 00:00:00"))));
			  }
			  if(Objects.nonNull(acctCdrSearchDTO.getTimeFrame()) && acctCdrSearchDTO.getTimeFrame().equalsIgnoreCase("Last 6 Month")){
				  acctbackupexp =acctbackupexp.and(qAcctCdrBackup.createdDate.eq(Timestamp.valueOf(LocalDate.now()+ " 00:00:00"))
						  .or(qAcctCdrBackup.createdDate.after(Timestamp.valueOf(LocalDate.now().minusMonths(6) + " 00:00:00"))));
			  }
				if(!(StringUtils.isBlank(acctCdrSearchDTO.getStartDate()) || acctCdrSearchDTO.getStartDate().equalsIgnoreCase("null")))
				{
					acctbackupexp=acctbackupexp.and(qAcctCdrBackup.lastmodifiedDate.eq(Timestamp.valueOf(acctCdrSearchDTO.getStartDate() + " 00:00:00"))
							.or(qAcctCdrBackup.lastmodifiedDate.after(Timestamp.valueOf(acctCdrSearchDTO.getStartDate() + " 00:00:00"))));
				}
				if(!(StringUtils.isBlank(acctCdrSearchDTO.getEndDate()) || acctCdrSearchDTO.getEndDate().equalsIgnoreCase("null")))
				{
					acctbackupexp=acctbackupexp.and(qAcctCdrBackup.lastmodifiedDate.eq(Timestamp.valueOf(acctCdrSearchDTO.getEndDate() + " 23:59:59"))
							.or(qAcctCdrBackup.lastmodifiedDate.before(Timestamp.valueOf(acctCdrSearchDTO.getEndDate() + " 23:59:59"))));
				}

				JPAQueryFactory queryFactoryForBackup = new JPAQueryFactory(entityManager);
				QueryResults<AcctShowDTO>  queryResultsForBackup = queryFactoryForBackup
						.select(Projections.constructor(AcctShowDTO.class,
								qAcctCdrBackup.userName,
								qAcctCdrBackup.createdDate,
								qAcctCdrBackup.acctInputOctets,
								qAcctCdrBackup.acctOutputOctets,
								qAcctCdrBackup.acctSessionId,
								qAcctCdrBackup.acctSessionTime)).from(qAcctCdrBackup).where(acctbackupexp).orderBy(qAcctCdrBackup.cdrId.asc()).offset((acctCdrSearchDTO.getPage() -1) * acctCdrSearchDTO.getPageSize()).limit(acctCdrSearchDTO.getPageSize()).fetchResults();
				acctShowDTOListForBackup = queryResultsForBackup.getResults();
				totalrecords = totalrecords + queryResultsForBackup.getTotal();
			}

			acctCdrList.addAll(acctShowDTOListForBackup);
			acctCdrList.addAll(acctShowDTOList);
			acctCdrList = acctCdrList.stream().peek(acctShowDTO -> {
				acctShowDTO.setInputOctant(convertByteToMB(acctShowDTO.getInputOctant()));
				acctShowDTO.setOutputOctant(convertByteToMB(acctShowDTO.getOutputOctant()));
				acctShowDTO.setCreatedate(acctShowDTO.getCreatedatetimestamp().toLocalDateTime());
			}).collect(Collectors.toList());
			return new PageImpl<>(acctCdrList , PageRequest.of(acctCdrSearchDTO.getPage() -1  , acctCdrSearchDTO.getPageSize()) , totalrecords);

		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public AcctCdr findAcctCdrById(Long id, Integer mvnoId) {
		try {
			QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
			BooleanExpression exp = qAcctCdr.isNotNull();
			if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
				exp = exp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			exp = exp.and(qAcctCdr.cdrId.eq(id));
			Optional<AcctCdr> acctCdr = acctCdrRepository.findOne(exp);
			
			if (!acctCdr.isPresent()) {
				throw new IllegalArgumentException(
						"No record found with AcctCdr id " + id + " . Please enter valid AcctCdr id.");
			}
			
			return acctCdr.get();
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private AcctCdr getAcctCdrById(Long id, Integer mvnoId) {
		try {
			QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
			BooleanExpression exp = qAcctCdr.isNotNull();
			if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
				exp = exp.and(qAcctCdr.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			exp = exp.and(qAcctCdr.cdrId.eq(id));
			Optional<AcctCdr> acctCdr = acctCdrRepository.findOne(exp);

			if (!acctCdr.isPresent()) {
				throw new IllegalArgumentException(
						"You do not have access to delete or update this record.");
			}

			return acctCdr.get();

		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String convertByteToMB(String bytes){
		String mb = "";
        Double doublebytes = Double.parseDouble(bytes);
		Double megabytes = doublebytes/(1024 * 1024);
		mb = megabytes.toString();
        return mb;
	}


}
