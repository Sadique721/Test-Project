package staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class Staff extends RestExecution {

    private static final String logFileName = "Staff.log";
    private static final String logModuleName = "Staff";
    private UpdateSheet updateSheet = new UpdateSheet();

    private void createStaff(Map<String, String> staffDetails) {
        try {
            String apiURL = getAPIURL("SavbillCommonGateway/staffuser");
            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

            // Initializing payload or API body
            String APIBody = getStaffJson(staffDetails);
            Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

            JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
            String response = JSONResponseBody.toString(4);
            Utility.printLog(logFileName, logModuleName, "Response", response);

            String staffUserName = staffDetails.get("UserName");
            ProductUtility.printResponse(JSONResponseBody, logModuleName, staffUserName);

            handleResponse(JSONResponseBody, staffUserName, staffDetails.get("RowIndex"));

        } catch (Exception e) {
            Utility.printLog(logFileName, logModuleName, "Error", e.getMessage());
        }

    }

//	public void createStaff(List<Map<String, String>> staffMapList) {
//
//		for (int i = 0; i < staffMapList.size(); i++) {
//
//			Map<String, String> map = new HashMap<String, String>();
//			map = staffMapList.get(i);
//			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//			createStaff(map);
//		}
//	}

    private void handleResponse(JSONObject response, String staffUserName, String rowIndex) {
        int status = response.optInt("status", 0);
        String migrationStatus = "Initial";
        String migrationDetail = "Initial";

        if (status == 200) {
            String message = response.optString("message", "Successfully Created") + " - " + staffUserName;
            migrationStatus = "Successfully Created";
            migrationDetail = message;
        } else if (status == 406) {
            String error = response.optString("responseMessage", "Already Exists") + " - " + staffUserName;
            migrationStatus = "Already Exists";
            migrationDetail = error;
        } else {
            // For other status codes or errors
            String error = response.optString("ERROR", response.optString("message", "Unknown Error")) + " - " + staffUserName;
            migrationStatus = "Error";
            migrationDetail = error;
        }

		String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail;
        updateSheet.setRowList(rowIndex, columnAndValue);
    }


    public void createStaff(List<Map<String, String>> staffMapList) {
//        int numThreads = Math.min(80, staffMapList.size());
        int numThreads = 80;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        updateSheet.setActiveSheetName("Staff"); // Make sure Excel sheet is set

        List<Callable<Void>> tasks = new ArrayList<>();

        for (Map<String, String> staffMap : staffMapList) {
            final Map<String, String> currentStaff = staffMap;

            tasks.add(() -> {
                int maxRetries = 3; // you can adjust retries
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    attempt++;
                    try {
                        Utility.printLog(logFileName, logModuleName, "Sheet Data", currentStaff.toString());

                        createStaff(currentStaff); // your existing method for single staff

                        success = true;

                    } catch (Exception e) {
                        Utility.printLog(logFileName, logModuleName, "Retry " + attempt,
                                "Error creating Staff: " + e.getMessage());

                        if (attempt < maxRetries) {
                            Thread.sleep((long) (2000L * Math.pow(2, attempt - 1))); // exponential backoff
                        } else {
                            Utility.printLog(logFileName, logModuleName, "Failed",
                                    "Staff creation failed after " + maxRetries + " attempts: " + currentStaff);
                        }
                    }
                }
                return null;
            });
        }

        try {
            // Wait for all tasks to finish
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

            // ✅ Update Excel after all tasks complete
            ReadWriteExcelFile rw = new ReadWriteExcelFile();
            rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.MASTERDATA_FILE);
        }
    }


    public List<Map<String, String>> readStaffList() {

        String sheetName = "Staff";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getMaterDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> staffMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String staffUsername = cellValue.get("Username");
            String migrationStatus = safeTrim(cellValue.get("MigrationStatus"));

            if ((!staffUsername.isEmpty()) && (!"Success".equalsIgnoreCase(migrationStatus)) && (!"Already Exists".equalsIgnoreCase(migrationStatus))) {
                if ((!"".equals(staffUsername)) && (staffUsername != null)) {

                    valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                    valuemap.put("UserName", safeTrim(cellValue.get("Username")));
                    valuemap.put("Password", safeTrim(cellValue.get("Password")));
                    valuemap.put("Email", safeTrim(cellValue.get("Email")));
                    valuemap.put("FirstName", safeTrim(cellValue.get("FirstName")));
                    valuemap.put("LastName", safeTrim(cellValue.get("LastName")));
                    valuemap.put("Status", safeTrim(cellValue.get("Status")));
                    valuemap.put("CountryCode", safeTrim(cellValue.get("CountryCode")));
                    valuemap.put("Mobile", safeTrim(cellValue.get("Mobile")));
                    valuemap.put("Roles", safeTrim(cellValue.get("Roles")));
                    valuemap.put("Teams", safeTrim(cellValue.get("Teams")));
                    valuemap.put("ServiceArea", safeTrim(cellValue.get("ServiceArea")));

                    //  BusinessUnit: send null if not provided in Excel
                    String businessUnit = cellValue.get("BusinessUnit");
                    if (businessUnit == null || businessUnit.trim().isEmpty()) {
                        valuemap.put("BusinessUnit", null);
                    } else {
                        valuemap.put("BusinessUnit", businessUnit.trim());
                    }
                    //				valuemap.put("BusinessUnit", cellValue.get("BusinessUnit"));
                    valuemap.put("Partner", safeTrim(cellValue.get("Partner")));
                    valuemap.put("Branch", safeTrim(cellValue.get("Branch")));
                    valuemap.put("HRMSID", safeTrim(cellValue.get("HRMSID")));
                    valuemap.put("ParentStaff", safeTrim(cellValue.get("ParentStaff")));

                    staffMapList.add(valuemap);
                }
            }
        }
        return staffMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


