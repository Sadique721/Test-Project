package masterdata;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class ServiceArea extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "ServiceArea";

	private void createServiceArea(Map<String, String> serviceArea) {

		String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/save");
		Utility.printLog(logFileName,logModuleName , "Request URL", apiURL);

		// Initializing payload or API body
//		String apiBody = getServiceAreaJson(serviceArea);
//		Utility.printLog(logFileName,logModuleName , "Request Body", apiBody);

        // Build JSON body
        String apiBody = getServiceAreaJson(serviceArea);
        if(apiBody == null) {
            Utility.printLog(logFileName, logModuleName , "Error", "Payload is null for " + serviceArea.get("ServiceArea"));
            return;
        }

        Utility.printLog(logFileName,logModuleName , "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName,logModuleName , "Response", response);

		String serviceAreaName = serviceArea.get("ServiceArea");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, serviceAreaName);

	}

    //This code will create service area with respect to your system capability ex. cores
    public void createServiceArea(List<Map<String, String>> serviceAreaMapList) {
        int numThreads = Runtime.getRuntime().availableProcessors(); // On your system = 10
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map<String, String> serviceArea : serviceAreaMapList) {
            tasks.add(() -> {
                // ✅ Duplication check (API + Excel)
                if (!isDuplicateServiceArea(serviceArea)) {
                    createServiceArea(serviceArea);
                } else {
                    Utility.printLog(logFileName, logModuleName, "Duplicate Skipped", serviceArea.get("ServiceArea"));
                }
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }

/*
	// This method handles the creation of prepaid plans in parallel using threads  using savana project
	public void createServiceArea(List<Map<String, String>> serviceAreaMapList) {

	    // Thread pool setup to handle parallel tasks
	    int numThreads = 8;  // Adjust the number of threads based on system capabilities
	    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

	    List<Callable<Void>> tasks = new ArrayList<>();
	    
	    for (Map<String, String> serviceArea : serviceAreaMapList) {
	        final Map<String, String> currentService = serviceArea; // Capture the current municipal map
	        tasks.add(() -> {
	            createServiceArea(currentService); // Process the current municipal in a separate thread
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

    /*
	public void createServiceArea(List<Map<String, String>> serviceAreaMapList) {
		
		for (int i = 0; i < serviceAreaMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = serviceAreaMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createServiceArea(map);
		}
	}
*/

    // Read ServiceArea list from Excel with column mapping + duplicate removal
    public List<Map<String, String>> readServiceAreaList() {

        String sheetName = "ServiceArea";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenServiceAreas = new HashSet<>();
        List<Map<String, String>> serviceAreaMapList = new ArrayList<>();

        for (Map<String, String> cellValue : sheetMap) {

            String serviceArea = cellValue.get("ServiceArea");
            String district = cellValue.get("District");
            String municipality = cellValue.get("Municipalties"); // Excel spelling
//            String siteName = cellValue.get("SiteName");

            // null + empty check
            if (serviceArea != null && !serviceArea.trim().isEmpty() &&
                    district != null && !district.trim().isEmpty() &&
                    municipality != null && !municipality.trim().isEmpty()) {

                // Excel duplicate check: ServiceArea|District|Municipalties
                String key = serviceArea.trim().toLowerCase() + "|" +
                        district.trim().toLowerCase() + "|" +
                        municipality.trim().toLowerCase();

                if (seenServiceAreas.contains(key)) {
                    Utility.printLog(logFileName, logModuleName, "Excel Duplicate Skipped", serviceArea);
                    continue;
                }
                seenServiceAreas.add(key);

                Map<String, String> valuemap = new HashMap<>();
                valuemap.put("RowIndex", cellValue.get("RowIndex"));
                valuemap.put("ServiceArea", serviceArea);
//            valuemap.put("SiteName", cellValue.get("SiteName"));
                valuemap.put("District", district);
                valuemap.put("Municipalties", municipality); // Excel spelling
//            valuemap.put("Latitude", cellValue.get("Latitude"));
//            valuemap.put("Longitude", cellValue.get("Longitude"));
//            valuemap.put("Radius", cellValue.get("Radius"));
//            valuemap.put("ServiceAreaType", cellValue.get("ServiceAreaType"));
//            valuemap.put("UnitNo", cellValue.get("UnitNo"));
                valuemap.put("Status", cellValue.get("Status"));

                serviceAreaMapList.add(valuemap);
            }
        }
        return serviceAreaMapList;
    }


    // Build JSON payload with safe municipality handling
    private String getServiceAreaJson(Map<String, String> serviceArea) {

        String jsonString = null;

        try {
            JSONObject serviceAreaJson = new JSONObject();

            CommonGetAPI commonGetAPI = new CommonGetAPI();
            String status = ProductUtility.getStatus(serviceArea.get("Status"));

            List<Integer> pincodesMultiple = new ArrayList<Integer>();
            List<Integer> polygonList = new ArrayList<Integer>();
            List<Integer> mvnoIds = new ArrayList<Integer>();
            List<Integer> locationIds = new ArrayList<Integer>();

            int districtId = -1;

            String district = serviceArea.get("District");
            districtId = commonGetAPI.getDistrictId(district);

            // Municipalities (handles single and multiple values)
            String municipalities = serviceArea.get("Municipalties");
            if (municipalities != null && !municipalities.trim().isEmpty()) {
                String[] temp = municipalities.split(",");
                for (String pin : temp) {
                    String details = commonGetAPI.getMasterDetailsByMunicipalityName(pin.trim());
                    if (details != null && details.contains(":")) {
                        String[] data = details.split(":"); // pincodeId:cityId
                        int pincdeId = Integer.parseInt(data[0]);
                        int cityId = Integer.parseInt(data[1]);

                        if(districtId == cityId) {
                            pincodesMultiple.add(pincdeId);
                        }
                    }
                }
            }

            // Build JSON
            serviceAreaJson.put("name", serviceArea.get("ServiceArea"));
            serviceAreaJson.put("siteName", serviceArea.get("SiteName"));
            serviceAreaJson.put("cityid", districtId);
            serviceAreaJson.put("pincodes", pincodesMultiple);
//            serviceAreaJson.put("latitude", serviceArea.get("Latitude"));
//            serviceAreaJson.put("longitude", serviceArea.get("Longitude"));
//            serviceAreaJson.put("radius", serviceArea.get("Radius"));
            serviceAreaJson.put("status", status);

            serviceAreaJson.put("mvnoIds", mvnoIds);
            serviceAreaJson.put("locationIds", locationIds); // fixed key
            serviceAreaJson.put("polyGoneList", polygonList);
            serviceAreaJson.put("id", "");
            serviceAreaJson.put("lastModifiedById", "");
            serviceAreaJson.put("isDeleted", false);
            serviceAreaJson.put("selectedPincodes", JSONObject.NULL);



            String serviceAreaType = serviceArea.get("ServiceAreaType");

            if ("private".equalsIgnoreCase(serviceAreaType)) {
                serviceAreaJson.put("serviceAreaType", serviceAreaType);
                serviceAreaJson.put("blockNo", serviceArea.get("UnitNo"));
            } else {
                serviceAreaJson.put("serviceAreaType", "public");
                serviceAreaJson.put("blockNo", JSONObject.NULL);
            }


            serviceAreaJson.put("areaid", JSONObject.NULL);
            serviceAreaJson.put("mvnoId", 2);

            jsonString = serviceAreaJson.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }
        System.out.println("Generate JSon -" + jsonString);
        return jsonString;
    }

    // API duplication check by ServiceArea name
    private boolean isDuplicateServiceArea(Map<String, String> serviceArea) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/getByName?name=" + serviceArea.get("ServiceArea"));
            JSONObject jsonResponse = httpGet(apiURL);

            if (jsonResponse != null && jsonResponse.has("responseCode")) {
                int status = jsonResponse.getInt("responseCode");
                if (status == 200) {
                    JSONArray dataArr = jsonResponse.optJSONArray("data");
                    if (dataArr != null && dataArr.length() > 0) {
                        return true; // already exists in API
                    }
                }
            }
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Duplication Check Error", e.getMessage());
        }
        return false;
    }

}

