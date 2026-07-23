package Act_Migration;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	import java.util.concurrent.TimeUnit;

	import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.util.Supplier;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
	import java.util.concurrent.atomic.AtomicInteger;
	import org.json.JSONObject;

import MigrationDataBase.DataBaseUpdateScript;
import api.ReadData;
	import api.RestExecution;
	import commons.CommonGetAPI;
	import commons.CommonList;
	import temp.UpdateSheet;
import utility.Constant;
import utility.ProductUtility;
import utility.ReadWriteExcelFile;
	import utility.Utility;
	import java.util.concurrent.CompletableFuture;
	import java.util.concurrent.CompletionException;
	

	/*************************************************************************************/

	public class ACT_Customer_Sheet extends RestExecution {

		private static String logFileName = "ActCustomer.log";
		private static String logModuleName = "CreateActCustomer";
		
		int thread_size = Constant.THREAD_POOL_SIZE;
		int batchSize=Constant.BATCH_SIZE;
		int retryLimit=Constant.RETRY_LIMIT; // remove  -->31 dec
		int retryDelayMS=Constant.RETRY_DELAY_MS; //remove --> 31 dec
		
		private static final AtomicInteger successCount = new AtomicInteger(0);
		private static final AtomicInteger failureCount = new AtomicInteger(0);

		private static XSSFWorkbook workbook = null;

		
		
		
		public void createPrepaidCustomer(Map<String, String> customerDetailsMap) {
		    try {
		        String rowIndex = customerDetailsMap.get("RowIndex");
		        String apiURL = getAPIURL("cpm/customers");

		        //1
		        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		        String apiBody = getPrepaidCustomerJson(customerDetailsMap);
		        //2
		        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		        if (apiBody != null && !apiBody.isEmpty()) {
		            StopWatch sw = new StopWatch();
		            sw.start();
		            JSONObject JSONResponseBody = null;
		            boolean success = false;

		            try {
		                JSONResponseBody = httpPost(apiURL, apiBody);
		                success = true; // Request successful

		                //3
		                String response = JSONResponseBody.toString(4);
		                Utility.printLog(logFileName, logModuleName, "Response", response);

		                //4
		                // String userName = customerDetailsMap.get("userName");
		                // ProductUtility.printResponse(JSONResponseBody, logModuleName, userName);

		            } catch (Exception e) {
		                Utility.printLog("execution.log", logModuleName, "ERROR",
		                        "API call failed: " + e.getMessage());
		            }

		            if (success && JSONResponseBody != null) {
		                sw.stop();
		                handleAPIResponse(JSONResponseBody, rowIndex, sw.getTime(), customerDetailsMap);
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        System.out.println("getting error in this method (createPrepaidCustomer)...... " + e.getMessage());
		    }
		}

		//removed retry 
		
		/*
		public void createPrepaidCustomer(Map<String, String> customerDetailsMap) {
			String rowIndex = customerDetailsMap.get("RowIndex");
			String apiURL = getAPIURL("cpm/customers");
			
			//1
			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);
			
			
			String apiBody = getPrepaidCustomerJson(customerDetailsMap);
//2
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);
			
			
			if (apiBody != null && !apiBody.isEmpty()) {
				StopWatch sw = new StopWatch();
				sw.start();
				JSONObject JSONResponseBody = null;
				boolean success = false;
				int attempts = 0;

				// Retry logic with exponential backoff
				while (attempts < retryLimit && !success) {
					try {
						
					 JSONResponseBody = httpPost(apiURL, apiBody);  
						
						
						success = true; // Request successful, break out of loop
						
						//3
						String response = JSONResponseBody.toString(4);
						Utility.printLog(logFileName, logModuleName, "Response", response);
						
						//4
//						String userName = customerDetailsMap.get("userName");
//						ProductUtility.printResponse(JSONResponseBody, logModuleName, userName);
						
					} catch (Exception e) {
						attempts++;
						if (attempts == retryLimit) {
							Utility.printLog("execution.log", logModuleName, "ERROR",
									"API call failed after retries: " + e.getMessage());
						}
						try {
							Thread.sleep(retryDelayMS * (long) Math.pow(2, attempts)); // Exponential backoff
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					}
				}

				if (success && JSONResponseBody != null) {
					sw.stop();
					handleAPIResponse(JSONResponseBody, rowIndex, sw.getTime(), customerDetailsMap);
					
					
				}
			}
		}  */
		// Handle API response
				private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime,
						Map<String, String> customerDetailsMap) {
					try {
						int status = response.getInt("status");
						String userName = customerDetailsMap.get("Username") + " - " + elapsedTime;

						if (!response.has("ERROR")) {
							if (status == 200) {
								successCount.incrementAndGet();
								String message = "New Customer added successfully - " + userName;
								System.out.println(message);
								Utility.printLog("execution.log", logModuleName, "Success", message);
								// Get Cust package id from resposnse-->
								int cprId = response.getJSONObject("customer").getJSONArray("planMappingList").getJSONObject(0)
										.getInt("id");
								int planMappingId = response.getJSONObject("customer").getJSONArray("planMappingList").getJSONObject(0)
										.getInt("custServiceMappingId");
								String columnAndValue = "cprid:" + cprId + "#" + "migrationstatus:Success";

								// get customer id here
								String customerId = response.getJSONObject("customer").get("id").toString();

								// here producer call sql producer will be call
								DataBaseUpdateScript dataBaseUpdateScript = new DataBaseUpdateScript();
							//	dataBaseUpdateScript.updateCustomerDataInDatabases(customerId, String.valueOf(cprId),String.valueOf(planMappingId),customerDetailsMap);


								//Update the cprid and migration status in sheet.
								UpdateSheet us = new UpdateSheet();
								us.setRowList(rowIndex, columnAndValue);

							} else if (status == 406) {
								String error = response.getString("responseMessage") + " - " + userName;
								System.out.println(error);
								Utility.printLog("execution.log", logModuleName, "Already Exists", error);
								failureCount.incrementAndGet();
							} else {
								failureCount.incrementAndGet();
								String error = "Error: " + response.get("ERROR") + " - " + userName;
								System.out.println(error);
								Utility.printLog("execution.log", logModuleName, "ERROR", error);
							}
						} else {
							failureCount.incrementAndGet();
							String message = response.get("ERROR") + " - " + userName;
							Utility.printLog("execution.log", logModuleName, "ERROR", message);
						}
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("getting error in this method (handleAPIResponse)...." + e.getMessage());
					}
				}

		// Handle API response 
				/*
		private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime,
				Map<String, String> customerDetailsMap) {
			int status = response.getInt("status");
			String userName = customerDetailsMap.get("Username") + " - " + elapsedTime;

			if (!response.has("ERROR")) {
				if (status == 200) {
					successCount.incrementAndGet();
					String message = "New Customer added successfully - " + userName;
					System.out.println(message);
					Utility.printLog("execution.log", logModuleName, "Success", message);
         // Get Cust package id from resposnse-->
					int cprId = response.getJSONObject("customer").getJSONArray("planMappingList").getJSONObject(0)
							.getInt("id"); 
					String columnAndValue = "cprid:" + cprId + "#" + "migrationstatus:Success";
					
					// here producer call sql producer will be call 
				   // sql conecction db mein data update ()
					
					
					
					//Update the cprid and migration status in sheet.
					UpdateSheet us = new UpdateSheet();
					us.setRowList(rowIndex, columnAndValue);
				
				} else if (status == 406) {
					String error = response.getString("responseMessage") + " - " + userName;
					System.out.println(error);
					Utility.printLog("execution.log", logModuleName, "Already Exists", error);
					failureCount.incrementAndGet();
				} else if (status==500){
					failureCount.incrementAndGet();
					String error = "Error: " + response.get("ERROR") + " - " + userName;
					System.out.println(error);
					Utility.printLog("execution.log", logModuleName, "ERROR", error);
				}
			} else {
				failureCount.incrementAndGet();
				String message = response.get("ERROR") + " - " + userName;
				Utility.printLog("execution.log", logModuleName, "ERROR", message);
			}
		}   */
/*   
		// Method to create postpaid customers in parallel
		public void createPrepaidCustomer(List<Map<String, String>> customerMapList) {
		//	int processors = Runtime.getRuntime().availableProcessors();
		//	System.err.println(processors);
			ExecutorService executorService = Executors.newFixedThreadPool(thread_size);  //-->thread read from dynamic
		//	ExecutorService executorService = Executors.newFixedThreadPool(processors);
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			UpdateSheet us = new UpdateSheet();
			
			us.setActiveSheetName("MigrationCustomerWithBaseUsaegs");     //here modifie the sheet name before --Customer and then ACustomer

			List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
			CommonGetAPI commonGetAPI = new CommonGetAPI();

			List<Callable<Void>> tasks = new ArrayList<>();

			// Split tasks for concurrent processing
			for (Map<String, String> customerDetails : customerMapList) {
				String userName = customerDetails.get("Username");
				String row = customerDetails.get("RowIndex");

				StopWatch sw = new StopWatch();
				sw.start();

				tasks.add(() -> {
					try {
						if (!commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
							Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, customerDetails.toString());
							createPrepaidCustomer(customerDetails); // Call API to create customer
						} else {
							System.out.println("Act Customer UserName already exists! - " + userName + " | " + sw.getTime());
						}

						// Batch Excel updates
						batchToWrite.add(customerDetails);

						// Write to Excel in batches
						if (batchToWrite.size() >= batchSize) {
							rw.setMultipleColumnInActiveSheet(batchToWrite);
							batchToWrite.clear(); // Clear the batch after writing
						}

					} catch (Exception e) {
						System.err.println("Error processing customer " + userName + ": " + e.getMessage());
					}
					return null;
				});
			}

			try {
				// Execute tasks in parallel
				executorService.invokeAll(tasks);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Error in task execution: " + e.getMessage());
			} finally {
				// Graceful shutdown of executor service
				executorService.shutdown();
				try {
					if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
						executorService.shutdownNow();
					}
				} catch (InterruptedException e) {
					executorService.shutdownNow();
				}

				// Write remaining batch to Excel
				if (!batchToWrite.isEmpty()) {
					rw.setMultipleColumnInActiveSheet(batchToWrite);
				}

				System.out.println("Final migration step completed.");
				System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
			}
		}*/
		public void createPrepaidCustomer(List<Map<String, String>> customerMapList) {
		    ExecutorService executorService = Executors.newFixedThreadPool(thread_size);  // Fixed thread pool size
		    ReadWriteExcelFile rw = new ReadWriteExcelFile();
		    UpdateSheet us = new UpdateSheet();
		    
		    us.setActiveSheetName("MigrationCustomerWithBaseUsaegs"); // Modify sheet name as required

		    List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>(); // Thread-safe list for batch processing
		    CommonGetAPI commonGetAPI = new CommonGetAPI();

		    List<Callable<Void>> tasks = new ArrayList<>();

		    // Split tasks for concurrent processing
		    for (Map<String, String> customerDetails : customerMapList) {
		        String userName = customerDetails.get("Username");
		        String row = customerDetails.get("RowIndex");

		        StopWatch sw = new StopWatch();
		        sw.start();

		        tasks.add(() -> {
		            try {
		                // Check if customer username already exists
		                if (!commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
		                    Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, customerDetails.toString());
		                    createPrepaidCustomer(customerDetails); // Call API to create customer
		                } else {
		                    System.out.println("Act Customer UserName already exists! - " + userName + " | " + sw.getTime());
		                }

		                // Batch Excel updates
		                batchToWrite.add(customerDetails);

		                // Write to Excel in batches
		                if (batchToWrite.size() >= batchSize) {
		                //   rw.setMultipleColumnInActiveSheett(batchToWrite); // Write batch to Excel
		                    batchToWrite.clear(); // Clear the batch after writing
		                }

		            } catch (Exception e) {
		                System.err.println("Error processing customer " + userName + ": " + e.getMessage());
		            }
		            return null;
		        });
		    }

		    try {
		        // Execute tasks in parallel
		        executorService.invokeAll(tasks);
		    } catch (InterruptedException e) {
		        Thread.currentThread().interrupt();
		        System.err.println("Error in task execution: " + e.getMessage());
		    } finally {
		        // Graceful shutdown of executor service
		        executorService.shutdown();
		        try {
		            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
		                executorService.shutdownNow();  // Force shutdown if tasks don't complete within 60 minutes
		            }
		        } catch (InterruptedException e) {
		            executorService.shutdownNow();
		        }

		        // Write remaining batch to Excel
		        if (!batchToWrite.isEmpty()) {
		           // rw.setMultipleColumnInActiveSheett(batchToWrite);
		        }

		        System.out.println("Final migration step completed.");
		        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
		    }
		}

		
		// Read customer data from Excel

				public List<Map<String, String>> readUniquePrepaidCustomerList() {

					String sheetName = "MigrationCustomerWithBaseUsaegs";  // This is sheet name.
					List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
					ReadData readData = new ReadData();
					sheetMap = readData.getActCustomerDataSheet(sheetName);

					Map<String, String> cellValue = new HashMap<String, String>();
					List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

					for (int i = 0; i < sheetMap.size(); i++) {

						Map<String, String> valuemap = new HashMap<String, String>();
						cellValue = sheetMap.get(i);

						String userName = cellValue.get("username");
						String mStatus = cellValue.get("migrationstatus");

						if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

							// Basic Details

							// sno	username	password	status	concurrentloginpolicy	radiuspolicy	additionalpolicy	param1	param2	param4	customeraltemailid	callingstationid	cui	macvalidation	msisdn	geolocation	param6	primarydns	secondarydns	primaryipv6dns	secondaryipc6dns	usedquota	startdate	enddate	cprid	migrationstatus
		// callingstationid   cui
							
							valuemap.put("RowIndex", cellValue.get("sno"));
							valuemap.put("Username", cellValue.get("username"));  
							valuemap.put("Password", cellValue.get("password"));
							valuemap.put("PrimaryMobile", cellValue.get("msisdn"));
							valuemap.put("Email", cellValue.get("customeraltemailid"));
							valuemap.put("BillDay", cellValue.get("additionalpolicy"));
							valuemap.put("Status", cellValue.get("status"));
							valuemap.put("Plan", cellValue.get("radiuspolicy"));
						
							// After Add ACt project  -->
							valuemap.put("FramedIPNetmask", cellValue.get("param2"));
							valuemap.put("Maxconcurrentsession", cellValue.get("concurrentloginpolicy"));
							valuemap.put("FramedIPAddress", cellValue.get("param1"));
							valuemap.put("Vlan_Id", cellValue.get("geolocation"));
							valuemap.put("GatewayIP", cellValue.get("param6"));
							valuemap.put("Mac_auth_enable", cellValue.get("macvalidation"));
							valuemap.put("NasPortId", cellValue.get("param4"));
							valuemap.put("PrimaryDNS", cellValue.get("primarydns"));
							valuemap.put("PrimaryIPv6DNS", cellValue.get("primaryipv6dns"));
							valuemap.put("SecondaryDNS", cellValue.get("secondarydns"));
							valuemap.put("SecondaryIPv6DNS", cellValue.get("secondaryipc6dns"));
							valuemap.put("callingstationid", cellValue.get("callingstationid"));
							valuemap.put("cui", cellValue.get("cui"));
							valuemap.put("macvalidation", cellValue.get("macvalidation"));
							valuemap.put("usedquota", cellValue.get("usedquota"));
							valuemap.put("startdate", cellValue.get("startdate"));
							valuemap.put("enddate", cellValue.get("enddate"));
							valuemap.put("cprid", cellValue.get("cprid"));
							customerMapList.add(valuemap);
						}
					}
					return customerMapList;
				}

		// @SuppressWarnings("unchecked")
		private String getPrepaidCustomerJson(Map<String, String> customerDetails) {

			String jsonString = null;

			try {

				JSONObject customerJsonObject = new JSONObject();
				CommonGetAPI commonGetAPI = new CommonGetAPI();
				//CommonList commonList = new CommonList();

			
				String customerType = "Postpaid";
				customerJsonObject.put("custtype", customerType);

				customerJsonObject.put("title", "Mr");
				customerJsonObject.put("firstname", customerDetails.get("Username"));
				customerJsonObject.put("lastname", customerDetails.get("Username"));
				customerJsonObject.put("username", customerDetails.get("Username"));

				customerJsonObject.put("password", customerDetails.get("Password"));
				customerJsonObject.put("countryCode", "+91");//Static
				 // Handle mobile number
                String mobNo = customerDetails.get("PrimaryMobile");
                customerJsonObject.put("mobile", (mobNo != null && !mobNo.isEmpty()) ? mobNo : "9999999999");
				
				customerJsonObject.put("phone", "");

				customerJsonObject.put("fax", ""); // --changes
				 // Handle email
                String email = customerDetails.get("Email");
                customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "act@123gmail.com");

				customerJsonObject.put("pan", "");
				customerJsonObject.put("contactperson", "Default");

				customerJsonObject.put("calendarType", "English");

				customerJsonObject.put("dunningCategory", "Silver");

				customerJsonObject.put("cafno", "");

				customerJsonObject.put("birthDate", JSONObject.NULL); // -->here we need to change date format.

				customerJsonObject.put("staffId", ""); // -->sar
			
				// Handle status
                String status = customerDetails.get("Status");
                switch (status != null ? status.toUpperCase() : "") {
                    case "Y":
                        customerJsonObject.put("status", "Active");
                        break;
                    case "N":
                        customerJsonObject.put("status", "In Active");
                        break;
                    case "SUSPEND":
                        customerJsonObject.put("status", "Suspend");
                        break;
                    default:
                        customerJsonObject.put("status", "Active");
                        break;
                }
				
				customerJsonObject.put("parentCustomerId", "");
				customerJsonObject.put("invoiceType", JSONObject.NULL);


				customerJsonObject.put("custlabel", "customer");
				customerJsonObject.put("salesremark", "");
				

				// *********** Service Area Details *****************

				int serviceAreaId = commonGetAPI.getServiceAreaIdList("ACT").get(0); //  here static service area
				customerJsonObject.put("serviceareaid", serviceAreaId);

				customerJsonObject.put("branch", JSONObject.NULL);
				customerJsonObject.put("partnerid", 1);

				String branchName = "ACT";


				if (!"".equals(branchName)) {
					int branchId = commonGetAPI.getBranchIdList(branchName).get(0);
					customerJsonObject.put("branch", branchId);
				} 
			
		
				// -- Customer Address Details --

				List<JSONObject> addressJsonObjectList = new ArrayList<JSONObject>();
				JSONObject presentAddressDetail = getCustomerAddressJson("Present", customerDetails);
				if (presentAddressDetail != null) {
					addressJsonObjectList.add(presentAddressDetail);
				}

				customerJsonObject.put("addressList", addressJsonObjectList);

				customerJsonObject.put("valleyType", "");
				customerJsonObject.put("customerArea", "");
				customerJsonObject.put("latitude", "");
				customerJsonObject.put("longitude", "");
				
					customerJsonObject.put("valleyType", "");

						customerJsonObject.put("customerArea", "");

				
					customerJsonObject.put("latitude", "");


				
					customerJsonObject.put("longitude", "");
				

				// ************ Network Location Details *********************

	

				customerJsonObject.put("oltid", "");
				customerJsonObject.put("masterdbid", "");
				customerJsonObject.put("splitterid", "");
				/*********** ACT **************************/
				customerJsonObject.put("primaryDNS", customerDetails.get("PrimaryDNS"));
				customerJsonObject.put("primaryIPv6DNS", customerDetails.get("PrimaryIPv6DNS"));
				customerJsonObject.put("secondaryDNS", customerDetails.get("SecondaryDNS"));
				customerJsonObject.put("secondaryIPv6DNS", customerDetails.get("SecondaryIPv6DNS"));
				/*************************************************/



				// -- Radius Service Details

				customerJsonObject.put("framedIp", customerDetails.get("FramedIPAddress"));
				customerJsonObject.put("framedIpBind", "");
				customerJsonObject.put("nasPort", JSONObject.NULL);
				customerJsonObject.put("ipPoolNameBind", "");

				customerJsonObject.put("failcount", 0);
				customerJsonObject.put("isCustCaf", "no");
				customerJsonObject.put("servicetype", "");

				customerJsonObject.put("isParentLocation", "");
				customerJsonObject.put("locations", JSONObject.NULL);
				customerJsonObject.put("maxconcurrentsession", customerDetails.get("Maxconcurrentsession"));
				customerJsonObject.put("nasIpAddress", "");
				customerJsonObject.put("nasPort", "");

				customerJsonObject.put("altmobile", "");
				customerJsonObject.put("billableCustomerId", "");

				if (customerType.equalsIgnoreCase("Postpaid")) {
					String billDay = customerDetails.get("BillDay");
	

					if (!"".equals(billDay)) {
						int intBillDay = Integer.parseInt(billDay);
						customerJsonObject.put("billday", intBillDay);
					}

						customerJsonObject.put("earlybillday", 0);

				}
				customerJsonObject.put("gst", "");
				customerJsonObject.put("aadhar", "");
				customerJsonObject.put("addparam1", "");
				customerJsonObject.put("addparam2", "");
				customerJsonObject.put("addparam3", "");
				customerJsonObject.put("addparam4", "");

				customerJsonObject.put("passportNo", "");
				customerJsonObject.put("tinNo", "");
				customerJsonObject.put("parentQuotaType", "");
				JSONObject paymentJson = new JSONObject();

				paymentJson.put("amount", 0);
				paymentJson.put("paymode", "");
				paymentJson.put("referenceno", "");
				paymentJson.put("paymentdate", "");
				customerJsonObject.put("paymentDetails", paymentJson);

				// --PlanMappingDetails
				customerJsonObject.put("istrialplan", false);

				List<JSONObject> planJsonObjectList = new ArrayList<JSONObject>();
				customerJsonObject.put("planMappingList", planJsonObjectList);

				String planCategory = "Individual";

				// --Individual Plan
				if (planCategory.equalsIgnoreCase("Individual")) {

				
					String billableCustomerId = null;
					float discountPercentage = 0;
					String discountType = null;
					String discountExpiryDate = null;
					boolean invoiceToOrg = false;
					boolean istrialplan = false;
					customerJsonObject.put("billableCustomerId", "");
					//customerJsonObject.put("billTo", "CUSTOMER");

					customerJsonObject.put("discount", 0);
					customerJsonObject.put("discountType", "One-time");
					customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
					customerJsonObject.put("planPurchaseType", "individual");
					customerJsonObject.put("vlan_id", customerDetails.get("Vlan_Id"));                           
					customerJsonObject.put("istrialplan", istrialplan);

					// Add pojo for Act-->

					// --Plan service List Details
	
						
					//	String service = null;
						String service = "BroadBand";   // ----------------------------> Add service Static
						
						
						// hare if plan is null then plan ="Default_plan"
						
						//String plan = null;
						
						//String plan = customerDetails.get("Plan"); // ----------------->here add.
						
						String plan = "";
						if(!"".equals(customerDetails.get("Plan"))){
							plan=customerDetails.get("Plan");
						}
						else {
							plan="Default";
						}
							
						
						discountType = "";
						discountExpiryDate = "";
						String tempDiscountPercentage = "";

						JSONObject planDetailJsonObject = new JSONObject();

						int planId = commonGetAPI.getPlanId(plan);
						int serviceId = commonGetAPI.getServiceId(service);

						String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

						String serviceName = planDetails[0];
						float offerPrice = Float.valueOf(planDetails[1]);
						int validity = Integer.parseInt(planDetails[2]);

						float flatAmount = offerPrice;

						

						flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
						// customerJsonObject.put("flatAmount", flatAmount);
						customerJsonObject.put("flatAmount", "");

						planDetailJsonObject.put("newAmount", "");
						float newAmount = offerPrice;



						planDetailJsonObject.put("planId", planId);
						planDetailJsonObject.put("service", serviceName);
						planDetailJsonObject.put("validity", validity);
						planDetailJsonObject.put("discount", discountPercentage);
						planDetailJsonObject.put("billTo", "CUSTOMER");
						planDetailJsonObject.put("billableCustomerId", "");

						planDetailJsonObject.put("newAmount", "");
						planDetailJsonObject.put("offerPrice", offerPrice);
						planDetailJsonObject.put("invoiceType", "");
						planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);
						planDetailJsonObject.put("istrialplan", JSONObject.NULL);
						planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
						planDetailJsonObject.put("discountType", discountType);
						planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
						planDetailJsonObject.put("serviceId", serviceId);

						planDetailJsonObject.put("serialNumber", "");
						planDetailJsonObject.put("discountType", "One-time");

						planJsonObjectList.add(planDetailJsonObject);

					}
					customerJsonObject.put("planMappingList", planJsonObjectList);
					customerJsonObject.put("isInvoiceToOrg", false);
				

				// --Plan Group
				customerJsonObject.put("plangroupid", JSONObject.NULL);


				
				
				customerJsonObject.put("voicesrvtype", "");
				customerJsonObject.put("didno", "");

				// a add key value by sarfraz -->
				customerJsonObject.put("customerSector", "");
				customerJsonObject.put("customerSubSector", "");
				customerJsonObject.put("customerSubType", "");
				customerJsonObject.put("customerType", "");
				customerJsonObject.put("department", "");
				customerJsonObject.put("discount", JSONObject.NULL);
				customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
				customerJsonObject.put("discountType", JSONObject.NULL);
				customerJsonObject.put("framedIpv6Address", "");

				// Add pojo for Act-->
				customerJsonObject.put("delegatedprefix", "");
				customerJsonObject.put("earlybilldate", "");
				customerJsonObject.put("framedIPNetmask", customerDetails.get("FramedIPNetmask"));
				customerJsonObject.put("framedIPv6Prefix", "");
				customerJsonObject.put("framedroute", "");
				customerJsonObject.put("gatewayIP", customerDetails.get("GatewayIP")); // gateway

				customerJsonObject.put("macRetentionPeriod", "7");  //static 
				customerJsonObject.put("macRetentionUnit", "DAY");  //static
				
				
				
				 // Handle NAS Port ID
                String nasPortId = extractValueFromRegex(customerDetails.get("NasPortId"), "\\[([^\\]]+)\\]");
                customerJsonObject.put("nasPortId", nasPortId != null ? nasPortId : customerDetails.get("NasPortId"));
				
                
                // i have add above simple logic
				/*
				    // here give code for remove regex-->
		     		String data=customerDetails.get("NasPortId");
				    //  String data = "0:92=\"[*3016:380*,*111 vlan*]\"";
		     		if ((!"".equals(data)) && (data != null)) {
			        // Use regular expression to extract the part inside the square brackets
			        String regex = "\\[([^\\]]+)\\]";
			        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
			        java.util.regex.Matcher matcher = pattern.matcher(data);

			        if (matcher.find()) {
			            // Extract the matched group
			            String extracted = matcher.group(1);
			        //    System.out.println("Extracted data: " + extracted);
			            
			            customerJsonObject.put("nasPortId", extracted);
			            
			        } else {
			          //  System.out.println("No match found.");
			        }
				
		     		}
		     		
		     		else {
		     			customerJsonObject.put("nasPortId", customerDetails.get("NasPortId"));
		     		}
				
				*/  // comment add above simple logic
				
			String mac_auth   =	customerDetails.get("Mac_auth_enable");
				if(mac_auth.equalsIgnoreCase("Y")) {
				customerJsonObject.put("mac_auth_enable", true);
				customerJsonObject.put("mac_provision", true);
				}
				
				else {
					customerJsonObject.put("mac_auth_enable", false);
					customerJsonObject.put("mac_provision", false);
				}
				// ------>

				// -- Over Direct Charge Mapping

				List<JSONObject> chargeJsonObjectList = new ArrayList<JSONObject>();
				customerJsonObject.put("overChargeList", chargeJsonObjectList);

				// sarfraz -->
				List<JSONObject> customerLocationsJsonObject = new ArrayList<JSONObject>();
				customerJsonObject.put("customerLocations", customerLocationsJsonObject);

				// --Customer MAC Addresses Mapping

				List<JSONObject> macJsonObjectList = new ArrayList<JSONObject>();
				customerJsonObject.put("custMacMapppingList", macJsonObjectList);

				List<JSONObject> mappingJsonObjectList = new ArrayList<JSONObject>();
				customerJsonObject.put("custIpMappingList", mappingJsonObjectList);
				jsonString = customerJsonObject.toString();

			} // take care of this brace
			catch (Exception e) {
				e.printStackTrace();
			}

			return jsonString;
		}

		/*
		@SuppressWarnings("unchecked")
		private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject addressDetailJsonObject = new JSONObject();

			boolean result = false;
			String addressType1 = "";
			String address = "";
			String landmark = "";
			String ward = "";
			String municipality = "";

			
			//Static i have add adresss
			if (addressType.equalsIgnoreCase("Present")) {
				/*I have add static adress here  */
			/*	addressType1 = "Present";
				address = "ACT";
				landmark = "ACT";
				ward = "ACT";   
				municipality = "500079";

				if ((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
					result = true;
				}

			} 

			if (result) {

				String wardHeirarchyDetail = commonGetAPI.getWardHierarchyDetailsByWardName(ward, municipality);
				String detail[] = wardHeirarchyDetail.split(":");

				int wardId = Integer.parseInt(detail[0]);
				int pincodeId = Integer.parseInt(detail[1]);
				int cityId = Integer.parseInt(detail[2]);
				int stateId = Integer.parseInt(detail[3]);
				int countryId = Integer.parseInt(detail[4]);

				addressDetailJsonObject.put("addressType", addressType1);
				addressDetailJsonObject.put("landmark", address);
				// addressDetailJsonObject.put("landmark1", landmark);
				addressDetailJsonObject.put("landmark1", JSONObject.NULL);
				addressDetailJsonObject.put("areaId", wardId);
				addressDetailJsonObject.put("pincodeId", pincodeId);
				addressDetailJsonObject.put("cityId", cityId);
				addressDetailJsonObject.put("stateId", stateId);
				addressDetailJsonObject.put("countryId", countryId);
				addressDetailJsonObject.put("version", "NEW");

			} else {
				addressDetailJsonObject = null;
			}

			return addressDetailJsonObject;
		

	}
		
		*/
		
		 private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {
	            CommonGetAPI commonGetAPI = new CommonGetAPI();
	            JSONObject addressDetailJsonObject = new JSONObject();

	            if ("Present".equalsIgnoreCase(addressType)) {
	                String address = "ACT";
	                String landmark = "ACT";
	                String ward = "ACT";
	                String municipality = "500079";

	                if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() && !municipality.isEmpty()) {
	                    String wardHeirarchyDetail = commonGetAPI.getWardHierarchyDetailsByWardName(ward, municipality);
	                    String[] detail = wardHeirarchyDetail.split(":");

	                    addressDetailJsonObject.put("addressType", addressType);
	                    addressDetailJsonObject.put("landmark", address);
	                    addressDetailJsonObject.put("landmark1", JSONObject.NULL);
	                    addressDetailJsonObject.put("areaId", Integer.parseInt(detail[0]));
	                    addressDetailJsonObject.put("pincodeId", Integer.parseInt(detail[1]));
	                    addressDetailJsonObject.put("cityId", Integer.parseInt(detail[2]));
	                    addressDetailJsonObject.put("stateId", Integer.parseInt(detail[3]));
	                    addressDetailJsonObject.put("countryId", Integer.parseInt(detail[4]));
	                    addressDetailJsonObject.put("version", "NEW");
	                } else {
	                    return null;
	                }
	            }

	            return addressDetailJsonObject;
	        }

		
		private String extractValueFromRegex(String input, String regex) {
            if (input == null || regex == null) return null;

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(input);

            return matcher.find() ? matcher.group(1) : null;
        }
	}
