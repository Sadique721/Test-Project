package partner;

import java.util.List;
import java.util.Map;

import utility.ModuleControlConstant;
import utility.Utility;

public class CreatePartnerData {

	public void createPartnerPlanBundle() {
		if(ModuleControlConstant.PARTNERPLANBUNDLE) {
			PartnerPlanBundle partnerPlanBundle = new PartnerPlanBundle();
			List<Map<String, String>> serviceMapList = partnerPlanBundle.readPartnerPlanBundleList();
			partnerPlanBundle.createPartnerPlanBundle(serviceMapList);
		}
	}
	
	public void createPartner() {
		if(ModuleControlConstant.PARTNER) {
			Partner partner = new Partner();
			List<Map<String, String>> partnerMapList = partner.readPartnerList();
			partner.createPartner(partnerMapList);
		}
	}
		
	public void generatePartnerData() {
		
		System.out.println("Started Generting Partner Data...!");
		Utility.printLog("execution.log", "PartnerData", "Started Generting Partner Data...!","");
		
		createPartnerPlanBundle();
		createPartner();
		
		System.out.println("Ended Generting Partner Data...!");
		Utility.printLog("execution.log", "PartnerData", "Ended Generting Partner Data...!","");
	}

}
