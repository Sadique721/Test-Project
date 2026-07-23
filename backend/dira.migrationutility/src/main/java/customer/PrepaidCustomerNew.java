package customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import temp.UpdateSheet;

import utility.ReadWriteExcelFile;
import utility.Utility;

public class PrepaidCustomerNew extends RestExecution {

	private static String logFileName = "customer.log";
	private static String logModuleName = "CreateCustomer";

	private static XSSFWorkbook workbook = null;

	/**********************Comment for Act***********Act Migration***********************************/
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
					String columnAndValue = "CPRID:" + cprId + "#" + "MigrationStatus:Success";

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
						rw.setMultipleColumnInActiveSheet();
					}
				} else {
					System.out.println("Customer UserName is Already Exists! - " + userName + " | " + sw.getTime());
					// UpdateSheet us = new UpdateSheet();
					// us.setRowList(row);
				}
			}

		} finally {
			rw.setMultipleColumnInActiveSheet();  //---> i have comment 31 dec
			System.out.println("called -->" + "final");
		}

	}   
	


//       // Handle the API response
//    private void handleAPIResponse(JSONObject response, String rowIndex, long elapsedTime) {
//        int status = response.getInt("status");
//        String userName = "Username - " + elapsedTime;
//
//        if (!response.has("ERROR")) {
//            if (status == 200) {
//                String message = "New Customer added successfully - " + userName;
//                System.out.println(message);
//                Utility.printLog("execution.log", logModuleName, "Success", message);
//
//                int cprId = response.getJSONObject("customer").getJSONArray("planMappingList")
//                        .getJSONObject(0).getInt("id");
//                String columnAndValue = "CPRID:" + cprId + "#" + "MigrationStatus:Success";
//
//                UpdateSheet us = new UpdateSheet();
//                us.setRowList(rowIndex, columnAndValue);
//            } else if (status == 406) {
//                String error = response.getString("responseMessage") + " - " + userName;
//                System.out.println(error);
//                Utility.printLog("execution.log", logModuleName, "Already Exists", error);
//            } else {
//                String error = "Error: " + response.get("ERROR") + " - " + userName;
//                System.out.println(error);
//                Utility.printLog("execution.log", logModuleName, "ERROR", error);
//            }
//        } else {
//            String message = response.get("ERROR") + " - " + userName;
//            Utility.printLog("execution.log", logModuleName, "ERROR", message);
//        }
//    }

    
   
  
   
              
	public List<Map<String, String>> readUniquePrepaidCustomerList() {

		String sheetName = "Customer";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("Username");
			String mStatus = cellValue.get("MigrationStatus");

			if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

				// Basic Details

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
				valuemap.put("Title", cellValue.get("Title"));
				valuemap.put("FirstName", cellValue.get("FirstName"));
				valuemap.put("LastName", cellValue.get("LastName"));
				valuemap.put("Username", cellValue.get("Username"));

				valuemap.put("Password", cellValue.get("Password"));
				valuemap.put("CountryCode", cellValue.get("CountryCode"));
				valuemap.put("PrimaryMobile", cellValue.get("PrimaryMobile"));
				valuemap.put("SecondaryMobile", cellValue.get("SecondaryMobile"));
				valuemap.put("Telephone", cellValue.get("Telephone"));

				valuemap.put("FAX", cellValue.get("FAX"));
				valuemap.put("Email", cellValue.get("Email"));
				valuemap.put("PAN", cellValue.get("PAN"));
				valuemap.put("ContactPerson", cellValue.get("ContactPerson"));

				valuemap.put("CalendarType", cellValue.get("CalendarType"));
				valuemap.put("CustomerCategory", cellValue.get("CustomerCategory"));
				valuemap.put("CDCustomerType", cellValue.get("CDCustomerType"));
				valuemap.put("CDCustomerSubType", cellValue.get("CDCustomerSubType"));

				valuemap.put("CustomerSector", cellValue.get("CustomerSector"));
				valuemap.put("CustomerSectorType", cellValue.get("CustomerSectorType"));
				valuemap.put("CAFNumber", cellValue.get("CAFNumber"));
				valuemap.put("DOB", cellValue.get("DOB"));
				valuemap.put("BillDay", cellValue.get("BillDay"));
				valuemap.put("EarlyBillDays", cellValue.get("EarlyBillDays"));

				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("DedicatedStaffUserName", cellValue.get("DedicatedStaffUserName"));
				valuemap.put("ParentCustomer", cellValue.get("ParentCustomer"));
				valuemap.put("CustomerType", cellValue.get("CustomerType"));

				valuemap.put("SalesMark", cellValue.get("SalesMark"));
				valuemap.put("ParentExperience", cellValue.get("ParentExperience"));

				// Service Area Details

				valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
				valuemap.put("Branch", cellValue.get("Branch"));
				valuemap.put("Partner", cellValue.get("Partner"));

				valuemap.put("Address", cellValue.get("Address"));
				valuemap.put("Landmark", cellValue.get("Landmark"));
				valuemap.put("Municipality", cellValue.get("Municipality"));
				valuemap.put("Ward", cellValue.get("Ward"));

				valuemap.put("ValleyType", cellValue.get("ValleyType"));
				valuemap.put("InsideOutSideValley", cellValue.get("InsideOutSideValley"));
				valuemap.put("Latitude", cellValue.get("Latitude"));
				valuemap.put("Longitude", cellValue.get("Longitude"));

				// Plan Details

				valuemap.put("PlanCategory", cellValue.get("PlanCategory"));
				valuemap.put("PlanGroupName", cellValue.get("PlanGroupName"));
				valuemap.put("InvoiceType", cellValue.get("InvoiceType"));
				valuemap.put("BillTo", cellValue.get("BillTo"));
				valuemap.put("InvoiceToOrganization", cellValue.get("InvoiceToOrganization"));
				valuemap.put("BillableTo", cellValue.get("BillableTo"));

				// valuemap.put("Service", cellValue.get("Service"));
				// valuemap.put("Plan", cellValue.get("Plan"));
				valuemap.put("DiscountType", cellValue.get("DiscountType"));

				valuemap.put("DiscountPercentage", cellValue.get("DiscountPercentage"));
				valuemap.put("DiscountExpiryDate", cellValue.get("DiscountExpiryDate"));
				valuemap.put("NewPriceWithDiscount", cellValue.get("NewPriceWithDiscount"));
				valuemap.put("[Service:Plan:DiscountType:DiscountPercentage:DiscountExpiryDate]",
						cellValue.get("[Service:Plan:DiscountType:DiscountPercentage:DiscountExpiryDate]"));

				// Network Location Details

				valuemap.put("POP", cellValue.get("POP"));
				valuemap.put("OLT", cellValue.get("OLT"));
				valuemap.put("MasterDB", cellValue.get("MasterDB"));
				valuemap.put("SplitterDB", cellValue.get("SplitterDB"));

				valuemap.put("StaticIP", cellValue.get("StaticIP"));
				valuemap.put("NASIP", cellValue.get("NASIP"));
				valuemap.put("NASPORTValidate", cellValue.get("NASPortValidate"));
				valuemap.put("IPPoolNameBind", cellValue.get("IPPoolNameBind"));

				// Act
				valuemap.put("FramedIPNetmask", cellValue.get("FramedIPNetmask"));
				valuemap.put("GatewayIP", cellValue.get("GatewayIP"));
				valuemap.put("Mac_auth_enable", cellValue.get("Mac_auth_enable"));
				valuemap.put("Mac_provision", cellValue.get("Mac_provision"));
				valuemap.put("MacRetentionPeriod", cellValue.get("MacRetentionPeriod"));
				valuemap.put("MacRetentionUnit", cellValue.get("MacRetentionUnit"));
				valuemap.put("NasPortId", cellValue.get("NasPortId"));
				valuemap.put("PrimaryDNS", cellValue.get("PrimaryDNS"));
				valuemap.put("PrimaryIPv6DNS", cellValue.get("PrimaryIPv6DNS"));
				valuemap.put("SecondaryDNS", cellValue.get("SecondaryDNS"));
				valuemap.put("SecondaryIPv6DNS", cellValue.get("SecondaryIPv6DNS"));

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
			CommonList commonList = new CommonList();

			// Integer number = Integer.parseInt(customerDetails.get("PrimaryMobile"));
			String customerType = customerDetails.get("SubscriberType");
			customerJsonObject.put("custtype", customerType);

			customerJsonObject.put("title", customerDetails.get("Title"));
			customerJsonObject.put("firstname", customerDetails.get("FirstName"));
			customerJsonObject.put("lastname", customerDetails.get("LastName"));
			customerJsonObject.put("username", customerDetails.get("Username"));

			customerJsonObject.put("password", customerDetails.get("Password"));
			customerJsonObject.put("countryCode", "+" + customerDetails.get("CountryCode"));
			customerJsonObject.put("mobile", customerDetails.get("PrimaryMobile"));

			// customerJsonObject.put("secondaryMobile",
			// customerDetails.get("SecondaryMobile")); //-->remove by sar
			customerJsonObject.put("phone", customerDetails.get("Telephone"));

			customerJsonObject.put("fax", customerDetails.get("FAX")); // --changes
			customerJsonObject.put("email", customerDetails.get("Email"));
			customerJsonObject.put("pan", customerDetails.get("PAN"));
			customerJsonObject.put("contactperson", customerDetails.get("ContactPerson"));

			customerJsonObject.put("calendarType", customerDetails.get("CalendarType"));
			// customerJsonObject.put("billableCustomerId", "");
			customerJsonObject.put("dunningCategory", customerDetails.get("CustomerCategory"));
			// remove by sarfraz
			/*
			 * customerJsonObject.put("dunningType", customerDetails.get("CDCustomerType"));
			 * customerJsonObject.put("dunningSubType",
			 * customerDetails.get("CDCustomerSubType"));
			 * 
			 * customerJsonObject.put("dunningSector",
			 * customerDetails.get("CustomerSector"));
			 * customerJsonObject.put("dunningSubSector",
			 * customerDetails.get("CustomerSectorType"));
			 */
			customerJsonObject.put("cafno", customerDetails.get("CAFNumber"));
			// customerJsonObject.put("birthDate", customerDetails.get("DOB"));
			customerJsonObject.put("birthDate", JSONObject.NULL); // -->here we need to change date format.
			// customerJsonObject.put("earlybillday",
			// customerDetails.get("EarlyBillDays"));//-->sarf early bill only postpaid.

			customerJsonObject.put("staffId", ""); // -->sar
			customerJsonObject.put("status", customerDetails.get("Status"));

			String staffUserName = customerDetails.get("DedicatedStaffUserName");
			if (!"".equals(staffUserName)) {
				int staffId = commonGetAPI.getStaffId(customerDetails.get("DedicatedStaffUserName"));
				customerJsonObject.put("staffId", staffId);
			}

			customerJsonObject.put("parentCustomerId", "");
			customerJsonObject.put("invoiceType", JSONObject.NULL);

			String parentCustomer = customerDetails.get("ParentCustomer");
			if (!"".equals(parentCustomer)) {
				int parentCustomerId = commonGetAPI.getCustomerId(parentCustomer, customerType);
				if (parentCustomerId != 0) {
					customerJsonObject.put("parentCustomerId", parentCustomerId);
					customerJsonObject.put("invoiceType", customerDetails.get("InvoiceType"));
					customerJsonObject.put("parentExperience", customerDetails.get("ParentExperience"));
				}
			}

			customerJsonObject.put("custlabel", "customer");
			// -->sar
			// customerJsonObject.put("custlabel",
			// customerDetails.get("CustomerType").toLowerCase()); //-->sar
			customerJsonObject.put("salesremark", customerDetails.get("SalesMark"));
			;

			// *********** Service Area Details *****************

			int serviceAreaId = commonGetAPI.getServiceAreaIdList(customerDetails.get("ServiceArea")).get(0);
			customerJsonObject.put("serviceareaid", serviceAreaId);

			customerJsonObject.put("branch", JSONObject.NULL);
			customerJsonObject.put("partnerid", 1);

			String branchName = customerDetails.get("Branch");
			String partner = customerDetails.get("Partner");

			if (!"".equals(branchName)) {
				int branchId = commonGetAPI.getBranchIdList(branchName).get(0);
				customerJsonObject.put("branch", branchId);
			} else if (!"".equals(partner)) {
				int partnerId = commonGetAPI.getPartnerId(partner);
				customerJsonObject.put("partnerid", partnerId);
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

			String valleyType = customerDetails.get("ValleyType");
			if (!"".equals(valleyType)) {
				customerJsonObject.put("valleyType", valleyType);

				String customerArea = customerDetails.get("InsideOutSideValley");
				if (!"".equals(customerArea)) {
					customerJsonObject.put("customerArea", customerArea);
				}
			}

			String latitude = customerDetails.get("Latitude");
			if (!"".equals(latitude)) {
				customerJsonObject.put("latitude", latitude);
			}

			String longitude = customerDetails.get("Longitude");
			if (!"".equals(longitude)) {
				customerJsonObject.put("longitude", longitude);
			}

			// ************ Network Location Details *********************

			customerJsonObject.put("popid", "");
			String popName = customerDetails.get("POP");
			if (!"".equals(popName)) {
				int popId = commonGetAPI.getPopId(popName);
				customerJsonObject.put("popid", popId);
			}

			customerJsonObject.put("oltid", "");
			customerJsonObject.put("masterdbid", "");
			customerJsonObject.put("splitterid", "");
			/*********** ACT **************************/
			customerJsonObject.put("primaryDNS", customerDetails.get("PrimaryDNS"));
			customerJsonObject.put("primaryIPv6DNS", customerDetails.get("PrimaryIPv6DNS"));
			customerJsonObject.put("secondaryDNS", customerDetails.get("SecondaryDNS"));
			customerJsonObject.put("secondaryIPv6DNS", customerDetails.get("SecondaryIPv6DNS"));
			/*************************************************/

			String oltid = customerDetails.get("OLT");
			if (!"".equals(oltid)) {
				customerJsonObject.put("oltid", oltid);
			}

			String masterdbid = customerDetails.get("MasterDB");
			if (!"".equals(masterdbid)) {
				customerJsonObject.put("masterdbid", masterdbid);
			}

			String splitterid = customerDetails.get("SplitterDB");
			if (!"".equals(splitterid)) {
				customerJsonObject.put("splitterid", splitterid);
			}

			// -- Radius Service Details

			customerJsonObject.put("framedIp", "");
			customerJsonObject.put("framedIpBind", "");
			customerJsonObject.put("nasPort", JSONObject.NULL);
			customerJsonObject.put("ipPoolNameBind", "");

			String staticIp = customerDetails.get("StaticIP");
			if (!"".equals(staticIp)) {
				customerJsonObject.put("framedIp", staticIp);
			}

			String nasIp = customerDetails.get("NASIP");
			if (!"".equals(nasIp)) {
				customerJsonObject.put("framedIpBind", nasIp);
			}

			String nasPort = customerDetails.get("NASPortValidate");
			if (!"".equals(nasPort)) {
				customerJsonObject.put("nasPort", nasPort);
			}

			String ipPoolNameBind = customerDetails.get("IPPoolNameBind");
			if (!"".equals(ipPoolNameBind)) {
				customerJsonObject.put("ipPoolNameBind", ipPoolNameBind);
			}
			// customerJsonObject.put("customerJsonObject", "");
			// customerJsonObject.put("billableCustomerId", "");
			customerJsonObject.put("failcount", 0);
			customerJsonObject.put("isCustCaf", "no");
			customerJsonObject.put("servicetype", "");
			// -->sarf

			// add by sarfraz key value
			customerJsonObject.put("isParentLocation", "");
			customerJsonObject.put("locations", JSONObject.NULL);
			customerJsonObject.put("maxconcurrentsession", "");
			customerJsonObject.put("nasIpAddress", "");
			customerJsonObject.put("nasPort", "");
			// ===========================
			// sarfraz 24 july
			customerJsonObject.put("altmobile", "");
			customerJsonObject.put("billableCustomerId", "");

			if (customerType.equalsIgnoreCase("Postpaid")) {
				String billDay = customerDetails.get("BillDay");
				String earlyBillDay = customerDetails.get("EarlyBillDays");

				if (!"".equals(billDay)) {
					int intBillDay = Integer.parseInt(billDay);
					customerJsonObject.put("billday", intBillDay);
					// customerJsonObject.put("billday", billDay);

				}
				// Early bill date Sarfraz
				if (!"".equals(earlyBillDay)) {
					customerJsonObject.put("earlybillday", earlyBillDay); // here may be changes
					// int intearlyBillDays = Integer.parseInt(earlyBillDay);
					// customerJsonObject.put("earlybillday", intearlyBillDays);
				}
			}

			// customerJsonObject.put("billday", "1");//--> bill day diff for prepaid cust
			// from here postpaid and prepaid paid is diff
			customerJsonObject.put("gst", "");
			customerJsonObject.put("aadhar", "");
			// Add pojo Act-->
			customerJsonObject.put("addparam1", "");
			customerJsonObject.put("addparam2", "");
			customerJsonObject.put("addparam3", "");
			customerJsonObject.put("addparam4", "");

			customerJsonObject.put("passportNo", "");
			customerJsonObject.put("tinNo", "");
			customerJsonObject.put("parentQuotaType", ""); // -->sar
			// -- Customer Payment Details --
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

			String planCategory = customerDetails.get("PlanCategory");

			// --Individual Plan
			if (planCategory.equalsIgnoreCase("Individual")) {

				String billTo = customerDetails.get("BillTo").toUpperCase();
				String invoiceToOrganization = customerDetails.get("InvoiceToOrganization").toUpperCase();
				String billableCustomerId = null;
				float discountPercentage = 0;
				String discountType = null;
				String discountExpiryDate = null;
				boolean invoiceToOrg = false;
				boolean istrialplan = false;
				customerJsonObject.put("billableCustomerId", "");
				customerJsonObject.put("billTo", billTo);
				// customerJsonObject.put("billableCustomerId", "");//-->once
				customerJsonObject.put("discount", discountType);
				customerJsonObject.put("discountType", discountType);
				customerJsonObject.put("discountExpiryDate", discountExpiryDate);
				customerJsonObject.put("planPurchaseType", "individual");
				customerJsonObject.put("vlan_id", "");
				customerJsonObject.put("istrialplan", istrialplan);

				// Add pojo for Act-->

				// --Plan service List Details
				String servicePlanDiscountDetails = customerDetails
						.get("[Service:Plan:DiscountType:DiscountPercentage:DiscountExpiryDate]");

				servicePlanDiscountDetails = servicePlanDiscountDetails.replaceAll("[\\[\\]]", "");
				String ans[] = servicePlanDiscountDetails.split(",");

				for (int i = 0; i < ans.length; i++) {

					String servicePlanDetails[] = ans[i].split(":");
					String service = null; // -->sar
					String plan = null;
					discountType = null;
					discountExpiryDate = null;
					String tempDiscountPercentage = "";

					JSONObject planDetailJsonObject = new JSONObject();

					int length = servicePlanDetails.length;
					// System.out.println("length=" + length);

					if (length >= 5) {
						service = servicePlanDetails[0].trim();
						plan = servicePlanDetails[1].trim();
						discountType = servicePlanDetails[2].trim();
						tempDiscountPercentage = servicePlanDetails[3].trim();
						discountExpiryDate = servicePlanDetails[4].trim();

					} else if (length >= 4) {
						service = servicePlanDetails[0].trim();
						plan = servicePlanDetails[1].trim();
						discountType = servicePlanDetails[2].trim();
						tempDiscountPercentage = servicePlanDetails[3].trim();

					} else if (length >= 3) {
						service = servicePlanDetails[0].trim();
						plan = servicePlanDetails[1].trim();
						discountType = servicePlanDetails[2].trim();
					} else if (length >= 1) {
						service = servicePlanDetails[0].trim();
						plan = servicePlanDetails[1].trim();
					}

					// int planId = commonGetAPI.getPlanId(customerDetails.get("Plan"));
					int planId = commonGetAPI.getPlanId(plan);
					int serviceId = commonGetAPI.getServiceId(service);

					String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

					String serviceName = planDetails[0];
					float offerPrice = Float.valueOf(planDetails[1]);
					int validity = Integer.parseInt(planDetails[2]);
					// String unitsOfValidity = planDetails[3];

					float flatAmount = offerPrice;
					// String tempDiscountPercentage = customerDetails.get("DiscountPercentage");

					if ((billTo.equalsIgnoreCase("CUSTOMER")) && (!"".equals(tempDiscountPercentage))) {
						discountPercentage = Float.valueOf(tempDiscountPercentage);
						flatAmount = offerPrice - (offerPrice * discountPercentage / 100);

						// discountType = customerDetails.get("DiscountType");
						if (!"".equals(discountType)) {
							if ("One-time".equals(discountType)) {
								discountType = "One-time";
							} else if ("Recurring".equals(discountType)) {
								discountType = "Recurring";
								// discountExpiryDate = customerDetails.get("DiscountExpiryDate");
								discountExpiryDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(
										discountExpiryDate, "dd-MMM-yyyy", "yyyy-MM-dd");
								discountExpiryDate = discountExpiryDate + "T00:00:00.000Z";
							}
						}
					}

					flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
					// customerJsonObject.put("flatAmount", flatAmount);
					customerJsonObject.put("flatAmount", JSONObject.NULL);

					planDetailJsonObject.put("newAmount", JSONObject.NULL);
					float newAmount = offerPrice;

					if (billTo.equalsIgnoreCase("SUBISU")) {
						if (invoiceToOrganization.equalsIgnoreCase("YES")) {
							invoiceToOrg = true;
						}

						String tempNewOfferPrice = customerDetails.get("NewPriceWithDiscount");
						if (!"".equals(tempNewOfferPrice)) {
							newAmount = Float.valueOf(tempNewOfferPrice);
							newAmount = Float.valueOf(Utility.formattedDecimalNumber(newAmount));
						}
					}

					planDetailJsonObject.put("planId", planId);
					planDetailJsonObject.put("service", serviceName);
					planDetailJsonObject.put("validity", validity);
					planDetailJsonObject.put("discount", discountPercentage);
					planDetailJsonObject.put("billTo", billTo);
					planDetailJsonObject.put("billableCustomerId", "");

					// planDetailJsonObject.put("newAmount", newAmount);
					planDetailJsonObject.put("newAmount", "");
					planDetailJsonObject.put("offerPrice", offerPrice);
					planDetailJsonObject.put("invoiceType", "");
					planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);
					planDetailJsonObject.put("istrialplan", JSONObject.NULL);
					planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
					planDetailJsonObject.put("discountType", discountType);
					planDetailJsonObject.put("discountExpiryDate", JSONObject.NULL);
					planDetailJsonObject.put("serviceId", serviceId); // ->sar

					planDetailJsonObject.put("serialNumber", "");
					planDetailJsonObject.put("discountType", "One-time");

					planJsonObjectList.add(planDetailJsonObject);

				}
				customerJsonObject.put("planMappingList", planJsonObjectList);
				customerJsonObject.put("isInvoiceToOrg", invoiceToOrg);
			}

			// --Plan Group
			customerJsonObject.put("plangroupid", JSONObject.NULL);

			if (planCategory.equalsIgnoreCase("Plan Group")) {

				String billTo = customerDetails.get("BillTo").toUpperCase();
				String invoiceToOrganization = customerDetails.get("InvoiceToOrganization").toUpperCase();
				String billableCustomerId = null;
				float discountPercentage = 0;
				String discountType = null;
				String discountExpiryDate = null;
				boolean invoiceToOrg = false;
				boolean istrialplan = false;

				customerJsonObject.put("billTo", billTo);
				// customerJsonObject.put("billableCustomerId", billableCustomerId);
				customerJsonObject.put("discount", discountPercentage);
				customerJsonObject.put("discountType", discountType);
				customerJsonObject.put("discountExpiryDate", discountExpiryDate);
				customerJsonObject.put("planPurchaseType", "groupPlan");
				customerJsonObject.put("istrialplan", istrialplan);

				String planGroup = customerDetails.get("PlanGroupName");
				String planGroupDetails[] = commonGetAPI.getPlanBundleDetails(planGroup).split(":");

				int planGroupId = Integer.parseInt(planGroupDetails[0]);
				float offerPrice = Float.valueOf(planGroupDetails[1]);

				float flatAmount = 0;
				String tempDiscountPercentage = customerDetails.get("DiscountPercentage");
				customerJsonObject.put("plangroupid", planGroupId);

				if ((billTo.equalsIgnoreCase("CUSTOMER")) && (!"".equals(tempDiscountPercentage))) {
					flatAmount = offerPrice;
					discountPercentage = Float.valueOf(tempDiscountPercentage);
					flatAmount = offerPrice - (offerPrice * discountPercentage / 100);
					flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
					customerJsonObject.put("discount", discountPercentage);
					customerJsonObject.put("flatAmount", flatAmount);

					discountType = customerDetails.get("DiscountType");
					if (!"".equals(discountType)) {
						if ("One-time".equals(discountType)) {
							discountType = "One-time";
						} else if ("Recurring".equals(discountType)) {
							discountType = "Recurring";
							discountExpiryDate = customerDetails.get("DiscountExpiryDate");
							discountExpiryDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(
									discountExpiryDate, "dd-MMM-yyyy", "yyyy-MM-dd");
							discountExpiryDate = discountExpiryDate + "T00:00:00.000Z";
						}
					}
				}

				customerJsonObject.put("discount", discountPercentage);
				customerJsonObject.put("discountType", discountType);
				customerJsonObject.put("discountExpiryDate", discountExpiryDate);

				if (billTo.equalsIgnoreCase("SUBISU")) {

					if (invoiceToOrganization.equalsIgnoreCase("YES")) {
						invoiceToOrg = true;
					}

					customerJsonObject.put("discount", discountPercentage);
					customerJsonObject.put("flatAmount", "");

					String planNameNewOfferPrice = customerDetails.get("[PlanName:NewOfferPrice]");
					planNameNewOfferPrice = planNameNewOfferPrice.replaceAll("[\\[\\]]", "");

					String ans[] = planNameNewOfferPrice.split(",");

					for (int i = 0; i < ans.length; i++) {

						String planNameNewOfferDetails[] = ans[i].split(":");
						String planName = planNameNewOfferDetails[0];
						float newOffer = Float.valueOf(planNameNewOfferDetails[1]);

						JSONObject planDetailJsonObject = new JSONObject();

						int planId = commonGetAPI.getPlanId(planName);
						String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

						String serviceName = planDetails[0];
						offerPrice = Float.valueOf(planDetails[1]);
						int validity = Integer.parseInt(planDetails[2]);
						// String unitsOfValidity = planDetails[3];

						planDetailJsonObject.put("planId", planId);
						planDetailJsonObject.put("name", planName);
						planDetailJsonObject.put("service", serviceName);
						planDetailJsonObject.put("validity", validity);
						planDetailJsonObject.put("billTo", billTo);
						planDetailJsonObject.put("discount", discountPercentage);
						planDetailJsonObject.put("newAmount", newOffer);
						planDetailJsonObject.put("offerPrice", offerPrice);
						planDetailJsonObject.put("chargeName", "");
						planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);

						planDetailJsonObject.put("discountType", discountType);
						planDetailJsonObject.put("discountExpiryDate", discountType);

						planJsonObjectList.add(planDetailJsonObject);
					}
					customerJsonObject.put("planMappingList", planJsonObjectList);
				}

				customerJsonObject.put("istrialplan", istrialplan);
				customerJsonObject.put("isInvoiceToOrg", invoiceToOrg);
			}

			// -- Customer Additional Service DetailsdunningCategory

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

			customerJsonObject.put("macRetentionPeriod", customerDetails.get("MacRetentionPeriod"));
			customerJsonObject.put("macRetentionUnit", customerDetails.get("MacRetentionUnit"));
			customerJsonObject.put("nasPortId", customerDetails.get("NasPortId"));
			customerJsonObject.put("mac_auth_enable", customerDetails.get("Mac_auth_enable"));
			customerJsonObject.put("mac_provision", customerDetails.get("Mac_provision"));

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

		if (addressType.equalsIgnoreCase("Present")) {
			addressType1 = "Present";
			address = customerDetails.get("Address");
			landmark = customerDetails.get("Landmark");
			ward = customerDetails.get("Ward");
			municipality = customerDetails.get("Municipality");

			if ((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
				result = true;
			}

		} else if (addressType.equalsIgnoreCase("Payment")) {
			addressType1 = "Payment";
			address = customerDetails.get("PaymentAddress");
			landmark = customerDetails.get("PaymentLandmark");
			ward = customerDetails.get("PaymentWard");
			municipality = customerDetails.get("PaymentMunicipality");

			if ((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
				result = true;
			}

		} else if (addressType.equalsIgnoreCase("Permanent")) {
			addressType1 = "Permanent";
			address = customerDetails.get("PermanentAddress");
			landmark = customerDetails.get("PermanentLandmark");
			ward = customerDetails.get("PermanentWard");
			municipality = customerDetails.get("PermanentMunicipality");

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

}
