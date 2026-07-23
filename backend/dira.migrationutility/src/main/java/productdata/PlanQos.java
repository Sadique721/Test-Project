package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class PlanQos extends RestExecution {
	
	private String logFileName = "prepaidplan.log";
	private String logModuleName = "CreateQos";

	private void createPlanQos(Map<String, String> qosDetails) {

		String apiURL = getAPIURL("cpm/qosPolicy/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body -->
		String apiBody = getPlanQosJson(qosDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);
		
		String qosName = qosDetails.get("QosPolicyName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, qosName);
		
	/*	if(JSONResponseBody.has("responseCode")) {		
			int status = JSONResponseBody.getInt("responseCode");
	
			if (status == 200 || status == 0) {
				String message = "New Plan-Qos is added successfully - " + qosName;
				System.out.println(message);
				Utility.printLog("execution.log", logModuleName, "Success", message);
	
			} else if (status == 406) {			
				String error = JSONResponseBody.getString("responseMessage") + " - " + qosName;
				System.out.println(error);
				Utility.printLog("execution.log", logModuleName, "Already Exist", error);
			} 
		} else {
			String error = JSONResponseBody.get("ERROR") + " - " + qosName;
			System.out.println(error);
			Utility.printLog("execution.log", logModuleName, "ERROR", error);
		}
	*/	
	}

	public void createPlanQos(List<Map<String, String>> qosMapList) {

		for (int i = 0; i < qosMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = qosMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPlanQos(map);
		}
	}

	public List<Map<String, String>> readUniquePlanQosList() {

		String sheetName = "Qos";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> qosMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);
			
			String qosPolicyName = cellValue.get("QosPolicyName");
			if ((!"".equals(qosPolicyName)) && (qosPolicyName != null)) {

				valuemap.put("QosPolicyName", cellValue.get("QosPolicyName"));
				valuemap.put("PolicyName", cellValue.get("PolicyName"));
				valuemap.put("BasePolicyName", cellValue.get("BasePolicyName"));
				valuemap.put("QoSSpeed", cellValue.get("QoSSpeed"));
				valuemap.put("Description", cellValue.get("Description"));
			
				String gateway1 = cellValue.get("Gateway1");
				
				
				
				if ((!"".equals(gateway1)) && (gateway1 != null)) {				
					valuemap.put("Gateway1", cellValue.get("Gateway1"));
					valuemap.put("Gateway1_DownloadSpeed", cellValue.get("Gateway1_DownloadSpeed"));
					valuemap.put("Gateway1_UploadSpeed", cellValue.get("Gateway1_UploadSpeed"));
					valuemap.put("Gateway1_BaseDownloadSpeed", cellValue.get("Gateway1_BaseDownloadSpeed"));
					valuemap.put("Gateway1_BaseUploadSpeed", cellValue.get("Gateway1_BaseUploadSpeed"));
					valuemap.put("Gateway1_ThDownloadSpeed", cellValue.get("Gateway1_ThDownloadSpeed"));
					valuemap.put("Gateway1_ThUploadSpeed", cellValue.get("Gateway1_ThUploadSpeed"));
				}

				String gateway2 = cellValue.get("Gateway2");
				if ((!"".equals(gateway2)) && (gateway2 != null)) {
					
					valuemap.put("Gateway2", cellValue.get("Gateway2"));
					valuemap.put("Gateway2_DownloadSpeed", cellValue.get("Gateway2_DownloadSpeed"));
					valuemap.put("Gateway2_UploadSpeed", cellValue.get("Gateway2_UploadSpeed"));
					valuemap.put("Gateway2_BaseDownloadSpeed", cellValue.get("Gateway2_BaseDownloadSpeed"));
					valuemap.put("Gateway2_BaseUploadSpeed", cellValue.get("Gateway2_BaseUploadSpeed"));
					valuemap.put("Gateway2_ThDownloadSpeed", cellValue.get("Gateway2_ThDownloadSpeed"));
					valuemap.put("Gateway2_ThUploadSpeed", cellValue.get("Gateway2_ThUploadSpeed"));
					
				}
	
				String gateway3 = cellValue.get("Gateway3");
				if ((!"".equals(gateway3)) && (gateway3 != null)) {
					
					valuemap.put("Gateway3", cellValue.get("Gateway3"));
					valuemap.put("Gateway3_DownloadSpeed", cellValue.get("Gateway3_DownloadSpeed"));
					valuemap.put("Gateway3_UploadSpeed", cellValue.get("Gateway3_UploadSpeed"));
					valuemap.put("Gateway3_BaseDownloadSpeed", cellValue.get("Gateway3_BaseDownloadSpeed"));
					valuemap.put("Gateway3_BaseUploadSpeed", cellValue.get("Gateway3_BaseUploadSpeed"));
					valuemap.put("Gateway3_ThDownloadSpeed", cellValue.get("Gateway3_ThDownloadSpeed"));
					valuemap.put("Gateway3_ThUploadSpeed", cellValue.get("Gateway3_ThUploadSpeed"));
				}
				
				String gateway4 = cellValue.get("Gateway4");
				if ((!"".equals(gateway4)) && (gateway4 != null)) {
					
					valuemap.put("Gateway4", cellValue.get("Gateway4"));
					valuemap.put("Gateway4_DownloadSpeed", cellValue.get("Gateway4_DownloadSpeed"));
					valuemap.put("Gateway4_UploadSpeed", cellValue.get("Gateway4_UploadSpeed"));
					valuemap.put("Gateway4_BaseDownloadSpeed", cellValue.get("Gateway4_BaseDownloadSpeed"));
					valuemap.put("Gateway4_BaseUploadSpeed", cellValue.get("Gateway4_BaseUploadSpeed"));
					valuemap.put("Gateway4_ThDownloadSpeed", cellValue.get("Gateway4_ThDownloadSpeed"));
					valuemap.put("Gateway4_ThUploadSpeed", cellValue.get("Gateway4_ThUploadSpeed"));
					
				}

				valuemap.put("Param1", cellValue.get("Param1"));
				valuemap.put("Param2", cellValue.get("Param2"));
				valuemap.put("Param3", cellValue.get("Param3"));
				valuemap.put("BaseParam1", cellValue.get("BaseParam1"));
				valuemap.put("BaseParam2", cellValue.get("BaseParam2"));
				valuemap.put("BaseParam3", cellValue.get("BaseParam3"));
				valuemap.put("MultiServiceType", cellValue.get("MultiServiceType"));
				
				qosMapList.add(valuemap);
			}
		}
		return qosMapList;
	}

	private String getPlanQosJson(Map<String, String> qosDetails) {

		String jsonString = null;

		try {

			JSONObject qosJson = new JSONObject();

			qosJson.put("name", qosDetails.get("QosPolicyName"));
			qosJson.put("thpolicyname", qosDetails.get("PolicyName"));
			qosJson.put("basepolicyname", qosDetails.get("BasePolicyName"));
			qosJson.put("description", qosDetails.get("Description"));
			
			String qosspeed = qosDetails.get("QoSSpeed");
			qosJson.put("qosspeed", JSONObject.NULL);
			if (!"".equals(qosspeed)) { qosJson.put("qosspeed", qosspeed); }
			
			List<JSONObject> gatewayMappingList = new ArrayList<JSONObject>();
			
			String gateway1 = qosDetails.get("Gateway1");
			if ((!"".equals(gateway1)) && (gateway1 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName", qosDetails.get("Gateway1"));
				gateway.put("downloadSpeed", qosDetails.get("Gateway1_DownloadSpeed"));
				gateway.put("uploadSpeed", qosDetails.get("Gateway1_UploadSpeed"));
				gateway.put("baseDownloadSpeed", qosDetails.get("Gateway1_BaseDownloadSpeed"));
				gateway.put("baseUploadSpeed", qosDetails.get("Gateway1_BaseUploadSpeed"));
				gateway.put("throttleDownloadSpeed", qosDetails.get("Gateway1_ThDownloadSpeed"));
				gateway.put("throttleUploadSpeed", qosDetails.get("Gateway1_ThUploadSpeed"));
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}
			
			String gateway2 = qosDetails.get("Gateway2");
			if ((!"".equals(gateway2)) && (gateway2 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName", qosDetails.get("Gateway2"));
				gateway.put("downloadSpeed", qosDetails.get("Gateway2_DownloadSpeed"));
				gateway.put("uploadSpeed", qosDetails.get("Gateway2_UploadSpeed"));
				gateway.put("baseDownloadSpeed", qosDetails.get("Gateway2_BaseDownloadSpeed"));
				gateway.put("baseUploadSpeed", qosDetails.get("Gateway2_BaseUploadSpeed"));
				gateway.put("throttleDownloadSpeed", qosDetails.get("Gateway2_ThDownloadSpeed"));
				gateway.put("throttleUploadSpeed", qosDetails.get("Gateway2_ThUploadSpeed"));
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}
			
			String Gateway3 = qosDetails.get("Gateway3");
			if ((!"".equals(Gateway3)) && (Gateway3 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName", qosDetails.get("Gateway3"));
				gateway.put("downloadSpeed", qosDetails.get("Gateway3_DownloadSpeed"));
				gateway.put("uploadSpeed", qosDetails.get("Gateway3_UploadSpeed"));
				gateway.put("baseDownloadSpeed", qosDetails.get("Gateway3_BaseDownloadSpeed"));
				gateway.put("baseUploadSpeed", qosDetails.get("Gateway3_BaseUploadSpeed"));
				gateway.put("throttleDownloadSpeed", qosDetails.get("Gateway3_ThDownloadSpeed"));
				gateway.put("throttleUploadSpeed", qosDetails.get("Gateway3_ThUploadSpeed"));
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}

			String Gateway4 = qosDetails.get("Gateway4");
			if ((!"".equals(Gateway4)) && (Gateway4 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName", qosDetails.get("Gateway4"));
				gateway.put("downloadSpeed", qosDetails.get("Gateway4_DownloadSpeed"));
				gateway.put("uploadSpeed", qosDetails.get("Gateway4_UploadSpeed"));
				gateway.put("baseDownloadSpeed", qosDetails.get("Gateway4_BaseDownloadSpeed"));
				gateway.put("baseUploadSpeed", qosDetails.get("Gateway4_BaseUploadSpeed"));
				gateway.put("throttleDownloadSpeed", qosDetails.get("Gateway4_ThDownloadSpeed"));
				gateway.put("throttleUploadSpeed", qosDetails.get("Gateway4_ThUploadSpeed"));
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}

			qosJson.put("qosPolicyGatewayMappingList", gatewayMappingList);
			
			qosJson.put("thparam1", qosDetails.get("Param1"));
			qosJson.put("thparam2", qosDetails.get("Param2"));
			qosJson.put("thparam3", qosDetails.get("Param3"));			
			qosJson.put("baseparam1", qosDetails.get("BaseParam1"));
			qosJson.put("baseparam2", qosDetails.get("BaseParam2"));
			qosJson.put("baseparam3", qosDetails.get("BaseParam3"));
			
			String type = qosDetails.get("MultiServiceType");
			qosJson.put("type", JSONObject.NULL);
			if (!"".equals(type)) { qosJson.put("type", type); }
			
			jsonString = qosJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}
