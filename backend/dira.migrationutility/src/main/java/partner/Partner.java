package partner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;

public class Partner extends RestExecution {

	private String logFileName = "Partner.log";
	private String logModuleName = "CreatePartner";

	private void createPartner(Map<String, String> partner) {

		String apiURL = getAPIURL("pms/partner");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getPartnerJson(partner);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("status");
		String partnerName = partner.get("Name");

		if (status == 200) {
			String message = "New Partner is added successfully - " + partnerName;
			System.out.println(message);
			Utility.printLog("execution.log", logModuleName, "Success", message);

		} else if (status == 406) {
			String error = JSONResponseBody.getString("ERROR") + " - " + partnerName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "Already Exist", error);
		} else {
			String error = "Error: " + JSONResponseBody.get("ERROR") + " - " + partnerName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "ERROR", error);
		}
	}

	public void createPartner(List<Map<String, String>> serviceMapList) {

		for (int i = 0; i < serviceMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = serviceMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPartner(map);
		}
	}

	public List<Map<String, String>> readPartnerList() {

		String sheetName = "PartnerDetails";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPartnerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> partnerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = cellValue.get("Name");
			if ((!"".equals(name)) && (name != null)) {
																									
				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("Name", cellValue.get("Name"));
				valuemap.put("PartnerCode", cellValue.get("PartnerCode"));
				valuemap.put("PartnerType", cellValue.get("PartnerType"));
				valuemap.put("TaxName", cellValue.get("TaxName"));
				valuemap.put("Balance", cellValue.get("Balance"));
				valuemap.put("Credit", cellValue.get("Credit"));
				valuemap.put("ParentPartner", cellValue.get("ParentPartner"));

				valuemap.put("CalendarType", cellValue.get("CalendarType"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("CountryCode", cellValue.get("CountryCode"));
				valuemap.put("MobileNumber", cellValue.get("MobileNumber"));
				valuemap.put("Email", cellValue.get("Email"));
				
				valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
				valuemap.put("PAN", cellValue.get("PAN"));
				valuemap.put("CompanyName", cellValue.get("CompanyName"));
				valuemap.put("ContactPersonName", cellValue.get("ContactPersonName"));
				valuemap.put("PlanBundle", cellValue.get("PlanBundle"));
				valuemap.put("CommissionDay", cellValue.get("CommissionDay"));
				valuemap.put("CommissionShareType", cellValue.get("CommissionShareType"));
				valuemap.put("Address1", cellValue.get("Address1"));
				valuemap.put("Address2", cellValue.get("Address2"));
				valuemap.put("Municipalities", cellValue.get("Municipalities"));

				partnerMapList.add(valuemap);
			}
		}
		return partnerMapList;
	}

	private String getPartnerJson(Map<String, String> partnerDetails) {

		String jsonString = null;

		try {
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			CommonList commonList = new CommonList();
			JSONObject partnerJson = new JSONObject();

			String status = ProductUtility.getStatus(partnerDetails.get("Status"));

			// -- Partner Basic Details
			
			partnerJson.put("name", partnerDetails.get("Name"));
			partnerJson.put("prcode", partnerDetails.get("PartnerCode"));
			
			
			String partnerType = partnerDetails.get("PartnerType");
			String commonPartnerType = commonList.getCommonPartnerType(partnerType);
			partnerJson.put("partnerType", commonPartnerType);
			
			int taxId = commonGetAPI.getTaxId(partnerDetails.get("TaxName"));
			partnerJson.put("taxid", taxId);
			partnerJson.put("taxName", JSONObject.NULL);
			

			partnerJson.put("outcomeBalance", Float.valueOf(partnerDetails.get("Balance")));
			partnerJson.put("credit", Float.valueOf(partnerDetails.get("Credit")));
			
			String parentPartner = null;
			partnerJson.put("parentpartnerid", parentPartner);			
			parentPartner = partnerDetails.get("ParentPartner");
			
			if(!"".equals(parentPartner) && parentPartner != null) {				
				int parentPartnerId = commonGetAPI.getPartnerId(parentPartner);
				partnerJson.put("parentpartnerid", parentPartnerId);
				partnerJson.put("parentPartnerName", JSONObject.NULL);
			}
			
		
			 
			partnerJson.put("calendarType", partnerDetails.get("CalendarType"));
			partnerJson.put("status",status.toUpperCase());
			
			// -- Partner Contact Details & Service Area Details
			
			partnerJson.put("countryCode", "+" + partnerDetails.get("CountryCode"));
			partnerJson.put("mobile", partnerDetails.get("MobileNumber"));
			partnerJson.put("email", partnerDetails.get("Email"));
			partnerJson.put("id", JSONObject.NULL);
			partnerJson.put("isDelete", false);
			partnerJson.put("lastbilldate", JSONObject.NULL);
			partnerJson.put("lastModifiedById", JSONObject.NULL);
			partnerJson.put("lastModifiedByName", JSONObject.NULL);
			partnerJson.put("nextbilldate", JSONObject.NULL);
			partnerJson.put("updatedate", JSONObject.NULL);
			partnerJson.put("taxName", JSONObject.NULL);
			
			String serviceAreas = partnerDetails.get("ServiceArea");
			partnerJson.put("serviceAreaIds", commonGetAPI.getServiceAreaIdList(serviceAreas) );
			partnerJson.put("panName", partnerDetails.get("PAN"));
			//sarfraz
			partnerJson.put("bussinessvertical", JSONObject.NULL);
			
			partnerJson.put("cname", partnerDetails.get("CompanyName"));
			partnerJson.put("cpName", partnerDetails.get("ContactPersonName"));
			partnerJson.put("createdate", JSONObject.NULL);
			partnerJson.put("createdByName", JSONObject.NULL);
			//staff id 
			String staffId = String.valueOf(commonGetAPI.getStaffId(Constant.STAFF_USERNAME));
			partnerJson.put("createdById",staffId);
			
			// -- Partner Commission Details
			
			int planGroupId = commonGetAPI.getPartnerPlanGroupId(partnerDetails.get("PlanBundle"));
			partnerJson.put("pricebookId", planGroupId);
			partnerJson.put("commtype", "PRICEBOOK");
			partnerJson.put("pricebookname", JSONObject.NULL);
			
			if (commonPartnerType.equalsIgnoreCase("Franchise")) {
				
				partnerJson.put("commdueday", Integer.parseInt(partnerDetails.get("CommissionDay")));
				partnerJson.put("commissionShareType", partnerDetails.get("CommissionShareType"));
				partnerJson.put("commrelvalue", JSONObject.NULL);
				
			} else if (commonPartnerType.equalsIgnoreCase("LCO")) {
				
				partnerJson.put("commdueday", "31");
				partnerJson.put("commissionShareType", "Revenue");
				partnerJson.put("commrelvalue", JSONObject.NULL);
				
			}
			
			// -- Partner Address Details
			partnerJson.put("addresstype", "Permanent");
			partnerJson.put("address1", partnerDetails.get("Address1"));
			partnerJson.put("address2", partnerDetails.get("Address2"));
			partnerJson.put("branch", JSONObject.NULL);
			
		//	int pincodeId = commonGetAPI.getMunicipalityId(partnerDetails.get("Municipalities"));
		//	partnerJson.put("pincode", pincodeId);
			
		//	String temp = commonGetAPI.getMasterDetailsFromMunicipalityId(pincodeId);
			
			String municipality = partnerDetails.get("Municipalities");
			
			if (!"".equals(municipality)) {

				String temp = commonGetAPI.getMasterDetailsByMunicipalityName(municipality);

				String data[] = temp.split(":");

				int pincodeId = Integer.parseInt(data[0]);
				int cityId = Integer.parseInt(data[1]);
				int stateId = Integer.parseInt(data[2]);
				int countryId = Integer.parseInt(data[3]);

				partnerJson.put("pincode", pincodeId);
				partnerJson.put("city", cityId);
				partnerJson.put("state", stateId);
				partnerJson.put("country", countryId);
				//srfraz
				partnerJson.put("cityName", JSONObject.NULL);
				partnerJson.put("countryName", JSONObject.NULL);
				partnerJson.put("stateName", JSONObject.NULL);
				partnerJson.put("region", JSONObject.NULL);
				partnerJson.put("serviceAreaNameList", JSONObject.NULL);
			}
			
			
			// -- Partner All Null
		
	/*		partnerJson.put("isDelete", false);
			
			String id = null;
			
			String createdById = null;
			String createdByName = null;
			String createdate = null;
			
			String updatedate = null;
			String lastModifiedById = null;
			String lastModifiedByName = null;
			
			partnerJson.put("id", id);
			partnerJson.put("updatedate", updatedate);
			partnerJson.put("lastModifiedById", lastModifiedById);
			partnerJson.put("lastModifiedByName", lastModifiedByName);
			partnerJson.put("createdById", createdById);
			partnerJson.put("createdByName", createdByName);
			partnerJson.put("createdate", createdate);
		*/	
			jsonString = partnerJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

	public List<Integer> getProductCategoryIdList(String productCategory) {

		String apiURL = getAPIURL("productCategory/getAllProductCategoriesByType?Type=customerbind");

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		List<Integer> productCategoryList = new ArrayList<Integer>();

		if (status == 200) {

			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			if (productCategory.equalsIgnoreCase("All")) {
				for (int i = 0; i < jsonArray.length(); i++) {
					productCategoryList.add(jsonArray.getJSONObject(i).getInt("id"));
				}
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					String receivedProductCategoryName = jsonArray.getJSONObject(i).getString("name");
					String productCategoryNameList[] = productCategory.split(",");
					for (int j = 0; j < productCategoryNameList.length; j++) {
						if (receivedProductCategoryName.equalsIgnoreCase(productCategoryNameList[j])) {
							productCategoryList.add(jsonArray.getJSONObject(i).getInt("id"));
							break;
						}
					}
				}
			}

		}

		if (productCategoryList.size() == 0) {
			System.out.println("Product-Category details not found - " + productCategory);
			Utility.printLog(logFileName, logModuleName, "Product-Category details not found - ", productCategory);
		}
		return productCategoryList;
	}

}
