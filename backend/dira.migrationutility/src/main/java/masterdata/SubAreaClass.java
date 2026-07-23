//package masterdata;
//
//import java.util.*;
//import java.util.concurrent.*;
//
//import org.json.JSONObject;
//
//import api.ReadData;
//import api.RestExecution;
//import commons.CommonGetAPI;
//import temp.UpdateSheet;
//import utility.Constant;
//import utility.ProductUtility;
//import utility.ReadWriteExcelFile;
//import utility.Utility;
//
//public class SubAreaClass extends RestExecution {
//
//    private static final String logFileName = "masterdata.log";
//    private static final String logModuleName = "SubArea";
//    private UpdateSheet updateSheet = new UpdateSheet();
//
//    // ✅ Cache objects to avoid repeated expensive calls
//    private final CommonGetAPI commonGetAPI = new CommonGetAPI();
//    private final Map<String, Integer> pincodeCache = new ConcurrentHashMap<>();
//    private final Map<String, String> masterDetailsCache = new ConcurrentHashMap<>();
//
//    // ------------------ API Call ------------------
//    private void createSubAreaClass(Map<String, String> subArea) {
//        try {
//            String apiURL = getAPIURL("SavbillCommonGateway/subarea/save");
//            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
//
//            String apiBody = getSubAreaJson(subArea);
//            if (apiBody == null) {
//                Utility.printLog(logFileName, logModuleName, "Skipped",
//                        "Skipping subarea creation due to missing location IDs or data: " + subArea);
//                return;
//            }
//
//            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);
//            JSONObject JSONResponseBody = httpPostFormDatabui(apiURL, apiBody);
//            String response = JSONResponseBody.toString(4);
//            Utility.printLog(logFileName, logModuleName, "Response", response);
//
//            String subAreaName = subArea.get("BUILDING NAME");
//            ProductUtility.printResponse(JSONResponseBody, logModuleName, subAreaName);
//
//            handleResponse(JSONResponseBody, subAreaName, subArea.get("RowIndex"));
//        } catch (Exception e) {
//            Utility.printLog(logFileName, logModuleName, "Error", e.getMessage());
//        }
//    }
//
//    private void handleResponse(JSONObject response, String subAreaName, String rowIndex) {
//        int status = response.getInt("responseCode");
//        String migrationStatus = "Initial";
//        String migrationDetail = "Initial";
//
//        if (!response.has("ERROR")) {
//            if (status == 200) {
//                String message = response.getString("responseMessage") + " - " + subAreaName;
//                migrationStatus = "Success";
//                migrationDetail = message;
//            } else if (status == 406) {
//                String error = response.getString("responseMessage") + " - " + subAreaName;
//                migrationStatus = "Already Exists";
//                migrationDetail = error;
//            } else {
//                String message = response.get("ERROR") + " - " + subAreaName;
//                migrationStatus = "Error";
//                migrationDetail = message;
//            }
//        } else {
//            String message = response.get("ERROR") + " - " + subAreaName;
//            migrationStatus = "Error";
//            migrationDetail = message;
//        }
//
//        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
//        updateSheet.setRowList(rowIndex, columnAndValue);
//    }
//
//    // ------------------ Parallel SubArea Creator with Retry ------------------
//    public void createSubAreaClass(List<Map<String, String>> subAreaMapList) {
//        int numThreads = 30;  // Adjust thread count as needed
//        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
//        updateSheet.setActiveSheetName("SubArea");
//
//        List<Callable<Void>> tasks = new ArrayList<>();
//
//        for (Map<String, String> subAreaData : subAreaMapList) {
//            final Map<String, String> currentSubArea = subAreaData;
//
//            tasks.add(() -> {
//                int maxRetries = 5;
//                int attempt = 0;
//                boolean success = false;
//
//                while (attempt < maxRetries && !success) {
//                    attempt++;
//                    try {
//                        // ------------------ Check before create ------------------
//                        Location5Ids ids = new CommonGetAPI().getCityPincodeWardId(
//                                currentSubArea.get("OLT"),
//                                currentSubArea.get("ROAD_NAME"),
//                                currentSubArea.get("FAT No.")
//                        );
//
//                        if (ids == null) {
//                            Utility.printLog(logFileName, logModuleName, "Skipped",
//                                    "Location IDs not found. SubArea skipped: " + currentSubArea);
//                            break;
//                        }
//
//                        boolean exists = checkSubAreaExists(ids, currentSubArea.get("SubArea"));
//                        if (!exists) {
//                            // Call the actual creation method (not recursive)
//                            createSubAreaClass(currentSubArea);
//                            Utility.printLog(logFileName, logModuleName, "Created",
//                                    "SubArea created successfully: " + currentSubArea);
//                        } else {
//                            Utility.printLog(logFileName, logModuleName, "Duplicate",
//                                    "SubArea already exists: " + currentSubArea);
//                        }
//
//                        success = true; // exit retry loop
//
//                    } catch (Exception e) {
//                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
//                                "Error creating SubArea: " + e.getMessage());
//
//                        if (attempt < maxRetries) {
//                            try {
//                                // Exponential backoff: 2s, 4s, 8s, 16s, 32s
//                                Thread.sleep((long) (2000L * Math.pow(2, attempt - 1)));
//                            } catch (InterruptedException ie) {
//                                Thread.currentThread().interrupt();
//                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted");
//                                break;
//                            }
//                        } else {
//                            Utility.printLog(logFileName, logModuleName, "Failed",
//                                    "SubArea creation failed after " + maxRetries + " attempts: " + currentSubArea);
//                        }
//                    }
//                }
//                return null;
//            });
//        }
//
//        try {
//            executorService.invokeAll(tasks);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//            try {
//                if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
//                    executorService.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executorService.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//            // Update Excel after all tasks complete
//            ReadWriteExcelFile rw = new ReadWriteExcelFile();
//            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);
//        }
//    }
//
//
//    // ------------------ Excel Reader + Duplicate Check ------------------
//    public List<Map<String, String>> readSubAreaList() {
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
//                    String key = (olt + "|" + fatNo + "|" + address + "|" + roadName + "|" + subArea).toLowerCase();
//
//                    if (seenSubAreas.add(key)) {
//                        Map<String, String> valueMap = new HashMap<>();
//                        valueMap.put("RowIndex", rowIndex);
//                        valueMap.put("BUILDING NAME", subArea);
//                        valueMap.put("ROAD_NAME", roadName);
//                        valueMap.put("ADDRESS", address);
//                        valueMap.put("FAT No.", fatNo);
//                        valueMap.put("OLT", olt);
//
//                        valueMap.put("Units", cellValue.get("UNITS"));
//                        valueMap.put("MDUSFU", cellValue.get("MDU/SFU"));
//                        valueMap.put("Status", "Active");
//                        subAreaMapList.add(valueMap);
//                    } else {
//                        System.out.println("⚠ Skipping duplicate SubArea: " +
//                                olt + "|" + fatNo + "|" + address + "|" + roadName + " - " + subArea);
//                    }
//
//                }
//            }
//        }
//        return subAreaMapList;
//    }
//
//    // ------------------ Helpers ------------------
//    private String safeTrim(String value) {
//        return value == null ? "" : value.trim();
//    }
//
//    // ------------------ JSON Builder with Cache ------------------
//    private String getSubAreaJson(Map<String, String> subArea) {
//
//        try {
//            CommonGetAPI commonGetAPI = new CommonGetAPI();
//            JSONObject subAreaJsonObject = new JSONObject();
//
//            Location5Ids ids = commonGetAPI.getCityPincodeWardId(
//
//                    subArea.get("OLT"),
//                    subArea.get("ROAD_NAME"),
//                    subArea.get("FAT No.")
//            );
//
//            String roadName = subArea.get("ROAD_NAME");
//            String fatNo = subArea.get("FAT No.");
//
//            if (ids == null) {
//                throw new Exception("Location5Ids not found for provided names");
//            }
//            int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME); // here i ahve add lower bcz  there is need lower case
//            // ✅ cached lookups
//            int pincodeId = getCachedPincode(roadName);
//            if (pincodeId != 0) {
//                String areaPinId = fatNo + pincodeId;
//                String details = getCachedMasterDetails(areaPinId, fatNo);
//
//                if (details != null) {
//                    String[] data = details.split(":");
//                    int countryId = Integer.parseInt(data[2]);
//                    int stateId = Integer.parseInt(data[3]);
//
//                    subAreaJsonObject.put("name", subArea.get("BUILDING NAME"));
//                    subAreaJsonObject.put("status", "Active");
//                    subAreaJsonObject.put("countryId", countryId);
//                    subAreaJsonObject.put("cityId", ids.getCityId());
//                    subAreaJsonObject.put("stateId", stateId);
//                    subAreaJsonObject.put("areaId", ids.getWardId());
//                    subAreaJsonObject.put("isDeleted", false);
//                    subAreaJsonObject.put("mvnoId", mvnoId);
//                    subAreaJsonObject.put("buId", JSONObject.NULL);
//                }
//            }
//            return subAreaJsonObject.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "{SubAreaJsonObject Body Null}";
//        }
//    }
//
//
//    //------------------Cache Helpers------------------
//    private int getCachedPincode(String roadName) {
//        return pincodeCache.computeIfAbsent(roadName, rn -> commonGetAPI.getPincodeId(rn));
//    }
//
//    private String getCachedMasterDetails(String areaPinId, String area) {
//        return masterDetailsCache.computeIfAbsent(areaPinId,
//                key -> commonGetAPI.getMasterDetailsByAreaNameFindWithPincode(areaPinId, area));
//    }
//
//    // ------------------ Helper: Check if SubArea Exists ------------------
//    private boolean checkSubAreaExists(Location5Ids ids, String subAreaName) {
//        try {
//            // TODO: Replace with actual API call
//            // Example:
//            // JSONObject response = httpGet("api/ward/list?cityId=" + ids.getCityId());
//            // return response contains subAreaName;
//
//            return false; // assume not exist for now
//        } catch (Exception e) {
//            Utility.printLog(logFileName, logModuleName, "Error",
//                    "Error checking SubArea existence: " + subAreaName + " | " + e.getMessage());
//            return false;
//        }
//    }
//}

