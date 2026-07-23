package masterdata;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.*;


import utility.ProductUtility;
import utility.Utility;

public class Municipality extends RestExecution {

    private static String logFileName = "masterdata.log";
    private static String logModuleName = "Municipality";
    private UpdateSheet updateSheet = new UpdateSheet();
    int thread_size = Constant.THREAD_POOL_SIZE;


    // ------------------ Single Municipality Creation ------------------
    private void createMunicipality(Map<String, String> muncipality) {

        String apiURL = getAPIURL("SavbillCommonGateway/pincode/save");
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        // Initialize payload or API body
        String apiBody = getMunicipalityJson(muncipality);
        if (apiBody == null) {
            Utility.printLog(logFileName, logModuleName, "Skipped",
                    "Skipping municipality creation due to missing location IDs or data: " + muncipality);
            return;
        }

        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

        JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
        String response = JSONResponseBody.toString(4);
        Utility.printLog(logFileName, logModuleName, "Response", response);

        String muncipalityName = muncipality.get("Municipalties");
        ProductUtility.printResponse(JSONResponseBody, logModuleName, muncipalityName);

        handleResponse(JSONResponseBody, muncipalityName, muncipality.get("RowIndex"));
    }

    //Update migration status column in the sheet.
    //Update Migration details column
    private void handleResponse(JSONObject response, String muncipalityName, String rowIndex) {
        int status = response.getInt("responseCode");
        String migrationStatus = "Initial";
        String migrationDetail = "Initial";

        if (!response.has("ERROR")) {
            if (status == 200) {
                String message = response.getString("responseMessage") + " - " + muncipalityName;
                migrationStatus = "Success";
                migrationDetail = message;
            } else if (status == 406) {
                String error = response.getString("responseMessage") + " - " + muncipalityName;
                migrationStatus = "Already Exists";
                migrationDetail = error;
            } else {
                String message = response.get("ERROR") + " - " + muncipalityName;
                migrationStatus = "Error";
                migrationDetail = message;
            }
        } else {
            String message = response.get("ERROR") + " - " + muncipalityName;
            migrationStatus = "Error";
            migrationDetail = message;
        }

        String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
        updateSheet.setRowList(rowIndex, columnAndValue);
    }

    //Multithreding is used to create municipality.
    // ------------------ Parallel Municipality Creator with Retry ------------------
    public void createMunicipality(List<Map<String, String>> municipalitiesMapList) {

        int numThreads = 18;  // Adjust thread count as needed
        ExecutorService executorService = Executors.newFixedThreadPool(thread_size);

        List<Callable<Void>> tasks = new ArrayList<>();
        updateSheet.setActiveSheetName("Municipalties");

        for (Map<String, String> municipal : municipalitiesMapList) {
            final Map<String, String> currentMunicipal = municipal; // Capture current item
            tasks.add(() -> {
                int maxRetries = 5;
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        // ------------------ Check before create ------------------
                        LocationIds ids = new CommonGetAPI().getCountryStateCityId(
                                currentMunicipal.get("Country"),
                                currentMunicipal.get("Province"),
                                currentMunicipal.get("District")
                        );

                        if (ids == null) {
                            Utility.printLog(logFileName, logModuleName, "Skipped",
                                    "Location IDs not found. Municipality skipped: " + currentMunicipal);
                            break;
                        }

                        boolean exists = checkMunicipalityExists(ids, currentMunicipal.get("Municipalties"));
                        if (!exists) {
                            createMunicipality(currentMunicipal);
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Duplicate",
                                    "Municipality already exists: " + currentMunicipal);
                        }


                        success = true;

                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating municipality: " + e.getMessage());

                        if (attempt < maxRetries) {
                            try {
                                Thread.sleep(2000L * attempt); // exponential backoff (2s, 4s, 6s)
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                Utility.printLog(logFileName, logModuleName, "Error", "Retry interrupted");
                                break;
                            }
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Failed",
                                    "Municipality creation failed after " + maxRetries + " attempts: " + currentMunicipal);
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
            ReadWriteExcelFile rw = new ReadWriteExcelFile();
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);
        }
    }

