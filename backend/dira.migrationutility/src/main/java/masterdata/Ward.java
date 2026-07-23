package masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class Ward extends RestExecution {

	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Ward";
	
	private void createWard(Map<String, String> ward) {
		
		String apiURL = getAPIURL("SavbillCommonGateway/area/save");
		Utility.printLog(logFileName,logModuleName , "Request URL", apiURL);
		
		String apiBody = getWardJson(ward);
		Utility.printLog(logFileName,logModuleName , "Request Body", apiBody);
		
		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName,logModuleName , "Response", response);
		
		String wardName = ward.get("WardName");
		String municipalityName = ward.get("Municipalties");
		wardName = wardName + " (" + municipalityName + ")";
		ProductUtility.printResponse(JSONResponseBody, logModuleName, wardName);
	}
	
/*	
	public void createWard(List<Map<String, String>> wardMapList) {

	    // Thread pool setup to handle parallel tasks
	    int numThreads = 16;  // Adjust the number of threads based on system capabilities
	    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

	    List<Callable<Void>> tasks = new ArrayList<>();
	    
	    for (Map<String, String> ward : wardMapList) {
	        final Map<String, String> currentWard = new HashMap<>(ward); // Capture a new copy of the current ward map
	        tasks.add(() -> {
	            int totalWard = Integer.parseInt(currentWard.get("TotalWard"));
	            for (int j = 1; j <= totalWard; j++) {
	                currentWard.put("WardName", String.valueOf(j));
	                Utility.printLog(logFileName, logModuleName, "Sheet Data", currentWard.toString());
	                // Process the current ward (if createWard is meant to process one ward at a time)
	                createWard(currentWard);  // Replace with the appropriate function to process each ward
	            }
	            return null; // Void return as we're not returning anything from this task
	        });
	    }

	    try {
	        // Execute all tasks in parallel
	        executorService.invokeAll(tasks);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
	    } finally {
	        executorService.shutdown();
	    }
	}
	*/
	
	public void createWard(List<Map<String, String>> wardMapList) {
		
		for (int i = 0; i < wardMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = wardMapList.get(i);
			
			int totalWard = Integer.parseInt(map.get("TotalWard"));			
			for(int j=1;j<=totalWard;j++) {
				map.put("WardName", String.valueOf(j));
				Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
				createWard(map);
			}
		}
	}

	public List<Map<String, String>> readWardList() {
		
		String sheetName = "Ward";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> wardMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String ward = cellValue.get("TotalWard");
			if ((!"".equals(ward)) && (ward != null)) {
				
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("TotalWard", cellValue.get("TotalWard"));
				valuemap.put("Municipalties", cellValue.get("Municipalties"));
				valuemap.put("Status", cellValue.get("Status"));
				wardMapList.add(valuemap);
			}
		}
		return wardMapList;
	}

	private String getWardJson(Map<String, String> ward) {

		String jsonString = null;

		try {

			JSONObject wardJsonObject = new JSONObject();
			JSONObject pincodeJsonObject = new JSONObject();
			
			String status = ProductUtility.getStatus(ward.get("Status"));
			String wardName = ward.get("WardName");
			
			String municipalityName = ward.get("Municipalties");
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			String details = commonGetAPI.getMasterDetailsByMunicipalityName(municipalityName);
			
			if(details != null) {
			
				String[] data = details.split(":");
				int pincodeId = Integer.parseInt(data[0]);
				int cityId = Integer.parseInt(data[1]);
				int stateId = Integer.parseInt(data[2]);			
				int countryId = Integer.parseInt(data[3]);
				
				wardJsonObject.put("name", wardName);
				wardJsonObject.put("pincodeId", pincodeId);
				wardJsonObject.put("cityId", cityId);
				wardJsonObject.put("stateId", stateId);
				wardJsonObject.put("countryId", countryId);
				wardJsonObject.put("status", status);
			
				pincodeJsonObject.put("pincodeid", pincodeId);
				pincodeJsonObject.put("pincode", municipalityName);
				pincodeJsonObject.put("status", "Active");
				pincodeJsonObject.put("isDeleted", false);
			}
			
			wardJsonObject.put("pincode", pincodeJsonObject);
			
			jsonString = wardJsonObject.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	
	
	

}
