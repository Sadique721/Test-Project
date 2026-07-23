package inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import commons.CommonGetAPI;
import utility.ProductUtility;
import utility.Utility;

public class Product extends RestExecution {

	private String logFileName = "inventory.log";
	private String logModuleName = "Product";

	private void createProduct(Map<String, String> productDetails) {

		String apiURL = getAPIURL("SavbillInventoryManagement/product/save");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		String apiBody = getProductJson(productDetails);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		if (!apiBody.equals(null)) {
			JSONObject JSONResponseBody = httpPostFormDataP(apiURL, apiBody);
			String response = JSONResponseBody.toString(4);
			Utility.printLog(logFileName, logModuleName, "Response", response);

			String productName = productDetails.get("Name");
			ProductUtility.printResponse(JSONResponseBody, logModuleName, productName);
		}
	}
	
	
	/*// json into form data 
	private void createProduct(Map<String, String> productDetails) {

	    String apiURL = getAPIURL("SavbillInventoryManagement/product/save");
	    Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

	    // Convert productDetails to form-data
	    String apiBody = getProductJson(productDetails);  // You can still use the getProductJson for the JSON conversion
	    Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

	    if (apiBody != null && !apiBody.equals("")) {
	        // Send form data instead of JSON by modifying the httpPost method
	        JSONObject JSONResponseBody = sendFormData(apiURL, productDetails);
	        String response = JSONResponseBody.toString(4);
	        Utility.printLog(logFileName, logModuleName, "Response", response);

	        String productName = productDetails.get("Name");
	        ProductUtility.printResponse(JSONResponseBody, logModuleName, productName);
	    }
	}

	private JSONObject sendFormData(String apiURL, Map<String, String> productDetails) {
	    JSONObject responseJson = null;

	    try {
	        // Construct the HttpPost request to send the form data
	        HttpPost post = new HttpPost(apiURL);

	        // Create MultipartEntityBuilder to build form data
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

	        // Add product details to the form data
	        for (Map.Entry<String, String> entry : productDetails.entrySet()) {
	            builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
	        }

	        // Create the entity
	        HttpEntity entity = builder.build();
	        post.setEntity(entity);

	        // Execute the HTTP request
	        try (CloseableHttpClient client = HttpClients.createDefault();
	             CloseableHttpResponse response = client.execute(post)) {

	            // Capture the response
	            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
	            responseJson = new JSONObject(responseString);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return responseJson;
	}

	*/
	
	
	

	public void createProduct(List<Map<String, String>> productMapList) {

		for (int i = 0; i < productMapList.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = productMapList.get(i);
			Utility.printLog(logFileName, logModuleName, "Sheet Raw Data", map.toString());
			createProduct(map);
		}
	}

