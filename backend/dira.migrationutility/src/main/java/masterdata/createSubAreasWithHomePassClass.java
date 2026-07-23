package masterdata;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.util.*;
import java.util.concurrent.*;

public class createSubAreasWithHomePassClass extends RestExecution {


    private static final String logFileName = "masterdata.log";
    private static final String logModuleName = "BuildingMangement";
    private static final String logModuleName1 = "SubArea";
    private static final String logModuleName2 = "HomePass";
    private UpdateSheet updateSheet = new UpdateSheet();

    private final ConcurrentMap<String, String> excelUpdates = new ConcurrentHashMap<>();

    // ✅ Cache objects to avoid repeated expensive calls
    private final CommonGetAPI commonGetAPI = new CommonGetAPI();
    private final Map<String, Integer> pincodeCache = new ConcurrentHashMap<>();
    private final Map<String, String> masterDetailsCache = new ConcurrentHashMap<>();

    private static final int NUM_THREADS = Math.min(80, Runtime.getRuntime().availableProcessors());

    // ------------------ Parallel SubArea Creator + HomePass Trigger ------------------
    public void createSubAreasWithHomePass(List<Map<String, String>> subAreaList) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        updateSheet.setActiveSheetName("SubArea");
        ReadWriteExcelFile rw = new ReadWriteExcelFile();
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map<String, String> subAreaMap : subAreaList) {
            tasks.add(() -> {
                int maxRetries = 5;
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        // Get Location IDs once per subarea
                        Location5Ids ids = commonGetAPI.getCityPincodeWardId(
                                subAreaMap.get("OLT"),
                                subAreaMap.get("ROAD_NAME"),
                                subAreaMap.get("Ward"),
                                subAreaMap.get("Country"),
                                subAreaMap.get("State")

                        );

                        if (ids == null) {
                            Utility.printLog(logFileName, logModuleName, "Skipped",
                                    "Location IDs not found. SubArea skipped: " + subAreaMap);
                            break;
                        }

                        if (!checkSubAreaExists(ids, subAreaMap.get("BUILDING NAME"))) {
                            // Create SubArea and get full response
                            JSONObject response = createSubArea(subAreaMap);

                            // ✅ Only create HomePass when SubArea creation was successful (200)
                            if (response != null && response.optInt("responseCode", 0) == 200) {
                                createHomePass(subAreaMap, response);
                            } else {
                                Utility.printLog(logFileName, logModuleName2, "Skip",
                                        "HomePass skipped for existing or failed SubArea: " + subAreaMap.get("BUILDING NAME")
                                                + " | ResponseCode: " + response.optInt("responseCode", 0)
                                                + " | Message: " + response.optString("responseMessage", "Unknown"));
                            }


                        }

                        success = true;

                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating SubArea/HomePass: " + e.getMessage());
                        if (attempt < maxRetries) Thread.sleep((long) (2000L * Math.pow(2, attempt - 1)));
                        else Utility.printLog(logFileName, logModuleName, "Failed",
                                "SubArea creation failed after " + maxRetries + " attempts: " + subAreaMap);
                    }
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
            try {
                if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) executorService.shutdownNow();
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
//            finally {
//                System.out.println("---------->   Started to write status in Fast Excel sheet. <-----------------");
//                rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);
//                System.out.println("---------->   Stopped to write status in Fast Excel sheet. <-----------------");
//            }
            finally {
                System.out.println("---------->   Started to write status in Fast Excel sheet. <-----------------");

                excelUpdates.forEach((rowIndex, columnAndValue) -> {
                    updateSheet.setRowList(rowIndex, columnAndValue);
                });

                rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);

                System.out.println("---------->   Stopped to write status in Fast Excel sheet. <-----------------");
            }
        }
    }

    private void handleResponse(JSONObject response, String wardName, String rowIndex) {
        int status = response.getInt("responseCode");
        String migrationStatus = "Initial";
        String migrationDetail = "Initial";

        if (!response.has("ERROR")) {
            if (status == 200) {
                String message = response.getString("responseMessage") + " - " + wardName;
                migrationStatus = "Success";
                migrationDetail = message;
            } else if (status == 406) {
                String error = response.getString("responseMessage") + " - " + wardName;
                migrationStatus = "Already Exists";
                migrationDetail = error;
            } else {
                String message = response.get("ERROR") + " - " + wardName;
                migrationStatus = "Error";
                migrationDetail = message;
            }
        } else {
            String message = response.get("ERROR") + " - " + wardName;
            migrationStatus = "Error";
            migrationDetail = message;
        }

        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
//        updateSheet.setRowList(rowIndex, columnAndValue);
        excelUpdates.put(rowIndex, columnAndValue);
    }

    private JSONObject createSubArea(Map<String, String> subAreaMap) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/subarea/save");
            Utility.printLog(logFileName, logModuleName1, "Request URL", apiURL);

            String apiBody = getSubAreaJson(subAreaMap);
            Utility.printLog(logFileName, logModuleName1, "Request Body", apiBody);

            JSONObject response = httpPostFormDatabui(apiURL, apiBody);
            String formattedResponse = response.toString(4);
            Utility.printLog(logFileName, logModuleName1, "Response", formattedResponse);

            String rowIndex = subAreaMap.get("RowIndex");
            String name = subAreaMap.get("BUILDING NAME");
            int status = response.optInt("responseCode", 0);

            ProductUtility.printResponse(response, logModuleName1, name);

            handleResponse(response, name, subAreaMap.get("RowIndex"));

            return response;

        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName1, "Error", "SubArea creation failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private void createHomePass(Map<String, String> homePassMap, JSONObject response) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/buildingmgmt/save");
            Utility.printLog(logFileName, logModuleName2, "Request URL", apiURL);

            String apiBody = getHomePassJson(homePassMap, response);
            Utility.printLog(logFileName, logModuleName2, "Request Body", apiBody);

            JSONObject resp = httpPostFormDatabui(apiURL, apiBody);
            String formattedResponse = resp.toString(4);
            Utility.printLog(logFileName, logModuleName2, "Response", formattedResponse);

            String rowIndex = homePassMap.get("RowIndex");
            String buildingName = homePassMap.get("BUILDING NAME");
            int status = resp.optInt("responseCode", 0);

            ProductUtility.printResponse(response, logModuleName2, buildingName);

        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName2, "Error", "HomePass creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    // ------------------ Excel Reader + Duplicate Check ------------------
