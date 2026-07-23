package ticketsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class TAT extends RestExecution {
	
	private static String logFileName = "ticketdata.log";
	private static String logModuleName = "TAT";

	private void createTAT(Map<String, String> TATDetails) {
		

		String apiURL = getAPIURL("TicketManagement/tickettatmatrix/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String APIBody = getTATJson(TATDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);
		
		String tatName = TATDetails.get("TicketName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, tatName);
		
	}

	
	public void createTAT(List<Map<String, String>> tatMapList) {
		
		for (int i = 0; i < tatMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = tatMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createTAT(map);
		}
	}

	
	public List<Map<String, String>> readTATList() {
		
		String sheetName = "TAT";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getTicketDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> tatMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String ticketName = safeTrim(cellValue.get("TicketName"));
			if (!"".equals(ticketName)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("TicketName", safeTrim(cellValue.get("TicketName")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				valuemap.put("ResponseTime", safeTrim(cellValue.get("ResponseTime")));
				valuemap.put("ResponseTimeUnit", safeTrim(cellValue.get("ResponseTimeUnit")));
				
				valuemap.put("SLATimeP1", safeTrim(cellValue.get("SLATimeP1")));
				valuemap.put("SLATimeP1Unit", safeTrim(cellValue.get("SLATimeP1Unit")));
				valuemap.put("SLATimeP2", safeTrim(cellValue.get("SLATimeP2")));
				valuemap.put("SLATimeP2Unit", safeTrim(cellValue.get("SLATimeP2Unit")));
				valuemap.put("SLATimeP3", safeTrim(cellValue.get("SLATimeP3")));
				valuemap.put("SLATimeP3Unit", safeTrim(cellValue.get("SLATimeP3Unit")));
				
				valuemap.put("[TATP1:TATP2:TATP3:Unit:Action]", safeTrim(cellValue.get("[TATP1:TATP2:TATP3:Unit:Action]")));
				
				tatMapList.add(valuemap);
			}
		}
		return tatMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

	private String getTATJson(Map<String, String> tatDetails) {

		String jsonString = null;

		try {

			JSONObject tatJson = new JSONObject();
			String status = ProductUtility.getStatus(tatDetails.get("Status"));
			
			tatJson.put("name", tatDetails.get("TicketName"));	
			tatJson.put("status", status );	
			
			int slaTimeP1 = Integer.valueOf(tatDetails.get("SLATimeP1"));
			String slaTimeP1Unit = ProductUtility.getTimeUnit(tatDetails.get("SLATimeP1Unit"));
			tatJson.put("slaTimep1", slaTimeP1);	
			tatJson.put("sunitp1", slaTimeP1Unit);
			
			int slaTimeP2 = Integer.valueOf(tatDetails.get("SLATimeP2"));
			String slaTimeP2Unit = ProductUtility.getTimeUnit(tatDetails.get("SLATimeP2Unit"));
			tatJson.put("slaTimep2", slaTimeP2);	
			tatJson.put("sunitp2", slaTimeP2Unit);
			
			int slaTimeP3 = Integer.valueOf(tatDetails.get("SLATimeP3"));
			String slaTimeP3Unit = ProductUtility.getTimeUnit(tatDetails.get("SLATimeP3Unit"));
			tatJson.put("slaTime3", slaTimeP3);	
			tatJson.put("sunitp3", slaTimeP3Unit);
			
			int responseTime = Integer.valueOf(tatDetails.get("ResponseTime"));
			String responseUnit = ProductUtility.getTimeUnit(tatDetails.get("ResponseTimeUnit"));
			tatJson.put("rtime", responseTime);
			tatJson.put("runit", responseUnit);
			
			
			
			// --TAT MatrixMappings List Details
			List<JSONObject> tatMatrixMappingsList = new ArrayList<JSONObject>();

			String tatTATP1TATP2TATP3UnitAction = tatDetails.get("[TATP1:TATP2:TATP3:Unit:Action]");

			tatTATP1TATP2TATP3UnitAction = tatTATP1TATP2TATP3UnitAction.replaceAll("[\\[\\]]", "");
			String ans[] = tatTATP1TATP2TATP3UnitAction.split(",");

			for (int i = 0; i < ans.length; i++) {

				String tatMappingDetails[] = ans[i].split(":");
				int mtime1 = Integer.valueOf(tatMappingDetails[0]);
				int mtime2 = Integer.valueOf(tatMappingDetails[1]);
				int mtime3 = Integer.valueOf(tatMappingDetails[2]);
				String munit =ProductUtility.getTimeUnit( tatMappingDetails[3]);
				String action = tatMappingDetails[4];
				
				int orderNo = i+1;
				String level = "Level " + (i+1);
				
				JSONObject tatMatrixMappingsJSON = new JSONObject();

				tatMatrixMappingsJSON.put("orderNo", orderNo);
				tatMatrixMappingsJSON.put("level", level);
				tatMatrixMappingsJSON.put("mtime1", mtime1);
				tatMatrixMappingsJSON.put("mtime2", mtime2);
				tatMatrixMappingsJSON.put("mtime3", mtime3);
				tatMatrixMappingsJSON.put("munit", munit);
				tatMatrixMappingsJSON.put("action", action);
				tatMatrixMappingsJSON.put("tatMappingtId", JSONObject.NULL);
				tatMatrixMappingsJSON.put("id", JSONObject.NULL);

				tatMatrixMappingsList.add(tatMatrixMappingsJSON);
			}

			tatJson.put("tatMatrixMappings", tatMatrixMappingsList);

			jsonString = tatJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}


