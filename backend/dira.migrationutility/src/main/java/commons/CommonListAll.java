package commons;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.RestExecution;
import utility.Utility;

public class CommonListAll extends RestExecution {

	private String logFileName = "common.log";
	private String logModuleName = "CommonList";
	
	public Map<String, String> getCommonChargeTypeAll() {
		
		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/generic/chargetype");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String chargeTypeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String chargeTypeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(chargeTypeText, chargeTypeValue);
			}
		}
		return commonListMap;
	}
	
	public Map<String, String> getCommonChargeCategoryAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/chargeCategory");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String chargeTypeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String chargeTypeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(chargeTypeText, chargeTypeValue);
			}
		}
		return commonListMap;

	}

	public Map<String, String> getCommonPlanGroupAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/planGroup");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String chargeTypeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String chargeTypeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(chargeTypeText, chargeTypeValue);
			}
		}
		return commonListMap;

	}

	public Map<String, String> getCommonPlanAccessibilityAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/accessibility");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String chargeTypeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String chargeTypeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(chargeTypeText, chargeTypeValue);
			}
		}
		return commonListMap;
	}

	public Map<String, String> getPaymentModeAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/paymentMode");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String paymentModeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String paymentModeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(paymentModeText, paymentModeValue);
			}
		}
		return commonListMap;
	}
	
	
	public Map<String, String> getCommonPlanTypeAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/planType");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String planTypeText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String planTypeValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(planTypeText, planTypeValue);
			}
		}
		return commonListMap;

	}

	
	public Map<String, String> getCommonPlanCategoryAll() {

		Map<String, String> commonListMap = new HashMap<String, String>();
		String apiURL = getAPIURL("SavbillCommonGateway/commonList/planCategory");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String planCategoryText = jsonArray.getJSONObject(i).getString("text").toLowerCase().trim();
				String planCategoryValue = jsonArray.getJSONObject(i).getString("value").trim();
				commonListMap.put(planCategoryText, planCategoryValue);
			}
		}
		return commonListMap;

	}
	
	//*******************************Caching NOT Done**************************************************
	
	public String getCommonPartnerType(String partnerType) {

		String apiURL = getAPIURL("SavbillCommonGateway/commonList/partnerType");
		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		String commonPartnerType = null;

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedChargeType = jsonArray.getJSONObject(i).getString("text");
				if (partnerType.equalsIgnoreCase(receivedChargeType)) {
					commonPartnerType = jsonArray.getJSONObject(i).getString("value");
				}
			}
		}

		if (commonPartnerType == null) {
			System.out.println("Common PaymentMode details not found - " + partnerType);
			Utility.printLog(logFileName, logModuleName, "Common PaymentMode details not found - ", partnerType);
		}
		return commonPartnerType;
	}
	
	public String getCommonActualTime(String paymentMode) {

		String apiURL = getAPIURL("SavbillCommonGateway/commonList/actual_time");

		JSONObject jsonResponse = httpGet(apiURL);
		// String ans = jsonResponse.toString(4);

		int status = jsonResponse.getInt("responseCode");
		String commonPaymentMode = null;

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedChargeType = jsonArray.getJSONObject(i).getString("text");
				if (paymentMode.equalsIgnoreCase(receivedChargeType)) {
					commonPaymentMode = jsonArray.getJSONObject(i).getString("value");
				}
			}
		}

		if (commonPaymentMode == null) {
			System.out.println("Common PaymentMode details not found - " + paymentMode);
			Utility.printLog(logFileName, logModuleName, "Common PaymentMode details not found - ", paymentMode);
		}
		return commonPaymentMode;
	}
	
}
