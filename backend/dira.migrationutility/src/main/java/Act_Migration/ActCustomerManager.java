package Act_Migration;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import temp.UpdateSheet;
import utility.ReadWriteExcelFile;
import utility.Utility;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ActCustomerManager extends RestExecution {

	
	//private final String logFileName = "execution.log";
	//private final String logModuleName = "PrepaidCustomerModule";


	private static String logFileName = "prepaidcustomer.log";
	private static String logModuleName = "CreatePrepaidCustomer";

	private void createPrepaidCustomer(Map<String, String> customerDetailsMap) {
		
		StopWatch sw1 = new StopWatch();
		sw1.start();		
		String row = customerDetailsMap.get("RowIndex");
		String apiURL = getAPIURL("customers");
		Utility.printLog(logFileName, logModuleName, "Request URL-"+row, apiURL);

		// Initializing payload or API body
		String apiBody = getPrepaidCustomerJson(customerDetailsMap);
		Utility.printLog(logFileName, logModuleName, "Request Body-"+row, apiBody);
		//System.out.println("json time = " + sw1.getTime());
		StopWatch sw = new StopWatch();
		sw.start();
		
		if (!apiBody.equals(null)) {

			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response-"+row, response);

			// Fetching the desired value of a parameter
			sw.stop();
			int status = JSONResponseBody.getInt("status");
			String userName = customerDetailsMap.get("Username") + " - " + sw.getTime() + " | " + sw1.getTime();

			if (!JSONResponseBody.has("ERROR")) {
				
				if (status == 200) {
					String message = "New Prepaid-Customer is added successfully - " + userName;
					System.out.println(message);
					Utility.printLog("execution.log", logModuleName, "Success", message);
					
					UpdateSheet us = new UpdateSheet();
					us.setRowList(row);

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

	public void createPrepaidCustomer(List<Map<String, String>> customerMapList) {

		for (int i = 0; i < customerMapList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map = customerMapList.get(i);

			String userName = map.get("Username");
			if (!checkcustomerUsernameIsAlreadyExists(userName)) {
				String row = map.get("RowIndex");
				Utility.printLog(logFileName, logModuleName, "Sheet Data-"+row, map.toString());
				createPrepaidCustomer(map);
			} else {
				System.out.println("Customer UserName is Already Exists! - " + userName);
			}
		}
	}
	public boolean checkcustomerUsernameIsAlreadyExists(String customerName) {

		String apiURL = "customer/customerUsernameIsAlreadyExists/" + customerName;
		apiURL = getAPIURL(apiURL);

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("status");

		boolean checkCust = false;
		if (status == 200) {
			checkCust = jsonResponse.getBoolean("isAlreadyExists");
		}
		return checkCust;
	}
	

		// Read customer data from Excel
	public List<Map<String, String>> readUniquePrepaidCustomerList() {

		String sheetName = "ACustomer";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getActCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("Username");
			String mStatus = cellValue.get("MigrationStatus");

			if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

				// Basic Details

				valuemap.put("RowIndex", cellValue.get("No"));
				valuemap.put("Username", cellValue.get("USERNAME"));  
				valuemap.put("Password", cellValue.get("PASSWORD"));
				valuemap.put("PrimaryMobile", cellValue.get("MSISDN"));
				valuemap.put("Email", cellValue.get("CUSTOMERALTEMAILID"));
				valuemap.put("BillDay", cellValue.get("ADDITIONALPOLICY"));
				valuemap.put("Status", cellValue.get("STATUS"));
				valuemap.put("Plan", cellValue.get("RADIUSPOLICY"));
				
		

				// Act
				valuemap.put("FramedIPNetmask", cellValue.get("PARAM2"));
				valuemap.put("Maxconcurrentsession", cellValue.get("CONCURRENTLOGINPOLICY"));
				valuemap.put("FramedIPAddress", cellValue.get("PARAM1"));
				valuemap.put("Vlan_Id", cellValue.get("GEOLOCATION"));
				valuemap.put("GatewayIP", cellValue.get("PARAM6"));
				valuemap.put("Mac_auth_enable", cellValue.get("MACVALIDATION"));
				valuemap.put("NasPortId", cellValue.get("PARAM4"));
				valuemap.put("PrimaryDNS", cellValue.get("PRIMARYDNS"));
				valuemap.put("PrimaryIPv6DNS", cellValue.get("PRIMARYIPV6DNS"));
				valuemap.put("SecondaryDNS", cellValue.get("SECONDARYDNS"));
				valuemap.put("SecondaryIPv6DNS", cellValue.get("SECONDARYIPV6DNS"));

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
			if ("Individual".equalsIgnoreCase(planCategory)) {
			    float discountPercentage = 0;
			    String discountType = "";
			    boolean invoiceToOrg = false;
			    boolean isTrialPlan = false;

			    customerJsonObject.put("billableCustomerId", "");
			    customerJsonObject.put("discount", discountPercentage);
			    customerJsonObject.put("discountType", "One-time");
			    customerJsonObject.put("discountExpiryDate", JSONObject.NULL);
			    customerJsonObject.put("planPurchaseType", "individual");
			    customerJsonObject.put("vlan_id", customerDetails.get("Vlan_Id"));
			    customerJsonObject.put("istrialplan", isTrialPlan);

			    String service = "BroadBand"; // Static service
			    String plan = customerDetails.get("Plan");

			    JSONObject planDetailJsonObject = new JSONObject();

			    int planId = commonGetAPI.getPlanId(plan);
			    int serviceId = commonGetAPI.getServiceId(service);

			    String[] planDetails = commonGetAPI.getPlanDetails(planId).split(":");
			    String serviceName = planDetails[0];
			    float offerPrice = Float.parseFloat(planDetails[1]);
			    int validity = Integer.parseInt(planDetails[2]);

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
			    planDetailJsonObject.put("serviceId", serviceId);
			    planDetailJsonObject.put("serialNumber", "");

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