//	private String getStaffJson(Map<String, String> staffDetails) {
//
//		String jsonString = null;
//
//		try {
//
//			CommonGetAPI commonGetAPI = new CommonGetAPI();
//			JSONObject staffJsonObject = new JSONObject();
//			String status = ProductUtility.getStatus(staffDetails.get("Status"));
//
//			staffJsonObject.put("username", staffDetails.get("UserName"));
//			staffJsonObject.put("password", staffDetails.get("Password"));
//			staffJsonObject.put("email", staffDetails.get("Email"));
//
//			staffJsonObject.put("firstname", staffDetails.get("FirstName"));
//			staffJsonObject.put("lastname", staffDetails.get("LastName"));
//			staffJsonObject.put("status", status.toUpperCase());
//
//			staffJsonObject.put("countryCode", "+" + staffDetails.get("CountryCode"));
//			staffJsonObject.put("phone", staffDetails.get("Mobile"));
//			staffJsonObject.put("roleIds", commonGetAPI.getRoleId(staffDetails.get("Roles")));
//			staffJsonObject.put("teamIds", commonGetAPI.getTeamIdList(staffDetails.get("Teams")));
//
//			String serviceArea = staffDetails.get("ServiceArea");
//			staffJsonObject.put("serviceAreaIdsList", commonGetAPI.getServiceAreaIdList(serviceArea));
//
//			String businessUnit = staffDetails.get("BusinessUnit");
//			staffJsonObject.put("businessUnitIdsList", commonGetAPI.getBusinessUnitIdList(businessUnit));
//
//		//	staffJsonObject.put("partnerid", commonGetAPI.getPartnerId(staffDetails.get("Partner")));
//			staffJsonObject.put("partnerid", staffDetails.get("Partner").equalsIgnoreCase("Default")?1:commonGetAPI.getPartnerId(staffDetails.get("Partner")));
//			staffJsonObject.put("branchId", JSONObject.NULL);
//
//			String branch = staffDetails.get("Branch");
//			if (!"".equals(branch)) {
//				int branchId = commonGetAPI.getBranchIdList(staffDetails.get("Branch")).get(0);
//				staffJsonObject.put("branchId", branchId);
//			}
//
//			staffJsonObject.put("hrmsId", staffDetails.get("HRMSID"));
//			staffJsonObject.put("file", JSONObject.NULL);
//
//			String parentStaffName = staffDetails.get("ParentStaff");
//			if (!"".equals(parentStaffName)) {
//				int parentStaffId = getStaffIdLocal(parentStaffName);
//				staffJsonObject.put("parentStaffId", parentStaffId);
//			}
//
//			staffJsonObject.put("mvnoid", JSONObject.NULL);
//			staffJsonObject.put("staffUserServiceMappingList", JSONObject.NULL);
//
//			jsonString = staffJsonObject.toString();
//
//		} catch (Exception e) {
//			jsonString = null;
//			e.printStackTrace();
//		}
//        System.out.println(jsonString);
//		return jsonString;
//	}
//
//	private int getStaffIdLocal(String staffUserName) {
//
//		String apiURL = getAPIURL("SavbillCommonGateway/staffuser/allActive");
//		JSONObject jsonResponse = httpGet(apiURL);
//		int status = jsonResponse.getInt("status");
//		int staffId = 0;
//
//		if (status == 200) {
//			JSONArray jsonArray = jsonResponse.getJSONArray("staffUserlist");
//			for (int i = 0; i < jsonArray.length(); i++) {
//				String receivedStaffUserName = jsonArray.getJSONObject(i).getString("username");
//				if (receivedStaffUserName.equalsIgnoreCase(staffUserName)) {
//					staffId = jsonArray.getJSONObject(i).getInt("id");
//					break;
//				}
//			}
//		}
//
//		if (staffId == 0) {
//			System.out.println("Staff Username details not found - " + staffUserName);
//			Utility.printLog(logFileName, logModuleName, "Staff Username not found - ", staffUserName);
//		}
//
//		return staffId;
//	}

