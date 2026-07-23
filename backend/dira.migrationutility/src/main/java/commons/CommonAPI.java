package commons;

import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import api.RestExecution;
import masterdata.Location2Ids;
import masterdata.Location4Ids;
import masterdata.Location5Ids;
import masterdata.Location6Ids;
import masterdata.LocationIds;
import utility.Utility;

public class CommonAPI extends RestExecution {

    private String logFileName = "common.log";
    private String logModuleName = "CommonGetAPI";

    public Map<Integer, String> getPlanDetailsAll() {

        Map<Integer, String> planDetailsMap = new HashMap<Integer, String>();
        String apiURL = "cpm/postpaidplan/all";
        apiURL = getAPIURL(apiURL);
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        String ans = "";
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("postpaidplanList");
            for (int i = 0; i < jsonArray.length(); i++) {

				int planId = jsonArray.getJSONObject(i).getInt("id");
                String serviceName = jsonArray.getJSONObject(i).getString("serviceName");

                if (serviceName != null) {
                    serviceName = serviceName.trim();

                    // If it's exactly "[]", make it empty
                    if ("[]".equals(serviceName)) {
                        serviceName = "";
                    } else {
                        // Remove only the brackets if present
                        serviceName = serviceName.replace("[", "").replace("]", "");
                    }
                }

                //float offerprice = jsonArray.getJSONObject(i).getFloat("offerprice");
				float offerprice = (float) jsonArray.getJSONObject(i).optDouble("offerprice", 0.0);

				int validity = jsonArray.getJSONObject(i).getInt("validity");
				String unitsOfValidity = jsonArray.getJSONObject(i).getString("unitsOfValidity");
				float newOfferPrice = jsonArray.getJSONObject(i).getFloat("newOfferPrice");

                ans = serviceName + ":" + offerprice + ":" + validity + ":" + unitsOfValidity + ":" + newOfferPrice;

                planDetailsMap.put(planId, ans);
            }
        }
        return planDetailsMap;
    }

    public Map<String, Integer> getPlanIdAll() {

        Map<String, Integer> planIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/postpaidplan/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("postpaidplanList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String planName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int planId = jsonArray.getJSONObject(i).getInt("id");
                planIdMap.put(planName, planId);
            }
        }
        return planIdMap;
    }

    public Map<String, Integer> getPartnerIdAll() {

        Map<String, Integer> partnerIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("pms/partner/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("partnerlist");
            for (int i = 0; i < jsonArray.length(); i++) {
                String partnerName = jsonArray.getJSONObject(i).getString("name").trim();
                int partnerId = jsonArray.getJSONObject(i).getInt("id");
                partnerIdMap.put(partnerName, partnerId);
            }
        }
        return partnerIdMap;
    }

    public Map<String, Integer> getServiceAreaIdAll() {

        Map<String, Integer> serviceAreaIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/all");
        //   String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/site/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String serviceAreaName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int serviceAreaId = jsonArray.getJSONObject(i).getInt("id");
                serviceAreaIdMap.put(serviceAreaName, serviceAreaId);
            }
        }
        return serviceAreaIdMap;
    }


    // tumil Direct charge
    //plan details for direct charge  for tumil rajnish
    public Map<String, String> getBasePlandetailsCustomerDirect(String planname, int custid, String service) {

        Map<String, String> plandetailshirechy = new HashMap<String, String>();

        String apiURL = getAPIURL("cpm/subscriber/getActivePlanList/" + custid + "?isNotChangePlan=true");
        JSONObject jsonResponse = httpGet(apiURL);
        int responseCode = jsonResponse.getInt("responseCode");

        String ans = null;
        if (responseCode == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                String basePlan = jsonArray.getJSONObject(i).getString("planName");
                //System.out.println("planname "+ basePlan);
                // System.out.println(" sheet planname "+ planname);
                String servicename = jsonArray.getJSONObject(i).getString("service");
                //System.out.println("service name "+servicename);
                // System.out.println("sheet service "+service);
                if (basePlan.equalsIgnoreCase(planname) && servicename.equalsIgnoreCase(service)) {
                    //System.out.println();
                    int planId = jsonArray.getJSONObject(i).getInt("planId");
                    String connection_no = jsonArray.getJSONObject(i).getString("connection_no");
                    int serviceId = jsonArray.getJSONObject(i).getInt("serviceId");
                    String startDate = jsonArray.getJSONObject(i).getString("startDate");
                    String endDate = jsonArray.getJSONObject(i).getString("endDate");

                    ans = basePlan + ":" + planId + ":" + connection_no + ":" + service + ":" + serviceId + ":" + startDate + ":" + endDate;
                    plandetailshirechy.put(basePlan, ans);
                }
            }
        }
        return plandetailshirechy;
    }

    //charge  details for direct charge for tumil  rajnish
    public Map<String, String> getCustomerDirectChargeDetails(String chargeName, String serviceId) {

        Map<String, String> directChargedetails = new HashMap<String, String>();

        String apiURL = getAPIURL("cpm/charge/ByType/CUSTOMER_DIRECT?serviceId=" + serviceId);
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("chargelist");
            for (int i = 0; i < jsonArray.length(); i++) {

                String directChargename = jsonArray.getJSONObject(i).getString("name");
                String chargetype = jsonArray.getJSONObject(i).getString("chargetype");
                if (directChargename.equalsIgnoreCase(chargeName) && chargetype.equalsIgnoreCase(chargetype)) {
                    int id = jsonArray.getJSONObject(i).getInt("id");
                    float actualprice = jsonArray.getJSONObject(i).getFloat("actualprice");


                    ans = id + ":" + actualprice;
                    directChargedetails.put(directChargename, ans);
                }
            }
        }
        return directChargedetails;
    }

    public Map<String, Integer> getChargeIdAll() {

        Map<String, Integer> chargeIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/charge/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("chargelist");
            for (int i = 0; i < jsonArray.length(); i++) {
                String chargeName = jsonArray.getJSONObject(i).getString("name").trim();
                int chargeId = jsonArray.getJSONObject(i).getInt("id");
                chargeIdMap.put(chargeName, chargeId);
            }
        }
        return chargeIdMap;
    }

    public Map<String, Integer> getQosPolicyIdAll() {

        Map<String, Integer> qosPolicyIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/qosPolicy/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String planName = jsonArray.getJSONObject(i).getString("name").trim();
                int planId = jsonArray.getJSONObject(i).getInt("id");
//                int qosPolicyId = jsonArray.getJSONObject(i).getInt("qosPolicyId");
//                System.out.println(jsonArray);
                qosPolicyIdMap.put(planName, planId);
            }
        }
        return qosPolicyIdMap;
    }

    public Map<String, Integer> getTimeBasePolicyIdAll() {

        Map<String, Integer> timebasePolicyIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/timebasepolicy/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String planName = jsonArray.getJSONObject(i).getString("name").trim();
                int planId = jsonArray.getJSONObject(i).getInt("id");
                timebasePolicyIdMap.put(planName, planId);
            }
        }
        return timebasePolicyIdMap;
    }

    public Map<String, Integer> getServiceIdListAll() {

        Map<String, Integer> serviceIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/planservice/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("serviceList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String serviceName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int serviceaId = jsonArray.getJSONObject(i).getInt("id");
                serviceIdMap.put(serviceName, serviceaId);
            }
        }
        return serviceIdMap;
    }

    public Map<String, Integer> getTaxIdAll() {

        Map<String, Integer> taxIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/taxes/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("taxlist");
            for (int i = 0; i < jsonArray.length(); i++) {
                String planName = jsonArray.getJSONObject(i).getString("name").trim();
                int planId = jsonArray.getJSONObject(i).getInt("id");
                taxIdMap.put(planName, planId);
            }
        }
        return taxIdMap;
    }

    public Map<String, Integer> getBusinessUnitIdListAll() {

        Map<String, Integer> businessUnitIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/businessUnit/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String businessUnitName = jsonArray.getJSONObject(i).getString("buname").toLowerCase().trim();
                int businessUnitId = jsonArray.getJSONObject(i).getInt("id");
                businessUnitIdMap.put(businessUnitName, businessUnitId);
            }
        }
        return businessUnitIdMap;
    }

    public Map<String, Integer> getRegionIdListAll() {

        Map<String, Integer> regionIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/region/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String businessUnitName = jsonArray.getJSONObject(i).getString("rname").toLowerCase().trim();
                int businessUnitId = jsonArray.getJSONObject(i).getInt("id");
                regionIdMap.put(businessUnitName, businessUnitId);
            }
        }
        return regionIdMap;
    }

    public Map<String, Integer> getBranchIdListAll() {

        Map<String, Integer> branchIdMap = new HashMap<String, Integer>();
//		String apiURL = getAPIURL("SavbillCommonGateway/branchManagement/all"); // Old Api
        String apiURL = getAPIURL("SavbillCommonGateway/branchManagement/findAll");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String branchName = jsonArray.getJSONObject(i).getString("name").trim();
                int branchId = jsonArray.getJSONObject(i).getInt("id");
                branchIdMap.put(branchName, branchId);
            }
        }
        return branchIdMap;
    }

    public Map<String, String> getMasterDetailsByMunicipalityNameAll() {

        Map<String, String> municipalityHierarchy = new HashMap<String, String>();
//		String apiURL = getAPIURL("SavbillCommonGateway/pincode/all");
        String apiURL = getAPIURL("SavbillCommonGateway/pincode/findAllPincode");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                String receivedMunicipalityName = jsonArray.getJSONObject(i).getString("pincode");
                int pincodeid = jsonArray.getJSONObject(i).getInt("pincodeid");
                int countryId = jsonArray.getJSONObject(i).getInt("countryId");
                int stateId = jsonArray.getJSONObject(i).getInt("stateId");
                int cityId = jsonArray.getJSONObject(i).getInt("cityId");

                ans = pincodeid + ":" + cityId + ":" + stateId + ":" + countryId;
                municipalityHierarchy.put(receivedMunicipalityName, ans);
            }
        }
        return municipalityHierarchy;
    }

    // detail by area name  new api for savanna
    public Map<String, String> getMasterDetailsByAreaNameAll() {

        Map<String, String> areaHierarchy = new HashMap<String, String>();
        String apiURL = getAPIURL("SavbillCommonGateway/area/allAreas");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {

                String receivedAreaName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();

                int areaid = jsonArray.getJSONObject(i).getInt("id");
                int cityid = jsonArray.getJSONObject(i).getInt("cityId");
                int countryid = jsonArray.getJSONObject(i).getInt("countryId");
                int stateid = jsonArray.getJSONObject(i).getInt("stateId");
                int pincodeId = jsonArray.getJSONObject(i).getInt("pincodeId");

                ans = areaid + ":" + cityid + ":" + countryid + ":" + stateid + ":" + pincodeId;
                areaHierarchy.put(receivedAreaName, ans);
            }
        }
        return areaHierarchy;
    }


    //
    // detail by area name  new api for savanna
	/*
	public Map<String, String> getMasterDetailsByAreaNameValidatingWithPincodeAll() {
		Map<String, String> areaHierarchy = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/area/allAreas");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		String ans = null;
		if (status == 200) {

			JSONArray jsonArray = jsonResponse.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {

				String receivedAreaName = jsonArray.getJSONObject(i).getString("name");
							
				int areaid = jsonArray.getJSONObject(i).getInt("id");
				int cityid = jsonArray.getJSONObject(i).getInt("cityId");
				int countryid = jsonArray.getJSONObject(i).getInt("countryId");
				int stateid = jsonArray.getJSONObject(i).getInt("stateId");
				int pincodeId=jsonArray.getJSONObject(i).getInt("pincodeId");
				System.out.println("Raw pincodeId: " +pincodeId);

				receivedAreaName=receivedAreaName+pincodeId;
				ans = areaid + ":" + cityid + ":" + countryid + ":" + stateid + ":" + pincodeId;
				// here key will be areaname+pincodeId 
				areaHierarchy.put(receivedAreaName, ans);
			}
		}
		return areaHierarchy;
	}
*/

    // updated
    public Map<String, String> getMasterDetailsByAreaNameValidatingWithPincodeAll() {
        Map<String, String> areaHierarchy = new HashMap<>();
        String apiURL = getAPIURL("SavbillCommonGateway/area/allAreas");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject areaObj = jsonArray.getJSONObject(i);

                String receivedAreaName = areaObj.optString("name", "");

                int areaid = areaObj.optInt("id", -1);
                int cityid = areaObj.optInt("cityId", -1);
                int countryid = areaObj.optInt("countryId", -1);
                int stateid = areaObj.optInt("stateId", -1);

                Object rawPincodeId = areaObj.opt("pincodeId");

                if (rawPincodeId == null || rawPincodeId.toString().equalsIgnoreCase("null")) {
                    System.out.println("Skipping: invalid or null pincodeId at index " + i + ": " + rawPincodeId);
                    continue;
                }

                int pincodeId;
                try {
                    pincodeId = Integer.parseInt(rawPincodeId.toString());
                } catch (NumberFormatException e) {
                    System.out.println("Skipping: pincodeId not a valid number at index " + i + ": " + rawPincodeId);
                    continue;
                }

                String key = receivedAreaName + pincodeId;
                String value = areaid + ":" + cityid + ":" + countryid + ":" + stateid + ":" + pincodeId;
                areaHierarchy.put(key, value);
            }
        }

        return areaHierarchy;
    }

    public Map<String, List<Integer>> getPincodeListWithServiceArea() {

        Map<String, List<Integer>> serviceAreaPincodesMap = new HashMap<>();
        String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/dropdown/all");
        JSONObject jsonResponse = httpGet(apiURL);

        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String serviceAreaName = obj.getString("name").toLowerCase().trim();

                // Correct conversion for pincodes (JSONArray → List<Integer>)
                List<Integer> pincodeList = new ArrayList<>();
                JSONArray pinArray = obj.getJSONArray("pincodes");

                for (int j = 0; j < pinArray.length(); j++) {
                    pincodeList.add(pinArray.getInt(j));
                }

                serviceAreaPincodesMap.put(serviceAreaName, pincodeList);
            }
        }

        return serviceAreaPincodesMap;
    }

    public Map<String, String> getWardHierarchyDetailsByWardNameAll() {

        Map<String, String> wardHierarchy = new HashMap<String, String>();
        String apiURL = getAPIURL("SavbillCommonGateway/area/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");
        String detail = "";

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                String municipalityName = jsonArray.getJSONObject(i).getString("code").trim();
                String wardName = jsonArray.getJSONObject(i).getString("name").trim();
                String key = wardName + "_" + municipalityName;

                int wardId = jsonArray.getJSONObject(i).getInt("id");
                int pincodeId = jsonArray.getJSONObject(i).getInt("pincodeId");
                int cityId = jsonArray.getJSONObject(i).getInt("cityId");
                int stateId = jsonArray.getJSONObject(i).getInt("stateId");
                int countryId = jsonArray.getJSONObject(i).getInt("countryId");
                detail = wardId + ":" + pincodeId + ":" + cityId + ":" + stateId + ":" + countryId;
                wardHierarchy.put(key, detail);
            }
        }
        return wardHierarchy;
    }


    public Map<String, Integer> getCountryIdAll() {

        Map<String, Integer> countryIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/country/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("countryList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String countryName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int countryId = jsonArray.getJSONObject(i).getInt("id");
                countryIdMap.put(countryName, countryId);
            }
        }
        return countryIdMap;
    }

    public Map<String, Integer> getProvinceIdAll() {

        Map<String, Integer> stateIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/state/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("stateList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String provinceName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int provinceId = jsonArray.getJSONObject(i).getInt("id");
                stateIdMap.put(provinceName, provinceId);
            }
        }
        return stateIdMap;
    }

    public Map<String, Integer> getDistrictIdAll() {

        Map<String, Integer> districtIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/city/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("cityList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String districtName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int districtId = jsonArray.getJSONObject(i).getInt("id");
                districtIdMap.put(districtName, districtId);
            }
        }
        return districtIdMap;
    }

    //====================================================================================================================

    // Thread-safe cache
    private final Map<String, LocationIds> allIdMap = new ConcurrentHashMap<>();

    /**
     * Fetch all country-state-city IDs via POST API and cache them.
     * Supports pagination automatically. Implemented for Uganda customer but any data will work with this.
     */
    public Map<String, LocationIds> getCountryStateCityAllIdsPost() {
        // Return cached map if already loaded
        if (!allIdMap.isEmpty()) {
            return allIdMap;
        }

        String apiURL = getAPIURL("SavbillCommonGateway/city/list");
        int page = 1;
        int pageSize = 5000; // adjust based on API limits
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                // Build request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                // Call POST API
                JSONObject jsonResponse = httpPost(apiURL, String.valueOf(requestBody));

                // Check for response status
                int status = jsonResponse.optInt("status", 0);
                if (status != 200) {
                    System.err.println("API returned status: " + status);
                    break;
                }

                // Extract pagination info
                JSONObject pageDetails = jsonResponse.optJSONObject("pageDetails");
                int totalPages = (pageDetails != null) ? pageDetails.optInt("totalPages", 1) : 1;

                // Extract list
                JSONArray cityList = jsonResponse.optJSONArray("cityList");
                if (cityList == null || cityList.isEmpty()) {
                    System.out.println("No records found on page " + page);
                    break;
                }

                // Process city list
                for (int i = 0; i < cityList.length(); i++) {
                    JSONObject cityObj = cityList.getJSONObject(i);

                    // Extract country and state info
                    JSONObject statePojo = cityObj.optJSONObject("statePojo");
                    if (statePojo == null) continue;

                    JSONObject countryPojo = statePojo.optJSONObject("countryPojo");
                    if (countryPojo == null) continue;

                    String countryName = countryPojo.optString("name", "").toLowerCase().trim();
                    int countryId = countryPojo.optInt("id", 0);

                    String stateName = statePojo.optString("name", "").toLowerCase().trim();
                    int stateId = statePojo.optInt("id", 0);

                    String cityName = cityObj.optString("name", "").toLowerCase().trim();
                    int cityId = cityObj.optInt("id", 0);

                    // Create composite key
                    String compositeKey = countryName + "|" + stateName + "|" + cityName;

                    // Store unique IDs in map
                    allIdMap.putIfAbsent(compositeKey, new LocationIds(countryId, stateId, cityId));
                }

//                System.out.println("Fetched page " + page + " (" + cityList.length() + " records)");

                hasMoreData = page < totalPages;
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

//        System.out.println("Total cached records: " + allIdMap.size());
        return allIdMap;
    }

