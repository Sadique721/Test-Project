package masterdata;

import java.util.*;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class District extends RestExecution {
	
	private static String logFileName = "masterdata.log";
	private static String logModuleName = "District";

	private void createDistrict(Map<String, String> district) {

		String apiURL = getAPIURL("SavbillCommonGateway/city");
		Utility.printLog(logFileName,logModuleName , "Request URL", apiURL);

		String APIBody = getDistrictJson(district);
		Utility.printLog(logFileName,logModuleName , "Request Body", APIBody);
		
		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName,logModuleName , "Response", response);
		
		String districtName = district.get("District");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, districtName);
		
	}

	public void createDistrict(List<Map<String, String>> districtMapList) {
		
		for (int i = 0; i < districtMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = districtMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createDistrict(map);
		}
	}

    public List<Map<String, String>> readDistrictList() {

        String sheetName = "District";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        List<Map<String, String>> districtMapList = new ArrayList<>();
        Set<String> seenCountryProvinceDistrict = new HashSet<>();

        for (int i = 0; i < sheetMap.size(); i++) {
            Map<String, String> cellValue = sheetMap.get(i);

            String country = cellValue.get("Country");
            String province = cellValue.get("Province");
            String district = cellValue.get("District");

            if (country != null && !country.trim().isEmpty() &&
                    province != null && !province.trim().isEmpty() &&
                    district != null && !district.trim().isEmpty()) {

                // Create unique key Country|Province|District
                String key = country.trim().toLowerCase() + "|" +
                        province.trim().toLowerCase() + "|" +
                        district.trim().toLowerCase();

                if (!seenCountryProvinceDistrict.contains(key)) {
                    seenCountryProvinceDistrict.add(key);

                    Map<String, String> valuemap = new HashMap<>();
                    valuemap.put("RowIndex", cellValue.get("RowIndex"));
                    valuemap.put("District", district);
                    valuemap.put("Province", province);
                    valuemap.put("Country", country);
                    valuemap.put("Status", cellValue.get("Status"));

                    districtMapList.add(valuemap);
                } else {
                    System.out.println("⚠ Skipping duplicate: "
                            + country + " - " + province + " - " + district);
                }
            }
        }
        return districtMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


    private String getDistrictJson(Map<String, String> district) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			
			String provinceName = district.get("Province");
			int provinceId = commonGetAPI.getProvinceId(provinceName);
						
			String countryName = district.get("Country");
			int countryId = commonGetAPI.getCountryId(countryName);	
			
			JSONObject districtJsonObject = new JSONObject();
			JSONObject statePojoJsonObject = new JSONObject();
			JSONObject countryPojoJsonObject = new JSONObject();

			String status = ProductUtility.getStatus(district.get("Status"));
			
			districtJsonObject.put("name", district.get("District"));
			districtJsonObject.put("countryName", countryName);
			districtJsonObject.put("countryId", countryId);
			districtJsonObject.put("status", status);

			statePojoJsonObject.put("name", provinceName);
			statePojoJsonObject.put("id", provinceId);
			statePojoJsonObject.put("status", "Active");

			countryPojoJsonObject.put("name", countryName);
			countryPojoJsonObject.put("id", countryId);
			countryPojoJsonObject.put("status", "Active");
			
			statePojoJsonObject.put("countryPojo", countryPojoJsonObject);
			districtJsonObject.put("statePojo", statePojoJsonObject);
			
			jsonString = districtJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}
	
	
}
