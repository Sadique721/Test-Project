package SavanaCustomer;

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
import java.util.concurrent.ScheduledExecutorService;
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
import temp.UpdateSheet;
import utility.Constant;
import utility.CustomerExecutionSchedulerHelper;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class Savana_Thread_Customer extends RestExecution {
	private final String urlCommon = Constant.URLCOMMON;
	private final String jdbcUrl1 = Constant.URLCONVERGE;
	private final String jdbcUrl2 = Constant.URLREVENUE;
	private final String dbUser = Constant.USERNAME;
	private final String dbPassword = Constant.PASSWORD;

	int iterationCounter = 0;
	private static final Logger log = LoggerFactory.getLogger(Savana_Thread_Customer.class);

	private static String logFileName = "savanna.log";
	private static String logModuleName = "CreatesavanaCustomer";

	int thread_size = Constant.THREAD_POOL_SIZE;
	int batchSize = Constant.BATCH_SIZE;
	int retryLimit = Constant.RETRY_LIMIT; // remove -->31 dec
	int retryDelayMS = Constant.RETRY_DELAY_MS; // remove --> 31 dec
	private UpdateSheet updateSheet = new UpdateSheet();
	
	private static final AtomicInteger successCount = new AtomicInteger(0);
	private static final AtomicInteger failureCount = new AtomicInteger(0);

	private static XSSFWorkbook workbook = null;
	ExecutorService executor = Executors.newCachedThreadPool();
	ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Use fixed thread pool

	public ArrayList<Object> createPrepaidCustomer(Map<String, String> customerDetailsMap,
			Map<String, Integer> serviceAreaIdAll) {
		ArrayList<Object> objects = new ArrayList<>();
		try {
			String rowIndex = customerDetailsMap.get("RowIndex");
			String apiURL = getAPIURL("cpm/customers");

			Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

			String apiBody = getPrepaidCustomerJson(customerDetailsMap, serviceAreaIdAll);
			Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

			// System.out.println(apiBody);

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
			String migrationStatus = "Initial";
	        String migrationDetail = "Initial";
	        int cprId = -1;
	        
			if (!response.has("ERROR")) {
				if (status == 200) {
					successCount.incrementAndGet();
					String message = "New Customer added successfully - " + customerDetailsMap.get("Username");
					System.out.println(message);
					Utility.printLog("execution.log", logModuleName, "Success", message);
					
	                migrationStatus = "Success";
	                migrationDetail = message;
					
					/*
					 * int cprId =
					 * response.getJSONObject("customer").getJSONArray("planMappingList").
					 * getJSONObject(0).getInt("id"); int planMappingId =
					 * response.getJSONObject("customer").getJSONArray("planMappingList").
					 * getJSONObject(0).getInt("custServiceMappingId"); String columnAndValue =
					 * "cprid:" + cprId + "#" + "MigrationStatus:Success"; String customerId =
					 * response.getJSONObject("customer").get("id").toString(); String createdbyname
					 * = response.getJSONObject("customer").get("createdById").toString(); String
					 * createdbyid =
					 * response.getJSONObject("customer").get("createdByName").toString();
					 */

					// enhanced
					// Extract the customer object once
					JSONObject customer = response.getJSONObject("customer");

					// Extract the first element from planMappingList
					JSONArray planMappingList = customer.getJSONArray("planMappingList");
					JSONObject firstPlanMapping = planMappingList.getJSONObject(0);

					// Extract the required values
					cprId = firstPlanMapping.getInt("id");
					int planMappingId = firstPlanMapping.getInt("custServiceMappingId");
					String customerId = customer.get("id").toString();
					String createdbyname = customer.get("createdByName").toString();
					String createdbyid = customer.get("createdById").toString();

					
					// here i have close for tumil only

					try (Connection converge = DriverManager.getConnection(jdbcUrl1, dbUser, dbPassword);
							Connection radius = DriverManager.getConnection(jdbcUrl2, dbUser, dbPassword)) {
						DataBaseUpdateScript dataBaseUpdateScript = new DataBaseUpdateScript();
						dataBaseUpdateScript.updateCustomerDataInDatabases(converge, radius, customerId,
								String.valueOf(cprId), String.valueOf(planMappingId), customerDetailsMap, createdbyname,
								createdbyid);
						log.info("****************************************Success With Count :::: {}",
								successCount.get());
						System.out.println("****************************************Success With Count :::: {}"
								+ successCount.get());
					} catch (SQLException e) {
						e.printStackTrace();
						log.error("Error During Database Update: " + e.getMessage());
						System.out.println("Error During Database Update: " + e.getMessage());
					}
				} else if (status == 406) {
					String error = response.getString("responseMessage") + " - " + userName;
					System.out.println(error);
					Utility.printLog("execution.log", logModuleName, "Already Exists", error);
					failureCount.incrementAndGet();
					
	                migrationStatus = "Already Exists";
	                migrationDetail = error;
					
				} else {
					failureCount.incrementAndGet();
					String message = response.get("ERROR") + " - " + userName;
					Utility.printLog("execution.log", logModuleName, "ERROR", message);
					
	                migrationStatus = "Error";
	                migrationDetail = message;
				}
				
			} else {
				failureCount.incrementAndGet();
				String message = response.get("ERROR") + " - " + userName;
				Utility.printLog("execution.log", logModuleName, "ERROR", message);
				migrationStatus = "Error";
                migrationDetail = message;
			}
			
			// Create the column and value string
			//String columnAndValue = "cprid:" + cprId + "#" + "MigrationStatus:Success";
			String columnAndValue = "MigrationStatus::" + migrationStatus + "#" + "MigrationDetail::" + migrationDetail + "#" + "cprid::" + cprId;
			//UpdateSheet us = new UpdateSheet();
			updateSheet.setRowList(rowIndex, columnAndValue);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (handleAPIResponse)...." + e.getMessage());
		}
	}

	public void createPrepaidCustomer(List<Map<String, String>> customerMapList,
			Map<String, Integer> serviceAreaIdAll) {
		ExecutorService executorService = Executors.newFixedThreadPool(thread_size); // Thread pool size is dynamically
																						// set
		ReadWriteExcelFile rw = new ReadWriteExcelFile();
		//UpdateSheet us = new UpdateSheet();
		updateSheet.setActiveSheetName("Customer");
		
		CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
        
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
//            System.out.println("Running task... " + System.currentTimeMillis());
        	customerScheduler.setCustomerExecutionSchedulerData(updateSheet,Constant.PREPAID_CUSTOMER_SCHEDULER);
        }, 15, 10, TimeUnit.SECONDS);

		
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
						result = createPrepaidCustomer(customerDetails, serviceAreaIdAll); // Call API to create
																							// customer
					} else {
						
						String message = "Savanna Customer UserName already exists! - " + userName;
						String migrationStatus = "Already Exists";
		                String migrationDetail = message;
		                String columnAndValue = "MigrationStatus:" + migrationStatus + "#" + "MigrationDetail:" + migrationDetail;
		    			updateSheet.setRowList(row, columnAndValue);
						System.out.println(message + " | " + sw.getTime());
						
					}

					// Batch Excel updates
					batchToWrite.add(customerDetails);

					// Write to Excel in batches
					if (batchToWrite.size() >= batchSize) {
						// rw.setMultipleColumnInActiveSheet(batchToWrite);
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
		scheduler.shutdown();
		try {
			if (!executorService.awaitTermination(15, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
			}
			if (!scheduler.awaitTermination(15, TimeUnit.SECONDS)) {
		            scheduler.shutdownNow();
		    }
		} catch (InterruptedException e) {
			executorService.shutdownNow();
			scheduler.shutdownNow();
		} finally {
			System.out.println("---------->   Started to write status in sheet. <-----------------");
			rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
			customerScheduler.clearExistingFile(Constant.PREPAID_CUSTOMER_SCHEDULER);
			System.out.println("---------->   Stopped to write status in sheet. <-----------------");
		}
	

		// Write remaining batch to Excel
		if (!batchToWrite.isEmpty()) {
			// ReadWriteExcelFile rw = new ReadWriteExcelFile();
			// rw.setMultipleColumnInActiveSheet(batchToWrite);
		}

		System.out.println("Final migration step completed.");
		System.out.println("Total Success: " + successCount.get() + ", Total Failure: " + failureCount.get());
	}

	public List<Map<String, String>> readUniquePrepaidCustomerList() {

		String sheetName = "Customer"; // This is sheet name.
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {
			
			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = safeTrim(cellValue.get("UserName"));
			String mStatus = safeTrim(cellValue.get("MigrationStatus"));

			if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus)) && (!"Already Exists".equalsIgnoreCase(mStatus))) {
				String resiteredDate = Utility.convertDate(safeTrim(cellValue.get("Registered")));// Test with a valid
																									// dat
				String startDatetemp = Utility.convertDate(safeTrim(cellValue.get("Renewed")));
				String lastLogingDate = Utility.convertDate(safeTrim(cellValue.get("LastLogin")));

				String endDate = "";

				// Check if the "Expires" key exists in the map and if the value is not null or
				// empty
				if (StringUtils.isNotEmpty(cellValue.get("Expires"))) {
					// If it's not empty or null, convert the date format using the Utility method
					endDate = Utility.convertEndDateFormat(safeTrim(cellValue.get("Expires")));
				}

				valuemap.put("RowIndex", safeTrim(cellValue.get("Sno")));
				valuemap.put("Title", safeTrim(cellValue.get("Title")));
				valuemap.put("Name", safeTrim(cellValue.get("Name")));
				valuemap.put("Username", safeTrim(cellValue.get("UserName")));
				valuemap.put("Password", safeTrim(cellValue.get("Password")));
				valuemap.put("PrimaryMobile", safeTrim(cellValue.get("Phone")));
				valuemap.put("Email", safeTrim(cellValue.get("Email")));
				valuemap.put("Servicearea", safeTrim(cellValue.get("Servicearea")));
				valuemap.put("UnitNo", safeTrim(cellValue.get("UnitNo")));
				valuemap.put("status", safeTrim(cellValue.get("Status")));
				valuemap.put("Currency", safeTrim(cellValue.get("Currency")));
				valuemap.put("Plan", safeTrim(cellValue.get("Plan")));

				// Updates
				valuemap.put("Notes", safeTrim(cellValue.get("Updates")));
				// After Add ACt project -->
				valuemap.put("IpAddress", safeTrim(cellValue.get("IpAddress")));
				valuemap.put("Branch", safeTrim(cellValue.get("Branch")));
				valuemap.put("Address", safeTrim(cellValue.get("Address")));
				valuemap.put("Municipality", safeTrim(cellValue.get("Road")));
				valuemap.put("Ward", safeTrim(cellValue.get("Building")));
				valuemap.put("subArea", safeTrim(cellValue.get("SubArea")));

				valuemap.put("buildingName", safeTrim(cellValue.get("BuildingName")));

				valuemap.put("buildingNumber", safeTrim(cellValue.get("BuildingNumber")));
				//

				valuemap.put("Landmark", safeTrim(cellValue.get("Landmark")));
				valuemap.put("OLT", safeTrim(cellValue.get("OLT")));
				valuemap.put("Service", safeTrim(cellValue.get("Service")));
				valuemap.put("Location", safeTrim(cellValue.get("Location"))); // decription
				valuemap.put("LastLogin", lastLogingDate);
				valuemap.put("Updates", safeTrim(cellValue.get("Updates"))); // note new feature
				valuemap.put("AccountNo", safeTrim(cellValue.get("AccountNo")));
				valuemap.put("Registered", resiteredDate);
				valuemap.put("startdate", startDatetemp);
				valuemap.put("enddate", endDate);
				// valuemap.put("isCredentialMatchWithAccountNo","true");
				valuemap.put("cprid", safeTrim(cellValue.get("cprid")));
				customerMapList.add(valuemap);
			}
		}
