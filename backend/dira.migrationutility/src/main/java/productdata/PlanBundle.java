package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Utility;

public class PlanBundle extends RestExecution {

	private final String logFileName = "prepaidplan.log";
	private final String logModuleName = "CreatePlanBundle";

	private void createPlanBundle(Map<String, String> planBundle) {

		String apiURL = getAPIURL("cpm/addPlanGroup");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// --> Initializing payload or API body
		String apiBody = getPlanBundleJson(planBundle);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("status");
		String planBundleName = planBundle.get("PlanBundleName");

		if (status == 200) {
			String message = "New Plan-Bundle is added successfully - " + planBundleName;
			System.out.println(message);
			Utility.printLog("execution.log", logModuleName, "Success", message);

		} else if (status == 406) {
			String error = JSONResponseBody.getString("ERROR") + " - " + planBundleName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "Already Exist", error);
		} else {
			String error = JSONResponseBody.get("ERROR") + " - " + planBundleName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "ERROR", error);
		}
	}

	public void createPlanBundle(List<Map<String, String>> planBundleMapList) {

		for (int i = 0; i < planBundleMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = planBundleMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPlanBundle(map);
		}
	}

	public List<Map<String, String>> readPlanBundleList() {

		String sheetName = "PlanBundle";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> planBundleMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {
			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String planBundleName = cellValue.get("PlanBundleName");
			if ((!"".equals(planBundleName)) && (planBundleName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("PlanBundleName", planBundleName);
				valuemap.put("PlanType", cellValue.get("PlanType"));
				valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
				valuemap.put("PlanMode", cellValue.get("PlanMode"));
				valuemap.put("PlanGroup", cellValue.get("PlanGroup"));
				valuemap.put("PlanCategory", cellValue.get("PlanCategory"));
				valuemap.put("AllowDiscount", cellValue.get("AllowDiscount"));
				valuemap.put("ServicesAndPlans", cellValue.get("[Service:PlanName]"));
				valuemap.put("Charge", cellValue.get("Charge"));
				planBundleMapList.add(valuemap);
			}
		}
		return planBundleMapList;
	}

	@SuppressWarnings("unchecked")
	private String getPlanBundleJson(Map<String, String> planBundle) {

		String jsonString = null;

		try {

			JSONObject planBundleJsonObject = new JSONObject();

			CommonGetAPI commonGetAPI = new CommonGetAPI();

			planBundleJsonObject.put("planMode", planBundle.get("PlanMode").toUpperCase());
			planBundleJsonObject.put("mvnoId", "48"); // here we have to delete from or add dynamic.
			planBundleJsonObject.put("planGroupId",JSONObject.NULL);
			planBundleJsonObject.put("planGroupName", planBundle.get("PlanBundleName"));

			List<Integer> serviceAreaId = commonGetAPI.getServiceAreaIdList(planBundle.get("ServiceArea"));
			if (serviceAreaId.size() > 0) {
				planBundleJsonObject.put("serviceAreaId", serviceAreaId);
			}

			planBundleJsonObject.put("status", "Active");
			planBundleJsonObject.put("planType", planBundle.get("PlanType"));
			planBundleJsonObject.put("planGroupType", planBundle.get("PlanGroup"));
			planBundleJsonObject.put("category", planBundle.get("PlanCategory"));
			planBundleJsonObject.put("allowdiscount", Boolean.valueOf(planBundle.get("AllowDiscount")));
			// planBundleJsonObject.put("productPlanGroupMappingList",
			// planMappingJsonObject);

			// --> Plan Details

			List<JSONObject> planDetailsList = new ArrayList<JSONObject>();

			String servicesAndPlans = planBundle.get("ServicesAndPlans");

			// String servicesAndPlans =
			// "[FTTH:Plan_4],[DTV:Plan_5],[FTTH:Plan_6],[DTV:Plan_7]";
			servicesAndPlans = servicesAndPlans.replaceAll("[\\[\\]]", "");
			String ans[] = servicesAndPlans.split(",");

			float totalOfferPrice = 0.0f;

			for (int i = 0; i < ans.length; i++) {

				String temp[] = ans[i].split(":");
				String service = temp[0];
				String planName = temp[1];

				int planId1 = commonGetAPI.getPlanId(planName);
				int serviceId = commonGetAPI.getServiceId(service);

				List<JSONObject> planChargeDetails = getPlanChargeDetail(planId1);
				// String list = planChargeDetails.toString();

				int planId = commonGetAPI.getPlanId(planName);
				String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

				String serviceName = planDetails[0];
				if (service.equalsIgnoreCase(serviceName)) {

					float offerPrice = Float.valueOf(planDetails[1]);
					int validity = Integer.parseInt(planDetails[2]);
					String unitsOfValidity = planDetails[3];
					float newOfferPrice = Float.valueOf(planDetails[4]);

					totalOfferPrice = totalOfferPrice + offerPrice;
					JSONObject planDetailsJsonObject = new JSONObject();

					planDetailsJsonObject.put("service", serviceId);
					planDetailsJsonObject.put("planId", planId);
					planDetailsJsonObject.put("validity", validity);
					planDetailsJsonObject.put("amount", offerPrice);
					planDetailsJsonObject.put("validityUnit", unitsOfValidity);
					planDetailsJsonObject.put("planGroupId", "");
					planDetailsJsonObject.put("planGroupMappingId", "");
					planDetailsJsonObject.put("chargeList", planChargeDetails);

					planDetailsJsonObject.put("newOfferPrice", newOfferPrice);

					planDetailsList.add(planDetailsJsonObject);
				}
			}
			// creating product plan group mapping list
			List<JSONObject> productPlanGroupMappingList = new ArrayList<JSONObject>();
			// put in json
			planBundleJsonObject.put("productPlanGroupMappingList", productPlanGroupMappingList);

			planBundleJsonObject.put("offerprice", totalOfferPrice);
			planBundleJsonObject.put("invoiceToOrg", false);
			planBundleJsonObject.put("requiredApproval", false);

			planBundleJsonObject.put("planMappingList", planDetailsList);

			jsonString = planBundleJsonObject.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	// add charge detail for plan bunddle.

	private List<JSONObject> getPlanChargeDetail(int planId) {

		String apiURL = "cpm/postpaidplan/" + planId;
		apiURL = getAPIURL(apiURL);

		JSONObject JSONResponseBody = httpGet(apiURL);
		int status = JSONResponseBody.getInt("status");
		// JSONArray chargeList = null;
		List<JSONObject> chargeList1 = new ArrayList<JSONObject>();
		// String chargeList1 = new ArrayList<String>();
		// String list = null;

		if (status == 200) {
			JSONObject jsonObject = JSONResponseBody.getJSONObject("postPaidPlan");
			// remove from json object
			JSONObject js = jsonObject.getJSONArray("chargeList").getJSONObject(0);
			js.remove("billingCycle");
			js.remove("chargeId");
			js.remove("createdate");
			js.remove("planId");

			String chargeName = js.getJSONObject("charge").getString("name");
			js.put("chargeName", chargeName);

			chargeList1.add(js);
			// list = chargeList.toString();
			// System.out.println(js.toString());
			// chargeList1.add(chargeList);

		}

		if (chargeList1.size() == 0) {
			System.out.println("plan detail not found - " + planId);
			Utility.printLog(logFileName, logModuleName, "plan not found - ", String.valueOf(planId));
		}

		return chargeList1;
	}

}
