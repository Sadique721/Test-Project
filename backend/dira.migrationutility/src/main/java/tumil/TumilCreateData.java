package tumil;
import java.util.List;
import java.util.Map;

import SavanaCustomer.AssignInventorySavana;
import SavanaCustomer.RecordPayment;
import commons.CommonAPI;
import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;
public class TumilCreateData {
	
	
			private void createPrepaidCustomer() {
				 try {
				if (ModuleControlConstant.CUSTOMERTUMIL) {
					
						 ReadWriteExcelFile rw = new ReadWriteExcelFile();
					 
					
						 
						 Tumil_Customer prepaidCustomerNew=new Tumil_Customer();
						 CommonAPI common=new CommonAPI();
						 List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();
					
						 
						 Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
						

					prepaidCustomerNew.createPrepaidCustomer(customerData,serviceAreaIdAll);
				    rw.setMultipleColumnInActiveSheetTumil();
				    
					 }
			        }
				catch (Exception e){
			            e.printStackTrace();
			            System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
			        }
				 finally {
						ReadWriteExcelFile rw = new ReadWriteExcelFile();//act migration 
						rw.setMultipleColumnInActiveSheetTumil();  //it is commenct by now after add addon
				}
				}
			
			
			private void AssignCustomerInventory() {
				try {
				if (ModuleControlConstant.ASSIGNINVENTORY_TUMIL_CUSTOMER) {
					AssignInventoryTumilCustomer assignInventory = new AssignInventoryTumilCustomer();
					List<Map<String, String>> customerMapList = assignInventory.readAssignInventoryCustomerList();
					assignInventory.AssignInventoryToCustomer(customerMapList);
				}
				 
	        }
		catch (Exception e){
	            e.printStackTrace();
	            System.out.println("getting error in this method (Assign Invetory to customer)..... " + e.getMessage());
	        }
			}
			private void recordPayment() {
				try {
				if (ModuleControlConstant.RECORD_PAYMENT_CUSTOMER_TUMIL) {
					RecordPaymentTumil record = new RecordPaymentTumil();
					List<Map<String, String>> customerMapList = record.readUniqueCustomerPaymentDetailsList();
					record.recordCustomerPaymentDetails(customerMapList);
				}
				 
	        }
		catch (Exception e){
	            e.printStackTrace();
	            System.out.println("getting error in this method (Record Payment to customer)..... " + e.getMessage());
	        }
		}
			
				private void createCafCustomer() {
					 try {
					if (ModuleControlConstant.CAFCUSTOMERTUMIL) {
						
							 ReadWriteExcelFile rw = new ReadWriteExcelFile();
						 
						
							 
							 CafCustomerTumil prepaidCustomerNew=new CafCustomerTumil();
							 CommonAPI common=new CommonAPI();
							 List<Map<String, String>> customerData = prepaidCustomerNew.readUniquePrepaidCustomerList();
						
							 
							 Map<String, Integer> serviceAreaIdAll = common.getServiceAreaIdAll();
							

						prepaidCustomerNew.createPrepaidCustomer(customerData,serviceAreaIdAll);
					    rw.setMultipleColumnInActiveSheetTumil();
					    
						 }
				        }
					catch (Exception e){
				            e.printStackTrace();
				            System.out.println("getting error in this method (createPrepaidCustomer)..... " + e.getMessage());
				        }
					 finally {
							ReadWriteExcelFile rw = new ReadWriteExcelFile();//act migration 
							rw.setMultipleColumnInActiveSheetTumil();  //it is commenct by now after add addon
					}
					}
		
				private void createDirectChargeCustomer() {
					 try {
					if (ModuleControlConstant.DIRECTCHARGE) {
						
							 ReadWriteExcelFile rw = new ReadWriteExcelFile();
						 
						
							 
							 TumilCustomerDirectData dc=new TumilCustomerDirectData();
							
							 List<Map<String, String>> customerData = dc.readCustomerDirectChargeDatalist();
						

							 dc.addCustomerdirectcharge(customerData);
					    rw.setMultipleColumnInActiveSheetTumil();
					    
						 }
				        }
					catch (Exception e){
				            e.printStackTrace();
				            System.out.println("getting error in this method (createDirectchargeCustomer)..... " + e.getMessage());
				        }
					 finally {
							ReadWriteExcelFile rw = new ReadWriteExcelFile();//act migration 
							rw.setMultipleColumnInActiveSheetTumil();  //it is commenct by now after add addon
					}
					}
				
			
			
			

			public void generatePrepaidCustomerData() throws Exception {
	try {
				System.out.println("Started Generting Tumil Customer Data...!");
				Utility.printLog("execution.log", "Tumil", "Started Generting SavannaCustomer Data...!", "");

				String fileName = Constant.TUMIL_FILE;
				ReadWriteExcelFile rwe = new ReadWriteExcelFile();
				rwe.isExcelFileOpen(fileName);

				createPrepaidCustomer();
				createCafCustomer();
				recordPayment();
				//assign inventory of tumil 
				AssignCustomerInventory();
				createDirectChargeCustomer();
				
				System.out.println("Ended Generting Tumil_Customer Data...!");
				Utility.printLog("execution.log", "Tumil", "Ended Generting SavannaCustomer Data...!", "");
	}
	catch(Exception e){
	    e.printStackTrace();
	    System.out.println("getting error in this method (generateTumilCustomerData).... " + e.getMessage());
	}
		}

	}


