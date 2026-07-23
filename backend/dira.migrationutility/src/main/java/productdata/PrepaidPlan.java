package productdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import commons.CommonAPI;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import utility.ProductUtility;
import utility.Utility;

public class PrepaidPlan extends RestExecution {

	private static String logFileName = "prepaidplan.log";
	private static String logModuleName = "PrepaidPlan";
	
	
	
	 private static final int THREAD_POOL_SIZE = 4; // Optimized thread pool size based on requirements
	    private static final int BATCH_SIZE = 10; // To control batch size for API requests (can be adjusted based on performance testing)
	    private ExecutorService executorService;

	/**************Thread****************************/
/*	private ExecutorService executorService;

        // Constructor to initialize executor service
        public PrepaidPlan()  {
        //	Thread.sleep(1000);
        // You can adjust the pool size based on your requirements
    	this.executorService = Executors.newFixedThreadPool(4);
    	//this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }  */
	    
	    /*   New Thread********************************/
	    
	    
	 // Constructor to initialize executor service
	    public PrepaidPlan() {
	        try {
	            // Optionally add a delay for testing purposes or initialization
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();  // Restore interrupted state
	        }
	        // Create a fixed thread pool based on available processors for high concurrency
	        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	    }
	    
	    // Create a prepaid plan via API
	    private void createPrepaidPlan(Map<String, String> planDetailMap) {
	        String apiURL = getAPIURL("cpm/postpaidplan");
	        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

	        String APIBody = getPrepaidPlanJson(planDetailMap);
	        Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

	        if (APIBody != null && !APIBody.isEmpty()) {
	            // Attempting API call with retries
	            JSONObject JSONResponseBody = null;
	            int attempts = 0;
	            boolean success = false;

	            // Retry logic with exponential backoff
	            while (attempts < 3 && !success) {
	                try {
	                    JSONResponseBody = httpPost(apiURL, APIBody);
	                    success = true;
	                } catch (Exception e) {
	                    attempts++;
	                    if (attempts == 3) {
	                        Utility.printLog("execution.log", logModuleName, "ERROR", "API call failed after retries: " + e.getMessage());
	                    }
	                    try {
	                        Thread.sleep(1000 * attempts); // Exponential backoff (increases with each attempt)
	                    } catch (InterruptedException ie) {
	                        Thread.currentThread().interrupt();
	                    }
	                }
	            }

	            // Handle API response
	            if (success && JSONResponseBody != null) {
	                String response = JSONResponseBody.toString(4);
	                Utility.printLog(logFileName, logModuleName, "Response", response);
	                String planName = planDetailMap.get("PlanName");
	                ProductUtility.printResponse(JSONResponseBody, logModuleName, planName);
	            }
	        }
	    }
	    
	    // Method to create multiple prepaid plans in parallel
	    public void createPrepaidPlan(List<Map<String, String>> planMapList) {
	        // Using a List to hold the Callable tasks
	        List<Callable<Void>> tasks = new ArrayList<>();

	        // Iterate through the plan details and add tasks
	        for (Map<String, String> map : planMapList) {
	            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());

	            // Add each task to the list
	            tasks.add(() -> {
	                try {
	                    // Process each plan in a separate thread
	                    createPrepaidPlan(map); // Handle plan creation
	                } catch (Exception e) {
	                    System.err.println("Error processing plan " + map.get("PlanName") + ": " + e.getMessage());
	                }
	                return null; // Void result for Callable
	            });
	        }

	        // Execute the tasks using invokeAll
	        try {
	            // invokeAll executes tasks and waits for all to complete
	            List<Future<Void>> futures = executorService.invokeAll(tasks);

	            // Wait for completion of all tasks
	            for (Future<Void> future : futures) {
	                future.get(); // Blocking call to ensure all tasks finish
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            System.err.println("Error executing tasks: " + e.getMessage());
	        } finally {
	            // Gracefully shut down the executor service
	            shutdownExecutorService();
	        }
	    }

