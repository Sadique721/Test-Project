package dunning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.DBOperations;
import utility.Utility;

public class DunningRule extends RestExecution {

	private void createDunningRule(Map<String, String> map){
		
		String logFileName = "dunning.log";
		String logModuleName = "CreateDunningRule";
		
		String apiURL=getAPIURL("dunningrule");
		Utility.printLog(logFileName,logModuleName , "Request URL", apiURL);
		
		//Initializing payload or API body
		
		 String APIBody = getDunningRuleJson(map);
		 Utility.printLog(logFileName,logModuleName , "Request Body", APIBody);
		 
		 JSONObject JSONResponseBody = httpPost(apiURL,APIBody);
		 String response = JSONResponseBody.toString(4);
		 Utility.printLog(logFileName,logModuleName , "Response", response);
		 

		 int status = JSONResponseBody.getInt("status");

		 if(status==200) {
			System.out.println("New DunningRule is added successfully - " + map.get("DunningName"));
			
			String type = "DunningRule";
			JSONObject cityJSONObject = JSONResponseBody.getJSONObject("dunningrule");
			String name = cityJSONObject.getString("name");
			int id = cityJSONObject.getInt("id");

			DBOperations dbo = new DBOperations();
			dbo.setAPIData(type,name,id,status);
			
		 }
	}
	
	public void createDunningRule(List<Map<String, String>> dunningMapList) {
		
		for (int i = 0; i < dunningMapList.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			map = dunningMapList.get(i);
			
			createDunningRule(map);
		}
	}

	public List<Map<String, String>> readUniqueDunningRuleList() {

		String sheetName = "Dunning ";
		int startRow = 1;
		int endRow = 5;

		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		//sheetMap = readData.getMaterDataSheet(sheetName, startRow, endRow);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> dunningMapList = new ArrayList<Map<String, String>>();

		for (int i = startRow; i < endRow; i++) {
			
			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);
			
			valuemap.put("DunningName", cellValue.get("Dunning Name "));
			valuemap.put("Status", cellValue.get("Status"));
			valuemap.put("DunningType", cellValue.get("Dunning Type(Customer Payment,Document,Advance Notifcation,Partner Document)"));
			valuemap.put("CredentialClass", cellValue.get("Credential Class(Gold,Silver,Platinum)"));
			valuemap.put("Email", cellValue.get("Email"));
			valuemap.put("SMS", cellValue.get("SMS"));
			valuemap.put("Deactivation", cellValue.get("Deactivation"));
			dunningMapList.add(valuemap);
								
		}
		return dunningMapList;
	}

	@SuppressWarnings("unchecked")
	private String getDunningRuleJson(Map<String, String> map) {
		
		String jsonString = ""; 
		
		try {
			
			JSONObject productJsonObject = new JSONObject();
			
			productJsonObject.put("name", map.get("DunningName"));
			
			String dunningType = map.get("DunningType").trim();
			if(dunningType.equalsIgnoreCase("Customer Payment")) {
				productJsonObject.put("dunningType", "Payment");
			}else if(dunningType.equalsIgnoreCase("Document")) {
				productJsonObject.put("dunningType", "Document");
			}else if(dunningType.equalsIgnoreCase("Advance Notification")) {
				productJsonObject.put("dunningType", "AdvanceNotification");
			}
			
			String emailDays = map.get("Email");
			int nEmailDays = Integer.parseInt(emailDays.substring(0, emailDays.indexOf(" ")));
			
			List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();			
			JSONObject email = new JSONObject();			
			
			email.put("action", "Email");
			email.put("days", nEmailDays);
			email.put("emailsub", "subisu@gmail.com");		
			email.put("dunningRuleId", "");
			email.put("id", "");			
			jsonObjectList.add(email);
			
			String smsDays = map.get("SMS");
			int nSmsDays = Integer.parseInt(smsDays.substring(0, smsDays.indexOf(" ")));
			
			JSONObject sms = new JSONObject();
			
			sms.put("action", "SMS");
			sms.put("days", nSmsDays);
			sms.put("emailsub", "subisu@gmail.com");		
			sms.put("dunningRuleId", "");
			sms.put("id", "");			
			jsonObjectList.add(sms);
			
			if(!dunningType.equalsIgnoreCase("Advance Notification")) {
				
				String deactivationDays = map.get("Deactivation");
				int nDeactivationDays = Integer.parseInt(deactivationDays.substring(0, deactivationDays.indexOf(" ")));
				
				JSONObject deactivation = new JSONObject();
				
				deactivation.put("action", "DeActivation");
				deactivation.put("days", nDeactivationDays);
				deactivation.put("emailsub", "subisu@gmail.com");		
				deactivation.put("dunningRuleId", "");
				deactivation.put("id", "");			
				jsonObjectList.add(deactivation);				
			}
			
			productJsonObject.put("dunningRuleActionPojoList", jsonObjectList);
			
			jsonString = productJsonObject.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}
	

}