//===================================================================================

    // Thread-safe cache
    private final Map<String, Location2Ids> all2IdMap = new ConcurrentHashMap<>();

    public Map<String, Location2Ids> getPairCityPincodeAllIdsPost() {
        if (!all2IdMap.isEmpty()) {
            return all2IdMap;
        }

        String apiURL = getAPIURL("SavbillCommonGateway/pincode");
        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
                int responseCode = jsonResponse.optInt("responseCode", 0);
                if (responseCode != 200) {
                    System.err.println("API returned responseCode: " + responseCode);
                    break;
                }

                JSONArray dataList = jsonResponse.optJSONArray("dataList");
                if (dataList == null || dataList.isEmpty()) {
                    System.out.println("No records found on page " + page);
                    break;
                }

                for (int i = 0; i < dataList.length(); i++) {
                    JSONObject obj = dataList.getJSONObject(i);

                    int pincodeId = obj.optInt("pincodeId", 0);
                    int cityId = obj.optInt("cityId", 0);

                    String cityName = obj.optString("cityName", "").toLowerCase().trim();
                    String pincodeName = obj.optString("pincode", "").toLowerCase().trim();

                    // Composite key includes pincode name
                    String compositeKey = cityName + "|" + pincodeName;

                    // Store all 4 IDs + pincode name
                    all2IdMap.putIfAbsent(compositeKey,
                            new Location2Ids(cityId, pincodeId));
                }

                hasMoreData = page < jsonResponse.optInt("totalPages", page);
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        return all2IdMap;
    }

    //===============================================================================================


    // Thread-safe cache
    private final Map<String, Location4Ids> all4IdMap = new ConcurrentHashMap<>();

    public Map<String, Location4Ids> getCountryStateCityPincodeAllIdsPost() {
        // ✅ return cached map if already loaded
        if (!all4IdMap.isEmpty()) return all4IdMap;

        String apiURL = getAPIURL("SavbillCommonGateway/pincode");
        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());

                if (jsonResponse.optInt("responseCode", 0) != 200) {
                    System.err.println("API returned responseCode: " + jsonResponse.optInt("responseCode", 0));
                    break;
                }

                JSONArray dataList = jsonResponse.optJSONArray("dataList");
                if (dataList != null) {
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject obj = dataList.getJSONObject(i);

                        int countryId = obj.optInt("countryId", 0);
                        int stateId = obj.optInt("stateId", 0);
                        int cityId = obj.optInt("cityId", 0);
                        int pincodeId = obj.optInt("pincodeid", 0);

                        String countryName = obj.optString("countryName", "").trim().toLowerCase();
                        String stateName = obj.optString("stateName", "").trim().toLowerCase();
                        String cityName = obj.optString("cityName", "").trim().toLowerCase();
                        String pincodeName = obj.optString("pincode", "").trim().toLowerCase();

                        if (countryName.isEmpty() || stateName.isEmpty() || cityName.isEmpty() || pincodeName.isEmpty()) {
                            continue;
                        }

                        // Create composite key
                        String compositeKey = countryName + "|" + stateName + "|" + cityName + "|" + pincodeName;

                        // Store unique IDs in map
                        all4IdMap.putIfAbsent(compositeKey, new Location4Ids(countryId, stateId, cityId, pincodeId));
                    }
                }
                int totalPages = jsonResponse.optInt("totalPages", page);
                hasMoreData = page < totalPages;
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("✅ Total unique locations loaded: " + all4IdMap.size());
        return all4IdMap;
    }


