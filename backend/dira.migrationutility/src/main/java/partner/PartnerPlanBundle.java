package partner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class PartnerPlanBundle extends RestExecution {

	private String logFileName = "Partner.log";
	private String logModuleName = "CreatePartnerPlanBundle";

	private void createPartnerPlanBundle(Map<String, String> service) {

		String apiURL = getAPIURL("cpm/priceBook/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPartnerPlanBundleJson(service);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("responseCode");
		String partnerPlanBundleName = service.get("Name");

		if (status == 200) {
			String message = "New Partner-PlanBundle is added successfully - " + partnerPlanBundleName;
			System.out.println(message);
			Utility.printLog("execution.log", logModuleName, "Success", message);

		} else if (status == 406) {
			String error = JSONResponseBody.getString("responseMessage") + " - " + partnerPlanBundleName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "Already Exist", error);
		} else {
			String error = JSONResponseBody.get("ERROR") + " - " + partnerPlanBundleName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "ERROR", error);
		}
	}

	public void createPartnerPlanBundle(List<Map<String, String>> serviceMapList) {

		for (int i = 0; i < serviceMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = serviceMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPartnerPlanBundle(map);
		}
	}

	public List<Map<String, String>> readPartnerPlanBundleList() {

		String sheetName = "PartnerPlanBundle";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPartnerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> serviceMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = cellValue.get("Name");
			if ((!"".equals(name)) && (name != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("Name", cellValue.get("Name"));
				valuemap.put("PlanGroup", cellValue.get("PlanGroup"));
				valuemap.put("PlanBundleType", cellValue.get("PlanBundleType"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("AGRPercentage", cellValue.get("AGRPercentage"));
				valuemap.put("TDSPercentage", cellValue.get("TDSPercentage"));
				valuemap.put("RevenueType", cellValue.get("RevenueType"));

				valuemap.put("CommissionOn", cellValue.get("CommissionOn"));
				valuemap.put("Description", cellValue.get("Description"));
				valuemap.put("[Service:RevenueSharePercentage:RoyaltyPercentage]",
						cellValue.get("[Service:RevenueSharePercentage:RoyaltyPercentage]"));
				valuemap.put("AllPlanSelect", cellValue.get("AllPlanSelect"));
				valuemap.put("PlanCategory", cellValue.get("PlanCategory"));
				valuemap.put("RevenueSharePercentage", cellValue.get("RevenueSharePercentage"));
				valuemap.put("[Plan_PlanGroup:RevenueSharePercentage]",
						cellValue.get("[Plan_PlanGroup:RevenueSharePercentage]"));

				valuemap.put("[SlabRangeFrom:SlabRangeUpto:SlabCommissionAmount]",
						cellValue.get("[SlabRangeFrom:SlabRangeUpto:SlabCommissionAmount]"));

				serviceMapList.add(valuemap);
			}
		}
		return serviceMapList;
	}

	private String getPartnerPlanBundleJson(Map<String, String> planBundleDetails) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject planBundleJson = new JSONObject();
			List<JSONObject> priceBookPlanDetailList = new ArrayList<JSONObject>();
			List<JSONObject> priceBookSlabDetailsList = new ArrayList<JSONObject>();
			List<JSONObject> serviceCommissionList = new ArrayList<JSONObject>();

			boolean isDeleted = false;

			float agrPercentage = Float.valueOf(planBundleDetails.get("AGRPercentage"));
			float tdsPercentage = Float.valueOf(planBundleDetails.get("TDSPercentage"));
			String revenueType = planBundleDetails.get("RevenueType");
			String commissionOn = planBundleDetails.get("CommissionOn");
			String status = ProductUtility.getStatus(planBundleDetails.get("Status"));

			planBundleJson.put("bookname", planBundleDetails.get("Name"));
			planBundleJson.put("status", status);

			planBundleJson.put("agrPercentage", agrPercentage);
			planBundleJson.put("tdsPercentage", tdsPercentage);
			
			planBundleJson.put("allSelection", JSONObject.NULL);
			planBundleJson.put("allSelection", JSONObject.NULL);
			
			planBundleJson.put("revenueType", revenueType);
			planBundleJson.put("commission_on", "Plan level");
			planBundleJson.put("description", planBundleDetails.get("Description"));

			planBundleJson.put("validFromString", "");
			planBundleJson.put("validToString", "");
			planBundleJson.put("validfrom", JSONObject.NULL);
			planBundleJson.put("validto", JSONObject.NULL);
			planBundleJson.put("partnerId", JSONObject.NULL);
			
			planBundleJson.put("isDeleted", isDeleted);
			planBundleJson.put("id", "");
			planBundleJson.put("updatedate", "");
			planBundleJson.put("lastModifiedById", "");
			planBundleJson.put("lastModifiedByName", "");
			planBundleJson.put("createdById", "");
			planBundleJson.put("createdByName", "");
			planBundleJson.put("createdate", "");
			planBundleJson.put("noPartnerAssociate", 0);

			// -- PartnerPlanBundle - Plan Details
			boolean isAllPlanSelected = Boolean.valueOf(planBundleDetails.get("AllPlanSelect"));
			String planCategory = planBundleDetails.get("PlanCategory");

			if (isAllPlanSelected) {
				planBundleJson.put("isAllPlanSelected", isAllPlanSelected);
				float revenueSharePercentage = Float.valueOf(planBundleDetails.get("RevenueSharePercentage"));
				planBundleJson.put("revenueSharePercentage", revenueSharePercentage);

				if (planCategory.equalsIgnoreCase("Individual")) {
					planBundleJson.put("planCategory", "individual");
				} else if (planCategory.equalsIgnoreCase("Plan Group")) {
					planBundleJson.put("planCategory", "groupPlan");
					//
					planBundleJson.put("isAllPlanGroupSelected",isAllPlanSelected );
				}

			} else {
				planBundleJson.put("isAllPlanSelected", isAllPlanSelected);
			//	String revenueSharePercentageNull = null;
				planBundleJson.put("revenueSharePercentage", JSONObject.NULL);
				

				String individualPlanMapping = planBundleDetails.get("[Plan_PlanGroup:RevenueSharePercentage]");

				individualPlanMapping = individualPlanMapping.replaceAll("[\\[\\]]", "");
				String ans[] = individualPlanMapping.split(",");

				for (int i = 0; i < ans.length; i++) {

					String PalnRevShareDetails[] = ans[i].split(":");
					String planPlanGroupName = PalnRevShareDetails[0].trim();
					float revenueShare = Float.valueOf(PalnRevShareDetails[1].trim());

					JSONObject planDetailsJson = new JSONObject();
					planDetailsJson.put("id", "");
					planDetailsJson.put("isDeleted", isDeleted);
					planDetailsJson.put("isTaxIncluded", true);
					planDetailsJson.put("partnerofficeprice", 0);
					planDetailsJson.put("registration", "No");
					planDetailsJson.put("renewal", "No");
					planDetailsJson.put("revsharen", "Yes");
					planDetailsJson.put("revenueSharePercentage", JSONObject.NULL);
					
					if (planCategory.equalsIgnoreCase("Individual")) {
						planBundleJson.put("planCategory", "individual");

						int planId = commonGetAPI.getPlanId(planPlanGroupName);

						String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");
						float offerPrice = Float.valueOf(planDetails[1]);

						JSONObject postpaidPlanId = new JSONObject();
						postpaidPlanId.put("id", planId);

						planDetailsJson.put("postpaidPlan", postpaidPlanId);
						planDetailsJson.put("offerprice", offerPrice);
						
						if (revenueType.equalsIgnoreCase("percentage") && commissionOn.equalsIgnoreCase("plan level")) {
							planDetailsJson.put("revenueSharePercentage", revenueShare);
						}

					} else if (planCategory.equalsIgnoreCase("Plan Group")) {
						planBundleJson.put("planCategory", "groupPlan");
						
						planBundleJson.put("isAllPlanGroupSelected",isAllPlanSelected );
						String planGroupDetails[] = commonGetAPI.getPlanBundleDetails(planPlanGroupName).split(":");
						int planGroupId = Integer.parseInt(planGroupDetails[0]);
						float offerPrice = Float.valueOf(planGroupDetails[1]);

						JSONObject planGroup = new JSONObject();
						planGroup.put("planGroupId", planGroupId);
                        
						planDetailsJson.put("planGroup", planGroup);
						planDetailsJson.put("offerprice", offerPrice);
						
						if (revenueType.equalsIgnoreCase("percentage") && commissionOn.equalsIgnoreCase("plan level")) {
							planDetailsJson.put("revenueSharePercentage", revenueShare);
						}
					}
					priceBookPlanDetailList.add(planDetailsJson);
					
				}
				
			}

			planBundleJson.put("priceBookPlanDetailList", priceBookPlanDetailList);

			// -- PartnerPlanBundle - Service Commission List
			
			if (revenueType.equalsIgnoreCase("percentage") && commissionOn.equalsIgnoreCase("Service Level")) {

				planBundleJson.put("commission_on", "Service level");
				String serviceLevelString = planBundleDetails.get("[Service:RevenueSharePercentage:RoyaltyPercentage]");

				serviceLevelString = serviceLevelString.replaceAll("[\\[\\]]", "");
				String ans[] = serviceLevelString.split(",");

				for (int i = 0; i < ans.length; i++) {

					String serviceLevelDetails[] = ans[i].split(":");
					String serviceName = serviceLevelDetails[0].trim();
					float revenue_share_percentage = Float.valueOf(serviceLevelDetails[1].trim());
					float royaltyPercentage = Float.valueOf(serviceLevelDetails[2].trim());

					int serviceId = commonGetAPI.getServiceIdList(serviceName).get(0);
					
					JSONObject serviceLevel = new JSONObject();
					serviceLevel.put("serviceId", serviceId);
					serviceLevel.put("revenue_share_percentage", revenue_share_percentage);
					serviceLevel.put("royaltyPercentage", royaltyPercentage);
					serviceLevel.put("id", "");

					serviceCommissionList.add(serviceLevel);
				}
			}

			planBundleJson.put("serviceCommissionList", serviceCommissionList);
			

			// -- PartnerPlanBundle - Slab Mapping
			if (revenueType.equalsIgnoreCase("slab") && commissionOn.equalsIgnoreCase("plan level")) {

				String slabString = planBundleDetails.get("[SlabRangeFrom:SlabRangeUpto:SlabCommissionAmount]");

				slabString = slabString.replaceAll("[\\[\\]]", "");
				String ans[] = slabString.split(",");

				for (int i = 0; i < ans.length; i++) {

					String slabDetails[] = ans[i].split(":");
					String fromRange = slabDetails[0].trim();
					String toRange = slabDetails[1].trim();
					float commissionAmount = Float.valueOf(slabDetails[2].trim());

					JSONObject slabGroup = new JSONObject();
					slabGroup.put("fromRange", fromRange);
					slabGroup.put("toRange", toRange);
					slabGroup.put("commissionAmount", commissionAmount);
					slabGroup.put("id", "");

					//priceBookSlabDetailsList.add(slabGroup);
				}
			}

			planBundleJson.put("priceBookSlabDetailsList", priceBookSlabDetailsList);

			jsonString = planBundleJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}
