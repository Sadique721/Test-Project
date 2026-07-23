package ticketsystem;

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

public class ProblemDomain extends RestExecution {
	
	private static String logFileName = "ticketdata.log";
	private static String logModuleName = "ProblemDomain";

	private void createProblemDomain(Map<String, String> problemDomainDetails) {
		

		String apiURL = getAPIURL("TicketManagement/ticketReasonCategory/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getProblemDomainJson(problemDomainDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);
		
		String problemDomainName = problemDomainDetails.get("TicketProblemDomainName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, problemDomainName);
		
	}

	
	public void createProblemDomain(List<Map<String, String>> problemDomainMapList) {
		
		for (int i = 0; i < problemDomainMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = problemDomainMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createProblemDomain(map);
		}
	}

	
	public List<Map<String, String>> readProblemDomainList() {
		
		String sheetName = "ProblemDomain";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getTicketDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> problemDomainMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String problemDomainName = safeTrim(cellValue.get("TicketProblemDomainName"));
			if (!"".equals(problemDomainName)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("TicketProblemDomainName", safeTrim(cellValue.get("TicketProblemDomainName")));
				valuemap.put("Service", safeTrim(cellValue.get("Service")));
				valuemap.put("Department", safeTrim(cellValue.get("Department")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				problemDomainMapList.add(valuemap);
			}
		}
		return problemDomainMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


	private String getProblemDomainJson(Map<String, String> problemDomain) {

		String jsonString = null;

		try {

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject problemDomainJson = new JSONObject();
			JSONObject serviceJson = new JSONObject();
            JSONArray tatMappingList = new JSONArray();
			String status = ProductUtility.getStatus(problemDomain.get("Status"));
			
			problemDomainJson.put("categoryName", problemDomain.get("TicketProblemDomainName"));
			
			String serviceName = problemDomain.get("Service");
			int serviceId = commonGetAPI.getServiceIdList(serviceName).get(0);
			serviceJson.put("id", serviceId);
			problemDomainJson.put("service", serviceJson );	
			
			problemDomainJson.put("department", problemDomain.get("Department"));	
			problemDomainJson.put("status", status );	
			problemDomainJson.put("slaTimeP1", 1);	
			problemDomainJson.put("slaTimeP2", 1);	
			problemDomainJson.put("slaTimeP3", 1);	
			problemDomainJson.put("slaUnitP1", "Day");
			problemDomainJson.put("slaUnitP2", "Day");
			problemDomainJson.put("slaUnitP3", "Day");


            // ticketReasonCategoryTATMappingList
            JSONObject tatMapping = new JSONObject();
            tatMapping.put("orderNumber", 1);
            tatMapping.put("teamId", JSONObject.NULL);
            tatMapping.put("time", 1);
            tatMapping.put("timeUnit", "Day");
            tatMapping.put("action", "Notification");
            tatMapping.put("mappingId", "");
            tatMapping.put("escalatedTime", 1);
            tatMapping.put("mediumTime", 1);
            tatMapping.put("level", "Level 1");

            tatMappingList.put(tatMapping);
            problemDomainJson.put("ticketReasonCategoryTATMappingList", tatMappingList);
			
			jsonString = problemDomainJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}


