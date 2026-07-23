package com.savbill.salescrmsbss.security.spring;//package com.savbill.ticketmanagement.core.security.spring;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component(value = "roleAccesses")
//public class RoleAccesses {
//
////    @Autowired
////	StaffRepository staffRepository;
////
////    @Autowired
////    RoleScreensRepository roleScreenRepository;
//
//    public boolean hasPermission(String screenName, String actionName) {
//
//	try {
////	    if(requestFrom==null || StringUtils.isBlank(requestFrom))
////	    {
////		throw new IllegalArgumentException("You are not an authorize user.");
////	    }
////	    if (requestFrom.equalsIgnoreCase(JwtAuthenticationFilter.SWAGGER)) {
////		boolean isAllowed = checkForValidUser(screenName, actionName);
////		if (!isAllowed) {
////		    throw new IllegalArgumentException("Sorry, You do not have permission to access this api.");
////		}
////		return isAllowed;
////	    }
//	    return true;
//
//	} catch (Throwable e) {
//	    // TODO: handle exception
//	    throw new RuntimeException(e.getMessage());
//	}
//    }
//
////    private boolean checkForValidUser(String screenName, String actionName) {
////	// TODO Auto-generated method stub
////
////	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
////	Staff optionalStaff = staffRepository.findByUserNameAndMvnoId(userName, getMvno());
////	if (optionalStaff != null) {
////	    if (optionalStaff.getUserName().equalsIgnoreCase("superadmin")) {
////		return true;
////	    }
////
////	    Map<String, Map<String, Object>> accessData = getAccessData(optionalStaff.getRole().getRoleId(), optionalStaff.getRole().getMvnoId());
////	    if (!accessData.isEmpty()) {
////		for (Entry<String, Map<String, Object>> data : accessData.entrySet()) {
////		    if (data.getKey().equalsIgnoreCase(screenName)) {
////			for (Entry<String, Object> childData : data.getValue().entrySet()) {
////			    if (childData.getKey().equalsIgnoreCase(actionName)) {
////				if ((boolean) childData.getValue()) {
////				    return true;
////				}
////			    }
////			}
////		    }
////		}
////	    }
////	} else {
////	    throw new IllegalArgumentException("You are not an authorize user.");
////	}
////	return false;
////    }
//
////    public Map<String, Map<String, Object>> getAccessData(Long roleId, Long mvnoId)
////    {
////	Map<String, Map<String, Object>> accessData = new HashMap<String, Map<String, Object>>();
////	List<RoleScreens> roleScreenList = roleScreenRepository.findByRoleIdAndMvnoId(roleId,mvnoId);
////	for (RoleScreens roleScreen : roleScreenList)
////	{
////		Map<String, Object> accessses = new HashMap<String, Object>();
////		accessses.put("readAccess", roleScreen.isReadOnly());
////		accessses.put("createUpdateAccess", roleScreen.isCreateUpdateOnly());
////		accessses.put("deleteAccess", roleScreen.isDeleteOnly());
////		accessses.put("screenId", roleScreen.getScreenId());
////		accessses.put("screenName", roleScreen.getScreens().getScreenname());
////		accessData.put(roleScreen.getScreens().getScreenname(), accessses);
////	}
////	return accessData;
////    }
////
////    public Map<String, Map<String, Object>> getAccessData() {
////	Map<String, Map<String, Object>> accessData = new HashMap<String, Map<String, Object>>();
////	List<RoleScreens> roleScreenList = roleScreenRepository.findAll();
////	for (RoleScreens roleScreen : roleScreenList) {
////
////	    Map<String, Object> accessses = new HashMap<String, Object>();
////	    accessses.put("readAccess", true);
////	    accessses.put("createUpdateAccess", true);
////	    accessses.put("deleteAccess", true);
////	    accessses.put("screenId", roleScreen.getScreenId());
////	    accessses.put("screenName", roleScreen.getScreens().getScreenname());
////	    accessData.put(roleScreen.getScreens().getScreenname(), accessses);
////	}
////	return accessData;
////    }
////
////    public Long getMvno() {
////	Long mvno = null;
////	if (LoginServiceImpl.mvno != null) {
////	    mvno = LoginServiceImpl.mvno;
////	}
////	return mvno;
////    }
//}
//
