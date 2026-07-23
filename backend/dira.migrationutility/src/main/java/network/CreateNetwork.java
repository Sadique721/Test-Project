package network;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class CreateNetwork extends RestExecution {
	private static String logFileName = "Network.log";
	private static String logModuleName = "CreateNetwork";
	
	private void createNetwork(Map<String, String> networkDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/product/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getNetworkJson(networkDetails);  // -->need to develop
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String networkName = networkDetails.get("Name");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, networkName);
		}
	}

	public void createProduct(List<Map<String, String>> networkMapList) {

		for (int i = 0; i < networkMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = networkMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
			createNetwork(map);
		}
	}

	
	
	// To read data from sheet
		public List<Map<String, String>> readNetworkList() {

			String sheetName = "CreateNetwork";
			List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
			ReadData readData = new ReadData();
			sheetMap = readData.getNetworkDataSheet(sheetName);

			Map<String, String> cellValue = new HashMap<String, String>();
			List<Map<String, String>> vendorMapList = new ArrayList<Map<String, String>>();

			for (int i = 0; i < sheetMap.size(); i++) {

				Map<String, String> valuemap = new HashMap<String, String>();
				cellValue = sheetMap.get(i);

				String vendorName = cellValue.get("Name");
				if ((!"".equals(vendorName)) && (vendorName != null)) {
					
					valuemap.put("Sno", cellValue.get("Sno"));
					valuemap.put("Name", cellValue.get("Name"));
					valuemap.put("DeviceType", cellValue.get("DeviceType"));
					valuemap.put("Product", cellValue.get("Product"));
					valuemap.put("ServiceArea", cellValue.get("ServiceArea"));
					valuemap.put("Latitude", cellValue.get("Latitude"));
					valuemap.put("Longitutde", cellValue.get("Longitutde"));
					valuemap.put("Status", cellValue.get("Status"));
					vendorMapList.add(valuemap);
				}
			}
			return vendorMapList;
		}
		
		private String getNetworkJson(Map<String, String> networkDetails) {

			String jsonString = null;

			try {
				CommonGetAPI commonGetAPI = new CommonGetAPI();
				JSONObject productJson = new JSONObject();
				String status = ProductUtility.getStatus(networkDetails.get("Status")).toUpperCase();

				// -- Basic Details

				productJson.put("id", JSONObject.NULL);
				productJson.put("name", networkDetails.get("Name"));
				productJson.put("productId", JSONObject.NULL);
				productJson.put("navLedgerId", JSONObject.NULL);

				String productId = networkDetails.get("ProductId");
				if (!"".equals(productId)) {
					productJson.put("productId", networkDetails.get("ProductId"));
				}

				String ledgerId = networkDetails.get("LedgerId");
				if (!"".equals(ledgerId)) {
					productJson.put("navLedgerId", networkDetails.get("LedgerId"));
				}

				int vendorId = commonGetAPI.getVendorId(networkDetails.get("Vendor"));
				productJson.put("vendorId", vendorId);
				
				productJson.put("hasOEMConsider", Boolean.valueOf(networkDetails.get("IsOEMConsider")));
				productJson.put("hasAssetConsider", Boolean.valueOf(networkDetails.get("IsAssetConsider")));
					

				String result = commonGetAPI.getProductCategoryIdAndType(networkDetails.get("Category"));
				if (result != null) {

					String ans[] = result.split(":");
					int productCategoryId = Integer.parseInt(ans[0]);
					String type = ans[1];

					productJson.put("productCategory", productCategoryId);

					int totalInPorts = -1;
					int totalOutPorts = -1;

					String inPorts = networkDetails.get("TotalInPorts");
					String outPorts = networkDetails.get("TotalOutPorts");

					if ((!"".equals(inPorts)) && (!"".equals(outPorts))) {
						if (type.equalsIgnoreCase("NetworkBind") || type.equalsIgnoreCase("CustomerBind")) {
							totalInPorts = Integer.parseInt(inPorts);
							totalOutPorts = Integer.parseInt(outPorts);
						}
					}

					productJson.put("totalInPorts", totalInPorts);
					productJson.put("totalOutPorts", totalOutPorts);
					productJson.put("availableInPorts", totalInPorts);
					productJson.put("availableOutPorts", totalOutPorts);
					productJson.put("caseId", JSONObject.NULL);

					if (type.equalsIgnoreCase("CustomerBind")) {
						String casName = networkDetails.get("CAS");
						if (!"".equals(casName)) {
							int caseId = commonGetAPI.getCASId(casName);
							productJson.put("caseId", caseId);
						}
					}
				}

				productJson.put("expiryTime", Integer.parseInt(networkDetails.get("WarrentyTime")));
				productJson.put("expiryTimeUnit", networkDetails.get("WarrentyTimeUnit"));
				productJson.put("description", networkDetails.get("Description"));
				productJson.put("status", status);

			
				
				productJson.put("newProductCharge", JSONObject.NULL);
				productJson.put("newPrice", JSONObject.NULL);
				productJson.put("newProductTax", JSONObject.NULL);  
				
				String newProductActualPrice1 = networkDetails.get("NewProductActualPrice");
				if (!"".equals(newProductActualPrice1)) {
					
					float newProductActualPrice = Utility.formattedFloatDecimalNumber(newProductActualPrice1);
					productJson.put("actualpricenewProduct", newProductActualPrice);
					
					String newProductTaxName = networkDetails.get("NewProductTaxName");
					int taxId = commonGetAPI.getTaxId(newProductTaxName);
					productJson.put("newProductTax", taxId);
					
				}
				
				String temp = networkDetails.get("NewProductRefundAmountInWarranty");
				float NewProductRefundAmountInWarranty = Utility.formattedFloatDecimalNumber(temp);
				productJson.put("newProductRefAmountInWarranty", NewProductRefundAmountInWarranty);

				temp = networkDetails.get("NewProductRefundAmountPostWarranty");
				float NewProductRefundAmountPostWarranty = Utility.formattedFloatDecimalNumber(temp);
				productJson.put("newProductRefAmountPostWarranty", NewProductRefundAmountPostWarranty);

			
			
				
				productJson.put("refurburshiedProductCharge", JSONObject.NULL);
				productJson.put("refurburshiedPrice", JSONObject.NULL);
				productJson.put("refurburshiedProductTax", JSONObject.NULL);  
				
				String refurburshiedProductActualPrice1 = networkDetails.get("RefurburshiedProductActualPrice");
				if (!"".equals(refurburshiedProductActualPrice1)) {
					
					float refurburshiedProductActualPrice = Utility.formattedFloatDecimalNumber(refurburshiedProductActualPrice1);
					productJson.put("actualpricerefurbishedProduct", refurburshiedProductActualPrice);
					
					String refurburshiedProductTaxName = networkDetails.get("RefurburshiedProductTaxName");
					int taxId = commonGetAPI.getTaxId(refurburshiedProductTaxName);
					productJson.put("refurburshiedProductTax", taxId);
					
				}
				
				temp = networkDetails.get("RefurburshiedProductRefundAmountInWarranty");
				float refurburshiedProductRefAmountInWarranty = Utility.formattedFloatDecimalNumber(temp);
				productJson.put("refurburshiedProductRefAmountInWarranty", refurburshiedProductRefAmountInWarranty);

				temp = networkDetails.get("RefurburshiedProductRefundAmountPostWarranty");
				float refurburshiedProductRefAmountPostWarranty = Utility.formattedFloatDecimalNumber(temp);
				productJson.put("refurburshiedProductRefAmountPostWarranty", refurburshiedProductRefAmountPostWarranty);

				List<JSONObject> specificationParametersDTOList = new ArrayList<JSONObject>();
				productJson.put("specificationParametersDTOList", specificationParametersDTOList);
				
				jsonString = productJson.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return jsonString;
		}
}
