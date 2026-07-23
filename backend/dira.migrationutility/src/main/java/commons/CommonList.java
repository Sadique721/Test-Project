package commons;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class CommonList extends RestExecution {

	private String logFileName = "common.log";
	private String logModuleName = "CommonList";
	
	private static Map<String, String> chargeTypeMap = new HashMap<String, String>();
	private static Map<String, String> chargeCategoryMap = new HashMap<String, String>();
	private static Map<String, String> commonPlanGroupMap = new HashMap<String, String>();
	private static Map<String, String> commonPlanTypeMap = new HashMap<String, String>();
	private static Map<String, String> commonPlanCategoryMap = new HashMap<String, String>();
	private static Map<String, String> commonPlanAccessibilityMap = new HashMap<String, String>();
	private static Map<String, String> paymentModeMap = new HashMap<String, String>();
	
	public String getCommonChargeType(String chargeType) {
		
		String commonChargeType = null;
		
		if(chargeTypeMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			chargeTypeMap = commonListAll.getCommonChargeTypeAll();
		} 
		
		commonChargeType = chargeTypeMap.get(chargeType.toLowerCase());
		
		if (commonChargeType == null) {
			System.out.println("Allowed CommonList : " + chargeTypeMap.keySet());
			String message = "CommonList | Chargetype details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, chargeType);
		}
			
		return commonChargeType;
	}
	
	public String getCommonChargeCategory(String chargeCategory) {
		
		String commonChargeCategory = null;
		if(chargeCategoryMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			chargeCategoryMap = commonListAll.getCommonChargeCategoryAll();
		} 
		commonChargeCategory = chargeCategoryMap.get(chargeCategory.toLowerCase());
		
		if (commonChargeCategory == null) {
			System.out.println("Allowed CommonList : " + chargeCategoryMap.keySet());
			String message = "CommonList | ChargeCategory details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, chargeCategory);
		}

		return commonChargeCategory;
	}
	
	public String getCommonPlanGroup(String planGroup) {
			
		String commonPlanGroup = null;
		if(commonPlanGroupMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			commonPlanGroupMap = commonListAll.getCommonPlanGroupAll();
		} 
		commonPlanGroup = commonPlanGroupMap.get(planGroup.toLowerCase());
		
		if (commonPlanGroup == null) {
			System.out.println("Allowed CommonList : " + commonPlanGroupMap.keySet());
			String message = "CommonList | Plan-Group details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, planGroup);
		}

		return commonPlanGroup;
	}
		
	public String getCommonPlanAccessibility(String accessibility) {
		
		String commonPlanAccessibility = null;
		if(commonPlanAccessibilityMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			commonPlanAccessibilityMap = commonListAll.getCommonPlanAccessibilityAll();
		} 
		commonPlanAccessibility = commonPlanAccessibilityMap.get(accessibility.toLowerCase());
		
		if (commonPlanAccessibility == null) {
			System.out.println("Allowed CommonList : " + commonPlanAccessibilityMap.keySet());
			String message = "CommonList | Plan-Accessibility details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, accessibility);
		}

		return commonPlanAccessibility;
	}
	
	public String getCommonPaymentMode(String paymentMode) {
		
		String commonPaymentMode = null;
		if(paymentModeMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			paymentModeMap = commonListAll.getPaymentModeAll();
		}
		commonPaymentMode = paymentModeMap.get(paymentMode.toLowerCase());	
		
		if (commonPaymentMode == null) {
			System.out.println("Allowed CommonList : " + paymentModeMap.keySet());
			String message = "CommonList | PaymentMode details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, paymentMode);
		}
		
		return commonPaymentMode;
	}
	
	public String getCommonPlanType(String planType) {
		
		String commonPlanType = null;
		if(commonPlanTypeMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			commonPlanTypeMap = commonListAll.getCommonPlanTypeAll();
		} 
		commonPlanType = commonPlanTypeMap.get(planType.toLowerCase());
		
		if (commonPlanType == null) {
			System.out.println("Allowed CommonList : " + commonPlanTypeMap.keySet());
			String message = "CommonList | Plan-Type details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, planType);
		}

		return commonPlanType;
	}
		
	public String getCommonPlanCategory(String planCategory) {
		
		String commonPlanCategory = null;
		if(commonPlanCategoryMap.isEmpty()) {
			CommonListAll commonListAll = new CommonListAll();
			commonPlanCategoryMap = commonListAll.getCommonPlanCategoryAll();
		} 
		commonPlanCategory = commonPlanCategoryMap.get(planCategory.toLowerCase());
		
		if (commonPlanCategory == null) {
			System.out.println("Allowed CommonList : " + commonPlanCategoryMap.keySet());
			String message = "CommonList | Plan-Category details not found";
			ProductUtility.stopExecution(logFileName, logModuleName, message, planCategory);
		}

		return commonPlanCategory;
	}
		
	
	
	//*******************************Caching NOT Done**************************************************
	//****************************************************************************************************
	
	
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
