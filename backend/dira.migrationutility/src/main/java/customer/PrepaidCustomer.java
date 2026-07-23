package customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import commons.CommonList;
import productdata.PrepaidPlan;
import temp.UpdateSheet;
import utility.Utility;

public class PrepaidCustomer extends RestExecution {

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

				valuemap.put("RowIndex", cellValue.get("RowIndex"));
				valuemap.put("Title", cellValue.get("Title"));
				valuemap.put("FirstName", cellValue.get("FirstName"));
				valuemap.put("LastName", cellValue.get("LastName"));
				valuemap.put("ContactPerson", cellValue.get("ContactPerson"));
				
				valuemap.put("CAFNo", null);
				valuemap.put("Username", cellValue.get("Username"));
				valuemap.put("Password", cellValue.get("Password"));
				valuemap.put("CalendarType", cellValue.get("CalendarType"));

				valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
				valuemap.put("Branch", cellValue.get("Branch"));
				valuemap.put("Status", cellValue.get("Status"));
				valuemap.put("POP", cellValue.get("POP"));
				
				valuemap.put("ValleyType", cellValue.get("ValleyType"));
				valuemap.put("InsideOutSideValley", cellValue.get("InsideOutSideValley"));
				valuemap.put("DedicatedStaffUserName", cellValue.get("DedicatedStaffUserName"));
				valuemap.put("ParentCustomer", cellValue.get("ParentCustomer"));
				valuemap.put("InvoiceType", cellValue.get("InvoiceType"));
				valuemap.put("CustomerType", cellValue.get("CustomerType"));
				
				valuemap.put("GST", cellValue.get("GST"));
				valuemap.put("PAN", cellValue.get("PAN"));
				valuemap.put("NationalId", cellValue.get("NationalId"));
				valuemap.put("PassportNo", cellValue.get("PassportNo"));
				valuemap.put("VAT", cellValue.get("VAT"));
				
				valuemap.put("CountryCode", cellValue.get("CountryCode"));
				valuemap.put("Mobile", cellValue.get("Mobile"));
				valuemap.put("Telephone", cellValue.get("Telephone"));
				valuemap.put("Email", cellValue.get("Email"));
				valuemap.put("CustomerCategory", cellValue.get("CustomerCategory"));
				valuemap.put("CDCustomerType", cellValue.get("CDCustomerType"));
				valuemap.put("CDCustomerSubType", cellValue.get("CDCustomerSubType"));
				valuemap.put("CustomerSector", cellValue.get("CustomerSector"));
				valuemap.put("CustomerSectorType", cellValue.get("CustomerSectorType"));
				
				valuemap.put("Latitude", cellValue.get("Latitude"));
				valuemap.put("Longitude", cellValue.get("Longitude"));
				
				valuemap.put("Partner", cellValue.get("Partner"));
				valuemap.put("SalesMark", cellValue.get("SalesMark"));
				
				valuemap.put("Amount", cellValue.get("Amount"));
				valuemap.put("ReferenceNo", cellValue.get("ReferenceNo"));
				valuemap.put("PaymentDate", cellValue.get("PaymentDate"));
				valuemap.put("PaymentMode", cellValue.get("PaymentMode"));
				
				valuemap.put("PresentAddress", cellValue.get("PresentAddress"));
				valuemap.put("PresentLandmark", cellValue.get("PresentLandmark"));
				valuemap.put("PresentMunicipality", cellValue.get("PresentMunicipality"));
				valuemap.put("PresentWard", cellValue.get("PresentWard"));
				
				valuemap.put("PaymentAddress", cellValue.get("PaymentAddress"));
				valuemap.put("PaymentLandmark", cellValue.get("PaymentLandmark"));
				valuemap.put("PaymentMunicipality", cellValue.get("PaymentMunicipality"));
				valuemap.put("PaymentWard", cellValue.get("PaymentWard"));
				
				valuemap.put("PermanentAddress", cellValue.get("PermanentAddress"));
				valuemap.put("PermanentLandmark", cellValue.get("PermanentLandmark"));
				valuemap.put("PermanentMunicipality", cellValue.get("PermanentMunicipality"));
				valuemap.put("PermanentWard", cellValue.get("PermanentWard"));

