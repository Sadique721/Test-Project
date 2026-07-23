package SavanaCustomer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.JSONObject;

import MigrationDataBase.DataBaseUpdateScript;
import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import temp.UpdateSheet;
import utility.Constant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateSavanaCustomer extends RestExecution {
	

		private static String logFileName = "SavanaCustomer.log";
		private static String logModuleName = "CreateCustomersavana";

		private static XSSFWorkbook workbook = null;
		private final String jdbcUrl1 = Constant.URLCONVERGE;
		private final String jdbcUrl2 = Constant.URLREVENUE;
		private final String dbUser = Constant.USERNAME;
		private final String dbPassword = Constant.PASSWORD;
		
		private void createPrepaidCustomer(Map<String, String> customerDetailsMap) {

			String rowIndex = customerDetailsMap.get("RowIndex");
			String apiURL = getAPIURL("cpm/customers");
			Utility.printLog(logFileName, logModuleName, "Request URL-" + rowIndex, apiURL);

			// Initializing payload or API body
			String apiBody = getPrepaidCustomerJson(customerDetailsMap);
			Utility.printLog(logFileName, logModuleName, "Request Body-" + rowIndex, apiBody);
			// System.out.println("json time = " + sw1.getTime());

			if (!apiBody.equals(null)) {
				StopWatch sw = new StopWatch();
				sw.start();
				JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
				sw.stop();
				String response = JSONResponseBody.toString(4);
				Utility.printLog(logFileName, logModuleName, "Response-" + rowIndex, response);

				// Fetching the desired value of a parameter
				int status = JSONResponseBody.getInt("status");
				String userName = customerDetailsMap.get("Username") + " - " + sw.getTime();

				if (!JSONResponseBody.has("ERROR")) {

					if (status == 200) {

						String message = "New Customer is added successfully - " + userName;
						System.out.println(message);
						Utility.printLog("execution.log", logModuleName, "Success", message);

						int cprId = JSONResponseBody.getJSONObject("customer").getJSONArray("planMappingList")
								.getJSONObject(0).getInt("id");
						String columnAndValue = "cprid:" + cprId + "#" + "MigrationStatus:Success";

						// updation	String customerId = response.getJSONObject("customer").get("id").toString();
						int planMappingId = JSONResponseBody.getJSONObject("customer").getJSONArray("planMappingList").getJSONObject(0)
								.getInt("custServiceMappingId");
						//String columnAndValue = "CPRID:" + cprId + "#" + "MIGRATONSTATUS:Success";
						// get customer id here
						String customerId = JSONResponseBody.getJSONObject("customer").get("id").toString();
						 String createdbyname = JSONResponseBody.getJSONObject("customer").get("createdByName").toString();
		                    String createdbyid = JSONResponseBody.getJSONObject("customer").get("createdById").toString();


						// here producer call sql producer will be call
						try (Connection converge = DriverManager.getConnection(jdbcUrl1, dbUser, dbPassword); Connection radius = DriverManager.getConnection(jdbcUrl2, dbUser, dbPassword)) {
							DataBaseUpdateScript dataBaseUpdateScript = new DataBaseUpdateScript();
							   dataBaseUpdateScript.updateCustomerDataInDatabases(converge, radius, customerId, String.valueOf(cprId), String.valueOf(planMappingId), customerDetailsMap,createdbyname,createdbyid);
							//log.info("****************************************Success With Count :::: {}", iterationCounter++);
						} catch (Exception e) {
							e.printStackTrace();
							//log.error("Getting Error During Update And Insert Data :::  " + e.getMessage());
						}
						
						
						UpdateSheet us = new UpdateSheet();
						us.setRowList(rowIndex, columnAndValue);

					} else if (status == 406) {
						String error = JSONResponseBody.getString("responseMessage") + " - " + userName;
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "Already Exist", error);
					} else {
						String error = "Error: " + JSONResponseBody.get("ERROR") + " - " + userName;
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "ERROR", error);
					}

				} else {
					String message = JSONResponseBody.get("ERROR") + " - " + userName;
					Utility.printLog("execution.log", logModuleName, "ERROR", message);
				}
			}
		}
	/* i hav comment this method for thread
	 */
		public void createPrepaidCustomer(List<Map<String, String>> customerMapList) {
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			UpdateSheet us = new UpdateSheet();
			us.setActiveSheetName("Customer");

			try {
				CommonGetAPI commonGetAPI = new CommonGetAPI();

				for (int i = 0; i < customerMapList.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map = customerMapList.get(i);

					String userName = map.get("Username");
					String row = map.get("RowIndex");
					StopWatch sw = new StopWatch();
					sw.start();
					if (!commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
						Utility.printLog(logFileName, logModuleName, "Sheet Data-" + row, map.toString());
						createPrepaidCustomer(map);
						if (i % 10 == 1) {
							rw.setMultipleColumnInActiveSheetSavana();
						}
					} else {
						System.out.println("Customer UserName is Already Exists! - " + userName + " | " + sw.getTime());
						// UpdateSheet us = new UpdateSheet();
						// us.setRowList(row);
					}
				}

			} finally {
				rw.setMultipleColumnInActiveSheetSavana();  //---> i have comment 31 dec
				System.out.println("called -->" + "final");
			}

		}   
			              

		public List<Map<String, String>> readUniquePrepaidCustomerList() {

			String sheetName = "Customer";  // This is sheet name.
			List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
			ReadData readData = new ReadData();
			sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

			Map<String, String> cellValue = new HashMap<String, String>();
			List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

			for (int i = 0; i < sheetMap.size(); i++) {

				Map<String, String> valuemap = new HashMap<String, String>();
				cellValue = sheetMap.get(i);

				String userName = cellValue.get("Username");
				String mStatus = cellValue.get("MigrationStatus");

				if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {
					String resiteredDate= Utility.convertDate(cellValue.get("Registered"));// Test with a valid dat       
					String startDatetemp=Utility.convertDate(cellValue.get("Renewed"));
					String lastLogingDate=Utility.convertDate(cellValue.get("LastLogin"));
					
					String endDate = "";

					// Check if the "Expires" key exists in the map and if the value is not null or empty
					if (StringUtils.isNotEmpty(cellValue.get("Expires"))) {
					    // If it's not empty or null, convert the date format using the Utility method
					    endDate = Utility.convertEndDateFormat(cellValue.get("Expires"));
					}
					
					valuemap.put("RowIndex", cellValue.get("Sno"));
					valuemap.put("Title", cellValue.get("Title"));
					valuemap.put("Name", cellValue.get("Name"));
					valuemap.put("Username", cellValue.get("UserName"));  
					valuemap.put("Password", cellValue.get("Password"));
					valuemap.put("PrimaryMobile", cellValue.get("Phone"));
					valuemap.put("Email", cellValue.get("Email"));
					valuemap.put("Servicearea", cellValue.get("Servicearea"));
					valuemap.put("UnitNo", cellValue.get("UnitNo"));
					valuemap.put("Status", cellValue.get("Status"));
					valuemap.put("Plan", cellValue.get("Plan"));
				
					// After Add ACt project  -->
					valuemap.put("IpAddress", cellValue.get("IpAddress"));
					valuemap.put("Branch", cellValue.get("Branch"));
					valuemap.put("Address", cellValue.get("Address"));
					valuemap.put("Municipality", cellValue.get("Road"));
					valuemap.put("Ward", cellValue.get("Building"));
					valuemap.put("Landmark", cellValue.get("Landmark"));
					valuemap.put("OLT", cellValue.get("OLT"));
					valuemap.put("Service", cellValue.get("Service"));
					valuemap.put("Plan", cellValue.get("Plan"));
					valuemap.put("Location", cellValue.get("Location"));  // decription
					valuemap.put("LastLogin", lastLogingDate);
					valuemap.put("Updates", cellValue.get("Updates")); // note new feature
					valuemap.put("AccountNo", cellValue.get("AccountNo"));
					valuemap.put("Registered", resiteredDate);
					valuemap.put("startdate", startDatetemp);
					valuemap.put("enddate", endDate);
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

			
	              // prepaid customer
				customerJsonObject.put("custtype", "Prepaid");
                 // name = first name and last name 
				// Extract the full name from the Map
		        String fullName = customerDetails.get("Name");

		        // Split the full name into parts based on space
		        String[] nameParts = fullName.split(" ");

		        // Extract the first name (first part)
		        String firstName = nameParts[0];

		        // Join the rest of the parts as the last name (everything after the first part)
		        String lastName = String.join(" ",Arrays.copyOfRange(nameParts, 1, nameParts.length));

		      if(lastName==null || lastName.isEmpty() ) {
		    	  lastName=firstName;
		      }
				
				
				customerJsonObject.put("title",customerDetails.get("Title") );
				customerJsonObject.put("firstname", firstName);
				customerJsonObject.put("lastname", lastName);
				customerJsonObject.put("username", customerDetails.get("Username"));

				customerJsonObject.put("password", customerDetails.get("Password"));
				
				// mobile number------------------------------------------------->
				customerJsonObject.put("countryCode", "+256");//Static
				 // Handle mobile number
                String mobNo = customerDetails.get("PrimaryMobile");
                String updatedNumber="";
                // Skip the first 3 characters (country code 256)
                // handle number
                if (mobNo.contains("E")) {
                    // Convert the scientific notation to a regular number string
                    String regularNumber = String.format("%.0f", Double.parseDouble(mobNo));
                    // Remove the first 3 digits (country code 256)
                     updatedNumber = regularNumber.substring(3);
                } else {
                    // If not in scientific notation, directly remove the first 3 digits (country code 256)
                  updatedNumber = mobNo.replaceAll("[^0-9]", "").substring(3);
                }
                
             //   String updatedNumber = mobNo.substring(3);
                customerJsonObject.put("mobile", (updatedNumber != null && !updatedNumber.isEmpty()) ? updatedNumber : "99999999");
				
                
                //--------------------------------------------------------------------------------->
				customerJsonObject.put("phone", "");

				customerJsonObject.put("fax", ""); // --changes
				 // Handle email
                String email = customerDetails.get("Email");
                customerJsonObject.put("email", (email != null && !email.isEmpty()) ? email : "savanna@123gmail.com");

				customerJsonObject.put("pan", "");
				customerJsonObject.put("contactperson", firstName);

				customerJsonObject.put("calendarType", "English");
				if(customerDetails.get("UnitNo") != null && !customerDetails.get("UnitNo").isEmpty()) {
				customerJsonObject.put("blockNo",customerDetails.get("UnitNo") ); // here block no will get from sheet .
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
                String ServiceArea=customerDetails.get("Servicearea");
				int serviceAreaId = commonGetAPI.getServiceAreaIdList(ServiceArea).get(0); //  here static service area
				customerJsonObject.put("serviceareaid", serviceAreaId);

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

				customerJsonObject.put("framedIp", customerDetails.get("IpAddress"));  // -->it will take fromm sheet
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

//				if (customerType.equalsIgnoreCase("Postpaid")) {
//					String billDay = customerDetails.get("BillDay");
//	
//
//					if (!"".equals(billDay)) {
//						int intBillDay = Integer.parseInt(billDay);
//						customerJsonObject.put("billday", intBillDay);
//					}
//
//						customerJsonObject.put("earlybillday", 0);
//
//				}
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


	

private JSONObject getCustomerAddressJson(String addressType, Map<String, String> customerDetails) {
    CommonGetAPI commonGetAPI = new CommonGetAPI();
    JSONObject addressDetailJsonObject = new JSONObject();

    if ("Present".equalsIgnoreCase(addressType)) {
        String address = customerDetails.get("Address");
        String landmark =customerDetails.get("Landmark") ;
        String ward = customerDetails.get("Ward");
        String municipality = customerDetails.get("Municipality");

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

}
