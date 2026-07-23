package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class PlanTaxNew extends RestExecution {

	private String logFileName = "prepaidplan.log";
	private String logModuleName = "Tax";

	private void createPlanTax(Map<String, String> tax) {

		String apiURL = getAPIURL("cpm/taxes");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPlanTaxJson(tax);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String taxName = tax.get("TaxName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, taxName);
		
	}

	public void createPlanTax(List<Map<String, String>> planTaxMapList) {

		for (int i = 0; i < planTaxMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = planTaxMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPlanTax(map);
		}
	}

	public List<Map<String, String>> readPlanTaxList() {

		String sheetName = "Tax";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> planTaxMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {
			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String taxName = cellValue.get("TaxName");
			if ((!"".equals(taxName)) && (taxName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("TaxName", cellValue.get("TaxName"));
				valuemap.put("TaxType", cellValue.get("TaxType"));
				valuemap.put("TaxStatus", cellValue.get("TaxStatus"));
				valuemap.put("TaxDescription", cellValue.get("TaxDescription"));

				valuemap.put("[Name:LedgerID:Rate:Group:BeforeTax]", cellValue.get("[Name:LedgerID:Rate:Group:BeforeTax]"));
				planTaxMapList.add(valuemap);
			}
		}
		return planTaxMapList;
	}

	private String getPlanTaxJson(Map<String, String> tax) {

		String jsonString = null;

		try {

			JSONObject planTaxJson = new JSONObject();

			planTaxJson.put("name", tax.get("TaxName"));
			planTaxJson.put("taxtype", tax.get("TaxType"));

			String status = tax.get("TaxStatus");
			if (!"".equals(status)) {
				if (status.equalsIgnoreCase("active")) {
					status = "Y";
				} else if (status.equalsIgnoreCase("inactive")) {
					status = "N";
				}
			}
			planTaxJson.put("status", status);

			
			
			planTaxJson.put("desc", tax.get("TaxDescription"));

			// --Tax Tier List Details
			List<JSONObject> taxTierDetailsList = new ArrayList<JSONObject>();

			String taxNameRateGroupStatus = tax.get("[Name:LedgerID:Rate:Group:BeforeTax]");

			taxNameRateGroupStatus = taxNameRateGroupStatus.replaceAll("[\\[\\]]", "");
			String[] ans = taxNameRateGroupStatus.split(",");

			for (int i = 0; i < ans.length; i++) {

				String[] taxTierDetails = ans[i].split(":");
				String taxTierName = taxTierDetails[0];
				String ledgerId = taxTierDetails[1];
				float taxTierRate = Float.valueOf(taxTierDetails[2]);
				String taxTierTaxGroup = taxTierDetails[3];
				boolean taxTierTaxStatus = Boolean.valueOf(taxTierDetails[4]);

				JSONObject taxTierJson = new JSONObject();

				taxTierJson.put("name", taxTierName);
				taxTierJson.put("rate", taxTierRate);
				taxTierJson.put("taxGroup", taxTierTaxGroup);
				taxTierJson.put("id", "");
				taxTierJson.put("beforeDiscount", taxTierTaxStatus);
				
				planTaxJson.put("ledgerId", JSONObject.NULL);
				if (!"".equals(ledgerId)) {
					taxTierJson.put("ledgerId", ledgerId);
				}
				
				
				taxTierDetailsList.add(taxTierJson);
			}

			planTaxJson.put("tieredList", taxTierDetailsList);
			jsonString = planTaxJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//System.out.println(jsonString);
		return jsonString;
	}

}
