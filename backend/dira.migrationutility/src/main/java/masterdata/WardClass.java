package masterdata;

import java.util.*;
import java.util.concurrent.*;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class WardClass extends RestExecution {

    private static final String logFileName = "masterdata.log";
    private static final String logModuleName = "Ward";
    private UpdateSheet updateSheet = new UpdateSheet();
    int thread_size = Constant.THREAD_POOL_SIZE;

    // ------------------ Single Ward Creation ------------------
    private void createWardClass(Map<String, String> ward) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/area/save");
            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            Utility.printLog(logFileName, logModuleName, "Debug",
                    "Calling getWardJson for ward: " + ward.get("WardName"));
            String apiBody = getWardJson(ward);

            if (apiBody == null) {
                Utility.printLog(logFileName, logModuleName, "Skipped",
                        "Skipping ward creation due to missing location IDs or invalid data: " + ward);
                return;
            }
            Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

            JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
            String response = JSONResponseBody.toString(4);
            Utility.printLog(logFileName, logModuleName, "Response", response);

            String wardName = ward.get("WardName");
            ProductUtility.printResponse(JSONResponseBody, logModuleName, wardName);

            handleResponse(JSONResponseBody, wardName, ward.get("RowIndex"));

        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error", e.getMessage());
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
        updateSheet.setRowList(rowIndex, columnAndValue);
    }

    // ------------------ Parallel Ward Creator with Retry ------------------
    public void createWardClass(List<Map<String, String>> wardMapList) {
        int numThreads = 18;  // Adjust thread count as needed
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size);

        updateSheet.setActiveSheetName("Ward");

        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map<String, String> wardData : wardMapList) {
            final Map<String, String> currentWard = wardData;

            tasks.add(() -> {
                int maxRetries = 5;
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        // ------------------ Check before create ------------------
                        Location4Ids ids = new CommonGetAPI().getCountryStateCityPincodeId(
                                currentWard.get("Country"),
                                currentWard.get("Province"),
                                currentWard.get("District"),
                                currentWard.get("Municipalties")
                        );

                        if (ids == null) {
                            Utility.printLog(logFileName, logModuleName, "Skipped",
                                    "Location IDs not found. Ward skipped: " + currentWard);
                            break;
                        }

                        boolean exists = checkWardExists(ids, currentWard.get("Ward"));
                        if (!exists) {
                            // Call the actual creation method (not recursive)
                            createWardClass(currentWard);
                            Utility.printLog(logFileName, logModuleName, "Created",
                                    "Ward created successfully: " + currentWard);
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Duplicate",
                                    "Ward already exists: " + currentWard);
                        }

                        success = true; // exit retry loop

                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating Ward: " + e.getMessage());

                        if (attempt < maxRetries) {
                            try {
                                // Exponential backoff: 2s, 4s, 8s, 16s, 32s
                                Thread.sleep((long) (2000L * Math.pow(2, attempt - 1)));
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted");
                                break;
                            }
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Failed",
                                    "Ward creation failed after " + maxRetries + " attempts: " + currentWard);
                        }
                    }
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
                if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                    executorService.shutdownNow();
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


    // ------------------ Read Ward List with Duplicate Detection ------------------
    public List<Map<String, String>> readWardList() {
        String sheetName = "Ward";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenWardMunicipalities = new HashSet<>();
        List<Map<String, String>> wardMapList = new ArrayList<>();
        int countryisEmpty = 0;
        int provinceisEmpty = 0;
        int districtisEmpty = 0;
        int municipalityisEmpty = 0;
        int wardisEmpty = 0;
        // ✅ Declare counters before the loop (so they don’t reset every time)
        int totalCount = 0;
        int uniqueCount = 0;
        int skippedCount = 0;

        for (Map<String, String> cellValue : sheetMap) {
            String ward = safeTrim(cellValue.get("WardName"));
            String municipality = safeTrim(cellValue.get("Municipalties"));
            String district = safeTrim(cellValue.get("District"));
            String province = safeTrim(cellValue.get("Province"));
            String country = safeTrim(cellValue.get("Country"));
            String status = safeTrim(cellValue.get("Status"));
            String rowIndex = safeTrim(cellValue.get("RowIndex"));
            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));

            if ((country.isEmpty())) {
                countryisEmpty++;
                    System.out.println("country rowNumber = " + rowIndex);
            }

            if ((province.isEmpty())) {
                provinceisEmpty++;
                System.out.println("province rowNumber = " + rowIndex);
            }

            if ((district.isEmpty())) {
                districtisEmpty++;
                System.out.println("district rowNumber = " + rowIndex);
            }

            if ((municipality.isEmpty())) {
                municipalityisEmpty++;
                System.out.println("municipality rowNumber = " + rowIndex);
            }

            if ((ward.isEmpty())) {
                wardisEmpty++;
                System.out.println("ward rowNumber = " + rowIndex);
            }

            if ((!ward.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {

//                if (!ward.isEmpty() && !municipality.isEmpty() && !district.isEmpty() && !province.isEmpty() && !country.isEmpty()) {

                    String key = (country + "|" + province + "|" + district + "|" + municipality + "|" + ward).toLowerCase();
                    totalCount++;
                    if (seenWardMunicipalities.add(key)) {
                        uniqueCount++;
                        Map<String, String> valueMap = new HashMap<>();
                        valueMap.put("RowIndex", rowIndex);
                        valueMap.put("WardName", ward);
                        valueMap.put("Municipalties", municipality);
                        valueMap.put("District", district);
                        valueMap.put("Province", province);
                        valueMap.put("Country", country);
                        valueMap.put("Status", status);

                        wardMapList.add(valueMap);
                    } else {
                        skippedCount++;
//                        System.out.println("⚠ Skipping duplicate Ward: " +
//                                country + "|" + province + "|" + district + "|" + municipality + " - " + ward);
                    }
//                }
            }
        }
        // ✅ Print summary AFTER processing all rows
        System.out.println("========== Summary ==========");
        System.out.println("Total records processed: " + totalCount);
        System.out.println("Unique records stored:   " + uniqueCount);
        System.out.println("Duplicates skipped:      " + skippedCount);
        System.out.println("countryisEmpty(Defective) = " + countryisEmpty);
        System.out.println("provinceisEmpty(Defective) = " + provinceisEmpty);
        System.out.println("districtisEmpty(Defective) = " + districtisEmpty);
        System.out.println("municipalityisEmpty(Defective) = " + municipalityisEmpty);
        System.out.println("wardisEmpty(Defective) = " + wardisEmpty);
        System.out.println("=============================");
        System.out.println("✅ Total unique wards loaded: " + wardMapList.size());
        System.out.println("✅ Total Sheet Map Count Should Match with Total Count: " + sheetMap.size());

        return wardMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // ------------------ Build JSON Payload for Ward ------------------
    private String getWardJson(Map<String, String> ward) {
        Utility.printLog(logFileName, logModuleName, "Debug",
                "Entered getWardJson for ward: " + ward.get("WardName"));
        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            Location4Ids ids = commonGetAPI.getCountryStateCityPincodeId(
                    ward.get("Country"),
                    ward.get("Province"),
                    ward.get("District"),
                    ward.get("Municipalties")
            );

            if (ids == null) {
                Utility.printLog(logFileName, logModuleName, "Error",
                        "Location4Ids not found for ward: " + ward.get("WardName"));
                return null;
            }

            String status = ProductUtility.getStatus(ward.get("Status"));
            JSONObject wardJsonObject = new JSONObject();

            wardJsonObject.put("name", safeTrim(ward.get("WardName")));
            wardJsonObject.put("pincodeId", ids.getPincodeId());
            wardJsonObject.put("cityId", ids.getCityId());
            wardJsonObject.put("stateId", ids.getStateId());
            wardJsonObject.put("countryId", ids.getCountryId());
            wardJsonObject.put("status", status);

            JSONObject pincodeJsonObject = new JSONObject();
            pincodeJsonObject.put("pincodeid", ids.getPincodeId());
            pincodeJsonObject.put("pincode", safeTrim(ward.get("Municipalties")));
            pincodeJsonObject.put("status", "Active");
            pincodeJsonObject.put("isDeleted", false);

            wardJsonObject.put("pincode", pincodeJsonObject);

            Utility.printLog(logFileName, logModuleName, "Debug",
                    "Successfully built ward JSON for: " + ward.get("WardName"));

            return wardJsonObject.toString();
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error",
                    "getWardJson Exception for ward: " + ward.get("WardName") + " | " + e.getMessage());
            return null;
        }
    }

    // ------------------ Helper: Check if Ward Exists ------------------
    private boolean checkWardExists(Location4Ids ids, String wardName) {
        try {
            // TODO: Replace with actual API call
            // Example:
            // JSONObject response = httpGet("api/ward/list?cityId=" + ids.getCityId());
            // return response contains wardName;

            return false; // assume not exist for now
        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error",
                    "Error checking ward existence: " + wardName + " | " + e.getMessage());
            return false;
        }
    }
}
