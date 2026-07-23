package tumil;

import utility.Constant;
import utility.DateFormatter;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MigrationDataBase.DataBaseUpdateScript;
import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import groovy.util.logging.Slf4j;
import temp.UpdateSheet;
public class Tumil_Customer extends RestExecution{
	
		
		
			private final String urlCommon = Constant.URLCOMMON;
			private final String jdbcUrl1 = Constant.URLCONVERGE;
			private final String jdbcUrl2 = Constant.URLREVENUE;
			private final String dbUser = Constant.USERNAME;
			private final String dbPassword = Constant.PASSWORD;

			int iterationCounter = 0;
			private static final Logger log = LoggerFactory.getLogger(Tumil_Customer.class);

			private static String logFileName = "savanna.log";
			private static String logModuleName = "CreatesavanaCustomer";

			int thread_size = Constant.THREAD_POOL_SIZE;
			int batchSize=Constant.BATCH_SIZE;
			int retryLimit=Constant.RETRY_LIMIT; // remove  -->31 dec
			int retryDelayMS=Constant.RETRY_DELAY_MS; //remove --> 31 dec

			private static final AtomicInteger successCount = new AtomicInteger(0);
			private static final AtomicInteger failureCount = new AtomicInteger(0);

			private static XSSFWorkbook workbook = null;
//			ExecutorService executor = Executors.newCachedThreadPool();
			 ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Use fixed thread pool

