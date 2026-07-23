package tumil;
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
import customer.CreditNote;
import utility.Constant;
import utility.Utility;
public class RecordPaymentTumil extends RestExecution {
	
		
			private static String logFileName = "CustomerRcordPayment.log";
			private static String logModuleName = "RecordPayment";
			private static int customerId = 0;
			private void recordCustomerPaymentDetails(Map<String, String> customerDetailsMap) {

				StopWatch sw = new StopWatch();
				sw.start();		
				
				String apiURL = getAPIURL("Revenue/record/payment");
				Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

				String APIBody = getPrepaidCustomerPaymentJson(customerDetailsMap);
				Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

				if (!APIBody.equals(null)) {

					String fileName = customerDetailsMap.get("FileNameToAttach");
					if ((fileName != null) && (!"".equals(fileName))) {
						String filePath = Constant.BASE_PATH + "\\TestData\\input\\uploads\\payment\\";
						fileName = filePath + fileName;
					}

					JSONObject JSONResponseBody = httpPostFormData(apiURL, APIBody, fileName);
					String response = JSONResponseBody.toString(4);
					Utility.printLog(logFileName, logModuleName, "Response", response);

					int status = JSONResponseBody.getInt("status");
					String userName = customerDetailsMap.get("CustomerUsername");
					float amount = Float.valueOf(customerDetailsMap.get("Amount"));

					if (status == 200) {
						String message = "New Payment of " + amount + " is done successfully for - " + userName + "|" + sw.getTime();
						System.out.println(message);
						Utility.printLog("execution.log", logModuleName, "Success", message);
						
						approvePayment(customerId,amount);
						System.out.println("Total time = " + sw.getTime());
						
					} else if (status == 406) {
						String error = JSONResponseBody.getString("responseMessage") + " - " + userName;
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "Already Exist", error);
					} else {
						String error = "Error: " + JSONResponseBody.get("ERROR") + " - " + userName;
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "ERROR", error);
					}
				}
			}

			public void recordCustomerPaymentDetails(List<Map<String, String>> customerMapList) {

				CommonGetAPI commonGetAPI = new CommonGetAPI();
				
				for (int i = 0; i < customerMapList.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map = customerMapList.get(i);

					String userName = map.get("CustomerUsername");
					if (commonGetAPI.checkcustomerUsernameIsAlreadyExists(userName)) {
						Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
						recordCustomerPaymentDetails(map);
					} else {
						System.out.println("Customer UserName is not Exists! - " + userName);
					}
				}
			}

			public List<Map<String, String>> readUniqueCustomerPaymentDetailsList() {

				String sheetName = "PaymentDetails";
				List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
				ReadData readData = new ReadData();
				//
				sheetMap = readData.getTumilCustomerDataSheet(sheetName);

				Map<String, String> cellValue = new HashMap<String, String>();
				List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

				for (int i = 0; i < sheetMap.size(); i++) {

					Map<String, String> valuemap = new HashMap<String, String>();
					cellValue = sheetMap.get(i);

					String userName = cellValue.get("CustomerUsername");
					String mStatus = cellValue.get("MigrationStatus");

					if ((!"".equals(userName)) && (!"Success".equalsIgnoreCase(mStatus))) {

						valuemap.put("RowIndex", cellValue.get("RowIndex"));
						valuemap.put("SubscriberType", cellValue.get("SubscriberType"));
						valuemap.put("CustomerUsername", cellValue.get("CustomerUsername"));
						valuemap.put("DocumentNumber", cellValue.get("DocumentNumber"));				
						valuemap.put("PaymentMode", cellValue.get("PaymentMode"));
						valuemap.put("Source", cellValue.get("Source"));
						valuemap.put("Amount", cellValue.get("Amount"));
						valuemap.put("FileNameToAttach", cellValue.get("FileNameToAttach"));

						valuemap.put("ChequeNumber", cellValue.get("ChequeNumber"));
						valuemap.put("ChequeDate", cellValue.get("ChequeTransactionDate"));
						valuemap.put("SourceBank", cellValue.get("SourceBank"));
						valuemap.put("DestinationBank", cellValue.get("DestinationBank"));
						valuemap.put("Branch", cellValue.get("Branch"));

						valuemap.put("ReferenceNumber", cellValue.get("ReferenceNumber"));
						valuemap.put("ReceiptNumber", cellValue.get("ReceiptNumber"));
						valuemap.put("TDS", cellValue.get("TDS"));
						valuemap.put("ABBS", cellValue.get("ABBS"));
						valuemap.put("Remark", cellValue.get("Remark"));

						customerMapList.add(valuemap);
					}
				}
				return customerMapList;
			}
			
			@SuppressWarnings("unused")
			private String getPrepaidCustomerPaymentJsonOld(Map<String, String> customerDetails) {

				String jsonString = null;

				try {
					CommonGetAPI commonGetAPI = new CommonGetAPI();
					JSONObject paymentJson = new JSONObject();
					String commonPaymentMode = null;

					String customerType = customerDetails.get("SubscriberType");
					String userName = customerDetails.get("CustomerUsername");
					if (!"".equals(userName)) {
						int customerId = commonGetAPI.getCustomerId(userName,customerType);
						if (customerId != 0) {
							paymentJson.put("customerid", customerId);

							String paymentMode = customerDetails.get("PaymentMode");

							CommonList commonList = new CommonList();
							commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
							paymentJson.put("paymode", commonPaymentMode);
							
							String documentNumber =  customerDetails.get("DocumentNumber");
							CreditNote creditNote = new CreditNote();
							int invoiceId = creditNote.getCustomerInvoiceId(customerId,documentNumber);
							List<Integer> invoiceList = new ArrayList<Integer>();
							invoiceList.add(invoiceId);
							paymentJson.put("invoiceId", invoiceList);
						}
					}

					if (commonPaymentMode.equals("Cheque")) {

						int ChequeNumber = Integer.parseInt(customerDetails.get("ChequeNumber"));
						String chequeDate = customerDetails.get("ChequeDate");
						chequeDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(chequeDate, "dd-MMM-yyyy",
								"yyyy-MM-dd");

						String sourceBank = customerDetails.get("SourceBank");
						int sourceBankId = getBankId(sourceBank, "other");
						String destinationBank = customerDetails.get("DestinationBank");
						int destinationBankId = getBankId(destinationBank, "operator");
						String branch = customerDetails.get("Branch");

						paymentJson.put("chequeno", ChequeNumber);
						paymentJson.put("chequedate", chequeDate);
						paymentJson.put("bankManagement", sourceBankId);
						paymentJson.put("destinationBank", destinationBankId);
						paymentJson.put("branch", branch);

					} else if (commonPaymentMode.equals("Online")) {

						String chequeDate = customerDetails.get("ChequeDate");
						chequeDate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(chequeDate, "dd-MMM-yyyy",
								"yyyy-MM-dd");

						String destinationBank = customerDetails.get("DestinationBank");
						int destinationBankId = getBankId(destinationBank, "operator");
						String branch = customerDetails.get("Branch");
						String onlineSource = customerDetails.get("OnlineSource");
						String paymentReferenceNumber = customerDetails.get("PaymentReferenceNumber");

						paymentJson.put("chequedate", chequeDate);
						paymentJson.put("bank", "");
						paymentJson.put("destinationBank", destinationBankId);
						paymentJson.put("branch", branch);

						paymentJson.put("onlinesource", onlineSource);
						paymentJson.put("paymentreferenceno", paymentReferenceNumber);

					} else if ((commonPaymentMode.equals("Credit Card")) || (commonPaymentMode.equals("Debit Card"))
							|| (commonPaymentMode.equals("NEFT/RTGS")) || (commonPaymentMode.equals("EFTs"))) {

						String paymentReferenceNumber = customerDetails.get("PaymentReferenceNumber");

						paymentJson.put("bank", JSONObject.NULL);
						paymentJson.put("branch", JSONObject.NULL);
						paymentJson.put("paymentreferenceno", paymentReferenceNumber);

					}

					float amount = Float.valueOf(customerDetails.get("Amount"));
					paymentJson.put("amount", amount);
					paymentJson.put("referenceno", customerDetails.get("ReferenceNumber"));
					paymentJson.put("reciptNo", customerDetails.get("ReceiptNumber"));
					paymentJson.put("remark", customerDetails.get("Remark"));
					paymentJson.put("type", "Payment");
					paymentJson.put("paytype", "invoice");
					paymentJson.put("tdsAmount", JSONObject.NULL);
					paymentJson.put("abbsAmount", JSONObject.NULL);

					boolean tds = Boolean.valueOf(customerDetails.get("TDS"));
					if (tds) {
						float tdsAmount = (float) (amount * 0.10);
						paymentJson.put("tdsAmount", tdsAmount);
					}

					boolean abbs = Boolean.valueOf(customerDetails.get("ABBS"));
					if (abbs) {
						float abbsAmount = (float) (amount * 0.10);
						paymentJson.put("abbsAmount", abbsAmount);
					}

					String fileName = customerDetails.get("FileNameToAttach");
					if (!"".equals(fileName)) {
						paymentJson.put("filename", fileName);
					}

					jsonString = paymentJson.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}

				return jsonString;
			}


			@SuppressWarnings("unchecked")
			private String getPrepaidCustomerPaymentJson(Map<String, String> customerDetails) {

				String jsonString = null;

				try {
					
					CommonGetAPI commonGetAPI = new CommonGetAPI();
					JSONObject paymentJson = new JSONObject();
					String commonPaymentMode = null;

					String customerType = customerDetails.get("SubscriberType");
					String userName = customerDetails.get("CustomerUsername");
					int invoiceId = 0;
					if (userName != null && !"".equals(userName)) {
						customerId = commonGetAPI.getCustomerId(userName,customerType);
						if (customerId != 0) {
							paymentJson.put("customerid", customerId);

							String paymentMode = customerDetails.get("PaymentMode");

							CommonList commonList = new CommonList();
							commonPaymentMode = commonList.getCommonPaymentMode(paymentMode);
							paymentJson.put("paymode", commonPaymentMode);
							
							String documentNumber =  customerDetails.get("DocumentNumber");
							CreditNote creditNote = new CreditNote();
							invoiceId = creditNote.getCustomerInvoiceId(customerId,documentNumber);
							List<Integer> invoiceList = new ArrayList<Integer>();
							invoiceList.add(invoiceId);
							paymentJson.put("invoiceId", invoiceList);
						}
					}

					paymentJson.put("onlinesource", "Cash_In_Hand");
					
					float tempAmount = Float.valueOf(customerDetails.get("Amount"));
					String amount = Utility.formattedDecimalNumber(tempAmount);
					paymentJson.put("amount", amount);
					paymentJson.put("referenceno", customerDetails.get("ReferenceNumber"));
					paymentJson.put("reciptNo", customerDetails.get("ReceiptNumber"));
					paymentJson.put("remark", customerDetails.get("Remark"));
					paymentJson.put("type", "Payment");
					paymentJson.put("paytype", "invoice");
					paymentJson.put("bank", "");
					paymentJson.put("tdsAmount", 0);
					paymentJson.put("abbsAmount", 0);

					boolean tds = Boolean.valueOf(customerDetails.get("TDS"));
					float tdsAmount = 0f;
					if (tds) {
						float tempTDSAmount = tempAmount * 0.10f;
						tdsAmount = Float.valueOf(Utility.formattedDecimalNumber(tempTDSAmount));
						paymentJson.put("tdsAmount", tdsAmount);
					}

					boolean abbs = Boolean.valueOf(customerDetails.get("ABBS"));
					float abbsAmount = 0f;
					if (abbs) {
						float tempABBSAmount = tempAmount * 0.10f;
						abbsAmount = Float.valueOf(Utility.formattedDecimalNumber(tempABBSAmount));
						paymentJson.put("abbsAmount", abbsAmount);
					}

					String fileName = customerDetails.get("FileNameToAttach");
					if ((!"".equals(fileName)) && (fileName != null)) {
						paymentJson.put("filename", fileName);
					}

					List<JSONObject> paymentListPojos = new ArrayList<JSONObject>();
					JSONObject paymentListPojosJson = new JSONObject();
					paymentListPojosJson.put("abbsAmountAgainstInvoice", abbsAmount);
					paymentListPojosJson.put("amountAgainstInvoice", tempAmount);
					paymentListPojosJson.put("invoiceId", invoiceId);
					paymentListPojosJson.put("tdsAmountAgainstInvoice", tdsAmount);
					
					paymentListPojos.add(paymentListPojosJson);
					
					paymentJson.put("paymentListPojos", paymentListPojos);
					
					jsonString = paymentJson.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}

				return jsonString;
			}

			public int getBankId(String bankName, String bankType) {

				String apiURL = "bankManagement/searchByStatus?banktype=" + bankType;
				apiURL = getAPIURL(apiURL);

				JSONObject jsonResponse = httpGet(apiURL);
				// String ans = jsonResponse.toString(4);

				int status = jsonResponse.getInt("responseCode");
				int bankId = 0;

				if (status == 200) {
					JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
					for (int i = 0; i < jsonArray.length(); i++) {
						String receivedBankName = jsonArray.getJSONObject(i).getString("bankname");
						if (receivedBankName.equalsIgnoreCase(bankName)) {
							bankId = jsonArray.getJSONObject(i).getInt("id");
							break;
						}
					}
				}

				if (bankId == 0) {
					String message = "Bank details not found - ";
					System.out.println(message + bankName);
					Utility.printLog(logFileName, logModuleName, message, bankName);
				}

				return bankId;
			}

			private void approvePayment(int custId, float paymentAmount) {

				try {

					JSONObject approvePayment = new JSONObject();
					JSONObject paymentHistoryJson = getCustomerPaymentHistory(custId,paymentAmount);
					
					float amount =  paymentHistoryJson.getFloat("amount");
					int idlist =  paymentHistoryJson.getInt("id");
					String paymode =  paymentHistoryJson.getString("paymode");
				//	String invoiceNumber =  paymentHistoryJson.getString("invoiceNumber");
					// here we will get 
					
					String invoiceNumber = paymentHistoryJson.optString("invoiceNumber", "defaultInvoiceNumber");  // I had add default 

					String paystatus = paymentHistoryJson.getString("status");
					String paytodate =  paymentHistoryJson.getString("paymentdate");
					paytodate = Utility.getDateTimeInRequiredFormatFromProvidedDateTime(paytodate, "dd-MM-yyyy",
							"yyyy-MM-dd");
					String referenceno =  paymentHistoryJson.getString("referenceno");
					String remarks =  "Migration created "+paymode+" payment is approved by migration";
					
					approvePayment.put("customerid", customerId);
					approvePayment.put("idlist", idlist);
					approvePayment.put("invoiceNumber", invoiceNumber);
					approvePayment.put("paymode", paymode);
					approvePayment.put("paystatus", paystatus);
					approvePayment.put("paytodate", paytodate);
					approvePayment.put("referenceno", referenceno);
					approvePayment.put("remarks", remarks);
					
					String apiURL = getAPIURL("cpm/payment/approve");
					String apiBody = approvePayment.toString();
					JSONObject JSONResponseBody =  httpPost(apiURL, apiBody);

					int status = JSONResponseBody.getInt("status");

					if (status == 200 || status == 0) {
						String message = paymode + " payment of " +amount+  " is approved successfully.";
						System.out.println(message);
						Utility.printLog("execution.log", logModuleName, "Success", message);

					} else {
						String error = "Error: " + JSONResponseBody.get("ERROR");
						System.out.println(error);
						Utility.printLog("execution.log", logModuleName, "ERROR", error);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			public JSONObject getCustomerPaymentHistory(int custId, float amount) {

				String apiURL = "Revenue/paymentHistory/" + custId;
				apiURL = getAPIURL(apiURL);

				JSONObject jsonResponse = httpGet(apiURL);
				int status = jsonResponse.getInt("responseCode");
				JSONObject paymentHistoryJson = null;

				if (status == 200) {
					JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
					for (int i = 0; i < jsonArray.length(); i++) {
						String paymentStatus = jsonArray.getJSONObject(i).getString("status");
						String paymentType = jsonArray.getJSONObject(i).getString("type");
						float paymentAmount =  jsonArray.getJSONObject(i).getFloat("amount");
						if (paymentStatus.equalsIgnoreCase("pending") && paymentType.equalsIgnoreCase("Payment")) {
							if(amount == paymentAmount) {
								paymentHistoryJson = jsonArray.getJSONObject(i);
								break;
							}
						}
					}
				}

				if (paymentHistoryJson == null) {
					String message = "Payment history details not found - ";
					System.out.println(message + custId);
					Utility.printLog(logFileName, logModuleName, message, String.valueOf(custId));
				}

				return paymentHistoryJson;
			}
				

		

	}