				valuemap.put("PlanCategory", cellValue.get("PlanCategory"));
				valuemap.put("BillTo", cellValue.get("BillTo"));
				valuemap.put("PlanGroup", cellValue.get("PlanGroup"));
				valuemap.put("[PlanName:NewOfferPrice]", cellValue.get("[PlanName:NewOfferPrice]"));
				valuemap.put("InvoiceToOrganization", cellValue.get("InvoiceToOrganization"));
				valuemap.put("NewOfferPrice", cellValue.get("NewOfferPrice"));
				valuemap.put("Service", cellValue.get("Service"));
				valuemap.put("Plan", cellValue.get("Plan"));
				valuemap.put("DiscountPercentage", cellValue.get("DiscountPercentage"));

				valuemap.put("DirectChargeName", cellValue.get("DirectChargeName"));
				valuemap.put("DirectChargeType", cellValue.get("DirectChargeType"));
				valuemap.put("DirectChargeNewPrice", cellValue.get("DirectChargeNewPrice"));

				valuemap.put("VoiceServiceType", cellValue.get("VoiceServiceType"));
				valuemap.put("DIDNo", cellValue.get("DIDNo"));
				
				valuemap.put("NASPortValidate", cellValue.get("NASPortValidate"));
				valuemap.put("FramedIPValidate", cellValue.get("FramedIPValidate"));
				valuemap.put("NASPortBind", cellValue.get("NASPortBind"));
				valuemap.put("FramedIPBind", cellValue.get("FramedIPBind"));
				
				valuemap.put("MAC", cellValue.get("MAC"));

