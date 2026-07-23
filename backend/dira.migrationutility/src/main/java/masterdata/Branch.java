package masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class Branch extends RestExecution {

	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Branch";

	private void createBranch(Map<String, String> branchDetails) throws ServiceAreaNotFoundException {

        CommonGetAPI commonGetAPI = new CommonGetAPI();
        List<Integer> serviceAreaIdList = commonGetAPI.getServiceAreaIdList(branchDetails.get("ServiceArea"));

        if (serviceAreaIdList == null || serviceAreaIdList.isEmpty()) {
            Utility.printLog(logFileName, logModuleName,
                    "ERROR", "Service Area not found for Branch: "
                            + branchDetails.get("BranchName")
                            + " | ServiceArea: " + branchDetails.get("ServiceArea"));
            return; // skip this branch only
        }


        String apiURL = getAPIURL("SavbillCommonGateway/branchManagement/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getBranchJson(branchDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String branchName = branchDetails.get("BranchName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, branchName);

	}

    public void createBranch(List<Map<String, String>> branchMapList) {

        for (Map<String, String> map : branchMapList) {
            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());

            try {
                createBranch(map);  // may throw ServiceAreaNotFoundException
            } catch (ServiceAreaNotFoundException e) {
                Utility.printLog(logFileName, logModuleName, "ERROR", e.getMessage());
                // continue with next branch
            } catch (Exception e) {
                Utility.printLog(logFileName, logModuleName,
                        "ERROR | Unexpected error for branch " + map.get("BranchName"), e.getMessage());
            }
        }
    }

/*
    public void createBranch(List<Map<String, String>> branchMapList) {

		for (int i = 0; i < branchMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = branchMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createBranch(map);
		}
	}

 */

	public List<Map<String, String>> readBranchList() {

		String sheetName = "Branch";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getMaterDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> branchMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String branchName = cellValue.get("BranchName");
			if ((!"".equals(branchName)) && (branchName != null)) {

				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("BranchName", safeTrim(cellValue.get("BranchName")));
				valuemap.put("BranchCode", safeTrim(cellValue.get("BranchCode")));
				valuemap.put("ServiceArea", safeTrim(cellValue.get("ServiceArea")));
				valuemap.put("RevenueSharing", safeTrim(cellValue.get("RevenueSharing")));
				valuemap.put("SharingPercentage", safeTrim(cellValue.get("SharingPercentage")));
				valuemap.put("DunningDay", safeTrim(cellValue.get("DunningDay")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				branchMapList.add(valuemap);
			}
		}
		return branchMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

	private String getBranchJson(Map<String, String> branchDetails) {

		String jsonString = null;

		try {

			JSONObject branchJsonObject = new JSONObject();

			String status = ProductUtility.getStatus(branchDetails.get("Status"));
			String branchName = branchDetails.get("BranchName");
			String serviceAreaName = branchDetails.get("ServiceArea");

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			List<Integer> serviceAreaIdList = commonGetAPI.getServiceAreaIdList(serviceAreaName);



			branchJsonObject.put("name", branchName);
			branchJsonObject.put("branch_code", branchDetails.get("BranchCode"));
			branchJsonObject.put("serviceAreaIdsList", serviceAreaIdList);

			String revenueSharing = branchDetails.get("RevenueSharing");

			if (revenueSharing.equalsIgnoreCase("Yes")) {
				branchJsonObject.put("revenue_sharing", true);
				String sharingPercentage = branchDetails.get("SharingPercentage");
				branchJsonObject.put("sharing_percentage", sharingPercentage);
			} else if (revenueSharing.equalsIgnoreCase("No")) {
				branchJsonObject.put("revenue_sharing", false);
				branchJsonObject.put("sharing_percentage", JSONObject.NULL);
			}

			branchJsonObject.put("dunningDays", branchDetails.get("DunningDay"));
			branchJsonObject.put("status", status);

			jsonString = branchJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

    public class ServiceAreaNotFoundException extends Exception {
        public ServiceAreaNotFoundException(String message) {
            super(message);
        }
    }


}
