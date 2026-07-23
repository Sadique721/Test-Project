package masterdata;

import java.util.*;
import java.util.concurrent.*;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class ServiceAreaClass extends RestExecution {

    private static final String logFileName = "masterdata.log";
    private static final String logModuleName = "ServiceArea";
    private UpdateSheet updateSheet = new UpdateSheet();
    int thread_size = Constant.THREAD_POOL_SIZE;

    private final CommonGetAPI commonGetAPI = new CommonGetAPI();

    // Core API call to create ServiceArea
    private void createServiceArea(Map<String, String> serviceArea) {
        String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/save");
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        // Build JSON body
        String apiBody = getServiceAreaJson(serviceArea);
        if (apiBody == null) {
            Utility.printLog(logFileName, logModuleName, "Error", "Payload is null for " + serviceArea.get("ServiceArea"));
            return;
        }

        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

        JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
        String response = JSONResponseBody.toString(4);
        Utility.printLog(logFileName, logModuleName, "Response", response);

//        ProductUtility.printResponse(JSONResponseBody, logModuleName, serviceArea.get("ServiceArea"));

        String serviceAreaName = serviceArea.get("ServiceArea");
        ProductUtility.printResponse(JSONResponseBody, logModuleName, serviceAreaName);

        handleResponse(JSONResponseBody, serviceAreaName, serviceArea.get("RowIndex"));
    }

    private void handleResponse(JSONObject response, String serviceAreaName, String rowIndex) {

        int status = response.optInt("responseCode", -1);
        String message = response.optString("responseMessage", "No message");

        String migrationStatus = "Initial";
        String migrationDetail = "Initial";

        if (status == 200) {
            // SUCCESS
            migrationStatus = "Success";
            migrationDetail = message + " - " + serviceAreaName;

        } else if (status == 406 || status == 417) {
            // ALREADY EXISTS
            migrationStatus = "Already Exists";
            migrationDetail = message + " - " + serviceAreaName;

        } else if (status == 400) {
            // PINCODE or VALIDATION ERROR
            migrationStatus = "Validation Error";
            migrationDetail = message + " - " + serviceAreaName;

        } else {
            // ANY OTHER ERROR
            migrationStatus = "Error";
            migrationDetail = message + " - " + serviceAreaName;
        }

        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" +
                "MigrationDetail::" + migrationDetail;

        updateSheet.setRowList(rowIndex, columnAndValue);

    }

    // Retry wrapper for transient API/network errors
    private void retryCreateServiceArea(Map<String, String> serviceArea) {
        int maxRetries = 3;
        int attempt = 0;
        long delay = 2000; // 2 seconds

        while (attempt < maxRetries) {
            try {
                createServiceArea(serviceArea);
                return; // success
            } catch (Exception e) {
                attempt++;
                Utility.printLog(logFileName, logModuleName,
                        "Retry Attempt " + attempt,
                        "Error creating service area " + serviceArea.get("ServiceArea") + ": " + e.getMessage());

                if (attempt >= maxRetries) {
                    Utility.printLog(logFileName, logModuleName, "Failed After Retries",
                            serviceArea.get("ServiceArea"));
                    break;
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                delay *= 2; // exponential backoff
            }
        }
    }

    // Create Service Areas concurrently
    public void createServiceAreaClass(List<Map<String, String>> serviceAreaMapList) {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size);

        updateSheet.setActiveSheetName("ServiceArea");

        List<Callable<Void>> tasks = new ArrayList<>();
        for (Map<String, String> serviceArea : serviceAreaMapList) {
            tasks.add(() -> {
                try {
                    if (!isDuplicateServiceArea(serviceArea)) {
                        retryCreateServiceArea(serviceArea);
                    } else {
                        Utility.printLog(logFileName, logModuleName, "Duplicate Skipped", serviceArea.get("ServiceArea"));
                    }
                } catch (Exception e) {
                    Utility.printLog(logFileName, logModuleName, "Error", "ServiceArea: " + serviceArea.get("ServiceArea") + " - " + e.getMessage());
                }
                return null;
            });
        }

        try {
            List<Future<Void>> futures = executorService.invokeAll(tasks);
            for (Future<Void> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    Utility.printLog(logFileName, logModuleName, "Task Execution Error", e.getCause().getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.out.println("Forcing executor shutdown...");
                    executorService.shutdownNow();

                    if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        System.err.println("Executor did not terminate cleanly.");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Update Excel after all tasks complete
            ReadWriteExcelFile rw = new ReadWriteExcelFile();
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);

        }
    }

    // Read ServiceArea list from Excel
    public List<Map<String, String>> readServiceAreaList() {
        String sheetName = "ServiceArea";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenServiceAreas = new HashSet<>();
        List<Map<String, String>> serviceAreaMapList = new ArrayList<>();
        int serviceAreaisEmpty = 0;
        int districtisEmpty = 0;
        int municipalityisEmpty = 0;
        // ✅ Declare counters before the loop (so they don’t reset every time)
        int totalCount = 0;
        int uniqueCount = 0;
        int skippedCount = 0;
        for (Map<String, String> cellValue : sheetMap) {
            String rowIndex = safeTrim(cellValue.get("RowIndex"));
            String serviceArea = cellValue.get("ServiceArea");
            String district = cellValue.get("District");
            String municipality = cellValue.get("Municipalties"); // Excel spelling
            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));

            if ((serviceArea.isEmpty())) {
                serviceAreaisEmpty++;
                System.out.println("serviceArea rowNumber = " + rowIndex);
            }

            if ((district.isEmpty())) {
                districtisEmpty++;
                System.out.println("district rowNumber = " + rowIndex);
            }

            if ((municipality.isEmpty())) {
                municipalityisEmpty++;
                System.out.println("municipality rowNumber = " + rowIndex);
            }

            if ((!serviceArea.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {
//                if (serviceArea != null && !serviceArea.trim().isEmpty() &&
//                        district != null && !district.trim().isEmpty() &&
//                        municipality != null && !municipality.trim().isEmpty()) {

                String key = serviceArea.trim().toLowerCase() + "|" +
                        district.trim().toLowerCase() + "|" +
                        municipality.trim().toLowerCase();
                totalCount++;
                if (seenServiceAreas.add(key)) {
                    uniqueCount++;
                    Map<String, String> valuemap = new HashMap<>();
                    valuemap.put("RowIndex", cellValue.get("RowIndex"));
                    valuemap.put("ServiceArea", serviceArea);
                    valuemap.put("District", district);
                    valuemap.put("Municipalties", municipality);
                    valuemap.put("Status", cellValue.get("Status"));

                    serviceAreaMapList.add(valuemap);
                } else {
                    skippedCount++;
                    System.out.println("⚠ Skipping duplicate SubArea: " +
                            serviceArea + "|" + district + "|" + municipality );
                }
            }
        }
        // ✅ Print summary AFTER processing all rows
        System.out.println("========== Summary ==========");
        System.out.println("Total records processed: " + totalCount);
        System.out.println("Unique records stored:   " + uniqueCount);
        System.out.println("Duplicates skipped:      " + skippedCount);
        System.out.println("serviceAreaisEmpty(Defective) = " + serviceAreaisEmpty);
        System.out.println("districtisEmpty(Defective) = " + districtisEmpty);
        System.out.println("municipalityisEmpty(Defective) = " + municipalityisEmpty);
        System.out.println("✅ Total Sheet Map Count Should Match with Total Count: " + sheetMap.size());
        System.out.println("=============================");
//        System.out.println(serviceAreaMapList);
        return serviceAreaMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // Generate Service Area JSON with all pincodes included
    public String getServiceAreaJson(Map<String, String> serviceArea) {
        JSONObject serviceAreaJson = new JSONObject();
        List<Integer> pincodesMultiple = new ArrayList<>();
        List<Integer> locationIds = new ArrayList<>();
        List<Integer> polygonList = new ArrayList<>();
        List<Integer> mvnoIds = new ArrayList<>();

        List<String> missingPins = new ArrayList<>();

        try {
            String cityName = serviceArea.get("District").trim();
            String pincodeName = serviceArea.get("Municipalties").trim();
            String status = ProductUtility.getStatus(serviceArea.get("Status"));
            int districtId = commonGetAPI.getDistrictId(cityName);

            if (pincodeName != null && !pincodeName.trim().isEmpty()) {
                String[] temp = pincodeName.split(",");
                for (String pin : temp) {
//                    Location6Ids ids = commonGetAPI.getCityPincodeList(cityName, (pin).trim().toLowerCase()); // simplified method
                    int pincodeId = commonGetAPI.getPincodeIdwithCityandPincodeNameList(cityName, pin.trim());

                    pincodesMultiple.add(pincodeId);

//                    if (ids != null) {
//                        for (int pid : ids.getPincodeIds()) {
//                            if (!pincodesMultiple.contains(pid)) pincodesMultiple.add(pid);
////                            System.out.println(pincodesMultiple);
//                        }
//                    }
                }
            }

//            if (pincodeName != null && !pincodeName.trim().isEmpty()) {
//                String[] temp = pincodeName.split(",");
//                for (String pin : temp) {
//                    pin = pin.trim().toLowerCase();
//                    Location6Ids ids = commonGetAPI.getCityPincodeList(cityName, pin); // your selective method
//
//                    if (ids != null) {
//                        for (int pid : ids.getPincodeIds()) {
//                            if (!pincodesMultiple.contains(pid)) {
//                                pincodesMultiple.add(pid);
//                            }
//                        }
//                    } else {
//                        // Pin not found — add to missing list
//                        missingPins.add(pin);
//                    }
//                }
//            }

//            Location6Ids cityLocation = commonGetAPI.getCityPincodeList(cityName, "");

            serviceAreaJson.put("name", serviceArea.getOrDefault("ServiceArea", ""));
            serviceAreaJson.put("siteName", serviceArea.getOrDefault("SiteName", ""));
            serviceAreaJson.put("cityid", districtId);
            serviceAreaJson.put("pincodes", pincodesMultiple);
            serviceAreaJson.put("status", status);
            serviceAreaJson.put("mvnoIds", mvnoIds);
            serviceAreaJson.put("locationIds", locationIds);
            serviceAreaJson.put("polyGoneList", polygonList);
            serviceAreaJson.put("id", "");
            serviceAreaJson.put("lastModifiedById", "");
            serviceAreaJson.put("isDeleted", false);
            serviceAreaJson.put("latitude", "");
            serviceAreaJson.put("longitude", "");
            serviceAreaJson.put("radius", "");
            serviceAreaJson.put("areaid", "");
            serviceAreaJson.put("selectedPincodes", JSONObject.NULL);

            String serviceAreaType = serviceArea.getOrDefault("ServiceAreaType", "public");
            if ("private".equalsIgnoreCase(serviceAreaType)) {
                serviceAreaJson.put("serviceAreaType", "private");
                serviceAreaJson.put("blockNo", serviceArea.getOrDefault("UnitNo", ""));
            } else {
                serviceAreaJson.put("serviceAreaType", "public");
                serviceAreaJson.put("blockNo", "");
            }

            int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);
            serviceAreaJson.put("mvnoId", mvnoId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println(serviceAreaJson);
        return serviceAreaJson.toString();
    }

    private boolean isDuplicateServiceArea(Map<String, String> serviceArea) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/serviceArea/getByName?name=" + serviceArea.get("ServiceArea"));
            JSONObject jsonResponse = httpGet(apiURL);

            if (jsonResponse != null && jsonResponse.has("responseCode")) {
                int status = jsonResponse.getInt("responseCode");
                if (status == 200) {
                    JSONArray dataArr = jsonResponse.optJSONArray("data");
                    return dataArr != null && dataArr.length() > 0;
                }
            }
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Duplication Check Error", e.getMessage());
        }
        return false;
    }
}
