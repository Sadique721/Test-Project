package staff;

import java.util.List;
import java.util.Map;

import utility.ModuleControlConstant;
import utility.Utility;

public class CreateStaffData {

	private void createTeam() {
		if(ModuleControlConstant.TEAM) {
			Team teams = new Team();
			List<Map<String, String>> teamsMapList = teams.readTeamList();
			teams.createTeam(teamsMapList);
		}
	}

	private void createStaff() {
		if(ModuleControlConstant.STAFF) {
			Staff staff = new Staff();
			List<Map<String, String>> staffMapList = staff.readStaffList();
			staff.createStaff(staffMapList);
		}
	}

	public void generateStaffData() {
		System.out.println("Started to generte Staff Data...!");
		Utility.printLog("execution.log", "StaffData", "Started Generting Staff Data...!","");
		
		createTeam();
		createStaff();

		System.out.println("Ended to generte Staff Data...!");
		Utility.printLog("execution.log", "StaffData", "Ended Generting Staff Data...!","");
	}

}
