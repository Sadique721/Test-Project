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
import utility.ProductUtility;
import utility.Utility;

public class PlanService extends RestExecution {

	private String logFileName = "prepaidplan.log";
	private String logModuleName = "Service";

	private void createPlanService(Map<String, String> service) {

		String apiURL = getAPIURL("cpm/planservice");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPlanServiceJson(service);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String serviceName = service.get("ServiceName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, serviceName);
		
	}

	public void createPlanService(List<Map<String, String>> serviceMapList) {

		for (int i = 0; i < serviceMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = serviceMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPlanService(map);
		}
	}

	public List<Map<String, String>> readPlanServiceList() {

		String sheetName = "Service";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> serviceMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String serviceName = cellValue.get("ServiceName");
			if ((!"".equals(serviceName)) && (serviceName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("ServiceName", cellValue.get("ServiceName"));
				valuemap.put("ICName", cellValue.get("ICName"));
				valuemap.put("ICCode", cellValue.get("ICCode"));
				valuemap.put("LedgerId", cellValue.get("LedgerId"));
				valuemap.put("IsDTVService", cellValue.get("IsDTVService"));
				valuemap.put("Expiry", cellValue.get("Expiry"));
				
				valuemap.put("QuotaConfigurationRequire", cellValue.get("QuotaConfigurationRequire"));
				
				valuemap.put("Feasibility", cellValue.get("Feasibility"));
				valuemap.put("Installation", cellValue.get("Installation"));
				valuemap.put("POC", cellValue.get("POC"));
				valuemap.put("AllowPriceOverride", cellValue.get("AllowPriceOverride"));
				valuemap.put("Provisioning", cellValue.get("Provisioning"));
				
								

				
				serviceMapList.add(valuemap);
			}
		}
		return serviceMapList;
	}

	private String getPlanServiceJson(Map<String, String> service) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject serviceJson = new JSONObject();

			serviceJson.put("name", service.get("ServiceName"));
            serviceJson.put("displayName", "");
            serviceJson.put("isServiceThroughLead", "");
            serviceJson.put("pcategoryId", JSONObject.NULL);

			serviceJson.put("icname", "");
			serviceJson.put("iccode", "");
			serviceJson.put("investmentid", "");

			String investmentCodeName = service.get("ICName");

			if (!"".equals(investmentCodeName)) {
				int investmentCodeId = commonGetAPI.getInvestmentCodeIdList(investmentCodeName).get(0);
				serviceJson.put("icname", investmentCodeName);
				serviceJson.put("iccode", service.get("ICCode"));
				serviceJson.put("investmentid", investmentCodeId);
			}
		
			serviceJson.put("ledgerId", service.get("LedgerId"));
			
			boolean isDTVService = Boolean.valueOf(service.get("IsDTVService"));
			String expiry = service.get("Expiry");
			serviceJson.put("is_dtv", false);
			
			if(isDTVService) {
				expiry = "at_midnight";
				serviceJson.put("is_dtv", true);
			} else {
				if (!"".equals(expiry)) {
					if (expiry.equalsIgnoreCase("Actual time")) {
						expiry = "actual_time";
					} else if (expiry.equalsIgnoreCase("Midnight")) {
						expiry = "at_midnight";
					}
				}
			}
			
			serviceJson.put("expiry", expiry);

		/*	String productCategory = service.get("RequiredInventory");
			List<Integer> productCategoryList = null;

			if (!"".equals(productCategory)) {
				productCategoryList = commonGetAPI.getProductCategoryIdListForCustomerBind(productCategory);
			}

			serviceJson.put("pcategoryId", productCategoryList);
		*/	//serviceJson.put("isQoSV", Boolean.valueOf(service.get("QuotaConfigurationRequire")));
			
			List<JSONObject> serviceParamMappingList = new ArrayList<JSONObject>();
			JSONObject serviceParamMappingJson = new JSONObject();
			boolean bandwidth = Boolean.valueOf(service.get("QuotaConfigurationRequire"));
			if(bandwidth) {
				
				serviceParamMappingJson.put("serviceParamId", 1);
				serviceParamMappingJson.put("isMandatory", true);
                serviceParamMappingJson.put("serviceParamName", "Bandwidth");
				serviceParamMappingJson.put("value", "");
				serviceParamMappingList.add(serviceParamMappingJson);
			}
			
			serviceJson.put("serviceParamMappingList", serviceParamMappingList);
			
			serviceJson.put("feasibility", Boolean.valueOf(service.get("Feasibility")));
			serviceJson.put("installation", Boolean.valueOf(service.get("Installation")));
			serviceJson.put("poc", Boolean.valueOf(service.get("POC")));
			serviceJson.put("isPriceEditable", Boolean.valueOf(service.get("AllowPriceOverride")));
			serviceJson.put("provisioning", Boolean.valueOf(service.get("Provisioning")));

			
			jsonString = serviceJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//        System.out.println(jsonString);
		return jsonString;
	}

	public List<Integer> getProductCategoryIdListOLD(String productCategory) {

		String apiURL = getAPIURL("productCategory/getAllProductCategoriesByType?Type=customerbind");

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		List<Integer> productCategoryList = new ArrayList<Integer>();

		if (status == 200) {

			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			if (productCategory.equalsIgnoreCase("All")) {
				for (int i = 0; i < jsonArray.length(); i++) {
					productCategoryList.add(jsonArray.getJSONObject(i).getInt("id"));
				}
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					String receivedProductCategoryName = jsonArray.getJSONObject(i).getString("name");
					String productCategoryNameList[] = productCategory.split(",");
					for (int j = 0; j < productCategoryNameList.length; j++) {
						if (receivedProductCategoryName.equalsIgnoreCase(productCategoryNameList[j])) {
							productCategoryList.add(jsonArray.getJSONObject(i).getInt("id"));
							break;
						}
					}
				}
			}

		}

		if (productCategoryList.size() == 0) {
			System.out.println("Product-Category details not found - " + productCategory);
			Utility.printLog(logFileName, logModuleName, "Product-Category details not found - ", productCategory);
		}
		return productCategoryList;
	}

}