//=====================================================================================================

    //For ServiceArea only For Duplication and any data
    private Map<String, Location6Ids> all6IdMap = new HashMap<>();

    /**
     * Fetch all locations from API (paged) and store in map.
     */
    public Map<String, Integer> getCityPincodeAll9IdsPost() {
        Map<String, Integer> cityNamepincodeNameIdMap = new HashMap<>();

        String apiURL = getAPIURL("SavbillCommonGateway/pincode");
        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
                if (jsonResponse.optInt("responseCode", 0) != 200) break;

                JSONArray dataList = jsonResponse.optJSONArray("dataList");
                if (dataList != null) {
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject obj = dataList.getJSONObject(i);
                        int cityId = obj.optInt("cityId", 0);
                        int pincodeId = obj.optInt("pincodeid", 0);

                        String cityName = obj.optString("cityName", "").trim().toLowerCase();
                        String pincodeName = obj.optString("pincode", "").trim().toLowerCase();
                        String cityName_pincodeName = cityName + "_" + pincodeName ;
                        cityNamepincodeNameIdMap.put(cityName_pincodeName, pincodeId);
//                        all6IdMap.put(cityName_pincodeName, pincodeId);
                    }
                }

                int totalPages = jsonResponse.optInt("totalPages", page);
                hasMoreData = page < totalPages;
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("✅ Total unique locations loaded: " + cityNamepincodeNameIdMap.size());
        return cityNamepincodeNameIdMap;
    }

    //=================================================================


