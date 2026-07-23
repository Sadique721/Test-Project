package SavanaCustomer;

import java.util.List;
import java.util.Map;

import after_Migration_Payments_CheckAndGetList.Check_Payments;
import commons.CommonAPI;
import temp.UpdateSheet;
import utility.Constant;
import utility.CustomerExecutionSchedulerHelper;
import utility.DataValidation;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateSavanaCustomerData {

	private void createPrepaidCustomer() {
		try {
			if (ModuleControlConstant.CUSTOMERSAVANA) {
				
				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
				UpdateSheet updateSheet = customerScheduler.getCustomerExecutionSchedulerData(Constant.PREPAID_CUSTOMER_SCHEDULER,"Customer");
				rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
				customerScheduler.clearExistingFile(Constant.PREPAID_CUSTOMER_SCHEDULER);
				
				// CafCustomer prepaidCustomerNew = new CafCustomer();
				Savana_Thread_Customer prepaidCustomerNew = new Savana_Thread_Customer();

				// Tumil prepaidCustomerNew=new Tumil();
				CommonAPI common = new CommonAPI();
				List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();
				// System.out.println(customerData);

				Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
				// System.out.println(serviceAreaIdAll);
				prepaidCustomerNew.createPrepaidCustomer(customerData, serviceAreaIdAll);
				// prepaidCustomerNew.createPrepaidCustomer(customerData);
				//rw.setMultipleColumnInActiveSheetSavana();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
		} finally {
			//ReadWriteExcelFile rw = new ReadWriteExcelFile();// act migration
			//rw.setMultipleColumnInActiveSheetSavana(); // it is commenct by now after add addon
		}
	}


    private void createPrepaidCustomerClass() {
        try {
            if (ModuleControlConstant.PREPAIDCUSTOMERSAVANA) {
 
            	ReadWriteExcelFile rw = new ReadWriteExcelFile();
				CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
				UpdateSheet updateSheet = customerScheduler.getCustomerExecutionSchedulerData(Constant.PREPAID_CUSTOMER_SCHEDULER,"Customer");
				rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
				customerScheduler.clearExistingFile(Constant.PREPAID_CUSTOMER_SCHEDULER);
				
                // CafCustomer prepaidCustomerNew = new CafCustomer();
                Savana_Thread_Customer_Class prepaidCustomerNew = new Savana_Thread_Customer_Class();

                // Tumil prepaidCustomerNew=new Tumil();
                CommonAPI common = new CommonAPI();
                List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();
                // System.out.println(customerData);

                Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
                // System.out.println(serviceAreaIdAll);
                prepaidCustomerNew.createPrepaidCustomer(customerData, serviceAreaIdAll);
                // prepaidCustomerNew.createPrepaidCustomer(customerData);
                //rw.setMultipleColumnInActiveSheetSavana();
			
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
        } finally {
            //ReadWriteExcelFile rw = new ReadWriteExcelFile();// act migration
            //rw.setMultipleColumnInActiveSheetSavana(); // it is commenct by now after add addon
        }
    }

	private void createPrepaidParentCustomerClass() {
		try {
			if (ModuleControlConstant.PREPAIDPARENTCUSTOMERSAVANA) {

				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
				UpdateSheet updateSheet = customerScheduler.getCustomerExecutionSchedulerData(Constant.PREPAID_CUSTOMER_SCHEDULER,"Customer");
				rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
				customerScheduler.clearExistingFile(Constant.PREPAID_CUSTOMER_SCHEDULER);

				// CafCustomer prepaidCustomerNew = new CafCustomer();
				Savana_Thread_ParentCustomer_Class prepaidParentCustomerNew = new Savana_Thread_ParentCustomer_Class();

				// Tumil prepaidCustomerNew=new Tumil();
				CommonAPI common = new CommonAPI();
				List<Map<String, String>> customerData = prepaidParentCustomerNew.readUniquePrepaidParentCustomerList();
				// System.out.println(customerData);

				Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
				// System.out.println(serviceAreaIdAll);
				prepaidParentCustomerNew.createPrepaidParentCustomer(customerData, serviceAreaIdAll);
				// prepaidCustomerNew.createPrepaidCustomer(customerData);
				//rw.setMultipleColumnInActiveSheetSavana();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (createPrepaidParentCustomer)..... " + e.getMessage());
		} finally {
			//ReadWriteExcelFile rw = new ReadWriteExcelFile();// act migration
			//rw.setMultipleColumnInActiveSheetSavana(); // it is commenct by now after add addon
		}
	}


	private void AssignCustomerInventory() {
		try {
			if (ModuleControlConstant.ASSIGNINVENTORY_SAVANNA_CUSTOMER) {
				AssignInventorySavana assignInventory = new AssignInventorySavana();
				List<Map<String, String>> customerMapList = assignInventory.readAssignInventoryCustomerList();
				assignInventory.AssignInventoryToCustomer(customerMapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (Assign Invetory to customer)..... " + e.getMessage());
		}
	}

	private void recordPayment() {
		try {
			if (ModuleControlConstant.RECORD_PAYMENT_CUSTOMER) {
				RecordPayment record = new RecordPayment();
				List<Map<String, String>> customerMapList = record.readUniqueCustomerPaymentDetailsList();
				record.recordCustomerPaymentDetails(customerMapList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (Record Payment to customer)..... " + e.getMessage());
		}
	}

    private void recordCAF_Payment() {
        try {
            if (ModuleControlConstant.RECORD_PAYMENT_CAF_CUSTOMER) {
                CAF_PastPayment recordCAFPayment = new CAF_PastPayment();
                List<Map<String, String>> customerMapList = recordCAFPayment.readCAFPaymentList();
                recordCAFPayment.createCAFPaymentRecord(customerMapList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (CAF Record Payment to customer)..... " + e.getMessage());
        }
    }

    private void recordPaymentwithBackDate() {
        try {
            if (ModuleControlConstant.RECORD_PAYMENT_CUSTOMER_BACKDATE) {
            	
//            	ReadWriteExcelFile rw = new ReadWriteExcelFile();
//				CustomerExecutionSchedulerHelper customerScheduler = new CustomerExecutionSchedulerHelper();
//				UpdateSheet updateSheet = customerScheduler.getCustomerExecutionSchedulerData(Constant.RECORD_PAYMENT_SCHEDULER,"PaymentDetails");
//				rw.setMultipleColumnInActiveSheetNew(updateSheet, Constant.SAVANACUSTOMER_FILE);
//				customerScheduler.clearExistingFile(Constant.RECORD_PAYMENT_SCHEDULER);
            	
                PastPayment pastPayment = new PastPayment();
                List<Map<String, String>> customerMapList = pastPayment.readPaymentList();
                pastPayment.createPaymentRecord(customerMapList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (Record Payment to customer)..... " + e.getMessage());
        }
    }

	private void checkPaymentRecordbyReferenceNo() {
		try {
			if (ModuleControlConstant.CHECK_PAYMENT_RECORD_BY_REFERENCE_NUMBER) {
				Check_Payments payment = new Check_Payments();
				payment.validatePaymentsFromExcel();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Record not Found)..... " + e.getMessage());
		}
	}

	private void cafCustomer() {
		try {
			if (ModuleControlConstant.CAFCUSTOMERSAVANA) {

				ReadWriteExcelFile rw = new ReadWriteExcelFile();

				CafCustomer prepaidCustomerNew = new CafCustomer();
				// Savana_Thread_Customer prepaidCustomerNew=new Savana_Thread_Customer();
				CommonAPI common = new CommonAPI();
				List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();
				// System.out.println(customerData);

				Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
				// System.out.println(serviceAreaIdAll);

				prepaidCustomerNew.createPrepaidCustomer(customerData, serviceAreaIdAll);
				// prepaidCustomerNew.createPrepaidCustomer(customerData);
				rw.setMultipleColumnInActiveSheetSavana();

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (Record Payment to customer)..... " + e.getMessage());
		}

	}

    private void CafCustomerClass() {
        try {
            if (ModuleControlConstant.CAFCUSTOMERCLASSSAVANNA) {

                ReadWriteExcelFile rw = new ReadWriteExcelFile();

                CafCustomerClass CAFCustomer = new CafCustomerClass();
                // Savana_Thread_Customer prepaidCustomerNew=new Savana_Thread_Customer();
                CommonAPI common = new CommonAPI();
                List<Map<String, String>> customerData = CAFCustomer.readUniquePrepaidCustomerList();
                // System.out.println(customerData);

                Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
                // System.out.println(serviceAreaIdAll);

                CAFCustomer.createPrepaidCustomer(customerData, serviceAreaIdAll);
                // prepaidCustomerNew.createPrepaidCustomer(customerData);
//                rw.setMultipleColumnInActiveSheetSavana();

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (CAF customer)..... " + e.getMessage());
        }

    }

	// Diret Carge
	private void createDirectChargeCustomer() {
		try {
			if (ModuleControlConstant.CUSTOMERDIRECTCHARGE) {

				ReadWriteExcelFile rw = new ReadWriteExcelFile();
				DirectCharge dc = new DirectCharge();

				List<Map<String, String>> customerData = dc.readCustomerDirectChargeDatalist();

				dc.addCustomerdirectcharge(customerData);
//				rw.setMultipleColumnInActiveSheetSavana();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (createDirectchargeCustomer)..... " + e.getMessage());
		} 
	}


    // CAF Diret Carge
    private void createCAFDirectChargeCustomer() {
        try {
            if (ModuleControlConstant.CAF_CUSTOMERDIRECTCHARGE) {

                ReadWriteExcelFile rw = new ReadWriteExcelFile();
                CAF_DirectCharge dc = new CAF_DirectCharge();

                List<Map<String, String>> customerData = dc.readCAF_CustomerDirectChargeDatalist();

                dc.addCAFCustomerdirectcharge(customerData);
//                rw.setMultipleColumnInActiveSheetSavana();

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (createCAF_DirectchargeCustomer)..... " + e.getMessage());
        } finally {
//            ReadWriteExcelFile rw = new ReadWriteExcelFile();// act migration
//            rw.setMultipleColumnInActiveSheetSavana(); // it is commenct by now after add addon
        }
    }

	
	public void generatePrepaidCustomerData() throws Exception {
		try {
			System.out.println("Started Generting Savanna Customer Data...!");
			Utility.printLog("execution.log", "SavannaCustomer", "Started Generting Customer Data...!", "");

			String fileName = Constant.SAVANACUSTOMER_FILE;
			ReadWriteExcelFile rwe = new ReadWriteExcelFile();
			rwe.isExcelFileOpen(fileName);

			DataValidation dataValidation = new DataValidation();
			//dataValidation.verifyCustomerServiceArea();
			//dataValidation.verifyCustomerWard();
			//dataValidation.verifyPincodeBelongsToServiceArea();

			createPrepaidCustomer();

            createPrepaidCustomerClass(); //New as per Payload

			createPrepaidParentCustomerClass();

			AssignCustomerInventory();

			recordPayment(); //for Prepaid payments

            recordCAF_Payment(); //for CAF payments

			cafCustomer();

            CafCustomerClass();

			createDirectChargeCustomer();

            createCAFDirectChargeCustomer(); //for CAF Direct Charge

            recordPaymentwithBackDate();

			checkPaymentRecordbyReferenceNo();
		
			System.out.println("Ended Generting SavannaCustomer Data...!");
			Utility.printLog("execution.log", "Customer Creation", "Ended Generting Customer Data...!", "");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getting error in this method (generateCustomerData).... " + e.getMessage());
		}
	}

}
