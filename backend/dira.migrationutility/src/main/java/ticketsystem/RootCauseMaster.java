package ticketsystem;

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

public class RootCauseMaster extends RestExecution {

	private static String logFileName = "ticketdata.log";
	private static String logModuleName = "RootCause";
	
	private void createRootCause(Map<String, String> rootCauseMaster) {

		String apiURL = getAPIURL("TicketManagement/resolutionReasons/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getRootCauseMasterJson(rootCauseMaster);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String rootCauseName = rootCauseMaster.get("RootCauseName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, rootCauseName);
		
	}

	public void createRootCause(List<Map<String, String>> rootCauseMapList) {
		
		for (int i = 0; i < rootCauseMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = rootCauseMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createRootCause(map);
		}
	}

	
	public List<Map<String, String>> readRootCauseList() {
		
		String sheetName = "RootCauseMaster";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getTicketDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> rootCauseMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String rootCauseName = safeTrim(cellValue.get("RootCauseName"));
			if (!"".equals(rootCauseName)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("RootCauseName", safeTrim(cellValue.get("RootCauseName")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				valuemap.put("SubProblemDomain", safeTrim(cellValue.get("SubProblemDomain")));
				valuemap.put("Resolution", safeTrim(cellValue.get("Resolution")));
				rootCauseMapList.add(valuemap);
			}
		}
		return rootCauseMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
	
	
	@SuppressWarnings("unchecked")
    private String getRootCauseMasterJson(Map<String, String> rootCauseMaster) {

        String jsonString = null;

        try {

            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject rootCauseJson = new JSONObject();
            String status = ProductUtility.getStatus(rootCauseMaster.get("Status"));

            // 1. name
            rootCauseJson.put("name", rootCauseMaster.get("RootCauseName"));

            // 2. status

//            if (status != null && !status.trim().isEmpty()) {
//                // Normalize to first letter uppercase, rest lowercase
//                status = status.trim().toLowerCase();
//                status = status.substring(0, 1).toUpperCase() + status.substring(1);
//            } else {
//                status = "Active"; // Default or fallback if needed
//            }

            if (status != null && !status.trim().isEmpty()) {
                status = status.trim().toUpperCase(); // convert to all uppercase
            } else {
                status = "ACTIVE"; // fallback if null or empty
            }

            rootCauseJson.put("status", status);




            // 3. rootCauseResolutionMappingList
            List<JSONObject> rootCauseResolutionMappingList = new ArrayList<JSONObject>();
            String resolutions = rootCauseMaster.get("Resolution");
            String tempResolutions[] = resolutions.split(",");

            for (int i = 0; i < tempResolutions.length; i++) {
                JSONObject resolutionJson = new JSONObject();
                resolutionJson.put("rootCauseReason", tempResolutions[i]);
                resolutionJson.put("resolutionId", "");
                rootCauseResolutionMappingList.add(resolutionJson);
            }
            rootCauseJson.put("rootCauseResolutionMappingList", rootCauseResolutionMappingList);

            // 4. resoSubCategoryMappingList
            List<JSONObject> resoSubCategoryMappingList = new ArrayList<JSONObject>();
            String subProblemDomain = rootCauseMaster.get("SubProblemDomain");
            String[] tempSubProblemDomain = subProblemDomain.split(",");

            for (int i = 0; i < tempSubProblemDomain.length; i++) {
                int subProblemDomainId = commonGetAPI.getSubReasonCategoryId(tempSubProblemDomain[i].trim());
                JSONObject resolutionJson = new JSONObject();
                resolutionJson.put("subcateId", subProblemDomainId);
                resolutionJson.put("resId", "");
                resoSubCategoryMappingList.add(resolutionJson);
            }
            rootCauseJson.put("resoSubCategoryMappingList", resoSubCategoryMappingList);

            // 5. isDeleted
            rootCauseJson.put("isDeleted", false);

            jsonString = rootCauseJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("Generated JSON: " + jsonString); // check JSON here

        return jsonString;
    }


}
