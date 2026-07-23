package dunning;

import java.util.List;
import java.util.Map;

public class CreateDunningData {

	private void createDunningRule() {
		DunningRule dunningRule = new DunningRule();
		List<Map<String, String>> dunningMapList = dunningRule.readUniqueDunningRuleList();
		dunningRule.createDunningRule(dunningMapList);
	}

	
	public void generateDunningData() {
		System.out.println("Started to generate Dunning Data ...!");

		createDunningRule();
		
		System.out.println("Ended to generate Dunning Data ...!");
	}
}
