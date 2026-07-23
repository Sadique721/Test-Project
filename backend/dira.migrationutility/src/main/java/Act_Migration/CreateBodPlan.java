package Act_Migration;


	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

	import api.ReadData;
	import api.RestExecution;
	import commons.CommonGetAPI;
	import commons.CommonList;
	import utility.ProductUtility;
	import utility.Utility;

	public class CreateBodPlan extends RestExecution {

		private static String logFileName = "Bandwidth.log";
		private static String logModuleName = "BandwidthPlan";

	   /* 
		private void createPrepaidPlan(Map<String, String> planDetailMap) {

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
		}   
		    
		    
		    
		/* Original method without thread*/
/*
		public void createPrepaidPlan(List<Map<String, String>> planMapList) {

			for (int i = 0; i < planMapList.size(); i++) {

				Map<String, String> map = new HashMap<String, String>();
				map = planMapList.get(i);
				Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
				createPrepaidPlan(map);
			}
		}  
		*/
		
		
		
		// This method handles the creation of prepaid plans in parallel using threads
	    public void createPrepaidPlanInParallel(List<Map<String, String>> planMapList) {

	        // Thread pool setup to handle parallel tasks
	        int numThreads = 2;  // Adjust the number of threads based on system capabilities
	        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

	        List<Callable<Void>> tasks = new ArrayList<>();
	        
	        for (Map<String, String> planMap : planMapList) {
	            tasks.add(() -> {
	                createPrepaidPlan(planMap); // Process each plan in a separate thread
	                return null; // Void return as we're not returning anything from this task
	            });
	        }

	        try {
	            // Execute all tasks in parallel
	            executorService.invokeAll(tasks);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            Utility.printLog(logFileName, logModuleName, "Error", "Thread execution interrupted: " + e.getMessage());
	        } finally {
	            executorService.shutdown();
	        }
	    }

	    // Method to handle the creation of a single prepaid plan (unchanged from the original code)
	    private void createPrepaidPlan(Map<String, String> planDetailMap) {

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
		}   

	    // Read the prepaid plans from the sheet (unchanged)
	    public List<Map<String, String>> readUniquePrepaidPlanList() {
	        String sheetName = "bod";
	        List<Map<String, String>> sheetMap = new ArrayList<>();
	        ReadData readData = new ReadData();
	        sheetMap = readData.getActPlan(sheetName);

	        List<Map<String, String>> planMapList = new ArrayList<>();
	        for (Map<String, String> cellValue : sheetMap) {

				Map<String, String> valuemap = new HashMap<String, String>();
				//cellValue = sheetMap.get(i);

				String planName = cellValue.get("PACKAGE_NAME");
				if ((!"".equals(planName)) && (planName != null)) {

					valuemap.put("RowIndex", cellValue.get("No"));
					valuemap.put("PlanName", cellValue.get("PACKAGE_NAME"));
				
					valuemap.put("QuotaData", cellValue.get("QUOTA"));
					valuemap.put("QuotaUnitData", cellValue.get("QUOTA_UNIT"));
					valuemap.put("QuotaUsageType", cellValue.get("QUOTA_TYPE"));
     				valuemap.put("QosPolicy", cellValue.get("QOS_NAME"));
     				valuemap.put("VALIDITY", cellValue.get("VALIDITY"));
     				valuemap.put("VALIDITY_UNIT", cellValue.get("VALIDITY_UNIT"));
     				
     				
					planMapList.add(valuemap);
				}
			}
			return planMapList;
		}
				

		private String getPrepaidPlanJson(Map<String, String> planDetails) {

			String jsonString = null;

			try {

				CommonGetAPI commonGetAPI = new CommonGetAPI();
				CommonList commonList = new CommonList();
				JSONObject planJson = new JSONObject();

				// -- Prepaid Plan Information
				
				String planType = "postpaid";
				
				planJson.put("name", planDetails.get("PlanName"));
				planJson.put("displayName", planDetails.get("PlanName"));
				planJson.put("code", planDetails.get("PlanName"));
				planJson.put("plantype", commonList.getCommonPlanType(planType));

				planJson.put("category","Normal");
				planJson.put("mode", "NORMAL");
				planJson.put("planGroup","Bandwidthbooster");
				
				int serviceId = commonGetAPI.getServiceIdList("BroadBand").get(0);   // I have add static service 
				planJson.put("serviceId", serviceId);
				planJson.put("serviceAreaIds", commonGetAPI.getServiceAreaIdList("ACT"));  // I have add static service_area .

				
					planJson.put("accessibility", "");
						
				
				
				String startDate = "3-Sep-2020";
				String endDate = "31-Dec-2030";
				startDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(startDate, "dd-MMM-yyyy", "yyyy-MM-dd");
				endDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(endDate, "dd-MMM-yyyy", "yyyy-MM-dd");
				planJson.put("startDate", startDate);
				planJson.put("endDate", endDate);
				
				
				
					planJson.put("validity", Integer.parseInt(planDetails.get("VALIDITY"))); // --> commnt by sarfraz for testing only postpaid cust.
			
					
			
					//for DAY val unit 
					String unitVal=planDetails.get("VALIDITY_UNIT");
					if(unitVal.contains("DAY") || unitVal.contains("day") ||unitVal.contains("Day")) {
						planJson.put("unitsOfValidity", "Days");
						
					}
					

					else if(unitVal.contains("MONTH") || unitVal.contains("Month") ||unitVal.contains("month")) {
						planJson.put("unitsOfValidity", "Months");
						
					}
					
					else if(unitVal.contains("HOUR") || unitVal.contains("Hour") ||unitVal.contains("hour")) {
						planJson.put("unitsOfValidity", "Hours");
						
					}
					else if(unitVal.contains("YEAR") || unitVal.contains("Year") ||unitVal.contains("year")) {
						planJson.put("unitsOfValidity", "Years");
						
					}
					else {
						planJson.put("unitsOfValidity", "Days");
					}
					
					

	         
			//	planJson.put("validity", Integer.parseInt(planDetails.get("Validity"))); // --> commnt by sarfraz for testing only postpaid cust.
			
				planJson.put("status", "Active");

				planJson.put("allowOverUsage", true);
				planJson.put("bandwidth", "");
				planJson.put("maxconcurrentsession", 5);
				
				//planJson.put("usageQuotaType", planDetails.get("TOTAL"));  //-->ADD QUOTA USAGE TYPE
			
				planJson.put("allowdiscount", false);
				planJson.put("desc", planDetails.get("PlanName"));

				// -- PrepaidPlan Quota Details
				boolean bandWidth = false;
				String serviceParamIds = commonGetAPI.getServiceParamIdsWithServiceId(serviceId);
				if(serviceParamIds.contains("1")) {
					bandWidth=true;
				}
				
				if(bandWidth) {
					
					String quotaType = "Data";   // static i have put Data
					planJson.put("quotatype", quotaType);
                    String quota=planDetails.get("QuotaData");
					
					if ((quotaType.equalsIgnoreCase("Data"))) {
						
						planJson.put("quotaUnit", JSONObject.NULL);
						
						if ((!"".equals(quota)) && (quota != null)) {
						planJson.put("quota", Integer.parseInt(planDetails.get("QuotaData")));
						}
						
						planJson.put("quotaUnit", planDetails.get("QuotaUnitData"));
						planJson.put("quotaResetInterval", "Monthly");
						planJson.put("usageQuotaType", planDetails.get("QuotaUsageType"));  
					}
					
				}
				// -- PrepaidPlan Additional Information
				
				
				planJson.put("saccode", JSONObject.NULL);
				
				String qosPolicy = planDetails.get("QosPolicy");
				planJson.put("qospolicyid", "");
				if (!"".equals(qosPolicy)) {
					planJson.put("qospolicyid", commonGetAPI.getQosPolicyId(qosPolicy));
				}
				
				

				
				
				planJson.put("param1", JSONObject.NULL);
				
				
				
				planJson.put("param2", JSONObject.NULL);
				
				
				planJson.put("param3", JSONObject.NULL);
				
				
				// -- PrepaidPlan Charge Details
				
				
				String billingCycle = "1";
				
				
				
				List<JSONObject> chargeJsonObjectList = new ArrayList<JSONObject>();

				float offerprice = 0.0f;
				String chargeNames[] = "Booster_Charge".split(","); ///static charge i have add.

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

			/*	String planCategory = planDetails.get("Category");
				if (planCategory.equalsIgnoreCase("Business Promotion")) {
					String newOfferPrice = planDetails.get("NewOfferPrice");
					if (!"".equals(newOfferPrice)) {
						float tempNewOfferPrice = Float.valueOf(newOfferPrice);
						String strNewOfferPrice = Utility.formattedDecimalNumber(tempNewOfferPrice);
						planJson.put("newOfferPrice", strNewOfferPrice);
					} else {
						planJson.put("newOfferPrice", tempOfferPrice);
					}

				}  */
				
				// -- PrepaidPlan Product Details
				
				
				/*
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
				} */
				List<JSONObject> productPlanMappingList = new ArrayList<JSONObject>();
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






