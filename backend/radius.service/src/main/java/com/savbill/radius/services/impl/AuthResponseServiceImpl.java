package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.savbill.radius.entity.QClientGroup;
import com.savbill.radius.helper.RequestDto;
import com.savbill.radius.repository.CustomersRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
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
import com.savbill.radius.entity.AuthResponse;
import com.savbill.radius.entity.QAuthResponse;
import com.savbill.radius.repository.AuthResponseRepository;
import com.savbill.radius.services.AuthResponseService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuthResponseServiceImpl implements AuthResponseService {
	
	@Autowired
	private AuthResponseRepository authResponseRepository;

	@Autowired
	CustomersRepository customersRepository;

	@Autowired
	StaffServiceImpl staffService;

	@Autowired
	private EntityManager entityManager;
	
	private static final Logger log = LoggerFactory.getLogger(AuthResponseServiceImpl.class);
	
	@Override
	public Page<AuthResponse> findAuthResponseByUserName(PaginationDTO paginationDTO ,String userName, Integer mvnoId) {
		
		try {

			QAuthResponse qAuthResponse = QAuthResponse.authResponse;
			BooleanExpression exp = qAuthResponse.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				exp = exp.and(qAuthResponse.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			if(paginationDTO.getPage() > 0) {
				paginationDTO.setPage(paginationDTO.getPage() - 1);
			}
			Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "eventTime"));
			if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) 
			{
				exp=exp.and(qAuthResponse.eventTime.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
						.or(qAuthResponse.eventTime.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
			}
			if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAuthResponse.eventTime.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
						.or(qAuthResponse.eventTime.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
			}
			if (StringUtils.isBlank(userName) || userName.equalsIgnoreCase("null")) {
				return authResponseRepository.findAll(exp, pageable);
			} else {
				exp = exp.and(qAuthResponse.userName.containsIgnoreCase(userName));
				return authResponseRepository.findAll(exp, pageable);
			}
			
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public Page<AuthResponse> findAllAuthResponse(Integer mvnoId, PaginationDTO paginationDTO, RequestDto requestDto, HttpServletRequest request) {
		try {
			QAuthResponse qAuthResponse = QAuthResponse.authResponse;
			QClientGroup qClientGroup = QClientGroup.clientGroup;
			BooleanExpression exp = qAuthResponse.isNotNull();
			if (mvnoId != null && mvnoId != 1) {
				exp = exp.and(qAuthResponse.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			} else if(mvnoId == 1 && paginationDTO == null) {
				Page<AuthResponse> page = new PageImpl<AuthResponse>(authResponseRepository.findAll());
				return page;
			}
			if(paginationDTO.getPage() > 0) {
				paginationDTO.setPage(paginationDTO.getPage() - 1);
			}
			Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "eventTime"));
			if(!(StringUtils.isBlank(requestDto.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAuthResponse.eventTime.eq(Timestamp.valueOf(requestDto.getFromDate() + " 00:00:00"))
						.or(qAuthResponse.eventTime.after(Timestamp.valueOf(requestDto.getFromDate() + " 00:00:00"))));
			}
			if(!(StringUtils.isBlank(requestDto.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
			{
				exp=exp.and(qAuthResponse.eventTime.eq(Timestamp.valueOf(requestDto.getToDate() + " 23:59:59"))
						.or(qAuthResponse.eventTime.before(Timestamp.valueOf(requestDto.getToDate() + " 23:59:59"))));
			}
			if(!(Objects.isNull(requestDto.getClientgroup())))
			{
				exp=exp.and(qClientGroup.name.containsIgnoreCase(requestDto.getClientgroup()));
			}
			if(!(Objects.isNull(requestDto.getClientip())))
			{
				exp=exp.and(qAuthResponse.clientIp.containsIgnoreCase(requestDto.getClientip()));
			}
			if(!(Objects.isNull(requestDto.getPackettype())))
			{
				exp=exp.and(qAuthResponse.packetType.containsIgnoreCase(requestDto.getPackettype()));
			}
			if(!(Objects.isNull(requestDto.getUsername())))
			{
				exp=exp.and(qAuthResponse.userName.containsIgnoreCase(requestDto.getUsername()));
			}

			if(!(Objects.isNull(requestDto.getReplymessage())))
			{
				exp=exp.and(qAuthResponse.replyMessage.containsIgnoreCase(requestDto.getReplymessage()));
			}

//			Integer staffIdFromApigw=null;
//			if(request.getHeader("staffIdFromApigw") != null) {
//				staffIdFromApigw = request.getIntHeader("staffIdFromApigw");
// 			}
//			List<Long> serviceAreaIds=staffService.ListOfIds(staffIdFromApigw);
//			List<Customers> customers=customersRepository.findAll();
//			Set<String> custUserName=customers.stream().filter(customers1 ->serviceAreaIds.contains(customers1.getServicearea())).map(Customers::getUsername).collect(Collectors.toSet());
//			if(!CollectionUtils.isEmpty(custUserName)){
//				exp=exp.and(qAuthResponse.userName.in(custUserName));
//			}


			JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
			JPQLQuery<Tuple> jpqlQuery = queryFactory
					.select(qAuthResponse, qClientGroup.name)
					.from(qAuthResponse)
					.leftJoin(qClientGroup)
					.on(qClientGroup.clientGroupId.eq(qAuthResponse.clientGroup.castToNum(Long.class)))
					.where(exp)
					.orderBy(qAuthResponse.eventTime.desc()); // Sort by eventTime DESC


			List<Tuple> resultList = jpqlQuery
					.offset(pageable.getOffset())
					.limit(pageable.getPageSize())
					.fetch();

			List<AuthResponse> content = resultList.stream()
					.map(tuple -> {
						AuthResponse authResponse = tuple.get(qAuthResponse);
						authResponse.setClientGroup(tuple.get(qClientGroup.name));
						return authResponse;
					})
					.collect(Collectors.toList());

			long totalCount = jpqlQuery.fetchCount();

			return new PageImpl<>(content, pageable, totalCount);
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteAuthResponseById(Long id, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		try {
			getAuthResponseById(id, mvnoId);
			authResponseRepository.deleteById(id);
			log.info("AuthResponse deleted succefully: "+id);
		} catch (RuntimeException e) {
			log.error("Error while deleting AuthResponse: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}
	public AuthResponse findAuthResponseById(Long id, Integer mvnoId) {
		
		try {
			QAuthResponse qAuthResponse = QAuthResponse.authResponse;
			BooleanExpression exp = qAuthResponse.isNotNull();
			if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
				exp = exp.and(qAuthResponse.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			exp = exp.and(qAuthResponse.authresId.eq(id));
			Optional<AuthResponse> authResponse = authResponseRepository.findOne(exp);

			if (!authResponse.isPresent()) {
				throw new IllegalArgumentException(
						"No record found with Auth Response id " + id + " . Please enter valid Auth Response id.");
			}
			
			return authResponse.get();
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private AuthResponse getAuthResponseById(Long id, Integer mvnoId) {

		try {
			QAuthResponse qAuthResponse = QAuthResponse.authResponse;
			BooleanExpression exp = qAuthResponse.isNotNull();
			if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
				exp = exp.and(qAuthResponse.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			exp = exp.and(qAuthResponse.authresId.eq(id));
			Optional<AuthResponse> authResponse = authResponseRepository.findOne(exp);

			if (!authResponse.isPresent()) {
				throw new IllegalArgumentException(
						"You do not have access to update or delete this record");
			}

			return authResponse.get();

		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