//=======
////package masterdata;
////
////import java.util.*;
////import java.util.concurrent.*;
////
////import org.json.JSONObject;
////
////import api.ReadData;
////import api.RestExecution;
////import commons.CommonGetAPI;
////import temp.UpdateSheet;
////import utility.Constant;
////import utility.ProductUtility;
////import utility.ReadWriteExcelFile;
////import utility.Utility;
////
////public class SubAreaClass extends RestExecution {
////
////    private static final String logFileName = "masterdata.log";
////    private static final String logModuleName = "SubArea";
////    private UpdateSheet updateSheet = new UpdateSheet();
////
////    // ✅ Cache objects to avoid repeated expensive calls
////    private final CommonGetAPI commonGetAPI = new CommonGetAPI();
////    private final Map<String, Integer> pincodeCache = new ConcurrentHashMap<>();
////    private final Map<String, String> masterDetailsCache = new ConcurrentHashMap<>();
////
////    // ------------------ API Call ------------------
////    private void createSubAreaClass(Map<String, String> subArea) {
////        try {
////            String apiURL = getAPIURL("SavbillCommonGateway/subarea/save");
////            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
////
////            String apiBody = getSubAreaJson(subArea);
////            if (apiBody == null) {
////                Utility.printLog(logFileName, logModuleName, "Skipped",
////                        "Skipping subarea creation due to missing location IDs or data: " + subArea);
////                return;
////            }
////
////            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);
////            JSONObject JSONResponseBody = httpPostFormDatabui(apiURL, apiBody);
////            String response = JSONResponseBody.toString(4);
////            Utility.printLog(logFileName, logModuleName, "Response", response);
////
////            String subAreaName = subArea.get("BUILDING NAME");
////            ProductUtility.printResponse(JSONResponseBody, logModuleName, subAreaName);
////
////            handleResponse(JSONResponseBody, subAreaName, subArea.get("RowIndex"));
////        } catch (Exception e) {
////            Utility.printLog(logFileName, logModuleName, "Error", e.getMessage());
////        }
////    }
////
////    private void handleResponse(JSONObject response, String subAreaName, String rowIndex) {
////        int status = response.getInt("responseCode");
////        String migrationStatus = "Initial";
////        String migrationDetail = "Initial";
////
////        if (!response.has("ERROR")) {
////            if (status == 200) {
////                String message = response.getString("responseMessage") + " - " + subAreaName;
////                migrationStatus = "Success";
////                migrationDetail = message;
////            } else if (status == 406) {
////                String error = response.getString("responseMessage") + " - " + subAreaName;
////                migrationStatus = "Already Exists";
////                migrationDetail = error;
////            } else {
////                String message = response.get("ERROR") + " - " + subAreaName;
////                migrationStatus = "Error";
////                migrationDetail = message;
////            }
////        } else {
////            String message = response.get("ERROR") + " - " + subAreaName;
////            migrationStatus = "Error";
////            migrationDetail = message;
////        }
////
////        String columnAndValue = "MigrationStatus:" + migrationStatus + "#" + "MigrationDetail:" + migrationDetail;
////        updateSheet.setRowList(rowIndex, columnAndValue);
////    }
////
////    // ------------------ Parallel SubArea Creator with Retry ------------------
////    public void createSubAreaClass(List<Map<String, String>> subAreaMapList) {
////        int numThreads = 30;  // Adjust thread count as needed
////        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
////
////        updateSheet.setActiveSheetName("SubArea");
////
////        List<Callable<Void>> tasks = new ArrayList<>();
////
////        for (Map<String, String> subAreaData : subAreaMapList) {
////            final Map<String, String> currentSubArea = subAreaData;
////
////            tasks.add(() -> {
////                int maxRetries = 5;
////                int attempt = 0;
////                boolean success = false;
////
////                while (attempt < maxRetries && !success) {
////                    attempt++;
////                    try {
////                        // ------------------ Check before create ------------------
////                        Location5Ids ids = new CommonGetAPI().getCityPincodeWardId(
////                                currentSubArea.get("OLT"),
////                                currentSubArea.get("ROAD_NAME"),
////                                currentSubArea.get("FAT No.")
////                        );
////
////                        if (ids == null) {
////                            Utility.printLog(logFileName, logModuleName, "Skipped",
////                                    "Location IDs not found. SubArea skipped: " + currentSubArea);
////                            break;
////                        }
////
////                        boolean exists = checkSubAreaExists(ids, currentSubArea.get("SubArea"));
////                        if (!exists) {
////                            // Call the actual creation method (not recursive)
////                            createSubAreaClass(currentSubArea);
////                            Utility.printLog(logFileName, logModuleName, "Created",
////                                    "SubArea created successfully: " + currentSubArea);
////                        } else {
////                            Utility.printLog(logFileName, logModuleName, "Duplicate",
////                                    "SubArea already exists: " + currentSubArea);
////                        }
////
////                        success = true; // exit retry loop
////
////                    } catch (Exception e) {
////                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
////                                "Error creating SubArea: " + e.getMessage());
////
////                        if (attempt < maxRetries) {
////                            try {
////                                // Exponential backoff: 2s, 4s, 8s, 16s, 32s
////                                Thread.sleep((long) (2000L * Math.pow(2, attempt - 1)));
////                            } catch (InterruptedException ie) {
////                                Thread.currentThread().interrupt();
////                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted");
////                                break;
////                            }
////                        } else {
////                            Utility.printLog(logFileName, logModuleName, "Failed",
////                                    "SubArea creation failed after " + maxRetries + " attempts: " + currentSubArea);
////                        }
////                    }
////                }
////                return null;
////            });
////        }
////
////        try {
////            executorService.invokeAll(tasks);
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////            Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
////        } finally {
////            executorService.shutdown();
////            try {
////                if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
////                    executorService.shutdownNow();
////                }
////            } catch (InterruptedException e) {
////                executorService.shutdownNow();
////                Thread.currentThread().interrupt();
////            }
////            // Update Excel after all tasks complete
////            ReadWriteExcelFile rw = new ReadWriteExcelFile();
////            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);
////        }
////    }
////
////
////    // ------------------ Excel Reader + Duplicate Check ------------------
////    public List<Map<String, String>> readSubAreaList() {
////
////        String sheetName = "SubArea";
////        ReadData readData = new ReadData();
////        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);
////
////        List<Map<String, String>> subAreaMapList = new ArrayList<>();
////        Set<String> seenSubAreas = new HashSet<>();
////
////        for (Map<String, String> cellValue : sheetMap) {
////            String subArea = safeTrim(cellValue.get("BUILDING NAME"));
////            String roadName = safeTrim(cellValue.get("ROAD_NAME"));
////            String address = safeTrim(cellValue.get("ADDRESS"));
////            String fatNo = safeTrim(cellValue.get("FAT No."));
////            String olt = safeTrim(cellValue.get("OLT"));
////            String rowIndex = safeTrim(cellValue.get("SNO"));
////            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));
////
////            if ((!subArea.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {
////                if (!subArea.isEmpty() && !roadName.isEmpty() && !address.isEmpty() && !fatNo.isEmpty() && !olt.isEmpty()) {
////                    String key = (olt + "|" + fatNo + "|" + address + "|" + roadName + "|" + subArea).toLowerCase();
////
////                    if (seenSubAreas.add(key)) {
////                        Map<String, String> valueMap = new HashMap<>();
////                        valueMap.put("RowIndex", rowIndex);
////                        valueMap.put("BUILDING NAME", subArea);
////                        valueMap.put("ROAD_NAME", roadName);
////                        valueMap.put("ADDRESS", address);
////                        valueMap.put("FAT No.", fatNo);
////                        valueMap.put("OLT", olt);
////
////                        valueMap.put("Units", cellValue.get("UNITS"));
////                        valueMap.put("MDUSFU", cellValue.get("MDU/SFU"));
////                        valueMap.put("Status", "Active");
////                        subAreaMapList.add(valueMap);
////                    } else {
////                        System.out.println("⚠ Skipping duplicate SubArea: " +
////                                olt + "|" + fatNo + "|" + address + "|" + roadName + " - " + subArea);
////                    }
////
////                }
////            }
////        }
////        return subAreaMapList;
////    }
////
////    // ------------------ Helpers ------------------
////    private String safeTrim(String value) {
////        return value == null ? "" : value.trim();
////    }
////
////    // ------------------ JSON Builder with Cache ------------------
////    private String getSubAreaJson(Map<String, String> subArea) {
////
////        try {
////            CommonGetAPI commonGetAPI = new CommonGetAPI();
////            JSONObject subAreaJsonObject = new JSONObject();
////
////            Location5Ids ids = commonGetAPI.getCityPincodeWardId(
////
////                    subArea.get("OLT"),
////                    subArea.get("ROAD_NAME"),
////                    subArea.get("FAT No.")
////            );
////
////            String roadName = subArea.get("ROAD_NAME");
////            String fatNo = subArea.get("FAT No.");
////
////            if (ids == null) {
////                throw new Exception("Location5Ids not found for provided names");
////            }
////            int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME); // here i ahve add lower bcz  there is need lower case
////            // ✅ cached lookups
////            int pincodeId = getCachedPincode(roadName);
////            if (pincodeId != 0) {
////                String areaPinId = fatNo + pincodeId;
////                String details = getCachedMasterDetails(areaPinId, fatNo);
////
////                if (details != null) {
////                    String[] data = details.split(":");
////                    int countryId = Integer.parseInt(data[2]);
////                    int stateId = Integer.parseInt(data[3]);
////
////                    subAreaJsonObject.put("name", subArea.get("BUILDING NAME"));
////                    subAreaJsonObject.put("status", "Active");
////                    subAreaJsonObject.put("countryId", countryId);
////                    subAreaJsonObject.put("cityId", ids.getCityId());
////                    subAreaJsonObject.put("stateId", stateId);
////                    subAreaJsonObject.put("areaId", ids.getWardId());
////                    subAreaJsonObject.put("isDeleted", false);
////                    subAreaJsonObject.put("mvnoId", mvnoId);
////                    subAreaJsonObject.put("buId", JSONObject.NULL);
////                }
////            }
////            return subAreaJsonObject.toString();
////        } catch (Exception e) {
////            e.printStackTrace();
////            return "{SubAreaJsonObject Body Null}";
////        }
////    }
////
////
////    //------------------Cache Helpers------------------
////    private int getCachedPincode(String roadName) {
////        return pincodeCache.computeIfAbsent(roadName, rn -> commonGetAPI.getPincodeId(rn));
////    }
////
////    private String getCachedMasterDetails(String areaPinId, String area) {
////        return masterDetailsCache.computeIfAbsent(areaPinId,
////                key -> commonGetAPI.getMasterDetailsByAreaNameFindWithPincode(areaPinId, area));
////    }
////
////    // ------------------ Helper: Check if SubArea Exists ------------------
////    private boolean checkSubAreaExists(Location5Ids ids, String subAreaName) {
////        try {
////            // TODO: Replace with actual API call
////            // Example:
////            // JSONObject response = httpGet("api/ward/list?cityId=" + ids.getCityId());
////            // return response contains subAreaName;
////
////            return false; // assume not exist for now
////        } catch (Exception e) {
////            Utility.printLog(logFileName, logModuleName, "Error",
////                    "Error checking SubArea existence: " + subAreaName + " | " + e.getMessage());
////            return false;
////        }
////    }
////}
//
