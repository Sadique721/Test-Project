package masterdata;

import java.util.*;
import java.util.concurrent.*;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;

public class SubArea extends RestExecution {

    private static final String logFileName = "masterdata.log";
    private static final String logModuleName = "SubArea";

    // ✅ Cache objects to avoid repeated expensive calls
    private final CommonGetAPI commonGetAPI = new CommonGetAPI();
    private final Map<String, Integer> pincodeCache = new ConcurrentHashMap<>();
    private final Map<String, String> masterDetailsCache = new ConcurrentHashMap<>();

    // ------------------ API Call ------------------
    private void createSubArea(Map<String, String> subArea) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/subarea/save");
            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            String apiBody = getSubAreaJson(subArea);
            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

            JSONObject JSONResponseBody = httpPostFormDatabui(apiURL, apiBody);
            String response = JSONResponseBody.toString(4);
            Utility.printLog(logFileName, logModuleName, "Response", response);

            String subAreaName = subArea.get("BuildingName");
            ProductUtility.printResponse(JSONResponseBody, logModuleName, subAreaName);
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error", e.getMessage());
        }
    }

    // ------------------ Parallel Executor ------------------
//    public void createSubArea(List<Map<String, String>> subAreaMapList) {
//        int numThreads = Math.min(5, subAreaMapList.size()); // dynamic thread count
//        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
//        for (Map<String, String> subArea : subAreaMapList) {
//            executorService.submit(() -> createSubArea(subArea));
//        }
//
//        executorService.shutdown();
//        try {
//            executorService.awaitTermination(30, TimeUnit.MINUTES); // wait for all tasks
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            Utility.printLog(logFileName, logModuleName, "Error", "Thread interrupted");
//        }
//    }

    // ------------------ Parallel Executor with Retry ------------------
    public void createSubArea(List<Map<String, String>> subAreaMapList) {
        int numThreads = Math.min(35, subAreaMapList.size()); // dynamic thread count
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (Map<String, String> subArea : subAreaMapList) {
            executorService.submit(() -> {
                int maxRetries = 3;
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        createSubArea(subArea); // your existing method
                        success = true;
                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating sub-area: " + e.getMessage());
                        if (attempt < maxRetries) {
                            try {
                                Thread.sleep(2000L * attempt); // exponential backoff
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted");
                                break;
                            }
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Failed",
                                    "Sub-area creation failed after " + maxRetries + " attempts: " + subArea);
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES); // wait for all tasks
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Utility.printLog(logFileName, logModuleName, "Error", "Thread interrupted while waiting");
        }
    }


    // ------------------ Excel Reader + Duplicate Check ------------------
    public List<Map<String, String>> readSubAreaList() {

        String sheetName = "SubArea";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        List<Map<String, String>> subAreaMapList = new ArrayList<>();
        Set<String> seenSubAreas = new HashSet<>();

        for (Map<String, String> cellValue : sheetMap) {
            Map<String, String> valuemap = new HashMap<>();

            String subArea = cellValue.get("BUILDING NAME");
            String roadName = cellValue.get("ROAD_NAME");
            String address = cellValue.get("ADDRESS");
            String fatNo = cellValue.get("FAT No.");
            String olt = cellValue.get("OLT");
//            String area = cellValue.get("AREA");

            // fallback building name
            subArea = (subArea == null || subArea.trim().isEmpty())
                    ? (address + "_" + roadName + "_" + fatNo)
                    : subArea.trim();

            if (subArea != null && !subArea.isEmpty() &&
                    roadName != null && !roadName.trim().isEmpty() &&
                    fatNo != null && !fatNo.trim().isEmpty() &&
                    olt != null && !olt.trim().isEmpty()) {

                // ✅ normalized duplicate check key
                String key = subArea.trim().toLowerCase() + "|" +
                        roadName.trim().toLowerCase() + "|" +
                        fatNo.trim().toLowerCase() + "|" +
                        olt.trim().toLowerCase();

                if (seenSubAreas.add(key)) {
                    valuemap.put("RowIndex", cellValue.get("SNO"));
                    valuemap.put("BuildingName", subArea);
                    valuemap.put("RoadName", roadName);
                    valuemap.put("FATNo", fatNo);
//                    valuemap.put("MDUSFU", cellValue.get("MDU/SFU"));
                    valuemap.put("OLT", olt);
//                    valuemap.put("Area", area);
                    valuemap.put("Status", "Active");

                    subAreaMapList.add(valuemap);
                } else {
                    System.out.println("⚠ Skipping duplicate SubArea for Building|Road|FAT|OLT: "
                            + subArea + " | " + roadName + " | " + fatNo + " | " + olt);
                }
            }
        }
        return subAreaMapList;
    }

    // ------------------ JSON Builder with Cache ------------------
    private String getSubAreaJson(Map<String, String> subArea) {
        try {
            JSONObject subAreaJsonObject = new JSONObject();

            String status = ProductUtility.getStatus(subArea.get("Status"));
            String SubArea = subArea.get("BuildingName");
            String roadName = subArea.get("RoadName");
            String fatNo = subArea.get("FATNo");

            // ✅ cached lookups
            int pincodeId = getCachedPincode(roadName);
            if (pincodeId != 0) {
                String areaPinId = fatNo + pincodeId;
                String details = getCachedMasterDetails(areaPinId, fatNo);

                int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME); // here i ahve add lower bcz  there is need lower case

                if (details != null) {
                    String[] data = details.split(":");
                    int areaId = Integer.parseInt(data[0]);
                    int cityId = Integer.parseInt(data[1]);
                    int countryId = Integer.parseInt(data[2]);
                    int stateId = Integer.parseInt(data[3]);

                    subAreaJsonObject.put("name", SubArea);
                    subAreaJsonObject.put("cityId", cityId);
                    subAreaJsonObject.put("stateId", stateId);
                    subAreaJsonObject.put("countryId", countryId);
                    subAreaJsonObject.put("status", status);
                    subAreaJsonObject.put("areaId", areaId);
                    subAreaJsonObject.put("mvnoId", mvnoId);
                    subAreaJsonObject.put("status", "Active");
                    subAreaJsonObject.put("buId", JSONObject.NULL);
                    subAreaJsonObject.put("isDeleted", false);
                }
            }
            return subAreaJsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // ------------------ Cache Helpers ------------------
    private int getCachedPincode(String roadName) {
        return pincodeCache.computeIfAbsent(roadName, rn -> commonGetAPI.getPincodeId(rn));
    }

    private String getCachedMasterDetails(String areaPinId, String area) {
        return masterDetailsCache.computeIfAbsent(areaPinId,
                key -> commonGetAPI.getMasterDetailsByAreaNameFindWithPincode(areaPinId, area));
    }
}