	    // Gracefully shut down the executor service
	    private void shutdownExecutorService() {
	        executorService.shutdown();
	        try {
	            // Wait for termination with timeout of 60 seconds
	            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
	                executorService.shutdownNow(); // Force shutdown if tasks don't complete in time
	            }
	        } catch (InterruptedException e) {
	            executorService.shutdownNow();
	        }
	    }
	   
	    
	    // Simulated API Call (replace with actual HTTP call logic)
	/*    public JSONObject httpPost(String url, String body) {
	        // Simulate API post (replace with actual implementation)
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("status", 200);
	        jsonResponse.put("message", "Success");
	        return jsonResponse;
	    }  */
	    
	    
  /************************Thread******************************/
     
            
          //  private ExecutorService executorService;

            // Constructor to initialize executor service
       /*     public PrepaidPlan() {
                try {
                    // Simulate a delay of 1 second (1000 milliseconds) during initialization
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Handle the interruption exception
                    Thread.currentThread().interrupt();  // Restore interrupted state
                }

                // You can adjust the pool size based on your requirements
               // this.executorService = Executors.newFixedThreadPool(4);
                // Alternatively, you could use the following for dynamic thread pool size based on available processors:
                 this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            }

            // Additional methods can go here */
        

    
    
	/*private void createPrepaidPlan(Map<String, String> planDetailMap) {

		String apiURL = getAPIURL("cpm/postpaidplan");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String APIBody = getPrepaidPlanJson(planDetailMap);
		Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

		if (!APIBody.equals(null)) {

			JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String planName = planDetailMap.get("PlanName");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, planName);
			
		}
	}   */
	    
	    
	    
	/* Original method without thread*/

	/*public void createPrepaidPlan(List<Map<String, String>> planMapList) {

		for (int i = 0; i < planMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = planMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createPrepaidPlan(map);
		}
	}   */
	
	/* cmt for thread original code */
	
	    
	    /*   cmt 2ndThread ------>*/
