package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.ReadData;
import commons.CommonAPI;

public class DataValidation {

	public void verifyCustomerServiceArea() {
		try {
			if (ModuleControlConstant.CUSTOMERSAVANA) {

				List<Map<String, String>> customerData = readUniquePrepaidCustomerList("Servicearea");
				verifyPrepaidCustomerSAandFAT(customerData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (verifyCustomerServiceArea)..... " + e.getMessage());
		}
	}

	private List<Map<String, String>> readUniquePrepaidCustomerList(String columnName) {

		String sheetName = "Customer"; // This is sheet name.
		List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
		ReadData readData = new ReadData();
		sheetMap = readData.getSavanaCustomerDataSheet(sheetName);

		Map<String, String> cellValue = new HashMap<String, String>();
		List<Map<String, String>> customerMapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < sheetMap.size(); i++) {

			Map<String, String> valuemap = new HashMap<String, String>();
			cellValue = sheetMap.get(i);

			valuemap.put("Sno", cellValue.get("Sno"));
			valuemap.put("Servicearea", cellValue.get("Servicearea"));
			valuemap.put("Building", cellValue.get("Building"));
			customerMapList.add(valuemap);
		}
		return customerMapList;
	}

	private void verifyPrepaidCustomerSAandFAT(List<Map<String, String>> customerMapList) {
		CommonAPI common = new CommonAPI();
		Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
		Map<String, String> serviceAreaNotFound = new HashMap<>();

		for (Map<String, String> customerDetails : customerMapList) {
			String serviceAreaName = customerDetails.get("Servicearea");
			Integer serviceareaId = serviceAreaIdAll.get(serviceAreaName.toLowerCase().trim());

			if (serviceareaId == null) {
				serviceAreaNotFound.put(serviceAreaName, serviceAreaName);
			}
		}

		for (String key : serviceAreaNotFound.keySet()) {
			String message = "Data Validation | ServiceArea details not found - " + key;
			System.out.println(message);
		}
		System.out.println("Data Validation | ServiceArea details not found Count - " + serviceAreaNotFound.size());
	}

	public void verifyCustomerWard() {
		try {
			if (ModuleControlConstant.CUSTOMERSAVANA) {

				List<Map<String, String>> customerData = readUniquePrepaidCustomerList("Building");
				verifyWardDetails(customerData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (verifyCustomerWard)..... " + e.getMessage());
		}
	}

	private void verifyWardDetails(List<Map<String, String>> customerMapList) {
		CommonAPI common = new CommonAPI();
		Map<String, Integer> wardIdAll = common.getAreaIdAll();
		Map<String, String> wardNotFound = new HashMap<>();

		for (Map<String, String> customerDetails : customerMapList) {
			String wardName = customerDetails.get("Building");
			Integer wardId = wardIdAll.get(wardName.toLowerCase().trim());

			if (wardId == null) {
				wardNotFound.put(wardName, wardName);
			}
		}

		for (String key : wardNotFound.keySet()) {
			String message = "Data Validation | Ward details not found - " + key;
			System.out.println(message);
		}
		System.out.println("Data Validation | Ward details not found Count - " + wardNotFound.size());
	}

	public void verifyPincodeBelongsToServiceArea() {
		try {
			if (ModuleControlConstant.CUSTOMERSAVANA) {

				List<Map<String, String>> customerData = readUniquePrepaidCustomerList("Building");
				verifyPincodeBelongsToServiceAreaAll(customerData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (verifPincodeBelongsToServiceArea)..... " + e.getMessage());
		}
	}

	
	private void verifyPincodeBelongsToServiceAreaAll(List<Map<String, String>> customerMapList) {
		CommonAPI common = new CommonAPI();
		Map<String, String> wardIdAll = common.getMasterDetailsByAreaNameAll();
		Map<String, List<Integer>> map = common.getPincodeListWithServiceArea();

		Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
		Map<String, Integer> pincodeIdAll = common.getPincodeIdAll();
		
		
		for (Map<String, String> customerDetails : customerMapList) {
			String wardName = customerDetails.get("Building");
			String serviceAreaName = customerDetails.get("Servicearea");
			String Row = customerDetails.get("Sno");
			String ans = wardIdAll.get(wardName.toLowerCase().trim());

			if (ans != null) {
				String result[] = ans.split(":");
				int pincodeId = Integer.parseInt(result[4]);
				//System.out.println("pincodeId = " + pincodeId);
				Integer serviceareaId = serviceAreaIdAll.get(serviceAreaName.toLowerCase().trim());

				if (serviceareaId != null) {
					List<Integer> pincodesListInServiceArea = map.get(serviceAreaName.toLowerCase().trim());

					if (pincodesListInServiceArea != null && pincodesListInServiceArea.contains(pincodeId)) {
						//System.out.println("Pincode exists in this Service Area");
					} else {
						String pincodeName = getKeyByValue(pincodeIdAll,pincodeId);
						System.out.println("Pincode Name : " + pincodeName + " does NOT exist in this Service Area : " + serviceAreaName);
					}
				} else {
					String message = "Data Validation | ServiceArea details not found - " + serviceAreaName;
					System.out.println(message);
				}

			} else {
				String message = "Data Validation | Ward details not found - " + wardName;
				System.out.println(message);
			}
		}
	}
	public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
	    for (Map.Entry<K, V> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null; // not found
	}

}