			    public ArrayList<Object> createPrepaidCustomer(Map<String, String> customerDetailsMap, Map <String, Integer> serviceAreaIdAll) {
			        ArrayList<Object> objects = new ArrayList<>();
			        try {
			            String rowIndex = customerDetailsMap.get("RowIndex");
			            String apiURL = getAPIURL("cpm/customers");

			            Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			            String apiBody = getPrepaidCustomerJson(customerDetailsMap, serviceAreaIdAll);
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
			                        success = true;

			                        String response = JSONResponseBody.toString(4);
			                        Utility.printLog(logFileName, logModuleName, "Response", response);

			                    } catch (Exception e) {
			                        attempts++;
			                        if (attempts == retryLimit) {
			                            Utility.printLog("execution.log", logModuleName, "ERROR", "API call failed after retries: " + e.getMessage());
			                        }
			                        try {
			                            Thread.sleep(retryDelayMS * (long) Math.pow(2, attempts)); // Exponential backoff
			                        } catch (InterruptedException ie) {
			                            Thread.currentThread().interrupt();
			                        }
			                    }
			                }
			                objects.add(JSONResponseBody);
			                objects.add(rowIndex);
			                objects.add(sw);
			                objects.add(customerDetailsMap);
			                return objects;
			            }
			        } catch (Exception e) {
			            e.printStackTrace();
			            System.out.println("getting error in this method (createPrepaidCustomer)...... " + e.getMessage());
			            failureCount.incrementAndGet();
			        }
			        return objects;
			    }

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
	
			                    // enhanced 
			                 // Extract the customer object once
			                    JSONObject customer = response.getJSONObject("customer");

			                    // Extract the first element from planMappingList
			                    JSONArray planMappingList = customer.getJSONArray("planMappingList");
			                    JSONObject firstPlanMapping = planMappingList.getJSONObject(0);

			                    // Extract the required values
      		                    int cprId = firstPlanMapping.getInt("id");
 			                    int planMappingId = firstPlanMapping.getInt("custServiceMappingId");
      		                    String customerId = customer.get("id").toString();
			                    String createdbyname = customer.get("createdByName").toString();
			                    String createdbyid = customer.get("createdById").toString();

			                    // Create the column and value string
			                    String columnAndValue = "cprid:" + cprId + "#" + "MigrationStatus:Success";

			              
			                    
			         // here i have close for tumil only           

			                    try (Connection converge = DriverManager.getConnection(jdbcUrl1, dbUser, dbPassword); 
			                         Connection radius = DriverManager.getConnection(jdbcUrl2, dbUser, dbPassword)) {
			                    	TumilConvergeUpdate dataBaseUpdateScript = new TumilConvergeUpdate();
			                       dataBaseUpdateScript.updateCustomerDataInDatabases(converge, radius, customerId, String.valueOf(cprId), String.valueOf(planMappingId), customerDetailsMap,createdbyname,createdbyid);
			                        log.info("****************************************Success With Count :::: {}", successCount.get());
			                        System.out.println("****************************************Success With Count :::: {}"+ successCount.get());
			                    } catch (SQLException e) {
			                        e.printStackTrace();
			                        log.error("Error During Database Update: " + e.getMessage());
			                        System.out.println("Error During Database Update: " + e.getMessage());
			                    }

			                    UpdateSheet us = new UpdateSheet();
			                    us.setRowList(rowIndex, columnAndValue);
			                } else if (status == 406) {
			                    String error = response.getString("responseMessage") + " - " + userName;
			                    System.out.println(error);
			                    Utility.printLog("execution.log", logModuleName, "Already Exists", error);
			                    failureCount.incrementAndGet();
			                } else {
			                    failureCount.incrementAndGet();
			                    String message = response.get("ERROR") + " - " + userName;
			                    Utility.printLog("execution.log", logModuleName, "ERROR", message);
			                }
			            } else {
			                failureCount.incrementAndGet();
			                String message = response.get("ERROR") + " - " + userName;
			                Utility.printLog("execution.log", logModuleName, "ERROR", message);
			            }
			        } catch (Exception e) {
			            e.printStackTrace();
			            System.out.println("getting error in this method (handleAPIResponse)...." + e.getMessage());
			        }
			    }

			    
			    public void createPrepaidCustomer(List<Map<String, String>> customerMapList,   Map<String, Integer> serviceAreaIdAll) {
			        ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Thread pool size is dynamically set
			        ReadWriteExcelFile rw = new ReadWriteExcelFile();
					UpdateSheet us = new UpdateSheet();

					us.setActiveSheetName("Customer");
			        List<Map<String, String>> batchToWrite = new CopyOnWriteArrayList<>();
			        CommonGetAPI commonGetAPI = new CommonGetAPI();

			        List<Future<ArrayList<Object>>> futures = new ArrayList<>();

			        // Submit tasks for concurrent processing
			        for (Map<String, String> customerDetails : customerMapList) {
			            String userName = customerDetails.get("Username");
			            String row = customerDetails.get("RowIndex");

			            futures.add(executorService.submit(() -> {
			                StopWatch sw = new StopWatch();
			                sw.start();
			                ArrayList<Object> result = new ArrayList<>();

			                try {
			                    if (!commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
			                        Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, customerDetails.toString());
			                        result = createPrepaidCustomer(customerDetails, serviceAreaIdAll); // Call API to create customer
			                    } else {
			                        System.out.println("tumil Customer UserName already exists! - " + userName + " | " + sw.getTime());
			                    }

			                    // Batch Excel updates
			                    batchToWrite.add(customerDetails);

			                    // Write to Excel in batches
			                    if (batchToWrite.size() >= batchSize) {
			                        //rw.setMultipleColumnInActiveSheet(batchToWrite);
			                        batchToWrite.clear(); // Clear the batch after writing
			                    }
			                } catch (Exception e) {
			                    failureCount.incrementAndGet();
			                    System.err.println("Error processing customer " + userName + ": " + e.getMessage());
			                }

			                return result;
			            }));
			        }

			        // Wait for all tasks to complete
			        for (Future<ArrayList<Object>> future : futures) {
			            try {
			                ArrayList<Object> objects = future.get(); // Retrieve the result of the task
			                JSONObject jsonObject = (JSONObject) objects.get(0);
			                String rowIndex = (String) objects.get(1);
			                StopWatch stopWatch = (StopWatch) objects.get(2);
			                Map<String, String> customerDetailsMap = (Map<String, String>) objects.get(3);
			                handleAPIResponse(jsonObject, rowIndex, stopWatch.getTime(), customerDetailsMap);
			                System.out.println("Task Completed Successfully: " + future.isDone());
			            } catch (Exception e) {
			                System.err.println("Error retrieving task result: " + e.getMessage());
			            }
			        }

			        // Gracefully shut down executor service
			        executorService.shutdown();
			        try {
			            if (!executorService.awaitTermination(90, TimeUnit.MINUTES)) {
			                executorService.shutdownNow();
			            }
			        } catch (InterruptedException e) {
			            executorService.shutdownNow();
			        }

			        // Write remaining batch to Excel
			        if (!batchToWrite.isEmpty()) {
			          //  ReadWriteExcelFile rw = new ReadWriteExcelFile();
			          //  rw.setMultipleColumnInActiveSheet(batchToWrite);
			        }

			        System.out.println("Final migration step completed.");
			        System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
			    }
			



			    public List<Map<String, String>> readUniquePrepaidCustomerList() {

					String sheetName = "Customer";  // This is sheet name.
					List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
					ReadData readData = new ReadData();
					sheetMap = readData.getTumilCustomerDataSheet(sheetName);

					Map<String, String> cellValue = new HashMap<String, String>();
					List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

					for (int i = 0; i < sheetMap.size(); i++) {

						Map<String, String> valuemap = new HashMap<String, String>();
						cellValue = sheetMap.get(i);

						String userName = cellValue.get("Username");
						String mStatus = cellValue.get("MigrationStatus");

						if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {
							
							// here date format inpot should be 10/06/2022	14/06/2022 00:00

						
							String resiteredDate= DateFormatter.formatToStandard(cellValue.get("Registered"));// Test with a valid dat       
							String startDatetemp=DateFormatter.formatToStandard(cellValue.get("Renewed")); // date format should be 10/06/2022	14/06/2022 00:00

							String lastLogingDate=DateFormatter.formatToStandard(cellValue.get("LastLogin"));
							
						//	String endDate = "";

							// Check if the "Expires" key exists in the map and if the value is not null or empty
						/*	if (StringUtils.isNotEmpty(cellValue.get("Expires"))) {
							    // If it's not empty or null, convert the date format using the Utility method
							    endDate = Utility.convertEndDateFormat(cellValue.get("Expires"));
							}   */
							
							// Pincode  chnange
							//Area  change
						//	Mobile  change
						//	Createdby  -- add
							
							// remove
							//	SubArea	BuildingName	BuildingNumber
							 // Updates	AccountNo		


							
							valuemap.put("RowIndex", cellValue.get("Sno"));
							valuemap.put("Title", cellValue.get("Title"));
							valuemap.put("Name", cellValue.get("Name"));
							valuemap.put("Username", cellValue.get("UserName"));  
							valuemap.put("Password", cellValue.get("Password"));
							// Mobile --primery
							
							valuemap.put("PrimaryMobile", cellValue.get("Mobile"));
							// sec phone
							valuemap.put("SecondryPhone", cellValue.get("Phone"));
							valuemap.put("Email", cellValue.get("Email"));
							valuemap.put("Servicearea", cellValue.get("Servicearea"));
					
							valuemap.put("Status", cellValue.get("Status"));
							valuemap.put("Plan", cellValue.get("Plan"));
							
						// Updates
						//	valuemap.put("Notes", cellValue.get("Updates"));
							// After Add ACt project  -->
							valuemap.put("IpAddress", cellValue.get("IpAddress"));
							valuemap.put("Branch", cellValue.get("Branch"));
							valuemap.put("Address", cellValue.get("Address"));
							valuemap.put("Municipality", cellValue.get("Pincode"));
							valuemap.put("Ward", cellValue.get("Area"));
						//	valuemap.put("subArea", cellValue.get("SubArea"));
							
							
						//	valuemap.put("buildingName", cellValue.get("BuildingName"));
							
						//	valuemap.put("buildingNumber", cellValue.get("BuildingNumber"));
							// 
							
							valuemap.put("Landmark", cellValue.get("Landmark"));
							valuemap.put("OLT", cellValue.get("OLT"));
							valuemap.put("Service", cellValue.get("Service"));
							valuemap.put("Plan", cellValue.get("Plan"));
							valuemap.put("Location", cellValue.get("Location"));  // decription
							
							valuemap.put("Updates", cellValue.get("Notes")); // note new feature
						//	valuemap.put("AccountNo", cellValue.get("AccountNo"));
							
							// here data is comment in futyre i will open
							
							valuemap.put("LastLogin", lastLogingDate);
							valuemap.put("Registered", resiteredDate);
							valuemap.put("startdate", startDatetemp);
						//	valuemap.put("enddate", endDate);   
							
							// Latitude	longitude
							valuemap.put("Latitude", cellValue.get("Latitude")); // note new feature
							valuemap.put("longitude", cellValue.get("longitude"));
							
							//Usedquota
							valuemap.put("Usedquota", cellValue.get("Usedquota"));
							
							//Createdby
							valuemap.put("Createdby", cellValue.get("Createdby"));
							valuemap.put("cprid", cellValue.get("cprid"));
							customerMapList.add(valuemap);
						}
					}
					return customerMapList;
				}


			// @SuppressWarnings("unchecked")
			private String getPrepaidCustomerJson(Map<String, String> customerDetails, Map<String,Integer> serviceAreaDetails) {

				String jsonString = null;

				try {

					JSONObject customerJsonObject = new JSONObject();
					CommonGetAPI commonGetAPI = new CommonGetAPI();
					//CommonList commonList = new CommonList();

				
		              // prepaid customer
					customerJsonObject.put("custtype", "Postpaid");  
		             // name = first name and last name 
					// Extract the full name from the Map
					String fullName = customerDetails.get("Name");
					String firstName="";
					String lastName="";
					// Check if fullName is null or empty
					if (fullName == null || fullName.trim().isEmpty()) {
					    // Handle the case where the full name is missing or empty
					    firstName = "Unknown"; // Or some default value
					    lastName = "Unknown";  // Or some default value
					} else {
					    // Split the full name into parts based on space
					    String[] nameParts = fullName.split("\\s+");

					    // Extract the first name (first part)
					     firstName = nameParts[0];

					    // Join the rest of the parts as the last name (everything after the first part)
					     lastName = (nameParts.length > 1) 
					        ? String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length)) 
					        : firstName; // If no last name exists, use first name as last name

					    // If lastName is still empty or null, fallback to firstName
					    if (lastName == null || lastName.isEmpty()) {
					        lastName = firstName;
					    }
					}
					
					
					customerJsonObject.put("title",customerDetails.get("Title") );
					customerJsonObject.put("firstname", firstName);
					customerJsonObject.put("lastname", lastName);
					customerJsonObject.put("username", customerDetails.get("Username"));

					customerJsonObject.put("password", customerDetails.get("Password"));
					
					// mobile number------------------------------------------------->
					customerJsonObject.put("countryCode", "+95");//Static
					 // Handle mobile number
					String mobNo = customerDetails.get("PrimaryMobile");
					Long updatedNumber = 0L; // Capital 'L' is preferred for readability

					if (mobNo != null && !mobNo.trim().isEmpty()) {
					    try {
					        updatedNumber = Long.parseLong(mobNo.trim());
					        customerJsonObject.put("mobile", updatedNumber); // stored as number in JSON
					    } catch (NumberFormatException e) {
					        customerJsonObject.put("mobile", 999999999L); // fallback if parsing fails
					    }
					} else {
					    customerJsonObject.put("mobile", 999999999L); // fallback if input is null or empty
					}

						

		            //--------------------------------------------------------------------------------->
					customerJsonObject.put("phone", customerDetails.get("SecondryPhone"));  // new added

					customerJsonObject.put("fax", ""); // --changes
					 // Handle email
		            String email = customerDetails.get("Email");
		            customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "tumildummy@123gmail.com");

					customerJsonObject.put("pan", "");
					customerJsonObject.put("contactperson", firstName);

					customerJsonObject.put("calendarType", "English");
				
					customerJsonObject.put("dunningCategory", "Gold");

					customerJsonObject.put("cafno", "");

					customerJsonObject.put("birthDate", JSONObject.NULL); // -->here we need to change date format.

					customerJsonObject.put("staffId", ""); // -->sar  here put logic for creted by 
				
					// Handle status
		            String status = customerDetails.get("Status");
		            switch (status != null ? status : "") {
		                case "Active":
		                    customerJsonObject.put("status", "Active");
		                    break;
		                case "In Active":
		                    customerJsonObject.put("status", "In Active");
		                    break;
		                case "Suspend":
		                    customerJsonObject.put("status", "Suspend");
		                    break;
		                default:
		                    customerJsonObject.put("status", "Terminate");
		                    break;
		            }
					
					customerJsonObject.put("parentCustomerId", "");
					customerJsonObject.put("invoiceType", JSONObject.NULL);


					customerJsonObject.put("custlabel", "customer");
					customerJsonObject.put("salesremark", "");
					

					// *********** Service Area Details *****************
		            String ServiceArea=customerDetails.get("Servicearea").toLowerCase();
		            
		     
					Integer serviceareaId=serviceAreaDetails.get(ServiceArea);
			
					
					customerJsonObject.put("serviceareaid", serviceareaId);

					customerJsonObject.put("branch", JSONObject.NULL);
					customerJsonObject.put("partnerid", 1);

					String branchName = customerDetails.get("Branch");


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

					
							//Latitude	longitude
							
							customerJsonObject.put("latitude", customerDetails.get("Latitude"));
							customerJsonObject.put("longitude", customerDetails.get("longitude"));
						

					// ************ Network Location Details *********************



					customerJsonObject.put("oltid", "");
					customerJsonObject.put("masterdbid", "");
					customerJsonObject.put("splitterid", "");
					/*********** ACT **************************/
					customerJsonObject.put("primaryDNS", "");
					customerJsonObject.put("primaryIPv6DNS", "");
					customerJsonObject.put("secondaryDNS", "");
					customerJsonObject.put("secondaryIPv6DNS", "");
					/*************************************************/



					// -- Radius Service Details

					customerJsonObject.put("framedIp", customerDetails.get("IpAddress"));  // -->it will take fromm sheet but need to cliryfy
					customerJsonObject.put("framedIpBind", "");
					customerJsonObject.put("nasPort", JSONObject.NULL);
					customerJsonObject.put("ipPoolNameBind", "");

					customerJsonObject.put("failcount", 0);
					customerJsonObject.put("isCustCaf", "no");
					customerJsonObject.put("servicetype", "");

					customerJsonObject.put("isParentLocation", "");
					customerJsonObject.put("locations", JSONObject.NULL);
					customerJsonObject.put("maxconcurrentsession", "");
					customerJsonObject.put("nasIpAddress", "");
					customerJsonObject.put("nasPort", "");

					customerJsonObject.put("altmobile", "");
					
					customerJsonObject.put("billday", 1);
					customerJsonObject.put("billableCustomerId", "");


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
						customerJsonObject.put("billTo", "CUSTOMER");

						customerJsonObject.put("discount", 0);
						customerJsonObject.put("discountType", "One-time");
						customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
						customerJsonObject.put("planPurchaseType", "individual");
						customerJsonObject.put("vlan_id", "");                           
						customerJsonObject.put("istrialplan", istrialplan);

						
						
							String service = customerDetails.get("Service");  
							
							
						
							String plan = customerDetails.get("Plan");
								
							
							discountType = "";
							discountExpiryDate = "";
							String tempDiscountPercentage = "";

							JSONObject planDetailJsonObject = new JSONObject();

							int planId = commonGetAPI.getPlanId(plan);
							int serviceId = commonGetAPI.getServiceId(service);

							String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

						  //  	String serviceName = planDetails[0];
							float offerPrice = Float.valueOf(planDetails[1]);
							
						//	float offerPrice = 0f;

							int validity = Integer.parseInt(planDetails[2]);

							float flatAmount = offerPrice;

							

							flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
							// customerJsonObject.put("flatAmount", flatAmount);
							customerJsonObject.put("flatAmount", "");

							planDetailJsonObject.put("newAmount", "");
							float newAmount = offerPrice;



							planDetailJsonObject.put("planId", planId);
							planDetailJsonObject.put("service", service); // here change
							planDetailJsonObject.put("validity", validity);
							planDetailJsonObject.put("discount", discountPercentage);
							planDetailJsonObject.put("billTo", "CUSTOMER");
							planDetailJsonObject.put("billableCustomerId", "");
							//billday
							planDetailJsonObject.put("billday", 1);
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
					
					//earlybillday
					customerJsonObject.put("earlybillday", "0");
					
					customerJsonObject.put("framedIPNetmask", "");
					customerJsonObject.put("framedIPv6Prefix", "");
					customerJsonObject.put("framedroute", "");
					customerJsonObject.put("gatewayIP", ""); // gateway

					customerJsonObject.put("macRetentionPeriod", "");  //static 
					customerJsonObject.put("macRetentionUnit", "");  //static
					
					
					
		           
			
					customerJsonObject.put("mac_auth_enable", true);
					customerJsonObject.put("mac_provision", true);
					
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



		// This is comment bcz  new api changes 
		private JSONObject getCustomerAddressJsonClose(String addressType, Map<String, String> customerDetails) {
		  CommonGetAPI commonGetAPI = new CommonGetAPI();
		JSONObject addressDetailJsonObject = new JSONObject();

		if ("Present".equalsIgnoreCase(addressType)) {
		    String address = customerDetails.get("Address");
		    String landmark =customerDetails.get("Landmark") ;
		    String ward = customerDetails.get("Ward");
		    String municipality = customerDetails.get("Municipality");

		    if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() && !municipality.isEmpty()) {

		    	 int pincodeid=commonGetAPI.getPincodeId(municipality);
		 
		          String wardHeirarchyDetail = commonGetAPI.getWardHierarchyDetailsByWardName(ward,pincodeid);
		 
		        String[] detail = wardHeirarchyDetail.split(":");
		        

		        addressDetailJsonObject.put("addressType", addressType);
		        addressDetailJsonObject.put("landmark", address);
		        addressDetailJsonObject.put("landmark1", JSONObject.NULL);
		        addressDetailJsonObject.put("areaId", Integer.parseInt(detail[0]));
		        addressDetailJsonObject.put("pincodeId", pincodeid);
		        addressDetailJsonObject.put("cityId", Integer.parseInt(detail[1]));
		        addressDetailJsonObject.put("stateId", Integer.parseInt(detail[2]));
		        addressDetailJsonObject.put("countryId", Integer.parseInt(detail[3]));
		        addressDetailJsonObject.put("version", "NEW");
		    } else {
		        return null;
		    }
		}

		return addressDetailJsonObject;
		}


		// new json Object of adresss json 
		private JSONObject getCustomerAddressJsonold(String addressType, Map<String, String> customerDetails) {
			  CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject addressDetailJsonObject = new JSONObject();

			if ("Present".equalsIgnoreCase(addressType)) {
			    String address = customerDetails.get("Address");
			    String landmark =customerDetails.get("Landmark") ;
			    String ward = customerDetails.get("Ward");
			    
			    String municipality = customerDetails.get("Municipality");

			    if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() && !municipality.isEmpty()) {

			    //	 int pincodeid=commonGetAPI.getPincodeId(municipality);
			 
			          String wardHeirarchyDetail = commonGetAPI.getMasterDetailsByMunicipalityName(municipality);
			        int areaId=commonGetAPI.getAreaId(ward);
			        int subAreaId=0;
			        String subArea = customerDetails.get("subArea");
			        if(!subArea.isEmpty()) {
			        subAreaId=commonGetAPI.getSubAreaId(subArea);
			        addressDetailJsonObject.put("subareaId",subAreaId );
			        }
			        else {
			        	addressDetailJsonObject.put("subareaId",JSONObject.NULL );
			        }
			        String[] detail = wardHeirarchyDetail.split(":");
			        

			        addressDetailJsonObject.put("addressType", addressType);
			        addressDetailJsonObject.put("landmark", address);
			        addressDetailJsonObject.put("landmark1", JSONObject.NULL);
			        addressDetailJsonObject.put("areaId", areaId);
			        addressDetailJsonObject.put("pincodeId", Integer.parseInt(detail[0]));
			        addressDetailJsonObject.put("cityId", Integer.parseInt(detail[3]));
			        addressDetailJsonObject.put("stateId", Integer.parseInt(detail[2]));
			        addressDetailJsonObject.put("countryId", Integer.parseInt(detail[1]));
			        
			        addressDetailJsonObject.put("building_mgmt_id", JSONObject.NULL);
			        addressDetailJsonObject.put("buildingNumber", JSONObject.NULL);
			      //  addressDetailJsonObject.put("subareaId",subAreaId );
			        addressDetailJsonObject.put("version", "NEW");
			    } else {
			        return null;
			    }
			}

			return addressDetailJsonObject;
			}

		private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {
		    CommonGetAPI commonGetAPI = new CommonGetAPI();
		    JSONObject addressDetailJsonObject = new JSONObject();

		    if (!"Present".equalsIgnoreCase(addressType)) {
		        return addressDetailJsonObject; // return empty object if not 'Present'
		    }

		    String address = customerDetails.get("Address");
		    if (address == null || address.isEmpty()) {
		        address = "Default";
		    }
		    
		    String landmark = customerDetails.get("Landmark");
		    if (address == null || address.isEmpty()) {
		    	landmark = "Default";
		    }
		    
		    String ward = customerDetails.get("Ward");
		    String municipality = customerDetails.get("Municipality");
   
		    // Validate required fields
		    if (isNullOrEmpty(address) || isNullOrEmpty(landmark) || isNullOrEmpty(ward) || isNullOrEmpty(municipality)) {
		        return null;
		    }

		    // Fetch hierarchical details
		    String wardHierarchyDetail = commonGetAPI.getMasterDetailsByAreaName(ward);
		    
		    
		    String[] hierarchyDetails = wardHierarchyDetail.split(":");

		    if (hierarchyDetails.length < 4) {
		        return null; // or throw exception if hierarchy format is wrong
		    }

		    
		    int pincodeId = commonGetAPI.getPincodeId(municipality);  // pincode id
//		    int subAreaId = isNullOrEmpty(subArea) ? 0 : commonGetAPI.getSubAreaId(subArea);

		    // Build JSON object
		    addressDetailJsonObject.put("addressType", addressType);
		    addressDetailJsonObject.put("landmark", address); // Assuming this is actual landmark
		    addressDetailJsonObject.put("landmark1", JSONObject.NULL);
		    addressDetailJsonObject.put("areaId", Integer.parseInt(hierarchyDetails[0]));
		   // addressDetailJsonObject.put("subareaId", subAreaId != 0 ? subAreaId : JSONObject.NULL);
		    addressDetailJsonObject.put("subareaId", JSONObject.NULL);
		    addressDetailJsonObject.put("pincodeId", pincodeId);
		    addressDetailJsonObject.put("countryId", Integer.parseInt(hierarchyDetails[2]));
		    addressDetailJsonObject.put("stateId", Integer.parseInt(hierarchyDetails[3]));
		    addressDetailJsonObject.put("cityId", Integer.parseInt(hierarchyDetails[1]));

		  //  addressDetailJsonObject.put("building_mgmt_id",buildingMgmtId != null ? buildingMgmtId : JSONObject.NULL);
		    addressDetailJsonObject.put("building_mgmt_id", JSONObject.NULL);
		    
		        
		    
//		    addressDetailJsonObject.put("buildingNumber",
//		        (buildingNumber != null && !buildingNumber.trim().isEmpty()) ? buildingNumber : JSONObject.NULL);
		    

		       addressDetailJsonObject.put("buildingNumber", JSONObject.NULL);

		    addressDetailJsonObject.put("version", "NEW");

		    return addressDetailJsonObject;
		}

		// Utility method to safely check for null or empty

		private boolean isNullOrEmpty(String str) {
		    return str == null || str.trim().isEmpty();
		}

		// get building managaemnt id 
		//for savanna building managament id
		/*
		public Integer getBuildingMgmtId(String buildingName) {
		    Integer buildingMgmtId = null;
		    String query = "SELECT building_mgmt_id FROM commongateway.tblmbuildingmanagement WHERE building_name = ?";

		    try (Connection conn = DriverManager.getConnection(urlCommon, dbUser, dbPassword);
		         PreparedStatement pstmt = conn.prepareStatement(query)) {

		        pstmt.setString(1, buildingName);

		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                buildingMgmtId = rs.getInt("building_mgmt_id");
		            }
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return buildingMgmtId;
		}
		*/
		}