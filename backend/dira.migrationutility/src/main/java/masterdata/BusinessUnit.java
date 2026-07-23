package masterdata;

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

public class BusinessUnit extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "BusinessUnit";
	
	private void createBusinessUnit(Map<String, String> businessUnit){
		
		String apiURL=getAPIURL("SavbillCommonGateway/businessUnit/save");
		Utility.printLog(logFileName,logModuleName , "Request URL", apiURL);
		
		String APIBody = getBusinessUnitJson(businessUnit);
		Utility.printLog(logFileName,logModuleName , "Request Body", APIBody);
		
		JSONObject JSONResponseBody = httpPost(apiURL,APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName,logModuleName , "Response", response);
		
		String buName = businessUnit.get("BusinessUnitName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, buName);
		
	}
	
	public void createBusinessUnit(List<Map<String, String>> businessUnitMapList) {
		
		for (int i = 0; i < businessUnitMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = businessUnitMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createBusinessUnit(map);
		}
	}

	public List<Map<String, String>> readBusinessUnitList() {

		String sheetName = "BusinessUnit";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> businessUnitMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String businessUnit = cellValue.get("BusinessUnitName");
			if ((!"".equals(businessUnit)) && (businessUnit != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("BusinessUnitName", cellValue.get("BusinessUnitName"));
				valuemap.put("BusinessUnitCode", cellValue.get("BusinessUnitCode"));
				valuemap.put("ICCodeName", cellValue.get("ICCodeName"));
				valuemap.put("Status", cellValue.get("Status"));
				businessUnitMapList.add(valuemap);
			}
		}
		return businessUnitMapList;
	}

	private String getBusinessUnitJson(Map<String, String> businessUnit) {
		
		String jsonString = null;
		
		try {
			
			JSONObject districtJsonObject = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			String status = ProductUtility.getStatus(businessUnit.get("Status"));
			
			districtJsonObject.put("buname", businessUnit.get("BusinessUnitName"));
			districtJsonObject.put("bucode", businessUnit.get("BusinessUnitCode"));
			
			String investmentCodeNames = businessUnit.get("ICCodeName");
			List<Integer> investmentCodeIdList = commonGetAPI.getInvestmentCodeIdList(investmentCodeNames);
			
			districtJsonObject.put("investmentCodeid", investmentCodeIdList);
			districtJsonObject.put("planBindingType", "Predefined");
			districtJsonObject.put("status", status);
			
			jsonString = districtJsonObject.toString();
			
		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		
		return jsonString;
	}
	

}
