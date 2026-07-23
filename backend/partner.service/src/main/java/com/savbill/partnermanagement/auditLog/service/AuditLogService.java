package com.savbill.partnermanagement.auditLog.service;


import com.savbill.partnermanagement.auditLog.Constants.AuditLogConstants;
import com.savbill.partnermanagement.auditLog.domain.AuditLogEntry;
import com.savbill.partnermanagement.auditLog.domain.QAuditLogEntry;
import com.savbill.partnermanagement.auditLog.mapper.AuditLogMapper;
import com.savbill.partnermanagement.auditLog.model.AuditLogEntryDTO;
import com.savbill.partnermanagement.auditLog.model.AuditLogSearchRequestDTO;
import com.savbill.partnermanagement.auditLog.queryScript.AuditSearchQueryScript;
import com.savbill.partnermanagement.auditLog.repository.AuditLogRepository;
import com.savbill.partnermanagement.constants.APIConstants;
import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.constants.PGConstants;
import com.savbill.partnermanagement.constants.SearchConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericIdModel;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.dto.PaginationRequestDTO;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.savbill.partnermanagement.utils.PropertyReaderUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class AuditLogService extends ExBaseAbstractService<AuditLogEntryDTO, AuditLogEntry, Long> {

    @Autowired
    private AuditLogRepository auditLogRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogRepository repository, AuditLogMapper mapper) {
        super(repository, mapper);
        //sortColMap.put("id", "audit_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "[AuditLogService]";
    }

    @Override
    public AuditLogEntryDTO saveEntity(AuditLogEntryDTO entity) throws Exception {
        return super.saveEntity(entity);
    }

    public void addAuditEntry(String module,
                              String operation,
                              String ipAddress,
                              String remark,
                              Long refId,
                              String refName) throws Exception {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        AuditLogEntryDTO auditLogEntry = new AuditLogEntryDTO();


        if (!loggedInUser.getRolesList().contains(CommonConstants.SUBSCRIBER_ROLE_ID.toString())) {
            auditLogEntry.setEmployeeName(loggedInUser.getFullName());
            auditLogEntry.setEmployeeId(loggedInUser.getUserId());
        }
        auditLogEntry.setUserName(loggedInUser.getFullName());
        auditLogEntry.setUserId(loggedInUser.getUserId());
        auditLogEntry.setModule(module);
        auditLogEntry.setOperation(operation);
        auditLogEntry.setIpAddress(ipAddress);
        if (remark == null)
            auditLogEntry.setRemark(createRemark(module, operation, loggedInUser.getFullName(), refName));
        else
            auditLogEntry.setRemark(remark);
        auditLogEntry.setEntityRefId(refId);
        auditLogEntry.setPartnerId(loggedInUser.getPartnerId());
        saveEntity(auditLogEntry);
    }

    public void addAuditLogin(String module,
                              String operation,
                              String ipAddress,
                              String remark,
                              Long refId,
                              String refName,
                              LoggedInUser loggedInUser) throws Exception {
        AuditLogEntryDTO auditLogEntry = new AuditLogEntryDTO();

        if (!loggedInUser.getRolesList().contains(CommonConstants.SUBSCRIBER_ROLE_ID.toString())) {
            auditLogEntry.setEmployeeName(loggedInUser.getFullName());
            auditLogEntry.setEmployeeId(loggedInUser.getUserId());
        }
        auditLogEntry.setUserName(loggedInUser.getFullName());
        auditLogEntry.setUserId(loggedInUser.getUserId());
        auditLogEntry.setModule(module);
        auditLogEntry.setOperation(operation);
        auditLogEntry.setIpAddress(ipAddress);
        if (remark == null)
            auditLogEntry.setRemark(createLoginRemark(loggedInUser.getFullName(), ipAddress));
        else
            auditLogEntry.setRemark(remark);
        auditLogEntry.setEntityRefId(refId);
        auditLogEntry.setPartnerId(loggedInUser.getPartnerId());
        saveEntity(auditLogEntry);
    }

    private String createRemark(String module, String operation, String user, String objName) {
        StringBuilder sb = new StringBuilder();
        try {
//            CacheManager cacheManager = CacheManager.getInstance();
//            Cache opCache = cacheManager.getCache("operationsCache");
//            Element el = opCache.get(operation);

            sb.append(module).append(": ");
            sb.append(user).append(" performed ");
            if (operation != null && !operation.isEmpty()) {
                sb.append(operation);
            } else {
                sb.append("unknown operation");
            }
            sb.append(" on ").append(objName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String createLoginRemark(String user, String address) {
        String remark = "User " + user + " logged in from " + address;
        return remark;
    }

    public GenericDataDTO getAuditHistoryByRequestParam(AuditLogSearchRequestDTO reqDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getAuditHistoryByRequestParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Pageable pageable = generatePageRequest(reqDTO.getPage(), reqDTO.getPageSize(), "audit_id", reqDTO.getSortOrder());
        StringBuilder commonQuery = new StringBuilder(AuditSearchQueryScript.COMMON_QUERY);
        StringBuilder whereCondition = new StringBuilder("");
        String finalQuery = "";
        List<GenericSearchModel> filterList = reqDTO.getFilters();
        try {
            boolean flag = false;


                if (null != reqDTO.getFromDate() && null != reqDTO.getToDate()) {
                    whereCondition.append(AuditSearchQueryScript.AUDIT_DATE + AuditSearchQueryScript.BETWEEN + "  date('" + reqDTO.getFromDate()
                            + "') AND date('" + reqDTO.getToDate() + "')  ");
                    flag = true;
                }
                if(null != reqDTO.getFromDate() && null == reqDTO.getToDate()){

                    whereCondition.append(AuditSearchQueryScript.AUDIT_DATE + AuditSearchQueryScript.AFTER + " date('"+ reqDTO.getFromDate()+ "')  ");
                    flag =true;
                }
                if(null != reqDTO.getToDate() && null == reqDTO.getFromDate()){

                    whereCondition.append(AuditSearchQueryScript.AUDIT_DATE + AuditSearchQueryScript.BEFORE + " date('"+ reqDTO.getToDate()+ "')  ");
                    flag =true;
                }

                if (null != reqDTO.getModule() && !"".equals(reqDTO.getModule())) {
                    if (flag)
                        whereCondition.append(AuditSearchQueryScript.AND);
                    whereCondition.append(AuditSearchQueryScript.MODULE + " ='" + reqDTO.getModule() + "'");
                    flag = true;
                }
            for(GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    if ("".equals(searchModel.getFilterValue())) {
                            if (null == reqDTO.getFromDate() && null == reqDTO.getToDate() && "".equals(reqDTO.getModule())) {
                                return makeGenericResponse(genericDataDTO, auditLogRepository.findAll(Pageable.unpaged()));
                            }

                    }
                }
            }
            if (null != reqDTO) {
                for(GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        if (!searchModel.getFilterValue().isEmpty()) {
                            String s1 = searchModel.getFilterValue();
                            if(null != reqDTO.getFilters()){
                                if(flag)
                                    whereCondition.append(AuditSearchQueryScript.AND);
                                whereCondition.append(AuditSearchQueryScript.USERNAME + AuditSearchQueryScript.LIKE+"'%" + s1 + "%'");
                                flag = true;
                            }
                        }
                    }
                }

                if (null != reqDTO.getAuditFor()) {
                    if (flag)
                        whereCondition.append(AuditSearchQueryScript.AND);
                    if (reqDTO.getAuditFor().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_EMPLOYEE)) {
                        whereCondition.append(AuditSearchQueryScript.USER_ID + AuditSearchQueryScript.IS_NOT_NULL
                                + AuditSearchQueryScript.AND +
                                AuditSearchQueryScript.EMP_ID + AuditSearchQueryScript.IS_NOT_NULL
                        );
                    }
                    if (reqDTO.getAuditFor().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_CUSTOMER)) {
                        whereCondition.append(AuditSearchQueryScript.USER_ID + AuditSearchQueryScript.IS_NOT_NULL
                                + AuditSearchQueryScript.AND + AuditSearchQueryScript.EMP_ID + AuditSearchQueryScript.IS_NULL);
                    }
                    if (reqDTO.getAuditFor().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER)) {
                        whereCondition.append(AuditSearchQueryScript.PARTNER_ID + AuditSearchQueryScript.IS_NOT_NULL
                                + AuditSearchQueryScript.AND + AuditSearchQueryScript.PARTNER_ID + " != 1 ");
                    }
                    flag = true;
                }

                if (null != reqDTO.getOperation()) {
                    if (flag)
                        whereCondition.append(AuditSearchQueryScript.AND);
                    whereCondition.append(AuditSearchQueryScript.OPERATION + " = '" + reqDTO.getOperation() + "'");
                    flag = true;
                }

                if (null != reqDTO.getCustomerId()) {
                    if (flag)
                        whereCondition.append(AuditSearchQueryScript.AND);
                    whereCondition.append(AuditSearchQueryScript.USER_ID + " = " + reqDTO.getCustomerId());
                }

                if (null != reqDTO.getPartnerId()) {
                    if (flag)
                        whereCondition.append(AuditSearchQueryScript.AND);
                    whereCondition.append(AuditSearchQueryScript.PARTNER_ID + " = " + reqDTO.getPartnerId());
                }

                commonQuery.append(" " + SubscriberSearchQueryScript.WHERE + whereCondition);
                finalQuery = commonQuery.toString();
                Query q = entityManager.createNativeQuery(finalQuery, GenericIdModel.class);
                List<GenericIdModel> resultList = q.getResultList();

                if(resultList.isEmpty()){
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                }

                if (null != resultList && 0 < resultList.size()) {
                    List<String> idList = new ArrayList<>();
                    for (GenericIdModel idModel : resultList) {
                        idList.add(idModel.getId().toString());
                    }
                   // return makeGenericResponse(genericDataDTO, auditLogRepository.findAllBy(pageable, idList));
                    return makeGenericResponse(genericDataDTO, auditLogRepository.findAllBy(pageable, idList));

                }
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public void addAuditForPlaceOrder(String module,
                                      String ipAddress,
                                      String remark,
                                      Long refId) throws Exception {
        AuditLogEntryDTO auditLogEntry = new AuditLogEntryDTO();


        LoggedInUser user;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (null != securityContext.getAuthentication()) {
            if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                auditLogEntry.setEmployeeName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                auditLogEntry.setEmployeeId(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                auditLogEntry.setUserId(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                auditLogEntry.setUserName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                auditLogEntry.setUserId(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                auditLogEntry.setRemark(createLoginRemark(properties.getProperty(PGConstants.PG_USER_STAFFNAME), ipAddress));
                auditLogEntry.setEntityRefId(refId);
                auditLogEntry.setPartnerId(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
            } else {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                if (!user.getRolesList().contains(CommonConstants.SUBSCRIBER_ROLE_ID.toString())) {
                    auditLogEntry.setEmployeeName(user.getFullName());
                    auditLogEntry.setEmployeeId(user.getUserId());
                } else if (user.getRolesList().contains(CommonConstants.PG_USER_ROLE_ID.toString())) {
                    auditLogEntry.setEmployeeName(user.getFullName());
                    auditLogEntry.setEmployeeId(CommonConstants.PG_USER_ROLE_ID.intValue());
                    auditLogEntry.setUserId(CommonConstants.PG_USER_ROLE_ID.intValue());
                }
                auditLogEntry.setUserName(user.getFullName());
                auditLogEntry.setUserId(user.getUserId());
                if (remark == null)
                    auditLogEntry.setRemark(createLoginRemark(user.getFullName(), ipAddress));
                else
                    auditLogEntry.setRemark(remark);
                auditLogEntry.setEntityRefId(refId);
                auditLogEntry.setPartnerId(user.getPartnerId());
            }
        }
        auditLogEntry.setModule(module);
        //auditLogEntry.setOperation(AclConstants.ACL_CLASS_ORDER_PLACE);
        auditLogEntry.setIpAddress(ipAddress);
        saveEntity(auditLogEntry);
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<AuditLogEntry> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "auditdate", sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = auditLogRepository.findAll(pageRequest);
        else
            paginationList = auditLogRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    public GenericDataDTO getAllEntitiesbyEntityrefId(Long entityId, PaginationRequestDTO paginationRequestDTO) {
        QAuditLogEntry qAuditLogEntry = QAuditLogEntry.auditLogEntry;
        BooleanExpression booleanExpression = qAuditLogEntry.isNotNull().and(qAuditLogEntry.entityRefId.eq(entityId));
        PageRequest pageRequest = generatePageRequest(paginationRequestDTO.getPage() , paginationRequestDTO.getPageSize() , paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        Page<AuditLogEntry>auditLogEntryList =  auditLogRepository.findAll(booleanExpression, pageRequest);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            return makeGenericResponse(genericDataDTO , auditLogEntryList);
    }
}