//    private String getStaffJson(Map<String, String> staffDetails) {
//        String jsonString = null;
//
//        try {
//            CommonGetAPI commonGetAPI = new CommonGetAPI();
//            JSONObject staffJsonObject = new JSONObject();
//            String status = ProductUtility.getStatus(staffDetails.get("Status"));
//
//            // Basic user details
//            staffJsonObject.put("username", staffDetails.get("UserName"));
//            staffJsonObject.put("password", staffDetails.get("Password"));
//            staffJsonObject.put("email", staffDetails.get("Email"));
//
//            // Name and status
//            staffJsonObject.put("firstname", staffDetails.get("FirstName"));
//            staffJsonObject.put("lastname", staffDetails.get("LastName"));
//            staffJsonObject.put("status", status.toUpperCase());
//
//            // Country and contact
//            staffJsonObject.put("countryCode", "+" + staffDetails.get("CountryCode"));
//            staffJsonObject.put("phone", Long.parseLong(staffDetails.get("Mobile")));
//
//            // Role & team
//            staffJsonObject.put("roleIds", commonGetAPI.getRoleId(staffDetails.get("Roles")));
//            staffJsonObject.put("teamIds", commonGetAPI.getTeamIdList(staffDetails.get("Teams")));
//
//            // Service areas
//            String serviceArea = staffDetails.get("ServiceArea");
//            staffJsonObject.put("serviceAreaIdsList", commonGetAPI.getServiceAreaIdList(serviceArea));
//
//            // Business units
//            String businessUnit = staffDetails.get("BusinessUnit");
//            staffJsonObject.put("businessUnitIdsList", commonGetAPI.getBusinessUnitIdList(businessUnit));
//
//            // Partner and branch
//            int partnerId = staffDetails.get("Partner").equalsIgnoreCase("Default")
//                    ? 1
//                    : commonGetAPI.getPartnerId(staffDetails.get("Partner"));
//            staffJsonObject.put("partnerid", partnerId);
//
//            staffJsonObject.put("branchId", JSONObject.NULL);
//
//            String branchName = staffDetails.get("Branch").trim();
//            if (branchName != null && !branchName.trim().isEmpty()) {
//                List<Integer> branchIds = commonGetAPI.getBranchIdList(branchName.trim());
//                if (branchIds != null && !branchIds.isEmpty()) {
//                    staffJsonObject.put("branchId", branchIds.get(0));
//                } else {
//                    Utility.printLog(logFileName, logModuleName, "Warning",
//                            "No branch found for: " + branchName + ". Setting branchId as null.");
//                    staffJsonObject.put("branchId", JSONObject.NULL);
//                }
//            } else {
//                staffJsonObject.put("branchId", JSONObject.NULL);
//            }
//
//
//            // HRMS & parent staff
////            staffJsonObject.put("hrmsId", staffDetails.get("HRMSID"));
//            staffJsonObject.put("hrmsId", JSONObject.NULL);
//            staffJsonObject.put("file", JSONObject.NULL);
//
//            String parentStaffName = staffDetails.get("ParentStaff");
//            if (parentStaffName != null && !parentStaffName.isEmpty()) {
//                Integer parentStaffId = getStaffIdLocal(parentStaffName);
//                if (parentStaffId != null) {
//                    staffJsonObject.put("parentStaffId", parentStaffId);
//                } else {
//                    staffJsonObject.put("parentStaffId", JSONObject.NULL);
//                }
//            } else {
//                staffJsonObject.put("parentStaffId", JSONObject.NULL);
//            }
//
//
//            // Optional fields
//            staffJsonObject.put("mvnoid", JSONObject.NULL);
//            staffJsonObject.put("staffUserServiceMappingList", JSONObject.NULL);
//            staffJsonObject.put("tacacsAccessLevelGroup", JSONObject.NULL);
//            staffJsonObject.put("assignableRoleIds", JSONObject.NULL);
//            staffJsonObject.put("department", JSONObject.NULL);
//
//            // ✅ Pretty print the final JSON
//            jsonString = staffJsonObject.toString(2); // 2 = indentation spaces
//
//        } catch (Exception e) {
//            jsonString = null;
//            e.printStackTrace();
//        }
//

    /// /        System.out.println(jsonString);
