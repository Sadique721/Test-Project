package netConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class NetConfCustomer extends RestExecution {
	private static String logFileName = "NetCon.log";
	private static String logModuleName = "NetCon";

	public void createNetConfCust(Map<String, String> netconfcustDetails) {

		String apiURL = getAPIURL("savbilltNetConfManagement/customer/customerMigration");     //here solve issue of id
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getnetConfPrepaidCustomerJson(netconfcustDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		String userName = netconfcustDetails.get("userName");
		ProductUtility.printResponse(JSONResponseBody, logModuleName, userName);

	}

	public void createNetConfCust(List<Map<String, String>> NetConPrepaidCustMapList) {

		for (int i = 0; i < NetConPrepaidCustMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = NetConPrepaidCustMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
			createNetConfCust(map);
		}
	}

	public List<Map<String, String>> readNetconfCustList() {

		String sheetName = "NetConfPrepaid";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getNetConfDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> netconfCustMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String userName = cellValue.get("userName");
			if ((!"".equals(userName)) && (userName != null)) {

				valuemap.put("RowIndex", cellValue.get("RowIndex"));

				valuemap.put("firstname", cellValue.get("firstname"));
				valuemap.put("lastname", cellValue.get("lastname"));
				valuemap.put("userName",cellValue.get("userName"));
				valuemap.put("password", cellValue.get("password"));
				valuemap.put("email", cellValue.get("email"));
		
				valuemap.put("acct_no", cellValue.get("acct_no"));
			
				valuemap.put("GatewayIpBind", cellValue.get("GatewayIpBind"));
				valuemap.put("bngrouterinterface", cellValue.get("bngrouterinterface"));
				valuemap.put("qos", cellValue.get("qos"));
				valuemap.put("vlanid", cellValue.get("vlanid"));
				valuemap.put("wanip", cellValue.get("wanip"));
				
				valuemap.put("remarks", cellValue.get("remarks"));
				valuemap.put("mobile", cellValue.get("mobile"));
				valuemap.put("addparam1", cellValue.get("addparam1"));
				valuemap.put("addparam4", cellValue.get("addparam4"));
				
				//static route lease
				valuemap.put("lanip", cellValue.get("lanip"));
				
				valuemap.put("asnnumber", cellValue.get("asnnumber"));
				valuemap.put("llaccountid", cellValue.get("llaccountid"));
				valuemap.put("ipprefixes", cellValue.get("lanipipprefixes"));
				valuemap.put("skipnetconf", cellValue.get("skipnetconf"));
				valuemap.put("peerip", cellValue.get("peerip"));
				valuemap.put("vsiid", cellValue.get("vsiid"));
				
			
				valuemap.put("rdexport", cellValue.get("rdexport"));
				valuemap.put("rdimport", cellValue.get("rdimport"));
				valuemap.put("rdvalue", cellValue.get("rdvalue"));
				valuemap.put("vrfname", cellValue.get("vrfname"));
				valuemap.put("vsiname", cellValue.get("vsiname"));
				
				
				valuemap.put("expiryDate", cellValue.get("expiryDate"));
				
				valuemap.put("status", cellValue.get("status"));
				valuemap.put("partnerid", cellValue.get("partnerid"));
				
				valuemap.put("ipprefixes", cellValue.get("lanipipprefixes"));
				
				netconfCustMapList.add(valuemap);
			}
		}
		return netconfCustMapList;
	}

	private String getnetConfPrepaidCustomerJson(Map<String, String> custDetails) {

		String jsonString = null;

		try {
		
			JSONObject confCustPojoObject = new JSONObject();
			String username = custDetails.get("userName");
			String firstname = custDetails.get("firstname");
			String lastname = custDetails.get("lastname");
			
			String status = custDetails.get("status");
			String partnerId = custDetails.get("partnerid");
			
			String password = custDetails.get("password");
			String email = custDetails.get("email");
			
			String acct_no = custDetails.get("acct_no");
			String addparam1 = custDetails.get("addparam1");
			String addparam4 = custDetails.get("addparam4");
			
			
			
			
			
			
			String gatewayIpBind = custDetails.get("GatewayIpBind");
			System.out.println(gatewayIpBind);
			String bngrouterinterface = custDetails.get("bngrouterinterface");
			String qos = custDetails.get("qos");
			String vlanid = custDetails.get("vlanid");
			String wanip = custDetails.get("wanip");
			
			String remarks = custDetails.get("remarks");
			String mobile = custDetails.get("mobile");
		
			String expiryDate = custDetails.get("expiryDate");
			
			String asnnumber = custDetails.get("asnnumber");
			String llaccountid = custDetails.get("llaccountid");
			String ipprefixes = custDetails.get("ipprefixes");
			String skipnetconf = custDetails.get("skipnetconf");
			String peerip = custDetails.get("peerip");
			
			String vsiid = custDetails.get("vsiid");
			
			
           String lanip=custDetails.get("lanip");
           System.out.println(lanip);
			
			String rdexport=custDetails.get("rdexport");
			String rdimport=custDetails.get("rdimport");
			String rdvalue=custDetails.get("rdvalue");
			String vrfname=custDetails.get("vrfname");
			String vsiname=custDetails.get("vsiname");
					
			confCustPojoObject.put("firstname", firstname);
			confCustPojoObject.put("lastname", lastname);  
			confCustPojoObject.put("username", username);  
			confCustPojoObject.put("password", password);  
			//-->
			confCustPojoObject.put("email", email);   
			confCustPojoObject.put("acctno", acct_no); 
	
			
			confCustPojoObject.put("gatewayIP", gatewayIpBind); 
			confCustPojoObject.put("bngrouterinterface", bngrouterinterface); 
			confCustPojoObject.put("qos", qos);  
			confCustPojoObject.put("vlanid", vlanid);  
			confCustPojoObject.put("wanip", wanip);  
			confCustPojoObject.put("remarks", remarks); 
			confCustPojoObject.put("phone", mobile);  
			confCustPojoObject.put("partnerid", partnerId); 
			confCustPojoObject.put("edate", expiryDate); 
			confCustPojoObject.put("status", status);  
			confCustPojoObject.put("addparam1", addparam1); 
			confCustPojoObject.put("addparam4", addparam4); 
			
			if (addparam4.equalsIgnoreCase("IPv4Customer") && !lanip.isEmpty()) {
				confCustPojoObject.put("lanip", lanip);				
			}
			
			else if(addparam4.equalsIgnoreCase("IPv4LeaseBGPCustomer")) {
				confCustPojoObject.put("asnnumber", asnnumber);
				confCustPojoObject.put("ipprefixes", ipprefixes);
				confCustPojoObject.put("llaccountid", llaccountid);
				confCustPojoObject.remove("expiryDate");
			}
			else if(addparam4.equalsIgnoreCase("IPPoolLeaseCustomer")) {
				confCustPojoObject.put("skipnetconf", skipnetconf);
				confCustPojoObject.remove("addparam1");
				confCustPojoObject.remove("vlanid");
				confCustPojoObject.remove("wanip");
				
			}
			else if(addparam4.equalsIgnoreCase("L3VPNLeaseCustomer")) {
				confCustPojoObject.put("lanip", lanip);
				confCustPojoObject.put("rdexport", rdexport);
				confCustPojoObject.put("rdimport", rdimport);
				confCustPojoObject.put("rdvalue", rdvalue);
				confCustPojoObject.put("vrfname", vrfname);
				
			}
			
			else if(addparam4.equalsIgnoreCase("L2VPNLeaseCustomer")) {
				confCustPojoObject.put("peerip", peerip);
				confCustPojoObject.put("vsiid", vsiid);
				confCustPojoObject.put("vsiname", vsiname);
				confCustPojoObject.remove("addparam1");
				confCustPojoObject.remove("wanip");
			}
		
			jsonString = confCustPojoObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}
		return jsonString;
	}

}