//    //For ServiceArea only For Duplication and any data
//    private Map<String, Location6Ids> all6IdMap = new HashMap<>();
//
//    /**
//     * Fetch all locations from API (paged) and store in map.
//     */
//    public Map<String, Integer> getCityPincodeAll6IdsPost() {
//        Map<String, Integer> cityNamepincodeNameIdMap = new HashMap<>();
//
//        String apiURL = getAPIURL("SavbillCommonGateway/pincode");
//        int page = 1;
//        int pageSize = 5000;
//        boolean hasMoreData = true;
//
//        while (hasMoreData) {
//            try {
//                JSONObject requestBody = new JSONObject();
//                requestBody.put("page", page);
//                requestBody.put("pageSize", pageSize);
//
//                JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
//                if (jsonResponse.optInt("responseCode", 0) != 200) break;
//
//                JSONArray dataList = jsonResponse.optJSONArray("dataList");
//                if (dataList != null) {
//                    for (int i = 0; i < dataList.length(); i++) {
//                        JSONObject obj = dataList.getJSONObject(i);
//                        int cityId = obj.optInt("cityId", 0);
//                        int pincodeId = obj.optInt("pincodeid", 0);
//
//                        String cityName = obj.optString("cityName", "").trim().toLowerCase();
//                        String pincodeName = obj.optString("pincode", "").trim().toLowerCase();
//                        String cityName_pincodeName = cityName + "_" + pincodeName ;
//
//                        cityNamepincodeNameIdMap.put(cityName_pincodeName, pincodeId);
//
//                    }
//                }
//
//                int totalPages = jsonResponse.optInt("totalPages", page);
//                hasMoreData = page < totalPages;
//                page++;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//        System.out.println("✅ Total unique locations loaded: " + cityNamepincodeNameIdMap.size());
//        return cityNamepincodeNameIdMap;
//    }


    //================================================================================================

    // Thread-safe cache
    private final Map<String, Location5Ids> all5IdMap = new ConcurrentHashMap<>();

    public Map<String, Location5Ids> getCityPincodeWardAllIdsPost() {
        if (!all5IdMap.isEmpty()) {
            return all5IdMap;
        }

        String apiURL = getAPIURL("SavbillCommonGateway/area");
        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
                int responseCode = jsonResponse.optInt("responseCode", 0);
                if (responseCode != 200) {
                    System.err.println("API returned responseCode: " + responseCode);
                    break;
                }

                JSONArray dataList = jsonResponse.optJSONArray("dataList");
                if (dataList == null || dataList.isEmpty()) {
                    System.out.println("No records found on page " + page);
                    break;
                }

                for (int i = 0; i < dataList.length(); i++) {
                    JSONObject obj = dataList.getJSONObject(i);

                    int wardId = obj.optInt("id", 0);
                    int pincodeId = obj.optInt("pincodeId", 0);
                    int cityId = obj.optInt("cityId", 0);
                    int stateId = obj.optInt("stateId", 0);
                    int countryId = obj.optInt("countryId", 0);


                    String cityName = obj.optString("cityName", "").toLowerCase().trim();
                    String pincodeName = obj.optString("code", "").toLowerCase().trim();
                    String wardName = obj.optString("name", "").toLowerCase().trim();
                    String stateName = obj.optString("stateName", "").toLowerCase().trim();
                    String countryName = obj.optString("countryName", "").toLowerCase().trim();


                    // Composite key includes pincode name
                    String compositeKey = cityName + "|" + pincodeName + "|" + wardName + "|" + stateName + "|" + countryName;

                    // Store all 5 IDs + Subarea name
                    all5IdMap.putIfAbsent(compositeKey,
                            new Location5Ids(cityId, pincodeId, wardId, stateId, countryId));
                }

                hasMoreData = page < jsonResponse.optInt("totalPages", page);
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
//        Utility.printLog("Temp_WardName.log", logModuleName,"Map List = ", all5IdMap.toString());
//        System.out.println(all5IdMap);
        return all5IdMap;
    }

    //=======================================================================================================



    //====================================================================================================

    /**
     * Fetch all locations from API (paged) and store in map.
     */
    public Map<String, Integer> getAllCAF_CustomerId() {

        Map<String, Integer> customerUsernameIdMap = new HashMap<>();

        String apiURL = getAPIURL("cpm/customers/list/Prepaid?orgcusttype=false");

        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {

            try {

                JSONObject requestBody = new JSONObject();

                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);
                requestBody.put("status", "NewActivation");

                JSONObject jsonResponse =
                        httpPost(apiURL, requestBody.toString());

                if (jsonResponse == null) {
                    System.out.println("API returned null response.");
                    break;
                }

                JSONArray dataList =
                        jsonResponse.optJSONArray("customerList");

                if (dataList != null && dataList.length() > 0) {

                    for (int i = 0; i < dataList.length(); i++) {

                        JSONObject obj = dataList.getJSONObject(i);

                        int custId = obj.optInt("id", 0);

                        String username = obj
                                .optString("username", "")
                                .trim()
                                .toLowerCase();

                        if (!username.isEmpty()) {
                            customerUsernameIdMap.put(username, custId);
                        }
                    }
                }

                JSONObject pageDetails =
                        jsonResponse.optJSONObject("pageDetails");

                int totalPages = page;

                if (pageDetails != null) {
                    totalPages = pageDetails.optInt("totalPages", page);
                }

                hasMoreData = page < totalPages;

                System.out.println("Loaded page " + page +
                        " | Total so far: " +
                        customerUsernameIdMap.size());

                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("✅ Total unique customers loaded: "
                + customerUsernameIdMap.size());

        return customerUsernameIdMap;
    }




    //=================================================================


    public Map<String, Integer> getAllCAFTicket_CustomerId() {

        Map<String, Integer> customerUsernameIdMapNew = new HashMap<>();

        String apiURL = getAPIURL("cpm/customers/search/Prepaid");

        int page = 1;
        int pageSize = 5000;
        boolean hasMoreData = true;

        while (hasMoreData) {

            try {

                // ===============================
                // Build Request Body
                // ===============================

                JSONObject requestBody = new JSONObject();

                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);

                // ✅ status is separate (NOT inside filters)
                requestBody.put("status", "NewActivation");

                requestBody.put("fromDate", JSONObject.NULL);
                requestBody.put("toDate", JSONObject.NULL);

                // ===============================
                // Filters Array
                // ===============================

                JSONArray filters = new JSONArray();

                JSONObject filterObj = new JSONObject();
                filterObj.put("filterDataType", "");
                filterObj.put("filterValue", "Sales@savanna");
                filterObj.put("filterColumn", "currentAssigneeName");
                filterObj.put("filterOperator", "equalto");
                filterObj.put("filterCondition", "and");

                filters.put(filterObj);

                requestBody.put("filters", filters);

                // DEBUG (optional)
                // System.out.println("REQUEST BODY: " + requestBody.toString(4));

                // ===============================
                // Call API
                // ===============================

                JSONObject jsonResponse =
                        httpPost(apiURL, requestBody.toString());

                if (jsonResponse == null) {
                    System.out.println("API returned null response.");
                    break;
                }

                JSONArray dataList =
                        jsonResponse.optJSONArray("customerList");

                if (dataList != null && dataList.length() > 0) {

                    for (int i = 0; i < dataList.length(); i++) {

                        JSONObject obj = dataList.getJSONObject(i);

                        int custId = obj.optInt("id", 0);

                        String custusername = obj
                                .optString("username", "")
                                .trim()
                                .toLowerCase();

                        if (!custusername.isEmpty()) {
                            customerUsernameIdMapNew.put(custusername, custId);
                        }
                    }
                }

                // ===============================
                // Pagination Handling
                // ===============================

                JSONObject pageDetails =
                        jsonResponse.optJSONObject("pageDetails");

                int totalPages = page;

                if (pageDetails != null) {
                    totalPages = pageDetails.optInt("totalPages", page);
                }

                hasMoreData = page < totalPages;

                System.out.println("Loaded page " + page +
                        " | Total customers so far: "
                        + customerUsernameIdMapNew.size());

                page++;

            } catch (Exception e) {

                e.printStackTrace();
                break;
            }
        }

        System.out.println("✅ Total unique customers loaded: "
                + customerUsernameIdMapNew.size());

        return customerUsernameIdMapNew;
    }




    //======================================================================


    //    // Cache per customer type (optional local API-level cache)
    private final Map<String, Integer> customerIdMap = new ConcurrentHashMap<>();