/*
	private String getServiceAreaJson(Map<String, String> serviceArea) {

		String jsonString = null;

		try {

			JSONObject serviceAreaJson = new JSONObject();
			
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			String status = ProductUtility.getStatus(serviceArea.get("Status"));
			
			List<Integer> pincodesMultiple = new ArrayList<Integer>();
			List<Integer> ploygoneList = new ArrayList<Integer>();
			//add list :-In pojo data is :   "mvnoIds": [ ],
			List<Integer> mvnoIds = new ArrayList<Integer>();
			List<Integer> loctionsIds = new ArrayList<Integer>();
			
			int districtId = -1;
			int pincodeId= -1;
			
			String district = serviceArea.get("District");
			districtId = commonGetAPI.getDistrictId(district);
			
			String pincode=serviceArea.get("Municipalties");
			String[] temp = pincode.split(",");

//			for (int i = 0; i < temp.length; i++) {
//			
//				pincodeId = commonGetAPI.getPincodeId(temp[i]);
//				pincodesMultiple.add(pincodeId);
//				
//		
//			}
					
			// new 
			for (int i = 0; i < temp.length; i++) {
				String pin=temp[i];

				String details= commonGetAPI.getMasterDetailsByMunicipalityName(pin);
				String[] data = details.split(":");
				int pincdeId = Integer.parseInt(data[0]);
				int cityId = Integer.parseInt(data[1]);


				if(districtId==cityId) {
				pincodesMultiple.add(pincdeId);
				}

			}
					
			
			serviceAreaJson.put("name", serviceArea.get("ServiceArea"));
			serviceAreaJson.put("siteName", serviceArea.get("SiteName"));
			
			serviceAreaJson.put("cityid", districtId);
			serviceAreaJson.put("pincodes", pincodesMultiple);
			serviceAreaJson.put("latitude", serviceArea.get("Latitude"));
			serviceAreaJson.put("longitude", serviceArea.get("Longitude"));
			serviceAreaJson.put("radius", serviceArea.get("Radius"));
			serviceAreaJson.put("status", status);
			
			serviceAreaJson.put("mvnoIds", mvnoIds);
			serviceAreaJson.put("mvnoIds", loctionsIds);
			serviceAreaJson.put("polyGoneList", ploygoneList);			
			serviceAreaJson.put("id", "");
			serviceAreaJson.put("lastModifiedById", "");
			serviceAreaJson.put("isDeleted", false);
			serviceAreaJson.put("selectedPincodes", JSONObject.NULL);
			if(serviceArea.get("ServiceAreaType").equalsIgnoreCase("private")){
			serviceAreaJson.put("serviceAreaType",serviceArea.get("ServiceAreaType"));
			serviceAreaJson.put("blockNo",serviceArea.get("UnitNo"));
			}
			else {
				serviceAreaJson.put("serviceAreaType", JSONObject.NULL);
				serviceAreaJson.put("blockNo", JSONObject.NULL);
			}
			serviceAreaJson.put("areaid", JSONObject.NULL);
			serviceAreaJson.put("mvnoId", 2);

			jsonString = serviceAreaJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

	private List<Integer> getPincodefromCity(int cityId) {

		String apiURL = "SavbillCommonGateway/serviceArea/getPincodefromCity?id=" + cityId;
		apiURL = getAPIURL(apiURL);

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		List<Integer> list = new ArrayList<Integer>();

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(jsonArray.getJSONObject(i).getInt("id"));
			}
		}
		return list;
	}

}
*/