package netConf;

import java.util.List;
import java.util.Map;

import utility.ModuleControlConstant;
import utility.Utility;

public class CreateNetconfCustmer {
	private void createNetConfCust() {
		if(ModuleControlConstant.CUSTOMERNETCONF) {
			NetConfCustomer netconf=new NetConfCustomer();
			//Location leadSourceMaster = new Location();
			List<Map<String, String>> NetConfCustomer = netconf.readNetconfCustList();
			netconf.createNetConfCust(NetConfCustomer);
		}
	}
	public void generateNetConfPrepaidCustData() {
		System.out.println("Started Generting NetConf Customer Data...!");
		Utility.printLog("execution.log", "Netconf", "Started Generting Netconf Prepaid customer Data...!","");
		
		createNetConfCust();
		
		
		System.out.println("Ended Generting Location Data...!");
		Utility.printLog("execution.log", "Netconf", "Ended Generting NetConf preapid customer Data...!","");
	}
}
