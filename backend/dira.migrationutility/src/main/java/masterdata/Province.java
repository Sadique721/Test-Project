package masterdata;

import java.util.*;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class Province extends RestExecution {

	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Province";

	public void createProvince(Map<String, String> provinceDetails) {

		String apiURL = getAPIURL("SavbillCommonGateway/state");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getProvinceJson(provinceDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String province = provinceDetails.get("Province");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, province);

	}

	public void createProvince(List<Map<String, String>> provinceMapList) {

		for (int i = 0; i < provinceMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = provinceMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createProvince(map);
		}
	}

//	public List<Map<String, String>> readProvinceList() {
//
//		String sheetName = "Province";
//		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
//		ReadData readData = new ReadData();
//		sheetMap = readData.getMaterDataSheet(sheetName);
//
//		Map<String, String> cellValue = new HashMap<String, String>();
//		List<Map<String, String>> provinceMapList = new ArrayList<Map<String, String>>();
//
//		for (int i = 0; i < sheetMap.size(); i++) {
//
//			Map<String, String> valuemap = new HashMap<String, String>();
//			cellValue = sheetMap.get(i);
//
//			String province = cellValue.get("Province");
//			if ((!"".equals(province)) && (province != null)) {
//
//				valuemap.put("RowIndex", cellValue.get("RowIndex"));
//
//				valuemap.put("Province", cellValue.get("Province"));
//				valuemap.put("Country", cellValue.get("Country"));
//				valuemap.put("Status", cellValue.get("Status"));
//			//	valuemap.put("Latitude", cellValue.get("Latitude"));
//				provinceMapList.add(valuemap);
//			}
//		}
//		return provinceMapList;
//	}

    //====================================

    //The duplicate province will be skipped
    public List<Map<String, String>> readProvinceList() {
        String sheetName = "Province";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        Set<String> seenCountryProvince = new HashSet<>();
        List<Map<String, String>> provinceMapList = new ArrayList<>();

        for (Map<String, String> cellValue : sheetMap) {
            String country = cellValue.get("Country");
            String province = cellValue.get("Province");

            if (province != null && !province.trim().isEmpty() &&
                    country != null && !country.trim().isEmpty()) {

                // Create a unique key: country + "|" + province
                String key = country.trim().toLowerCase() + "|" + province.trim().toLowerCase();

                if (seenCountryProvince.add(key)) {  // add() returns false if duplicate
                    Map<String, String> valueMap = new HashMap<>();
                    valueMap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                    valueMap.put("Province", province);
                    valueMap.put("Country", country);
                    valueMap.put("Status", safeTrim(cellValue.get("Status")));
                    provinceMapList.add(valueMap);
                } else {
                    System.out.println("⚠ Skipping duplicate Province for Country: "
                            + country + " - " + province);
                }
            }
        }

        return provinceMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }



    private String getProvinceJson(Map<String, String> provinceDetails) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject statePojoJsonObject = new JSONObject();
			JSONObject countryPojoJsonObject = new JSONObject();

			String provinceName = provinceDetails.get("Province");
			String countryName = provinceDetails.get("Country");
			
			int countryId = commonGetAPI.getCountryId(countryName);

			String status = ProductUtility.getStatus(provinceDetails.get("Status"));
			//String latitude= provinceDetails.get("Latitude");
			statePojoJsonObject.put("name", provinceName);
			statePojoJsonObject.put("status", status);

			countryPojoJsonObject.put("name", countryName);
			countryPojoJsonObject.put("id", countryId);
			countryPojoJsonObject.put("status", "Active");
			//statePojoJsonObject.put("latitude", latitude);
			statePojoJsonObject.put("countryPojo", countryPojoJsonObject);

			jsonString = statePojoJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}