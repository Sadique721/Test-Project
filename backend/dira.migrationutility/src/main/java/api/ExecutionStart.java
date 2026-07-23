package api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.StopWatch;

import Act_Migration.ActAddonData;
import SalesCRM.CreateSalesCRMData;
import SavanaCustomer.CreateSavanaCustomerData;
import SavanaCustomer.PaymentData;
import SavanaCustomer.UpdationDatabase;
import customer.CreateCustomerData;
import inventory.CreateInventoryData;
import location.CreateLocationData;
import masterdata.CreateMasterData;
import netConf.CreateNetconfCustmer;
import partner.CreatePartnerData;
import productdata.CreateProductData;
import sanityCheck.ActCustomerSheetSanity;
import staff.CreateStaffData;
import staff.Login;
import ticketsystem.CreateTicketData;
import tumil.TumilCreateData;
import tumil.TumilProducer;
import utility.ModuleControlConstant;
import utility.Utility;
import workflow.CreateWorkflowData;

public class ExecutionStart {

	public static void main(String[] args) {

		StopWatch overallSW = new StopWatch(); // new stopwatch for full execution

		StopWatch sw = new StopWatch();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date startTime = new Date();

		try {
			System.out.println("Started Migration Utility...!");
			Utility.printLog("execution.log", "MAIN", "Started Migration Utility...!", "");

			System.out.println("Start Time: " + sdf.format(startTime));
			Utility.printLog("execution.log", "MAIN", "Start Time: " + sdf.format(startTime), "");

			overallSW.start(); // start the overall timer
			sw.start();
			startExecution();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			// stop both timers
			overallSW.stop();
			sw.stop();

			Date endTime = new Date();

			// ---- Overall Time ----
			long totalMillis = overallSW.getTime();
			long seconds = (totalMillis / 1000) % 60;
			long minutes = (totalMillis / (1000 * 60)) % 60;
			long hours = (totalMillis / (1000 * 60 * 60)) % 24;
			String duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

			String overallMsg = "Overall Execution Summary -> Start: " + sdf.format(startTime) + " | End: "
					+ sdf.format(endTime) + " | Total Duration: " + duration + " (" + totalMillis + " ms)";

			System.out.println(overallMsg);
			Utility.printLog("execution.log", "MAIN", overallMsg, "");

			// ---- Existing Stopwatch Time ----
			String msg = "Ended Migration Utility...! | Taken Time (Method SW): " + sw.getTime() + " ms";
			System.out.println(msg);
			Utility.printLog("execution.log", "MAIN", msg, "");
		}

	}

	static void startExecution() throws Exception { // static or private for direct and public multithreading-->

		Login login = new Login();
		login.setAuthBearer();

		/*
		 * CreateDunningData createDunningData = new CreateDunningData();
		 * createDunningData.generateDunningData();
		 */

		if (ModuleControlConstant.MASTERDATA_MIGRATION) {
			CreateMasterData createMasterData = new CreateMasterData();
			createMasterData.generateMasterData();
			// location master execution
		}
		if (ModuleControlConstant.LOCATION_MIGRATION) {
			CreateLocationData createLocationMasterData = new CreateLocationData();
			createLocationMasterData.generateLocationData();
		}

		if (ModuleControlConstant.STAFFDATA_MIGRATION) {
			CreateStaffData createStaffData = new CreateStaffData();
			createStaffData.generateStaffData();
		}

		if (ModuleControlConstant.PLANDATA_MIGRATION) {
			// Act base plan Also
			CreateProductData createProductData = new CreateProductData();
			createProductData.generateProductData();
		}

		if (ModuleControlConstant.PARTNERDATA_MIGRATION) {
			CreatePartnerData createPartnerData = new CreatePartnerData();
			createPartnerData.generatePartnerData();
		}

		if (ModuleControlConstant.INVENTORYDATA_MIGRATION) {
			CreateInventoryData createInventoryData = new CreateInventoryData();
			createInventoryData.generateInventoryData();
		}

		if (ModuleControlConstant.CUSTOMERDATA_MIGRATION) {
			CreateCustomerData createCustomerData = new CreateCustomerData();
			createCustomerData.generatePrepaidCustomerData();
		}

		if (ModuleControlConstant.TICKETDATA_MIGRATION) {

			CreateTicketData createTicketData = new CreateTicketData();
			createTicketData.generateTicketData();
		}

		if (ModuleControlConstant.SALESCRMDATA_MIGRATION) {
			CreateSalesCRMData createSalesCRMData = new CreateSalesCRMData();
			createSalesCRMData.generateSalesCRMData();
		}

		if (ModuleControlConstant.WORKFLOW_MIGRATION) {
			CreateWorkflowData createWorkflowData = new CreateWorkflowData();
			createWorkflowData.generateWorkflowData();
		}

		// NetConf customer -->
		if (ModuleControlConstant.NETCONF_MIGRATION) {
			CreateNetconfCustmer createNetconfData = new CreateNetconfCustmer();
			createNetconfData.generateNetConfPrepaidCustData();
		}

		// Act Prepaid Customer-->

		if (ModuleControlConstant.ACT_MIGRATION) {
			// here i have change thread --->
			// ActDataThread act = new ActDataThread();

			ActCustomerSheetSanity sanity = new ActCustomerSheetSanity();
			sanity.sanitySheetCustomer();
			// ActCustomerData act=new ActCustomerData();
			// act.generateActCustomerData();
		}
		// Act addon Plan for act customer this is seprate because large no of data so
		// we will execute sepratly-->
		if (ModuleControlConstant.ADDONCUSTOMER) {
			ActAddonData addon = new ActAddonData();
			addon.generateAddonData();
		}

		// savanna
		if (ModuleControlConstant.SAVANNA_MIGRATION) {
			CreateSavanaCustomerData customer = new CreateSavanaCustomerData();

			customer.generatePrepaidCustomerData();

			UpdationDatabase update = new UpdationDatabase(); // savana
			// TumilProducer update=new TumilProducer();
			update.callProducer();

		}

		// payement api
		if (ModuleControlConstant.SAVANNA_PAYMENT) {
			PaymentData customer = new PaymentData();
			customer.generatePrepaidCustomerData();

		}

		// Tumil Customer
		if (ModuleControlConstant.TUMIL_MIGRATION) {
			TumilCreateData customer = new TumilCreateData();
			customer.generatePrepaidCustomerData();
			TumilProducer update = new TumilProducer();
			update.callProducer();

		}

//        if (ModuleControlConstant.DEPARTMENT_MANAGEMENT) {
//            CreateMasterData createMasterData = new CreateMasterData();
//            createMasterData.generateMasterData();
//        }

	}

}