//        System.out.println(customerMapList);
		return customerMapList;
	}

	// ------------------ Helpers ------------------
	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}

	// @SuppressWarnings("unchecked")
	private String getPrepaidCustomerJson(Map<String, String> customerDetails,
			Map<String, Integer> serviceAreaDetails) {

		String jsonString = null;

		try {

			JSONObject customerJsonObject = new JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			// CommonList commonList = new CommonList();

			// prepaid customer

			customerJsonObject.put("custtype", "Prepaid");
			// name = first name and last name
			// Extract the full name from the Map
			String fullName = customerDetails.get("Name");
			String firstName = "";
			String lastName = "";
			// Check if fullName is null or empty
			if (fullName == null || fullName.trim().isEmpty()) {
				// Handle the case where the full name is missing or empty
				firstName = "Unknown"; // Or some default value
				lastName = "Unknown"; // Or some default value
			} else {
				// Split the full name into parts based on space
				String[] nameParts = fullName.split("\\s+");

				// Extract the first name (first part)
				firstName = nameParts[0];

				// Join the rest of the parts as the last name (everything after the first part)
				lastName = (nameParts.length > 1) ? String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length))
						: firstName; // If no last name exists, use first name as last name

				// If lastName is still empty or null, fallback to firstName
				if (lastName == null || lastName.isEmpty()) {
					lastName = firstName;
				}
			}

			customerJsonObject.put("title", customerDetails.get("Title"));
			customerJsonObject.put("firstname", firstName);
			customerJsonObject.put("lastname", lastName);
			customerJsonObject.put("username", customerDetails.get("Username"));

			customerJsonObject.put("password", customerDetails.get("Password"));

			// ---------------------- Mobile number ----------------------

			String mobNo = customerDetails.get("PrimaryMobile");
			String updatedNumber = "99999999"; // Default fallback
			String countryCode = "+256"; // Default static code if extraction fails

			if (mobNo != null && !mobNo.trim().isEmpty()) {
				try {
					// Handle numbers in scientific notation (e.g., 2.567E9)
					if (mobNo.contains("E") || mobNo.contains("e")) {
						mobNo = String.format("%.0f", Double.parseDouble(mobNo));
					}
					String numericOnly = mobNo.replaceAll("[^0-9]", "");

					// Extract first 3 digits for dynamic country code
					if (numericOnly.length() >= 3) {
						countryCode = "+" + numericOnly.substring(0, 3);
					}

					// Remove first 3 digits for local number if possible
					if (numericOnly.length() > 3) {
						updatedNumber = numericOnly.substring(3);
					} else {
						updatedNumber = numericOnly; // use as-is if shorter
					}

				} catch (Exception e) {
					// fallback in case of parsing or format error
					updatedNumber = "99999999";
					countryCode = "+256";
				}
			}

			// Put values into JSON safely
			customerJsonObject.put("countryCode", countryCode);

			try {
				Long mobileNumber = Long.parseLong(updatedNumber);
				customerJsonObject.put("mobile", mobileNumber);
				customerJsonObject.put("mobilenumber", mobileNumber);
			} catch (NumberFormatException e) {
				customerJsonObject.put("mobile", 99999999L); // fallback if invalid
			}

