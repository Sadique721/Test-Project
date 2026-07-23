package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.DBOperations;
import utility.Utility;

public class PlanTax extends RestExecution {

	private void createPlanTax(String taxName, String taxTierNameDetail) {

		String logFileName = "prepaidplan.log";
		String logModuleName = "CreateTax";

		String apiURL = getAPIURL("cpm/taxes");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPlanTaxJson(taxName, taxTierNameDetail);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		// Fetching the desired value of a parameter
		int status = JSONResponseBody.getInt("status");
		// System.out.println("status = " + result1);

		if (status == 200) {
			System.out.println("New Plan-Tax is added successfully - " + taxName);

			String type = "Plan-Tax";
			JSONObject cityJSONObject = JSONResponseBody.getJSONObject("tax");
			String name = cityJSONObject.getString("name");
			int id = cityJSONObject.getInt("id");

			DBOperations dbo = new DBOperations();
			dbo.setAPIData(type, name, id, status);

		} else if (status == 406) {
			String error = JSONResponseBody.getString("ERROR");
			System.out.println(error + " - " + taxName);
		}
	}

	public void createPlanTax(Map<String, String> map) {

		Set<String> keys = map.keySet();
		Iterator<String> keyIter = keys.iterator();

		while (keyIter.hasNext()) {
			String key = keyIter.next();
			String value = map.get(key);

			String ans[] = value.split(":");

			String taxName = ans[0];
			String taxTierNameDetail = "";

			int find = value.lastIndexOf(":");
			int len = value.length();

			if ((len - find) > 1) {
				taxTierNameDetail = ans[1];
			}

			createPlanTax(taxName, taxTierNameDetail);
		}
	}

	public Map<String, String> readUniquePlanTaxList() {

		String sheetName = "Tax";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		Map<String, String> valuemap = new HashMap<String, String>();

		String prevousTaxName = "";
		String ans = "";

		for (int i = 0; i < sheetMap.size(); i++) {

			cellValue = sheetMap.get(i);

			String taxName = cellValue.get("TaxName");
			String taxTierName = cellValue.get("TaxTierName");
			String taxTierRate = cellValue.get("TaxTierRate");
			String taxTierTaxGroup = cellValue.get("TaxTierTaxGroup");

			taxTierName = taxTierName + "#" + taxTierRate + "#" + taxTierTaxGroup;

			if (prevousTaxName.equals(taxName)) {
				ans = ans + "," + taxTierName;
				continue;
			} else {
				if (!prevousTaxName.equals(ans)) {
					valuemap.put(ans, ans);
				}
			}

			ans = taxName + ":" + taxTierName;
			prevousTaxName = taxName;
		}

		if (!"".equals(ans)) {
			valuemap.put(ans, ans);
		}
		return valuemap;
	}

	@SuppressWarnings("unchecked")
	private String getPlanTaxJson(String taxName, String taxTierNameDetail) {

		String jsonString = "";

		try {

			JSONObject planTaxJson = new JSONObject();

			planTaxJson.put("name", taxName);
			planTaxJson.put("taxtype", "TIER");
			planTaxJson.put("desc", "Migration");
			planTaxJson.put("status", "Y");
			planTaxJson.put("ledgerId", JSONObject.NULL);

			List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();

			String totalTaxTierNameDetail[] = taxTierNameDetail.split(",");

			for (int i = 0; i < totalTaxTierNameDetail.length; i++) {

				String taxTierDetails[] = totalTaxTierNameDetail[i].split("#");
				String taxTierName = taxTierDetails[0];
				float taxTierRate = Float.valueOf(taxTierDetails[1]);
				String taxTierTaxGroup = taxTierDetails[2];

				JSONObject taxDetailJsonObject = new JSONObject();

				taxDetailJsonObject.put("name", taxTierName);
				taxDetailJsonObject.put("rate", taxTierRate);
				taxDetailJsonObject.put("taxGroup", taxTierTaxGroup);
				taxDetailJsonObject.put("id", "");
				taxDetailJsonObject.put("beforeDiscount", false);
				jsonObjectList.add(taxDetailJsonObject);

			}

			planTaxJson.put("tieredList", jsonObjectList);
			jsonString = planTaxJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println(jsonString);
		return jsonString;
	}

}
