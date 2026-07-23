package masterdata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;
public class BuildingMangament extends RestExecution{
	

			private static String logFileName = "masterdata.log";
			private static String logModuleName = "BuildingMangement";

			private void createBuilding(Map<String, String> buildingDetails) {

				String apiURL = getAPIURL("SavbillCommonGateway/buildingmgmt/save");
				Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

				String apiBody = buildJsonPayload(buildingDetails);
				Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

				if (!apiBody.equals(null)) {
					JSONObject JSONResponseBody = httpPostFormDatabui(apiURL, apiBody);
					String response = JSONResponseBody.toString(4);
					Utility.printLog(logFileName, logModuleName, "Response", response);

					String buildingName = buildingDetails.get("BuildingName");
					ProductUtility.printResponse(JSONResponseBody, logModuleName, buildingName);
				}
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
			// This method handles the creation of municipalities in parallel using threads
				public void createBuilding(List<Map<String, String>> subAreaMapList) {

				    // Thread pool setup to handle parallel tasks
				    int numThreads = 4;  // Adjust the number of threads based on system capabilities
				    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

				    List<Callable<Void>> tasks = new ArrayList<>();
				    
				    for (Map<String, String> wardD : subAreaMapList) {
				        final Map<String, String> ward = wardD; // Capture the current ward map
				        tasks.add(() -> {
				        	createBuilding(ward); // Process each ward in a separate thread
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
			public List<Map<String, String>> readBuildingList() {

				String sheetName = "SubArea";
				List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
				ReadData readData = new ReadData();
				sheetMap = readData.getMaterDataSheet(sheetName);

				Map<String, String> cellValue = new HashMap<String, String>();
				List<Map<String, String>> subAreaMapList = new ArrayList<Map<String, String>>();

				for (int i = 0; i < sheetMap.size(); i++) {
					Map<String, String> valuemap = new HashMap<String, String>();
					cellValue = sheetMap.get(i);
					String subArea = cellValue.get("BUILDING NAME");
					if ((!"".equals(subArea)) && (subArea != null)) {
						String RoadName=cellValue.get("ROAD_NAME");
						String Adress=cellValue.get("ADDRESS");
						String FATNo=cellValue.get("FAT No.");
						String SubArea="";
						// Building name logic 
						
						/*
						 * here i will get building name from sheet if buldingname not contains any no that is bulding anme and patter will be 
						 *  with Building name:  Address no._Building Name_FAT no.
						 */
						
						if (subArea.matches(".*[0-9].*")) {
						    // or perform any logic you want here
							SubArea=Adress+"_"+RoadName+"_"+FATNo;
						}
						else {
							SubArea=Adress+"_"+subArea+"_"+FATNo;
						}
						
						valuemap.put("RowIndex", cellValue.get("SNO"));
						valuemap.put("SubArea", cellValue.get("BUILDING NAME"));
						valuemap.put("BuildingName", SubArea);
						valuemap.put("RoadName", cellValue.get("ROAD_NAME"));
						valuemap.put("Adress", cellValue.get("ADDRESS"));
						valuemap.put("Units", cellValue.get("UNITS"));
						valuemap.put("FloorNo", cellValue.get("NO.FLOORS"));
						valuemap.put("FDTNo", cellValue.get("FDT No."));
						valuemap.put("FATNo", cellValue.get("FAT No."));
						valuemap.put("MDUSFU", cellValue.get("MDU/SFU"));
						valuemap.put("OLT", cellValue.get("OLT"));
						valuemap.put("Area", cellValue.get("AREA"));
						
						valuemap.put("Status", "Active");
						subAreaMapList.add(valuemap);
					}
				}
				return subAreaMapList;
			}

			public String buildJsonPayload(Map<String, String> building) {
			    String jsonString = null;

			    try {
			        JSONObject subAreaJsonObject = new JSONObject();

			        
			        String subArea = building.get("BuildingName");
			      
			        CommonGetAPI commonGetAPI = new CommonGetAPI();
			        
			        int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
                    int subAreaid=commonGetAPI.getSubAreaId(subArea);
                    
                    String unitsStr = building.get("Units");
                    int range = 0;

                    if (unitsStr != null && !unitsStr.isEmpty()) {
                        try {
                            range = Integer.parseInt(unitsStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace(); // or handle it gracefully
                        }
                    }
                      
			            // Set basic subArea details
			            subAreaJsonObject.put("buildingName", subArea);
			            subAreaJsonObject.put("buildingType",building.get("MDUSFU") ); // Hardcoded or can be dynamic
			            subAreaJsonObject.put("pincodeId", "");
			            subAreaJsonObject.put("areaId", JSONObject.NULL);
			            subAreaJsonObject.put("subAreaId", subAreaid); 
			            subAreaJsonObject.put("mvnoId", mvnoId);
			            subAreaJsonObject.put("buid", JSONObject.NULL);
			            subAreaJsonObject.put("isDeleted", false);

			            // Add building mappings (1 to 15)
			            JSONArray buildingMappings = new JSONArray();
			            for (int i = 1; i <= range; i++) {
			                JSONObject mapping = new JSONObject();
			                mapping.put("buildingNumber", String.valueOf(i));
			                mapping.put("isDeleted", false);
			                buildingMappings.put(mapping);
			            }
			            subAreaJsonObject.put("buildingMappings", buildingMappings);
			        

			        jsonString = subAreaJsonObject.toString();

			    } catch (Exception e) {
			        e.printStackTrace();
			    }

			    return jsonString;
			}
		}





