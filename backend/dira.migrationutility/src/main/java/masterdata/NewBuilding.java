package masterdata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;
public class NewBuilding extends RestExecution {
	
 

	    private static final String logFileName = "masterdata.log";
	    private static final String logModuleName = "BuildingMangement";

	    // Field constants to avoid typo issues
	    private static final String FIELD_BUILDING_NAME = "BUILDING NAME";
	    private static final String FIELD_ADDRESS = "ADDRESS";
	    private static final String FIELD_FAT_NO = "FAT No.";
	    private static final String FIELD_ROAD_NAME = "ROAD_NAME";

	    private void createBuilding(Map<String, String> buildingDetails) {
	        String apiURL = getAPIURL("SavbillCommonGateway/buildingmgmt/save");
	        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

	        String apiBody = buildJsonPayload(buildingDetails);
	        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

	        if (apiBody != null) {
	            try {
	                JSONObject JSONResponseBody = httpPostFormDatabui(apiURL, apiBody);
	                String response = JSONResponseBody.toString(4);
	                Utility.printLog(logFileName, logModuleName, "Response", response);

	                String buildingName = buildingDetails.get("BuildingName");
	                ProductUtility.printResponse(JSONResponseBody, logModuleName, buildingName);
	            } catch (Exception e) {
	                Utility.printLog(logFileName, logModuleName, "API Error", e.getMessage());
	            }
	        }
	    }

	    // Run in parallel
	    public void processBuildingList(List<Map<String, String>> subAreaMapList) {
	        int numThreads = 35;
	        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
	        AtomicInteger counter = new AtomicInteger();

	        List<Callable<Void>> tasks = new ArrayList<>();

	        for (Map<String, String> ward : subAreaMapList) {
	            tasks.add(() -> {
	                try {
	                    createBuilding(ward);
	                    Utility.printLog(logFileName, logModuleName, "Progress",
	                            "Processed: " + counter.incrementAndGet() + " / " + subAreaMapList.size());
	                    
	                    
	                } catch (Exception e) {
	                    Utility.printLog(logFileName, logModuleName, "Task Error", e.getMessage());
	                }
	                return null;
	            });
	        }

	        try {
	            executorService.invokeAll(tasks);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
	        } finally {
	            executorService.shutdown();
	            try {
	                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
	                    executorService.shutdownNow();
	                }
	            } catch (InterruptedException e) {
	                executorService.shutdownNow();
	            }
	        }
	    }

	    public List<Map<String, String>> readBuildingList() {
	        String sheetName = "SubArea";
	        ReadData readData = new ReadData();
	        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

	        List<Map<String, String>> subAreaMapList = new ArrayList<>();

	        for (Map<String, String> cellValue : sheetMap) {
	            String buildingName = cellValue.get(FIELD_BUILDING_NAME);

	                String address = cellValue.get(FIELD_ADDRESS);
	                String roadName = cellValue.get(FIELD_ROAD_NAME);
	                String fatNo = cellValue.get(FIELD_FAT_NO);
	                String formattedBuildingName;

					if ((buildingName == null || buildingName.isEmpty())){
						// or perform any logic you want here
						formattedBuildingName=address+"_"+roadName+"_"+fatNo;
					}
					else {
						formattedBuildingName=buildingName;
					}

	                /*if (buildingName.matches(".*[0-9].*")) {
	                    formattedBuildingName = address + "_" + roadName + "_" + fatNo;
	                } else {
	                    formattedBuildingName = address + "_" + buildingName + "_" + fatNo;
	                }*/

	                Map<String, String> valuemap = new HashMap<>();
	                valuemap.put("RowIndex", cellValue.get("SNO"));
	                valuemap.put("SubArea", formattedBuildingName);
	                valuemap.put("BuildingName", formattedBuildingName);
	                valuemap.put("RoadName", roadName);
	                valuemap.put("Adress", address);
	                valuemap.put("Units", cellValue.get("UNITS"));
//	                valuemap.put("FloorNo", cellValue.get("NO.FLOORS"));
//	                valuemap.put("FDTNo", cellValue.get("FDT No."));
	                valuemap.put("FATNo", fatNo);
	                valuemap.put("MDUSFU", cellValue.get("MDU/SFU"));
//	                valuemap.put("OLT", cellValue.get("OLT"));
//	                valuemap.put("Area", cellValue.get("AREA"));
	                valuemap.put("Status", "Active");

	                subAreaMapList.add(valuemap);

	        }

	        return subAreaMapList;
	    }

	    public String buildJsonPayload(Map<String, String> building) {
	        try {
	            JSONObject subAreaJsonObject = new JSONObject();

	            String subArea = building.get("BuildingName");
	            CommonGetAPI commonGetAPI = new CommonGetAPI();

	            int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
	            int subAreaId = commonGetAPI.getSubAreaId(subArea);

	            int units = 0;
	            try {
                    String unitsStr = building.get("Units");
	                if (unitsStr != null && !unitsStr.isEmpty()) {
	                    units = Integer.parseInt(unitsStr);
	                }
	            } catch (NumberFormatException e) {
	                Utility.printLog(logFileName, logModuleName, "Parsing Error", "Invalid unit number: " + e.getMessage());
	            }

	            subAreaJsonObject.put("buildingName", subArea);
	            subAreaJsonObject.put("buildingType", building.get("MDUSFU"));
	            subAreaJsonObject.put("pincodeId", "");
	            subAreaJsonObject.put("areaId", JSONObject.NULL);
	            subAreaJsonObject.put("subAreaId", subAreaId);
	            subAreaJsonObject.put("mvnoId", mvnoId);
	            subAreaJsonObject.put("buid", JSONObject.NULL);
	            subAreaJsonObject.put("isDeleted", false);

	            JSONArray buildingMappings = new JSONArray();
	            for (int i = 1; i <= units; i++) {
	                JSONObject mapping = new JSONObject();
	                mapping.put("buildingNumber", String.valueOf(i));
	                mapping.put("isDeleted", false);
	                buildingMappings.put(mapping);
	            }

	            subAreaJsonObject.put("buildingMappings", buildingMappings);
//                System.out.println(subAreaJsonObject);
	            return subAreaJsonObject.toString();

	        } catch (Exception e) {
	            Utility.printLog(logFileName, logModuleName, "Payload Error", e.getMessage());
	            return null;
	        }
	    }
	}

