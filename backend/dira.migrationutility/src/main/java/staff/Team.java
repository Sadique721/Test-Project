package staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class  Team extends RestExecution {
	
	private static String logFileName = "Staff.log";
	private static String logModuleName = "Team";

	private void createTeam(Map<String, String> teamDetails) {

		String apiURL = getAPIURL("SavbillCommonGateway/teams/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getTeamJson(teamDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String teamName = teamDetails.get("TeamName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, teamName);
	
	}

	public void createTeam(List<Map<String, String>> teamsMapList) {
		
		for (int i = 0; i < teamsMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = teamsMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createTeam(map);
		}
	}

	public List<Map<String, String>> readTeamList() {
		
		String sheetName = "Teams";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> teamsMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String teamName = safeTrim(cellValue.get("TeamName"));
			if ((!"".equals(teamName)) && (teamName != null)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("TeamName", safeTrim(cellValue.get("TeamName")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				teamsMapList.add(valuemap);
			}
		}
		return teamsMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

	private String getTeamJson(Map<String, String> teamDetails) {

		String jsonString = null;

		try {

			JSONObject teamJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(teamDetails.get("Status"));
			
			teamJsonObject.put("name", teamDetails.get("TeamName"));
			teamJsonObject.put("status", status.toLowerCase());
			teamJsonObject.put("teamType", teamJsonObject.NULL);
			teamJsonObject.put("product", "BSS");
			
			jsonString = teamJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}


}


