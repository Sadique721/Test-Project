package ticketsystem;

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

public class SubProblemDomain extends RestExecution {
	
	private static String logFileName = "ticketdata.log";
	private static String logModuleName = "SubProblemDomain";

	private void createSubProblemDomain(Map<String, String> subProblemDomain) {

		String apiURL = getAPIURL("TicketManagement/ticketReasonSubCategory/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getSubProblemDomainJson(subProblemDomain);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String subProblemDomainName = subProblemDomain.get("SubProblemDomainName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, subProblemDomainName);
		
	}

	public void createSubProblemDomain(List<Map<String, String>> subProblemDomainMapList) {
		
		for (int i = 0; i < subProblemDomainMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = subProblemDomainMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createSubProblemDomain(map);
		}
	}

	
	public List<Map<String, String>> readSubProblemDomainList() {
		
		String sheetName = "SubProblemDomain";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getTicketDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> subProblemDomainMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String subProblemDomainName = safeTrim(cellValue.get("SubProblemDomainName"));
			if (!"".equals(subProblemDomainName)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("SubProblemDomainName", safeTrim(cellValue.get("SubProblemDomainName")));
				valuemap.put("ParentCategory", safeTrim(cellValue.get("ParentCategory")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				valuemap.put("Reason", safeTrim(cellValue.get("Reason")));
				valuemap.put("TATMappingList", safeTrim(cellValue.get("TATMappingList")));
				subProblemDomainMapList.add(valuemap);
			}
		}
		return subProblemDomainMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


    private String getSubProblemDomainJson(Map<String, String> subProblemDomain) {

        String jsonString = null;

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject subProblemDomainJson = new JSONObject();
            String status = ProductUtility.getStatus(subProblemDomain.get("Status"));

            // 1. subCategoryName
            subProblemDomainJson.put("subCategoryName", subProblemDomain.get("SubProblemDomainName"));

            // 2. ticketSubCategoryGroupReasonMappingList
            List<JSONObject> ticketSubCategoryGroupReasonMappingList = new ArrayList<>();
            String reasons = subProblemDomain.get("Reason");
            String tempReasons[] = reasons.split(",");
            for (int i = 0; i < tempReasons.length; i++) {
                JSONObject reasonsJson = new JSONObject();
                reasonsJson.put("reason", tempReasons[i]);
                reasonsJson.put("ticketReasonSubCategoryId", "");
                ticketSubCategoryGroupReasonMappingList.add(reasonsJson);
            }
            subProblemDomainJson.put("ticketSubCategoryGroupReasonMappingList", ticketSubCategoryGroupReasonMappingList);

            // 3. ticketSubCategoryTatMappingList
            List<JSONObject> ticketSubCategoryTatMappingList = new ArrayList<>();
            String tatNames = subProblemDomain.get("TATMappingList");
            String[] tempTATNames = tatNames.split(",");
            for (int i = 0; i < tempTATNames.length; i++) {
                List<JSONObject> tatQueryFieldMappingList = null;
                int orderid = i + 1;

                JSONObject tatMappingListJson = new JSONObject();
                tatMappingListJson.put("id", "");
                tatMappingListJson.put("orderid", orderid);
                tatMappingListJson.put("tatQueryFieldMappingList", tatQueryFieldMappingList);

                JSONObject ticketTatMatrixJson = new JSONObject();
                int tatId = commonGetAPI.getTATId(tempTATNames[i]);
                ticketTatMatrixJson.put("id", tatId);
                tatMappingListJson.put("ticketTatMatrix", ticketTatMatrixJson);

                ticketSubCategoryTatMappingList.add(tatMappingListJson);
            }
            subProblemDomainJson.put("ticketSubCategoryTatMappingList", ticketSubCategoryTatMappingList);

            // 4. ticketSubCategoryReasonCategoryMappingList
//            List<JSONObject> ticketSubCategoryReasonCategoryMappingList = new ArrayList<>();
//            String parentCategories1 = subProblemDomain.get("ParentCategory");
//            List<Integer> parentCategoryIdList = commonGetAPI.getReasonCategoryIdList(parentCategories1);
//            for (int i = 0; i < parentCategoryIdList.size(); i++) {
//                int parentCategoryId = parentCategoryIdList.get(i);
//
//                JSONObject parentCategoryJson = new JSONObject();
//                parentCategoryJson.put("ticketReasonCategoryId", parentCategoryId);
//                parentCategoryJson.put("ticketReasonSubCategoryId", "");
//                ticketSubCategoryReasonCategoryMappingList.add(parentCategoryJson);
//            }
//            subProblemDomainJson.put("ticketSubCategoryReasonCategoryMappingList", ticketSubCategoryReasonCategoryMappingList);

            List<JSONObject> ticketSubCategoryReasonCategoryMappingList = new ArrayList<>();

            String parentCategories = subProblemDomain.get("ParentCategory");

            List<Integer> parentCategoryIdList = new ArrayList<>();

            if (parentCategories != null && !parentCategories.trim().isEmpty()) {
                // Split by comma & trim spaces
                String[] categoryArray = parentCategories.split(",");

                for (String categoryName : categoryArray) {
                    categoryName = categoryName.trim(); // remove extra spaces

                    // Convert categoryName → ID from API lookup
                    List<Integer> ids = commonGetAPI.getReasonCategoryIdList(categoryName);

                    if (ids != null) {
                        parentCategoryIdList.addAll(ids); // merge all
                    }
                }
            }

            for (Integer parentCategoryId : parentCategoryIdList) {
                JSONObject parentCategoryJson = new JSONObject();
                parentCategoryJson.put("ticketReasonCategoryId", parentCategoryId);
                parentCategoryJson.put("ticketReasonSubCategoryId", "");
                ticketSubCategoryReasonCategoryMappingList.add(parentCategoryJson);
            }

            subProblemDomainJson.put("ticketSubCategoryReasonCategoryMappingList", ticketSubCategoryReasonCategoryMappingList);



            // 5. status
            subProblemDomainJson.put("status", status);

            // 6. id
            subProblemDomainJson.put("id", "");

            // 7. isDefaultSubProblemDomain
            subProblemDomainJson.put("isDefaultSubProblemDomain", false);

            jsonString = subProblemDomainJson.toString(2); // pretty print
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("Generated JSON: " + jsonString); //check Expected Json here

        return jsonString;
    }



}


