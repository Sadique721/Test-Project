package Act_Migration;

/*
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import org.json.JSONObject;

	import api.ReadData;
	import api.RestExecution;
	import utility.ProductUtility;
	import utility.Utility;

	public class CreateActBaseQos extends RestExecution {
		
		private String logFileName = "ActBaseQos.log";
		private String logModuleName = "CreateActBaseQos";

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

		public void createPlanQos(List<Map<String, String>> qosMapList) {

			for (int i = 0; i < qosMapList.size(); i++) {

				Map<String, String> map = new HashMap<String, String>();
				map = qosMapList.get(i);
				Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
				createPlanQos(map);
			}
		}
		 public void createPlanQos(List<Map<String, String>> qosMapList) {
    	int numThreads = Runtime.getRuntime().availableProcessors();
    	
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); // Use 10 threads for parallel execution

        for (Map<String, String> map : qosMapList) {
            executorService.submit(() -> createPlanQos(map)); // Submit each task for execution
        }

        executorService.shutdown(); // Initiate the shutdown of the executor service
    }

		public List<Map<String, String>> readUniquePlanQosList() {

			String sheetName = "QosBasePlan";
			List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
			ReadData readData = new ReadData();
			sheetMap = readData.getPlanDataSheet(sheetName);

			Map<String, String> cellValue = new HashMap<String, String>();
			List<Map<String, String>> qosMapList = new ArrayList<Map<String, String>>();

			for (int i = 0; i < sheetMap.size(); i++) {

				Map<String, String> valuemap = new HashMap<String, String>();
				cellValue = sheetMap.get(i);
				
				String qosPolicyName = cellValue.get("QOS_NAME");
				if ((!"".equals(qosPolicyName)) && (qosPolicyName != null)) {
					valuemap.put("RowIndex", cellValue.get("NO"));
					valuemap.put("QosPolicyName", cellValue.get("QOS_NAME"));
					
				
					
					
					valuemap.put("Pre-FUP Speed", cellValue.get("Pre-FUP Speed"));
					valuemap.put("SPEED_UNIT", cellValue.get("SPEED_UNIT"));
					
					valuemap.put("Post-FUP Speed", cellValue.get("Post-FUP Speed"));
					valuemap.put("SPEED_UNIT_POST", cellValue.get("SPEED_UNIT_POST"));
					
					valuemap.put("PRE_FUP_PROF_NOKIA", cellValue.get("PRE_FUP_PROF_NOKIA"));
					valuemap.put("POST_FUP_PROF_NOKIA", cellValue.get("POST_FUP_PROF_NOKIA"));
					
					valuemap.put("PRE_FUP_PROF_DL_ERIC", cellValue.get("PRE_FUP_PROF_DL_ERIC"));
					valuemap.put("PRE_FUP_PROF_UL_ERIC", cellValue.get("PRE_FUP_PROF_UL_ERIC"));
					
					valuemap.put("POST_FUP_PROF_DL_ERIC", cellValue.get("POST_FUP_PROF_DL_ERIC"));
					valuemap.put("POST_FUP_PROF_UL_ERIC", cellValue.get("POST_FUP_PROF_UL_ERIC"));
					


	
					
					
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
					qosJson.put("qosspeed", qosspeed); }
				
				List<JSONObject> gatewayMappingList = new ArrayList<JSONObject>();
				
				String PreFUPSpeed=qosDetails.get("Pre-FUP Speed");    
				String preUnit=qosDetails.get("SPEED_UNIT");
				
				String PostFUPSpeed=qosDetails.get("Post-FUP Speed"); 
				String postUnit=qosDetails.get("SPEED_UNIT_POST");
				
				
				
				// convert into kb
				long pre=Long.parseLong(PreFUPSpeed);
				long PreFUPSpeedKb=pre*1024;  
				
				//if pre speed unit in kbps 
				if(preUnit.equalsIgnoreCase("Kbps")) {
					 PreFUPSpeedKb=pre;	
				}
		
				// if post speed unit in Mbps
				int kbpsPostFUPSpeed=0;
				if(!PostFUPSpeed.equalsIgnoreCase("N/A")) {
				 kbpsPostFUPSpeed=Integer.parseInt(PostFUPSpeed);
				if(postUnit.equalsIgnoreCase("Mbps")) {
					kbpsPostFUPSpeed=kbpsPostFUPSpeed*1024;
				}
				}
				
				String gateway1 = "Mikrotik";
				if ((!"".equals(gateway1)) && (gateway1 != null)) {				
					JSONObject gateway= new JSONObject();		
					if(gateway1.equalsIgnoreCase("Mikrotik")) {
			
						String preFUPSpeed=PreFUPSpeed+"M"+"/"+PreFUPSpeed+"M";

				
					if(preUnit.equalsIgnoreCase("Kbps")) {
						preFUPSpeed=PreFUPSpeed+"K"+"/"+PreFUPSpeed+"K";
						
					}
					
					//if the post speed in kbps
					 String Throtle=PostFUPSpeed+"K"+"/"+PostFUPSpeed+"K";
					 
					 // if in post speed in Mbps.
					 if(postUnit.equalsIgnoreCase("Mbps")) {
						 Throtle=PostFUPSpeed+"M"+"/"+PostFUPSpeed+"M";
					 }
				
						
					gateway.put("gatewayName",gateway1);
					gateway.put("downloadSpeed", preFUPSpeed);
					gateway.put("uploadSpeed",preFUPSpeed );
					gateway.put("baseDownloadSpeed", preFUPSpeed);
					gateway.put("baseUploadSpeed",preFUPSpeed );
				
					//for unlimited plan qos-->
					if(PostFUPSpeed.equalsIgnoreCase("N/A")) {
						gateway.put("throttleDownloadSpeed", preFUPSpeed);
						gateway.put("throttleUploadSpeed", preFUPSpeed);
					}
					else {
					gateway.put("throttleDownloadSpeed", Throtle);
					gateway.put("throttleUploadSpeed", Throtle);
					}
					gateway.put("qosPolicyId", "");
					gatewayMappingList.add(gateway);				
				}	
				}
				
				// Nokia
				String Gateway2 = "Nokia";
				if ((!"".equals(Gateway2)) && (Gateway2 != null)) {				
					JSONObject gateway= new JSONObject();	
					
					gateway.put("gatewayName", Gateway2);
					
					
				   String formtDow ="e:q:1:pir="+PreFUPSpeedKb+",cir="+PreFUPSpeedKb+",mbs=-1,cbs=-1";
				   String formtUpl ="i:q:1:pir="+PreFUPSpeedKb+",cir="+PreFUPSpeedKb+",mbs=-1,cbs=-1";
				   
				   
				   String formtDowTho ="e:q:1:pir="+kbpsPostFUPSpeed+",cir="+kbpsPostFUPSpeed+",mbs=-1,cbs=-1";
				   String formtUplTho ="i:q:1:pir="+kbpsPostFUPSpeed+",cir="+kbpsPostFUPSpeed+",mbs=-1,cbs=-1";
				                    
				   
					
					gateway.put("downloadSpeed", formtDow);
					gateway.put("uploadSpeed",formtUpl );
					
					
					gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));  //read from excel is remaing
					gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));    ////read from excel is remaing
					
					if(PostFUPSpeed.equalsIgnoreCase("N/A")) {
						gateway.put("throttleDownloadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));
						gateway.put("throttleUploadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));
					
					}
					else {
						gateway.put("throttleDownloadSpeed", formtDowTho);
						gateway.put("throttleUploadSpeed", formtUplTho);
					}
					gateway.put("qosPolicyId", "");
					gatewayMappingList.add(gateway);				
				}
				
				//Errcsion
				String Gateway3 = "Ericsson";
				if ((!"".equals(Gateway3)) && (Gateway3 != null)) {				
				
					JSONObject gateway= new JSONObject();				
					gateway.put("gatewayName",Gateway3 );
					
					gateway.put("downloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));   // read from excel in rem
					gateway.put("uploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
					gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));
					gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
					
					if(PostFUPSpeed.equalsIgnoreCase("N/A")) {
					gateway.put("throttleDownloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));
					gateway.put("throttleUploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
					}
					
					else {
						gateway.put("throttleDownloadSpeed", qosDetails.get("POST_FUP_PROF_DL_ERIC"));
						gateway.put("throttleUploadSpeed", qosDetails.get("POST_FUP_PROF_UL_ERIC"));
					}
					gateway.put("qosPolicyId", "");
					gatewayMappingList.add(gateway);				
				}

				String Gateway4 = "Huwai";
				if ((!"".equals(Gateway4)) && (Gateway4 != null)) {				
				
					JSONObject gateway= new JSONObject();				
					gateway.put("gatewayName", Gateway4);
					gateway.put("downloadSpeed",PreFUPSpeedKb);
					gateway.put("uploadSpeed",PreFUPSpeedKb);
					gateway.put("baseDownloadSpeed", PreFUPSpeedKb);
					gateway.put("baseUploadSpeed", PreFUPSpeedKb);
					
					if(PostFUPSpeed.equalsIgnoreCase("N/A")) {
						gateway.put("throttleDownloadSpeed", PreFUPSpeedKb);
						gateway.put("throttleUploadSpeed", PreFUPSpeedKb);
					}
					else {
					gateway.put("throttleDownloadSpeed", kbpsPostFUPSpeed);
					gateway.put("throttleUploadSpeed", kbpsPostFUPSpeed);
					}
					
					gateway.put("qosPolicyId", "");
					gatewayMappingList.add(gateway);				
				}

				qosJson.put("qosPolicyGatewayMappingList", gatewayMappingList);
				
				qosJson.put("thparam1", "N/A");
				qosJson.put("thparam2", "N/A");
				qosJson.put("thparam3", "N/A");	
				//for unlimited plan qos-->
				if(PostFUPSpeed.equalsIgnoreCase("N/A")) {
					qosJson.put("baseparam1", qosDetails.get("POST_FUP_PROF_NOKIA"));
				}
				//--->
				else {
				qosJson.put("baseparam1", qosDetails.get("POST_FUP_PROF_NOKIA"));
				}
				qosJson.put("baseparam2", "N/A");
				qosJson.put("baseparam3","N/A");
				
				
				qosJson.put("type", JSONObject.NULL);
			
				
				jsonString = qosJson.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return jsonString;
		

	}

}  */





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