//    public List<Map<String, String>> readSubAreaHomePassList() {
//
//        String sheetName = "SubArea";
//        ReadData readData = new ReadData();
//        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);
//
//        List<Map<String, String>> subAreaMapList = new ArrayList<>();
//        Set<String> seenSubAreas = new HashSet<>();
//
//        for (Map<String, String> cellValue : sheetMap) {
//            String subArea = safeTrim(cellValue.get("BUILDING NAME"));
//            String roadName = safeTrim(cellValue.get("ROAD_NAME"));
//            String address = safeTrim(cellValue.get("ADDRESS"));
//            String fatNo = safeTrim(cellValue.get("FAT No."));
//            String olt = safeTrim(cellValue.get("OLT"));
//            String rowIndex = safeTrim(cellValue.get("SNO"));
//            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));
//
//            if ((!subArea.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {
//                if (!subArea.isEmpty() && !roadName.isEmpty() && !address.isEmpty() && !fatNo.isEmpty() && !olt.isEmpty()) {
//                    String key = (olt + "|" + fatNo + "|" + roadName + "|" + subArea).toLowerCase();
//
//                    if (seenSubAreas.add(key)) {
//                        Map<String, String> valueMap = new HashMap<>();
//                        valueMap.put("RowIndex", rowIndex);
//                        valueMap.put("BUILDING NAME", subArea);
//                        valueMap.put("ROAD_NAME", roadName);
//                        valueMap.put("ADDRESS", address);
//                        valueMap.put("Ward", fatNo);
//                        valueMap.put("OLT", olt);
//                        valueMap.put("Country", "Kampala");
//                        valueMap.put("State", "Kampala");
//
//                        valueMap.put("Units", cellValue.get("UNITS"));
//                        valueMap.put("MDUSFU", cellValue.get("MDU/SFU"));
//                        valueMap.put("Status", "Active");
//                        subAreaMapList.add(valueMap);
//                    } else {
//                        System.out.println("⚠ Skipping duplicate SubArea: " +
//                                olt + "|" + fatNo + "|" + roadName + " - " + subArea);
//                    }
//
//                }
//            }
//        }
//        return subAreaMapList;
//    }


    // ------------------ Excel Reader + Duplicate Check ------------------
    public List<Map<String, String>> readSubAreaHomePassList() {

        String sheetName = "SubArea";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        List<Map<String, String>> subAreaMapList = new ArrayList<>();
        Set<String> seenSubAreas = new HashSet<>();
        int subAreaisEmpty = 0;
        int roadNameisEmpty = 0;
        int fatNoisEmpty = 0;
        int oltisEmpty = 0;
        // ✅ Declare counters before the loop (so they don’t reset every time)
        int totalCount = 0;
        int uniqueCount = 0;
        int skippedCount = 0;

        for (Map<String, String> cellValue : sheetMap) {

            String subArea = safeTrim(cellValue.get("BUILDING NAME"));
            String roadName = safeTrim(cellValue.get("ROAD_NAME"));
            String address = safeTrim(cellValue.get("ADDRESS"));
            String fatNo = safeTrim(cellValue.get("FAT No."));
            String olt = safeTrim(cellValue.get("OLT"));
            String rowIndex = safeTrim(cellValue.get("SNO"));
            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));

            if ((subArea.isEmpty())) {
                subAreaisEmpty++;
                System.out.println("subArea rowNumber = " + rowIndex);
            }

            if ((roadName.isEmpty())) {
                roadNameisEmpty++;
                System.out.println("roadName rowNumber = " + rowIndex);
            }

            if ((fatNo.isEmpty())) {
                fatNoisEmpty++;
                System.out.println("fatNo rowNumber = " + rowIndex);
            }

            if ((olt.isEmpty())) {
                oltisEmpty++;
                System.out.println("olt rowNumber = " + rowIndex);
            }

            if ((!subArea.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus))
                    && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {


//                if (!subArea.isEmpty() && !roadName.isEmpty()
//                        && !fatNo.isEmpty() && !olt.isEmpty()) {

                    String key = (olt + "|" + fatNo + "|" + roadName + "|" + subArea).toLowerCase();
                    totalCount++;
                    if (seenSubAreas.add(key)) {
                        uniqueCount++;

                        Map<String, String> valueMap = new HashMap<>();
                        valueMap.put("RowIndex", rowIndex);
                        valueMap.put("BUILDING NAME", subArea);
                        valueMap.put("ROAD_NAME", roadName);
                        valueMap.put("ADDRESS", address);
                        valueMap.put("Ward", fatNo);
                        valueMap.put("OLT", olt);
                        valueMap.put("Country", "Kampala");
                        valueMap.put("State", "Kampala");
                        valueMap.put("Units", cellValue.get("UNITS"));
                        valueMap.put("MDUSFU", cellValue.get("MDU/SFU"));
                        valueMap.put("Status", "Active");

                        subAreaMapList.add(valueMap);

                    } else {
                        skippedCount++;
                        System.out.println("⚠ Skipping duplicate SubArea: " +
                                olt + "|" + fatNo + "|" + roadName + " - " + subArea);
                    }
//                }
            }
        }

        // ✅ Print summary AFTER processing all rows
        System.out.println("========== Summary ==========");
        System.out.println("Total records processed: " + totalCount);
        System.out.println("Unique records stored:   " + uniqueCount);
        System.out.println("Duplicates skipped:      " + skippedCount);
        System.out.println("subAreaisEmpty(Defective) = " + subAreaisEmpty);
        System.out.println("roadNameisEmpty(Defective) = " + roadNameisEmpty);
        System.out.println("fatNoisEmpty(Defective) = " + fatNoisEmpty);
        System.out.println("oltisEmpty(Defective) = " + oltisEmpty);
        System.out.println("✅ Total Sheet Map Count Should Match with Total Count: " + sheetMap.size());
        System.out.println("=============================");

        return subAreaMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // ------------------ JSON Builder with Cache ------------------
    private String getSubAreaJson(Map<String, String> subArea) {

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject subAreaJsonObject = new JSONObject();

            Location5Ids ids = commonGetAPI.getCityPincodeWardId(

                    subArea.get("OLT"),
                    subArea.get("ROAD_NAME"),
                    subArea.get("Ward"),
                    subArea.get("Country"),
                    subArea.get("State")
            );

            String roadName = subArea.get("ROAD_NAME");
            String fatNo = subArea.get("FAT No.");

            if (ids == null) {
                throw new Exception("Location5Ids not found for provided names");
            }
            int mvnoId = commonGetAPI.getMvnoId(Constant.STAFF_USERNAME); // here i have add lower bcz  there is need lower case
            // ✅ cached lookups

                if (ids != null) {
                    subAreaJsonObject.put("name", subArea.get("BUILDING NAME"));
                    subAreaJsonObject.put("status", "Active");
                    subAreaJsonObject.put("countryId", ids.getCountryId());
                    subAreaJsonObject.put("cityId", ids.getCityId());
                    subAreaJsonObject.put("stateId", ids.getStateId());
                    subAreaJsonObject.put("areaId", ids.getWardId());
                    subAreaJsonObject.put("isDeleted", false);
                    subAreaJsonObject.put("mvnoId", mvnoId);
                    subAreaJsonObject.put("buId", JSONObject.NULL);
                }

//            System.out.println("subAreaJsonObject = " + subAreaJsonObject);
            return subAreaJsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{SubAreaJsonObject Body Null}";
        }
    }

    //------------------Cache Helpers------------------
    private int getCachedPincode(String roadName) {
        return pincodeCache.computeIfAbsent(roadName, rn -> commonGetAPI.getPincodeId(rn));
    }

    //=====================================================================================================================

    public String getHomePassJson(Map<String, String> homePass, JSONObject response) {
        try {
            JSONObject homePassJsonObject = new JSONObject();

            String HomePass = homePass.get("BUILDING NAME");
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            int units = 0;
            try {
                String unitsStr = homePass.get("Units");
                if (unitsStr != null && !unitsStr.isEmpty()) {
                    units = Integer.parseInt(unitsStr);
                }
            } catch (NumberFormatException e) {
                Utility.printLog(logFileName, logModuleName, "Parsing Error", "Invalid unit number: " + e.getMessage());
            }


            int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME); // here i ahve add lower bcz  there is need lower case

            homePassJsonObject.put("buildingName", homePass.get("BUILDING NAME"));
            homePassJsonObject.put("buildingType", homePass.get("MDUSFU"));
            homePassJsonObject.put("pincodeId", "");
            homePassJsonObject.put("areaId", JSONObject.NULL);
            try {
                JSONObject dataObject = response.optJSONObject("data");

                if (dataObject != null && dataObject.has("id")) {
                    String subAreaId = String.valueOf(dataObject.get("id"));
                    homePassJsonObject.put("subAreaId", subAreaId);
                    Utility.printLog(logFileName, logModuleName, "Info", "SubArea ID found: " + subAreaId);
                } else {
                    Utility.printLog(logFileName, logModuleName, "Warning",
                            "SubArea ID missing in API response. Defaulting to null.");
                    homePassJsonObject.put("subAreaId", JSONObject.NULL);
                }

            } catch (Exception e) {
                Utility.printLog(logFileName, logModuleName, "Error",
                        "Failed to extract SubArea ID: " + e.getMessage());
                e.printStackTrace();
                homePassJsonObject.put("subAreaId", JSONObject.NULL);
            }
            homePassJsonObject.put("isDeleted", false);
            homePassJsonObject.put("mvnoId", mvnoId);
            homePassJsonObject.put("buid", JSONObject.NULL);

            JSONArray buildingMappings = new JSONArray();
            for (int i = 1; i <= units; i++) {
                JSONObject mapping = new JSONObject();
                mapping.put("buildingNumber", String.valueOf(i));
                mapping.put("isDeleted", false);
                buildingMappings.put(mapping);
            }

            homePassJsonObject.put("buildingMappings", buildingMappings);

            return homePassJsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{SubAreaJsonObject Body Null}";
        }

    }

    // ------------------ Helper: Check if SubArea Exists ------------------
    private boolean checkSubAreaExists(Location5Ids ids, String subAreaName) {
        try {
            // TODO: Replace with actual API call
            // Example:
            // JSONObject response = httpGet("api/ward/list?cityId=" + ids.getCityId());
            // return response contains subAreaName;

            return false; // assume not exist for now
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error",
                    "Error checking SubArea existence: " + subAreaName + " | " + e.getMessage());
            return false;
        }
    }
}

