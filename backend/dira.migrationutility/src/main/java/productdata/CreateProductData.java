package productdata;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import Act_Migration.CreateActBaseQos;
import Act_Migration.CreateActVolPlan;
import Act_Migration.CreateBandwidthQosPolicy;
import Act_Migration.CreateBodPlan;
import Act_Migration.Create_Act_Plan;
import utility.ModuleControlConstant;
import utility.Utility;

public class CreateProductData {
	
	private void createPlanService() {
		if(ModuleControlConstant.PLANSERVICE) {
			PlanService planService = new PlanService();
			List<Map<String, String>> serviceMapList = planService.readPlanServiceList();
			planService.createPlanService(serviceMapList);
		}
	}

	private void createPlanTax() {
		if(ModuleControlConstant.PLANTAX) {
			PlanTaxNew planTax = new PlanTaxNew();
			List<Map<String, String>> taxMapList = planTax.readPlanTaxList();
			planTax.createPlanTax(taxMapList);
		}
	}
	
	private void createPlanCharge() {
		if(ModuleControlConstant.PLANCHARGE) {
			PlanCharge planCharge = new PlanCharge();
			List<Map<String, String>> chargeMapList = planCharge.readUniquePlanChargeList();
			planCharge.createPlanCharge(chargeMapList);
		}
	}

	private void createPlanQos() {
		if(ModuleControlConstant.PLANQOS) {
			PlanQos planQos = new PlanQos();
			List<Map<String, String>> qosMapList = planQos.readUniquePlanQosList();
			planQos.createPlanQos(qosMapList);
		}
	}

	private void createPrepaidPlan() {
		if(ModuleControlConstant.PREPAIDPLAN) {
			PrepaidPlan prepaidPlan = new PrepaidPlan();
			List<Map<String, String>> planMapList = prepaidPlan.readUniquePrepaidPlanList();
			prepaidPlan.createPrepaidPlan(planMapList);
		}
	}  
	
	private void createPlanBundle() {
		if(ModuleControlConstant.PLANBUNDLE) {
			PlanBundle planBundle = new PlanBundle();
			List<Map<String, String>> planBundleMapList = planBundle.readPlanBundleList();
			planBundle.createPlanBundle(planBundleMapList);		
		}
	}

	//Act  for read data or eecute will strt--->
	public synchronized void createPrepaidBasePlan() {
		if(ModuleControlConstant.PREPAIDBASEPLAN) {
			Create_Act_Plan prepaidPlan = new Create_Act_Plan();
			List<Map<String, String>> planMapList = prepaidPlan.readUniquePrepaidPlanList();
			//prepaidPlan.createPrepaidPlan(planMapList);
			prepaidPlan.createPrepaidPlanInParallel(planMapList);
		}
	}
	//vod plan
	public synchronized void createPrepaidVolPlan() {
		if(ModuleControlConstant.PREPAIDVODPLAN) {
			CreateActVolPlan prepaidPlan = new CreateActVolPlan();
			List<Map<String, String>> planMapList = prepaidPlan.readUniquePrepaidPlanList();
			//prepaidPlan.createPrepaidPlan(planMapList);
			prepaidPlan.createPrepaidPlanInParallel(planMapList);
		}
	}
	
	//Bod plan
	public synchronized void createPrepaidBodPlan() {
			if(ModuleControlConstant.PREPAIDBODPLAN) {
				CreateBodPlan prepaidPlan = new CreateBodPlan();
				List<Map<String, String>> planMapList = prepaidPlan.readUniquePrepaidPlanList();
				//prepaidPlan.createPrepaidPlan(planMapList);
				
				prepaidPlan.createPrepaidPlanInParallel(planMapList);
				
			}
		}
	
	public synchronized void createBaseQos() {
		if(ModuleControlConstant.BASEQOS) {
			CreateActBaseQos prepaidPlan = new CreateActBaseQos();
			List<Map<String, String>> planMapList = prepaidPlan.readUniquePlanQosList();
			prepaidPlan.createPlanQos(planMapList);
		}
	}
	
	//Bandwidth qos
	public synchronized void createBandwidthQos() {
		if(ModuleControlConstant.BANDWIDTHQOS) {
			CreateBandwidthQosPolicy prepaidPlan = new CreateBandwidthQosPolicy();
			List<Map<String, String>> planMapList = prepaidPlan.readUniquePlanQosList();
			prepaidPlan.createPlanQos(planMapList);
		}
	}
	
	
	public void generateProductData() {

		System.out.println("Started Generting Product Data...!");
		Utility.printLog("execution.log", "ProductData", "Started Generting Product Data...!","");
		
		createPlanService();	
		createPlanTax();
		createPlanCharge();
		createPlanQos();
		createPrepaidPlan();
		createPlanBundle();
		
		
		//Act
		/*
		createBaseQos();  
		createBandwidthQos();
		createPrepaidVolPlan(); 
		createPrepaidBodPlan();
		createPrepaidBasePlan(); */ 
		
		 // bod plan
		// Sequential execution using CompletableFuture
	    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> createBaseQos())
	        .thenRun(() -> createBandwidthQos())
	        .thenRun(() -> createPrepaidVolPlan())
	        .thenRun(() -> createPrepaidBodPlan())
	        .thenRun(() -> createPrepaidBasePlan());

	    future.join(); // Ensure all tasks are completed before proceeding

		
		System.out.println("Ended Generting Product Data...!");
		Utility.printLog("execution.log", "ProductData", "Ended Generting Product Data...!","");
	}

}