//        return jsonString;
//    }
    private String getStaffJson(Map<String, String> staffDetails) {
        String jsonString = null;

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject staffJsonObject = new JSONObject();

            String status = ProductUtility.getStatus(staffDetails.get("Status"));

            // Basic user details
            staffJsonObject.put("username", staffDetails.get("UserName"));
            staffJsonObject.put("password", staffDetails.get("Password"));
            staffJsonObject.put("email", staffDetails.get("Email"));

            // Name & status
            staffJsonObject.put("firstname", staffDetails.get("FirstName"));
            staffJsonObject.put("lastname", staffDetails.get("LastName"));
            staffJsonObject.put("status", status.toUpperCase());

            // Country & Phone
            staffJsonObject.put("countryCode", "+" + staffDetails.get("CountryCode"));
            staffJsonObject.put("phone", Long.parseLong(staffDetails.get("Mobile")));

            // Role IDs
            List<Integer> roleIds = commonGetAPI.getRoleId(staffDetails.get("Roles"));
            if (roleIds != null) {
                staffJsonObject.put("roleIds", roleIds);
            } else {
                staffJsonObject.put("roleIds", JSONObject.NULL);
            }

            // Team IDs
            List<Integer> teamIds = commonGetAPI.getTeamIdList(staffDetails.get("Teams"));
            if (teamIds != null) {
                staffJsonObject.put("teamIds", teamIds);
            } else {
                staffJsonObject.put("teamIds", JSONObject.NULL);
            }

            // Service Area IDs
            String serviceArea = staffDetails.get("ServiceArea");
            List<Integer> serviceAreaIds = commonGetAPI.getServiceAreaIdList(serviceArea);
            if (serviceAreaIds != null && !serviceAreaIds.isEmpty()) {
                staffJsonObject.put("serviceAreaIdsList", serviceAreaIds);
            } else {
                staffJsonObject.put("serviceAreaIdsList", JSONObject.NULL);
            }

            // Business Unit IDs (must not send [null])
            String businessUnit = staffDetails.get("BusinessUnit");
            List<Integer> businessUnitIds = commonGetAPI.getBusinessUnitIdList(businessUnit);

            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {

                boolean hasValidId = false;
                for (Integer id : businessUnitIds) {
                    if (id != null) {
                        hasValidId = true;
                        break;
                    }
                }

                if (hasValidId) {
                    staffJsonObject.put("businessUnitIdsList", businessUnitIds);
                } else {
                    staffJsonObject.put("businessUnitIdsList", JSONObject.NULL);
                }

            } else {
                staffJsonObject.put("businessUnitIdsList", JSONObject.NULL);
            }


            // Partner ID
            String partnerName = staffDetails.get("Partner");
            int partnerId;
            if (partnerName != null && partnerName.equalsIgnoreCase("Default")) {
                partnerId = 1;
            } else {
                partnerId = commonGetAPI.getPartnerId(partnerName);
            }
            staffJsonObject.put("partnerid", partnerId);

            // Branch ID (nullable)
            String branchName = staffDetails.get("Branch");
            if (branchName != null && !branchName.trim().isEmpty()) {
                List<Integer> branchIds = commonGetAPI.getBranchIdList(branchName.trim());
                if (branchIds != null && !branchIds.isEmpty()) {
                    staffJsonObject.put("branchId", branchIds.get(0));
                } else {
                    staffJsonObject.put("branchId", JSONObject.NULL);
                }
            } else {
                staffJsonObject.put("branchId", JSONObject.NULL);
            }

            // HRMS + file always null
            staffJsonObject.put("hrmsId", JSONObject.NULL);
            staffJsonObject.put("file", JSONObject.NULL);

            // Parent staff
            String parentStaffName = staffDetails.get("ParentStaff");
            if (parentStaffName != null && !parentStaffName.isEmpty()) {

                Map<String, Object> parentStaffInfo = getStaffIdLocal(parentStaffName);

                Integer parentStaffId = (Integer) parentStaffInfo.get("staffId");
                String parentFullName = (String) parentStaffInfo.get("fullName");

                staffJsonObject.put("parentStaffId", parentStaffId != null ? parentStaffId : JSONObject.NULL);
                staffJsonObject.put("parentStaffName", parentFullName != null ? parentFullName : JSONObject.NULL);

            } else {
                staffJsonObject.put("parentStaffId", JSONObject.NULL);
                staffJsonObject.put("parentStaffName", JSONObject.NULL);
            }


            // Always-null fields
            staffJsonObject.put("mvnoid", JSONObject.NULL);
            staffJsonObject.put("staffUserServiceMappingList", JSONObject.NULL);
            staffJsonObject.put("tacacsAccessLevelGroup", JSONObject.NULL);
            staffJsonObject.put("assignableRoleIds", JSONObject.NULL);
            staffJsonObject.put("department", JSONObject.NULL);

            // Final JSON
            jsonString = staffJsonObject.toString(2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }



//    private Integer getStaffIdLocal(String staffUserName) {
//        String apiURL = getAPIURL("SavbillCommonGateway/staffuser/allActive");
//        JSONObject jsonResponse = httpGet(apiURL);
//        int status = jsonResponse.getInt("status");
//        Integer staffId = null;  // ✅ Use Integer object type
//
//        Integer staffId = null;
//        String fullName = null;
//
//        if (status == 200) {
//            JSONArray jsonArray = jsonResponse.getJSONArray("staffUserlist");
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                JSONObject obj = jsonArray.getJSONObject(i);
//                String receivedStaffUserName = obj.getString("username");
//
//                if (receivedStaffUserName.equalsIgnoreCase(staffUserName)) {
//
//                    staffId = obj.getInt("id");
//
//                    String FullName = obj.optString("fullName", "");
//
//
//                    fullName = (FullName).trim();
//
//                    break;
//                }
//            }
//        }
//
//        if (staffId == null) {
////            System.out.println("❌ Parent Staff Username details not found - " + staffUserName);
//            Utility.printLog(logFileName, logModuleName, "Staff Username not found - ", staffUserName);
//        }
//
//        return staffId;
//    }


    private Map<String, Object> getStaffIdLocal(String staffUserName) {

        String apiURL = getAPIURL("SavbillCommonGateway/staffuser/allActive");
        JSONObject jsonResponse = httpGet(apiURL);

        int status = jsonResponse.getInt("status");

        Integer staffId = null;
        String fullName = null;

        if (status == 200) {
            JSONArray jsonArray = jsonResponse.getJSONArray("staffUserlist");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                String receivedStaffUserName = obj.getString("username");

                if (receivedStaffUserName.equalsIgnoreCase(staffUserName)) {

                    staffId = obj.getInt("id");

                    // fullName from API
                    fullName = obj.optString("fullName", "").trim();

                    break;
                }
            }
        }

        if (staffId == null) {
            Utility.printLog(logFileName, logModuleName,
                    "Staff Username not found - ", staffUserName);
        } else {
            Utility.printLog(logFileName, logModuleName,
                    "Staff Found → ID: " + staffId + ", Full Name: " + fullName,
                    staffUserName);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("staffId", staffId);
        result.put("fullName", fullName);

        return result;
    }


}
