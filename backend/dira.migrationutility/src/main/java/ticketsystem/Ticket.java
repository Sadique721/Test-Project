package ticketsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import customer.AssignInventory;
import utility.Constant;
import utility.ProductUtility;
import utility.Utility;

public class Ticket extends RestExecution {
	
	private static String logFileName = "ticketdata.log";
	private static String logModuleName = "Ticket";
	private static String basePath =  Constant.BASE_PATH + "\\TestData\\input\\uploads\\ticket\\";
	
	private void createTicket(Map<String, String> ticketDetails) {
		
		String apiURL = getAPIURL("TicketManagement/case/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getTicketJson(ticketDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if(apiBody != null) {
			
			String fileName = ticketDetails.get("FileNameToAttach");
			if ((fileName != null) && (!"".equals(fileName))) {
				fileName = basePath + "\\" + fileName;
			}

			JSONObject JSONResponseBody = httpPostFormData2(apiURL, apiBody, fileName);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);
			
			String username = ticketDetails.get("Username");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, username);
		}
	}

    public void createTicket(List<Map<String, String>> problemDomainMapList) {

        int numThreads = Math.min(30, problemDomainMapList.size()); // Max threads = 5
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (Map<String, String> map : problemDomainMapList) {
            executorService.submit(() -> {
                try {
                    Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
                    createTicket(map);  // ✅ calling your existing same method
                } catch (Exception e) {
                    Utility.printLog(logFileName, logModuleName,
                            "Error", "Ticket creation failed for: " + map + " | " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.MINUTES)) {
                Utility.printLog(logFileName, logModuleName,
                        "Timeout", "Some ticket creation tasks did not finish in time!");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Utility.printLog(logFileName, logModuleName,
                    "Error", "Thread interrupted while waiting!");
            executorService.shutdownNow();
        }
    }




//	public void createTicket(List<Map<String, String>> problemDomainMapList) {
//
//		for (int i = 0; i < problemDomainMapList.size(); i++) {
//
//			Map<String, String> map = new HashMap<String, String>();
//			map = problemDomainMapList.get(i);
//			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
//			createTicket(map);
//		}
//	}

	
	public List<Map<String, String>> readTicketList() {
		
		String sheetName = "Ticket";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getTicketDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> problemDomainMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String problemDomainName = safeTrim(cellValue.get("TicketName"));
			if (!"".equals(problemDomainName)) {

				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("CaseTitle", safeTrim(cellValue.get("CaseTitle")));
				valuemap.put("SubscriberType", safeTrim(cellValue.get("SubscriberType")));
				valuemap.put("Username", safeTrim(cellValue.get("Username")));
				valuemap.put("Services", safeTrim(cellValue.get("Services")));
				valuemap.put("Type", safeTrim(cellValue.get("Type")));
				valuemap.put("TicketType", safeTrim(cellValue.get("TicketType")));
				valuemap.put("TicketProblemDomain", safeTrim(cellValue.get("TicketProblemDomain")));
				valuemap.put("TicketSubProblemDomain", safeTrim(cellValue.get("TicketSubProblemDomain")));
				valuemap.put("Status", safeTrim(cellValue.get("Status")));
				valuemap.put("FileNameToAttach", safeTrim(cellValue.get("FileNameToAttach")));
				valuemap.put("Remark", safeTrim(cellValue.get("Remark")));
				problemDomainMapList.add(valuemap);
			}
		}
		return problemDomainMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }


	private String getTicketJson(Map<String, String> ticket) {

		String jsonString = null;

		try {

			CommonGetAPI commonGetAPI = new CommonGetAPI();
			JSONObject ticketJson = new JSONObject();
			
			ticketJson.put("caseStatus", ticket.get("Status"));
			ticketJson.put("caseTitle", ticket.get("CaseTitle"));
			ticketJson.put("caseType", ticket.get("Type"));
			
			
			String ticketProblemDomain = ticket.get("TicketProblemDomain");
			int ticketReasonCategoryId = commonGetAPI.getReasonCategoryIdList(ticketProblemDomain).get(0);
			ticketJson.put("ticketReasonCategoryId", ticketReasonCategoryId);
			
			String ticketSubProblemDomain = ticket.get("TicketSubProblemDomain");
			int reasonSubCategoryId = commonGetAPI.getSubReasonCategoryId(ticketSubProblemDomain);
			ticketJson.put("reasonSubCategoryId", reasonSubCategoryId);
			
			String nextFollowupDate = Utility.getCurrentDateTimeByProvidedFormat("yyyy-MM-dd");
			String nextFollowupTime = Utility.getCurrentDateTimeByProvidedFormat("HH:mm:ss");
			
			ticketJson.put("nextFollowupDate", nextFollowupDate);
			ticketJson.put("nextFollowupTime", nextFollowupTime);
			
			AssignInventory assignInventory = new AssignInventory();
			String customerUsername = ticket.get("Username");
			String customerType = ticket.get("SubscriberType");
			int customerId = commonGetAPI.getCustomerId(customerUsername,customerType);
			if (customerId != 0) {
				ticketJson.put("customersId", customerId);
			}
			
			
			
			String serviceName = ticket.get("Services");
			List<Integer> serviceIds = commonGetAPI.getServiceIdList(serviceName);
			JSONObject serviceIdsJson = new JSONObject();
			for(int i=0;i<serviceIds.size();i++) {
				serviceIdsJson.put("serviceid", serviceIds.get(i));
			}
			
			List<JSONObject> ticketServicemappingList = new ArrayList<JSONObject>();
			ticketServicemappingList.add(serviceIdsJson);
			ticketJson.put("ticketServicemappingList", ticketServicemappingList );	
			
			
			ticketJson.put("department", ticket.get("TicketType"));	
			ticketJson.put("firstRemark", ticket.get("Remark"));
			
			ticketJson.put("priority", "Low");
			ticketJson.put("caseForPartner", "Customer");
			ticketJson.put("caseFor", "Customer");
			ticketJson.put("caseOrigin", "Phone");
			ticketJson.put("serialNumber", "");
			ticketJson.put("rootCauseReasonId", "");
			
			ticketJson.put("file", JSONObject.NULL);
			String fileName = ticket.get("FileNameToAttach");
			if ((fileName != null) && (!"".equals(fileName))) {
				ticketJson.put("file", fileName);
			}
			
			ticketJson.put("groupReasonId", JSONObject.NULL);			
			ticketJson.put("currentAssigneeId", JSONObject.NULL);	
			ticketJson.put("customerAdditionalEmail", JSONObject.NULL);	
			ticketJson.put("customerAdditionalMobileNumber", JSONObject.NULL);	
			ticketJson.put("helperName", JSONObject.NULL);	
				
			ticketJson.put("source", JSONObject.NULL);	
			ticketJson.put("subSource", JSONObject.NULL);	
			
			jsonString = ticketJson.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
//        System.out.println("Generated JSON: " + jsonString); // Json create

		return jsonString;
	}

}


