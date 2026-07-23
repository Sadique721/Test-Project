package masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class InvestmentCode extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "InvestmentCode";

	private void createInvestmentCode(Map<String, String> investmentCode) {

		String apiURL = getAPIURL("SavbillCommonGateway/investmentCode/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getInvestmentCodeJson(investmentCode);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String investmentCodeName = investmentCode.get("InvestmentCodeName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, investmentCodeName);
		
	}

	public void createInvestmentCode(List<Map<String, String>> investmentCodeMapList) {
		
		for (int i = 0; i < investmentCodeMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = investmentCodeMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createInvestmentCode(map);
		}
	}

	public List<Map<String, String>> readInvestmentCodeList() {
		
		String sheetName = "InvestmentCode";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> investmentCodeMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String investmentCodeName = cellValue.get("InvestmentCodeName");
			if (!"".equals(investmentCodeName)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("InvestmentCodeName", cellValue.get("InvestmentCodeName"));
				valuemap.put("InvestmentCode", cellValue.get("InvestmentCode"));
				valuemap.put("Status", cellValue.get("Status"));
				investmentCodeMapList.add(valuemap);
			}
		}
		return investmentCodeMapList;
	}

	private String getInvestmentCodeJson(Map<String, String> investmentCode) {

		String jsonString = null;

		try {

			JSONObject investmentCodeJson = new JSONObject();
			String status = ProductUtility.getStatus(investmentCode.get("Status"));
			
			investmentCodeJson.put("icname", investmentCode.get("InvestmentCodeName"));
			investmentCodeJson.put("iccode", investmentCode.get("InvestmentCode"));
			investmentCodeJson.put("status", status);
			investmentCodeJson.put("id", "");

			jsonString = investmentCodeJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}
