package location;

import java.util.List;
import java.util.Map;

import javax.tools.DocumentationTool.Location;

import utility.ModuleControlConstant;
import utility.Utility;

public class CreateLocationData {

	
	private void createlocationMaster() {
		if(ModuleControlConstant.LOCATION) {
			LocationMaster locationMaster=new LocationMaster();
			//Location leadSourceMaster = new Location();
			List<Map<String, String>> LocationMaster = locationMaster.readLocationList();
			locationMaster.createLocation(LocationMaster);
		}
	}
	public void generateLocationData() {
		System.out.println("Started Generting Location Data...!");
		Utility.printLog("execution.log", "Location", "Started Generting Location Data...!","");
		
		createlocationMaster();
		
		
		System.out.println("Ended Generting Location Data...!");
		Utility.printLog("execution.log", "Location", "Ended Generting Location Data...!","");
	}

}
