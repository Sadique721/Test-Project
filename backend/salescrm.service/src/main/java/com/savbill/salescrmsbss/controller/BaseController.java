package com.savbill.salescrmsbss.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.repository.LeadSourceRepository;
import com.savbill.salescrmsbss.repository.RejectReasonRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.APIConstants;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.utils.ClientServiceConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {

	public Integer MAX_PAGE_SIZE;
	public Integer PAGE;
	public Integer PAGE_SIZE;
	public Integer SORT_ORDER;
	public String SORT_BY;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	StaffUserRepository staffUserRepository;

	@Autowired
	LeadSourceRepository leadSourceRepository;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	RejectReasonRepository rejectReasonRepository;

	public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
		PAGE = Integer
				.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
		PAGE_SIZE = Integer.parseInt(
				clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
		SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
		SORT_ORDER = Integer.parseInt(
				clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
		MAX_PAGE_SIZE = Integer
				.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

		if (null == requestDTO.getPage())
			requestDTO.setPage(PAGE);
		if (null == requestDTO.getPageSize())
			requestDTO.setPageSize(PAGE_SIZE);
		if (null == requestDTO.getSortBy())
			requestDTO.setSortBy(SORT_BY);
		if (null == requestDTO.getSortOrder())
			requestDTO.setSortOrder(SORT_ORDER);
		if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
			requestDTO.setPageSize(MAX_PAGE_SIZE);
		return requestDTO;
	}

	public String getDecoded(String encodedToken) throws UnsupportedEncodingException {
		String[] pieces = encodedToken.split("\\.");
		String b64payload = pieces[1];
		String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
		return jsonString;
	}

	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public Long getMvnoId(String encodedToken) throws IOException {
		String decodedToken = getDecoded(encodedToken);
		Long mavnoId = null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			mavnoId = mainObj.getLong("mvnoId");
		}
		return mavnoId;
	}

	public Long getStaffId(String encodedToken) throws UnsupportedEncodingException {
		String decodedToken = getDecoded(encodedToken);
		Long staffId = null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			staffId = mainObj.getLong("userId");
		}
		return staffId;
	}


	public Long getBUId(String encodedToken) throws UnsupportedEncodingException {
		String decodedToken = getDecoded(encodedToken);
		Long buId = null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			JSONArray buIds = mainObj.getJSONArray("buIds");
			if(buIds != null && buIds.length() > 0) {
				buId = buIds.getLong(0);
			}
		}
		return buId;
	}
	public List getServiceArea(String encodedToken) throws UnsupportedEncodingException {
		String decodedToken = getDecoded(encodedToken);
		List<Long> serviceareaid = new ArrayList<>();
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			Integer size=mainObj.getJSONArray("serviceAreaIdList").length();
			for (int i=0;i<=size-1;i++){
				serviceareaid.add( mainObj.getJSONArray("serviceAreaIdList").getLong(i));
			}
		}
		return serviceareaid;
	}


	public Long getMvnoFromCurrentStaff(String encodedToken)throws UnsupportedEncodingException{
		String decodedToken = getDecoded(encodedToken);
		Long staffId = null;
		Long mvnoId= null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			staffId = mainObj.getLong("userId");

			// find mvnoid from the staffuser
			StaffUser staffUser = staffUserRepository.findById(staffId.intValue()).orElse(null);
			if(staffUser!=null){
				mvnoId = staffUser.getMvnoId().longValue();
			}
		}
		return mvnoId;
	}


	public List<Long> getBuFromCurrentStaff(String encodedToken)throws UnsupportedEncodingException{
		String decodedToken = getDecoded(encodedToken);
		Long staffId = null;
		List<Long> buIds= null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			staffId = mainObj.getLong("userId");

			// find mvnoid from the staffuser
			StaffUser staffUser = staffUserRepository.findById(staffId.intValue()).orElse(null);
			if(staffUser!=null){
				for(BusinessUnit businessUnit : staffUser.getBusinessUnitNameList()){
					buIds.add(businessUnit.getId());
				}
			}
		}
		return buIds;
	}


	public Object getEntityForUpdateAndDelete(Integer id, String entityName) throws Exception {
		LeadSource leadSource = new LeadSource();
		LeadMaster leadMaster = new LeadMaster();
		RejectReason rejectReason = new RejectReason();
		if(entityName.equalsIgnoreCase("leadSource")){
			leadSource = leadSourceRepository.findById(id.longValue()).orElse(null);
			if(leadSource == null || (!(getLoggedInMvnoId() == 1 || getLoggedInMvnoId() == leadSource.getMvnoId().intValue()) && (leadSource.getMvnoId() == 1 || getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().contains(leadSource.getBuId()))))
					throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, APIConstants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
			return leadSource;
		}
		else if(entityName.equalsIgnoreCase("leadMaster")){
			leadMaster = leadMasterRepository.findById(id.longValue()).orElse(null);
			if(leadMaster == null || (!(getLoggedInMvnoId() == 1 || getLoggedInMvnoId() == leadMaster.getMvnoId().intValue()) && (leadMaster.getMvnoId() == 1 || getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().contains(leadMaster.getBuId()))))
				throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, APIConstants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
			return leadMaster;
		} else if (entityName.equalsIgnoreCase("rejectReason")) {
			rejectReason = rejectReasonRepository.findById(id.longValue()).orElse(null);
			if(rejectReason == null || (!(getLoggedInMvnoId() == 1 || getLoggedInMvnoId() == rejectReason.getMvnoId().intValue()) && (rejectReason.getMvnoId() == 1 || getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().contains(rejectReason.getBuId()))))
				throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, APIConstants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
			return rejectReason;
		}

		return null;
	}

	public void getLoggedInUserBuIds(String encodedToken) throws UnsupportedEncodingException{
		String decodedToken = getDecoded(encodedToken);
		Long buId = null;
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			JSONArray buIds = mainObj.getJSONArray("buIds");
			if(buIds != null && buIds.length() > 1) {
				throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED,"You are not allowed to perform this action, Please contact your system administrator",null);
			}

	}

	public  List<Long> getBUIdLists(String encodedToken) throws UnsupportedEncodingException {
		String decodedToken = getDecoded(encodedToken);
		Long buId = null;
		List<Long> buIdList = new ArrayList<>();
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			JSONArray buIds = mainObj.getJSONArray("buIds");
			if (buIds != null) {
				for (int i = 0; i < buIds.length(); i++) {
					buIdList.add(buIds.getLong(i));
				}
			}
		}
		return buIdList;
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

	public List<Long> getLoggedInBuIdList() {
		List<Long> loggedInBuIdList = new ArrayList<>() ;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInBuIdList = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();

			}
		}
		catch (Exception e) {
			loggedInBuIdList.add(0L);
		}
		return loggedInBuIdList;
	}

	public List<Integer> getLoggedInServiceAreaIds() {
		List<Integer> loggedInServiceAreaList = new ArrayList<>() ;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInServiceAreaList = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getServiceAreaIdList();
			}
		} catch (Exception e) {
			loggedInServiceAreaList.add(null);
		}
		return loggedInServiceAreaList;
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
}