//			// mobile number------------------------------------------------->
//			customerJsonObject.put("countryCode", "+256");//Static
//			 // Handle mobile number
//			String mobNo = customerDetails.get("PrimaryMobile");
//			String updatedNumber = "";
//
//			if (mobNo != null && !mobNo.trim().isEmpty()) {
//			    if (mobNo.contains("E")) {
//			        String regularNumber = String.format("%.0f", Double.parseDouble(mobNo));
//			        updatedNumber = regularNumber.substring(3);
//			    } else {
//			        updatedNumber = mobNo.replaceAll("[^0-9]", "").substring(3);
//			    }
//			} else {
//			    updatedNumber = "99999999";
//			}
//
//			// Convert to number (Long) if valid, else use default
//			try {
//			    Long mobileNumber = Long.parseLong(updatedNumber);
//			    customerJsonObject.put("mobile", mobileNumber); // stored as number in JSON
//			} catch (NumberFormatException e) {
//			    customerJsonObject.put("mobile", 99999999); // fallback
//			}

			// --------------------------------------------------------------------------------->
			customerJsonObject.put("phone", "");

			customerJsonObject.put("fax", ""); // --changes
			// Handle email
			String email = customerDetails.get("Email");
			customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "savanna@123gmail.com");

			customerJsonObject.put("pan", "");
			customerJsonObject.put("contactperson", firstName);
			customerJsonObject.put("currency", customerDetails.get("Currency")); // currency support

			customerJsonObject.put("calendarType", "English");
			if (customerDetails.get("UnitNo") != null && !customerDetails.get("UnitNo").isEmpty()) {
				customerJsonObject.put("blockNo", customerDetails.get("UnitNo")); // here block no will get from sheet .
			}
			customerJsonObject.put("dunningCategory", "Gold");

			customerJsonObject.put("cafno", "");

			customerJsonObject.put("birthDate", JSONObject.NULL); // -->here we need to change date format.

			customerJsonObject.put("staffId", ""); // -->sar

			// Handle status
			String status = customerDetails.get("status");
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
			String ServiceArea = customerDetails.get("Servicearea").toLowerCase();

			// System.out.println(ServiceArea+"--------------------------------");
			// int serviceAreaId = commonGetAPI.getServiceAreaIdList(ServiceArea).get(0);
			Integer serviceareaId = serviceAreaDetails.get(ServiceArea);
			// System.out.println(serviceAreaDetails);
			// System.out.println(serviceAreaDetails.get("Serv01"));

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

			customerJsonObject.put("latitude", "");

			customerJsonObject.put("longitude", "");

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

			customerJsonObject.put("framedIp", customerDetails.get("IpAddress")); // -->it will take fromm sheet
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
				// customerJsonObject.put("isCredentialMatchWithAccountNo", true);

				String service = customerDetails.get("Service");

				String plan = customerDetails.get("Plan");

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
			customerJsonObject.put("framedIPNetmask", "");
			customerJsonObject.put("framedIPv6Prefix", "");
			customerJsonObject.put("framedroute", "");
			customerJsonObject.put("gatewayIP", ""); // gateway

			customerJsonObject.put("macRetentionPeriod", ""); // static
			customerJsonObject.put("macRetentionUnit", ""); // static

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
//        System.out.println(jsonString);

		return jsonString;
	}

	/*
	 * // This is comment bcz new api changes private JSONObject
	 * getCustomerAddressJsonClose(String addressType, Map<String, String>
	 * customerDetails) { CommonGetAPI commonGetAPI = new CommonGetAPI(); JSONObject
	 * addressDetailJsonObject = new JSONObject();
	 * 
	 * if ("Present".equalsIgnoreCase(addressType)) { String address =
	 * customerDetails.get("Address"); String landmark =
	 * customerDetails.get("Landmark"); String ward = customerDetails.get("Ward");
	 * String municipality = customerDetails.get("Municipality");
	 * 
	 * if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() &&
	 * !municipality.isEmpty()) {
	 * 
	 * int pincodeid = commonGetAPI.getPincodeId(municipality);
	 * 
	 * String wardHeirarchyDetail =
	 * commonGetAPI.getWardHierarchyDetailsByWardName(ward, pincodeid);
	 * 
	 * String[] detail = wardHeirarchyDetail.split(":");
	 * 
	 * 
	 * addressDetailJsonObject.put("addressType", addressType);
	 * addressDetailJsonObject.put("landmark", address);
	 * addressDetailJsonObject.put("landmark1", JSONObject.NULL);
	 * addressDetailJsonObject.put("areaId", Integer.parseInt(detail[0]));
	 * addressDetailJsonObject.put("pincodeId", pincodeid);
	 * addressDetailJsonObject.put("cityId", Integer.parseInt(detail[1]));
	 * addressDetailJsonObject.put("stateId", Integer.parseInt(detail[2]));
	 * addressDetailJsonObject.put("countryId", Integer.parseInt(detail[3]));
	 * addressDetailJsonObject.put("version", "NEW"); } else { return null; } }
	 * 
	 * return addressDetailJsonObject; }
	 * 
	 * 
	 * // new json Object of adresss json private JSONObject
	 * getCustomerAddressJsonold(String addressType, Map<String, String>
	 * customerDetails) { CommonGetAPI commonGetAPI = new CommonGetAPI(); JSONObject
	 * addressDetailJsonObject = new JSONObject();
	 * 
	 * if ("Present".equalsIgnoreCase(addressType)) { String address =
	 * customerDetails.get("Address"); String landmark =
	 * customerDetails.get("Landmark"); String ward = customerDetails.get("Ward");
	 * 
	 * String municipality = customerDetails.get("Municipality");
	 * 
	 * if (!address.isEmpty() && !landmark.isEmpty() && !ward.isEmpty() &&
	 * !municipality.isEmpty()) {
	 * 
	 * // int pincodeid=commonGetAPI.getPincodeId(municipality);
	 * 
	 * String wardHeirarchyDetail =
	 * commonGetAPI.getMasterDetailsByMunicipalityName(municipality); int areaId =
	 * commonGetAPI.getAreaId(ward); int subAreaId = 0; String subArea =
	 * customerDetails.get("subArea"); if (!subArea.isEmpty()) { subAreaId =
	 * commonGetAPI.getSubAreaId(subArea); addressDetailJsonObject.put("subareaId",
	 * subAreaId); } else { addressDetailJsonObject.put("subareaId",
	 * JSONObject.NULL); } String[] detail = wardHeirarchyDetail.split(":");
	 * 
	 * 
	 * addressDetailJsonObject.put("addressType", addressType);
	 * addressDetailJsonObject.put("landmark", address);
	 * addressDetailJsonObject.put("landmark1", JSONObject.NULL);
	 * addressDetailJsonObject.put("areaId", areaId);
	 * addressDetailJsonObject.put("pincodeId", Integer.parseInt(detail[0]));
	 * addressDetailJsonObject.put("cityId", Integer.parseInt(detail[3]));
	 * addressDetailJsonObject.put("stateId", Integer.parseInt(detail[2]));
	 * addressDetailJsonObject.put("countryId", Integer.parseInt(detail[1]));
	 * 
	 * addressDetailJsonObject.put("building_mgmt_id", JSONObject.NULL);
	 * addressDetailJsonObject.put("buildingNumber", JSONObject.NULL); //
	 * addressDetailJsonObject.put("subareaId",subAreaId );
	 * addressDetailJsonObject.put("version", "NEW"); } else { return null; } }
	 * 
	 * return addressDetailJsonObject; }
	 * 
	 */
	private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		JSONObject addressDetailJsonObject = new JSONObject();

		if (!"Present".equalsIgnoreCase(addressType)) {
			return addressDetailJsonObject; // return empty object if not 'Present'
		}

		String address = customerDetails.get("Address");
		String landmark = customerDetails.get("Landmark");
		String ward = customerDetails.get("Ward");
		String municipality = customerDetails.get("Municipality");
		String subArea = customerDetails.get("subArea");
		String buildingName = customerDetails.get("buildingName");
		String buildingNumber = customerDetails.get("buildingNumber");

		// Validate required fields
		if (isNullOrEmpty(address) || isNullOrEmpty(landmark) || isNullOrEmpty(ward) || isNullOrEmpty(municipality)) {
			return null;
		}

		// Fetch hierarchical details
		String wardHierarchyDetail = commonGetAPI.getMasterDetailsByAreaName(ward.toLowerCase().trim());

		String[] hierarchyDetails = wardHierarchyDetail.split(":");

		if (hierarchyDetails.length < 4) {
			return null; // or throw exception if hierarchy format is wrong
		}

		Integer buildingMgmtId = null;

		if (!isNullOrEmpty(buildingName)) {
			buildingMgmtId = getBuildingMgmtId(buildingName);
		}

		// int areaId = commonGetAPI.getAreaId(ward);

