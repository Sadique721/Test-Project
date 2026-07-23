package network;

import java.util.List;
import java.util.Map;

import utility.ModuleControlConstant;
import utility.Utility;

public class CreateNetworkData {



	private void createNetwork() {
		if (ModuleControlConstant.NETWORK) {
			CreateNetwork network = new CreateNetwork();
			List<Map<String, String>> vendorMapList = network.readNetworkList();
			// vendor.createVendor(vendorMapList);

		}

	}

	public void generateInventoryData() {
		System.out.println("Started to generate Network Data ...!");
		Utility.printLog("execution.log", "NetworkData", "Started Generting Network Data...!", "");

		createNetwork();

		System.out.println("Ended to generate Network Data ...!");
		Utility.printLog("execution.log", "NetworkData", "Ended Generting Inventory Data...!", "");
	}
}
