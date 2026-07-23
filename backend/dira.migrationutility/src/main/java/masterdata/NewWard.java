package masterdata;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class NewWard extends RestExecution {

	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Ward";

	private void createWard(Map<String, String> ward) {

		String apiURL = getAPIURL("SavbillCommonGateway/area/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getWardJson(ward);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String wardName = ward.get("WardName");
		String municipalityName = ward.get("Municipalties");
		wardName = wardName + " (" + municipalityName + ")";
		ProductUtility.printResponse(JSONResponseBody, logModuleName, wardName);
	}
	/*
	 public void createWard(List<Map<String, String>> wardMapList) {
	        // Create a thread pool with a fixed number of threads
	        ExecutorService executor = Executors.newFixedThreadPool(10);  // You can adjust the number of threads as needed

	        for (int i = 0; i < wardMapList.size(); i++) {
	            Map<String, String> map = wardMapList.get(i);
	            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());

	            // Submit each ward creation task to the executor
	            executor.submit(() -> createWard(map));
	        }

	        // Shut down the executor after all tasks are submitted
	        executor.shutdown();
	    }
*/


    // ------------------ Parallel Ward Creator with Retry ------------------
    public void createWard(List<Map<String, String>> wardMapList) {

        int numThreads = 35; // Adjust based on system performance
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map<String, String> wardData : wardMapList) {
            final Map<String, String> ward = wardData; // capture current ward data
            tasks.add(() -> {
                int maxRetries = 3;
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        createWard(ward); // your actual processing logic
                        success = true; // mark as done if successful
                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating ward: " + e.getMessage());

                        if (attempt < maxRetries) {
                            try {
                                Thread.sleep(2000L * attempt); // exponential backoff (2s, 4s, 6s)
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted for ward: " + ward);
                                break;
                            }
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Failed",
                                    "Ward creation failed after " + maxRetries + " attempts: " + ward);
                        }
                    }
                }

                return null;
            });
        }

        try {
            // Execute all tasks concurrently, waits for all to complete
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Utility.printLog(logFileName, logModuleName, "Error",
                    "Thread execution interrupted: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }


	
/*
	public void createWard(List<Map<String, String>> wardMapList) {

		for (int i = 0; i < wardMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = wardMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createWard(map);
		}
	}
*/
//	public List<Map<String, String>> readWardList() {
//
//		String sheetName = "Ward";
//		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
//		ReadData readData = new ReadData();
//		sheetMap = readData.getMaterDataSheet(sheetName);
//
//		Map<String, String> cellValue = new HashMap<String, String>();
//		List<Map<String, String>> wardMapList = new ArrayList<Map<String, String>>();
//
//		for (int i = 0; i < sheetMap.size(); i++) {
//
//			Map<String, String> valuemap = new HashMap<String, String>();
//			cellValue = sheetMap.get(i);
//
//			String ward = cellValue.get("WardName");
//			if ((!"".equals(ward)) && (ward != null)) {
//
//				valuemap.put("RowIndex", cellValue.get("RowIndex"));
//				valuemap.put("WardName", cellValue.get("WardName"));
//				valuemap.put("Municipalties", cellValue.get("Municipalties"));
//				valuemap.put("Status", cellValue.get("Status"));
//				wardMapList.add(valuemap);
//			}
//		}
//		return wardMapList;
//	}

    //=========================================

    public List<Map<String, String>> readWardList() {

        String sheetName = "Ward";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenWardMunicipalities = new HashSet<>();
        List<Map<String, String>> wardMapList = new ArrayList<>();

        for (Map<String, String> cellValue : sheetMap) {
            String ward = cellValue.get("WardName");
            String municipality = cellValue.get("Municipalties");
            String district = cellValue.get("District");
            String province = cellValue.get("Province");
            String country = cellValue.get("Country");

            // null + empty check
            if (ward != null && !ward.trim().isEmpty() &&
                    municipality != null && !municipality.trim().isEmpty() &&
                    district != null && !district.trim().isEmpty() &&
                    province != null && !province.trim().isEmpty() &&
                    country != null && !country.trim().isEmpty()) {

                // ✅ normalized key for duplicate detection
                String key = country.trim().toLowerCase() + "|" +
                        province.trim().toLowerCase() + "|" +
                        municipality.trim().toLowerCase() + "|" +
                        ward.trim().toLowerCase();

                if (seenWardMunicipalities.add(key)) { // add returns false if duplicate
                    Map<String, String> valueMap = new HashMap<>();
                    valueMap.put("RowIndex", cellValue.get("RowIndex"));
                    valueMap.put("WardName", ward.trim());
                    valueMap.put("Municipalties", municipality.trim());
                    valueMap.put("District", district.trim());
                    valueMap.put("Province", province.trim());
                    valueMap.put("Country", country.trim());
                    valueMap.put("Status", cellValue.get("Status"));

                    wardMapList.add(valueMap);
                } else {
                    System.out.println("⚠ Skipping duplicate Ward for Country|Province|Municipality: "
                            + country + " | " + province + " | " + municipality + " - " + ward);
                }
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

			if (details != null) {

				String data[] = details.split(":");
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
