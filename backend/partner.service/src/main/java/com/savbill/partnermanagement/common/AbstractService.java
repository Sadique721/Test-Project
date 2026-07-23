package com.savbill.partnermanagement.common;


import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserService;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractService<T, DTO, Long> {

    public Integer MAX_PAGE_SIZE;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;


    @Autowired
    StaffUserService staffUserService;


    protected abstract JpaRepository<T, Long> getRepository();
    
    public Integer getMvnoIdFromCurrentStaff() {
        //TODO: Change once API work on live BSS server
    	Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
            	    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public Page<T> getList(Integer pageNumber) {
        return getList(pageNumber, CommonConstants.DB_PAGE_SIZE);
    }

    public Page<T> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (null == filterList || 0 == filterList.size())
            return getRepository().findAll(pageRequest);
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }

    public Page<T> getList(Integer pageNumber, Integer customPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
        return getRepository().findAll(pageRequest);
    }

    public T save(T entity) {
        return getRepository().save(entity);
    }

    public T get(Long id) {
        return getRepository().findById(id).orElse(null);
    }

    public void delete(Long id) {
        try {
            getRepository().deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            ApplicationLogger.logger.error("Abstract Service delete() " + e.getMessage(), e);
        }
    }

    public boolean deleteVerification(Integer id) throws Exception{
    	return false;
    }

    public boolean duplicateVerifyAtSave(String name) throws Exception{
    	return false;
    }
    public boolean duplicateVerifyStateAtSave(String name , Integer countryId , Integer STATEID) throws Exception{
        return false;
    }
    public boolean duplicateVerifyStateAtEdit(String name , Integer countryId , Integer STATEID, Integer id) throws Exception{
        return false;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception{
    	return false;
    }
    
    public boolean duplicateVerifyAtSave(String name, Integer mvnoId) throws Exception{
    	return false;
    }
    public boolean duplicateVerifyCountryAtSave(String name , Integer countryId) throws Exception{
        return false;
    }
    public boolean duplicateVerifyCountryAtEdit(String name , Integer countryId, Integer id) throws Exception{
        return false;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id, Integer mvnoId) throws Exception{
    	return false;
    }
    public T update(T entity) {
        return getRepository().save(entity);
    }

    public T updateById(Long Id, T entity) {

        if (Id != null) {
            T checkEntity = getRepository().findById(Id).orElse(null);
            if (checkEntity == null) {
                return null;
            }
        }
        return getRepository().save(entity);
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

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }
    
    public int getLoggedInMvnoId() {
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

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE =5; // Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE)
            MAX_PAGE_SIZE = pageSize;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }


    public Page<T> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    public Page<T> searchByColumns(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }


    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }


    public List<java.lang.Long> getServiceAreaIdList() {
        List<java.lang.Long> idList = new ArrayList<>();

        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                idList = staffUserService.get(getLoggedInUserId()).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
                if(idList==null || idList.isEmpty()){
                    idList = staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
                }
                idList.addAll(staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }

        return idList;
    }

}