				customerMapList.add(valuemap);
			}
		}
		return customerMapList;
	}

	@SuppressWarnings("unchecked")
	private String getPrepaidCustomerJson(Map<String, String> customerDetails) {

		String jsonString = null;

		try {

			org.json.simple.JSONObject customerJsonObject = new org.json.simple.JSONObject();
			CommonGetAPI commonGetAPI = new CommonGetAPI();
			CommonList commonList = new CommonList();
			
			// ReadData readData = new ReadData();
			// customerJsonObject = readData.readJSONFile("CreatePrepaidCustomer.json");

			customerJsonObject.put("custtype", "Prepaid");

			customerJsonObject.put("title", customerDetails.get("Title"));
			customerJsonObject.put("firstname", customerDetails.get("FirstName"));
			customerJsonObject.put("lastname", customerDetails.get("LastName"));
			customerJsonObject.put("contactperson", customerDetails.get("ContactPerson"));

			customerJsonObject.put("cafno", customerDetails.get("CAFNo"));
			customerJsonObject.put("username", customerDetails.get("Username"));
			customerJsonObject.put("password", customerDetails.get("Password"));
			customerJsonObject.put("calendarType", customerDetails.get("CalendarType"));
			
			int serviceAreaId = commonGetAPI.getServiceAreaIdList(customerDetails.get("ServiceArea")).get(0);
			customerJsonObject.put("serviceareaid", serviceAreaId);
			
			customerJsonObject.put("branch", null);
			String branchName = customerDetails.get("Branch");
			if (!"".equals(branchName)) {
				int branchId = commonGetAPI.getBranchIdList(branchName).get(0);
				customerJsonObject.put("branch", branchId);
			}
			
			customerJsonObject.put("status", customerDetails.get("Status"));
			
			customerJsonObject.put("popid", null);
			String popName = customerDetails.get("POP");
			if (!"".equals(popName)) {
				int popId = commonGetAPI.getPopId(popName);
				customerJsonObject.put("popid", popId);
			}

			customerJsonObject.put("failcount", 0);
			customerJsonObject.put("isCustCaf", null);
			
			customerJsonObject.put("servicetype", "");
			
			customerJsonObject.put("valleyType", customerDetails.get("ValleyType"));
			customerJsonObject.put("customerArea", customerDetails.get("InsideOutSideValley"));
			
			customerJsonObject.put("staffId", null);
			String staffUserName = customerDetails.get("DedicatedStaffUserName");
			if (!"".equals(staffUserName)) {
				int staffId = commonGetAPI.getStaffId(customerDetails.get("DedicatedStaffUserName"));
				customerJsonObject.put("staffId", staffId);
			}
			
			customerJsonObject.put("parentCustomerId", null);
			customerJsonObject.put("custlabel", customerDetails.get("CustomerType").toLowerCase());

			String parentCustomer = customerDetails.get("ParentCustomer");
			if (!"".equals(parentCustomer)) {
				int parentCustomerId = getCustomerId(parentCustomer);
				if (parentCustomerId != 0) {
					customerJsonObject.put("parentCustomerId", parentCustomerId);
					customerJsonObject.put("invoiceType", customerDetails.get("InvoiceType"));
				}
			}
			
			// -- Customer KYC Details --
			
			customerJsonObject.put("gst", customerDetails.get("GST"));
			customerJsonObject.put("pan", customerDetails.get("PAN"));
			customerJsonObject.put("aadhar", customerDetails.get("NationalId"));
			customerJsonObject.put("passportNo", customerDetails.get("PassportNo"));
			customerJsonObject.put("tinNo", customerDetails.get("VAT"));

			// -- Customer Contact Details --
			
			customerJsonObject.put("countryCode", "+" + customerDetails.get("CountryCode"));
			customerJsonObject.put("mobile", customerDetails.get("Mobile"));
			customerJsonObject.put("phone", customerDetails.get("Telephone"));
			customerJsonObject.put("email", customerDetails.get("Email"));
			customerJsonObject.put("dunningCategory", customerDetails.get("CustomerCategory"));
			customerJsonObject.put("dunningType", customerDetails.get("CDCustomerType"));
			customerJsonObject.put("dunningSubType", customerDetails.get("CDCustomerSubType"));
			customerJsonObject.put("dunningSector", customerDetails.get("CustomerSector"));
			customerJsonObject.put("dunningSubSector", customerDetails.get("CustomerSectorType"));

			// -- Customer Subscriber connection & Network Details

			customerJsonObject.put("latitude", customerDetails.get("Latitude"));
			customerJsonObject.put("longitude", customerDetails.get("Longitude"));

			// -- Customer Business Partner Details
			
			String partner = customerDetails.get("Partner");
			int partnerId = commonGetAPI.getPartnerId(partner);
			customerJsonObject.put("partnerid", partnerId);
			customerJsonObject.put("salesremark", customerDetails.get("SalesMark"));

			// -- Customer Payment Details --
			org.json.simple.JSONObject paymentJson = new org.json.simple.JSONObject();
			
			//valuemap.put("PaymentMode", cellValue.get("PaymentMode"));
			
			
			paymentJson.put("amount", 0);
			paymentJson.put("paymode", null);
			paymentJson.put("referenceno", customerDetails.get("ReferenceNo"));
			paymentJson.put("paymentdate", null);
			
			String txtAmount = customerDetails.get("Amount");
			if (!"".equals(txtAmount)) {
				float amount = Float.valueOf(txtAmount);
				paymentJson.put("amount", amount);
			}
			
			String paymentMode = customerDetails.get("PaymentMode");
			if (!"".equals(paymentMode)) {
				paymentMode = commonList.getCommonPaymentMode(paymentMode);
				paymentJson.put("paymode", paymentMode);
			}
			
			String paymentdate = customerDetails.get("PaymentDate");
			if (!"".equals(paymentdate)) {
				paymentdate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(paymentdate, "dd-MMM-yyyy", "yyyy-MM-dd");
				paymentJson.put("paymentdate", paymentdate);
			}
			
			customerJsonObject.put("paymentDetails", paymentJson);

			//  Customer Address Details -->

			List<org.json.simple.JSONObject> addressJsonObjectList = new ArrayList<org.json.simple.JSONObject>();
			org.json.simple.JSONObject presentAddressDetail = getCustomerAddressJson("Present",customerDetails);
			if(presentAddressDetail != null) { addressJsonObjectList.add(presentAddressDetail); }
			
			org.json.simple.JSONObject paymentAddressDetail = getCustomerAddressJson("Payment",customerDetails);
			if(paymentAddressDetail != null) { addressJsonObjectList.add(paymentAddressDetail); }
			
			org.json.simple.JSONObject permanentAddressDetail = getCustomerAddressJson("Permanent",customerDetails);
			if(permanentAddressDetail != null) { addressJsonObjectList.add(permanentAddressDetail); }
			
			customerJsonObject.put("addressList", addressJsonObjectList);

			// --PlanMappingDetails
			customerJsonObject.put("istrialplan", false);
			String planCategory = customerDetails.get("PlanCategory");

			//Individual Plan
			if (planCategory.equalsIgnoreCase("Individual")) {

				String billTo = customerDetails.get("BillTo").toUpperCase();
				String invoiceToOrganization = customerDetails.get("InvoiceToOrganization").toUpperCase();
				boolean invoiceToOrg = false;
				boolean istrialplan = false;
				
				customerJsonObject.put("billTo", billTo);
				customerJsonObject.put("discount", 0);

				int planId = commonGetAPI.getPlanId(customerDetails.get("Plan"));
				
				int serviceId = commonGetAPI.getServiceId(customerDetails.get("Service")); //-->sar
				   
				String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

				String serviceName = planDetails[0];
				float offerPrice = Float.valueOf(planDetails[1]);
				int validity = Integer.parseInt(planDetails[2]);
				// String unitsOfValidity = planDetails[3];

				float flatAmount = offerPrice;
				float discountPercentage = 0;
				String tempDiscountPercentage = customerDetails.get("DiscountPercentage");

				if ((billTo.equalsIgnoreCase("CUSTOMER")) && (!"".equals(tempDiscountPercentage))) {
					discountPercentage = Float.valueOf(customerDetails.get("DiscountPercentage"));
					flatAmount = offerPrice - (offerPrice * discountPercentage / 100);
					flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
				}

				customerJsonObject.put("flatAmount", flatAmount);

				List<org.json.simple.JSONObject> planJsonObjectList = new ArrayList<org.json.simple.JSONObject>();
				org.json.simple.JSONObject planDetailJsonObject = new org.json.simple.JSONObject();

				planDetailJsonObject.put("newAmount", null);
				float newAmount = 0;

				if (billTo.equalsIgnoreCase("SUBISU")) {
					if (invoiceToOrganization.equalsIgnoreCase("YES")) {
						invoiceToOrg = true;
					}

					String tempNewOfferPrice = customerDetails.get("NewOfferPrice");
					if (!"".equals(tempNewOfferPrice)) {
						newAmount = Float.valueOf(tempNewOfferPrice);
						newAmount = Float.valueOf(Utility.formattedDecimalNumber(newAmount));
						//planDetailJsonObject.put("newAmount", newAmount);
					} else {
						//planDetailJsonObject.put("newAmount", offerPrice);
						newAmount = offerPrice;
					}
				}
				
				
				planDetailJsonObject.put("planId", planId);
				 
				planDetailJsonObject.put("serviceId", serviceId);   //-->sar 
				planDetailJsonObject.put("billableCustomerId", "");    //--sar
				planDetailJsonObject.put("serialNumber", JSONObject.NULL);  //-->sar
				planDetailJsonObject.put("billTo", billTo);
				planDetailJsonObject.put("service", serviceName);
				planDetailJsonObject.put("validity", validity);
				planDetailJsonObject.put("discount", discountPercentage);
				planDetailJsonObject.put("newAmount", newAmount);
				planDetailJsonObject.put("offerPrice", offerPrice);
				planDetailJsonObject.put("invoiceType", null);
				planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);
				planDetailJsonObject.put("istrialplan", null);

				planJsonObjectList.add(planDetailJsonObject);
				customerJsonObject.put("planMappingList", planJsonObjectList);
				customerJsonObject.put("istrialplan", istrialplan);
				customerJsonObject.put("isInvoiceToOrg", invoiceToOrg);
				
			}

			// --Plan Group
			customerJsonObject.put("plangroupid", null);

			if (planCategory.equalsIgnoreCase("Plan Group")) {

				String billTo = customerDetails.get("BillTo").toUpperCase();
				String invoiceToOrganization = customerDetails.get("InvoiceToOrganization").toUpperCase();
				boolean invoiceToOrg = false;
				boolean istrialplan = false;

				customerJsonObject.put("billTo", billTo);
				customerJsonObject.put("discount", 0);

				String planGroup = customerDetails.get("PlanGroup");
				String planGroupDetails[] = commonGetAPI.getPlanBundleDetails(planGroup).split(":");

				int planGroupId = Integer.parseInt(planGroupDetails[0]);
				float offerPrice = Float.valueOf(planGroupDetails[1]);

				float flatAmount = 0;
				float discountPercentage = 0;
				String tempDiscountPercentage = customerDetails.get("DiscountPercentage");

				customerJsonObject.put("plangroupid", planGroupId);

				if ((billTo.equalsIgnoreCase("CUSTOMER")) && (!"".equals(tempDiscountPercentage))) {
					flatAmount = offerPrice;
					discountPercentage = Float.valueOf(customerDetails.get("DiscountPercentage"));
					flatAmount = offerPrice - (offerPrice * discountPercentage / 100);
					flatAmount = Float.valueOf(Utility.formattedDecimalNumber(flatAmount));
					customerJsonObject.put("discount", discountPercentage);
					customerJsonObject.put("flatAmount", flatAmount);
				}

				if (billTo.equalsIgnoreCase("SUBISU")) {

					if (invoiceToOrganization.equalsIgnoreCase("YES")) {
						invoiceToOrg = true;
					}

					customerJsonObject.put("discount", discountPercentage);
					customerJsonObject.put("flatAmount", flatAmount);

					List<org.json.simple.JSONObject> planJsonObjectList = new ArrayList<org.json.simple.JSONObject>();

					String planNameNewOfferPrice = customerDetails.get("[PlanName:NewOfferPrice]");
					planNameNewOfferPrice = planNameNewOfferPrice.replaceAll("[\\[\\]]", "");

					String ans[] = planNameNewOfferPrice.split(",");

					for (int i = 0; i < ans.length; i++) {

						String planNameNewOfferDetails[] = ans[i].split(":");
						String planName = planNameNewOfferDetails[0];
						float newOffer = Float.valueOf(planNameNewOfferDetails[1]);

						org.json.simple.JSONObject planDetailJsonObject = new org.json.simple.JSONObject();
						
						
						int planId = commonGetAPI.getPlanId(planName);
						String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

						String serviceName = planDetails[0];
						int serviceId = commonGetAPI.getServiceId(serviceName);//--> sar
						offerPrice = Float.valueOf(planDetails[1]);
						int validity = Integer.parseInt(planDetails[2]);
						
						// String unitsOfValidity = planDetails[3];
					//	planDetailJsonObject.put("serviceId", serviceId);     //-->sar 
						planDetailJsonObject.put("planId", planId);
						
						
						planDetailJsonObject.put("name", planName);  //-->
						planDetailJsonObject.put("service", serviceName);
						planDetailJsonObject.put("validity", validity);
						planDetailJsonObject.put("billTo", billTo);
						planDetailJsonObject.put("discount", discountPercentage);
						planDetailJsonObject.put("newAmount", newOffer);
						planDetailJsonObject.put("offerPrice", offerPrice);
						planDetailJsonObject.put("offerPrice", offerPrice); 
					 	//--sar
						planDetailJsonObject.put("chargeName", "");
						planDetailJsonObject.put("isInvoiceToOrg", invoiceToOrg);

						planJsonObjectList.add(planDetailJsonObject);
					}
					customerJsonObject.put("planMappingList", planJsonObjectList);
				}

				customerJsonObject.put("istrialplan", istrialplan);
				customerJsonObject.put("isInvoiceToOrg", invoiceToOrg);
			}

			// -- Customer Additional Service Details

			customerJsonObject.put("voicesrvtype", customerDetails.get("VoiceServiceType"));
			customerJsonObject.put("didno", customerDetails.get("DIDNo"));

			// -- Radius Service Details

			customerJsonObject.put("nasPort", null);
			customerJsonObject.put("framedIp", null);
			customerJsonObject.put("framedIpBind", null);
			customerJsonObject.put("ipPoolNameBind", null);
			
			String nasPort = customerDetails.get("NASPortValidate");
			if (!"".equals(nasPort)) { customerJsonObject.put("nasPort", nasPort); }
			
			String framedIp = customerDetails.get("FramedIPValidate");
			if (!"".equals(framedIp)) { customerJsonObject.put("framedIp", framedIp); }
			
			String framedIpBind = customerDetails.get("NASPortBind");
			if (!"".equals(framedIpBind)) { customerJsonObject.put("framedIpBind", framedIpBind); }
			
			String ipPoolNameBind = customerDetails.get("FramedIPBind");
			if (!"".equals(ipPoolNameBind)) { customerJsonObject.put("ipPoolNameBind", ipPoolNameBind); }
			
			
			// -- Over Direct Charge Mapping

			List<org.json.simple.JSONObject> chargeJsonObjectList = new ArrayList<org.json.simple.JSONObject>();
			String directChargeName = customerDetails.get("DirectChargeName");

			if (!"".equals(directChargeName)) {

				int planId = commonGetAPI.getPlanId(customerDetails.get("Plan"));
				String planDetails[] = commonGetAPI.getPlanDetails(planId).split(":");

				//String serviceName = planDetails[0]; //-->sarfraz
				// float offerPrice = Float.valueOf(planDetails[1]);
				int validity = Integer.parseInt(planDetails[2]);
				String unitsOfValidity = planDetails[3];

				float directChargeNewPrice = Float.valueOf(customerDetails.get("DirectChargeNewPrice"));
				int chargeId = commonGetAPI.getChargeId(directChargeName);

				PrepaidPlan prepaidPlan = new PrepaidPlan();
				String taxAmountAndActualPrice = prepaidPlan.getChargeByIdAndTaxAmountAndActualPrice(chargeId);
				String ans[] = taxAmountAndActualPrice.split(":");
				// float taxamount = Float.parseFloat(ans[0]);
				float actualprice = Float.parseFloat(ans[1]);

				org.json.simple.JSONObject chargeJsonObject = new org.json.simple.JSONObject();
				
				String currentDate = Utility.getCurrentDateTimeByProvidedFormat("yyyy-MM-dd");

				chargeJsonObject.put("actualprice", actualprice);
				chargeJsonObject.put("billingCycle", null);
				chargeJsonObject.put("charge_date", currentDate);
				chargeJsonObject.put("chargeid", chargeId);
				chargeJsonObject.put("id", null);

				chargeJsonObject.put("planid", planId);
				chargeJsonObject.put("price", directChargeNewPrice);
				chargeJsonObject.put("type", "One-time");
				chargeJsonObject.put("unitsOfValidity", unitsOfValidity);
				chargeJsonObject.put("validity", validity);
				
				chargeJsonObjectList.add(chargeJsonObject);
			}
			customerJsonObject.put("overChargeList", chargeJsonObjectList);

			// --Customer MAC Addresses Mapping

			List<org.json.simple.JSONObject> macJsonObjectList = new ArrayList<org.json.simple.JSONObject>();
			String macList = customerDetails.get("MAC");

			if (!"".equals(macList)) {

				String macArray[] = customerDetails.get("MAC").split(",");

				for (int i = 0; i < macArray.length; i++) {
					org.json.simple.JSONObject macObject = new org.json.simple.JSONObject();
					macObject.put("macAddress", macArray[i]);
					macJsonObjectList.add(macObject);
				}
			}
			customerJsonObject.put("custMacMapppingList", macJsonObjectList);

			jsonString = customerJsonObject.toJSONString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
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

	
	
	@SuppressWarnings("unchecked")
	public int getCustomerId(String userName) {

		String jsonString = null;
		org.json.simple.JSONObject searchCustomerJson = new org.json.simple.JSONObject();

		// ReadData readData = new ReadData();
		// searchCustomerJsonObject =
		// readData.readJSONFile("SearchPrepaidCustomer.json");

		List<org.json.simple.JSONObject> customerFilterJsonObjectList = new ArrayList<org.json.simple.JSONObject>();
		org.json.simple.JSONObject filterObject = new org.json.simple.JSONObject();

		filterObject.put("filterDataType", "");
		filterObject.put("filterValue", userName);
		filterObject.put("filterColumn", "username");
		filterObject.put("filterOperator", "equalto");
		filterObject.put("filterCondition", "and");

		customerFilterJsonObjectList.add(filterObject);
		searchCustomerJson.put("filters", customerFilterJsonObjectList);

		searchCustomerJson.put("page", 1);
		searchCustomerJson.put("pageSize", 5);

		jsonString = searchCustomerJson.toJSONString();

		String apiURL = getAPIURL("customers/search/Prepaid");
		String APIBody = jsonString;

		JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
		int status = JSONResponseBody.getInt("status");
		int customerId = 0;

		if (status == 200) {
			JSONArray jsonArray = JSONResponseBody.getJSONArray("customerList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedUserName = jsonArray.getJSONObject(i).getString("username");
				if (receivedUserName.equalsIgnoreCase(userName)) {
					customerId = jsonArray.getJSONObject(i).getInt("id");
					break;
				}
			}
		}

		if (customerId == 0) {
			System.out.println("Customer details not found - " + userName);
			Utility.printLog(logFileName, logModuleName, "Customer details not found - ", userName);
		}

		return customerId;
	}

	@SuppressWarnings("unchecked")
	private org.json.simple.JSONObject getCustomerAddressJson(String addressType,Map<String, String> customerDetails){
		
		CommonGetAPI commonGetAPI = new CommonGetAPI();
		org.json.simple.JSONObject addressDetailJsonObject = new org.json.simple.JSONObject();
		
		boolean result=false;
		String addressType1 = "";
		String address = "";
		String landmark = "";
		String ward = "";
		String municipality = "";
		
		if(addressType.equalsIgnoreCase("Present")) {
			addressType1 = "Present";
			address = customerDetails.get("PresentAddress");
			landmark = customerDetails.get("PresentLandmark");
			ward = customerDetails.get("PresentWard");
			municipality = customerDetails.get("PresentMunicipality");
			
			if((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
				result = true;
			}
			
		} else if(addressType.equalsIgnoreCase("Payment")) {
			addressType1 = "Payment";
			address = customerDetails.get("PaymentAddress");
			landmark = customerDetails.get("PaymentLandmark");
			ward = customerDetails.get("PaymentWard");
			municipality = customerDetails.get("PaymentMunicipality");
			
			if((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
				result = true;
			}
			
		} else if(addressType.equalsIgnoreCase("Permanent")) {
			addressType1 = "Permanent";
			address = customerDetails.get("PermanentAddress");
			landmark = customerDetails.get("PermanentLandmark");
			ward = customerDetails.get("PermanentWard");
			municipality = customerDetails.get("PermanentMunicipality");
			
			if((!"".equals(address)) && (!"".equals(landmark)) && (!"".equals(ward)) && (!"".equals(municipality))) {
				result = true;
			}
		}
		
		if(result) {
			
			String wardHeirarchyDetail = commonGetAPI.getWardHierarchyDetailsByWardName(ward,municipality);
			String detail[] = wardHeirarchyDetail.split(":");

			int wardId = Integer.parseInt(detail[0]);
			int pincodeId = Integer.parseInt(detail[1]);
			int cityId = Integer.parseInt(detail[2]);
			int stateId = Integer.parseInt(detail[3]);
			int countryId = Integer.parseInt(detail[4]);

			addressDetailJsonObject.put("addressType", addressType1);
			addressDetailJsonObject.put("landmark", address);
			addressDetailJsonObject.put("landmark1", landmark);
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
