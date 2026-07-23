package customer;

import java.util.List;
import java.util.Map;

import temp.EnhancedExcelBatchProcessor;
import temp.SimpleThreadPool;
import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateCustomerData {

	private void createPrepaidCustomer() {
		if (ModuleControlConstant.CUSTOMER) {
			PrepaidCustomerNew prepaidCustomerNew = new PrepaidCustomerNew();
			List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();

			// SimpleThreadPool stp = new SimpleThreadPool();   //-->here changes
		  //    stp.executeThreadsBatch(customerMapList);      //-->here changes
		 
			prepaidCustomerNew.createPrepaidCustomer(customerData);
		}
	}

	private void recordPaymentDetails() {
		if (ModuleControlConstant.RECORDPAYMENT) {
			CustomerPaymentDetails customerPaymentDetails = new CustomerPaymentDetails();
			List<Map<String, String>> paymentDetailsMapList = customerPaymentDetails
					.readUniqueCustomerPaymentDetailsList();
			customerPaymentDetails.recordCustomerPaymentDetails(paymentDetailsMapList);
		}
	}

	private void createCreditNote() {
		if (ModuleControlConstant.CREDITNOTE) {
			CreditNote creditNote = new CreditNote();
			List<Map<String, String>> creditNoteMapList = creditNote.readCreditNoteList();
			creditNote.createCreditNote(creditNoteMapList);
		}
	}

	private void AssignCustomerInventory() {
		if (ModuleControlConstant.ASSIGNINVENTORY_CUSTOMER) {
			AssignInventory assignInventory = new AssignInventory();
			List<Map<String, String>> customerMapList = assignInventory.readAssignInventoryCustomerList();
			assignInventory.AssignInventoryToCustomer(customerMapList);
		}
	}

	private void uploadCustomerDocument() {
		if (ModuleControlConstant.UPLOADDOCUMENT) {
			CustomerDocumentUpload customerDocumentUpload = new CustomerDocumentUpload();
			List<Map<String, String>> customerMapList = customerDocumentUpload.readUploadDocumentList();
			customerDocumentUpload.uploadCustomerDocumentDetails(customerMapList);
		}
	}

	private void RenewPlan() {
		if (ModuleControlConstant.RENEWPLAN) {
			RenewPlan renewPlan = new RenewPlan();
			List<Map<String, String>> customerMapList = renewPlan.readRenewPlanCustomerList();
			renewPlan.renewCustomerPlan(customerMapList);
		}
	}

	public void generatePrepaidCustomerData() throws Exception {

		System.out.println("Started Generting PrepaidCustomer Data...!");
		Utility.printLog("execution.log", "PrepaidCustomer", "Started Generting PrepaidCustomer Data...!", "");

		String fileName = Constant.CUSTOMER_DATA_FILE;
		ReadWriteExcelFile rwe = new ReadWriteExcelFile();
		rwe.isExcelFileOpen(fileName);

		createPrepaidCustomer();
		recordPaymentDetails();
		 createCreditNote();
		uploadCustomerDocument();
		AssignCustomerInventory();
		RenewPlan();

		System.out.println("Ended Generting PrepaidCustomer Data...!");
		Utility.printLog("execution.log", "PrepaidCustomer", "Ended Generting PrepaidCustomer Data...!", "");

	}

}