//
//    /**
//     * Fetches all customers (username|type → ID) via API with pagination.
//     */
//    public Map<String, Integer> getAllCustomersByType(String customerType) {
//        // Reuse cached map if already loaded
//        if (!customerIdMap.isEmpty()) {
//            return customerIdMap;
//        }
//
//        try {
//            String custType = "";
//            if (customerType.equalsIgnoreCase("prepaid")) {
//                custType = "Prepaid";
//            } else if (customerType.equalsIgnoreCase("postpaid")) {
//                custType = "Postpaid";
//            } else {
//                throw new IllegalArgumentException("Invalid customer type: " + customerType);
//            }
//
//            String apiURL = getAPIURL("cpm/customers/search/" + custType);
//
//            int page = 1;
//            int pageSize = 5000;
//            boolean hasMorePages = true;
//
//            while (hasMorePages) {
//                // Build filter JSON
//                JSONObject filter = new JSONObject();
//                filter.put("filterDataType", "");
//                filter.put("filterValue", "");
//                filter.put("filterColumn", "username");
//                filter.put("filterOperator", "contains");
//                filter.put("filterCondition", "and");
//
//                JSONArray filters = new JSONArray();
//                filters.put(filter);
//
//                JSONObject requestBody = new JSONObject();
//                requestBody.put("filters", filters);
//                requestBody.put("page", page);
//                requestBody.put("pageSize", pageSize);
//
//                // API call
//                JSONObject response = httpPost(apiURL, requestBody.toString());
//                int status = response.optInt("status", 0);
//
//                if (status != 200) break;
//
//                JSONArray customerList = response.optJSONArray("customerList");
//                if (customerList == null || customerList.isEmpty()) break;
//
//                // Process response
//                for (int i = 0; i < customerList.length(); i++) {
//                    JSONObject cust = customerList.getJSONObject(i);
//                    String userName = cust.optString("username", "").toLowerCase().trim();
//                    int id = cust.optInt("id", 0);
//
//                    if (!userName.isEmpty() && id != 0) {
//                        String key = userName + "|" + customerType.toLowerCase().trim();
//                        customerIdMap.putIfAbsent(key, id);
//                    }
//                }
//
//                // Continue until no more pages
//                hasMorePages = page < response.optInt("totalPages", page);
//                page++;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(customerIdMap);
//        return customerIdMap;
//    }


    public Map<String, Integer> getAllCustomersByType(String customerType) {

        if (!customerIdMap.isEmpty()) {
            return customerIdMap;
        }

        try {
            String custType;
            if (customerType.equalsIgnoreCase("prepaid")) {
                custType = "Prepaid";
            } else if (customerType.equalsIgnoreCase("postpaid")) {
                custType = "Postpaid";
            } else {
                throw new IllegalArgumentException("Invalid customer type: " + customerType);
            }

            String apiURL = getAPIURL("cpm/customers/search/" + custType);

            int page = 1;
            int pageSize = 5000;
            boolean hasMore = true;

            while (hasMore) {

                // Build EXACT request expected by Backend
                JSONArray filters = new JSONArray();
                JSONObject filter = new JSONObject();

                filter.put("filterDataType", "");
                filter.put("filterValue", "");
                filter.put("filterColumn", "any");
                filter.put("filterOperator", "equalto");
                filter.put("filterCondition", "and");

                filters.put(filter);

                JSONObject requestBody = new JSONObject();
                requestBody.put("filters", filters);
                requestBody.put("page", page);
                requestBody.put("pageSize", pageSize);
                requestBody.put("fromDate", JSONObject.NULL);
                requestBody.put("toDate", JSONObject.NULL);
                requestBody.put("status", "Active");

                JSONObject response = httpPost(apiURL, requestBody.toString());

                int status = response.optInt("status", 0);
                if (status != 200) {
                    break;
                }

                JSONArray customerList = response.optJSONArray("customerList");
                if (customerList == null || customerList.length() == 0) {
                    break;
                }

                // Store records
                for (int i = 0; i < customerList.length(); i++) {
                    JSONObject cust = customerList.getJSONObject(i);

                    String username = cust.optString("username", "").toLowerCase().trim();
                    int id = cust.optInt("id", 0);

                    if (!username.isEmpty() && id != 0) {
                        String key = username + "|" + customerType.toLowerCase();
                        customerIdMap.putIfAbsent(key, id);
                    }
                }

                // Pagination
                int totalPages = response.optInt("totalPages", -1);

                if (totalPages == -1) {
                    hasMore = (customerList.length() == pageSize);
                } else {
                    hasMore = (page < totalPages);
                }

                page++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.print(customerIdMap);
        System.out.println("Total records stored: " + customerIdMap.size());
        return customerIdMap;
    }




//====================================================================================================================


    /**
     * Fetches all plan details for a customer.
     * Returns Map<planNameLower, "serviceId:connectionNumber:custPlanMappingId">
     */
    public Map<String, String> getPlansByCustomer(int custId) {
        Map<String, String> planMap = new ConcurrentHashMap<>();

        try {
            String queryParam = "cpm/subscriber/getPlanByCustService/" + custId
                    + "?isAllRequired=true&isNotChangePlan=true";
            String apiURL = getAPIURL(queryParam);

            long startTime = System.currentTimeMillis();
            JSONObject JSONResponseBody = httpGet(apiURL);
            long endTime = System.currentTimeMillis();

            int status = JSONResponseBody.optInt("responseCode", -1);
            if (status != 0) {
                Utility.printLog(logFileName, logModuleName,
                        "Failed to fetch plans for custId=" + custId,
                        "ResponseCode: " + status);
                return planMap;
            }

            JSONArray jsonArray = JSONResponseBody.optJSONArray("dataList");
            if (jsonArray == null) return planMap;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject planObj = jsonArray.getJSONObject(i);
                String planName = planObj.optString("planName", "").toLowerCase().trim();
                int serviceId = planObj.optInt("serviceId", 0);
                String connectionNumber = planObj.optString("connection_no", "");
                int custPlanMappingId = planObj.optInt("custPlanMapppingId", 0);

                if (!planName.isEmpty() && serviceId > 0) {
                    String value = serviceId + ":" + connectionNumber + ":" + custPlanMappingId;
                    planMap.put(planName, value);
                }
            }

            Utility.printLog(logFileName, logModuleName,
                    "Fetched " + planMap.size() + " plans for custId=" + custId,
                    "TimeTaken=" + (endTime - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return planMap;
    }

    //===================================================================================================================


    public Map<String, String> getReferenceNoAll() {

        // key   = referenceno (lowercase)
        // value = paymentId
        Map<String, String> referenceMap = new ConcurrentHashMap<>();

        try {
            int page = 1;
            int pageSize = 5000;
            int totalPages = 1; // will update after first API call

            do {
                String url = "cpm/paymentGateway/payment/search"
                        + "?customerid=&paystatus=&paytodate=&payfromdate="
                        + "&type=Payment&invoiceNumber=&chequeNo=&staff=&paymode="
                        + "&branchname=&buID=&referenceno=&approveId=&receiptNo="
                        + "&chequedate=&paydetails1=&destinationBank=&partnerName="
                        + "&serviceAreaId="
                        + "&page=" + page
                        + "&pageSize=" + pageSize;

                String apiURL = getAPIURL(url);

                JSONObject jsonResponse = httpGet(apiURL);
                int status = jsonResponse.optInt("status", -1);

                if (status != 200) {
                    System.out.println("Payment API failed at page " + page);
                    break;
                }

                // 1️⃣ Read payments
                JSONArray paymentArray = jsonResponse.optJSONArray("creditDocumentPojoList");
                if (paymentArray != null) {
                    for (int i = 0; i < paymentArray.length(); i++) {
                        JSONObject paymentObj = paymentArray.getJSONObject(i);

                        String referenceNo = paymentObj.optString("referenceno", null);
                        String paymentId = String.valueOf(paymentObj.optLong("id", 0));

                        if (referenceNo != null && !referenceNo.trim().isEmpty()) {
                            referenceMap.put(
                                    referenceNo.trim().toLowerCase(),
                                    paymentId
                            );
                        }
                    }
                }

                // 2️⃣ Read pagination info (only once or every page – safe)
                JSONObject pageDetails = jsonResponse.optJSONObject("pageDetails");
                if (pageDetails != null) {
                    totalPages = pageDetails.optInt("totalPages", totalPages);
                }

                Utility.printLog(
                        logFileName,
                        logModuleName,
                        "Loaded page " + page + " / " + totalPages
                                + " | Total refs so far: " + referenceMap.size(),
                        ""
                );

                page++;

            } while (page <= totalPages);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return referenceMap;
    }


    //===================================================================================================================

    // new api for get pincode
    public Map<String, Integer> getPincodeIdAll() {

        Map<String, Integer> pincodeIdMap = new HashMap<String, Integer>();
        String oldapiURL = getAPIURL("SavbillCommonGateway/pincode/all"); // it is old api where changes while in savanna

        String apiURL = getAPIURL("SavbillCommonGateway/pincode/getAll");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String pincodeName = jsonArray.getJSONObject(i).getString("pincode").toLowerCase().trim();
                int pincodeId = jsonArray.getJSONObject(i).getInt("pincodeid");
                pincodeIdMap.put(pincodeName, pincodeId);
            }
        }
        return pincodeIdMap;
    }

    // get all area id
    public Map<String, Integer> getAreaIdAll() {

        Map<String, Integer> areaIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/area/all"); // it is old api where changes while in savanna

        //	String apiURL = getAPIURL("SavbillCommonGateway/area/getAll");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String areaName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int areaId = jsonArray.getJSONObject(i).getInt("id");
                areaIdMap.put(areaName, areaId);
            }
        }
        return areaIdMap;
    }


    // sub area
    // get all area id
    public Map<String, Integer> getSubAreaIdAll() {

        Map<String, Integer> subAreaIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/subarea/all"); // it is old api where changes while in savanna

        //	String apiURL = getAPIURL("SavbillCommonGateway/area/getAll");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String subareaName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int subAreaId = jsonArray.getJSONObject(i).getInt("id");
                subAreaIdMap.put(subareaName, subAreaId);
            }
        }
        return subAreaIdMap;
    }


    public Map<String, Integer> getInvestmentCodeIdAll() {

        Map<String, Integer> investmentCodeIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/investmentCode/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String investmentCodeName = jsonArray.getJSONObject(i).getString("icname").toLowerCase().trim();
                int investmentCodeId = jsonArray.getJSONObject(i).getInt("id");
                investmentCodeIdMap.put(investmentCodeName, investmentCodeId);
            }
        }
        return investmentCodeIdMap;
    }

    public Map<String, Integer> getBusinessVerticalIdAll() {

        Map<String, Integer> businessVerticalIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/businessverticals/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String businessVerticalName = jsonArray.getJSONObject(i).getString("vname").trim();
                int businessVerticalId = jsonArray.getJSONObject(i).getInt("id");
                businessVerticalIdMap.put(businessVerticalName, businessVerticalId);
            }
        }
        return businessVerticalIdMap;
    }

    public Map<String, Integer> getTeamIdListAll() {

        Map<String, Integer> teamIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/teams/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String teamName = jsonArray.getJSONObject(i).getString("name").trim();
                int teamId = jsonArray.getJSONObject(i).getInt("id");
                teamIdMap.put(teamName, teamId);
            }
        }
        return teamIdMap;
    }

    public Map<String, Integer> getRoleIdAll() {

        Map<String, Integer> roleIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillCommonGateway/role/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String roleName = jsonArray.getJSONObject(i).getString("rolename").trim();
                int roleId = jsonArray.getJSONObject(i).getInt("id");
                roleIdMap.put(roleName, roleId);
            }
        }
        return roleIdMap;
    }

    public Map<String, Integer> getStaffIdAll() {
        Map<String, Integer> staffIdMap = new HashMap<>();
//         String apiURL = getAPIURL("SavbillCommonGateway/staffuser/allActive");
        String apiURL = getAPIURL("SavbillInventoryManagement/staffuser/allActive");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String staffUserName = jsonArray.getJSONObject(i).getString("username").trim();
                int staffUserId = jsonArray.getJSONObject(i).getInt("id");

                // ✅ store key in lowercase
                staffIdMap.put(staffUserName, staffUserId);
            }
        }
        return staffIdMap;
    }


    //===================================================================

    public Map<String, Integer> postListStaff() {
        Map<String, Integer> staffUserNameIdMap = new HashMap<>();

        String apiURL = getAPIURL("SavbillCommonGateway/staffuser/list?product=BSS");

        int page = 1;
        int size = 5000;

        while (true) {

            JSONObject requestBody = new JSONObject();
            requestBody.put("page", page);
            requestBody.put("pagesize", size);

            // ✅ convert JSONObject to String
            JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
            int status = jsonResponse.getInt("status");

            if (status != 200) {
                break;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("staffUserlist");
            if (jsonArray.isEmpty()) {
                break;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String staffUserName = obj.getString("username").trim().toLowerCase();
                int StaffId = obj.getInt("id");

                staffUserNameIdMap.put(staffUserName, StaffId);
            }

            page++;
        }

        return staffUserNameIdMap;
    }


    //=====================================================================

    //===================================================================

    public Map<String, Integer> getTeamIdAllBSS() {
        Map<String, Integer> teamIdMap = new HashMap<>();

        String apiURL = getAPIURL("SavbillCommonGateway/teams/permissions?productType=BSS");

        int page = 1;
        int size = 5000;

        while (true) {

            JSONObject requestBody = new JSONObject();
            requestBody.put("page", page);
            requestBody.put("size", size);

            // ✅ convert JSONObject to String
            JSONObject jsonResponse = httpPost(apiURL, requestBody.toString());
            int status = jsonResponse.getInt("responseCode");

            if (status != 200) {
                break;
            }

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            if (jsonArray.isEmpty()) {
                break;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String teamName = obj.getString("name").trim();
                int teamId = obj.getInt("id");

                teamIdMap.put(teamName, teamId);
            }

            page++;
        }

        return teamIdMap;
    }




    //=====================================================================

    public Map<String, Integer> getMVNOIdAll() {
        Map<String, Integer> mvnoMap = new HashMap<>();

        String apiURL = getAPIURL("SavbillCommonGateway/mvno/getMvnoNameAndIds");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.optInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");

            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name").trim();
                int id = jsonArray.getJSONObject(i).getInt("id");

                // Store as-is (case-sensitive) — because your getMvnoId() also trims only
                mvnoMap.put(name, id);
            }
        }

        return mvnoMap;
    }


    public Map<String, Integer> getReasonCategoryIdAll() {

        Map<String, Integer> reasonCategoryIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("TicketManagement/ticketReasonCategory/getAllActiveReasonCatgory");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String reasonCategoryName = jsonArray.getJSONObject(i).getString("categoryName").trim();
                int reasonCategoryId = jsonArray.getJSONObject(i).getInt("id");
                reasonCategoryIdMap.put(reasonCategoryName, reasonCategoryId);
            }
        }
        return reasonCategoryIdMap;
    }

    public Map<String, Integer> getTATIdAll() {

        Map<String, Integer> tatIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("TicketManagement/tickettatmatrix/searchByStatus");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String tatName = jsonArray.getJSONObject(i).getString("name").trim();
                int tatId = jsonArray.getJSONObject(i).getInt("id");
                tatIdMap.put(tatName, tatId);
            }
        }
        return tatIdMap;
    }

    public Map<String, Integer> getSubReasonCategoryIdAll() {

        Map<String, Integer> subReasonCategoryIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("TicketManagement/ticketReasonSubCategory");
        JSONObject rootCauseJson = new JSONObject();
        rootCauseJson.put("page", 1);
        rootCauseJson.put("pageSize", 5000);
        String APIBody = rootCauseJson.toString();

        JSONObject jsonResponse = httpPost(apiURL, APIBody);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String subCategoryName = jsonArray.getJSONObject(i).getString("subCategoryName").trim();
                int subCategoryId = jsonArray.getJSONObject(i).getInt("id");
                subReasonCategoryIdMap.put(subCategoryName, subCategoryId);
            }
        }
        return subReasonCategoryIdMap;
    }

    public Map<String, Integer> getProductCategoryIdAllForCustomerBind() {

        Map<String, Integer> productCategoryForCustomerBindIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/productCategory/getAllProductCategoriesByType?Type=customerbind");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String tatName = jsonArray.getJSONObject(i).getString("name").trim();
                int tatId = jsonArray.getJSONObject(i).getInt("id");
                productCategoryForCustomerBindIdMap.put(tatName, tatId);
            }
        }
        return productCategoryForCustomerBindIdMap;
    }

    public Map<String, String> getProductAndProductCategoryDetailsAll() {

        Map<String, String> productIdAndBindProductCategoryIdTypeMap = new HashMap<String, String>();
        String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllActiveProduct");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                String productName = jsonArray.getJSONObject(i).getString("name");
                int productId = jsonArray.getJSONObject(i).getInt("id");

                JSONObject productCategory = jsonArray.getJSONObject(i).getJSONObject("productCategory");
                int productCategoryId = productCategory.getInt("id");
                String productCategoryType = productCategory.getString("type");

                ans = productId + ":" + productCategoryId + ":" + productCategoryType;
                productIdAndBindProductCategoryIdTypeMap.put(productName, ans);
            }
        }
        return productIdAndBindProductCategoryIdTypeMap;
    }

    public Map<String, Integer> getVendorIdAll() {

        Map<String, Integer> vendorIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/vendor/findAll");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String vendorName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int vendorId = jsonArray.getJSONObject(i).getInt("id");
                vendorIdMap.put(vendorName, vendorId);
            }
        }
        return vendorIdMap;
    }


    /**
     * Fetch all inventory products for a service.
     * Returns Map<serviceId|productNameLower, newProductAmount>
     */
    public Map<String, String> getInventoryProductsByService(int serviceId) {
        Map<String, String> productMap = new ConcurrentHashMap<>();
        try {
            String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllProductByServiceId?serviceId=" + serviceId);
            JSONObject response = httpGet(apiURL);

            int status = response.optInt("responseCode", 0);
            if (status != 200) return productMap;

            JSONArray dataList = response.optJSONArray("dataList");
            if (dataList == null) return productMap;

            for (int i = 0; i < dataList.length(); i++) {
                JSONObject obj = dataList.getJSONObject(i);
                String productName = obj.optString("name", "").toLowerCase().trim();
                String newProductAmount = obj.optString("newProductAmount", ""); // default empty

                if (!productName.isEmpty()) {
                    String key = serviceId + "|" + productName;
                    productMap.putIfAbsent(key, newProductAmount);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productMap;
    }


    //===========================================================

    /**
     * Fetches all item history of a product for a given owner.
     * Returns JSONArray of all items.
     */
//    public JSONArray getItemHistoryByProduct(int productId, String ownerId) {
//        JSONArray finalList = new JSONArray();
//
//        try {
//            String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct";
////                    + "?productId=" + productId
////                    + "&ownerId=" + ownerId
////                    + "&ownerType=staff";
//            apiURL = getAPIURL(apiURL);
//
//            int page = 1;
//            int pageSize = 5000;
//
//            while (true) {
//
//                JSONObject payload = new JSONObject();
//                payload.put("productId", productId);
//                payload.put("ownerId", ownerId);
//                payload.put("ownerType", "staff");
//
//                JSONObject pagination = new JSONObject();
//                pagination.put("page", page);
//                pagination.put("pageSize", pageSize);
//                payload.put("paginationRequestDTO", pagination);
//
//                JSONObject response = httpPost(apiURL, payload.toString());
//                int status = response.optInt("responseCode", 0);
//                System.out.println(response);
//                if (status != 200) break;
//
//                JSONArray arr = response.optJSONArray("dataList");
//                if (arr == null || arr.length() == 0) {
//                    break; // No more pages
//                }
//
//                // Add to final array
//                for (int i = 0; i < arr.length(); i++) {
//                    finalList.put(arr.get(i));
//                }
//
//                // Next page
//                page++;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return finalList;
//    }


    public JSONArray getItemHistoryByProduct(int productId, String ownerId) {

        JSONArray finalList = new JSONArray();

        try {
            String apiURL = getAPIURL("SavbillInventoryManagement/outwards/getItemHistoryByProduct");

            int page = 1;
            int pageSize = 5000;

            while (true) {

                // ---- Request Payload ----
                JSONObject payload = new JSONObject();
                payload.put("productId", productId);
                payload.put("ownerId", ownerId);
                payload.put("ownerType", "staff");

                JSONObject pagination = new JSONObject();
                pagination.put("page", page);
                pagination.put("pageSize", pageSize);
                payload.put("paginationRequestDTO", pagination);

                // ---- API CALL ----
                JSONObject response = httpPost(apiURL, payload.toString());

                int status = response.optInt("responseCode", 0);
                if (status != 200) break;

                JSONArray arr = response.optJSONArray("dataList");
                if (arr == null || arr.length() == 0) break;

                // ---- Extract only mac + serial ----
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    JSONObject pair = new JSONObject();
                    pair.put("mac", obj.optString("macAddress", ""));
                    pair.put("serial", obj.optString("serialNumber", ""));
                    pair.put("itemId", obj.optString("itemId", ""));
                    pair.put("condition", obj.optString("condition", ""));
                    pair.put("id", obj.optString("id", ""));
                    pair.put("inwardId", obj.optString("inwardId", ""));
                    pair.put("outwardId", obj.optString("outwardId", ""));
                    pair.put("status", obj.optString("status", ""));
                    finalList.put(pair);
                }

                System.out.println("[INFO] Page " + page + " loaded → " + arr.length() + " records");

                // If fewer than pageSize → last page
                if (arr.length() < pageSize) break;

                page++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalList;
    }




//    // Global cache map
//    private final Map<String, JSONObject> itemHistoryMap = new HashMap<>();
//
//    public JSONArray getItemHistoryByProduct(int productId, String ownerId) {
//        JSONArray finalList = new JSONArray();
//
//        try {
//            String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct"
//                    + "?productId=" + productId
//                    + "&ownerId=" + ownerId
//                    + "&ownerType=staff";
//            apiURL = getAPIURL(apiURL);
//
//            int page = 1;
//            int pageSize = 5000;
//
//            while (true) {
//
//                JSONObject payload = new JSONObject();
//                payload.put("productId", productId);
//                payload.put("ownerId", ownerId);
//                payload.put("ownerType", "staff");
//
//                JSONObject pagination = new JSONObject();
//                pagination.put("page", page);
//                pagination.put("pageSize", pageSize);
//                payload.put("paginationRequestDTO", pagination);
//
//                JSONObject response = httpPost(apiURL, payload.toString());
//                int status = response.optInt("responseCode", 0);
//
//                if (status != 200) break;
//
//                JSONArray arr = response.optJSONArray("dataList");
//                if (arr == null || arr.length() == 0) break;
//
//                for (int i = 0; i < arr.length(); i++) {
//
//                    JSONObject item = arr.getJSONObject(i);
//
//                    // Extract mac + serial
//                    String mac = item.optString("macAddress", "").toLowerCase().trim();
//                    String serial = item.optString("serialNumber", "").toLowerCase().trim();
//
//                    // Build key with ONLY mac + serial
//                    String key = mac + "|" + serial;
//
//                    // Store item in map
//                    itemHistoryMap.put(key, item);
//
//                    // Add to final list
//                    finalList.put(item);
//                }
//
//                page++;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return finalList;
//    }



    // Store only keys
    private final Set<String> itemHistoryKeys = new HashSet<>();

    public void loadItemHistoryKeys(int productId, String ownerId) {

        try {
            String apiURL = "SavbillInventoryManagement/outwards/getItemHistoryByProduct"
                    + "?productId=" + productId
                    + "&ownerId=" + ownerId
                    + "&ownerType=staff";
            apiURL = getAPIURL(apiURL);

            int page = 1;
            int pageSize = 5000;

            while (true) {
                JSONObject payload = new JSONObject();
                payload.put("productId", productId);
                payload.put("ownerId", ownerId);
                payload.put("ownerType", "staff");

                JSONObject pagination = new JSONObject();
                pagination.put("page", page);
                pagination.put("pageSize", pageSize);
                payload.put("paginationRequestDTO", pagination);

                JSONObject response = httpPost(apiURL, payload.toString());
                int status = response.optInt("responseCode", 0);
                if (status != 200) break;

                JSONArray arr = response.optJSONArray("dataList");
                if (arr == null || arr.length() == 0) break;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);

                    // Build key: mac|serial
                    String mac = item.optString("macAddress", "").toLowerCase().trim();
                    String serial = item.optString("serialNumber", "").toLowerCase().trim();
                    String key = mac + "|" + serial;

                    // ✅ Store only the key
                    itemHistoryKeys.add(key);
                }

                page++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//==================================================================================================


    /**
     * Fetches all non-serialized inventory products.
     * Returns Map<productNameLower, newProductAmount>
     */
    public Map<String, String> getNonSerializedInventoryProducts() {
        Map<String, String> productMap = new ConcurrentHashMap<>();

        try {
            String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllProductForNonTrackableProductCategory");
            JSONObject response = httpGet(apiURL);

            int status = response.optInt("responseCode", 0);
            if (status != 200) return productMap;

            JSONArray dataList = response.optJSONArray("dataList");
            if (dataList == null) return productMap;

            for (int i = 0; i < dataList.length(); i++) {
                JSONObject obj = dataList.getJSONObject(i);
                String productName = obj.optString("name", "").toLowerCase().trim();
                String newProductAmount = obj.optString("newProductAmount", "");

                if (!productName.isEmpty()) {
                    productMap.putIfAbsent(productName, newProductAmount);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productMap;
    }


    //=================================================================================================================


    /**
     * Fetch all inventory items for a customer.
     * Returns JSONArray of inventory items.
     */
    public JSONArray getCustomerInventoryList(int custId) {
        JSONArray dataList = new JSONArray();
        try {
            String apiURL = getAPIURL("SavbillInventoryManagement/inwards/getAllCustomerInventoryList?custId=" + custId);
            JSONObject response = httpGet(apiURL);

            int status = response.optInt("responseCode", 0);
            if (status == 200) {
                JSONArray arr = response.optJSONArray("dataList");
                if (arr != null) {
                    dataList = arr;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }


    //===========================================================================================================



    /**
     * Fetch non-trackable product quantity from API.
     * Returns JSONObject of product details, or null if not found.
     */
    public JSONObject fetchNonTrackableProductQty(int productId, String ownerId) {
        JSONObject itemJson = null;

        try {
            String queryParam = "?productId=" + productId + "&ownerId=" + ownerId + "&ownerType=Staff";
            String apiURL = getAPIURL("SavbillInventoryManagement/outwards/getNonTrackableProductQty" + queryParam);

            JSONObject response = httpGet(apiURL);
            int status = response.optInt("responseCode", 0);

            if (status == 200) {
                JSONArray dataList = response.optJSONArray("dataList");
                if (dataList != null && dataList.length() > 0) {
                    JSONObject obj = dataList.getJSONObject(0);
                    int receivedProductId = obj.optInt("productId", 0);
                    if (receivedProductId == productId) {
                        itemJson = obj;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemJson;
    }

//============================================================================================================


    public Map<String, Integer> getProductIdAll() {

        Map<String, Integer> productIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllActiveProduct");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String productName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int productId = jsonArray.getJSONObject(i).getInt("id");
                productIdMap.put(productName, productId);
            }
        }
        return productIdMap;
    }

    public Map<String, Integer> getCASIdAll() {

        Map<String, Integer> casIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/casepackage/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String casName = jsonArray.getJSONObject(i).getString("casname").toLowerCase().trim();
                int casId = jsonArray.getJSONObject(i).getInt("id");
                casIdMap.put(casName, casId);
            }
        }
        return casIdMap;
    }

    public Map<String, Integer> getWarehouseIdAll() {

        Map<String, Integer> warehouseIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/warehouseManagement/getAllActiveWarehouse");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String warehouseName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int warehouseId = jsonArray.getJSONObject(i).getInt("id");
                warehouseIdMap.put(warehouseName, warehouseId);
            }
        }
        return warehouseIdMap;
    }

    public Map<String, String> getProductCategoryMACSerialTrackDetailsAll() {

        Map<String, String> productCategoryMACSerialTrackDetailsAll = new HashMap<String, String>();
        String apiURL = getAPIURL("SavbillInventoryManagement/product/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                String productName = jsonArray.getJSONObject(i).getString("name");
                //int productId = jsonArray.getJSONObject(i).getInt("id");

                JSONObject productCategory = jsonArray.getJSONObject(i).getJSONObject("productCategory");
                //int productCategoryId = productCategory.getInt("id");
                //String productCategoryType = productCategory.getString("type");

                boolean hasTrackable = productCategory.getBoolean("hasTrackable");
                boolean hasSerial = productCategory.getBoolean("hasSerial");
                boolean hasMac = productCategory.getBoolean("hasMac");

                ans = hasTrackable + ":" + hasSerial + ":" + hasMac;
                productCategoryMACSerialTrackDetailsAll.put(productName, ans);
            }
        }
        return productCategoryMACSerialTrackDetailsAll;
    }

    public Map<String, Integer> getDirectChargeIdAll() {

        Map<String, Integer> directChargeIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/product/getAllChargeType/CUSTOMER_DIRECT");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String directChargeName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int directChargeId = jsonArray.getJSONObject(i).getInt("id");
                directChargeIdMap.put(directChargeName, directChargeId);
            }
        }
        return directChargeIdMap;
    }

    public Map<String, Integer> getPartnerPlanGroupIdAll() {

        Map<String, Integer> planGroupIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("cpm/priceBook/active");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String planGroupName = jsonArray.getJSONObject(i).getString("bookname").toLowerCase().trim();
                int planGroupId = jsonArray.getJSONObject(i).getInt("id");
                planGroupIdMap.put(planGroupName, planGroupId);
            }
        }
        return planGroupIdMap;
    }


    public Map<String, Integer> getTeamIdAllListBasedOnAttchedStaff() {

        Map<String, Integer> teamIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/teams/getAllTeamBasedOnAttchedStaff");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String teamName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int teamId = jsonArray.getJSONObject(i).getInt("id");
                teamIdMap.put(teamName, teamId);
            }
        }
        return teamIdMap;
    }


    public Map<String, String> getProductCategoryIdAndTypeDetailsAll() {

        Map<String, String> productCategoryIdAndTypeMap = new HashMap<String, String>();
        String apiURL = getAPIURL("SavbillInventoryManagement/productCategory/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject productCategory = jsonArray.getJSONObject(i);
                String productCategoryName = productCategory.getString("name").toLowerCase().trim();
                int productCategoryId = productCategory.getInt("id");
                String productCategoryType = productCategory.getString("type");

                ans = productCategoryId + ":" + productCategoryType;
                productCategoryIdAndTypeMap.put(productCategoryName, ans);
            }
        }
        return productCategoryIdAndTypeMap;
    }


    public Map<String, Integer> getPopIdAll() {

        Map<String, Integer> popIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillInventoryManagement/popmanagement/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("responseCode");

        if (status == 200 || status == 0) {
            JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String popName = jsonArray.getJSONObject(i).getString("name").toLowerCase().trim();
                int popId = jsonArray.getJSONObject(i).getInt("id");
                popIdMap.put(popName, popId);
            }
        }
        return popIdMap;
    }


    public Map<Integer, String> getServiceParamIdsWithServiceIdAll() {

        Map<Integer, String> serviceParamIdMap = new HashMap<Integer, String>();
        String apiURL = getAPIURL("cpm/planservice/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("serviceList");
            for (int i = 0; i < jsonArray.length(); i++) {
                int serviceaId = jsonArray.getJSONObject(i).getInt("id");
                String serviceParamIds = "";
                JSONArray serviceParamJsonArray = jsonArray.getJSONObject(i).getJSONArray("serviceParamMappingList");
                for (int j = 0; j < serviceParamJsonArray.length(); j++) {
                    int serviceParamId = serviceParamJsonArray.getJSONObject(j).getInt("serviceParamId");
                    serviceParamIds += ":" + serviceParamId;
                }
                if (serviceParamIds.length() > 0) {
                    serviceParamIds = serviceParamIds.substring(1);
                }
                serviceParamIdMap.put(serviceaId, serviceParamIds);
            }
        }
        //System.out.println("map = " + serviceParamIdMap.toString());
        return serviceParamIdMap;
    }


    public Map<String, String> getPlanBundleDetailsAll() {

        Map<String, String> planGroupDetailsMap = new HashMap<String, String>();
        String apiURL = getAPIURL("cpm/planGroupMappings?mode=");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        String ans = null;
        if (status == 200) {

            JSONArray jsonArray = jsonResponse.getJSONArray("planGroupList");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject planGroup = jsonArray.getJSONObject(i);
                String planGroupName = planGroup.getString("planGroupName").toLowerCase().trim();
                int planGroupId = planGroup.getInt("planGroupId");
                float planGroupOfferprice = planGroup.getFloat("offerprice");

                ans = planGroupId + ":" + planGroupOfferprice;
                planGroupDetailsMap.put(planGroupName, ans);
            }
        }
        return planGroupDetailsMap;
    }

    // *************** FOR BELOW CACHING IS REMAINED ***********************************************

    public int getMunicipalityId(String municipalityName) {

        String apiURL = getAPIURL("SavbillCommonGateway/pincode/all");

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
            System.out.println("Municipality details not found - " + municipalityName);
            Utility.printLog(logFileName, logModuleName, "Municipality details not found - ", municipalityName);
        }
        return municipalityId;
    }

    public String getMasterDetailsFromMunicipalityId(int municipalityId) {

        String apiURL = getAPIURL("SavbillCommonGateway/pincode/all");
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

//	public Map<String, String> getAreaDetailsByPincodeIdAll(int pincodeid) {
//
//		Map<String, String> municipalityHierarchy = new HashMap<String, String>();
//
//		String apiURL = getAPIURL("SavbillCommonGateway/area/pincode?pincodeId="+pincodeid); // countyId = null , arealist = []
//
////        String apiURL = getAPIURL("SavbillCommonGateway/pincode/"+pincodeid);  //  arealist = [] not avi.
//		JSONObject jsonResponse = httpGet(apiURL);
//
//
//		int status = jsonResponse.getInt("responseCode");
//
//		String ans = null;
//		if (status == 200) {
//
//

    /// /            JSONObject JsonData = jsonResponse.getJSONObject("data");
    /// /
    /// /            String wardName = JsonData.getJSONObject(i).getString("name");
    /// /
    /// /                int countryId = JsonData.getJSONObject(i).getInt("countryId");
    /// /				int wardId = JsonData.getJSONObject(i).getInt("id");
    /// /				int stateId = JsonData.getJSONObject(i).getInt("stateId");
    /// /				int cityId = JsonData.getJSONObject(i).getInt("cityId");
//
//			JSONArray jsonArray = jsonResponse.getJSONArray("areaList");
//			for (int i = 0; i < jsonArray.length(); i++) {
//
//				String wardName = jsonArray.getJSONObject(i).getString("name");
//
//				int countryId = jsonArray.getJSONObject(i).getInt("countryId");
//				int wardId = jsonArray.getJSONObject(i).getInt("id");
//				int stateId = jsonArray.getJSONObject(i).getInt("stateId");
//				int cityId = jsonArray.getJSONObject(i).getInt("cityId");
//
//				ans = wardId + ":" + cityId + ":" + stateId + ":" + countryId;
//				municipalityHierarchy.put(wardName, ans);
//			}
//		}
//		return municipalityHierarchy;
//	}
    public Map<String, String> getAreaDetailsByAreaId(int areaId) {
        Map<String, String> municipalityHierarchy = new HashMap<>();

        String apiURL = getAPIURL("SavbillCommonGateway/area/" + areaId);
        JSONObject jsonResponse = httpGetcaf(apiURL);

        int status = jsonResponse.has("responseCode") ? jsonResponse.getInt("responseCode") : 0;

        if (status == 200 && jsonResponse.has("data")) {
            JSONObject data = jsonResponse.getJSONObject("data");

            int wardId = data.getInt("id");
            String wardName = data.getString("name");
            int countryId = data.getInt("countryId");
            int stateId = data.getInt("stateId");
            int cityId = data.getInt("cityId");

            String ans = wardId + ":" + cityId + ":" + stateId + ":" + countryId;
            municipalityHierarchy.put(wardName, ans);
        }

        return municipalityHierarchy;
    }

    public Map<String, String> getAreaDetailsByAreaId_CAF(int areaId) {
        Map<String, String> planDetailsMap = new HashMap<>();

        String apiURL = getAPIURL("cpm/plans/serviceArea?planmode=ALL&serviceAreaId=" + areaId);
        JSONObject jsonResponse = httpGet(apiURL);

        int status = jsonResponse.has("status") ? jsonResponse.getInt("status") : 0;

        if (status == 200 && jsonResponse.has("postpaidplanList")) {
            JSONObject data = jsonResponse.getJSONObject("postpaidplanList");

            int wardId = data.getInt("id");
            String serviceName = data.getString("serviceName");
            float offerPrice = (float) data.getFloat("offerprice");
            int validity = data.getInt("validity");
            String unitOfValidity = data.getString("unitsOfValidity");
            float newOfferPrice = data.getFloat("newOfferPrice");

            String ans = wardId + ":" + serviceName + ":" + offerPrice + ":" + validity + ":" + unitOfValidity + ":" + newOfferPrice;
            planDetailsMap.put(serviceName, ans);
        }

        return planDetailsMap;
    }


    // leassourceallid
    // get all area id
    public Map<String, Integer> getLeadSourceAll() {
        Map<String, Integer> sourceMasterIdMap = new HashMap<String, Integer>();
        String apiURL = getAPIURL("SavbillSalesCrmsBss/leadSource/all");
        JSONObject jsonResponse = httpGet(apiURL);
        int status = jsonResponse.getInt("status");

        if (status == 200) {
            JSONObject leadSourceList = jsonResponse.getJSONObject("leadSourceList");
            JSONArray contentArray = leadSourceList.getJSONArray("content");

            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject leadSource = contentArray.getJSONObject(i);
                String leadSourceName = leadSource.getString("leadSourceName").toLowerCase().trim();
                int lSNId = leadSource.getInt("id");
                sourceMasterIdMap.put(leadSourceName, lSNId);
            }
        }
        return sourceMasterIdMap;
    }
}
