package masterdata;

import java.util.*;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class Country extends RestExecution {

	private static String logFileName = "masterdata.log";
	private static String logModuleName = "Country";

	public void createCountry(Map<String, String> map) {

		String apiURL = getAPIURL("SavbillCommonGateway/country");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getCountryJson(map);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

        //System.out.println(apiBody);

		String country = map.get("Country");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, country);
	}

	public void createCountry(List<Map<String, String>> countryMapList) {

		for (int i = 0; i < countryMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = countryMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createCountry(map);
		}

	}

    public List<Map<String, String>> readCountryList() {
        String sheetName = "Country";
        ReadData readData = new ReadData();
        List<Map<String, String>> sheetMap = readData.getMaterDataSheet(sheetName);

        List<Map<String, String>> countryMapList = new ArrayList<>();
        Set<String> seenCountries = new HashSet<>();

        for (int i = 0; i < sheetMap.size(); i++) {
            Map<String, String> cellValue = sheetMap.get(i);
            String country = cellValue.get("Country");

            if (country != null && !country.trim().isEmpty()) {
                // ✅ Build key for duplicate check
                String key = country.trim().toLowerCase();

                if (!seenCountries.add(key)) {
                    Utility.printLog(logFileName, logModuleName, "Duplicate in Excel skipped", country);
                    continue; // Skip duplicate (case-insensitive)
                }

                // ✅ Store original country in output
                Map<String, String> valueMap = new HashMap<>();
                valueMap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                valueMap.put("Country", safeTrim(country));
                valueMap.put("Status", safeTrim(cellValue.get("Status")));

                countryMapList.add(valueMap);
            }
        }
        return countryMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


    private String getCountryJson(Map<String, String> map) {

		String jsonString = null;

		try {
			JSONObject countryJsonObject = new JSONObject();
			String status = ProductUtility.getStatus(map.get("Status"));
			
			countryJsonObject.put("name", map.get("Country"));
			countryJsonObject.put("status", status);
			countryJsonObject.put("delete", false);
			countryJsonObject.put("isDelete", false);
			
			jsonString = countryJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}
	
	
}