/*	public void createPrepaidPlan(List<Map<String, String>> planMapList) {
        // Submit each plan creation as a task to the executor
        for (int i = 0; i < planMapList.size(); i++) {
            Map<String, String> map = planMapList.get(i);
            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());

            // Submit a task to the executor
            executorService.submit(() -> {
            	
                createPrepaidPlan(map);
            });
        }

        // Shutdown the executor after all tasks are submitted
        executorService.shutdown();
    } */
	      /*   this method is used for multithreading*/
	
	    
	    
	/* Till----->*/

	public List<Map<String, String>> readUniquePrepaidPlanList() {

		String sheetName = "Plan";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getPlanDataSheet(sheetName);
		
		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> planMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String planName = cellValue.get("PlanName");
			if ((!"".equals(planName)) && (planName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("PlanName", cellValue.get("PlanName"));
				valuemap.put("DisplayName", cellValue.get("DisplayName"));
				valuemap.put("Code", cellValue.get("Code"));
				valuemap.put("Type", cellValue.get("Type"));
				valuemap.put("Category", cellValue.get("Category"));
				valuemap.put("Mode", cellValue.get("Mode"));
				valuemap.put("Group", cellValue.get("Group"));
				valuemap.put("Currency", cellValue.get("Currency"));

				valuemap.put("Service", cellValue.get("Service"));
				valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
				valuemap.put("Accessibility", cellValue.get("Accessibility"));
				valuemap.put("StartDate", cellValue.get("StartDate"));
				valuemap.put("EndDate", cellValue.get("EndDate"));

				valuemap.put("Validity", cellValue.get("Validity"));
				valuemap.put("ValidityUnit", cellValue.get("ValidityUnit"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("InvoiceToOrg", cellValue.get("InvoiceToOrg"));
				valuemap.put("RequiredApproval", cellValue.get("RequiredApproval"));
					
				valuemap.put("AllowOverUsage", cellValue.get("AllowOverUsage"));
				valuemap.put("MaxCurrentSession", cellValue.get("MaxCurrentSession"));
				valuemap.put("AllowDiscount", cellValue.get("AllowDiscount"));
				valuemap.put("Description", cellValue.get("Description"));

				valuemap.put("QuotaType", cellValue.get("QuotaType"));
				valuemap.put("QuotaTime", cellValue.get("QuotaTime"));
				valuemap.put("QuotaUnitTime", cellValue.get("QuotaUnitTime"));
				valuemap.put("QuotaData", cellValue.get("QuotaData"));
				valuemap.put("QuotaUnitData", cellValue.get("QuotaUnitData"));
				valuemap.put("QuotaResetInterval", cellValue.get("QuotaResetInterval"));
//                valuemap.put("usageQuotaType"), cellValue.get("")

				valuemap.put("SACCode", cellValue.get("SACCode"));
				valuemap.put("QosPolicy", cellValue.get("QosPolicy"));
				valuemap.put("TimeBasePolicy", cellValue.get("TimeBasePolicy"));
				valuemap.put("Param1", cellValue.get("Param1"));
				valuemap.put("Param2", cellValue.get("Param2"));
				valuemap.put("Param3", cellValue.get("Param3"));
				
				valuemap.put("ChargeName", cellValue.get("ChargeName"));
				valuemap.put("NewOfferPrice", cellValue.get("NewOfferPrice"));
				valuemap.put("PostpaidBilingCycle", cellValue.get("PostpaidBilingCycle"));
//				valuemap.put("MaxHoldDays", cellValue.get("MaxHoldDays"));
//				valuemap.put("MaxHoldAttempts", cellValue.get("MaxHoldAttempts"));
				valuemap.put("[Product:ProductType:OwnershipType:ReviseCharge]", cellValue.get("[Product:ProductType:OwnershipType:ReviseCharge]"));
				
				planMapList.add(valuemap);
			}
		}
//        System.out.println(planMapList);

		return planMapList;
	}
			

	private String getPrepaidPlanJson(Map<String, String> planDetails) {

		String jsonString = null;

		try {

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			CommonList commonList = new CommonList();
			JSONObject planJson = new JSONObject();

			// -- Prepaid Plan Information
			
			String planType = planDetails.get("Type");
			
			planJson.put("name", planDetails.get("PlanName"));
			planJson.put("displayName", planDetails.get("DisplayName"));
			planJson.put("code", planDetails.get("Code"));
			planJson.put("plantype", commonList.getCommonPlanType(planDetails.get("Type")));

			planJson.put("category", commonList.getCommonPlanCategory(planDetails.get("Category")));
			planJson.put("mode", planDetails.get("Mode").toUpperCase());
			planJson.put("planGroup", commonList.getCommonPlanGroup(planDetails.get("Group")));
			
			int serviceId = commonGetAPI.getServiceIdList(planDetails.get("Service")).get(0);
			planJson.put("serviceId", serviceId);
			planJson.put("serviceAreaIds", commonGetAPI.getServiceAreaIdList(planDetails.get("ServiceArea")));

			String accessibility = planDetails.get("Accessibility");
			planJson.put("accessibility", JSONObject.NULL);
			if(!"".equals(accessibility)) {
				accessibility = commonList.getCommonPlanAccessibility(accessibility);
				planJson.put("accessibility", accessibility);
			} 		
			
			
			String startDate = planDetails.get("StartDate");
			String endDate = planDetails.get("EndDate");
			startDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(startDate, "dd-MMM-yyyy", "yyyy-MM-dd");
			endDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(endDate, "dd-MMM-yyyy", "yyyy-MM-dd");
			planJson.put("startDate", startDate);
			planJson.put("endDate", endDate);
			planJson.put("currency",planDetails.get("Currency") );  // currency support
			
			String vlidty=planDetails.get("Validity");
			if(!"".equals(vlidty)) {
				planJson.put("validity", planDetails.get("Validity")); // --> commnt by sarfraz for testing only postpaid cust.
			}
			
			else {
				planJson.put("validity", JSONObject.NULL);
			}
			
         
		//	planJson.put("validity", Integer.parseInt(planDetails.get("Validity"))); // --> commnt by sarfraz for testing only postpaid cust.
		//	planJson.put("validity", planDetails.get("Validity")); // --> commnt by sarfraz for testing only postpaid cust.
			planJson.put("unitsOfValidity", planDetails.get("ValidityUnit"));
			planJson.put("status", planDetails.get("Status").toUpperCase());

			planJson.put("allowOverUsage", Boolean.valueOf(planDetails.get("AllowOverUsage")));
			planJson.put("bandwidth", "");
			planJson.put("maxconcurrentsession", Integer.parseInt(planDetails.get("MaxCurrentSession")));

//			planJson.put("maxHoldDurationDays", Integer.parseInt(planDetails.get("MaxHoldDays")));
//			planJson.put("maxHoldAttempts", Integer.parseInt(planDetails.get("MaxHoldAttempts")));


			
			planJson.put("usageQuotaType", planDetails.get("TOTAL"));  //-->ADD QUOTA USAGE TYPE
			
			String allowDiscount = planDetails.get("AllowDiscount");
			boolean allowDiscount1 = false;
			if((!"".equals(allowDiscount)) && (allowDiscount != null)) {
				if(allowDiscount.equalsIgnoreCase("Yes")) {
					allowDiscount1 = true;
				}
			}
			planJson.put("allowdiscount", allowDiscount1);
			planJson.put("desc", planDetails.get("Description"));

			// -- PrepaidPlan Quota Details
			boolean bandWidth = false;
			String serviceParamIds = commonGetAPI.getServiceParamIdsWithServiceId(serviceId);
			if(serviceParamIds.contains("1")) {
				bandWidth=true;
			}
			
			if(bandWidth) {
				
				String quotaType = planDetails.get("QuotaType");
				planJson.put("quotatype", quotaType);

				if ((quotaType.equalsIgnoreCase("Time")) || (quotaType.equalsIgnoreCase("Both"))) {
					planJson.put("quotatime", Integer.parseInt(planDetails.get("QuotaTime")));
					planJson.put("quotaunittime", planDetails.get("QuotaUnitTime").toUpperCase());
				}

				if ((quotaType.equalsIgnoreCase("Data")) || (quotaType.equalsIgnoreCase("Both"))) {
					planJson.put("quota", Integer.parseInt(planDetails.get("QuotaData")));
					planJson.put("quotaUnit", planDetails.get("QuotaUnitData"));
										
					
				}
				planJson.put("quotaResetInterval", planDetails.get("QuotaResetInterval"));
				planJson.put("usageQuotaType", "TOTAL");       //-->ADD QUOTA USAGE TYPE  -->it will call from sheet.
				
			} else {
				
				planJson.put("quotatype", "Data");
				planJson.put("quota", 1);
				planJson.put("quotaUnit", "GB");           //here we have to change gb,mb take from sheet
				planJson.put("quotaResetInterval", "Total");
			}
			
			
			// -- PrepaidPlan Additional Information
			
			String saccode = planDetails.get("SACCode");
			planJson.put("saccode", JSONObject.NULL);
			if (!"".equals(saccode)) {
				planJson.put("saccode",  saccode);
			}
			
			String qosPolicy = planDetails.get("QosPolicy");
			planJson.put("qospolicyid", "");
			if (!"".equals(qosPolicy)) {
				planJson.put("qospolicyid", commonGetAPI.getQosPolicyId(qosPolicy));
			}
			
			
			String timeBasePolicy = planDetails.get("TimeBasePolicy");
			planJson.put("timebasepolicyId", "");
			if (!"".equals(timeBasePolicy)) {
				planJson.put("timebasepolicyId", commonGetAPI.getTimeBasePolicyId(timeBasePolicy));
			}

			
			String param1 = planDetails.get("Param1");
			planJson.put("param1", JSONObject.NULL);
			if (!"".equals(param1)) {
				planJson.put("param1",  param1);
			}
			
			String param2 = planDetails.get("Param2");
			planJson.put("param2", JSONObject.NULL);
			if (!"".equals(param2)) {
				planJson.put("param2",  param2);
			}
			String param3 = planDetails.get("Param3");
			planJson.put("param3", JSONObject.NULL);
			if (!"".equals(param3)) {
				planJson.put("param3",  param3);
			}
			
			// -- PrepaidPlan Charge Details
			
			
			String billingCycle = "1";
			
			if (planType.equalsIgnoreCase("Postpaid")) {
				billingCycle = planDetails.get("PostpaidBilingCycle");
			}
			
			List<JSONObject> chargeJsonObjectList = new ArrayList<JSONObject>();

			float offerprice = 0.0f;
			String chargeNames[] = planDetails.get("ChargeName").split(",");

			for (int i = 0; i < chargeNames.length; i++) {

				String tempChargeName = chargeNames[i];

				int chargeId = commonGetAPI.getChargeId(tempChargeName);
				String taxAmountAndActualPrice = getChargeByIdAndTaxAmountAndActualPrice(chargeId);
				String ans[] = taxAmountAndActualPrice.split(":");
				float taxamount = Float.parseFloat(ans[0]);
				float actualprice = Float.parseFloat(ans[1]);

				offerprice = offerprice + taxamount + actualprice;

				JSONObject chargeListObject = new JSONObject();
				JSONObject chargeObject = new JSONObject();

				chargeObject.put("id", chargeId);
				chargeObject.put("actualprice", actualprice);
				chargeObject.put("taxamount", taxamount);
				chargeObject.put("price", actualprice);
				

				chargeListObject.put("billingCycle", billingCycle);
				chargeListObject.put("chargeprice", actualprice);
				chargeListObject.put("charge", chargeObject);
				chargeJsonObjectList.add(chargeListObject);

			}

			String tempOfferPrice = Utility.formattedDecimalNumber(offerprice);
			planJson.put("offerprice", tempOfferPrice);
			planJson.put("chargeList", chargeJsonObjectList);
			planJson.put("newOfferPrice", 0);

			String planCategory = planDetails.get("Category");
			if (planCategory.equalsIgnoreCase("Business Promotion")) {
				String newOfferPrice = planDetails.get("NewOfferPrice");
				if (!"".equals(newOfferPrice)) {
					float tempNewOfferPrice = Float.valueOf(newOfferPrice);
					String strNewOfferPrice = Utility.formattedDecimalNumber(tempNewOfferPrice);
					planJson.put("newOfferPrice", strNewOfferPrice);
				} else {
					planJson.put("newOfferPrice", tempOfferPrice);
				}

			}
			
			// -- PrepaidPlan Product Details
			
			List<JSONObject> productPlanMappingList = new ArrayList<JSONObject>();
			
			String productProductTypeOwnerShipReviseCharge = planDetails.get("[Product:ProductType:OwnershipType:ReviseCharge]");

			if (!"".equals(productProductTypeOwnerShipReviseCharge)) {
				
				productProductTypeOwnerShipReviseCharge = productProductTypeOwnerShipReviseCharge.replaceAll("[\\[\\]]", "");
				String temp[] = productProductTypeOwnerShipReviseCharge.split(",");

				for (int i = 0; i < temp.length; i++) {
					
					String productDetails[] = temp[i].split(":");
					String productName = productDetails[0];
					String productType =  productDetails[1];
					String ownershipType =  productDetails[2];

					String productAndBindPrductCategoryDetails = commonGetAPI.getProductAndProductCategoryDetails(productName);
					
					if(productAndBindPrductCategoryDetails != null) {
						
						String ans[] = productAndBindPrductCategoryDetails.split(":");
						int productId = Integer.parseInt(ans[0]);
						int productCategoryId = Integer.parseInt(ans[1]);
						//String productCategoryType = ans[2];
						
						JSONObject planProductMappingJson = new JSONObject();
						
						planProductMappingJson.put("productCategoryId", productCategoryId);
						planProductMappingJson.put("productId", productId);
						planProductMappingJson.put("product_type", productType);
						planProductMappingJson.put("ownershipType", ownershipType);
						planProductMappingJson.put("revisedCharge", JSONObject.NULL);
					
						if(ownershipType.equalsIgnoreCase("Sold")) {
							int reviseCharge =  Integer.parseInt(productDetails[3]);
							planProductMappingJson.put("revisedCharge", reviseCharge);
						}
						
						planProductMappingJson.put("id", JSONObject.NULL);
						planProductMappingJson.put("name", JSONObject.NULL);

						productPlanMappingList.add(planProductMappingJson);
						
					} else {
						String message = "Product Details not found with Customer Bind Type";
						ProductUtility.stopExecution(logFileName, logModuleName, message, productName);
					}
				}
			}
			
			planJson.put("product_category", JSONObject.NULL);
			planJson.put("product_type", JSONObject.NULL);
			planJson.put("ProductsType", JSONObject.NULL);
			
			
			planJson.put("productplanmappingList", productPlanMappingList);
			
			List<JSONObject> planCasMappingList = new ArrayList<JSONObject>();
			planJson.put("planCasMappingList", planCasMappingList);
			
			List<JSONObject> planQosMappingEntityList = new ArrayList<JSONObject>();
			planJson.put("planQosMappingEntityList", planQosMappingEntityList);
			
			jsonString = planJson.toString();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
//System.out.println(jsonString);
		return jsonString;
	}

	public String getChargeByIdAndTaxAmountAndActualPrice(int chargeId) {

		String tempURL = "cpm/charge/" + chargeId;
		String apiURL = getAPIURL(tempURL);

		JSONObject jsonResponse = httpGet(apiURL);

		int status = jsonResponse.getInt("status");
		String taxAmountAndActualPrice = "";

		if (status == 200) {
			JSONObject jsonObject = jsonResponse.getJSONObject("chargebyid");

			int taxAmount = jsonObject.getInt("taxamount");
			int actualprice = jsonObject.getInt("actualprice");
			taxAmountAndActualPrice = taxAmount + ":" + actualprice;
		}

		if ("".equals(taxAmountAndActualPrice)) {
			System.out.println("Taxamount and Actualprice details not found from chargeId - " + chargeId);
			Utility.printLog(logFileName, logModuleName, "Taxamount and Actualprice details not found from chargeId - ",
					String.valueOf(chargeId));
		}

		return taxAmountAndActualPrice;
	}

}
