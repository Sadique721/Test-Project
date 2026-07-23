package commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import masterdata.Location4Ids;
import masterdata.Location5Ids;
import masterdata.Location6Ids;
import masterdata.LocationIds;
import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;

import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class CommonGetAPI extends RestExecution {

    private String logFileName = "common.log";
    private String logModuleName = "CommonGetAPI";

    private static Map<String, Integer> countryIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> stateIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> districtIdMap = new HashMap<String, Integer>();
    //    private static HashMap<Object, LocationIds> allIdMap = new HashMap<Object, LocationIds>();
    private static Map<String, Integer> pincodeIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> areaIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> subAreaIdMap = new HashMap<String, Integer>();


    private static Map<String, String> municipalityHierarchy = new HashMap<String, String>();
    private static Map<String, String> areaHierarchy = new HashMap<String, String>();

    private static Map<String, String> areaPincodeHierarchy = new HashMap<String, String>();

    private static Map<String, String> wardHierarchy = new HashMap<String, String>();
    private static Map<String, String> wardHierarchyByPincode = new HashMap<String, String>();

    private static Map<String, String> wardHierarchyByPincodecaf = new HashMap<String, String>();

    private static Map<String, Integer> serviceAreaIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> branchIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> businessUnitIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> regionIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> investmentCodeIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> teamIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> roleIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> staffIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> serviceIdMap = new ConcurrentHashMap<String, Integer>();
    private static Map<Integer, String> serviceParamIdMap = new HashMap<Integer, String>();
    private static Map<String, Integer> taxIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> qosPolicyIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> timebasePolicyIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> partnerIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> planIdMap = new ConcurrentHashMap<String, Integer>();
    private static Map<Integer, String> planDetailsMap = new HashMap<Integer, String>();
    private static Map<String, Integer> planGroupIdMap = new HashMap<String, Integer>();
    private static Map<String, String> planGroupDetailsMap = new HashMap<String, String>();

    private static Map<String, Integer> chargeIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> businessVerticalIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> reasonCategoryIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> subReasonCategoryIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> tatIdMap = new HashMap<String, Integer>();

    private static Map<String, Integer> productCategoryForCustomerBindIdMap = new HashMap<String, Integer>();
    private static Map<String, String> productIdAndBindProductCategoryIdTypeMap = new HashMap<String, String>();
    private static Map<String, String> productCategoryIdAndTypeMap = new HashMap<String, String>();
    private static Map<String, String> productIdAndBindProductCategoryIdMACSerialTrackDetailMap = new HashMap<String, String>();
    private static Map<String, Integer> vendorIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> directChargeIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> productIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> casIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> popIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> warehouseIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> warehouseTeamIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> leadSourceMAsterIdMap = new HashMap<String, Integer>();

    private static Map<String, String> plandetailsCustomerDirect = new HashMap<String, String>();
    private static Map<String, String> customerDirectChargeDetails = new HashMap<String, String>();


//	public String getPlanDetails(int planId) {
//
//		String ans = null;
//
//		if(planDetailsMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			planDetailsMap = commonAPI.getPlanDetailsAll();
//		}
//		ans = planDetailsMap.get(planId);
//
//		if (ans == null) {
//			System.out.println("Common | Plan details not found - " + planId);
//			Utility.printLog(logFileName, logModuleName, "Plan details not found - ", String.valueOf(planId));
//		}
//		return ans;
//	}

    //-======================================

    /**
     * Fetches plan details by planId with caching.
     */
    public String getPlanDetails(int planId) {
        String ans = null;

        try {
            // 1️⃣ Check cache first
            ans = planDetailsMap.get(planId);
            if (ans != null) return ans;

            synchronized (this) {
                // Double-check in case another thread loaded it
                ans = planDetailsMap.get(planId);
                if (ans != null) return ans;

                // 2️⃣ Load all plan details from API
                CommonAPI commonAPI = new CommonAPI();
                Map<Integer, String> allPlans = commonAPI.getPlanDetailsAll();

                // 3️⃣ Cache all plans
                planDetailsMap.putAll(allPlans);

                // 4️⃣ Retrieve requested plan
                ans = planDetailsMap.get(planId);

                // 5️⃣ Log if not found
                if (ans == null) {
                    String msg = "Common | Plan details not found - " + planId;
                    System.out.println(msg);
                    Utility.printLog(logFileName, logModuleName, msg, String.valueOf(planId));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ans;
    }

    //===============================================================================================


    public Integer getServiceAreaId(String serviceAreaName) {
        Integer ans = null;

        try {
            String key = serviceAreaName.toLowerCase().trim();

            // 1️⃣ Check cache
            ans = serviceAreaIdMap.get(key);
            if (ans != null) return ans;

            synchronized (this) {

                // Double-check
                ans = serviceAreaIdMap.get(key);
                if (ans != null) return ans;

                // 2️⃣ Load all service areas from API
                CommonAPI api = new CommonAPI();
                Map<String, Integer> all = api.getServiceAreaIdAll();

                // 3️⃣ Cache everything
                serviceAreaIdMap.putAll(all);

                // 4️⃣ Retrieve requested service area ID
                ans = serviceAreaIdMap.get(key);

                // 5️⃣ Log if not found
                if (ans == null) {
                    String msg = "Common | ServiceArea not found - " + serviceAreaName;
                    System.out.println(msg);
                    Utility.printLog(logFileName, logModuleName, msg, serviceAreaName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ans;
    }


//	public int getPlanId(String planName) {
//
//		String searchPlanName = planName.toLowerCase();
//		int planId = 0;
//
//		if(planIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			planIdMap = commonAPI.getPlanIdAll();
//		}
//
//		try {
//			planId = planIdMap.get(searchPlanName);
//		} catch(NullPointerException npe) {
//			String message = "Common | Plan details not found";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, planName);
//		}
//
//		return planId;
//	}


    //API call once then use if not found then go for store it again

    /**
     * Fetches Plan ID by name.
     * Loads all plan IDs once initially, then only fetches missing keys and caches them.
     */
    public int getPlanId(String planName) {
        int planId = 0;

        try {
            // Normalize key
            String key = planName.toLowerCase().trim();

            // Check cache first
            Integer cachedId = planIdMap.get(key);

            if (cachedId == null) {
                synchronized (this) {
                    // Double-check inside synchronized block
                    cachedId = planIdMap.get(key);
                    if (cachedId == null) {
                        CommonAPI commonAPI = new CommonAPI();

                        // If cache empty, load all once
                        if (planIdMap.isEmpty()) {
                            Map<String, Integer> allPlans = commonAPI.getPlanIdAll();
                            planIdMap.putAll(allPlans);
                            cachedId = planIdMap.get(key);
                        }

                        // If still not found, refresh from API again
                        if (cachedId == null) {
                            Map<String, Integer> newPlans = commonAPI.getPlanIdAll();
                            planIdMap.putAll(newPlans);
                            cachedId = planIdMap.get(key);

                            if (cachedId == null) {
                                String message = "Common | Plan details not found for plan: " + key;
                                ProductUtility.stopExecution(logFileName, logModuleName, message, key);
                            }
                        }
                    }
                }
            }

            planId = (cachedId != null) ? cachedId : 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return planId;
    }


//    public int getServiceId(String ServiceName) {
//
//		String searchServiceName = ServiceName.toLowerCase();
//		int serviceId = 0;
//
//		if(serviceIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			serviceIdMap = commonAPI.getServiceIdListAll();
//		}
//
//		try {
//			serviceId = serviceIdMap.get(searchServiceName);
//		} catch(NullPointerException npe) {
//			String message = "Common | service details not found";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, ServiceName);
//		}
//
//		return serviceId;
//	}


    /**
     * Fetches Service ID by name.
     * Loads all service IDs once initially, then only fetches missing keys and caches them.
     */
    public int getServiceId(String serviceName) {
        int serviceId = 0;

        try {
            // Normalize key
            String key = serviceName.toLowerCase().trim();

            // Check cache first
            Integer cachedId = serviceIdMap.get(key);

            if (cachedId == null) {
                synchronized (this) {
                    // Double-check inside synchronized block
                    cachedId = serviceIdMap.get(key);
                    if (cachedId == null) {
                        CommonAPI commonAPI = new CommonAPI();

                        // If cache empty, load all once
                        if (serviceIdMap.isEmpty()) {
                            Map<String, Integer> allServices = commonAPI.getServiceIdListAll();
                            serviceIdMap.putAll(allServices);
                            cachedId = serviceIdMap.get(key);
                        }

                        // If still not found, refresh from API again
                        if (cachedId == null) {
                            Map<String, Integer> newServices = commonAPI.getServiceIdListAll();
                            serviceIdMap.putAll(newServices);
                            cachedId = serviceIdMap.get(key);

                            if (cachedId == null) {
                                String message = "Common | Service details not found for: " + key;
                                ProductUtility.stopExecution(logFileName, logModuleName, message, key);
                            }
                        }
                    }
                }
            }

            serviceId = (cachedId != null) ? cachedId : 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceId;
    }


    public int getPartnerId(String partnerName) {

        int partnerId = 0;  // here 0 to 1

        if (partnerIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            partnerIdMap = commonAPI.getPartnerIdAll();
        }

        try {
            partnerId = partnerIdMap.get(partnerName);
        } catch (NullPointerException npe) {
            String message = "Common | Partner details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, partnerName);
        }

        return partnerId;
    }

    //get plan details by custid for customer direct charge
    public String getPlandetailsForCustoemrDirectChareg(String basePlan, int custid, String service) {

        String key = basePlan;
        String ans = null;

        if (plandetailsCustomerDirect.isEmpty() || !plandetailsCustomerDirect.containsKey(basePlan)) {
            CommonAPI commonAPI = new CommonAPI();
            plandetailsCustomerDirect = commonAPI.getBasePlandetailsCustomerDirect(basePlan, custid, service);
        }
        ans = plandetailsCustomerDirect.get(key);

        if (ans == null) {
            System.out.println("Common | baseplan details not found - " + key);
            Utility.printLog(logFileName, logModuleName, "baseplan details not found - ", key);
        }
        return ans;
    }

    //get plan details by custid for customer direct charge
    public String getChargedetailsForCustoemrDirectChareg(String chargeName, String serviceId) {

        String key = chargeName;
        String ans = null;

        if (customerDirectChargeDetails.isEmpty() || !customerDirectChargeDetails.containsKey(chargeName)) {
            CommonAPI commonAPI = new CommonAPI();
            customerDirectChargeDetails = commonAPI.getCustomerDirectChargeDetails(chargeName, serviceId);
        }
        ans = customerDirectChargeDetails.get(key);

        if (ans == null) {
            System.out.println("Common | direct charge details not found - " + key);
            Utility.printLog(logFileName, logModuleName, "direct charge details not found - ", key);
        }
        return ans;
    }


//    //Service area is not found then use this code
//    public List<Integer> getServiceAreaIdList(String serviceAreaName) {
//        boolean mandatory = false;
//        Map<String, Boolean> map = null;
//        List<Integer> serviceAreaIdList = new ArrayList<>();
//
//        if (serviceAreaIdMap.isEmpty()) {
//            CommonAPI commonAPI = new CommonAPI();
//            serviceAreaIdMap = commonAPI.getServiceAreaIdAll();
//        }
//
//        if (serviceAreaName.equalsIgnoreCase("All")) {
//            for (String key : serviceAreaIdMap.keySet()) {
//                serviceAreaIdList.add(serviceAreaIdMap.get(key));
//            }
//        } else {
//            map = listMap(serviceAreaName);
//
//            for (String key : serviceAreaIdMap.keySet()) {
//                String[] serviceAreaNameList = serviceAreaName.split(",");
//                for (String sa : serviceAreaNameList) {
//                    if (key.equalsIgnoreCase(sa.trim())) {
//                        serviceAreaIdList.add(serviceAreaIdMap.get(key));
//                        map.put(sa.trim(), true);
//                        break;
//                    }
//                }
//            }
//        }
//
//        // 🚫 Do not stop execution, just log & return empty
//        if ((map != null) && (map.containsValue(false))) {
//            String message = "Common | Service Area details not found - " + getListNotFoundKeys(map);
//            Utility.printLog(logFileName, logModuleName, "WARNING", message);
//            return new ArrayList<>(); // return empty -> caller skips this record
//        } else if ((serviceAreaIdList.isEmpty()) && (mandatory)) {
//            String message = "Common | Service Area details not found - " + serviceAreaName;
//            Utility.printLog(logFileName, logModuleName, "WARNING", message);
//            return new ArrayList<>();
//        }
//
//        return serviceAreaIdList;
//    }


    // Inside CommonGetAPI class - To get API info for once then it will handle it from cache and refresh it end of the execution
    private static Map<String, Integer> cachedServiceAreaIdMap = new HashMap<>();
    private static final Object serviceAreaLock = new Object();

    public List<Integer> getServiceAreaIdList(String serviceAreaName) {
        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> serviceAreaIdList = new ArrayList<>();

        // ✅ Only hit API first time
        if (cachedServiceAreaIdMap.isEmpty()) {
            synchronized (serviceAreaLock) {
                if (cachedServiceAreaIdMap.isEmpty()) { // double-check for safety
                    CommonAPI commonAPI = new CommonAPI();
                    cachedServiceAreaIdMap = commonAPI.getServiceAreaIdAll();
                    System.out.println("[INFO] ServiceArea cache initialized with " + cachedServiceAreaIdMap.size() + " entries.");
                }
            }
        }

        // ✅ Use cached map now
        if (serviceAreaName.equalsIgnoreCase("All")) {
            for (Integer id : cachedServiceAreaIdMap.values()) {
                serviceAreaIdList.add(id);
            }
        } else {
            map = listMap(serviceAreaName);
            for (String key : cachedServiceAreaIdMap.keySet()) {
                String[] serviceAreaNameList = serviceAreaName.split(",");
                for (String sa : serviceAreaNameList) {
                    if (key.equalsIgnoreCase(sa.trim())) {
                        serviceAreaIdList.add(cachedServiceAreaIdMap.get(key));
                        map.put(sa.trim(), true);
                        break;
                    }
                }
            }
        }

        // ⚠️ Log warning instead of stopping execution
        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Service Area details not found - " + getListNotFoundKeys(map);
            Utility.printLog(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>(); // skip, continue
        } else if ((serviceAreaIdList.isEmpty()) && (mandatory)) {
            String message = "Common | Service Area details not found - " + serviceAreaName;
            Utility.printLog(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>();
        }

        return serviceAreaIdList;
    }


	/*
	public List<Integer> getServiceAreaIdList(String serviceAreaName) {

		boolean mandatory = false;
		Map<String, Boolean> map = null;
		List<Integer> serviceAreaIdList = new ArrayList<Integer>();

		if(serviceAreaIdMap.isEmpty()) {
			CommonAPI commonAPI = new CommonAPI();
			serviceAreaIdMap = commonAPI.getServiceAreaIdAll();
		}

		if (serviceAreaName.equalsIgnoreCase("All")) {

			Set<String> keys = serviceAreaIdMap.keySet();
			Iterator<String> keyIter = keys.iterator();

			while (keyIter.hasNext()) {
				String key = keyIter.next();
				int id = serviceAreaIdMap.get(key);
				serviceAreaIdList.add(id);
			}

		} else {

			map = listMap(serviceAreaName);
			Set<String> keys = serviceAreaIdMap.keySet();
			Iterator<String> keyIter = keys.iterator();

			while (keyIter.hasNext()) {
				String key = keyIter.next();
				String serviceAreaNameList[] = serviceAreaName.split(",");
				for (int j = 0; j < serviceAreaNameList.length; j++) {
					if (key.equalsIgnoreCase(serviceAreaNameList[j])) {
						serviceAreaIdList.add(serviceAreaIdMap.get(key));
						map.put(serviceAreaNameList[j], true);
						break;
					}
				}
			}
		}

		if((map != null) && (map.containsValue(false))) {
			String message = "Common | Service Area details not found - " + getListNotFoundKeys(map);;
			ProductUtility.stopExecution(logFileName, logModuleName, message, serviceAreaName);
		} else if ((serviceAreaIdList.size() == 0) && (mandatory)) {
			String message = "Common | Service Area details not found - ";
			ProductUtility.stopExecution(logFileName, logModuleName, message, serviceAreaName);
		}

		return serviceAreaIdList;
	}

	 */


    public int getChargeId(String chargeName) {

        int chargeId = 0;
        if (chargeIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            chargeIdMap = commonAPI.getChargeIdAll();
        }

        try {
            chargeId = chargeIdMap.get(chargeName);
        } catch (NullPointerException npe) {
            String message = "Common | Charge details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, chargeName);
        }

        return chargeId;
    }


    //sarfraz

    public List<Integer> getChargeIdAllList(String chargeName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> chargeList = new ArrayList<Integer>();

        if (chargeIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            chargeIdMap = commonAPI.getChargeIdAll();
        }

        if (chargeName.equalsIgnoreCase("All")) {

            Set<String> keys = chargeIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = chargeIdMap.get(key);
                chargeList.add(id);
            }

        } else {

            map = listMap(chargeName);
            Set<String> keys = chargeIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String chargeNameList[] = chargeName.split(",");
                for (int j = 0; j < chargeNameList.length; j++) {
                    if (key.equalsIgnoreCase(chargeNameList[j])) {
                        chargeList.add(chargeIdMap.get(key));
                        map.put(chargeNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Charge details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, chargeName);
        } else if ((chargeList.size() == 0) && (mandatory)) {
            String message = "Common | Charge details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, chargeName);
        }

        return chargeList;
    }

    public int getQosPolicyId(String qosPolicyName) {

        int qosPolicyId = 0;

        if (qosPolicyIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            qosPolicyIdMap = commonAPI.getQosPolicyIdAll();
        }

        try {
            qosPolicyId = qosPolicyIdMap.get(qosPolicyName);
        } catch (NullPointerException npe) {
            String message = "Common | Qos-Policy details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, qosPolicyName);
        }

        return qosPolicyId;
    }


    public int getTimeBasePolicyId(String timeBasePolicy) {

        int timeBasePolicyId = 0;

        if (timebasePolicyIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            timebasePolicyIdMap = commonAPI.getTimeBasePolicyIdAll();
        }

        try {
            timeBasePolicyId = timebasePolicyIdMap.get(timeBasePolicy);
        } catch (NullPointerException npe) {
            String message = "Common | Timebase Policy details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, timeBasePolicy);
        }

        return timeBasePolicyId;
    }


    public List<Integer> getServiceIdList(String serviceName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> serviceIdList = new ArrayList<Integer>();

        if (serviceIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            serviceIdMap = commonAPI.getServiceIdListAll();
        }

        if (serviceName.equalsIgnoreCase("All")) {

            Set<String> keys = serviceIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = serviceIdMap.get(key);
                serviceIdList.add(id);
            }

        } else {

            map = listMap(serviceName);
            Set<String> keys = serviceIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String serviceNameList[] = serviceName.split(",");
                for (int j = 0; j < serviceNameList.length; j++) {
                    if (key.equalsIgnoreCase(serviceNameList[j])) {
                        serviceIdList.add(serviceIdMap.get(key));
                        map.put(serviceNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Service details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, serviceName);
        } else if ((serviceIdList.size() == 0) && (mandatory)) {
            String message = "Common | Service details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, serviceName);
        }

        return serviceIdList;
    }


    public int getTaxId(String taxName) {

        int taxId = 0;

        if (taxIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            taxIdMap = commonAPI.getTaxIdAll();
        }

        try {
            taxId = taxIdMap.get(taxName);
        } catch (NullPointerException npe) {
            String message = "Common | Tax details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, taxName);
        }

        return taxId;
    }


//	public List<Integer> getBusinessUnitIdList(String businessUnitNames) {
//
//		boolean mandatory = false;
//		Map<String, Boolean> map = null;
//		List<Integer> businessUnitIdList = new ArrayList<Integer>();
//
//		if(businessUnitIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			businessUnitIdMap = commonAPI.getBusinessUnitIdListAll();
//		}
//
//		if (businessUnitNames.equalsIgnoreCase("All")) {
//
//			Set<String> keys = businessUnitIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				int id = businessUnitIdMap.get(key);
//				businessUnitIdList.add(id);
//			}
//		} else {
//
//			map = listMap(businessUnitNames);
//			Set<String> keys = businessUnitIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				String businessUnitNameList[] = businessUnitNames.split(",");
//				for (int j = 0; j < businessUnitNameList.length; j++) {
//					if (key.equalsIgnoreCase(businessUnitNameList[j])) {
//						businessUnitIdList.add(businessUnitIdMap.get(key));
//						map.put(businessUnitNameList[j], true);
//						break;
//					}
//				}
//			}
//		}
//
//		if((map != null) && (map.containsValue(false))) {
//			String message = "Common | BusinessUnit details not found - " + getListNotFoundKeys(map);;
//			ProductUtility.stopExecution(logFileName, logModuleName, message, businessUnitNames);
//		} else if ((businessUnitIdList.size() == 0) && (mandatory)) {
//			String message = "Common | BusinessUnit details not found - ";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, businessUnitNames);
//		}
//
//		return businessUnitIdList;
//	}

    private static Map<String, Integer> cachedBusinessUnitIdMap = new HashMap<>();
    private static final Object businessUnitLock = new Object();

    public List<Integer> getBusinessUnitIdList(String businessUnitNames) {
        List<Integer> businessUnitIdList = new ArrayList<>();

        // ✅ If input is null → just return list with null
        if (businessUnitNames == null || businessUnitNames.trim().isEmpty()) {
            businessUnitIdList.add(null);
            return businessUnitIdList;
        }

        Map<String, Boolean> map = listMap(businessUnitNames);

        // ✅ Initialize cache only once
        if (cachedBusinessUnitIdMap.isEmpty()) {
            synchronized (businessUnitLock) {
                if (cachedBusinessUnitIdMap.isEmpty()) {
                    CommonAPI commonAPI = new CommonAPI();
                    cachedBusinessUnitIdMap = commonAPI.getBusinessUnitIdListAll();
                    System.out.println("[INFO] BusinessUnit cache initialized with " + cachedBusinessUnitIdMap.size() + " entries.");
                }
            }
        }

        if (businessUnitNames.equalsIgnoreCase("All")) {
            for (Integer id : cachedBusinessUnitIdMap.values()) {
                businessUnitIdList.add(id);
            }
        } else {
            String[] businessUnitNameList = businessUnitNames.split(",");
            for (String bu : businessUnitNameList) {
                bu = bu.trim();
                if (cachedBusinessUnitIdMap.containsKey(bu)) {
                    businessUnitIdList.add(cachedBusinessUnitIdMap.get(bu));
                    map.put(bu, true);
                } else {
                    businessUnitIdList.add(null); // Not found → add null
                    map.put(bu, false);
                }
            }
        }

        // ⚠️ Log missing items (no stop execution)
        if (map.containsValue(false)) {
            String message = "Common | BusinessUnit details not found - " + getListNotFoundKeys(map);
            Utility.printLog(logFileName, logModuleName, "WARNING", message);
        }

        return businessUnitIdList;
    }


    public List<Integer> getRegionIdList(String regionNames) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> regionIdList = new ArrayList<Integer>();

        if (regionIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            regionIdMap = commonAPI.getRegionIdListAll();
        }

        if (regionNames.equalsIgnoreCase("All")) {

            Set<String> keys = regionIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = regionIdMap.get(key);
                regionIdList.add(id);
            }
        } else {

            map = listMap(regionNames);
            Set<String> keys = regionIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String regionNameList[] = regionNames.split(",");
                for (int j = 0; j < regionNameList.length; j++) {
                    if (key.equalsIgnoreCase(regionNameList[j])) {
                        regionIdList.add(regionIdMap.get(key));
                        map.put(regionNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Region details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, regionNames);
        } else if ((regionIdList.size() == 0) && (mandatory)) {
            String message = "Common | Region details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, regionNames);
        }

        return regionIdList;
    }


//	public List<Integer> getBranchIdList(String branchName) {
//
//		boolean mandatory = false;
//		Map<String, Boolean> map = null;
//		List<Integer> branchIdList = new ArrayList<Integer>();
//
//		if(branchIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			branchIdMap = commonAPI.getBranchIdListAll();
//		}
//
//		if (branchName.equalsIgnoreCase("All")) {
//
//			Set<String> keys = branchIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				int id = branchIdMap.get(key);
//				branchIdList.add(id);
//			}
//		} else {
//
//			map = listMap(branchName);
//			Set<String> keys = branchIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				String branchNameList[] = branchName.split(",");
//				for (int j = 0; j < branchNameList.length; j++) {
//					if (key.equalsIgnoreCase(branchNameList[j])) {
//						branchIdList.add(branchIdMap.get(key));
//						map.put(branchNameList[j], true);
//						break;
//					}
//				}
//			}
//		}
//
//		if((map != null) && (map.containsValue(false))) {
//			String message = "Common | Branch details not found - " + getListNotFoundKeys(map);;
//			ProductUtility.stopExecution(logFileName, logModuleName, message, branchName);
//		} else if ((branchIdList.size() == 0) && (mandatory)) {
//			String message = "Common | Branch details not found - ";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, branchName);
//		}
//
//		return branchIdList;
//	}


    // lock for thread safety (optional but good practice)
    private static final Object branchLock = new Object();

    public List<Integer> getBranchIdList(String branchName) {
        List<Integer> branchIdList = new ArrayList<>();

        // 🔹 Load cache only once
        if (branchIdMap.isEmpty()) {
            synchronized (branchLock) {
                if (branchIdMap.isEmpty()) { // double-check inside lock
                    CommonAPI commonAPI = new CommonAPI();
                    branchIdMap = commonAPI.getBranchIdListAll();
                    System.out.println("✅ BranchIdMap loaded from API...");
                }
            }
        }

        // 🔹 If user requested "All", return all branch IDs
        if (branchName.equalsIgnoreCase("All")) {
            branchIdList.addAll(branchIdMap.values());
        } else {
            String[] branchNames = branchName.split(",");
            for (String bName : branchNames) {
                for (Map.Entry<String, Integer> entry : branchIdMap.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(bName.trim())) {
                        branchIdList.add(entry.getValue());
                        break;
                    }
                }
            }
        }

        return branchIdList;
    }

    // Optional: Clear cache at the end of execution
    public static void clearBranchCache() {
        branchIdMap.clear();
        System.out.println("🧹 BranchIdMap cache cleared.");
    }


    public String getMasterDetailsByMunicipalityName(String municipalityName) {

        String ans = null;

        if (municipalityHierarchy.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            municipalityHierarchy = commonAPI.getMasterDetailsByMunicipalityNameAll();

        }
        ans = municipalityHierarchy.get(municipalityName);

        if (ans == null) {
            System.out.println("Common | Municipality details not found - " + municipalityName);
            Utility.printLog(logFileName, logModuleName, "Municipality details not found - ", municipalityName);
        }
        return ans;
    }

    // detail by area  name

//	public String getMasterDetailsByAreaName(String areaName) {
//
//		String ans = null;
//
//		if(areaHierarchy.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			areaHierarchy = commonAPI.getMasterDetailsByAreaNameAll();
//
//		}
//		ans = areaHierarchy.get(areaName);
//
//		if (ans == null) {
//			System.out.println("Common | Area details not found - " + areaName);
//			Utility.printLog(logFileName, logModuleName, "Area details not found - ", areaName);
//		}
//		return ans;
//	}


    // 🔹 Thread lock to ensure safe loading
    private static final Object areaLock = new Object();

    /**
     * Get area master details by area name.
     * Loads all area data from API once and caches it for later use.
     */
    public String getMasterDetailsByAreaName(String areaName) {
        String ans = null;

        // ✅ Step 1: Load cache once
        if (areaHierarchy.isEmpty()) {
            synchronized (areaLock) {
                if (areaHierarchy.isEmpty()) { // double-check
                    try {
                        CommonAPI commonAPI = new CommonAPI();
                        areaHierarchy = commonAPI.getMasterDetailsByAreaNameAll();
                        System.out.println("✅ Area hierarchy loaded from API (" + areaHierarchy.size() + " records)");
                    } catch (Exception e) {
                        System.err.println("❌ Failed to load area hierarchy: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        // ✅ Step 2: Retrieve from cache
        ans = areaHierarchy.get(areaName.toLowerCase());

        // ✅ Step 3: Handle missing area gracefully
        if (ans == null) {
            System.out.println("⚠️ Common | Area details not found - " + areaName);
            Utility.printLog(logFileName, logModuleName, "Area details not found - ", areaName);
        }

        return ans;
    }

    // ✅ Optional: to clear cache after execution
    public static void clearAreaCache() {
        areaHierarchy.clear();
        System.out.println("🧹 Area hierarchy cache cleared.");
    }


    //getMasterDetailsByAreaNameValidatingWithPincodeAll
    public String getMasterDetailsByAreaNameFindWithPincode(String areaName, String area) {

        String ans = null;

        if (areaPincodeHierarchy.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            areaPincodeHierarchy = commonAPI.getMasterDetailsByAreaNameValidatingWithPincodeAll();

        }
        Utility.printLog("Temp_areaPincodeHierarchy.log", logModuleName,"Map List = ", areaPincodeHierarchy.toString());
        ans = areaPincodeHierarchy.get(areaName);

        if (ans == null) {
            System.out.println("Common | Area details not found - " + area);
            Utility.printLog(logFileName, logModuleName, "Area details not found - ", area);
        }
        return ans;
    }

    public String getWardHierarchyDetailsByWardName(String wardName, String municipalityName) {

        String key = wardName + "_" + municipalityName;
        String ans = null;

        if (wardHierarchy.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            wardHierarchy = commonAPI.getWardHierarchyDetailsByWardNameAll();
        }
        ans = wardHierarchy.get(key);

        if (ans == null) {
            System.out.println("Common | Ward-Municipality details not found - " + key);
            Utility.printLog(logFileName, logModuleName, "Ward-Municipality details not found - ", key);
        }
        return ans;
    }

//	// get detail area by pincode id --->
//
//	 public String getWardHierarchyDetailsByWardName(String wardName,int areaId) {
//
//	String key = wardName;
//	String ans = null;
//
//	if( wardHierarchyByPincodecaf.isEmpty() ||  !wardHierarchyByPincodecaf.containsKey(wardName)) {
//		CommonAPI commonAPI = new CommonAPI();
//        wardHierarchyByPincodecaf = commonAPI.getAreaDetailsByAreaId(areaId);
//	}
//	ans = wardHierarchyByPincodecaf.get(key);
//         System.out.println("Looking for key: '" + key + "'");
//         System.out.println("All keys in map: " + wardHierarchyByPincodecaf);
//
//	if (ans == null) {
//		System.out.println("Common | Ward details not found - " + key);
//		Utility.printLog(logFileName, logModuleName, "Ward details not found - ", key);
//	}
//	return ans;
//}


    // 🔹 Cache: Map<areaId, Map<wardName, wardDetails>>
    private static final Map<Integer, Map<String, String>> wardHierarchyCache = new HashMap<>();

    // 🔹 Thread-safety lock
    private static final Object wardLock = new Object();

    /**
     * Get ward hierarchy details by ward name for a specific area.
     * Loads ward hierarchy once per areaId and reuses it from cache.
     */
    public String getWardHierarchyDetailsByWardName(String wardName, int areaId) {
        if (wardName == null || wardName.trim().isEmpty()) {
            Utility.printLog(logFileName, logModuleName, "Ward name is null or empty", "");
            return null;
        }

        String key = wardName.trim();
        String ans = null;

        // ✅ Step 1: Check if cache for this areaId exists
        Map<String, String> wardHierarchyByPincodecaf = wardHierarchyCache.get(areaId);

        if (wardHierarchyByPincodecaf == null || wardHierarchyByPincodecaf.isEmpty()) {
            synchronized (wardLock) {
                // Double-check inside lock to prevent duplicate API calls
                wardHierarchyByPincodecaf = wardHierarchyCache.get(areaId);
                if (wardHierarchyByPincodecaf == null || wardHierarchyByPincodecaf.isEmpty()) {
                    CommonAPI commonAPI = new CommonAPI();
                    wardHierarchyByPincodecaf = commonAPI.getAreaDetailsByAreaId(areaId);
                    wardHierarchyCache.put(areaId, wardHierarchyByPincodecaf);
                    System.out.println("✅ Ward hierarchy loaded from API for areaId: " + areaId);
                }
            }
        }

        // ✅ Step 2: Lookup ward name in cached data
        ans = wardHierarchyByPincodecaf.get(key);

        if (ans == null) {
            System.out.println("⚠️ Common | Ward details not found - " + key + " (areaId=" + areaId + ")");
            Utility.printLog(logFileName, logModuleName, "Ward details not found - ", key);
        }

        return ans;
    }

    // ✅ Optional: Clear all caches (useful at the end of execution)
    public static void clearWardHierarchyCache() {
        wardHierarchyCache.clear();
        System.out.println("🧹 Ward hierarchy cache cleared for all areaIds.");
    }


// get detail area by Area id --->

    public String getWardHierarchyDetailsByWardNameW(String wardName, int areaId) {

        String key = wardName;
        String ans = null;

        if (wardHierarchyByPincode.containsKey(wardName)) {
            CommonAPI commonAPI = new CommonAPI();
            wardHierarchyByPincode = commonAPI.getAreaDetailsByAreaId_CAF(areaId);
        }
        ans = wardHierarchyByPincode.get(key);

        if (ans == null) {
            System.out.println("Common | Ward details not found - " + key);
            Utility.printLog(logFileName, logModuleName, "Ward details not found - ", key);
        }
        return ans;
    }


    // get detail by pin code id and store in pincode name as key

    // get detail area by pincode id --->

    public String getDetailsByPincodeId(String pincodeName, int areaId) {

        String key = pincodeName;
        String ans = null;

        if (wardHierarchyByPincode.isEmpty() || !wardHierarchyByPincode.containsKey(pincodeName)) {
            CommonAPI commonAPI = new CommonAPI();
            wardHierarchyByPincode = commonAPI.getAreaDetailsByAreaId(areaId);
        }
        ans = wardHierarchyByPincode.get(key);

        if (ans == null) {
            System.out.println("Common | Ward details not found - " + key);
            Utility.printLog(logFileName, logModuleName, "Ward details not found - ", key);
        }
        return ans;
    }


    public int getCountryId(String countryName) {

        String searchCountryName = countryName.toLowerCase();
        int countryId = 0;
        if (countryIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            countryIdMap = commonAPI.getCountryIdAll();
        }

        try {
            countryId = countryIdMap.get(searchCountryName);
        } catch (NullPointerException npe) {
            String message = "Common | Country details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, countryName);
        }

        return countryId;
    }


    public int getProvinceId(String provinceName) {

        String searchProvinceName = provinceName.toLowerCase();
        int provinceId = 0;
        if (stateIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            stateIdMap = commonAPI.getProvinceIdAll();
        }

        try {
            provinceId = stateIdMap.get(searchProvinceName);
        } catch (NullPointerException npe) {
            String message = "Common | Province details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, provinceName);
        }

        return provinceId;
    }


    public int getDistrictId(String districtName) {

        String searchDistrictName = districtName.toLowerCase();
        int districtId = 0;
        if (districtIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            districtIdMap = commonAPI.getDistrictIdAll();
        }

        try {
            districtId = districtIdMap.get(searchDistrictName);
        } catch (NullPointerException npe) {
            String message = "Common | District details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, districtName);
        }

        return districtId;
    }


    // Location cache for all country|state|city combinations for Municipality
    private static final Map<String, LocationIds> allIdMap = new ConcurrentHashMap<>();

    /**
     * Fetches Country-State-City IDs with caching.
     * Loads all data once initially, then only fetches missing keys and caches them.
     */
    public LocationIds getCountryStateCityId(String countryName, String stateName, String cityName) {
        LocationIds ids = null;

        try {
            // Build composite key
            String key = (countryName + "|" + stateName + "|" + cityName)
                    .toLowerCase().trim();

            // Check cache first
            ids = allIdMap.get(key);

            if (ids == null) {
                synchronized (this) {
                    // Double-check after acquiring lock
                    ids = allIdMap.get(key);
                    if (ids == null) {
                        CommonAPI commonAPI = new CommonAPI();

                        // Load all data once if cache is empty
                        if (allIdMap.isEmpty()) {
                            Map<String, LocationIds> all = commonAPI.getCountryStateCityAllIdsPost();
                            allIdMap.putAll(all);
                            ids = allIdMap.get(key);
                        }

                        // If still not found, fetch from API again
                        if (ids == null) {
                            Map<String, LocationIds> newIds = commonAPI.getCountryStateCityAllIdsPost();
                            allIdMap.putAll(newIds);

                            ids = allIdMap.get(key);

                            if (ids == null) {
                                String message = "Common | Location details not found for key: " + key;
                                ProductUtility.stopExecution(logFileName, logModuleName, message, key);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }


//    // ServiceArea cache for all city|pincode combinations
//    private static final Map<String, Location6Ids> all6IdMap = new ConcurrentHashMap<>();
//
//    /**
//     * Get a specific Location6Ids object by city and optional pincode.
//     * Loads all data once initially, then looks up from cache.
//     */
//    public Location6Ids getCityPincodeList(String cityName, String pin) {
//        String city = cityName.toLowerCase().trim();
//        String pincode = pin.toLowerCase().trim();
//        Location6Ids loc = null;
//
//        try {
//            // Load cache if empty
//            if (all6IdMap.isEmpty()) {
//                synchronized (this) {
//                    if (all6IdMap.isEmpty()) {
//                        CommonAPI commonAPI = new CommonAPI();
//                        all6IdMap.putAll(commonAPI.getCityPincodeAll9IdsPost());
//
//                    }
//                }
//            }
//
//            // Lookup by city
//            for (Map.Entry<String, Location6Ids> entry : all6IdMap.entrySet()) {
//                if (entry.getKey().equals(city)) {
//                    loc = entry.getValue();
//
//                    // If pincode is empty, return all
//                    if (pincode.isEmpty()) {
//                        return loc;
//                    }
//
//                    // Find index of requested pincode
//                    int index = loc.getPincodeNames().indexOf(pin);
//                    if (index != -1) {
//                        int pid = loc.getPincodeIds().get(index);
//
//                        // Create a new Location6Ids with only the selected pincode
//                        Location6Ids selectiveLoc = new Location6Ids(loc.getCityId());
//                        selectiveLoc.addPincode(pid, pin);
//
//                        return selectiveLoc;
//                    }
//                }
//            }
//
//            System.out.println("⚠️ Location not found for city: " + cityName + " | pincode: " + pin);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

//======================================================================================================================

    private static Map<String, Integer> all8IdMap = new ConcurrentHashMap<>();

    public int getPincodeIdwithCityandPincodeNameList(String cityName, String pin) {
        String city = cityName.toLowerCase().trim();
        String pincode = pin.toLowerCase().trim();
        String searchPincodeid = city + "_" + pincode;

        // Load cache if empty
        if (all8IdMap.isEmpty()) {
            synchronized (this) {
                if (all8IdMap.isEmpty()) {
                    CommonAPI commonAPI = new CommonAPI();
//                        all8IdMap.putAll(commonAPI.getCityPincodeAll6IdsPost());
                    all8IdMap = commonAPI.getCityPincodeAll9IdsPost();
                }
            }
        }

        // ✅ Step 2: Lookup from cache
        Integer id = all8IdMap.get(searchPincodeid);

        // ✅ Step 3: Handle missing entries gracefully
        if (id == null) {
            String message = "Common | Pincode details not found - " + searchPincodeid;
            System.out.println("⚠️ " + message);
            ProductUtility.stopExecution(logFileName, logModuleName, message, searchPincodeid);
            return 0;
        }

        return id;

    }


//=============================================================================================

    // WARD = Location cache for all country|state|city|pincode combinations
    private static final Map<String, Location4Ids> all4IdMap = new ConcurrentHashMap<>();

    /**
     * Fetches Country-State-City-Pincode IDs with caching.
     * Loads all data once initially, then only fetches missing keys and caches them.
     */
    public Location4Ids getCountryStateCityPincodeId(String countryName, String stateName, String cityName, String pincodeName) {
        Location4Ids ids = null;

        try {
            // Build composite key
            String key = (countryName + "|" + stateName + "|" + cityName + "|" + pincodeName)
                    .toLowerCase().trim();

            // Check cache first
            ids = all4IdMap.get(key);

            if (ids == null) {
                synchronized (this) {
                    // Double-check after acquiring lock
                    ids = all4IdMap.get(key);
                    if (ids == null) {
                        CommonAPI commonAPI = new CommonAPI();

                        // Load all data once if cache is empty
                        if (all4IdMap.isEmpty()) {
                            Map<String, Location4Ids> all = commonAPI.getCountryStateCityPincodeAllIdsPost();
                            all4IdMap.putAll(all);
                            ids = all4IdMap.get(key);
                        }

                        // If still not found, fetch from API again
                        if (ids == null) {
                            Map<String, Location4Ids> newIds = commonAPI.getCountryStateCityPincodeAllIdsPost();
                            all4IdMap.putAll(newIds);

                            ids = all4IdMap.get(key);

                            if (ids == null) {
                                String message = "Common | Location details not found for key: " + key;
                                ProductUtility.stopExecution(logFileName, logModuleName, message, key);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }


//================================================================================


    // Location cache for all country|state|city combinations
    private static final Map<String, Location5Ids> all5IdMap = new ConcurrentHashMap<>();

    /**
     * Fetches City-Pincode-Ward IDs with caching.
     * Loads all data once initially, then only fetches missing keys and caches them.
     */

    public Location5Ids getCityPincodeWardId(String cityName, String pincodeName, String wardName, String stateName, String countryName) {
        Location5Ids ids = null;

        try {
            String key = (cityName + "|" + pincodeName + "|" + wardName + "|" + stateName + "|" + countryName )
                    .toLowerCase().trim();

            // 1️⃣ Check cache first
            ids = all5IdMap.get(key);

            if (ids == null) {
                synchronized (this) {

                    // Double check inside synchronized block
                    ids = all5IdMap.get(key);

                    if (ids == null) {

                        // 2️⃣ If cache empty → load everything only once
                        if (all5IdMap.isEmpty()) {
                            CommonAPI commonAPI = new CommonAPI();
                            Map<String, Location5Ids> all = commonAPI.getCityPincodeWardAllIdsPost();
                            all5IdMap.putAll(all);

                            // Try again after loading
                            ids = all5IdMap.get(key);
                        }

                        // 3️⃣ If still not found → DO NOT CALL API AGAIN
                        if (ids == null) {
                            String message = "Common | Location details not found for key: " + key;
                            ProductUtility.stopExecution(logFileName, logModuleName, message, key);

                            // continue execution, do not stop thread
                            return null;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }


//public Location5Ids getCityPincodeWardId(String cityName, String pincodeName, String wardName) {
//    Location5Ids ids = null;
//
//    try {
//        String key = (cityName + "|" + pincodeName + "|" + wardName).toLowerCase().trim();
//
//        // Check cache first
//        ids = all5IdMap.get(key);
//
//        if (ids == null) {
//            synchronized (this) {
//                // Double-check in case another thread just loaded it
//                ids = all5IdMap.get(key);
//                if (ids == null) {
//                    CommonAPI commonAPI = new CommonAPI();
//
//                    // If cache empty, load all once
//                    if (all5IdMap.isEmpty()) {
//                        Map<String, Location5Ids> all = commonAPI.getCityPincodeWardAllIdsPost();
//                        all5IdMap.putAll(all);
//                        ids = all5IdMap.get(key);
//                    }
//
//                    // If still not found (new/unknown combination)
//                    if (ids == null) {
//                        // Fetch missing combination only
//                        Map<String, Location5Ids> newIds = commonAPI.getCityPincodeWardAllIdsPost();
//                        // ✅ Note: calling the same API again updates cache with new data
//                        all5IdMap.putAll(newIds);
//
//                        // Try to get again
//                        ids = all5IdMap.get(key);
//
//                        if (ids == null) {
//                            String message = "Common | Location details not found for key: " + key;
//                            ProductUtility.stopExecution(logFileName, logModuleName, message, key);
//                        }
//                    }
//                }
//            }
//        }
//
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//
//    return ids;
//}


// New pincode id get after savanna project.
//public int getPincodeId(String pincodeName) {
//
//		String searchPincodeName = pincodeName.toLowerCase();
//		int pincodeId = 0;
//		if(pincodeIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			pincodeIdMap = commonAPI.getPincodeIdAll();
//			//System.out.println(pincodeIdMap);
//		}
//
//		try {
//			pincodeId = pincodeIdMap.get(searchPincodeName);
//		} catch(NullPointerException npe) {
//			String message = "Common | Pincode details not found";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, pincodeName);
//		}
//
//		return pincodeId;
//	}


    // 🔹 Thread-safety lock
    private static final Object pincodeLock = new Object();

    /**
     * Gets the pincode ID for the given pincode name.
     * Loads all pincodes from the API once and caches them.
     */
    public int getPincodeId(String pincodeName) {
        if (pincodeName == null || pincodeName.isEmpty()) {
            ProductUtility.stopExecution(logFileName, logModuleName, "Pincode name is empty or null", "");
            return 0;
        }

        String searchPincodeName = pincodeName.toLowerCase();

        // ✅ Step 1: Load cache only once
        if (pincodeIdMap.isEmpty()) {
            synchronized (pincodeLock) {
                if (pincodeIdMap.isEmpty()) { // double-check
                    CommonAPI commonAPI = new CommonAPI();
                    pincodeIdMap = commonAPI.getPincodeIdAll();
                    System.out.println("✅ PincodeIdMap loaded from API (" + pincodeIdMap.size() + " entries)");
                }
            }
        }

        // ✅ Step 2: Lookup from cache
        Integer id = pincodeIdMap.get(searchPincodeName);

        // ✅ Step 3: Handle missing entries gracefully
        if (id == null) {
            String message = "Common | Pincode details not found - " + pincodeName;
            System.out.println("⚠️ " + message);
            ProductUtility.stopExecution(logFileName, logModuleName, message, pincodeName);
            return 0;
        }

        return id;
    }


    //==============================================================================

    // Thread-safe cache for Staff Username → ID
    private static final Map<String, Integer> staffUsernameIdCache = new ConcurrentHashMap<>();

    /**
     * Fetches staff ID by name with caching.
     * Loads all staff once initially, then only fetches missing keys.
     */
    public int staffUserNameIdList (String userName) {
        int staffId = 0;
        String key = userName.toLowerCase().trim();

        try {
            // 1️⃣ Check cache first
            Integer cachedId = staffUsernameIdCache.get(key);
            if (cachedId != null) return cachedId;

            synchronized (this) {
                // Double-check after acquiring lock
                cachedId = staffUsernameIdCache.get(key);
                if (cachedId != null) return cachedId;

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, Integer> allStaffs = commonAPI.postListStaff();

                // 3️⃣ Cache all results
                staffUsernameIdCache.putAll(allStaffs);

                // 4️⃣ Retrieve requested product
                staffId = staffUsernameIdCache.getOrDefault(key, 0);

                if (staffId == 0) {
                    String message = "Common | Staff details not found for: " + userName;
                    ProductUtility.stopExecution(logFileName, logModuleName, message, userName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return staffId;
    }


    //==============================================================================

    public int getAreaId(String areaName) {

        String searchAreaName = areaName.toLowerCase();
        int areaId = 0;
        if (areaIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            areaIdMap = commonAPI.getAreaIdAll();
            //System.out.println(pincodeIdMap);
        }

        try {
            areaId = areaIdMap.get(searchAreaName);
        } catch (NullPointerException npe) {
            String message = "Common | Area details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, areaName);
        }

        return areaId;
    }

// sub area

    private static final Object subAreaLock = new Object();

    public int getSubAreaId(String subAreaName) {

        // ✅ Validate input
        if (subAreaName == null || subAreaName.trim().isEmpty()) {
            System.out.println("[WARNING] SubArea name is null or empty.");
            return 0;
        }

        // ✅ Initialize cache only once per JVM execution
        if (subAreaIdMap.isEmpty()) {
            synchronized (subAreaLock) {
                if (subAreaIdMap.isEmpty()) { // double-check
                    CommonAPI commonAPI = new CommonAPI();
                    subAreaIdMap = commonAPI.getSubAreaIdAll();
                    System.out.println("[INFO] SubArea cache initialized with " + subAreaIdMap.size() + " entries.");
                }
            }
        }

        // ✅ Normalize input
        String searchSubAreaName = subAreaName.trim().toLowerCase();

        // ✅ Get sub-area ID from cache
        Integer subAreaId = subAreaIdMap.get(searchSubAreaName);
        if (subAreaId == null) {
            System.out.println("[WARNING] Common | Sub Area details not found: " + subAreaName);
            subAreaId = 0;
        }
        return subAreaId;
    }


    public List<Integer> getInvestmentCodeIdList(String investmentCodeName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> investmentCodeIdList = new ArrayList<Integer>();

        if (investmentCodeIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            investmentCodeIdMap = commonAPI.getInvestmentCodeIdAll();
        }

        if (investmentCodeName.equalsIgnoreCase("All")) {

            Set<String> keys = investmentCodeIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = investmentCodeIdMap.get(key);
                investmentCodeIdList.add(id);
            }
        } else {

            map = listMap(investmentCodeName);
            Set<String> keys = investmentCodeIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String investmentCodeNameList[] = investmentCodeName.split(",");
                for (int j = 0; j < investmentCodeNameList.length; j++) {
                    if (key.equalsIgnoreCase(investmentCodeNameList[j])) {
                        investmentCodeIdList.add(investmentCodeIdMap.get(key));
                        map.put(investmentCodeNameList[j], true);
                        break;
                    }
                }
            }
        }


        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | InvestmentCode details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, investmentCodeName);
        } else if ((investmentCodeIdList.size() == 0) && (mandatory)) {
            String message = "Common | InvestmentCode details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, investmentCodeName);
        }

        return investmentCodeIdList;
    }


    public int getBusinessVerticalId(String businessVerticalName) {

        int businessVerticalId = 0;
        if (businessVerticalIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            businessVerticalIdMap = commonAPI.getBusinessVerticalIdAll();
        }

        try {
            businessVerticalId = businessVerticalIdMap.get(businessVerticalName);
        } catch (NullPointerException npe) {
            String message = "Common | Business-Vertical details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, businessVerticalName);
        }

        return businessVerticalId;
    }


//	public List<Integer> getTeamIdList(String teamName) {   // EveryTime API calls and get response
//
//		boolean mandatory = false;
//		Map<String, Boolean> map = null;
//		List<Integer> teamIdList = new ArrayList<Integer>();
//
//		if(teamIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			teamIdMap = commonAPI.getTeamIdListAll();
//		}
//
//		if (teamName.equalsIgnoreCase("All")) {
//
//			Set<String> keys = teamIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				int id = teamIdMap.get(key);
//				teamIdList.add(id);
//			}
//		} else {
//			map = listMap(teamName);
//			Set<String> keys = teamIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				String teamNameList[] = teamName.split(",");
//				for (int j = 0; j < teamNameList.length; j++) {
//					if (key.equalsIgnoreCase(teamNameList[j])) {
//						teamIdList.add(teamIdMap.get(key));
//						map.put(teamNameList[j], true);
//						break;
//					}
//				}
//			}
//		}
//
//		if((map != null) && (map.containsValue(false))) {
//			String message = "Common | Team details not found - " + getListNotFoundKeys(map);;
//			ProductUtility.stopExecution(logFileName, logModuleName, message, teamName);
//		} else if ((teamIdList.size() == 0) && (mandatory)) {
//			String message = "Common | Team details not found - ";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, teamName);
//		}
//
//		return teamIdList;
//	}

    // Inside CommonGetAPI class
    private static Map<String, Integer> cachedTeamIdMap = new HashMap<>();
    private static final Object teamLock = new Object();

    // API call only once; then always use cache
    public List<Integer> getTeamIdList(String teamName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> teamIdList = new ArrayList<>();

        // ✅ Only first time API hit; then use cache
        if (cachedTeamIdMap.isEmpty()) {
            synchronized (teamLock) {
                if (cachedTeamIdMap.isEmpty()) { // double-check for thread safety
                    CommonAPI commonAPI = new CommonAPI();
                    cachedTeamIdMap = commonAPI.getTeamIdListAll();
                    System.out.println("[INFO] Team cache initialized with " + cachedTeamIdMap.size() + " entries.");
                }
            }
        }

        // ✅ Now always use cached map
        if (teamName.equalsIgnoreCase("All")) {
            for (Integer id : cachedTeamIdMap.values()) {
                teamIdList.add(id);
            }
        } else {
            map = listMap(teamName);
            for (String key : cachedTeamIdMap.keySet()) {
                String[] teamNameList = teamName.split(",");
                for (String t : teamNameList) {
                    if (key.equalsIgnoreCase(t.trim())) {
                        teamIdList.add(cachedTeamIdMap.get(key));
                        map.put(t.trim(), true);
                        break;
                    }
                }
            }
        }

        // ⚠️ Log warning instead of stopping execution
        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Team details not found - " + getListNotFoundKeys(map);
            ProductUtility.stopExecution(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>(); // skip missing, continue
        } else if ((teamIdList.isEmpty()) && (mandatory)) {
            String message = "Common | Team details not found - " + teamName;
            ProductUtility.stopExecution(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>();
        }

        return teamIdList;
    }


//API call every time to get info
//	public List<Integer> getRoleId(String roleName) {
//
//		 // role is always single so below code for All is commented
//		boolean mandatory = false;
//		Map<String, Boolean> map = null;
//		List<Integer> roleIdList = new ArrayList<Integer>();
//
//		if(roleIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			roleIdMap = commonAPI.getRoleIdAll();
//		}
//
//	/*	if (roleName.equalsIgnoreCase("All")) {
//
//			Set<String> keys = roleIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				int id = roleIdMap.get(key);
//				roleIdList.add(id);
//			}
//		} else {*/
//		{
//			map = listMap(roleName);
//			Set<String> keys = roleIdMap.keySet();
//			Iterator<String> keyIter = keys.iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				String roleNameList[] = roleName.split(",");
//				for (int j = 0; j < roleNameList.length; j++) {
//					if (key.equalsIgnoreCase(roleNameList[j])) {
//						roleIdList.add(roleIdMap.get(key));
//						map.put(roleNameList[j], true);
//						break;
//					}
//				}
//			}
//		}
//
//		if((map != null) && (map.containsValue(false))) {
//			String message = "Common | Staff-Role details not found - " + getListNotFoundKeys(map);;
//			ProductUtility.stopExecution(logFileName, logModuleName, message, roleName);
//		} else if ((roleIdList.size() == 0) && (mandatory)) {
//			String message = "Common | Staff-Role details not found - ";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, roleName);
//		}
//
//		return roleIdList;
//	}

    private static Map<String, Integer> cachedRoleIdMap = new HashMap<>();
    private static final Object roleLock = new Object();

    // API call only once; then always use cache
    public List<Integer> getRoleId(String roleName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> roleIdList = new ArrayList<>();

        // ✅ Only first time API hit; then use cache
        if (cachedRoleIdMap.isEmpty()) {
            synchronized (roleLock) {
                if (cachedRoleIdMap.isEmpty()) { // double-check for thread safety
                    CommonAPI commonAPI = new CommonAPI();
                    cachedRoleIdMap = commonAPI.getRoleIdAll();
                    System.out.println("[INFO] Role cache initialized with " + cachedRoleIdMap.size() + " entries.");
                }
            }
        }

        // ✅ Now always use cached map
        if (roleName.equalsIgnoreCase("All")) {
            for (Integer id : cachedRoleIdMap.values()) {
                roleIdList.add(id);
            }
        } else {
            map = listMap(roleName);
            for (String key : cachedRoleIdMap.keySet()) {
                String[] roleNameList = roleName.split(",");
                for (String r : roleNameList) {
                    if (key.equalsIgnoreCase(r.trim())) {
                        roleIdList.add(cachedRoleIdMap.get(key));
                        map.put(r.trim(), true);
                        break;
                    }
                }
            }
        }

        // ⚠️ Log warning instead of stopping execution
        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Staff-Role details not found - " + getListNotFoundKeys(map);
            ProductUtility.stopExecution(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>(); // skip missing, continue
        } else if ((roleIdList.isEmpty()) && (mandatory)) {
            String message = "Common | Staff-Role details not found - " + roleName;
            ProductUtility.stopExecution(logFileName, logModuleName, "WARNING", message);
            return new ArrayList<>();
        }

        return roleIdList;
    }


    private static final Object staffLock = new Object();

    public int getStaffId(String staffUserName) {
        // ✅ Initialize cache only once per JVM execution
        if (staffIdMap.isEmpty()) {
            synchronized (staffLock) {
                if (staffIdMap.isEmpty()) { // double-check for thread safety
                    CommonAPI commonAPI = new CommonAPI();
                    staffIdMap = commonAPI.getStaffIdAll();
                    System.out.println("[INFO] StaffId cache initialized with " + staffIdMap.size() + " entries.");
                }
            }
        }

        // ✅ Validate input
        if (staffUserName == null || staffUserName.trim().isEmpty()) {
            System.out.println("[WARNING] Staff username is null or empty.");
            return 0;
        }

        // ✅ Trim input to avoid space issues
        staffUserName = staffUserName.trim();

        // ✅ Get staff ID from cache
        Integer staffId = staffIdMap.get(staffUserName);
        if (staffId == null) {
            System.out.println("[WARNING] Common | Staff Username not found: " + staffUserName);
            staffId = 0;
        }

        return staffId;
    }

    //============================================================================

    private static final Object teamLockNew = new Object();
    private static Map<String, Integer> teamIdMapNew = new HashMap<>();

    public int getTeamId(String teamName) {

        if (teamIdMapNew.isEmpty()) {
            synchronized (teamLockNew) {
                if (teamIdMapNew.isEmpty()) {
                    CommonAPI commonAPI = new CommonAPI();
                    teamIdMapNew = commonAPI.getTeamIdAllBSS();
                    System.out.println("[INFO] Team cache initialized with " + teamIdMapNew.size() + " entries.");
                }
            }
        }

        if (teamName == null || teamName.trim().isEmpty()) {
            System.out.println("[WARNING] Team name is null or empty.");
            return 0;
        }

        teamName = teamName.trim();

        Integer teamId = teamIdMapNew.get(teamName);
        if (teamId == null) {
            System.out.println("[WARNING] Common | Team name not found: " + teamName);
            teamId = 0;
        }

        return teamId;
    }



//=============================================================================


    // Lock object for thread-safety
    private static final Object mvnoLock = new Object();

    // Cache: MVNO Name → MVNO ID
    private static Map<String, Integer> mvnoMap = new HashMap<>();

    /**
     * Returns MVNO ID from cache (lazy-loaded + thread-safe).
     */
    public int getMvnoId(String mvnoName) {

        // Initialize cache only once (Double-Checked Locking)
        if (mvnoMap.isEmpty()) {
            synchronized (mvnoLock) {
                if (mvnoMap.isEmpty()) {
                    CommonAPI commonAPI = new CommonAPI();
                    mvnoMap = commonAPI.getMVNOIdAll();
                    System.out.println("[INFO] MVNO cache initialized with " + mvnoMap.size() + " entries.");
                }
            }
        }

        // Validate input
        if (mvnoName == null || mvnoName.trim().isEmpty()) {
            System.out.println("[WARNING] MVNO name is null or empty.");
            return 0;
        }

        // Trim input
        mvnoName = mvnoName.trim();

        // ✅ Split by '@' and use the part after it if present
        if (mvnoName.contains("@")) {
            String[] parts = mvnoName.split("@");
            if (parts.length > 1) {
                mvnoName = parts[1];
            }
        }

        Integer mvnoId = mvnoMap.get(mvnoName);

        if (mvnoId == null) {
            System.out.println("[WARNING] MVNO not found: " + mvnoName);
            return 0;
        }

        return mvnoId;
    }


    // Cache: serviceId + productName -> newProductAmount
    private static final Map<String, String> inventoryProductCache = new ConcurrentHashMap<>();

    /**
     * Fetches serialized inventory product details with caching.
     */
    public String getSerializedInventoryProductDetails(int serviceId, String productName) {
        String result = null;
        String key = serviceId + "|" + productName.toLowerCase().trim();

        try {
            // 1️⃣ Check cache first
            String cachedValue = inventoryProductCache.get(key);
            if (cachedValue != null) return cachedValue;

            synchronized (this) {
                // Double-check inside synchronized block
                cachedValue = inventoryProductCache.get(key);
                if (cachedValue != null) return cachedValue;

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, String> allProducts = commonAPI.getInventoryProductsByService(serviceId);

                // 3️⃣ Cache all products
                inventoryProductCache.putAll(allProducts);

                // 4️⃣ Retrieve requested product
                result = allProducts.get(key);

                if (result == null) {
                    String msg = "Inventory details not found - " + productName + " (serviceId=" + serviceId + ")";
                    System.out.println(msg);
                    Utility.printLog(logFileName, logModuleName, msg, productName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


//===================================================================================

// Cache: productId|ownerId|mac|serial -> item JSON
//private static final Map<String, JSONObject> itemHistoryCache = new ConcurrentHashMap<>();
//
    /// **
// * Fetches item history by product, MAC address, and serial number.
// * Uses cache for repeated requests.
// */
//public JSONObject getItemHistoryByProduct(
//        int productId,
//        String ownerId,
//        String assignedMacAddress,
//        String assignedSerialNumber
//) {
//    JSONObject itemJson = null;
//    String key = productId + "|" + ownerId + "|" + assignedMacAddress.toLowerCase().trim()
//            + "|" + assignedSerialNumber.toLowerCase().trim();
//
//    try {
//        // 1️⃣ Check cache first
//        itemJson = itemHistoryCache.get(key);
//        if (itemJson != null) return itemJson;
//
//        synchronized (this) {
//            // Double-check after acquiring lock
//            itemJson = itemHistoryCache.get(key);
//            if (itemJson != null) return itemJson;
//
//            // 2️⃣ Load from API
//            CommonAPI commonAPI = new CommonAPI();
//            JSONArray allItems = commonAPI.getItemHistoryByProduct(productId, ownerId);
//
//            // 3️⃣ Find matching MAC & Serial
//            if (allItems != null) {
//                for (int i = 0; i < allItems.length(); i++) {
//                    JSONObject item = allItems.getJSONObject(i);
//                    String serialNumber = item.optString("serialNumber", "").toLowerCase();
//                    String macAddress = item.optString("macAddress", "").toLowerCase();
//
//                    if (assignedMacAddress.equalsIgnoreCase(macAddress)
//                            && assignedSerialNumber.equalsIgnoreCase(serialNumber)) {
//                        itemJson = item;
//                        break;
//                    }
//                }
//            }
//
//            // 4️⃣ Cache result (even if null to avoid repeated API calls for missing items)
//            itemHistoryCache.put(key, itemJson);
//
//            // 5️⃣ Log if not found
//            if (itemJson == null) {
//                String message = "Item history of productId " + productId
//                        + " with MAC=" + assignedMacAddress
//                        + " and Serial=" + assignedSerialNumber + " not found";
//                System.out.println(message);
//                Utility.printLog(logFileName, logModuleName, message, "");
//            }
//        }
//
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//
//    return itemJson;
//}

    private static final Map<String, JSONObject> itemHistoryCache = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> productOwnerFetched = new ConcurrentHashMap<>();

    public JSONObject getItemHistoryByProduct(
            int productId,
            String ownerId,
            String assignedMacAddress,
            String assignedSerialNumber
    ) {
        JSONObject itemJson = null;
        String key = assignedMacAddress.toLowerCase().trim() + "|" + assignedSerialNumber.toLowerCase();

        try {
            // 1️⃣ Check cache first
            if (itemHistoryCache.containsKey(key)) {
                return itemHistoryCache.get(key);
            }

            // Unique key to track if API fetch for product+owner is done
            String productOwnerKey = productId + "|" + ownerId;

            synchronized (this) {
                // Double-check cache
                if (itemHistoryCache.containsKey(key)) {
                    return itemHistoryCache.get(key);
                }

                // 2️⃣ Fetch all items for this product+owner only once
                if (!productOwnerFetched.containsKey(productOwnerKey)) {
                    CommonAPI commonAPI = new CommonAPI();
                    JSONArray allItems = commonAPI.getItemHistoryByProduct(productId, ownerId);

                    if (allItems != null) {
                        for (int i = 0; i < allItems.length(); i++) {
                            JSONObject item = allItems.getJSONObject(i);
                            String mac = item.optString("mac", "").toLowerCase();
                            String serial = item.optString("serial", "").toLowerCase();
                            String itemKey = mac + "|" + serial;

                            // Cache each item individually
                            itemHistoryCache.put(itemKey, item);
                        }
                    }

                    // Mark as fetched
                    productOwnerFetched.put(productOwnerKey, true);
                }

                // 3️⃣ Return from cache
                itemJson = itemHistoryCache.get(key);

                if (itemJson == null) {
                    System.out.println("NOT FOUND: MAC=" + assignedMacAddress
                            + " SERIAL=" + assignedSerialNumber);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemJson;
    }



//    /**
//     * Fetches item history by product, MAC address, and serial number.
//     * Uses cache for repeated requests.
//     */
//    public JSONObject getItemHistoryByProduct(
//            int productId,
//            String ownerId,
//            String assignedMacAddress,
//            String assignedSerialNumber
//    ) {
//        JSONObject itemJson = null;
//
//        // ✔ KEY FORMAT ONLY: mac|serial
//        String key = assignedMacAddress.toLowerCase().trim()
//                + "|" + assignedSerialNumber.toLowerCase().trim();
//
//        try {
//            // 1️⃣ Check cache first
//            itemJson = itemHistoryCache.get(key);
//            if (itemJson != null) return itemJson;
//
//            synchronized (this) {
//
//                // Double-check after acquiring lock
//                itemJson = itemHistoryCache.get(key);
//                if (itemJson != null) return itemJson;
//
//                // 2️⃣ Load list from API
//                CommonAPI commonAPI = new CommonAPI();
//                JSONArray allItems = commonAPI.getItemHistoryByProduct(productId, ownerId);
//
//                // 3️⃣ Try to find matching MAC & Serial
//                if (allItems != null) {
//                    for (int i = 0; i < allItems.length(); i++) {
//                        JSONObject item = allItems.getJSONObject(i);
//
//                        String mac = item.optString("macAddress", "").toLowerCase().trim();
//                        String serial = item.optString("serialNumber", "").toLowerCase().trim();
//
//                        // ✔ Match only MAC + SERIAL
//                        if (assignedMacAddress.equalsIgnoreCase(mac)
//                                && assignedSerialNumber.equalsIgnoreCase(serial)) {
//
//                            itemJson = item;
//                            break;
//                        }
//                    }
//                }
//
//                // 4️⃣ Cache result
//                // (cache null also → prevents repeated API calls next time)
//                itemHistoryCache.put(key, itemJson);
//
//                // 5️⃣ Log if not found
//                if (itemJson == null) {
//                    String message = "Item not found: MAC=" + assignedMacAddress
//                            + ", Serial=" + assignedSerialNumber
//                            + " for productId=" + productId + ", ownerId=" + ownerId;
//
//                    System.out.println(message);
//                    Utility.printLog(logFileName, logModuleName, message, "");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return itemJson;
//    }


//=====================================================================================


    // Cache: productId|ownerId -> item JSON
    private static final Map<String, JSONObject> nonTrackableProductCache = new ConcurrentHashMap<>();

    /**
     * Fetches non-trackable product quantity for a given productId and ownerId.
     * Uses cache to avoid repeated API calls.
     */
    public JSONObject getNonTrackableProductQty(int productId, String ownerId) {
        JSONObject itemJson = null;
        String key = productId + "|" + ownerId;

        try {
            // 1️⃣ Check cache first
            itemJson = nonTrackableProductCache.get(key);
            if (itemJson != null) return itemJson;

            synchronized (this) {
                // Double-check inside synchronized block
                itemJson = nonTrackableProductCache.get(key);
                if (itemJson != null) return itemJson;

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                itemJson = commonAPI.fetchNonTrackableProductQty(productId, ownerId);

                // 3️⃣ Cache result (even if null)
                nonTrackableProductCache.put(key, itemJson);

                // 4️⃣ Log if not found
                if (itemJson == null) {
                    String msg = "Non serialized product not found - " + productId;
                    System.out.println(msg);
                    Utility.printLog(logFileName, logModuleName, msg, String.valueOf(productId));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemJson;
    }

//=============================================================================================


    // Cache: productName -> newProductAmount
    private static final Map<String, String> nonSerializedProductCache = new ConcurrentHashMap<>();

    /**
     * Fetches non-serialized inventory product details with caching.
     */
    public String getNonSerializedInventoryProductDetails(String productName) {
        String result = null;
        String key = productName.toLowerCase().trim();

        try {
            // 1️⃣ Check cache first
            result = nonSerializedProductCache.get(key);
            if (result != null) return result;

            synchronized (this) {
                // Double-check after acquiring lock
                result = nonSerializedProductCache.get(key);
                if (result != null) return result;

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, String> allProducts = commonAPI.getNonSerializedInventoryProducts();

                // 3️⃣ Cache all products
                nonSerializedProductCache.putAll(allProducts);

                // 4️⃣ Retrieve requested product
                result = allProducts.get(key);

                if (result == null) {
                    String msg = "Inventory details not found - " + productName;
                    System.out.println(msg);
                    Utility.printLog(logFileName, logModuleName, msg, productName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


//===============================================================================================


    // Optional cache for non-serialized items: custId|productName|qty -> assemblyId
    private static final Map<String, Integer> nonSerializedInventoryCache = new ConcurrentHashMap<>();

    /**
     * Fetches assemblyId of a customer inventory item (Serialized / Non-Serialized)
     */
    public int getAllCustomerInventoryList(
            String itemType,
            int custId,
            String macAddress,
            String serialNumber,
            String productName,
            String qty
    ) {
        int assemblyId = 0;
        String key = custId + "|" + productName + "|" + qty;

        try {
            // 1️⃣ For non-serialized items, check cache
            if ("Non Serialized Item".equalsIgnoreCase(itemType)) {
                assemblyId = nonSerializedInventoryCache.getOrDefault(key, 0);
                if (assemblyId != 0) return assemblyId;
            }

            // 2️⃣ Fetch from API
            CommonAPI commonAPI = new CommonAPI();
            JSONArray inventoryList = commonAPI.getCustomerInventoryList(custId);

            for (int i = 0; i < inventoryList.length(); i++) {
                JSONObject item = inventoryList.getJSONObject(i);
                String receivedStatus = item.optString("status", "");
                if (!"PENDING".equalsIgnoreCase(receivedStatus)) continue;

                if ("Serialized Item".equalsIgnoreCase(itemType)) {
                    JSONArray mapping = item.optJSONArray("inOutWardMACMapping");
                    if (mapping != null && mapping.length() > 0) {
                        JSONObject map = mapping.getJSONObject(0);
                        String receivedSerial = map.optString("serialNumber", "");
                        String receivedMAC = map.optString("macAddress", "");
                        if (serialNumber.equalsIgnoreCase(receivedSerial)
                                && macAddress.equalsIgnoreCase(receivedMAC)) {
                            assemblyId = item.optInt("id", 0);
                            break;
                        }
                    }
                } else if ("Non Serialized Item".equalsIgnoreCase(itemType)) {
                    String receivedProductName = item.optString("productName", "");
                    int receivedQty = item.optInt("qty", 0);
                    if (productName.equalsIgnoreCase(receivedProductName) && Integer.parseInt(qty) == receivedQty) {
                        assemblyId = item.optInt("id", 0);
                        // Cache result for non-serialized items
                        nonSerializedInventoryCache.put(key, assemblyId);
                        break;
                    }
                }
            }

            if (assemblyId == 0) {
                String msg = "Customer inventory mapping details not found - " + custId;
                System.out.println(msg);
                Utility.printLog(logFileName, logModuleName, msg, String.valueOf(custId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return assemblyId;
    }


//===================================================================================


    public List<Integer> getReasonCategoryIdList(String reasonCategoryName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> reasonCategoryIdList = new ArrayList<Integer>();

        if (reasonCategoryIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            reasonCategoryIdMap = commonAPI.getReasonCategoryIdAll();
        }

        if (reasonCategoryName.equalsIgnoreCase("All")) {

            Set<String> keys = reasonCategoryIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = reasonCategoryIdMap.get(key);
                reasonCategoryIdList.add(id);
            }
        } else {

            map = listMap(reasonCategoryName);
            Set<String> keys = reasonCategoryIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String reasonCategoryNameList[] = reasonCategoryName.split(",");
                for (int j = 0; j < reasonCategoryNameList.length; j++) {
                    if (key.equalsIgnoreCase(reasonCategoryNameList[j])) {
                        reasonCategoryIdList.add(reasonCategoryIdMap.get(key));
                        map.put(reasonCategoryNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Reason-Category details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, reasonCategoryName);
        } else if ((reasonCategoryIdList.size() == 0) && (mandatory)) {
            String message = "Common | Reason-Category details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, reasonCategoryName);
        }

        return reasonCategoryIdList;
    }


    public int getTATId(String tatName) {

        int tadId = 0;
        if (tatIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            tatIdMap = commonAPI.getTATIdAll();
        }

        try {
            tadId = tatIdMap.get(tatName);
        } catch (NullPointerException npe) {
            String message = "Common | TAT details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, tatName);
        }

        return tadId;
    }


    public int getSubReasonCategoryId(String subReasonCategoryName) {

        int subReasonCatergoryId = 0;
        if (subReasonCategoryIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            subReasonCategoryIdMap = commonAPI.getSubReasonCategoryIdAll();
        }

        try {
            subReasonCatergoryId = subReasonCategoryIdMap.get(subReasonCategoryName);
        } catch (NullPointerException npe) {
            String message = "Common | SubReason-Category details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, subReasonCategoryName);
        }

        return subReasonCatergoryId;
    }


    public List<Integer> getProductCategoryIdListForCustomerBind(String productCategoryName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> productCategoryIdList = new ArrayList<Integer>();

        if (productCategoryForCustomerBindIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            productCategoryForCustomerBindIdMap = commonAPI.getProductCategoryIdAllForCustomerBind();
        }


        if (productCategoryName.equalsIgnoreCase("All")) {

            Set<String> keys = productCategoryForCustomerBindIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = productCategoryForCustomerBindIdMap.get(key);
                productCategoryIdList.add(id);
            }
        } else {

            map = listMap(productCategoryName);
            Set<String> keys = productCategoryForCustomerBindIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String productCategoryNameList[] = productCategoryName.split(",");
                for (int j = 0; j < productCategoryNameList.length; j++) {
                    if (key.equalsIgnoreCase(productCategoryNameList[j])) {
                        productCategoryIdList.add(productCategoryForCustomerBindIdMap.get(key));
                        map.put(productCategoryNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Product-Category details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, productCategoryName);
        } else if ((productCategoryIdList.size() == 0) && (mandatory)) {
            String message = "Common | Product-Category details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, productCategoryName);
        }

        return productCategoryIdList;
    }

    public String getProductAndProductCategoryDetails(String productName) {

        String ans = null;

        if (productIdAndBindProductCategoryIdTypeMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            productIdAndBindProductCategoryIdTypeMap = commonAPI.getProductAndProductCategoryDetailsAll();
        }
        ans = productIdAndBindProductCategoryIdTypeMap.get(productName);

        if (ans == null) {
            System.out.println("Common | Product and its Product Category details not found - " + productName);
            Utility.printLog(logFileName, logModuleName, "Product and its Product Category details not found - ", productName);
        }
        return ans;
    }


    public int getVendorId(String vendorName) {

        String searchVendorName = vendorName.toLowerCase();
        int vendorId = 0;
        if (vendorIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            vendorIdMap = commonAPI.getVendorIdAll();
        }

        try {
            vendorId = vendorIdMap.get(searchVendorName);
        } catch (NullPointerException npe) {
            String message = "Common | Vendor details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, vendorName);
        }
        return vendorId;
    }

//	public int getProductId(String productName) {
//
//		String searchProductName = productName.toLowerCase();
//		int productId = 0;
//		if(productIdMap.isEmpty()) {
//			CommonAPI commonAPI = new CommonAPI();
//			productIdMap = commonAPI.getProductIdAll();
//		}
//
//		try {
//			productId = productIdMap.get(searchProductName);
//		} catch(NullPointerException npe) {
//			String message = "Common | Product details not found";
//			ProductUtility.stopExecution(logFileName, logModuleName, message, productName);
//		}
//		return productId;
//	}

    // Thread-safe cache for product name → ID
    private static final Map<String, Integer> productIdCache = new ConcurrentHashMap<>();

    /**
     * Fetches Product ID by name with caching.
     * Loads all products once initially, then only fetches missing keys.
     */
    public int getProductId(String productName) {
        int productId = 0;
        String key = productName.toLowerCase().trim();

        try {
            // 1️⃣ Check cache first
            Integer cachedId = productIdCache.get(key);
            if (cachedId != null) return cachedId;

            synchronized (this) {
                // Double-check after acquiring lock
                cachedId = productIdCache.get(key);
                if (cachedId != null) return cachedId;

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, Integer> allProducts = commonAPI.getProductIdAll();

                // 3️⃣ Cache all results
                productIdCache.putAll(allProducts);

                // 4️⃣ Retrieve requested product
                productId = productIdCache.getOrDefault(key, 0);

                if (productId == 0) {
                    String message = "Common | Product details not found for: " + productName;
                    ProductUtility.stopExecution(logFileName, logModuleName, message, productName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productId;
    }


    public int getCASId(String CASName) {

        String searchCASName = CASName.toLowerCase();
        int casId = 0;
        if (casIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            casIdMap = commonAPI.getCASIdAll();
        }

        try {
            casId = casIdMap.get(searchCASName);
        } catch (NullPointerException npe) {
            String message = "Common | CAS details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, CASName);
        }
        return casId;
    }

    public int getWarehouseId(String warehouseName) {

        String searchWarehouseName = warehouseName.toLowerCase();
        int warehouseId = 0;
        if (warehouseIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            warehouseIdMap = commonAPI.getWarehouseIdAll();
        }

        try {
            warehouseId = warehouseIdMap.get(searchWarehouseName);
        } catch (NullPointerException npe) {
            String message = "Common | Warehouse details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, warehouseName);
        }
        return warehouseId;
    }

    public String getProductCategoryMACSerialTrackDetailsAll(String productName) {

        String ans = null;

        if (productIdAndBindProductCategoryIdMACSerialTrackDetailMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            productIdAndBindProductCategoryIdMACSerialTrackDetailMap = commonAPI.getProductCategoryMACSerialTrackDetailsAll();
        }
        ans = productIdAndBindProductCategoryIdMACSerialTrackDetailMap.get(productName);

        if (ans == null) {
            System.out.println("Common | Product and its Product Category details not found - " + productName);
            Utility.printLog(logFileName, logModuleName, "Product and its Product Category details not found - ", productName);
        }
        return ans;
    }

    public int getDirectChargeId(String directChargeName) {

        String searchDirectChargeName = directChargeName.toLowerCase();
        int directChargeId = 0;
        if (directChargeIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            directChargeIdMap = commonAPI.getDirectChargeIdAll();
        }

        try {
            directChargeId = directChargeIdMap.get(searchDirectChargeName);
        } catch (NullPointerException npe) {
            String message = "Common | Direct Charge details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, directChargeName);
        }
        return directChargeId;
    }

    public int getPartnerPlanGroupId(String planGroupName) {

        String searchPlanGroupName = planGroupName.toLowerCase();
        int planGroupId = 0;
        if (planGroupIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            planGroupIdMap = commonAPI.getPartnerPlanGroupIdAll();
        }

        try {
            planGroupId = planGroupIdMap.get(searchPlanGroupName);
        } catch (NullPointerException npe) {
            String message = "Common | Partner PlanGroup details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, planGroupName);
        }
        return planGroupId;
    }

    public List<Integer> getTeamIdListBasedOnAttchedStaff(String teamName) {

        boolean mandatory = false;
        Map<String, Boolean> map = null;
        List<Integer> teamIdList = new ArrayList<Integer>();

        if (warehouseTeamIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            warehouseTeamIdMap = commonAPI.getTeamIdAllListBasedOnAttchedStaff();
        }

        if (teamName.equalsIgnoreCase("All")) {

            Set<String> keys = warehouseTeamIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                int id = warehouseTeamIdMap.get(key);
                teamIdList.add(id);
            }
        } else {

            map = listMap(teamName);
            Set<String> keys = warehouseTeamIdMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                String teamNameList[] = teamName.split(",");
                for (int j = 0; j < teamNameList.length; j++) {
                    if (key.equalsIgnoreCase(teamNameList[j])) {
                        teamIdList.add(warehouseTeamIdMap.get(key));
                        map.put(teamNameList[j], true);
                        break;
                    }
                }
            }
        }

        if ((map != null) && (map.containsValue(false))) {
            String message = "Common | Warehouse Team details not found - " + getListNotFoundKeys(map);
            ;
            ProductUtility.stopExecution(logFileName, logModuleName, message, teamName);
        } else if ((teamIdList.size() == 0) && (mandatory)) {
            String message = "Common | Warehouse Team details not found - ";
            ProductUtility.stopExecution(logFileName, logModuleName, message, teamName);
        }

        return teamIdList;
    }

    public String getProductCategoryIdAndType(String productCategoryName) {

        String searchProductCategoryName = productCategoryName.toLowerCase();
        String ans = null;

        if (productCategoryIdAndTypeMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            productCategoryIdAndTypeMap = commonAPI.getProductCategoryIdAndTypeDetailsAll();
        }
        ans = productCategoryIdAndTypeMap.get(searchProductCategoryName);

        if (ans == null) {
            String message = "Common | Product Category details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, productCategoryName);
        }

        return ans;
    }

    public int getPopId(String popName) {

        String searchPopName = popName.toLowerCase();
        int popId = 0;
        if (popIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            popIdMap = commonAPI.getPopIdAll();
        }

        try {
            popId = popIdMap.get(searchPopName);
        } catch (NullPointerException npe) {
            String message = "Common | Pop details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, popName);
        }
        return popId;
    }


    public String getServiceParamIdsWithServiceId(int serviceId) {

        String serviceParamIds = null;

        if (serviceParamIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            serviceParamIdMap = commonAPI.getServiceParamIdsWithServiceIdAll();
        }
        serviceParamIds = serviceParamIdMap.get(serviceId);

        if (serviceParamIds == null) {
            String message = "Common | ServiceId details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, String.valueOf(serviceId));
        }

        return serviceParamIds;
    }


    public String getPlanBundleDetails(String planGroupName) {

        String searchPlanBundleName = planGroupName.toLowerCase();
        String ans = null;

        if (planGroupDetailsMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            planGroupDetailsMap = commonAPI.getPlanBundleDetailsAll();
        }
        ans = planGroupDetailsMap.get(searchPlanBundleName);

        if (ans == null) {
            String message = "Common | Plan Bundle details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, planGroupName);
        }

        return ans;
    }

//******************************** FOR BELOW CACHING IS REMAINED ***********************************************

    public int getMunicipalityId_OLD(String municipalityName) {

        String apiURL = getAPIURL("pincode/all");

        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");
        int municipalityId = 0;

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String receivedMunicipalityName = jsonArray.getJSONObject(i).getString("pincode");
                if (municipalityName.equalsIgnoreCase(receivedMunicipalityName)) {
                    municipalityId = jsonArray.getJSONObject(i).getInt("pincodeid");
                    break;
                }
            }
        }

        if (municipalityId == 0) {
            System.out.println("Common | Municipality details not found - " + municipalityName);
            Utility.printLog(logFileName, logModuleName, "Municipality details not found - ", municipalityName);
        }
        return municipalityId;
    }

    public String getMasterDetailsFromMunicipalityId_OLD(int municipalityId) {

        String apiURL = getAPIURL("pincode/all");
        apiURL = getAPIURL(apiURL);

        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONObject picodeJSONObject = jsonResponse.getJSONObject("data");
            int countryId = picodeJSONObject.getInt("countryId");
            int stateId = picodeJSONObject.getInt("stateId");
            int cityId = picodeJSONObject.getInt("cityId");

            ans = countryId + ":" + stateId + ":" + cityId;
        }

        return ans;
    }


    private Map<String, Boolean> listMap(String list) {

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String keys[] = list.split(",");
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].trim();
            if (!key.equals("")) {
                map.put(key, false);
            }
        }
        return map;
    }

    private String getListNotFoundKeys(Map<String, Boolean> map) {

        String result = "";
        Set<String> keys = map.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            boolean value = map.get(key);
            if (!value) {
                result = result + "," + key;
            }
        }
        result = result.substring(1);
        String message = "\"" + (result) + "\" from provided list";
        return message;
    }

    public boolean checkcustomerUsernameIsAlreadyExists(String customerName) {

        String apiURL = "cpm/customer/customerUsernameIsAlreadyExists/" + customerName;
        apiURL = getAPIURL(apiURL);

        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        boolean checkCust = false;
        if (status == 200) {
            checkCust = jsonResponse.getBoolean("isAlreadyExists");
        }
        return checkCust;
    }

    @SuppressWarnings("unchecked")
//	public int getCustomerId(String userName,String customerType) {
//
//		String jsonString = null;
//		JSONObject searchCustomerJson = new JSONObject();
//
//		List<JSONObject> customerFilterJsonObjectList = new ArrayList<JSONObject>();
//		JSONObject filterObject = new JSONObject();
//
//		filterObject.put("filterDataType", "");
//		filterObject.put("filterValue", userName);
//		filterObject.put("filterColumn", "username");
//		filterObject.put("filterOperator", "equalto");
//		filterObject.put("filterCondition", "and");
//
//		customerFilterJsonObjectList.add(filterObject);
//		searchCustomerJson.put("filters", customerFilterJsonObjectList);
//        //Below this two line filter the pages---->
//		searchCustomerJson.put("page", 1);
//		searchCustomerJson.put("pageSize", 100); //here increase page size for better result but according to me this is issue suppose if i get username in 2nd page the i have to add one loop
//
//		jsonString = searchCustomerJson.toString();
//
//		String custType = "";
//		String apiURL = "";
//		if(customerType.equalsIgnoreCase("prepaid")) {
//			custType = "Prepaid";
//		}else if(customerType.equalsIgnoreCase("postpaid")) {
//			custType = "Postpaid";
//		}
//
//		apiURL = getAPIURL("cpm/customers/search/" + custType);
//		String APIBody = jsonString;
//
//		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
//		int status = JSONResponseBody.getInt("status");
//		int customerId = 0;
//
//		if (status == 200) {
//			JSONArray jsonArray = JSONResponseBody.getJSONArray("customerList");
//			for (int i = 0; i < jsonArray.length(); i++) {
//				String receivedUserName = jsonArray.getJSONObject(i).getString("username");
//				if (receivedUserName.equalsIgnoreCase(userName)) {
//					customerId = jsonArray.getJSONObject(i).getInt("id");
//					break;
//				}
//			}
//		}
//
//		if (customerId == 0) {
//			String msg = custType + " Customer details not found - " + userName;
//			System.out.println(msg);
//			Utility.printLog(logFileName, logModuleName, msg, "");
//		}
//
//		return customerId;
//	}

// ========================================================================================================
// Cache for customer username|type combinations
//    private static final Map<String, Integer> customerIdCache = new ConcurrentHashMap<>();
//
//    /**
//     * Fetches Customer ID by username and type with caching.
//     * Loads from API only when not found in cache.
//     */
//    public int getCustomerId(String userName, String customerType) {
//        int customerId = 0;
//        String key = (userName + "|" + customerType).toLowerCase().trim();
//
//        try {
//            // 1️⃣ Check cache first
//            Integer cachedId = customerIdCache.get(key);
//            if (cachedId != null) {
//                return cachedId;
//            }
//
//            synchronized (this) {
//                // Double-check in synchronized block
//                cachedId = customerIdCache.get(key);
//                if (cachedId != null) {
//                    return cachedId;
//                }
//
//                // 2️⃣ Load data from API
//                CommonAPI commonAPI = new CommonAPI();
//                Map<String, Integer> allCustomers = commonAPI.getAllCustomersByType(customerType);
//
//                // 3️⃣ Update cache with all loaded customers
//                customerIdCache.putAll(allCustomers);
//
//                // 4️⃣ Try to find requested one
//                customerId = customerIdCache.getOrDefault(key, 0);
//
//                if (customerId == 0) {
//                    // Try re-fetch if not found (maybe new customer added)
//                    Map<String, Integer> newCustomers = commonAPI.getAllCustomersByType(customerType);
//                    customerIdCache.putAll(newCustomers);
//                    customerId = customerIdCache.getOrDefault(key, 0);
//
//                    if (customerId == 0) {
//                        String msg = customerType + " Customer not found - " + userName;
//                        Utility.printLog(logFileName, logModuleName, msg, "");
//                        ProductUtility.stopExecution(logFileName, logModuleName, msg, userName);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return customerId;
//    }

    //=======================================================================

    private static final Map<String, Integer> customerIdCache = new ConcurrentHashMap<>();

    /**
     * Fetches Customer ID by username and type with caching.
     * Loads from API only when not found in cache.
     */

    public int getCustomerId(String userName, String customerType) {
        int customerId = 0;

        try {
            String key = (userName + "|" + customerType).toLowerCase().trim();

            // 1️⃣ Check cache first
            Integer id = customerIdCache.get(key);

            if (id == null) {
                synchronized (this) {

                    // Double check inside synchronized block
                    id = customerIdCache.get(key);

                    if (id == null) {

                        // 2️⃣ If cache empty → load everything only once
                        if (customerIdCache.isEmpty()) {
                            CommonAPI commonAPI = new CommonAPI();
                            Map<String, Integer> all = commonAPI.getAllCustomersByType(customerType);
                            customerIdCache.putAll(all);

                            // Try again after first load
                            id = customerIdCache.get(key);
                        }

                        // 3️⃣ If still not found → DO NOT CALL API AGAIN
                        if (id == null) {
                            String message = customerType + " Customer not found - " + userName;
                            Utility.printLog(logFileName, logModuleName, message, "");
                            ProductUtility.stopExecution(logFileName, logModuleName, message, key);

                            return 0; // Or throw if needed
                        }
                    }
                }
            }

            customerId = id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerId;
    }


//=====================================================================================================================================

    //=======================================================================

    private static final Map<String, Integer> CAFcustomerIdCache = new ConcurrentHashMap<>();

    /**
     * Fetches Customer ID by username and type with caching.
     * Loads from API only when not found in cache.
     */

    public int getCAFCustomerId(String userName) {
        int customerId = 0;

        try {
            String key = (userName).toLowerCase().trim();

            // 1️⃣ Check cache first
            Integer id = CAFcustomerIdCache.get(key);

            if (id == null) {
                synchronized (this) {

                    // Double check inside synchronized block
                    id = CAFcustomerIdCache.get(key);

                    if (id == null) {

                        // 2️⃣ If cache empty → load everything only once
                        if (CAFcustomerIdCache.isEmpty()) {
                            CommonAPI commonAPI = new CommonAPI();
                            Map<String, Integer> all = commonAPI.getAllCAF_CustomerId();
                            CAFcustomerIdCache.putAll(all);

                            // Try again after first load
                            id = CAFcustomerIdCache.get(key);
                        }

                        // 3️⃣ If still not found → DO NOT CALL API AGAIN
                        if (id == null) {
                            String message = " CAF Customer not found - " + userName;
                            Utility.printLog(logFileName, logModuleName, message, "");
                            ProductUtility.stopExecution(logFileName, logModuleName, message, key);

                            return 0; // Or throw if needed
                        }
                    }
                }
            }

            customerId = id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerId;
    }


//=====================================================================================================================================


    private static final Map<String, Integer> CAFcustomerIdCacheNew =
            new ConcurrentHashMap<String, Integer>();

    private static final Object LOCK = new Object();

    private static volatile boolean cacheLoaded = false;

    public int getCAFCustomerIdNew (String userName) {

        int customerId = 0;

        try {

            String key = userName.toLowerCase().trim();

            // 1️⃣ Check cache first
            Integer id = CAFcustomerIdCacheNew.get(key);

            if (id != null) {
                return id.intValue();
            }

            // 2️⃣ Load cache only once
            if (!cacheLoaded) {

                synchronized (LOCK) {

                    // Double check
                    if (!cacheLoaded) {

                        CommonAPI commonAPI = new CommonAPI();

                        Map<String, Integer> all =
                                commonAPI.getAllCAF_CustomerId();

                        if (all != null && !all.isEmpty()) {
                            CAFcustomerIdCacheNew.putAll(all);
                        }

                        cacheLoaded = true;

                        System.out.println("CAF cache size: "
                                + CAFcustomerIdCacheNew.size());
                    }
                }

//                System.out.println(CAFcustomerIdCacheNew);

                // try again after loading
                id = CAFcustomerIdCacheNew.get(key);
            }

            // 3️⃣ Not found
            if (id == null) {

                String message = "CAF Customer not found - " + userName;

                Utility.printLog(logFileName, logModuleName, message, "");

                ProductUtility.stopExecution(
                        logFileName,
                        logModuleName,
                        message,
                        key);

                return 0;
            }

            customerId = id.intValue();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customerId;
    }


    //=================================================================================

    private static final Map<Integer, Map<String, String>> customerPlanCache = new ConcurrentHashMap<>();

    /**
     * Fetches plan details for a given customer and plan name.
     * Returns "serviceId:connectionNumber:custPlanMappingId".
     */
    public String getPlanByCustService(int custId, String planName) {
        String result = null;

        try {
            // Normalize name
            String searchPlanName = planName.toLowerCase().trim();

            // 1️⃣ Check cache first
            Map<String, String> planMap = customerPlanCache.get(custId);
            if (planMap != null && planMap.containsKey(searchPlanName)) {
                return planMap.get(searchPlanName);
            }

            synchronized (this) {
                // Double check after sync
                planMap = customerPlanCache.get(custId);
                if (planMap != null && planMap.containsKey(searchPlanName)) {
                    return planMap.get(searchPlanName);
                }

                // 2️⃣ Load from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, String> allPlans = commonAPI.getPlansByCustomer(custId);

                // 3️⃣ Cache all results
                customerPlanCache.put(custId, allPlans);

                // 4️⃣ Retrieve target plan
                result = allPlans.get(searchPlanName);
            }

            if (result == null) {
                String msg = "Customer Plan not found for plan: " + planName + " (custId=" + custId + ")";
                Utility.printLog(logFileName, logModuleName, msg, "");
                System.out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    //===================================================================================================================

    // =====================================================================================================

    /**
     * Cache:
     * key   = referenceno (lowercase)
     * value = paymentId
     */
    private static final Map<String, String> referenceCache =
            new ConcurrentHashMap<>();

    /**
     * Returns paymentId for a given reference number (case-insensitive).
     * Loads data from API once and caches it.
     */
    public String getPaymentByReferenceNo(String referenceNo) {

        if (referenceNo == null || referenceNo.trim().isEmpty()) {
            return null;
        }

        // Normalize reference number
        String searchReference = referenceNo.trim().toLowerCase();

        try {
            // 1️⃣ Fast cache lookup
            String cachedValue = referenceCache.get(searchReference);
            if (cachedValue != null) {
                return cachedValue;
            }

            synchronized (this) {

                // Double-check inside sync
                cachedValue = referenceCache.get(searchReference);
                if (cachedValue != null) {
                    return cachedValue;
                }

                // 2️⃣ Load all references from API
                CommonAPI commonAPI = new CommonAPI();
                Map<String, String> allReferences = commonAPI.getReferenceNoAll();

                if (allReferences != null && !allReferences.isEmpty()) {
                    // Normalize keys before caching
                    for (Map.Entry<String, String> entry : allReferences.entrySet()) {
                        if (entry.getKey() != null) {
                            referenceCache.put(
                                    entry.getKey().trim().toLowerCase(),
                                    entry.getValue()
                            );
                        }
                    }
                }
            }

            // 3️⃣ Final lookup
            String result = referenceCache.get(searchReference);

            if (result == null) {
                String msg = "Payment reference not found: " + referenceNo;
                Utility.printLog(logFileName, logModuleName, msg, "");
                System.out.println(msg);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //===================================================================================================================


    // lead sourcemaster
    public int getLeadSourceMasterId(String leadSourceName) {

        String searchLeadSourceMasterName = leadSourceName.toLowerCase();
        int leadSourceId = 0;
        if (leadSourceMAsterIdMap.isEmpty()) {
            CommonAPI commonAPI = new CommonAPI();
            leadSourceMAsterIdMap = commonAPI.getLeadSourceAll();
        }

        try {
            leadSourceId = leadSourceMAsterIdMap.get(searchLeadSourceMasterName);
        } catch (NullPointerException npe) {
            String message = "Common | Lead Source Master details not found";
            ProductUtility.stopExecution(logFileName, logModuleName, message, leadSourceName);
        }

        return leadSourceId;
    }


}