    // ------------------ Read Municipality List ------------------
    public List<Map<String, String>> readMunicipalityList() {
        String sheetName = "Municipalties"; // keep original spelling
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenMunicipalities = new HashSet<>();
        List<Map<String, String>> municipalitiesMapList = new ArrayList<>();
        int countryisEmpty = 0;
        int provinceisEmpty = 0;
        int districtisEmpty = 0;
        int municipalityisEmpty = 0;
        // ✅ Declare counters before the loop (so they don’t reset every time)
        int totalCount = 0;
        int uniqueCount = 0;
        int skippedCount = 0;
        for (Map<String, String> cellValue : sheetMap) {
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

            if ((!municipality.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {

//                if (!municipality.isEmpty() && !district.isEmpty() && !province.isEmpty() && !country.isEmpty()) {

                    String key = (country + "|" + province + "|" + district + "|" + municipality).toLowerCase();
                    totalCount++;
                    if (seenMunicipalities.add(key)) {
                        uniqueCount++;
                        Map<String, String> valueMap = new HashMap<>();
                        valueMap.put("RowIndex", rowIndex);
                        valueMap.put("Municipalties", municipality);
                        valueMap.put("District", district);
                        valueMap.put("Province", province);
                        valueMap.put("Country", country);
                        valueMap.put("Status", status);

                        municipalitiesMapList.add(valueMap);
                    } else {
                        skippedCount++;
                        System.out.println("⚠ Skipping duplicate Municipality: " +
                                country + " | " + province + " | " + district + " - " + municipality);
                    }
//                }
            }
        }

        // ✅ Print summary AFTER processing all rows
        System.out.println("========== Summary ==========");
        System.out.println("Total records processed: " + totalCount);
        System.out.println("Unique records stored:   " + uniqueCount);
        System.out.println("Duplicates skipped:      " + skippedCount);
        System.out.println("CountryisEmpty(Defective) = " + countryisEmpty);
        System.out.println("ProvinceisEmpty(Defective) = " + provinceisEmpty);
        System.out.println("DistrictisEmpty(Defective) = " + districtisEmpty);
        System.out.println("PincodesisEmpty(Defective) = " + municipalityisEmpty);
        System.out.println("✅ Total Sheet Map Count Should Match with Total Count: " + sheetMap.size());
        System.out.println("=============================");

        return municipalitiesMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getMunicipalityJson(Map<String, String> municipality) {
        String jsonString = null;

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();

            // ✅ Fetch all 3 IDs (auto-loaded from CommonGetAPI cache)
            LocationIds ids = commonGetAPI.getCountryStateCityId(
                    municipality.get("Country"),
                    municipality.get("Province"),
                    municipality.get("District")
            );

            if (ids == null) {
                throw new Exception("LocationIds not found for provided names");
            }

            String status = ProductUtility.getStatus(municipality.get("Status"));

            JSONObject municipalityJson = new JSONObject();
            municipalityJson.put("pincode", municipality.get("Municipalties"));
            municipalityJson.put("cityId", ids.getCityId());
            municipalityJson.put("stateId", ids.getStateId());
            municipalityJson.put("countryId", ids.getCountryId());
            municipalityJson.put("status", status);

            jsonString = municipalityJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
            jsonString = null;
        }

        return jsonString;
    }


    // ------------------ Helper: Check if Municipality Exists ------------------
    private boolean checkMunicipalityExists(LocationIds ids, String municipalityName) {
        try {
            // TODO: Replace this with actual API call or map lookup
            // Example:
            // JSONObject response = httpGet("api/muncipality/list?cityId=" + ids.getCityId());
            // return response contains municipalityName;

            // For now, assume it does not exist
            return false;

        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error",
                    "Error checking municipality existence: " + municipalityName + " | " + e.getMessage());
            return false;
        }
    }

}


//    private String getMunicipalityJson(Map<String, String> muncipality) {
//
//		String jsonString = null;
//
//		try {
//
//			JSONObject municipalityJson = new JSONObject();
//			CommonGetAPI commonGetAPI = new CommonGetAPI();
//
//			int districtId = commonGetAPI.getDistrictId(muncipality.get("District"));
//			int provinceId = commonGetAPI.getProvinceId(muncipality.get("Province"));
//			int countryId = commonGetAPI.getCountryId(muncipality.get("Country"));
//			String status = ProductUtility.getStatus(muncipality.get("Status"));
//
//			municipalityJson.put("pincode", muncipality.get("Municipalties"));
//			municipalityJson.put("cityId", districtId);
//			municipalityJson.put("stateId", provinceId);
//			municipalityJson.put("countryId", countryId);
//			municipalityJson.put("status", status);
//
//			jsonString = municipalityJson.toString();
//
//		} catch (Exception e) {
//			jsonString = null;
//			e.printStackTrace();
//		}
//		System.out.println(jsonString);
//		return jsonString;
//	}
	
	

