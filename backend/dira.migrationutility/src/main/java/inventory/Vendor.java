package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.CommonGetAPI;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;

public class Vendor extends RestExecution {

    private String logFileName = "inventory.log";
    private String logModuleName = "Vendor";

    private final CommonGetAPI commonGetAPI = new CommonGetAPI();

    private void createVendor(Map<String, String> vendorDetails) {

        String row = vendorDetails.get("RowIndex");
        String apiURL = getAPIURL("SavbillInventoryManagement/vendor/save");
        Utility.printLog(logFileName, logModuleName, "Request URL-" + row, apiURL);

        String apiBody = getVendorJson(vendorDetails);
        Utility.printLog(logFileName, logModuleName, "Request Body-" + row, apiBody);

        if (!apiBody.equals(null)) {
            JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
            String response = JSONResponseBody.toString(4);
            Utility.printLog(logFileName, logModuleName, "Response-" + row, response);
			
		/*	int status = JSONResponseBody.getInt("responseCode");
			if (status == 200 || status == 406) {
				//ReadWriteExcelFile rw = new ReadWriteExcelFile();
				//rw.setMigrationStatus("Vendor",row);
				UpdateSheet us = new UpdateSheet();
				us.setRowList(row);
			}
		*/
            String vendorName = vendorDetails.get("VendorName");
            ProductUtility.printResponse(JSONResponseBody, logModuleName, vendorName);
        }
    }

    public void createVendor(List<Map<String, String>> vendorMapList) {

        for (int i = 0; i < vendorMapList.size(); i++) {

            Map<String, String> map = new HashMap<String, String>();
            map = vendorMapList.get(i);
            String row = map.get("RowIndex");
            Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, map.toString());
            createVendor(map);
        }
    }

    public List<Map<String, String>> readVendorList() {

        String sheetName = "Vendor";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getInventoryDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();
        List<Map<String, String>> vendorMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String vendorName = safeTrim(cellValue.get("VendorName *"));
            if ((!"".equals(vendorName)) && (vendorName != null)) {

                valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                valuemap.put("VendorName", safeTrim(cellValue.get("VendorName *")));
                valuemap.put("Status", safeTrim(cellValue.get("Status *")));
                vendorMapList.add(valuemap);
            }
        }
        return vendorMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getVendorJson(Map<String, String> vendorDetails) {
        String jsonString = null;

        int mvnoId = commonGetAPI.getStaffId(Constant.STAFF_USERNAME);

        try {
            JSONObject vendorJson = new JSONObject();
            String status = ProductUtility.getStatus(vendorDetails.get("Status"));

            vendorJson.put("name", vendorDetails.get("VendorName"));
            vendorJson.put("status", status);
            vendorJson.put("id", "");
            vendorJson.put("mvnoId", mvnoId);
            vendorJson.put("isDelete", false);  // Use UI naming

            jsonString = vendorJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(jsonString);

        return jsonString;
    }
}