public class CreateActBaseQos extends RestExecution {

    private String logFileName = "ActBaseQos.log";
    private String logModuleName = "CreateActBaseQos";

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

    public void createPlanQos(List<Map<String, String>> qosMapList) {
    	//int numThreads = Runtime.getRuntime().availableProcessors();
    	
        ExecutorService executorService = Executors.newFixedThreadPool(4); // Use 10 threads for parallel execution

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
        String sheetName = "QosBasePlan";
        List<Map<String, String>> sheetMap = new ArrayList<>();
        ReadData readData = new ReadData();
        sheetMap = readData.getActPlan(sheetName);

        List<Map<String, String>> qosMapList = new ArrayList<>();

        for (Map<String, String> cellValue : sheetMap) {
            String qosPolicyName = cellValue.get("QOS_NAME");
            if ((!"".equals(qosPolicyName)) && (qosPolicyName != null)) {
                Map<String, String> valuemap = new HashMap<>();
                valuemap.put("RowIndex", cellValue.get("NO"));
                valuemap.put("QosPolicyName", cellValue.get("QOS_NAME"));
                valuemap.put("Pre-FUP Speed", cellValue.get("Pre-FUP Speed"));
                valuemap.put("SPEED_UNIT", cellValue.get("SPEED_UNIT"));
                valuemap.put("Post-FUP Speed", cellValue.get("Post-FUP Speed"));
                valuemap.put("SPEED_UNIT_POST", cellValue.get("SPEED_UNIT_POST"));
                valuemap.put("PRE_FUP_PROF_NOKIA", cellValue.get("PRE_FUP_PROF_NOKIA"));
                valuemap.put("POST_FUP_PROF_NOKIA", cellValue.get("POST_FUP_PROF_NOKIA"));
                valuemap.put("PRE_FUP_PROF_DL_ERIC", cellValue.get("PRE_FUP_PROF_DL_ERIC"));
                valuemap.put("PRE_FUP_PROF_UL_ERIC", cellValue.get("PRE_FUP_PROF_UL_ERIC"));
                valuemap.put("POST_FUP_PROF_DL_ERIC", cellValue.get("POST_FUP_PROF_DL_ERIC"));
                valuemap.put("POST_FUP_PROF_UL_ERIC", cellValue.get("POST_FUP_PROF_UL_ERIC"));

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
            String speedUnit = qosDetails.get("SPEED_UNIT");
            Long preFUPSpeedKb = null;
            int kbpsPostFUPSpeed = 0;
            String postFupSpeed=qosDetails.get("Post-FUP Speed");
            String postSpeedUnit=qosDetails.get("SPEED_UNIT_POST");
            
            qosJson.put("qosspeed", JSONObject.NULL);
            if (!"".equals(qosspeed)) {
                qosJson.put("qosspeed", qosspeed);
            }
            
            if(speedUnit.equals("Mbps") ||speedUnit.equals("mbps")) {
            	preFUPSpeedKb = Long.parseLong(qosDetails.get("Pre-FUP Speed")) * 1024;
            }
      
            
          if (postSpeedUnit.equalsIgnoreCase("Mbps") ) {
        	  
        	  
        	 kbpsPostFUPSpeed=Integer.parseInt(postFupSpeed)*1024;
        	  
          }
          else if( postSpeedUnit.equalsIgnoreCase("Kbps")){
        	  kbpsPostFUPSpeed =Integer.parseInt(postFupSpeed);
          }
          
            
           
            
            
            String preFUPSpeed = qosDetails.get("Pre-FUP Speed") + "M" + "/" + qosDetails.get("Pre-FUP Speed") + "M";
            if (qosDetails.get("SPEED_UNIT").equalsIgnoreCase("Kbps")) {
                preFUPSpeed = qosDetails.get("Pre-FUP Speed") + "K" + "/" + qosDetails.get("Pre-FUP Speed") + "K";
            }
            List<JSONObject> gatewayMappingList = new ArrayList<>();
          
     
            
           //MikroTik
            // Huawei
         //   3rd: Nokia
           // 4th: Ericsson
            
            
            
            //1--->MikroTik
            String gateway1 = "MikroTik";
          
            if ((!"".equals(gateway1)) && (gateway1 != null)) {
                JSONObject gateway = new JSONObject();
              

                String throttle = qosDetails.get("Post-FUP Speed") + "K" + "/" + qosDetails.get("Post-FUP Speed") + "K";
                if (qosDetails.get("SPEED_UNIT_POST").equalsIgnoreCase("Mbps")) {
                    throttle = qosDetails.get("Post-FUP Speed") + "M" + "/" + qosDetails.get("Post-FUP Speed") + "M";
                }

                gateway.put("gatewayName", gateway1);
                gateway.put("downloadSpeed", preFUPSpeed);
                gateway.put("uploadSpeed", preFUPSpeed);
                gateway.put("baseDownloadSpeed", preFUPSpeed);
                gateway.put("baseUploadSpeed", preFUPSpeed);

                if ("N/A".equals(qosDetails.get("Post-FUP Speed"))) {
                    gateway.put("throttleDownloadSpeed", preFUPSpeed);
                    gateway.put("throttleUploadSpeed", preFUPSpeed);
                } else {
                    gateway.put("throttleDownloadSpeed", throttle);
                    gateway.put("throttleUploadSpeed", throttle);
                }

                gateway.put("qosPolicyId", "");
                gatewayMappingList.add(gateway);
            }

            
            // Huawei
            String Gateway2 = "Huawei";
			if ((!"".equals(Gateway2)) && (Gateway2 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName", Gateway2);
				gateway.put("downloadSpeed",preFUPSpeedKb);
				gateway.put("uploadSpeed",preFUPSpeedKb);
				gateway.put("baseDownloadSpeed", preFUPSpeedKb);
				gateway.put("baseUploadSpeed", preFUPSpeedKb);
				
				if ("N/A".equals(qosDetails.get("Post-FUP Speed"))) {
					gateway.put("throttleDownloadSpeed", preFUPSpeedKb);
					gateway.put("throttleUploadSpeed", preFUPSpeedKb);
				}
				else {
				gateway.put("throttleDownloadSpeed", kbpsPostFUPSpeed);
				gateway.put("throttleUploadSpeed", kbpsPostFUPSpeed);
				}
				
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}
            
            
            
            //3 Nokia Gateway
            String gateway3 = "Nokia";
            if ((!"".equals(gateway3)) && (gateway3 != null)) {
                JSONObject gateway = new JSONObject();
                String formatDown = "e:q:1:pir=" + preFUPSpeedKb + ",cir=" + preFUPSpeedKb + ",mbs=-1,cbs=-1";
                String formatUp = "i:q:1:pir=" + preFUPSpeedKb + ",cir=" + preFUPSpeedKb + ",mbs=-1,cbs=-1";

                String formatDownThrottle = "e:q:1:pir=" + kbpsPostFUPSpeed + ",cir=" + kbpsPostFUPSpeed + ",mbs=-1,cbs=-1";
                String formatUpThrottle = "i:q:1:pir=" + kbpsPostFUPSpeed + ",cir=" + kbpsPostFUPSpeed + ",mbs=-1,cbs=-1";

                gateway.put("gatewayName", gateway3);
                gateway.put("downloadSpeed", formatDown);
                gateway.put("uploadSpeed", formatUp);
                gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));
                gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_NOKIA"));

                if ("N/A".equals(qosDetails.get("Post-FUP Speed"))) {
                    gateway.put("throttleDownloadSpeed", formatDown);
                    gateway.put("throttleUploadSpeed", formatUp);
                } else {
                    gateway.put("throttleDownloadSpeed", formatDownThrottle);
                    gateway.put("throttleUploadSpeed", formatUpThrottle);
                }

                gateway.put("qosPolicyId", "");
                gatewayMappingList.add(gateway);
            }
            
            
          //4-->Ericsson
			String Gateway4 = "Ericsson";
			if ((!"".equals(Gateway4)) && (Gateway4 != null)) {				
			
				JSONObject gateway= new JSONObject();				
				gateway.put("gatewayName",Gateway4 );
				
				gateway.put("downloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));   // read from excel in rem
				gateway.put("uploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
				gateway.put("baseDownloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));
				gateway.put("baseUploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
				
				if ("N/A".equals(qosDetails.get("Post-FUP Speed"))) {
				gateway.put("throttleDownloadSpeed", qosDetails.get("PRE_FUP_PROF_DL_ERIC"));
				gateway.put("throttleUploadSpeed", qosDetails.get("PRE_FUP_PROF_UL_ERIC"));
				}
				
				else {
					gateway.put("throttleDownloadSpeed", qosDetails.get("POST_FUP_PROF_DL_ERIC"));
					gateway.put("throttleUploadSpeed", qosDetails.get("POST_FUP_PROF_UL_ERIC"));
				}
				gateway.put("qosPolicyId", "");
				gatewayMappingList.add(gateway);				
			}

			
            qosJson.put("qosPolicyGatewayMappingList", gatewayMappingList);

            qosJson.put("thparam1", "N/A");
            qosJson.put("thparam2", "N/A");
            qosJson.put("thparam3", "N/A");
            if ("N/A".equals(qosDetails.get("Post-FUP Speed"))) {
            	qosJson.put("baseparam1", qosDetails.get("PRE_FUP_PROF_NOKIA"));
            }
            else {
            qosJson.put("baseparam1", qosDetails.get("POST_FUP_PROF_NOKIA"));
            }
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