//        int pincodeId = commonGetAPI.getPincodeId(municipality);  // pincode id
		int subAreaId = isNullOrEmpty(subArea) ? 0 : commonGetAPI.getSubAreaId(subArea);

		// Build JSON object
		addressDetailJsonObject.put("addressType", addressType);
		addressDetailJsonObject.put("landmark", address); // Assuming this is actual landmark
		addressDetailJsonObject.put("landmark1", JSONObject.NULL);
		addressDetailJsonObject.put("areaId", Integer.parseInt(hierarchyDetails[0]));
		addressDetailJsonObject.put("subareaId", subAreaId != 0 ? subAreaId : JSONObject.NULL);
		addressDetailJsonObject.put("pincodeId", Integer.parseInt(hierarchyDetails[4]));
		addressDetailJsonObject.put("countryId", Integer.parseInt(hierarchyDetails[2]));
		addressDetailJsonObject.put("stateId", Integer.parseInt(hierarchyDetails[3]));
		addressDetailJsonObject.put("cityId", Integer.parseInt(hierarchyDetails[1]));

		addressDetailJsonObject.put("building_mgmt_id", buildingMgmtId != null ? buildingMgmtId : JSONObject.NULL);

		addressDetailJsonObject.put("buildingNumber",
				(buildingNumber != null && !buildingNumber.trim().isEmpty()) ? buildingNumber : JSONObject.NULL);

		addressDetailJsonObject.put("version", "NEW");

		return addressDetailJsonObject;
	}

	// Utility method to safely check for null or empty
	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	// get building managaemnt id
//for savanna building managament id
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

}