	public List<Map<String, String>> readUniqueProductList() {

		String sheetName = "Product";
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getInventoryDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> productMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			String name = safeTrim(cellValue.get("Name *"));
			if ((!"".equals(name)) && (name != null)) {
				
				valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
				valuemap.put("Name", safeTrim(cellValue.get("Name *")));
				valuemap.put("ProductId", safeTrim(cellValue.get("ProductId")));
				valuemap.put("LedgerId", safeTrim(cellValue.get("LedgerId")));

				valuemap.put("Category", safeTrim(cellValue.get("Category *")));
				valuemap.put("IsOEMConsider", safeTrim(cellValue.get("IsOEMConsider")));
				valuemap.put("IsAssetConsider", safeTrim(cellValue.get("IsAssetConsider")));
				valuemap.put("Vendor", safeTrim(cellValue.get("Vendor *")));
				valuemap.put("CAS", safeTrim(cellValue.get("CAS")));
				valuemap.put("TotalInPorts", safeTrim(cellValue.get("TotalInPorts *")));
				valuemap.put("TotalOutPorts", safeTrim(cellValue.get("TotalOutPorts *")));

				valuemap.put("WarrentyTime", safeTrim(cellValue.get("WarrentyTime *")));
				valuemap.put("WarrentyTimeUnit", safeTrim(cellValue.get("WarrentyTimeUnit *")));
				valuemap.put("Status", safeTrim(cellValue.get("Status *")));
				valuemap.put("Description", safeTrim(cellValue.get("Description *")));

				valuemap.put("NewProductActualPrice", safeTrim(cellValue.get("NewProductActualPrice")));
				valuemap.put("NewProductTaxName", safeTrim(cellValue.get("NewProductTaxName")));
				valuemap.put("NewProductRefundAmountInWarranty", safeTrim(cellValue.get("NewProductRefundAmountInWarranty *")));
				valuemap.put("NewProductRefundAmountPostWarranty", safeTrim(cellValue.get("NewProductRefundAmountPostWarranty *")));

				valuemap.put("RefurburshiedProductActualPrice", safeTrim(cellValue.get("RefurburshiedProductActualPrice")));
				valuemap.put("RefurburshiedProductTaxName", safeTrim(cellValue.get("RefurburshiedProductTaxName")));
				valuemap.put("RefurburshiedProductRefundAmountInWarranty", safeTrim(cellValue.get("RefurburshiedProductRefundAmountInWarranty *")));
				valuemap.put("RefurburshiedProductRefundAmountPostWarranty", safeTrim(cellValue.get("RefurburshiedProductRefundAmountPostWarranty *")));

				productMapList.add(valuemap);
			}
		}
		return productMapList;
	}

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String getProductJson(Map<String, String> productDetails) {
        String jsonString = null;

        try {
            CommonGetAPI commonGetAPI = new CommonGetAPI();
            JSONObject productJson = new JSONObject();

            // --- Basic Fields ---
            productJson.put("id", JSONObject.NULL);
            productJson.put("name", productDetails.get("Name"));
            productJson.put("productId", JSONObject.NULL);
            productJson.put("navLedgerId", JSONObject.NULL);

            String productId = productDetails.get("ProductId");
            if (productId != null && !"".equals(productId)) {
                productJson.put("productId", productId);
            }

            String ledgerId = productDetails.get("LedgerId");
            if (ledgerId != null && !"".equals(ledgerId)) {
                productJson.put("navLedgerId", ledgerId);
            }

            int vendorId = commonGetAPI.getVendorId(productDetails.get("Vendor"));
            productJson.put("vendorId", vendorId);

            productJson.put("hasOEMConsider", Boolean.parseBoolean(productDetails.get("IsOEMConsider")));
            productJson.put("hasAssetConsider", Boolean.parseBoolean(productDetails.get("IsAssetConsider")));

            // --- Product Category & Ports ---
            String result = commonGetAPI.getProductCategoryIdAndType(productDetails.get("Category"));
            int totalInPorts = -1, totalOutPorts = -1;
            String type = "";

            if (result != null) {
                String[] ans = result.split(":");
                int productCategoryId = Integer.parseInt(ans[0]);
                type = ans[1];
                productJson.put("productCategory", productCategoryId);

                String inPorts = productDetails.get("TotalInPorts");
                String outPorts = productDetails.get("TotalOutPorts");

                if (inPorts != null && outPorts != null && !"".equals(inPorts) && !"".equals(outPorts)) {
                    if (type.equalsIgnoreCase("NetworkBind") || type.equalsIgnoreCase("CustomerBind")) {
                        totalInPorts = Integer.parseInt(inPorts);
                        totalOutPorts = Integer.parseInt(outPorts);
                    }
                }
            }

            productJson.put("totalInPorts", totalInPorts);
            productJson.put("totalOutPorts", totalOutPorts);
            productJson.put("availableInPorts", totalInPorts);
            productJson.put("availableOutPorts", totalOutPorts);

            productJson.put("caseId", JSONObject.NULL);
            if ("CustomerBind".equalsIgnoreCase(type)) {
                String casName = productDetails.get("CAS");
                if (casName != null && !"".equals(casName)) {
                    int caseId = commonGetAPI.getCASId(casName);
                    productJson.put("caseId", caseId);
                }
            }

            // --- Expiry & Description ---
            productJson.put("expiryTime", Integer.parseInt(productDetails.get("WarrentyTime")));
            productJson.put("expiryTimeUnit", productDetails.get("WarrentyTimeUnit"));
            productJson.put("description", productDetails.get("Description"));
            productJson.put("status", ProductUtility.getStatus(productDetails.get("Status")).toUpperCase());
            productJson.put("licenseDate", JSONObject.NULL);

            // --- New Product ---
            productJson.put("newPrice", JSONObject.NULL);
            productJson.put("newProductCharge", JSONObject.NULL);
            productJson.put("newProductTax", JSONObject.NULL);

            String newProductActualPrice = productDetails.get("NewProductActualPrice");
            if (newProductActualPrice != null && !"".equals(newProductActualPrice)) {
                float actualPrice = Utility.formattedFloatDecimalNumber(newProductActualPrice);
                productJson.put("actualpricenewProduct", actualPrice);

                String newProductTaxName = productDetails.get("NewProductTaxName");
                int taxId = commonGetAPI.getTaxId(newProductTaxName);
                productJson.put("newProductTax", taxId);
            } else {
                productJson.put("actualpricenewProduct", 0);
            }

            productJson.put("newProductRefAmountInWarranty",
                    Utility.formattedFloatDecimalNumber(productDetails.get("NewProductRefundAmountInWarranty")));
            productJson.put("newProductRefAmountPostWarranty",
                    Utility.formattedFloatDecimalNumber(productDetails.get("NewProductRefundAmountPostWarranty")));

            // --- Refurbished Product ---
            productJson.put("refurburshiedPrice", JSONObject.NULL);
            productJson.put("refurburshiedProductCharge", JSONObject.NULL);
            productJson.put("refurburshiedProductTax", JSONObject.NULL);

            String refurbPrice = productDetails.get("RefurburshiedProductActualPrice");
            if (refurbPrice != null && !"".equals(refurbPrice)) {
                float actualRefurbPrice = Utility.formattedFloatDecimalNumber(refurbPrice);
                productJson.put("actualpricerefurbishedProduct", actualRefurbPrice);

                String refurbTaxName = productDetails.get("RefurburshiedProductTaxName");
                int taxId = commonGetAPI.getTaxId(refurbTaxName);
                productJson.put("refurburshiedProductTax", taxId);
            } else {
                productJson.put("actualpricerefurbishedProduct", 0);
            }

            productJson.put("refurburshiedProductRefAmountInWarranty",
                    Utility.formattedFloatDecimalNumber(productDetails.get("RefurburshiedProductRefundAmountInWarranty")));
            productJson.put("refurburshiedProductRefAmountPostWarranty",
                    Utility.formattedFloatDecimalNumber(productDetails.get("RefurburshiedProductRefundAmountPostWarranty")));

            // --- Specs ---
            productJson.put("specificationParametersDTOList", new JSONArray());

            jsonString = productJson.toString(2); // pretty print with indentation

        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println(jsonString);
        return jsonString;
    }


//    private String getProductJson(Map<String, String> productDetails) {
//
//		String jsonString = null;
//
//		try {
//			CommonGetAPI commonGetAPI = new CommonGetAPI();
//			JSONObject productJson = new JSONObject();
//			String status = ProductUtility.getStatus(productDetails.get("Status")).toUpperCase();
//
//			// -- Basic Details
//
//			productJson.put("id", JSONObject.NULL);
//			productJson.put("name", productDetails.get("Name"));
//			productJson.put("productId", JSONObject.NULL);
//			productJson.put("navLedgerId", JSONObject.NULL);
//
//			String productId = productDetails.get("ProductId");
//			if (!"".equals(productId)) {
//				productJson.put("productId", productDetails.get("ProductId"));
//			}
//
//			String ledgerId = productDetails.get("LedgerId");
//			if (!"".equals(ledgerId)) {
//				productJson.put("navLedgerId", productDetails.get("LedgerId"));
//			}
//
//			int vendorId = commonGetAPI.getVendorId(productDetails.get("Vendor"));
//			productJson.put("vendorId", vendorId);
//
//			productJson.put("hasOEMConsider", Boolean.valueOf(productDetails.get("IsOEMConsider")));
//			productJson.put("hasAssetConsider", Boolean.valueOf(productDetails.get("IsAssetConsider")));
//
//
//			String result = commonGetAPI.getProductCategoryIdAndType(productDetails.get("Category"));
//			if (result != null) {
//
//				String ans[] = result.split(":");
//				int productCategoryId = Integer.parseInt(ans[0]);
//				String type = ans[1];
//
//				productJson.put("productCategory", productCategoryId);
//
//				int totalInPorts = -1;
//				int totalOutPorts = -1;
//
//				String inPorts = productDetails.get("TotalInPorts");
//				String outPorts = productDetails.get("TotalOutPorts");
//
//				if ((!"".equals(inPorts)) && (!"".equals(outPorts))) {
//					if (type.equalsIgnoreCase("NetworkBind") || type.equalsIgnoreCase("CustomerBind")) {
//						totalInPorts = Integer.parseInt(inPorts);
//						totalOutPorts = Integer.parseInt(outPorts);
//					}
//				}
//
//				productJson.put("totalInPorts", totalInPorts);
//				productJson.put("totalOutPorts", totalOutPorts);
//				productJson.put("availableInPorts", totalInPorts);
//				productJson.put("availableOutPorts", totalOutPorts);
//				productJson.put("caseId", JSONObject.NULL);
//
//				if (type.equalsIgnoreCase("CustomerBind")) {
//					String casName = productDetails.get("CAS");
//					if (!"".equals(casName)) {
//						int caseId = commonGetAPI.getCASId(casName);
//						productJson.put("caseId", caseId);
//					}
//				}
//			}
//
//			productJson.put("expiryTime", Integer.parseInt(productDetails.get("WarrentyTime")));
//			productJson.put("expiryTimeUnit", productDetails.get("WarrentyTimeUnit"));
//			productJson.put("description", productDetails.get("Description"));
//			productJson.put("status", status);
//
//			// -- New Product Details
//
//	/*		String newProductCharge = productDetails.get("NewProductCharge");
//			if ("".equals(newProductCharge)) {
//				productJson.put("newProductCharge", JSONObject.NULL);
//			} else {
//				int chargeId = commonGetAPI.getDirectChargeId(newProductCharge);
//				if (chargeId != 0) {
//					productJson.put("newProductCharge", chargeId);
//				}
//			}
//	*/
//			productJson.put("newProductCharge", JSONObject.NULL);
//			productJson.put("newPrice", JSONObject.NULL);
//			productJson.put("newProductTax", JSONObject.NULL);
//
//			String newProductActualPrice1 = productDetails.get("NewProductActualPrice");
//			if (!"".equals(newProductActualPrice1)) {
//
//				float newProductActualPrice = Utility.formattedFloatDecimalNumber(newProductActualPrice1);
//				productJson.put("actualpricenewProduct", newProductActualPrice);
//
//				String newProductTaxName = productDetails.get("NewProductTaxName");
//				int taxId = commonGetAPI.getTaxId(newProductTaxName);
//				productJson.put("newProductTax", taxId);
//
//			}
//
//			String temp = productDetails.get("NewProductRefundAmountInWarranty");
//			float NewProductRefundAmountInWarranty = Utility.formattedFloatDecimalNumber(temp);
//			productJson.put("newProductRefAmountInWarranty", NewProductRefundAmountInWarranty);
//
//			temp = productDetails.get("NewProductRefundAmountPostWarranty");
//			float NewProductRefundAmountPostWarranty = Utility.formattedFloatDecimalNumber(temp);
//			productJson.put("newProductRefAmountPostWarranty", NewProductRefundAmountPostWarranty);
//
//
//			// -- Refurburshied Product Details
//
//	/*		String refurburshiedProductCharge = productDetails.get("RefurburshiedProductCharge");
//			if ("".equals(refurburshiedProductCharge)) {
//				productJson.put("refurburshiedProductCharge", JSONObject.NULL);
//			} else {
//				int chargeId = commonGetAPI.getDirectChargeId(refurburshiedProductCharge);
//				if (chargeId != 0) {
//					productJson.put("refurburshiedProductCharge", chargeId);
//				}
//			}
//	*/
//
//			productJson.put("refurburshiedProductCharge", JSONObject.NULL);
//			productJson.put("refurburshiedPrice", JSONObject.NULL);
//			productJson.put("refurburshiedProductTax", JSONObject.NULL);
//
//			String refurburshiedProductActualPrice1 = productDetails.get("RefurburshiedProductActualPrice");
//			if (!"".equals(refurburshiedProductActualPrice1)) {
//
//				float refurburshiedProductActualPrice = Utility.formattedFloatDecimalNumber(refurburshiedProductActualPrice1);
//				productJson.put("actualpricerefurbishedProduct", refurburshiedProductActualPrice);
//
//				String refurburshiedProductTaxName = productDetails.get("RefurburshiedProductTaxName");
//				int taxId = commonGetAPI.getTaxId(refurburshiedProductTaxName);
//				productJson.put("refurburshiedProductTax", taxId);
//
//			}
//
//			temp = productDetails.get("RefurburshiedProductRefundAmountInWarranty");
//			float refurburshiedProductRefAmountInWarranty = Utility.formattedFloatDecimalNumber(temp);
//			productJson.put("refurburshiedProductRefAmountInWarranty", refurburshiedProductRefAmountInWarranty);
//
//			temp = productDetails.get("RefurburshiedProductRefundAmountPostWarranty");
//			float refurburshiedProductRefAmountPostWarranty = Utility.formattedFloatDecimalNumber(temp);
//			productJson.put("refurburshiedProductRefAmountPostWarranty", refurburshiedProductRefAmountPostWarranty);
//
//			List<JSONObject> specificationParametersDTOList = new ArrayList<JSONObject>();
//			productJson.put("specificationParametersDTOList", specificationParametersDTOList);
//
//			jsonString = productJson.toString();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//        System.out.println(jsonString);
//		return jsonString;
//	}

	public String getProductCategoryIdAndType_OLD(String productCategoryName) {

		String apiURL = getAPIURL("SavbillInventoryManagement/productCategory/all");

		JSONObject jsonResponse = httpGet(apiURL);
		int status = jsonResponse.getInt("responseCode");
		String result = null;

		if (status == 200) {
			JSONArray jsonArray = jsonResponse.getJSONArray("dataList");
			for (int i = 0; i < jsonArray.length(); i++) {
				String receivedProductCategoryName = jsonArray.getJSONObject(i).getString("name");
				if (receivedProductCategoryName.equalsIgnoreCase(productCategoryName)) {
					int Id = jsonArray.getJSONObject(i).getInt("id");
					String type = jsonArray.getJSONObject(i).getString("type");
					result = Id + ":" + type;
					break;
				}
			}
		}

		if (result == null) {
			System.out.println("ProductCategory details not found - " + productCategoryName);
			Utility.printLog(logFileName, logModuleName, "ProductCategory details not found - ", productCategoryName);
		}
		return result;
	}

}
