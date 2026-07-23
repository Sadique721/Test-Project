package Act_Migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class CreateBandwidthQosPolicy extends RestExecution {

	private String logFileName = "ActBandwidthQos.log";
	private String logModuleName = "ActBandwidthQos";

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

	}
	/*
	  public void createPlanQos(List<Map<String, String>> qosMapList) {
	  
	  for (int i = 0; i < qosMapList.size(); i++) {
	  
	  Map<String, String> map = new HashMap<String, String>(); map =
	  qosMapList.get(i); Utility.printLog(logFileName, logModuleName, "Sheet Data",
	  map.toString()); createPlanQos(map);
	  } 
	  
	  }
	 */
   //thread is close
	public void createPlanQos(List<Map<String, String>> qosMapList) {
		//int numThreads = Runtime.getRuntime().availableProcessors();
		int numThreads = 2;

		ExecutorService executorService = Executors.newFixedThreadPool(numThreads); // Use 10 threads for parallel
																					// execution

		for (Map<String, String> map : qosMapList) {

			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			executorService.submit(() -> createPlanQos(map)); // Submit each task for execution

		}

		executorService.shutdown(); // Initiate the shutdown of the executor service
	
	try {
	    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
	        executorService.shutdownNow();
	    }
	} catch (InterruptedException e) {
	    executorService.shutdownNow();
	}
	
}
	public List<Map<String, String>> readUniquePlanQosList() {

		String sheetName = "Bqos";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getActPlan(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> qosMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			// PRE_FUP_PROF_DL_HW PRE_FUP_PROF_UL_HW
			

			String qosPolicyName = cellValue.get("QOS_NAME");
			if ((!"".equals(qosPolicyName)) && (qosPolicyName != null)) {
				valuemap.put("RowIndex", cellValue.get("NO"));
				valuemap.put("QosPolicyName", cellValue.get("QOS_NAME"));

				valuemap.put("Pre-FUP Speed", cellValue.get("SPEED"));
				valuemap.put("SPEED_UNIT", cellValue.get("SPEED_UNIT"));

				valuemap.put("PRE_FUP_PROF_NOKIA", cellValue.get("PRE_FUP_PROF_NOKIA"));

				valuemap.put("PRE_FUP_PROF_DL_ERIC", cellValue.get("PRE_FUP_PROF_DL_ERIC"));
				valuemap.put("PRE_FUP_PROF_UL_ERIC", cellValue.get("PRE_FUP_PROF_UL_ERIC"));

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
			qosJson.put("thpolicyname", qosDetails.get("QosPolicyName"));
			qosJson.put("basepolicyname", qosDetails.get("QosPolicyName"));
			qosJson.put("description", qosDetails.get("QosPolicyName"));

			String qosspeed = qosDetails.get("Pre-FUP Speed");
			qosJson.put("qosspeed", JSONObject.NULL);
			if (!"".equals(qosspeed)) {
				qosJson.put("qosspeed", qosspeed);
			}

			List<JSONObject> gatewayMappingList = new ArrayList<JSONObject>();

			String PreFUPSpeed = qosDetails.get("Pre-FUP Speed");
			String preUnit = qosDetails.get("SPEED_UNIT");

			// convert into kb
			int pre = Integer.parseInt(PreFUPSpeed);
			int PreFUPSpeedKb = pre * 1024;

			// if pre speed unit in kbps
			if (preUnit.equalsIgnoreCase("Kbps")) {
				PreFUPSpeedKb = pre;
			}
			/*
			 * 1st: MikroTik 2nd: Huawei 3rd:Nokia  4th: Ericsson
			 */
			
			
			//1-->MikroTik
			
			String gateway1 = "MikroTik";
			if ((!"".equals(gateway1)) && (gateway1 != null)) {
				JSONObject gateway = new JSONObject();
				if (gateway1.equalsIgnoreCase("MikroTik")) {

					// if pre speed in Mbps-->
					String preFUPSpeed = PreFUPSpeed + "M" + "/" + PreFUPSpeed + "M";

					if (preUnit.equalsIgnoreCase("Kbps")) {
						preFUPSpeed = PreFUPSpeed + "K" + "/" + PreFUPSpeed + "K";
					}

					gateway.put("gatewayName", gateway1);
					gateway.put("downloadSpeed", preFUPSpeed);
					gateway.put("uploadSpeed", preFUPSpeed);
					gateway.put("baseDownloadSpeed", preFUPSpeed);
					gateway.put("baseUploadSpeed", preFUPSpeed);

					gateway.put("throttleDownloadSpeed", "N/A");
					gateway.put("throttleUploadSpeed", "N/A");
					gateway.put("qosPolicyId", "");
					gatewayMappingList.add(gateway);
				}
			}

			//2nd--> Huawei
			String Gateway2 = "Huawei";
			if ((!"".equals(Gateway2)) && (Gateway2 != null)) {

				JSONObject gateway = new JSONObject();
				gateway.put("gatewayName", Gateway2);
				gateway.put("downloadSpeed", PreFUPSpeedKb);
				gateway.put("uploadSpeed", PreFUPSpeedKb);
				gateway.put("baseDownloadSpeed", PreFUPSpeedKb);
				gateway.put("baseUploadSpeed", PreFUPSpeedKb);
				gateway.put("throttleDownloadSpeed", "N/A");
				gateway.put("throttleUploadSpeed", "N/A");
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);
			}

		//	3rd-->Nokia
			String Gateway3 = "Nokia";
			if ((!"".equals(Gateway3)) && (Gateway3 != null)) {
				JSONObject gateway = new JSONObject();

				gateway.put("gatewayName", Gateway3);

				String formtDow = "e:q:1:pir=" + PreFUPSpeedKb + ",cir=" + PreFUPSpeedKb + ",mbs=-1,cbs=-1";
				String formtUpl = "i:q:1:pir=" + PreFUPSpeedKb + ",cir=" + PreFUPSpeedKb + ",mbs=-1,cbs=-1";
				
				gateway.put("downloadSpeed", formtDow);
				gateway.put("uploadSpeed", formtUpl);

				gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));
				gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));

				gateway.put("throttleDownloadSpeed", "N/A");
				gateway.put("throttleUploadSpeed", "N/A");
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);
			}

			
			// -->4th Ericsson
			String Gateway4 = "Ericsson";
			if ((!"".equals(Gateway4)) && (Gateway4 != null)) {

				JSONObject gateway = new JSONObject();
				gateway.put("gatewayName", Gateway4);

				gateway.put("downloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC")); // read from excel in rem
				gateway.put("uploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
				gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));
				gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
				gateway.put("throttleDownloadSpeed", "N/A");
				gateway.put("throttleUploadSpeed", "N/A");
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);
			}

			
			qosJson.put("qosPolicyGatewayMappingList", gatewayMappingList);

			qosJson.put("thparam1", "N/A");
			qosJson.put("thparam2", "N/A");
			qosJson.put("thparam3", "N/A");
			qosJson.put("baseparam1", "N/A");
			qosJson.put("baseparam2", "N/A");
			qosJson.put("baseparam3", "N/A");

			qosJson.put("type", JSONObject.NULL);

			jsonString = qosJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;

	}

}
