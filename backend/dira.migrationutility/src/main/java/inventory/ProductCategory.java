package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class ProductCategory extends RestExecution {
	
	private String logFileName = "inventory.log";
	private String logModuleName = "ProductCategory";

	private void createProductCategory(Map<String, String> productCategoryDetails) {

		String apiURL = "SavbillInventoryManagement/productCategory/save";
		apiURL = getAPIURL(apiURL);
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getProductCategoryJson(productCategoryDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String pcName = productCategoryDetails.get("Name");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, pcName);
		}
	}

	public void createProductCategory(List<Map<String, String>> productCategoryMapList) {

		for (int i = 0; i < productCategoryMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = productCategoryMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createProductCategory(map);
		}
	}

	public List<Map<String, String>> readUniqueProductCategoryList() {

		String sheetName = "ProductCategory";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);
		
		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> productCategoryMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = safeTrim(cellValue.get("Name *"));
			if ((!"".equals(name)) && (name != null)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("Name", safeTrim(cellValue.get("Name *")));
				valuemap.put("ProductId", safeTrim(cellValue.get("ProductId")));
				valuemap.put("UOM", safeTrim(cellValue.get("UOM *")));
				valuemap.put("HasMac", safeTrim(cellValue.get("HasMac")));
				valuemap.put("HasSerial", safeTrim(cellValue.get("HasSerial")));
				valuemap.put("HasTrackable", safeTrim(cellValue.get("HasTrackable")));
				valuemap.put("HasPort", safeTrim(cellValue.get("HasPort")));
				valuemap.put("HasCas", safeTrim(cellValue.get("HasCAS")));
				valuemap.put("DTVCategory", safeTrim(cellValue.get("DTVCategory")));
				valuemap.put("Type", safeTrim(cellValue.get("Type *")));
				valuemap.put("DeviceType", safeTrim(cellValue.get("DeviceType")));
				valuemap.put("Status", safeTrim(cellValue.get("Status *")));
				productCategoryMapList.add(valuemap);
			}
		}
		return productCategoryMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }



    private String getProductCategoryJson(Map<String, String> productCategoryDetails) {
        String jsonString = null;

        try {
            JSONObject productCategory = new JSONObject();
            String status = ProductUtility.getStatus(productCategoryDetails.get("Status")).toUpperCase();

            productCategory.put("id", JSONObject.NULL);
            productCategory.put("name", productCategoryDetails.get("Name"));
            productCategory.put("productId", JSONObject.NULL); // should be null, not string
            productCategory.put("unit", productCategoryDetails.get("UOM"));
            productCategory.put("type", productCategoryDetails.get("Type"));
            productCategory.put("status", status);

            // empty array
            List<JSONObject> specificationParametersDTOList = new ArrayList<JSONObject>();
            productCategory.put("specificationParametersDTOList", specificationParametersDTOList);

            // boolean flags
            productCategory.put("hasMac", Boolean.valueOf(productCategoryDetails.get("HasMac")));
            productCategory.put("hasSerial", Boolean.valueOf(productCategoryDetails.get("HasSerial")));
            productCategory.put("hasTrackable", Boolean.valueOf(productCategoryDetails.get("HasTrackable")));

            // optional (nullable)
            productCategory.put("hasPort", Boolean.valueOf(productCategoryDetails.get("HasPort")));
            productCategory.put("hasCas", JSONObject.NULL);

            // expiry details (nullable)
            productCategory.put("expiryTime", JSONObject.NULL);
            productCategory.put("expiryTimeUnit", JSONObject.NULL);

            productCategory.put("dtvCategory", JSONObject.NULL);
            productCategory.put("deviceType", productCategoryDetails.get("DeviceType"));

            jsonString = productCategory.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }

//        System.out.println(jsonString);
        return jsonString;
    }


}
